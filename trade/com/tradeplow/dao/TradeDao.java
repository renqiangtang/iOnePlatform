package com.tradeplow.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bank.dao.BankBaseDao;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.base.utils.DateUtils;
import com.base.utils.IDGenerator;
import com.base.utils.Lottery;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sms.dao.SmsDao;
import com.sysman.dao.ConfigDao;
import com.sysman.service.UserService;
import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.tradeplow.engine.EngineDao;
import com.tradeplow.service.TradeService;

public class TradeDao extends BaseDao {
	Logger log = Logger.getLogger(this.getClass());

	// private static Hashtable<String, String> targetLotteryMap = new
	// Hashtable<String, String>();
	// public static void putLotteryMap(String key , String value) {
	// targetLotteryMap.put(key, value);
	// }
	// public static void removeLotteryMap(String key) {
	// targetLotteryMap.remove(key);
	// }
	// public static String getLotteryMap(String key) {
	// if(targetLotteryMap.containsKey(key)){
	// return targetLotteryMap.get(key);
	// }else{
	// return null;
	// }
	// }
	// public static Hashtable<String, String> getLotteryMap() {
	// Hashtable<String, String> newHashTable = new Hashtable<String, String>();
	// newHashTable.putAll(targetLotteryMap);
	// return newHashTable;
	// }
	/**
	 * 保存出价
	 * 
	 * @param targetId
	 * @param userId
	 * @param price
	 * @return
	 * @throws Exception
	 */
	public synchronized ParaMap savePrice(String targetId, String userId,
			BigDecimal price, String enPrice, String remark, String inPtype,
			String inQtype, String isTrust) throws Exception {

		ParaMap out = new ParaMap();

		ParaMap targetMap = this.getTargetData(targetId);
		if (targetMap == null || targetMap.getRecordCount() < 1) {
			out.put("state", 0);
			out.put("result", "4");
			out.put("message", "出价失败!标的当前状态不能出价");
			return out;
		} else {
			int is_valid = targetMap.getRecordInt(0, "is_valid");
			String status = targetMap.getRecordString(0, "status");
			String businessType = targetMap.getRecordString(0, "business_type");
			if (is_valid != 1) {
				out.put("state", 0);
				out.put("result", "4");
				out.put("message", "出价失败!标的当前状态不能出价");
				return out;
			}
			String goodsType = businessType.substring(0, 3);
			goodsType = goodsType.equals("401") ? "301" : goodsType;
			UserService us = new UserService();
			ParaMap pMap = new ParaMap();
			pMap.put("goodsType", goodsType);
			pMap.put("u", userId);
			pMap = us.checkBlackUser(pMap);
			if (pMap.containsKey("state") && pMap.getInt("state") != 1) {
				out.put("state", 0);
				out.put("result", "4");
				out.put("message", "出价失败!您已被列入黑名单，不允许出价");
				return out;
			}
		}

		ParaMap pMap = getRelevantAmountParam(targetId);

		String ptype = pMap.getString("ptype");
		String qtype = pMap.getString("qtype");
		if (ptype == null
				|| (!ptype.equals(Constants.LFocus) && !ptype
						.equals(Constants.LLimit))) {
			out.put("state", 0);
			out.put("message", "出价失败!标的当前状态不能出价");
			return out;
		}
		String unit = pMap.getString(Constants.Punit);
		String multiId = pMap.getString(Constants.PmultiId);
		BigDecimal beginPrice = pMap.getBigDecimal(Constants.PinitValue);
		BigDecimal finalPrice = pMap.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = pMap.getBigDecimal(Constants.Pstep);
		BigDecimal lastPrice = pMap.getBigDecimal(Constants.Pprice);
		String taskNodeId = pMap.getString("tasknodeid");

		price = this.getTrueMoney(price, unit);
		// 得到是否是有权限的竞买人
		ParaMap tasknodeuser = this.isTaskNodeUser(taskNodeId, userId);
		int isCanPay = tasknodeuser.getInt("rightType");
		if (isCanPay == Constants.RightExecute) {
			int sign = step.compareTo(new BigDecimal("0")); // 判断是增长还是降低
			String mess = step.compareTo(new BigDecimal("0")) == 1 ? "大于"
					: "小于";
			String mess2 = step.compareTo(new BigDecimal("0")) == 1 ? "必须大于等于"
					: "必须小于等于";
			String mess3 = step.compareTo(new BigDecimal("0")) == 1 ? "封顶价"
					: "最低价";
			if (lastPrice != null
					&& finalPrice != null
					&& (lastPrice.compareTo(finalPrice) == sign || lastPrice
							.compareTo(finalPrice) == 0)) {
				if (sign == 1 && finalPrice.compareTo(new BigDecimal("0")) <= 0) {

				} else {
					if (price.compareTo(finalPrice) == sign
							|| price.compareTo(finalPrice) == 0) {
						if (Constants.LFocus.equals(ptype)
								|| (Constants.LLimit.equals(ptype) && Constants.QPrice
										.equals(qtype))) {
							EngineDao engineDao = new EngineDao();
							engineDao.updateUserAgree(targetId, taskNodeId,
									userId, 1);// 同意进入
							out.put("state", 1);
							out.put("message", "出价成功!当前已有竞买人" + mess3 + "："
									+ formatMoney(finalPrice, unit)
									+ "了。您同意进入下一阶段。");

							return out;
						}
					} else {
						// 当前最高价已经超过封顶价，不能再次出价
						out.put("state", 0);
						out.put("message", "出价失败!当前已有竞买人" + mess3 + "："
								+ formatMoney(finalPrice, unit) + "了。");

						return out;
					}
				}
			}

			int status = this.getPriceStatus(price, lastPrice, beginPrice,
					finalPrice, step);
			if (status == -1) { // 出价失败
				out.put("state", 0);
				out.put("message", "出价" + formatMoney(price, unit) + "失败!");
				return out;
			} else {
				int logStatus = status;
				BigDecimal priceBak = price;
				if (status == 3 || status == 2) { // 价格超过封顶价或价格等于封顶价，就以封顶价出价
					price = finalPrice;
					logStatus = 1;
				}

				// 得到licenseId
				String licenseId = this.getLicenseId(targetId, userId);
				// 插入出价表
				String offerLogId = this.saveOfferLog(licenseId, multiId,
						price, logStatus, targetId, userId, remark, isTrust);
				if (status == 0) {
					out.put("state", 0);
					BigDecimal mustPrice = lastPrice == null ? beginPrice
							: (lastPrice.add(step));
					out.put("message", "出价失败!您的出价：" + formatMoney(price, unit)
							+ mess2 + formatMoney(mustPrice, unit));
				} else if (status == 1 || status == 2 || status == 3) {
					// 插入taskNode表
					this.deleteWfParameter(taskNodeId, "price");
					this.saveWfParameter(taskNodeId, "price", price.toString());
					this.deleteWfParameter(taskNodeId, "priceTime");
					this.saveWfParameter(taskNodeId, "priceTime",
							DateUtils.nowStr());
					this.deleteWfParameter(taskNodeId, "priceUser");
					this.saveWfParameter(taskNodeId, "priceUser", userId);
					this.deleteWfParameter(taskNodeId, "firstWait");
					// 保存加密信息
					if (StrUtils.isNotNull(enPrice)) {
						saveSysLobCa(offerLogId, "trans_offer_log", userId,
								enPrice);
					}
					if (status == 1) {
						out.put("state", 1);
						out.put("message", "出价:" + formatMoney(price, unit)
								+ "成功!");
					} else if (status == 2 || status == 3) {
						// 修改trans_target_multi_trade表
						if (qtype != null && !"".equals(qtype)
								&& !qtype.equalsIgnoreCase("null")) {
							this.updateMulti(qtype, lastPrice, targetId);
						}
						// 出封顶价同意进入下一轮
						this.updateAgreeNext(taskNodeId, userId);

						int isLast = pMap.getInt("isLast");
						if (status == 2) {
							int taskNodeUserNum = this
									.getTaskNodeUserNum(targetId);
							if (taskNodeUserNum == 1) { // 只有一个人竞价
								this.doEndTarget(targetId);
								out.put("state", 1);
								out.put("message",
										"出价:" + formatMoney(price, unit)
												+ "成功!您出了" + mess3
												+ "，恭喜您竞得该耕地指标");
							} else {
								out.put("state", 1);
								out.put("message",
										"出价:" + formatMoney(price, unit)
												+ "成功!您出了" + mess3
												+ "，请等待进入摇号阶段");
							}
						} else if (status == 3) {
							int taskNodeUserNum = this
									.getTaskNodeUserNum(targetId);
							if (taskNodeUserNum == 1) {// 只有一个人竞价
								this.doEndTarget(targetId);
								out.put("state", 1);
								out.put("message",
										"出价:" + formatMoney(priceBak, unit)
												+ mess + "该耕地指标的" + mess3
												+ formatMoney(finalPrice, unit)
												+ "。所以您此次出价为出" + mess3
												+ formatMoney(finalPrice, unit)
												+ "成功！恭喜您竞得该耕地指标");
							} else {
								out.put("state", 1);
								out.put("message",
										"出价:" + formatMoney(priceBak, unit)
												+ mess + "该耕地指标的" + mess3
												+ formatMoney(finalPrice, unit)
												+ "。所以您此次出价为出" + mess3
												+ formatMoney(finalPrice, unit)
												+ "成功！请等待进入摇号阶段");
							}
						}
					}
				}
			}
		} else {
			out.put("state", 0);
			out.put("message", "出价失败!您在该阶段无权出价");
		}
		DataSourceManager.commit();
		TradeService.invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeDao.savePrice()");
		return out;
	}

	/**
	 * 委托出价
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap payTrustPrice(String targetId) throws Exception {
		ParaMap out = new ParaMap();
		List licenseList = this.getTrustPriceLicenseList(targetId);
		if (licenseList == null || licenseList.size() < 1) {
			out.put("state", 1);
			out.put("message", "没有要委托出价的竞买人！");
			return out;
		}
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessId(targetId);
		this.saveTrustPrice(targetId, processId);
		out.put("state", 1);
		out.put("message", "委托出价完毕！");
		return out;
	}

	public void saveTrustPrice(String targetId, String processId)
			throws Exception {
		EngineDao engineDao = new EngineDao();
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		BigDecimal price = taskNodeMap.getBigDecimal(Constants.Pprice);
		BigDecimal initValue = taskNodeMap.getBigDecimal(Constants.PinitValue);
		BigDecimal finalValue = taskNodeMap
				.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = taskNodeMap.getBigDecimal(Constants.Pstep);
		String unit = DateUtils.nullToEmpty(taskNodeMap
				.getString(Constants.Punit));
		String multiId = taskNodeMap.getString(Constants.PmultiId);
		String ptype = taskNodeMap.getString("ptype");
		String qtype = taskNodeMap.getString("qtype");
		String priceUser = taskNodeMap.getString("priceUser");

		int sign = step.compareTo(new BigDecimal("0"));
		if (Constants.LFocus.equals(ptype)
				|| (Constants.LLimit.equals(ptype) && Constants.QPrice
						.equals(qtype))) {
			if (price != null
					&& finalValue != null
					&& (price.compareTo(finalValue) == sign || price
							.compareTo(finalValue) == 0)) {
				// 当前价格已经是封顶价
				// 找到还没有同意的并且委托价格超过封顶价的静脉申请
				List list = this
						.getTrustPriceLicenseNotAgree(
								targetId,
								taskNodeId,
								finalValue,
								step.compareTo(new BigDecimal("0")) == 1 ? " and tl.trust_price >= :mustPrice "
										: " and tl.trust_price <= :mustPrice ");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						String userId = ((ParaMap) list.get(i))
								.getString("user_id");
						engineDao.updateUserAgree(targetId, taskNodeId, userId,
								1);// 同意进入
					}
				}
				return;
			} else {
				// 当前价格不是封顶价
				// 得到必须的出价

				int isFinal = 0;
				BigDecimal mustPrice = this.getMustPrice(price, initValue,
						finalValue, step);
				if (finalValue != null
						&& (mustPrice.compareTo(finalValue) == sign || mustPrice
								.compareTo(finalValue) == 0)) {
					if (sign == 1) {
						if (finalValue.compareTo(new BigDecimal("0")) > 0) {
							mustPrice = finalValue;
							isFinal = 1;
						}
					} else {
						mustPrice = finalValue;
						isFinal = 1;
					}
				}
				// 得到有资格的委托出价的竞买人
				List list = this
						.getTrustPriceLicenseList(
								targetId,
								mustPrice,
								sign == 1 ? " and tl.trust_price >= :mustPrice order by tl.trust_price asc, tl.id asc "
										: " and tl.trust_price <= :mustPrice order by tl.trust_price desc , tl.id asc ");
				if (list != null && list.size() > 0) {
					if (isFinal == 1) {// 如果该轮出价是出封顶价
						for (int i = 0; i < list.size(); i++) {
							String userId = ((ParaMap) list.get(i))
									.getString("user_id");
							if (i == 0) {
								this.savePrice(targetId, userId,
										this.formatPrice(mustPrice, unit),
										null, "自动委托出封顶价", ptype, qtype, "1");// 第一个人出封顶价
							} else {
								engineDao.updateUserAgree(targetId, taskNodeId,
										userId, 1);// 其他人同意进入
							}
						}
						return;
					} else {
						// 不是要出封顶价
						if (list.size() == 1) {
							// 只有一个人资格的话，如果前一个出价是他出的价，他将不再出价，直接跳出，否者他再出一次价然后跳出
							String userId = ((ParaMap) list.get(0))
									.getString("user_id");
							if (priceUser == null || !priceUser.equals(userId)) {
								this.savePrice(targetId, userId,
										this.formatPrice(mustPrice, unit),
										null, "自动委托出价", ptype, qtype, "1");
							}
							return;
						} else {
							// 多于一个人的时候，随机找一个人做有效，其他人做无效报价，然后递归调用本方法，进入下一轮报价
							Lottery lottery = new Lottery();
							int listIndex = lottery.hit(list.size());
							this.savePrice(targetId, ((ParaMap) list
									.get(listIndex)).getString("user_id"), this
									.formatPrice(mustPrice, unit), null,
									"自动委托出价", ptype, qtype, "1");
							for (int i = 0; i < list.size(); i++) {
								if (i != listIndex) {
									this.savePrice(targetId, ((ParaMap) list
											.get(i)).getString("user_id"), this
											.formatPrice(mustPrice, unit),
											null, "自动委托出价", ptype, qtype, "1");
								}
							}
							this.saveTrustPrice(targetId, processId);
						}
					}
				}
			}
		}
		return;
	}

	public String saveOfferLog(String licenseId, String multiId,
			BigDecimal price, int status, String targetId, String userId,
			String remark, String isTrust) throws Exception {
		int isT = 0;
		if (StrUtils.isNotNull(isTrust)) {
			isT = Integer.parseInt(isTrust);
		}
		PreparedStatement pstm = null;
		String sql = "";
		sql = "insert into trans_offer_log(id,license_id,multi_trade_id,price,offer_date,status,type,increase,create_user_id,remark,is_trust) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		String offerId = IDGenerator.newGUID();
		long sysTimeLong = DateUtils.nowTime();
		pstm.setString(1, offerId);
		pstm.setString(2, licenseId);
		pstm.setString(3, StrUtils.isNull(multiId) ? "" : multiId);
		pstm.setBigDecimal(4, price);
		pstm.setTimestamp(5, new Timestamp(sysTimeLong));
		pstm.setInt(6, status);
		pstm.setInt(7, 1);
		pstm.setInt(8, getOfferIncrease(targetId, sysTimeLong));
		pstm.setString(9, userId);
		pstm.setString(10, remark);
		pstm.setInt(11, isT);
		pstm.executeUpdate();
		pstm.close();
		return offerId;
	}

	public void updateMulti(String qtype, BigDecimal price, String targetId)
			throws Exception {
		PreparedStatement pstm = null;
		String sql = "";
		sql = "update trans_target_multi_trade set success_value = ? where target_id = ? and type = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setBigDecimal(1, price);
		pstm.setString(2, targetId);
		pstm.setString(3, qtype);
		pstm.executeUpdate();
		pstm.close();
	}

	public void updateAgreeNext(String taskNodeId, String userId)
			throws Exception {
		PreparedStatement pstm = null;
		String sql = "";
		sql = "update wf_tasknodeuser set isagree = ? , agreeturn = ? "
				+ "where tasknodeid = ? " + "and type = ? "
				+ "and right_type = ? " + "and value = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, "1");
		pstm.setString(2, "1");
		pstm.setString(3, taskNodeId);
		pstm.setString(4, String.valueOf(Constants.DataTypePerson));
		pstm.setString(5, String.valueOf(Constants.RightExecute));
		pstm.setString(6, userId);
		pstm.executeUpdate();
		pstm.close();
	}

	public ParaMap deleteWfParameter(String refid, String name)
			throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("refid", refid);
		sqlMap.put("name", name);
		ParaMap out = dao.deleteSimpleData("wf_parameter", sqlMap);
		return out;
	}

	public ParaMap saveWfParameter(String refid, String name, String value)
			throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("id", "");
		sqlMap.put("refid", refid);
		sqlMap.put("name", name);
		sqlMap.put("value", value);
		ParaMap out = dao.updateData("wf_parameter", "id", sqlMap);
		return out;
	}

	/**
	 * 判断是否是有效用户
	 * 
	 * @param taskNodeId
	 * @param userId
	 * @return 0 不是有效用户 1可写 2 只读
	 * @throws Exception
	 */
	public ParaMap isTaskNodeUser(String taskNodeId, String userId)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("tasknodeid", taskNodeId);
		keyData.put("type", Constants.DataTypePerson);
		keyData.put("value", userId);
		ParaMap dataMap = dataSetDao
				.querySimpleData("wf_tasknodeuser", keyData);
		int rightType = 0;
		int isAgree = 0;
		String agreeTurn = "";
		if (dataMap.getSize() > 0) {
			rightType = (int) Math.ceil(Double.parseDouble(String
					.valueOf(dataMap.getRecordValue(0, "right_type"))));
			String isAgree_ = String.valueOf(dataMap.getRecordValue(0,
					"isagree"));
			isAgree = isAgree_ == null || "".equals(isAgree_)
					|| isAgree_.equalsIgnoreCase("null") ? 0 : (int) Math
					.ceil(Double.parseDouble(isAgree_));
			agreeTurn = (String) dataMap.getRecordValue(0, "agreeturn");
		}
		ParaMap out = new ParaMap();
		out.put("rightType", rightType);
		out.put("isAgree", isAgree);
		out.put("agreeTurn", agreeTurn);
		return out;
	}

	/**
	 * 得到价格的类型 0 无效出价 1 有效出价 2成交价（该函数不提供） 3 超出封顶价 -1失败
	 * 
	 * @param price
	 * @param lastPrice
	 * @param beginPrice
	 * @param finalPrice
	 * @param step
	 * @return
	 * @throws Exception
	 */
	public int getPriceStatus(BigDecimal price, BigDecimal lastPrice,
			BigDecimal beginPrice, BigDecimal finalPrice, BigDecimal step)
			throws Exception {
		int result = 0;
		int sign = step.compareTo(new BigDecimal("0"));// 得到是升序还是降序 sign -1 降序 1
														// 升序 。最终价为null 就是升序。
		BigDecimal mustPrice = lastPrice == null ? beginPrice : (lastPrice
				.add(step));// 是否已经出价，得到应出价
		// 出价和必须出价比较，相等、大于（升序）或小于（降序）都是有效出价
		if (price.compareTo(mustPrice) == sign
				|| price.compareTo(mustPrice) == 0) { // 所出价大于等于必出价
			if (finalPrice != null) {
				if (sign == 1 && finalPrice.compareTo(new BigDecimal("0")) <= 0) {// (增价幅度上涨，封顶价却小于等于0，认为是没有封顶价)
					result = 1;// 可出价
				} else {
					if (price.compareTo(finalPrice) == sign) {
						result = 3; // 价格大于封顶价
					} else if (price.compareTo(finalPrice) == 0) {
						result = 2; // 价格等于封顶价
					} else {
						result = 1; // 价格小于封顶价 可出
					}
				}
			} else {
				result = 1;// 可出价
			}
		} else {
			// 所出价小于必出价
			if (finalPrice != null) {
				if (sign == 1 && finalPrice.compareTo(new BigDecimal("0")) <= 0) {// (增价幅度上涨，封顶价却小于等于0，认为是没有封顶价)
					result = 0;// 不可出价
				} else {
					if (price.compareTo(finalPrice) == sign) {
						result = 3;// 价格大于封顶价
					} else if (price.compareTo(finalPrice) == 0) {
						result = 2;// 价格等于封顶价
					} else {
						result = 0;// 不可出价
					}
				}
			} else {
				result = 0;// 不可出价
			}
		}
		return result;
	}

	/**
	 * 得到必要的出价，如果大于封顶价则返回封顶价
	 * 
	 * @param lastPrice
	 * @param beginPrice
	 * @param finalPrice
	 * @param step
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getMustPrice(BigDecimal lastPrice, BigDecimal beginPrice,
			BigDecimal finalPrice, BigDecimal step) throws Exception {
		// 得到是升序还是降序 sign -1 降序 1 升序 。最终价为null 就是升序。
		int sign = step.compareTo(new BigDecimal("0"));
		// 是否已经出价，得到应出价
		BigDecimal mustPrice = lastPrice == null ? beginPrice : (lastPrice
				.add(step));
		return mustPrice;
		// if(finalPrice != null){
		// if(mustPrice.compareTo(finalPrice) == sign){//如果必须出价大于封顶价，则返回封顶价
		// return finalPrice;
		// }else{
		// return mustPrice;
		// }
		// }else{
		// return mustPrice;
		// }
	}

	public BigDecimal getUnitPrice(BigDecimal lastPrice, BigDecimal beginPrice,
			String goods_area) throws Exception {
		BigDecimal price = beginPrice;
		if (lastPrice != null) {
			price = lastPrice;
		}
		BigDecimal area = new BigDecimal("1");
		if (StrUtils.isNotNull(goods_area)) {
			try {
				area = new BigDecimal(goods_area);
			} catch (Exception e) {
				e.printStackTrace();
				area = new BigDecimal("1");
			}
		}
		return price.divide(area, 2, BigDecimal.ROUND_HALF_UP);

	}

	/**
	 * 根据targetId得到出价相关的参数
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getRelevantAmountParam(String targetId) throws Exception {
		ParaMap out = new ParaMap();

		EngineDao engineDao = new EngineDao();
		// 得到流程id
		String processId = engineDao.getProcessId(targetId);

		// 得到taskNode相关信息（关联tasknodeuser表，判断有无出价资格）
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		String nextId = taskNodeMap.getString("nextid");
		int isLast = nextId == null
				|| nextId.equals(targetId + Constants.NodeEndChar) ? 1 : 0; // 1最后一个指标
																			// ，
																			// 0
																			// 非最后一个指标
		BigDecimal pice = taskNodeMap.getBigDecimal(Constants.Pprice);
		BigDecimal initValue = taskNodeMap.getBigDecimal(Constants.PinitValue);
		BigDecimal finalValue = taskNodeMap
				.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = taskNodeMap.getBigDecimal(Constants.Pstep);
		String unit = DateUtils.nullToEmpty(taskNodeMap
				.getString(Constants.Punit));
		String multiId = taskNodeMap.getString(Constants.PmultiId);
		String ptype = taskNodeMap.getString("ptype");
		String qtype = taskNodeMap.getString("qtype");

		out.put(Constants.Pprice, pice);
		out.put(Constants.PinitValue, initValue);
		out.put(Constants.PfinalValue, finalValue);
		out.put(Constants.Pstep, step);
		out.put(Constants.Punit, unit);
		out.put(Constants.PmultiId, multiId);
		out.put("tasknodeid", taskNodeId);
		out.put("ptype", ptype);
		out.put("qtype", qtype);
		out.put("qtype", qtype);
		out.put("isLast", isLast);

		return out;
	}

	public ParaMap getTargets(String userId, String targetId) throws Exception {

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_active_targets_plow");
		sqlParams.put("type", Constants.DataTypePerson);
		sqlParams.put("userId", userId);
		ParaMap targets = dataSetDao.queryData(sqlParams);
		List list = new ArrayList();
		if (targets.getSize() > 0) {
			int size = targets.getSize();
			int flag = 0;
			if (!DateUtils.isNull(targetId)) {
				list.add(new ParaMap());
				flag = 1;
			}
			for (int i = 0; i < size; i++) {
				String id = (String) targets.getRecordValue(i, "targetid");
				String targetName = (String) targets.getRecordValue(i,
						"targetname");
				String targetNo = (String) targets
						.getRecordValue(i, "targetno");
				String name = (String) targets.getRecordValue(i, "name");
				String ptype = (String) targets.getRecordValue(i, "ptype");
				String qtype = (String) targets.getRecordValue(i, "qtype");
				String righttype = (String) targets.getRecordValue(i,
						"righttype");
				int wpState = targets.getRecordInt(i, "wp_state");
				int isSuspend = targets.getRecordInt(i, "is_suspend");
				int status = (int) Math.ceil(Double.parseDouble(String
						.valueOf(targets.getRecordValue(i, "status"))));
				if (wpState == 2) {
					ptype = "Wait";
				}
				if (isSuspend == 1) {
					ptype = "Pause";
				}
				if (status == 5 || status == 7 || status == 8 || status == 9) {
					ptype = "Ok";
				} else if (status == 6) {
					ptype = "Abort";
				}
				// ParaMap targetMap = new ParaMap();
				ParaMap pm = new ParaMap();
				pm.put("targetId", id);
				pm.put("targetName", targetName);
				pm.put("targetNo", targetNo);
				pm.put("pName", name);
				pm.put("ptype", ptype);
				pm.put("qtype", qtype);
				pm.put("rightType", righttype);
				// targetMap.put(id, pm);
				if (!DateUtils.isNull(targetId) && targetId.equals(id)) {
					list.set(0, pm);
					flag = 0;
				} else {
					list.add(pm);
				}
			}
			if (flag == 1) {
				list.remove(0);
			}
		}
		ParaMap out = new ParaMap();
		out.put("targetList", list);
		return out;
	}

	public ParaMap getTargetsForMonitor() throws Exception {
		String sql = "	select distinct tt.id as targetid,tt.name as targetname,tt.no as targetno,wn.name,wn.ptype,wn.qtype ,wtu.right_type as righttype,tt.status,tt.is_suspend, "
				+ "abs((sysdate - tt.begin_limit_time)*24*60*60) as sort "
				+ "from wf_process wp "
				+ "left join wf_tasknode wt  on wp.tasknodeid = wt.id and wp.id = wt.processid "
				+ "left join wf_node wn on wt.nodeid = wn.id "
				+ "left join wf_tasknodeuser wtu on wt.id = wtu.tasknodeid "
				+ "left join trans_target tt on wp.templateid = tt.id "
				+ "where tt.is_valid = 1  and tt.is_stop = 0 "
				+ "and tt.business_type like '501%'  "
				+ "and (wp.state in (1,50) or (tt.status in (3,4) and tt.is_suspend=1)) "
				+ "order by  sort asc , tt.id";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		ParaMap targets = dataSetDao.queryData(sqlParams);
		List list = new ArrayList();
		if (targets.getSize() > 0) {
			int size = targets.getSize();
			for (int i = 0; i < size; i++) {
				String id = (String) targets.getRecordValue(i, "targetid");
				String targetName = (String) targets.getRecordValue(i,
						"targetname");
				String targetNo = (String) targets
						.getRecordValue(i, "targetno");
				String name = (String) targets.getRecordValue(i, "name");
				String ptype = (String) targets.getRecordValue(i, "ptype");
				String qtype = (String) targets.getRecordValue(i, "qtype");
				String righttype = (String) targets.getRecordValue(i,
						"righttype");
				int isSuspend = targets.getRecordInt(i, "is_suspend");
				int status = (int) Math.ceil(Double.parseDouble(String
						.valueOf(targets.getRecordValue(i, "status"))));
				if (isSuspend == 1) {
					ptype = "Pause";
				}
				if (status == 5 || status == 7 || status == 8 || status == 9) {
					ptype = "Ok";
				} else if (status == 6) {
					ptype = "Abort";
				}
				// ParaMap targetMap = new ParaMap();
				ParaMap pm = new ParaMap();
				pm.put("targetId", id);
				pm.put("targetName", targetName);
				pm.put("targetNo", targetNo);
				pm.put("pName", name);
				pm.put("ptype", ptype);
				pm.put("qtype", qtype);
				pm.put("rightType", righttype);
				// targetMap.put(id, pm);
				list.add(pm);
			}
		}
		ParaMap out = new ParaMap();
		out.put("targetList", list);
		return out;
	}

	public ParaMap getTradeInfo(String targetId, String userId, int count)
			throws Exception {

		// 得到标的信息
		ParaMap target = this.getTargetMap(targetId);
		// 得到交易类型名称
		String businessName = ConfigDao.getBusinessTypeLabel(target
				.getString("business_type"));
		target.put("business_type_name", businessName);
		// 判断是否已成交
		int tempStatus = target.getInt("status");
		if (tempStatus == 5 || tempStatus == 7 || tempStatus == 8
				|| tempStatus == 9) {
			return getOkInfo(targetId, userId, count, target);
		} else if (target.getInt("status") == 6) {
			return getAbortInfo(targetId, userId, count, target);
		}

		ParaMap phaseMap = new ParaMap();
		ParaMap targetMap = new ParaMap();
		ParaMap listMap = new ParaMap();
		ParaMap out = new ParaMap();

		EngineDao engineDao = new EngineDao();
		// 得到流程id
		String processId = engineDao.getProcessId(targetId);

		// 得到taskNode相关信息
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		String nodeId = taskNodeMap.getString("nodeid");
		String unit = taskNodeMap.getString(Constants.Punit);
		String ptype = taskNodeMap.getString("ptype");
		String qtype = taskNodeMap.getString("qtype");
		String wpstate = taskNodeMap.getString("wpstate");

		phaseMap.put(Constants.Punit, unit);
		phaseMap.put("decimalNumber", this.getPriceDecimalNumber(unit));
		phaseMap.put("ptype", ptype);
		phaseMap.put("qtype", qtype);
		if (ptype.equals(Constants.LNotice)) {
			phaseMap.put("pname", "公告期");
		} else if (ptype.equals(Constants.LFocus)) {
			phaseMap.put("pname", "集中报价期");
		} else if (ptype.equals(Constants.LLimit) && "Q01".equals(qtype)) {
			phaseMap.put("pname", "限时竞价期");
		} else if (ptype.equals("L04") && "Q04".equals(qtype)) {
			phaseMap.put("pname", "摇号");
		}

		// 得到倒计时
		Date sysDate = DateUtils.now();
		if (wpstate.equals("2")) {
			String beginTime = taskNodeMap.getString(Constants.PbeginTime);
			if (!DateUtils.isNull(beginTime)) {
				long restMillisecond = DateUtils.getDate(beginTime).getTime()
						- sysDate.getTime();
				phaseMap.put("restMillisecond", restMillisecond);
				String restTimeStr = DateUtils.sub(sysDate,
						DateUtils.getDate(beginTime));
				if (restTimeStr.startsWith("-"))
					restTimeStr = "正在处理中...";
				phaseMap.put("restTime", restTimeStr);
				// phaseMap.put("process",
				// DateUtils.getProcess(DateUtils.getDate(startTime),
				// DateUtils.getDate(endTime)));
				phaseMap.put(Constants.PbeginTime + "Str", beginTime);
			}
		} else {
			if (!DateUtils.isNull(taskNodeMap.getString(Constants.PbeginTime))
					&& !DateUtils.isNull(taskNodeMap
							.getString(Constants.PendTime))) {
				String beginTime = taskNodeMap.getString(Constants.PbeginTime);
				String endTime = taskNodeMap.getString(Constants.PendTime);
				String priceTime = taskNodeMap.getString(Constants.PpriceTime);
				String startTime = beginTime;
				if (ptype.equals(Constants.LLimit)) {
					if (StrUtils.isNotNull(priceTime)
							&& DateUtils.getDate(priceTime).getTime() >= DateUtils
									.getDate(beginTime).getTime()) {
						startTime = priceTime;
					}
				}
				phaseMap.put("restTime",
						DateUtils.sub(sysDate, DateUtils.getDate(endTime)));
				phaseMap.put("process", DateUtils.getProcess(
						DateUtils.getDate(startTime),
						DateUtils.getDate(endTime)));
				phaseMap.put(Constants.PbeginTime + "Str", beginTime);
				phaseMap.put(Constants.PendTime + "Str", endTime);
				phaseMap.put(Constants.PpriceTime + "Str", priceTime);
				if (ptype.equals(Constants.LNotice)) {
					phaseMap.put(Constants.PbeginTime,
							this.getLeftString(beginTime, 16));
					phaseMap.put(Constants.PendTime,
							this.getLeftString(endTime, 16));
				} else {
					phaseMap.put(Constants.PbeginTime,
							this.getLeftString(beginTime, 19));
					phaseMap.put(Constants.PendTime,
							this.getLeftString(endTime, 19));
				}
				String nextBeginTime = this.getNodeBeginTime(taskNodeMap
						.getString("nextid"));
				phaseMap.put("nextBeginTime", nextBeginTime);
			}
		}
		BigDecimal lastPrice = taskNodeMap.getBigDecimal(Constants.Pprice);
		BigDecimal beginPrice = taskNodeMap.getBigDecimal(Constants.PinitValue);
		BigDecimal finalPrice = taskNodeMap
				.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = taskNodeMap.getBigDecimal(Constants.Pstep);
		// 得到必须的出价
		BigDecimal mustPrice = this.getMustPrice(lastPrice, beginPrice,
				finalPrice, step);
		// 得到当前单价
		BigDecimal unitPrice = this.getUnitPrice(lastPrice, beginPrice,
				target.getString("goods_area"));
		// 格式化金额
		phaseMap.put(Constants.Pprice, formatPrice(lastPrice, unit));
		phaseMap.put(Constants.PinitValue, formatPrice(beginPrice, unit));
		phaseMap.put(Constants.Pstep, formatPrice(step, unit));
		phaseMap.put("mustValue", formatPrice(mustPrice, unit));
		phaseMap.put("priceUser", taskNodeMap.getString("priceUser"));
		phaseMap.put("unitPrice", formatPrice(unitPrice, unit));
		phaseMap.put(Constants.Pprice + "Str", formatLastMoney(lastPrice, unit));
		phaseMap.put(Constants.PinitValue + "Str",
				formatMoney(beginPrice, unit));
		phaseMap.put(Constants.Pstep + "Str", formatMoney(step, unit));
		phaseMap.put("mustValue" + "Str", formatMoney(mustPrice, unit));
		phaseMap.put("unitPrice" + "Str", formatMoney(unitPrice, unit) + "/亩");
		// 得到是否是有权限的竞买人
		ParaMap tasknodeuser = this.isTaskNodeUser(taskNodeId, userId);
		phaseMap.put("rightType", tasknodeuser.getInt("rightType"));
		phaseMap.put("isAgree", tasknodeuser.getInt("isAgree"));
		phaseMap.put("agreeTurn", tasknodeuser.getString("agreeTurn"));
		// 得到是否该指标已结束（弹出同意与否）
		if (finalPrice != null && lastPrice != null) {
			int sign = step.compareTo(new BigDecimal("0"));
			if (target.getString("business_type").equals("501006")) {
				if (lastPrice.compareTo(finalPrice) == sign
						|| lastPrice.compareTo(finalPrice) == 0) {
					phaseMap.put("isEnd", 1);
					phaseMap.put(Constants.PfinalValue + "Str",
							formatMoney(finalPrice, unit));
				} else {
					phaseMap.put("isEnd", 0);
				}
			} else if (target.getString("business_type").equals("501005")) {
				if (finalPrice.compareTo(new BigDecimal("0")) <= 0) {
					phaseMap.put("isEnd", 0);
				} else {
					if (lastPrice.compareTo(finalPrice) == sign
							|| lastPrice.compareTo(finalPrice) == 0) {
						phaseMap.put("isEnd", 1);
						phaseMap.put(Constants.PfinalValue + "Str",
								formatMoney(finalPrice, unit));
					} else {
						phaseMap.put("isEnd", 0);
					}
				}
			}
		} else {
			phaseMap.put("isEnd", 0);
		}
		// 服务器时间
		targetMap.put("stdTime", DateUtils.getStr(sysDate));
		targetMap.put("stdMillisecond", sysDate.getTime() + "");

		// 得到人员信息
		targetMap.putAll(this.getBidderInfo(userId));
		targetMap.putAll(this.getMainBidderInfo(targetId,
				targetMap.getString("bidderid")));
		ParaMap licenseMap = this.getLicenseInfo(targetId,
				targetMap.getString("bidderid"));
		targetMap.put("licenseId", licenseMap.get("id"));
		BigDecimal trustPrice = licenseMap.getBigDecimal("trust_price");
		if (trustPrice == null
				|| trustPrice.compareTo(new BigDecimal("0")) <= 0) {
			targetMap.put("trustPrice", "未设置委托价格");
		} else {
			targetMap.put("trustPrice", formatMoney(trustPrice, unit));
		}

		// 如果是进入了摇号时间，需要展示能够摇号的竞买人列表
		if (StrUtils.isNotNull(qtype) && qtype.equals("Q04")) {
			List list = this.getTaskNodeUserList(targetId, taskNodeId);
			ParaMap lotteryMap = new ParaMap();
			lotteryMap.put("lotteryList", list);
			if (list != null && list.size() > 0) {
				long advanceSecond = 30;
				String advanceSecondStr = AppConfig
						.getPro("lottery_success_second");
				if (StrUtils.isNotNull(advanceSecondStr)) {
					try {
						advanceSecond = Long.parseLong(advanceSecondStr);
					} catch (Exception e) {
						advanceSecond = 30;
					}
				}
				String endTime = taskNodeMap.getString(Constants.PendTime);
				if (StrUtils.isNotNull(endTime)
						&& target.getInt("is_online") == 1) {
					if (DateUtils.nowTime() < (DateUtils.getDate(endTime)
							.getTime() - advanceSecond * 1000L)) {
						lotteryMap.put("state", "start");
					} else {
						lotteryMap.put("state", "stop");
						int succIndex = 0;
						String succLicenseId = taskNodeMap
								.getString("succLicenseId");
						String succBidder = "";
						if (StrUtils.isNull(succLicenseId)) {
							Lottery lottery = new Lottery();
							succIndex = lottery.hit(list.size());
							succLicenseId = ((ParaMap) list.get(succIndex))
									.getString("license_id");
							succBidder = ((ParaMap) list.get(succIndex))
									.getString("name");
							engineDao.updateParaMap(taskNodeId,
									"succLicenseId", succLicenseId);
						} else {
							for (int i = 0; i < list.size(); i++) {
								ParaMap lottMap = (ParaMap) list.get(i);
								String lottLicenseId = lottMap
										.getString("license_id");
								if (lottLicenseId.equals(succLicenseId)) {
									succIndex = i;
									succBidder = lottMap.getString("name");
									break;
								}
							}
						}
						lotteryMap.put("succIndex", succIndex);
						lotteryMap.put("succLicenseId", succLicenseId);
						lotteryMap.put("succBidder", succBidder);
					}
				}

			}
			out.put("lottery", lotteryMap);

			// if(list !=null && list.size()>0){
			// Lottery lottery = new Lottery();
			// int listIndex = lottery.hit(list.size());
			// ParaMap lotteryMap = new ParaMap();
			// lotteryMap.put("lotteryList", list);
			// lotteryMap.put("lotteryIndex", listIndex);
			// lotteryMap.put("lotteryNum",
			// ((ParaMap)list.get(listIndex)).getString("lottery_num"));
			// out.put("lottery", lotteryMap);
			// putLotteryMap(targetId,((ParaMap)list.get(listIndex)).getString("license_id"));
			// }
		} else {
			// 得到出价列表
			listMap = this.getBidList(targetId, count, 1);
			out.put("list", listMap);
		}

		// 得到标的相关信息
		targetMap.putAll(target);
		String targetName = targetMap.getString("target_name");
		String targetNo = targetMap.getString("target_no");
		targetName = targetName == null || "".equals(targetName)
				|| targetName.equalsIgnoreCase("null") ? targetNo : targetName;
		targetMap.put("targetName", targetName);
		targetMap.remove("target_name");
		targetMap.remove("target_no");
		targetMap.remove("bidderid");
		targetMap.remove("trans_price");
		targetMap.remove("end_trans_time");
		targetMap.remove("target_price_step");
		targetMap.remove("target_begin_price");
		targetMap.remove("begin_notice_time");

		if (target.getInt("is_suspend") == 1) {
			phaseMap.put("ptype", "Pause");
			phaseMap.put("pauseTime",
					taskNodeMap.getString(Constants.pstopTime));
		}

		out.put("target", targetMap);
		out.put("node", phaseMap);

		if (Constants.LLimit.equals(phaseMap.getString("ptype"))) {
			out.put("quotas", this.getQuotas(targetId));
		}
		return out;
	}

	public ParaMap getLotteryInfo(String targetId, String userId, boolean isSucc)
			throws Exception {

		// 得到标的信息
		ParaMap target = this.getTargetMap(targetId);
		// 得到交易类型名称
		String businessName = ConfigDao.getBusinessTypeLabel(target
				.getString("business_type"));
		target.put("business_type_name", businessName);

		ParaMap phaseMap = new ParaMap();
		ParaMap targetMap = new ParaMap();
		ParaMap out = new ParaMap();

		EngineDao engineDao = new EngineDao();
		// 得到流程id
		String processId = engineDao.getProcessId(targetId);

		// 得到taskNode相关信息
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		String nodeId = taskNodeMap.getString("nodeid");
		String unit = taskNodeMap.getString(Constants.Punit);
		String ptype = taskNodeMap.getString("ptype");
		String qtype = taskNodeMap.getString("qtype");

		phaseMap.put(Constants.Punit, unit);
		phaseMap.put("decimalNumber", this.getPriceDecimalNumber(unit));
		phaseMap.put("ptype", ptype);
		phaseMap.put("qtype", qtype);
		phaseMap.put("pname", "摇号");

		// 得到倒计时
		Date sysDate = DateUtils.now();
		if (!DateUtils.isNull(taskNodeMap.getString(Constants.PbeginTime))
				&& !DateUtils.isNull(taskNodeMap.getString(Constants.PendTime))) {
			String beginTime = taskNodeMap.getString(Constants.PbeginTime);
			String endTime = taskNodeMap.getString(Constants.PendTime);
			String priceTime = taskNodeMap.getString(Constants.PpriceTime);
			String startTime = beginTime;
			if (StrUtils.isNotNull(priceTime)
					&& DateUtils.getDate(priceTime).getTime() >= DateUtils
							.getDate(beginTime).getTime()) {
				startTime = priceTime;
			}
			long advanceSecond = -30;
			endTime = this.timeAddSecond(endTime, advanceSecond);
			phaseMap.put("restTime",
					DateUtils.sub(sysDate, DateUtils.getDate(endTime)));
			phaseMap.put("process", DateUtils.getProcess(
					DateUtils.getDate(startTime), DateUtils.getDate(endTime)));
			phaseMap.put(Constants.PbeginTime + "Str", beginTime);
			phaseMap.put(Constants.PendTime + "Str", endTime);
			phaseMap.put(Constants.PpriceTime + "Str", priceTime);
			phaseMap.put(Constants.PbeginTime,
					this.getLeftString(beginTime, 19));
			phaseMap.put(Constants.PendTime, this.getLeftString(endTime, 19));
			String nextBeginTime = this.getNodeBeginTime(taskNodeMap
					.getString("nextid"));
			phaseMap.put("nextBeginTime", nextBeginTime);
		}
		BigDecimal lastPrice = taskNodeMap.getBigDecimal(Constants.Pprice);
		BigDecimal beginPrice = taskNodeMap.getBigDecimal(Constants.PinitValue);
		BigDecimal finalPrice = taskNodeMap
				.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = taskNodeMap.getBigDecimal(Constants.Pstep);
		// 得到当前单价
		BigDecimal unitPrice = this.getUnitPrice(lastPrice, beginPrice,
				target.getString("goods_area"));
		// 格式化金额
		phaseMap.put(Constants.Pprice, formatPrice(lastPrice, unit));
		phaseMap.put(Constants.PinitValue, formatPrice(beginPrice, unit));
		phaseMap.put(Constants.Pstep, formatPrice(step, unit));

		phaseMap.put("priceUser", taskNodeMap.getString("priceUser"));
		phaseMap.put("unitPrice", formatPrice(unitPrice, unit));
		phaseMap.put(Constants.Pprice + "Str", formatLastMoney(lastPrice, unit));
		phaseMap.put(Constants.PinitValue + "Str",
				formatMoney(beginPrice, unit));
		phaseMap.put(Constants.Pstep + "Str", formatMoney(step, unit));
		phaseMap.put("unitPrice" + "Str", formatMoney(unitPrice, unit) + "/亩");
		// 得到是否是有权限的竞买人
		ParaMap tasknodeuser = this.isTaskNodeUser(taskNodeId, userId);
		phaseMap.put("rightType", tasknodeuser.getInt("rightType"));
		phaseMap.put("isAgree", tasknodeuser.getInt("isAgree"));
		phaseMap.put("agreeTurn", tasknodeuser.getString("agreeTurn"));
		phaseMap.put("isEnd", 0);

		// 服务器时间
		targetMap.put("stdTime", DateUtils.getStr(sysDate));
		targetMap.put("stdMillisecond", sysDate.getTime() + "");

		// 得到人员信息
		targetMap.putAll(this.getBidderInfo(userId));
		targetMap.putAll(this.getMainBidderInfo(targetId,
				targetMap.getString("bidderid")));
		ParaMap licenseMap = this.getLicenseInfo(targetId,
				targetMap.getString("bidderid"));
		targetMap.put("licenseId", licenseMap.get("id"));

		// 摇号信息
		List list = this.getTaskNodeUserList(targetId, taskNodeId);
		ParaMap lotteryMap = new ParaMap();
		lotteryMap.put("lotteryList", list);
		out.put("lottery", lotteryMap);
		if (isSucc && list != null && list.size() > 0) {
			int succIndex = 0;
			String succLicenseId = taskNodeMap.getString("succLicenseId");
			if (StrUtils.isNull(succLicenseId)) {
				Lottery lottery = new Lottery();
				succIndex = lottery.hit(list.size());
				engineDao
						.updateParaMap(taskNodeId, "succLicenseId",
								((ParaMap) list.get(succIndex))
										.getString("license_id"));
			} else {
				for (int i = 0; i < list.size(); i++) {
					ParaMap lottMap = (ParaMap) list.get(i);
					String lottLicenseId = lottMap.getString("license_id");
					if (lottLicenseId.equals(succLicenseId)) {
						succIndex = i;
						break;
					}
				}
			}
			phaseMap.put("succIndex", "succIndex");
		}

		// 得到标的相关信息
		targetMap.putAll(target);
		String targetName = targetMap.getString("target_name");
		String targetNo = targetMap.getString("target_no");
		targetName = targetName == null || "".equals(targetName)
				|| targetName.equalsIgnoreCase("null") ? targetNo : targetName;
		targetMap.put("targetName", targetName);
		targetMap.remove("target_name");
		targetMap.remove("target_no");
		targetMap.remove("bidderid");
		targetMap.remove("trans_price");
		targetMap.remove("end_trans_time");
		targetMap.remove("target_price_step");
		targetMap.remove("target_begin_price");
		targetMap.remove("begin_notice_time");

		if (target.getInt("is_suspend") == 1) {
			phaseMap.put("ptype", "Pause");
			phaseMap.put("pauseTime",
					taskNodeMap.getString(Constants.pstopTime));
		}

		out.put("target", targetMap);
		out.put("node", phaseMap);
		return out;
	}

	public List getQuotas(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("templateid", targetId);
		keyData.put("ptype", "L03");
		ParaMap dataMap = dataSetDao.querySimpleData("wf_node", keyData);
		List qoutaList = new ArrayList();
		if (dataMap.getSize() > 0) {
			int mapSize = dataMap.getSize();
			List idList = new ArrayList();
			List nextIdList = new ArrayList();
			String firstId = null;
			for (int i = 0; i < mapSize; i++) {
				String id = (String) dataMap.getRecordValue(i, "id");
				idList.add(id);
				String nextid = (String) dataMap.getRecordValue(i, "nextid");
				nextIdList.add(nextid);
			}
			for (int i = 0; i < mapSize; i++) {
				String tempId = (String) idList.get(i);
				int index = nextIdList.indexOf(tempId);
				if (index < 0) {
					firstId = tempId;
					break;
				}
			}
			String thisId = firstId;
			for (int i = 0; i < mapSize; i++) {
				ParaMap pMap = new ParaMap();
				for (int j = 0; j < mapSize; j++) {
					String id = (String) dataMap.getRecordValue(j, "id");
					if (id.equals(thisId)) {
						String nextid = (String) dataMap.getRecordValue(j,
								"nextid");
						String name = (String) dataMap
								.getRecordValue(j, "name");
						String qtype = (String) dataMap.getRecordValue(j,
								"qtype");
						pMap.put("name", name);
						pMap.put("qtype", qtype);
						thisId = nextid;
						break;
					}
				}
				qoutaList.add(pMap);
			}

		}
		return qoutaList;
	}

	public ParaMap getOkInfo(String targetId, String userId, int count,
			ParaMap targetMap) throws Exception {
		ParaMap out = new ParaMap();
		// 得到标的信息

		String targetName = targetMap.getString("target_name");
		String targetNo = targetMap.getString("target_no");
		targetName = targetName == null || "".equals(targetName)
				|| targetName.equalsIgnoreCase("null") ? targetNo : targetName;

		BigDecimal beginPrice = targetMap.getBigDecimal("target_begin_price");
		BigDecimal step = targetMap.getBigDecimal("target_price_step");

		// String unit = queryWfParameter(targetId , "unit");
		// unit = unit == null || "".equals(unit) ||
		// unit.equalsIgnoreCase("null") ? "元" : unit;

		String unit = targetMap.getString("target_unit");

		// 格式化金额
		targetMap.put(Constants.PinitValue + "Str",
				formatMoney(beginPrice, unit));
		targetMap.put(Constants.Pstep + "Str", formatMoney(step, unit));
		targetMap.put("targetName", targetName);
		targetMap.put("beginTime", targetMap.getString("begin_notice_time"));
		targetMap.put("endTime", targetMap.getString("end_trans_time"));
		targetMap.put("succPrice",
				formatMoney(targetMap.getBigDecimal("trans_price"), unit));
		targetMap.remove("target_name");
		targetMap.remove("target_no");
		targetMap.remove("bidderid");
		targetMap.remove("trans_price");
		targetMap.remove("end_trans_time");
		targetMap.remove("target_price_step");
		targetMap.remove("target_begin_price");
		targetMap.remove("begin_notice_time");

		// 得到人员信息
		targetMap.putAll(this.getBidderInfo(userId));
		targetMap.putAll(this.getMainBidderInfo(targetId,
				targetMap.getString("bidderid")));
		// ParaMap licenseMap = this.getLicenseInfo(targetId,
		// targetMap.getString("bidderid"));
		// targetMap.put("licenseId", licenseMap.get("id"));
		// 得到成交人信息
		ParaMap okBidder = getOkBidder(targetId);
		targetMap.put("succlicense", okBidder.getString("succlicense"));
		ParaMap okUnionBidder = getOkUnionBidder(targetId);
		List succBidder = new ArrayList();
		succBidder.add(okBidder.getString("succbiddername"));
		if (okUnionBidder.getSize() > 0) {
			int okUnionBidderSize = okUnionBidder.getSize();
			for (int i = 0; i < okUnionBidderSize; i++) {
				succBidder.add(okUnionBidder.getRecordValue(i, "bidder_name"));
			}
		}
		targetMap.put("succbiddername", succBidder);

		targetMap.put("userId", userId);

		out.put("target", targetMap);
		// 得到出价列表
		out.put("list", this.getBidList(targetId, count, 1));

		return out;

	}

	public ParaMap getAbortInfo(String targetId, String userId, int count,
			ParaMap targetMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetName = targetMap.getString("target_name");
		String targetNo = targetMap.getString("target_no");
		targetName = targetName == null || "".equals(targetName)
				|| targetName.equalsIgnoreCase("null") ? targetNo : targetName;

		BigDecimal beginPrice = targetMap.getBigDecimal("target_begin_price");
		BigDecimal step = targetMap.getBigDecimal("target_price_step");

		String unit = queryWfParameter(targetId, "unit");
		unit = unit == null || "".equals(unit) || unit.equalsIgnoreCase("null") ? "元"
				: unit;

		// 格式化金额
		targetMap.put(Constants.PinitValue + "Str",
				formatMoney(beginPrice, unit));
		targetMap.put(Constants.Pstep + "Str", formatMoney(step, unit));
		targetMap.put("targetName", targetName);
		targetMap.put("beginTime", targetMap.getString("begin_notice_time"));
		targetMap.put("endTime", targetMap.getString("end_trans_time"));
		targetMap.remove("target_name");
		targetMap.remove("target_no");
		targetMap.remove("bidderid");
		targetMap.remove("trans_price");
		targetMap.remove("end_trans_time");
		targetMap.remove("target_price_step");
		targetMap.remove("target_begin_price");
		targetMap.remove("begin_notice_time");

		// 得到人员信息
		targetMap.putAll(this.getBidderInfo(userId));
		targetMap.putAll(this.getMainBidderInfo(targetId,
				targetMap.getString("bidderid")));
		// ParaMap licenseMap = this.getLicenseInfo(targetId,
		// targetMap.getString("bidderid"));
		// targetMap.put("licenseId", licenseMap.get("id"));

		int licenseNum = this.getEffectiveLicneseNum(targetId);
		if (licenseNum > 0) {
			int priceNum = this.getValidOfferLogLicenseNum(targetId);
			if (priceNum > 0) {
				BigDecimal maxPrice = this.getMaxPrice(targetId);
				BigDecimal reservePrice = this.getReservePrice(targetId);
				if (maxPrice != null && reservePrice != null) {
					if (reservePrice.compareTo(maxPrice) > 0) {
						targetMap.put("abortMessage", "最高有效报价未达到标的底价");
					}
				}
			} else {
				targetMap.put("abortMessage", "无人出价");
			}
		} else {
			targetMap.put("abortMessage", "无人申请竞买");
		}

		// 得到出价列表
		out.put("target", targetMap);
		// 得到出价列表
		out.put("list", this.getBidList(targetId, count, 1));
		return out;
	}

	public List getTaskNodeUserList(String targetId, String taskNodeId)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_target_tasknode_bidder_list");
		sqlParams.put("targetId", targetId);
		sqlParams.put("taskNodeId", taskNodeId);
		sqlParams.put("rightType", "1");
		return dataSetDao.queryData(sqlParams).getListObj();

	}

	public int getTaskNodeUserNum(String targetId) throws Exception {
		// 得到标的节点信息
		EngineDao engineDao = new EngineDao();
		// 得到流程id
		String processId = engineDao.getProcessId(targetId);
		// 得到taskNode相关信息
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);

		// 得到已经进入了多少人
		ParaMap userMap = engineDao.getTaskUsers(
				taskNodeMap.getString("tasknodeid"), 1);

		return userMap.getSize();

	}

	public void doEndScenceLottery(String targetId, String succLicenseId)
			throws Exception {
		// this.putLotteryMap(targetId, succLicenseId);
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessId(targetId);
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		engineDao.updateParaMap(taskNodeId, "succLicenseId", succLicenseId);
		ParaMap out = Engine.getPlowEngine().commit(Constants.Action_Next,
				taskNodeMap);
	}

	public void doEndTarget(String targetId, String succLicenseId)
			throws Exception {
		DecimalFormat df = new DecimalFormat("#############.##");
		ParaMap offerMap = this.getOfferLogList(targetId, 0, 1);
		String multiId = null;
		BigDecimal multiPrice = null;
		String offerId = null;
		int status = -1;
		if (offerMap.getSize() > 0) {
			for (int i = 0; i < offerMap.getSize(); i++) {
				String offerLicenseId = offerMap
						.getRecordString(i, "licenseid");
				if (offerLicenseId.equals(succLicenseId)) {
					multiPrice = new BigDecimal(df.format(Double
							.parseDouble(String.valueOf(offerMap
									.getRecordValue(i, "price")))));
					status = (int) Math.ceil(Double.parseDouble(String
							.valueOf(offerMap.getRecordValue(i, "status"))));
					multiId = (String) offerMap.getRecordValue(i,
							"multi_trade_id");
					offerId = (String) offerMap.getRecordValue(i, "id");
					break;
				}
			}
		}
		this.doOkTarget(targetId, succLicenseId, offerId, multiPrice, multiId,
				multiPrice, 1);
	}

	public void doEndTarget(String targetId) throws Exception {
		DecimalFormat df = new DecimalFormat("#############.##");
		ParaMap offerMap = this.getOfferLogList(targetId, 0, 1);
		if (offerMap.getSize() > 0) {
			// 得到最后价格相关信息
			BigDecimal multiPrice = new BigDecimal(df.format(Double
					.parseDouble(String.valueOf(offerMap.getRecordValue(0,
							"price")))));
			String multiId = (String) offerMap.getRecordValue(0,
					"multi_trade_id");
			String licenseId = (String) offerMap.getRecordValue(0, "licenseid");
			String offerId = (String) offerMap.getRecordValue(0, "id");

			BigDecimal price = multiPrice;
			ParaMap target = this.getTargetData(targetId);
			String reservePriceStr = String.valueOf(target.getRecordValue(0,
					"reserve_price"));
			String businessType = String.valueOf(target.getRecordValue(0,
					"business_type"));
			if (StrUtils.isNotNull(reservePriceStr)) {
				BigDecimal reservePrice = new BigDecimal(reservePriceStr);
				if (businessType.equals("501005")) {
					if (price.compareTo(reservePrice) == -1) { // 成交价小于低价流拍
						this.doAbortTarget(targetId);
					} else {
						this.doOkTarget(targetId, licenseId, offerId, price,
								multiId, multiPrice, 0);
					}
				} else {
					if (reservePrice.compareTo(new BigDecimal("0")) == 1
							&& price.compareTo(reservePrice) == 1) {// 成交价大于低价流拍
						this.doAbortTarget(targetId);
					} else {
						this.doOkTarget(targetId, licenseId, offerId, price,
								multiId, multiPrice, 0);
					}
				}
			} else {
				this.doOkTarget(targetId, licenseId, offerId, price, multiId,
						multiPrice, 0);
			}

		} else {
			this.doAbortTarget(targetId);// 没有出价流拍
		}
		DataSourceManager.commit();
		TradeService.invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeDao.doEndTarget()");
		sendSubmitChildNoXml(targetId);
		sendMessage(targetId);
	}

	public void doOkTarget(String targetId, String licenseId, String offerId,
			BigDecimal price, String multiId, BigDecimal multiPrice, int kind)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "";
		// 1.修改标的信息
		sql = "update trans_target set status = ? , trans_price  = ?,  end_trans_time = ?  where id = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 5);
		pstm.setBigDecimal(2, price);
		pstm.setTimestamp(3, new Timestamp(DateUtils.nowTime()));
		pstm.setString(4, targetId);
		pstm.executeUpdate();
		// 修改竞得人的状态
		sql = "update trans_license set status = ? , trans_price = ? , trans_date = ?  where target_id = ?  and id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 4);
		pstm.setBigDecimal(2, price);
		pstm.setTimestamp(3, new Timestamp(DateUtils.nowTime()));
		pstm.setString(4, targetId);
		pstm.setString(5, licenseId);
		pstm.executeUpdate();
		// 修改未竞得人状态
		sql = "update trans_license set status = ?  where target_id = ?  and status = ? and id != ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 2);
		pstm.setString(2, targetId);
		pstm.setInt(3, 1);
		pstm.setString(4, licenseId);
		pstm.executeUpdate();
		// 修改出价表信息
		sql = "update trans_offer_log set status = ? , kind = " + kind
				+ " where id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 2);
		pstm.setString(2, offerId);
		pstm.executeUpdate();
		if (StringUtils.isNotEmpty(multiId)) {
			// 修改指标表信息
			sql = "update trans_target_multi_trade set success_value = ?  where target_id = ?  and id = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setBigDecimal(1, multiPrice);
			pstm.setString(2, targetId);
			pstm.setString(3, multiId);
			pstm.executeUpdate();
		}
		// 查询互斥编号
		String rejectId = "";
		String bidderId = "";
		sql = "select tt.reject_id  , tl.bidder_id  "
				+ "from trans_target tt , trans_license tl "
				+ "where tt.id = tl.target_id and tt.id = ? and tl.id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, targetId);
		pstm.setString(2, licenseId);
		rs = pstm.executeQuery();
		if (rs.next()) {
			rejectId = rs.getString("reject_id");
			bidderId = rs.getString("bidder_id");
		}
		// 4.互斥
		if (rejectId != null && !"".equals(rejectId)) {
			this.doReject(targetId, rejectId, bidderId, licenseId);
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}

	}

	public void doAbortTarget(String targetId) throws Exception {
		PreparedStatement pstm = null;
		String sql = "";
		// 修改标的信息
		sql = " update trans_target set status = ? ,  end_trans_time = ? where id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 6);
		pstm.setTimestamp(2, new Timestamp(DateUtils.nowTime()));
		pstm.setString(3, targetId);
		pstm.executeUpdate();
		// 修改有效竞买申请为未成交
		sql = "update trans_license set status = ? where target_id = ?  and status = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setInt(1, 2);
		pstm.setString(2, targetId);
		pstm.setInt(3, 1);
		pstm.executeUpdate();
		pstm.close();
	}

	/**
	 * 得到标的信息
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargetMap(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_targetInfo_forBid_plow");
		sqlParams.put("id", targetId);
		ParaMap targetInfo = dataSetDao.queryData(sqlParams);
		return getRowOneInfo(targetInfo);
	}

	/**
	 * 得到标的信息
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargetData(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		return dataSetDao.querySimpleData("trans_target", keyData);
	}

	public ParaMap getMultiInfo(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_multi_trade_bytargetid");
		sqlParams.put("targetId", targetId);
		ParaMap multi = dataSetDao.queryData(sqlParams);
		return multi;
	}

	/**
	 * 得到当前竞买人的信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBidderInfo(String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap bidderParams = new ParaMap();
		bidderParams.put("moduleNo", "trans_trade_plow");
		bidderParams.put("dataSetNo", "get_bidderInfo_byuserid");
		bidderParams.put("userId", userId);
		ParaMap bidderMap = dataSetDao.queryData(bidderParams);
		return getRowOneInfo(bidderMap);
	}

	/**
	 * 联合竞买主竞买人id和所有联合竞买人的名字
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getMainBidderInfo(String targetId, String bidderId)
			throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "trans_trade_plow");
		keyData.put("dataSetNo", "get_licenseId_byBidderId");
		keyData.put("targetId", targetId);
		keyData.put("bidderId", bidderId);
		ParaMap licenseMap = dataSetDao.queryData(keyData);
		if (licenseMap.getSize() > 0) {
			String licenseId = (String) licenseMap.getRecordValue(0, "id");

			String mainBidderId = (String) licenseMap.getRecordValue(0,
					"bidder_id");
			keyData.clear();
			keyData.put("id", mainBidderId);
			keyData.put("is_valid", 1);
			ParaMap dataMap = dataSetDao.querySimpleData("trans_bidder",
					keyData);
			String mainBidderName = dataMap.getSize() > 0 ? (String) dataMap
					.getRecordValue(0, "name") : "";
			keyData.clear();
			keyData.put("license_id", licenseId);
			keyData.put("is_valid", 1);
			keyData.put("status", 1);
			dataMap = dataSetDao
					.querySimpleData("trans_license_union", keyData);
			String subBidderName = "";
			int mapSize = dataMap.getSize();
			if (mapSize > 0) {
				for (int i = 0; i < mapSize; i++) {
					subBidderName = subBidderName + ","
							+ dataMap.getRecordValue(i, "bidder_name");
				}
			}

			out.put("licenseId", licenseId);
			String bidderName = mainBidderName + subBidderName;
			out.put("biddername",
					bidderName.length() > 15 ? bidderName.substring(0, 20)
							+ "..." : bidderName);
			return out;
		} else {
			return out;
		}
	}

	/**
	 * 得到License的信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getLicenseInfo(String targetId, String bidderId)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("bidder_id", bidderId);
		keyData.put("is_valid", 1);
		ParaMap dataMap = dataSetDao.querySimpleData("trans_license", keyData);
		return getRowOneInfo(dataMap);
	}

	/**
	 * 得到竞买申请Id
	 * 
	 * @param targetId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getLicenseId(String targetId, String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_licenseInfo_byTagetIdUserId");
		sqlParams.put("userId", userId);
		sqlParams.put("targetId", targetId);
		ParaMap lMap = dataSetDao.queryData(sqlParams);
		String licenseId = "";
		if (lMap.getSize() > 0) {
			licenseId = (String) lMap.getRecordValue(0, "id");
		}
		return licenseId;
	}

	/**
	 * 得到竞得人id和name
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOkBidder(String targetId) throws Exception {
		// 标的信息
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_ok_bidder");
		sqlParams.put("targetId", targetId);
		ParaMap out = dataSetDao.queryData(sqlParams);
		return getRowOneInfo(out);
	}

	/**
	 * 得到联合竞得人id和name
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOkUnionBidder(String targetId) throws Exception {
		// 标的信息
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_ok_union_bidder");
		sqlParams.put("targetId", targetId);
		ParaMap out = dataSetDao.queryData(sqlParams);
		return out;
	}

	/**
	 * 得到互斥信息
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public void doReject(String targetId, String rejectId, String bidderId,
			String licenseId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_reject_licenseId");
		sqlParams.put("targetId", targetId);
		sqlParams.put("rejectId", rejectId);
		sqlParams.put("bidderId", bidderId);
		sqlParams.put("bidderId2", bidderId);
		sqlParams.put("licenseId", licenseId);
		sqlParams.put("licenseId2", licenseId);
		ParaMap data = dataSetDao.queryData(sqlParams);

		if (data.getSize() > 0) {
			for (int i = 0; i < data.getSize(); i++) {
				ParaMap pMap = new ParaMap();
				pMap.put("id", data.getRecordValue(i, "id"));
				pMap.put("status", 2);
				pMap.put("rejected", 1);
				dataSetDao.updateData("trans_license", "id", pMap, null);

				pMap = new ParaMap();
				pMap.put("moduleNo", "trans_trade_plow");
				pMap.put("dataSetNo", "update_offerlog_status");
				pMap.put("status", 0);
				pMap.put("licenseId", data.getRecordValue(i, "id"));
				dataSetDao.executeSQL(pMap);

			}

			// sql =
			// "update trans_offer_log set status = 0 where license_id = ? ";
			// pstm = getCon().prepareStatement(sql);
			// for(int i = 0 ; i < licenseList.size() ; i ++){
			// String _licenseId = (String)licenseList.get(i);
			// pstm.setString(1, _licenseId);
			// pstm.executeUpdate();
			// }
		}

	}

	public synchronized int getOfferIncrease(String targetId, long sysTimeLong)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_ffferLog_Increase");
		sqlParams.put("targetId", targetId);
		sqlParams.put("begintime", "1970-01-01 00:00:00");
		sqlParams.put("dataPattern", "yyyy-mm-dd hh24:mi:ss");
		sqlParams.put("sysTimeLong", sysTimeLong);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		int thisIncrease = 0;
		if (dataMap.getSize() > 0) {
			thisIncrease = (int) Math.ceil(Double.parseDouble(String
					.valueOf(dataMap.getRecordValue(0, "increase"))));
		}
		if (thisIncrease < 100) {
			thisIncrease = 100;
		}
		return thisIncrease + 1;

	}

	/**
	 * 从 rf，rs模式 得到第一行的结果key：value模式
	 * 
	 * @param in
	 * @return
	 */
	public ParaMap getRowOneInfo(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		if (in.getSize() > 0) {
			List filds = in.getFields();
			List record = (List) in.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					out.put((String) filds.get(i), record.get(i));
				}
			}
		}
		return out;
	}

	/**
	 * 得到实际竞买申请数量（不管任何阶段）
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public int getEffectiveLicneseNum(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_effective_licneseNum");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		return (int) Math.ceil(Double.parseDouble(String.valueOf(dataMap
				.getRecordValue(0, "num"))));
	}

	/**
	 * 得到有效地出价竞买人的个数
	 * 
	 * @param targetId
	 * @return
	 * @throws SQLException
	 */
	public int getValidOfferLogLicenseNum(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_validOfferLog_licenseNum");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		return (int) Math.ceil(Double.parseDouble(String.valueOf(dataMap
				.getRecordValue(0, "num"))));
	}

	/**
	 * 得到底价
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getReservePrice(String targetId) throws Exception {
		DecimalFormat df = new DecimalFormat("#############.##");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_reservePrice_forTrade");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if (dataMap.size() > 0) {
			Object obj = dataMap.getRecordValue(0, "reserve_price");
			BigDecimal reservePrice = obj == null
					|| "".equals(String.valueOf(obj)) ? null : new BigDecimal(
					df.format(Double.parseDouble(String.valueOf(obj))));
			return reservePrice;
		} else {
			return null;
		}
	}

	/**
	 * 得到价格阶段的最高价
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getMaxPrice(String targetId) throws Exception {
		DecimalFormat df = new DecimalFormat("#############.##");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_maxPrice_L02L03Q01");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if (dataMap.size() > 0) {
			Object obj = dataMap.getRecordValue(0, "maxprice");
			System.out.println("===========================" + obj);
			BigDecimal maxPrice = obj == null || "".equals(String.valueOf(obj)) ? null
					: new BigDecimal(df.format(Double.parseDouble(String
							.valueOf(obj))));
			return maxPrice;
		} else {
			return null;
		}
	}

	/**
	 * 得到有效的出价个数
	 * 
	 * @param targetId
	 * @return
	 * @throws SQLException
	 */
	public int getValidOfferLogNum(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_valid_offerLogNum");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		String num = String.valueOf(dataMap.getRecordValue(0, "num"));
		return (int) Math.ceil(Double.parseDouble(num));
	}

	/**
	 * 得到有效的出价列表
	 * 
	 * @param targetId
	 * @return
	 * @throws SQLException
	 */
	public ParaMap getOfferLogList(String targetId, int count, int pageIndex)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_offerLogList_byTargetId");
		sqlParams.put("targetId", targetId);
		sqlParams.put("offerDatePattern", "yyyy-MM-dd HH24:MI:SS.FF3");
		if (count > 0) {
			pageIndex = pageIndex <= 0 ? 1 : pageIndex;
			sqlParams.put(DataSetDao.SQL_PAGE_INDEX, pageIndex);
			sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, count);
		}
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		return dataMap;
	}

	/**
	 * 得到某个标的所有指标单位
	 * 
	 * @param targetId
	 * @throws Exception
	 */
	public ParaMap getAllNodeUnit(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "get_multiUnit_forTarget");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);

		ParaMap out = new ParaMap();

		int mapSize = dataMap.getSize();
		if (mapSize > 0) {
			for (int i = 0; i < mapSize; i++) {
				Object obj = dataMap.getRecordValue(i, "multiid");
				String mId = obj == null || "".equals((String) obj) ? "noMultiId"
						: (String) obj;
				ParaMap pMap = new ParaMap();
				pMap.put("name", dataMap.getRecordValue(i, "name"));
				pMap.put("unit", dataMap.getRecordValue(i, "unit"));
				out.put(mId, pMap);
			}
		}
		if (out.get("noMultiId") == null) {
			ParaMap keyData = new ParaMap();
			keyData.put("id", targetId);
			ParaMap paraMap = dataSetDao.querySimpleData("trans_target",
					keyData);
			String unit = "元";
			if (paraMap.getSize() > 0) {
				unit = (String) paraMap.getRecordValue(0, "unit");
			}
			ParaMap pMap = new ParaMap();
			pMap.put("name", "价格指标");
			pMap.put("unit", unit);
			out.put("noMultiId", pMap);
		}
		return out;
	}

	/**
	 * 竞价倒序列表 序号 //count = 0为所有记录，其他的值为显示几条记录
	 * 
	 */
	public ParaMap getBidList(String targetId, int count, int pageIndex)
			throws Exception {
		int logNum = this.getValidOfferLogNum(targetId);
		ParaMap offerMap = this.getOfferLogList(targetId, count, pageIndex);
		// int logNum = offerMap.getSize();

		ParaMap out = new ParaMap();
		List fields = offerMap.getFields();
		ParaMap fieldsMap = new ParaMap();
		for (int i = 0; i < fields.size(); i++) {
			fieldsMap.put((String) fields.get(i), i);
		}
		List records = offerMap.getRecords();
		int recordsSize = records == null || records.size() < 1 ? 0 : records
				.size();
		int showCount = count == 0 ? recordsSize : count;
		pageIndex = pageIndex <= 0 ? 1 : pageIndex;
		List newRecords = new ArrayList();
		if (recordsSize > 0) {
			for (int i = 0; i < recordsSize && i < showCount; i++) {
				List record = (List) records.get(i);
				String multi_trade_id = (String) record.get(fieldsMap
						.getInt("multi_trade_id"));
				multi_trade_id = StrUtils.isNull(multi_trade_id) ? "noMultiId"
						: multi_trade_id;
				record.set(fieldsMap.getInt("bidtimes"), logNum
						- (pageIndex - 1) * count - i);
				String nodeunit = (String) record.get(fieldsMap
						.getInt("nodeunit"));
				String nodename = (String) record.get(fieldsMap
						.getInt("nodename"));
				record.set(fieldsMap.getInt("nodename"),
						StrUtils.isNull(nodename) ? "价格指标" : nodename);
				String priceStr = String.valueOf(record.get(fieldsMap
						.getInt("price")));
				BigDecimal price = new BigDecimal(priceStr);
				int status = (int) Math.ceil(Double.parseDouble(String
						.valueOf(record.get(fieldsMap.getInt("status")))));
				String priceShow = this.formatMoney(price, nodeunit);
				// priceShow = status == 2 ?
				// "<font color='red'>"+priceShow+"</font>" : priceShow;
				record.set(fieldsMap.getInt("price"), priceShow);
				newRecords.add(record);
			}
		}
		offerMap.put("rs", newRecords);
		return offerMap;
	}

	public String queryWfParameter(String refid, String name) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("refid", refid);
		keyData.put("name", name);
		ParaMap dataMap = dataSetDao.querySimpleData("wf_parameter", keyData);
		if (dataMap.getSize() > 0) {
			return String.valueOf(dataMap.getRecordValue(0, "value"));
		} else {
			return null;
		}
	}

	public List getTrustPriceLicenseList(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("trust_type", 1);
		keyData.put("is_valid", 1);
		ParaMap dataMap = dataSetDao.querySimpleData("trans_license", keyData);
		if (dataMap.getSize() > 0) {
			return dataMap.getListObj();
		} else {
			return null;
		}
	}

	public List getTrustPriceLicenseList(String targetId, BigDecimal mustPrice,
			String sql) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_trust_price_license_list");
		sqlParams.put("targetId", targetId);
		sqlParams.put("mustPrice", mustPrice);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if (dataMap.getSize() > 0) {
			return dataMap.getListObj();
		} else {
			return null;
		}
	}

	public List getTrustPriceLicenseNotAgree(String targetId,
			String taskNodeId, BigDecimal mustPrice, String sql)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_trust_price_license_notagree");
		sqlParams.put("targetId", targetId);
		sqlParams.put("taskNodeId", taskNodeId);
		sqlParams.put("mustPrice", mustPrice);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if (dataMap.getSize() > 0) {
			return dataMap.getListObj();
		} else {
			return null;
		}
	}

	public ParaMap convertBidList(ResultSet rs, String unit) throws Exception {
		ParaMap pm = new ParaMap();
		List fields = new ArrayList();
		List records = new ArrayList();
		int colCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String name = rs.getMetaData().getColumnName(i).toLowerCase();
			fields.add(name);
		}

		while (rs.next()) {
			List record = new ArrayList();
			for (int i = 1; i <= colCount; i++) {
				String name = rs.getMetaData().getColumnName(i).toLowerCase();
				if ("price".equals(name) || "PRICE".equals(name)) {
					BigDecimal price = rs.getBigDecimal(i);
					String showPrice = formatMoney(price, unit);
					record.add(showPrice);
				} else {
					record.add(getValue(rs, i));
				}
			}
			records.add(record);
		}
		pm.put("fs", fields);
		pm.put("rs", records);
		return pm;
	}

	public ParaMap convertBidList(ParaMap offer, String unit) throws Exception {

		List filds = offer.getFields();
		int priceIndex = filds.indexOf("price");

		List cList = new ArrayList();

		List records = offer.getRecords();
		if (records != null && records.size() > 0) {
			for (int i = 0; i < records.size(); i++) {
				List rsList = (List) records.get(i);
				BigDecimal price = (BigDecimal) rsList.get(priceIndex);
				rsList.set(priceIndex, formatMoney(price, unit));
				cList.add(rsList);
			}
		}
		ParaMap out = new ParaMap();
		out.put("fs", filds);
		out.put("rs", cList);
		return out;
	}

	public String formatLastMoney(BigDecimal money, String unit)
			throws Exception {
		if (money == null) {
			return "无人出价";
		}
		if (unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")) {
			return money.toString();
		} else {
			if (unit.equals("元")) { // 如果是元格式化为千分位逗号
				DecimalFormat df = new DecimalFormat("###,###,###,###.##");
				return df.format(money.doubleValue()) + unit;
			} else if (unit.equals("万元")) {// 如果是万元保留4为小数
				DecimalFormat df = new DecimalFormat("###,###,###,###.####");
				return df.format((money.divide(new BigDecimal(Math.pow(10, 4)),
						4, BigDecimal.ROUND_HALF_UP))) + unit;
			} else {
				DecimalFormat df = new DecimalFormat("###########.##");
				return df.format(money.doubleValue()) + unit;
			}
		}
	}

	public String formatMoney(BigDecimal money, String unit) throws Exception {
		if (money == null) {
			return null;
		}
		if (unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")) {
			return money.toString();
		} else {
			if (unit.equals("元")) { // 如果是元格式化为千分位逗号
				DecimalFormat df = new DecimalFormat("###,###,###,###.##");
				return df.format(money.doubleValue()) + unit;
			} else if (unit.equals("万元")) {// 如果是万元保留4为小数
				DecimalFormat df = new DecimalFormat("###,###,###,###.####");
				return df.format((money.divide(new BigDecimal(Math.pow(10, 4)),
						4, BigDecimal.ROUND_HALF_UP))) + unit;
			} else {
				DecimalFormat df = new DecimalFormat("###########.##");
				return df.format(money.doubleValue()) + unit;
			}
		}
	}

	public BigDecimal formatPrice(BigDecimal money, String unit)
			throws Exception {
		if (money == null) {
			return null;
		}
		if (unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")) {
			return money;
		} else {
			if (unit.equals("元")) {
				return money;
			} else if (unit.equals("万元")) {
				return money.divide(new BigDecimal(Math.pow(10, 4)), 4,
						BigDecimal.ROUND_HALF_UP);
			} else {
				return money;
			}
		}
	}

	public BigDecimal getTrueMoney(BigDecimal money, String unit)
			throws Exception {
		if (unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")) {
			return money;
		} else {
			if (unit.equals("万元")) {
				return money.multiply(new BigDecimal(Math.pow(10, 4)))
						.setScale(2, BigDecimal.ROUND_HALF_UP); // 得到真实的金额
			} else if (unit.equals("元")) {
				return money.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				return money;
			}
		}
	}

	public int getPriceDecimalNumber(String unit) throws Exception {
		if (unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")) {
			return 2;
		} else {
			if (unit.equals("万元")) {
				return 4; // 得到真实的金额
			} else if (unit.equals("元")) {
				return 0;
			} else {
				return 2;
			}
		}
	}

	public String getLeftString(String source, int leftNum) {
		if (source == null || "".equals(source)
				|| source.equalsIgnoreCase("null")) {
			return "";
		} else {
			if (source.length() <= leftNum) {
				return source;
			} else {
				return source.substring(0, leftNum);
			}
		}
	}

	public String addLicense(String targetId, String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", userId);
		ParaMap paraMap = dataSetDao.querySimpleData("sys_user", keyData);
		String bidderId = (String) paraMap.getRecordValue(0, "ref_id");

		ParaMap pMap = new ParaMap();
		pMap.put("id", null);
		pMap.put("target_id", targetId);
		pMap.put("bidder_id", bidderId);
		pMap.put("license_no", bidderId + targetId);
		pMap.put("status", 1);
		pMap.put("is_valid", 1);
		ParaMap out = dataSetDao.updateData("trans_license", "id", pMap, null);
		return out.getString("id");

	}

	public ParaMap getNotEndProcess(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		if (moduleId == null || moduleId.equals("")
				|| moduleId.equalsIgnoreCase("null")
				|| moduleId.equalsIgnoreCase("undefined")) {
			sqlParams.put("moduleNo", "trans_trade_plow");
			sqlParams.remove("moduleId");
		} else {
			sqlParams.put("moduleId", moduleId);
		}
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null")) {
			sqlParams.put("dataSetNo", "query_active_processes");
		} else {
			sqlParams.put("dataSetNo", dataSetNo);
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	public String getNodeBeginTime(String nodeId) throws Exception {
		if (StrUtils.isNull(nodeId)) {
			return null;
		}
		EngineDao engineDao = new EngineDao();
		ParaMap map = engineDao.getNodeInfo(nodeId);
		if (map != null && map.get(Constants.PbeginTime) != null) {
			return map.getString(Constants.PbeginTime);
		} else {
			return null;
		}
	}

	public String downLoadEnOfferLog(String licenseId) throws Exception {
		String result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_enofferlog_bylicenseid");
		sqlParams.put("licenseId", licenseId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if ("1".equals(dataMap.getString("state"))) {
			List list = new ArrayList();
			int size = dataMap.getInt("rowCount");
			for (int i = 0; i < size; i++) {
				String blobid = (String) dataMap.getRecordValue(i, "blobid");
				list.add(blobid);
			}
			result = getSysLobCa(list);
		}
		return result;

	}

	public void sendSubmitChildNoXml(String targetId) {
		BankBaseDao bankBaseDao = new BankBaseDao();
		try {
			bankBaseDao.submitChildAccount(targetId);
		} catch (Exception e) {
			log.error(targetId + ":发送子转总报文失败！");
			log.error(e.getMessage());
		}
	}

	public void sendMessage(String targetId) {
		try {
			// 得到标的信息
			ParaMap targetMap = this.getTargetData(targetId);
			if (targetMap.getSize() > 0) {
				String targetName = targetMap.getRecordValue(0, "name") != null ? (String) targetMap
						.getRecordValue(0, "name") : (String) targetMap
						.getRecordValue(0, "no");
				String status = String.valueOf(targetMap.getRecordValue(0,
						"status"));
				String Message = "您好，您申请的标的 " + targetName;
				if (status.equals("5")) {
					Message = Message + " 已成交。竞得人为:";
					ParaMap okBidder = getOkBidder(targetId);
					targetMap.put("succlicense",
							okBidder.getString("succlicense"));
					String succBidderName = okBidder
							.getString("succbiddername");
					ParaMap okUnionBidder = getOkUnionBidder(targetId);
					if (okUnionBidder.getSize() > 0) {
						int okUnionBidderSize = okUnionBidder.getSize();
						for (int i = 0; i < okUnionBidderSize; i++) {
							succBidderName = succBidderName
									+ ","
									+ okUnionBidder.getRecordValue(i,
											"bidder_name");
						}
					}
					Message = Message + succBidderName + "。";
				} else if (status.equals("6")) {
					Message = Message + " 已流拍。";
				}
				// 得到竞买人信息(包括电话号码)
				DataSetDao dataSetDao = new DataSetDao();
				ParaMap sqlParams = new ParaMap();
				sqlParams.put("moduleNo", "trans_trade_plow");
				sqlParams.put("dataSetNo", "get_licenseInfo_byTargetId");
				sqlParams.put("targetId", targetId);
				ParaMap licenseMap = dataSetDao.queryData(sqlParams);
				String mobile = null;
				if (licenseMap.getSize() > 0) {
					for (int i = 0; i < licenseMap.getSize(); i++) {
						String thism = null;
						if (licenseMap.getRecordValue(i, "mobile") != null
								&& !"".equals((String) licenseMap
										.getRecordValue(i, "mobile"))
								&& !((String) licenseMap.getRecordValue(i,
										"mobile")).equalsIgnoreCase("null")) {
							thism = (String) licenseMap.getRecordValue(i,
									"mobile");
						}
						if (thism != null)
							mobile = mobile == null || "".equals(mobile) ? thism
									: mobile + "," + thism;
					}
				}
				// 发送短信
				SmsDao smsDao = new SmsDao();
				smsDao.sendMessage(SmsDao.CATEGORY_TransTargetComplete, mobile,
						Message, "trans_target", targetId);
				System.out.println(mobile);
				System.out.println(Message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String virtualTrade() throws Exception {

		// 第一个标的公告开始时间
		String first_notice_begin_time = AppConfig
				.getPro("demo_first_notice_begin_time");
		first_notice_begin_time = StrUtils.isNull(first_notice_begin_time) ? "08:00:00"
				: first_notice_begin_time.trim();
		// 共创建几个标的
		String target_num = AppConfig.getPro("demo_target_num");
		target_num = StrUtils.isNull(target_num) ? "5" : target_num;
		// 每个标的之间的时间间隔
		String target_between_hour = AppConfig
				.getPro("demo_target_between_hour");
		target_between_hour = StrUtils.isNull(target_between_hour) ? "2"
				: target_between_hour;
		// 每个标的的公告期为几分钟
		String target_notice_delay_minute = AppConfig
				.getPro("demo_target_notice_delay_minute");
		target_notice_delay_minute = StrUtils
				.isNull(target_notice_delay_minute) ? "60"
				: target_notice_delay_minute;
		// 每个标的的集中报价期为几分钟
		String target_focus_delay_minute = AppConfig
				.getPro("demo_target_focus_delay_minute");
		target_focus_delay_minute = StrUtils.isNull(target_focus_delay_minute) ? "30"
				: target_notice_delay_minute;
		// 每个标的类型，拍卖还是挂牌或随机
		String trans_type = AppConfig.getPro("demo_trans_type");
		trans_type = StrUtils.isNull(trans_type) ? "-1" : trans_type;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date nowDate = DateUtils.now();
		Date beginTime = sdf2.parse(sdf.format(nowDate) + " "
				+ first_notice_begin_time);
		Date endTime = new Date(beginTime.getTime()
				+ Long.parseLong(target_notice_delay_minute) * 60 * 1000L);

		ParaMap inMap = new ParaMap();
		inMap.put("firstBeginTime", sdf2.format(beginTime));// 第一个创建时间
		inMap.put("targetNum", target_num);// 共创建几个
		inMap.put("betweenMinute", (new BigDecimal(target_between_hour))
				.multiply(new BigDecimal("60")));// 每个创建的时间间隔（分钟）
		inMap.put("noticeDelay", target_notice_delay_minute);// 公告期（分钟）
		inMap.put("focusDelay", target_focus_delay_minute);// 集中报价期（分钟）进入集中报价的时间为
															// noticeDelay -
															// focusDelay
		inMap.put("isLicense", 0);// 是否自动做竞买申请 1做 0 不做
		inMap.put("transType", trans_type);// 交易类型 1拍卖 0挂牌
		this.virtualTrade(inMap);
		return null;
	}

	public String virtualTrade(ParaMap inMap) throws Exception {
		int firstTargetDelay = 0;// 第一个创建等待时间
		if (inMap.containsKey("firstTargetDelay")) {
			firstTargetDelay = inMap.getInt("firstTargetDelay");
		}
		String firstBeginTime = inMap.getString("firstBeginTime");
		int targetNum = 1;
		if (inMap.containsKey("targetNum")) {
			targetNum = inMap.getInt("targetNum");
		}
		int betweenMinute = 0;
		if (inMap.containsKey("betweenMinute")) {
			BigDecimal bm = inMap.getBigDecimal("betweenMinute");
			betweenMinute = bm.intValue();
		}
		int noticeDelay = 10;
		if (inMap.containsKey("noticeDelay")) {
			noticeDelay = inMap.getInt("noticeDelay");
		}
		int focusDelay = 10;
		if (inMap.containsKey("focusDelay")) {
			focusDelay = inMap.getInt("focusDelay");
		}
		if (focusDelay > noticeDelay) {
			focusDelay = noticeDelay;
		}
		int isLicense = 0;
		if (inMap.containsKey("isLicense")) {
			isLicense = inMap.getInt("isLicense");
		}
		int transType = -1;
		if (inMap.containsKey("transType")) {
			transType = inMap.getInt("transType");
		}
		int isOnline = -1;
		if (inMap.containsKey("isOnline")) {
			isOnline = inMap.getInt("isOnline");
		}
		String[] goodsUseArray = new String[] { "商住混合用地", "酒店用地", "商业", "居住用地",
				"工业用地", "加油站用地", "地下车库", "幼儿园用地", "自用停车场", "旅馆业用地", "建筑用花岗岩",
				"机场用地" };
		String[] cantonArray = new String[] { "440101", "440103", "440104",
				"440105", "440106", "440111", "440112", "440113", "440114",
				"440115", "440116", "440183" };
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = DateUtils.now();
		long minute = 1000l * 60;
		long day = 1000l * 60 * 60 * 24;

		Date begin = new Date(currentDate.getTime() + minute * firstTargetDelay);
		if (StrUtils.isNotNull(firstBeginTime)) {
			begin = DateUtils.getDate(firstBeginTime);
		}
		Date end = new Date(begin.getTime() + minute * noticeDelay);
		Date beginFocus = new Date(begin.getTime() + minute
				* (noticeDelay - focusDelay));
		Date endFocus = new Date(begin.getTime() + minute * noticeDelay);

		Date begin_2 = new Date(begin.getTime());
		Date end_2 = new Date(end.getTime());
		Date beginFocus_2 = new Date(beginFocus.getTime());
		Date endFocus_2 = new Date(endFocus.getTime());

		Random random = new Random();
		String targetID = "";
		for (int i = 0; i < targetNum; i++) {
			String beginDate = sdf2.format(begin);
			String endDate = sdf2.format(end);
			System.out.println(i + "============================" + endDate);
			if (end.getTime() > currentDate.getTime()) {
				String beginFocusDate = sdf2.format(beginFocus);
				String endFocusDate = sdf2.format(endFocus);
				int randomer = Math.abs(random.nextInt() % 10);
				BigDecimal beginPrice = null;
				BigDecimal step = null;
				BigDecimal finalPrice = null;

				String goodsUse = "耕指出售";
				String businessType = "501005";
				beginPrice = new BigDecimal("100000").add(new BigDecimal(
						randomer * 100000));
				step = new BigDecimal("20000");

				String flag = "0#2#0";
				String transTypeName = "拍卖";
				String transTypeId = "108";
				if (transType == 0) {
					flag = "1#1#0";
					transTypeName = "挂牌";
					transTypeId = "107";
				} else if (transType == 1) {
					flag = "1#2#0";
					transTypeName = "拍卖";
					transTypeId = "108";
				} else if (transType == -1) {
					randomer = Math.abs(random.nextInt() % 10);
					if (randomer <= 3) {
						flag = "1#1#0";
						transTypeName = "挂牌";
						transTypeId = "107";
						transType = 0;
					} else {
						flag = "1#2#0";
						transTypeName = "拍卖";
						transTypeId = "108";
						transType = 1;
					}
				}

				if (isOnline == -1) {
					randomer = Math.abs(random.nextInt() % 10);
					if (randomer <= 5) {
						isOnline = 1;
					} else {
						isOnline = 0;
					}
				}

				randomer = Math.abs(random.nextInt() % 10);
				String cantonId = cantonArray[randomer];

				ParaMap pMap = new ParaMap();
				pMap.put("beginPrice", beginPrice);
				pMap.put("step", step);
				pMap.put("finalPrice", finalPrice);
				pMap.put("flag", flag);
				pMap.put("transType", transType);
				pMap.put("transTypeId", transTypeId);
				pMap.put("transTypeName", transTypeName);
				pMap.put("beginDate", beginDate);
				pMap.put("endDate", endDate);
				pMap.put("beginFocusDate", beginFocusDate);
				pMap.put("endFocusDate", endFocusDate);
				pMap.put("businessType", businessType);
				pMap.put("goodsUse", goodsUse);
				pMap.put("cantonId", cantonId);
				pMap.put("isHaveMulti", 1);
				pMap.put("isLicense", isLicense);
				pMap.put("isOnline", isOnline);
				if (StrUtils.isNull(targetID)) {
					targetID = this.createVirtualTarget(pMap);
				} else {
					targetID = targetID + ";" + this.createVirtualTarget(pMap);
				}
			}
			begin = new Date(begin.getTime() + betweenMinute * minute);
			end = new Date(end.getTime() + betweenMinute * minute);
			beginFocus = new Date(beginFocus.getTime() + betweenMinute * minute);
			endFocus = new Date(endFocus.getTime() + betweenMinute * minute);
		}
		this.createVirtualTemplate(targetID);
		if (isLicense == 1) {
			this.createVirtualLicense(targetID);
		}

		String targetID2 = "";
		for (int i = 0; i < targetNum; i++) {
			String beginDate = sdf2.format(begin_2);
			String endDate = sdf2.format(end_2);
			System.out.println(i + "============================" + endDate);
			if (end.getTime() > currentDate.getTime()) {
				String beginFocusDate = sdf2.format(beginFocus_2);
				String endFocusDate = sdf2.format(endFocus_2);
				int randomer = Math.abs(random.nextInt() % 10);
				BigDecimal beginPrice = null;
				BigDecimal step = null;
				BigDecimal finalPrice = null;

				String goodsUse = "耕指购买";
				String businessType = "501006";
				beginPrice = new BigDecimal("500000").add(new BigDecimal(
						randomer * 100000));
				step = new BigDecimal("-25000");
				finalPrice = new BigDecimal("50000");

				String flag = "0#2#0";
				String transTypeName = "拍卖";
				String transTypeId = "108";
				if (transType == 0) {
					flag = "1#1#0";
					transTypeName = "挂牌";
					transTypeId = "107";
				} else if (transType == 1) {
					flag = "1#2#0";
					transTypeName = "拍卖";
					transTypeId = "108";
				} else if (transType == -1) {
					randomer = Math.abs(random.nextInt() % 10);
					if (randomer <= 3) {
						flag = "1#1#0";
						transTypeName = "挂牌";
						transTypeId = "107";
						transType = 0;
					} else {
						flag = "1#2#0";
						transTypeName = "拍卖";
						transTypeId = "108";
						transType = 1;
					}
				}

				randomer = Math.abs(random.nextInt() % 10);
				String cantonId = cantonArray[randomer];

				ParaMap pMap = new ParaMap();
				pMap.put("beginPrice", beginPrice);
				pMap.put("step", step);
				pMap.put("finalPrice", finalPrice);
				pMap.put("flag", flag);
				pMap.put("transType", transType);
				pMap.put("transTypeId", transTypeId);
				pMap.put("transTypeName", transTypeName);
				pMap.put("beginDate", beginDate);
				pMap.put("endDate", endDate);
				pMap.put("beginFocusDate", beginFocusDate);
				pMap.put("endFocusDate", endFocusDate);
				pMap.put("businessType", businessType);
				pMap.put("goodsUse", goodsUse);
				pMap.put("cantonId", cantonId);
				pMap.put("isHaveMulti", 1);
				pMap.put("isLicense", isLicense);
				if (StrUtils.isNull(targetID2)) {
					targetID2 = this.createVirtualTarget(pMap);
				} else {
					targetID2 = targetID2 + ";"
							+ this.createVirtualTarget(pMap);
				}
			}
			begin_2 = new Date(begin_2.getTime() + betweenMinute * minute);
			end_2 = new Date(end_2.getTime() + betweenMinute * minute);
			beginFocus_2 = new Date(beginFocus_2.getTime() + betweenMinute
					* minute);
			endFocus_2 = new Date(endFocus_2.getTime() + betweenMinute * minute);
		}
		this.createVirtualTemplate(targetID2);
		if (isLicense == 1) {
			this.createVirtualLicense(targetID2);
		}
		return targetID + ";" + targetID2;

	}

	public String createVirtualTarget(ParaMap inMap) throws Exception {
		BigDecimal beginPrice = inMap.getBigDecimal("beginPrice");
		BigDecimal step = inMap.getBigDecimal("step");
		BigDecimal finalPrice = inMap.getBigDecimal("finalPrice");
		String flag = inMap.getString("flag");
		int transType = inMap.getInt("transType");
		String transTypeName = inMap.getString("transTypeName");
		String transTypeId = inMap.getString("transTypeId");
		String beginDate = inMap.getString("beginDate");
		String endDate = inMap.getString("endDate");
		String beginFocusDate = inMap.getString("beginFocusDate");
		String endFocusDate = inMap.getString("endFocusDate");
		String businessType = inMap.getString("businessType");
		String goodsUse = inMap.getString("goodsUse");
		String cantonId = inMap.getString("cantonId");
		int isHaveMulti = inMap.getInt("isHaveMulti");
		int isLicense = inMap.getInt("isLicense");
		int isOnline = inMap.getInt("isOnline");
		String nowStr = beginDate.substring(beginDate.indexOf("-"),
				beginDate.lastIndexOf(":"));

		String trustID = IDGenerator.generatorID("trans_trust");
		String sqltttt = "   insert into trans_trust(id,is_valid) values('"
				+ trustID + "',1) ";
		PreparedStatement pstm = getCon().prepareStatement(sqltttt);
		pstm.executeUpdate();

		String targetID = IDGenerator.generatorID("trans_target");
		StringBuffer sql = new StringBuffer("declare");
		sql.append("   new_target_id varchar2(50);");
		sql.append("   new_goods_id varchar2(50);");
		sql.append(" begin");
		sql.append("   new_target_id := ?;");
		sql.append("   new_goods_id := '"
				+ IDGenerator.generatorID("trans_goods") + "';");
		// 插入标的记录
		sql.append("   insert into trans_target(id,no,name, business_type, trans_type, is_net_trans,  is_limit_trans, status, begin_price,  price_step, earnest_money,final_price,reserve_price ");
		sql.append(", begin_apply_time");
		sql.append(", end_apply_time");
		sql.append(", begin_earnest_time");
		sql.append(", end_earnest_time");
		sql.append(", begin_notice_time");
		sql.append(", end_notice_time");
		sql.append(", begin_list_time");
		sql.append(", end_list_time");
		sql.append(", begin_focus_time");
		sql.append(", end_focus_time");
		sql.append(", begin_limit_time");
		sql.append(", unit");
		sql.append(", trans_type_name");
		sql.append(", trans_type_label");
		sql.append(", allow_union");
		sql.append(", is_after_check");
		sql.append(", organ_id , trans_organ_id , trans_type_id , is_online");
		sql.append(") values(new_target_id,?,?,?, ?,  ?, ?, ?, ?, ?, ?,?,? ");
		sql.append(", to_date('" + beginDate + "', 'yyyy-mm-dd hh24:mi:ss')");// begin_apply_time
		sql.append(", to_date('" + endDate + "', 'yyyy-mm-dd hh24:mi:ss')");// end_apply_time
		sql.append(", to_date('" + beginDate + "', 'yyyy-mm-dd hh24:mi:ss')");// begin_earnest_time
		sql.append(", to_date('" + endDate + "', 'yyyy-mm-dd hh24:mi:ss')");// end_earnest_time
		sql.append(", to_date('" + beginDate + "', 'yyyy-mm-dd hh24:mi:ss')");// begin_notice_time
		sql.append(", to_date('" + endDate + "', 'yyyy-mm-dd hh24:mi:ss')");// end_notice_time
		sql.append(", to_date('" + beginDate + "', 'yyyy-mm-dd hh24:mi:ss')");// begin_list_time
		sql.append(", to_date('" + endDate + "', 'yyyy-mm-dd hh24:mi:ss')");// end_list_time
		sql.append(", to_date('" + beginFocusDate
				+ "', 'yyyy-mm-dd hh24:mi:ss')");// begin_focus_time
		sql.append(", to_date('" + endFocusDate + "', 'yyyy-mm-dd hh24:mi:ss')");// end_foxus_time
		sql.append(", to_date('" + endFocusDate + "', 'yyyy-mm-dd hh24:mi:ss')");// begin_limit_time
		sql.append(", '元'");
		sql.append(", '" + transTypeName + "'");
		sql.append(", '" + transTypeName + "'");
		sql.append(", '1'");
		sql.append(", '1'");
		sql.append(", '4080E8280362438E94FFEA1720AE5E10'");
		sql.append(", '4080E8280362438E94FFEA1720AE5E10'");
		sql.append(", '" + transTypeId + "'");
		sql.append(", " + isOnline);
		sql.append(");");
		// 插入交易物记录
		sql.append("   insert into trans_goods(id,goods_type, no, name,goods_use,canton_id, trust_id , area) values(new_goods_id, '501',?, ? , ?,?,'"
				+ trustID + "','100');");
		// 插入标的交易物关联
		sql.append("   insert into trans_target_goods_rel(id, target_id, goods_id) values('"
				+ IDGenerator.generatorID("trans_target_goods_rel")
				+ "', new_target_id, new_goods_id);");
		sql.append(" end;");
		System.out.println(sql.toString());
		pstm = getCon().prepareStatement(sql.toString());
		pstm.setString(1, targetID);
		pstm.setString(2, goodsUse + nowStr);
		pstm.setString(3, goodsUse + nowStr);
		pstm.setString(4, businessType);// business_type
		pstm.setInt(5, transType);// trans_type
		pstm.setInt(6, 1);// is_net_trans
		pstm.setInt(7, 1);// is_limit_trans
		pstm.setInt(8, 3);// status
		pstm.setBigDecimal(9, beginPrice);// begin_price
		pstm.setBigDecimal(10, step);// price_step
		pstm.setDouble(11, 50000);// earnest_money
		pstm.setBigDecimal(12, finalPrice);// fianl_price
		pstm.setBigDecimal(13, beginPrice);// reserve_price

		pstm.setString(14, goodsUse + nowStr);
		pstm.setString(15, goodsUse + nowStr);
		pstm.setString(16, goodsUse);
		pstm.setString(17, cantonId);
		pstm.executeUpdate();

		String sqlrrrrr = "insert into trans_trust_bidder_rel(id,trust_id,bidder_id,is_valid) "
				+ "values('"
				+ IDGenerator.generatorID("trans_trust_bidder_rel")
				+ "','"
				+ trustID + "','0002',1)";
		pstm = getCon().prepareStatement(sqlrrrrr);
		pstm.executeUpdate();

		String sql3 = "insert into trans_target_multi_trade(id,target_id,name,type,unit,init_value,step,enter_flag,status,is_valid,turn,ptype,final_value "
				+ " ) values(?,?,?,?,?,?,?,?,?,?,?,?,? )";
		pstm = getCon().prepareStatement(sql3);
		pstm.setString(1, IDGenerator.generatorID("trans_target_multi_trade"));
		pstm.setString(2, targetID);
		pstm.setString(3, "价格指标");
		pstm.setString(4, "Q01");
		pstm.setString(5, "元");
		pstm.setBigDecimal(6, beginPrice);
		pstm.setBigDecimal(7, step);
		pstm.setString(8, "0#1#0");
		pstm.setInt(9, 0);
		pstm.setInt(10, 1);
		pstm.setInt(11, 1);
		pstm.setString(12, "L03");
		if (finalPrice != null) {
			pstm.setBigDecimal(13, finalPrice);
		} else {
			pstm.setString(13, "");
		}
		int rst = pstm.executeUpdate();

		if (businessType.equals("501006")) {
			pstm.setString(1,
					IDGenerator.generatorID("trans_target_multi_trade"));
			pstm.setString(2, targetID);
			pstm.setString(3, "摇号");
			pstm.setString(4, "Q04");
			pstm.setString(5, "元");
			pstm.setBigDecimal(6, beginPrice);
			pstm.setBigDecimal(7, step);
			pstm.setString(8, "0#2#0");
			pstm.setInt(9, 0);
			pstm.setInt(10, 1);
			pstm.setInt(11, 2);
			pstm.setString(12, "L04");
			if (finalPrice != null) {
				pstm.setBigDecimal(13, finalPrice);
			} else {
				pstm.setString(13, "");
			}
			int rst2 = pstm.executeUpdate();
		}

		String noticeId = IDGenerator.generatorID("trans_notice");
		String sqlNotice = "insert into trans_notice(id, notice_type, no, name, is_valid , status ,notice_date)"
				+ " values('"
				+ noticeId
				+ "',0, '"
				+ goodsUse
				+ "公告"
				+ nowStr
				+ "', '" + goodsUse + "公告" + nowStr + "',1,2,sysdate)";
		pstm = getCon().prepareStatement(sqlNotice);
		pstm.executeUpdate();

		String sqlTransAccount = "select id from trans_account where business_type ='"
				+ businessType + "'";
		pstm = getCon().prepareStatement(sqlTransAccount);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			String accountId = rs.getString("id");

			String sqlAccount = "insert into trans_notice_account_rel(id,notice_id,account_id,is_valid)"
					+ " values('"
					+ IDGenerator.generatorID("trans_notice_account_rel")
					+ "','" + noticeId + "','" + accountId + "', 1)";

			pstm = getCon().prepareStatement(sqlAccount);
			pstm.executeUpdate();
		}

		String sqlNoticeTargetRel = "insert into trans_notice_target_rel(id, notice_id, target_id)"
				+ " values('"
				+ IDGenerator.generatorID("trans_notice_target_rel")
				+ "', '"
				+ noticeId + "', '" + targetID + "')";
		pstm = getCon().prepareStatement(sqlNoticeTargetRel);
		pstm.executeUpdate();

		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return targetID;

	}

	public void createVirtualTemplate(String targetIds) throws Exception {
		if (StrUtils.isNull(targetIds)) {
			return;
		}

		String[] targetIdArray = targetIds.split(";");

		for (int i = 0; i < targetIdArray.length; i++) {
			String targetId = targetIdArray[i];
			if (StrUtils.isNotNull(targetId)) {
				EngineDao engineDao = new EngineDao();
				engineDao.createTemplateInfo(targetId);
			}
		}
	}

	public void createVirtualLicense(String targetIds) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		EngineDao engineDao = new EngineDao();
		String[] targetIdArray = targetIds.split(";");
		for (int i = 0; i < targetIdArray.length; i++) {
			String targetId = targetIdArray[i];
			if (StrUtils.isNotNull(targetId)) {
				String sqlUser = "select su.id,su.ref_id as bidder_id from sys_user su  where  su.user_type = 1 and su.user_name like 't0%'";
				pstm = getCon().prepareStatement(sqlUser);
				rs = pstm.executeQuery();
				while (rs.next()) {
					String userId = rs.getString("id");
					String bidderId = rs.getString("bidder_id");

					String licenseId = IDGenerator.generatorID("trans_license");
					String sqlLicense = "insert into trans_license(id, bidder_id, target_id, status, license_no)"
							+ " values('"
							+ licenseId
							+ "', '"
							+ bidderId
							+ "', '"
							+ targetId
							+ "', 1,'"
							+ targetId
							+ bidderId + "')";
					pstm = getCon().prepareStatement(sqlLicense);
					pstm.executeUpdate();

					String cartId = IDGenerator.generatorID("trans_cart");
					String sqlCart = "insert into trans_cart(id, bidder_id,  status) values ('"
							+ cartId + "', '" + bidderId + "',1)";
					pstm = getCon().prepareStatement(sqlCart);
					pstm.executeUpdate();

					String sqlCartDtl = "insert into trans_cart_dtl(id, cart_id,target_id,  status , license_id) "
							+ "values ('"
							+ IDGenerator.generatorID("trans_cart_dtl")
							+ "', '"
							+ cartId
							+ "', '"
							+ targetId
							+ "',2, '"
							+ licenseId + "')";
					pstm = getCon().prepareStatement(sqlCartDtl);
					pstm.executeUpdate();

					engineDao.addUserId(targetId, userId);
				}
			}
		}

		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
	}

	public String timeAddSecond(String dateStr, long addSecond)
			throws Exception {
		Date date = DateUtils.getDate(dateStr);
		return DateUtils.getStr(new Date(date.getTime() + addSecond * 1000L));
	}

}
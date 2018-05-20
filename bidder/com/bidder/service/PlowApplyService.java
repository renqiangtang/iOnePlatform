package com.bidder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.bidder.dao.PlowApplyDao;

public class PlowApplyService extends BaseService
{

	/**
	 * 购物列表页面查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap goodsCantonList(ParaMap inMap) throws Exception
	{
		String businessType = inMap.getString("businessType");
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap cantonList = applyDao.goodsCantonList(businessType);
		ParaMap out = new ParaMap();
		out.put("cantons", cantonList.getListObj());
		return out;
	}

	/**
	 * 标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap transTargetlist(ParaMap inMap) throws Exception
	{
		ParaMap sqlParams = new ParaMap();
		sqlParams.putAll(inMap);
		String userId = inMap.getString("u");
		String cantonId = inMap.getString("cantonId");
		String businessType = inMap.getString("businessType");
		String beginArea = inMap.getString("beginArea");
		String endArea = inMap.getString("endArea");

		String bidderId = this.getBidderId(userId);
		sqlParams.put("bidderId", bidderId);
		String sql = "";
		if (StrUtils.isNotNull(cantonId) && !"-1".equals(cantonId))
		{
			sql = sql + " and tg.canton_id = :cantonId ";
		}
		if (StrUtils.isNotNull(businessType) && !"-1".equals(businessType))
		{
			sql = sql + " and tt.business_type = :businessType ";
		}
		else
		{
			sql = sql + " and tt.business_type like '501%'";
		}
		if (StrUtils.isNotNull(beginArea) && !"-1".equals(beginArea))
		{
			sqlParams.put("begin_area", beginArea);
			sql = sql + " and tg.area >=to_number(:begin_area) ";
		}
		if (StrUtils.isNotNull(endArea) && !"-1".equals(endArea))
		{
			sqlParams.put("end_area", endArea);
			sql = sql + " and tg.area <= to_number(:end_area) ";
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, StrUtils.isNull(inMap
		        .getString("pageIndex")) ? 1 : inMap.getString("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT,
		        inMap.getString("pageRowCount"));
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap out = applyDao.transTargetlist(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List resultList = new ArrayList();
		if (out.getSize() > 0)
		{
			List filds = out.getFields();
			for (int i = 0; i < out.getSize(); i++)
			{
				ParaMap targetMap = new ParaMap();
				List record = (List) out.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0)
				{
					for (int j = 0; j < filds.size(); j++)
					{
						targetMap.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j)))
						{
							id = (String) record.get(j);
						}
					}
				}
				// String canton_all_name =
				// targetMap.getString("canton_all_name");
				// targetMap.put("canton_all_name",
				// canton_all_name.substring(canton_all_name.indexOf(" "))); //
				// 去掉省

				String licenseStatus = targetMap.getString("license_status");
				int is_union = Integer
				        .parseInt(targetMap.getString("is_union"));
				Date begin_apply_time = null;
				String begin_apply_str = targetMap
				        .getString("begin_apply_time");
				if (StrUtils.isNotNull(begin_apply_str))
				{
					begin_apply_time = targetMap.getDate("begin_apply_time");
				}
				Date end_apply_time = null;
				String end_apply_str = targetMap.getString("end_apply_time");
				if (StrUtils.isNotNull(end_apply_str))
				{
					end_apply_time = targetMap.getDate("end_apply_time");
				}
				String bidder_id = targetMap.getString("trust_bidder_id");

				if (StrUtils.isNotNull(licenseStatus))
				{
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "您已申请了该指标！");
				}
				else if (is_union == 1)
				{
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "指标被联合竞买人选择");
				}
				else if (begin_apply_time == null || end_apply_time == null)
				{
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "该指标竞买申请时间错误");
				}
				else if ((DateUtils.now()).getTime() < begin_apply_time
				        .getTime())
				{
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "未到该指标竞买申请时间");
				}
				else if ((DateUtils.now()).getTime() > end_apply_time.getTime())
				{
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "已超过该指标竞买申请时间");
				}
				else
				{
					if (bidder_id.equals(bidderId))
					{
						targetMap.put("is_bid", 0);
						targetMap.put("bid_message", "自己发布的耕地指标");
					}
					else
					{
						targetMap.put("is_bid", 1);
						targetMap.put("bid_message", "能够购买");
					}
				}
				resultList.add(targetMap);
			}
		}
		out.clear();
		out.put("Rows", resultList);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 得到银行账号信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTransAccounts(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap target = applyDao.getTargetById(targetId);
		if (target == null)
		{
			out.put("state", 0);
			out.put("message", "请选择有效标的");
			return out;
		}
		String businessType = target.getString("business_type");
		ParaMap account = applyDao.getTransAccount(businessType);
		int totalRowCount = account.getInt("totalRowCount");
		out.put("banks", account.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 得到标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap targetInfo(ParaMap inMap) throws Exception
	{
		String targetId = inMap.getString("targetId");
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap target = applyDao.getTargetInfo(targetId);
		return target;
	}

	/**
	 * 得到标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap licenseInfo(ParaMap inMap) throws Exception
	{
		String licenseId = inMap.getString("licenseId");
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap license = applyDao.getLicenseInfo(licenseId);
		return license;
	}

	/**
	 * 是否能够购买
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap isCanBuy(ParaMap inMap) throws Exception
	{
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String targetId = inMap.getString("targetId");
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap out = new ParaMap();
		// 判断是否登录
		if (StrUtils.isNull(bidderId))
		{
			out.put("state", 0);
			out.put("message", "请先登录。");
			return out;
		}
		ParaMap target = applyDao.getTargetById(targetId);
		if (target == null)
		{
			out.put("state", 0);
			out.put("message", "耕地指标无效。");
			return out;
		}
		// 校验是否能够购买
		ParaMap canMap = applyDao.isCanBuy(bidderId, target);
		if (!"1".equals(canMap.getString("state")))
		{
			out.put("state", 0);
			out.put("message", canMap.getString("message"));
			return out;
		}
		out.put("state", 1);
		out.put("message", "可以竞买。");
		return out;
	}

	/**
	 * 保存竞买申请
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveApply(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String targetId = inMap.getString("targetId");
		PlowApplyDao applyDao = new PlowApplyDao();
		// 判断是否登录
		if (StrUtils.isNull(bidderId))
		{
			out.put("state", 0);
			out.put("message", "提交 竞买申请失败！请先登录。");
			return out;
		}
		// 判断标的是否有效
		if (StrUtils.isNull(targetId))
		{
			out.put("state", 0);
			out.put("message", "提交竞买申请失败！请选择有效耕地指标。");
			return out;
		}
		ParaMap target = applyDao.getTargetById(targetId);
		if (target == null)
		{
			out.put("state", 0);
			out.put("message", "提交竞买申请失败！请选择有效耕地指标。");
			return out;
		}
		// 校验是否能够购买
		ParaMap canMap = applyDao.isCanBuy(bidderId, target);
		if (!"1".equals(canMap.getString("state")))
		{
			out.put("state", 0);
			out.put("message", "竞买申请失败！" + canMap.getString("message"));
			return out;
		}

		// 得到银行账号信息
		String accountId = inMap.getString("accountId");
		ParaMap accountMap = new ParaMap();
		if (StrUtils.isNull(accountId))
		{
			out.put("state", 0);
			out.put("message", "竞买申请失败！请选择交款银行。");
			return out;
		}
		else
		{
			accountMap = applyDao.getBankAccount(accountId, userId);
			if (!"1".equals(accountMap.getString("state")))
			{
				if (StrUtils.isNotNull(accountMap.getString("message")))
				{// 说明得到银行账号出错
					out.put("state", 0);
					out.put("message",
					        "竞买申请失败！" + accountMap.getString("message"));
					return out;
				}
				else
				{
					out.put("state", 0);
					out.put("message", "竞买申请失败！获取交款账号失败。");
					return out;
				}
			}
		}

		// 保存trans_license
		String trustType = "0";
		String trustPrice = inMap.getString("trustPrice");
		if (StrUtils.isNotNull(trustPrice))
		{
			trustType = "1";
		}
		else
		{
			trustType = "0";
			trustPrice = "";
		}
		String licenseId = null;
		ParaMap licenseMap = applyDao.saveLicense(bidderId, targetId,
		        trustType, trustPrice, userId);
		if ("1".equals(licenseMap.getString("state")))
		{
			licenseId = licenseMap.getString("id");
			out.put("licenseId", licenseId);
		}
		else
		{
			out.put("state", 0);
			out.put("message", "竞买申请失败！保存竞买申请信息失败。");
			return out;
		}

		// 保存入账申请单
		ParaMap applyMap = applyDao.saveAccountBillApply(licenseId, targetId,
		        accountMap);
		if (!"1".equals(applyMap.getString("state")))
		{
			throw new Exception("竞买申请失败！保存竞买申请信息失败。");
		}
		// 如果是虚拟子账号则更新子账号使用次数
		String inAccountMode = accountMap.getString("inAccountMode");
		if ("2".equals(inAccountMode))
		{
			applyDao.updateSubAccountUseNum(accountMap
			        .getString("childAccountId"));
		}

		// 发送短信
		applyDao.saveLicenseSendMessage(targetId, userId, bidderId, null,
		        licenseId);

		out.put("state", 1);
		out.put("licenseId", licenseId);
		return out;
	}

	public ParaMap deleteApply(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		String licenseId = inMap.getString("licenseId");
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		if (StrUtils.isNull(bidderId))
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！请先登录或失败。");
			return out;
		}
		PlowApplyDao applyDao = new PlowApplyDao();
		ParaMap license = applyDao.getLicenseById(licenseId);
		if (license == null)
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！请选择有效的竞买申请。");
			return out;
		}
		if (!bidderId.equals(license.getString("bidder_id")))
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！您不是该竞买申请的有效竞买人。");
			return out;
		}
		if (license.getInt("status") == 1)
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！竞买申请已确认通过。");
			return out;
		}
		if (license.getInt("status") > 1)
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！交易已结束。");
			return out;
		}
		if (license.getString("earnest_money_pay") != null
		        && license.getString("earnest_money_pay").equals("1"))
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！已交满保证金。");
			return out;
		}
		ParaMap target = applyDao.getTargetById(license.getString("target_id"));
		if (target != null
		        && StrUtils.isNotNull(target.getString("trans_condition"))
		        && license.getString("confirmed") != null
		        && license.getString("confirmed").equals("1"))
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！竞买申请已审核。");
			return out;
		}
		List accountList = applyDao.getTransAccountBillList(licenseId);
		if (accountList != null && accountList.size() > 0)
		{
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！该竞买申请已经有款进账。");
			return out;
		}
		out = applyDao.deleteApply(licenseId);
		return out;
	}

	public ParaMap bidderLicenseList(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		// 组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");

		ParaMap sqlParams = inMap;// new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT,
		        inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		if (StrUtils.isNull(bidderId))
		{
			out.put("state", 0);
			out.put("message", "传入的用户非竞买人，无法查询竞买申请列表");
			return out;
		}
		sqlParams.put("bidder_id", bidderId);
		// 自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("targetNo"))
		{
			String target = inMap.getString("targetNo");
			if (StrUtils.isNotNull(target))
			{
				sqlParams.put("no", target);
				sqlParams.put("name", target);
				customCondition
				        .append(" and (t.no like '%' || :no || '%' or t.name like '%' || :name || '%')");
			}
		}
		if (inMap.containsKey("targetStatus"))
		{
			String targetStatus = inMap.getString("targetStatus");
			if (StrUtils.isNotNull(targetStatus) && !"-1".equals(targetStatus))
			{
				List<String> targetStatusList = StrUtils.getSubStrs(
				        targetStatus, ";", true);
				if (targetStatusList != null || targetStatusList.size() > 0)
				{
					customCondition.append(" and t.status in (");
					for (int i = 0; i < targetStatusList.size(); i++)
					{
						sqlParams.put("trans_status" + i,
						        targetStatusList.get(i));
						customCondition.append(":trans_status" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("businessType"))
		{
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType) && !"-1".equals(businessType))
			{
				List<String> businessTypeList = StrUtils.getSubStrs(
				        businessType, ";", true);
				if (businessTypeList != null || businessTypeList.size() > 0)
				{
					customCondition.append(" and t.business_type in (");
					for (int i = 0; i < businessTypeList.size(); i++)
					{
						sqlParams.put("business_type" + i,
						        businessTypeList.get(i));
						customCondition.append(":business_type" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("transType"))
		{
			String transType = inMap.getString("transType");
			if (StrUtils.isNotNull(transType) && !"-1".equals(transType))
			{
				List<String> transTypeList = StrUtils.getSubStrs(transType,
				        ";", true);
				if (transTypeList != null || transTypeList.size() > 0)
				{
					customCondition.append(" and t.trans_type_label in (");
					for (int i = 0; i < transTypeList.size(); i++)
					{
						sqlParams.put("trans_type" + i, transTypeList.get(i));
						customCondition.append(":trans_type" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("applyTime"))
		{
			String applyTime = inMap.getString("applyTime");
			if (applyTime != null && !applyTime.equals("")
			        && !applyTime.equalsIgnoreCase("null"))
			{
				sqlParams.put("applyTime", applyTime);
				customCondition
				        .append(" and l.apply_date >= to_date(:applyTime,'yyyy-mm-dd')  ");
			}
		}
		
		if (inMap.containsKey("endapplyTime"))
		{
			String endapplyTime = inMap.getString("endapplyTime");
			if (endapplyTime != null && !endapplyTime.equals("")
			        && !endapplyTime.equalsIgnoreCase("null"))
			{
				sqlParams.put("endapplyTime", endapplyTime);
				customCondition
				        .append(" and l.apply_date < to_date(:endapplyTime,'yyyy-mm-dd')+1 ");
			}
		}
		if (inMap.containsKey("beginLimitTime"))
		{
			String beginLimitTime = inMap.getString("beginLimitTime");
			if (beginLimitTime != null && !beginLimitTime.equals("")
			        && !beginLimitTime.equalsIgnoreCase("null"))
			{
				sqlParams.put("begin_limit_time", beginLimitTime);
				customCondition
				        .append(" and t.begin_limit_time <to_date(:begin_limit_time,'yyyy-mm-dd')+1");
			}
		}
		if (inMap.containsKey("status"))
		{
			String status = inMap.getString("status");
			if (StrUtils.isNotNull("status") && !"-1".equals(status))
			{
				sqlParams.put("status", status);
				customCondition.append(" and l.status = :status ");
			}
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		// 查询数据
		PlowApplyDao applyDao = new PlowApplyDao();
		out = applyDao.getBidderLicenseList(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List list = out.getListObj();
		out.clear();
		out.put("Rows", list);
		out.put("Total", totalRowCount);
		return out;
	}

	public String getBidderId(String userId) throws Exception
	{
		String bidderId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.querySimpleData("sys_user", sqlParams);
		String user_type = null;
		String ref_id = null;
		if (result.getSize() > 0)
		{
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0)
			{
				for (int i = 0; i < filds.size(); i++)
				{
					if (filds.get(i).equals("user_type"))
					{
						user_type = String.valueOf(record.get(i));
					}
					if (filds.get(i).equals("ref_id"))
					{
						ref_id = (String) record.get(i);
					}
				}
			}
		}
		if (StrUtils.isNotNull(user_type) && "1".equals(user_type))
		{
			bidderId = ref_id;
		}
		return bidderId;
	}

}

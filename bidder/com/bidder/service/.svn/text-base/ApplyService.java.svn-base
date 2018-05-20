package com.bidder.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.Pdf2SwfUtils;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.before.dao.TransGoodsLandDao;
import com.before.dao.TransNoticeDao;
import com.bidder.dao.TransLicenseDao;
import com.trademan.dao.LicenseManageDao;

public class ApplyService extends BaseService {

	/**
	 * 初始化交易须知
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initShould(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		String pdfFullName = licenseDao.getTargetAttachFilePath(targetId);
		String fileRoot = AppConfig.getPro("fileRoot");
		File inFile = new File(fileRoot + "/" + pdfFullName);
		if (inFile.getAbsolutePath().endsWith(".pdf")) {
			String swfFullDir = fileRoot + "/swf/should/" + targetId;
			File outFile = new File(swfFullDir);
			if (!outFile.exists())
				Pdf2SwfUtils.pdf2swf(inFile.getAbsolutePath(),
						outFile.getAbsolutePath());
			out.put("path", swfFullDir.substring(fileRoot.length()));
			out.put("count", outFile.listFiles().length);
		}
		return out;

	}

	public ParaMap checkCarTarget(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		// 校验当前标的状态及是否已经购买
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap result = licenseDao.isCanBuy(bidderId, targetId);
		return result;
	}

	public ParaMap getTransOrganByTargetId(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		TransGoodsLandDao goodsDao = new TransGoodsLandDao();
		ParaMap result = goodsDao.getTargetTransOrgan(targetId);
		if (result != null && result.getSize() > 0) {
			out.put("organId", result.getRecordString(0, "id"));
			out.put("transOrganName", result.getRecordString(0, "name"));
			out.put("cantonName", result.getRecordString(0, "canton_name"));
			out.put("cantonId", result.getRecordString(0, "canton_id"));
			out.put("transTypeName", result.getRecordString(0, "trans_type_name"));
			
		}
		result = goodsDao.getTargetTypeInfo(targetId);
		if (result != null && result.getSize() > 0) {
			out.put("businessName", result.getRecordString(0, "business_name"));
			out.put("goodsTypeName",
					StrUtils.isNotNull(result.getRecordString(0,
							"goods_type_full_name")) ? result.getRecordString(
							0, "goods_type_full_name") : result
							.getRecordString(0, "goods_type_name"));
		}
		return out;
	}

	public ParaMap getTransOrganByLicenseId(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String licenseId = inMap.getString("licenseId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap licenseMap = licenseDao.getTransLicense(licenseId);
		if (licenseMap != null && licenseMap.getSize() > 0) {
			String targetId = licenseMap.getRecordString(0, "target_id");
			TransGoodsLandDao goodsDao = new TransGoodsLandDao();
			ParaMap result = goodsDao.getTargetTransOrgan(targetId);
			if (result != null && result.getSize() > 0) {
				out.put("organId", result.getRecordString(0, "id"));
				out.put("transOrganName", result.getRecordString(0, "name"));
				out.put("cantonName", result.getRecordString(0, "canton_name"));
				out.put("cantonId", result.getRecordString(0, "canton_id"));
				out.put("noticeName", result.getRecordString(0, "notice_name"));
				out.put("transTypeName", result.getRecordString(0, "trans_type_name"));
			}
			result = goodsDao.getTargetTypeInfo(targetId);
			if (result != null && result.getSize() > 0) {
				out.put("businessName",
						result.getRecordString(0, "business_name"));
				out.put("goodsTypeName",
						StrUtils.isNotNull(result.getRecordString(0,
								"goods_type_full_name")) ? result
								.getRecordString(0, "goods_type_full_name")
								: result.getRecordString(0, "goods_type_name"));
			}
		}

		return out;
	}

	/**
	 * 得到竞买步骤
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initLicenseEditStep(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		// 校验当前标的状态及是否已经购买
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap result = licenseDao.isCanBuy(bidderId, targetId);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "竞买申请失败！" + result.getString("message"));
			return out;
		}
		// 得到交易方式，交易类型id
		ParaMap targetMap = result;
		String businessType = String.valueOf(targetMap.getRecordValue(0,
				"business_type"));
		String transTypeId = String.valueOf(targetMap.getRecordValue(0,
				"trans_type_id"));
		String allow_union = String.valueOf(targetMap.getRecordValue(0,
				"allow_union"));
		String allow_trust = String.valueOf(targetMap.getRecordValue(0,
				"allow_trust"));
		List stepsList = licenseDao.getTargetLicenseSetps(targetId,
				businessType, transTypeId, allow_union, allow_trust);
		out.put("state", 1);
		out.put("steps", stepsList);
		return out;
	}

	/**
	 * 得到银行账号信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTransAccounts(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		TransNoticeDao transNoticeDao = new TransNoticeDao();
		// 得到标的保证金类型
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap earnestMap = dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
		List earnestList = this.changeToObjList(earnestMap, null);
		// 根据标的得到公告id
		ParaMap noticeMap = transNoticeDao.getNoticeByTargetId(targetId, "0");
		String noticeId = null;
		if (noticeMap.getSize() > 0) {
			noticeId = (String) noticeMap.getRecordValue(0, "id");
		}
		// 得到公告银行账号
		out = transNoticeDao.getNoticeAccountList(noticeId);
		int totalRowCount = out.getInt("totalRowCount");
		List items = this.changeToObjList(out, null);
		// 组装成币种和银行列表
		List currencyList = new ArrayList();
		
		//只查找保证金币种的公告银行账户
//		for (int k = 0; k < earnestList.size(); k++) {
//			ParaMap earnest = (ParaMap) earnestList.get(k);
//			String earnestCurrency = earnest.getString("currency");
//			if (items != null && items.size() > 0) {
//				for (int i = 0; i < items.size(); i++) {
//					ParaMap account = (ParaMap) items.get(i);
//					String currency = account.getString("currency");
//					if (earnestCurrency.equals(currency)) {
//						if (currencyList != null && currencyList.size() > 0) {
//							if (currencyList.indexOf(currency) < 0) {
//								currencyList.add(currency);
//							}
//						} else {
//							currencyList.add(currency);
//						}
//						break;
//					}
//				}
//			}
//		}
		
		//所有的公告银行账户
		for (int i = 0; i < items.size(); i++) {
			ParaMap account = (ParaMap) items.get(i);
			String currency = account.getString("currency");
			if (currencyList != null && currencyList.size() > 0) {
				if (currencyList.indexOf(currency) < 0) {
					currencyList.add(currency);
				}
			} else {
				currencyList.add(currency);
			}
		}

		List accountList = new ArrayList();
		ParaMap accountMap = new ParaMap();
		if (currencyList != null && currencyList.size() > 0) {
			for (int i = 0; i < currencyList.size(); i++) {
				String currency = (String) currencyList.get(i);
				List currencyAccountList = new ArrayList();
				for (int j = 0; j < items.size(); j++) {
					ParaMap account = (ParaMap) items.get(j);
					String currencyTemp = account.getString("currency");
					if (currencyTemp.equals(currency)) {
						currencyAccountList.add(account);
					}
				}
				accountMap.put(currency, currencyAccountList);
			}
		}

		out.clear();
		out.put("banks", accountMap);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 保存竞买申请
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveLicenseApply(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");// 登录人ID
		String targetId = inMap.getString("targetId");
		String goodsType = inMap.getString("goodsType");
		// applyUnit='{style:1,proportions:{[A公司:20],[B公司:30]}}';//style=1 联合竞买
		// =0 独立竞买
		// applyTrust='{style:1,account:1000}';//style=1 委托系统自动报价 =0 无
		// applyBank='{bankAccount:"fadsfasdf"}';//bankAccount 银行账号
		String applyUnit = inMap.getString("applyUnit");
		String applyCorp = inMap.getString("applyCorp");
		String applyTrust = inMap.getString("applyTrust");
		String applyBank = inMap.getString("applyBank");

		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap user = licenseDao.getSysUser(userId);
		if (user != null && user.getSize() > 0) {
			// String listType = user.getRecordString(0,
			// "list_type");//user_type
			// if(listType.startsWith("2")){
			// out.put("state", 0);
			// String message =
			// "竞买申请失败！你因违反国土局相关规定已被系统列入黑名单，禁止参与系统竞买，请与国土局相关单位联系！";
			// out.put("message", message);
			// return out;
			// }
		} else {
			out.put("state", 0);
			out.put("message", "竞买申请失败！您不是有效系统用户。");
			return out;
		}

		// 竞买方式,联合竞买，独立竞买
		int unitStyle = 0;
		int dlstyle=-1;
		List unitProportions = new ArrayList();
		ParaMap unitMap = null;
		if (StrUtils.isNotNull(applyUnit)) {
			unitMap = ParaMap.fromString(applyUnit);
			unitStyle = unitMap.getInt("style");
			if(unitStyle==0){
				dlstyle = unitMap.getInt("dlstyle");
			}
			
			if (unitMap.get("proportions") != null) {
				unitProportions = unitMap.getList("proportions");
			}
		}
		// 委托报价
		String trustType = null;
		String trustPrice = null;
		if (StrUtils.isNotNull(applyTrust)) {
			ParaMap trustMap = ParaMap.fromString(applyTrust);
			trustType = trustMap.getString("style");
			trustPrice = trustMap.getString("account");
		}

		// 银行账号
		String accountId = null;
		if (StrUtils.isNotNull(applyBank)) {
			ParaMap bankMap = ParaMap.fromString(applyBank);
			accountId = bankMap.getString("bankAccount");
		}

		// 判断是否登录
		String bidderId = this.getBidderId(userId);
		if (StrUtils.isNull(bidderId)) {
			out.put("state", 0);
			out.put("message", "竞买申请失败！请先登录或失败。");
			return out;
		}
		if (StrUtils.isNull(targetId)) {
			out.put("state", 0);
			out.put("message", "竞买申请失败！请选择要申请的标的。");
			return out;
		}

		// 判断标的是否能够购买
		ParaMap targetMap = licenseDao.isCanBuy(bidderId, targetId);
		if (!"1".equals(targetMap.getString("state"))) {
			out.put("state", 0);
			out.put("message", "竞买申请失败！" + targetMap.getString("message"));
			return out;
		}
		// 校验联合竞买并得到所有联合竞买人
		List unionBidderList = new ArrayList();
		if (unitStyle == 1) {
			ParaMap biddersMap = licenseDao.getBidders(unitProportions,goodsType);
			if ("1".equals(biddersMap.getString("state"))) {
				unionBidderList = biddersMap.getList("bidderList");
			} else {
				if (StrUtils.isNotNull(biddersMap.getString("message"))) {// 说明得到银行账号出错
					out.put("state", 0);
					out.put("message",
							"竞买申请失败！" + biddersMap.getString("message"));
					return out;
				} else {
					out.put("state", 0);
					out.put("message", "竞买申请失败！获取联合竞买人信息错误。");
					return out;
				}
			}
		}

		ParaMap corpMap = null;
		List corpProportions = new ArrayList();
		ParaMap name = new ParaMap();
		// 校验股东并得到所有股东
		List corpBidderList = new ArrayList();
		if (StrUtils.isNotNull(applyCorp) && !applyCorp.equals("{}")) {
			corpMap = ParaMap.fromString(applyCorp);
			if (corpMap.get("proportions") != null) {
				corpProportions = corpMap.getList("proportions");
				ParaMap biddersMap = licenseDao.getCorp(corpProportions);
				if ("1".equals(biddersMap.getString("state"))) {
					corpBidderList = biddersMap.getList("bidderList");
				} else {
					if (StrUtils.isNotNull(biddersMap.getString("message"))) {
						out.put("state", 0);
						out.put("message",
								"竞买申请失败！" + biddersMap.getString("message"));
						return out;
					} else {
						out.put("state", 0);
						out.put("message", "竞买申请失败！获取股东信息错误。");
						return out;
					}
				}
			}
		}

		// 得到银行账号信息
		ParaMap accountMap = new ParaMap();
		if (StrUtils.isNull(accountId)) {
			out.put("state", 0);
			out.put("message", "竞买申请失败！请选择交款银行。");
			return out;
		} else {
			accountMap = licenseDao.getBankAccount(accountId, userId);
			if (!"1".equals(accountMap.getString("state"))) {
				if (StrUtils.isNotNull(accountMap.getString("message"))) {// 说明得到银行账号出错
					out.put("state", 0);
					out.put("message",
							"竞买申请失败！" + accountMap.getString("message"));
					return out;
				} else {
					out.put("state", 0);
					out.put("message", "竞买申请失败！获取交款账号失败。");
					return out;
				}
			}
		}

		// 保存trans_license
		String licenseId = null;
		int is_after_check = targetMap.getRecordInteger(0, "is_after_check");
		ParaMap licenseMap = licenseDao.saveLicense(bidderId, targetId,
				trustType, trustPrice, userId, is_after_check, unitStyle);
		if ("1".equals(licenseMap.getString("state"))) {
			licenseId = licenseMap.getString("id");
			if(unitStyle==0){
				//保存护展字段  0本地公司 1外地公司 2自然人   3境外及港、澳、台申请人4其他组织
				updateExtend("trans_license", licenseId, "dlstyle", String.valueOf(dlstyle), "0本地公司 1外地公司 2自然人   3境外及港、澳、台申请人4其他组织");
			}
			
			out.put("licenseId", licenseId);
			
			//保存扩展字段公司类型
			
			
		} else {
			out.put("state", 0);
			out.put("message", "竞买申请失败！保存竞买申请信息失败。");
			return out;
		}
		// 保存联合竞买人
		if (unitStyle == 1) {
			ParaMap unionMap = licenseDao.saveUnionBidder(licenseId, userId,
					unionBidderList);
			if (!"1".equals(unionMap.getString("state"))) {
				if (StrUtils.isNotNull(unionMap.getString("message"))) {
					throw new Exception("竞买申请失败！"
							+ unionMap.getString("message"));
				} else {
					throw new Exception("竞买申请失败！保存联合竞买信息失败。");
				}
			}
		}
		// 保存拟成立项目公司
		if (StrUtils.isNotNull(applyCorp) && !applyCorp.equals("{}")) {
			ParaMap companyMap = licenseDao.saveVirtualCompany(licenseId,
					corpMap, corpProportions);
			if (!"1".equals(companyMap.getString("state"))) {
				if (StrUtils.isNotNull(companyMap.getString("message"))) {
					throw new Exception("竞买申请失败！"
							+ companyMap.getString("message"));
				} else {
					throw new Exception("竞买申请失败！保存虚拟公司信息失败。");
				}
			}
		}
		// 保存入账申请单
		ParaMap applyMap = licenseDao.saveAccountBillApply(licenseId,
				accountMap);
		if (!"1".equals(applyMap.getString("state"))) {
			throw new Exception("竞买申请失败！保存竞买申请信息失败。");
		}
		// 如果是虚拟子账号则更新子账号使用次数
		String inAccountMode = accountMap.getString("inAccountMode");
		if ("2".equals(inAccountMode)) {
			licenseDao.updateSubAccountUseNum(accountMap
					.getString("childAccountId"));
		}
		// 修改购物车明细
		licenseDao.updateCartDtl(bidderId, targetId, 1, 2, userId);
		// 发送短信
		licenseDao.saveLicenseSendMessage(targetId, userId, bidderId,
				unionBidderList, licenseId);

		out.put("state", 1);
		out.put("licenseId", licenseId);
		return out;
	}

	public ParaMap deleteLicenseApply(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		if (StrUtils.isNull(bidderId)) {
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！请先登录或失败。");
			return out;
		}
		// 得到竞买申请信息 委托报价信息
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap licenseListMap = licenseDao.getMainTransLicense(targetId,
				bidderId);
		if (licenseListMap == null || licenseListMap.getSize() <= 0) {
			out.put("state", 0);
			out.put("message", "删除竞买申请失败！未取得有效的竞买");
			return out;
		}
		LicenseManageDao dao = new LicenseManageDao();
		out = dao.deleteLicenseData(null, null,
				String.valueOf(licenseListMap.getRecordValue(0, "id")));
		if ("1".equals(out.getString("state"))) {
			// 修改购物车明细
			licenseDao.updateCartDtl(bidderId, targetId, 2, 1, userId);
		}
		return out;
	}

	/**
	 * 订单信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBillList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");// 登录人ID
		String bidderId = this.getBidderId(userId);

		ParaMap sqlParam = new ParaMap();
		sqlParam.put("bidderId", bidderId);
		sqlParam.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParam.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		sqlParam.put("moduleNo", "trans_bidder_license_land");
		sqlParam.put("dataSetNo", "get_orders_list");

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = dataSetDao.queryData(sqlParam);
		int totalRowCount = out.getInt("totalRowCount");
		List items = this.changeToObjList(out, null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	// applyUnit='{style:1,proportions:{A公司:20,B公司:30}}';//style=1 联合竞买 =0 独立竞买
	// applyTrust='{style:1,account:1000}';//style=1 委托系统自动报价 =0 无
	// applyBank='{bankAccount:"fadsfasdf"}';//bankAccount 银行账号
	/**
	 * 得到订单信息
	 */
	public ParaMap getBillInfo(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String licenseId = inMap.getString("licenseId");
		String valueTag = inMap.getString("valueTag");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap targetListMap = new ParaMap();
		ParaMap licenseListMap = new ParaMap();
		String targetId = null;
		if (StrUtils.isNotNull(licenseId)) {
			licenseListMap = licenseDao.getTransLicense(licenseId);
			if (licenseListMap.getSize() > 0) {
				targetId = String.valueOf(licenseListMap.getRecordValue(0,
						"target_id"));
				targetListMap = licenseDao.getTransTarget(targetId);
			}
		} else {
			targetId = inMap.getString("targetId");
			String userId = inMap.getString("u");
			String bidderId = this.getBidderId(userId);
			targetListMap = licenseDao.getTransTarget(targetId);
			licenseListMap = licenseDao.getTransLicense(targetId, bidderId);
		}

		if (targetListMap == null || targetListMap.getSize() <= 0) {
			out.put("state", 0);
			out.put("message", "查看竞买申请信息失败！");
			return out;
		}
		out.put("targetNo", targetListMap.getRecordValue(0, "no"));

		if (licenseListMap == null || licenseListMap.getSize() <= 0) {
			out.put("state", 0);
			out.put("message", "查看竞买申请信息失败！");
			return out;
		}
		List licenseList = this.changeToObjList(licenseListMap, null);
		ParaMap licenseMap = (ParaMap) licenseList.get(0);
		licenseId = licenseMap.getString("id");

		List targetList = this.changeToObjList(targetListMap, null);
		ParaMap targetMap = (ParaMap) targetList.get(0);

		// 得到委托报价信息
		if ("applyTrust".equals(valueTag)) {
			out.put("style", licenseMap.getString("trust_type"));
			out.put("account", licenseMap.getBigDecimal("trust_price"));
		}

		// 得到联合竞买人信息
		if ("applyUnit".equals(valueTag)) {
			List resultList = licenseDao.getLicenseUnionList(licenseId);
			if (resultList != null && resultList.size() > 0) {
				out.put("state", 1);
				out.put("unit", resultList);
				if (resultList.size() > 1) {
					out.put("style", "1");
					out.put("styleName", "联合竞买");
				} else {
					out.put("style", "0");
					out.put("styleName", "独立竞买");
					// ParaMap virtualCompany = licenseDao
					// .getVirtualCompany(licenseId);
					// if (virtualCompany == null) {
					// out.put("style", "0");
					// out.put("styleName", "独立竞买");
					// }
					// else {
					// out.put("style", "2");
					// out.put("styleName", "虚拟项目公司");
					// out.put("company", virtualCompany);
					// }
				}
			} else {
				out.put("state", 0);
				out.put("message", "查看联合竞买人信息失败！");
				return out;
			}
		}
		// 得到拟成立项目公司信息
		if ("applyCorp".equals(valueTag)) {
			List resultList = licenseDao.getLicenseUnionList(licenseId);
			if (resultList != null && resultList.size() > 0) {
				ParaMap virtualCompany = licenseDao
						.getVirtualCompany(licenseId);
				out.put("state", 1);
				out.put("corp", resultList);
				out.put("styleName", "拟成立项目公司");
				out.put("company", virtualCompany);
			} else {
				out.put("state", 0);
				out.put("message", "查看拟成立项目公司信息失败！");
				return out;
			}
		}

		// 得到入账信息
		if ("applyInAccount".equals(valueTag)) {
			out = licenseDao.getAccount(licenseId, targetId);
		}
		// 步骤
		if ("steps".equals(valueTag)) {
			String businessType = String.valueOf(targetMap
					.getString("business_type"));
			String transTypeId = String.valueOf(targetMap
					.getString("trans_type_id"));
			String allow_union = String.valueOf(targetMap
					.getString("allow_union"));
			String allow_trust = String.valueOf(targetMap
					.getString("allow_trust"));
			List stepsList = licenseDao.getTargetLicenseSetps(targetId,
					businessType, transTypeId, allow_union, allow_trust);
			out.put("steps", stepsList);
		}
		out.put("state", 1);
		out.put("licenseId", licenseId);
		return out;
	}

	/**
	 * 得到入账单信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getApplyInAccount(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String licenseId = inMap.getString("licenseId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap targetListMap = new ParaMap();
		ParaMap licenseListMap = new ParaMap();
		String targetId = null;
		if (StrUtils.isNotNull(licenseId)) {
			licenseListMap = licenseDao.getTransLicense(licenseId);
			if (licenseListMap.getSize() > 0) {
				targetId = String.valueOf(licenseListMap.getRecordValue(0,
						"target_id"));
				targetListMap = licenseDao.getTransTarget(targetId);
			}
		} else {
			targetId = inMap.getString("targetId");
			String userId = inMap.getString("u");
			String bidderId = this.getBidderId(userId);
			targetListMap = licenseDao.getTransTarget(targetId);
			licenseListMap = licenseDao.getTransLicense(targetId, bidderId);
		}
		if (licenseListMap == null || licenseListMap.getSize() <= 0) {
			out.put("state", 0);
			out.put("message", "查看入账申请信息失败！");
			return out;
		}
		List licenseList = this.changeToObjList(licenseListMap, null);
		ParaMap licenseMap = (ParaMap) licenseList.get(0);
		licenseId = licenseMap.getString("id");

		// 得到标的信息
		targetListMap = licenseDao.getTransTarget(targetId);
		if (targetListMap == null || targetListMap.getSize() <= 0) {
			out.put("state", 0);
			out.put("message", "查看入账申请信息失败！");
			return out;
		}
		List targetList = this.changeToObjList(targetListMap, null);
		ParaMap targetMap = (ParaMap) targetList.get(0);

		out = licenseDao.getAccount(licenseId, targetId);
		if(!out.containsKey("amount")){
			out.put("oldCurrency", out.getString("currency"));
			out.put("currency", "CNY");
			out.put("amount", licenseDao.getTargetEarnest(targetId, "CNY"));
		}
		out.put("no", targetMap.getString("no"));
		out.put("earnest_money", targetMap.get("earnest_money"));
		out.put("trustType", licenseMap.getString("trust_type"));
		out.put("trustPrice", licenseMap.getString("trust_price"));
		out.put("target_unit", targetMap.getString("unit"));

		List unionList = licenseDao.getLicenseUnionList(licenseId);
		if (unionList != null && unionList.size() > 0) {
			out.put("unit", unionList);
			if (unionList.size() > 1) {
				out.put("unitStyle", 1);
				out.put("unitStyleName", "联合竞买");
				//联合竞买则查询被联合人是否全部同意 
				ParaMap keyData=new ParaMap();
				keyData.put("license_id", licenseId);
				keyData.put("status", 1);
				DataSetDao dataSetDao=new DataSetDao();
				ParaMap pm=dataSetDao.querySimpleData("trans_license_union", keyData);
				if(pm.getSize()!=unionList.size()-1){//-1是因为被联合人要减去自己
					out.put("unionMessage", "<span class='c6 fb'>被联合人全部同意联合后,在我的订单中查询入款帐号。</span>");
				}
			} else {
				out.put("unitStyle", 0);
				out.put("unitStyleName", "独立竞买");
				// ParaMap virtual = licenseDao.getVirtualCompany(licenseId);
				// if (virtual == null) {
				// out.put("unitStyle", 0);
				// out.put("unitStyleName", "独立竞买");
				// } else {
				// out.put("unitStyle", 2);
				// out.put("unitStyleName", "虚拟子公司");
				// }
			}
			
		} else {
			out.put("state", 0);
			out.put("message", "查看入账申请信息失败！");
			return out;
		}
		out.put("state", 1);
		out.put("licenseId", licenseId);
		return out;
	}

	/**
	 * 查看竞买情况，份额
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getLicenseUnionList(ParaMap inMap) throws Exception {
		String licenseId=inMap.getString("licenseId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap out=new ParaMap();
		List unionList = licenseDao.getLicenseUnionList(licenseId);
		if (unionList != null && unionList.size() > 0) {
			out.put("unit", unionList);
			if (unionList.size() > 1) {
				out.put("unitStyle", 1);
				out.put("unitStyleName", "联合竞买");
			} else {
				out.put("unitStyle", 0);
				out.put("unitStyleName", "独立竞买");
			}
		} else {
			out.put("state", 0);
			out.put("message", "查看竞买情况失败！");
			return out;
		}
		return out;
	}
	// /**
	// * 入账单列表
	// * @param inMap
	// * @return
	// * @throws Exception
	// */
	// public ParaMap getAccountBillList(ParaMap inMap)throws Exception{
	// ParaMap out = new ParaMap();
	// String licenseId = inMap.getString("licenseId");
	// TransLicenseDao licenseDao = new TransLicenseDao();
	// ParaMap targetListMap = new ParaMap();
	// ParaMap licenseListMap = new ParaMap();
	// String targetId = null;
	// if(StrUtils.isNotNull(licenseId)){
	// licenseListMap = licenseDao.getTransLicense(licenseId);
	// if(licenseListMap.getSize()>0){
	// targetId = String.valueOf(licenseListMap.getRecordValue(0, "target_id"));
	// targetListMap = licenseDao.getTransTarget(targetId);
	// }
	// }else{
	// targetId = inMap.getString("targetId");
	// String userId = inMap.getString("u");
	// String bidderId = this.getBidderId(userId);
	// targetListMap = licenseDao.getTransTarget(targetId);
	// licenseListMap = licenseDao.getTransLicense(targetId , bidderId);
	// }
	//
	// if(licenseListMap==null || licenseListMap.getSize()<=0){
	// out.put("state",0);
	// out.put("message","查看入账单失败！");
	// return out;
	// }
	// licenseId = (String)licenseListMap.getRecordValue(0, "id");
	// String licenseBidderId = (String)licenseListMap.getRecordValue(0,
	// "bidder_id");
	// if(!licenseBidderId.equals(bidderId)){
	// ParaMap unionListMap = licenseDao.getTransLicenseUnion(licenseId);
	// if(unionListMap.getSize()>0){
	// boolean inFalg = false;
	// int unionStatus = 0;
	// for(int i = 0 ; i < unionListMap.getSize() ; i++){
	// String tempBidderId = String.valueOf(unionListMap.getRecordValue(i,
	// "bidder_id"));
	// String status = String.valueOf(unionListMap.getRecordValue(i, "status"));
	// if(tempBidderId.equals(bidderId)){
	// inFalg = true;
	// unionStatus = Integer.parseInt(status);
	// break;
	// }
	// }
	// if(inFalg){
	// if(unionStatus!=1){
	// out.put("state",0);
	// out.put("message","查看入账单失败！您未确认被联合竞买该标的。");
	// return out;
	// }
	// }else{
	// out.put("state",0);
	// out.put("message","查看入账单失败！您不是该标的的有效竞买人。");
	// return out;
	// }
	// }else{
	// out.put("state",0);
	// out.put("message","查看入账单失败！您不是该标的的有效竞买人");
	// return out;
	// }
	// }
	// out = licenseDao.getAccountBillList(licenseId);
	// int totalRowCount = out.getInt("totalRowCount");
	// List items = this.changeToObjList(out, null);
	// out.clear();
	// out.put("Rows", items);
	// out.put("Total", totalRowCount);
	// return out;
	// }

	/**
	 * 确认联合竞买申请
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap confirmUnionLicense(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String licenseId = inMap.getString("licenseId");
		String confirmed = inMap.getString("confirmed");
		ParaMap licenseMap = new ParaMap();
		licenseMap.put("u", userId);
		licenseMap.put("bidder_id", bidderId);
		licenseMap.put("id", licenseId);
		licenseMap.put("confirmed", confirmed);
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.confirmUnionLicense(null, null, licenseMap);
		return out;
	}

	public String getBidderId(String userId) throws Exception {
		String bidderId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.querySimpleData("sys_user", sqlParams);
		String user_type = null;
		String ref_id = null;
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("user_type")) {
						user_type = String.valueOf(record.get(i));
					}
					if (filds.get(i).equals("ref_id")) {
						ref_id = (String) record.get(i);
					}
				}
			}
		}
		if (StrUtils.isNotNull(user_type) && "1".equals(user_type)) {
			bidderId = ref_id;
		}
		return bidderId;
	}

	/**
	 * 得到单位id
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("id")) {
						organId = (String) record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}

	public List changeToObjList(ParaMap in, String firstId) throws Exception {
		List resultList = new ArrayList();
		if (StrUtils.isNotNull(firstId)) {
			ParaMap fMap = new ParaMap();
			fMap.put("id", firstId);
			resultList.add(fMap);
		}
		if (in.getSize() > 0) {
			List filds = in.getFields();
			for (int i = 0; i < in.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) in.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				if (StrUtils.isNotNull(firstId) && StrUtils.isNotNull(id)
						&& id.equals(firstId)) {
					resultList.set(0, out);
				} else {
					resultList.add(out);
				}
			}
		}
		return resultList;
	}

	public int getPageTotal(int rowCount, int pageSize) {
		return (rowCount + pageSize - 1) / pageSize;
	}

	public ParaMap getAllowUnion(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", inMap.get("targetId"));
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_target", sqlParams);
		String allowUnion = "";
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("allow_union")) {
						allowUnion = String.valueOf(record.get(i));
						out.put("allowUnion", allowUnion);
					}
				}
			}
		}
		return out;
	}
	
	public ParaMap updateExtend(String ref_table_name, String ref_id,
			String field_no, String field_value, String remark)
			throws Exception {
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap extendMap = new ParaMap();
		extendMap.put("id", null);
		extendMap.put("ref_table_name", ref_table_name);
		extendMap.put("ref_id", ref_id);
		extendMap.put("field_no", field_no);
		extendMap.put("field_value", field_value);
		extendMap.put("remark", remark);
		result = dataSetDao.updateData("sys_extend", "id",
				"ref_table_name,ref_id,field_no", extendMap, null);
		return result;
	}

	/**
	 * 获取竞买方式及信息
	 * @param inMap
	 * @return
	 * @throws Exception 
	 */
	public ParaMap getUnion(ParaMap inMap) throws Exception{
		DataSetDao dataSetDao=new DataSetDao();
		String licenseId=inMap.getString("licenseId");
		ParaMap out=new ParaMap();
		if(licenseId==null || licenseId==""){
			out.put("state", 0);
			out.put("message","竞买申请ID为空！");
			return out;
		}
		TransLicenseDao licenseDao = new TransLicenseDao();
		List resultList = licenseDao.getLicenseUnionList(licenseId);
		if (resultList != null && resultList.size() > 0) {
			out.put("unit", resultList);
			if (resultList.size() > 1) {
				out.put("style", "1");
				out.put("styleName", "联合竞买");
				//联合竞买查找联合人
				out.put("Rows", resultList);
			} else {
				out.put("style", "0");
				out.put("styleName", "独立竞买");
				//独立竞买查找他是什么公司 0本地公司 1外地公司 2自然人   3境外及港、澳、台申请人4其他组织
				ParaMap keyData=new ParaMap();
				keyData.clear();
				keyData.put("ref_id", licenseId);
				keyData.put("ref_table_name", "trans_license");
				ParaMap extendMap = dataSetDao.querySimpleData("sys_extend",
						keyData);
				if (extendMap.getSize() > 0) {
					List filds = extendMap.getFields();
					for (int i = 0; i < extendMap.getSize(); i++) {
						List record = (List) extendMap.getRecords().get(i);
						String field_no = "";
						String field_value = "";
						if (filds != null && filds.size() > 0) {
							for (int j = 0; j < filds.size(); j++) {
								if ("field_no".equals((String) filds.get(j))) {
									field_no = (String) record.get(j);
								}
								if ("field_value".equals((String) filds.get(j))) {
									field_value = (String) record.get(j);
								}
							}
						}
						out.put(field_no, field_value);
					}
				}
			}
			
		} 
		
		//获取拟成立公司的相关信息
		ParaMap projectDealerMap=new ParaMap();
		projectDealerMap.put("license_Id", licenseId);
		ParaMap outPDMap=dataSetDao.querySimpleData("trans_project_dealer", projectDealerMap);
		List items=MakeJSONData.makeItems(outPDMap);
		out.put("projectDealer", items);
		if(outPDMap.getSize()>0){//有拟成立公司，则查找股东
			ParaMap projectDealerOwnerMap=new ParaMap();
			projectDealerOwnerMap.put("pd_Id", outPDMap.getRecordValue(0, "id"));
			ParaMap outPDOMap=dataSetDao.querySimpleData("trans_project_dealer_owner", projectDealerOwnerMap);
			List list=MakeJSONData.makeItems(outPDOMap);
			out.put("RowsPDO", list);
		}
		return out;
	}
	/*
	 * public ParaMap getSteps(ParaMap targetMap)throws Exception{ ParaMap out =
	 * new ParaMap(); String targetId =
	 * String.valueOf(targetMap.getRecordValue(0, "id")); String businessType =
	 * String.valueOf(targetMap.getRecordValue(0, "business_type")); String
	 * transTypeId = String.valueOf(targetMap.getRecordValue(0,
	 * "trans_type_id")); DataSetDao dataSetDao = new DataSetDao(); ParaMap
	 * keyData = new ParaMap(); keyData.put("business_type_rel_id",
	 * businessType); keyData.put("trans_type_id", transTypeId);
	 * keyData.put("is_valid",1); ParaMap transTypeRelMap =
	 * dataSetDao.querySimpleData("trans_transaction_type_rel", keyData); String
	 * transactionRelId = null; if(transTypeRelMap.getSize()>0){
	 * transactionRelId = String.valueOf(transTypeRelMap.getRecordValue(0,
	 * "id")); }else{ out.put("state", 0); out.put("message",
	 * "竞买申请失败！标的交易方式或业务类型错误。"); return out; } //得到业务类别范围 ConfigDao configDao =
	 * new ConfigDao(); String configStr = configDao.getBusinessType(6); ParaMap
	 * configObj = ParaMap.fromString(configStr); List list =
	 * (List)configObj.getDataByPath
	 * (businessType+"."+transactionRelId+".steps");
	 * 
	 * //以下循环步骤，排除标的不需要的步骤，并且设置module_url List stepsList = new ArrayList();
	 * 
	 * if(list!=null && list.size()>0){ for(int i = 0 ; i < list.size(); i++){
	 * ParaMap stepMap = (ParaMap)list.get(i); String no =
	 * stepMap.getString("no"); String moduleId =
	 * stepMap.getString("module_id"); String moduleParam =
	 * stepMap.getString("module_params"); String moduleUrl =
	 * stepMap.getString("module_url"); String condition =
	 * stepMap.getString("condition"); boolean isMust = true;
	 * if(StrUtils.isNotNull(condition)){ keyData.clear(); keyData.put("sql",
	 * condition); keyData.put("target_id",targetId); ParaMap conditionResult =
	 * dataSetDao.queryData(keyData); if(conditionResult.getSize()>0){ String
	 * is_must = String.valueOf(conditionResult.getRecordValue(0, 0)); isMust =
	 * "1".equals(is_must)?true:false; } } if(isMust){
	 * if(StrUtils.isNull(moduleUrl)){ String url = "";
	 * if(StrUtils.isNotNull(moduleId)){ keyData.clear(); keyData.put("id",
	 * moduleId); keyData.put("is_valid",1); ParaMap moduleMap =
	 * dataSetDao.querySimpleData("sys_module", keyData);
	 * if(moduleMap.getSize()>0){ url =
	 * String.valueOf(moduleMap.getRecordValue(0, "class_name")); } }
	 * if(StrUtils.isNotNull(url)){ if(StrUtils.isNotNull(moduleParam)){ url =
	 * url.indexOf("?")>=0? url+"&"+moduleParam : url+"?"+moduleParam; } }
	 * stepMap.put("module_url", url); } stepsList.add(stepMap); } } }
	 * out.put("state", 1); out.put("steps", stepsList); return out; }
	 */

}

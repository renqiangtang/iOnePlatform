package com.tradeplow.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.tradeplow.dao.OfferLogDao;

public class OfferLogService extends BaseService {
	public ParaMap getOfferLogListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("") && !sortField.equalsIgnoreCase("null")
				&& sortDir != null && !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//检查调用DAO中的哪个方法
		int callDaoMethod;
		String licenseId = inMap.getString("license_id");
		if (licenseId == null || licenseId.equals("") || licenseId.equalsIgnoreCase("null")) {
			String targetId = inMap.getString("target_id");
			if (targetId == null || targetId.equals("") || targetId.equalsIgnoreCase("null")) {
				String bidderId = inMap.getString("bidder_id");
				if (bidderId == null || bidderId.equals("") || bidderId.equalsIgnoreCase("null")) {
					String userId = inMap.getString("u");
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap userKeyData = new ParaMap();
					userKeyData.put("id", userId);
					userKeyData.put("user_type", 1);
					ParaMap userData = dataSetDao.querySimpleData("sys_user", userKeyData);
					int state = userData.getInt("state");
					if (state == 0)
						return userData;
					else {
						if (userData.getInt("totalRowCount") == 0) {
							userData.put("state", 0);
							userData.put("message", "传入的用户非竞买人，无法查询竞买申请列表");
							return userData;
						} else {
							bidderId = String.valueOf(userData.getRecordValue(0, "ref_id"));
						}
					}
				}
				sqlParams.put("bidder_id", bidderId);
				callDaoMethod = 0;
			} else {
				callDaoMethod = 2;
			}
		} else {
			callDaoMethod = 1;
		}
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("target")) {
			String target = inMap.getString("target");
			if (target != null && !target.equals("") && !target.equalsIgnoreCase("null")) {
				sqlParams.put("target", target);
				customCondition.append(" and target_name like '%' || :target || '%'");
			}
		}
		if (inMap.containsKey("licenseNo")) {
			String licenseNo = inMap.getString("licenseNo");
			if (licenseNo != null && !licenseNo.equals("") && !licenseNo.equalsIgnoreCase("null")) {
				sqlParams.put("license_no", licenseNo);
				customCondition.append(" and license_no = :license_no");
			}
		}
		if (inMap.containsKey("offerDate1")) {
			String offerDate1 = inMap.getString("offerDate1");
			if (offerDate1 != null && !offerDate1.equals("") && !offerDate1.equalsIgnoreCase("null")) {
				sqlParams.put("offer_date1", offerDate1);
				sqlParams.put("date_time_format", "yyyy-mm-dd hh24:mi:ss");
				customCondition.append(" and to_char(offer_date, :date_time_format) >= :offer_date1");
			}
		}
		if (inMap.containsKey("offerDate2")) {
			String offerDate2 = inMap.getString("offerDate2");
			if (offerDate2 != null && !offerDate2.equals("") && !offerDate2.equalsIgnoreCase("null")) {
				sqlParams.put("offer_date2", offerDate2);
				sqlParams.put("date_time_format", "yyyy-mm-dd hh24:mi:ss");
				customCondition.append(" and to_char(offer_date, :date_time_format) <= :offer_date2");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		OfferLogDao offerLogDao = new OfferLogDao();
		ParaMap out;
		if (callDaoMethod == 0) {
			out = offerLogDao.getBidderOfferLogListData(moduleId, dataSetNo, sqlParams);
		} else if (callDaoMethod == 1) {
			out = offerLogDao.getLicenseOfferLogListData(moduleId, dataSetNo, sqlParams);
		} else {
			out = offerLogDao.getTargetOfferLogListData(moduleId, dataSetNo, sqlParams);
		}
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("bidder_id", "bidder_id");
		fields.put("license_no", "license_no");
		fields.put("target_name", "target_name");
		fields.put("price", "price");
		fields.put("multi_trade_name", "multi_trade_name");
		fields.put("price_unit", "price_unit");
		fields.put("offer_date", "offer_date");
		fields.put("status", "status");
		fields.put("rn", "rn");
		fields.put("is_trust", "is_trust");
		fields.put("kind", "kind");
		fields.put("target_status", "target_status");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getOfferLogData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		OfferLogDao offerLogDao = new OfferLogDao();
		ParaMap out = offerLogDao.getOfferLogData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getTargetMultiTradeCount(ParaMap inMap) throws Exception {
		ParaMap keyData = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		String targetId = null;
		ParaMap out = null;
		if (inMap.containsKey("targetId"))
			targetId = inMap.getString("targetId");
		else if (inMap.containsKey("licenseId")) {
			keyData.put("id", inMap.getString("licenseId"));
			out = dataSetDao.querySimpleData("trans_license", keyData);
			if (out.getInt("state") == 1 && out.getInt("totalRowCount") > 0) {
				targetId = String.valueOf(out.getRecordValue(0, "target_id"));
			}
		} else if (inMap.containsKey("id")) {
			keyData.put("id", inMap.getString("id"));
			out = dataSetDao.querySimpleData("trans_offer_log", keyData);
			if (out.getInt("state") == 1 && out.getInt("totalRowCount") > 0) {
				keyData.put("id", String.valueOf(out.getRecordValue(0, "license_id")));
				out = dataSetDao.querySimpleData("trans_license", keyData);
				if (out.getInt("state") == 1 && out.getInt("totalRowCount") > 0) {
					targetId = String.valueOf(out.getRecordValue(0, "target_id"));
				}
			}
		}
		if (StrUtils.isNotNull(targetId)) {
			keyData.clear();
			keyData.put("target_id", targetId);
			out = dataSetDao.querySimpleRowCount("trans_target_multi_trade", keyData);
		}
		return out;
	}
}

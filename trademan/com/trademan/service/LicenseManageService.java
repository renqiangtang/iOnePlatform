package com.trademan.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.ContentTypes;
import com.sysman.dao.UserDao;
import com.trademan.dao.LicenseManageDao;

public class LicenseManageService extends BaseService {
	public ParaMap getBidderLicenseListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			//前端表格字段名和数据集字段名不一致
			if (sortField.equalsIgnoreCase("targetName"))
				sortField = "target_name";
			else if (sortField.equalsIgnoreCase("businessType"))
				sortField = "business_type";
			else if (sortField.equalsIgnoreCase("transType"))
				sortField = "trans_type";
			else if (sortField.equalsIgnoreCase("address"))
				sortField = "address";
			else if (sortField.equalsIgnoreCase("earnestMoney"))
				sortField = "earnest_money";
			else if (sortField.equalsIgnoreCase("applyDate"))
				sortField = "apply_date";
			else if (sortField.equalsIgnoreCase("status"))
				sortField = "status";
			else if (sortField.equalsIgnoreCase("maxPrice"))
				sortField = "max_price";
			else if (sortField.equalsIgnoreCase("targetStatus"))
				sortField = "target_status";
			else
				sortField = "";
			if (StrUtils.isNotNull(sortField) && StrUtils.isNotNull(sortDir))
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;//new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		String bidderId = inMap.getString("bidder_id");
		if (StrUtils.isNull(bidderId)) {
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
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("target")) {
			String target = inMap.getString("target");
			if (StrUtils.isNotNull(target)) {
				sqlParams.put("no", target);
				sqlParams.put("name", target);
				customCondition.append(" and (t.no like '%' || :no || '%' or t.name like '%' || :name || '%')");
			}
		}
		if (inMap.containsKey("targetStatus")) {
			String targetStatus = inMap.getString("targetStatus");
			if (StrUtils.isNotNull(targetStatus)) {
				List<String> targetStatusList = StrUtils.getSubStrs(targetStatus, ";", true);
				if (targetStatusList != null || targetStatusList.size() > 0) {
					customCondition.append(" and t.status in (");
					for(int i = 0; i < targetStatusList.size(); i++) {
						sqlParams.put("trans_status" + i, targetStatusList.get(i));
						customCondition.append(":trans_status" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				List<String> businessTypeList = StrUtils.getSubStrs(businessType, ";", true);
				if (businessTypeList != null || businessTypeList.size() > 0) {
					customCondition.append(" and t.business_type in (");
					for(int i = 0; i < businessTypeList.size(); i++) {
						sqlParams.put("business_type" + i, businessTypeList.get(i));
						customCondition.append(":business_type" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("transType")) {
			String transType = inMap.getString("transType");
			if (StrUtils.isNotNull(transType)) {
				List<String> transTypeList = StrUtils.getSubStrs(transType, ";", true);
				if (transTypeList != null || transTypeList.size() > 0) {
					customCondition.append(" and t.trans_type_label in (");
					for(int i = 0; i < transTypeList.size(); i++) {
						sqlParams.put("trans_type" + i, transTypeList.get(i));
						customCondition.append(":trans_type" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("isUnion")) {
			String isUnion = inMap.getString("isUnion");
			if (isUnion.equals("1") || isUnion.equals("2")) {
				if (isUnion.equals("1"))
					customCondition.append(" and not exists(select * from trans_license_union where license_id = l.id)");
				else
					customCondition.append(" and exists(select * from trans_license_union where license_id = l.id)");
			}
		}
		if (inMap.containsKey("beginListTime")) {
			String beginListTime = inMap.getString("beginListTime");
			if (beginListTime != null && !beginListTime.equals("") && !beginListTime.equalsIgnoreCase("null")) {
				sqlParams.put("begin_list_time", beginListTime);
				customCondition.append(" and to_char(t.begin_list_time, 'yyyy-mm-dd') >= :begin_list_time");
			}
		}
		if (inMap.containsKey("endListTime")) {
			String endListTime = inMap.getString("endListTime");
			if (endListTime != null && !endListTime.equals("") && !endListTime.equalsIgnoreCase("null")) {
				sqlParams.put("end_list_time", endListTime);
				customCondition.append(" and to_char(t.end_list_time, 'yyyy-mm-dd') <= :end_list_time");
			}
		}
		if (inMap.containsKey("beginLimitTime")) {
			String beginLimitTime = inMap.getString("beginLimitTime");
			if (beginLimitTime != null && !beginLimitTime.equals("") && !beginLimitTime.equalsIgnoreCase("null")) {
				sqlParams.put("begin_limit_time", beginLimitTime);
				customCondition.append(" and to_char(t.begin_limit_time, 'yyyy-mm-dd') = :begin_limit_time");
			}
		}
		if (inMap.containsKey("status")) {
			String status = inMap.getString("status");
			if (status != null && !status.equals("") && !status.equalsIgnoreCase("null")) {
				customCondition.append(" and ( 1 = 2");
				status = ";" + status + ";";
				StringBuffer transTypes = new StringBuffer();
				if (status.indexOf(";0;") >= 0)//申请中
					transTypes.append("0,");
				if (status.indexOf(";1;") >= 0)//已通过
					transTypes.append("1,");
				if (status.indexOf(";2;") >= 0)//未成交
					transTypes.append("2,");
				if (status.indexOf(";3;") >= 0)//已成交
					transTypes.append("3,4,");
				if (status.indexOf(";4;") >= 0)//撤销成交
					transTypes.append("5,");
				if (transTypes.length() > 0) {
					transTypes.deleteCharAt(transTypes.length() - 1);
					customCondition.append(" or l.status in (" + transTypes.toString() + ") ");
				}
				if (status.indexOf(";5;") == -1 || status.indexOf(";6;") == -1 || status.indexOf(";7;") == -1) {
					if (status.indexOf(";5;") >= 0)//未交保证金
						customCondition.append(" or l.earnest_money_pay = 0");
					if (status.indexOf(";6;") >= 0) {//未交足保证金
						customCondition.append(" or (l.earnest_money_pay = 0 and exists(select * from trans_account_bill_apply where apply_type = 0 and ref_id = l.id " +
								" and exists(select * from trans_account_bill where bill_type = 1 and apply_id = trans_account_bill_apply.id)))");
					}
					if (status.indexOf(";7;") >= 0)//已交足保证金
						customCondition.append(" or l.earnest_money_pay = 1");
				}
				if (status.indexOf(";8;") == -1 || status.indexOf(";9;") == -1 || status.indexOf(";10;") == -1 || status.indexOf(";11;") == -1) {
					if (status.indexOf(";8;") >= 0) {//不需要审批
						customCondition.append(" or t.trans_condition is null");
					} else if (status.indexOf(";9;") >= 0 && status.indexOf(";10;") >= 0 && status.indexOf(";11;") >= 0) {//需要审批
						customCondition.append(" or t.trans_condition is not null");
					} else if (status.indexOf(";9;") >= 0 || status.indexOf(";10;") >= 0 || status.indexOf(";11;") >= 0) {
						customCondition.append(" or (t.trans_condition is not null and (1 = 2");
						if (status.indexOf(";9;") >= 0)//暂未审批
							customCondition.append(" or l.confirmed = 0");
						if (status.indexOf(";10;") >= 0)//未审批通过
							customCondition.append(" or l.confirmed = 2");
						if (status.indexOf(";11;") >= 0)//审批通过
							customCondition.append(" or l.confirmed = 1");
						customCondition.append("))");
					}
				}
				if (status.indexOf(";12;") == -1 || status.indexOf(";13;") == -1 || status.indexOf(";14;") == -1 || status.indexOf(";15;") == -1) {
					if (status.indexOf(";12;") >= 0)//联合竞买无确认
						customCondition.append(" or not exists(select * from trans_license_union where license_id = l.id and status > 0) ");
					if (status.indexOf(";13;") >= 0)//联合竞买未全部确认
						customCondition.append(" or (exists(select * from trans_license_union where license_id = l.id and status = 0) " +
								" and exists(select * from trans_license_union where license_id = l.id and status = 1))");
					if (status.indexOf(";14;") >= 0)//联合竞买全部确认
						customCondition.append(" or not exists(select * from trans_license_union where license_id = l.id and status <> 1) ");
					if (status.indexOf(";15;") >= 0)//联合竞买拒绝
						customCondition.append(" or not exists(select * from trans_license_union where license_id = l.id and status = 2) ");
				}
				customCondition.append(")");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getBidderLicenseListData(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("targetId", "target_id");
		fields.put("licenseNo", "license_no");
		fields.put("targetName", "target_name");
		fields.put("businessType", "business_type");
		fields.put("transType", "trans_type");
		fields.put("transTypeName", "trans_type_name");
		fields.put("transTypeLabel", "trans_type_label");
		fields.put("address", "address");
		fields.put("earnestMoney", "earnest_money");
		fields.put("applyDate", "apply_date");
		fields.put("status", "status");
		fields.put("licenseCount", "license_count");
		fields.put("isupload", "isupload");//竞买申请时间截止后不允许上传，2可以上传，1不能上传
		fields.put("maxPrice", "max_price");
		fields.put("targetStatus", "target_status");
		fields.put("isNetTrans", "is_net_trans");
		fields.put("address", "address");
		fields.put("goods_size", "goods_size");
		fields.put("hasTransCondition", "has_trans_condition");
		fields.put("confirmed", "confirmed");
		fields.put("confirmedDate", "confirmed_date");
		fields.put("confirmedOpinion", "confirmed_opinion");
		fields.put("payEarnest", "earnest_money_pay");
		fields.put("isLimitTrans", "is_limit_trans");
		fields.put("endFocusTime", "end_focus_time");
		fields.put("beginLimitTime", "begin_limit_time");
		fields.put("isUnion", "is_union");
		fields.put("unionStatus", "union_status");
		fields.put("unionCount", "union_count");
		fields.put("unionConfirmCount", "union_confirm_count");
		fields.put("unionRejectCount", "union_reject_count");
		fields.put("enterFlag", "enter_flag");
		fields.put("unit", "unit");
		fields.put("tnno", "tn_no");
		fields.put("tpgs_name", "tpgs_name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap checkTransTargetEarnestMoneyPay(ParaMap inMap) throws Exception{
		ParaMap out =new ParaMap();
		DataSetDao dataSetDao=new DataSetDao();
		ParaMap keyData=new ParaMap();
		if(!inMap.containsKey("licenseId")){
			out.clear();
			out.put("state", 0);
			out.put("message", "查询订单出错,没有需要查询的订单");
			return out;
		}else{
			keyData.put("id", inMap.getString("licenseId"));
			out=dataSetDao.querySimpleData("trans_license", keyData);
			if(out.getSize()>0){
				int earnestMoneyPay=out.getRecordInt(0, "earnest_money_pay");
				out.clear();
				out.put("emp", earnestMoneyPay);
				out.put("state", 1);
			}else{
				out.clear();
				out.put("state", 0);
				out.put("message", "不存在的订单");
				return out;
			}
		}
		return out;
	}
	
	public ParaMap getConfirmLicenseListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			//前端表格字段名和数据集字段名不一致
			if (sortField.equalsIgnoreCase("targetName"))
				sortField = "target_name";
			else if (sortField.equalsIgnoreCase("businessType"))
				sortField = "business_type";
			else if (sortField.equalsIgnoreCase("transType"))
				sortField = "trans_type";
			else if (sortField.equalsIgnoreCase("address"))
				sortField = "address";
			else if (sortField.equalsIgnoreCase("earnestMoney"))
				sortField = "earnest_money";
			else if (sortField.equalsIgnoreCase("applyDate"))
				sortField = "apply_date";
			else if (sortField.equalsIgnoreCase("status"))
				sortField = "status";
			else if (sortField.equalsIgnoreCase("maxPrice"))
				sortField = "max_price";
			else if (sortField.equalsIgnoreCase("targetStatus"))
				sortField = "target_status";
			else
				sortField = "";
			if (StrUtils.isNotNull(sortField))
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		String isOrgan = inMap.getString("isOrgan");
		if(StrUtils.isNotNull(isOrgan) && isOrgan.equals("1")){
			if (inMap.containsKey("u")) {
				String userId = inMap.getString("u");
				UserDao userDao = new UserDao();
				ParaMap user = userDao.getUserOrganInfo(null, null, userId);
				String organId = null;
				if(user!=null && user.get("id")!=null){
					organId = user.getString("id");
				}
				if (StrUtils.isNotNull(organId)) {
					sqlParams.put("organId", organId);
					customCondition.append(" and t.trans_organ_id = :organId ");
				}
			}
		}
		if (inMap.containsKey("goodsType")) {
			String goodsType = inMap.getString("goodsType");
			if (StrUtils.isNotNull(goodsType)) {
				customCondition.append(" and substr(t.business_type,1,3) in ("+goodsType+ ") ");
			}
		}
		
		if (inMap.containsKey("bidderName")) {
			String bidderName = inMap.getString("bidderName");
			if (StrUtils.isNotNull(bidderName)) {
				sqlParams.put("bidder_name", bidderName);
				customCondition.append(" and (b.name like '%' || :bidder_name || '%' " +
						" or exists(select * from trans_license_union where trans_license_union.license_id = l.id" +
						"   and exists(select * from trans_bidder where id = trans_license_union.bidder_id and name like '%' || :bidder_name || '%')))");
			}
		}
		if (inMap.containsKey("target")) {
			String target = inMap.getString("target");
			if (StrUtils.isNotNull(target)) {
				sqlParams.put("no", target);
				sqlParams.put("name", target);
				customCondition.append(" and (t.no like '%' || :no || '%' or t.name like '%' || :name || '%')");
			}
		}
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				//sqlParams.put("business_type", businessType);
				//customCondition.append(" and instr(';' || :business_type || ';', ';' || business_type || ';') > 0");
				customCondition.append(" and t.business_type in (" + businessType.replaceAll(";", ",") + ")");
			}
		}
		if (inMap.containsKey("transType")) {
			String transType = inMap.getString("transType");
			if (StrUtils.isNotNull(transType)) {
				List<String> transTypeList = StrUtils.getSubStrs(transType, ";", true);
				if (transTypeList != null || transTypeList.size() > 0) {
					customCondition.append(" and t.trans_type_label in (");
					for(int i = 0; i < transTypeList.size(); i++) {
						sqlParams.put("trans_type" + i, transTypeList.get(i));
						customCondition.append(":trans_type" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("isUnion")) {
			String isUnion = inMap.getString("isUnion");
			if (isUnion.equals("1") || isUnion.equals("2")) {
				if (isUnion.equals("1"))
					customCondition.append(" and not exists(select * from trans_license_union where license_id = l.id)");
				else
					customCondition.append(" and exists(select * from trans_license_union where license_id = l.id)");
			}
		}
		if (inMap.containsKey("beginListTime")) {
			String beginListTime = inMap.getString("beginListTime");
			if (StrUtils.isNotNull(beginListTime)) {
				sqlParams.put("begin_list_time", beginListTime);
				customCondition.append(" and to_char(t.begin_list_time, 'yyyy-mm-dd') >= :begin_list_time");
			}
		}
		if (inMap.containsKey("endListTime")) {
			String endListTime = inMap.getString("endListTime");
			if (StrUtils.isNotNull(endListTime)) {
				sqlParams.put("end_list_time", endListTime);
				customCondition.append(" and to_char(t.end_list_time, 'yyyy-mm-dd') <= :end_list_time");
			}
		}
		if (inMap.containsKey("beginLimitTime")) {
			String beginLimitTime = inMap.getString("beginLimitTime");
			if (StrUtils.isNotNull(beginLimitTime)) {
				sqlParams.put("begin_limit_time", beginLimitTime);
				customCondition.append(" and to_char(t.begin_limit_time, 'yyyy-mm-dd') = :begin_limit_time");
			}
		}
		if (inMap.containsKey("status")) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				status = ";" + status + ";";
				StringBuffer transTypes = new StringBuffer();
				if (status.indexOf(";1;") >= 0)//未交易
					transTypes.append("3,");
				if (status.indexOf(";2;") >= 0)//交易中
					transTypes.append("4,");
				if (status.indexOf(";3;") >= 0)//交易结束
					transTypes.append("5,6,");
				if (transTypes.length() > 0) {
					transTypes.deleteCharAt(transTypes.length() - 1);
					customCondition.append(" and t.status in (" + transTypes.toString() + ") ");
				}
			}
		}
		if (inMap.containsKey("confirmOrgan")) {
			String confirmOrgan = inMap.getString("confirmOrgan");
			if (StrUtils.isNotNull(confirmOrgan)) {
				sqlParams.put("confirm_organ", Integer.parseInt(confirmOrgan) - 1);
				customCondition.append(" and t.condition_organ_name like '%' || :confirm_organ || '%' ");
			}
		}
		if (inMap.containsKey("confirmStatus")) {
			String confirmStatus = inMap.getString("confirmStatus");
			if (confirmStatus.equals("1") || confirmStatus.equals("2") || confirmStatus.equals("3")) {
				sqlParams.put("confirmed", Integer.parseInt(confirmStatus) - 1);
				customCondition.append(" and confirmed = :confirmed ");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getConfirmLicenseListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("targetId", "target_id");
		fields.put("bidderName", "bidder_name");
		fields.put("licenseNo", "license_no");
		fields.put("targetName", "target_name");
		fields.put("businessType", "business_type");
		fields.put("transType", "trans_type");
		fields.put("transTypeName", "trans_type_name");
		fields.put("transTypeLabel", "trans_type_label");
		fields.put("address", "address");
		fields.put("earnestMoney", "earnest_money");
		fields.put("applyDate", "apply_date");
		fields.put("status", "status");
		fields.put("licenseCount", "license_count");
		fields.put("maxPrice", "max_price");
		fields.put("targetStatus", "target_status");
		fields.put("isNetTrans", "is_net_trans");
		fields.put("address", "address");
		fields.put("goods_size", "goods_size");
		fields.put("unionCount", "union_count");
		fields.put("confirmed", "confirmed");
		fields.put("confirmedDate", "confirmed_date");
		fields.put("confirmedOpinion", "confirmed_opinion");
		fields.put("payEarnest", "earnest_money_pay");
		fields.put("isLimitTrans", "is_limit_trans");
		fields.put("endFocusTime", "end_focus_time");
		fields.put("beginLimitTime", "begin_limit_time");
		fields.put("enterFlag", "enter_flag");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap checkLicenseBidder(ParaMap inMap) throws Exception {
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = inMap.containsKey("bidder_id") ?
				licenseDao.checkLicenseBidder(inMap.getString("id"), inMap.getString("bidder_id"))
				: licenseDao.checkLicenseBidderByUserId(inMap.getString("id"), inMap.getString("u"));
		return out;
	}
	
	public ParaMap confirmUnionLicense(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		LicenseManageDao licenseDao = new LicenseManageDao();
		String userId = inMap.getString("u");
		ParaMap user = licenseDao.getSysUser(userId);
//		if(user!=null && user.getSize()>0){
//			String listType = user.getRecordString(0, "list_type");
//			if(listType.startsWith("2")){
//				out.put("state", 0);
//				String message = "你因违反国土局相关规定已被系统列入黑名单，禁止参与系统竞买，请与国土局相关单位联系！";
//				out.put("message", message);
//				return out;
//			}
//		}
		out = licenseDao.confirmUnionLicense(inMap.getString("moduleId"), inMap.getString("dataSetNo"), inMap);
		return out;
	}
	
	public ParaMap checkLicenseStatusPass(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.checkLicenseStatusPass(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap setLicenseStatusPass(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.setLicenseStatusPass(inMap.getString("moduleId"), inMap.getString("dataSetNo"), id);
		return out;
	}
	
	public ParaMap getLicenseCertData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getLicenseCertData(moduleId, dataSetNo, id);
		return out;
	}
	
	public byte[] downloadLicenseCert(ParaMap inMap) throws Exception {
		String licenseId = inMap.getString("licenseId");
		String type = ContentTypes.getContentType("lic");
		this.getResponse().reset();
		this.getResponse().setContentType(type + "; charset=UTF-8");
		ParaMap licenseData = getLicenseCertData(inMap);
		
		StringBuffer bidderNames = new StringBuffer(licenseData.getString("bidderName"));
    	int unionBidderCount = licenseData.getInt("unionBidderCount");
    	if (unionBidderCount > 0) {
    		for(int i = 0; i < unionBidderCount; i++) {
    			bidderNames.append(licenseData.getString("unionBidderName" + i) + ",");
    		}
    		bidderNames.deleteCharAt(bidderNames.length() - 1);
    	}	
		String bidderName = new String(bidderNames.toString().getBytes("ISO-8859-1"),"UTF-8"); 
		String targetName = new String(licenseData.getString("targetName").getBytes("ISO-8859-1"),"UTF-8"); 
		String applyDate = new String(licenseData.getString("applyDate").getBytes("ISO-8859-1"),"UTF-8");
		String downloadFileName = bidderName +"[" + targetName + "]资格确认书.lic";
		downloadFileName = new String(downloadFileName.getBytes("gb2312"), "ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downloadFileName + "\";");
		//String content = dao.downLoadEnOfferLog(licenseId);
		//return content.getBytes();
		return null;
	}
	
	public ParaMap getLicenseTransCertData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getLicenseTransCertData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap confirmLicense(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.confirmLicense(moduleId, dataSetNo, inMap);
		return out;
	}
	
	public ParaMap getConfirmLicenseData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getConfirmLicenseData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap deleteLicenseData(ParaMap inMap) throws Exception {
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.deleteLicenseData(null, null, inMap.getString("id"));
		return out;
	}
	
	
	
	public ParaMap getConfirmLicenseListDataAfter(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		String goodsType = inMap.getString("goodsType");
		String userId = inMap.getString("u");
		UserDao userDao = new UserDao();
		ParaMap user = userDao.getUserOrganInfo(null, null, userId);
		String organId = null;
		if(user!=null && user.get("id")!=null){
			organId = user.getString("id");
		}
		if (StrUtils.isNotNull(sortField)){
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "end_trans_time desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			sqlParams.put("organId", organId);
			customCondition.append(" and tt.trans_organ_id = :organId ");
		}
		if (StrUtils.isNotNull(goodsType)) {
			if(goodsType=="101"||goodsType.equals("101")){
				customCondition.append(" and substr(tt.business_type,1,3)= :goodsType ");
			}else{
				customCondition.append(" and substr(tt.business_type,1,3) in ('301','401') ");
			}
		}
		if (inMap.containsKey("bidderName")) {
			String bidderName = inMap.getString("bidderName");
			if (StrUtils.isNotNull(bidderName)) {
				customCondition.append(" and (get_union_bidder(tl.id) like '%' || :bidderName || '%' ) ");
			}
		}
		if (inMap.containsKey("targetNo")) {
			String targetNo = inMap.getString("targetNo");
			if (StrUtils.isNotNull(targetNo)) {
				customCondition.append(" and (tt.no like '%' || :targetNo || '%' or tt.name like '%' || :targetNo || '%')");
			}
		}
		if (inMap.containsKey("address")) {
			String address = inMap.getString("address");
			if (StrUtils.isNotNull(address)) {
				customCondition.append(" and (tg.address like '%' || :address || '%')");
			}
		}
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				customCondition.append(" and tt.business_type in (" + businessType.replaceAll(";", ",") + ")");
			}
		}
		if (inMap.containsKey("transType")) {
			String transType = inMap.getString("transType");
			if (StrUtils.isNotNull(transType)) {
				List<String> transTypeList = StrUtils.getSubStrs(transType, ";", true);
				if (transTypeList != null || transTypeList.size() > 0) {
					customCondition.append(" and tt.trans_type_id in (");
					for(int i = 0; i < transTypeList.size(); i++) {
						sqlParams.put("trans_type" + i, transTypeList.get(i));
						customCondition.append(":trans_type" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("isUnion")) {
			String isUnion = inMap.getString("isUnion");
			if (isUnion.equals("1") || isUnion.equals("2")) {
				if (isUnion.equals("1"))
					customCondition.append(" and not exists(select * from trans_license_union where license_id = tl.id)");
				else
					customCondition.append(" and exists(select * from trans_license_union where license_id = tl.id)");
			}
		}
		if (inMap.containsKey("beginTime")) {
			String beginTime = inMap.getString("beginTime");
			if (StrUtils.isNotNull(beginTime)) {
				customCondition.append(" and to_char(tt.end_trans_time, 'yyyy-mm-dd') >= :beginTime");
			}
		}
		if (inMap.containsKey("endTime")) {
			String endTime = inMap.getString("endTime");
			if (StrUtils.isNotNull(endTime)) {
				customCondition.append(" and to_char(tt.end_trans_time, 'yyyy-mm-dd') <= :endTime");
			}
		}
		if (inMap.containsKey("confirmStatus")) {
			String confirmStatus = inMap.getString("confirmStatus");
			if (confirmStatus.equals("7") || confirmStatus.equals("5") || confirmStatus.equals("6")) {
				customCondition.append(" and tt.status = :confirmStatus ");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		//查询数据
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.getConfirmLicenseListDataAfter(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = out.getListObj();
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * 审核
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap confirmLicenseAfter(ParaMap inMap) throws Exception {
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.confirmLicenseAfter(inMap);
		return out;
	}
	
	/**
	 * 复核
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap confirmLicenseAfterFh(ParaMap inMap) throws Exception {
		LicenseManageDao licenseDao = new LicenseManageDao();
		ParaMap out = licenseDao.confirmLicenseAfterFh(inMap);
		return out;
	}
	
}

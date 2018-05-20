package com.trademan.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.ExtendDao;
import com.trademan.dao.BidderDao;

public class BidderService extends BaseService {
	public ParaMap getBidderListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField) && StrUtils.isNotNull(sortDir))
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
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("name")) {
			String bidderName = inMap.getString("name");
			if (bidderName != null && !bidderName.equals("") && !bidderName.equalsIgnoreCase("null")) {
				sqlParams.put("name", bidderName);
				customCondition.append(" and name like '%' || :name || '%'");
			}
		}
		if (inMap.containsKey("bidderType")) {
			String strBidderType = inMap.getString("bidderType");
			List<String> bidderTypes = StrUtils.getSubStrs(strBidderType, ",");
			if (bidderTypes != null && bidderTypes.size() > 0) {
				customCondition.append(" and bidder_type in (");
				for(int i = 0; i < bidderTypes.size(); i++) {
					sqlParams.put("bidder_type" + i, bidderTypes.get(i));
					customCondition.append(":bidder_type" + i + ",");
				}
				customCondition.deleteCharAt(customCondition.length() - 1);
				customCondition.append(") ");
			}
		}
		if (inMap.containsKey("isCompany")) {
			String isCompany = inMap.getString("isCompany");
			if (isCompany.equals("1") || isCompany.equals("0")) {
				sqlParams.put("is_company", Integer.parseInt(isCompany));
				customCondition.append(" and is_company = :is_company");
			}
		}
		if (inMap.containsKey("cantonName")) {
			String cantonName = inMap.getString("cantonName");
			if (StrUtils.isNotNull(cantonName)) {
				sqlParams.put("canton_name", cantonName);
				customCondition.append(" and canton_id in (select id from sys_canton CONNECT BY parent_id = PRIOR id start with name like '%' || :canton_name || '%')");
			}
		}
		if (inMap.containsKey("isOversea")) {
			String isOversea = inMap.getString("isOversea");
			if (StrUtils.isNotNull(isOversea) && (isOversea.equals("1") || isOversea.equals("0"))) {
				sqlParams.put("is_oversea", Integer.parseInt(isOversea));
				customCondition.append(" and is_oversea = :is_oversea");
			}
		}
		if (inMap.containsKey("address")) {
			String address = inMap.getString("address");
			if (address != null && !address.equals("") && !address.equalsIgnoreCase("null")) {
				sqlParams.put("address", address);
				customCondition.append(" and address like '%' || :address || '%'");
			}
		}
		if (inMap.containsKey("hasCakey")) {
			String hasCakey = inMap.getString("hasCakey");
			if (hasCakey.equals("1"))
				customCondition.append(" and exists(select * from sys_user where user_type = 1 and ref_id = trans_bidder.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key is not null))");
			else if (hasCakey.equals("0"))
				customCondition.append(" and (not exists(select * from sys_user where user_type = 1 and ref_id = trans_bidder.id) " +
						"or exists(select * from sys_user where user_type = 1 and ref_id = trans_bidder.id" +
						"  and (not exists(select * from sys_cakey where user_id = sys_user.id)" +
						"    or exists(select * from sys_cakey where user_id = sys_user.id and key is null))))");
		}
		if (inMap.containsKey("cakey")) {
			String cakey = inMap.getString("cakey");
			if (cakey != null && !cakey.equals("") && !cakey.equalsIgnoreCase("null")) {
				sqlParams.put("cakey", cakey);
				customCondition.append(" and exists(select * from sys_user where user_type = 1 and ref_id = trans_bidder.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		}
		if (inMap.containsKey("status")) {
			String statuses = inMap.getString("status");
			if (StrUtils.isNotNull(statuses)) {
				boolean hasOverdueStatusCondition = false;
				int validStatusCount = 0;
				List<String> statusList = statuses.indexOf(",") >= 0 ? StrUtils.getSubStrs(statuses, ",") : StrUtils.getSubStrs(statuses, ";");
				StringBuffer subCondition = new StringBuffer("");
				subCondition.append(" and (status in (");
				for(int i = 0; i < statusList.size(); i++) {
					String status = statusList.get(i);
					if (StrUtils.isNotNull(status)) {
						if (status.equals("100")) {//已过期
							hasOverdueStatusCondition = true;
						} else {
							sqlParams.put("status" + i, status);
							subCondition.append(":status" + i + ",");
							validStatusCount++;
						}
					}
				}
				if (validStatusCount > 0 || hasOverdueStatusCondition) {
					if (validStatusCount > 0) {
						subCondition.deleteCharAt(subCondition.length() - 1);
						subCondition.append(")");
						if (hasOverdueStatusCondition)
							subCondition.append(" or valid_date is not null and trunc(valid_date) < trunc(sysdate)");
						subCondition.append(")");
					} else if (hasOverdueStatusCondition) {
						subCondition = new StringBuffer("");
						subCondition.append(" and valid_date is not null and trunc(valid_date) < trunc(sysdate)");
					}
					customCondition.append(subCondition.toString());
				}
			}
		}
		//静态查询条件
		if (inMap.containsKey("staticExcludeId")) {
			String excludeId = inMap.getString("staticExcludeId");
			if (StrUtils.isNotNull(excludeId)) {
				List<String> excludeIds = StrUtils.getSubStrs(excludeId, ",");
				if (excludeIds.size() > 0) {
					customCondition.append(" and id not in (");
					for(int i = 0; i < excludeIds.size(); i++) {
						sqlParams.put("exclude_id" + i, excludeIds.get(i));
						customCondition.append(":exclude_id" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("staticStatus")) {
			String staticStatus = inMap.getString("staticStatus");
			if (StrUtils.isNotNull(staticStatus)) {
				List<String> staticStatusList = staticStatus.indexOf(",") >= 0 ? StrUtils.getSubStrs(staticStatus, ",") : StrUtils.getSubStrs(staticStatus, ";");
				customCondition.append(" and status in (");
				for(int i = 0; i < staticStatusList.size(); i++) {
					sqlParams.put("static_status" + i, staticStatusList.get(i));
					customCondition.append(":static_status" + i + ",");
				}
				customCondition.deleteCharAt(customCondition.length() - 1);
				customCondition.append(")");
			}
		}
		if (inMap.containsKey("staticBidderType")) {
			String staticBidderType = inMap.getString("staticBidderType");
			if (StrUtils.isNotNull(staticBidderType)) {
				customCondition.append(" and bidder_type = :static_bidder_type");
				if (staticBidderType.equals("1") || staticBidderType.equalsIgnoreCase("true") || staticBidderType.equalsIgnoreCase("yes"))
					sqlParams.put("static_bidder_type", 1);
				else
					sqlParams.put("static_bidder_type", 0);
			}
		}
		if (inMap.containsKey("staticIsCompany")) {
			String staticIsCompany = inMap.getString("staticIsCompany");
			if (StrUtils.isNotNull(staticIsCompany)) {
				customCondition.append(" and is_company = :static_is_company");
				if (staticIsCompany.equals("1") || staticIsCompany.equalsIgnoreCase("true") || staticIsCompany.equalsIgnoreCase("yes"))
					sqlParams.put("static_is_company", 1);
				else
					sqlParams.put("static_is_company", 0);
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		BidderDao bidderDao = new BidderDao();
		ParaMap out = bidderDao.getBidderListData(null, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getBidderData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BidderDao bidderDao = new BidderDao();
		ParaMap out = bidderDao.getBidderData(moduleId, dataSetNo, id);
		ExtendDao etd=new ExtendDao();
		ParaMap etdMap=etd.getExtendData("trans_bidder", id);
		List liEtd=MakeJSONData.makeItems(etdMap);
		if(liEtd.size()>0){
			for(int i=0;i<liEtd.size();i++){
				ParaMap map=(ParaMap)liEtd.get(i); 
				if(map.getString("field_no").equals("txtAgentName")){
					out.put("txtAgentName", map.getString("field_value"));
				}else if(map.getString("field_no").equals("txtAgentNo")){
					out.put("txtAgentNo", map.getString("field_value"));
				}else if(map.getString("field_no").equals("txtAgentPhone")){
					out.put("txtAgentPhone", map.getString("field_value"));
				}
			}
		}
		
		return out;
	}
	
	public ParaMap updateBidderData(ParaMap bidderData) throws Exception {
		BidderDao bidderDao = new BidderDao();
		ParaMap out = new ParaMap();
		try {
			out = bidderDao.updateBidderData(bidderData);
		} catch (Exception e) {
			System.out.println(e);
			out.put("state", 0);
			out.put("message", "用户已存在!");
			// TODO: handle exception
		}
		
		return out;
	}

	public ParaMap deleteBidderData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BidderDao bidderDao = new BidderDao();
		ParaMap out = bidderDao.deleteBidderData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateBidderUser(ParaMap bidderData) throws Exception {
		BidderDao bidderDao = new BidderDao();
		ParaMap out = bidderDao.updateBidderUser(bidderData);
		return out;
	}
}

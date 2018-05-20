package com.bidder.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.bidder.dao.EarnestBillDao;

/**
 * 保证金到账查询Service
 * 
 * @author chenl 2012-05-15
 */
public class EarnestBillService extends BaseService {
	public ParaMap getEarnestBillListData(ParaMap inMap) throws Exception {
		// 组织查询条件及分页信息
		ParaMap sqlParams = inMap;
		getPageInfo(inMap);
		getQueryCondition(sqlParams);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		EarnestBillDao earnestDao = new EarnestBillDao();
		ParaMap out = earnestDao.getEarnestBillListData(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	

	private ParaMap getPageInfo(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		if (sortField != null && !sortField.equals("")
				&& !sortField.equalsIgnoreCase("null") && sortDir != null
				&& !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, " bank_business_date desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		sqlParams.put("id", sqlParams.getString("u"));
		return sqlParams;
	}
	
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("licenseNO")) {
			String licenseNO = inMap.getString("licenseNO");
			if (licenseNO != null && !licenseNO.equals("")
					&& !licenseNO.equalsIgnoreCase("null")) {
				sqlParams.put("licenseNO", licenseNO);
				customCondition
						.append(" and tl.license_no like '%' || :licenseNO || '%'");
			}
		}
		if (inMap.containsKey("targetName")) {
			String name = inMap.getString("targetName");
			if (name != null && !name.equals("")
					&& !name.equalsIgnoreCase("null")) {
				sqlParams.put("name", name);
				customCondition
						.append(" and (tt.name like '%' || :name || '%'");
				sqlParams.put("no", name);
				customCondition.append(" or tt.no like '%' || :no || '%')");
			}
		}
		if (inMap.containsKey("bidderAccountBank")) {
			String name = inMap.getString("bidderAccountBank");
			if (name != null && !name.equals("")
					&& !name.equalsIgnoreCase("null")) {
				sqlParams.put("bidderAccountBank", name);
				customCondition
						.append(" and tab.bidder_account_bank like '%' || :bidderAccountBank || '%'");
			}
		}
		if (inMap.containsKey("bidderAccountNo")) {
			String name = inMap.getString("bidderAccountNo");
			if (name != null && !name.equals("")
					&& !name.equalsIgnoreCase("null")) {
				sqlParams.put("bidderAccountNo", name);
				customCondition
						.append(" and tab.bidder_account_no like '%' || :bidderAccountNo || '%'");
			}
		}
		if (inMap.containsKey("bidderAccountName")) {
			String name = inMap.getString("bidderAccountName");
			if (name != null && !name.equals("")
					&& !name.equalsIgnoreCase("null")) {
				sqlParams.put("bidderAccountName", name);
				customCondition
						.append(" and tab.bidder_account_name like '%' || :bidderAccountName || '%'");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
	
	private List getDataInfo(ParaMap out) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("license_no", "license_no");
		fields.put("target_name", "target_name");
		fields.put("bidder_account_bank", "bidder_account_bank");
		fields.put("bidder_account_no", "bidder_account_no");
		fields.put("bidder_account_name", "bidder_account_name");
		fields.put("amount", "amount");
		fields.put("bank_business_date", "bank_business_date");
		fields.put("currency", "currency");
		fields.put("unit", "unit");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}
}

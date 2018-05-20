package com.bidder.service;

import java.util.ArrayList;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.bidder.dao.EarnestDao;

/**
 * 保证金查询Service
 * 
 * @author chenl 2012-05-15
 */
public class EarnestService extends BaseService {
	public ParaMap getEarnestListData(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		getPageInfo(inMap);
		getQueryCondition(sqlParams);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		EarnestDao earnestDao = new EarnestDao();
		ParaMap out = earnestDao.getEarnestListData(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		ParaMap amountMap = earnestDao.getEarnestAmountTotal(sqlParams
				.getString("u"));
		Object amount = ((ArrayList) ((ArrayList) amountMap.get("rs")).get(0))
				.get(0);
		if (amount == "" || amount == null)
			amount = 0;
		out.put("amountTotal", amount);
		return out;
	}
	
	private List getDataInfo(ParaMap out) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("license_no", "license_no");
		fields.put("target_name", "target_name");
		fields.put("currency", "currency");
		fields.put("earnest_money", "earnest_money");
		fields.put("amount", "amount");
		fields.put("apply_date", "apply_date");
		fields.put("unit", "unit");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}
	
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("licenseNo")) {
			String licenseNo = inMap.getString("licenseNo");
			if (licenseNo != null && !licenseNo.equals("")
					&& !licenseNo.equalsIgnoreCase("null")) {
				sqlParams.put("licenseNo", licenseNo);
				//customCondition.append(" and tl.license_no like '%' || :licenseNo || '%'");
				customCondition.append(" and tl.license_no = :licenseNo");
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
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置和读取分页信息
	 */
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
			sqlParams.put(DataSetDao.SQL_ORDER_BY, " apply_date desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		sqlParams.put("id", sqlParams.getString("u"));
		return sqlParams;
	}

}

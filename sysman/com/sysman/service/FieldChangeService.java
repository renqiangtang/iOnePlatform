package com.sysman.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.JsonUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.FieldChangeDao;

/**
 * 字段变更Service
 * samonhua
 * 2013-01-24
 */
public class FieldChangeService extends BaseService {
	public ParaMap updateFieldChanges(ParaMap inMap) throws Exception {
		String changes = inMap.getString("changes");
		if (StrUtils.isNotNull(changes)) {
			List changeList = JsonUtils.StrToList(changes);
			FieldChangeDao fieldChangeDao = new FieldChangeDao();
			return fieldChangeDao.updateFieldChanges(changeList);
		} else {
			FieldChangeDao fieldChangeDao = new FieldChangeDao();
			return fieldChangeDao.updateFieldChange(inMap);
		}
	}
	
	public ParaMap getFieldChangeListData(ParaMap inMap) throws Exception {
		String strPageQuery = inMap.getString("pageQuery");
		boolean pageQuery = StrUtils.isNotNull(strPageQuery) && (strPageQuery.equals("1") || strPageQuery.equalsIgnoreCase("true") || strPageQuery.equalsIgnoreCase("yes"));
		FieldChangeDao fieldChangeDao = new FieldChangeDao();
		ParaMap out = null;
		if (pageQuery) {
			String sortField = inMap.getString("sortField");
			String sortDir = inMap.getString("sortDir");
			if (StrUtils.isNotNull(sortField)) {
				if (StrUtils.isNotNull(sortDir))
					sortField = sortField + " " + sortDir;
			}
			ParaMap sqlParams = inMap;
			sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
			sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
			if (StrUtils.isNull(sortField))
				sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
			else
				sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
			//自定义查询条件
			StringBuffer customCondition = new StringBuffer("");
			if (inMap.containsKey("refTableName")) {
				String refTableName = inMap.getString("refTableName");
				if (StrUtils.isNotNull(refTableName)) {
					sqlParams.put("ref_table_name", refTableName);
					customCondition.append(" and ref_table_name = :ref_table_name");
				}
			}
			if (inMap.containsKey("refId")) {
				String refId = inMap.getString("refId");
				if (StrUtils.isNotNull(refId)) {
					sqlParams.put("ref_id", refId);
					customCondition.append(" and ref_id = :ref_id");
				}
			}
			if (inMap.containsKey("fieldName")) {
				String fieldName = inMap.getString("fieldName");
				if (StrUtils.isNotNull(fieldName)) {
					sqlParams.put("field_name", fieldName);
					customCondition.append(" and field_name = :field_name");
				}
			}
			if (inMap.containsKey("changeType")) {
				String changeType = inMap.getString("changeType");
				if (StrUtils.isNotNull(changeType)) {
					sqlParams.put("change_type", changeType);
					customCondition.append(" and change_type = :change_type");
				}
			}
			if (inMap.containsKey("changeDate1")) {
				String changeDate1 = inMap.getString("changeDate1");
				if (StrUtils.isNotNull(changeDate1)) {
					sqlParams.put("change_date_begin", changeDate1);
					customCondition.append(" and to_char(change_date, 'yyyy-mm-dd') >= :change_date_begin ");
				}
			}
			if (inMap.containsKey("changeDate2")) {
				String changeDate2 = inMap.getString("changeDate2");
				if (StrUtils.isNotNull(changeDate2)) {
					sqlParams.put("change_date_end", changeDate2);
					customCondition.append(" and to_char(change_date, 'yyyy-mm-dd') <= :change_date_end ");
				}
			}
			//加载静态条件
			if (inMap.containsKey("staticRefTableName")) {
				String staticRefTableName = inMap.getString("staticRefTableName");
				if (StrUtils.isNotNull(staticRefTableName)) {
					sqlParams.put("statuc_ref_table_name", staticRefTableName);
					customCondition.append(" and ref_table_name = :statuc_ref_table_name");
				}
			}
			if (inMap.containsKey("staticRefId")) {
				String staticRefId = inMap.getString("staticRefId");
				if (StrUtils.isNotNull(staticRefId)) {
					sqlParams.put("statuc_ref_id", staticRefId);
					customCondition.append(" and ref_id = :statuc_ref_id");
				}
			}
			if (inMap.containsKey("staticFieldName")) {
				String staticFieldName = inMap.getString("staticFieldName");
				if (StrUtils.isNotNull(staticFieldName)) {
					sqlParams.put("statuc_field_name", staticFieldName);
					customCondition.append(" and field_name = :statuc_field_name");
				}
			}
			if (inMap.containsKey("staticCreateType")) {
				String staticCreateType = inMap.getString("staticCreateType");
				if (StrUtils.isNotNull(staticCreateType)) {
					sqlParams.put("statuc_create_type", staticCreateType);
					customCondition.append(" and create_type = :statuc_create_type");
				}
			}
			ParaMap customConditions = new ParaMap();
			customConditions.put("CUSTOM_CONDITION", customCondition.toString());
			sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
			out = fieldChangeDao.getFieldChangePageListData(sqlParams);
		} else
			out = fieldChangeDao.getFieldChangeListData(inMap);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getFieldChangeData(ParaMap inMap) throws Exception {
		FieldChangeDao fieldChangeDao = new FieldChangeDao();
		return fieldChangeDao.getFieldChangeData(inMap.getString("id"));
	}
	
	public ParaMap deleteFieldChangeData(ParaMap inMap) throws Exception {
		FieldChangeDao fieldChangeDao = new FieldChangeDao();
		return fieldChangeDao.deleteFieldChangeData(inMap.getString("id"));
	}
}

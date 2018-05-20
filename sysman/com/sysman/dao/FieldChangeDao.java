package com.sysman.dao;

import java.util.List;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 字段变更Dao
 * samonhua
 * 2013-01-24
 */
public class FieldChangeDao extends BaseDao {
	/**
	 * 批量保存字段值变更日志
	 * @param changes 字段值变更记录列表，项格式必须为ParaMap格式，并且
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateFieldChanges(List changes) throws Exception {
		ParaMap result = null;
		if (changes == null || changes.size() == 0) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "无字段值变更内容");
			return result;
		}
		for(int i = 0; i < changes.size(); i++) {
			Object change = changes.get(i);
			if (change instanceof ParaMap) {
				result = updateFieldChange((ParaMap) change);
				if (result == null || result.getInt("state") != 1) {
					throw new Exception("保存字段值变更日志失败");
				}
			} else {
				result = new ParaMap();
				result.put("state", 0);
				result.put("message", "字段值变更日志项内容格式不正确");
				return result;
			}
		}
		result = new ParaMap();
		result.put("state", 1);
		return result;
	}
	
	public ParaMap updateFieldChange(ParaMap change) throws Exception {
		ParaMap result = null;
		if (change == null || change.size() == 0) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "无字段值变更内容");
			return result;
		}
		if (!(change.containsKey("ref_table_name") && change.containsKey("ref_id")
				&& change.containsKey("field_name") && change.containsKey("new_value"))) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "字段值变更日志项内容格式不正确，表名、键值、字段名、新值必须传入且不能为空");
			return result;
		}
		if (StrUtils.isNull(change.getString("ref_table_name")) || StrUtils.isNull(change.getString("ref_id"))
				|| StrUtils.isNull(change.getString("field_name")) || StrUtils.isNull(change.getString("new_value"))) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "字段值变更日志项内容不正确，表名、键值、字段名、新值必须传入且不能为空");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		//如果没有旧值则主要查询
		if (!change.containsKey("old_value")) {
			ParaMap data = new ParaMap();
			data.put("id", change.getString("ref_id"));
			data = dataSetDao.querySimpleData(change.getString("ref_table_name"), data, null, change.getString("field_name"));
			if (data.getInt("state") == 1 && data.getRecordCount() > 0)
				change.put("old_value", data.getRecordString(0, 0));
		}
		return dataSetDao.updateData("sys_field_change_log", "id", change);
	}
	
	/**
	 * 查询字段值变更日志列表数据
	 * @param keyData 过滤数据关键字段值
	 * @return
	 * @throws Exception
	 */
	public ParaMap getFieldChangeListData(ParaMap keyData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("sys_field_change_log", keyData, "ref_table_name, ref_id, field_name, create_date desc");
		return result;
	}
	
	/**
	 * 查询字段值变更日志列表数据（未分页）
	 * @param refTableName 关联表名
	 * @param refId 关联ID
	 * @param fieldName 变更字段名
	 * @param createType 变更类别
	 * @return
	 * @throws Exception
	 */
	public ParaMap getFieldChangeListData(String refTableName, String refId, String fieldName, String changeType) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("ref_table_name", refTableName);
		keyData.put("ref_id", refId);
		keyData.put("field_name", fieldName);
		if(StrUtils.isNotNull(changeType)){
			keyData.put("change_type", changeType);
		}
		return getFieldChangeListData(keyData);
	}
	
	public ParaMap getFieldChangePageListData(ParaMap sqlParams) throws Exception {
		String sql = "select * from sys_field_change_log where 1 = 1 :CUSTOM_CONDITION";
		sqlParams.put("sql", sql);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	/**
	 * 查询字段值变更日志数据（未分页）
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap getFieldChangeData(String id) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.querySimpleData("sys_field_change_log", keyData);
	}

	public ParaMap deleteFieldChangeData(String id) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.deleteSimpleData("sys_field_change_log", keyData);
	}
}

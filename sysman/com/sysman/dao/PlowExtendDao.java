package com.sysman.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 扩展信息管理Dao
 * huafc
 * 2012-06-11
 */
public class PlowExtendDao extends BaseDao {
	/**
	 * 更新扩展信息
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param defineNo 扩展信息自定义属性编号，可忽略传入null
	 * @param data 扩展信息值。可以是多条扩展记录，两种传参方式：<br/>
	 * 方式一：键为sys_extend.field_no，值为sys_extend.field_value<br/>
	 * 方式二：键为sys_extend.field_no，值为Map：键只能为field_value、extend1、extend2、extend3、remark等（即sys_extend中的其它字段名），值为对应的字段值
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateExtend(String refTableName, String refId, String defineNo, ParaMap data) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		Iterator it = data.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			Object value = data.get(key);
			ParaMap extendData = new ParaMap();
			extendData.put("ref_table_name", refTableName);
			extendData.put("ref_id", refId);
			extendData.put("field_no", key);
			if (value instanceof Map) {
				Map valueMap = (Map) value;
				if (valueMap.containsKey("field_value"))
					extendData.put("field_value", valueMap.get("field_value"));
				if (valueMap.containsKey("extend1"))
					extendData.put("extend1", valueMap.get("extend1"));
				if (valueMap.containsKey("extend2"))
					extendData.put("extend2", valueMap.get("extend2"));
				if (valueMap.containsKey("extend3"))
					extendData.put("extend3", valueMap.get("extend3"));
			} else
				extendData.put("field_value", value);
			if (StrUtils.isNotNull(defineNo)) {
				extendData.put("define_no", defineNo);
				result = dataSetDao.updateData("sys_extend", "id", "ref_table_name;ref_id;define_no;field_no", extendData);
			} else
				result = dataSetDao.updateData("sys_extend", "id", "ref_table_name;ref_id;field_no", extendData);
			if (result.getInt("state") != 1)
				throw new Exception("保存扩展数据 " + key + " 失败，操作取消。");
		}
		return result;
	}
	
	/**
	 * 更新扩展信息
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param data 扩展信息值。可以是多条扩展记录，两种传参方式：<br/>
	 * 方式一：键为sys_extend.field_no，值为sys_extend.field_value<br/>
	 * 方式二：键为sys_extend.field_no，值为Map：键只能为field_value、extend1、extend2、extend3、remark等（即sys_extend中的其它字段名），值为对应的字段值
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateExtend(String refTableName, String refId, ParaMap data) throws Exception {
		return updateExtend(refTableName, refId, null, data);
	}
	
	/**
	 * 删除扩展数据
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param defineNo 扩展信息自定义属性编号，可忽略传入null
	 * @param fieldNos 需要删除的sys_extend.field_no值列表
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteExtend(String refTableName, String refId, String defineNo, List<String> fieldNos) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		for(int i = 0; i < fieldNos.size(); i++) {
			String fieldNo = fieldNos.get(i);
			ParaMap keyData = new ParaMap();
			keyData.put("ref_table_name", refTableName);
			keyData.put("ref_id", refId);
			keyData.put("field_no", fieldNo);
			if (StrUtils.isNotNull(defineNo))
				keyData.put("define_no", defineNo);
			result = dataSetDao.deleteSimpleData("sys_extend", keyData);
			if (result.getInt("state") != 1)
				throw new Exception("删除扩展数据 " + fieldNo + " 失败，操作取消。");
		}
		return result;
	}
	
	/**
	 * 删除扩展数据
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param fieldNos 需要删除的sys_extend.field_no值列表
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteExtend(String refTableName, String refId, List<String> fieldNos) throws Exception {
		return deleteExtend(refTableName, refId, null, fieldNos);
	}
	
	/**
	 * 获取扩展信息，如有特殊需求请自行查询
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param defineNo 扩展信息自定义属性编号，可忽略传入null
	 * @param fieldNos 需要删除的sys_extend.field_no值列表
	 * @return
	 * @throws Exception
	 */
	public ParaMap getExtend(String refTableName, String refId, String defineNo, List<String> fieldNos) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = new ParaMap();
		result.put("ref_table_name", refTableName);
		result.put("ref_id", refId);
		result.put("field_no", fieldNos);
		result = dataSetDao.querySimpleData("sys_extend", result);
		ParaMap resultData = new ParaMap();
		for(int i = 0; i < result.getRecordCount(); i++) {
			ParaMap record = new ParaMap();
			record.put("field_value", result.getRecordString(i, "field_value"));
			record.put("extend1", result.getRecordString(i, "extend1"));
			record.put("extend2", result.getRecordString(i, "extend2"));
			record.put("extend3", result.getRecordString(i, "extend3"));
			record.put("remark", result.getRecordString(i, "remark"));
			resultData.put(result.getRecordString(i, "field_no"), record);
		}
		result = new ParaMap();
		result.put("state", 1);
		result.put("totalRowCount", resultData.size());
		result.put("data", resultData);
		return result;
	}
	
	/**
	 * 获取扩展信息，如有特殊需求请自行查询
	 * @param refTableName 扩展信息所有者记录所在表名，sys_extend.ref_table_name
	 * @param refId 扩展信息所有者记录ID，sys_extend.ref_id
	 * @param fieldNos 需要删除的sys_extend.field_no值列表
	 * @return
	 * @throws Exception
	 */
	public ParaMap getExtend(String refTableName, String refId, List<String> fieldNos) throws Exception {
		return getExtend(refTableName, refId, null, fieldNos);
	}
}

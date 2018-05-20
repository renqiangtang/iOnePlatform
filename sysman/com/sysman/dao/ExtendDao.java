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
public class ExtendDao extends BaseDao {
	/**
	 * 保存扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param defineNo 扩展信息定义编号，可以为null
	 * @param data 扩展信息。键为sys_extend.field_no，值如果为map则可包含其它多个字段的值，否则为sys_extend.field_value
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateExtendData(String refTableName, String refId, String defineNo, ParaMap data) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(refTableName) || StrUtils.isNull(refId)) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "必须包含扩展信息的归属数据");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		Iterator it = data.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			Object value = data.get(key);
			ParaMap extendData = new ParaMap();
			extendData.put("ref_table_name", refTableName);
			extendData.put("ref_id", refId);
			if (StrUtils.isNotNull(defineNo))
				extendData.put("define_no", defineNo);
			extendData.put("field_no", key);
			if (value instanceof Map) {
				//如果需要写入包含extend1、extend2、extend3、remark等更多的字段值请使用Map
				Map valueMap = (Map) value;
				Iterator valueIt = valueMap.keySet().iterator();
				while(valueIt.hasNext()){
					String valueKey = valueIt.next().toString();
					extendData.put(valueKey, valueMap.get(valueKey));
				}
			} else
				extendData.put("field_value", value);
			result = dataSetDao.updateData("sys_extend", "id", "ref_table_name;ref_id;define_no;field_no", extendData);
			if (result.getInt("state") != 1)
				return result;
		}
		return result;
	}
	
	/**
	 * 保存扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param data 扩展信息。键为sys_extend.field_no，值如果为map则可包含其它多个字段的值，否则为sys_extend.field_value
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateExtendData(String refTableName, String refId, ParaMap data) throws Exception {
		return updateExtendData(refTableName, refId, null, data);
	}
	
	/**
	 * 删除扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param defineNo 扩展信息定义编号，可以为null
	 * @param fieldNos 扩展信息编号列表。值为sys_extend.field_no
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteExtendData(String refTableName, String refId, String defineNo, List<String> fieldNos) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(refTableName) || StrUtils.isNull(refId)) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "必须包含扩展信息的归属数据");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("ref_table_name", refTableName);
		keyData.put("ref_id", refId);
		if (StrUtils.isNotNull(defineNo))
			keyData.put("define_no", defineNo);
		if (fieldNos != null && fieldNos.size() > 0)
			keyData.put("field_no", fieldNos);
		result = dataSetDao.deleteSimpleData("sys_extend", keyData);
		return result;
	}
	
	/**
	 * 删除扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param fieldNos 扩展信息编号列表。值为sys_extend.field_no
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteExtendData(String refTableName, String refId, List<String> fieldNos) throws Exception {
		return deleteExtendData(refTableName, refId, null, fieldNos);
	}
	
	/**
	 * 查询扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param defineNo 扩展信息定义编号，可以为null
	 * @param fieldNos 扩展信息编号列表。值为sys_extend.field_no，为null或者为空时查询所有扩展数据
	 * @return
	 * @throws Exception
	 */
	public ParaMap getExtendData(String refTableName, String refId, String defineNo, List<String> fieldNos) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(refTableName) || StrUtils.isNull(refId)) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "必须包含扩展信息的归属数据");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("ref_table_name", refTableName);
		keyData.put("ref_id", refId);
		if (StrUtils.isNotNull(defineNo))
			keyData.put("define_no", defineNo);
		if (fieldNos != null && fieldNos.size() > 0)
			keyData.put("field_no", fieldNos);
		result = dataSetDao.querySimpleData("sys_extend", keyData);
		return result;
	}
	
	/**
	 * 查询扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @param fieldNos 扩展信息编号列表。值为sys_extend.field_no，为null或者为空时查询所有扩展数据
	 * @return
	 * @throws Exception
	 */
	public ParaMap getExtendData(String refTableName, String refId, List<String> fieldNos) throws Exception {
		return getExtendData(refTableName, refId, null, fieldNos);
	}
	
	/**
	 * 查询扩展数据
	 * @param refTableName 引用数据所属表名
	 * @param refId 引用数据ID
	 * @return
	 * @throws Exception
	 */
	public ParaMap getExtendData(String refTableName, String refId) throws Exception {
		return getExtendData(refTableName, refId, null);
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
}

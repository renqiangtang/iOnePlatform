package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 行政区管理Dao
 * huafc
 * 2012-04-21
 */
public class CantonDao extends BaseDao {
	static private String system_default_canton_id = null;
	
	//返回parentId下一级子节点记录
	public ParaMap getCantonFlatTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回指定根节点的递归树记录
	public ParaMap getCantonRecurseTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回parentId下一级子节点记录(按模块编号)
	public ParaMap getCantonFlatTreeDataByModuleNo(String moduleNo, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleNo == null || moduleNo.equals("") || moduleNo.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleNo", moduleNo);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回指定根节点的递归树记录(按模块编号)
	public ParaMap getCantonRecurseTreeDataByModuleNo(String moduleNo, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleNo == null || moduleNo.equals("") || moduleNo.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleNo", moduleNo);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询行政区数据
	public ParaMap getCantonData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}
	
	//更新行政区
	public ParaMap updateCantonData(ParaMap cantonData) throws Exception {
		ParaMap result;
		int state;
		//检查同一父节点下是否有同名行政区
		ParaMap checkCantonData = new ParaMap();
		String name = cantonData.getString("name");
		if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
			checkCantonData.put("id", cantonData.getString("id"));
			checkCantonData.put("name", name);
			checkCantonData.put("parent_id", cantonData.getString("parent_id"));
			result = checkCantonExists(null, null, checkCantonData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "同一父行政区下不能存在同名（" + cantonData.getString("name") + "）的行政区");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		return dataSetDao.updateData("sys_canton", "id", cantonData, format);
	}
	
	//删除行政区数据
	public ParaMap deleteCantonData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "deleteCantonData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	//检查行政区数据是否存在，保存前调用
	public ParaMap checkCantonExists(String moduleId, String dataSetNo, ParaMap cantonData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_canton_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCantonExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		//行政区在同一父下面应该不能存在同名的
		if (cantonData.containsKey("name")) {
			boolean blnHasId = false;
			if (cantonData.containsKey("id")) {
				String id = cantonData.getString("id");
				if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
					sqlParams.put("id", id);
					customCondition.append(" and id <> :id");
					blnHasId = true;
				}
			}
			String name = cantonData.getString("name");
			if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
				if (blnHasId)
					customCondition.append(" and parent_id in (select parent_id from sys_canton where id = :id)");
				else {
					boolean blnHasParentId = false;
					if (cantonData.containsKey("parent_id")) {
						String parentId = cantonData.getString("parent_id");
						if (parentId != null && !parentId.equals("") && !parentId.equalsIgnoreCase("null")) {
							sqlParams.put("parent_id", parentId);
							customCondition.append(" and parent_id = :parent_id");
							blnHasParentId = true;
						}
					}
					if (!blnHasParentId)
						customCondition.append(" and parent_id is null");
				}
			}
		}
		//检查是否是id记录的子节点，用于移动或者设置父节点时检测
		if (cantonData.containsKey("source_id") && cantonData.containsKey("target_id")) {
			String sourceId = cantonData.getString("source_id");
			String targetId = cantonData.getString("target_id");
			sqlParams.put("source_id", sourceId);
			sqlParams.put("target_id", targetId);
			customCondition.append(" and id = :target_id and id in (select id from sys_canton CONNECT BY PARENT_ID = PRIOR id start with parent_id = :source_id)");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 获取当前系统默认单位指向的行政区
	 * @return
	 * @throws Exception 
	 */
	static public String getSystemCantonId(boolean forceQueryDB) throws Exception {
		if (forceQueryDB)
			system_default_canton_id = null;
		if (StrUtils.isNull(system_default_canton_id)) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap data = new ParaMap();
			data.put("no", "system_default_organ");
			data = dataSetDao.querySimpleData("sys_params", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				String organId = String.valueOf(data.getRecordValue(0, "lvalue"));
				data.clear();
				data.put("id", organId);
				data = dataSetDao.querySimpleData("sys_organ", data);
				if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0)
					system_default_canton_id = String.valueOf(data.getRecordValue(0, "canton_id"));
			}
		}
		return system_default_canton_id;
	}
}

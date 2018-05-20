package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 单位管理Dao
 * huafc
 * 2012-05-05
 */
public class OrganDao extends BaseDao {
	//返回parentId下一级子节点记录
	public ParaMap getOrganFlatTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryOrganFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回指定根节点的递归树记录
	public ParaMap getOrganRecurseTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryOrganRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询单位数据
	public ParaMap getOrganData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryOrganData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//更新单位
	public ParaMap updateOrganData(ParaMap organData) throws Exception {
		ParaMap result;
		int state;
		//检查是否有同名单位
		ParaMap checkOrganData = new ParaMap();
		String name = organData.getString("name");
		if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
			checkOrganData.put("id", organData.getString("id"));
			checkOrganData.put("name", name);
			result = checkOrganExists(null, null, checkOrganData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "不能存在同名（" + organData.getString("name") + "）的单位");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("sys_organ", "id", organData, format);
		if (organData.containsKey("setDefaultOrgan")) {
			int setDefaultOrgan = organData.getInt("setDefaultOrgan");
			if (setDefaultOrgan == 1) {
				state = result.getInt("state");
				if (state == 1) {
					String id = organData.getString("id");
					boolean blnReturnId = false;
					if (id == null || id.equals("") || id.equalsIgnoreCase("null")) {
						id = result.getString("id");
						blnReturnId = true;
					}
					result = setDefaultOrgan(null, null, id);
					if (blnReturnId)
						result.put("id", id);
					state = result.getInt("state");
					if (state == 1) {
						result.put("setDefaultOrgan", 1);
					} else if (state == 0) {
						result.put("setDefaultOrgan", -1);
					}
				}
			}
		} else {
			result.put("setDefaultOrgan", 0);
		}
		return result;
	}
	
	//删除单位数据
	public ParaMap deleteOrganData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "deleteOrganData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", id);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	//检查单位数据是否存在，保存前调用
	public ParaMap checkOrganExists(String moduleId, String dataSetNo, ParaMap organData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryOrganExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		//单位不能绝对同名的
		if (organData.containsKey("id")) {
			String id = organData.getString("id");
			if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (organData.containsKey("name")) {
			String name = organData.getString("name");
			if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
			}
		}
		//检查是否是id记录的子节点，用于移动或者设置父节点时检测
		if (organData.containsKey("source_id") && organData.containsKey("target_id")) {
			String sourceId = organData.getString("source_id");
			String targetId = organData.getString("target_id");
			sqlParams.put("source_id", sourceId);
			sqlParams.put("target_id", targetId);
			customCondition.append(" and id = :target_id and id in (select id from sys_organ CONNECT BY PARENT_ID = PRIOR id start with parent_id = :source_id)");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//设置单位为当前系统的缺省单位
	public ParaMap setDefaultOrgan(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "setDefaultOrgan");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.executeSQL(sqlParams);
		if (result.getInt("state") == 1)
			CantonDao.getSystemCantonId(true);
		return result;
	}
	
	//获取当前系统的缺省单位信息
	public ParaMap getDefaultOrganData(String moduleId, String dataSetNo) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getDefaultOrganData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	
	//获取单位的交易资格
	public ParaMap getOrganQualification(String moduleId, String dataSetNo,String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_organ_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getOrganQualification");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}
}

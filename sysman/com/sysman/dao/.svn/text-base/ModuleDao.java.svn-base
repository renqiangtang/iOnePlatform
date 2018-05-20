package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 模块管理Dao
 * huafc
 * 2012-05-19
 */
public class ModuleDao extends BaseDao {
	//返回模块树
	public ParaMap getModuleTreeData(String moduleId, String dataSetNo, String parentId,String searchNO) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleAllTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		
		StringBuffer sql=new StringBuffer("");
		ParaMap customConditions = new ParaMap();
		if(StrUtils.isNotNull(searchNO)){
			sqlParams.put("dataSetNo", "queryModuleAllFlatTreeSearch");
			sql.append(" and NO = '"+searchNO+"' ");
		}else{
			sqlParams.put("parent_id", parentId);
		}
		customConditions.put("CUSTOM_CONDITION", sql.toString() );
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getModuleTreeDataByModuleNo(String moduleNo, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleNo == null || moduleNo.equals("") || moduleNo.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleNo", moduleNo);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleAllTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询模块数据
	public ParaMap getModuleData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询数据集数据
	public ParaMap getDataSetData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.querySimpleData("sys_dataset", keyData);
	}
	
	//查询字符串资源数据
	public ParaMap getResourceData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.querySimpleData("sys_resource", keyData);
	}
	
	//获取模块最大排序数
	public ParaMap getModuleMaxTurn(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleMaxTurn");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		return dataSetDao.queryData(sqlParams);
	}
	
	//获取数据集最大排序数
	public ParaMap getDataSetMaxTurn(String moduleId, String dataSetNo, String dataSetModuleId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleDataSetTurn");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("module_id", dataSetModuleId);
		return dataSetDao.queryData(sqlParams);
	}
	
	//获取字符串资源最大排序数
	public ParaMap getResourceMaxTurn(String moduleId, String dataSetNo, String resourceModuleId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryModuleResourceTurn");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("module_id", resourceModuleId);
		return dataSetDao.queryData(sqlParams);
	}
	
	//更新模块数据
	public ParaMap updateModuleData(ParaMap moduleData) throws Exception {
		ParaMap result;
		int state;
		//检查同一父节点下是否有同名模块
		ParaMap checkModuleData = new ParaMap();
		String name = moduleData.getString("name");
		if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
			checkModuleData.put("id", moduleData.getString("id"));
			checkModuleData.put("name", name);
			checkModuleData.put("parent_id", moduleData.getString("parent_id"));
			result = checkModuleExists(null, null, checkModuleData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "模块重复。模块编号如果不为空则必须是唯一的，同时同一父模块下不能存在同名（" + moduleData.getString("name") + "）的子模块");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		moduleData.put("create_user_id", moduleData.getString("u"));
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		String id = moduleData.getString("id");
		if ((id == null || id.equals("") || id.equalsIgnoreCase("null")) && !moduleData.containsKey("turn")) {
			ParaMap maxTurnResult = getModuleMaxTurn(moduleData.getString("moduleId"), null, moduleData.getString("parent_id"));
			int maxTurn = Math.round(Float.parseFloat((String.valueOf(maxTurnResult.getRecordValue(0, "turn")))));
			moduleData.put("turn", maxTurn + 1);
		}
		result = dataSetDao.updateData("sys_module", "id", moduleData, format);
		return result;
	}
	
	//更新数据集数据
	public ParaMap updateDataSetData(ParaMap dataSetData) throws Exception {
		ParaMap result;
		int state;
		//检查同一模块下是否有同名、编号的数据集
		ParaMap checkDataSetData = new ParaMap();
		checkDataSetData.put("id", dataSetData.getString("id"));
		checkDataSetData.put("no", dataSetData.getString("no"));
		checkDataSetData.put("name", dataSetData.getString("name"));
		checkDataSetData.put("module_id", dataSetData.getString("module_id"));
		result = checkDataSetExists(null, null, checkDataSetData);
		state = result.getInt("state");
		if (state == 1) {
			if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
				result.clear();
				result.put("state", 0);
				result.put("message", "同一模块下不能存在同编号（" + dataSetData.getString("no") + "）或者同名（" + dataSetData.getString("name") + "）的数据集");
				return result;
			}
		} else
			return result;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		String id = dataSetData.getString("id");
		if ((id == null || id.equals("") || id.equalsIgnoreCase("null")) && !dataSetData.containsKey("turn")) {
			ParaMap maxTurnResult = getDataSetMaxTurn(dataSetData.getString("moduleId"), null, dataSetData.getString("module_id"));
			int maxTurn = Math.round(Float.parseFloat((String.valueOf(maxTurnResult.getRecordValue(0, "turn")))));
			dataSetData.put("turn", maxTurn + 1);
		}
		result = dataSetDao.updateData("sys_dataset", "id", dataSetData, format);
		return result;
	}
	
	//更新字符串资源数据
	public ParaMap updateResourceData(ParaMap resourceData) throws Exception {
		ParaMap result;
		int state;
		//检查同一模块下是否有同名、编号的字符串资源
		ParaMap checkResourceData = new ParaMap();
		checkResourceData.put("id", resourceData.getString("id"));
		checkResourceData.put("no", resourceData.getString("no"));
		checkResourceData.put("name", resourceData.getString("name"));
		checkResourceData.put("module_id", resourceData.getString("module_id"));
		result = checkResourceExists(null, null, checkResourceData);
		state = result.getInt("state");
		if (state == 1) {
			if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
				result.clear();
				result.put("state", 0);
				result.put("message", "同一模块下不能存在同编号（" + resourceData.getString("no") + "）或者同名（" + resourceData.getString("name") + "）的字符串资源");
				return result;
			}
		} else
			return result;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		String id = resourceData.getString("id");
		if ((id == null || id.equals("") || id.equalsIgnoreCase("null")) && !resourceData.containsKey("turn")) {
			ParaMap maxTurnResult = getResourceMaxTurn(resourceData.getString("moduleId"), null, resourceData.getString("module_id"));
			int maxTurn = Math.round(Float.parseFloat((String.valueOf(maxTurnResult.getRecordValue(0, "turn")))));
			resourceData.put("turn", maxTurn + 1);
		}
		return dataSetDao.updateData("sys_resource", "id", resourceData, format);
	}
	
	//删除模块数据
	public ParaMap deleteModuleData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "deleteModuleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	//删除数据集数据
	public ParaMap deleteDataSetData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.deleteSimpleData("sys_dataset", keyData);
	}
	
	//删除字符串资源数据
	public ParaMap deleteResourceData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.deleteSimpleData("sys_resource", keyData);
	}

	//检查模块数据是否存在，保存前调用
	public ParaMap checkModuleExists(String moduleId, String dataSetNo, ParaMap moduleData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryModuleExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		//模块在同一父下面应该不能存在同名的，同名的无意义
		if (moduleData.containsKey("no") || moduleData.containsKey("name")) {
			String no = moduleData.getString("no");
			String name = moduleData.getString("name");
			if (StrUtils.isNotNull(no) || StrUtils.isNotNull(name)) {
				boolean blnHasId = false;
				if (moduleData.containsKey("id")) {
					String id = moduleData.getString("id");
					if (StrUtils.isNotNull(id)) {
						sqlParams.put("id", id);
						customCondition.append(" and id <> :id");
						blnHasId = true;
					}
				}
				customCondition.append(" and (1 = 2 ");
				if (StrUtils.isNotNull(no)) {
					sqlParams.put("no", no);
					customCondition.append(" or no = :no");
				}
				if (StrUtils.isNotNull(name)) {
					sqlParams.put("name", name);
					customCondition.append(" or (name = :name");
					if (blnHasId)
						customCondition.append(" and parent_id in (select parent_id from sys_module where id = :id)");
					else {
						boolean blnHasParentId = false;
						if (moduleData.containsKey("parent_id")) {
							String parentId = moduleData.getString("parent_id");
							if (parentId != null && !parentId.equals("") && !parentId.equalsIgnoreCase("null")) {
								sqlParams.put("parent_id", parentId);
								customCondition.append(" and parent_id = :parent_id");
								blnHasParentId = true;
							}
						}
						if (!blnHasParentId)
							customCondition.append(" and parent_id is null");
					}
					customCondition.append(")");
				}
				customCondition.append(")");
			}
		}
		//检查是否是id记录的子节点，用于移动或者设置父节点时检测
		if (moduleData.containsKey("source_id") && moduleData.containsKey("target_id")) {
			String sourceId = moduleData.getString("source_id");
			String targetId = moduleData.getString("target_id");
			sqlParams.put("source_id", sourceId);
			sqlParams.put("target_id", targetId);
			customCondition.append(" and id = :target_id and id in (select id from sys_module CONNECT BY PARENT_ID = PRIOR id start with parent_id = :source_id)");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//检查数据集数据是否存在，保存前调用
	public ParaMap checkDataSetExists(String moduleId, String dataSetNo, ParaMap dataSetData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryDataSetExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		if (dataSetData.containsKey("id")) {
			String id = dataSetData.getString("id");
			if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (dataSetData.containsKey("module_id")) {
			String dataSetModuleId = dataSetData.getString("module_id");
			if (dataSetModuleId != null && !dataSetModuleId.equals("") && !dataSetModuleId.equalsIgnoreCase("null")) {
				sqlParams.put("module_id", dataSetModuleId);
				customCondition.append(" and module_id = :module_id");
			}
		}
		//同一模块下的数据集应该不能存在同名及同编号的
		if (dataSetData.containsKey("no") && dataSetData.containsKey("name")) {
			customCondition.append(" and (1 = 2");
			if (dataSetData.containsKey("no")) {
				String no = dataSetData.getString("no");
				if (no != null && !no.equals("") && !no.equalsIgnoreCase("null")) {
					sqlParams.put("no", no);
					customCondition.append(" or no = :no");
				}
			}
			if (dataSetData.containsKey("name")) {
				String name = dataSetData.getString("name");
				if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
					sqlParams.put("name", name);
					customCondition.append(" or name = :name");
				}
			}
			customCondition.append(")");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//检查字符串资源数据是否存在，保存前调用
	public ParaMap checkResourceExists(String moduleId, String dataSetNo, ParaMap resourceData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_module_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryResourceExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		if (resourceData.containsKey("id")) {
			String id = resourceData.getString("id");
			if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (resourceData.containsKey("module_id")) {
			String resourceModuleId = resourceData.getString("module_id");
			if (resourceModuleId != null && !resourceModuleId.equals("") && !resourceModuleId.equalsIgnoreCase("null")) {
				sqlParams.put("module_id", resourceModuleId);
				customCondition.append(" and module_id = :module_id");
			}
		}
		//同一模块下的字符串资源应该不能存在同名及同编号的
		if (resourceData.containsKey("no") && resourceData.containsKey("name")) {
			customCondition.append(" and (1 = 2");
			if (resourceData.containsKey("no")) {
				String no = resourceData.getString("no");
				if (no != null && !no.equals("") && !no.equalsIgnoreCase("null")) {
					sqlParams.put("no", no);
					customCondition.append(" or no = :no");
				}
			}
			if (resourceData.containsKey("name")) {
				String userName = resourceData.getString("name");
				if (userName != null && !userName.equals("") && !userName.equalsIgnoreCase("null")) {
					sqlParams.put("name", userName);
					customCondition.append(" or name = :name");
				}
			}
			customCondition.append(")");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

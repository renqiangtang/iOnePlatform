package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class MenuDao extends BaseDao {
	/**
	 * 获取通用的菜单记录
	 */
	public ParaMap getMenuRecurseTreeData(String rootMenuId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_menu_manager");
		sqlParams.put("dataSetNo", "queryMenuRecurseTree");
		sqlParams.put("parent_id", rootMenuId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	/**
	 * 按模块获取菜单
	 */
	public ParaMap getModuleMenuRecurseTreeData(String moduleId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_menu_manager");
		sqlParams.put("dataSetNo", "queryModuleMenuRecurseTree");
		sqlParams.put("module_id", moduleId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	/**
	 * 按角色获取菜单
	 */
	public ParaMap getRoleMenuRecurseTreeData(String roleId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", roleId);
		ParaMap result = dataSetDao.querySimpleData("sys_role", keyData);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String mainMenuId = String.valueOf(result.getRecordValue(0, "main_root_menu_id"));
			result = getMenuRecurseTreeData(mainMenuId);
		}
		return result;
	}
	/**
	 * 按用户获取菜单，返回的用户缺省角色的菜单
	 */
	public ParaMap getUserMenuRecurseTreeData(String userId) throws Exception {
		UserDao userDao = new UserDao();
		return getRoleMenuRecurseTreeData(userDao.getUserDefaultRoleId(userId));
	}
	
	public ParaMap getMenuRecurseTreeData(ParaMap inMap) throws Exception {
		ParaMap result = null;
		if (inMap.containsKey("rootMenuId"))
			result = getMenuRecurseTreeData(inMap.getString("rootMenuId"));
		else if (inMap.containsKey("menuModuleId"))
			result = getModuleMenuRecurseTreeData(inMap.getString("menuModuleId"));
		else if (inMap.containsKey("roleId"))
			result = getRoleMenuRecurseTreeData(inMap.getString("roleId"));
		else if (inMap.containsKey("userId"))
			result = getUserMenuRecurseTreeData(inMap.getString("userId"));
		return result;
	}
	/**
	 * 返回菜单树
	 */
	public ParaMap getMenuTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		//if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_menu_manager");
		//else
		//	sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryMenuRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 根据父ID获取菜单表数据
	 */
	public ParaMap getMenuData(String moduleId,String dataSetNo,String parentId,String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_menu_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "query_meun");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	
	
	

}

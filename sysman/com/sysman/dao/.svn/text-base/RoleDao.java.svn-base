package com.sysman.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 角色管理Dao
 * huafc
 * 2012-05-03
 */
public class RoleDao extends BaseDao {
	//返回parentId下一级子节点记录
	public ParaMap getRoleFlatTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回指定根节点的递归树记录
	public ParaMap getRoleRecurseTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getRoleGoodsType(String roleId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_role_manager");
		sqlParams.put("dataSetNo", "getRoleGoodsType");
		sqlParams.put("roleId", roleId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	//查询角色数据
	public ParaMap getRoleData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询角色下属用户数据
	public ParaMap getRoleUserData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleUserData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询角色下属岗位数据
	public ParaMap getRoleJobData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleJobData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询角色下属部门数据
	public ParaMap getRoleDepartmentData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleDepartmentData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	//更新角色数据
	public ParaMap updateRoleData(ParaMap roleData) throws Exception {
		ParaMap result;
		int state;
		//检查同一父节点下是否有同名角色
		ParaMap checkRoleData = new ParaMap();
		String name = roleData.getString("name");
		if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
			checkRoleData.put("id", roleData.getString("id"));
			checkRoleData.put("name", name);
			checkRoleData.put("parent_id", roleData.getString("parent_id"));
			result = checkRoleExists(null, null, checkRoleData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "同一父角色下不能存在同名（" + roleData.getString("name") + "）的角色");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("sys_role", "id", roleData, format);
		
		state = result.getInt("state");
		if (state == 1) {
			String id = roleData.getString("id");
			if (id == null || id.equals("") || id.equalsIgnoreCase("null"))//新增数据
				id = result.getString("id");
			//处理角色下属人员数据
			String addUserIds = roleData.getString("addUserIds");
			if (addUserIds != null && !addUserIds.equals("") && !addUserIds.equalsIgnoreCase("null")) {
				List updateRoleUsers = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addUserIds, ";");
		        while(st.hasMoreTokens()) {
		        	String userId = st.nextToken();
		        	if (userId != null && !userId.equals("") && !userId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("id", "");
						data.put("role_id", id);
						data.put("ref_type", 0);
						data.put("ref_id", userId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "id");
						record.put("data", data);
						updateRoleUsers.add(record);
		        	}
		        }
		        if (updateRoleUsers.size() > 0)
		        	result = dataSetDao.updateData(updateRoleUsers);
			}
			String deleteUserIds = roleData.getString("deleteUserIds");
			if (deleteUserIds != null && !deleteUserIds.equals("") && !deleteUserIds.equalsIgnoreCase("null")) {
				List updateRoleUsers = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteUserIds, ";");
				while(st.hasMoreTokens()) {
		        	String userId = st.nextToken();
		        	if (userId != null && !userId.equals("") && !userId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("role_id", id);
						data.put("ref_type", 0);
						data.put("ref_id", userId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "role_id;ref_type;ref_id");
						record.put("data", data);
						updateRoleUsers.add(record);
		        	}
		        }
		        if (updateRoleUsers.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateRoleUsers);
			}
			//处理角色下属岗位数据
			String addJobIds = roleData.getString("addJobIds");
			if (addJobIds != null && !addJobIds.equals("") && !addJobIds.equalsIgnoreCase("null")) {
				List updateRoleJobs = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addJobIds, ";");
		        while(st.hasMoreTokens()) {
		        	String jobId = st.nextToken();
		        	if (jobId != null && !jobId.equals("") && !jobId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("id", "");
						data.put("role_id", id);
						data.put("ref_type", 1);
						data.put("ref_id", jobId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "id");
						record.put("data", data);
						updateRoleJobs.add(record);
		        	}
		        }
		        if (updateRoleJobs.size() > 0)
		        	result = dataSetDao.updateData(updateRoleJobs);
			}
			String deleteJobIds = roleData.getString("deleteJobIds");
			if (deleteJobIds != null && !deleteJobIds.equals("") && !deleteJobIds.equalsIgnoreCase("null")) {
				List updateRoleJobs = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteJobIds, ";");
				while(st.hasMoreTokens()) {
		        	String jobId = st.nextToken();
		        	if (jobId != null && !jobId.equals("") && !jobId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("role_id", id);
						data.put("ref_type", 1);
						data.put("ref_id", jobId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "role_id;ref_type;ref_id");
						record.put("data", data);
						updateRoleJobs.add(record);
		        	}
		        }
		        if (updateRoleJobs.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateRoleJobs);
			}
			//处理角色下属部门数据
			String addDepartmentIds = roleData.getString("addDepartmentIds");
			if (addDepartmentIds != null && !addDepartmentIds.equals("") && !addDepartmentIds.equalsIgnoreCase("null")) {
				List updateRoleDepartments = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addDepartmentIds, ";");
		        while(st.hasMoreTokens()) {
		        	String departmentId = st.nextToken();
		        	if (departmentId != null && !departmentId.equals("") && !departmentId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("id", "");
						data.put("role_id", id);
						data.put("ref_type", 2);
						data.put("ref_id", departmentId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "id");
						record.put("data", data);
						updateRoleDepartments.add(record);
		        	}
		        }
		        if (updateRoleDepartments.size() > 0)
		        	result = dataSetDao.updateData(updateRoleDepartments);
			}
			String deleteDepartmentIds = roleData.getString("deleteDepartmentIds");
			if (deleteDepartmentIds != null && !deleteDepartmentIds.equals("") && !deleteDepartmentIds.equalsIgnoreCase("null")) {
				List updateRoleDepartments = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteDepartmentIds, ";");
				while(st.hasMoreTokens()) {
		        	String departmentId = st.nextToken();
		        	if (departmentId != null && !departmentId.equals("") && !departmentId.equalsIgnoreCase("null")) {
			        	ParaMap data = new ParaMap();
			        	data.clear();
						data.put("role_id", id);
						data.put("ref_type", 2);
						data.put("ref_id", departmentId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "role_id;ref_type;ref_id");
						record.put("data", data);
						updateRoleDepartments.add(record);
		        	}
		        }
		        if (updateRoleDepartments.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateRoleDepartments);
			}
			
			//以下更新角色交易物类型权限 
			String roleId=roleData.getString("roleId");//获取角色ID
			//删除此角色的权限
			ParaMap trmap=new ParaMap();
			trmap.put("role_id", roleId);
			dataSetDao.deleteSimpleData("trans_role_goods_type_rel",trmap);
			
			//插入新的角色权限
			String roleTypeids=roleData.getString("roleTypeids");// : "101,301,401,201"
			if(roleTypeids!=null && roleTypeids!=""){
				if(roleTypeids.indexOf(",")!=-1){
					ParaMap sqlmap=new ParaMap();
					String rts[]=roleTypeids.split(",");
					for(int i=0;i<rts.length;i++){
						sqlmap.put("role_id", roleId);
						sqlmap.put("goods_type_id", rts[i]);
						sqlmap.put("is_valid", "1");
						sqlmap.put("create_user_id", "0001");
						ParaMap pa=dataSetDao.updateData("trans_role_goods_type_rel", "id", sqlmap);
					}
					
				}else{
					ParaMap sqlmap=new ParaMap();
					sqlmap.put("role_id", roleId);
					sqlmap.put("goods_type_id", roleTypeids);
					sqlmap.put("is_valid", "1");
					sqlmap.put("create_user_id", "001");
					ParaMap pa=dataSetDao.updateData("trans_role_goods_type_rel", "id", sqlmap);
				}
			}
			result.clear();
			result.put("state", 1);
			result.put("id", id);
		}
		return result;
	}
	
	//删除角色数据
	public ParaMap deleteRoleData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "deleteRoleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	//检查角色数据是否存在，保存前调用
	public ParaMap checkRoleExists(String moduleId, String dataSetNo, ParaMap roleData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_role_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryRoleExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		//角色在同一父下面应该不能存在同名的
		if (roleData.containsKey("name")) {
			boolean blnHasId = false;
			if (roleData.containsKey("id")) {
				String id = roleData.getString("id");
				if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
					sqlParams.put("id", id);
					customCondition.append(" and id <> :id");
					blnHasId = true;
				}
			}
			String name = roleData.getString("name");
			if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
				if (blnHasId)
					customCondition.append(" and parent_id in (select parent_id from sys_role where id = :id)");
				else {
					boolean blnHasParentId = false;
					if (roleData.containsKey("parent_id")) {
						String parentId = roleData.getString("parent_id");
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
		if (roleData.containsKey("source_id") && roleData.containsKey("target_id")) {
			String sourceId = roleData.getString("source_id");
			String targetId = roleData.getString("target_id");
			sqlParams.put("source_id", sourceId);
			sqlParams.put("target_id", targetId);
			customCondition.append(" and id = :target_id and id in (select id from sys_role CONNECT BY PARENT_ID = PRIOR id start with parent_id = :source_id)");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

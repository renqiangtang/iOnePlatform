package com.sysman.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 内部人员管理Dao
 * huafc
 * 2012-05-09
 */
public class EmpDao extends BaseDao {
	//查询人员列表数据
	public ParaMap getEmpListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询人员数据
	public ParaMap getEmpData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询人员所属部门数据
	public ParaMap getEmpDepartmentData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpDepartmentData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询人员所属岗位数据
	public ParaMap getEmpJobData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpJobData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询人员所属角色数据
	public ParaMap getEmpRoleData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpRoleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateEmpData(ParaMap empData)  throws Exception{
		ParaMap result;
		int state;
		ParaMap checkEmpData = new ParaMap();
		//检查用户名
		String userName = empData.getString("user_name");
		if (StrUtils.isNotNull(userName)) {
			checkEmpData.clear();
			checkEmpData.put("id", empData.getString("id"));
			checkEmpData.put("userName", userName);
			result = checkEmpExists(null, null, checkEmpData);
			state = result.getInt("state");
			if (state == 1) {
				if ((Integer) result.getRecordValue(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "用户名已经被使用");
					return result;
				}
			} else
				return result;
		}
		//检查CAKEY
		String cakey = empData.getString("cakey");
		if (StrUtils.isNotNull(cakey)) {
			checkEmpData.clear();
			checkEmpData.put("id", empData.getString("id"));
			checkEmpData.put("cakey", cakey);
			result = checkEmpExists(null, null, checkEmpData);
			state = result.getInt("state");
			if (state == 1) {
				if ((Integer) result.getRecordValue(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "CAKEY 已经被使用");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		result = dataSetDao.updateData("sys_emp", "id", empData, null);
		state = result.getInt("state");
		if (state == 1) {
			String id = empData.getString("id");
			if (StrUtils.isNull(id))
				id = result.getString("id");
			//新增人员用户，如果存在则不需要处理
			String userId = empData.getString("user_id");
			String cakeyId = empData.getString("cakey_id");
			if (StrUtils.isNotNull(userId) || StrUtils.isNotNull(userName)) {
				ParaMap data = new ParaMap();
				if (StrUtils.isNull(userId))
					data.put("id", "");
				else
					data.put("id", userId);
				data.put("user_name", userName);
				data.put("password", empData.getString("password"));
				data.put("user_type", 0);
				data.put("is_valid", empData.getString("user_is_valid"));
				data.put("status", 1);
				data.put("ref_table_name", "sys_emp");
				data.put("ref_id", id);
				data.put("validity", 365);
				result = dataSetDao.updateData("sys_user", "id", data, null);
				state = result.getInt("state");
				if (state == 1 && StrUtils.isNull(userId))
					userId = result.getString("id");
			}
			//保存人员CAKEY，必须有保存登录用户数据
			if (StrUtils.isNotNull(userId)) {
				if (StrUtils.isNotNull(cakeyId) || StrUtils.isNotNull(cakey)) {
					ParaMap data = new ParaMap();
					if (StrUtils.isNull(cakeyId))
						data.put("id", "");
					else
						data.put("id", cakeyId);
					data.put("user_id", userId);
					if (StrUtils.isNotNull(cakey))
						data.put("key", cakey);
					result = dataSetDao.updateData("sys_cakey", "id", data, null);
					state = result.getInt("state");
					if (state == 1 && StrUtils.isNull(cakeyId))
						cakeyId = result.getString("id");
				}
			}
			//处理所属部门数据
			String addDepartmentIds = empData.getString("addDepartmentIds");
			if (StrUtils.isNotNull(addDepartmentIds)) {
				List updateEmpDepartments = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addDepartmentIds, ";");
		        while(st.hasMoreTokens()) {
		        	String departmentId = st.nextToken();
		        	if (StrUtils.isNotNull(departmentId)) {
		        		ParaMap data = new ParaMap();
						data.put("id", "");
						data.put("department_id", departmentId);
						data.put("emp_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_department_emp_rel");
						record.put("keyField", "id");
						record.put("data", data);
						updateEmpDepartments.add(record);
		        	}
		        }
		        if (updateEmpDepartments.size() > 0)
		        	result = dataSetDao.updateData(updateEmpDepartments);
			}
			String deleteDepartmentIds = empData.getString("deleteDepartmentIds");
			if (StrUtils.isNotNull(deleteDepartmentIds)) {
				List updateEmpDepartments = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteDepartmentIds, ";");
				while(st.hasMoreTokens()) {
		        	String departmentId = st.nextToken();
		        	if (StrUtils.isNotNull(departmentId)) {
		        		ParaMap data = new ParaMap();
			        	data.put("department_id", departmentId);
						data.put("emp_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_department_emp_rel");
						record.put("keyField", "department_id;emp_id");
						record.put("data", data);
						updateEmpDepartments.add(record);
		        	}
		        }
		        if (updateEmpDepartments.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateEmpDepartments);
			}
			//处理所属岗位数据
			String addJobIds = empData.getString("addJobIds");
			if (StrUtils.isNotNull(addJobIds)) {
				List updateEmpJobs = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addJobIds, ";");
		        while(st.hasMoreTokens()) {
		        	String jobId = st.nextToken();
		        	if (StrUtils.isNotNull(jobId)) {
		        		ParaMap data = new ParaMap();
						data.put("id", "");
						data.put("job_id", jobId);
						data.put("emp_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_job_emp_rel");
						record.put("keyField", "id");
						record.put("data", data);
						updateEmpJobs.add(record);
		        	}
		        }
		        if (updateEmpJobs.size() > 0)
		        	result = dataSetDao.updateData(updateEmpJobs);
			}
			String deleteJobIds = empData.getString("deleteJobIds");
			if (StrUtils.isNotNull(deleteJobIds)) {
				List updateEmpJobs = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteJobIds, ";");
				while(st.hasMoreTokens()) {
		        	String jobId = st.nextToken();
		        	if (StrUtils.isNotNull(jobId)) {
		        		ParaMap data = new ParaMap();
						data.put("job_id", jobId);
						data.put("emp_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_job_emp_rel");
						record.put("keyField", "job_id;emp_id");
						record.put("data", data);
						updateEmpJobs.add(record);
		        	}
		        }
		        if (updateEmpJobs.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateEmpJobs);
			}
			//处理所属角色数据，必须有保存登录用户数据
			if (StrUtils.isNotNull(userId)) {
				String addRoleIds = empData.getString("addRoleIds");
				if (StrUtils.isNotNull(addRoleIds)) {
					List updateEmpRoles = new ArrayList();
					StringTokenizer st = null;
					st = new StringTokenizer(addRoleIds, ";");
			        while(st.hasMoreTokens()) {
			        	String roleId = st.nextToken();
			        	if (StrUtils.isNotNull(roleId)) {
			        		ParaMap data = new ParaMap();
							data.put("id", "");
							data.put("role_id", roleId);
							data.put("ref_type", 0);
							data.put("ref_id", userId);
							ParaMap record = new ParaMap();
							record.put("tableName", "sys_role_rel");
							record.put("keyField", "id");
							record.put("data", data);
							updateEmpRoles.add(record);
			        	}
			        }
			        if (updateEmpRoles.size() > 0)
			        	result = dataSetDao.updateData(updateEmpRoles);
				}
				String deleteRoleIds = empData.getString("deleteRoleIds");
				if (StrUtils.isNotNull(deleteRoleIds)) {
					List updateEmpRoles = new ArrayList();
					StringTokenizer st = null;
					st = new StringTokenizer(deleteRoleIds, ";");
					while(st.hasMoreTokens()) {
			        	String roleId = st.nextToken();
			        	if (StrUtils.isNotNull(roleId)) {
			        		ParaMap data = new ParaMap();
							data.put("role_id", roleId);
							data.put("ref_type", 0);
							data.put("ref_id", userId);
							ParaMap record = new ParaMap();
							record.put("tableName", "sys_role_rel");
							record.put("keyField", "role_id;ref_type;ref_id");
							record.put("data", data);
							updateEmpRoles.add(record);
			        	}
			        }
			        if (updateEmpRoles.size() > 0)
			        	result = dataSetDao.deleteSimpleData(updateEmpRoles);
				}
			}
			result.clear();
			result.put("state", 1);
			result.put("id", id);
			result.put("userId", userId);
			result.put("cakeyId", cakeyId);
		}
		return result;
	}
	
	public ParaMap deleteEmpData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteEmpData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}

	public ParaMap checkEmpExists(String moduleId, String dataSetNo, ParaMap empData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_emp_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = empData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(id)) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
		customCondition.append(" and (1 = 2");
		if (empData.containsKey("name")) {
			String name = empData.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" or name = :name");
			}
		}
		if (empData.containsKey("userName")) {
			String userName = empData.getString("userName");
			if (StrUtils.isNotNull(userName)) {
				sqlParams.put("user_name", userName);
				customCondition.append(" or exists(select * from sys_user where 1 = 1");
				if (StrUtils.isNotNull(id))
					customCondition.append(" and (user_type <> 0 or ref_id <> :id)");
				customCondition.append(" and user_name = :user_name)");
			}
		}
		if (empData.containsKey("cakey")) {
			String empCakey = empData.getString("cakey");
			if (StrUtils.isNotNull(empCakey)) {
				sqlParams.put("cakey", empCakey);
				customCondition.append(" or exists(select * from sys_user where 1 = 1 ");
				if (StrUtils.isNotNull(id))
					customCondition.append(" and (user_type <> 0 or ref_id <> :id)");
				customCondition.append(" and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		}
		customCondition.append(")");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

package com.sysman.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 部门管理Dao
 * huafc
 * 2012-05-07
 */
public class DepartmentDao extends BaseDao {
	//返回parentId下一级子节点记录
	public ParaMap getDepartmentTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDepartmentFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	//查询部门数据
	public ParaMap getDepartmentData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDepartmentData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询部门下属岗位数据
	public ParaMap getDepartmentJobData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDepartmentJobData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询部门下属人员数据
	public ParaMap getDepartmentEmpData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDepartmentEmpData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	//更新部门数据
	public ParaMap updateDepartmentData(ParaMap departmentData) throws Exception {
		ParaMap result;
		//检查同一父节点下是否有同名部门
		ParaMap checkDepartmentData = new ParaMap();
		String name = departmentData.getString("name");
		if (StrUtils.isNotNull(name)) {
			checkDepartmentData.put("id", departmentData.getString("id"));
			checkDepartmentData.put("name", name);
			checkDepartmentData.put("parent_id", departmentData.getString("parent_id"));
			checkDepartmentData.put("organ_id", departmentData.getString("organ_id"));
			result = checkDepartmentExists(null, null, checkDepartmentData);
			if (result.getInt("state") == 1) {
				if ((Integer) result.getRecordValue(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "同一父部门下不能存在同名（" + departmentData.getString("name") + "）的子部门");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("sys_department", "id", departmentData, format);
		if (result.getInt("state") == 1) {
			String organId = departmentData.getString("organ_id");
			String id = departmentData.getString("id");
			if (StrUtils.isNull(id))//新增数据
				id = result.getString("id");
			JSONObject jsonObject;
			JSONArray jsonArray;
			//处理部门下属岗位数据
			List updateDepartmentJobs = new ArrayList();
			if (departmentData.containsKey("jobAdded")) {
				String jobAdded = departmentData.getString("jobAdded");
				if (StrUtils.isNotNull(jobAdded) && !jobAdded.equalsIgnoreCase("undefined")) {
					jsonArray = JSONArray.fromObject(jobAdded);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						ParaMap data = new ParaMap();
						data.put("id", "");
						data.put("name", jsonObject.getString("name"));
						data.put("no", jsonObject.getString("no"));
						data.put("is_valid", jsonObject.getString("isValid"));
						data.put("department_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_job");
						record.put("keyField", "id");
						record.put("data", data);
						updateDepartmentJobs.add(record);
			        }
				}
			}
			if (departmentData.containsKey("jobUpdated")) {
				String jobUpdated = departmentData.getString("jobUpdated");
				if (StrUtils.isNotNull(jobUpdated) && !jobUpdated.equalsIgnoreCase("undefined")) {
					jsonArray = JSONArray.fromObject(jobUpdated);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						ParaMap data = new ParaMap();
						data.put("id", jsonObject.getString("id"));
						data.put("name", jsonObject.getString("name"));
						data.put("no", jsonObject.getString("no"));
						data.put("is_valid", jsonObject.getString("isValid"));
						data.put("department_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_job");
						record.put("keyField", "id");
						record.put("data", data);
						updateDepartmentJobs.add(record);
			        }
				}
			}
			if (updateDepartmentJobs.size() > 0) {
				result = dataSetDao.updateData(updateDepartmentJobs);
			}
			updateDepartmentJobs.clear();
			if (departmentData.containsKey("jobDeleted")) {
				String jobDeleted = departmentData.getString("jobDeleted");
				if (StrUtils.isNotNull(jobDeleted) && !jobDeleted.equalsIgnoreCase("undefined")) {
					jsonArray = JSONArray.fromObject(jobDeleted);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						ParaMap data = new ParaMap();
						String jobId = jsonObject.getString("id");
						if (StrUtils.isNotNull(jobId)) {
							data.put("id", jobId);
							ParaMap record = new ParaMap();
							record.put("tableName", "sys_job");
							record.put("keyField", "id");
							record.put("data", data);
							updateDepartmentJobs.add(record);
							//删除角色与岗位关联
							data = new ParaMap();
							data.put("ref_type", 1);
							data.put("ref_id", jobId);
							record = new ParaMap();
							record.put("tableName", "sys_role_rel");
							record.put("keyField", "ref_type;ref_id");
							record.put("data", data);
							updateDepartmentJobs.add(record);
						}
			        }
				}
			}
			if (updateDepartmentJobs.size() > 0) {
				result = dataSetDao.deleteSimpleData(updateDepartmentJobs);
			}
			//处理部门下属人员数据
			List updateDepartmentEmps = new ArrayList();
			if (departmentData.containsKey("empAdded")) {
				String empAdded = departmentData.getString("empAdded");
				if (StrUtils.isNotNull(empAdded)) {
					jsonArray = JSONArray.fromObject(empAdded);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						ParaMap data = new ParaMap();
						data.put("id", jsonObject.getString("id"));
						data.put("name", jsonObject.getString("name"));
						data.put("no", jsonObject.getString("no"));
						data.put("gender", jsonObject.getString("gender"));
						data.put("tel", jsonObject.getString("tel"));
						data.put("mobile", jsonObject.getString("mobile"));
						data.put("email", jsonObject.getString("email"));
						data.put("is_valid", jsonObject.getString("isValid"));
						data.put("organ_id", organId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_emp");
						record.put("keyField", "id");
						record.put("data", data);
						updateDepartmentEmps.add(record);
			        }
				}
			}
			if (departmentData.containsKey("empUpdated")) {
				String empUpdated = departmentData.getString("empUpdated");
				if (StrUtils.isNotNull(empUpdated) && !empUpdated.equalsIgnoreCase("undefined")) {
					jsonArray = JSONArray.fromObject(empUpdated);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						ParaMap data = new ParaMap();
						data.put("id", jsonObject.getString("id"));
						data.put("name", jsonObject.getString("name"));
						data.put("no", jsonObject.getString("no"));
						data.put("gender", jsonObject.getString("gender"));
						data.put("tel", jsonObject.getString("tel"));
						data.put("mobile", jsonObject.getString("mobile"));
						data.put("email", jsonObject.getString("email"));
						data.put("is_valid", jsonObject.getString("isValid"));
						data.put("organ_id", organId);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_emp");
						record.put("keyField", "id");
						record.put("data", data);
						updateDepartmentEmps.add(record);
			        }
				}
			}
			if (updateDepartmentEmps.size() > 0) {
				result = dataSetDao.updateData(updateDepartmentEmps);
				List updateIds = result.getList("ids");
				updateDepartmentEmps.clear();
				for(int i = 0; i < updateIds.size(); i++) {
					ParaMap keyData = new ParaMap();
					String empId = (String) updateIds.get(i);
					ParaMap data = new ParaMap();
					data.put("id", null);
					data.put("department_id", id);
					data.put("emp_id", empId);
					ParaMap record = new ParaMap();
					record.put("tableName", "sys_department_emp_rel");
					record.put("keyField", "id");
					record.put("checkField", "department_id;emp_id");
					record.put("data", data);
					updateDepartmentEmps.add(record);
				}
				if (updateDepartmentEmps.size() > 0) {
					result = dataSetDao.updateData(updateDepartmentEmps);
				}
			}
			updateDepartmentEmps.clear();
			if (departmentData.containsKey("empDeleted")) {
				String deleteEmpRelIds = departmentData.getString("deleteEmpRelIds");//仅删除关联的人员ID列表
				String empDeleted = departmentData.getString("empDeleted");
				if (StrUtils.isNotNull(empDeleted) && !empDeleted.equalsIgnoreCase("undefined")) {
					jsonArray = JSONArray.fromObject(empDeleted);
					for ( int i = 0 ; i < jsonArray.size(); i++) {
						jsonObject = jsonArray.getJSONObject(i);
						String empId = jsonObject.getString("id");
						if (StrUtils.isNotNull(empId)) {
							ParaMap data = new ParaMap();
							data.put("department_id", id);
							data.put("emp_id", empId);
							ParaMap record = new ParaMap();
							record.put("tableName", "sys_department_emp_rel");
							record.put("keyField", "department_id,emp_id");
							record.put("data", data);
							updateDepartmentEmps.add(record);
							//删除人员数据
							if (StrUtils.isNull(deleteEmpRelIds) || deleteEmpRelIds.indexOf(";" + empId + ";") == -1) {
								data = new ParaMap();
								data.put("id", empId);
								record = new ParaMap();
								record.put("tableName", "sys_emp");
								record.put("keyField", "id");
								record.put("data", data);
								updateDepartmentEmps.add(record);
								//删除角色与人员关联
								data = new ParaMap();
								data.put("ref_type", 0);
								data.put("ref_id", empId);
								record = new ParaMap();
								record.put("tableName", "sys_role_rel");
								record.put("keyField", "ref_type;ref_id");
								record.put("data", data);
								updateDepartmentJobs.add(record);
							}
						}
			        }
				}
			}
			if (updateDepartmentEmps.size() > 0) {
				result = dataSetDao.deleteSimpleData(updateDepartmentEmps);
			}
			result.clear();
			result.put("state", 1);
			result.put("id", id);
		}
		return result;
	}
	
	//删除部门数据
	public ParaMap deleteDepartmentData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteDepartmentData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	//检查部门数据是否存在，保存前调用
	public ParaMap checkDepartmentExists(String moduleId, String dataSetNo, ParaMap departmentData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_department_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDepartmentExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		//部门在同一父下面应该不能存在同名的
		if (departmentData.containsKey("name")) {
			boolean blnHasId = false;
			if (departmentData.containsKey("id")) {
				String id = departmentData.getString("id");
				if (StrUtils.isNotNull(id)) {
					sqlParams.put("id", id);
					customCondition.append(" and id <> :id");
					blnHasId = true;
				}
			}
			boolean blnHasOrganId = departmentData.containsKey("organ_id") && StrUtils.isNotNull(departmentData.getString("organ_id"));
			boolean blnHasParentId = departmentData.containsKey("parent_id") && StrUtils.isNotNull(departmentData.getString("parent_id"));
			String name = departmentData.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
				if (blnHasId)
					customCondition.append(" and parent_id in (select parent_id from sys_department where id = :id)");
				else if (blnHasParentId) {
							sqlParams.put("parent_id", departmentData.getString("parent_id"));
							customCondition.append(" and parent_id = :parent_id");
				} else if (blnHasOrganId) {
					sqlParams.put("organ_id", departmentData.getString("organ_id"));
					customCondition.append(" and organ_id = :organ_id");
		}
			}
		}
		//检查是否是id记录的子节点，用于移动或者设置父节点时检测
		if (departmentData.containsKey("source_id") && departmentData.containsKey("target_id")) {
			String sourceId = departmentData.getString("source_id");
			String targetId = departmentData.getString("target_id");
			sqlParams.put("source_id", sourceId);
			sqlParams.put("target_id", targetId);
			customCondition.append(" and id = :target_id and id in (select id from sys_department CONNECT BY PARENT_ID = PRIOR id start with parent_id = :source_id)");
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

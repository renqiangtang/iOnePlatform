package com.sysman.service;

import java.math.BigDecimal;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.EmpDao;

public class EmpService extends BaseService {
	public ParaMap getEmpListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			//前端表格字段名和数据集字段名不一致
			if (sortField.equalsIgnoreCase("relRoles"))
				sortField = "emp_roles";
			else if (sortField.equalsIgnoreCase("relJobs"))
				sortField = "emp_jobs";
			else if (sortField.equalsIgnoreCase("relDepartments"))
				sortField = "emp_departments";
			else if (sortField.equalsIgnoreCase("createDate"))
				sortField = "create_date";
			else if (sortField.equalsIgnoreCase("isValid"))
				sortField = "is_valid";
			if (StrUtils.isNotNull(sortDir))
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("empName")) {
			String empName = inMap.getString("empName");
			if (empName != null && !empName.equals("") && !empName.equalsIgnoreCase("null")) {
				sqlParams.put("name", empName);
				customCondition.append(" and e.name like '%' || :name || '%'");
			}
		}
		if (inMap.containsKey("empGender")) {
			String empGender = inMap.getString("empGender");
			if (empGender.equals("男") || empGender.equals("女")) {
				sqlParams.put("gender", empGender);
				customCondition.append(" and e.gender = :gender");
			}
		}
		if (inMap.containsKey("empMobile")) {
			String empMobile = inMap.getString("empMobile");
			if (empMobile != null && !empMobile.equals("") && !empMobile.equalsIgnoreCase("null")) {
				sqlParams.put("mobile", empMobile);
				customCondition.append(" and e.mobile like '%' || :mobile || '%'");
			}
		}
		if (inMap.containsKey("empEmail")) {
			String empEmail = inMap.getString("empEmail");
			if (empEmail != null && !empEmail.equals("") && !empEmail.equalsIgnoreCase("null")) {
				sqlParams.put("email", empEmail);
				customCondition.append(" and e.email like '%' || :email || '%'");
			}
		}
		if (inMap.containsKey("empIsValid")) {
			String empIsValid = inMap.getString("empIsValid");
			if (empIsValid.equals("0") || empIsValid.equals("1")) {
				sqlParams.put("is_valid", Integer.parseInt(empIsValid));
				customCondition.append(" and e.is_valid = :is_valid");
			}
		}
		if (inMap.containsKey("empHasCakey")) {
			String empHasCakey = inMap.getString("empHasCakey");
			if (empHasCakey.equals("1"))
				customCondition.append(" and exists(select * from sys_user where user_type = 0 and ref_id = e.id " +
						" and exists(select * from sys_cakey where user_id = sys_user.id and key is not null))");
			else if (empHasCakey.equals("0"))
				customCondition.append(" and (not exists(select * from sys_user where user_type = 0 and ref_id = e.id)" +
						" or exists(select * from sys_user where user_type = 0 and ref_id = e.id " +
						"   and (not exists(select * from sys_cakey where user_id = sys_user.id)" +
						"     or exists(select * from sys_cakey where user_id = sys_user.id and key is null))))");
		}
		if (inMap.containsKey("empCakey")) {
			String empCakey = inMap.getString("empCakey");
			if (empCakey != null && !empCakey.equals("") && !empCakey.equalsIgnoreCase("null")) {
				sqlParams.put("cakey", empCakey);
				customCondition.append(" and exists(select * from sys_user where user_type = 0 and ref_id = e.id " +
						" and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.getEmpListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("gender", "gender");
		fields.put("tel", "tel");
		fields.put("mobile", "mobile");
		fields.put("email", "email");
		fields.put("relRoles", "emp_roles");
		fields.put("relJobs", "emp_jobs");
		fields.put("relDepartments", "emp_departments");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		fields.put("remark", "remark");
		
		fields.put("refId", "refId");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getEmpData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.getEmpData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getEmpDepartmentData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		//组织分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("") && !sortField.equalsIgnoreCase("null")
				&& sortDir != null && !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.getEmpDepartmentData(moduleId, dataSetNo, inMap);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getEmpJobData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		//组织分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("") && !sortField.equalsIgnoreCase("null")
				&& sortDir != null && !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.getEmpJobData(moduleId, dataSetNo, inMap);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getEmpRoleData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		//组织分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("") && !sortField.equalsIgnoreCase("null")
				&& sortDir != null && !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.getEmpRoleData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap updateEmpData(ParaMap bidderData) throws Exception {
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.updateEmpData(bidderData);
		return out;
	}

	public ParaMap deleteEmpData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		EmpDao empDao = new EmpDao();
		ParaMap out = empDao.deleteEmpData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap checkEmpExists(ParaMap inMap) throws Exception{
		EmpDao empDao = new EmpDao();
		ParaMap checkEmpData = new ParaMap();
		checkEmpData.put("id", inMap.getString("id"));
		if (inMap.containsKey("name"))
			checkEmpData.put("name", inMap.getString("name"));
		if (inMap.containsKey("cakey"))
			checkEmpData.put("cakey", inMap.getString("cakey"));
		ParaMap result = empDao.checkEmpExists(null, null, checkEmpData);
		return result;
	}
}

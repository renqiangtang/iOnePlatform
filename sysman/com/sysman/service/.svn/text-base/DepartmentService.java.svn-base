package com.sysman.service;

import java.math.BigDecimal;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.DepartmentDao;

public class DepartmentService extends BaseService {
	public ParaMap getDepartmentTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.getDepartmentTreeData(moduleId, dataSetNo, parentId);
		//转换为前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, intRecurse);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "tree_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		custom.put(MakeJSONData.CUSTOM_CHILD_COUNT_FIELD_NAME, "child_count");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("parentId", "parent_id");
		fields.put("organId", "organ_id");
		fields.put("organName", "organ_name");
		fields.put("relRoles", "department_roles");
		fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("data", items);
		return out;
	}
	
	public ParaMap getDepartmentData(ParaMap inMap) throws Exception {
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.getDepartmentData(inMap.getString("moduleId"),
				inMap.getString("dataSetNo"), inMap.getString("id"));
		return out;
	}
	
	public ParaMap getDepartmentJobData(ParaMap inMap) throws Exception {
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
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.getDepartmentJobData(inMap.getString("moduleId"),
				inMap.getString("dataSetNo"), inMap.getString("id"));
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("departmentName", "department_name");
		fields.put("organName", "organ_name");
		fields.put("relRoles", "job_roles");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getDepartmentEmpData(ParaMap inMap) throws Exception {
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
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.getDepartmentEmpData(inMap.getString("moduleId"),
				inMap.getString("dataSetNo"), inMap.getString("id"));
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
		fields.put("organId", "organ_id");		
		fields.put("isValid", "is_valid");
		fields.put("relRoles", "user_roles");
		fields.put("relJobs", "emp_jobs");
		fields.put("relDepartments", "emp_departments");
		fields.put("organName", "organ_name");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap updateDepartmentData(ParaMap inMap) throws Exception {
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.updateDepartmentData(inMap);
		return out;
	}
	
	public ParaMap deleteDepartmentData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		DepartmentDao departmentDao = new DepartmentDao();
		ParaMap out = departmentDao.deleteDepartmentData(moduleId, dataSetNo, id);
		return out;
	}
}

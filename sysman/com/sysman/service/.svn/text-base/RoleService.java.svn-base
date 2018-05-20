package com.sysman.service;

import java.math.BigDecimal;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.RoleDao;

public class RoleService extends BaseService {
	public ParaMap getRoleTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		RoleDao dao = new RoleDao();
		ParaMap out = null;
		if (intRecurse == 1)
			out = dao.getRoleRecurseTreeData(moduleId, dataSetNo, parentId);
		else
			out = dao.getRoleFlatTreeData(moduleId, dataSetNo, parentId);
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
		fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("data", items);
		return out;
	}
	
	public ParaMap getRoleGoodsType(ParaMap inMap) throws Exception{
		String roleId = inMap.getString("roleId");
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.getRoleGoodsType(roleId);
		
		//转换json格式
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("goods_type_id", "goods_type_id");
		//fields.put("goods_type_no", "goods_type_no");
		fields.put("goods_type_name", "goods_type_name");
		//fields.put("goods_name", "goods_name");
		fields.put("rel_id", "rel_id");
		//fields.put("role_id", "role_id");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getRoleData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.getRoleData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getRoleUserData(ParaMap inMap) throws Exception {
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
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.getRoleUserData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("userName", "user_name");
		fields.put("userType", "user_type");
		fields.put("activeDate", "active_date");
		fields.put("validity", "validity");
		fields.put("status", "status");
		fields.put("organName", "organ_name");
		fields.put("relName", "rel_name");
		fields.put("relRoles", "user_roles");
		fields.put("relJobs", "emp_jobs");
		fields.put("relDepartments", "emp_departments");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getRoleJobData(ParaMap inMap) throws Exception {
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
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.getRoleJobData(moduleId, dataSetNo, inMap);
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
	
	public ParaMap getRoleDepartmentData(ParaMap inMap) throws Exception {
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
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.getRoleDepartmentData(moduleId, dataSetNo, inMap);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("organName", "organ_name");
		fields.put("relRoles", "department_roles");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap updateRoleData(ParaMap inMap) throws Exception {
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.updateRoleData(inMap);
		return out;
	}
	
	public ParaMap deleteRoleData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		RoleDao roleDao = new RoleDao();
		ParaMap out = roleDao.deleteRoleData(moduleId, dataSetNo, id);
		return out;
	}
}

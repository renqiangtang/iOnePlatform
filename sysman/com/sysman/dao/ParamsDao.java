package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 系统参数管理Dao
 * huafc
 * 2012-05-09
 */
public class ParamsDao extends BaseDao {
	public static final String systemParamsRootId;
	public static final String userParamsRootId;
	public static final String systemParamsRootNo = "SYSTEM_PARAMS_ROOT";
	public static final String userParamsRootNo = "USER_PARAMS_ROOT";
	
	static {
		String strSystemParamsRootId = null;
		String strUserParamsRootId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap paramData = new ParaMap();
		try {
			paramData.put("no", systemParamsRootNo);
			paramData = dataSetDao.querySimpleData("sys_params", paramData);
			if (paramData.getInt("state") == 1 && paramData.getInt("state") > 0) {
				strSystemParamsRootId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData.clear();
				paramData.put("id", "");
				paramData.put("no", systemParamsRootNo);
				paramData.put("name", "系统参数");
				paramData.put("remark", "所有系统参数的根节点");
				paramData = dataSetDao.updateData("sys_params", "id", paramData);
				if (paramData.getInt("state") == 1) {
					strSystemParamsRootId = paramData.getString("id");
				}
			}
			paramData.clear();
			paramData.put("no", userParamsRootNo);
			paramData = dataSetDao.querySimpleData("sys_params", paramData);
			if (paramData.getInt("state") == 1 && paramData.getInt("state") > 0) {
				strUserParamsRootId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData.clear();
				paramData.put("id", "");
				paramData.put("no", userParamsRootNo);
				paramData.put("name", "用户参数");
				paramData.put("remark", "所有用户参数的根节点");
				paramData = dataSetDao.updateData("sys_params", "id", paramData);
				if (paramData.getInt("state") == 1) {
					strUserParamsRootId = paramData.getString("id");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		systemParamsRootId = strSystemParamsRootId;
		userParamsRootId = strUserParamsRootId;
	}
	
	//返回parentId下一级子节点记录
	public ParaMap getParamsFlatTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_params_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryParamsFlatTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//返回指定根节点的递归树记录
	public ParaMap getParamsRecurseTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_params_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryParamsRecurseTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询系统参数数据(按id)
	public ParaMap getParamsData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.querySimpleData("sys_params", keyData);
	}
	
	//查询系统参数数据(按no)
	public ParaMap getParamsDataByNo(String no, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", no);
		if (sqlParams != null && sqlParams.size() > 0) {
			keyData.putAll(sqlParams);
		}
		return dataSetDao.querySimpleData("sys_params", keyData, "turn");
	}
	
	public ParaMap getParamsDataByNo(String no) throws Exception {
		return getParamsDataByNo(no, null);
	}
	
	//查询系统参数数据(按父id仅返回下级)
	public ParaMap getParamsDataByParentId(String parentId, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("parent_id", parentId);
		if (sqlParams != null && sqlParams.size() > 0) {
			keyData.putAll(sqlParams);
		}
		return dataSetDao.querySimpleData("sys_params", keyData, "turn");
	}
	
	public ParaMap getParamsDataByParentId(String parentId) throws Exception {
		return getParamsDataByParentId(parentId, null);
	}
	
	//查询系统参数数据(按父编号仅返回下级)
	public ParaMap getParamsDataByParentNo(String parentNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", parentNo);
		ParaMap result = dataSetDao.querySimpleData("sys_params", keyData);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") >= 1) {
			result = getParamsDataByParentId(String.valueOf(result.getRecordValue(0, "id")), sqlParams);
		}
		return result;
	}
	
	public ParaMap getParamsDataByParentNo(String parentNo) throws Exception {
		return getParamsDataByParentNo(parentNo, null);
	}
	
	//查询系统参数数据(按父id递归返回所有下级)
	public ParaMap getParamsRecurseDataByParentId(String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_params_manager");
		sqlParams.put("dataSetNo", "queryRecurseParamsData");
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询系统参数数据(按父编号递归返回所有下级)
	public ParaMap getParamsRecurseDataByParentNo(String parentNo) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", parentNo);
		ParaMap result = dataSetDao.querySimpleData("sys_params", keyData);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") >= 1) {
			result = getParamsRecurseDataByParentId(String.valueOf(result.getRecordValue(0, "id")));
		}
		return result;
	}
	
	//更新系统参数
	public ParaMap updateParamsData(ParaMap paramsData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		return dataSetDao.updateData("sys_params", "id", paramsData, format);
	}
	
	public ParaMap updateParamsData(String checkFieldName, String id, String no, String name, String parentId,
			String lValue, String mValue, String hValue, String remark,
			Integer turn, Integer hidden) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap paramData = new ParaMap();
		paramData.put("id", id);
		paramData.put("no", no);
		if (!StrUtils.isNull(name))
			paramData.put("name", name);
		if (!StrUtils.isNull(parentId))
			paramData.put("parent_id", parentId);
		if (!StrUtils.isNull(lValue))
			paramData.put("lvalue", lValue);
		if (!StrUtils.isNull(mValue))
			paramData.put("mvalue", mValue);
		if (!StrUtils.isNull(hValue))
			paramData.put("hvalue", hValue);
		if (!StrUtils.isNull(remark))
			paramData.put("remark", remark);
		if (turn != null)
			paramData.put("turn", turn);
		if (hidden != null)
			paramData.put("hidden", hidden);
		String strCheckFieldName = StrUtils.isNull(checkFieldName) ? "no" : checkFieldName;
		return dataSetDao.updateData("sys_params", "id", strCheckFieldName, paramData);
	}
	
	//删除系统参数数据
	public ParaMap deleteParamsData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.deleteSimpleData("sys_params", keyData);
	}
}

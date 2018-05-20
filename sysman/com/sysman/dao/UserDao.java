package com.sysman.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.jasper.tagplugins.jstl.core.Param;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.tradeland.engine.EngineDao;
import com.base.utils.CommonUtils;
import com.base.utils.IDGenerator;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.tradeland.dao.TradeDao;
import com.trademan.dao.LicenseManageDao;
import com.base.utils.StrUtils;
import com.ctc.wstx.util.StringUtil;

/**
 * 用户管理Dao
 * huafc
 * 2012-04-20
 */
public class UserDao extends BaseDao {
	private static String bidder_role_id = null;
	private static String user_ref_table_names = "0=sys_emp;1=trans_bidder;2=trans_bank";
	
	public ParaMap getLoginUser(String userName, String password, int bidderType,ParaMap customConditions) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "queryLoginUserData");
		sqlParams.put("user_name", userName);
		sqlParams.put("password", password);
		if (bidderType == 0 || bidderType == 1 || bidderType == 2 || bidderType == 3)
			sqlParams.put("bidder_type", bidderType);
		
		if (customConditions!=null){
			sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		}
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getLoginUser(String userName, String password) throws Exception {
		return getLoginUser(userName, password, -1,null);
	}
	
	public ParaMap getLoginUser(String cakey, int bidderType) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "queryLoginUserDataByCakey");
		sqlParams.put("cakey", cakey);
		if (bidderType == 0 || bidderType == 1 || bidderType == 2 || bidderType == 3)
			sqlParams.put("bidder_type", bidderType);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getLoginUser(String cakey) throws Exception {
		return getLoginUser(cakey, -1);
	}
	
	public ParaMap getUserListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	public ParaMap getUserData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getUserDataByUsername(String moduleId, String dataSetNo, String userName) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserDataByUsername");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("user_name", userName);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getUserDataByCakey(String moduleId, String dataSetNo, String cakey) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserDataByCakey");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("cakey", cakey);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getUserDataByRefid(String moduleId, String dataSetNo, String refid) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserDataByRefid");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("ref_id", refid);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getEmpData(String moduleId, String dataSetNo, String id) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryEmpDataByUserId");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getBidderData(String moduleId, String dataSetNo, String id) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderDataByUserId");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateEmpOrgan(ParaMap inMap) throws Exception{
		 DataSetDao dataSetDao=new DataSetDao();
		 ParaMap sqlMap=new ParaMap();
		 sqlMap.put("organ_id",inMap.getString("organ_id"));
		 sqlMap.put("id",inMap.getString("id"));
		 return dataSetDao.updateData("sys_emp", "id", sqlMap);
	}
	
	public ParaMap getBankData(String moduleId, String dataSetNo, String id) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBankDataByUserId");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	//查询用户所属角色数据
	public ParaMap getUserRoleData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserRoleData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateLoginUserData(ParaMap userData)  throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("sys_user", "id", userData, null);
		if (result.getInt("state") == 1)
			result.put("message", "修改用户信息成功");
		return result;
	}
	
	public ParaMap updateUserData(ParaMap userData)  throws Exception{
		ParaMap result = null;
		//检查用户名
		ParaMap checkUserData = new ParaMap();
		String userName = userData.getString("user_name");
		if (!StrUtils.isNull(userName)) {
			checkUserData.put("id", userData.getString("id"));
			checkUserData.put("user_name", userName);
			result = checkUserExists(null, null, checkUserData);
			if (result.getInt("state") == 1) {
				if (result.getRecordInteger(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "用户名" + userData.getString("user_name") + "已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		//检查CAKEY
		String cakey = userData.getString("cakey");
		if (!StrUtils.isNull(cakey)) {
			checkUserData.clear();
			checkUserData.put("id", userData.getString("id"));
			checkUserData.put("cakey", cakey);
			result = checkUserExists(null, null, checkUserData);
			if (result.getInt("state") == 1) {
				if (result.getRecordInteger(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "CAKEY 已经被使用");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		result = dataSetDao.updateData("sys_user", "id", userData, null);
		if (result.getInt("state") == 1) {
			String id = userData.getString("id");
			if (StrUtils.isNull(id))
				id = result.getString("id");
			//保存CAKEY
			String cakeyId = userData.getString("cakey_id");
			if (userData.containsKey("cakey_id") || userData.containsKey("cakey")) {
				if (StrUtils.isNull(cakeyId) || !StrUtils.isNull(cakey)) {
					ParaMap data = new ParaMap();
					data.clear();
					if (StrUtils.isNull(cakeyId))
						data.put("id", "");
					else
						data.put("id", cakeyId);
					data.put("user_id", id);
					if (!StrUtils.isNull(cakey))
						data.put("key", cakey);
					result = dataSetDao.updateData("sys_cakey", "id", data, null);
					if (result.getInt("state") == 1)
						cakeyId = result.getString("id");
				}
			}
			//处理所属角色数据
			String addRoleIds = userData.getString("addRoleIds");
			if (!StrUtils.isNull(addRoleIds)) {
				List updateEmpRoles = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(addRoleIds, ";");
		        while(st.hasMoreTokens()) {
		        	String roleId = st.nextToken();
		        	if (!StrUtils.isNull(roleId)) {
		        		ParaMap data = new ParaMap();
						data.put("id", "");
						data.put("role_id", roleId);
						data.put("ref_type", 0);
						data.put("ref_id", id);
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
			String deleteRoleIds = userData.getString("deleteRoleIds");
			if (!StrUtils.isNull(deleteRoleIds)) {
				List updateUserRoles = new ArrayList();
				StringTokenizer st = null;
				st = new StringTokenizer(deleteRoleIds, ";");
				while(st.hasMoreTokens()) {
		        	String roleId = st.nextToken();
		        	if (!StrUtils.isNull(roleId)) {
		        		ParaMap data = new ParaMap();
						data.put("role_id", roleId);
						data.put("ref_type", 0);
						data.put("ref_id", id);
						ParaMap record = new ParaMap();
						record.put("tableName", "sys_role_rel");
						record.put("keyField", "role_id;ref_type;ref_id");
						record.put("data", data);
						updateUserRoles.add(record);
		        	}
		        }
		        if (updateUserRoles.size() > 0)
		        	result = dataSetDao.deleteSimpleData(updateUserRoles);
			}
			result.clear();
			result.put("state", 1);
			result.put("id", id);
			result.put("cakeyId", cakeyId);
		}
		return result;
	}
	
	/**
	 * 记录用户登录信息
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateUserLoginInfo(String id) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(id)) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "用户信息无效");
			return result;
		}
		ParaMap userData = getUserData(null, null, id);
		if (userData == null || userData.getInt("state") != 1 || userData.getRecordCount() <= 0) {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "用户信息不存在");
			return result;
		}
		//记录登录次数及最后登录时间
		ParaMap userLoginInfo = getUserLoginInfo(id);
		int loginTimes = userLoginInfo != null && userLoginInfo.getInt("state") == 1 ? userLoginInfo.getInt("loginTimes") : 0;
		String lastLoginTime = userLoginInfo != null && userLoginInfo.getInt("state") == 1 ? userLoginInfo.getString("loginTime") : null;
		Date loginTime = DataSetDao.getDBServerDate();
		ExtendDao extendDao = new ExtendDao();
		ParaMap loginData = new ParaMap();
		loginData.put("field_value", loginTimes + 1);
		loginData.put("extend1", StrUtils.dateToString(loginTime));//本次登录时间
		loginData.put("extend2", lastLoginTime);//上次登录时间
		ParaMap data = new ParaMap();
		data.put("userLogin", loginData);
		result = extendDao.updateExtendData("sys_user", id, data);
		if (result != null && result.getInt("state") == 1) {
			result.put("loginTimes", loginTimes + 1);
			result.put("lastLoginTime", loginTime);
		}
		//记录登录日志
		OperateLogDao.writeInfo(
			CommonUtils.decode(new Object[] {1, "竞买人", 2, "银行工作人员", ""}) + "用户" + userData.getRecordString(0, "display_name") + "("
					+ userData.getRecordString(0, "user_name") + ")登录系统。", "login", "sys_user", "id",
					userData.getRecordString(0, "id"));
		return result;
	}
	
	/**
	 * 读取用户登录信息
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap getUserLoginInfo(String id) throws Exception {
		ParaMap result = null;
		int loginTimes = 0;
		Date loginTime = null;
		Date lastLoginTime = null;
		ExtendDao extendDao = new ExtendDao();
		List<String> extendFieldNos = new ArrayList();
		extendFieldNos.add("userLogin");
		ParaMap extendData = extendDao.getExtendData("sys_user", id, extendFieldNos);
		if (extendData != null && extendData.getInt("state") == 1 && extendData.getRecordCount() > 0) {
			loginTimes = extendData.getRecordInt(0, "field_value");
			loginTime = extendData.getRecordDateTime(0, "extend1");
			lastLoginTime = extendData.getRecordDateTime(0, "extend2");
			result = new ParaMap();
			result.put("state", 1);
			result.put("loginTimes", loginTimes);
			result.put("loginTime", StrUtils.dateToString(loginTime));
			result.put("lastLoginTime", StrUtils.dateToString(lastLoginTime));
			return result;
		} else
			return extendData;
	}
	
	public ParaMap deleteUserData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteUserData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	public ParaMap updateEmpData(ParaMap empData)  throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("sys_emp", "", "id", empData);
		return result;
	}
	
	public ParaMap updateBidderData(ParaMap bidderData) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_bidder", "", "id", bidderData);
		return result;
	}
	
	public ParaMap updateBankData(ParaMap bankData) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_bank", "", "id", bankData);
		return result;
	}
	
	public ParaMap checkUserExists(String moduleId, String dataSetNo, ParaMap userData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		if (userData.containsKey("id")) {
			String id = userData.getString("id");
			if (StrUtils.isNotNull(id)) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (userData.containsKey("user_name")) {
			String userName = userData.getString("user_name");
			if (StrUtils.isNotNull(userName)) {
				sqlParams.put("user_name", userName);
				customCondition.append(" and user_name = :user_name");
			}
		}
		if (userData.containsKey("cakey")) {
			String userCakey = userData.getString("cakey");
			if (StrUtils.isNotNull(userCakey)) {
				sqlParams.put("cakey", userCakey);
				customCondition.append(" and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey)");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public String getUserDefaultRoleId(String id) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		String defaultRoleId = "";
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		ParaMap queryData = dataSetDao.querySimpleData("sys_user", keyData);
		if (queryData.getInt("state") == 1) {
			if (queryData.getInt("totalRowCount") > 0) {
				defaultRoleId = String.valueOf(queryData.getRecordValue(0, "default_role_id"));
				keyData.clear();
				keyData.put("ref_id", id);
				keyData.put("ref_type", 0);
				queryData = dataSetDao.querySimpleData("sys_role_rel", keyData);
				if (queryData.getInt("state") == 1) {
					if (queryData.getInt("totalRowCount") > 0) {
						String firstRoleId = "";
						boolean defaultRoleIdExists = false;
						for(int i = 0; i < queryData.getRecords().size(); i++) {
							String roleId = String.valueOf(queryData.getRecordValue(i, "role_id"));
							if (i == 0) {
								firstRoleId = roleId;
								if (StrUtils.isNull(defaultRoleId)) {
									break;
								}
							}
							if (roleId.equalsIgnoreCase(defaultRoleId)) {
								defaultRoleIdExists = true;
								break;
							}
						}
						if (!defaultRoleIdExists) {
							defaultRoleId = firstRoleId;
						}
					} else {
						defaultRoleId = "";
					}
				}
			} else {
				defaultRoleId = "";
			}
		}
		return defaultRoleId;
	}
	
	public ParaMap saveUserDefaultRoleId(String id, String roleId) throws Exception {
		if (StrUtils.isNull(id) || StrUtils.isNull(roleId)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "无有效" + (StrUtils.isNull(id) ? "用户" : "角色") + "信息");
			return result;
		}
		ParaMap userData = new ParaMap();
		userData.put("id", id);
		userData.put("default_role_id", roleId);
		ParaMap result = updateUserData(userData);
		return result;
	}
	
	public ParaMap saveUserDefaultRoleNo(String id, String roleNo) throws Exception {
		if (StrUtils.isNull(id) || StrUtils.isNull(roleNo)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "无有效" + (StrUtils.isNull(id) ? "用户" : "角色") + "信息");
			return result;
		}
		ParaMap roleData = new ParaMap();
		roleData.put("no", roleNo);
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		roleData = dataSetDao.querySimpleData("sys_role", roleData);
		if (roleData != null || roleData.getInt("state") == 1) {
			if (roleData.getRecordCount() > 0)
				result = saveUserDefaultRoleId(id, roleData.getRecordString(0, "id"));
			else {
				result = new ParaMap();
				result.put("state", 0);
				result.put("message", "角色（编号：" + roleNo + "）不存在");
			}
		} else {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "查询角色（编号：" + roleNo + "）数据失败");
		}
		return result;
	}
	
	/**
	 * 首页显示的角色列表，将默认角色显示为父角色，形成树形菜单
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap getUserGrantRoleList(String moduleId, String dataSetNo, String id) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryUserGrantRoleList");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("default_role_id", getUserDefaultRoleId(id));
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getUserDefaultModule(String moduleId, String dataSetNo, String id) throws Exception{
		String defaultRoleId = getUserDefaultRoleId(id);
		if (StrUtils.isNull(defaultRoleId)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "指定人员未找到缺省角色信息");
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", defaultRoleId);
		ParaMap result = dataSetDao.querySimpleData("sys_role", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") > 0) {
				boolean blnMainDefaultModule = false;
				String mainMenuId = String.valueOf(result.getRecordValue(0, "main_menu_id"));
				String mainModuleId = String.valueOf(result.getRecordValue(0, "main_module_id"));
				if (!StrUtils.isNull(mainMenuId)) {
					keyData.clear();
					keyData.put("id", mainMenuId);
					result = dataSetDao.querySimpleData("sys_menu", keyData);
					if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
						String menuModuleId = String.valueOf(result.getRecordValue(0, "module_id"));
						String menuModuleParams = String.valueOf(result.getRecordValue(0, "module_params"));
						if (!StrUtils.isNull(menuModuleId)) {
							keyData.clear();
							keyData.put("id", menuModuleId);
							result = dataSetDao.querySimpleData("sys_module", keyData);
							if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
								String menuModuleUrl = String.valueOf(result.getRecordValue(0, "class_name"));
								//将菜单参数附加到模块链接后面
								if (!StrUtils.isNull(menuModuleUrl)) {
									result.clear();
									result.put("state", 1);
									result.put("userId", id);
									result.put("defaultRoleId", defaultRoleId);
									result.put("defaultMenuId", mainMenuId);
									result.put("defaultModuleId", mainModuleId);
									result.put("moduleId", menuModuleId);
									if (!StrUtils.isNull(menuModuleParams)) {
										if (menuModuleUrl.indexOf('?') >= 0) {
											Map moduleParamsMap = StrUtils.getParams(menuModuleParams, false, "=", "&");
											Iterator it = moduleParamsMap.keySet().iterator();
											while(it.hasNext()){
												String key = it.next().toString();
												menuModuleUrl = StrUtils.setParamValue(menuModuleUrl, key, String.valueOf(moduleParamsMap.get(key)), false, "=", "&");  
											}
										} else {
											menuModuleUrl+= "?" + menuModuleParams;
										}
									}
									result.put("url", menuModuleUrl);
									blnMainDefaultModule = true;
								}
							}
						}
					}
				}
				if (!blnMainDefaultModule) {
					if (StrUtils.isNull(mainModuleId)) {
						result.clear();
						result.put("state", 1);
						result.put("userId", id);
						result.put("defaultRoleId", defaultRoleId);
						result.put("defaultMenuId", "");
						result.put("defaultModuleId", "");
						result.put("moduleId", "");
						result.put("url", "");
					} else {
						keyData.clear();
						keyData.put("id", mainModuleId);
						result = dataSetDao.querySimpleData("sys_module", keyData);
						if (result.getInt("state") == 1) {
							if (result.getInt("totalRowCount") > 0) {
								String moduleUrl = String.valueOf(result.getRecordValue(0, "class_name"));
								result.clear();
								result.put("state", 1);
								result.put("userId", id);
								result.put("defaultRoleId", defaultRoleId);
								result.put("defaultMenuId", "");
								result.put("defaultModuleId", mainModuleId);
								result.put("moduleId", mainModuleId);
								result.put("url", moduleUrl);
								blnMainDefaultModule = true;
							} else {
								result.clear();
								result.put("state", 0);
								result.put("message", "指定人员缺省主界面信息无效");
							}
						}
					}
				}
			} else {
				result.clear();
				result.put("state", 0);
				result.put("message", "指定人员缺省角色信息无效");
			}
		}
		return result;
	}
	
	/**
	 * 创建测试的竞买人用户，同时添加所有还未交易标的竞买申请，如果无标的可用则新增标的并申请。仅测试系统使用
	 * @param userName 用户名（同时也是竞买人名称）
	 * @param password 密码
	 * @return state竞买人存在且密码正确返回1，密码错误返回0。不存在则创建后返回1。非竞买人用户返回2
	 * @throws Exception
	 */
	public ParaMap registerTestBidder(String userName, String password) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		boolean blnNewBidder = false;
		ParaMap data = new ParaMap();
		data.put("user_name", userName);
		data = dataSetDao.querySimpleData("sys_user", data);
		if (data.getInt("state") == 1) {
			String bidderId = null;
			String userId = null;
			//创建竞买人及用户
			if (data.getInt("totalRowCount") == 0) {
				blnNewBidder = true;
				String bidderName = userName;
				//检查用户能否作为竞买人名称
				data.clear();
				data.put("name", bidderName);
				data = dataSetDao.querySimpleRowCount("trans_bidder", data);
				if (data.getInt("state") == 1 && data.getInt("rowCount") > 0)
					bidderName += "_" + IDGenerator.newGUID();
				data.clear();
				data.put("id", null);
				data.put("name", bidderName);
				data.put("bidder_type", 0);
				data.put("canton_id", CantonDao.getSystemCantonId(false));
				data.put("is_valid", 1);
				data.put("status", 1);
				data.put("remark", "<demo auto create>");
				data = dataSetDao.updateData("trans_bidder", "id", data);
				if (data.getInt("state") == 1)
					bidderId = data.getString("id");
				else {
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "创建临时竞买人信息失败");
					return result;
				}
				data.clear();
				data.put("id", null);
				data.put("user_name", userName);
				data.put("password", password);
				data.put("user_type", 1);
				data.put("ref_table_name", "trans_bidder");
				data.put("ref_id", bidderId);
				data.put("is_valid", 1);
				data.put("status", 1);
				data.put("remark", "<demo auto create>");
				data = dataSetDao.updateData("sys_user", "id", data);
				if (data.getInt("state") == 1)
					userId = data.getString("id");
				else {
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "创建临时竞买人用户信息失败");
					return result;
				}
				if (StrUtils.isNull(bidder_role_id)) {
					data.clear();
					data.put("no", "bidder");
					data = dataSetDao.querySimpleData("sys_role", data);
					if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
						bidder_role_id = String.valueOf(data.getRecordValue(0, "id"));
					} else {
						throw new Exception("竞买人角色不存在，无法自动添加临时竞买人及用户");
					}
				}
				data.clear();
				data.put("id", null);
				data.put("role_id", bidder_role_id);
				data.put("ref_type", 0);
				data.put("ref_id", userId);
				data.put("remark", "<demo auto create>");
				data = dataSetDao.updateData("sys_role_rel", "id", data);
				if (data.getInt("state") != 1) {
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "创建临时竞买人与竞买人角色关联信息失败");
					return result;
				}
			} else {
				if (!String.valueOf(data.getRecordValue(0, "password")).equals(password)) {
					//验证密码是否正确
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "密码不正确");
					return result;
				} else if (!String.valueOf(data.getRecordValue(0, "user_type")).equals("1")) {
					//如果已经存在的用户，但不是竞买人用户
					ParaMap result = new ParaMap();
					result.put("state", 2);
					result.put("message", "非竞买人用户");
					return result;
				}
				userId = String.valueOf(data.getRecordValue(0, "id"));
				bidderId = String.valueOf(data.getRecordValue(0, "ref_id"));
			}
			//创建竞买人有效标的的竞买申请
			LicenseManageDao licenseDao = new LicenseManageDao();
			ParaMap format = new ParaMap();
			format.put("apply_date", "sysdate");
			List targetStatuses = new ArrayList();
			targetStatuses.add(3);
			targetStatuses.add(4);
			data.clear();
			data.put("status", targetStatuses);
			ParaMap targetData = dataSetDao.querySimpleData("trans_target", data, null, "id,status");
			if (targetData.getInt("state") == 1 && targetData.getInt("totalRowCount") > 0) {
				for(int i = 0; i < targetData.getRecordCount(); i++) {
					String targetId = String.valueOf(targetData.getRecordValue(i, 0));
					String targetStatus = String.valueOf(targetData.getRecordValue(i, 1));
					boolean blnExistsLicense = false;
					if (!blnNewBidder) {
						data.clear();
						data.put("bidder_id", bidderId);
						data.put("target_id", targetId);
						data = dataSetDao.querySimpleRowCount("trans_license", data);
						blnExistsLicense = data.getInt("state") == 1 && data.getInt("rowCount") > 0;
					}
					if (blnNewBidder || !blnExistsLicense) {
						ParaMap licenseData = new ParaMap();
						licenseData.put("id", null);
						licenseData.put("bidder_id", bidderId);
						licenseData.put("target_id", targetId);
						licenseData.put("license_no", licenseDao.getNewLicenseNo(null, null, targetId).getString("no"));
						licenseData.put("card_no", licenseDao.getNewCardNo(null, null, targetId).getString("no"));
						licenseData.put("apply_no", licenseDao.getNewApplyNo(null, null, targetId).getString("no"));
						licenseData.put("apply_date", null);
						licenseData.put("confirmed", 1);
						licenseData.put("earnest_money_pay", 1);
						licenseData.put("status", 1);
						licenseData.put("remark", "<demo auto create>");
						data = dataSetDao.updateData("trans_license", "id", licenseData, format);
						//if (data.getInt("state") == 1)
						//	licenseDao.setLicenseStatusPass(null, null, data.getString("id"));
						if (targetStatus.equals("4")) {//交易中的标的则强制添加竞买人
							EngineDao engineDao = new EngineDao();
							engineDao.addUserId(targetId, userId);
						}
					}
				}
			} else {//没有标的则添加一个标的
				TradeDao tradeDao = new TradeDao();
				String targetId = tradeDao.virtualTrade();
				ParaMap licenseData = new ParaMap();
				licenseData.put("id", null);
				licenseData.put("bidder_id", bidderId);
				licenseData.put("target_id", targetId);
				licenseData.put("license_no", licenseDao.getNewLicenseNo(null, null, targetId).getString("no"));
				licenseData.put("card_no", licenseDao.getNewCardNo(null, null, targetId).getString("no"));
				licenseData.put("apply_no", licenseDao.getNewApplyNo(null, null, targetId).getString("no"));
				licenseData.put("apply_date", null);
				licenseData.put("confirmed", 1);
				licenseData.put("earnest_money_pay", 1);
				licenseData.put("status", 1);
				licenseData.put("remark", "<demo auto create>");
				data = dataSetDao.updateData("trans_license", "id", licenseData, format);
				//if (data.getInt("state") == 1)
				//	licenseDao.setLicenseStatusPass(null, null, data.getString("id"));
			}
			ParaMap result = new ParaMap();
			result.put("state", 1);
			return result;
		} else {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "查询用户信息失败");
			return result;
		}
	}
	
	/**
	 * 创建测试的竞买人用户
	 * @param password 竞买人登录输入的密码，无用途，仅作一次登录使用
	 * @return
	 * @throws Exception
	 */
	public ParaMap demoRegisterBidder1(String userName , String password , String goodsType) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		String bidderId = null;
		String userId = null;
		//判断用户名是否重复
		data.clear();
		data.put("user_name", userName);
		List userList= dataSetDao.querySimpleData("sys_user", data).getListObj();
		if(userList!=null && userList.size()>0){
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "注册失败！用户名已存在。");
			return result;
		}
		//创建竞买人及用户
		String bidderName = "演示_" + userName;
		data.clear();
		data.put("id", null);
		data.put("name", bidderName);
		int bidder_type = 0;
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.indexOf("101")>=0){
				bidder_type = 0;
			}else if(goodsType.indexOf("301")>=0 || goodsType.indexOf("401")>=0 ){
				bidder_type = 2;
			}else if(goodsType.indexOf("501")>=0){
				bidder_type = 3;
			}
		}
		data.put("bidder_type", bidder_type);
		data.put("canton_id", CantonDao.getSystemCantonId(false));
		data.put("is_valid", 1);
		data.put("status", 1);
		data.put("remark", "<demo auto create>");
		data = dataSetDao.updateData("trans_bidder", "id", data);
		if (data.getInt("state") == 1)
			bidderId = data.getString("id");
		else {
			throw new Exception("创建临时竞买人信息失败");
		}
		data.clear();
		data.put("id", null);
		data.put("user_name", userName);
		data.put("password", password);
		data.put("user_type", 1);
		data.put("ref_table_name", "trans_bidder");
		data.put("ref_id", bidderId);
		data.put("is_valid", 1);
		data.put("status", 1);
		data.put("remark", "<demo auto create>");
		data = dataSetDao.updateData("sys_user", "id", data);
		if (data.getInt("state") == 1)
			userId = data.getString("id");
		else {
			throw new Exception("创建临时竞买人用户信息失败");
		}
		if(StrUtils.isNotNull(goodsType)){
			String bidderRoleId = null;
			String roleNo = "bidder";
			if(goodsType.indexOf("101")>=0){
				roleNo = "td_bidder";
			}else if(goodsType.indexOf("301")>=0 || goodsType.indexOf("401")>=0 ){
				roleNo = "kc_bidder";
			}else if(goodsType.indexOf("501")>=0){
				roleNo = "gz_bidder";
			}
			data.clear();
			data.put("no", roleNo);
			data = dataSetDao.querySimpleData("sys_role", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				bidderRoleId = String.valueOf(data.getRecordValue(0, "id"));
			} else {
				throw new Exception("竞买人角色不存在，无法自动添加临时竞买人及用户");
			}
			
			data.clear();
			data.put("id", null);
			data.put("role_id", bidderRoleId);
			data.put("ref_type", 0);
			data.put("ref_id", userId);
			data.put("remark", "<demo auto create>");
			data = dataSetDao.updateData("sys_role_rel", "id", data);
			if (data.getInt("state") != 1) {
				throw new Exception("创建临时竞买人与竞买人角色关联信息失败");
			}
		}else{
			List list = new ArrayList();
			list.add("td_bidder");
			list.add("kc_bidder");
			list.add("gz_bidder");
			data.clear();
			data.put("no", list);
			data = dataSetDao.querySimpleData("sys_role", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				for(int i = 0 ; i < data.getInt("totalRowCount") ; i ++ ){
					String  bidderRoleId = data.getRecordString(i, "id");
					
					ParaMap keySet = new ParaMap();
					keySet.put("id", null);
					keySet.put("role_id", bidderRoleId);
					keySet.put("ref_type", 0);
					keySet.put("ref_id", userId);
					keySet.put("remark", "<demo auto create>");
					keySet = dataSetDao.updateData("sys_role_rel", "id", keySet);
					if (keySet.getInt("state") != 1) {
						throw new Exception("创建临时竞买人与竞买人角色关联信息失败");
					}
				}
			}
		}
		DataSourceManager.commit();
		ParaMap result = new ParaMap();
		result.put("userName", bidderName);
		result.put("state", 1);
		return result;
		
	}
	
	/**
	 * 获取用户关联数据ID
	 * @param id 用户ID，sys_user.id
	 * @return
	 * @throws Exception
	 */
	public ParaMap getUserRefData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = new ParaMap();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		ParaMap userData = dataSetDao.querySimpleData("sys_user", keyData, null, "id, user_name, user_type, ref_id");
		if (userData.getInt("state") == 1 && userData.getInt("totalRowCount") > 0) {
			int userType = userData.getRecordInteger(0, "user_type");
			String refTableName = StrUtils.getParamValue(user_ref_table_names, String.valueOf(userType));
			if (StrUtils.isNull(refTableName)) {
				result.put("state", 0);
				result.put("message", "不存在用户关联信息说明");
				result.put("user_name", userData.getRecordString(0, "user_name"));
				result.put("user_type", userType);
				result.put("ref_id", userData.getRecordString(0, "ref_id"));
			} else {
				keyData.clear();
				keyData.put("id", String.valueOf(userData.getRecordValue(0, "ref_id")));
				result = dataSetDao.querySimpleData(refTableName, keyData);
				if (result != null && result.getInt("state") == 1) {
					result.put("user_name", userData.getRecordString(0, "user_name"));
					result.put("user_type", userType);
					result.put("ref_id", userData.getRecordString(0, "ref_id"));
				}
			}
		} else {
			result.put("state", 0);
			result.put("message", "不存在用户");
		}
		return result;
	}
	
	/**
	 * 获取用户关联数据ID
	 * @param id 用户ID，sys_user.id
	 * @param userType  用户类别，sys_user.user_type。指定用户ID如果不是指定的用户类别返回空，如果不希望检查用户类型，请传入-1
	 * @return
	 * @throws Exception
	 */
	public String getUserRefId(String id, int userType) throws Exception {
		if (StrUtils.isNull(id))
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		if (userType >= 0)
			keyData.put("user_type", userType);
		ParaMap userData = dataSetDao.querySimpleData("sys_user", keyData);
		if (userData.getInt("state") == 1 && userData.getInt("totalRowCount") > 0)
			return String.valueOf(userData.getRecordValue(0, "ref_id"));
		else
			return null;
	}
	
	/**
	 * 获取用户关联数据ID
	 * @param id 用户ID，sys_user.id
	 * @return
	 * @throws Exception
	 */
	public String getUserRefId(String id) throws Exception {
		return getUserRefId(id, -1);
	}
	
	public ParaMap getUserOrganInfo(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "sys_user_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getUserOrganInfo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.queryData(sqlParams);
		ParaMap out = new ParaMap();
		if(result.getSize()>0){
			List filds = result.getFields();
			List record = (List)result.getRecords().get(0);
			if(filds != null && filds.size()>0 ){
				for(int i = 0 ; i < filds.size() ; i ++ ){
					out.put((String)filds.get(i), record.get(i));
				}
			}
		}
		//得到扩展信息
		sqlParams.clear();
		sqlParams.put("ref_table_name", "sys_organ");
		sqlParams.put("ref_id", out.getString("id"));
		ParaMap extend = dataSetDao.querySimpleData("sys_extend", sqlParams);
		if(extend.getSize()>0){
			for(int i = 0 ; i < extend.getSize() ; i ++){
				String key = (String)extend.getRecordValue(i, "field_no");
				String value = (String)extend.getRecordValue(i, "field_value");
				if(!key.equals("logo_Name") && !key.equals("head_left_Name")){
					out.put(key, value);
				}
			}
		}
		return out;
	}
	
	/**
	 * 获取指定用户允许操作的交易物类别ID
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List getUserAllowGoodsTypes(String id) throws Exception {
		if (StrUtils.isNull(id))
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		data.put("role_id", getUserDefaultRoleId(id));
		data = dataSetDao.querySimpleData("trans_role_goods_type_rel", data, "goods_type_id", "goods_type_id");
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			List goodsTypes = new ArrayList();
			for(int i = 0; i < data.getSize(); i++) {
				String goodsTypeId = data.getRecordString(i, "goods_type_id");
				goodsTypes.add(goodsTypeId);
			}
			return goodsTypes;
		} else
			return null;
	}
	
	/**
	 * 获取黑名单
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBlackUserListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "blackuser");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "getBlackUserList");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap updateBlackUserData(ParaMap userData)  throws Exception{
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		//检查用户名
		ParaMap checkUserData = new ParaMap();
		String no=userData.getString("no");
		String ban_type = userData.getString("ban_type");
		String remark=userData.getString("remark");
		String name=userData.getString("name");
		int bidder_type=userData.getInt("bidder_type");//竞买人类型  0个人，1企业
		if(StrUtils.isNull(remark.trim())){
			result.clear();
			result.put("state", 0);
			result.put("message", "原因不能为空！");
			return result;
		}else if(StrUtils.isNull(name)){
			result.clear();
			result.put("state", 0);
			result.put("message", "名称不能为空！");
			return result;
		}
		else{
			if(checkStringLength(remark,100)==false){
				result.clear();
				result.put("state", 0);
				result.put("message", "原因长度不能超过100！");
				return result;
			}
		}
		if(bidder_type==0&&!ban_type.equals("501")){
			if(StrUtils.isNull(no.trim()) || StrUtils.isNull(name.trim())){
				result.clear();
				result.put("state", 0);
				result.put("message", "竞买人类型为个人时，名称和证件号码不能为空！");
				return result;
			}else{
				if(checkStringLength(no,100)==false){
					result.clear();
					result.put("state", 0);
					result.put("message", "证件号码长度不能超过100！");
					return result;
				}
				//验证是否存在 黑名单中，即limit_type=0
				checkUserData.clear();
				checkUserData.put("name", name);
				checkUserData.put("no", no);
				checkUserData.put("limit_type", 0);
				checkUserData.put("bidder_type", 0);
				checkUserData.put("ban_type", userData.getString("ban_type"));
				ParaMap checkMap=dataSetDao.querySimpleData("trans_bidder_limit_dtl", checkUserData);
				if(checkMap.getInt("totalRowCount")>0){
					result.clear();
					result.put("state", 0);
					result.put("message", "名称"+name+"证件号码"+no+"已经存在黑名单中！");
					return result;
				}
				
				//验证是否存在非黑名单中，即limit_type=4
				checkUserData.clear();
				checkUserData.put("name", name);
				checkUserData.put("no", no);
				checkUserData.put("limit_type", 4);
				checkUserData.put("bidder_type", 0);
				checkUserData.put("ban_type", userData.getString("ban_type"));
				ParaMap checkMap1=dataSetDao.querySimpleData("trans_bidder_limit_dtl", checkUserData);
				if(checkMap1.getInt("totalRowCount")>0){
					List list=MakeJSONData.makeItems(checkMap1);
					ParaMap rm=(ParaMap)list.get(0);
					checkUserData.put("id", rm.getString("id"));
					checkUserData.put("limit_type", 0);
					result.clear();
					result=dataSetDao.updateData("trans_bidder_limit_dtl", "id", checkUserData);
					return result;
				}
			}
			
		}else if(bidder_type==1){
			if(StrUtils.isNull(name.trim())){
				result.clear();
				result.put("state", 0);
				result.put("message", "竞买人类型为企业时，名称不能为空！");
				return result;
			}else{
				if(checkStringLength(name,200)==false){
					result.clear();
					result.put("state", 0);
					result.put("message", "企业名称长度不能超过200！");
					return result;
				}
				if(StrUtils.isNull(no)&&!ban_type.equals("501")){
					result.clear();
					result.put("state", 0);
					result.put("message", "竞买人类型为企业时，组织机构代码不能为空！");
					return result;
				}else  if(StrUtils.isNull(no)&&ban_type.equals("501")){
					result.clear();
					result.put("state", 0);
					result.put("message", "竞买人类型为企业时，组织机构代码不能为空！");
					return result;
					
				}
				checkUserData.clear();
				checkUserData.put("name", name);
				checkUserData.put("limit_type", 0);
				checkUserData.put("bidder_type", 1);
				checkUserData.put("ban_type", userData.getString("ban_type"));
				ParaMap checkMap=dataSetDao.querySimpleData("trans_bidder_limit_dtl", checkUserData);
				if(checkMap.getInt("totalRowCount")>0){
					result.clear();
					result.put("state", 0);
					result.put("message", "企业"+name+"已经存在黑名单中！");
					return result;
				}
				//验证是否存在非黑名单中，即limit_type=4
				checkUserData.clear();
				checkUserData.put("name", name);
				checkUserData.put("limit_type", 4);
				checkUserData.put("bidder_type", 1);
				checkUserData.put("ban_type", userData.getString("ban_type"));
				ParaMap checkMap1=dataSetDao.querySimpleData("trans_bidder_limit_dtl", checkUserData);
				if(checkMap1.getInt("totalRowCount")>0){
					List list=MakeJSONData.makeItems(checkMap1);
					ParaMap rm=(ParaMap)list.get(0);
					checkUserData.put("id", rm.getString("id"));
					checkUserData.put("limit_type", 0);
					result.clear();
					result=dataSetDao.updateData("trans_bidder_limit_dtl", "id", checkUserData);
					return result;
				}
			}
		}
		
		userData.put("status", 2);
		result = dataSetDao.updateData("trans_bidder_limit_dtl", "id", userData);
		return result;
	}
	
	/**
	 * 加入白名单或黑名单
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap setBlackUserData(ParaMap pm) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		String remark=pm.getString("remark");
		ParaMap result=new ParaMap();
		if(StrUtils.isNull(remark.trim())){
			result.clear();
			result.put("state", 0);
			result.put("message", "原因不能为空！");
			return result;
		}else{
			if(checkStringLength(remark,100)==false){
				result.clear();
				result.put("state", 0);
				result.put("message", "原因长度不能超过100！");
				return result;
			}
		}
		pm.put("limit_type", pm.getInt("limittype"));
		return dataSetDao.updateData("trans_bidder_limit_dtl", "id", pm);
	}
	
	/**
	 * 判断字符串是否超过长度
	 * @param text  要判断的字符串
	 * @param len 长度
	 * @return
	 */
	private boolean checkStringLength(String text,int len){
		if(text.length()>len){
			return false;
		}else{
			return true;
		}
	}
}
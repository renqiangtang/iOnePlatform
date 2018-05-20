package com.sysman.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.base.web.TokenCheck;
import com.before.dao.TransGoodsMineDao;
import com.hnca.certtools.HttpRequestProxy;
import com.sysman.dao.ConfigDao;
import com.sysman.dao.FieldChangeDao;
import com.sysman.dao.OperateLogDao;
import com.sysman.dao.OrganDao;
import com.sysman.dao.ParamsDao;
import com.sysman.dao.UserDao;

public class UserService extends BaseService {

	/**
	 * 退出登录
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap logout(ParaMap inMap) throws Exception {
		this.getSession().removeAttribute("userInfo.userId");
		return new ParaMap();
	}

	// 登录验证调用
	public ParaMap getLoginUser(ParaMap inMap) throws Exception {
		ParaMap out = checkSecurityCode(inMap);
		if (out.getInt("state") != 1) {
			out.put("errorType", 0);
			return out;
		}
		ParaMap customConditions = new ParaMap();
		int bidderType = 0;
		String goodsTypeIds = inMap.getString("goodsTypeIds");// 101,201
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(goodsTypeIds)) {
			if (goodsTypeIds.equals("301") || goodsTypeIds.equals("401")){
				bidderType = 2;
				customCondition
				.append(" and (srno<>'recorder101' and srno<>'insider')  ");
			}
			else if (goodsTypeIds.equals("501")){
				customCondition
				.append(" and (srno<>'recorder101' and srno<>'recorder301')  ");
				bidderType = 3;
			}else if(goodsTypeIds.equals("101")){
				customCondition
				.append(" and (srno<>'recorder301' and srno<>'insider' and srno<>'notice_check')  ");
			}
		}
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		int intLoginByCakey = 0;
		UserDao userDao = new UserDao();
		if (inMap.containsKey("userName")) {
			out = userDao.getLoginUser(inMap.getString("userName"),
					inMap.getString("password"), bidderType,customConditions);
		} else {
			out = userDao.getLoginUser(inMap.getString("cakey"), bidderType);
			intLoginByCakey = 1;
		}
		if (out.getInt("state") == 1 && out.getInt("totalRowCount") >= 1) {
			String userId = out.getRecordString(0, "id");
			String userName = out.getRecordString(0, "user_name");
			out.put("userId", userId);
			out.put("userName", userName);
			out.put("loginByCakey", intLoginByCakey);
			// 返回登录用户人缺省角色信息
			String defaultRoleId = userDao.getUserDefaultRoleId(userId);
			ParaMap defaultRole = new ParaMap();
			defaultRole.put("id", defaultRoleId);
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", defaultRoleId);
			ParaMap defaultRoleData = dataSetDao.querySimpleData("sys_role",
					keyData);
			if (defaultRoleData.getInt("state") == 1
					&& defaultRoleData.getRecordCount() > 0) {
				defaultRole.put("no", defaultRoleData.getRecordString(0, "no"));
				defaultRole.put("name",
						defaultRoleData.getRecordString(0, "name"));
			}
			out.put("defaultRole", defaultRole);
			out.put("goodsTypeIds", inMap.getString("goodsTypeIds"));
			setUserSessionData(out);
			out.remove("loginByCakey");
			int userType = out.getRecordInteger(0, "user_type");
			out.put("userType", userType);
			out.put("refId", out.getRecordString(0, "ref_id"));
			String userTypeLabel;
			if (userType == 1) {
				userTypeLabel = "竞买人";
				ParaMap data = userDao.getUserRefData(out.getRecordString(0,
						"id"));
				int refStatus = -1;
				int refIsValid = -1;
				if (data.hasField("status")) {
					refStatus = StrUtils.strToInt(data.getRecordString(0,
							"status"));
					out.put("refStatus", data.getRecordString(0, "status"));
				}
				if (data.hasField("is_valid")) {
					refIsValid = StrUtils.strToInt(data.getRecordString(0,
							"is_valid"));
					out.put("refIsValid", data.getRecordString(0, "is_valid"));
				}
			} else if (userType == 2)
				userTypeLabel = "银行工作人员";
			else
				userTypeLabel = "";
			// 将所属的角色列表返回
			// ParaMap roleData = userDao.getUserRoleData(null, null, inMap);
			// if (roleData.getInt("state") == 1 && roleData.getRecordCount() >
			// 0) {
			// ParaMap roles = new ParaMap();
			// for(int i = 0; i < roleData.getRecordCount(); i++)
			// roles.put(roleData.getRecordString(i, "id"),
			// roleData.getRecordString(i, "name"));
			// out.put("roles", roles);
			// }
			// 判断是否指定角色登录指定系统，业务要求
			// String goodsTypeIds = inMap.getString("goodsTypeIds");//101,201
			// if (StrUtils.isNotNull(goodsTypeIds) && userType == 1 &&
			// !userName.equals("Admin")) {
			// String defaultRoleNo = defaultRole.getString("no");
			// if (StrUtils.isNull(defaultRoleNo)) {
			// out.put("state", 0);
			// out.put("message", "您可能申请的业务暂未审批通过，请等待审批或者补充注册信息。");
			// } else if (goodsTypeIds.equals("101")) {
			// if (!defaultRoleNo.equals("td_bidder")) {
			// out.put("state", 0);
			// out.put("message", "您不是土地竞买人，不允许登录土地交易系统。");
			// }
			// } else if (goodsTypeIds.equals("301") ||
			// goodsTypeIds.equals("401")) {
			// if (!defaultRoleNo.equals("kc_bidder")) {
			// out.put("state", 0);
			// out.put("message", "您不是矿产竞买人，不允许登录矿产交易系统。");
			// }
			// } else if (goodsTypeIds.equals("501")) {
			// if (!defaultRoleNo.equals("gz_bidder")) {
			// out.put("state", 0);
			// out.put("message", "您不是耕地指标竞买人，不允许登录耕地指标交易系统。");
			// }
			// } else {
			// out.put("state", 0);
			// out.put("message", "您注册的信息不被允许登录及使用系统。");
			// }
			// }
			if (out.getInt("state") == 1) {
				ParaMap loginInfo = userDao.updateUserLoginInfo(userId);
				if (loginInfo != null && loginInfo.getInt("state") == 1) {
					out.put("loginTimes", loginInfo.get("loginTimes"));
					out.put("lastLoginTime", loginInfo.get("lastLoginTime"));
				}
			} else
				out.put("errorType", 0);
		} else {
			// 登录失败
			out.put("state", 0);
			// 查询用户信息
			ParaMap data = null;
			if (inMap.containsKey("userName"))
				data = userDao.getUserDataByUsername(null, null,
						inMap.getString("userName"));
			else
				data = userDao.getUserDataByCakey(null, null,
						inMap.getString("cakey"));
			if (data == null || data.getInt("state") != 1) {
				out.put("errorType", 1);
				out.put("message", "检查用户信息失败");
			} else if (data.getRecordCount() == 0) {
				out.put("errorType", 2);
				out.put("message", "用户不存在");
			} else {
				int userType = data.getRecordInteger(0, "user_type");
				String refId = data.getRecordString(0, "ref_id");
				out.put("userType", userType);
				out.put("refId", refId);
				if (!StrUtils.equals(data.getRecordString(0, "password"),
						inMap.getString("password"))) {
					out.put("errorType", 3);
					out.put("message", "用户密码错误");
				} else if (data.getRecordInteger(0, "is_valid") != 1) {
					out.put("errorType", 4);
					out.put("message", "用户处于无效状态");
				} else {
					data = userDao
							.getUserRefData(data.getRecordString(0, "id"));
					if (data == null || data.getInt("state") != 1) {
						out.put("errorType", 5);
						out.put("message", "用户关联信息读取失败");
					} else if (data.getRecordCount() == 0) {
						out.put("errorType", 6);
						out.put("message", "用户关联信息不存在");
					} else {
						int refStatus = -1;
						int refIsValid = -1;
						if (data.hasField("status")) {
							refStatus = StrUtils.strToInt(data.getRecordString(
									0, "status"));
							out.put("refStatus",
									data.getRecordString(0, "status"));
						}
						if (data.hasField("is_valid")) {
							refIsValid = StrUtils.strToInt(data
									.getRecordString(0, "is_valid"));
							out.put("refIsValid",
									data.getRecordString(0, "is_valid"));
							if (refIsValid != 1) {
								out.put("errorType", 7);
								out.put("message", (userType == 1 ? "竞买人"
										: (userType == 1 ? "银行" : "内部人员"))
										+ "处于无效状态");
							}
						}
						if (!out.containsKey("errorType")) {
							if (userType == 1) {
								if (refStatus == 1) {
									out.put("errorType", 100);
									out.put("message", "未知错误");
								} else {
									out.put("errorType", 8);
									out.put("message",
											"竞买人处于"
													+ (refStatus == 2 ? "冻结"
															: (refStatus == 3 ? "待年审"
																	: (refStatus == 4 ? "注销"
																			: "申请中"))));
								}
							} else {
								out.put("errorType", 101);
								out.put("message", "未知错误");
							}
						}
					}
				}
			}

		}
		out.remove("fs");
		out.remove("rs");
		return out;
	}

	// 查询登录用户信息
	public ParaMap getLoginUserData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		UserDao userDao = new UserDao();
		ParaMap userParam = userDao.getUserData(null, null, userId);
		if (userParam.getRecords().size() > 0) {
			int userType = (Integer) userParam.getRecordValue(0, "user_type");
			String styleName = (String) userParam.getRecordValue(0,
					"style_name");
			out.put("userType", userType);
			out.put("styleName", styleName);
			if (userType == 0) {// 内部用户
				ParaMap emp = userDao.getEmpData(null, null, userId);
				out.put("emp", emp);
			} else if (userType == 1) {// 竞买人
				ParaMap bidder = userDao.getBidderData(null, null, userId);
				out.put("bidder", bidder);
			} else if (userType == 2) {// 银行工作人员
				ParaMap bank = userDao.getBankData(null, null, userId);
				out.put("bank", bank);
			}
		}
		return out;
	}

	/**
	 * 验证校验码
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap checkSecurityCode(ParaMap inMap) throws Exception {
		String oldCode = (String) this.getSession()
				.getAttribute("securityCode");
		String newCode = inMap.getString("newCode");
		ParaMap out = new ParaMap();
		if (!oldCode.equals(newCode)) {
			out.put("state", 0);
			out.put("message", "验证码错误");
		} else {
			out.put("state", 1);
		}
		return out;
	}

	public ParaMap updateLoginUserData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		int userType = inMap.getInt("userType");
		ParaMap param = inMap;
		param.remove("module");
		param.remove("service");
		param.remove("method");
		param.remove("userType");
		param.remove("token");
		param.remove("ms");
		UserDao userDao = new UserDao();
		if (userType == 0) {// 内部用户
			out = userDao.updateEmpData(param);
		} else if (userType == 1) {// 竞买人
			out = userDao.updateBidderData(param);
		} else if (userType == 2) {// 银行工作人员
			out = userDao.updateBankData(param);
		}
		return out;
	}

	public ParaMap changeStyle(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String styleName = inMap.getString("styleName");
		ParaMap paraMap = new ParaMap();
		paraMap.put("id", userId);
		paraMap.put("style_name", styleName);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = dataSetDao.updateData("sys_user", "id", paraMap, null);
		return out;
	}

	public String getStyle(String u) throws Exception {
		String style = null;
		ParaMap paraMap = new ParaMap();
		paraMap.put("id", u);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("sys_user", paraMap);
		if (result.getRecordCount() > 0) {
			style = (String) result.getRecordValue(0, "style_name");
		}
		return style;
	}

	public ParaMap getUserListData(ParaMap inMap) throws Exception {
		// 组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("")
				&& !sortField.equalsIgnoreCase("null")) {
			// 前端表格字段名和数据集字段名不一致
			if (sortField.equalsIgnoreCase("userName"))
				sortField = "user_name";
			else if (sortField.equalsIgnoreCase("userType"))
				sortField = "user_type";
			else if (sortField.equalsIgnoreCase("activeDate"))
				sortField = "active_date";
			else if (sortField.equalsIgnoreCase("validity"))
				sortField = "validity";
			else if (sortField.equalsIgnoreCase("status"))
				sortField = "status";
			else
				sortField = "";
			if (!sortField.equals("") && sortDir != null && !sortDir.equals("")
					&& !sortDir.equalsIgnoreCase("null"))
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// 自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("userName")) {
			String userName = inMap.getString("userName");
			if (userName != null && !userName.equals("")
					&& !userName.equalsIgnoreCase("null")) {
				sqlParams.put("user_name", userName);
				customCondition
						.append(" and u.user_name like '%' || :user_name || '%'");
			}
		}
		if (inMap.containsKey("userType")) {
			String userType = inMap.getString("userType");
			if (userType.equals("0") || userType.equals("1")
					|| userType.equals("2")) {
				sqlParams.put("user_type", Integer.parseInt(userType));
				customCondition.append(" and u.user_type = :user_type");
			}
		}
		if (inMap.containsKey("userStatus")) {
			String userStatus = inMap.getString("userStatus");
			if (userStatus.equals("0") || userStatus.equals("1")
					|| userStatus.equals("2")) {
				sqlParams.put("status", Integer.parseInt(userStatus));
				customCondition
						.append(" and u.is_valid = 1 and u.status = :status");
			} else if (userStatus.equals("-1"))
				customCondition
						.append(" and (u.is_valid is null or u.is_valid <> 1)");
		}
		if (inMap.containsKey("userHasCakey")) {
			String userHasCakey = inMap.getString("userHasCakey");
			if (userHasCakey.equals("1"))
				customCondition
						.append(" and exists(select * from sys_cakey where user_id = u.id and key is not null)");
			else if (userHasCakey.equals("0"))
				customCondition
						.append(" and (not exists(select * from sys_cakey where user_id = u.id)"
								+ "or exists(select * from sys_cakey where user_id = u.id and key is null))");
		}
		if (inMap.containsKey("userCakey")) {
			String userCakey = inMap.getString("userCakey");
			if (userCakey != null && !userCakey.equals("")
					&& !userCakey.equalsIgnoreCase("null")) {
				sqlParams.put("cakey", userCakey);
				customCondition
						.append(" and exists(select * from sys_cakey where user_id = u.id and key = :cakey)");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		// 查询数据
		UserDao userDao = new UserDao();
		ParaMap out = userDao.getUserListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("userName", "user_name");
		fields.put("userType", "user_type");
		fields.put("activeDate", "active_date");
		fields.put("validity", "validity");
		fields.put("refName", "ref_name");
		fields.put("refId", "ref_id");
		fields.put("organName", "organ_name");
		fields.put("relRoles", "user_roles");
		fields.put("relJobs", "emp_jobs");
		fields.put("relDepartments", "emp_departments");
		fields.put("status", "status");
		fields.put("isValid", "is_valid");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap getUserData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		UserDao userDao = new UserDao();
		ParaMap out = userDao.getUserData(moduleId, dataSetNo, id);
		return out;
	}

	public ParaMap getUserRoleData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		UserDao userDao = new UserDao();
		ParaMap out = userDao.getUserRoleData(moduleId, dataSetNo, inMap);
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
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

	public ParaMap updateUserData(ParaMap userData) throws Exception {
		UserDao userDao = new UserDao();
		ParaMap out = userDao.updateUserData(userData);
		return out;
	}

	public ParaMap deleteUserData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		UserDao userDao = new UserDao();
		ParaMap out = userDao.deleteUserData(moduleId, dataSetNo, id);
		return out;
	}

	public ParaMap saveUserDefaultRoleId(ParaMap userData) throws Exception {
		String userId = userData.getString("id");
		if (StrUtils.isNull(userId)) {
			userId = userData.getString("userId");
			if (StrUtils.isNull(userId)) {
				ParaMap out = new ParaMap();
				out.put("state", 0);
				out.put("message", "无有效用户信息");
				return out;
			}
		}
		UserDao userDao = new UserDao();
		ParaMap out = userDao.saveUserDefaultRoleId(userId,
				userData.getString("default_role_id"));
		// 执行成功将刷新主界面，重写一次session
		if (out.getInt("state") == 1) {
			ParaMap user = userDao.getUserData(null, null, userId);
			setUserSessionData(user);
		}
		return out;
	}

	public ParaMap getUserGrantRoleList(ParaMap inMap) throws Exception {
		UserDao userDao = new UserDao();
		String id = inMap.getString("id");
		if (id == null || id.equals("") || id.equalsIgnoreCase("null"))
			id = inMap.getString("userId");
		ParaMap out = userDao.getUserGrantRoleList(null, null, id);
		// 转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 1);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "menu_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("parent_id", "parent_id");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List menu = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("menu", menu);
		return out;
	}

	private void setUserSessionData(ParaMap userData) throws Exception {
		if (userData.getRecords().size() > 0) {
			String userId = String.valueOf(userData.getRecordValue(0, "id"));
			this.getSession().setAttribute("userInfo.userId", userId);
			this.getSession().setAttribute("userInfo.userName",
					userData.getRecordValue(0, "user_name"));
			this.getSession().setAttribute("userInfo.displayName",
					userData.getRecordValue(0, "display_name"));
			this.getSession().setAttribute("userInfo.caKeyType",
					userData.getRecordValue(0, "key_type"));
			this.getSession().setAttribute(
					"userInfo.caKey",
					userData.containsKey("loginByCakey")
							&& userData.getInt("loginByCakey") == 1 ? userData
							.getRecordValue(0, "key") : "");
			this.getSession().setAttribute("userInfo.organId",
					userData.getRecordValue(0, "organ_id"));
			this.getSession().setAttribute("userInfo.userType",
					userData.getRecordValue(0, "user_type"));
			this.getSession().setAttribute("userInfo.userStatus",
					userData.getRecordValue(0, "status"));
			this.getSession().setAttribute("userInfo.refId",
					userData.getRecordValue(0, "ref_id"));
			this.getSession().setAttribute("userInfo.refStatus",
					userData.getRecordValue(0, "ref_status"));
			this.getSession().setAttribute("userInfo.refIsValid",
					userData.getRecordValue(0, "ref_is_valid"));

			// 添加用户角色信息
			UserDao userDao = new UserDao();
			this.getSession().setAttribute("userInfo.goodsTypeIds",
					userDao.getUserAllowGoodsTypes(userId));
			String defaultRoleId = userDao.getUserDefaultRoleId(userId);
			ParaMap roleData = userDao.getUserGrantRoleList(null, null, userId);
			if (roleData.getInt("state") == 1 && roleData.getRecordCount() > 0) {
				List roles = null;
				for (int i = 0; i < roleData.getRecordCount(); i++) {
					ParaMap role = new ParaMap();
					role.put("id", roleData.getRecordString(i, "id"));
					role.put("no", roleData.getRecordString(i, "no"));
					role.put("name", roleData.getRecordString(i, "name"));
					if (StrUtils.isNotNull(defaultRoleId)
							&& defaultRoleId.equals(roleData.getRecordString(i,
									"id")))
						this.getSession().setAttribute("userInfo.defaultRole",
								role);
					if (roles == null)
						roles = new ArrayList();
					roles.add(role);
				}
				this.getSession().setAttribute("userInfo.roles", roles);
			}
		}
		String goodsTypeIds = userData.getString("goodsTypeIds");
		if (StrUtils.isNotNull(goodsTypeIds)) {
			List goodsTypes = null;
			if (goodsTypeIds.indexOf(",") > 0)
				goodsTypes = StrUtils.getSubStrs(goodsTypeIds, ",");
			else
				goodsTypes = StrUtils.getSubStrs(goodsTypeIds, ";");
			this.getSession().setAttribute("sysInfo.goodsTypeIds", goodsTypes);
		}
		String versionMode = AppConfig.getPro("versionMode");
		this.getSession().setAttribute("sysInfo.versionMode", versionMode);
		OrganDao organDao = new OrganDao();
		ParaMap defaultOrganData = organDao.getDefaultOrganData(null, null);
		if (defaultOrganData.getInt("state") == 1
				&& defaultOrganData.getInt("totalRowCount") > 0) {
			Map defaultOrgan = new HashMap();
			defaultOrgan.put("id", defaultOrganData.getRecordString(0, "id"));
			defaultOrgan.put("name",
					defaultOrganData.getRecordString(0, "name"));
			defaultOrgan.put("address",
					defaultOrganData.getRecordString(0, "address"));
			defaultOrgan.put("regCorporation",
					defaultOrganData.getRecordString(0, "reg_corporation"));
			this.getSession()
					.setAttribute("sysInfo.defaultOrgan", defaultOrgan);
		}
		ConfigDao configDao = new ConfigDao();
		ParaMap generalParamsData = configDao.getGeneralParams();
		if (generalParamsData.getInt("state") == 1
				&& generalParamsData.getInt("totalRowCount") > 0) {
			Map generalParams = new HashMap();
			for (int i = 0; i < generalParamsData.getRecordCount(); i++) {
				String paramNo = generalParamsData.getRecordString(i, "no");
				if (paramNo.equalsIgnoreCase("transSystemName")) {
					generalParams.put("systemName",
							generalParamsData.getRecordString(i, "lvalue"));
				} else if (paramNo.equalsIgnoreCase("transSystemShortName")) {
					generalParams.put("systemShortName",
							generalParamsData.getRecordString(i, "lvalue"));
				} else if (paramNo.equalsIgnoreCase("transCRTrustName")) {
					generalParams.put("CRTrustName",
							generalParamsData.getRecordString(i, "lvalue"));
				} else if (paramNo.equalsIgnoreCase("transCRTrustCorporation")) {
					generalParams.put("CRTrustCorporation",
							generalParamsData.getRecordString(i, "lvalue"));
				} else if (paramNo.equalsIgnoreCase("transCRTrustAddress")) {
					generalParams.put("CRTrustAddress",
							generalParamsData.getRecordString(i, "lvalue"));
				}
			}
			this.getSession().setAttribute("sysInfo.generalParams",
					generalParams);
		}
	}

	public ParaMap getUserDefaultModule(ParaMap inMap) throws Exception {
		UserDao userDao = new UserDao();
		String id = inMap.getString("id");
		if (id == null || id.equals("") || id.equalsIgnoreCase("null"))
			id = inMap.getString("userId");
		ParaMap out = userDao.getUserDefaultModule(null, null, id);
		return out;
	}

	public ParaMap getLoginUserAllData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("userId");
		out.put("defaultModule", getUserDefaultModule(inMap));
		MenuService menuService = new MenuService();
		out.put("mainMenu", menuService.getMenuRecurseTreeData(inMap));
		out.put("roleListMenu", getUserGrantRoleList(inMap));
		ParamsDao paramsDao = new ParamsDao();
		ParaMap paramsData = paramsDao
				.getParamsDataByNo("transSystemShortName");
		if (paramsData.getInt("state") == 1
				&& paramsData.getInt("totalRowCount") > 0) {
			out.put("systemShortName",
					String.valueOf(paramsData.getRecordValue(0, "lvalue")));
		}
		return out;
	}

	public ParaMap checkUserExists(ParaMap inMap) throws Exception {
		UserDao userDao = new UserDao();
		return userDao.checkUserExists(null, null, inMap);
	}

	/**
	 * DEMO版本竞买人测试注册
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap demoRegister(ParaMap inMap) throws Exception {
		String userName = inMap.getString("userName");
		String password = inMap.getString("password");
		if (StrUtils.isNull(userName) || StrUtils.isNull(password)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "注册失败！请输入有效的用户名和密码。");
			return result;
		}
		String goodsType = inMap.getString("goodsTypeIds");
		UserDao userDao = new UserDao();
		ParaMap result = userDao.demoRegisterBidder1(userName, password,
				goodsType);
		return result;
		// if (result.getInt("state") > 0) {
		// inMap.put("newCode", (String)
		// this.getSession().getAttribute("securityCode"));
		// return getLoginUser(inMap);
		// } else{
		// return result;
		// }
	}

	public ParaMap getUserOrganInfo(ParaMap inMap) throws Exception {
		UserDao userDao = new UserDao();
		String id = inMap.getString("id");
		if (StrUtils.isNull(id))
			id = inMap.getString("userId");
		ParaMap out = userDao.getUserOrganInfo(null, null, id);
		return out;
	}

	public ParaMap updateEmpOrgan(ParaMap inMap){
		UserDao userDao = new UserDao();
		ParaMap out=new ParaMap();
		try {
			out = userDao.updateEmpOrgan(inMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * 获取黑名单
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBlackUserListData(ParaMap inMap) throws Exception {
		// 组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String limit_type = inMap.getString("limit_type");
		String ban_type = inMap.getString("ban_type");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// 自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("username")) {
			String userName = inMap.getString("username");
			if (userName != null && !userName.equals("")
					&& !userName.equalsIgnoreCase("null")) {
				sqlParams.put("no", userName);
				sqlParams.put("name", userName);
				sqlParams.put("tbname", userName);
				customCondition
						.append(" and (tbld.no like '%' || :no || '%' or tbld.name like '%' || :name || '%' )");
			}
		}
		sqlParams.put("limit_type", limit_type);
		sqlParams.put("ban_type", ban_type);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		// 查询数据
		UserDao userDao = new UserDao();
		ParaMap out = userDao.getBlackUserListData(null, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 新增或修改黑名单
	 * 
	 * @param userData
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateBlackUserData(ParaMap userData) throws Exception {
		UserDao userDao = new UserDao();
		ParaMap out = userDao.updateBlackUserData(userData);
		if (out.getInt("state") == 1) {
			ParaMap change = new ParaMap();
			change.put("ref_table_name", "trans_bidder_limit_dtl");
			change.put("ref_id", out.getString("id"));
			change.put("field_name", "is_valid");
			change.put("old_value", "移出黑名单");
			change.put("new_value", "加入黑名单");
			change.put("change_cause", userData.getString("remark"));
			change.put("change_user_id", userData.getString("create_user_id"));
			FieldChangeDao fcDao = new FieldChangeDao();
			fcDao.updateFieldChange(change);
		} else {
			out.put("state", 0);
			out.put("message", out.getString("message"));
			return out;
		}
		return out;
	}

	/**
	 * 移出黑名单或黑名单
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap setBlackUserData(ParaMap inMap) throws Exception {
		UserDao userDao = new UserDao();
		ParaMap out = userDao.setBlackUserData(inMap);
		String message = "";
		if (out.getInt("state") == 1) {
			ParaMap change = new ParaMap();
			change.put("ref_table_name", "trans_bidder_limit_dtl");
			change.put("ref_id", inMap.getString("id"));
			change.put("field_name", "is_valid");
			if (inMap.getInt("limittype") == 0) {
				change.put("old_value", "移出黑名单");
				change.put("new_value", "加入黑名单");
				message = "加入黑名单成功！";
			} else if (inMap.getInt("limittype") == 4) {
				change.put("old_value", "加入黑名单");
				change.put("new_value", "移出黑名单");
				message = "移出黑名单成功！";
			}
			change.put("change_cause", inMap.getString("remark"));
			change.put("change_user_id", inMap.getString("create_user_id"));
			FieldChangeDao fcDao = new FieldChangeDao();
			fcDao.updateFieldChange(change);
			out.put("state", 1);
			out.put("message", message);
		} else {
			out.put("state", 0);
			out.put("message", out.getString("message"));
			return out;
		}
		return out;
	}

	/**
	 * 验证是否加入黑名单
	 * 
	 * @param inMap
	 *            需要参数 goodsType:标的类型 ，u：要验证的用户ID
	 * @return
	 * @throws Exception
	 */
	public ParaMap checkBlackUser(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		String goodsType = inMap.getString("goodsType");
		DataSetDao da = new DataSetDao();
		UserDao us = new UserDao();
		ParaMap pauser = us.getBidderData(null, null, inMap.getString("u"));
		List items = MakeJSONData.makeItems(pauser);
		int isc = -1;
		if (items.size() > 0) {
			ParaMap userpa = (ParaMap) items.get(0);
			isc = userpa.getInt("is_company");
			ParaMap sqlmap = new ParaMap();
			if (isc == 0) {// 个人与企业分开写的目的是为了防止个人 名称，号码与企业名称号码相同
				sqlmap.put("dataSetNo", "checkIsBlack1");
				sqlmap.put("no", userpa.getString("certificate_no"));// 身份证号码或组织机构
			} else if (isc == 1) {// 企业
				sqlmap.put("dataSetNo", "checkIsBlack2");
			}
			sqlmap.put("name", userpa.getString("name"));// 名称
			sqlmap.put("bidder_type", isc);
			sqlmap.put("ban_type", "%" + goodsType + "%");
			sqlmap.put("moduleNo", "blackuser");
			ParaMap pmresult = da.queryData(sqlmap);
			if (pmresult.getInt("totalRowCount") > 0) {
				result.clear();
				result.put("state", -1);
				result.put("message", userpa.getString("name")
						+ "已被列入黑名单，无法竞买申请");
				return result;
			}
			//查找竞买人的股东
			ParaMap bidderMap=new ParaMap();
			bidderMap.put("pd_id", userpa.getString("id"));
			bidderMap.put("status", 1);
			ParaMap pbidderMap=getProjectBidderList(bidderMap);
			List li=pbidderMap.getList("Rows");
			for(int i=0;i<li.size();i++){
				ParaMap bl=(ParaMap)li.get(i);
				ParaMap blackmap =new ParaMap();
				blackmap.put("goodsType",goodsType);
				blackmap.put("name", bl.getString("name"));
				blackmap.put("is_company", bl.getString("biddertype"));
				blackmap.put("no", bl.getString("idno"));
				ParaMap pm=checkBlackUserByNamePb(blackmap);
				if(pm.getInt("state")!=1){
					result.clear();
					result.put("state", -1);
					result.put("message", userpa.getString("name")+"的股东"+bl.getString("name")
							+ "已被列入黑名单，所以"+userpa.getString("name")+"无法竞买申请");
					return result;
				}
			}
			
			
		}
		result.clear();
		result.put("state", 1);
		return result;
	}

	/**
	 * 根据名称验证是否加入黑名单
	 * 
	 * @param inMap
	 *            需要参数 goodsType:标的类型
	 *            ，is_company：要验证的用户是企业还是个人(0个人，1企业 ,-1不判断是否是个人还是企业)，name:要验证的名称，no：个人类型 需要验证身份证，企业则不需要些参数
	 * @return state -1被列入黑名单，1没有列入，最好判断不等于 1则是黑名单
	 * @throws Exception
	 */
	public ParaMap checkBlackUserByName(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		String goodsType = inMap.getString("goodsType");
		DataSetDao da = new DataSetDao();
		int isc = -1;
		ParaMap userpa = inMap;
		isc = userpa.getInt("is_company");
		ParaMap sqlmap = new ParaMap();
		if (isc == 0||"501".equals(goodsType)) {// 个人与企业分开写的目的是为了防止个人 名称，号码与企业名称号码相同
			if(isc==-1){
				sqlmap.put("dataSetNo", "checkIsBlack3");
			}else{
				if(goodsType.equals("501")){
					sqlmap.put("dataSetNo", "checkIsBlack2");
				}else{
					sqlmap.put("dataSetNo", "checkIsBlack1");
				}
			}
			sqlmap.put("no", userpa.getString("no"));// 身份证号码或组织机构
		} else if (isc == 1) {// 企业
			sqlmap.put("dataSetNo", "checkIsBlack2");
		} else if(isc==-1){
			sqlmap.put("dataSetNo", "checkIsBlack3");
		}
		sqlmap.put("name", userpa.getString("name"));// 名称
		sqlmap.put("bidder_type", isc);
		sqlmap.put("ban_type", "%" + goodsType + "%");
		sqlmap.put("moduleNo", "blackuser");
		ParaMap pmresult = da.queryData(sqlmap);
		if (pmresult.getInt("totalRowCount") > 0) {
			result.clear();
			result.put("state", -1);
			result.put("message", userpa.getString("name") + "已被列入黑名单,无法竞买申请");
			return result;
		}
		result.clear();
		result.put("state", 1);
		return result;
	}

	
	/**
	 * 验证股东是否加入黑名单
	 * 
	 * @param inMap
	 *            需要参数 goodsType:标的类型
	 *            ，is_company：要验证的用户是企业还是个人(0个人，1企业 ,-1不判断是否是个人还是企业)，name:要验证的名称，no：个人类型 需要验证身份证，企业则不需要些参数
	 * @return state -1被列入黑名单，1没有列入，最好判断不等于 1则是黑名单
	 * @throws Exception
	 */
	public ParaMap checkBlackUserByNamePb(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		String goodsType = inMap.getString("goodsType");
		DataSetDao da = new DataSetDao();
		int isc = -1;
		ParaMap userpa = inMap;
		isc = userpa.getInt("is_company");
		ParaMap sqlmap = new ParaMap();
		if (isc == 0||"501".equals(goodsType)) {// 个人与企业分开写的目的是为了防止个人 名称，号码与企业名称号码相同
			if(isc==-1){
				sqlmap.put("dataSetNo", "checkIsBlack3");
			}else{
				if(goodsType.equals("501")){
					sqlmap.put("dataSetNo", "checkIsBlack2");
				}else{
					sqlmap.put("dataSetNo", "checkIsBlack1");
				}
			}
			sqlmap.put("no", userpa.getString("no"));// 身份证号码或组织机构
		} else if (isc == 1) {// 企业
			sqlmap.put("dataSetNo", "checkIsBlack2");
		} else if(isc==-1){
			sqlmap.put("dataSetNo", "checkIsBlack3");
		}
		sqlmap.put("name", userpa.getString("name"));// 名称
		sqlmap.put("bidder_type", isc);
		sqlmap.put("ban_type", "%" + goodsType + "%");
		sqlmap.put("moduleNo", "blackuser");
		ParaMap pmresult = da.queryData(sqlmap);
		if (pmresult.getInt("totalRowCount") > 0) {
			result.clear();
			result.put("state", -1);
			result.put("message", userpa.getString("name") + "已被列入黑名单,无法竞买申请");
			return result;
		}
		result.clear();
		result.put("state", 1);
		return result;
	}
	/**
	 * 返回黑名单状态，竞买人列表需要
	 * 
	 * @param inMap
	 *            需要参数name:名称 ，no：证件号
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBlackStatus(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		String name = inMap.getString("name");
		String no = inMap.getString("no");
		int is_company = inMap.getInt("is_company");
		String goodsType = inMap.getString("goodsType");
		DataSetDao da = new DataSetDao();
		ParaMap sqlmap = new ParaMap();
		if (is_company == 0||goodsType.equals("501")) {// 个人与企业分开写的目的是为了防止个人 名称，号码与企业名称号码相同
			sqlmap.put("dataSetNo", "checkIsBlack1");
			if(goodsType.equals("501"))
			{
			sqlmap.put("dataSetNo", "checkIsBlack2");
			}			//sqlmap.put("no", "");
			else 
			sqlmap.put("no", no);// 身份证号码或组织机构
		} else if (is_company == 1) {// 企业
			sqlmap.put("dataSetNo", "checkIsBlack2");
		}
		sqlmap.put("name", name);// 名称
		sqlmap.put("bidder_type", is_company);
		sqlmap.put("ban_type", "%0%");
		sqlmap.put("moduleNo", "blackuser");
		ParaMap pmresult = da.queryData(sqlmap);
		if (pmresult.getInt("totalRowCount") > 0) {
			result.clear();
			result.put("state", 1);// 是黑名单
			return result;
		}
		result.clear();
		result.put("state", 2);// 不是黑名单
		return result;
	}

	/**
	 * 公布或隐藏黑名单
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap OvertBlack(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		int status = inMap.getInt("status");
		if (status != 0 && status != 1) {
			result.clear();
			result.put("state", 0);
			result.put("message", "状态无效！");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("goods_type", inMap.getString("goods_type"));
		result = dataSetDao.querySimpleData("TRANS_BIDDER_LIMIT_OVERT", sqlMap);
		if (result.getInt("totalRowCount") == 0) {// 黑名单公布表没有记录则插入一条记录
			result.clear();
			sqlMap.clear();
			sqlMap.put("status", inMap.getString("status"));
			sqlMap.put("goods_type", inMap.getString("goods_type"));
			result = dataSetDao.updateData("TRANS_BIDDER_LIMIT_OVERT", "id",
					sqlMap);
		} else {// 黑名单表有记录则更改状态
			sqlMap.clear();
			ParaMap formatMap = new ParaMap();
			sqlMap.put("status", inMap.getString("status"));
			sqlMap.put("id", result.getRecordValue(0, "id"));
			sqlMap.put("update_time", null);
			formatMap.put("update_time", "sysdate");
			result.clear();
			result = dataSetDao.updateData("TRANS_BIDDER_LIMIT_OVERT", "id",
					sqlMap, formatMap);
		}
		result.put("status", status);
		return result;
	}

	/**
	 * 获取黑名单状态，0非公布，1公布
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOvertBlack(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("goods_type", inMap.getString("goods_type"));
		result = dataSetDao.querySimpleData("TRANS_BIDDER_LIMIT_OVERT", sqlMap);
		if (result.getInt("totalRowCount") == 0) {// 没有记录时默认为不公开状态
			result.clear();
			result.put("status", 0);
		} else {
			int status = result.getRecordInt(0, "status");
			result.clear();
			result.put("status", status);
		}
		return result;
	}
	
	/**
	 * 获取跟竞买人相关的股东列表
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getProjectBidderList(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		getQueryCondition(inMap);
		inMap.put("moduleNo", "projectBidder");
		inMap.put("dataSetNo", "getProjectBidderList");
		out = dataSetDao.queryData(inMap);
		int totalRowCount=out.getInt("totalRowCount");
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * 获取编辑股东数据
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap editProjectBidder(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("id", inMap.getString("id"));
//		sqlMap.put("pd_id", out.getString("pd_id"));
		out = dataSetDao.querySimpleData("TRANS_PROJECT_BIDDER_OWNER", sqlMap);
		int totalRowCount=out.getInt("totalRowCount");
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("projectBidder", items!=null && items.size() > 0 ? items.get(0) : null);
		out.put("status", items!=null && items.size() > 0 ? ((ParaMap)items.get(0)).getInt("status") : null);
		return out;
	}
	
	/**
	 * 保存股东
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveProjectBidder(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		String id=inMap.getString("id");
		String pd_id=inMap.getString("pd_id");
		String name=inMap.getString("name");
		String idno=inMap.getString("idno");
		String address=inMap.getString("address");
		String tel=inMap.getString("tel");
		String email=inMap.getString("email");
		String percent=inMap.getString("percent");
		String reamrk=inMap.getString("reamrk");
		String status=inMap.getString("status");
		String biddertype=inMap.getString("biddertype");
		
		if(id==null || id.equals("")){//新增验证是否重复
			ParaMap vlidMap=new ParaMap();
			if(biddertype.equals(0)){//个人验证名称跟身份证
				vlidMap.put("name", name);
				vlidMap.put("idno",idno);
				vlidMap.put("pd_id", pd_id);
			}else{//公司验证名称
				vlidMap.put("name", name);
				vlidMap.put("pd_id", pd_id);
			}
			ParaMap vlidOutMap=dataSetDao.querySimpleData("TRANS_PROJECT_BIDDER_OWNER", vlidMap);
			if(vlidOutMap.getInt("totalRowCount") >0){
				out.put("state", 0);
				out.put("message", "股东已经存在！不能重复添加！");
				return out;
			}
		}
		
		ParaMap sqlMap = new ParaMap();
		ParaMap formatMap = new ParaMap();
		sqlMap.put("id", id);
		sqlMap.put("pd_id", pd_id);
		sqlMap.put("name",name);
		sqlMap.put("idno",idno);
		sqlMap.put("address",address);
		sqlMap.put("tel",tel);
		sqlMap.put("email",email);
		sqlMap.put("percent",percent);
		sqlMap.put("reamrk",reamrk);
		sqlMap.put("createDate", null);
		sqlMap.put("status", status);
		sqlMap.put("biddertype", biddertype);
		
		formatMap.put("createDate", "sysdate");
		out = dataSetDao.updateData("TRANS_PROJECT_BIDDER_OWNER", "id",
				sqlMap,formatMap);
		if(!out.containsKey("id")){
			out.put("id", id);
		}
		out.put("status", status);
		return out;
	}
	
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		String name = inMap.getString("name");
		if (StrUtils.isNotNull(name)) {
			sqlParams.put("name", name);
			customCondition.append(" and tpbo.name like '%'||:name||'%'");
		}
		
		String pd_id = inMap.getString("pd_id");
		if (StrUtils.isNotNull(pd_id)) {
			sqlParams.put("pd_id", pd_id);
			customCondition.append(" and tpbo.pd_id = :pd_id");
		}
		
		String status = inMap.getString("status");
		if (StrUtils.isNotNull(status)) {
			sqlParams.put("status", status);
			customCondition.append(" and tpbo.status = :status");
		}
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
	
	/**
	 * 删除股东
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteProjectBidder(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		String id=inMap.getString("id");
		String ids[]=id.split(",");
		for(int i=0;i<ids.length;i++){
			inMap.put("inid",ids[i]);
			inMap.put("moduleNo", "projectBidder");
			inMap.put("dataSetNo", "deleteProjctBidder");
			out = dataSetDao.executeSQL(inMap);
		}
		return out;
	}

}
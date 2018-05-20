package com.trademan.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 交易交款银行DAO
 * @author SAMONHUA
 * 2012-05-18
 */
public class BankDao extends BaseDao {
	private static String bankRoleId = null;
	
	public ParaMap getBankListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBankListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getBankData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBankData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateBankData(ParaMap bankData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result;
		int state;
		//检查银行名称
		ParaMap checkBankData = new ParaMap();
		String name = bankData.getString("name");
		if (StrUtils.isNotNull(name)) {
			checkBankData.clear();
			checkBankData.put("id", bankData.getString("id"));
			checkBankData.put("name", name);
			result = checkBankExists(null, null, checkBankData);
			state = result.getInt("state");
			if (state == 1) {
				if (Integer.valueOf(String.valueOf(result.getRecordValue(0, "row_count"))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "银行名称" + name + "已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		//检查银行名称
		String no = bankData.getString("no");
		if (StrUtils.isNotNull(no)) {
//			checkBankData.clear();
//			checkBankData.put("id", bankData.getString("id"));
//			checkBankData.put("no", no);
//			result = checkBankExists(null, null, checkBankData);
//			state = result.getInt("state");
//			if (state == 1) {
//				if (Integer.valueOf(String.valueOf(result.getRecordValue(0, "row_count"))) > 0) {
//					result.clear();
//					result.put("state", 0);
//					result.put("message", "银行编号" + no + "已存在，不能重复");
//					return result;
//				}
//			} else
//				return result;
		}
		//检查CAKEY
		String cakey = bankData.getString("cakey");
		if (StrUtils.isNotNull(cakey)) {
			checkBankData.clear();
			checkBankData.put("id", bankData.getString("id"));
			checkBankData.put("cakey", cakey);
			result = checkBankExists(null, null, checkBankData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "CAKEY 已经被使用");
					return result;
				}
			} else
				return result;
		}
		//保存银行
		bankData.put("create_user_id", bankData.getString("u"));
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_bank", "id", bankData, format);
		state = result.getInt("state");
		if (state == 1) {
			ParaMap data = new ParaMap();
			String id = bankData.getString("id");
			if (id == null || id.equals("") || id.equalsIgnoreCase("null"))//新增数据
				id = result.getString("id");
			//新增银行用户，如果存在则不需要处理
			String userId = bankData.getString("user_id");
			String cakeyId = bankData.getString("cakey_id");
			if (bankData.containsKey("user_id")) {
				if (StrUtils.isNull(userId)) {
					String bankUserName = "BANK_" + bankData.getString("name");
					//检查即将新增的用户名是否重复
					int existsUserIndex = 0;
					while (true) {
						data.clear();
						String checkBankUserName;
						if (existsUserIndex == 0)
							checkBankUserName = bankUserName;
						else
							checkBankUserName = bankUserName + existsUserIndex;
						data.put("user_name", checkBankUserName);
						ParaMap checkBankUserData = dataSetDao.querySimpleData("sys_user", data);
						if (checkBankUserData.getInt("state") == 1) {
							if (checkBankUserData.getInt("totalRowCount") == 0) {
								bankUserName = checkBankUserName;
								break;
							}
						}
						existsUserIndex++;
					}
					data.clear();
					data.put("id", "");
					data.put("user_name", bankUserName);
					data.put("password", "");
					data.put("user_type", 2);
					data.put("status", 1);
					data.put("ref_table_name", "trans_bank");
					data.put("ref_id", id);
					data.put("validity", 365);
					result = dataSetDao.updateData("sys_user", "id", data, format);
					state = result.getInt("state");
					if (state == 1) {
						userId = result.getString("id");
						appendBankDefaultRoloe(userId);
					}
				}
				//保存银行CAKEY
				if (bankData.containsKey("cakey_id") || bankData.containsKey("cakey")) {
					if (StrUtils.isNull(cakeyId) || StrUtils.isNotNull(cakey)) {
						data.clear();
						if (StrUtils.isNull(cakeyId))
							data.put("id", "");
						else
							data.put("id", cakeyId);
						data.put("user_id", userId);
						if (StrUtils.isNotNull(cakey))
							data.put("key", cakey);
						result = dataSetDao.updateData("sys_cakey", "id", data, null);
						state = result.getInt("state");
						if (state == 1)
							cakeyId = result.getString("id");
					}
				}
			}
			//创建单位和银行之间的关系
			String organId = bankData.getString("organId");
			if(StrUtils.isNotNull(organId)){
				data.clear();
				data.put("bank_id", id);
				data.put("ref_type", 0);
				data.put("is_valid", 1);
				result = dataSetDao.querySimpleData("trans_organ_bank_rel", data);
				if(result.getSize()>0){
					String enOrganId = String.valueOf(result.getRecordValue(0, "organ_id"));
					if(!organId.equals(enOrganId)){
						data.clear();
						data.put("id",  String.valueOf(result.getRecordValue(0, "id")));
						data.put("organ_id", organId);
						result = dataSetDao.updateData("trans_organ_bank_rel", "id", data, null);
					}
				}else{
					data.clear();
					data.put("id", "");
					data.put("bank_id", id);
					data.put("organ_id", organId);
					data.put("ref_type", 0);
					data.put("is_valid", 1);
					result = dataSetDao.updateData("trans_organ_bank_rel", "id", data, format);
				}
			}
			result.clear();
			result.put("state", 1);
			result.put("id", id);
			result.put("cakeyId", cakeyId);
			result.put("userId", userId);
		}
		return result;
	}
	
	//添加银行角色
	public ParaMap appendBankDefaultRoloe(String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (bankRoleId == null) {
			ParaMap keyData = new ParaMap();
			keyData.put("no", "banker");
			ParaMap bankRoleData = dataSetDao.querySimpleData("sys_role", keyData);
			if (bankRoleData.getInt("state") == 1 && bankRoleData.getInt("totalRowCount") > 0) {
				bankRoleId = String.valueOf(bankRoleData.getRecordValue(0, "id"));
			}
		}
		if (StrUtils.isNull(bankRoleId)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "无缺省银行角色");
			return result;
		}
		ParaMap data = new ParaMap();
		data.put("id", "");
		data.put("role_id", bankRoleId);
		data.put("ref_type", 0);
		data.put("ref_id", userId);
		ParaMap result = dataSetDao.updateData("sys_role_rel", "id", "role_id;ref_type;ref_id", data);
		return result;
	}
	
	public ParaMap getBankAccounts(String bankId)throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("bank_id", bankId);
		sqlParams.put("is_valid", 1);
		return dataSetDao.querySimpleData("trans_account", sqlParams);
	}
	
	public ParaMap deleteBankData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteBankData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	public ParaMap checkBankExists(String moduleId, String dataSetNo, ParaMap bankData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBankExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = bankData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(id)) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
		customCondition.append(" and (1 = 2");
		if (bankData.containsKey("name")) {
			String name = bankData.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" or name = :name");
			}
		}
		if (bankData.containsKey("no")) {
			String no = bankData.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" or upper(no) = upper(:no)");
			}
		}
		if (bankData.containsKey("cakey")) {
			String bankCakey = bankData.getString("cakey");
			if (StrUtils.isNotNull(bankCakey)) {
				sqlParams.put("cakey", bankCakey);
				customCondition.append(" or exists(select * from sys_user where 1 = 1 ");
				if (id != null && !id.equals("") && !id.equalsIgnoreCase("null"))
					customCondition.append(" and (user_type <> 2 or ref_id <> :id)");
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

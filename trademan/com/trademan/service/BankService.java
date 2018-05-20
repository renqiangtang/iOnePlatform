package com.trademan.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trademan.dao.BankDao;

/**
 * 交易交款银行service
 * @author SAMONHUA
 * 2012-05-18
 */
public class BankService extends BaseService {
	public ParaMap getBankListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			//前端表格字段名和数据集字段名不一致
			if (sortField.equalsIgnoreCase("cantonName"))
				sortField = "canton_name";
			else if (sortField.equalsIgnoreCase("createDate"))
				sortField = "create_date";
			else if (sortField.equalsIgnoreCase("isValid"))
				sortField = "is_valid";
			if (StrUtils.isNotNull(sortDir))
				sortField = sortField + " " + sortDir;
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;//new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("bankName")) {
			String bankName = inMap.getString("bankName");
			if (bankName != null && !bankName.equals("") && !bankName.equalsIgnoreCase("null")) {
				sqlParams.put("name", bankName);
				customCondition.append(" and b.name like '%' || :name || '%'");
			}
		}
		if (inMap.containsKey("bankNo")) {
			String bankNo = inMap.getString("bankNo");
			if (bankNo != null && !bankNo.equals("") && !bankNo.equalsIgnoreCase("null")) {
				sqlParams.put("no", bankNo);
				customCondition.append(" and b.no like '%' || :no || '%'");
			}
		}
		if (inMap.containsKey("bankTel")) {
			String bankTel = inMap.getString("bankTel");
			if (bankTel != null && !bankTel.equals("") && !bankTel.equalsIgnoreCase("null")) {
				sqlParams.put("tel", bankTel);
				customCondition.append(" and b.tel like '%' || :tel || '%'");
			}
		}
		if (inMap.containsKey("bankAddress")) {
			String bankAddress = inMap.getString("bankAddress");
			if (bankAddress != null && !bankAddress.equals("") && !bankAddress.equalsIgnoreCase("null")) {
				sqlParams.put("address", bankAddress);
				customCondition.append(" and b.address like '%' || :address || '%'");
			}
		}
		if (inMap.containsKey("bankHasCakey")) {
			String bankHasCakey = inMap.getString("bankHasCakey");
			if (bankHasCakey.equals("1"))
				customCondition.append(" and exists(select * from sys_user where user_type = 2 and ref_id = b.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key is not null))");
			else if (bankHasCakey.equals("0"))
				customCondition.append(" and (not exists(select * from sys_user where user_type = 2 and ref_id = b.id) " +
						"or exists(select * from sys_user where user_type = 2 and ref_id = b.id" +
						"  and (not exists(select * from sys_cakey where user_id = sys_user.id)" +
						"    or exists(select * from sys_cakey where user_id = sys_user.id and key is null))))");
		}
		if (inMap.containsKey("bankCakey")) {
			String bankCakey = inMap.getString("bankCakey");
			if (bankCakey != null && !bankCakey.equals("") && !bankCakey.equalsIgnoreCase("null")) {
				sqlParams.put("cakey", bankCakey);
				customCondition.append(" and exists(select * from sys_user where user_type = 2 and ref_id = b.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		BankDao bankDao = new BankDao();
		ParaMap out = bankDao.getBankListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("tel", "tel");
		fields.put("contact", "contact");
		fields.put("address", "address");
		fields.put("cantonName", "canton_name");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getBankData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BankDao bankDao = new BankDao();
		ParaMap out = bankDao.getBankData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateBankData(ParaMap bankData) throws Exception {
		BankDao bankDao = new BankDao();
		ParaMap out = bankDao.updateBankData(bankData);
		return out;
	}

	public ParaMap deleteBankData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BankDao bankDao = new BankDao();
		ParaMap accounts = bankDao.getBankAccounts(id);
		if(accounts.getSize()>0){
			out.put("state", 0);
			out.put("message", "删除失败！请先删除该银行的账号。");
			return out;
		}
		out = bankDao.deleteBankData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getAliasList(ParaMap inMap) throws Exception{
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParam = new ParaMap();
		ParaMap out = dao.querySimpleData("trans_bank_alias", sqlParam);
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("alias", items);
		return out;
	}
}

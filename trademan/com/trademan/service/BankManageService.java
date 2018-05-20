package com.trademan.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trademan.dao.BankManageDao;

public class BankManageService extends BaseService {

	Logger log = Logger.getLogger(this.getClass());
	
	/*
	 * 直连银行账号列表
	 */
	public ParaMap myBankList(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String organId = inMap.getString("organId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap out = bankManageDao.getOrganBankAccount(moduleId,dataSetNo,organId,0,"");
		
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("bank_id", "bank_id");
		fields.put("ref_type", "ref_type");
		fields.put("canton_id", "canton_id");
		fields.put("canton_name", "canton_name");
		fields.put("account_id", "account_id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("bank_no", "bank_no");
		fields.put("bank_name", "bank_name");
		fields.put("business_type", "business_type");
		fields.put("currency", "currency");
		fields.put("in_account_mode", "in_account_mode");
		fields.put("business_name", "business_name");
		fields.put("is_outside", "is_outside");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/*
	 * 委托银行账号列表
	 */
	public ParaMap trustBankAccountList(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String organId = inMap.getString("organId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap out = bankManageDao.getOrganTrustBankAccount(moduleId,dataSetNo,organId,"");
		
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("bank_id", "bank_id");
		fields.put("ref_type", "ref_type");
		fields.put("canton_id", "canton_id");
		fields.put("canton_name", "canton_name");
		fields.put("account_id", "account_id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("bank_no", "bank_no");
		fields.put("bank_name", "bank_name");
		fields.put("business_type", "business_type");
		fields.put("currency", "currency");
		fields.put("in_account_mode", "in_account_mode");
		fields.put("business_name", "business_name");
		fields.put("organ_id", "organ_id");
		fields.put("organ_no", "organ_no");
		fields.put("organ_name", "organ_name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	/*
	 * 委托银行列表
	 */
	public ParaMap trustBankList(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String organId = inMap.getString("organId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap out = bankManageDao.getOrganTrustBank(moduleId,dataSetNo,organId);
		
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("bank_id", "bank_id");
		fields.put("ref_type", "ref_type");
		fields.put("canton_id", "canton_id");
		fields.put("canton_name", "canton_name");
		fields.put("account_id", "account_id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("organ_id", "organ_id");
		fields.put("organ_no", "organ_no");
		fields.put("organ_name", "organ_name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getAccountList(ParaMap inMap)throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String bankId = inMap.getString("bankId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap out = bankManageDao.getAccountList(moduleId,dataSetNo,bankId);
		
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("account_id", "account_id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("bank_no", "bank_no");
		fields.put("bank_name", "bank_name");
		fields.put("business_type", "business_type");
		fields.put("currency", "currency");
		fields.put("in_account_mode", "in_account_mode");
		fields.put("business_name", "business_name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	

	
	/*
	 * 带选择的委托账号列表
	 */
	public ParaMap otherBankList(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String organId = inMap.getString("organId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap out = bankManageDao.getOtherOrganBank(moduleId,dataSetNo,organId);
		
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("bank_id", "bank_id");
		fields.put("ref_type", "ref_type");
		fields.put("canton_id", "canton_id");
		fields.put("canton_name", "canton_name");
		fields.put("account_id", "account_id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("organ_id", "organ_id");
		fields.put("organ_no", "organ_no");
		fields.put("organ_name", "organ_name");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/*
	 * 修改银行账号信息
	 */
	public ParaMap updateBankInfo(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String id = in.getString("id");
		String name = in.getString("name");
		String no = in.getString("no");
		String bank_id = in.getString("bank_id");
		String bank_name = in.getString("bank_name");
		String bank_no = in.getString("bank_no");
		String business_type = in.getString("business_type");
		String currency = in.getString("currency");
		String in_account_mode = in.getString("in_account_mode");
		String is_outside = in.getString("is_outside");
		String organId = in.getString("organId");
		String userId = in.getString("userId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", StrUtils.isNull(id)?"":id);
		sqlParams.put("name", name);
		sqlParams.put("no", no);
		sqlParams.put("bank_id", bank_id);
		sqlParams.put("bank_name", bank_name);
		sqlParams.put("bank_no", bank_no);
		sqlParams.put("business_type", business_type);
		sqlParams.put("currency", currency);
		sqlParams.put("in_account_mode", in_account_mode);
		sqlParams.put("is_valid", 1);
		sqlParams.put("create_user_id", name);
		sqlParams.put("is_outside", is_outside);
		out = dataSetDao.updateData("trans_account", "id", sqlParams);
		if(StrUtils.isNotNull(id)){
			out.put("id", id);
		}
		return out;
		
	}
	
	/*
	 * 选择委托银行账号
	 */
	public ParaMap selectTrustBank(ParaMap inMap) throws Exception {
		String organId = inMap.getString("organId");
		String ids = inMap.getString("ids");
		String userId = inMap.getString("userId");
		
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		ParaMap out  = new ParaMap();
		//循环新增
		if(StringUtils.isNotEmpty(ids)){
			String[] id = ids.split(",");
			for(int i= 0 ; i < id.length ; i++){
				if(StringUtils.isNotEmpty(id[i])){
					keyData.clear();
					keyData.put("id", "");
					keyData.put("organ_id", organId);
					keyData.put("bank_id", id[i]);
					keyData.put("ref_type", "1");
					keyData.put("is_valid", "1");
					keyData.put("create_user_id", userId);
					out=dataSetDao.updateData("trans_organ_bank_rel","id", keyData);
				}
			}
		}
		return out;
	}
	
	/*
	 * 取得银行账号信息
	 */
	public ParaMap getBankInfo(ParaMap in) throws Exception {
		String id = in.getString("id");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", id);
		return dataSetDao.querySimpleData("trans_account", sqlParams);
	}
	
	/*
	 * 删除直连银行账号
	 */
	public ParaMap deleteMyBank(ParaMap in) throws Exception {
		String ids = in.getString("ids");
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		//循环删除
		if(StringUtils.isNotEmpty(ids)){
			String[] id = ids.split(",");
			for(int i= 0 ; i < id.length ; i++){
				if(StringUtils.isNotEmpty(id[i])){
					keyData.clear();
					keyData.put("id", id[i]);
					out=dataSetDao.deleteSimpleData("trans_account", keyData);
				}
			}
		}
		return out;
	}
	
	/*
	 * 删除委托银行账号
	 */
	public ParaMap deleteTrustBank(ParaMap in) throws Exception {
		String ids = in.getString("ids");
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		//循环新增资格
		if(StringUtils.isNotEmpty(ids)){
			String[] id = ids.split(",");
			for(int i= 0 ; i < id.length ; i++){
				if(StringUtils.isNotEmpty(id[i])){
					keyData.clear();
					keyData.put("id", id[i]);
					out=dataSetDao.deleteSimpleData("trans_organ_bank_rel ", keyData);
				}
			}
		}
		return out;
	}
	
	/*
	 * 初始化页面下拉列表框
	 * 单位银行，交易物，交易类别，币种，区域等
	 */
	public ParaMap getPageSelect(ParaMap inMap) throws Exception{
		ParaMap out = new ParaMap();
		String organId = inMap.getString("organId");
		BankManageDao bankManageDao = new BankManageDao();
		ParaMap bankList = bankManageDao.getOrganMyBankList(organId);
		out.put("bank",bankList.getRecords());
		ParaMap businessList = bankManageDao.getOrganBusinessList(organId);
		out.put("business",businessList.getRecords());
		return out;
	}
	

}

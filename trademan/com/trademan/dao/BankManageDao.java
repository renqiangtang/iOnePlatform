package com.trademan.dao;



import org.apache.log4j.Logger;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;



/**
 * 
 * @author shis
 *
 */
public class BankManageDao extends BaseDao {
	Logger log = Logger.getLogger(this.getClass());
	
	/*
	 * 本单位直属银行账号
	 */
	public ParaMap getOrganBankAccount(String moduleId, String dataSetNo,String organId,int refType , String customCondition) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getOrganBankAccount");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("organId", organId);
		sqlParams.put("refType", refType);
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		return dataSetDao.queryData(sqlParams);
	}
	
	/*
	 * 本单位委托银行账号
	 */
	public ParaMap getOrganTrustBankAccount(String moduleId, String dataSetNo,String organId, String customCondition) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getOrganTrustBankAccount");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("organId", organId);
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		return dataSetDao.queryData(sqlParams);
	}
	
	/*
	 * 本单位委托银行
	 */
	public ParaMap getOrganTrustBank(String moduleId, String dataSetNo,String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getOrganTrustBank");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}
	
	/*
	 * 银行账号列表
	 */
	public ParaMap getAccountList(String moduleId, String dataSetNo,String bankId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getBankAccountList");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("bankId", bankId);
		return dataSetDao.queryData(sqlParams);
	}
	
	/*
	 * 本单位直属银行列表
	 */
	public ParaMap getOrganMyBankList(String organId)throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bank_list");
		sqlParams.put("dataSetNo", "getOrganMyBankList");
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}
	
	
	/*
	 * 本单位交易资格列表
	 */
	public ParaMap getOrganBusinessList(String organId)throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_organ_manager");
		sqlParams.put("dataSetNo", "getOrganBusinessList");
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}
	
	/*
	 * 其他单位直属银行列表
	 */
	public ParaMap getOtherOrganBank(String moduleId, String dataSetNo,String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_bank_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "getOtherOrganBank");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}

}

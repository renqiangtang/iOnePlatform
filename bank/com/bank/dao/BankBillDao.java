package com.bank.dao;

import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;


public class BankBillDao extends BaseDao {
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             银行入账单，包括未提交，未结束的标的
	 */
	public ParaMap getBankBillListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankBillQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             银行入账单更新
	 */
	public ParaMap updateBankBillData(ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("bank_business_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		String id = sqlParams.getString("id");
		if(StringUtils.isNotEmpty(id))
			sqlParams.put("id", id);
		else
			sqlParams.put("id", null);
		ParaMap result = dataSetDao.updateData("trans_account_bill", "id",
				sqlParams, format);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 *             银行入账单获取
	 */
	public ParaMap getBankBillData(String moduleId, String dataSetNo, String id)
			throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankBillSingleQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param id
	 * @return
	 * @throws Exception
	 *             银行入账单删除
	 */
	public ParaMap deleteBankBillData(String moduleId, String dataSetNo,
			String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankBillDelete");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 *             判断是否是错转款
	 */
	public ParaMap submitCheckBankBillData(String moduleId,
			String dataSetNo, ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "checkBankBillData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap querySysdate(String moduleId,
			String dataSetNo, ParaMap sqlParams) throws Exception{
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "querySysdate");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	/**
	 * 
	 * 新增入账单判断交款户名与入账申请单户名是否一至
	 */
	public ParaMap saveBankBillData(String moduleId,
			String dataSetNo, ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "saveBankBillData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * 汇总金额再来判断总金额是否大于或等于保证金:amountEarnestMoney
	 * 
	 */
	public ParaMap amountEarnestMoney(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "amountEarnestMoney");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	/**
	 * 
	 * 设置竞买申请通过：transLicenseUpdate
	 */
	public ParaMap transLicenseUpdate(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transLicenseUpdate");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	/**
	 * 
	 * 查找竞买申请通过id：queryTLid
	 */
	public ParaMap queryTLid(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryTLid");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		return dataSetDao.queryData(sqlParams);
	}
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             子账号转主账号,包括已提交、已结束的标的
	 */
	public ParaMap getBankBillSubAccountListData(String moduleId,
			String dataSetNo, ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankBillSubAccountQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             银行所有入账单，包括已提交、已结束的标的
	 */
	public ParaMap getBankBillAllListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankBillAllQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             获取银行登录者的银行信息
	 */
	public ParaMap getBankInfoData(String moduleId, ParaMap sqlParams)
			throws Exception {
		sqlParams.put("moduleId", moduleId);
		sqlParams.put("dataSetNo", "transBankInfoQuery");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             银行流水号
	 */
	public ParaMap getBankSNListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankSNQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             银行子账号
	 */
	public ParaMap getBankSubListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "transBankSubQuery");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 银行入账查询
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBankBill(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("")
				|| dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryTransAccountBill");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

package com.after.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class TransAccountDao extends BaseDao {
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取退款清单
	 * @throws Exception
	 */
	public ParaMap getRefundAccountListData(String moduleId, String dataSetNo,ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if(StringUtils.isEmpty(moduleId)){
			sqlParams.put("moduleNo", "refund_account_manage");
		}
		if (StringUtils.isEmpty(dataSetNo))
			sqlParams.put("dataSetNo", "query_refund_account");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	
	public List getCenterBankList(String goodsType) throws Exception{
		
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "finance_refund_account_manage");
		sqlParams.put("dataSetNo", "query_center_bank_list");
		sqlParams.put("goodsType", goodsType);
		String sql = " ";
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.indexOf("301")>=0){
				goodsType = "'301','401'";
			}else{
				goodsType = "'"+goodsType+"'";
			}
			sql = " and substr(business_type,1,3) in ("+goodsType+") ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql );
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result.getListObj();
	}
	
	public List getCantonList() throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "finance_refund_account_manage");
		sqlParams.put("dataSetNo", "refund_account_canton_list");
		sqlParams.put("cantonId", "440000");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result.getListObj();
	}
	
	
	public ParaMap targetAccountBillList(ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleNo", "finance_refund_account_manage");
		sqlParams.put("dataSetNo", "query_target_account_bill_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public void updateRefundTime( ParaMap  inMap , String businessId, String businessNo)throws Exception{
		String id = inMap.getString("id");
		String time = inMap.getString("time");
		String no = inMap.getString("no");
		String name = inMap.getString("name");
		String bank = inMap.getString("bank");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", id);
		sqlParams.put("refund_time", time);
		sqlParams.put("status", 3);
		sqlParams.put("out_sequence_no", businessNo);
		ParaMap formatMap = new ParaMap();
		formatMap.put("refund_time","to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_account_bill", "id",sqlParams, formatMap);
		
		ParaMap bill = this.getAccountBill(id);
		
		sqlParams.clear();
		sqlParams.put("bill_id", id);
		sqlParams.put("account_bank", bill.getString("account_bank"));
		sqlParams.put("account_no", bill.getString("account_no"));
		sqlParams.put("account_name", bill.getString("account_name"));
		sqlParams.put("child_account_bank", bill.getString("child_account_bank"));
		sqlParams.put("child_account_no", bill.getString("child_account_no"));
		sqlParams.put("child_account_name", bill.getString("child_account_name"));
		sqlParams.put("bidder_account_bank", bank);
		sqlParams.put("bidder_account_no", no);
		sqlParams.put("bidder_account_name", name);
		sqlParams.put("amount", bill.getString("amount"));
		sqlParams.put("currency", bill.getString("currency"));
		sqlParams.put("refund_no", businessNo);
		sqlParams.put("refund_id", businessId);
		sqlParams.put("refund_time", bill.getString("refund_time"));
		formatMap.clear();
		formatMap.put("refund_time","to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_account_bill_refund", "id",sqlParams, formatMap);

		
	}
	
	public ParaMap getAccountBill(String billId) throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", billId);
		sqlParams.put("is_valid",1);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_account_bill", sqlParams);
		if(result!=null && result.getSize()>0){
			List list = result.getListObj();
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	public String getBankNoByAccountNo(String accountNo)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("no", accountNo);
		sqlParams.put("is_valid",1);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_account", sqlParams);
		if(result!=null && result.getSize()>0){
			List list = result.getListObj();
			ParaMap accountMap =  (ParaMap)list.get(0);
			return accountMap.getString("bank_no");
		}else{
			return null;
		}
	}
}

package com.bank.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bank.dao.BankBaseDao;
import com.bank.dao.BankDao;
import com.bank.util.BankUtil;
import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.base.service.BaseService;
import com.base.utils.CharsetUtils;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;

public class BankService extends BaseService {

	Logger log = Logger.getLogger(this.getClass());

	/**
	 * 处理银行发送来的报文（目前为来帐通知）
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String dealBankSendXml(ParaMap inMap) throws Exception {
		String xml = inMap.getString("xml");
		//xml = CharsetUtils.getString(xml);
		String bankId = inMap.getString("bankId");
		String trxcode = inMap.getString("trxcode");
		BankBaseDao baseDao = (BankBaseDao) BankUtil.createInstance(bankId);
		Method m = baseDao.getClass().getMethod("dealWith" + trxcode,
				String.class);
		String returnXml = (String) m.invoke(baseDao, xml);
		return returnXml;
	}

	/**
	 * 处理银行的回执报文
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public int dealBankFeedBack(ParaMap inMap) throws Exception {
		String xml = inMap.getString("xml");
		String businessId = inMap.getString("businessId");
		BankDao bankDao = new BankDao();
		int result = bankDao.dealBankFeedBack(businessId, xml);
		return result;
	}

	/**
	 * 处理银行的回执报文
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String dealBankFeedBack10004(ParaMap inMap) throws Exception {
		String xml = inMap.getString("xml");
		xml = CharsetUtils.getString(xml);
		log.debug("银行处理1004结果：begin");
		log.debug("银行处理1004结果：" + xml);
		log.debug("银行处理1004结果：end");
		String businessId = inMap.getString("businessId");
		BankDao bankDao = new BankDao();
		ParaMap out = bankDao.dealBankFeedBack10004(businessId, xml);
		String result = out.getString("result");
		log.debug("===========>dealBankFeedBack10004.result=" + result);
		DataSourceManager.commit();
		if (result != null && "success".equals(result)) {
			String targetIds = out.getString("targetIds");
			log.debug("===========>dealBankFeedBack10004 judge is deal success :"
					+ targetIds);
			if (targetIds != null && !targetIds.equals("")) {
				String[] targetIdArray = targetIds.split(",");
				if (targetIdArray != null && targetIdArray.length > 0) {
					for (int i = 0; i < targetIdArray.length; i++) {
						String targetId = targetIdArray[i];
						boolean isFeedback = bankDao.isAllSubmit(targetId);
						log.debug("===========>dealBankFeedBack10004 judge is can send bank :"
								+ isFeedback);
						if (isFeedback) {
							feedbackTargetInfo(targetId);
						}
					}
				}
			}
		}

		return result;
	}

	public void feedbackTargetInfo(String targetId) {
		try {
			log.info("发送标的信息回执：" + targetId);
			String flag = AppConfig.getPro("targetFeedBackFlag");
		} catch (Exception e) {
			log.error("发送标的回执信息失败！");
			log.error(e.getMessage());
		}
	}

	/**
	 * 得到系统要发送的报文
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String getWebSendXml(ParaMap inMap) throws Exception {
		String type = inMap.getString("type");
		BankDao bankDao = new BankDao();
		ParaMap result = bankDao.getWebSendXml(type);
		return result.toString();
	}

	/**
	 * 销户
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String cancelChildAccount(String applyId, int businessType)
			throws Exception {
		String result = null;
		BankDao dao = new BankDao();
		ParaMap applyMap = dao.GetTransAccountBillApply(applyId);
		ParaMap accountMap = dao.getBankAccount(
				applyMap.getString("account_no"), 1);
		if (accountMap.getInt("in_account_mode") == 3) {
			BankBaseDao baseDao = (BankBaseDao) BankUtil
					.createInstance(accountMap.getString("bank_no"));
			result = baseDao.cancelChildAccount(
					applyMap.getString("child_account_no"),
					accountMap.getString("bank_no"), applyMap.getString("id"));
		}
		return result;
	}

	public ParaMap monthSubmitChild(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String message = "";
		BankDao dao = new BankDao();
		BankBaseDao bankBaseDao = new BankBaseDao();
		ParaMap result = dao.getBankNoListNotSubmit();
		String send_date = DateUtils.getStr(DateUtils.now());
		String send_no = send_date.replaceAll("-", "").replaceAll(":", "")
				.replaceAll(" ", "");
		if (result != null && result.getSize() > 0) {
			for (int i = 0; i < result.getSize(); i++) {
				String bankNo = result.getRecordString(i, "bank_no");
				String bankName = result.getRecordString(i, "bank_name");
				// 得到具体的DAO
				BankBaseDao baseDao = (BankBaseDao) BankUtil
						.createInstance(bankNo);
				Method m = baseDao.getClass().getMethod("monthSubmitChild",
						String.class, String.class, String.class);
				String msg = (String) m.invoke(baseDao, bankNo, send_no,
						send_date);
				if (StrUtils.isNull(msg)) {
					message = message + bankName + "(" + bankNo
							+ "):发送报文成功; <br>";
				} else {
					message = message + bankName + "(" + bankNo + "):主账号为"
							+ msg + "发送报文失败;<br>";
				}
			}
		} else {
			out.put("message", "没有需要支付的银行");
			return out;
		}
		out.put("message", message);
		return out;
	}

	public ParaMap monthTransferList(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_bank_month_transfer_list");
		ParaMap dataMap = dao.queryData(sqlParams);

		List resultList = new ArrayList();
		if (dataMap.getSize() > 0) {
			List filds = dataMap.getFields();
			for (int i = 0; i < dataMap.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) dataMap.getRecords().get(i);
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
					}
				}
				resultList.add(out);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", dataMap.getInt("totalRowCount"));
		return out;
	}

	public ParaMap monthDetailList(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_bank_month_detail_list");
		sqlParams.put("mtId", inMap.getString("id"));
		ParaMap dataMap = dao.queryData(sqlParams);

		List resultList = new ArrayList();
		if (dataMap.getSize() > 0) {
			List filds = dataMap.getFields();
			for (int i = 0; i < dataMap.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) dataMap.getRecords().get(i);
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
					}
				}
				resultList.add(out);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", dataMap.getSize());
		return out;
	}

	public ParaMap bankTargetList(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = inMap;
		String no = inMap.getString("no");
		String name = inMap.getString("name");
		String status = inMap.getString("status");
		String goodsType = inMap.getString("goodsType");
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(organId)) {
			sqlParams.put("organId", organId);
			customCondition.append(" and tt.trans_organ_id = :organId ");
		}
		if (StrUtils.isNotNull(no)) {
			sqlParams.put("no", no);
			customCondition.append(" and tt.no like '%'||:no||'%' ");
		}
		if (StrUtils.isNotNull(name)) {
			sqlParams.put("name", name);
			customCondition.append(" and tt.name like '%'||:name||'%' ");
		}
		if (StrUtils.isNotNull(status)) {
			sqlParams.put("status", status);
			customCondition.append(" and tt.status = :status ");
		}
		if (StrUtils.isNotNull(goodsType)) {
			if(goodsType.indexOf(",")!=-1){
				customCondition.append(" and (tt.business_type like '301'||'%' or tt.business_type like '401'||'%') ");
			}else{
				sqlParams.put("goodsType", goodsType);
				customCondition.append(" and tt.business_type like :goodsType||'%' ");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);

		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_bank_target_list");
		ParaMap dataMap = dao.queryData(sqlParams);

		List resultList = new ArrayList();
		if (dataMap.getSize() > 0) {
			List filds = dataMap.getFields();
			for (int i = 0; i < dataMap.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) dataMap.getRecords().get(i);
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
					}
				}
				resultList.add(out);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", dataMap.getInt("totalRowCount"));
		return out;
	}

	public ParaMap bankAccountBillList(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_bank_account_bill_list");
		sqlParams.put("targetId", inMap.getString("targetId"));
		ParaMap dataMap = dao.queryData(sqlParams);

		List resultList = new ArrayList();
		if (dataMap.getSize() > 0) {
			List filds = dataMap.getFields();
			for (int i = 0; i < dataMap.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) dataMap.getRecords().get(i);
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
					}
				}
				resultList.add(out);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", dataMap.getSize());
		return out;
	}

	public ParaMap submitChildNo(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String billId = inMap.getString("billId");
		if (StrUtils.isNull(billId)) {
			out.put("message", "子转总失败！入账单id错误。");
			return out;
		}
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		String sql = "select tab.id,tab.amount , tab.status , tab.child_account_no , ta.no as center_no , ta.bank_no ,ta.name  "
				+ "from trans_account_bill tab, trans_account ta  "
				+ "where tab.account_no = ta.no and tab.business_type = ta.business_type  "
				+ "and ta.in_account_mode = 3 and ta.is_valid = 1 "
				+ "and tab.id = :billId";
		sqlParams.put("sql", sql);
		sqlParams.put("billId", billId);
		ParaMap dataMap = dao.queryData(sqlParams);
		if (dataMap != null && dataMap.getSize() > 0) {
			int status = dataMap.getRecordInteger(0, "status");
			if (status > 1) {
				out.put("message", "子转总失败！入账单已经转入主账户。");
				return out;
			}
			String centerNo = dataMap.getRecordString(0, "center_no");
			String name = dataMap.getRecordString(0, "name");
			String bankId = dataMap.getRecordString(0, "bank_no");
			String no = dataMap.getRecordString(0, "child_account_no");
			BigDecimal amount = new BigDecimal(dataMap.getRecordString(0,
					"amount"));
			DecimalFormat df1 = new DecimalFormat("############0.00");
			String amoutStr = amount == null ? "0" : df1
					.format(((BigDecimal) amount).doubleValue());
			if (amoutStr != null && !"".equals(amoutStr)) {
				if (amoutStr.indexOf(".") > 0) {
					String[] as = amoutStr.split("\\.");
					String secondAS = "00";
					if (as.length > 1) {
						secondAS = as[1];
						if (secondAS == null || secondAS.equals("")) {
							secondAS = "00";
						} else if (secondAS.length() == 0) {
							secondAS = "00";
						} else if (secondAS.length() == 1) {
							secondAS = secondAS + "0";
						} else if (secondAS.length() == 2) {
							secondAS = secondAS;
						} else if (secondAS.length() > 2) {
							secondAS = secondAS.substring(0, 2);
						}
					}
					amoutStr = as[0] + secondAS;
				} else {
					amoutStr = amount.toString() + "00";
				}
			}
			BankBaseDao bankBaseDao = new BankBaseDao();
			String result = bankBaseDao.submitChildAccount(centerNo, name, no,
					bankId, amoutStr, billId);
			if (StrUtils.isNull(result)) {
				result = "子转总失败！";
			}
			out.put("message", result);
			return out;
		} else {
			out.put("message", "子转总失败！未找到有效入账单信息。");
			return out;
		}
	}

	public ParaMap deleteChildNo(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String applyId = inMap.getString("applyId");
		if (StrUtils.isNull(applyId)) {
			out.put("message", "销户失败！参数错误。");
			return out;
		}
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		String sql = "select count(*) as no_submit from trans_account_bill where apply_id = :applyId and status <2";
		sqlParams.put("sql", sql);
		sqlParams.put("applyId", applyId);
		ParaMap dataMap = dao.queryData(sqlParams);
		if (dataMap != null && dataMap.getSize() > 0) {
			int noSubmit = dataMap.getRecordInteger(0, "no_submit");
			if (noSubmit > 0) {
				out.put("message", "销户失败！该子账户有未提交到主账户的入账单。");
				return out;
			}
		}
		sql = "select taba.id , taba.child_account_no , taba.account_no , "
				+ "(select ta.bank_no from trans_account ta  "
				+ "where  ta.no = taba.account_no  and rownum<=1)  as bank_no  "
				+ "from trans_account_bill_apply taba "
				+ "where taba.id = :applyId ";
		sqlParams.clear();
		sqlParams.put("sql", sql);
		sqlParams.put("applyId", applyId);
		dataMap = dao.queryData(sqlParams);
		if (dataMap != null && dataMap.getSize() > 0) {
			String bankId = dataMap.getRecordString(0, "bank_no");
			String no = dataMap.getRecordString(0, "child_account_no");
			BankBaseDao bankBaseDao = new BankBaseDao();
			String result = bankBaseDao.cancelChildAccount(no, bankId, applyId);
			if (StrUtils.isNull(result)) {
				result = "销户失败！";
			}
			out.put("message", result);
			return out;
		} else {
			out.put("message", "销户失败！未找到有效子账号信息。");
			return out;
		}
	}
	
	
	
	/**
	 * 得到退款结果
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String getRefundResultXml(ParaMap inMap) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "finance_refund_account_manage");
		sqlParams.put("dataSetNo", "get_wait_refund_result_list");
		DataSetDao dao = new DataSetDao();
		ParaMap dataMap = dao.queryData(sqlParams);
		List resultList = dataMap.getListObj();
		List list = new ArrayList();
		ParaMap out = new ParaMap();
		if(resultList!=null && resultList.size()>0){
			for(int i = 0 ; i < resultList.size() ; i ++ ){
				ParaMap bill = (ParaMap)resultList.get(i);
				ParaMap xmlMap = new ParaMap();
				String bankNo = bill.getString("bank_no");
				String id = bill.getString("id");
				String child_account_no = bill.getString("child_account_no");
				String bidder_account_no = bill.getString("bidder_account_no");
				String bidder_no = bill.getString("bidder_no");
				if(StrUtils.isNull(bidder_no)){
					bidder_no = bidder_account_no;
				}
				String amount = bill.getString("amount");
				String in_sequence_no = bill.getString("in_sequence_no");
				BankBaseDao baseDao = (BankBaseDao) BankUtil.createInstance(bankNo);
				String result = baseDao.refundAccountResult(bankNo, child_account_no, bidder_no, amount, in_sequence_no); 
				if(result!=null && result.equals("success")){
					ParaMap keyset = new ParaMap();
					keyset.put("id", id);
					keyset.put("status", 4);
					dao.updateData("trans_account_bill", "id", keyset);
				}
				
			}
		}
		return "end";
	}
	
	
	
	/**
	 * 银行报文列表
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap bankBusinessList(ParaMap inMap)throws Exception{
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if(inMap.containsKey("bankId")){
			String bankId = inMap.getString("bankId");
			if (StrUtils.isNotNull(bankId)) {
				sqlParams.put("bankId", bankId);
				customCondition.append(" and b.bank_id = :bankId ");
			}
		}
		if(inMap.containsKey("trxCode")){
			String trxCode = inMap.getString("trxCode");
			if (StrUtils.isNotNull(trxCode)) {
				sqlParams.put("trxCode", trxCode);
				customCondition.append(" and b.trx_code = :trxCode ");
			}
		}
		sqlParams.put("date_time_format", "yyyy-mm-dd hh24:mi:ss");
		if (inMap.containsKey("startTime")) {
			String startTime = inMap.getString("startTime");
			if (StrUtils.isNotNull(startTime)) {
				sqlParams.put("startTime", startTime + " 00:00:00");
				customCondition.append(" and to_char(b.busi_date, :date_time_format) >= :startTime");
			}
		}
		if (inMap.containsKey("endTime")) {
			String endTime = inMap.getString("endTime");
			if (StrUtils.isNotNull(endTime)) {
				sqlParams.put("endTime", endTime + " 23:59:59");
				customCondition.append(" and to_char(b.busi_date, :date_time_format) <= :endTime");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);

		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_trans_bank_business_list");
		ParaMap dataMap = dao.queryData(sqlParams);
		int rowCount =  dataMap.getInt("totalRowCount");
		List resultList = dataMap.getListObj();
		
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", rowCount);
		return out;
	}
	
	
	public ParaMap bankBusinessDetail(ParaMap inMap)throws Exception{
		DataSetDao dao = new DataSetDao();
		String id = inMap.getString("id");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_trans_bank_business_list");
		sqlParams.put("bId", id);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", " and b.id =:bId");
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap dataMap = dao.queryData(sqlParams);
		int rowCount =  dataMap.getInt("totalRowCount");
		List resultList = dataMap.getListObj();
		if(resultList!=null && resultList.size()>0){
			return (ParaMap)resultList.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 得到单位id
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("id")) {
						organId = (String) record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}

}

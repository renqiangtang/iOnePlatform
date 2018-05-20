package com.bank.dao;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.bank.util.XmlInput;
import com.bank.util.XmlOutput;
import com.bank.util.XmlUtil;
import com.bank.util.XmlVo;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.base.http.HttpManager;
import com.base.utils.CharsetUtils;
import com.base.utils.DateUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.trademan.dao.LicenseManageDao;

/**
 * 
 * @author shis
 * 
 */
public class BankDao extends BaseDao {
	Logger log = Logger.getLogger(this.getClass());

	public static HashMap bankSendServiceXmlMap = new HashMap();

	/**
	 * 得到要发送的报文，并发发送状态改为发送中
	 * 
	 * @return
	 * @throws Exception
	 */
	public ParaMap getWebSendXml(String type) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "get_not_send_xml");
		String str = "and trx_code in ('10001','10003','10004','10005','10000')";
		if (type == null || "".equals(type) || type.equalsIgnoreCase("null")) {
			str = "and trx_code in ('10001','10003','10004','10005','10000')";
		} else {
			str = "and trx_code in ('" + type + "')";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", str);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap businessMap = dataSetDao.queryData(sqlParams);
		List list = new ArrayList();
		if (businessMap.getSize() > 0) {
			int size = businessMap.getSize();
			for (int i = 0; i < size; i++) {
				ParaMap pMap = new ParaMap();
				String businessId = (String) businessMap.getRecordValue(i, "id");
				pMap.put("id", businessId);
				pMap.put("bankId", businessMap.getRecordValue(i, "bank_id"));
				//pMap.put("xml", businessMap.getRecordString(i, "content"));
				pMap.put("xml", URLEncoder.encode(businessMap.getRecordString(i, "content")));
				list.add(pMap);
				ParaMap keyData = new ParaMap();
				keyData.put("id", businessId);
				keyData.put("status", 1);
				dataSetDao.updateData("trans_bank_business", "id", keyData,null);
			}
		}
		ParaMap result = new ParaMap();
		result.put("xmls", list);
		return result;
	}

	/**
	 * 处理银行的回执 0未处理 1已发送 2成功 3失败
	 * 
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public int dealBankFeedBack(String id, String response) throws Exception {
		if (response == null || "".equals(response)) {
			return 1;
		}
		XmlUtil xu = new XmlUtil();
		XmlVo xv = xu.readXML(response);
		XmlOutput output = xv.getOutput();
		int status = 1;
		String result = null;
		String _rStatus = output.getStatus();
		String _retCode = output.getRetcode();
		String _retmsg = output.getRetmsg();
		// if(("0000".equals(_retCode)||"0210".equals(_retCode))&&"00".equals(_rStatus)){
		if ("0000".equals(_retCode) || "0210".equals(_retCode)) {
			status = 2;
			// bankSendServiceXmlMap.put(id, output);
		} else {
			status = 3;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		keyData.put("status", status);
		keyData.put("response", response);
		dataSetDao.updateData("trans_bank_business", "id", keyData, null);
		return status;
	}

	/**
	 * 处理银行的回执 0未处理 1已发送 2成功 3失败
	 * 
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ParaMap dealBankFeedBack10004(String id, String response)
			throws Exception {
		ParaMap out = new ParaMap();
		if (response == null || "".equals(response)) {
			out.put("result", "false");
			return out;
		}
		XmlUtil xu = new XmlUtil();
		XmlVo xv = xu.readXML(response);
		XmlOutput output = xv.getOutput();
		int status = 1;
		String _rStatus = output.getStatus();
		String _retCode = output.getRetcode();
		String _retmsg = output.getRetmsg();
		if ("0000".equals(_retCode) || "0210".equals(_retCode)) {
			status = 2;
			out.put("result", "success");
		} else {
			status = 3;
			out.put("result", "false");
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		keyData.put("status", status);
		keyData.put("response", response);
		dataSetDao.updateData("trans_bank_business", "id", keyData, null);

		if (status == 2) {
			keyData = new ParaMap();
			keyData.put("id", id);
			ParaMap busiMap = dataSetDao.querySimpleData("trans_bank_business",
					keyData);
			if (busiMap.getSize() > 0) {
				String billId = (String) busiMap.getRecordValue(0, "bill_id");
				billId = billId.indexOf(",") < 0 ? "'" + billId + "'" : "'"
						+ billId.replaceAll(",", "','") + "'";
				keyData = new ParaMap();
				keyData.put("moduleNo", "bank_server");
				keyData.put("dataSetNo", "update_account_bill_status");
				keyData.put("status", 2);
				ParaMap customConditions = new ParaMap();
				customConditions.put("CUSTOM_CONDITION", " where id in ("
						+ billId + ")");
				keyData.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
				dataSetDao.executeSQL(keyData);

				// 得到入账单对应的标的id
				keyData.clear();
				keyData = new ParaMap();
				keyData.put("moduleNo", "bank_server");
				keyData.put("dataSetNo", "query_targetId_byBill");
				customConditions.clear();
				customConditions = new ParaMap();
				customConditions.put("CUSTOM_CONDITION", " and tab.id in ("
						+ billId + ")");
				keyData.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
				ParaMap targetMap = dataSetDao.queryData(keyData);
				String targetIds = null;
				if (targetMap.getSize() > 0) {
					for (int i = 0; i < targetMap.getSize(); i++) {
						targetIds = targetIds == null || "".equals(targetIds) ? (String) targetMap
								.getRecordValue(i, "id") : targetIds + ","
								+ (String) targetMap.getRecordValue(i, "id");
					}
				}
				out.put("targetIds", targetIds);
			}
		}
		return out;
	}

	public void updateAccountBillStatus(String billId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "bank_server");
		keyData.put("dataSetNo", "update_account_bill_status");
		keyData.put("status", 2);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", " where id in (" + billId
				+ ")");
		keyData.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		dataSetDao.executeSQL(keyData);
	}

	public boolean isAllSubmit(String targetId) throws Exception {
		if (targetId == null || "".equals(targetId)) {
			return false;
		} else {
			ParaMap keyData = new ParaMap();
			keyData.put("moduleNo", "bank_server");
			keyData.put("dataSetNo", "query_billStatus_byTargetId");
			keyData.put("targetId", targetId);
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap result = dataSetDao.queryData(keyData);
			int count = 0;
			if (result.getSize() > 0) {
				count = (int) Math.ceil(Double.parseDouble(String
						.valueOf(result.getRecordValue(0, "num"))));
			}
			return count == 0 ? true : false;
		}
	}

	/**
	 * 处理银行的回执
	 * 
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public XmlOutput parseFeedBack(String response) throws Exception {
		XmlOutput output = null;
		XmlUtil xu = new XmlUtil();
		XmlVo xv = xu.readXML(response);
		output = xv.getOutput();
		return output;
	}

	public ParaMap save10002(String busiNo, String bankId, String trxCode,
			String xml) throws Exception {
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "bank_server");
		keyData.put("dataSetNo", "get_bank_business");
		keyData.put("sequenceNo", busiNo);
		keyData.put("trxCode", trxCode);
		keyData.put("bankId", bankId);
		ParaMap businessMap = dataSetDao.queryData(keyData);
		if (businessMap.getSize() > 0) {
			String status = String.valueOf(businessMap.getRecordValue(0,
					"status"));
			result.put("id", (String) businessMap.getRecordValue(0, "id"));
			result.put("billId",
					(String) businessMap.getRecordValue(0, "bill_id"));
			if (status != null && "2".equals(status)) {// 已经成功发送
				result.put("isEnd", 1);
				result.put("status", "00");
				result.put("retcode", "0021");
				result.put("retmsg", "交易流水号已经存在,之前交易成功!");
			} else {
				result.put("isEnd", 0);
			}
		} else {
			long sysTimeLong = System.currentTimeMillis();
			ParaMap offerMap = new ParaMap();
			offerMap.put("id", null);
			offerMap.put("trx_code", trxCode);
			offerMap.put("bank_id", bankId);
			offerMap.put("sequence_no", busiNo);
			offerMap.put("busi_date", DateUtils.getStr(DateUtils.now()));
			offerMap.put("content", xml);
			offerMap.put("status", 0);
			ParaMap format = new ParaMap();
			format.put("busi_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
			ParaMap bParaMap = dataSetDao.updateData("trans_bank_business",
					"id", offerMap, format);
			result.put("id", bParaMap.getString("id"));
			result.put("isEnd", 0);
		}
		return result;
	}

	public String createFeedback10002(XmlInput input, String status,
			String retcode, String retmsg) throws Exception {
		String feedbackMsg = "";
		XmlOutput output = new XmlOutput();
		output.setStatus(status);
		output.setRetcode(retcode);
		output.setRetmsg(retmsg);
		output.setCommseq(input.getBusino());
		XmlVo xv = new XmlVo();
		xv.setInput(input);
		xv.setOutput(output);
		XmlUtil xu = new XmlUtil();
		feedbackMsg = xu.writeXMLFile(xv);
		System.out.println("===============bankDao.createFeedback10002==="+feedbackMsg);
		feedbackMsg = URLEncoder.encode(feedbackMsg,CharsetUtils.gbk);
		//feedbackMsg = URLEncoder.encode(feedbackMsg);
		return feedbackMsg;
	}

	public void updateDealerBy10002(String draccbank, String draccno,
			String draccname, String billId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap paraMap = new ParaMap();
		paraMap.put("id", billId);
		paraMap.put("bidder_account_bank", draccbank);
		paraMap.put("bidder_account_no", draccno);
		paraMap.put("bidder_account_name", draccname);
		ParaMap bParaMap = dataSetDao.updateData("trans_account_bill", "id",
				paraMap, null);
	}

	public int isSub(String bankid, String craccno, String busiid,
			String currency) throws Exception {
		int isSub = -1;// 子账号
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "bank_server");
		keyData.put("dataSetNo", "get_bankAcount_byBankNo");
		keyData.put("bankNo", bankid);
		keyData.put("currency", currency);
		ParaMap dataMap = dataSetDao.queryData(keyData);
		if (dataMap.getSize() > 0) {
			int dataMapSize = dataMap.getSize();
			for (int i = 0; i < dataMapSize; i++) {
				String no = (String) dataMap.getRecordValue(i, "no");
				String in_account_mode = String.valueOf(dataMap.getRecordValue(
						i, "in_account_mode"));
				if (!no.equals(craccno) && "3".equals(in_account_mode)) {
					isSub = 1;
				}
				if (no.equals(craccno) && "1".equals(in_account_mode)) {
					isSub = 0;// 主账号
					break;
				}
			}
		} else {
			isSub = -1;
		}
		return isSub;
	}

	public boolean isRightChildNo(String craccno, String currency, String bankNo)
			throws Exception {
		boolean result = true;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", craccno);
		keyData.put("currency", currency);
		keyData.put("bank_no", bankNo);
		keyData.put("is_valid", 1);
		ParaMap dataMap = dataSetDao.querySimpleData("trans_child_account",
				keyData);
		if (dataMap.getSize() > 0) {
			keyData.clear();
			keyData.put("child_account_no", craccno);
			keyData.put("currency", currency);
			dataMap = dataSetDao.querySimpleData("trans_account_bill_apply",
					keyData);
			if (dataMap.getSize() > 0) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}

	public ParaMap getMainAccount(String no) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", no);
		keyData.put("in_account_mode", 1);
		ParaMap info = dataSetDao.querySimpleData("trans_account", keyData);
		ParaMap out = getRowOneInfo(info);
		return out;
	}

	public ParaMap getBankAccount(String mainNo, int businessType)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", mainNo);
		keyData.put("business_type", businessType);
		ParaMap info = dataSetDao.querySimpleData("trans_account", keyData);
		ParaMap out = getRowOneInfo(info);
		return out;
	}

	public ParaMap GetTransAccountBillApply(String applyId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", applyId);
		ParaMap info = dataSetDao.querySimpleData("trans_account_bill_apply",
				keyData);
		ParaMap out = getRowOneInfo(info);
		return out;
	}

	public ParaMap GetTransAccountBillApply(int isSub, String childAccountNo,
			String orderNo) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		if (isSub == 1) {
			sqlParams.put("dataSetNo", "get_transAccountBillApply_sub");
			sqlParams.put("childAccountNo", childAccountNo);
		} else if (isSub == 0) {
			sqlParams.put("dataSetNo", "get_transAccountBillApply_main");
			sqlParams.put("orderNo", orderNo);
		} else {
			return null;
		}
		ParaMap info = dataSetDao.queryData(sqlParams);
		return getRowOneInfo(info);
	}

	public BigDecimal getEarnestMoney(String targetId, String currency)
			throws Exception {
		BigDecimal result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("currency", currency);
		keyData.put("is_valid", 1);
		ParaMap info = dataSetDao.querySimpleData("trans_target_earnest_money",
				keyData);
		if (info.getSize() > 0) {
			try {
				result = new BigDecimal(String.valueOf(info.getRecordValue(0,
						"amount")));
			} catch (Exception e) {
				result = null;
			}
		}
		return result;
	}

	public ParaMap GetTableInfo(String tablename, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		keyData.put("is_valid", 1);
		ParaMap info = dataSetDao.querySimpleData(tablename, keyData);
		return getRowOneInfo(info);
	}

	public void updateBankBusinessStatus(String id, String billId, int status)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		keyData.put("status", status);
		keyData.put("bill_id", billId);
		dataSetDao.updateData("trans_bank_business", "id", keyData, null);
	}

	public void updateBillApplyChildAccountFreed(String id, int freed)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		keyData.put("child_account_freed", freed);
		dataSetDao.updateData("trans_account_bill_apply", "id", keyData, null);
	}

	public String saveTransBillAccount(ParaMap accountMap, String draccbank,
			String draccno, String draccname, BigDecimal amount, Date inDate,
			String applyId, int businessType, int billType, int status,
			String currency,String in_sequence_no) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap billMap = new ParaMap();
		billMap.put("id", null);
		billMap.put("business_type", businessType);
		billMap.put("bill_type", billType);
		billMap.put("account_bank", accountMap.getString("account_bank"));
		billMap.put("account_no", accountMap.getString("account_no"));
		billMap.put("account_name", accountMap.getString("account_name"));
		billMap.put("child_account_bank",
				accountMap.getString("child_account_bank"));
		billMap.put("child_account_no",
				accountMap.getString("child_account_no"));
		billMap.put("child_account_name",
				accountMap.getString("child_account_name"));
		billMap.put("bidder_account_bank", draccbank);
		billMap.put("bidder_account_no", draccno);
		billMap.put("bidder_account_name", draccname);
		billMap.put("bank_business_date", DateUtils.getStr(inDate));
		billMap.put("amount", amount);
		billMap.put("apply_id", applyId);
		billMap.put("is_bank_input", 0);
		billMap.put("status", status);
		billMap.put("is_valid", 1);
		billMap.put("create_date", DateUtils.getStr(DateUtils.now()));
		billMap.put("currency", currency);
		billMap.put("in_sequence_no", in_sequence_no);
		ParaMap format = new ParaMap();
		format.put("bank_business_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap dataMap = dataSetDao.updateData("trans_account_bill", "id",
				billMap, format);
		String billId = dataMap.getString("id");
		return billId;
	}

	public int getBillType(int earnestMoneyPay, int licenseStatus,
			Date endEarnestTime, Date beginLimitTime, Date inDate)
			throws Exception {
		int billType = 0;// 0其它款，1收保证金，2收服务费，3收成交款，4退成交款（委托方退给中心），5暂交款，6错转款
		if (1 == earnestMoneyPay) { // 已交足保证金
			billType = 6;
		} else {// 还未缴满保证金
				// 交保证金的时间已经过了
			Date judgeTime = endEarnestTime != null && beginLimitTime != null
					&& endEarnestTime.getTime() > beginLimitTime.getTime() ? beginLimitTime
					: endEarnestTime;
			if (judgeTime != null && inDate != null
					&& inDate.getTime() > judgeTime.getTime()) {
				billType = 6;// 指定的标的交保证金时间已过
			} else {
				billType = 1;// 要保存成保证金
			}
		}
		return billType;
	}

	public BigDecimal getBillSumAmount(String applyId, int billType)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "get_bill_sumamount");
		sqlParams.put("applyId", applyId);
		sqlParams.put("billType", billType);
		ParaMap info = dataSetDao.queryData(sqlParams);
		if (info.getSize() > 0) {
			String amount = String.valueOf(info.getRecordValue(0, "sumamount"));
			if (amount == null || "".equals(amount)
					|| amount.equalsIgnoreCase("null")) {
				return new BigDecimal("0");
			} else {
				return new BigDecimal(amount);
			}
		} else {
			return new BigDecimal("0");
		}
	}

	public void updateLicenseEarnestPay(String licenseId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", licenseId);
		keyData.put("earnest_money_pay", 1);
		dataSetDao.updateData("trans_license", "id", keyData, null);

		LicenseManageDao licenseDao = new LicenseManageDao();
		licenseDao.setLicenseStatusPass(null, null, licenseId);
	}

	public String getUnionDidderName(String licenseId) throws Exception {
		String result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "get_union_license_bidder");
		sqlParams.put("licenseId", licenseId);
		ParaMap info = dataSetDao.queryData(sqlParams);
		if (info.getSize() > 0) {
			result = (String) info.getRecordValue(0, "name");
			int infoSize = info.getSize();
			for (int i = 0; i < info.getSize(); i++) {
				String bidderName = (String) info.getRecordValue(i,
						"bidder_name");
				if (bidderName != null && !"".equals(bidderName)
						&& !bidderName.equalsIgnoreCase("null")) {
					result = result + "," + bidderName;
				}
			}
		} else {
			result = "";
		}
		return result;
	}

	public String getReceiptUse(String targetName, String targetNo,
			int businessType, int receiptType) throws Exception {
		String result = "";
		String targetShow = targetName == null || "".equals(targetName)
				|| targetName.equalsIgnoreCase("null") ? targetNo : targetName;
		String receiptShow = "";
		switch (receiptType) {
		case 0:
			receiptShow = "保证金";
			break;
		case 1:
			receiptShow = "成交款";
			break;
		case 2:
			receiptShow = "服务费";
			break;
		case 3:
			receiptShow = "应收款";
			break;
		case 4:
			receiptShow = "暂交款";
			break;
		case 5:
			receiptShow = "多交款";
			break;
		case 6:
			receiptShow = "错转款";
			break;
		case 7:
			receiptShow = "法院返回款";
			break;
		case 8:
			receiptShow = "首期款";
			break;
		case 9:
			receiptShow = "首期款";
			break;
		case 10:
			receiptShow = "按揭款";
			break;
		}
		switch (businessType) {
		case 0:
			result = "出让宗地：" + targetShow + "-" + receiptShow;
			break;
		case 1:
			result = "转让宗地：" + targetShow + "-" + receiptShow;
			break;
		case 2:
			result = "拍卖房产：" + targetShow + "-" + receiptShow;
			break;
		case 3:
			result = "挂牌房产：" + targetShow + "-" + receiptShow;
			break;
		case 4:
			result = "出让探矿：" + targetShow + "-" + receiptShow;
			break;
		case 5:
			result = "转让探矿：" + targetShow + "-" + receiptShow;
			break;
		case 6:
			result = "出让采矿：" + targetShow + "-" + receiptShow;
			break;
		case 7:
			result = "转染采矿：" + targetShow + "-" + receiptShow;
			break;
		}
		return result;
	}

	public ParaMap getRowOneInfo(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		if (in.getSize() > 0) {
			List filds = in.getFields();
			List record = (List) in.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					out.put((String) filds.get(i), record.get(i));
				}
			}
		} else {
			out = null;
		}
		return out;
	}

	public ParaMap getSubmitParam(String targetId) throws Exception {
		DecimalFormat df1 = new DecimalFormat("############0.00");
		ParaMap childNoMap = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bank_server");
		sqlParams.put("dataSetNo", "query_target_bill_amount");
		sqlParams.put("targetId", targetId);
		ParaMap dataMap = dataSetDao.queryData(sqlParams);
		if (dataMap.getSize() > 0) {
			int size = dataMap.getSize();
			for (int i = 0; i < size; i++) {
				BigDecimal amount = new BigDecimal(String.valueOf(dataMap
						.getRecordValue(i, "amount")));
				String licenseId = (String) dataMap.getRecordValue(i,
						"license_id");
				String accNo = (String) dataMap.getRecordValue(i,
						"child_account_no");
				String accBank = (String) dataMap.getRecordValue(i,
						"child_account_bank");
				String accName = (String) dataMap.getRecordValue(i,
						"child_account_name");
				String centerNo = (String) dataMap.getRecordValue(i,
						"account_no");
				String billId = (String) dataMap.getRecordValue(i, "billid");
				System.out.println("**********************************"
						+ billId);
				int businessType = (int) Math.ceil(Double.parseDouble(String
						.valueOf(dataMap.getRecordValue(0, "business_type"))));
				sqlParams = new ParaMap();
				sqlParams.put("no", centerNo);
				sqlParams.put("business_type", businessType);
				ParaMap bankMap = dataSetDao.querySimpleData("trans_account",
						sqlParams);
				if (bankMap.getSize() > 0) {
					int in_account_mode = (int) Math.ceil(Double
							.parseDouble(String.valueOf(bankMap.getRecordValue(
									0, "in_account_mode"))));
					String bankid = (String) bankMap.getRecordValue(0,
							"bank_no");
					if (in_account_mode == 1) {
						sqlParams = new ParaMap();
						sqlParams.put("id", billId);
						sqlParams.put("status", 2);
						dataSetDao.updateData("trans_account_bill", "id",
								sqlParams);
					} else if (in_account_mode == 3) {
						ParaMap accMap = new ParaMap();
						accMap.put("no", accNo);
						accMap.put("bank", accBank);
						accMap.put("name", accName);
						accMap.put("centerNo", centerNo);
						accMap.put("billId", billId);
						accMap.put("amount", amount);
						accMap.put("bankId", bankid);
						if (childNoMap.get(accNo) == null) {
							childNoMap.put(accNo, accMap);
						} else {
							ParaMap inMap = (ParaMap) childNoMap.get(accNo);
							accMap.put("amount", inMap.getBigDecimal("amount")
									.add(amount));
							String _billId = inMap.getString("billId") + ","
									+ billId;
							System.out
									.println("*****************************_billId="
											+ _billId);
							accMap.put("billId", _billId);
							childNoMap.put(accNo, accMap);
						}
					}
				}
			}
		}
		return childNoMap;
	}

	public String saveXml(String sequence_no, String bankId, String trxCode,
			String xml, String billId) throws Exception {
		PreparedStatement pstm = null;
		String sql = "insert into trans_bank_business(id,trx_code,bank_id,sequence_no,busi_date,content,status,bill_id)"
				+ "values(?,?,?,?,?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		String businssId = IDGenerator.newGUID();
		pstm.setString(1, businssId);
		pstm.setString(2, trxCode);
		pstm.setString(3, bankId);
		pstm.setString(4, sequence_no);
		pstm.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
		pstm.setString(6, xml);
		pstm.setInt(7, 0);
		pstm.setString(8, billId);
		pstm.executeUpdate();
		pstm.close();
		DataSourceManager.commit();
		return businssId;

		// DataSetDao dataSetDao = new DataSetDao();
		// ParaMap sqlMap = new ParaMap();
		// sqlMap.put("id", null);
		// sqlMap.put("trx_code", trxCode);
		// sqlMap.put("bank_id", bankId);
		// sqlMap.put("sequence_no", sequence_no);
		// sqlMap.put("busi_date", DateUtils.getStr(DateUtils.now()));
		// sqlMap.put("content", xml);
		// sqlMap.put("status", 0);
		// ParaMap format = new ParaMap();
		// format.put("busi_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		// ParaMap bParaMap = dataSetDao.updateData("trans_bank_business", "id",
		// sqlMap, format);
		// String id = bParaMap.getString("id");
		// return id;
	}

	public XmlOutput getFeedBack(String id) throws Exception {
		XmlOutput output = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		ParaMap info = dataSetDao.querySimpleData("trans_bank_business",
				keyData);
		if (info.size() > 0) {
			String response = (String) info.getRecordValue(0, "response");
			int status = (int) Math.ceil(Double.parseDouble(String.valueOf(info
					.getRecordValue(0, "status"))));
			if (status == 2 || status == 3) {
				if (response != null && !"".equals(response)
						&& !response.equalsIgnoreCase("null")) {
					XmlUtil xu = new XmlUtil();
					XmlVo xv = xu.readXML(response);
					output = xv.getOutput();
				}
			}
		}
		return output;
	}

	public XmlOutput getOutput(String id) throws Exception {
		XmlOutput backOuput = null;
		if (bankSendServiceXmlMap.get(id) != null) {
			backOuput = (XmlOutput) bankSendServiceXmlMap.get(id);
			System.out.println("--" + backOuput);
		}
		return backOuput;
	}

	public String getBankLSH() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long r1 = System.currentTimeMillis();
		String dateStr = sdf.format(DateUtils.now());
		if (dateStr != null) {
			dateStr = dateStr.replaceAll("-", "");
		}
		String r1Str = String.valueOf(r1);
		if (r1Str.length() >= 6) {
			return dateStr
					+ r1Str.substring(r1Str.length() - 6, r1Str.length());
		} else
			return dateStr + r1Str;
	}

	public String sendXml(String xml, String bankId, String businessId)
			throws Exception {
		String url = AppConfig.getPro("bankClienUrlPrefix");
		if (StrUtils.isNull(url)) {
			url = "http://127.0.0.1/bk/xml?module=bankClient&service=Bank&method=webSendXml";
		}
		url += "&xml=" + URLEncoder.encode(xml);
		url += "&bankId=" + bankId;
		url += "&businessId=" + businessId;

		HttpManager httpManager = new HttpManager();
		String result = httpManager.getDataString2(url);
		System.out.println("result_code==========>"+CharsetUtils.getEncoding(result));
		log.info("银行返回：begin");
		log.info("银行返回：" + result);
		log.info("银行返回：end");
		return result;
	}

	/**
	 * 旧的方法，已经不使用了,仅作参考
	 * 
	 * @return
	 * @throws Exception
	 */
	/*
	 * public String sendXml(String xml,String bankId, String businessId) throws
	 * Exception{ String url = AppConfig.getPro("bankClienUrlPrefix"); String
	 * port = "443"; String[] urlArray = url.split(":"); if(urlArray!=null &&
	 * urlArray.length>=3){ String portString = urlArray[2];
	 * if(portString.indexOf("/")>0){ port =
	 * portString.substring(0,portString.indexOf("/")); }else{ port =
	 * portString; } } HttpClient httpclient = new DefaultHttpClient(); String
	 * uri = url+"/xml?module=bankClient&service=Bank&method=webSendXml";
	 * log.debug("请求银行接口服务====>"+uri); if (StrUtils.isNotNull(url) &&
	 * url.substring(0, 5).equalsIgnoreCase("https")) { String keystoreFile =
	 * AppConfig.getPro("keystoreFile"); String keystorePass =
	 * AppConfig.getPro("keystorePass");
	 * 
	 * KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	 * FileInputStream instream = new FileInputStream(new File(keystoreFile));
	 * trustStore.load(instream, keystorePass.toCharArray()); SSLSocketFactory
	 * socketFactory = new SSLSocketFactory(trustStore);
	 * socketFactory.setHostnameVerifier
	 * (SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); Scheme sch = new
	 * Scheme("https", Integer.parseInt(port), socketFactory);
	 * httpclient.getConnectionManager().getSchemeRegistry().register(sch); }
	 * HttpPost httppost = new HttpPost(uri); List<NameValuePair>list=new
	 * ArrayList<NameValuePair>(); list.add(new BasicNameValuePair("xml", xml));
	 * list.add(new BasicNameValuePair("bankId", bankId)); list.add(new
	 * BasicNameValuePair("businessId", businessId)); httppost.setEntity(new
	 * UrlEncodedFormEntity(list,HTTP.UTF_8)); HttpResponse response =
	 * httpclient.execute(httppost); int code =
	 * response.getStatusLine().getStatusCode(); String result = null; if (code
	 * == HttpStatus.SC_OK) { result =
	 * EntityUtils.toString(response.getEntity());// 返回json格式： } return result;
	 * }
	 */

	public ParaMap getBankNoListNotSubmit() throws Exception {
		String sql = "select distinct ta.bank_no ,ta.bank_name from trans_account ta where ta.in_account_mode = 3 and ta.is_valid = 1 "
				+ "and exists ( "
				+ "             select tab.* from trans_account_bill tab "
				+ "             where tab.account_no = ta.no and tab.account_no <> tab.child_account_no and tab.status =1 and tab.apply_id is not null"
				+ "             and not exists (select * from trans_bank_month_detail where bill_id = tab.id ) "
				+ ") order by ta.bank_no ";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		return dataSetDao.queryData(sqlParams);
	}

}

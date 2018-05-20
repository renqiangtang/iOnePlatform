package com.after.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang.StringUtils;

import com.bank.dao.BankBaseDao;
import com.bank.util.BankUtil;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.FsUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.ResourceUtils;
import com.after.dao.TransAccountDao;

public class TransAccountService extends BaseService {
	/**
	 * 获取退款清单
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getRefundAccountListData(ParaMap inMap) throws Exception {

		ParaMap sqlParams = getPageInfo(inMap);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		sqlParams = getQueryCondition(sqlParams);
		TransAccountDao transAccountDao = new TransAccountDao();
		ParaMap out = transAccountDao.getRefundAccountListData(moduleId, dataSetNo,sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置和读取分页信息
	 */
	private ParaMap getPageInfo(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortDir))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (StringUtils.isEmpty(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "license_id asc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		return sqlParams;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置自定义查询条件
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		
		if(sqlParams.containsKey("goodsType")){
			String goodsType = sqlParams.getString("goodsType");
			if(StrUtils.isNotNull(goodsType)){
				if(goodsType.indexOf(",")!=-1){
					customCondition.append(" and  substr(tt.business_type,1,3 ) in ('301','401') ");
				}else{
					customCondition.append(" and  substr(tt.business_type,1,3) in ( '"+goodsType+"')  ");
				}
			}
		}
		
		if (inMap.containsKey("tab_id")) {
			String tab_id = inMap.getString("tab_id");
			if (StringUtils.isNotEmpty(tab_id)) {
				sqlParams.put("tab_id", tab_id);
				customCondition.append(" and tab.id = :tab_id ");
			}
		}
		
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StringUtils.isNotEmpty(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and tt.no like '%'||:no||'%' ");
			}
		}
		
		if (inMap.containsKey("accountBank")) {//竞买人开户行
			String accountBank = inMap.getString("accountBank");
			if (StringUtils.isNotEmpty(accountBank)) {
				sqlParams.put("accountBank", accountBank);
				customCondition.append(" and tab.account_bank like '%'||:accountBank||'%' ");
			}
		}
		
		if (inMap.containsKey("accountNo")) {//竞买人账号
			String accountNo = inMap.getString("accountNo");
			if (StringUtils.isNotEmpty(accountNo)) {
				sqlParams.put("accountNo", accountNo);
				customCondition.append(" and tab.account_no like '%'||:accountNo||'%'  ");
			}
		}
		
		if (inMap.containsKey("bidderAccountName")) {//竞买人户名
			String bidderAccountName = inMap.getString("bidderAccountName");
			if (StringUtils.isNotEmpty(bidderAccountName)) {
				sqlParams.put("bidderAccountName", bidderAccountName);
				customCondition.append(" and tab.bidder_account_name like '%'||:bidderAccountName||'%' ");
			}
		}
		
		if (inMap.containsKey("billType")) {//入账类型
			String billType = inMap.getString("billType");
			if (StringUtils.isNotEmpty(billType)) {
				sqlParams.put("billType", billType);
				customCondition.append(" and tab.bill_type = :billType ");
			}
		}
		String txtBeginDate="";
		String txtEenDate="";
		if (inMap.containsKey("txtBeginDate")) {//交易开始日期
			txtBeginDate = inMap.getString("txtBeginDate");
			sqlParams.put("txtBeginDate", txtBeginDate);
		}
		if (inMap.containsKey("txtEenDate")) {//交易结束日期
			txtEenDate = inMap.getString("txtEenDate");
			sqlParams.put("txtEenDate", txtEenDate);
		}
		
		if(StringUtils.isNotEmpty(txtBeginDate)&&StringUtils.isNotEmpty(txtEenDate)){
			customCondition.append(" And to_char(tab.bank_business_date,'yyyy-mm-dd')>= :txtBeginDate And to_char(tab.bank_business_date,'yyyy-mm-dd')<= :txtEenDate ");		
		}
		if(StringUtils.isNotEmpty(txtBeginDate)&&!StringUtils.isNotEmpty(txtEenDate)){
			customCondition.append(" And to_char(tab.bank_business_date,'yyyy-mm-dd')>= :txtBeginDate ");
		}
		if(!StringUtils.isNotEmpty(txtBeginDate)&&StringUtils.isNotEmpty(txtEenDate)){
			customCondition.append(" And to_char(tab.bank_business_date,'yyyy-mm-dd')<= :txtEenDate ");
		}
		
		
		
		
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}

	/**
	 * @param out
	 * @return
	 * @throws Exception
	 *             获取所有数据后解析成集合
	 */
	private List getDataInfo(ParaMap out) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("tab_id", "tab_id");
		fields.put("account_bank", "account_bank");
		fields.put("account_no", "account_no");
		fields.put("account_name", "account_name");
		fields.put("child_account_bank", "child_account_bank");
		fields.put("child_account_no", "child_account_no");
		fields.put("child_account_name", "child_account_name");
		fields.put("bidder_account_bank", "bidder_account_bank");
		fields.put("bidder_account_no", "bidder_account_no");
		fields.put("bidder_account_name", "bidder_account_name");
		fields.put("bank_business_date", "bank_business_date");
		fields.put("tab_status", "tab_status");
		fields.put("bill_type", "bill_type");
		fields.put("amount", "amount");
		fields.put("license_id", "license_id");
		fields.put("bidder_id", "bidder_id");
		fields.put("apply_no", "apply_no");
		fields.put("target_id", "target_id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("target_status", "target_status");
		fields.put("bidder_name", "bidder_name");
		fields.put("unit", "unit");
		fields.put("currency", "currency");
	
		
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}
	/**
	 * 查询退款清单详情
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getRefundAccountViewData(ParaMap inMap) throws Exception {
		ParaMap returnMap=new ParaMap();
		String moduleId=inMap.getString("moduleId");
		String tab_id=inMap.getString("tab_id");
		TransAccountDao transAccountDao = new TransAccountDao();
		returnMap=getQueryCondition(inMap);
		returnMap = transAccountDao.getRefundAccountListData(moduleId, "",returnMap);
		return returnMap;
	}	
	
	public ParaMap editStatusAccount(ParaMap inMap) throws Exception {
		ParaMap returnMap=new ParaMap();
		String tab_id=inMap.getString("tab_id");
		String status=inMap.getString("status");
		DataSetDao dataSetDao = new DataSetDao();
		returnMap.put("id", tab_id);
		returnMap.put("status", status);
		returnMap = dataSetDao.updateData("trans_account_bill", "id",returnMap, null);
		if(returnMap.getInt("state")==1){
			returnMap.clear();
			returnMap.put("message", "操作成功");
		}else{
			returnMap.clear();
			returnMap.put("message", "操作失败");
		}
		
		return returnMap;
	}
	
	/**
	 * 得到单位id
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
		if(result.getSize()>0){
			List filds = result.getFields();
			List record = (List)result.getRecords().get(0);
			if(filds != null && filds.size()>0 ){
				for(int i = 0 ; i < filds.size() ; i ++ ){
					if(filds.get(i).equals("id")){
						organId = (String)record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}
	
	public ParaMap initPageParams(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		//得到中心银行
		TransAccountDao transAccountDao = new TransAccountDao();
		List bankList = transAccountDao.getCenterBankList(goodsType);
		//得到区域列表
		List cantonList = transAccountDao.getCantonList();
		ParaMap out = new ParaMap();
		out.put("bankList", bankList);
		out.put("cantonList", cantonList);
		return out;
	}
	
	
	public ParaMap targetAccountBillList(ParaMap inMap)throws Exception{
		ParaMap sqlParams = inMap;
		
		//设置分页
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		sqlParams.put(DataSetDao.SQL_ORDER_BY, "begin_limit_time desc , target_id desc , license_id asc");
		
		//动态查询条件
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
	
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		
		if(sqlParams.containsKey("goodsType")){
			String goodsType = sqlParams.getString("goodsType");
			if(StrUtils.isNotNull(goodsType)){
				if(goodsType.indexOf("301")>=0){
					customCondition.append(" and  substr(tt.business_type,1,3 ) in ('301','401') ");
				}else{
					customCondition.append(" and  substr(tt.business_type,1,3) in ( '"+goodsType+"')  ");
				}
			}
		}
		
		if (inMap.containsKey("noticeNo")) {
			String noticeNo = inMap.getString("noticeNo");
			if (StringUtils.isNotEmpty(noticeNo)) {
				customCondition.append(" and tt.id in (select tntr.target_id from trans_notice tn , trans_notice_target_rel tntr where tntr.notice_id = tn.id and tn.no like '%'||:noticeNo||'%') " );
			}
		}
		
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StringUtils.isNotEmpty(no)) {
				customCondition.append(" and tt.no like '%'||:no||'%' ");
			}
		}
		
		if (inMap.containsKey("dealerBank")) {//竞买人开户行
			String dealerBank = inMap.getString("dealerBank");
			if (StringUtils.isNotEmpty(dealerBank)) {
				customCondition.append(" and tab.bidder_account_bank like '%'||:dealerBank||'%' ");
			}
		}
		
		if (inMap.containsKey("dealerName")) {//竞买人账号
			String dealerName = inMap.getString("dealerName");
			if (StringUtils.isNotEmpty(dealerName)) {
				customCondition.append(" and tab.bidder_account_name like '%'||:dealerName||'%'  ");
			}
		}
		
		if (inMap.containsKey("accountBank")) {//
			String accountBank = inMap.getString("accountBank");
			if (StringUtils.isNotEmpty(accountBank) && !accountBank.equals("-1")) {
				customCondition.append(" and tab.account_no like '%'||:accountBank||'%' ");
			}
		}
		
		if (inMap.containsKey("noticeBeginDate")) {//公告开始日期
			String noticeBeginDate = inMap.getString("noticeBeginDate");
			if(StringUtils.isNotEmpty(noticeBeginDate)){
				customCondition.append(" And tt.begin_notice_time>=to_date('"+noticeBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("noticeEndDate")) {//公告结束日期
			String noticeEndDate = inMap.getString("noticeEndDate");
			if(StringUtils.isNotEmpty(noticeEndDate)){
				customCondition.append(" And tt.end_notice_time<=to_date('"+timeAddDay(noticeEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("succBeginDate")) {//结束交易开始日期
			String succBeginDate = inMap.getString("succBeginDate");
			if(StringUtils.isNotEmpty(succBeginDate)){
				customCondition.append(" And tt.end_trans_time>=to_date('"+succBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("succEndDate")) {//结束交易结束日期
			String succEndDate = inMap.getString("succEndDate");
			if(StringUtils.isNotEmpty(succEndDate)){
				customCondition.append(" And tt.end_trans_time<=to_date('"+timeAddDay(succEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("inBeginDate")) {//结束交易开始日期
			String inBeginDate = inMap.getString("inBeginDate");
			if(StringUtils.isNotEmpty(inBeginDate)){
				customCondition.append(" And tab.bank_business_date>=to_date('"+inBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("inEndDate")) {//结束交易结束日期
			String inEndDate = inMap.getString("inEndDate");
			if(StringUtils.isNotEmpty(inEndDate)){
				customCondition.append(" And tab.bank_business_date<=to_date('"+timeAddDay(inEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("bidderCanton")) {
			String bidderCanton = inMap.getString("bidderCanton");
			if(StrUtils.isNotNull(bidderCanton) && !bidderCanton.equals("-1")){
				customCondition.append(" And tb.canton_id = '"+bidderCanton+"' ");		
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		
		TransAccountDao transAccountDao = new TransAccountDao();
		ParaMap out = transAccountDao.targetAccountBillList(sqlParams);
		List list = out.getListObj();
		int totalRowCount = out.getInt("totalRowCount");
		
		out.clear();
		out.put("Rows", list);
		out.put("Total", totalRowCount);
		return out;
		
	}
	
	
	public ParaMap refundAccountTarget(ParaMap inMap)throws Exception{
		ParaMap sqlParams = inMap;
		
		//设置分页
		sqlParams.put(DataSetDao.SQL_ORDER_BY, "tab_status asc, license_id asc");
		
		//动态查询条件
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
	
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		
		
		if (inMap.containsKey("targetId")) {
			String targetId = inMap.getString("targetId");
			if(StringUtils.isNotEmpty(targetId)){
				customCondition.append(" And tt.id = '"+targetId+"' ");		
			}
		}
		customCondition.append(" and tl.status <> 4 ");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		
		TransAccountDao transAccountDao = new TransAccountDao();
		ParaMap out = transAccountDao.targetAccountBillList(sqlParams);
		List list = out.getListObj();
		int totalRowCount = out.getInt("totalRowCount");
		
		out.clear();
		out.put("Rows", list);
		out.put("Total", totalRowCount);
		return out;
		
	}
	
	
	
	public ParaMap refundAccountBill(ParaMap inMap)throws Exception{
		ParaMap sqlParams = inMap;
		
		//设置分页
		sqlParams.put(DataSetDao.SQL_ORDER_BY, "tab_status asc, license_id asc");
		
		//动态查询条件
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
	
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		
		
		if (inMap.containsKey("billId")) {
			String billId = inMap.getString("billId");
			if(StringUtils.isNotEmpty(billId)){
				customCondition.append(" And tab.id = '"+billId+"' ");		
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		
		TransAccountDao transAccountDao = new TransAccountDao();
		ParaMap out = transAccountDao.targetAccountBillList(sqlParams);
		List list = out.getListObj();
		int totalRowCount = out.getInt("totalRowCount");
		
		out.clear();
		out.put("Rows", list);
		out.put("Total", totalRowCount);
		return out;
		
	}
	
	public ParaMap sendRefundAccount(ParaMap inMap)throws Exception{
		   ParaMap out = new ParaMap();
		   TransAccountDao transAccountDao = new TransAccountDao();
		   String sendData = inMap.getString("sendData");
		   //保存退款时间
		   String[] sendDataArray = sendData.split(";");
		   ParaMap messageMap = new ParaMap();
		   ParaMap billListMap = new ParaMap();
		   String message = "";
		   if(sendDataArray!=null && sendDataArray.length>0){
			   for(int i = 0 ; i < sendDataArray.length ; i ++ ){
				   String billAndTime = sendDataArray[i];
				   if(billAndTime.indexOf(",")<=0){
					   out.put("state", 0);
					   out.put("message", "发送申请失败，数据错误！");
				       return out;
				   }
				   String[] btArray = billAndTime.split(",");
				   if(btArray==null || btArray.length!=5){
					   out.put("state", 0);
					   out.put("message", "发送申请失败，数据错误！");
				       return out;
				   }
				   String billId = btArray[0];
				   String timeStr = btArray[1];
				   String no = btArray[2];
				   String name = btArray[3];
				   String bank = btArray[4];
				   ParaMap billIdTime = new ParaMap();
				   billIdTime.put("id", billId);
				   billIdTime.put("time", timeStr);
				   billIdTime.put("no", no);
				   billIdTime.put("name", name);
				   billIdTime.put("bank", bank);
				   
				   String xmlTimeStr = this.changeTimeFormat(timeStr);
				   ParaMap billMap = transAccountDao.getAccountBill(billId);
				   String status = billMap.getString("status");
				   if(!status.equals("2")){
					   out.put("state", 0);
					   out.put("message", "发送申请失败，入账单状态错误！");
				       return out;
				   }
//				   String msg =billMap.getString("in_sequence_no")+","+billMap.getString("bidder_account_name")+","+billMap.getString("bidder_account_no")+","+billMap.getString("bidder_account_bank")+","+
//				   billMap.getString("child_account_no")+","+billMap.getString("account_no")+","+changeMoneyFormat(billMap.getBigDecimal("amount"))+","+xmlTimeStr+";";
				   String msg =billMap.getString("in_sequence_no")+","+name+","+no+","+bank+","+
				               billMap.getString("child_account_no")+","+billMap.getString("account_no")+","+
				               changeMoneyFormat(billMap.getBigDecimal("amount"))+","+xmlTimeStr+";";
				  
				  
				   
				   
				   
				   String accountNo = billMap.getString("account_no");
				   if(messageMap.containsKey(accountNo)){
					  String oldMsg = messageMap.getString(accountNo);
					  messageMap.put(accountNo, oldMsg+msg);
					  
					  List list = billListMap.getList(accountNo);
					  list.add(billIdTime);
					  billListMap.put(accountNo, list);
				   }else{
					  messageMap.put(accountNo, msg);
					  
					  List list = new ArrayList();
					  list.add(billIdTime);
					  billListMap.put(accountNo, list);
				   }
				}
			   
			    Iterator iter = messageMap.entrySet().iterator();
				while (iter.hasNext()) {
					  Map.Entry entry = (Map.Entry) iter.next(); 
					  String key = (String)entry.getKey();
					  String val = (String)entry.getValue();
					  String bankNo = transAccountDao.getBankNoByAccountNo(key);
					  if(bankNo!=null){
						  BankBaseDao baseDao = (BankBaseDao) BankUtil.createInstance(bankNo);
						  ParaMap result = baseDao.refundAccount(val, bankNo);
						  if(result.getInt("state")==1){
							  List list = billListMap.getList(key);
							  if(list!=null && list.size()>0){
								  for(int i = 0 ; i < list.size() ; i ++ ){
									  ParaMap map = (ParaMap)list.get(i);
									  transAccountDao.updateRefundTime(map,result.getString("businessId"),result.getString("businessNo"));
								  }
							  }
							  message +="主账号为"+key+"的退款申请发送成功！;";
						  }else{
							  message +="主账号为"+key+"的退款申请发送失败！;";
						  }
					  }
					  
				}
				
			}
		   
		   out.put("state", 1);
		   out.put("message", message);
	       return out;
	}
	
	
	public byte[] expBillList(ParaMap inMap)throws Exception{
		ParaMap sqlParams = inMap;
		//设置分页
		sqlParams.put(DataSetDao.SQL_ORDER_BY, "begin_limit_time desc , target_id desc , license_id asc");
		
		//动态查询条件
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
	
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		
		if(sqlParams.containsKey("goodsType")){
			String goodsType = sqlParams.getString("goodsType");
			if(StrUtils.isNotNull(goodsType)){
				if(goodsType.indexOf("301")>=0){
					customCondition.append(" and  substr(tt.business_type,1,3 ) in ('301','401') ");
				}else{
					customCondition.append(" and  substr(tt.business_type,1,3) in ( '"+goodsType+"')  ");
				}
			}
		}
		
		if (inMap.containsKey("noticeNo")) {
			String noticeNo = inMap.getString("noticeNo");
			if (StringUtils.isNotEmpty(noticeNo)) {
				customCondition.append(" and tt.id in (select tntr.target_id from trans_notice tn , trans_notice_target_rel tntr where tntr.notice_id = tn.id and tn.no like '%'||:noticeNo||'%') " );
			}
		}
		
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StringUtils.isNotEmpty(no)) {
				customCondition.append(" and tt.no like '%'||:no||'%' ");
			}
		}
		
		if (inMap.containsKey("dealerBank")) {//竞买人开户行
			String dealerBank = inMap.getString("dealerBank");
			if (StringUtils.isNotEmpty(dealerBank)) {
				customCondition.append(" and tab.bidder_account_bank like '%'||:dealerBank||'%' ");
			}
		}
		
		if (inMap.containsKey("dealerName")) {//竞买人账号
			String dealerName = inMap.getString("dealerName");
			if (StringUtils.isNotEmpty(dealerName)) {
				customCondition.append(" and tab.bidder_account_name like '%'||:dealerName||'%'  ");
			}
		}
		
		if (inMap.containsKey("accountBank")) {//
			String accountBank = inMap.getString("accountBank");
			if (StringUtils.isNotEmpty(accountBank) && !accountBank.equals("-1")) {
				customCondition.append(" and tab.account_no like '%'||:accountBank||'%' ");
			}
		}
		
		if (inMap.containsKey("noticeBeginDate")) {//公告开始日期
			String noticeBeginDate = inMap.getString("noticeBeginDate");
			if(StringUtils.isNotEmpty(noticeBeginDate)){
				customCondition.append(" And tt.begin_notice_time>=to_date('"+noticeBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("noticeEndDate")) {//公告结束日期
			String noticeEndDate = inMap.getString("noticeEndDate");
			if(StringUtils.isNotEmpty(noticeEndDate)){
				customCondition.append(" And tt.end_notice_time<=to_date('"+timeAddDay(noticeEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("succBeginDate")) {//结束交易开始日期
			String succBeginDate = inMap.getString("succBeginDate");
			if(StringUtils.isNotEmpty(succBeginDate)){
				customCondition.append(" And tt.end_trans_time>=to_date('"+succBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("succEndDate")) {//结束交易结束日期
			String succEndDate = inMap.getString("succEndDate");
			if(StringUtils.isNotEmpty(succEndDate)){
				customCondition.append(" And tt.end_trans_time<=to_date('"+timeAddDay(succEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("inBeginDate")) {//结束交易开始日期
			String inBeginDate = inMap.getString("inBeginDate");
			if(StringUtils.isNotEmpty(inBeginDate)){
				customCondition.append(" And tab.bank_business_date>=to_date('"+inBeginDate+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("inEndDate")) {//结束交易结束日期
			String inEndDate = inMap.getString("inEndDate");
			if(StringUtils.isNotEmpty(inEndDate)){
				customCondition.append(" And tab.bank_business_date<=to_date('"+timeAddDay(inEndDate,1)+"','yyyy-mm-dd') ");		
			}
		}
		if (inMap.containsKey("bidderCanton")) {
			String bidderCanton = inMap.getString("bidderCanton");
			if(StrUtils.isNotNull(bidderCanton) && !bidderCanton.equals("-1")){
				customCondition.append(" And tb.canton_id = '"+bidderCanton+"' ");		
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		TransAccountDao transAccountDao = new TransAccountDao();
		ParaMap out = transAccountDao.targetAccountBillList(sqlParams);
		List billList = out.getListObj();
		
		String[] colum1 = new String[16];
		String[] colum2 = new String[16];
		colum1[0]="标的号";
		colum1[1]="公告号";
		colum1[2]="竞买/竞卖人";
		colum1[3]="竞买人联系电话";
		colum1[4]="保证金";
		colum1[5]="实交保证金";
		colum1[6]="汇入日期";
		colum1[7]="交款人";
		colum1[8]="交款银行";
		colum1[9]="交款银行账号";
		colum1[10]="中心银行";
		colum1[11]="中心银行子账号";
		colum1[12]="中心银行主账号";
		colum1[13]="是否竞得";
		colum1[14]="退保证金时间";
		colum1[15]="备注";
		colum2[0]="no";
		colum2[1]="notice_name";
		colum2[2]="bidder_name";
		colum2[3]="mobile";
		colum2[4]="earnest_money";
		colum2[5]="amount";
		colum2[6]="bank_business_date";
		colum2[7]="bidder_account_name";
		colum2[8]="bidder_account_bank";
		colum2[9]="bidder_account_no";
		colum2[10]="account_bank";
		colum2[11]="child_account_no";
		colum2[12]="account_no";
		colum2[13]="license_status";
		colum2[14]="refund_time";
		colum2[15]="target_status";
		List list = new ArrayList();
		DecimalFormat df = new DecimalFormat("###,###,###,###.00");
		for(int i = 0 ; i < billList.size() ; i ++ ){
			ParaMap listMap = new ParaMap();
			ParaMap billObj = (ParaMap)billList.get(i);
			listMap.put("no", billObj.getString("no"));
			listMap.put("notice_name", billObj.getString("notice_name"));
			listMap.put("bidder_name", billObj.getString("bidder_name"));
			listMap.put("mobile", billObj.getString("mobile"));
			listMap.put("earnest_money", df.format(billObj.getBigDecimal("earnest_money").doubleValue()));
			listMap.put("amount", df.format(billObj.getBigDecimal("amount").doubleValue()));
			listMap.put("bank_business_date", billObj.getString("bank_business_date"));
			listMap.put("bidder_account_name", billObj.getString("bidder_account_name"));
			listMap.put("bidder_account_bank", billObj.getString("bidder_account_bank"));
			listMap.put("bidder_account_no", billObj.getString("bidder_account_no"));
			listMap.put("account_bank", billObj.getString("account_bank"));
			listMap.put("child_account_no", billObj.getString("child_account_no"));
			listMap.put("account_no", billObj.getString("account_no"));
			int licenseStatus = billObj.getInt("license_status");
			String licenseStatusStr = "";
			if(licenseStatus<1){
				licenseStatusStr = "无效";
			}else if(licenseStatus==1){
				licenseStatusStr = "有效";
			}else if(licenseStatus==2){
				licenseStatusStr = "未竞得";
			}else{
				licenseStatusStr = "已竞得";
			}
			listMap.put("license_status", licenseStatusStr);
			String refundTime = billObj.getString("refund_time");
			listMap.put("refund_time", StrUtils.isNull(refundTime)?"":refundTime);
			listMap.put("target_status", "");
			list.add(listMap);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		String nowStr = sdf.format(new Date());
		String file = this.createExcel("财务退款("+nowStr+").xls", "refundAccountExcel", colum1, colum2, list);
		byte[] buf = this.downloadFile("财务退款("+nowStr+").xls", file);
		
		return buf;
	}
	
	 /** 
     * 生成Excel文件 
     * @param path 
     * @param dataList 
     * @return 
     */  
    public String createExcel(String filename ,String path,String[] column1 ,String[] column2, List dataList) throws Exception{  
        File file = FsUtils.get(path);  
        file = new File(file,filename);  
        WritableWorkbook book=null;  
        try {  
            book = Workbook.createWorkbook(file);  
            WritableSheet sheet = book.createSheet(file.getName(), 0);  
            this.setHeader(sheet,column1); //设置Excel标题信息  
            this.setBody(sheet,dataList,column2); // 设置Excel内容主体信息  
            book.write();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (WriteException e) {  
            e.printStackTrace();  
        } catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            try {  
                book.close();  
            } catch (WriteException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file.getAbsolutePath();  
    } 
	
	
	/** 
     * 设置Excel标题信息 
     * @param sheet 
     * @param column 
     * @throws WriteException 
     */  
    public void setHeader(WritableSheet sheet,String[] column) throws WriteException{ 
    	WritableFont font = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
        WritableCellFormat headerFormat = new  WritableCellFormat(font);  
        headerFormat.setAlignment(Alignment.CENTRE);  //水平居中对齐  
        headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);   //竖直方向居中对齐  
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);  
        for(int i=0;i<column.length;i++){  
            Label label = new Label(i,0,column[i],headerFormat);  
            sheet.addCell(label);  
            sheet.setColumnView(i, 30);  
            sheet.setRowView(0, 500);  
        }  
    }  
    
    /** 
     * 设置Excel内容主体信息 
     * @param sheet 
     * @param rowList 
     * @param column 
     * @throws Exception 
     */  
    public void setBody(WritableSheet sheet,List rowList,String[] column) throws Exception{  
       WritableCellFormat bodyFormat = new  WritableCellFormat();  
       bodyFormat.setAlignment(Alignment.CENTRE); //水平居中对齐  
       bodyFormat.setVerticalAlignment(VerticalAlignment.CENTRE);   //竖直方向居中对齐  
       bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);  
       Label label = null;  
       for(int i=0;i<rowList.size();i++){  
           ParaMap obj = (ParaMap)rowList.get(i);  
           for(int j=0;j<column.length;j++){  
              label = new Label(j,i+1,String.valueOf(((ParaMap)obj).get(column[j].toLowerCase())),bodyFormat);  
              sheet.addCell(label);  
              sheet.setRowView(i+1, 350);  
           }  
       }  
    } 
    
    
    /**
	 * 下载文件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadFile(String fileName , String filePath) throws Exception {
		this.getResponse().reset();
		this.getResponse().addHeader("Content-Disposition","attachment; filename=\"" + new String(fileName.getBytes("gb2312"),"ISO8859-1")+ "\";");
		this.getResponse().setContentType("application/octet-stream; charset=UTF-8");
		byte[] buf = ResourceUtils.getBytes(filePath);
		return buf;
    }
	
	
	
	public String changeTimeFormat(String old)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(old);
		return sdf2.format(date);
	}
	
	public String timeAddDay(String dateStr , int add)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateStr);
		return sdf.format(new Date(date.getTime()+add*24L*60L*60L*1000L));
	}
	
	public String changeMoneyFormat(BigDecimal old)throws Exception{
		DecimalFormat df1 = new DecimalFormat("############0.00"); 
		String amoutStr = old == null ? "0" : df1.format(old.doubleValue());
		if(amoutStr!=null&&!"".equals(amoutStr)){
			if(amoutStr.indexOf(".")>0){
				String[] as = amoutStr.split("\\.");
				String secondAS = "00";
				if(as.length>1){
					secondAS = as[1];
					if(secondAS==null||secondAS.equals("")){
						secondAS = "00" ;
					}else if(secondAS.length()==0){
						secondAS = "00" ;
					}else if(secondAS.length()==1){
						secondAS = secondAS+"0" ;
					}else if(secondAS.length()==2){
						secondAS = secondAS ;
					}else if(secondAS.length()>2){
						secondAS = secondAS.substring(0,2) ;
					}
				}
				amoutStr = as[0]+secondAS;
			}else{
				amoutStr = old.toString()+"00";
			}
		}
		return amoutStr;
	}
	
}

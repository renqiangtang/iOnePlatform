package com.bidder.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bank.dao.BankBaseDao;
import com.bank.util.BankUtil;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.tradeplow.engine.EngineDao;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sms.dao.SmsDao;
import com.trademan.dao.LicenseManageDao;

public class PlowApplyDao extends BaseDao {
	
	/**
	 * 交易中交易物区域
	 * @param businessType
	 * @return
	 * @throws Exception
	 */
	public ParaMap goodsCantonList(String businessType)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_trans_target_goods_cantons");
		sqlParams.put("businessType", businessType);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 能够交易的商品列表
	 * @param sqlParams
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public ParaMap transTargetlist(ParaMap sqlParams) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_trans_target_plow_list");
		return dataSetDao.queryData(sqlParams);
	}
	
	
	/**
	 * 根据id得到标的信息
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransAccount(String businessType) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_trans_account_bank_list");
		sqlParams.put("businessType", businessType);
		return dataSetDao.queryData(sqlParams);
	}
	
	
	/**
	 * 判断是否能够购买该标的
	 * @param bidderId
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap isCanBuy(String bidderId , ParaMap target )throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("bidder_id", bidderId);
		keyData.put("target_id", target.getString("id"));
		keyData.put("is_valid", 1);
		ParaMap bidderLicenseMap = dataSetDao.querySimpleData("trans_license", keyData);
		if(bidderLicenseMap.getSize()>0){
			out.put("state", 0);
			out.put("message", "您已申请了该耕地指标。");
			return out;
		}
		keyData.clear();
		keyData.put("moduleNo", "trans_bidder_license");
		keyData.put("dataSetNo", "get_target_trust_bidder");
		keyData.put("targetId", target.getString("id"));
		ParaMap biddersMap = dataSetDao.queryData(keyData);
		if(biddersMap !=null && biddersMap.getSize()>0){
			for(int i = 0 ; i < biddersMap.getSize() ; i ++ ){
				String trustBidderId = biddersMap.getRecordString(i, "id");
				if(trustBidderId.equals(bidderId)){	
					out.put("state", 0);
					out.put("message", "该耕地指标是您自己发布的。");
					return out;
				}
			}
		}
		
		
		
		int status = target.getInt("status");
		int is_stop = target.getInt("is_stop");
		int is_suspend = target.getInt("is_suspend");
		Date beginApplyTime = target.getDate("begin_apply_time");
		Date endApplyTime = target.getDate("end_apply_time");
		if(status!=3 && status!=4){
			out.put("state", 0);
			out.put("message", "该耕地指标状态错误。");
			return out;
		}
		if(is_stop==1){
			out.put("state", 0);
			out.put("message", "该耕地指标已终止。");
			return out;
		}
		if(is_suspend==1){
			out.put("state", 0);
			out.put("message", "该耕地指标已中止。");
			return out;
		}
		if((DateUtils.now()).getTime() < beginApplyTime.getTime()){
			out.put("state", 0);
			out.put("message", "该耕地指标尚未到申请时间。");
			return out;
		}
		if((DateUtils.now()).getTime() > endApplyTime.getTime()){
			out.put("state", 0);
			out.put("message", "该耕地指标已经过了申请时间。");
			return out;
		}
		out.put("state", 1);
		return out;
	}
	
	/**
	 * 检测并得到联合竞买人
	 * @param bidderNames
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBidders(List unitProportionsList)throws Exception{
		ParaMap out = new ParaMap();
		if(unitProportionsList==null || unitProportionsList.size()<1){
			out.put("state", 0);
		    out.put("message", "无有效的联合竞买人。");
	    	return out;
		}
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		ParaMap conditionMap = new ParaMap();
		List bidderList = new ArrayList();
		BigDecimal allPercent = new BigDecimal("0");
		String bidderNames = "";
		String repeatName = "";
		String notGetName = "";
		String percentErrorName = "";
		for(int i = 0 ; i < unitProportionsList.size(); i++){
			ParaMap bidderMap = (ParaMap)unitProportionsList.get(i);
			String name = "";
			String value = "";
			Iterator iter = bidderMap.entrySet().iterator(); 
			while (iter.hasNext()) { 
				 Map.Entry entry = (Map.Entry) iter.next(); 
				 name = String.valueOf(entry.getKey()); 
				 value = String.valueOf(entry.getValue()); 
			}
			if((","+bidderNames+",").indexOf(","+name+",")>=0){
				repeatName = StrUtils.isNull(repeatName) ? name : repeatName+","+name;
			}else{
				sqlParams.clear();
				conditionMap.clear();
				conditionMap.put("CUSTOM_CONDITION", "and name = '"+name+"'");
				sqlParams.put(dao.SQL_CUSTOM_CONDITIONS, conditionMap);
				sqlParams.put("moduleNo", "trans_web_main_license");
				sqlParams.put("dataSetNo", "query_union_bidder");
				ParaMap result = dao.queryData(sqlParams);
				if(result.getSize()>0){
					bidderMap.put("bidder", result);
					bidderNames = StrUtils.isNull(bidderNames) ? name : bidderNames+","+name;
				}else{
					notGetName = StrUtils.isNull(notGetName) ? name : notGetName+","+name;
				}
			}
			BigDecimal percent = new BigDecimal("0");
		    try{
		    	percent = new BigDecimal(String.valueOf(value)); 
		    	if(percent.compareTo(new BigDecimal("0"))<=0){
		    		percentErrorName = StrUtils.isNull(percentErrorName) ? name : percentErrorName+","+name;
		    	}else{
		    		bidderMap.put("percent", percent);
		    	}
		    }catch(Exception e){
		    	percentErrorName = StrUtils.isNull(percentErrorName) ? name : percentErrorName+","+name;
			}
			allPercent = allPercent.add(percent);
			bidderList.add(bidderMap);
		}
		if(StrUtils.isNotNull(notGetName)){
		    out.put("state", 0);
		    out.put("message", "联合竞买人"+notGetName+"不是有效的竞买用户。");
	    	return out;
		}	
		if(StrUtils.isNotNull(repeatName)){
		    out.put("state", 0);
		    out.put("message", "联合竞买人"+repeatName+"重复。");
	    	return out;
		}
		if(StrUtils.isNotNull(percentErrorName)){
			out.put("state", 0);
		    out.put("message", "联合竞买人"+repeatName+"比例错误。");
	    	return out;
		}
		if(StrUtils.isNull(bidderNames)){
			out.put("state", 0);
		    out.put("message", "无有效的联合竞买人。");
	    	return out;
		}
		if(allPercent.compareTo(new BigDecimal("100"))>=0 || allPercent.compareTo(new BigDecimal("0"))<=0){
		    out.put("state", 0);
		    out.put("message", "联合竞买人比例错误。");
	    	return out;
		}
	    out.put("state", 1);
		out.put("bidderList", bidderList);	
		return out;
	}
	
	
	/**
	 * 获取子账号
	 * @param accountId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBankAccount(String accountId , String userId)throws Exception{
		ParaMap returnMap=new ParaMap();
		
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", accountId);
		ParaMap transAccountMap=dao.querySimpleData("trans_account", sqlParams);//查询交易中心账户表
		if(transAccountMap.getSize()>0){
			returnMap.put("mainAccount", (ParaMap)transAccountMap.getListObj().get(0));//把交易中心账号返回
			
			int inAccountMode= (Integer) transAccountMap.getRecordValue(0, "in_account_mode") ;//进款账号模式。
			String no=(String) transAccountMap.getRecordValue(0, "no");//供调用银行接口是传递参数
			String name=(String) transAccountMap.getRecordValue(0, "name");//供调用银行接口是传递参数
			String bankNo=(String) transAccountMap.getRecordValue(0, "bank_no");//供调用银行接口是传递参数
			String bankName=(String) transAccountMap.getRecordValue(0, "bank_name");//供调用银行接口是传递参数
			String bankId = (String) transAccountMap.getRecordValue(0, "bank_id");
			String currency = (String) transAccountMap.getRecordValue(0, "currency");
			
			returnMap.put("inAccountMode", inAccountMode);//把进款账号模式返回
			if(inAccountMode==0||inAccountMode==1){//0基本账户手工模式；1基本账号直连直通模式,返回竞买流水号
				ParaMap transChildAccountMap = new ParaMap();
				transChildAccountMap.put("no", no);
				transChildAccountMap.put("name", name);
				transChildAccountMap.put("bank_id", bankId);
				transChildAccountMap.put("bank_no", bankNo);
				transChildAccountMap.put("bank_name", bankName);
				transChildAccountMap.put("main_account_id", accountId);
				transChildAccountMap.put("main_account_no", no);
				transChildAccountMap.put("main_account_name", name);
				transChildAccountMap.put("main_account_bank_id", bankId);
				transChildAccountMap.put("main_account_bank_name", bankName);
				transChildAccountMap.put("main_account_bank_no", bankNo);
				transChildAccountMap.put("create_user_id", userId);
				transChildAccountMap.put("account_type", 1);
				transChildAccountMap.put("currency", currency);
				returnMap.put("subAccount", transChildAccountMap);//把得到的子账号返回
				returnMap.put("state", 1);
				return returnMap;
			}else if(inAccountMode==2){//虚拟子账号模式,返回子账号表中的子账号
				sqlParams.clear();
				sqlParams.put("moduleNo", "trans_web_main_license");
				sqlParams.put("dataSetNo", "query_trans_child_account");
				sqlParams.put("mainAccountId", accountId);
				sqlParams.put("mainAccountBankId", bankId);
				ParaMap transChildAccountMap = new ParaMap();
				transChildAccountMap=dao.queryData(sqlParams);
				if(transChildAccountMap.getSize()>0){
					returnMap.put("state", 1);
					returnMap.put("childAccountId", transChildAccountMap.getRecordValue(0, "id"));
					returnMap.put("subAccount", (ParaMap)transChildAccountMap.getListObj().get(0));//把得到的子账号返回
				}else{
					returnMap.put("state", 0);
					returnMap.put("message", "没有空闲的子账号,请录入。");
				}
			}else if(inAccountMode==3){//直连直通子账号模式,返回调用银行开户接口返回的子账号
				//此处调用银行接口
				String childNo = null;
				String versionMode = AppConfig.getPro("versionMode");
				if (versionMode != null && versionMode.equalsIgnoreCase("demo")){// 演示版本
					childNo = String.valueOf(DateUtils.nowTime());
				}else {
					BankBaseDao baseDao = (BankBaseDao)BankUtil.createInstance(bankNo);
					try {
						childNo = baseDao.createChildAccount(name, bankNo, no , currency);
						//childNo = String.valueOf(DateUtils.nowTime());
					} catch (Exception e) {
						returnMap.put("state", 0);
						returnMap.put("message", "获取子账号出错。");
						return returnMap;
					}
				}
				if(StrUtils.isNotNull(childNo)){
					String childAccountId = null;
					ParaMap tcaMap = new ParaMap();//查询是否已经存在该子账号
					tcaMap.put("no", childNo);
					tcaMap.put("bank_id", bankId);
					tcaMap.put("main_account_no", no);
					ParaMap queryReturn = dao.querySimpleData("trans_child_account", tcaMap);
					if(queryReturn!=null && queryReturn.getSize()>0){
						childAccountId = queryReturn.getRecordString(0, "id");
						int use_count = queryReturn.getRecordInteger(0, "use_count");
						tcaMap.clear();
						tcaMap.put("id", childAccountId);
						tcaMap.put("use_count", use_count+1);
						tcaMap.put("currency", currency);
						dao.updateData("trans_child_account", "id", tcaMap, null); //修改使用次数
					}else{
						ParaMap saveOut = new ParaMap();// 保存子账号
						saveOut.put("id", "");
						saveOut.put("no", childNo);
						saveOut.put("name", name);
						saveOut.put("bank_id", bankId);
						saveOut.put("bank_no", bankNo);
						saveOut.put("bank_name", bankName);
						saveOut.put("main_account_id", accountId);
						saveOut.put("main_account_no", no);
						saveOut.put("main_account_name", name);
						saveOut.put("main_account_bank_id", bankId);
						saveOut.put("main_account_bank_name", bankName);
						saveOut.put("main_account_bank_no", bankNo);
						saveOut.put("create_user_id", userId);
						saveOut.put("account_type", 1);
						saveOut.put("currency", currency);
						ParaMap saveReturn = dao.updateData("trans_child_account", "id", saveOut, null);
						childAccountId = saveReturn.getString("id");
					}
					//以下是查询子账号
					ParaMap keyData = new ParaMap();
					keyData.put("id", childAccountId);
					ParaMap transChildAccountMap=dao.querySimpleData("trans_child_account",keyData);
					if(transChildAccountMap.getSize()>0){
						returnMap.put("state", 1);
						returnMap.put("childAccountId", transChildAccountMap.getRecordValue(0, "id"));
						returnMap.put("subAccount", (ParaMap)transChildAccountMap.getListObj().get(0));//把得到的子账号返回
					}else{
						returnMap.put("state", 0);
						returnMap.put("message", "查询子账号出错。");
					}
				}else{
					returnMap.put("state", 0);
					returnMap.put("message", "调用银行借款未返回账号。");
				}

			}else{
				returnMap.put("state", 0);
				returnMap.put("message", "交款账号模式为空。");
			}
		}else{
			returnMap.put("state", 0);
			returnMap.put("message", "没有查询到交款账户。");
		}
		return returnMap;
	}
	
	
	
	/**
	 * 保存竞买申请
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveLicense(String bidderId , String targetId , String trustType , String trustPrice , String userId) throws Exception{
		ParaMap out=new ParaMap();
		LicenseManageDao licenseManage = new LicenseManageDao();
		String licenseNo= null;
		ParaMap licenseNoMap =	licenseManage.getNewLicenseNo(null,null,targetId);
		if("1".equals(licenseNoMap.getString("state"))){
			licenseNo = licenseNoMap.getString("no");
		}else{
			return licenseNoMap;
		}
		String applyNo= null;
		ParaMap applyNoMap =	licenseManage.getNewApplyNo(null,null,targetId);
		if("1".equals(applyNoMap.getString("state"))){
			applyNo = applyNoMap.getString("no");
		}else{
			return applyNoMap;
		}
		String cardNo = null;
		ParaMap cardNoMap =	licenseManage.getNewCardNo(null,null,targetId);
		if("1".equals(cardNoMap.getString("state"))){
			cardNo = cardNoMap.getString("no");
		}else{
			return cardNoMap;
		}
		String filetime=DateUtils.getStr(DateUtils.now());
		ParaMap transLicenseMap=new ParaMap();
		ParaMap format=new ParaMap();
		transLicenseMap.put("id", "");//竞买人ID
		transLicenseMap.put("bidder_id", bidderId);//竞买人ID
		transLicenseMap.put("target_id", targetId);//标的ID
		transLicenseMap.put("license_no", licenseNo);//竞买申请号，全局唯一随机编号
		transLicenseMap.put("apply_date", filetime);//申请日期
		//transLicenseMap.put("card_no", "");//竞买号牌，网上交易无意义
		transLicenseMap.put("apply_no", applyNo);//申请编号，此号是打印有竞买申请书上的编号
		transLicenseMap.put("card_no", cardNo);//牌号
		if(StrUtils.isNotNull(trustType)){//是否有委托报价
			transLicenseMap.put("trust_type", trustType);//委托报价方式。0无，1系统自行报委托报价以下的报价，2网络自助报价，3允许电话委托报价
			if("1".equals(trustType)){//如果委托报价类型为委托系统自动报价，则需要最高委托金额
				if(StrUtils.isNotNull(trustPrice)){
					transLicenseMap.put("trust_price", Double.parseDouble(trustPrice));//委托金额
				}
			}
		}
		String versionMode = AppConfig.getPro("versionMode");
		if (versionMode != null && versionMode.equalsIgnoreCase("demo")){
			transLicenseMap.put("status", 1);
		}else{
			transLicenseMap.put("status", 0);
		}
		transLicenseMap.put("create_user_id", userId);//创建人ID
		format.put("apply_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		DataSetDao dao = new DataSetDao();
		out = dao.updateData("trans_license", "id", transLicenseMap, format);
		if("1".equals(out.getString("state")) && versionMode != null && versionMode.equalsIgnoreCase("demo")){
			EngineDao engineDao = new EngineDao();
			engineDao.addUserId(targetId, userId);
		}
		return out;
	}
	
	
	
	/**
	 * 保存入账申请单
	 * @param licenseId
	 * @param accountMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveAccountBillApply(String licenseId ,String targetId,ParaMap accountMap)throws Exception{
		LicenseManageDao licenseManage = new LicenseManageDao();
		String orderNo= null;
		ParaMap orderNoMap =	licenseManage.getNewOrderNo(null,null,targetId);//获取竞买申请号
		if("1".equals(orderNoMap.getString("state"))){
			orderNo = orderNoMap.getString("no");
		}else{
			return orderNoMap;
		}
		ParaMap transAccountBillApplyMap=new ParaMap();
		ParaMap mainAccount = (ParaMap)accountMap.get("mainAccount");
		ParaMap subAccount =  (ParaMap)accountMap.get("subAccount");
		String account_bank= mainAccount.getString("bank_name");//中心账号开户行
		String account_no= mainAccount.getString("no");//中心账号
		String account_name= mainAccount.getString("name");//中心账号
		transAccountBillApplyMap.put("id", "");
		transAccountBillApplyMap.put("ref_id", licenseId);
		transAccountBillApplyMap.put("order_no", orderNo);//入账订单流水号，此号在划款时必须写在备注信息中,暂时写为空
		transAccountBillApplyMap.put("account_bank", account_bank);
		transAccountBillApplyMap.put("account_no", account_no);
		transAccountBillApplyMap.put("account_name", account_name);
		if(subAccount!=null){//子账号信息,用主账号的情况下子账号为空
			String child_account_bank= subAccount.getString("bank_name");//中心子账户开户行
			String child_account_no= subAccount.getString("no");//中心子账户账号
			String child_account_name= subAccount.getString("name");//中心子账户户名
			transAccountBillApplyMap.put("child_account_bank", child_account_bank);
			transAccountBillApplyMap.put("child_account_no", child_account_no);
			transAccountBillApplyMap.put("child_account_name", child_account_name);
			transAccountBillApplyMap.put("currency", subAccount.getString( "currency"));
		}else{
			transAccountBillApplyMap.put("child_account_bank", account_bank);
			transAccountBillApplyMap.put("child_account_no", account_no);
			transAccountBillApplyMap.put("child_account_name", account_name);
			transAccountBillApplyMap.put("currency", mainAccount.getString( "currency"));
		}
		ParaMap out=new ParaMap();
		DataSetDao dao = new DataSetDao();
		out = dao.updateData("trans_account_bill_apply", "id", transAccountBillApplyMap, null);
		return out;
	}
	
	/**
	 * 更新虚拟子账号使用次数
	 * @param child_account_id
	 * @throws Exception
	 */
    public void updateSubAccountUseNum(String child_account_id)throws Exception{
		if(StrUtils.isNotNull(child_account_id) ){
			ParaMap countUseMap=new ParaMap();
			countUseMap.put("moduleNo", "trans_web_main_license");
			countUseMap.put("dataSetNo", "query_max_trans_child_account");
			countUseMap.put("id", child_account_id);
			DataSetDao dao = new DataSetDao();
			countUseMap=dao.queryData(countUseMap);
			if("1".equals(countUseMap.getString("state"))){
				int usecount= (Integer) countUseMap.getRecordValue(0, "usecount") ;
				ParaMap updateCountUseMap=new ParaMap();
				updateCountUseMap.put("id", child_account_id);
				updateCountUseMap.put("use_count", usecount+1);
				dao.updateData("trans_child_account", "id", updateCountUseMap, null);
			}
		}
	}
    
    /**
     * 竞买申请结束发送短信
     * @param targetId
     * @param userId
     * @param bidderId
     * @param biddersMap
     * @param licenseId
     * @throws Exception
     */
    public void saveLicenseSendMessage(String targetId , String userId ,String bidderId  , List bidderList , String licenseId)throws Exception{
    	DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("id", targetId);
		ParaMap targetMap= dao.querySimpleData("trans_target", sqlParam);
		String targetNo = "";
		if(targetMap.getSize()>0){
			targetNo=(String) targetMap.getRecordValue(0,"no");
		}
		sqlParam.clear();
    	sqlParam.put("id", bidderId);
		ParaMap bidderMap= dao.querySimpleData("trans_bidder", sqlParam);
		String bidderMoblie = "";
		if(targetMap.getSize()>0){
			bidderMoblie=(String) bidderMap.getRecordValue(0,"mobile");
		}
		
		//先给主竞买人发
		if(StrUtils.isNotNull(bidderMoblie)){
			this.sendMessage(bidderMoblie, "尊敬的客户，您已成功申请了'"+targetNo+"'标的物。", licenseId,"TransLicense");
		}
		//再给联合竞买人发送
		if(bidderList!=null && bidderList.size()>0){
			sqlParam.clear();
	    	sqlParam.put("id", userId);
			ParaMap userMap= dao.querySimpleData("sys_user", sqlParam);
			String userName = "";
			if(targetMap.getSize()>0){
				bidderMoblie=(String) bidderMap.getRecordValue(0,"user_name");
			}
			for(int i = 0 ; i<bidderList.size(); i++){
				ParaMap tempMap = (ParaMap)bidderList.get(i);
				ParaMap bidder = (ParaMap)tempMap.get("bidder");
				String unionBidderMobile=(String) bidder.getRecordValue(0,"mobile");
				if(StrUtils.isNotNull(unionBidderMobile)){
					this.sendMessage(unionBidderMobile, "尊敬的客户，"+userName+"联合您申请'"+targetNo+"'标的物，请登录交易系统确认！", licenseId,"TransLicense1");
				}
			}
		}
    	
    }
    
    
    public List getTransAccountBillList(String licenseId)throws Exception{
    	DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_license_account_bill_list");
		sqlParams.put("licenseId", licenseId);
		return dataSetDao.queryData(sqlParams).getListObj();
    }
    
    /**
     * 删除竞买申请
     * @param moduleId
     * @param dataSetNo
     * @param id
     * @return
     * @throws Exception
     */
    public ParaMap deleteApply(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		String bidderId = null;
		String targetId = null;
		ParaMap data = new ParaMap();
		data.put("id", id);
		data = dataSetDao.querySimpleData("trans_license", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			if (data.getRecordInteger(0, "status") != 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "删除竞买申请失败！指定竞买申请处于已经通过或者其它不允许删除的状态。");
				return result;
			}
			if (data.getRecordInteger(0, "earnest_money_pay") != 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "删除竞买申请失败！指定竞买申请已交保证金款。");
				return result;
			}
			if (data.getRecordInteger(0, "confirmed") ==1) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "删除竞买申请失败！指定竞买申请已审核通过。");
				return result;
			}
			bidderId = data.getRecordString(0, "bidder_id");
			targetId = data.getRecordString(0, "target_id");
			data.clear();
			data.put("id", targetId);
			data = dataSetDao.querySimpleData("trans_target", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				if (data.getRecordInteger(0, "status") >= 4) {
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "删除竞买申请失败！指定竞买申请标的已经开始交易或者交易结束，不能删除。");
					return result;
				}
			}
		} else {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "删除竞买申请失败！未查询到竞买申请。");
			return result;
		}
		data.clear();
		data.put("apply_type", 0);
		data.put("ref_id", id);
		data = dataSetDao.querySimpleData("trans_account_bill_apply", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			String applyId = data.getRecordString(0, "id");
			String childAccountNo = data.getRecordString(0, "id");
			List statusList = new ArrayList();
			statusList.add(1);
			statusList.add(2);
			data.clear();
			data.put("apply_id", applyId);
			data.put("status", statusList);
			data = dataSetDao.querySimpleRowCount("trans_account_bill", data);
			if (data.getInt("state") == 1 && data.getInt("rowCount") > 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "删除竞买申请失败！已经有保证金或者其它款项转入，不允许删除竞买申请。");
				return result;
			}
		}
		//删除竞买申请
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_license_list");
		sqlParams.put("dataSetNo", "deleteLicenseData");
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.executeSQL(sqlParams);
		return result;
	}
	
    
    
    
    
    
    public ParaMap sendMessage(String phoneNo,String content,String licenseId,String category)throws Exception{
		SmsDao smsDao=new SmsDao();
		ParaMap returnMap=new ParaMap();
		ParaMap messageData=new ParaMap();
		messageData.put("category", category);
		messageData.put("phoneNo", phoneNo);
		messageData.put("content", content);
		messageData.put("sourceTableName", "trans_license");//短信来源表
		messageData.put("sourceKey", licenseId);//短信来源表键值
		messageData.put("sendTime", "");//发送时间，缺省为立刻
		messageData.put("overdueTime", "");//过期时间，缺省为sendTime的半小时后
		messageData.put("sendCount", "");//重复发送次数，缺省1次
		messageData.put("sendInterval", "");//发送时间间隔，单位分钟，缺省30分钟，仅当sendCount>1时有效
		messageData.put("multiSendMode", "");//多次发送时的模式，仅当sendCount>1有效。0发送sendCount次，1对方有收到即停止发送，2对方有回复停止发送
		messageData.put("replyNo", "");//短信回复标识符
		messageData.put("sendPhoneNo", "");//发送短信号码
		messageData.put("forceSend", "");//true强制发送，缺省值为false。当系统设置category为可强制发送时设置此值有效
		try {
			returnMap = smsDao.sendMessage(messageData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
    
    
    public ParaMap getTargetInfo(String targetId) throws Exception{
    	DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_apply_target_info");
		sqlParams.put("targetId", targetId);
		ParaMap map =  dataSetDao.queryData(sqlParams);
		List list = map.getListObj();
		if(list!=null && list.size()>0){
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
    }
    
    public ParaMap getLicenseInfo(String licenseId) throws Exception{
    	DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_apply_license_info");
		sqlParams.put("licenseId", licenseId);
		ParaMap map =  dataSetDao.queryData(sqlParams);
		List list = map.getListObj();
		if(list!=null && list.size()>0){
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
    }
    
    
    
    /**
	 * 根据id得到标的信息
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargetById(String targetId) throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		keyData.put("is_valid", 1);
		ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
		List list = targetMap.getListObj();
		if(list!=null && list.size()>0){
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * 根据id得到竞买申请的信息
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getLicenseById(String licenseId) throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", licenseId);
		keyData.put("is_valid", 1);
		ParaMap targetMap = dataSetDao.querySimpleData("trans_license", keyData);
		List list = targetMap.getListObj();
		if(list!=null && list.size()>0){
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	
	public ParaMap getBidderLicenseList(ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleNo", "trans_bidder_license");
		sqlParams.put("dataSetNo", "get_bidder_license_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	
}

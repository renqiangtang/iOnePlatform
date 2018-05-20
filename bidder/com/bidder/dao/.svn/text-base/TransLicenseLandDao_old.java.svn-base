package com.bidder.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bank.dao.BankBaseDao;
import com.bank.util.BankUtil;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.DateUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sms.dao.SmsDao;
import com.sysman.dao.ConfigDao;
import com.tradeland.engine.EngineDao;
import com.trademan.dao.LicenseManageDao;

public class TransLicenseLandDao_old extends BaseDao {
	
	/**这个类只作备份，其它地方没有调用
	 * 交易单位列表
	 * @param bidderId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransOrganList(String bidderId)throws Exception{
		String bidderOrganFilter = AppConfig.getPro("bidderOrganFilter");
		bidderOrganFilter = StrUtils.isNull(bidderOrganFilter)?"0":bidderOrganFilter;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license_land");
		sqlParams.put("dataSetNo", "get_trans_organ_list");
		String sql = "";
		if("0".equals(bidderOrganFilter)){
			sql = " ";
		}else if("1".equals(bidderOrganFilter)){
			sql = " and exists ( select * from trans_bidder_organ_rel tbor " +
					"where tbor.organ_id = so.id and tbor.is_valid = 1 " +
					"and tbor.bidder_id = :bidderId  and tbor.ref_type = :refType  ) ";
			sqlParams.put("bidderId", bidderId);
			sqlParams.put("refType", 1);
		}else if("2".equals(bidderOrganFilter)){
			sql = " and not exists ( select * from trans_bidder_organ_rel tbor " +
					"where tbor.organ_id = so.id and tbor.is_valid = 1 " +
					"and tbor.bidder_id = :bidderId  and tbor.ref_type = :refType  ) ";
			sqlParams.put("bidderId", bidderId);
			sqlParams.put("refType", 11);
		}else{
			sql = " and 1 = 2 ";
		}
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 得到单位id
	 * @param organMap
	 * @return
	 * @throws Exception
	 */
	public String getOrganIdsByMap(ParaMap organMap)throws Exception{
		String organIds = null;
		if(organMap.getSize()>0){
			List filds = organMap.getFields();
			for(int i = 0 ; i < organMap.getSize() ; i ++){
				ParaMap out = new ParaMap();
				List record = (List)organMap.getRecords().get(i);
				String id = "";
				if(filds != null && filds.size()>0 ){
					for(int j = 0 ; j < filds.size() ; j ++ ){
						out.put((String)filds.get(j), record.get(j));
						if("id".equals((String)filds.get(j))){
							id = (String)record.get(j);
							break;
						}
					}
				}
				organIds = StrUtils.isNull(organIds)?id:organIds+","+id;
			}
		}
		return organIds;
	}
	
	
	/**
	 * 交易区域列表
	 * @param organIds
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransCantonList(String organIds)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license_land");
		sqlParams.put("dataSetNo", "get_trans_canton_list");
		String sql = "" ; 
		if(StrUtils.isNotNull(organIds)){
			organIds= "'"+organIds.replaceAll(",", "','")+"'";
			sql = " and tt.trans_organ_id in ("+organIds+")";
		}else{
			sql = " and 1 = 2 ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 交易类别列表
	 * @param organIds
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransBusinessTypeList(String organIds)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license_land");
		sqlParams.put("dataSetNo", "get_trans_business_type_list");
		String sql = "" ; 
		if(StrUtils.isNotNull(organIds)){
			organIds= "'"+organIds.replaceAll(",", "','")+"'";
			sql = " and tt.trans_organ_id in ("+organIds+")";
		}else{
			sql = " and 1 = 2 ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 交易方式列表
	 * @param organIds
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTransactionTypeList(String organIds)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_license_land");
		sqlParams.put("dataSetNo", "get_trans_transaction_type_list");
		String sql = "" ; 
		if(StrUtils.isNotNull(organIds)){
			organIds= "'"+organIds.replaceAll(",", "','")+"'";
			sql = " and tt.trans_organ_id in ("+organIds+")";
		}else{
			sql = " and 1 = 2 ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 能够交易的商品列表
	 * @param sqlParams
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public ParaMap transTargetlist(ParaMap sqlParams , String sql) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleNo", "trans_bidder_license_land");
		sqlParams.put("dataSetNo", "get_trans_target_list");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 商品保证金列表
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public List getTargetEarnestMoneyList(String targetId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap earnesMap=dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
		List resultList = new ArrayList();
		if(earnesMap.getSize()>0){
			List filds = earnesMap.getFields();
			for(int i = 0 ; i < earnesMap.getSize() ; i ++){
				ParaMap out = new ParaMap();
				List record = (List)earnesMap.getRecords().get(i);
				if(filds != null && filds.size()>0 ){
					for(int j = 0 ; j < filds.size() ; j ++ ){
						String key = (String)filds.get(j);
						if("id".equals(key) || "currency".equals(key) || "amount".equals(key) ){
							out.put(key, record.get(j));
						}
					}
				}
				resultList.add(out);
			}
		}
		return resultList;
	}
	
	/**
	 * 购物车中标的列表
	 * @param bidderId
	 * @param type
	 * @param page
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public ParaMap getCartTargetList(String bidderId , String type , int page , int pagesize)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_main");
		sqlParams.put("dataSetNo", "get_cart_target_list");
		sqlParams.put("bidderId", bidderId);
		sqlParams.put("cartType", 0);
		sqlParams.put("type", type);
		if(page>=0 && pagesize>0){
			sqlParams.put(DataSetDao.SQL_PAGE_INDEX, page);
			sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, pagesize);
		}
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 保存购物车
	 * @param bidderId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String saveCart(String bidderId , String userId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", null);
		keyData.put("bidder_id", bidderId);
		keyData.put("create_user_id", userId);
		keyData.put("create_date", DateUtils.getStr(DateUtils.now()));
		keyData.put("status", 0);
		ParaMap formatMap = new ParaMap();
		formatMap.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap out = dataSetDao.updateData("trans_cart", "id", keyData,formatMap);
		return out.getString("id");
	}
	
	/**
	 * 商品加入购物车
	 * @param cartId
	 * @param targetId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String saveCartDtl(String cartId , String targetId , String userId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", null);
		keyData.put("cart_id", cartId);
		keyData.put("target_id", targetId);
		keyData.put("create_user_id", userId);
		keyData.put("create_date", DateUtils.getStr(DateUtils.now()));
		keyData.put("status", 1);
		ParaMap formatMap = new ParaMap();
		formatMap.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap out = dataSetDao.updateData("trans_cart_dtl", "id", keyData,formatMap);
		return out.getString("id");
	}
	
	
	/**
	 * 移除购物车中的商品
	 * @param dtlId
	 * @return
	 * @throws Exception
	 */
	public ParaMap removeCartDtl(String dtlId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", dtlId);
		keyData.put("status", 0);
		ParaMap out = dataSetDao.updateData("trans_cart_dtl", "id", keyData);
		return out;
	}
	
	
	/**
	 * 删除购物车中的上皮
	 * @param dtlId
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteCarTarget(String dtlId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", dtlId);
		keyData.put("status", 0);
		ParaMap out = dataSetDao.deleteSimpleData("trans_cart_dtl", keyData);
		return out;
	}
	
	
	/**
	 * 判断是否能够购买该标的
	 * @param bidderId
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap isCanBuy(String bidderId , String targetId )throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("bidder_id", bidderId);
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap bidderLicenseMap = dataSetDao.querySimpleData("trans_license", keyData);
		if(bidderLicenseMap.getSize()>0){
			out.put("state", 0);
			out.put("message", "您已申请购买了该标的。");
			return out;
		}
		keyData.clear();
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap licenseMap = dataSetDao.querySimpleData("trans_license", keyData);
		if(licenseMap.getSize()>0){
			keyData.clear();
			keyData.put("bidderId", bidderId);
			keyData.put("targetId", targetId);
			keyData.put("moduleNo", "trans_bidder_license01");
			keyData.put("dataSetNo", "getNoUnion");
			ParaMap pm=dataSetDao.queryData(keyData);
			if(pm.getSize()>0){
				int sfunion=pm.getRecordInt(0, "isunion");
				if(sfunion>0){
					out.put("state", 0);
					out.put("message", "您已被联合申请购买了该标的。");
					return out;
				}
			}else{
				out.put("state", 0);
				out.put("message", "查询联合信息出错。");
				return out;
			}
		}
		keyData.clear();
		keyData.put("id", targetId);
		keyData.put("is_valid", 1);
		ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
		if(targetMap.getSize()>0){
			int status = Integer.parseInt(String.valueOf(targetMap.getRecordValue(0, "status")));
			int is_stop = Integer.parseInt(String.valueOf(targetMap.getRecordValue(0, "is_stop")));
			int is_suspend = Integer.parseInt(String.valueOf(targetMap.getRecordValue(0, "is_suspend")));
			Date beginApplyTime = DateUtils.getDate(String.valueOf(targetMap.getRecordValue(0, "begin_apply_time")));
			Date endApplyTime = DateUtils.getDate(String.valueOf(targetMap.getRecordValue(0, "end_apply_time")));
			if(status!=3 && status!=4){
				out.put("state", 0);
				out.put("message", "该标的状态错误。");
				return out;
			}
			if(is_stop==1){
				out.put("state", 0);
				out.put("message", "该标的已终止。");
				return out;
			}
			if(is_suspend==1){
				out.put("state", 0);
				out.put("message", "该标的已中止。");
				return out;
			}
			if((DateUtils.now()).getTime() < beginApplyTime.getTime()){
				out.put("state", 0);
				out.put("message", "该标的尚未到申请时间。");
				return out;
			}
			if((DateUtils.now()).getTime() > endApplyTime.getTime()){
				out.put("state", 0);
				out.put("message", "该标的已经过了申请时间。");
				return out;
			}
			out.putAll(targetMap);//把target信息传出去，不用再次查询了
		}else{
			out.put("state", 0);
			out.put("message", "该标的状态错误。");
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
				sqlParams.put("moduleNo", "trans_web_main_license_land");
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
		if(allPercent.compareTo(new BigDecimal("100"))>=0 || allPercent.compareTo(new BigDecimal("0"))<0){
		    out.put("state", 0);
		    out.put("message", "联合竞买人比例错误。");
	    	return out;
		}
	    out.put("state", 1);
		out.put("bidderList", bidderList);	
		return out;
	}
	
	/**
	 * 检测并得到股东
	 * @param bidderNames
	 * @return
	 * @throws Exception
	 */
	public ParaMap getCorp(List unitProportionsList)throws Exception{
		ParaMap out = new ParaMap();
		if(unitProportionsList==null || unitProportionsList.size()<1){
			out.put("state", 0);
		    out.put("message", "无有效的股东。");
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
			String pValue = "";
			String nValue = "";
			Iterator iter = bidderMap.entrySet().iterator(); 
			while (iter.hasNext()) { 
				 Map.Entry entry = (Map.Entry) iter.next(); 
				 name = String.valueOf(entry.getKey()); 
				 value = String.valueOf(entry.getValue()); 
				 if(name.equals("percent")){
					 pValue = value;
				 }
				 if(name.equals("name")){
					 nValue = value;
				 }
			}
			if((","+bidderNames+",").indexOf(","+nValue+",")>=0){
				repeatName = StrUtils.isNull(repeatName) ? nValue : repeatName+","+nValue;
			}else{
				sqlParams.clear();
				conditionMap.clear();
				conditionMap.put("CUSTOM_CONDITION", "and name = '"+nValue+"'");
				sqlParams.put(dao.SQL_CUSTOM_CONDITIONS, conditionMap);
				sqlParams.put("moduleNo", "trans_web_main_license_land");
				sqlParams.put("dataSetNo", "query_union_bidder");
				ParaMap result = dao.queryData(sqlParams);
				if(result.getSize()>0){
					bidderMap.put("bidder", result);
					bidderNames = StrUtils.isNull(bidderNames) ? nValue : bidderNames+","+nValue;
				}else{
					notGetName = StrUtils.isNull(notGetName) ? nValue : notGetName+","+nValue;
				}
			}
			BigDecimal percent = new BigDecimal("0");
		    try{
		    	percent = new BigDecimal(String.valueOf(pValue)); 
		    	if(percent.compareTo(new BigDecimal("0"))<=0){
		    		percentErrorName = StrUtils.isNull(percentErrorName) ? nValue : percentErrorName+","+nValue;
		    	}else{
		    		bidderMap.put("percent", percent);
		    	}
		    }catch(Exception e){
		    	percentErrorName = StrUtils.isNull(percentErrorName) ? nValue : percentErrorName+","+nValue;
			}
			allPercent = allPercent.add(percent);
			bidderList.add(bidderMap);
		}
		if(StrUtils.isNotNull(notGetName)){
		    out.put("state", 0);
		    out.put("message", "股东:"+notGetName+"不是有效的竞买用户。");
	    	return out;
		}	
		if(StrUtils.isNotNull(repeatName)){
		    out.put("state", 0);
		    out.put("message", "股东："+repeatName+"重复。");
	    	return out;
		}
		if(StrUtils.isNotNull(percentErrorName)){
			out.put("state", 0);
		    out.put("message", "股东："+repeatName+"比例错误。");
	    	return out;
		}
		if(StrUtils.isNull(bidderNames)){
			out.put("state", 0);
		    out.put("message", "无有效的股东。");
	    	return out;
		}
		if(allPercent.compareTo(new BigDecimal("100"))>0 || allPercent.compareTo(new BigDecimal("0"))<0){
		    out.put("state", 0);
		    out.put("message", "股东投资比例错误。");
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
			returnMap.put("mainAccount", transAccountMap);//把交易中心账号返回
			int inAccountMode= (Integer) transAccountMap.getRecordValue(0, "in_account_mode") ;//进款账号模式。
			String no=(String) transAccountMap.getRecordValue(0, "no");//供调用银行接口是传递参数
			String name=(String) transAccountMap.getRecordValue(0, "name");//供调用银行接口是传递参数
			String bankNo=(String) transAccountMap.getRecordValue(0, "bank_no");//供调用银行接口是传递参数
			String bankName=(String) transAccountMap.getRecordValue(0, "bank_name");//供调用银行接口是传递参数
			String bankId = (String) transAccountMap.getRecordValue(0, "bank_id");
			String currency = (String) transAccountMap.getRecordValue(0, "currency");
			returnMap.put("inAccountMode", inAccountMode);//把进款账号模式返回
			if(inAccountMode==0||inAccountMode==1){//0基本账户手工模式；1基本账号直连直通模式,返回竞买流水号
				returnMap.put("state", 1);
			}else if(inAccountMode==2){//虚拟子账号模式,返回子账号表中的子账号
				sqlParams.clear();
				sqlParams.put("moduleNo", "trans_web_main_license_land");
				sqlParams.put("dataSetNo", "query_trans_child_account");
				sqlParams.put("mainAccountId", accountId);
				sqlParams.put("mainAccountBankId", bankId);
				ParaMap transChildAccountMap = new ParaMap();
				transChildAccountMap=dao.queryData(sqlParams);
				if(transChildAccountMap.getSize()>0){
					returnMap.put("state", 1);
					returnMap.put("childAccountId", transChildAccountMap.getRecordValue(0, "id"));
					returnMap.put("subAccount", transChildAccountMap);//把得到的子账号返回
				}else{
					returnMap.put("state", 0);
					returnMap.put("message", "没有空闲的子账号,请录入。");
				}

			}else if(inAccountMode==3){//直连直通子账号模式,返回调用银行开户接口返回的子账号
				//此处调用银行接口
				BankBaseDao baseDao = (BankBaseDao)BankUtil.createInstance(bankNo);
				String childNo="";
				try {
					//childNo = baseDao.createChildAccount(name, bankNo, no , currency);
					childNo = String.valueOf(DateUtils.nowTime());
				} catch (Exception e) {
					returnMap.put("state", 0);
					returnMap.put("message", "获取子账号出错。");
					return returnMap;
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
						returnMap.put("subAccount", transChildAccountMap);//把得到的子账号返回
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
	
//	/**
//	 * 竞买申请号
//	 * @return
//	 * @throws Exception
//	 */
//	public String getNewLicenseNo() throws Exception {
//		String result = null;
//		ParaMap sqlParams = new ParaMap();
//		sqlParams.put("moduleNo", "trans_license_list");
//		sqlParams.put("dataSetNo", "queryNewLicenseNo");
//		DataSetDao dataSetDao = new DataSetDao();
//		ParaMap resultMap = dataSetDao.queryData(sqlParams);
//		if ( resultMap.getSize()>0) {
//			result = String.valueOf(resultMap.getRecordValue(0, 0));
//		} 
//		return result;
//	}
//	
//	/**
//	 * 竞买申请入账单流水号
//	 * @return
//	 * @throws Exception
//	 */
//	public String getNewApplyNo() throws Exception {
//		String result = null;
//		ParaMap sqlParams = new ParaMap();
//		sqlParams.put("moduleNo", "trans_license_list");
//		sqlParams.put("dataSetNo", "queryNewApplyNo");
//		DataSetDao dataSetDao = new DataSetDao();
//		ParaMap resultMap = dataSetDao.queryData(sqlParams);
//		if (resultMap.getSize()>0) {
//			result = String.valueOf(resultMap.getRecordValue(0, 0));
//		} 
//		return result;
//	}
//	
//	/**
//	 * 竞买申请入账单申请编号
//	 * @return
//	 * @throws Exception
//	 */
//	public String getNewOrderNo() throws Exception {
//		String result = null;
//		ParaMap sqlParams = new ParaMap();
//		sqlParams.put("moduleNo", "trans_license_list");
//		sqlParams.put("dataSetNo", "queryNewOrderNo");
//		DataSetDao dataSetDao = new DataSetDao();
//		ParaMap resultMap = dataSetDao.queryData(sqlParams);
//		if (resultMap.getSize()>0) {
//			result = String.valueOf(resultMap.getRecordValue(0, 0));
//		} 
//		return result;
//	}
	/**
	 * 保存竞买申请
	 * @return
	 * @throws Exception
	 */
	public synchronized ParaMap saveLicense(String bidderId , String targetId , String trustType , String trustPrice , String userId ,int is_after_check) throws Exception{
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
//		String filetime=DateUtils.nowStr();
//		ParaMap transLicenseMap=new ParaMap();
//		ParaMap format=new ParaMap();
//		transLicenseMap.put("id", "");//竞买人ID
//		transLicenseMap.put("bidder_id", bidderId);//竞买人ID
//		transLicenseMap.put("target_id", targetId);//标的ID
//		transLicenseMap.put("license_no", licenseNo);//竞买申请号，全局唯一随机编号
//		transLicenseMap.put("apply_date", filetime);//申请日期
//		//transLicenseMap.put("card_no", "");//竞买号牌，网上交易无意义
//		transLicenseMap.put("apply_no", applyNo);//申请编号，此号是打印有竞买申请书上的编号
//		transLicenseMap.put("card_no", cardNo);//牌号
//		if(StrUtils.isNotNull(trustType)){//是否有委托报价
//			transLicenseMap.put("trust_type", trustType);//委托报价方式。0无，1系统自行报委托报价以下的报价，2网络自助报价，3允许电话委托报价
//			if("1".equals(trustType)){//如果委托报价类型为委托系统自动报价，则需要最高委托金额
//				if(StrUtils.isNotNull(trustPrice)){
//					transLicenseMap.put("trust_price", Double.parseDouble(trustPrice));//委托金额
//				}
//			}
//		}
//		transLicenseMap.put("create_user_id", userId);//创建人ID
//		format.put("apply_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
//		DataSetDao dao = new DataSetDao();
//		out = dao.updateData("trans_license", "id", transLicenseMap, format);
		
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "";
		if(is_after_check == 1){
			sql = "insert into trans_license(id,bidder_id,target_id,license_no,apply_date,apply_no,card_no,trust_type,trust_price,create_user_id,confirmed,status) " +
			  "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		}else{
			sql = "insert into trans_license(id,bidder_id,target_id,license_no,apply_date,apply_no,card_no,trust_type,trust_price,create_user_id,status) " +
			  "values(?,?,?,?,?,?,?,?,?,?,?)";
		}
		
		pstm = getCon().prepareStatement(sql);
		String id = IDGenerator.local();
		pstm.setString(1, id);
		pstm.setString(2, bidderId);
		pstm.setString(3, targetId);
		pstm.setString(4, licenseNo);
		pstm.setTimestamp(5, new Timestamp(DateUtils.nowTime()));
		pstm.setString(6, applyNo);
		pstm.setString(7, cardNo);
		pstm.setString(8, trustType);
		trustPrice = StrUtils.isNotNull(trustPrice)?trustPrice:"";
		pstm.setString(9, trustPrice);
		pstm.setString(10, userId);
		if(is_after_check == 1){
			pstm.setInt(11, 1);
			pstm.setInt(12, 1);
		}else{
			pstm.setInt(11, 1);
		}
		pstm.executeUpdate();
		if(pstm != null){
			pstm.close();
		}
		EngineDao engineDao = new EngineDao();
		engineDao.addUserId(targetId, userId);
		out.put("state", 1);
		out.put("id", id);
		return out;
	}
	
	/**
	 * 保存联合竞买信息	
	 * @param licenseId
	 * @param userId
	 * @param bidderNameValues
	 * @param percentValues
	 * @param biddersMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveUnionBidder(String licenseId , String userId ,List bidderList)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dao = new DataSetDao();
		if(bidderList!=null && bidderList.size()>0){
			for(int i=0 ; i<bidderList.size() ; i++){
				ParaMap bidderMap = (ParaMap)bidderList.get(i);
				ParaMap bidder = (ParaMap)bidderMap.get("bidder");
				BigDecimal percent = bidderMap.getBigDecimal("percent");
				String bidder_id=(String) bidder.getRecordValue(0,"id");
				String bidder_name=(String) bidder.getRecordValue(0,"name");
				String bidder_address=(String) bidder.getRecordValue(0,"address");
				String bidder_contact=(String) bidder.getRecordValue(0,"contact");
				String bidder_tel=(String) bidder.getRecordValue(0,"tel");
				String bidder_idno=(String) bidder.getRecordValue(0,"certificate_no");
				ParaMap transLicenseUnionMap=new ParaMap();
				transLicenseUnionMap.put("id", "");
				transLicenseUnionMap.put("license_id", licenseId);
				transLicenseUnionMap.put("bidder_id", bidder_id);
				transLicenseUnionMap.put("bidder_name", bidder_name);
				transLicenseUnionMap.put("bidder_address", bidder_address);
				transLicenseUnionMap.put("bidder_contact", bidder_contact);
				transLicenseUnionMap.put("bidder_tel", bidder_tel);
				transLicenseUnionMap.put("bidder_idno", bidder_idno);
				transLicenseUnionMap.put("create_user_id", userId);
				transLicenseUnionMap.put("percent", percent);
				out = dao.updateData("trans_license_union", "id", transLicenseUnionMap, null);
				if(!"1".equals(out.getString("state"))){
					throw new Exception("创建联合竞买信息出错。");
				}
			}														
		}else{
			out.put("state", 0);
			out.put("message", "联合竞买参数错误。");
			return out;
		}
		out.put("state", 1);
		return out;
	}
	
	
	public ParaMap saveVirtualCompany(String licenseId , ParaMap companyMap , List ownerList)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dao = new DataSetDao();
		String companyId = null;
		if(companyMap!=null && companyMap.get("name")!=null){
			if(ownerList == null || ownerList.size()<=0){
				out.put("state", 0);
				out.put("message", "虚拟公司股东信息不能为空。");
				return out;
			}
			//保存公司信息
			companyMap.remove("proportions");
			companyMap.remove("style");
			companyMap.put("id", null);
			companyMap.put("license_id", licenseId);
			ParaMap result = dao.updateData("trans_project_dealer", "id", companyMap);
			if(!"1".equals(result.getString("state"))){
				throw new Exception("创建虚拟公司信息出错。");
			}
			companyId = result.getString("id");
			//循环保存股东信息
			for(int i = 0 ; i < ownerList.size(); i ++ ){
				ParaMap ownerMap = (ParaMap)ownerList.get(i);
				ownerMap.put("id", null);
				ownerMap.put("pd_id", companyId);
				result = dao.updateData("trans_project_dealer_owner", "id", ownerMap);
				if(!"1".equals(result.getString("state"))){
					throw new Exception("创建虚拟公司股东信息出错。");
				}
			}
		}else{
			out.put("state", 0);
			out.put("message", "虚拟公司名称不能为空值。");
			return out;
		}
		out.put("companyId", companyId);
		out.put("state", 1);
		return out;
	}
	
	/**
	 * 保存入账申请单
	 * @param licenseId
	 * @param accountMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveAccountBillApply(String licenseId ,ParaMap accountMap)throws Exception{
		LicenseManageDao licenseManage = new LicenseManageDao();
		String orderNo= null;
		ParaMap orderNoMap =	licenseManage.getNewApplyNo(null,null,null);//获取竞买申请号
		if("1".equals(orderNoMap.getString("state"))){
			orderNo = orderNoMap.getString("no");
		}else{
			return orderNoMap;
		}
		ParaMap transAccountBillApplyMap=new ParaMap();
		ParaMap mainAccount = (ParaMap)accountMap.get("mainAccount");
		ParaMap subAccount =  (ParaMap)accountMap.get("subAccount");
		String account_bank=(String) mainAccount.getRecordValue(0,"bank_name");//中心账号开户行
		String account_no=(String) mainAccount.getRecordValue(0,"no");//中心账号
		String account_name=(String) mainAccount.getRecordValue(0,"name");//中心账号
		transAccountBillApplyMap.put("id", "");
		transAccountBillApplyMap.put("ref_id", licenseId);
		transAccountBillApplyMap.put("order_no", orderNo);//入账订单流水号，此号在划款时必须写在备注信息中,暂时写为空
		transAccountBillApplyMap.put("account_bank", account_bank);
		transAccountBillApplyMap.put("account_no", account_no);
		transAccountBillApplyMap.put("account_name", account_name);
		if(subAccount.getSize()>0){//子账号信息,用主账号的情况下子账号为空
			String child_account_bank=(String) subAccount.getRecordValue(0,"bank_name");//中心子账户开户行
			String child_account_no=(String) subAccount.getRecordValue(0,"no");//中心子账户账号
			String child_account_name=(String) subAccount.getRecordValue(0,"name");//中心子账户户名
			transAccountBillApplyMap.put("child_account_bank", child_account_bank);
			transAccountBillApplyMap.put("child_account_no", child_account_no);
			transAccountBillApplyMap.put("child_account_name", child_account_name);
			transAccountBillApplyMap.put("currency", subAccount.getRecordValue(0, "currency"));
		}else{
			transAccountBillApplyMap.put("child_account_bank", account_bank);
			transAccountBillApplyMap.put("child_account_no", account_no);
			transAccountBillApplyMap.put("child_account_name", account_name);
			transAccountBillApplyMap.put("currency", mainAccount.getRecordValue(0, "currency"));
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
			countUseMap.put("moduleNo", "trans_web_main_license_land");
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
	 * 修改购物车中的标的状态
	 * @param bidderId
	 * @param targetId
	 * @throws Exception
	 */
    public void updateCartDtl(String bidderId , String targetId , int begin , int end , String userId)throws Exception{
		 ParaMap ketSet=new ParaMap();
		 ketSet.put("bidder_id", bidderId);
		 ketSet.put("status", 0);
		 DataSetDao dao = new DataSetDao();
		 ParaMap result =dao.querySimpleData("trans_cart", ketSet);
		 String cartId = null;
		 if(result.getSize()>0){
			 cartId = (String)result.getRecordValue(0, "id");
		 }else{
			 cartId = this.saveCart(bidderId , userId);
		 }
		 ketSet.clear();
		 ketSet.put("cart_id", cartId);
		 ketSet.put("target_id", targetId);
		 //ketSet.put("status", begin);
		 result =dao.querySimpleData("trans_cart_dtl", ketSet);
		 String dtlId = null;
		 if(result.getSize()>0){
			 dtlId = (String)result.getRecordValue(0, "id");
			 ketSet.clear();
			 ketSet.put("id", dtlId);
			 ketSet.put("status", end);
			 dao.updateData("trans_cart_dtl", "id", ketSet);
		 }else{
			 ketSet.put("id", null);
			 ketSet.put("cart_id", cartId);
			 ketSet.put("target_id", targetId);
			 ketSet.put("create_user_id", userId);
			 ketSet.put("create_date", DateUtils.getStr(DateUtils.now()));
			 ketSet.put("status", end);
			 ParaMap formatMap = new ParaMap();
			 formatMap.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
			 dao.updateData("trans_cart_dtl", "id", ketSet,formatMap);
		 }
	}
    
    
    public ParaMap getTransLicense(String licenseId)throws Exception{
    	DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("id", licenseId);
    	sqlParam.put("is_valid",1);
		ParaMap out= dao.querySimpleData("trans_license", sqlParam);
		return out;
    }
    
    public ParaMap getTransLicense(String targetId , String bidderId)throws Exception{
    	DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("moduleNo", "trans_bidder_license_land");
		sqlParam.put("dataSetNo", "get_bidder_target_license");
    	sqlParam.put("targetId", targetId);
    	sqlParam.put("bidderId", bidderId);
    	ParaMap out= dao.queryData(sqlParam);
		return out;
    }
    
    public ParaMap getMainTransLicense(String targetId , String bidderId)throws Exception{
    	DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("target_id", targetId);
		sqlParam.put("bidder_id", bidderId);
    	sqlParam.put("is_valid", 1);
    	ParaMap out= dao.querySimpleData("trans_license", sqlParam);
		return out;
    }
    
	public ParaMap getTransTarget(String targetId) throws Exception {
		DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("id", targetId);
    	sqlParam.put("is_valid",1);
		ParaMap out= dao.querySimpleData("trans_target", sqlParam);
		return out;
	}
	
	
	  public ParaMap getTransLicenseUnion(String licenseId)throws Exception{
	    	DataSetDao dao = new DataSetDao();
	    	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("license_id", licenseId);
	    	sqlParam.put("is_valid", 1);
			ParaMap out= dao.querySimpleData("trans_license_union", sqlParam);
			return out;
	  }
	  
	  public List getLicenseUnionList(String licenseId)throws Exception{
		  List proportions = new ArrayList();
		  ParaMap licenseMap = this.getTransLicense(licenseId);
		  if(licenseMap.getSize()>0){
			    DataSetDao dao = new DataSetDao();
		    	ParaMap sqlParam = new ParaMap();
		    	sqlParam.put("id", (String)licenseMap.getRecordValue(0, "bidder_id"));
		    	sqlParam.put("is_valid", 1);
				ParaMap bidderMap= dao.querySimpleData("trans_bidder", sqlParam);
				if(bidderMap.getSize()>0){
					ParaMap mainBidder = new ParaMap();
					mainBidder.put("id", String.valueOf(bidderMap.getRecordValue(0, "id")));
					mainBidder.put("name", String.valueOf(bidderMap.getRecordValue(0, "name")));
					mainBidder.put("tel", String.valueOf(bidderMap.getRecordValue(0, "tel")));
					mainBidder.put("percent", 100);
					mainBidder.put("status", -1);
					proportions.add(mainBidder);
					ParaMap unionListMap = this.getTransLicenseUnion(licenseId);
					BigDecimal otherPercent = new BigDecimal("0");
					if(unionListMap.getSize()>0){
						for(int i = 0 ; i < unionListMap.getSize() ; i ++ ){
							String bidder_id = String.valueOf(unionListMap.getRecordValue(i, "bidder_id"));
							String bidder_name = String.valueOf(unionListMap.getRecordValue(i, "bidder_name"));
							String bidder_tel = String.valueOf(unionListMap.getRecordValue(i, "bidder_tel"));
							BigDecimal percent = new BigDecimal(String.valueOf(unionListMap.getRecordValue(i, "percent")));
							String status = String.valueOf(unionListMap.getRecordValue(i, "status"));
							ParaMap up = new ParaMap();
							up.put("id", bidder_id);
							up.put("name", bidder_name);
							up.put("tel", bidder_tel);
							up.put("percent", percent);
							up.put("status", status);
							proportions.add(up);
							otherPercent = otherPercent.add(percent);
						}
					}
					if(otherPercent.compareTo(new BigDecimal("0"))>0){
						mainBidder.put("percent", (new BigDecimal(100)).subtract(otherPercent));
						proportions.set(0, mainBidder);
					}
				}else{
					return null;
				}
			}else{
			  return null;
		  }
		  return proportions;
	  }
	  
	  public ParaMap getVirtualCompany(String licenseId)throws Exception{
		  	DataSetDao dao = new DataSetDao();
		  	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("license_id", licenseId);
	    	ParaMap company = dao.querySimpleData("trans_project_dealer", sqlParam);
	    	if(company!=null && company.getSize()>0){
	    		company = company.getListObj().get(0);
	    		String companyId = company.getString("id");
	    		sqlParam.clear();
	    		sqlParam.put("pd_id", companyId);
	    		ParaMap companyOwner = dao.querySimpleData("trans_project_dealer_owner", sqlParam , " percent desc ");
	    		if(companyOwner!=null && companyOwner.getSize()>0){
	    			company.put("owners", companyOwner.getListObj());
	    		}
	    		return company;
	    	}else{
	    		return null;
	    	}
	  }
	  
	  
	  public ParaMap getAccount(String licenseId , String targetId)throws Exception{
		    ParaMap out = new ParaMap();
	    	DataSetDao dao = new DataSetDao();
	    	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("ref_id", licenseId);
	    	sqlParam.put("apply_type", 0);
	    	sqlParam.put("bill_type", 0);
	    	ParaMap result= dao.querySimpleData("trans_account_bill_apply", sqlParam);
	    	if(result.getSize()>0){
	    		out.put("order_no", result.getRecordValue(0, "order_no"));
	    		out.put("account_bank", result.getRecordValue(0, "account_bank"));
	    		out.put("account_no", result.getRecordValue(0, "account_no"));
	    		out.put("account_name", result.getRecordValue(0, "account_name"));
	    		out.put("child_account_bank", result.getRecordValue(0, "child_account_bank"));
	    		out.put("child_account_no", result.getRecordValue(0, "child_account_no"));
	    		out.put("child_account_name", result.getRecordValue(0, "child_account_name"));
	    		out.put("currency", result.getRecordValue(0, "currency"));
	    	}
	    	sqlParam.clear();
	    	sqlParam.put("id", targetId);
	    	sqlParam.put("is_valid", 1);
	    	result= dao.querySimpleData("trans_target", sqlParam);
	    	if(result.getSize()>0){
	    		out.put("unit", result.getRecordValue(0, "unit"));
	    	}
	    	sqlParam.clear();
	    	sqlParam.put("target_id", targetId);
	    	sqlParam.put("is_valid", 1);
	    	result= dao.querySimpleData("trans_target_earnest_money", sqlParam);
	    	if(result.getSize()>0){
	    		for(int i = 0 ; i < result.getSize() ; i++){
	    			String currency = String.valueOf(result.getRecordValue(i, "currency"));
	    			if(currency !=null && out.get("currency")!=null && currency.equals(out.getString("currency"))){
	    				out.put("amount", result.getRecordValue(i, "amount"));
	    				break;
	    			}
	    		}
	    	}
			return out;
	  }
	  
	  public ParaMap getAccountBillList(String licenseId)throws Exception{
		    DataSetDao dao = new DataSetDao();
	    	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("moduleNo", "trans_bidder_license_land");
			sqlParam.put("dataSetNo", "get_license_account_bill_list");
	    	sqlParam.put("licenseId", licenseId);
	    	ParaMap out= dao.queryData(sqlParam);
			return out;
	  }
	  
	  public List getTargetLicenseSetps(String targetId , String businessType ,String transTypeId ,String allow_union,String allow_trust)throws Exception{
		   //得到交易方式，交易类型id
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("business_type_rel_id", businessType);
			keyData.put("trans_type_id", transTypeId);
			keyData.put("is_valid",1);
			ParaMap transTypeRelMap = dataSetDao.querySimpleData("trans_transaction_type_rel", keyData);
			String transactionRelId = null;
			if(transTypeRelMap.getSize()>0){
				transactionRelId = String.valueOf(transTypeRelMap.getRecordValue(0, "id"));
			}else{
				return null;
			}
			//得到业务类别范围
			ConfigDao configDao = new ConfigDao();
			String configStr = configDao.getBusinessType(6);
			ParaMap configObj = ParaMap.fromString(configStr);
			List list = (List)configObj.getDataByPath(businessType+"."+transactionRelId+".steps");
			
			//以下循环步骤，排除标的不需要的步骤，并且设置module_url
			List stepsList = new ArrayList();
			if(list!=null && list.size()>0){
				for(int i = 0 ; i < list.size(); i++){
					ParaMap stepMap = (ParaMap)list.get(i);
					String no = stepMap.getString("no");
					String moduleId = stepMap.getString("module_id");
					String moduleParam = stepMap.getString("module_params");
					String moduleUrl = stepMap.getString("module_url");
					String condition = stepMap.getString("condition");
					boolean isMust = true;
					if(StrUtils.isNotNull(condition)){
						keyData.clear();
						keyData.put("sql", condition);
						keyData.put("target_id",targetId);
						ParaMap conditionResult = dataSetDao.queryData(keyData);
						if(conditionResult.getSize()>0){
							String is_must = String.valueOf(conditionResult.getRecordValue(0, 0));
							isMust = "1".equals(is_must)?true:false;
						}
					}
					if(isMust){
						if(StrUtils.isNull(moduleUrl)){
							String url = "";
							if(StrUtils.isNotNull(moduleId)){
								keyData.clear();
								keyData.put("id", moduleId);
								keyData.put("is_valid",1);
								ParaMap moduleMap = dataSetDao.querySimpleData("sys_module", keyData);
								if(moduleMap.getSize()>0){
									url =  String.valueOf(moduleMap.getRecordValue(0, "class_name"));
								}
							}
							if(StrUtils.isNotNull(url)){
								if(StrUtils.isNotNull(moduleParam)){
									url = url.indexOf("?")>=0? url+"&"+moduleParam : url+"?"+moduleParam;
								}
							}
							stepMap.put("module_url", url);
						}
						stepsList.add(stepMap);
					}
				}
			}
			return stepsList;
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
    
    public String getTargetAttachFilePath(String targetId)throws Exception{
    	ParaMap target = this.getTransTarget(targetId);
    	String business_type = target.getRecordString(0, "business_type");
    	String templateNo = "businessType"+business_type+"TargetAttach";
    	String attachNo = "jyxz";
    	String tableName = "trans_target";
    	String tableId = targetId;
    	ParaMap out = this.getAttachPath(templateNo, attachNo, tableName, tableId);
    	if(out!=null && out.getSize()>0){
    		return out.getRecordString(0, "file_path");
    	}else{
    		return null;
    	}
    }
    
    public ParaMap getAttachPath(String templateNo , String attachNo , String tableName , String tableId) throws Exception{
    	DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("moduleNo", "trans_bidder_license_land");
		sqlParam.put("dataSetNo", "get_attach_file_info");
    	sqlParam.put("templateNo", templateNo);
    	sqlParam.put("attachNo", attachNo);
    	sqlParam.put("tableName", tableName);
    	sqlParam.put("tableId", tableId);
    	ParaMap out= dao.queryData(sqlParam);
		return out;
    }
    
    public ParaMap getSysUser(String userId)throws Exception{
    	 ParaMap out = new ParaMap();
	    	DataSetDao dao = new DataSetDao();
	    	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("id", userId);
	    	sqlParam.put("is_valid", 1);
	    	ParaMap result= dao.querySimpleData("sys_user", sqlParam);
	    	return result;
    }
}

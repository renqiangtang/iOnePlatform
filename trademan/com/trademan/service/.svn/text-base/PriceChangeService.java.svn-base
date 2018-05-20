package com.trademan.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trademan.dao.PriceChangeDao;



public class PriceChangeService extends BaseService {

	Logger log = Logger.getLogger(this.getClass());
	
	public ParaMap lowestPriceTargetList(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		String businessType = inMap.getString("businessType");
		String targetNo =  inMap.getString("targetNo");
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.equals("301") || goodsType.equals("401") ){
				goodsType = "301,401";
			}
			goodsType = goodsType.replace(",", "','");
			customCondition.append(" and substr(tt.business_type,0,3) in ('"+goodsType+"') ");
		}
		if(StrUtils.isNotNull(businessType)){
			customCondition.append(" and tt.business_type=:businessType ");
		}
		if(StrUtils.isNotNull(targetNo)){
			customCondition.append(" and tt.no=:targetNo ");
		}
		String organId = getOrganId(inMap.getString("u"));
		customCondition.append(" and ( tt.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");
		inMap.put("organId", organId);
		
		ParaMap sqlMap = new ParaMap();
		sqlMap.putAll(inMap);
		sqlMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.getTargetList(sqlMap);
		int totalRowCount = result.getInt("totalRowCount");
		List list  = result.getListObj();
//		List resultList = new ArrayList();
//		if(totalRowCount>0){
//			for(int i = 0 ; i < totalRowCount ; i ++ ){
//				ParaMap temp = (ParaMap)list.get(i);
//				String targetId = result.getRecordString(i, "id");
//				List logList = priceChangeDao.getTargetLowestChangeList(targetId);
//				temp.put("change_log", logList);
//				resultList.add(temp);
//			}
//		}
		ParaMap out = new ParaMap();
		out.put("Rows", list);
		out.put("Total", totalRowCount);
		return out;
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
	
	public ParaMap lowestChangeLogList(ParaMap inMap)throws Exception{
		String targetId = inMap.getString("targetId");
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.lowestChangeLogList(targetId);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap saveLowestPrice(ParaMap inMap)throws Exception{
		String userId = inMap.getString("u");
		String lowestPrice = inMap.getString("lowestPrice");
		String targetId = inMap.getString("targetId");
		String cause = inMap.getString("cause");
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("target_id", targetId);
		sqlParams.put("price_type", 0);
		sqlParams.put("is_valid", 1);
		List list = new ArrayList();
		list.add("0");
		list.add("1");
		sqlParams.put("status", list);
		ParaMap log = dao.querySimpleData("trans_price_change_log", sqlParams);
		ParaMap out = new ParaMap();
		if(log!=null && log.getSize()>0){
			out.put("state", 0);
			out.put("message", "操作失败！您已经有未审核的修改记录，不能添加新的修改记录。");
			return out;
		}
		
		sqlParams.clear();
		sqlParams.put("id", targetId);
		sqlParams.put("is_valid", 1);
		ParaMap target = dao.querySimpleData("trans_target", sqlParams);
		String reservePrice = "";
		if(target.getSize()>0){
			reservePrice = target.getRecordString(0, "reserve_price");
		}
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.savePriceChange(targetId, "0", "", reservePrice, lowestPrice, cause, userId);
		
		if("1".equals(result.getString("state"))){
			out.put("state", 1);
			out.put("message", "操作成功！");
		}else{
			out.put("state", 0);
			out.put("message", "操作失败！");
		}
		return out;
	}
	
	public ParaMap checkLowestPrice(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String status = inMap.getString("status");
		String logId = inMap.getString("logId");
		String checkMessage = inMap.getString("checkMessage");
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap changeLog = priceChangeDao.getChangeLog(logId);
		if(changeLog == null){
			out.put("state", 0);
			out.put("message", "审核失败！审核参数错误。");
			return out;
		}
		String targetId = changeLog.getString("target_id");
		String newValue = changeLog.getString("new_value");
		int priceType = changeLog.getInt("price_type");
		if(priceType!=0){
			out.put("state", 0);
			out.put("message", "审核失败！类型参数错误。");
			return out;
		}
		ParaMap target = priceChangeDao.getTransTarget(targetId);
		if(target == null){
			out.put("state", 0);
			out.put("message", "审核失败！标的参数错误。");
			return out;
		}
		if(status.equals("2")){//审核成功
			int targetStatus = target.getInt("status");
			if(targetStatus>4){
				out.put("state", 0);
				out.put("message", "审核通过失败！标的已经交易结束，修改底价无效。请选择审核不通过");
				return out;
			}
		}
		ParaMap result = priceChangeDao.checkPriceChange(logId, userId, status, checkMessage);
		if(!"1".equals(result.getString("state"))){
			out.put("state", 0);
			out.put("message", "审核失败！");
			return out;
		}
		if(status.equals("2")){//如果是审核通过需要修改标的表中的底价
			result = priceChangeDao.updateTargetReservePrice(targetId,newValue);
			if(!"1".equals(result.getString("state"))){
				throw new Exception("操作失败！");//需要回滚修改log表的操作
			}
		}
		out.put("state", 1);
		out.put("message", "审核成功！");
		return out;
	}
	
	public ParaMap waitCheckLowestList(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		String businessType = inMap.getString("businessType");
		String targetNo =  inMap.getString("targetNo");
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.equals("301") || goodsType.equals("401") ){
				goodsType = "301,401";
			}
			goodsType = goodsType.replace(",", "','");
			customCondition.append(" and substr(tt.business_type,0,3) in ('"+goodsType+"') ");
		}
		if(StrUtils.isNotNull(businessType)){
			customCondition.append(" and tt.business_type=:businessType ");
		}
		if(StrUtils.isNotNull(targetNo)){
			customCondition.append(" and tt.no=:targetNo ");
		}
		String organId = getOrganId(inMap.getString("u"));
		customCondition.append(" and ( tt.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");
		inMap.put("organId", organId);
		ParaMap sqlMap = new ParaMap();
		sqlMap.putAll(inMap);
		sqlMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.waitCheckLowestTargetList(sqlMap);
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", result.getInt("totalRowCount"));
		return out;
	}
	
	
	public ParaMap finalPriceTargetList(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		String businessType = inMap.getString("businessType");
		String targetNo =  inMap.getString("targetNo");
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.equals("301") || goodsType.equals("401") ){
				goodsType = "301,401";
			}
			goodsType = goodsType.replace(",", "','");
			customCondition.append(" and substr(tt.business_type,0,3) in ('"+goodsType+"') ");
		}
		if(StrUtils.isNotNull(businessType)){
			customCondition.append(" and tt.business_type=:businessType ");
		}
		if(StrUtils.isNotNull(targetNo)){
			customCondition.append(" and tt.no=:targetNo ");
		}
		
		String organId = getOrganId(inMap.getString("u"));
		customCondition.append(" and ( tt.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");
		inMap.put("organId", organId);
		
		ParaMap sqlMap = new ParaMap();
		sqlMap.putAll(inMap);
		sqlMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("page"));
		sqlMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pagesize"));
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.getTargetList(sqlMap);
		int totalRowCount = result.getInt("totalRowCount");
		List list  = result.getListObj();
		List resultList = new ArrayList();
		if(totalRowCount>0){
			for(int i = 0 ; i < totalRowCount ; i ++ ){
				ParaMap temp = (ParaMap)list.get(i);
				String targetId = result.getRecordString(i, "id");
				List logList = priceChangeDao.getTargetMultiList(targetId);
				temp.put("multi_list", logList);
				resultList.add(temp);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap targetMultiList(ParaMap inMap)throws Exception{
		String targetId = inMap.getString("targetId");
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		List list = priceChangeDao.getTargetMultiList(targetId);
//		List resultList = new ArrayList();
//		if(list!=null && list.size()>0){
//			for(int i = 0 ; i < list.size();i++){
//				ParaMap temp = (ParaMap)list.get(i);
//				String type = temp.getString("type");
//				String finalValue = temp.getString("final_value");
//				if(type.equals("Q01") && StrUtils.isNotNull(finalValue)){
//					String newValue = toWan(finalValue);
//					temp.put("final_value", newValue);
//				}
//				resultList.add(temp);
//			}
//		}
		ParaMap out = new ParaMap();
		out.put("Rows", list);
		out.put("Total", list!=null && list.size()>0 ? list.size() : 0);
		return out;
	}
	
	public ParaMap finalChangeLogList(ParaMap inMap)throws Exception{
		String multiId = inMap.getString("multiId");
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.finalChangeLogList(multiId);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap saveFinalPrice(ParaMap inMap)throws Exception{
		String userId = inMap.getString("u");
		String newfinalPrice = inMap.getString("finalPrice");
		String targetId = inMap.getString("targetId");
		String multiId = inMap.getString("multiId");
		String cause = inMap.getString("cause");
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("target_id", targetId);
		sqlParams.put("price_type", 1);
		sqlParams.put("multi_id", multiId);
		sqlParams.put("is_valid", 1);
		List list = new ArrayList();
		list.add("0");
		list.add("1");
		sqlParams.put("status", list);
		ParaMap log = dao.querySimpleData("trans_price_change_log", sqlParams);
		ParaMap out = new ParaMap();
		if(log!=null && log.getSize()>0){
			out.put("state", 0);
			out.put("message", "操作失败！您已经有未审核的修改记录，不能添加新的修改记录。");
			return out;
		}
		
		sqlParams.clear();
		sqlParams.put("id", multiId);
		sqlParams.put("is_valid", 1);
		ParaMap multi = dao.querySimpleData("trans_target_multi_trade", sqlParams);
		String finalPrice = "";
		if(multi.getSize()>0){
			finalPrice = multi.getRecordString(0, "final_value");
		}
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.savePriceChange(targetId, "1", multiId, finalPrice, newfinalPrice, cause, userId);
		
		if("1".equals(result.getString("state"))){
			out.put("state", 1);
			out.put("message", "操作成功！");
		}else{
			out.put("state", 0);
			out.put("message", "操作失败！");
		}
		return out;
	}
	
	
	public ParaMap waitCheckFinalList(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		String businessType = inMap.getString("businessType");
		String targetNo =  inMap.getString("targetNo");
		StringBuffer customCondition = new StringBuffer("");
		if(StrUtils.isNotNull(goodsType)){
			if(goodsType.equals("301") || goodsType.equals("401") ){
				goodsType = "301,401";
			}
			goodsType = goodsType.replace(",", "','");
			customCondition.append(" and substr(tt.business_type,0,3) in ('"+goodsType+"') ");
		}
		if(StrUtils.isNotNull(businessType)){
			customCondition.append(" and tt.business_type=:businessType ");
		}
		if(StrUtils.isNotNull(targetNo)){
			customCondition.append(" and tt.no=:targetNo ");
		}
		
		String organId = getOrganId(inMap.getString("u"));
		customCondition.append(" and ( tt.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");
		inMap.put("organId", organId);
		ParaMap sqlMap = new ParaMap();
		sqlMap.putAll(inMap);
		sqlMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap result = priceChangeDao.waitCheckFinalTargetList(sqlMap);
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", result.getInt("totalRowCount"));
		return out;
	}
	
	
	
	public ParaMap checkFinalPrice(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String status = inMap.getString("status");
		String logId = inMap.getString("logId");
		String checkMessage = inMap.getString("checkMessage");
		PriceChangeDao priceChangeDao = new PriceChangeDao();
		ParaMap changeLog = priceChangeDao.getChangeLog(logId);
		if(changeLog == null){
			out.put("state", 0);
			out.put("message", "审核失败！审核参数错误。");
			return out;
		}
		String targetId = changeLog.getString("target_id");
		String multiId = changeLog.getString("multi_id");
		String newValue = changeLog.getString("new_value");
		int priceType = changeLog.getInt("price_type");
		if(StrUtils.isNull(multiId) || priceType!=1){
			out.put("state", 0);
			out.put("message", "审核失败！类型参数错误。");
			return out;
		}
		ParaMap target = priceChangeDao.getTransTarget(targetId);
		if(target == null){
			out.put("state", 0);
			out.put("message", "审核失败！标的参数错误。");
			return out;
		}
		ParaMap multi = priceChangeDao.getTransMulti(multiId);
		if(multi == null){
			out.put("state", 0);
			out.put("message", "审核失败！指标参数错误。");
			return out;
		}
		if(status.equals("2")){//如果是审核成功,如果标的已经开始交易，则审核失败
			int targetStatus = target.getInt("status");
			if(targetStatus>3){
				out.put("state", 0);
				out.put("message", "审核通过失败！标的已经开始交易，修改最终价无效。请选择审核不通过");
				return out;
			}
		}
		ParaMap result = priceChangeDao.checkPriceChange(logId, userId, status, checkMessage);
		if(!"1".equals(result.getString("state"))){
			throw new Exception("审核失败！保存审核信息错误。");
		}
		if(status.equals("2")){//审核成功需要修改多指标表和流程表
			result = priceChangeDao.updateMultiFinalPrice(multiId,newValue);
			if(!"1".equals(result.getString("state"))){
				throw new Exception("审核失败！保存指标信息错误。");
			}
			String qType = multi.getString("type");
			if("Q01".equals(qType)){
				result = priceChangeDao.updateTargetFinalPrice(targetId,newValue);
				if(!"1".equals(result.getString("state"))){
					throw new Exception("审核失败！保存指标信息错误。");
				}
			}
			
			result = priceChangeDao.updateProccessFinalPrice(targetId,multiId,newValue,qType);
			if(!"1".equals(result.getString("state"))){
				throw new Exception("审核失败！保存指标信息错误。");
			}
		}
		out.put("state", 1);
		out.put("message", "审核成功！");
		return out;
	}
	
	
	public String toWan(String money)throws Exception{
		if(StrUtils.isNull(money)){
			return null;
		}
		BigDecimal price = null;
		try{
			price = new BigDecimal(money);
		}catch(Exception e){
			return null;
		}
		DecimalFormat df = new DecimalFormat("###,###,###,###.####");
		return df.format((price.divide(new BigDecimal(Math.pow(10,4)), 4, BigDecimal.ROUND_HALF_UP))); 
				
	}

	
 
}

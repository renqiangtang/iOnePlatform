package com.after.service;

import java.util.List;

import com.after.dao.PlowConfirmDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class ConfirmService extends BaseService {
	
	/**
	 * 交易后标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap succTargetList(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String cantonId = inMap.getString("cantonId");
		String businessType = inMap.getString("businessType");
		String targetStatus = inMap.getString("targetStatus");
		String targetNo = inMap.getString("targetNo");
		
		String sql = "";
		if (StrUtils.isNotNull(targetNo) && !"-1".equals(targetNo)) {
			sql = sql + " and (tt.no like '%' || :targetNo || '%' or tt.name like '%' || :targetNo || '%') ";
		}
		if (StrUtils.isNotNull(cantonId) && !"-1".equals(cantonId)) {
			sql = sql + " and tg.canton_id = :cantonId ";
		}
		if (StrUtils.isNotNull(businessType) && !"-1".equals(businessType)) {
			sql = sql + " and tt.business_type = :businessType ";
		}
		if (StrUtils.isNotNull(targetStatus) && !"-1".equals(targetStatus)) {
			sql = sql + " and tt.status = :targetStatus ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getString("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getString("pageSize"));
		PlowConfirmDao checkDao = new PlowConfirmDao();
		ParaMap result = checkDao.succTargetList(sqlParams);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	/**
	 * 交易后标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap mySuccTargetList(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String cantonId = inMap.getString("cantonId");
		String businessType = inMap.getString("businessType");
		String targetStatus = inMap.getString("targetStatus");
		String targetNo = inMap.getString("targetNo");
		
		sqlParams.put("bidderId", bidderId);
		String sql = " and tl.bidder_id =:bidderId ";
		if (StrUtils.isNotNull(targetNo) && !"-1".equals(targetNo)) {
			sql = sql + " and (tt.no like '%' || :targetNo || '%' or tt.name like '%' || :targetNo || '%') ";
		}
		if (StrUtils.isNotNull(cantonId) && !"-1".equals(cantonId)) {
			sql = sql + " and tg.canton_id = :cantonId ";
		}
		if (StrUtils.isNotNull(businessType) && !"-1".equals(businessType)) {
			sql = sql + " and tt.business_type = :businessType ";
		}	else
		{
			sql = sql + " and tt.business_type like '501%'";
		}
		if (StrUtils.isNotNull(targetStatus) && !"-1".equals(targetStatus)) {
			sql = sql + " and tt.status = :targetStatus ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getString("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getString("pageSize"));
		PlowConfirmDao checkDao = new PlowConfirmDao();
		ParaMap result = checkDao.mySuccTargetList(sqlParams);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	
	/**
	 * 审核
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap confirmSuccTarget(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		String confirm = inMap.getString("confirm"); // 0 审核不通过、1审核通过
		if(confirm==null || ( !confirm.equals("0") && !confirm.equals("1"))){
			out.put("state", 0);
			out.put("message", "操作失败！操作类型错误。");
			return out;
		}
		PlowConfirmDao checkDao = new PlowConfirmDao();
		ParaMap target = checkDao.getTargetById(targetId);
		if(target == null ){
			out.put("state", 0);
			out.put("message", "操作失败！请选择有效的耕地指标。");
			return out;
		}
		if(target.getInt("status")!=5){
			out.put("state", 0);
			out.put("message", "操作失败！该耕地指标状态错误。");
			return out;
		}
		int status = confirm.equals("1")?7:8;
		out = checkDao.updateTargetStatus(targetId, status);
		if(!"1".equals(out.getString("state"))){
			out.put("message", "操作失败！");
		}else{
			out.put("message", "操作成功！");
		}
		return out;
		
	}
	
	/**
	 * 签订合同
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap contractSuccTarget(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		PlowConfirmDao checkDao = new PlowConfirmDao();
		ParaMap target = checkDao.getTargetById(targetId);
		if(target == null ){
			out.put("state", 0);
			out.put("message", "操作失败！请选择有效的耕地指标。");
			return out;
		}
		if(target.getInt("status")!=7){
			out.put("state", 0);
			out.put("message", "操作失败！该耕地指标状态错误。");
			return out;
		}
		out = checkDao.updateTargetStatus(targetId, 9);
		if(!"1".equals(out.getString("state"))){
			out.put("message", "操作失败！");
		}else{
			out.put("message", "操作成功！");
		}
		return out;
	}
	
	public String getBidderId(String userId) throws Exception {
		String bidderId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.querySimpleData("sys_user", sqlParams);
		String user_type = null;
		String ref_id = null;
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("user_type")) {
						user_type = String.valueOf(record.get(i));
					}
					if (filds.get(i).equals("ref_id")) {
						ref_id = (String) record.get(i);
					}
				}
			}
		}
		if (StrUtils.isNotNull(user_type) && "1".equals(user_type)) {
			bidderId = ref_id;
		}
		return bidderId;
	}
	
	
	public ParaMap getContractInfo(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		PlowConfirmDao checkDao = new PlowConfirmDao();
		ParaMap sellMap = checkDao.getSellBidder(targetId);
		if(sellMap!=null && sellMap.getSize()>0){
			out.put("sellBidder", sellMap.getRecordString(0, "name"));
			out.put("sellAddress", sellMap.getRecordString(0, "address"));
			out.put("sellPostCode", sellMap.getRecordString(0, "post_code"));
			out.put("sellRegCorPoration", sellMap.getRecordString(0, "reg_corporation"));
		}
		ParaMap buyMap = checkDao.getBuyBidder(targetId);
		if(buyMap!=null && buyMap.getSize()>0){
			out.put("buyBidder", buyMap.getRecordString(0, "name"));
			out.put("buyAddress", buyMap.getRecordString(0, "address"));
			out.put("buyPostCode", buyMap.getRecordString(0, "post_code"));
			out.put("buyRegCorPoration", buyMap.getRecordString(0, "reg_corporation"));
		}
		ParaMap targetMap = checkDao.getTargetInfo(targetId);
		if(targetMap!=null && targetMap.getSize()>0){
			out.put("targetNo", targetMap.getRecordString(0, "no"));
			out.put("targetArea", targetMap.getRecordString(0, "area"));
			out.put("targetUnitPrice", targetMap.getRecordString(0, "unit_price"));
			out.put("targetPrice", targetMap.getRecordString(0, "trans_price"));
		}
		return out;
	}
}

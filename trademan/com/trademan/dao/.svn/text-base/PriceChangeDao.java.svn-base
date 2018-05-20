package com.trademan.dao;

import java.util.List;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;


public class PriceChangeDao extends BaseDao {
	
	public ParaMap getTargetList(ParaMap sqlParams)throws Exception{
		sqlParams.put("moduleNo", "trans_target_lowest_price");
		sqlParams.put("dataSetNo", "get_target_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	public ParaMap getChangeLogList(String sql)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_target_lowest_price");
		sqlParams.put("dataSetNo", "get_price_change_Log_list");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getChangeLog(String id)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", id);
		sqlParams.put("is_valid", 1);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_price_change_log", sqlParams);
		if(result!=null && result.getSize()>0){
			List list = result.getListObj();
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	public ParaMap getTransTarget(String id)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", id);
		sqlParams.put("is_valid", 1);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_Target", sqlParams);
		if(result!=null && result.getSize()>0){
			List list = result.getListObj();
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	public ParaMap getTransMulti(String id)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", id);
		sqlParams.put("is_valid", 1);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.querySimpleData("trans_target_multi_trade", sqlParams);
		if(result!=null && result.getSize()>0){
			List list = result.getListObj();
			return (ParaMap)list.get(0);
		}else{
			return null;
		}
	}
	
	public ParaMap lowestChangeLogList(String targetId)throws Exception{
		String sql = " and l.price_type = 0 and l.target_id = '"+targetId+"' ";
		return getChangeLogList(sql);
	}
	
	public ParaMap finalChangeLogList(String multiId)throws Exception{
		String sql = " and l.price_type = 1 and l.multi_id = '"+multiId+"' ";
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_target_final_price");
		sqlParams.put("dataSetNo", "get_final_price_change_Log_list");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	public ParaMap savePriceChange(String targetId,String priceType , String multiId ,String oldValue , String newValue, String changeCause , String userId)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("target_id", targetId);
		sqlParams.put("price_type", priceType);
		sqlParams.put("multi_id", multiId);
		sqlParams.put("old_value", oldValue);
		sqlParams.put("new_value", newValue);
		sqlParams.put("change_cause", changeCause);
		sqlParams.put("create_user_id", userId);
		sqlParams.put("create_date", DateUtils.nowStr());
		ParaMap formatMap = new ParaMap();
		formatMap.put("create_date","to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_price_change_log", "id",sqlParams, formatMap);
		return result;
	}
	
	
	public ParaMap waitCheckLowestTargetList(ParaMap sqlParams)throws Exception{
		sqlParams.put("moduleNo", "trans_target_lowest_price_check");
		sqlParams.put("dataSetNo", "wait_check_lowest_targets");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap checkPriceChange(String logId , String userId , String status , String checkMessage)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", logId);
		sqlParams.put("status", status);
		sqlParams.put("check_message", checkMessage);
		sqlParams.put("check_user_id", userId);
		sqlParams.put("check_date", DateUtils.nowStr());
		ParaMap formatMap = new ParaMap();
		formatMap.put("check_date","to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_price_change_log", "id",sqlParams, formatMap);
		return result;
	}
	
	public ParaMap updateTargetReservePrice(String targetId , String reservePrice)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", targetId);
		sqlParams.put("reserve_price", reservePrice);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target", "id",sqlParams);
		return result;
	}
	
	
	public List getTargetMultiList(String targetId)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_target_final_price");
		sqlParams.put("dataSetNo", "get_target_multi_list");
		sqlParams.put("targetId", targetId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result.getListObj();
	}
	
	public ParaMap waitCheckFinalTargetList(ParaMap sqlParams)throws Exception{
		sqlParams.put("moduleNo", "trans_target_final_price_check");
		sqlParams.put("dataSetNo", "wait_check_final_targets");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	public ParaMap updateMultiFinalPrice(String multiId , String fianlPrice)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", multiId);
		sqlParams.put("final_value", fianlPrice);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target_multi_trade", "id",sqlParams);
		return result;
	}
	
	
	public ParaMap updateTargetFinalPrice(String targetId , String finalPrice)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", targetId);
		sqlParams.put("final_price", finalPrice);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target", "id",sqlParams);
		return result;
	}
	
	
	public ParaMap updateProccessFinalPrice(String targetId ,String multiId , String finalPrice , String qType)throws Exception{
		ParaMap out = new ParaMap();
		ParaMap keyData = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		//得到流程节点信息
		keyData.put("templateid", targetId);
		List nodeList = dataSetDao.querySimpleData("wf_node", keyData).getListObj();
		//得到当前阶段
		keyData.clear();
		keyData.put("moduleNo", "trans_target_final_price_check");
		keyData.put("dataSetNo", "get_target_process_tasknode");
		keyData.put("targetId", targetId);
		ParaMap nodeMap = dataSetDao.queryData(keyData);
		if(nodeMap==null || nodeMap.getSize()<1){
			throw new Exception("审核失败！流程信息错误。");
		}
		String taskNodeId = nodeMap.getRecordString(0, "tasknodeid");
		String nodeId = nodeMap.getRecordString(0, "nodeid");
		String taskPtype = nodeMap.getRecordString(0, "ptype");
		String taskQtype = nodeMap.getRecordString(0, "qtype");
		if(qType.equals("Q01")){//价格指标，需要修改公告期，集中报价期
			this.updateParameterFinalPrice(targetId+"L01",finalPrice);
			this.updateParameterFinalPrice(targetId+"L02",finalPrice);
		}
		this.updateParameterFinalPrice(targetId+"L03"+qType,finalPrice);
		this.updateParameterFinalPrice(taskNodeId,finalPrice);
		out.put("state", 1);
		out.put("message", "审核成功！");
		return out;
	}
	
	public ParaMap updateParameterFinalPrice(String refId,String value)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap insertMap = new ParaMap();
		insertMap.put("refid", refId);
		insertMap.put("name", "finalValue");
		insertMap.put("value", value);
		ParaMap result = dataSetDao.updateData("wf_parameter", "id","refid,name", insertMap);
		if("1".equals(result.getString("state"))){
			return result;
		}else{
			throw new Exception("审核失败！修改流程信息错误。");
		}
	}
	
	
	
	
}

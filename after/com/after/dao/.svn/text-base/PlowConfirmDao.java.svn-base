package com.after.dao;

import java.util.List;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

public class PlowConfirmDao extends BaseDao {
	
	public ParaMap succTargetList(ParaMap sqlParams) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleNo", "trans_after_confirm");
		sqlParams.put("dataSetNo", "get_trade_success_target_list");
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap mySuccTargetList(ParaMap sqlParams) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		sqlParams.put("moduleNo", "trans_after_confirm");
		sqlParams.put("dataSetNo", "get_my_success_target_list");
		return dataSetDao.queryData(sqlParams);
	}
	
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
	
	public ParaMap updateTargetStatus(String targetId , int status)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		keyData.put("status", status);
		ParaMap out = dataSetDao.updateData("trans_target", "id", keyData);
		return out;
	}
	
	public ParaMap getSellBidder(String targetId) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_after_confirm");
		sqlParams.put("dataSetNo", "get_target_sell_bidder");
		sqlParams.put("targetId", targetId);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getBuyBidder(String targetId) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_after_confirm");
		sqlParams.put("dataSetNo", "get_target_buy_succbidder");
		sqlParams.put("targetId", targetId);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getTargetInfo(String targetId) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_after_confirm");
		sqlParams.put("dataSetNo", "get_contract_target_info");
		sqlParams.put("targetId", targetId);
		return dataSetDao.queryData(sqlParams);
	}
}

package com.after.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

public class LandStatisticsQueryDao extends BaseDao {
	private DataSetDao  dataSetDao = new DataSetDao();
	/**
	 * 统计-标的查询
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryLandList(ParaMap inMap) throws Exception {
		inMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getString("pageIndex"));
		inMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getString("pageRowCount"));
		inMap.put(DataSetDao.SQL_ORDER_BY, inMap.getString(""));
		inMap.put("moduleNo", "query_land");
		inMap.put("dataSetNo", "queryLandList");
		return dataSetDao.queryData(inMap);
	}
	/**
	 * 
	 * @param target_id
	 * @return
	 */
	public ParaMap queryVendee(String target_id) throws Exception {
		ParaMap inMap = new ParaMap();
		inMap.put("moduleNo", "query_land");
		inMap.put("dataSetNo", "queryVendee");
		inMap.put("target_id", target_id);
		return dataSetDao.queryData(inMap);
	}
	/**
	 * 查询起始价
	 * @param target_id
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryBginPrice(String target_id,String organId) throws Exception {
		ParaMap inMap = new ParaMap();
		inMap.put("moduleNo", "query_land");
		inMap.put("dataSetNo", "queryBginPrice");
		inMap.put("target_id", target_id);
		inMap.put("organId", organId);
		return dataSetDao.queryData(inMap);
	}
	/**
	 * 查询竞价记录
	 * @param inMap
	 * @return
	 */
	public ParaMap queryTranrLog(ParaMap inMap)throws Exception {
		inMap.put("moduleNo", "query_land");
		inMap.put("dataSetNo", "queryTransOffer");
		inMap.put("target_id", inMap.getString("target_id"));
		return dataSetDao.queryData(inMap);
	}
	/**
	 * 查询竞买申请
	 * @param inMap
	 * @return
	 */
	public ParaMap exportEnlistList(ParaMap inMap) throws Exception {
		inMap.put("moduleNo", "query_land");
		inMap.put("dataSetNo", "queryTransLicense");
		inMap.put("target_id", inMap.getString("target_id"));
		return dataSetDao.queryData(inMap);
	}
}

package com.trademan.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 
 * @author danqing.luo
 * 
 */
public class PlowReportDao extends BaseDao {

	/**
	 * 交易统计报表数据查询
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransReportData(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (inMap.getString("dataSetNo") == null
				|| "".equals(inMap.getString("dataSetNo")))
			inMap.put("dataSetNo", "queryTransReport");
		ParaMap out = dataSetDao.queryData(inMap);
		return out;
	}

	/**
	 * 查询指标库统计
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransBudgetLib(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (inMap.getString("dataSetNo") == null
				|| "".equals(inMap.getString("dataSetNo")))
			inMap.put("dataSetNo", "querybudgetBase");
		ParaMap out = dataSetDao.queryData(inMap);
		return out;
	}

	/**
	 * 查询指标审核情况
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransBudgetAudit(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (inMap.getString("dataSetNo") == null
				|| "".equals(inMap.getString("dataSetNo")))
			inMap.put("dataSetNo", "HistogramStatistics");
		ParaMap out = dataSetDao.queryData(inMap);
		return out;

	}

	/**
	 * 价款统计报表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransBudgetPrice(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		if (inMap.getString("dataSetNo") == null
				|| "".equals(inMap.getString("dataSetNo")))
			inMap.put("dataSetNo", "queryPriceReport");
		ParaMap out = dataSetDao.queryData(inMap);
		return out;

	}

	public ParaMap getExcelBudgetStatistics(ParaMap inMap) throws Exception {

		DataSetDao dataSetDao = new DataSetDao();
		if (inMap.getString("dataSetNo") == null
				|| "".equals(inMap.getString("dataSetNo")))
			inMap.put("moduleNo", "suc_statisticw");
		inMap.put("dataSetNo", "statisticsExcel");
		ParaMap out = dataSetDao.queryData(inMap);
		return out;
	}
}

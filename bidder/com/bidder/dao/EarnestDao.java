package com.bidder.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 保证金查询DAO
 * 
 * @author chenl 2012-05-15
 */
public class EarnestDao extends BaseDao {
	public ParaMap getEarnestListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderTransEarnest");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	public ParaMap getEarnestAmountTotal(String userId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "bidder_trans_earnest");
		sqlParams.put("dataSetNo", "queryBidderTransEarnestSum");
		sqlParams.put("id", userId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

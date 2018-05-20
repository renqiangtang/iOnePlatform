package com.bidder.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 错转款查询DAO
 * 
 * @author chenl 2012-05-15
 */
public class PlowEarnestErrorDao extends BaseDao {
	public ParaMap getEarnestErrorListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderTransEarnestError");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

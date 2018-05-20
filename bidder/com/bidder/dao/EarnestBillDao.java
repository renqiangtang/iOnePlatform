package com.bidder.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 保证金到账查询DAO
 * 
 * @author chenl 2012-05-15
 */
public class EarnestBillDao extends BaseDao {
	public ParaMap getEarnestBillListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderTransEarnestBill");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

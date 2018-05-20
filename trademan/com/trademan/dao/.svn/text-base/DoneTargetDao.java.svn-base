package com.trademan.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 成交公示DAO
 * @author SAMONHUA
 * 2012-09-28
 */
public class DoneTargetDao extends BaseDao {	
	public ParaMap getDoneTargetListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_done_target_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryDoneTargetListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

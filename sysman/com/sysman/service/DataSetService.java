package com.sysman.service;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;

public class DataSetService extends BaseService {
	public ParaMap getDataSetSQL(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.getDataSetSQL(inMap);
	}

	public ParaMap queryData(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(inMap);
	}
	
	public ParaMap executeSQL(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.executeSQL(inMap);
	}
}

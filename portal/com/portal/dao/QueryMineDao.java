package com.portal.dao;

import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

public class QueryMineDao  extends BaseDao{
	/**
	 * 查找标的
	 * 
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetList(ParaMap sqlParams) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

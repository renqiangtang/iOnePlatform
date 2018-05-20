package com.portal.dao;

import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 门户查询DAO
 * 
 * @author  tyw2013-06-04
 */
public class QueryDao extends BaseDao {

	/**
	 * 查找标的
	 * 
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetList(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (StringUtils.isEmpty(moduleId)) {
			sqlParams.put("moduleNo", "trans_portal");
		}
		if (StringUtils.isEmpty(dataSetNo))
			sqlParams.put("dataSetNo", "query_trans_target_list");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

}

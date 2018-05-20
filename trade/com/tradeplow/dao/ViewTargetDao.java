package com.tradeplow.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 查看标的详情DAO
 * @author SAMONHUA
 * 2012-05-16
 */
public class ViewTargetDao extends BaseDao {
	public ParaMap getTargetData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_view_target");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTargetData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTargetGoodsData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_view_target");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo)) {
			String targetId = sqlParams.getString("targetId");
			if (StrUtils.isNull(targetId))
				targetId = sqlParams.getString("target_id");
			if (StrUtils.isNull(targetId))
				targetId = sqlParams.getString("id");
			DataSetDao dataSetDao = new DataSetDao();
			if (StrUtils.isNotNull(targetId)) {
				ParaMap data = new ParaMap();
				data.put("id", targetId);
				data = dataSetDao.querySimpleData("trans_target", data, null, "business_type");
				String businessType = data.getRecordString(0, 0);
				sqlParams.put("dataSetNo", "queryTargetGoodsData" + businessType.substring(0, 3));
				sqlParams.put("target_id", targetId);
			} else {
				String goodsId = sqlParams.getString("goodsId");
				ParaMap data = new ParaMap();
				data.put("id", goodsId);
				data = dataSetDao.querySimpleData("trans_goods", data, null, "goods_type");
				sqlParams.put("dataSetNo", "queryGoodsData" + data.getRecordString(0, 0));
				sqlParams.put("goods_id", targetId);
			}
		} else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTargetAttachData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_plow_view");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTargetAttachData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getGoodsAttachData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_view_target");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryGoodsAttachData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTargetNoticeData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_view_target");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTargetNoticeData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

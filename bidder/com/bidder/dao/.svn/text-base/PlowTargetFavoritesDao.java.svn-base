package com.bidder.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class PlowTargetFavoritesDao extends BaseDao {
	/**
	 * 添加标的到指定竞买人收藏夹
	 * @param bidderId
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap addTarget(String bidderId, String targetId) throws Exception {
		ParaMap data = new ParaMap();
		data.put("id", null);
		data.put("bidder_id", bidderId);
		data.put("target_id", targetId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_bidder_favorites", "id", "bidder_id,target_id", data);
		return result;
	}
	
	/**
	 * 查询收藏夹标的列表
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getFavoritesListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_favorites_list_plow");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryFavoritesListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}

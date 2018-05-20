package com.bidder.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.bidder.dao.TargetFavoritesDao;
import com.sysman.dao.UserDao;

public class PlowTargetFavoritesService extends BaseService {
	public ParaMap addTarget(ParaMap inMap) throws Exception {
		String bidderId = inMap.getString("bidder_id");
		if (StrUtils.isNull(bidderId)) {
			String userId = inMap.getString("u");
			if (StrUtils.isNull(userId)) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "未传入竞买人相关信息，无法添加标的到收藏夹");
				return result;
			}
			UserDao userDao = new UserDao();
			bidderId = userDao.getUserRefId(userId);
			if (StrUtils.isNull(bidderId)) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "未传入竞买人相关信息，而当前用户非竞买人用户，无法添加标的到收藏夹");
				return result;
			}
		}
		TargetFavoritesDao targetFavoritesDao = new TargetFavoritesDao();
		return targetFavoritesDao.addTarget(bidderId, inMap.getString("target_id"));
	}
	
	public ParaMap getFavoritesListData(ParaMap inMap) throws Exception {
		ParaMap result = null;
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField) && StrUtils.isNotNull(sortDir))
			sortField = sortField + " " + sortDir;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		String bidderId = inMap.getString("bidder_id");
		if (StrUtils.isNull(bidderId)) {
			String userId = inMap.getString("u");
			if (StrUtils.isNull(userId)) {
				result = new ParaMap();
				result.put("state", 0);
				result.put("message", "未传入竞买人相关信息，无法添加标的到收藏夹");
				return result;
			}
			UserDao userDao = new UserDao();
			bidderId = userDao.getUserRefId(userId);
			if (StrUtils.isNull(bidderId)) {
				result = new ParaMap();
				result.put("state", 0);
				result.put("message", "传入的用户非竞买人，无法查询收藏列表");
				return result;
			}
		}
		sqlParams.put("bidder_id", bidderId);
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("target")) {
			String target = inMap.getString("target");
			if (StrUtils.isNotNull(target)) {
				sqlParams.put("no", target);
				sqlParams.put("name", target);
				customCondition.append(" and (t.no like '%' || :no || '%' or t.name like '%' || :name || '%')");
			}
		}
		if (inMap.containsKey("beginCreateDate")) {
			String beginCreateDate = inMap.getString("beginCreateDate");
			if (StrUtils.isNotNull(beginCreateDate)) {
				sqlParams.put("create_date1", beginCreateDate);
				customCondition.append(" and to_char(t.create_date, 'yyyy-mm-dd') >= :create_date1");
			}
		}
		if (inMap.containsKey("endCreateDate")) {
			String endCreateDate = inMap.getString("endCreateDate");
			if (StrUtils.isNotNull(endCreateDate)) {
				sqlParams.put("create_date2", endCreateDate);
				customCondition.append(" and to_char(t.create_date, 'yyyy-mm-dd') <= :create_date2");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		TargetFavoritesDao targetFavoritesDao = new TargetFavoritesDao();
		result = targetFavoritesDao.getFavoritesListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = result.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		custom.put(MakeJSONData.CUSTOM_FIELDS, null);
		List items = MakeJSONData.makeItems(result, custom);
		result.clear();
		result.put("Rows", items);
		result.put("Total", totalRowCount);
		return result;
	}
}

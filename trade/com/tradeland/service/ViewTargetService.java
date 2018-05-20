package com.tradeland.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsLandDao;
import com.sysman.dao.ConfigDao;
import com.tradeland.dao.ViewTargetDao;

/**
 * 查看标的详情Service
 * 
 * @author SAMONHUA 2012-05-16
 */
public class ViewTargetService extends BaseService {
	public ParaMap getTargetData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getTargetData(inMap.getString("moduleId"),
				inMap.getString("dataSetNo"), inMap);
		ParaMap businessTypeConfig = ParaMap.fromString(ConfigDao
				.getBusinessType(6));
		if (businessTypeConfig != null && out.getInt("state") == 1
				&& out.getInt("totalRowCount") > 0) {
			businessTypeConfig = (ParaMap) businessTypeConfig.get(String
					.valueOf(out.getRecordValue(0, "business_type")));
			if (businessTypeConfig != null) {
				List moduleIds = new ArrayList();
				Iterator it = businessTypeConfig.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					if (key.endsWith("module_id")) {// StrUtils.indexOfIgnoreCase(key,
													// "module_id") >= 0
						String moduleId = businessTypeConfig.getString(key);
						if (StrUtils.isNotNull(moduleId)
								&& moduleIds.indexOf(moduleId) == -1) {
							moduleIds.add(moduleId);
						}
					}
				}
				if (moduleIds.size() > 0) {
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap keyData = new ParaMap();
					keyData.put("id", moduleIds);
					ParaMap data = dataSetDao.querySimpleData("sys_module",
							keyData);
					if (data != null && data.getInt("state") == 1
							&& data.getInt("totalRowCount") > 0) {
						ParaMap modules = new ParaMap();
						for (int i = 0; i < data.getSize(); i++)
							modules.put(String.valueOf(data.getRecordValue(i,
									"id")), String.valueOf(data.getRecordValue(
									i, "class_name")));
						businessTypeConfig.put("modules", modules);
					}
				}
				out.put("businessTypeConfig", businessTypeConfig);
			}
		}
		// 获取交易物信息
		String targetId = inMap.getString("id");
		TransGoodsLandDao dao = new TransGoodsLandDao();
		ParaMap goodsMap = dao.getGoodsInfoByTargetId(targetId);
		out.putAll(goodsMap);
		return out;
	}

	public ParaMap getTargetGoodsData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getTargetGoodsData(
				inMap.getString("moduleId"), inMap.getString("dataSetNo"),
				inMap);
		return out;
	}
	
	public ParaMap getTargetGoods(ParaMap inMap) throws Exception {
		TransGoodsLandDao transGoodsDao = new TransGoodsLandDao();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = transGoodsDao.getTransTargetGoodsList(inMap.getString("targetId"), null);
		List goodsList = out.getListObj();
		List resultList = new ArrayList();
		ParaMap keyData = new ParaMap();
		if(goodsList !=null && goodsList.size()>0){
			for(int i = 0 ; i < goodsList.size(); i ++ ){
				ParaMap dataMap = (ParaMap)goodsList.get(i);
				keyData.clear();
				keyData.put("ref_id", dataMap.getString("id"));
				keyData.put("ref_table_name", "trans_goods");
				ParaMap extendMap = dataSetDao.querySimpleData("sys_extend",keyData);
				List extendList = extendMap.getListObj();
				if(extendList!=null && extendList.size()>0){
					for(int j = 0 ; j < extendList.size() ; j ++ ){
						String field_no = ((ParaMap)extendList.get(j)).getString("field_no");
						String field_value = ((ParaMap)extendList.get(j)).getString("field_value");
						dataMap.put(field_no, field_value);
					}
				}
				resultList.add(dataMap);
				
			}
		}
		out.clear();
		out.put("Rows", resultList);
		return out;
	}

	public ParaMap getTargetAttachData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getTargetAttachData(
				inMap.getString("moduleId"), inMap.getString("dataSetNo"),
				inMap);
		return out;
	}

	public ParaMap getGoodsAttachData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getGoodsAttachData(
				inMap.getString("moduleId"), inMap.getString("dataSetNo"),
				inMap);
		return out;
	}

	public ParaMap getTargetNoticeData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getTargetNoticeData(
				inMap.getString("moduleId"), inMap.getString("dataSetNo"),
				inMap);
		return out;
	}
}

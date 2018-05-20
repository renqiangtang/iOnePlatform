package com.tradeplow.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsLandDao;
import com.sysman.dao.ConfigDao;
import com.sysman.dao.FieldChangeDao;
import com.tradeplow.dao.ViewTargetDao;

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
		
		FieldChangeDao fcDao = new FieldChangeDao();
		
		ParaMap changeMap=fcDao.getFieldChangeListData("trans_target", targetId, "status", "");
		if(changeMap.getInt("totalRowCount")>0){
			String change_cause=changeMap.getRecordString(0, "change_cause");
			out.put("change_cause", change_cause);
		}
		return out;
	}

	public ParaMap getTargetGoodsData(ParaMap inMap) throws Exception {
		ViewTargetDao viewTargetDao = new ViewTargetDao();
		ParaMap out = viewTargetDao.getTargetGoodsData(
				inMap.getString("moduleId"), inMap.getString("dataSetNo"),
				inMap);
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

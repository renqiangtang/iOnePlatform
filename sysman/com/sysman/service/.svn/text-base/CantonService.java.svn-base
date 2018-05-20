package com.sysman.service;

import java.util.List;

import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.CantonDao;

/**
 * 行政区管理Service
 * huafc
 * 2012-04-21
 */
public class CantonService extends BaseService {
	public ParaMap getCantonTreeData(ParaMap inMap) throws Exception {
		boolean blnUseModuleNo = inMap.containsKey("moduleNo");
		String moduleId = inMap.getString("moduleId");
		String moduleNo = inMap.getString("moduleNo");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		CantonDao cantonDao = new CantonDao();
		ParaMap out = null;
		if (intRecurse == 1) {
			if (blnUseModuleNo)
				out = cantonDao.getCantonRecurseTreeDataByModuleNo(moduleNo, dataSetNo, parentId);
			else
				out = cantonDao.getCantonRecurseTreeData(moduleId, dataSetNo, parentId);
		} else {
			if (blnUseModuleNo)
				out = cantonDao.getCantonFlatTreeDataByModuleNo(moduleNo, dataSetNo, parentId);
			else
				out = cantonDao.getCantonFlatTreeData(moduleId, dataSetNo, parentId);
		}
		//转换为前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, intRecurse);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "tree_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		custom.put(MakeJSONData.CUSTOM_CHILD_COUNT_FIELD_NAME, "child_count");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("parentId", "parent_id");
		fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("data", items);
		return out;
	}
	
	public ParaMap getCantonData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		CantonDao cantonDao = new CantonDao();
		ParaMap out = cantonDao.getCantonData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateCantonData(ParaMap inMap) throws Exception {
		CantonDao cantonDao = new CantonDao();
		ParaMap out = cantonDao.updateCantonData(inMap);
		return out;
	}
	
	public ParaMap deleteCantonData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		CantonDao cantonDao = new CantonDao();
		ParaMap out = cantonDao.deleteCantonData(moduleId, dataSetNo, id);
		return out;
	}
}

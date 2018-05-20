package com.sysman.service;

import java.util.List;

import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.ParamsDao;

/**
 * 系统参数管理Service
 * huafc
 * 2012-05-09
 */
public class ParamsService extends BaseService {
	public ParaMap getParamsTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		ParamsDao dao = new ParamsDao();
		ParaMap out = null;
		if (intRecurse == 1)
			out = dao.getParamsRecurseTreeData(moduleId, dataSetNo, parentId);
		else
			out = dao.getParamsFlatTreeData(moduleId, dataSetNo, parentId);
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
	
	public ParaMap getParamsData(ParaMap inMap) throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap out = paramsDao.getParamsData(inMap.getString("id"));
		return out;
	}
	
	public ParaMap updateParamsData(ParaMap inMap) throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap out = paramsDao.updateParamsData(inMap);
		return out;
	}
	
	public ParaMap deleteParamsData(ParaMap inMap) throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap out = paramsDao.deleteParamsData(inMap.getString("id"));
		return out;
	}
}

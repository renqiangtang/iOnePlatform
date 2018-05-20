package com.sysman.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.ModuleDao;
import com.sysman.service.MenuService.MenuTreeCallBack;
import java.net.URLDecoder;

public class ModuleService extends BaseService {	
	public ParaMap getModuleTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String moduleNo = inMap.getString("moduleNo");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		String searchNO=inMap.getString("searchNO");
		int intRecurse = 0;
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = null;
		if (moduleNo != null && !moduleNo.equalsIgnoreCase("null")) {
			if (moduleNo.equals(""))
				out = moduleDao.getModuleTreeData(null, dataSetNo, parentId,null);
			else
				out = moduleDao.getModuleTreeDataByModuleNo(moduleNo, dataSetNo, parentId);
		} else
			out = moduleDao.getModuleTreeData(moduleId, dataSetNo, parentId,searchNO);
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
		fields.put("dataType", "data_type");
		fields.put("treeLevel", "tree_level");
		fields.put("childCount", "child_count");
		if (inMap.containsKey("isexpand"))
			fields.put("isexpand", inMap.getString("isexpand"));
		else if (intRecurse == 1)
			fields.put("isexpand", "true");
		else
			fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("data", items);
		return out;
	}
	
	public ParaMap getModuleData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.getModuleData(inMap.getString("moduleId"), inMap.getString("dataSetNo"),
					inMap.getString("id"));
		return out;
	}
	
	public ParaMap getDataSetData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.getDataSetData(inMap.getString("id"));
		return out;
	}
	
	public ParaMap getResourceData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.getResourceData(inMap.getString("id"));
		return out;
	}
	
	public ParaMap updateModuleData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.updateModuleData(inMap);
		return out;
	}
	
	public ParaMap updateDataSetData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.updateDataSetData(inMap);
		return out;
	}
	
	public ParaMap updateResourceData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.updateResourceData(inMap);
		return out;
	}
	
	public ParaMap deleteModuleData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.deleteModuleData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap deleteDataSetData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.deleteDataSetData(inMap.getString("id"));
		return out;
	}
	
	public ParaMap deleteResourceData(ParaMap inMap) throws Exception {
		ModuleDao moduleDao = new ModuleDao();
		ParaMap out = moduleDao.deleteResourceData(inMap.getString("id"));
		return out;
	}
}

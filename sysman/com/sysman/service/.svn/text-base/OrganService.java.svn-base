package com.sysman.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.sysman.dao.OrganDao;

/**
 * 单位管理Service
 * huafc
 * 2012-05-05
 */
public class OrganService extends BaseService {
	public ParaMap getOrganTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		OrganDao dao = new OrganDao();
		ParaMap out = null;
		if (intRecurse == 1)
			out = dao.getOrganRecurseTreeData(moduleId, dataSetNo, parentId);
		else
			out = dao.getOrganFlatTreeData(moduleId, dataSetNo, parentId);
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
	
	public ParaMap getOrganData(ParaMap inMap) throws Exception {
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.getOrganData(inMap.getString("moduleId"), inMap.getString("dataSetNo"), inMap.getString("id"));
		return out;
	}
	
	public ParaMap updateOrganData(ParaMap inMap) throws Exception {
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.updateOrganData(inMap);
		return out;
	}
	
	public ParaMap deleteOrganData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.deleteOrganData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap setDefaultOrgan(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.setDefaultOrgan(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getDefaultOrganData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.getDefaultOrganData(moduleId, dataSetNo);
		return out;
	}
	
	/*
	 * 得到单位的交易资格列表
	 */
	public ParaMap getOrganQualification(ParaMap inMap) throws Exception{
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String organId = inMap.getString("organId");
		OrganDao organDao = new OrganDao();
		ParaMap out = organDao.getOrganQualification(moduleId,dataSetNo,organId);
		
		//转换json格式
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("goods_type_id", "goods_type_id");
		fields.put("business_type_id", "business_type_id");
		fields.put("name", "name");
		fields.put("organ_id", "organ_id");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/*
	 * 修改单位交易资格
	 */
	public ParaMap updateQualification(ParaMap inMap) throws Exception{
		String organId = inMap.getString("organId");
		String ids = inMap.getString("ids");
		String userId = inMap.getString("userId");
		
		//删除已有的资格
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("organ_id", organId);
		ParaMap out=dataSetDao.deleteSimpleData("trans_organ_business_rel", keyData);
		
		//循环新增资格
		if(StringUtils.isNotEmpty(ids)){
			String[] id = ids.split(",");
			for(int i= 0 ; i < id.length ; i++){
				if(StringUtils.isNotEmpty(id[i])){
					keyData.clear();
					keyData.put("id", "");
					keyData.put("organ_id", organId);
					keyData.put("business_type_rel_id", id[i]);
					keyData.put("is_valid", "1");
					keyData.put("create_user_id", userId);
					out=dataSetDao.updateData("trans_organ_business_rel","id", keyData);
				}
			}
		}
		return out;
	}
	
	
	
	
}

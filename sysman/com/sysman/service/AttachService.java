package com.sysman.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.JsonUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.AttachDao;

/**
 * 附件管理Service
 * huafc
 * 2012-05-26
 */
public class AttachService extends BaseService {
	public ParaMap getAttachRecurseTreeData(ParaMap inMap) throws Exception {
		String owners = inMap.getString("owners");
		AttachDao attachDao = new AttachDao();
		ParaMap out = null;
		if (owners.indexOf("{") >= 0 && owners.indexOf("}") >= 0 && owners.indexOf("[") >= 0 && owners.indexOf("]") >= 0)
			//ownersJSON对象: [{id: "0001", name: "标的", title: "标题", tableName: "trans_target", templetNo: "businessType0TargetAttach", category: null}]
			out = attachDao.getAttachRecurseTreeData(JsonUtils.StrToList(owners));
		else
			/**
			 * owners数据格式：所有者ID=名称,标题,表名,模板No;...
			 * 如：000001=标的,G2506-1113,trans_target,XXXXX
			 */
			out = attachDao.getAttachRecurseTreeData(owners);
		//转换为前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 1);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "tree_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		custom.put(MakeJSONData.CUSTOM_CHILD_COUNT_FIELD_NAME, "child_count");
//		ParaMap fields = new ParaMap();
//		fields.put("text", "name");
//		fields.put("id", "id");
//		fields.put("real_id", "real_id");
//		fields.put("parent_id", "parent_id");
//		fields.put("real_parent_id", "real_parent_id");
//		fields.put("ref_table_name", "ref_table_name");
//		fields.put("category", "category");
//		fields.put("templet_dtl_id", "templet_dtl_id");
//		fields.put("tree_level", "tree_level");
//		fields.put("templet_file_no", "templet_file_no");
//		fields.put("allow_dtl_count", "allow_dtl_count");
//		fields.put("allow_dtl_file_count", "allow_dtl_file_count");
//		fields.put("allow_file_type", "allow_file_type");
//		fields.put("isexpand", "true");
//		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out);//custom
		out.clear();
		out.put("data", items);
		return out;
	}
	
	//查询附件明细列表数据
	public ParaMap getAttachDtlListData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		//组织分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField) && StrUtils.isNotNull(sortDir))
			sortField = sortField + " " + sortDir;
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (StrUtils.isNotNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.getAttachDtlListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getAttachData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.getAttachData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateAttachData(ParaMap inMap) throws Exception {
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.updateAttachData(inMap);
		return out;
	}
	
	public ParaMap deleteAttachData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.deleteAttachData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateAttachDtlData(ParaMap inMap) throws Exception {
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.updateAttachDtlData(inMap);
		return out;
	}
	
	public ParaMap deleteAttachDtlData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.deleteAttachDtlData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getTempletTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String moduleNo = inMap.getString("moduleNo");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0; //0列表式，1递归树，2延迟加载树
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		AttachDao attachDao = new AttachDao();
		ParaMap out = null;
		if (moduleNo != null && !moduleNo.equalsIgnoreCase("null")) {
			if (moduleNo.equals(""))
				out = attachDao.getTempletTreeData(null, dataSetNo, parentId);
			else
				out = attachDao.getTempletTreeDataByModuleNo(moduleNo, dataSetNo, parentId);
		} else
			out = attachDao.getTempletTreeData(moduleId, dataSetNo, parentId);
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
	
	public ParaMap getTempletData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		AttachDao attachDao = new AttachDao();
		return attachDao.getTempletData(moduleId, dataSetNo, id);
	}
	
	public ParaMap getTempletDtlData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		if (StrUtils.isNull(id))
			id = inMap.getString("templetId");
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.getTempletDtlData(moduleId, dataSetNo, id);
		//是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect) && (strDirect.equalsIgnoreCase("true") || strDirect.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap updateTempletData(ParaMap bidderData) throws Exception {
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.updateTempletData(bidderData);
		return out;
	}

	public ParaMap deleteTempletData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.deleteTempletData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getAttachLobData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		List ownerList = null;
		String owners = StrUtils.trim(inMap.getString("owners"));
		if (StrUtils.isNotNull(owners)) {
			if (owners.startsWith("{")) {
				//前台通过json2转换数组到后台，IE下有BUG，转换成对象MAP（数组索引为key）
				ParaMap ownerMap = JsonUtils.StrToMap(owners);
				if (ownerMap != null && ownerMap.size() > 0) {
					ownerList = new ArrayList();
					Iterator it = ownerMap.keySet().iterator();
					while (it.hasNext())
						ownerList.add(ownerMap.get(it.next().toString()));
				}
			} else
				ownerList = JsonUtils.StrToList(owners);
		} else {
			ownerList = new ArrayList();
			ownerList.add(inMap);
		}
		AttachDao attachDao = new AttachDao();
		ParaMap out = attachDao.getAttachLobData(moduleId, dataSetNo, ownerList);
		//是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect) && (strDirect.equalsIgnoreCase("true") || strDirect.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap requiredAttachCheck(ParaMap inMap) throws Exception {
		AttachDao attachDao = new AttachDao();
		String refId = inMap.getString("refId");
		String refTableName = inMap.getString("refTableName");
		String templetId = inMap.getString("templetId");
		String category = inMap.getString("category");
		if (StrUtils.isNotNull(refId) && StrUtils.isNotNull(refTableName))
			return attachDao.requiredAttachCheck(refId, refTableName, templetId, category);
		else {
			String owners = inMap.getString("owners");
			if (StrUtils.isNotNull(owners)) {
				List ownerList = JsonUtils.StrToList(owners);
				if (ownerList != null && ownerList.size() > 0) {
					List checkFailList = new ArrayList();
					for(int i = 0; i < ownerList.size(); i++) {
						Map owner = (Map) ownerList.get(i);
						refId = String.valueOf(owner.get("refId"));
						refTableName = String.valueOf(owner.get("refTableName"));
						templetId = String.valueOf(owner.get("templetId"));
						category = String.valueOf(owner.get("category"));
						if (StrUtils.isNotNull(refId) && StrUtils.isNotNull(refTableName)) {
							ParaMap checkResult = attachDao.requiredAttachCheck(refId, refTableName, templetId, category);
							if (checkResult.getInt("state") != 1) {
								checkResult.put("state", 0);
								checkResult.put("refId", refId);
								checkResult.put("refTableName", refTableName);
								checkResult.put("templetId", templetId);
								checkResult.put("category", category);
								checkFailList.add(checkResult);
							}
						}
					}
					if (checkFailList.size() > 0) {
						ParaMap out = new ParaMap();
						out.put("state", 0);
						out.put("failOwners", checkFailList);
						return out;
					}
				}
			}
			ParaMap out = new ParaMap();
			out.put("state", 1);
			return out;
		}
	}
}
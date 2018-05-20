package com.sysman.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseUploadService;
import com.base.utils.JsonUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 附件管理Dao
 * huafc
 * 2012-05-26
 */
public class AttachDao extends BaseDao {
	/**
	 * 复制附件模板到通用附件表(从sys_attach_templet_dtl复制到sys_attach)
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @param deleteOldTempletData 是否删除本次复制模板之外的其它已经存在的来自模板的附件数据
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap copyAttachTemplet(String refId, String refTableName, String templetId, String category, boolean deleteOldTempletData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (deleteOldTempletData) {
			ParaMap templetData = getAttachAllTemplets(refId, refTableName, category);
			if (templetData.getInt("state") == 1 && templetData.getRecordCount() > 0)
				for(int i = 0; i < templetData.getRecordCount(); i++) {
					String oldTempletId = templetData.getRecordString(i, "id");
					if (!oldTempletId.equals(templetId))
						clearAttachTemplet(refId, refTableName, oldTempletId, category);
				}
		}
		sqlParams.clear();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "copyAttachTemplet");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("templet_id", templetId);
		sqlParams.put("category", category);
		ParaMap result = dataSetDao.executeSQL(sqlParams);
		return result;
	}
	
	/**
	 * 复制附件模板到通用附件表(从sys_attach_templet_dtl复制到sys_attach)
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板No，即sys_attach_templet.no
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @param deleteOldTempletData 是否删除本次复制模板之外的其它已经存在的来自模板的附件数据
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap copyAttachTempletByNo(String refId, String refTableName, String templetNo, String category, boolean deleteOldTempletData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", templetNo);
		ParaMap result = dataSetDao.querySimpleData("sys_attach_templet", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") > 0) {
				result = copyAttachTemplet(refId, refTableName, result.getRecordString(0, "id"), category, deleteOldTempletData);
			} else {
				result.clear();
				result.put("state", 0);
				result.put("message", "未找到指定附件模板记录");
			}
		}
		return result;
	}
	
	/**
	 * 复制附件模板到通用附件表(从sys_attach_templet_dtl复制到sys_attach)
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板No，即sys_attach_templet.no
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap copyAttachTempletByNo(String refId, String refTableName, String templetNo, String category) throws Exception {
		return copyAttachTempletByNo(refId, refTableName, templetNo, category, false);
	}
	
	/**
	 * 复制附件模板到通用附件表(从sys_attach_templet_dtl复制到sys_attach)
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板No，即sys_attach_templet.no
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap copyAttachTempletByNo(String refId, String refTableName, String templetNo) throws Exception {
		return copyAttachTempletByNo(refId, refTableName, templetNo, null);
	}
	
	/**
	 * 更换附件所有者的附件清单的模块。清除以前的模板，但保留新模板中有相同文件编号（sys_attach_templet_dtl.no）的记录，以及用户自行新增的明细
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap changeAttachTemplet(String refId, String refTableName, String templetId, String category) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "changeAttachTemplet");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("templet_id", templetId);
		sqlParams.put("category", category);
		ParaMap result = dataSetDao.executeSQL(sqlParams);
		return result;
	}
	
	/**
	 * 更换附件所有者的附件清单的模块。清除以前的模板，但保留新模板中有相同文件编号（sys_attach_templet_dtl.no）的记录，以及用户自行新增的明细
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap changeAttachTemplet(String refId, String refTableName, String templetId) throws Exception {
		return changeAttachTemplet(refId, refTableName, templetId, null);
	}
	
	/**
	 * 更换附件所有者的附件清单的模块。清除以前的模板，但保留新模板中有相同文件编号（sys_attach_templet_dtl.no）的记录，以及用户自行新增的明细
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板No，即sys_attach_templet.no
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap changeAttachTempletByNo(String refId, String refTableName, String templetNo, String category) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", templetNo);
		ParaMap result = dataSetDao.querySimpleData("sys_attach_templet", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") > 0) {
				result = changeAttachTemplet(refId, refTableName, result.getRecordString(0, "id"), category);
			} else {
				result.clear();
				result.put("state", 0);
				result.put("message", "未找到指定附件模板记录");
			}
		}
		return result;
	}
	
	/**
	 * 更换附件所有者的附件清单的模块。清除以前的模板，但保留新模板中有相同文件编号（sys_attach_templet_dtl.no）的记录，以及用户自行新增的明细
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板No，即sys_attach_templet.no
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap changeAttachTempletByNo(String refId, String refTableName, String templetNo) throws Exception {
		return changeAttachTempletByNo(refId, refTableName, templetNo, null);
	}
	
	/**
	 * 清除所有者所有来自指定模板的附件数据
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id。指定则只清除来自此模板，否则所有
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap clearAttachTemplet(String refId, String refTableName, String templetId, String category) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "queryAttachListData");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("category", category);
		if (StrUtils.isNull(templetId)) {
			ParaMap templetData = getAttachAllTemplets(refId, refTableName, category);
			if (templetData.getInt("state") == 1 && templetData.getRecordCount() > 0) {
				for(int i = 0; i < templetData.getRecordCount(); i++) {
					sqlParams.put("templet_id", templetData.getRecordString(i, "id"));
					ParaMap attachData = dataSetDao.queryData(sqlParams);
					if (attachData.getInt("state") == 1 && attachData.getRecordCount() > 0) {
						for(int j = 0; j < attachData.getRecordCount(); j++) {
							result = deleteAttachFile(attachData.getRecordString(j, "id"));
							if (result.getInt("state") != 1)
								throw new Exception(result.getString("message")); 
						}
					}
					sqlParams.put("dataSetNo", "clearAttachTemplet");
					result = dataSetDao.executeSQL(sqlParams);
				}
			} else {
				result = new ParaMap();
				result.put("state", 1);
				result.put("message", "无来自模板的附件数据");
			}
		} else {
			sqlParams.put("templet_id", templetId);
			ParaMap attachData = dataSetDao.queryData(sqlParams);
			if (attachData.getInt("state") == 1 && attachData.getRecordCount() > 0) {
				for(int j = 0; j < attachData.getRecordCount(); j++) {
					result = deleteAttachFile(attachData.getRecordString(j, "id"));
					if (result.getInt("state") != 1)
						throw new Exception(result.getString("message")); 
				}
			}
			sqlParams.put("dataSetNo", "clearAttachTemplet");
			result = dataSetDao.executeSQL(sqlParams);
		}
		return result;
	}
	
	/**
	 * 清除所有者所有来自模板的附件数据
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap clearAttachTemplet(String refId, String refTableName, String category) throws Exception {
		return clearAttachTemplet(refId, refTableName, null, category);
	}
	
	/**
	 * 清除所有者所有来自模板的附件数据
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap clearAttachTemplet(String refId, String refTableName) throws Exception {
		return clearAttachTemplet(refId, refTableName, null);
	}
	
	/**
	 * 清除所有者所有来自指定模板编号的附件数据
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板no，即sys_attach_templet.no。指定则只清除来自此模板，否则所有
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap clearAttachTempletByNo(String refId, String refTableName, String templetNo, String category) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("no", templetNo);
		ParaMap result = dataSetDao.querySimpleData("sys_attach_templet", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") > 0) {
				result = clearAttachTempletByNo(refId, refTableName, result.getRecordString(0, "id"), category);
			} else {
				result.clear();
				result.put("state", 0);
				result.put("message", "未找到指定附件模板记录");
			}
		}
		return result;
	}
	
	/**
	 * 清除所有者所有来自指定模板编号的附件数据
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetNo 附件模板no，即sys_attach_templet.no。指定则只清除来自此模板，否则所有
	 * @return 复制成功返回state=1。可重复调用
	 * @throws Exception
	 */
	public ParaMap clearAttachTempletByNo(String refId, String refTableName, String templetNo) throws Exception {
		return clearAttachTempletByNo(refId, refTableName, templetNo, null);
	}
	
	/**
	 * 清除指定所有者所有附件
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return
	 * @throws Exception
	 */
	public ParaMap clearAttach(String refId, String refTableName, String category) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "queryAttachListData");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("category", null);
		sqlParams.put("templet_id", null);
		ParaMap attachData = dataSetDao.queryData(sqlParams);
		if (attachData.getInt("state") == 1 && attachData.getRecordCount() > 0) {
			for(int i = 0; i < attachData.getRecordCount(); i++) {
				result = deleteAttachData(null, null, attachData.getRecordString(i, "id"));
				if (result.getInt("state") != 1)
					throw new Exception(result.getString("message")); 
			}
		}
		return result;
	}
	
	/**
	 * 清除指定所有者所有附件
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @return
	 * @throws Exception
	 */
	public ParaMap clearAttach(String refId, String refTableName) throws Exception {
		return clearAttach(refId, refTableName, null);
	}
	
	public ParaMap getAttachRecurseTreeData(String owners) throws Exception {
		/**
		 * parentDatas数据格式：所有者ID=名称,标题,表名,模板No;...
		 * 如：000001=标的,G2506-1113,trans_target,XXXXX
		 */
		if (StrUtils.isNull(owners))
			return null;
		List<ParaMap> ownerList = new ArrayList<ParaMap>();
		for(int i = 0; i <= StrUtils.getSubStrCount(owners, ";", false); i++) {
			String subStr = StrUtils.getSubStr(owners, ";", i, false);
			String refId = StrUtils.getSubStr(subStr, "=", 0, false);
			if (StrUtils.isNotNull(refId)) {
				ParaMap owner = new ParaMap();
				owner.put("id", refId);
				String refValue = StrUtils.getSubStr(subStr, "=", 1, false);
				owner.put("name", StrUtils.getSubStr(refValue, ",", 0, false));
				owner.put("title", StrUtils.getSubStr(refValue, ",", 1, false));
				owner.put("tableName", StrUtils.getSubStr(refValue, ",", 2, false));
				owner.put("templetNo", StrUtils.getSubStr(refValue, ",", 3, false));
				owner.put("clearTemplet", StrUtils.getSubStr(refValue, ",", 4, false));
				ownerList.add(owner);
			}
		}
		return getAttachRecurseTreeData(ownerList);
	}
	
	public ParaMap getAttachRecurseTreeData(List owners) throws Exception {
		//ownersJSON对象: [{id: "0001", name: "标的", title: "标题", tableName: "trans_target", templetNo: "businessType0TargetAttach", category: null}]
		if (owners == null || owners.size() == 0)
			return null;
		ParaMap sqlParams = new ParaMap();
		StringBuffer sql = new StringBuffer();
		for(int i = 0; i < owners.size(); i++) {
			ParaMap owner = (ParaMap) owners.get(i);
			String refId = owner.getString("id");
			if (StrUtils.isNotNull(refId)) {
				String refName = owner.getString("name");
				String refTitle = owner.getString("title");
				String refTableName = owner.getString("tableName");
				String templetNo = owner.getString("templetNo");
				String clearTemplet = owner.getString("clearTemplet");
				String category = owner.getString("category");
				if (i > 0)
					sql.append(" union \n");
				sql.append(" select :ref_id_" + i + " || '-' || '" + (StrUtils.isNull(category) ? "" : category) + "' as id, :ref_id_" + i + " as real_id,"
						+ " :ref_label_" + i + " as text, " + " :ref_label_" + i + " as name, 1 as tree_level, null as parent_id, null as real_parent_id,"
						+ " 0 as child_count, " + i + " as turn, :ref_table_name_" + i + " as ref_table_name, null as templet_dtl_id, "
						+ (StrUtils.isNull(category) ? "null" : "'" + category + "'") + " as category,"
						+ " null as templet_file_no, 0 as allow_dtl_count, 0 as allow_dtl_file_count, null as allow_file_type,"
						+ " null as file_desc, 0 as file_size, 0 as is_publish, 0 as is_required"
						+ " from dual \n");
				sql.append(" union select a.id, a.id as real_id,"
						+ " nvl(a.name, a.no) as text, " + " nvl(a.name, a.no) as name, 2 as tree_level, a.ref_id || '-' || a.category as parent_id, a.ref_id as real_parent_id,"
						+ " 0 as child_count, a.turn, a.ref_table_name, a.templet_dtl_id,"
						+ " a.category,"
						+ " td.no as templet_file_no, nvl(td.dtl_count, 0) as allow_dtl_count, nvl(td.dtl_file_count, 0) as allow_dtl_file_count, td.file_type as allow_file_type,"
						+ " td.file_desc, td.file_size, td.is_publish, td.is_required"
						+ " from sys_attach a"
						+ " left join sys_attach_templet_dtl td on td.id = a.templet_dtl_id"
						+ " where a.ref_id = :ref_id_" + i);
				if (StrUtils.isNotNull(refTableName))
					sql.append(" and a.ref_table_name = :ref_table_name_" + i);
				else
					sql.append(" and a.ref_table_name is null");
				if (StrUtils.isNotNull(category))
					sql.append(" and a.category = :category_" + i);
				else
					sql.append(" and a.category is null");
				sqlParams.put("ref_id_" + i, refId);
				sqlParams.put("ref_label_" + i, StrUtils.isNotNull(refTitle) ? refTitle : StrUtils.isNotNull(refName) ? refName : refId);
				sqlParams.put("ref_table_name_" + i, refTableName);
				if (StrUtils.isNotNull(category))
					sqlParams.put("category_" + i, category);
				//如果模板ID不为空
				if (StrUtils.isNotNull(templetNo)) {
					boolean deleteOldTempletData = false;
					if (StrUtils.isNotNull(clearTemplet) && (clearTemplet.equals("1") || clearTemplet.equalsIgnoreCase("true") || clearTemplet.equalsIgnoreCase("yes")))
						deleteOldTempletData = true;
					copyAttachTempletByNo(refId, refTableName, templetNo, category, deleteOldTempletData);
				}
			}
		}
		sql.append(" order by turn, id");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sql.toString(), sqlParams);
		return result;
	}
	
	//查询附件列表（树）数据
	public ParaMap getAttachListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//查询附件明细列表数据
	public ParaMap getAttachDtlListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachDtlListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getAttachData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateAttachData(ParaMap attachData)  throws Exception{
		ParaMap result;
		//检附件名称
		ParaMap checkAttachData = new ParaMap();
		String attachName = attachData.getString("name");
		if (!StrUtils.isNull(attachName)) {
			checkAttachData.put("id", attachData.getString("id"));
			checkAttachData.put("ref_id", attachData.getString("ref_id"));
			checkAttachData.put("ref_table_name", attachData.getString("ref_table_name"));
			checkAttachData.put("category", attachData.getString("category"));
			checkAttachData.put("name", attachName);
			result = checkAttachExists(null, null, checkAttachData);
			int state = result.getInt("state");
			if (state == 1) {
				if ((Integer) result.getRecordValue(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "附件名 " + attachData.getString("name") + " 已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		result = dataSetDao.updateData("sys_attach", "id", attachData);
		return result;
	}
	
	public ParaMap deleteAttachData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteAttachData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = deleteAttachFile(id);
		if (result.getInt("state") == 1)
			result = dataSetDao.executeSQL(sqlParams);
		return result;
	}
	
	public ParaMap updateAttachDtlData(ParaMap attachDtlData)  throws Exception{
		ParaMap result;
		//检附件名称
		ParaMap checkAttachDtlData = new ParaMap();
		String attachDtlName = attachDtlData.getString("file_name");
		if (!StrUtils.isNull(attachDtlName)) {
			checkAttachDtlData.put("id", attachDtlData.getString("id"));
			checkAttachDtlData.put("attach_id", attachDtlData.getString("attach_id"));
			checkAttachDtlData.put("file_name", attachDtlName);
			result = checkAttachDtlExists(null, null, checkAttachDtlData);
			int state = result.getInt("state");
			if (state == 1) {
				if ((Integer) result.getRecordValue(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "附件明细文件名 " + attachDtlData.getString("file_name") + " 已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		result = dataSetDao.updateData("sys_attach_dtl", "id", attachDtlData);
		return result;
	}
	
	public ParaMap deleteAttachDtlData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteAttachDtlData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = deleteAttachDtlFile(id);
		if (result.getInt("state") == 1)
			result = dataSetDao.executeSQL(sqlParams);
		return result;
	}
	
	public ParaMap checkAttachExists(String moduleId, String dataSetNo, ParaMap attachData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		if (attachData.containsKey("id")) {
			String id = attachData.getString("id");
			if (StrUtils.isNotNull(id)) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (attachData.containsKey("ref_id")) {
			String refId = attachData.getString("ref_id");
			if (StrUtils.isNotNull(refId)) {
				sqlParams.put("ref_id", refId);
				customCondition.append(" and ref_id = :ref_id");
			}
		}
		if (attachData.containsKey("ref_table_name")) {
			String refTableName = attachData.getString("ref_table_name");
			if (StrUtils.isNotNull(refTableName)) {
				sqlParams.put("ref_id", refTableName);
				customCondition.append(" and ref_table_name = :ref_table_name");
			}
		}
		if (attachData.containsKey("category")) {
			String category = attachData.getString("category");
			if (StrUtils.isNotNull(category)) {
				sqlParams.put("category", category);
				customCondition.append(" and category = :category");
			} else
				customCondition.append(" and category is null");
		} else
			customCondition.append(" and category is null");
		if (attachData.containsKey("name")) {
			String name = attachData.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap checkAttachDtlExists(String moduleId, String dataSetNo, ParaMap attachDtlData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachDtlExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		StringBuffer customCondition = new StringBuffer("");
		if (attachDtlData.containsKey("id")) {
			String id = attachDtlData.getString("id");
			if (StrUtils.isNotNull(id)) {
				sqlParams.put("id", id);
				customCondition.append(" and id <> :id");
			}
		}
		if (attachDtlData.containsKey("attach_id")) {
			String attachId = attachDtlData.getString("attach_id");
			if (StrUtils.isNotNull(attachId)) {
				sqlParams.put("attach_id", attachId);
				customCondition.append(" and attach_id = :attach_id");
			}
		}
		if (attachDtlData.containsKey("file_name")) {
			String fileName = attachDtlData.getString("file_name");
			if (StrUtils.isNotNull(fileName)) {
				sqlParams.put("file_name", fileName);
				customCondition.append(" and file_name = :file_name");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 模块树数据
	 * @param moduleId
	 * @param dataSetNo
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTempletTreeData(String moduleId, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTempletTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTempletTreeDataByModuleNo(String moduleNo, String dataSetNo, String parentId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleNo))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleNo", moduleNo);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTempletTree");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("parent_id", parentId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTempletData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTempletData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getTempletDtlData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTempletDtlData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("templet_id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateTempletData(ParaMap templetData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result;
		//检查模板名称及编号
		ParaMap checkTempletData = new ParaMap();
		String name = templetData.getString("name");
		if (StrUtils.isNotNull(name)) {
			checkTempletData.put("id", templetData.getString("id"));
			checkTempletData.put("name", name);
			checkTempletData.put("no", templetData.getString("no"));
			result = checkTempletExists(null, null, checkTempletData);
			if (result.getInt("state") == 1) {
				if (result.getRecordInteger(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					if (StrUtils.isNotNull(templetData.getString("no")))
						result.put("message", "附件模板名称" + templetData.getString("name") + "或者编号" + templetData.getString("no") + "已存在，不能重复");
					else
						result.put("message", "附件模板名称" + templetData.getString("name") + "已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		//保存模板
		String id = templetData.getString("id");
		if (!templetData.containsKey("create_user_id"))
			templetData.put("create_user_id", templetData.getString("u"));
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("sys_attach_templet", "id", templetData, format);
		if (result.getInt("state") == 1) {
			if (StrUtils.isNull(id))//新增数据
				id = result.getString("id");
		} else
			throw new Exception("保存附件模板数据失败，操作取消。");
		ParaMap data = new ParaMap();
		List updateDatas = new ArrayList();
		//保存代理人数据
		String deleteTempletDtlIds = templetData.getString("deleteTempletDtlIds");
		if (StrUtils.isNotNull(deleteTempletDtlIds)) {
			List deleteTempletDtlIdList = StrUtils.getSubStrs(deleteTempletDtlIds, ";");
			if (deleteTempletDtlIdList.size() > 0) {
				data.clear();
				data.put("attach_templet_id", id);
				data.put("id", deleteTempletDtlIdList);
				dataSetDao.deleteSimpleData("sys_attach_templet_dtl", data);
			}
		}
		String templetDtls = templetData.getString("templetDtls");
		if (StrUtils.isNotNull(templetDtls)) {
			List templetDtlList = JsonUtils.StrToList(templetDtls);
			for (int i = 0 ; i < templetDtlList.size(); i++) {
				Map templetDtl = (Map) templetDtlList.get(i);
				String dtlId = String.valueOf(templetDtl.get("id"));
				if (StrUtils.isNull(dtlId))
					dtlId = null;
				ParaMap templetDtlData = new ParaMap();
				templetDtlData.put("id", dtlId);
				templetDtlData.put("attach_templet_id", id);
				templetDtlData.put("name", templetDtl.get("name"));
				templetDtlData.put("no", templetDtl.get("no"));
				templetDtlData.put("media", templetDtl.get("media"));
				templetDtlData.put("is_required", templetDtl.get("is_required"));
				templetDtlData.put("is_publish", templetDtl.get("is_publish"));
				templetDtlData.put("is_archive", templetDtl.get("is_archive"));
				templetDtlData.put("dtl_count", templetDtl.get("dtl_count"));
				templetDtlData.put("dtl_file_count", templetDtl.get("dtl_file_count"));
				templetDtlData.put("file_type", templetDtl.get("file_type"));
				templetDtlData.put("file_size", templetDtl.get("file_size"));
				templetDtlData.put("file_desc", templetDtl.get("file_desc"));
				templetDtlData.put("turn", templetDtl.get("turn"));
				templetDtlData.put("remark", templetDtl.get("remark"));
				if (StrUtils.isNull(dtlId))
					templetDtlData.put("create_user_id", templetDtl.get("u"));
				ParaMap record = new ParaMap();
				record.put("tableName", "sys_attach_templet_dtl");
				record.put("keyField", "id");
				record.put("data", templetDtlData);
				record.put("format", format);
				updateDatas.add(record);
	        }
			if (updateDatas.size() > 0) {
				result = dataSetDao.updateData(updateDatas);
				updateDatas.clear();
			}
		}
		result.clear();
		result.put("state", 1);
		result.put("id", id);
		return result;
	}
	
	public ParaMap deleteTempletData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteTempletData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	public ParaMap checkTempletExists(String moduleId, String dataSetNo, ParaMap templetData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_templet_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTempletExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = templetData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(id)) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
		customCondition.append(" and (1 = 2 ");
		//String name = templetData.getString("name");
		//sqlParams.put("name", name);
		//customCondition.append(" or name = :name");
		String no = templetData.getString("no");
		if (StrUtils.isNotNull(no)) {
			sqlParams.put("no", no);
			customCondition.append(" or no = :no");
		}
		customCondition.append(")");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 获取指定条件下所有附件sys_lob记录
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getAttachLobData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (sqlParams == null)
			sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_attach_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryAttachLobData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 获取指定附件所有者的所有附件sys_lob记录
	 * @param moduleId
	 * @param dataSetNo
	 * @param owners
	 * @return
	 * @throws Exception
	 */
	public ParaMap getAttachLobData(String moduleId, String dataSetNo, List<ParaMap> owners) throws Exception {
		ParaMap result = null;
		if (owners != null && owners.size() > 0) {
			ParaMap sqlParams = new ParaMap();
			StringBuffer customCondition = new StringBuffer("");
			customCondition.append(" and (1 = 2 ");
			for(int i = 0; i < owners.size(); i++) {
				ParaMap owner = (ParaMap) owners.get(i);
				if (owner != null && owner.containsKey("refId")) {
					String refId = owner.getString("refId");
					sqlParams.put("ref_id", refId);
					customCondition.append(" or (a.ref_id = :ref_id");
					if (owner.containsKey("refTableName")) {
						String refTableName = owner.getString("refTableName");
						if (StrUtils.isNotNull(refTableName)) {
							sqlParams.put("ref_table_name", refTableName);
							customCondition.append(" and a.ref_table_name = :ref_table_name");
						}
					} else {
						customCondition.append(" and a.ref_table_name is null");
					}
					if (owner.containsKey("category")) {
						String category = owner.getString("category");
						if (StrUtils.isNotNull(category)) {
							sqlParams.put("category", category);
							customCondition.append(" and a.category = :category");
						}
					} else {
						customCondition.append(" and a.category is null");
					}
					if (owner.containsKey("templetNo") || owner.containsKey("publishOnly")) {
						String templetNo = owner.getString("templetNo");
						String publishOnly = owner.getString("publishOnly");
						boolean requiredPublish = StrUtils.isNotNull(publishOnly) && (publishOnly.equals("1") || publishOnly.equalsIgnoreCase("true") || publishOnly.equalsIgnoreCase("yes"));
						if (StrUtils.isNotNull(templetNo) || requiredPublish) {
							customCondition.append(" and a.templet_dtl_id in (select id from sys_attach_templet_dtl where 1 = 1 ");
							if (StrUtils.isNotNull(templetNo)) {
								sqlParams.put("templet_no", templetNo);
								customCondition.append(" and attach_templet_id in (select id from sys_attach_templet where no = :templet_no)");
							}
							if (requiredPublish)
								customCondition.append(" and is_publish = 1");
							customCondition.append(")");
						}
					}
					if (owner.containsKey("fileNo")) {
						String fileNo = owner.getString("fileNo");
						if (StrUtils.isNotNull(fileNo)) {
							List<String> fileNos = StrUtils.getSubStrs(fileNo, fileNo.indexOf(";") >= 0 ? ";" : ",");
							if (fileNos != null && fileNos.size() > 0) {
								customCondition.append(" and a.no in (");
								for(int j = 0; j < fileNos.size(); j++) {
									sqlParams.put("no" + j, fileNos.get(j));
									sqlParams.put("file_no"+(j==0?"":j), fileNos.get(j));
									customCondition.append(":no" + j + ",");
								}
								customCondition.deleteCharAt(customCondition.length() - 1);
								customCondition.append(") ");
							}
						}
					}
					if (owner.containsKey("fileType")) {
						String fileType = owner.getString("fileType");
						if (StrUtils.isNotNull(fileType)) {
							List<String> fileTypes = StrUtils.getSubStrs(fileType, fileType.indexOf(";") >= 0 ? ";" : ",");
							if (fileTypes != null && fileTypes.size() > 0) {
								customCondition.append(" and substr(ad.file_name, instr(ad.file_name, '.', -1)) in (");
								for(int j = 0; j < fileTypes.size(); j++) {
									sqlParams.put("file_type" + j, fileTypes.get(j));
									customCondition.append(":file_type" + j + ",");
								}
								customCondition.deleteCharAt(customCondition.length() - 1);
								customCondition.append(") ");
							}
						}
					}
					customCondition.append(") ");
				}
			}
			customCondition.append(")");
			ParaMap customConditions = new ParaMap();
			customConditions.put("CUSTOM_CONDITION", customCondition.toString());
			sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
			result = getAttachLobData(moduleId, dataSetNo, sqlParams);
		} else {
			result = new ParaMap();
			result.put("state", 0);
			result.put("message", "未指定附件所有者信息");
		}
		return result;
	}
	
	/**
	 * 删除附件所有明细关联的文件及sys_lob表记录，但不删除attachId对应的sys_attach记录及所有的sys_attach_dtl记录
	 * @param attachId 附件id，sys_attach.id
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteAttachFile(String attachId) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(attachId)) {
			result = new ParaMap();
			result.put("state", 1);
			result.put("message", "未传附件ID");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap dtlData = new ParaMap();
		dtlData.put("attach_id", attachId);
		dtlData = dataSetDao.querySimpleData("sys_attach_dtl", dtlData);
		if (dtlData.getInt("state") == 1 && dtlData.getInt("totalRowCount") > 0) {
			for(int i = 0; i < dtlData.getRecordCount(); i++) {
				result = deleteAttachDtlFile(dtlData.getRecordString(i, "id"));
				if (result.getInt("state") != 1)
					throw new Exception("删除附件所有文件失败" + (result.containsKey("message") ? "，错误信息：" + result.getString("message") : "") + "。");
			}
		} else {
			result = new ParaMap();
			result.put("state", 1);
			result.put("message", "未查询到附件明细数据");
		}
		return result;
	}
	
	/**
	 * 删除附件明细关联的文件及sys_lob表记录，但不删除attachDtlId对应的sys_attach_dtl记录
	 * @param attachDtlId 附件明细id，sys_attach_dtl.id
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteAttachDtlFile(String attachDtlId) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(attachDtlId)) {
			result = new ParaMap();
			result.put("state", 1);
			result.put("message", "未传附件明细ID");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap lobData = new ParaMap();
		lobData.put("ref_table_name", "sys_attach_dtl");
		lobData.put("ref_id", attachDtlId);
		lobData = dataSetDao.querySimpleData("sys_lob", lobData);
		if (lobData.getInt("state") == 1 && lobData.getInt("totalRowCount") > 0) {
			ParaMap data = new ParaMap();
			BaseUploadService bus = new BaseUploadService();
			for(int i = 0; i < lobData.getRecordCount(); i++) {
				String lobId = lobData.getRecordString(i, "id");
				data.clear();
				data.put("lobId", lobId);
				result = bus.delSysLob(data);
//				if (result.getInt("state") == 1) {
					data.clear();
					data.put("id", lobId);
					result = dataSetDao.deleteSimpleData("sys_lob", data);
					if (result.getInt("state") != 1)
						throw new Exception("删除附件明细lob记录失败" + (result.containsKey("message") ? "，错误信息：" + result.getString("message") : "") + "。");
//				} else
//					throw new Exception("删除附件明细文件失败" + (result.containsKey("message") ? "，错误信息：" + result.getString("message") : "") + "。");
			}
		} else {
			result = new ParaMap();
			result.put("state", 1);
			result.put("message", "未查询到附件明细lob数据");
		}
		return result;
	}
	
	/**
	 * 查询指定附件所有者所有附件来源模板记录
	 * @param refId 所有者id
	 * @param refTableName 所有者来源表
	 * @param category 附件列表分类码
	 * @return
	 * @throws Exception
	 */
	public ParaMap getAttachAllTemplets(String refId, String refTableName, String category) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "queryAttachAllTemplets");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("category", category);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	/**
	 * 检查必须的附件是否有上传、格式、数量等要求是否正确
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id
	 * @param category 同一附件所有者挂载多个附件清单时，通过此分类属性区别
	 * @return 检查成功并且所有必须附件都已上传则返回state=1
	 * @throws Exception
	 */
	public ParaMap requiredAttachCheck(String refId, String refTableName, String templetId, String category) throws Exception {
		ParaMap result = null;
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_attach_manager");
		sqlParams.put("dataSetNo", "checkRequiredAttachData");
		sqlParams.put("ref_id", refId);
		sqlParams.put("ref_table_name", refTableName);
		sqlParams.put("templet_id", templetId);
		sqlParams.put("category", category);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap checkData = dataSetDao.queryData(sqlParams);
		if (checkData.getInt("state") == 1) {
			if (checkData.getRecordCount() > 0) {
				int requiredCount = 0;
				int attachDtlCount = 0;
				int attachDtlFileCount = 0;
				List<String> errorAttachs = new ArrayList<String>();
				for(int i = 0; i < checkData.getRecordCount(); i++) {
					StringBuffer error = new StringBuffer();
					if (checkData.getRecordInt(i, "is_required") == 1) {
						requiredCount++;
						if (checkData.getRecordInt(i, "attach_dtl_count") <= 0 || checkData.getRecordInt(i, "attach_dtl_file_count") <= 0)
							error.append("必须上传附件,");
					}
					if (checkData.getRecordInt(i, "dtl_count") > 0 && checkData.getRecordInt(i, "dtl_count") != checkData.getRecordInt(i, "attach_dtl_count"))
						error.append("要求附件数为" + checkData.getRecordInt(i, "dtl_count") + "，实际上传附件数" + checkData.getRecordInt(i, "attach_dtl_count") + ",");
					if (checkData.getRecordInt(i, "dtl_file_count") > 0 && checkData.getRecordInt(i, "dtl_file_count") != checkData.getRecordInt(i, "attach_dtl_file_count"))
						error.append("要求附件文件数为" + checkData.getRecordInt(i, "dtl_file_count") + "，实际上传附件文件数" + checkData.getRecordInt(i, "attach_dtl_file_count") + ",");
					String fileType = checkData.getRecordString(i, "file_type");
					if (StrUtils.isNotNull(fileType)) {
						List<String> limitFileExtList = StrUtils.getSubStrs(fileType, ";");
						if (limitFileExtList != null && limitFileExtList.size() > 0) {
							String fileExts = checkData.getRecordString(i, "attach_dtl_file_exts");
							List<String> fileExtList = StrUtils.getSubStrs(fileExts, ";");
							if (fileExtList != null && fileExtList.size() > 0) {
								StringBuffer errorFileExt = new StringBuffer();
								for(int j = 0; j < fileExtList.size(); j++) {
									String fileExt = fileExtList.get(j);
									if (limitFileExtList.indexOf(fileExt) == -1)
										errorFileExt.append(fileExt + ",");
								}
								if (errorFileExt.length() > 0) 
									error.append("仅允许上传" + fileType + "等格式文件，实际上传了" + fileExts + "等格式文件,");
							}
						}
					}
					if (error.length() > 0) {
						error.deleteCharAt(error.length() - 1);
						errorAttachs.add(checkData.getRecordString(i, "name") + error.toString() + "。");
					}
					attachDtlCount += checkData.getRecordInt(i, "attach_dtl_count");
					attachDtlFileCount += checkData.getRecordInt(i, "attach_dtl_file_count");
				}
				result = new ParaMap();
				result.put("state", errorAttachs.size() > 0 ? 0 : 1);
				result.put("attachCount", checkData.getRecordCount());
				result.put("attachDtlCount", attachDtlCount);
				result.put("attachDtlFileCount", attachDtlFileCount);
				result.put("requiredCount", requiredCount);
				if (errorAttachs.size() > 0)
					result.put("errorAttachs", errorAttachs);
			} else {
				result = new ParaMap();
				result.put("state", 1);
				result.put("message", "没有附件记录");
			}
			return result;
		} else
			return checkData;
	}
	
	/**
	 * 检查必须的附件是否有上传、格式、数量等要求是否正确
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @param templetId 附件模板Id，即sys_attach_templet.id
	 * @return 检查成功并且所有必须附件都已上传则返回state=1
	 * @throws Exception
	 */
	public ParaMap requiredAttachCheck(String refId, String refTableName, String templetId) throws Exception {
		return requiredAttachCheck(refId, refTableName, templetId, null);
	}
	
	/**
	 * 检查必须的附件是否有上传、格式、数量等要求是否正确
	 * @param refId 附件所有者id，即sys_attach.ref_id
	 * @param refTableName 附件所有者表名，即sys_attach.ref_table_name
	 * @return 检查成功并且所有必须附件都已上传则返回state=1
	 * @throws Exception
	 */
	public ParaMap requiredAttachCheck(String refId, String refTableName) throws Exception {
		return requiredAttachCheck(refId, refTableName, null);
	}
}

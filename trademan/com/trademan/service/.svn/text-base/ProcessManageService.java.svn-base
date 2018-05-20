package com.trademan.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.task.TaskManager;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsLandDao;
import com.sysman.dao.FieldChangeDao;
import com.trade.utils.Constants;
import com.tradeland.engine.EngineDao;
import com.tradeland.engine.LandEngine;
import com.trademan.dao.ProcessManageDao;

public class ProcessManageService extends BaseService {

	Logger log = Logger.getLogger(this.getClass());

	/**
	 * 引擎信息
	 * 
	 * @param inMap
	 * @return
	 */
	public ParaMap processMemInfo(ParaMap inMap) {
		String processId = inMap.getString("processId");
		String info = TaskManager.memInfo(processId);
		ParaMap outMap = new ParaMap();
		outMap.put("info", info);
		return outMap;
	}

	/**
	 * 活动的流程
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap processList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		String no = inMap.getString("no");
		String business_type = inMap.getString("business_type");
		String goods_type = inMap.getString("goods_type");
		String status = inMap.getString("status");

		ParaMap sqlParams = inMap;
		sqlParams.put("organId", organId);
		String sql = "";
		if (StrUtils.isNotNull(no) && !"-1".equals(no)) {
			sql = sql + " and t.no like '%" + no + "%' ";
		}
		if (StrUtils.isNotNull(business_type) && !"-1".equals(business_type)) {
			sql = sql + " and t.business_type in (" + business_type + ") ";
		} else {
			if (StrUtils.isNotNull(goods_type)) {
				if (goods_type.indexOf(",") != -1) {
					sql = sql
							+ " and  substr(t.business_type,1,3) in ('301','401')";
				} else {
					sql = sql + " and substr(t.business_type,1,3) in ('"
							+ goods_type + "') ";
				}
			}
		}
		if (StrUtils.isNotNull(status) && !"-1".equals(status)) {
			sql = sql + " and wp.state = :status ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);

		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNull(sortField) || StrUtils.isNull(sortDir)
				|| "undefined".equals(sortField) || "undefined".equals(sortDir)) {
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "id desc");
		} else {
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField + " " + sortDir);
		}
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));

		ProcessManageDao processDao = new ProcessManageDao();
		ParaMap out = processDao.processList(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("processid", "processid");
		fields.put("templateid", "templateid");
		fields.put("tasknodeid", "tasknodeid");
		fields.put("state", "state");
		fields.put("mome", "mome");
		fields.put("targetname", "targetname");
		fields.put("phase", "phase");
		fields.put("task", "task");
		fields.put("begin_notice_time", "begin_notice_time");
		fields.put("business_name", "business_name");
		fields.put("organ_name", "organ_name");
		fields.put("trans_organ_name", "trans_organ_name");
		fields.put("target_earnest_money", "target_earnest_money");
		fields.put("notice_name", "notice_name");
		fields.put("unit", "unit");
		fields.put("business_type", "business_type");
		fields.put("earnest_money", "earnest_money");
		LandEngine tradeEngine = new LandEngine();
		List items = new ArrayList();
		for (int i = 0; i < out.getRecords().size(); i++) {
			ParaMap item = new ParaMap();
			Iterator it = fields.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = fields.getString(key);
				if (!value.equals("task")) {
					value = String.valueOf(out.getRecordValue(i, key));
				} else {
					String processId = String.valueOf(out.getRecordValue(i,
							"id"));
					value = tradeEngine.getIsHaveTimer(processId);
				}
				item.put(key, value);
			}
			items.add(item);
		}

		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 流程停止
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap stopProcess(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("id");
		String reason = inMap.getString("reason");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		ParaMap target = dataSetDao.querySimpleData("trans_target", keyData);
		if (target != null && target.getSize() > 0) {
			String businessType = target.getRecordString(0, "business_type");
			if (businessType.startsWith("101")) {
				out = com.trade.utils.Engine.getLandEngine().commit(
						com.trade.utils.Constants.Action_Stop, inMap);
			} else if (businessType.startsWith("301")
					|| businessType.startsWith("401")) {
				out = com.trade.utils.Engine.getMineEngine().commit(
						com.trade.utils.Constants.Action_Stop, inMap);
			} else if (businessType.startsWith("501")) {
				out = com.trade.utils.Engine.getPlowEngine().commit(
						com.trade.utils.Constants.Action_Stop, inMap);
			} else {
				out.put("state", 0);
				out.put("message", "操作失败！请选择有效的标的。");
			}
			if ("1".equals(out.getString("state"))) {
				FieldChangeDao fieldChangeDao = new FieldChangeDao();
				ParaMap change = new ParaMap();
				change.put("ref_table_name", "trans_target");
				change.put("ref_id", targetId);
				change.put("field_name", "is_suspend");
				change.put("old_value", 0);
				change.put("new_value", 1);
				change.put("change_cause", "中止:" + reason);
				fieldChangeDao.updateFieldChange(change);
			}
		} else {
			out.put("state", 0);
			out.put("message", "操作失败！请选择有效的标的。");
		}
		return out;
	}

	/**
	 * 流程重启
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap restartProcess(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("id");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		ParaMap target = dataSetDao.querySimpleData("trans_target", keyData);
		if (target != null && target.getSize() > 0) {
			String businessType = target.getRecordString(0, "business_type");
			if (businessType.startsWith("101")) {
				out = com.trade.utils.Engine.getLandEngine().commit(
						com.trade.utils.Constants.Action_Restart, inMap);
				;
			} else if (businessType.startsWith("301")
					|| businessType.startsWith("401")) {
				out = com.trade.utils.Engine.getMineEngine().commit(
						com.trade.utils.Constants.Action_Restart, inMap);
			} else if (businessType.startsWith("501")) {
				out = com.trade.utils.Engine.getPlowEngine().commit(
						com.trade.utils.Constants.Action_Restart, inMap);
				;
			} else {
				out.put("state", 0);
				out.put("message", "操作失败！请选择有效的标的。");
			}
			if ("1".equals(out.getString("state"))) {
				FieldChangeDao fieldChangeDao = new FieldChangeDao();
				ParaMap change = new ParaMap();
				change.put("ref_table_name", "trans_target");
				change.put("ref_id", targetId);
				change.put("field_name", "is_suspend");
				change.put("old_value", 1);
				change.put("new_value", 0);
				change.put("change_cause", "重启");
				fieldChangeDao.updateFieldChange(change);
			}
		} else {
			out.put("state", 0);
			out.put("message", "操作失败！请选择有效的标的。");
		}
		return out;
	}

	/**
	 * 流程终止
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap cancelProcess(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("id");
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessIdByTargetId(targetId);
		ProcessManageDao dao = new ProcessManageDao();
		ParaMap out = dao.cancelProcess(processId);
		if ("1".equals(out.getString("state"))) {
			LandEngine tradeEngine = new LandEngine();
			tradeEngine.removeTask(processId);
			// 记录历史
			FieldChangeDao fieldChangeDao = new FieldChangeDao();
			ParaMap change = new ParaMap();
			change.put("ref_table_name", "trans_target");
			change.put("ref_id", targetId);
			change.put("field_name", "is_stop");
			change.put("old_value", 0);
			change.put("new_value", 1);
			change.put("change_cause", "终止");
			fieldChangeDao.updateFieldChange(change);

		}
		return out;
	}

	/**
	 * 得到流程信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getProcessParameter(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("id");
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessIdByTargetId(targetId);

		ParaMap processMap = engineDao.getProcessInfo(processId);
		String state = String.valueOf(processMap.getRecordValue(0, "state"));
		out.put("state", state);

		ParaMap tasknodeMap = engineDao.curTaskNodeInfo(processId);
		out.put("beginTime", tasknodeMap.getString(Constants.PbeginTime));
		out.put("endTime", tasknodeMap.getString(Constants.PendTime));
		out.put("stopTime", tasknodeMap.getString(Constants.pstopTime));
		out.put("ptype", tasknodeMap.getString("ptype"));
		out.put("qtype", tasknodeMap.getString("qtype"));
		out.put("tasknodeId", tasknodeMap.getString("tasknodeid"));
		String ptype = tasknodeMap.getString("ptype");
		if (StrUtils.isNotNull(ptype) && ptype.equals(Constants.LLimit)) {
			out.put("isLimit", 1);
		}

		String nodeId = tasknodeMap.getString("nodeid");
		ParaMap nodeMap = engineDao.getNodeInfo(nodeId);
		String qtype = nodeMap.getString("qtype");
		String phase = StrUtils.isNotNull(qtype) ? "限时竞价("
				+ nodeMap.getString("name") + ")" : nodeMap.getString("name");
		out.put("phase", phase);
		out.put("nodeId", nodeId);

		TransGoodsLandDao goodsDao = new TransGoodsLandDao();
		ParaMap targetMap = goodsDao.getTargetInfo(targetId);
		List targetList = this.changeToObjList(targetMap, null);
		ParaMap target = new ParaMap();
		if (targetList != null && targetList.size() > 0) {
			target = (ParaMap) targetList.get(0);
		}
		out.put("target", target);

		ProcessManageDao manageDao = new ProcessManageDao();
		List phaseList = manageDao.getPhaseTimesList(targetId);
		out.put("phaseList", phaseList);

		return out;
	}

	public ParaMap historyList(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("id");

		ParaMap sqlParams = inMap;
		String sql = " and ref_table_name = 'trans_target' and ref_id = '"
				+ targetId
				+ "' and (field_name = 'is_suspend' or field_name = 'is_stop' ) order by create_date asc";
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);

		FieldChangeDao fieldChangeDao = new FieldChangeDao();
		ParaMap result = fieldChangeDao.getFieldChangePageListData(sqlParams);
		ParaMap out = new ParaMap();
		out.put("Total", result.getInt("totalRowCount"));
		out.put("Rows", result.getListObj());
		return out;
	}

	/**
	 * 得到单位id
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("id")) {
						organId = (String) record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}

	public List changeToObjList(ParaMap in, String firstId) throws Exception {
		List resultList = new ArrayList();
		if (StrUtils.isNotNull(firstId)) {
			ParaMap fMap = new ParaMap();
			fMap.put("id", firstId);
			resultList.add(fMap);
		}
		if (in.getSize() > 0) {
			List filds = in.getFields();
			for (int i = 0; i < in.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) in.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				if (StrUtils.isNotNull(firstId) && StrUtils.isNotNull(id)
						&& id.equals(firstId)) {
					resultList.set(0, out);
				} else {
					resultList.add(out);
				}
			}
		}
		return resultList;
	}

}

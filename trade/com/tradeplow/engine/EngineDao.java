package com.tradeplow.engine;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.base.utils.DateUtils;
import com.base.utils.FlagUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.tradeplow.dao.TradeDao;
import com.tradeplow.service.TradeService;

public class EngineDao extends BaseDao {
	Logger log = Logger.getLogger(this.getClass());
	private static ParaMap ruleMap = null;

	public synchronized ParaMap getRuleMap() {
		if (ruleMap == null) {
			ruleMap = new ParaMap();
			try {
				SAXReader saxReader = new SAXReader();
				InputStream in = this.getClass().getResourceAsStream(
						"TradeRule.xml");
				Document doc = saxReader.read(in);
				List list = doc.getRootElement().elements();
				for (int i = 0; i < list.size(); i++) {
					Element ele = (Element) list.get(i);
					String id = ele.attributeValue("id");
					ParaMap attMap = new ParaMap();
					for (int j = 0; j < ele.attributeCount(); j++) {
						String attName = ele.attribute(j).getName();
						if (!attName.equals("id")) {
							String attValue = ele.attributeValue(attName);
							attMap.put(attName, attValue);
						}
					}
					ruleMap.put(id, attMap);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ruleMap;
	}

	/**
	 * 获取属性
	 * 
	 * @param refId
	 * @return
	 */
	public ParaMap getParaMap(String refId) throws Exception {
		String sql = "select id, refid , name ,value , memo from wf_parameter where refid = :refid";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		sqlParams.put("refid", refId);
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * 更新属性
	 * 
	 * @param refId
	 * @param map
	 */
	public void updateParaMap(String refId, String name, String value)
			throws Exception {
		String sql = "delete from wf_parameter where refid = :refid and name = :name";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		sqlParams.put("refid", refId);
		sqlParams.put("name", name);
		dataSetDao.executeSQL(sqlParams);

		ParaMap insertMap = new ParaMap();
		insertMap.put("id", null);
		insertMap.put("refid", refId);
		insertMap.put("name", name);
		insertMap.put("value", value);
		dataSetDao.updateData("wf_parameter", "id", insertMap, null);

	}

	public void updateParaMap(String refId, ParaMap in) throws Exception {
		String keyString = "";
		List list = new ArrayList();
		Iterator it = in.keySet().iterator();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = in.getString(key);
			keyString = keyString == null || "".equals(keyString) ? "'" + key
					+ "'" : keyString + ",'" + key + "'";
		}

		String sql = "delete from wf_parameter where refid = ? and name in (?)";
		PreparedStatement pstm = getCon().prepareStatement(sql);
		pstm.setString(1, refId);
		pstm.setString(2, keyString);
		pstm.executeUpdate();

		Iterator it2 = in.keySet().iterator();
		sql = "insert into wf_parameter(id,refid,name,value)values(?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		while (it2.hasNext()) {
			String key = String.valueOf(it2.next());
			String value = in.getString(key);
			pstm.setString(1, IDGenerator.newGUID());
			pstm.setString(2, refId);
			pstm.setString(3, key);
			pstm.setString(4, value);
			pstm.executeUpdate();
		}
		pstm.close();

	}

	/**
	 * 得到执行人
	 * 
	 * @param taskNodeId
	 *            任务实例ID
	 * @param rightType
	 *            权限类型
	 * @return
	 */
	public ParaMap getTaskUsers(String taskNodeId, int rightType)
			throws Exception {

		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select id,tasknodeid,type,right_type,value, memo  from wf_tasknodeuser where tasknodeid = ? and type = ?";
		String subSql = rightType != 0 ? " and right_type = " + rightType : "";
		pstm = getCon().prepareStatement(sql + subSql);
		pstm.setString(1, taskNodeId);
		pstm.setString(2, String.valueOf(Constants.DataTypePerson));
		rs = pstm.executeQuery();
		return convert(rs);
	}

	/**
	 * 修改执行人
	 * 
	 * @param taskNodeId
	 *            任务实例ID
	 * @param rightType
	 *            权限类型
	 * @param value
	 *            sql或者id1,id2,...
	 * @throws Exception
	 */
	public void updateTaskUsers(String taskNodeId, int rightType, int dataType,
			String value) throws Exception {
		String sql = "delete from wf_nodeuser where nodeid = :taskNodeId and righttype = : rightType and datatype = :dataType";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		sqlParams.put("taskNodeId", taskNodeId);
		sqlParams.put("rightType", rightType);
		sqlParams.put("dataType", dataType);
		dataSetDao.executeSQL(sqlParams);
		if (value != null && !value.equals("")) {
			if (rightType == Constants.DataTypeSql) {
				ParaMap pMap = new ParaMap();
				pMap.put("id", null);
				pMap.put("taskNodeId", taskNodeId);
				pMap.put("rightType", rightType);
				pMap.put("dataType", dataType);
				pMap.put("value", value);
				dataSetDao.updateData("wf_nodeuser", "id", pMap, null);
			} else {
				String[] values = value.split(",");
				if (values != null && values.length > 0) {
					for (int i = 0; i < values.length; i++) {
						if (values[i] != null && !values[i].equals("")) {
							ParaMap pMap = new ParaMap();
							pMap.put("id", null);
							pMap.put("nodeid", taskNodeId);
							pMap.put("righttype", rightType);
							pMap.put("datatype", dataType);
							pMap.put("value", values[i]);
							dataSetDao.updateData("wf_nodeuser", "id", pMap,
									null);
						}
					}
				}
			}
		}

	}

	public String genTaskNode(String processId, String nodeId) throws Exception {
		String sql = "insert into wf_tasknode(id,processid,nodeid,time) values(?,?,?,?)";
		PreparedStatement pstm = getCon().prepareStatement(sql);
		String taskNodeId = IDGenerator.newGUID(true);
		pstm.setString(1, taskNodeId);
		pstm.setString(2, processId);
		pstm.setString(3, nodeId);
		pstm.setString(4, DateUtils.getStr(DateUtils.now()));
		pstm.executeUpdate();
		return taskNodeId;
	}

	/**
	 * 得到流程id,如果没有流程就创建一个流程
	 * 
	 * @param templateId
	 *            模板Id 交易流程一般为标的Id+‘_’+实例数
	 * @return
	 */
	public String getProcessId(String templateId) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select id , instancecount , memo from wf_template where id  = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, templateId);
		rs = pstm.executeQuery();
		String instancecount = "";
		if (rs.next()) {
			instancecount = rs.getString("instancecount");
		} else {
			rs.close();
			pstm.close();
			return null;
		}
		// 得到实例号，如果为null，实例号为1
		instancecount = instancecount == null || instancecount.equals("") ? "1"
				: String.valueOf(Integer.parseInt(instancecount));

		// 查询看有无该实例
		sql = "select id from wf_process where id = ? and templateid = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, templateId + "_" + instancecount);
		pstm.setString(2, templateId);
		rs = pstm.executeQuery();
		String processId = "";
		if (rs.next()) {
			processId = rs.getString("id");
		} else {

			// 修改模板实例号
			sql = "update wf_template set instancecount = ? where id = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, instancecount);
			pstm.setString(2, templateId);
			pstm.executeUpdate();

			// 得到开始节点信息
			sql = "select id , nextid from wf_node where ptype='N01' and templateid = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, templateId);
			rs = pstm.executeQuery();
			String nodeId = "";
			String nextId = "";
			if (rs.next()) {
				nodeId = rs.getString("id");
				nextId = rs.getString("nextid");
			}

			processId = templateId + "_" + instancecount;
			String taskNodeId = IDGenerator.newGUID();

			// 先创建任务节点
			sql = "insert into wf_tasknode(id,processid,nodeid,time) values(?,?,?,?)";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, taskNodeId);
			pstm.setString(2, processId);
			pstm.setString(3, nodeId);
			pstm.setString(4, DateUtils.getStr(DateUtils.now()));
			pstm.executeUpdate();
			// 建立上下文
			this.updateParaMap(taskNodeId, "nextId", nextId);
			// 插入
			sql = "insert into wf_process(id,templateid,tasknodeid,state) values(?,?,?,?)";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, processId);
			pstm.setString(2, templateId);
			pstm.setString(3, taskNodeId);
			pstm.setString(4, String.valueOf(Constants.ProcessRunning));
			pstm.executeUpdate();
		}

		return processId;
	}

	/**
	 * 得到流程id,如果没有流程就创建一个流程
	 * 
	 * @param templateId
	 *            模板Id 交易流程一般为标的Id+‘_’+实例数
	 * @return
	 */
	public String getProcessIdByTargetId(String targetId) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select id , instancecount , memo from wf_template where id  = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, targetId);
		rs = pstm.executeQuery();
		String instancecount = "";
		if (rs.next()) {
			instancecount = rs.getString("instancecount");
		} else {
			rs.close();
			pstm.close();
			return null;
		}
		// 得到实例号，如果为null，实例号为1
		instancecount = instancecount == null || instancecount.equals("") ? "1"
				: String.valueOf(Integer.parseInt(instancecount));

		// 查询看有无该实例
		sql = "select id from wf_process where id = ? and templateid = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, targetId + "_" + instancecount);
		pstm.setString(2, targetId);
		rs = pstm.executeQuery();
		String processId = "";
		if (rs.next()) {
			processId = rs.getString("id");
		} else {
			processId = null;
		}

		return processId;
	}

	public String getTemplateId(String processId) throws Exception {
		String result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlMap = new ParaMap();
		sqlMap.put("id", processId);
		ParaMap dataMap = dataSetDao.querySimpleData("wf_process", sqlMap);
		if (dataMap.getSize() > 0) {
			result = (String) dataMap.getRecordValue(0, "templateid");
		}
		return result;
	}

	/**
	 * 
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getNodeInfo(String nodeId) throws Exception {
		ParaMap out = new ParaMap();
		// 得到Node所有信息
		ResultSet rs = null;
		PreparedStatement pstm = null;
		String sql = "select id,templateid,name,ptype,qtype,nextid,memo from wf_node where id = :nodeId";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, nodeId);
		String templateId = null;
		String name = null;
		String ptype = null;
		String qtype = null;
		String nextId = null;
		rs = pstm.executeQuery();
		// out = convertRsOneRow(rs);
		if (rs.next()) {
			templateId = rs.getString("templateid");
			name = rs.getString("name");
			ptype = rs.getString("ptype");
			qtype = rs.getString("qtype");
			nextId = rs.getString("nextid");
		}
		// 得到node wf_parameter所有信息
		sql = "select name ,value from wf_parameter where refid = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, nodeId);
		rs = pstm.executeQuery();
		while (rs.next()) {
			String key = rs.getString("name");
			String value = rs.getString("value");
			out.put(key, value);
		}
		out.put("id", nodeId);
		out.put("templateid", templateId);
		out.put("name", name);
		out.put("ptype", ptype);
		out.put("qtype", qtype);
		out.put("nextid", nextId);

		// 得到rulemap相关信息
		String ruleMapId = DateUtils.isNull(qtype) ? ptype : qtype;
		ParaMap rule = (ParaMap) getRuleMap().get(ruleMapId);
		ParaMap result = new ParaMap();
		if (rule != null) {
			result.putAll(rule);
		}
		result.putAll(out);
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

	/**
	 * 
	 * @param processId
	 * @return
	 * @throws Exception
	 */

	public ParaMap curTaskNodeInfo(String processId) throws Exception {
		ParaMap out = new ParaMap();
		// 得到taskNode所有信息
		ResultSet rs = null;
		PreparedStatement pstm = null;
		String sql = "select wt.id , wt.processid , wt.nodeid , wt.time , wt.userid, wt.memo ,wp.state as wpstate from wf_tasknode wt , wf_process wp where wp.tasknodeid = wt.id and wt.processid = wp.id and wt.processid= ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, processId);
		rs = pstm.executeQuery();
		// out = convertRsOneRow(rs);
		String taskNodeId = null;
		String nodeId = null;
		String time = null;
		String userid = null;
		String wpstate = null;
		if (rs.next()) {
			taskNodeId = rs.getString("id");
			nodeId = rs.getString("nodeid");
			time = rs.getString("time");
			userid = rs.getString("userid");
			wpstate = rs.getString("wpstate");
		}

		sql = "select name ,value from wf_parameter where refid = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, taskNodeId);
		rs = pstm.executeQuery();
		while (rs.next()) {
			String name = rs.getString("name");
			String value = rs.getString("value");
			out.put(name, value);
		}
		out.put("tasknodeid", taskNodeId);
		out.put("processid", processId);
		out.put("nodeid", nodeId);
		out.put("time", time);
		out.put("userid", userid);
		out.put("wpstate", wpstate);
		return out;

	}

	public ParaMap getProcessInfo(String processId) throws Exception {
		String sql = "   select id, templateid , tasknodeId , state , memo from wf_process where id = :processId";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		sqlParams.put("processId", processId);
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNotEndProcess() throws Exception {
		String sql = "   select id, templateid , tasknodeId , state , memo from wf_process where state  in  ("
				+ Constants.ProcessRunning + "," + Constants.ProcessWait + ")";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNotEndProcess(String goods_types) throws Exception {
		String sql = "   select wp.id, wp.templateid , wp.tasknodeId , wp.state , wp.memo "
				+ "   from wf_process wp , trans_target tt  "
				+ "   where wp.templateid = tt.id and substr(tt.business_type,1,3) in ("
				+ goods_types
				+ ") "
				+ "   and  wp.state in  ("
				+ Constants.ProcessRunning + "," + Constants.ProcessWait + ")";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * 根据流程状态获取流程信息
	 * 
	 * @param processState
	 * @return
	 */
	public ParaMap getProcesses(int processState) throws Exception {
		String sql = " select id, templateid , curnodeid , state , memo from wf_process where state = :processState ";
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("sql", sql);
		sqlParams.put("processState", processState);
		return dataSetDao.queryData(sqlParams);
	}

	public void createTemplateInfo(String targetId) throws Exception {
		DecimalFormat myformat = new DecimalFormat("############.##");
		// 得到标的信息
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		ParaMap pMap = dataSetDao.querySimpleData("trans_target", keyData);
		String targetName = String.valueOf(pMap.getRecordValue(0, "name"));
		String targetNo = String.valueOf(pMap.getRecordValue(0, "no"));
		targetName = DateUtils.isNull(targetName) ? targetNo : targetName;

		ParaMap target = new ParaMap();
		if (pMap.getSize() > 0) {
			List filds = pMap.getFields();
			List record = (List) pMap.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					target.put((String) filds.get(i), record.get(i));
				}
			}
		}

		// 得到多指标信息
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "query_multi_trade_bytargetid");
		sqlParams.put("targetId", targetId);
		ParaMap multi = dataSetDao.queryData(sqlParams);
		int multiSize = multi.getSize();

		// list 保存节点信息
		List list = new ArrayList();

		ParaMap startMap = new ParaMap();
		startMap.put("id", targetId + Constants.NodeStartChar);
		startMap.put("name", "开始节点");
		startMap.put("ptype", Constants.NodeStart);
		startMap.put("nextid", targetId + Constants.LNotice);
		list.add(startMap);

		ParaMap noticeMap = new ParaMap();
		noticeMap.put("id", targetId + Constants.LNotice);
		noticeMap.put("name", "公告期");
		noticeMap.put("ptype", Constants.LNotice);
		noticeMap.put("beginTime", target.getString("begin_notice_time"));
		noticeMap
				.put("initValue",
						myformat.format(target.getBigDecimal("begin_price"))
								.toString());
		noticeMap
				.put("finalValue",
						target.getBigDecimal("final_price") == null ? ""
								: myformat.format(
										target.getBigDecimal("final_price"))
										.toString());
		noticeMap.put("step",
				myformat.format(target.getBigDecimal("price_step")).toString());
		noticeMap.put("unit", target.getString("unit"));

		if (target.getInt("trans_type") == 0) { // 挂牌
			noticeMap.put("nextid", targetId + Constants.LFocus);
			noticeMap.put("endTime", target.getString("begin_focus_time"));
			list.add(noticeMap);

			ParaMap focusMap = new ParaMap();
			focusMap.put("id", targetId + Constants.LFocus);
			focusMap.put("name", "集中报价期");
			focusMap.put("ptype", Constants.LFocus);
			focusMap.put("beginTime", target.getString("begin_focus_time"));
			focusMap.put("endTime", target.getString("end_focus_time"));
			focusMap.put("initValue",
					myformat.format(target.getBigDecimal("begin_price"))
							.toString());
			focusMap.put("finalValue",
					target.getBigDecimal("final_price") == null ? "" : myformat
							.format(target.getBigDecimal("final_price"))
							.toString());
			focusMap.put("step",
					myformat.format(target.getBigDecimal("price_step"))
							.toString());
			focusMap.put("unit", target.getString("unit"));

			if (multi.getSize() > 0) {
				String qtype = (String) multi.getRecordValue(0, "type");
				String ptype = (String) multi.getRecordValue(0, "ptype");
				focusMap.put("nextid", targetId + ptype + qtype);
				focusMap.put("endTime", target.getString("begin_limit_time"));
				list.add(focusMap);
				List mList = this.createNodeList(multi, targetId,
						target.getString("begin_limit_time"));
				if (mList != null && mList.size() > 0) {
					for (int i = 0; i < mList.size(); i++) {
						ParaMap p = (ParaMap) mList.get(i);
						list.add(p);
					}
				}
			} else {
				noticeMap.put("nextid", targetId + Constants.NodeEndChar);
				noticeMap.put("endTime", target.getString("begin_limit_time"));
				list.add(noticeMap);
			}

		} else if (target.getInt("trans_type") == 1) {// 拍卖

			if (multi.getSize() > 0) {
				String qtype = (String) multi.getRecordValue(0, "type");
				String ptype = (String) multi.getRecordValue(0, "ptype");
				noticeMap.put("nextid", targetId + ptype + qtype);
				// noticeMap.put("endTime",
				// target.getString("begin_limit_time"));
				String end_notice_time = target.getString("begin_limit_time");
				String begin_limit_time = target.getString("begin_limit_time");
				if (StrUtils.isNotNull(end_notice_time)
						&& StrUtils.isNotNull(begin_limit_time)) {
					if (DateUtils.getDate(end_notice_time).getTime() > DateUtils
							.getDate(begin_limit_time).getTime()) {
						noticeMap.put("endTime",
								target.getString("begin_limit_time"));
					} else {
						noticeMap.put("endTime",
								target.getString("end_notice_time"));
					}
				} else {
					noticeMap.put("endTime",
							target.getString("begin_limit_time"));
				}
				list.add(noticeMap);
				List mList = this.createNodeList(multi, targetId,
						target.getString("begin_limit_time"));
				if (mList != null && mList.size() > 0) {
					for (int i = 0; i < mList.size(); i++) {
						ParaMap p = (ParaMap) mList.get(i);
						list.add(p);
					}
				}
			} else {
				noticeMap.put("nextid", targetId + Constants.NodeEndChar);
				noticeMap.put("endTime", target.getString("begin_limit_time"));
				list.add(noticeMap);
			}

		}

		ParaMap endMap = new ParaMap();
		endMap.put("id", targetId + Constants.NodeEndChar);
		endMap.put("name", "结束节点");
		endMap.put("ptype", Constants.NodeEnd);
		list.add(endMap);

		createWfTemplate(targetId);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				ParaMap node = (ParaMap) list.get(i);
				String id = node.getString("id");
				String name = node.getString("name");
				String nextId = node.getString("nextid");
				String ptype = node.getString("ptype");
				String qtype = node.getString("qtype");
				this.createWfNode(id, targetId, name, ptype, qtype, nextId, "");
				node.remove("id");
				node.remove("name");
				node.remove("nextid");
				node.remove("ptype");
				node.remove("qtype");
				Iterator it = node.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (!key.equals("id") && !key.equals("name")
							&& !key.equals("ptype") && !key.equals("qtype")
							&& !key.equals("nextid")) {
						String value = (String) node.get(key);
						this.createWfParameter(id, key, value, null);
					}
				}
				this.createWfParameter(id, "targetName", targetName, null);
			}
		}

		// startTemplate(targetId);
		ParaMap inMap = new ParaMap();
		inMap.put("templateId", targetId);
		ParaMap out = Engine.getPlowEngine().commit(
				Constants.Action_StartTemplate, inMap);

	}

	public List createNodeList(ParaMap multiMap, String targetId,
			String begin_limit_time) {
		DecimalFormat myformat = new DecimalFormat("############.##");
		List list;
		int size = multiMap.getSize();
		if (size > 0) {
			list = new ArrayList();
			for (int i = 0; i < size; i++) {
				String ptype = (String) multiMap.getRecordValue(i, "ptype");
				String qtype = (String) multiMap.getRecordValue(i, "type");
				ParaMap multi = new ParaMap();
				multi.put(
						"id",
						targetId + ptype
								+ (String) multiMap.getRecordValue(i, "type"));
				multi.put("name", (String) multiMap.getRecordValue(i, "name"));
				multi.put("ptype", ptype);
				multi.put("qtype", qtype);
				if (i == 0) {
					multi.put("beginTime", begin_limit_time);
				}
				multi.put(
						"initValue",
						myformat.format(
								multiMap.getRecordValue(i, "init_value"))
								.toString());
				multi.put(
						"finalValue",
						multiMap.getRecordValue(i, "final_value") == null ? ""
								: myformat.format(
										multiMap.getRecordValue(i,
												"final_value")).toString());
				multi.put("step",
						myformat.format(multiMap.getRecordValue(i, "step"))
								.toString());
				multi.put("unit", (String) multiMap.getRecordValue(i, "unit"));
				multi.put("flag",
						(String) multiMap.getRecordValue(i, "enter_flag"));
				multi.put("firstWait", String.valueOf(multiMap.getRecordValue(
						i, "first_wait")));
				multi.put("limitWait", String.valueOf(multiMap.getRecordValue(
						i, "limit_wait")));
				multi.put("lastWait",
						String.valueOf(multiMap.getRecordValue(i, "last_wait")));
				multi.put("multiId", multiMap.getRecordValue(i, "id"));
				if (i < size - 1) {
					String nextPtype = (String) multiMap.getRecordValue(i + 1,
							"ptype");
					multi.put("nextid", targetId + nextPtype
							+ (String) multiMap.getRecordValue(i + 1, "type"));
				} else {// 最后一条
					multi.put("nextid", targetId + Constants.NodeEndChar);
				}
				list.add(multi);
			}
		} else {
			list = null;
		}
		return list;
	}

	/**
	 * 修改标的信息 no ， name
	 * 
	 * @param targetId
	 * @param targetNo
	 * @param targetName
	 * @throws Exception
	 */
	public void updateTargetNoName(String targetId, String targetNo,
			String targetName) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap targetMap = new ParaMap();
		targetMap.put("id", targetId);
		targetMap.put("no", targetNo);
		targetMap.put("name", targetName);
		ParaMap result = dataSetDao.updateData("trans_target", "id", targetMap,
				null);
	}

	/**
	 * 新创建一个wf_template
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void createWfTemplate(String id) throws Exception {

		ResultSet rs = null;
		PreparedStatement pstm = null;
		String sql = "select * from wf_template where id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, id);
		rs = pstm.executeQuery();
		if (rs.next()) {

			// 删除wf_tasknodeuser
			sql = "delete from wf_tasknodeuser where tasknodeid in (select wt.id from wf_tasknode wt , wf_process wp where wt.processid = wp.id and wp.templateid = ? )";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_parameter(wf_tasknode关联)
			sql = "delete from wf_parameter where refid in (select wt.id from wf_tasknode wt , wf_process wp where wt.processid = wp.id and wp.templateid = ? )";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_tasknode
			sql = "delete from wf_tasknode where processid in (select id from  wf_process wp where wp.templateid = ? )";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_process
			sql = "delete from wf_process where templateid = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_parameter
			sql = "delete from wf_parameter where refid in (select id from wf_node where templateid = ?)";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_node
			sql = "delete from wf_node where templateid = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

			// 删除wf_template
			sql = "delete from wf_template where id = ?";
			pstm = getCon().prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();

		}
		sql = "insert into wf_template(id) values(?)";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, id);
		pstm.executeUpdate();
		rs.close();
		pstm.close();
	}

	/**
	 * 新创建一个wf_node
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void createWfNode(String id, String templateId, String name,
			String ptype, String qtype, String nextId, String memo)
			throws Exception {

		PreparedStatement pstm = null;
		String sql = "insert into wf_node(id , templateid , name , ptype , qtype , nextid , memo) values(?,?,?,?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, id);
		pstm.setString(2, templateId);
		pstm.setString(3, name);
		pstm.setString(4, ptype);
		pstm.setString(5, qtype);
		pstm.setString(6, nextId);
		pstm.setString(7, memo);
		pstm.executeUpdate();
		pstm.close();
	}

	/**
	 * 新创建一个wf_Parameter
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void createWfParameter(String refId, String name, String value,
			String memo) throws Exception {
		ParaMap pMap = new ParaMap();
		pMap.put("id", IDGenerator.newGUID());
		pMap.put("refId", refId);
		pMap.put("name", name);
		pMap.put("value", value);
		pMap.put("memo", memo);
		DataSetDao dataSetDao = new DataSetDao();
		dataSetDao.updateData("wf_parameter", "id", pMap, null);
	}

	/**
	 * 修改流程的taskNodeId
	 * 
	 * @param processId
	 * @param state
	 * @throws Exception
	 */
	public void setProcessTaskNodeId(String processId, String taskNodeId)
			throws Exception {
		ParaMap pMap = new ParaMap();
		pMap.put("id", processId);
		pMap.put("tasknodeid", taskNodeId);
		DataSetDao dataSetDao = new DataSetDao();
		dataSetDao.updateData("wf_process", "", "id", pMap, null);
	}

	/**
	 * 改变流程状态
	 * 
	 * @param processId
	 * @param state
	 * @throws Exception
	 */
	public void setProcessState(String processId, int state) throws Exception {
		ParaMap pMap = new ParaMap();
		pMap.put("id", processId);
		pMap.put("state", state);
		DataSetDao dataSetDao = new DataSetDao();
		dataSetDao.updateData("wf_process", "", "id", pMap, null);
	}

	/**
	 * 根据templateId启动一个流程
	 * 
	 * @param templateId
	 * @throws Exception
	 */

	public String startTemplate(String templateId) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select id , instancecount , memo from wf_template where id  = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, templateId);
		rs = pstm.executeQuery();
		String instancecount = "";
		if (rs.next()) {
			instancecount = rs.getString("instancecount");
		} else {
			rs.close();
			pstm.close();
		}
		instancecount = DateUtils.isNull(instancecount) ? "1" : String
				.valueOf(Integer.parseInt(instancecount) + 1);

		// 修改模板实例号
		sql = "update wf_template set instancecount = ? where id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, instancecount);
		pstm.setString(2, templateId);
		pstm.executeUpdate();

		// 得到开始节点信息
		sql = "select id , nextid from wf_node where ptype=? and templateid = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, Constants.NodeStart);
		pstm.setString(2, templateId);
		rs = pstm.executeQuery();
		String nodeId = "";
		if (rs.next()) {
			nodeId = rs.getString("id");
		}

		// 插入流程信息
		String proccessId = templateId + "_" + instancecount;
		sql = "insert into wf_process(id,templateid,tasknodeid,state) values(?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, proccessId);
		pstm.setString(2, templateId);
		pstm.setString(3, "");
		pstm.setString(4, String.valueOf(Constants.ProcessRunning));
		pstm.executeUpdate();

		// 新增上下文信息
		ParaMap context = new ParaMap();
		context.put("processid", proccessId);
		genTaskNode(nodeId, context);

		return proccessId;
	}

	public ParaMap genTaskNode(String nodeId, ParaMap context) throws Exception {
		ParaMap newContext = new ParaMap();
		String priorTaskId = context.getString("tasknodeid");
		String priorNodeId = context.getString("nodeid");
		String priorPtype = context.getString("ptype");
		String priorQtype = context.getString("qtype");
		String from = (StrUtils.isNull(priorPtype) ? "" : priorPtype)
				+ (StrUtils.isNull(priorQtype) ? "" : priorQtype);
		newContext.putAll(this.getNodeInfo(nodeId));
		String processId = context.getString("processid");
		// 先创建任务节点
		String taskNodeId = IDGenerator.newGUID();
		String sql = "insert into wf_tasknode(id,processid,nodeid,time) values(?,?,?,?)";
		PreparedStatement pstm = getCon().prepareStatement(sql);
		pstm.setString(1, taskNodeId);
		pstm.setString(2, processId);
		pstm.setString(3, nodeId);
		pstm.setString(4, DateUtils.getStr(DateUtils.now()));
		pstm.executeUpdate();
		// 修改流程的taskNodeId
		this.setProcessTaskNodeId(processId, taskNodeId);
		// 置流程状态
		this.setProcessState(processId, Constants.ProcessWait);
		// 更新上下文
		this.updateParaMap(taskNodeId, newContext);
		String qtype = newContext.getString("qtype");
		if (qtype != null
				&& (qtype.equals(Constants.QPrice) || qtype.equals("Q04"))) {
			this.updateParaMap(taskNodeId, Constants.Pprice,
					context.getString(Constants.Pprice));
			this.updateParaMap(taskNodeId, Constants.PpriceTime,
					context.getString(Constants.PpriceTime));
			this.updateParaMap(taskNodeId, "priceUser",
					context.getString("priceUser"));
		}
		// 流程自己信息
		newContext.put("tasknodeid", taskNodeId);
		newContext.put("processid", processId);
		// 复制用户
		this.copayUser(priorTaskId, priorNodeId, taskNodeId, nodeId);
		String newPtype = newContext.getString("ptype");
		String newQtype = newContext.getString("qtype");
		String to = (StrUtils.isNull(newPtype) ? "" : newPtype)
				+ (StrUtils.isNull(newQtype) ? "" : newQtype);
		// if(!this.isDoEndNext(processId, from, to)){
		// throw new Exception("流程已进入下一阶段！");
		// }
		DataSourceManager.commit();
		TradeService.invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^EngineDao.genTaskNode()");
		return newContext;
	}

	public void removeUserId(String targetId, String userIds) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		// 得到 tasknodeid
		String sql = "select wf.tasknodeid from wf_process wf , wf_template wt where wf.templateid = wt.id and wf.id = wt.id||'_'||wt.instancecount and wt.id = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, targetId);
		rs = pstm.executeQuery();
		String taskNodeId = "";
		if (rs.next()) {
			taskNodeId = rs.getString("tasknodeid");
		} else {
			rs.close();
			pstm.close();
			return;
		}
		String[] userIdArray = userIds.split(",");
		if (userIdArray != null && userIdArray.length > 0) {
			sql = "delete from  wf_tasknodeuser where tasknodeid = ? and type = ? and value = ?";
			pstm = getCon().prepareStatement(sql);
			for (int i = 0; i < userIdArray.length; i++) {
				pstm.setString(1, taskNodeId);
				pstm.setString(2, String.valueOf(Constants.DataTypePerson));
				pstm.setString(3, userIdArray[i]);
				pstm.executeUpdate();
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}

	}

	public ParaMap addUserId(String targetId, String userIds) throws Exception {
		return this.addUserId(targetId, userIds, Constants.RightExecute);
	}

	public ParaMap addUserId(String targetId, String userIds, int rightType)
			throws Exception {
		TradeService.invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^EngineDao.addUserId()");
		PreparedStatement pstm = null;
		ResultSet rs = null;
		// 得到 tasknodeid
		String sql = "select wf.tasknodeid from wf_process wf , wf_template wt where wf.templateid = wt.id and wf.id = wt.id||'_'||wt.instancecount and wt.id = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, targetId);
		rs = pstm.executeQuery();
		String taskNodeId = "";
		if (rs.next()) {
			taskNodeId = rs.getString("tasknodeid");
		} else {
			rs.close();
			pstm.close();
			return null;
		}

		String[] userIdArray = userIds.split(",");
		if (userIdArray != null && userIdArray.length > 0) {
			sql = "insert into wf_tasknodeuser(id,tasknodeid,type,value,right_type,isagree) values(?,?,?,?,?,?)";
			pstm = getCon().prepareStatement(sql);
			for (int i = 0; i < userIdArray.length; i++) {
				pstm.setString(1, IDGenerator.newGUID());
				pstm.setString(2, taskNodeId);
				pstm.setString(3, String.valueOf(Constants.DataTypePerson));
				pstm.setString(4, userIdArray[i]);
				pstm.setString(5, String.valueOf(rightType));
				pstm.setString(6, "0");
				pstm.executeUpdate();
			}
		}

		sql = "select value as userId , right_type  from wf_tasknodeuser where tasknodeid = ? and type = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, taskNodeId);
		pstm.setString(2, String.valueOf(Constants.DataTypePerson));
		rs = pstm.executeQuery();
		ParaMap out = convert(rs);
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		TradeDao dao = new TradeDao();
		dao.payTrustPrice(targetId);
		return out;
	}

	public String getNextPhaseNodeId(String nodeId) throws Exception {
		String result = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select a.id , a.nextid  from wf_node a inner join wf_node b on a.templateid = b.templateid and a.ptype = b.ptype where b.id = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, nodeId);
		rs = pstm.executeQuery();
		List idList = new ArrayList();
		List nextIdList = new ArrayList();
		while (rs.next()) {
			idList.add(rs.getString("id"));
			nextIdList.add(rs.getString("nextid"));
		}
		if (nextIdList != null && nextIdList.size() > 0) {
			int isIn = 0;
			for (int i = 0; i < nextIdList.size(); i++) {
				String nextId = (String) nextIdList.get(i);// 得到某一个nextId
				for (int j = 0; j < idList.size(); j++) {// 看他是否在idList 中
					String id = (String) idList.get(j);
					if (id.equals(nextId)) {// 如果在IdList里面，说明他不是最后一个节点
						isIn++;
						break;
					}
				}
				if (isIn == 0) {// isIn = 0 说明该nextId 不再idList里面
					result = nextId;
					break;
				} else {
					isIn = 0;
				}
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

	/**
	 * 得到同意进入下一轮的人数
	 * 
	 * @param taskNodeId任务实例ID
	 * @return
	 */
	public int getNextAgreeNum(String taskNodeId) throws Exception {
		int result = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select count(*) as usernum  from wf_tasknodeuser where tasknodeid = ? and type = ? and right_type = ? and isagree = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, taskNodeId);
		pstm.setString(2, String.valueOf(Constants.DataTypePerson));
		pstm.setString(3, String.valueOf(Constants.RightExecute));
		pstm.setString(4, String.valueOf(1));
		rs = pstm.executeQuery();
		if (rs.next()) {
			result = rs.getInt("usernum");
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

	/**
	 * 得到下一轮要求的人数
	 * 
	 * @param taskNodeId任务实例ID
	 * @return
	 */
	public int getNextMustNum(String nextNodeId) throws Exception {

		int result = 0;

		PreparedStatement pstm = null;
		ResultSet rs = null;

		String sql = "select value from wf_parameter where refid = ? and name = ?";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, nextNodeId);
		pstm.setString(2, Constants.Pflag);
		rs = pstm.executeQuery();
		if (rs.next()) {
			String flag = rs.getString("value");
			Integer mustLicenseNum = FlagUtils.getInt(flag, 1);
			if (mustLicenseNum != null) {
				result = mustLicenseNum.intValue();
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

	/**
	 * 修改是否同意进入下一轮
	 * 
	 * @param taskNodeId
	 * @param userId
	 * @throws Exception
	 */
	public void updateUserAgree(String targetId, String taskNodeId,
			String userId, Integer isAgree) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyset = new ParaMap();
		keyset.put("tasknodeid", taskNodeId);
		keyset.put("type", 1);
		keyset.put("value", userId);
		keyset.put("isagree", isAgree);
		ParaMap tasknodeuser = dataSetDao.querySimpleData("wf_tasknodeuser",
				keyset);
		if (tasknodeuser != null && tasknodeuser.getSize() > 0) {
			return; // 已经有了该状态的用户了
		}
		int maxturn = 0;
		if (isAgree == 1) {
			ParaMap sqlParams = new ParaMap();
			sqlParams.put("moduleNo", "trans_trade_plow");
			sqlParams.put("dataSetNo", "get_tasknodeuser_maxagreeturn");
			sqlParams.put("taskNodeId", taskNodeId);
			sqlParams.put("type", String.valueOf(Constants.DataTypePerson));
			sqlParams.put("rightType", String.valueOf(Constants.RightExecute));
			sqlParams.put("isagree", isAgree);
			ParaMap maxMap = dataSetDao.queryData(sqlParams);
			if (maxMap.getSize() > 0) {
				String max = String
						.valueOf(maxMap.getRecordValue(0, "maxturn"));
				maxturn = max == null || "".equals(max)
						|| max.equalsIgnoreCase("null") ? 0 : (int) Math
						.ceil(Double.parseDouble(max));
			}
			maxturn++;
		}

		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade_plow");
		sqlParams.put("dataSetNo", "update_taskuser_isagree");
		sqlParams.put("isAgree", isAgree);
		sqlParams.put("agreeTurn", maxturn);
		sqlParams.put("taskNodeId", taskNodeId);
		sqlParams.put("type", String.valueOf(Constants.DataTypePerson));
		sqlParams.put("rightType", String.valueOf(Constants.RightExecute));
		sqlParams.put("valueInfo", userId);
		dataSetDao.executeSQL(sqlParams);

		// 如果不是第一个进入的需要增加一条虚拟出价记录
		if (maxturn > 1) {
			// 得到本节点信息
			String processId = this.getProcessId(targetId);
			ParaMap taskMap = this.curTaskNodeInfo(processId);

			sqlParams = new ParaMap();
			sqlParams.put("moduleNo", "trans_trade_plow");
			sqlParams.put("dataSetNo", "get_licenseInfo_byTagetIdUserId");
			sqlParams.put("userId", userId);
			sqlParams.put("targetId", targetId);
			ParaMap lMap = dataSetDao.queryData(sqlParams);
			String licenseId = "";
			if (lMap.getSize() > 0) {
				licenseId = (String) lMap.getRecordValue(0, "id");
			}

			ParaMap offerMap = new ParaMap();
			offerMap.put("id", null);
			offerMap.put("license_id", licenseId);
			offerMap.put("multi_trade_id",
					taskMap.getString(Constants.PmultiId));
			offerMap.put("price", taskMap.getBigDecimal(Constants.Pprice));
			offerMap.put("offer_date", new Timestamp(DateUtils.nowTime()));
			offerMap.put("status", 1);
			offerMap.put("type", 0);
			offerMap.put("create_user_id", userId);
			offerMap.put("increase", "101");
			offerMap.put("remark", "同意出封顶价");
			dataSetDao.updateData("trans_offer_log", "id", offerMap, null);
		}
	}

	public void copayUser(String priorTaskId, String priorNodeId,
			String taskId, String nodeId) throws Exception {
		ParaMap nodemap = this.getNodeInfo(nodeId);
		String ptype = nodemap.getString("ptype");
		String qtype = nodemap.getString("qtype");
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "";

		List userList = new ArrayList();
		// 当前为限时竞价(价格指标)，之前为集中报价
		int rightTypeFlag = 0; // 0 都有权限 ，1是否在userList里 2 按照isagree走
		if (Constants.LLimit.equals(ptype)) {
			if (Constants.QPrice.equals(qtype)) {
				ParaMap priorMap = this.getNodeInfo(priorNodeId);
				String priorPtype = priorMap.getString("ptype");
				if (Constants.LFocus.equals(priorPtype)) {
					// 判断是否是只有集中报价期出价的人才有出价权限
					String flag = nodemap.getString(Constants.Pflag);
					if (StrUtils.isNotNull(flag)) {
						String isPayEnter = FlagUtils.getString(flag, 0);
						if (isPayEnter != null && isPayEnter.equals("1")) {
							rightTypeFlag = 1;
							// 得到所有出价的人
							sql = " select distinct su.id as userid  from trans_offer_log tol "
									+ "left join trans_license tl on tol.license_id = tl.id "
									+ "left join sys_user su on tl.bidder_id = su.ref_id and su.user_type = 1 "
									+ "where tl.target_id =?  and tol.status in (1,2)";
							pstm = getCon().prepareStatement(sql);
							pstm.setString(1, nodemap.getString("templateid"));
							rs = pstm.executeQuery();
							while (rs.next()) {
								userList.add(rs.getString("userid"));
							}
						}
					}
				}
			} else {
				rightTypeFlag = 2;
			}
		} else if ("L04".equals(ptype) && "Q04".equals(qtype)) {
			rightTypeFlag = 2;
		}

		sql = "  select value , right_type , isagree, agreeturn from wf_tasknodeuser where tasknodeid = ? and type = ? ";
		pstm = getCon().prepareStatement(sql);
		pstm.setString(1, priorTaskId);
		pstm.setString(2, String.valueOf(Constants.DataTypePerson));
		rs = pstm.executeQuery();

		sql = "insert into wf_tasknodeuser(id,tasknodeid,type,value,right_type,isagree) values(?,?,?,?,?,?)";
		pstm = getCon().prepareStatement(sql);
		while (rs.next()) {
			String userId = rs.getString("value");
			int rightType = rs.getInt("right_type");
			int isagree = rs.getInt("isagree");
			int agreeturn = rs.getInt("agreeturn");

			pstm.setString(1, IDGenerator.newGUID());
			pstm.setString(2, taskId);
			pstm.setString(3, String.valueOf(Constants.DataTypePerson));
			pstm.setString(4, userId);
			int newRgiht = Constants.RightReadOnly;
			if (rightTypeFlag == 0) {
				newRgiht = Constants.RightExecute;
			} else if (rightTypeFlag == 1) {// 出价的人才有权限
				if (userList.indexOf(userId) >= 0) {
					newRgiht = Constants.RightExecute;
				}
			} else if (rightTypeFlag == 2) {
				if (rightType == Constants.RightExecute && isagree == 1) {
					newRgiht = Constants.RightExecute;
					if (agreeturn == 1) {// 第一个进入 放到parameter中
						this.updateParaMap(taskId, Constants.PfirstEnterUser,
								userId);
					}
				}
			}
			pstm.setString(5, String.valueOf(newRgiht));
			pstm.setString(6, "0");
			pstm.executeUpdate();
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
	}

	public ParaMap deleteTemplateProcess(String templateId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_info_modify");
		sqlParams.put("dataSetNo", "delete_template_process");
		sqlParams.put("templateId", templateId);
		ParaMap out = dataSetDao.executeSQL(sqlParams);
		return out;
	}

	public ParaMap convertRsOneRow(ResultSet rs) throws Exception {
		ParaMap out = new ParaMap();
		List fields = new ArrayList();
		int colCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String name = rs.getMetaData().getColumnName(i).toLowerCase();
			fields.add(name);
		}
		if (rs.next()) {
			if (fields != null && fields.size() > 0) {
				for (int i = 0; i < fields.size(); i++) {
					out.put(fields.get(i), rs.getObject((String) fields.get(i)));
				}
			}
		}
		return out;

	}

	public void payTrustPrice(String processId) throws Exception {
		ParaMap taskNodeMap = this.curTaskNodeInfo(processId);
		String targetId = this.getTemplateId(processId);
		TradeDao dao = new TradeDao();
		ParaMap out = dao.payTrustPrice(targetId);
	}

	public NodeHandle getHandle(ParaMap map) {
		NodeHandle handle = null;
		try {
			String clazz = map.getString("clazz");
			if (StringUtils.isNotEmpty(clazz)) {
				handle = (NodeHandle) (Class.forName(clazz).newInstance());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return handle;
	}

	public boolean isDoBeginNext(String processId) throws Exception {
		boolean result = true;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "  select id,to_char(begin_time,'yyyy-mm-dd hh24:mi:ss.ff3') begin_time , to_char(systimestamp,'yyyy-mm-dd hh24:mi:ss.ff3') sys_time from wf_lock where process_id = '"
				+ processId + "'";
		pstm = getCon().prepareStatement(sql);
		rs = pstm.executeQuery();
		String lockId = null;
		String beginTimeStr = null;
		String sysTimeStr = null;
		if (rs.next()) {
			lockId = rs.getString("id");
			beginTimeStr = rs.getString("begin_time");
			sysTimeStr = rs.getString("sys_time");
			Date beginTime = StrUtils.stringToDate(beginTimeStr,
					"yyyy-MM-dd HH:mm:ss.SSS");
			Date sysTime = StrUtils.stringToDate(sysTimeStr,
					"yyyy-MM-dd HH:mm:ss.SSS");
			String wf_process_lock_time = AppConfig
					.getPro("wf_process_lock_time");
			wf_process_lock_time = StrUtils.isNull(wf_process_lock_time) ? "1000"
					: wf_process_lock_time;
			long lockTime = Long.valueOf(wf_process_lock_time);
			if (beginTime.getTime() + lockTime > sysTime.getTime()) {
				// 当前时间在状态改变开始时间结束后1秒之内
				result = false;
			} else {
				sql = " update wf_lock set begin_time = systimestamp where id = '"
						+ lockId + "'";
				pstm = getCon().prepareStatement(sql);
				pstm.executeUpdate();
				result = true;
			}
		} else {
			sql = "insert into wf_lock(id,process_id,begin_time) values('"
					+ IDGenerator.newGUID() + "','" + processId
					+ "',systimestamp)";
			pstm = getCon().prepareStatement(sql);
			pstm.executeUpdate();
			result = true;
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

	public boolean isDoEndNext(String processId, String from, String to)
			throws Exception {
		boolean result = true;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "  select id,to_char(end_time,'yyyy-mm-dd hh24:mi:ss.ff3') end_time , "
				+ "to_char(systimestamp,'yyyy-mm-dd hh24:mi:ss.ff3') sys_time , from_phase , to_phase "
				+ "from wf_lock where process_id = '" + processId + "'";
		pstm = getCon().prepareStatement(sql);
		rs = pstm.executeQuery();
		String lockId = null;
		String endTimeStr = null;
		String sysTimeStr = null;
		String fromPhase = null;
		String toPhase = null;
		if (rs.next()) {
			lockId = rs.getString("id");
			endTimeStr = rs.getString("end_time");
			sysTimeStr = rs.getString("sys_time");
			fromPhase = rs.getString("from_phase");
			toPhase = rs.getString("to_phase");
			Date endTime = StrUtils.stringToDate(endTimeStr,
					"yyyy-MM-dd HH:mm:ss.SSS");
			Date sysTime = StrUtils.stringToDate(sysTimeStr,
					"yyyy-MM-dd HH:mm:ss.SSS");
			String wf_process_lock_time = AppConfig
					.getPro("wf_process_lock_time");
			wf_process_lock_time = StrUtils.isNull(wf_process_lock_time) ? "1000"
					: wf_process_lock_time;
			long lockTime = Long.valueOf(wf_process_lock_time);
			if (StrUtils.isNull(endTimeStr) || StrUtils.isNull(fromPhase)
					|| StrUtils.isNull(toPhase)) {
				sql = " update wf_lock set end_time = systimestamp , from_phase = '"
						+ from
						+ "' , to_phase = '"
						+ to
						+ "' where id = '"
						+ lockId + "'";
				pstm = getCon().prepareStatement(sql);
				pstm.executeUpdate();
				result = true;
			} else {
				if (endTime.getTime() + lockTime > sysTime.getTime()
						&& (!from.equals(fromPhase) || !to.equals(toPhase))) {
					// 当前时间在状态改变时间结束后1秒之内，并且开始状态和结束状态都不一样人为是重复操作
					result = false;
				} else {
					sql = " update wf_lock set end_time = systimestamp , from_phase = '"
							+ from
							+ "' , to_phase = '"
							+ to
							+ "' where id = '"
							+ lockId + "'";
					pstm = getCon().prepareStatement(sql);
					pstm.executeUpdate();
					result = true;
				}
			}
		} else {
			sql = "insert into wf_lock(id,process_id,begin_time,end_time,from_phase , to_phase) "
					+ "values('"
					+ IDGenerator.newGUID()
					+ "','"
					+ processId
					+ "',systimestamp,systimestamp,'"
					+ from
					+ "','"
					+ to
					+ "')";
			pstm = getCon().prepareStatement(sql);
			pstm.executeUpdate();
			result = true;
		}
		if (rs != null) {
			rs.close();
		}
		if (pstm != null) {
			pstm.close();
		}
		return result;
	}

}

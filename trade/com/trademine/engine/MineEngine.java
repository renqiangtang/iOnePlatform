package com.trademine.engine;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.ds.DataSourceManager;
import com.base.task.MyTimerTask;
import com.base.task.TaskManager;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.trade.utils.EngineManageDao;
import com.trademan.dao.ProcessManageDao;
import com.trademine.dao.TradeDao;
import com.trademine.service.TradeService;

public class MineEngine {
	Logger log = Logger.getLogger(this.getClass());
	private static HashMap<String, MyTimerTask> taskMap = new HashMap<String, MyTimerTask>();
	private EngineDao engineDao = new EngineDao();
	private TradeDao tradeDao = new TradeDao();

	public void addTask(MyTimerTask task, Date time) throws Exception {
		Date adjustDate = DateUtils.adjust(time);
		TaskManager.add(task, adjustDate);
		taskMap.put(task.getId(), task);
	}

	public void addTask(MyTimerTask task, long delay) throws Exception {
		long d1 = DateUtils.nowTime() + delay;
		Date d = new Date(d1);
		addTask(task, d);
	}

	public void removeTask(String processId) {
		TaskManager.remove(processId);
		taskMap.remove(processId);
	}

	public String getIsHaveTimer(String processId) {
		String timerStr = "";
		MyTimerTask old = taskMap.get(processId);
		if (old != null) {
			timerStr = "已有定时任务";
		}
		return timerStr;
	}

	public boolean getEngineState() {
		return EngineManageDao.engineStarted(Engine.Type_Mine);
	}

	/**
	 * 提交引擎
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap commit(int action, ParaMap in) throws Exception {
		ParaMap out = null;
		switch (action) {
		case Constants.Action_Manual:
			out = HandlePriceManual(in);
			break;
		case Constants.Action_Next:
			next(in);
			break;
		case Constants.Action_CommitContext:
			HandleCommitContext(in);
			break;
		case Constants.Action_EndProcess:
			HandleEndProcess(in);
			break;
		case Constants.Action_StartEngine:
			startEngine();
			break;
		case Constants.Action_StartTemplate:
			startTemplate(in);
			break;
		case Constants.Action_Opinion:
			opinion(in);
			break;
		case Constants.Action_Stop:
			out = stop(in);
			break;
		case Constants.Action_Restart:
			out = restart(in);
			break;
		default:
			log.debug("Hava no implement for Action:" + action);
			break;
		}
		DataSourceManager.commit();
		return out;
	}

	/**
	 * 重启
	 * 
	 * @param in
	 * @throws Exception
	 *             1.计算时间递推 2.写入重启时间 3.重启启动引擎
	 */
	private ParaMap restart(ParaMap in) throws Exception {
		ProcessManageDao dao = new ProcessManageDao();
		ParaMap out = dao.restartProcess(in);
		EngineManageDao.updateTimeAll();
		// startEngine();
		TradeService.invalidateTargetInfoMap();
		return out;
	}

	/**
	 * 停止交易
	 * 
	 * @param in
	 * @throws Exception
	 */
	private ParaMap stop(ParaMap in) throws Exception {
		String targetId = in.getString("id");
		String processId = engineDao.getProcessIdByTargetId(targetId);
		ProcessManageDao dao = new ProcessManageDao();
		ParaMap out = dao.stopProcess(processId);
		EngineManageDao.updateTimeAll();
		// removeTask(processId);
		return out;
	}

	/**
	 * 同意不同意进入
	 * 
	 * @param in
	 * @throws Exception
	 */
	private void opinion(ParaMap in) throws Exception {
		String targetId = in.getString("targetId");
		String processId = engineDao.getProcessId(targetId);
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		String tasknodeid = context.getString("tasknodeid");
		String userid = in.getString("u");
		Integer agreeType = in.getInt("agreeType");
		engineDao.updateUserAgree(targetId, tasknodeid, userid, agreeType);
	}

	/**
	 * 启动引擎
	 * 
	 * @throws Exception
	 */
	private ParaMap startEngine() throws Exception {
		// 先把已经等待结束的进程结束掉
		engineDao.doEndProcess();
		ParaMap map = engineDao.getNotEndProcess("'301','401'");
		log.debug("引擎启动,待处理的任务数..." + map.getSize());
		for (int i = 0; i < map.getSize(); i++) {
			try {
				String processId = String.valueOf(map.getRecordValue(i, "id"));
				ParaMap context = engineDao.curTaskNodeInfo(processId);
				Date beginTime = context.getDate(Constants.PbeginTime);
				Date endTime = context.getDate(Constants.PendTime);
				if (beginTime == null && endTime == null) {
					log.debug("..." + processId + "有异常,没有开始时间和结束时间!");
				}
				if (beginTime != null
						&& DateUtils.nowTime() < beginTime.getTime()) {
					NodeHandle handle = engineDao.getHandle(context);
					if (handle != null)
						handle.init(context);
				} else if (endTime != null) {
					if (DateUtils.nowTime() >= endTime.getTime())
						this.next(context);
					else
						this.addTask(new EndTimerTask(processId), endTime);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * 启动一个流程
	 * 
	 * @param tempalteId
	 * @throws Exception
	 */
	private ParaMap startTemplate(ParaMap in) throws Exception {
		String templateId = in.getString("templateId");
		String processId = engineDao.startTemplate(templateId);
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		next(context);
		return null;
	}

	/**
	 * 处理下一个节点
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public synchronized void next(ParaMap context) throws Exception {
		// 取上下文中下一个阶段nodeid 和 数据库中的本阶段nodeid 比较，如果相等，就不进入下一个阶段了
		String thisNextId = context.getString("nextid");
		ParaMap currentNode = engineDao.getCurrentNodeInfo(context
				.getString("processid"));
		if (currentNode != null && currentNode.getSize() > 0) {
			int isSuspend = currentNode.getRecordInt(0, "is_suspend");
			if (isSuspend == 1) {
				return;
			}
			String thisNodeId = currentNode.getRecordString(0, "nodeid");
			if (StrUtils.isNotNull(thisNextId)
					&& StrUtils.isNotNull(thisNodeId)
					&& thisNextId.equals(thisNodeId)) {
				return;
			}
		}
		//
		NodeHandle handle = engineDao.getHandle(context);
		if (handle != null) {
			handle.after(context);
		}
		// after方法中可能会修改nextid等信息，所以再取一次context信息
		context = engineDao.curTaskNodeInfo(context.getString("processid"));
		//
		String nextId = context.getString("nextid");
		ParaMap newContext = engineDao.genTaskNode(nextId, context);
		//
		handle = engineDao.getHandle(newContext);
		if (handle != null) {
			handle.init(newContext);
		}
	}

	/**
	 * 处理手工提交
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private ParaMap HandlePriceManual(ParaMap in) throws Exception {
		String targetId = in.getString("targetId");
		String processId = engineDao.getProcessId(targetId);
		String userId = in.getString("u");
		BigDecimal price = in.getBigDecimal("price");
		String enPrice = in.getString("enPrice");
		String ptype = in.getString("ptype");
		String qtype = in.getString("qtype");
		ParaMap out = tradeDao.savePrice(targetId, userId, price, enPrice,
				null, ptype, qtype, null);
		int state = out.getInt("state");
		if (state == 0)
			return out;
		if (state == 1) {
			tradeDao.payTrustPrice(targetId);
		}
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		NodeHandle handle = engineDao.getHandle(context);
		if (handle != null) {
			handle.run(context);
		}
		return out;
	}

	/**
	 * 处理提交修改的上下文
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private void HandleCommitContext(ParaMap in) throws Exception {
		String tasknodeid = in.getString("tasknodeid");
		if (StringUtils.isNotEmpty(tasknodeid)) {
			in.remove("tasknodeid");
			engineDao.updateParaMap(tasknodeid, in);
			in.put("tasknodeid", tasknodeid);
		} else {
			throw new Exception("has not found the parameter:tasknodeid");
		}
	}

	/**
	 * 处理特权指令
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private void HandleEndProcess(ParaMap context) throws Exception {
		HandleN02 handle = new HandleN02();
		handle.run(context);
	}

}

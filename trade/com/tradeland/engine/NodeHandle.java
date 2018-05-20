package com.tradeland.engine;

import java.util.Date;

import org.apache.log4j.Logger;

import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.tradeland.engine.BeginTimerTask;
import com.tradeland.engine.EndTimerTask;
import com.tradeland.engine.EngineDao;
import com.tradeland.engine.LandEngine;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.tradeland.dao.TradeDao;
import com.tradeland.service.TradeService;

public class NodeHandle {

	Logger log = Logger.getLogger(this.getClass());

	public EngineDao engineDao = new EngineDao();
	public TradeDao tradeDao = new TradeDao();
	public LandEngine engine = Engine.getLandEngine();

	/**
	 * 初始化
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void init(ParaMap context) throws Exception {
		TradeService.invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^NodeHandle.init()");
		String templateId = context.getString("templateid");
		TradeService.removeTargetPriceMap(templateId);
		String processId = context.getString("processid");
		Date beginTime = context.getDate(Constants.PbeginTime);
		if (beginTime != null && DateUtils.nowTime() < beginTime.getTime()) {
			engineDao.setProcessState(processId, Constants.ProcessWait);
			engine.addTask(new BeginTimerTask(processId), beginTime);
		} else {
			before(context);
		}
	}

	/**
	 * 运行前
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void before(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		engineDao.setProcessState(processId, Constants.ProcessRunning);
		Date endTime = context.getDate(Constants.PendTime);
		if (endTime != null && DateUtils.nowTime() < endTime.getTime())
			engine.addTask(new EndTimerTask(processId), endTime);
	}

	/**
	 * 运行中
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void run(ParaMap context) throws Exception {

	}

	/**
	 * 运行后
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void after(ParaMap context) throws Exception {

	}
}

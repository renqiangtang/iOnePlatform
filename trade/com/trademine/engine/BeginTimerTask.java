package com.trademine.engine;

import com.base.task.MyTimerTask;
import com.base.utils.ParaMap;
import com.trade.utils.Constants;

public class BeginTimerTask extends MyTimerTask {

	public BeginTimerTask(String processId) {
		super(processId);
		this.setInfo("矿产交易-开始任务");
	}

	public void execute() throws Exception {
		EngineDao engineDao = new EngineDao();
		String processId = getId();
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		engineDao.setProcessState(processId, Constants.ProcessRunning);
		NodeHandle handle = engineDao.getHandle(context);
		if (handle != null)
			handle.before(context);
	}

}

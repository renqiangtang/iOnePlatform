package com.trademine.engine;

import com.base.task.MyTimerTask;
import com.base.utils.ParaMap;
import com.trade.utils.Engine;

public class EndTimerTask extends MyTimerTask {
	public EndTimerTask(String processId) {
		super(processId);
		this.setInfo("矿产交易-结束任务");
	}

	@Override
	public void execute() throws Exception {
		EngineDao engineDao = new EngineDao();
		String processId = getId();
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		Engine.getMineEngine().next(context);
	}

}

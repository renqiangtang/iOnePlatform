package com.tradeland.engine;

import com.base.task.MyTimerTask;
import com.base.utils.ParaMap;
import com.trade.utils.Engine;

public class EndTimerTask extends MyTimerTask {

	public EndTimerTask(String processId) {
		super(processId);
		this.setInfo("土地交易-结束任务");
	}

	@Override
	public void execute() throws Exception {
		EngineDao engineDao = new EngineDao();
		String processId = getId();
		ParaMap context = engineDao.curTaskNodeInfo(processId);
		Engine.getLandEngine().next(context);

	}

}

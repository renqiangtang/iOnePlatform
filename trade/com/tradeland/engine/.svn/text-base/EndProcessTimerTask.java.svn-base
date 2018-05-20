package com.tradeland.engine;

import com.base.task.MyTimerTask;
import com.trade.utils.Constants;

public class EndProcessTimerTask extends MyTimerTask {

	public EndProcessTimerTask(String processId) {
		super(processId);
		this.setInfo("土地交易-流程结束");
	}

	public void execute() throws Exception {
		EngineDao engineDao = new EngineDao();
		String processId = getId();
		engineDao.setProcessState(processId, Constants.ProcessEnd);

	}
}

package com.base.task;

import org.apache.log4j.Logger;

import com.base.log.LogUtils;
import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.trade.utils.EngineManageDao;

public class EngineTimerTask extends MyTimerTask {
	static Logger log = Logger.getLogger(EngineTimerTask.class);

	public EngineTimerTask(String id) {
		super(id);
		this.setInfo("引擎");
	}

	@Override
	public void execute() throws Exception {
		String type = this.getId();
		String info = this.getInfo();
		try {
			boolean started = EngineManageDao.engineStarted(type);
			if (!started) {
				TaskManager.clear();
				Engine.getLandEngine().commit(Constants.Action_StartEngine,
						null);
				Engine.getMineEngine().commit(Constants.Action_StartEngine,
						null);
				EngineManageDao.updateTime(type);
			}
			String localIp = Engine.getWebId();
			String dbIp = EngineManageDao.ip(type);
			if (!localIp.equals(dbIp))
				TaskManager.clear();
			if (localIp.equals(dbIp))
				EngineManageDao.updateTime(type);
			StringBuffer sb = new StringBuffer();
			sb.append(type + info + "在机器[" + dbIp + "]已经启动...\n");
			if (TaskManager.oneTimeTaskMap.size() > 0) {
				sb.append("本机定时任务数:" + TaskManager.oneTimeTaskMap.size() + "\n");
				sb.append(TaskManager.allinfo());
			} else {
				sb.append("本机无定时任务\n");
			}
			LogUtils.info(1, sb.toString());
		} catch (Exception ex) {
			throw ex;
		} finally {
			TaskManager.add(new EngineTimerTask(type), 1000);
		}

	}
}

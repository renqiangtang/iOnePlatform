package com.trademine.engine;

import java.util.Timer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.trade.utils.Constants;
import com.trademine.engine.EndProcessTimerTask;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.trademine.service.TradeService;

/**
 * 流程结束
 * 
 * @author 朱金亮
 * 
 */
public class HandleN02 extends NodeHandle {
	
	Logger log = Logger.getLogger(this.getClass());

	public void init(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		if (StringUtils.isNotEmpty(processId)) {
			engineDao.setProcessState(processId, Constants.ProcessEndPre);
			engineDao.updateParaMap(processId, Constants.PendTime,DateUtils.nowStr());
			String targetId = engineDao.getTemplateId(processId);
			tradeDao.doEndTarget(targetId);
			TradeService.invalidateTargetInfoMap();
			log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^HandleN02.init()");
			//tradeDao.sendSubmitChildNoXml(targetId);
			EndProcessTimerTask endProcessTimerTask = new EndProcessTimerTask(processId);
			Timer timer = new Timer();
			timer.schedule(endProcessTimerTask, 60 * 60 * 1000);
		}
	}

}

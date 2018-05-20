package com.tradeplow.engine;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trade.utils.Constants;
import com.tradeplow.service.TradeService;

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
		String succLicenseId = context.getString("succLicenseId");
		if (StringUtils.isNotEmpty(processId)) {
			engineDao.setProcessState(processId, Constants.ProcessEndPre);
			engineDao.updateParaMap(processId, Constants.PendTime,
					DateUtils.nowStr());
			String targetId = engineDao.getTemplateId(processId);
			if (StrUtils.isNotNull(succLicenseId)) {
				tradeDao.doEndTarget(targetId, succLicenseId);
			} else {
				tradeDao.doEndTarget(targetId);
			}
			TradeService.invalidateTargetInfoMap();
			log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^HandleN02.init()");
			// tradeDao.sendSubmitChildNoXml(targetId);
			EndProcessTimerTask endProcessTimerTask = new EndProcessTimerTask(
					processId);
			engine.addTask(endProcessTimerTask, 30 * 60 * 1000);
		}
	}

}

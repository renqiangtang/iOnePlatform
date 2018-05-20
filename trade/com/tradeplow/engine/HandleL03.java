package com.tradeplow.engine;

import java.math.BigDecimal;
import java.util.Date;

import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.trade.utils.Constants;

/**
 * 竞价期
 * 
 * @author 朱金亮
 * 
 */
public class HandleL03 extends NodeHandle {
	public void before(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		engineDao.setProcessState(processId, Constants.ProcessRunning);
		Integer firstWait = context.getInt("firstWait");// 秒
		Date endTime = context.getDate(Constants.PendTime);
		Date beginTime = context.getDate(Constants.PbeginTime);
		String taskNodeId = context.getString("tasknodeid");
		if (endTime == null && firstWait != null) {// 更新结束时间
			long n1 = DateUtils.nowTime();
			n1 += firstWait * 1000;
			endTime = new Date(n1);
			engineDao.updateParaMap(taskNodeId, Constants.PendTime,
					DateUtils.getStr(endTime));
		}
		if (beginTime == null) {// 更新开始时间
			engineDao.updateParaMap(taskNodeId, Constants.PbeginTime,
					DateUtils.getStr(DateUtils.now()));
		}
		engine.addTask(new EndTimerTask(processId), endTime);
		// 委托出价处理
		engineDao.payTrustPrice(processId);
	}

	public void run(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		BigDecimal curPrice = context.getBigDecimal(Constants.Pprice);
		BigDecimal finalPrice = context.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = context.getBigDecimal(Constants.Pstep);
		int sign = step.compareTo(new BigDecimal("0")) > 0 ? 1 : -1;
		Integer waitTime = null;
		if (finalPrice != null
				&& (curPrice.compareTo(finalPrice) == 0 || curPrice
						.compareTo(finalPrice) == sign)) {// 最后一次等待
			waitTime = context.getInt("lastWait");
			if (waitTime == null) {
				waitTime = 60 * 5;
			}
		} else {// 平常等待
			waitTime = context.getInt("limitWait");
			if (waitTime == null) {
				waitTime = 60 * 5;
			}
		}
		String endTime = DateUtils.getStr(new Date(DateUtils.nowTime()
				+ waitTime * 1000));
		String tasknodeId = context.getString("tasknodeid");
		engineDao.updateParaMap(tasknodeId, Constants.PendTime, endTime);
		engine.removeTask(processId);
		engine.addTask(new EndTimerTask(processId), waitTime * 1000);
	}

	public void after(ParaMap context) throws Exception {
		BigDecimal curPrice = context.getBigDecimal(Constants.Pprice);
		BigDecimal finalPrice = context.getBigDecimal(Constants.PfinalValue);
		BigDecimal stepPrice = context.getBigDecimal(Constants.Pstep);
		int sign = stepPrice.compareTo(new BigDecimal("0"));
		String tasknodeId = context.getString("tasknodeid");
		String templateId = context.getString("templateid");
		String nextId = context.getString("nextid");
		// 如果价格超过或等于封顶价需要进行人数检测，否者直接交易结束
		if (curPrice != null
				&& finalPrice != null
				&& (curPrice.compareTo(finalPrice) == 0 || curPrice
						.compareTo(finalPrice) == sign)) {
			int curAgreeNum = engineDao.getNextAgreeNum(tasknodeId);
			int mustAgreeNum = engineDao.getNextMustNum(nextId);
			if (curAgreeNum < mustAgreeNum) {
				engineDao.updateParaMap(tasknodeId, "nextid", templateId
						+ Constants.NodeEndChar);// 直接结束
			}
		} else {
			engineDao.updateParaMap(tasknodeId, "nextid", templateId
					+ Constants.NodeEndChar);// 直接结束
		}
	}
}

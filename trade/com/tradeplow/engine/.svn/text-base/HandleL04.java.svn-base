package com.tradeplow.engine;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.base.utils.DateUtils;
import com.base.utils.Lottery;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trade.utils.Constants;
import com.tradeplow.dao.TradeDao;

/**
 * 摇号
 * 
 * @author 朱金亮
 * 
 */
public class HandleL04 extends NodeHandle {

	/**
	 * 运行前
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void before(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		engineDao.setProcessState(processId, Constants.ProcessRunning);
		String templateId = context.getString("templateid");
		TradeDao tdao = new TradeDao();
		ParaMap targetMap = tdao.getTargetData(templateId);
		int is_online = 0;
		if (targetMap != null && targetMap.getSize() > 0) {
			is_online = targetMap.getRecordInt(0, "is_online");
		}
		Integer firstWait = context.getInt("firstWait");// 秒
		Date endTime = context.getDate(Constants.PendTime);
		Date beginTime = context.getDate(Constants.PbeginTime);
		String taskNodeId = context.getString("tasknodeid");
		if (beginTime == null) {// 更新开始时间
			engineDao.updateParaMap(taskNodeId, Constants.PbeginTime,
					DateUtils.getStr(DateUtils.now()));
		}
		if (is_online == 1) {
			if (endTime == null) {// 更新结束时间
				firstWait = firstWait == null ? 5 : firstWait;
				long n1 = DateUtils.nowTime();
				n1 += firstWait * 1000;
				endTime = new Date(n1);
				engineDao.updateParaMap(taskNodeId, Constants.PendTime,
						DateUtils.getStr(endTime));
			}
			if (endTime != null && DateUtils.nowTime() < endTime.getTime()) {
				engine.addTask(new EndTimerTask(processId), endTime);
			}
		}

		// 生成成交人
		List list = tradeDao.getTaskNodeUserList(templateId, taskNodeId);
		if (list != null && list.size() > 0) {
			Lottery lottery = new Lottery();
			int listIndex = lottery.hit(list.size());
			engineDao.updateParaMap(taskNodeId, "succLicenseId",
					((ParaMap) list.get(listIndex)).getString("license_id"));
		}
	}

	public void after(ParaMap context) throws Exception {
		String templateId = context.getString("templateid");
		// String succLicenseId = TradeDao.getLotteryMap(templateId);
		String succLicenseId = context.getString("succLicenseId");
		String tasknodeId = context.getString("tasknodeid");
		if (StrUtils.isNull(succLicenseId)) {
			TradeDao tdao = new TradeDao();
			List list = tdao.getTaskNodeUserList(templateId, tasknodeId);
			if (list != null && list.size() > 0) {
				if (list.size() == 1) {
					succLicenseId = ((ParaMap) list.get(0))
							.getString("license_id");
				} else {
					Lottery lottery = new Lottery();
					int listIndex = lottery.hit(list.size());
					succLicenseId = ((ParaMap) list.get(listIndex))
							.getString("license_id");
				}
			}
			// TradeDao.putLotteryMap(templateId, succLicenseId);
		}
		String nextId = context.getString("nextid");
		engineDao.updateParaMap(tasknodeId, "succLicenseId", succLicenseId);// 在本阶段上下文中写入成交人
		engineDao.updateParaMap(nextId, "succLicenseId", succLicenseId);// 在下一个阶段写入成交人
		// TradeDao.removeLotteryMap(templateId);
	}
}

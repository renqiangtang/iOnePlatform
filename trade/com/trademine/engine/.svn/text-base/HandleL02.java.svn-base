package com.trademine.engine;

import java.math.BigDecimal;
import java.util.Date;

import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.trademine.engine.EndTimerTask;
import com.trademine.engine.EngineDao;
import com.base.dao.DataSetDao;
import com.base.utils.DateUtils;
import com.base.utils.FlagUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 限时竞价
 * 
 * @author 朱金亮
 * 
 */
public class HandleL02 extends NodeHandle {

	public void before(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		engineDao.setProcessState(processId, Constants.ProcessRunning);
		Date endTime = context.getDate(Constants.PendTime);
		if (endTime != null && DateUtils.nowTime() < endTime.getTime()) {
			engine.addTask(new EndTimerTask(processId), endTime);
		}
		// 委托出价处理
		engineDao.payTrustPrice(processId);
	}

	public void run(ParaMap context) throws Exception {
		String processId = context.getString("processid");
		BigDecimal curPrice = context.getBigDecimal(Constants.Pprice);
		BigDecimal finalPrice = context.getBigDecimal(Constants.PfinalValue);
		BigDecimal step = context.getBigDecimal(Constants.Pstep);
		Date endTime = context.getDate(Constants.PendTime);
		int sign = step.compareTo(new BigDecimal("0")) > 0 ? 1 : -1;
		boolean isFinal = false;
		if (finalPrice != null && sign == 1
				&& finalPrice.compareTo(new BigDecimal("0")) <= 0) {
			isFinal = false;
		} else {
			if (finalPrice != null
					&& curPrice != null
					&& (curPrice.compareTo(finalPrice) == 0 || curPrice
							.compareTo(finalPrice) == sign)) {// 最后一次等待
				isFinal = true;
			} else {
				isFinal = false;
			}
		}
		if (isFinal) {
			Integer waitTime = 300;
			String tasknodeId = context.getString("tasknodeid");
			String nextId = context.getString("nextid");
			ParaMap nodeMap = engineDao.getNodeInfo(nextId);
			String lastWait = nodeMap.getString("lastWait");
			if (StrUtils.isNotNull(lastWait)) {
				waitTime = Integer.parseInt(lastWait);
			}
			String delayEndTime = DateUtils.getStr(new Date(endTime.getTime()
					+ (waitTime * 1000)));
			engineDao.updateParaMap(tasknodeId, Constants.PendTime,
					delayEndTime);
			engine.removeTask(processId);
			engine.addTask(new EndTimerTask(processId),
					DateUtils.getDate(delayEndTime));
		}
	}

	public void after(ParaMap context) throws Exception {

		EngineDao engineDao = new EngineDao();
		String taskNodeId = context.getString("tasknodeid");
		String templateId = context.getString("templateid");
		String nextId = context.getString("nextid");
		ParaMap nodeMap = engineDao.getNodeInfo(nextId);

		BigDecimal finalPrice = context.getBigDecimal(Constants.PfinalValue);
		BigDecimal lastPrice = context.getBigDecimal(Constants.Pprice);
		String ptype = nodeMap.getString("ptype");
		String qtype = nodeMap.getString("qtype");
		// 如果出到了封顶价不再进入限时竞价
		if (finalPrice != null && lastPrice != null
				&& lastPrice.compareTo(finalPrice) >= 0) {
			// 如果下一阶段是限时竞价
			if (Constants.LLimit.equals(ptype)) {
				Integer mustLicenseNum = null;
				Integer mustPayNum = null;
				if (qtype != null && qtype.equals(Constants.QPrice)) {
					String flag = nodeMap.getString("flag");
					mustLicenseNum = FlagUtils.getInt(flag, 1);
					mustPayNum = FlagUtils.getInt(flag, 2);
					engineDao.updateParaMap(taskNodeId, "nextid",
							nodeMap.getString("nextid"));
					engineDao.updateParaMap(nodeMap.getString("nextid"),
							"beginTime", nodeMap.getString("beginTime"));
				} else {
					String flag = nodeMap.getString("flag");
					mustLicenseNum = FlagUtils.getInt(flag, 1);
					mustPayNum = FlagUtils.getInt(flag, 2);
				}
				boolean l = true;// 是否达到竞买人数
				if (mustLicenseNum != null) {
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap sqlParams = new ParaMap();
					sqlParams.put("moduleNo", "trans_trade");
					sqlParams.put("dataSetNo", "get_effective_licneseNum");
					sqlParams.put("targetId", nodeMap.getString("templateid"));
					ParaMap licenseMap = dataSetDao.queryData(sqlParams);
					int licenseNum = (int) Math.ceil(Double.parseDouble(String
							.valueOf(licenseMap.getRecordValue(0, "num"))));
					if (mustLicenseNum.intValue() > licenseNum) {
						l = false;
					} else {
						l = true;
					}
				}

				boolean p = true;// 是否达到出价人数
				if (mustPayNum != null) {
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap sqlParams = new ParaMap();
					sqlParams.put("moduleNo", "trans_trade");
					sqlParams.put("dataSetNo", "get_validOfferLog_licenseNum");
					sqlParams.put("targetId", nodeMap.getString("templateid"));
					ParaMap offerMap = dataSetDao.queryData(sqlParams);
					int payNum = (int) Math.ceil(Double.parseDouble(String
							.valueOf(offerMap.getRecordValue(0, "num"))));
					if (mustPayNum > payNum) {
						p = false;
					} else {
						p = true;
					}
				}
				if (!l || !p) {
					engineDao.updateParaMap(taskNodeId, "nextid", templateId
							+ Constants.NodeEndChar);
				}
			}
		} else {
			// 进入限时竞价
			if (Constants.LLimit.equals(ptype)) {

				String flag = nodeMap.getString("flag");
				Integer mustLicenseNum = FlagUtils.getInt(flag, 1);
				Integer mustPayNum = FlagUtils.getInt(flag, 2);

				boolean l = true;// 是否达到竞买人数
				if (mustLicenseNum != null) {
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap sqlParams = new ParaMap();
					sqlParams.put("moduleNo", "trans_trade");
					sqlParams.put("dataSetNo", "get_effective_licneseNum");
					sqlParams.put("targetId", nodeMap.getString("templateid"));
					ParaMap licenseMap = dataSetDao.queryData(sqlParams);
					int licenseNum = (int) Math.ceil(Double.parseDouble(String
							.valueOf(licenseMap.getRecordValue(0, "num"))));
					if (mustLicenseNum.intValue() > licenseNum) {
						l = false;
					} else {
						l = true;
					}
				}

				boolean p = true;// 是否达到出价人数
				if (mustPayNum != null) {
					DataSetDao dataSetDao = new DataSetDao();
					ParaMap sqlParams = new ParaMap();
					sqlParams.put("moduleNo", "trans_trade");
					sqlParams.put("dataSetNo", "get_validOfferLog_licenseNum");
					sqlParams.put("targetId", nodeMap.getString("templateid"));
					ParaMap offerMap = dataSetDao.queryData(sqlParams);
					int payNum = (int) Math.ceil(Double.parseDouble(String
							.valueOf(offerMap.getRecordValue(0, "num"))));
					if (mustPayNum > payNum) {
						p = false;
					} else {
						p = true;
					}
				}
				if (!l || !p) {
					engineDao.updateParaMap(taskNodeId, "nextid", templateId
							+ Constants.NodeEndChar);
				}

			}
		}
	}

}

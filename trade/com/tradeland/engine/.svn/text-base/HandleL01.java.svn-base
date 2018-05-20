package com.tradeland.engine;

import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.tradeland.engine.EngineDao;
import com.base.dao.DataSetDao;
import com.base.utils.FlagUtils;
import com.base.utils.ParaMap;

/**
 * 公告
 * @author 朱金亮
 *
 */
public class HandleL01 extends NodeHandle {

	public void after(ParaMap context) throws Exception {
		ParaMap out = new ParaMap();
		EngineDao engineDao = new EngineDao();
		String taskNodeId = context.getString("tasknodeid");
		String templateId = context.getString("templateid");
		String nextId = context.getString("nextid");
		ParaMap nodeMap = engineDao.getNodeInfo(nextId);
		String ptype = nodeMap.getString("ptype");
		//公告期过后修改trans_target.status
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", templateId);
		sqlParams.put("status", 4);
		dataSetDao.updateData("trans_target", "id", sqlParams);
		//公告期过后直接是限时竞价的话（拍卖），检查人数
		if (Constants.LLimit.equals(ptype)) {
			String flag = nodeMap.getString("flag");
			Integer mustLicenseNum = FlagUtils.getInt(flag, 1);
			if (mustLicenseNum != null) {
				sqlParams = new ParaMap();
				sqlParams.put("moduleNo", "trans_trade");
				sqlParams.put("dataSetNo", "get_effective_licneseNum");
				sqlParams.put("targetId", nodeMap.getString("templateid"));
				ParaMap licenseMap = dataSetDao.queryData(sqlParams);
				int licenseNum = (int) Math.ceil(Double.parseDouble(String.valueOf(licenseMap.getRecordValue(0, "num"))));
				if (mustLicenseNum.intValue()>licenseNum) {
					//没有达到进入限时竞价的人数，直接下个节点是结束节点
					engineDao.updateParaMap(taskNodeId, "nextid",templateId+Constants.NodeEndChar);
				}
			}

		}	
	}

}

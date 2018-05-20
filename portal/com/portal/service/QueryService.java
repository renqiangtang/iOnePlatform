package com.portal.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.portal.dao.QueryDao;
import com.trade.utils.Constants;
import com.tradeland.dao.TradeDao;
import com.tradeland.engine.EngineDao;

public class QueryService extends BaseService {
	public final String moduleNo = "trans_portal";
	public DataSetDao dao = new DataSetDao();

	/**
	 * 查询条件：区域、业务类型（矿业权、探矿权、土地） 公告号, 出让类型 ,宗地编号 , 有无低价 , 挂牌起始时间, 挂牌截止时间, 保证金,
	 * 保证金到账截止时间, 起始价, 最高报价
	 * 
	 * @param in
	 *            goodsType:交易物类型 cantonId：区域
	 * @return CUSTOM_CONDITION
	 * @throws Exception
	 */
	public ParaMap targetOfNotice(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap customConditions = new ParaMap();
		ParaMap sqlParams = new ParaMap();
		StringBuffer customCondition = new StringBuffer();
		if (in.containsKey("cantonId")) {// 区域
			String value = in.getString("cantonId");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition.append(" and canton.id = :cantonId ");
				sqlParams.put("cantonId", value);
			}
		}
		if (in.containsKey("goodsType")) {// 区域
			String value = in.getString("goodsType");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition
						.append(" and substr(notice.business_type, 1, 3) = :goodsType ");
				sqlParams.put("goodsType", value);
			}
		}
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap noticeOut = dao.queryDataByModuleNo(moduleNo, "queryNotice",
				sqlParams);
		List<ParaMap> noticeList = noticeOut.getListObj();
		for (int i = 0; i < noticeList.size(); i++) {
			ParaMap item = noticeList.get(i);
			String noticeId = item.getString("id");
			ParaMap in2 = new ParaMap();
			in2.put("noticeId", noticeId);
			ParaMap targetOut = dao.queryDataByModuleNo(moduleNo,
					"queryTargetByNotice", in2);
			List<ParaMap> targetList = targetOut.getListObj();
			for (int j = 0; j < targetList.size(); j++) {
				// 保证金
				ParaMap item2 = targetList.get(j);
				ParaMap earnestMap = new ParaMap();
				earnestMap.put("targetId", item2.getString("targetid"));
				ParaMap earnestOut = dao.queryDataByModuleNo(moduleNo,
						"queryEarnestByTargetId", earnestMap);
				item2.put("earnestList", earnestOut.getListObj());
			}
			//
			item.put("list", targetList);
		}
		out.put("notice_data", noticeList);
		return out;
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryCanton(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap out1 = dao.queryDataByModuleNo(moduleNo, "queryCanton", in);
		out.put("cantons", out1.getListObj());
		return out;
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryGoodsType(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap out1 = dao.queryDataByModuleNo(moduleNo, "queryGoodsType", in);
		out.put("goodsTypes", out1.getListObj());
		return out;
	}

	/**
	 * 查找标的成交情况
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryFinishedTarget(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		StringBuffer customCondition = new StringBuffer("");
		ParaMap sqlParams = new ParaMap();
		if (in.containsKey("cantonId")) {// 区域
			String value = in.getString("cantonId");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition
						.append(" and exists(select * from sys_organ o1 where o1.id=target.trans_organ_id and o1.canton_id=:cantonId)");
				sqlParams.put("cantonId", value);
			}
		}
		if (in.containsKey("goodsType")) {// 交易类型
			String value = in.getString("goodsType");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition
						.append(" and substr(target.business_type,1,3)=:goodsType");
				sqlParams.put("goodsType", value);
			}
		}
		if (in.containsKey("status")) {// 交易结果（成交5、流拍6等）
			String value = in.getString("status");
			if (StringUtils.isNotEmpty(value)) {
				if (value.equals("0")) {
					customCondition.append(" and target.status>=5");
				} else {
					customCondition.append(" and target.status=:status");
					sqlParams.put("status", value);
				}
			}
		}
		if (in.containsKey("targetNo")) {// 按宗地编号查询
			String value = in.getString("targetNo");
			//value = CharsetUtils.getString(value);
			if (StringUtils.isNotEmpty(value)) {
				customCondition
						.append(" and target.no like '%' || :targetNo || '%'");
				sqlParams.put("targetNo", value);
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap out1 = dao.queryDataByModuleNo(moduleNo, "queryFinishedTarget",
				sqlParams);
		List<ParaMap> targetList = out1.getListObj();
		for (int i = 0; i < targetList.size(); i++) {
			ParaMap earnestMap = new ParaMap();
			ParaMap item2 = targetList.get(i);
			earnestMap.put("targetId", item2.get("targetid"));
			ParaMap earnestOut = dao.queryDataByModuleNo(moduleNo,
					"queryEarnestByTargetId", earnestMap);
			item2.put("earnestList", earnestOut.getListObj());
		}
		out.put("result_data", targetList);
		return out;
	}

	/**
	 * 查找正在交易的标的
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryDealingTarget(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		StringBuffer customCondition = new StringBuffer("");
		ParaMap sqlParams = new ParaMap();
		if (in.containsKey("cantonId")) {// 区域
			String value = in.getString("cantonId");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition.append(" and organ.canton_id=:cantonId");
				sqlParams.put("cantonId", value);
			}
		}
		if (in.containsKey("goodsType")) {// 交易类型
			String value = in.getString("goodsType");
			if (StringUtils.isNotEmpty(value) && !"0".equals(value)) {
				customCondition
						.append(" and substr(target.business_type,1,3)=:goodsType");
				sqlParams.put("goodsType", value);
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap targetMap = dao.queryDataByModuleNo(moduleNo,
				"queryDealingTarget", sqlParams);
		List<ParaMap> targetList = targetMap.getListObj();
		for (int i = 0; i < targetList.size(); i++) {
			ParaMap earnestMap = new ParaMap();
			ParaMap item2 = targetList.get(i);
			earnestMap.put("targetId", item2.get("targetid"));
			ParaMap earnestOut = dao.queryDataByModuleNo(moduleNo,
					"queryEarnestByTargetId", earnestMap);
			item2.put("earnestList", earnestOut.getListObj());
		}
		out.put("trading_data", targetList);
		return out;
	}

	/**
	 * 竞价中的标的出价列表
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryTargetOffer(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = in.getString("targetId");
		int count = 5;
		if (in.get("count") != null) {
			count = in.getInt("count");
		}
		int pageIndex = 1;
		if (in.get("pageIndex") != null) {
			pageIndex = in.getInt("pageIndex");
		}
		TradeDao tradeDao = new TradeDao();
		ParaMap target = tradeDao.getRowOneInfo(dao.queryDataByModuleNo(
				moduleNo, "query_offer_target", in));

		// 得到taskNode相关信息
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessId(targetId);
		ParaMap taskNodeMap = engineDao.curTaskNodeInfo(processId);
		String taskNodeId = taskNodeMap.getString("tasknodeid");
		String nodeId = taskNodeMap.getString("nodeid");
		String unit = taskNodeMap.getString(Constants.Punit);
		String ptype = taskNodeMap.getString("ptype");
		BigDecimal lastPrice = taskNodeMap.getBigDecimal(Constants.Pprice);
		BigDecimal beginPrice = taskNodeMap.getBigDecimal(Constants.PinitValue);
		BigDecimal step = taskNodeMap.getBigDecimal(Constants.Pstep);

		target.put(Constants.Punit, unit);
		if (ptype.equals(Constants.LNotice)) {
			target.put("pname", "公告期");
		} else if (ptype.equals(Constants.LFocus)) {
			target.put("pname", "集中报价期");
		} else if (ptype.equals(Constants.LLimit)) {
			target.put("pname", "限时竞价期");
		}
		// 格式化金额
		target.put(Constants.Pprice + "Str",
				tradeDao.formatMoney(lastPrice, unit));
		target.put(Constants.PinitValue + "Str",
				tradeDao.formatMoney(beginPrice, unit));
		target.put(Constants.Pstep + "Str", tradeDao.formatMoney(step, unit));

		// 得到倒计时
		Date sysDate = DateUtils.now();
		if (!DateUtils.isNull(taskNodeMap.getString(Constants.PbeginTime))
				&& !DateUtils.isNull(taskNodeMap.getString(Constants.PendTime))) {
			String beginTime = taskNodeMap.getString(Constants.PbeginTime);
			String endTime = taskNodeMap.getString(Constants.PendTime);
			String priceTime = taskNodeMap.getString(Constants.PpriceTime);
			String startTime = beginTime;
			if (ptype.equals(Constants.LLimit)) {
				if (StrUtils.isNotNull(priceTime)
						&& DateUtils.getDate(priceTime).getTime() >= DateUtils
								.getDate(beginTime).getTime()) {
					startTime = priceTime;
				}
			}
			target.put("restTime",
					DateUtils.sub(sysDate, DateUtils.getDate(endTime)));
			target.put(Constants.PbeginTime + "Str", beginTime);
			target.put(Constants.PendTime + "Str", endTime);
			target.put("stdTime", DateUtils.getStr(sysDate));// 服务器时间
		} else {
			target.put("stdTime", DateUtils.getStr(sysDate));// 服务器时间
			target.put("restTime", null);
		}

		ParaMap offer = tradeDao.getBidList(targetId, count, pageIndex);

		out.put("target", target);
		out.put("list", offer);

		return out;

	}
	
	public ParaMap cantonList(ParaMap in)throws Exception{
		ParaMap out = new ParaMap();
		StringBuffer customCondition = new StringBuffer("");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("cantonId", "370100");
		sqlParams.put("moduleNo", "trans_portal");
		sqlParams.put("dataSetNo", "get_portal_canton_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);	
		List<ParaMap> cantonList = result.getListObj();
		out.put("cantonList", cantonList);
		return out;
	}

	public ParaMap now(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		out.put("now", DateUtils.nowStr());
		return out;
	}
	
}

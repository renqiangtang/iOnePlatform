package com.tradeland.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.sysman.service.UserService;
import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.tradeland.engine.EngineDao;
import com.tradeland.engine.LandEngine;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.utils.StreamUtils;
import com.base.web.ContentTypes;
import com.tradeland.dao.TradeDao;

public class TradeService extends BaseService {

	Logger log = Logger.getLogger(this.getClass());

	public static Hashtable<String, String> allTargetMap = new Hashtable<String, String>();
	public static Hashtable<String, String> targetInfoMap = new Hashtable<String, String>();
	public static Hashtable<String, BigDecimal> targetPriceMap = new Hashtable<String, BigDecimal>();
	TradeDao dao = new TradeDao();

	public static void invalidateAllTargetMap() {
		allTargetMap = new Hashtable<String, String>();
	}

	public static void invalidateTargetInfoMap() {
		targetInfoMap = new Hashtable<String, String>();
	}

	public static void removeTargetPriceMap(String key) {
		targetPriceMap.remove(key);
	}

	/**
	 * 得到我所有的标的
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargets(ParaMap inMap) throws Exception {
		long startMillisecond = (DateUtils.now()).getTime();
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		String goodsType = inMap.getString("goodsType");
		ParaMap out = dao.getTargets(userId, targetId , goodsType);
		out.put("stdTime", DateUtils.getStr(DateUtils.now()));
		long endMillisecond = (DateUtils.now()).getTime();
		long diffMillisecond = endMillisecond - startMillisecond;
		if (diffMillisecond <= 1000) {
			log.debug("getTargets:" + userId + "****：" + diffMillisecond+ "(毫秒)");
		} else {
			log.debug("getTargets:" + userId + "@@@@@：" + diffMillisecond+ "(毫秒)");
		}
		return out;
	}

	/**
	 * 得到我所有的标的
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargetsForMonitor(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String goodsType =  inMap.getString("goodsType");
		String menuId =  inMap.getString("menuId");
//		if (StrUtils.isNotNull(userId) && StrUtils.isNotNull(menuId)) {
//			String menuMainMenuId = dao.getMenuMainMenuId(menuId);
//			String userMainMenuId = dao.getUserMainMenuId(userId);
//			if(StrUtils.isNotNull(menuMainMenuId) && StrUtils.isNotNull(userMainMenuId) && userMainMenuId.equals(menuMainMenuId)){
				out = dao.getTargetsForMonitor(goodsType);
//			}
//		}
		return out;
	}

	/**
	 * 得到竞价信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTradeInfo(ParaMap inMap) throws Exception {
		// long startMillisecond = (DateUtils.now()).getTime();
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		// String ptype = inMap.getString("ptype");
		// String qtype = inMap.getString("qtype");
		int count = 5;
		if (inMap.get("count") != null) {
			count = inMap.getInt("count");
		}
//		String jsonTemp = targetInfoMap.get(targetId + "_" + userId);
//		String json = jsonTemp == null ? null : new String(jsonTemp);
//		if (StrUtils.isNull(json) || json.indexOf(":\"" + targetId + "\"") < 0) {// 重新取数据
			String json = dao.getTradeInfo(targetId, userId, count).toString();
			log.debug("========>取得新的标的数据:" + targetId + "_" + userId + ":"
					+ json);
			ParaMap out = this.restTime(json);
//			targetInfoMap.put(targetId + "_" + userId, json);
//		} else {
//			log.debug("========>取得内存中的标的数据:" + targetId + "_" + userId + ":"
//					+ json);
//		}
//		String newjson = new String(json);
//		ParaMap out = this.restTime(newjson);
		// long endMillisecond = (DateUtils.now()).getTime();
		// long diffMillisecond = endMillisecond - startMillisecond;
		// if(diffMillisecond<=1000){
		// log.debug("*****花费时间(毫秒)："+diffMillisecond);
		// }else{
		// log.debug("@@@@@");
		// log.debug("@@@@@花费时间(毫秒)："+diffMillisecond);
		// log.debug("@@@@@");
		// log.debug("");
		// log.debug("");
		// log.debug("");
		// }
		// ParaMap out = new ParaMap();
		// String json = dao.getTradeInfo(targetId, userId, count).toString();
		// out.put("jsonStr", json);
		return out;
	}

	/**
	 * 出价
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap savePrice(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		String priceStr = inMap.getString("price");
		if(priceStr.indexOf(".")>=0){
			String subPrice = priceStr.substring(priceStr.indexOf(".")+1);
			if(subPrice.length()>4){
				out.put("state", 0);
				out.put("message", "出价失败！价格不能超过四位小数。");
				return out;
			}
			String aheadPrice = priceStr.substring(0,priceStr.indexOf("."));
			if(aheadPrice.length()>15){
				out.put("state", 0);
				out.put("message", "出价失败！价格不能超过百万亿。");
				return out;
			}
		}
		BigDecimal price = inMap.getBigDecimal("price");
		BigDecimal step = inMap.getBigDecimal("step");
		int sign = step.compareTo(new BigDecimal("0"));
		if (targetPriceMap.get(targetId) == null) {
			targetPriceMap.put(targetId, price);
		} else {
			BigDecimal maxPrice = targetPriceMap.get(targetId);
			if (price.compareTo(maxPrice) != sign) {
				out.put("message", "出价失败!");
				return out;
			} else {
				targetPriceMap.put(targetId, price);
			}
		}
		try{
			out = Engine.getLandEngine().commit(Constants.Action_Manual, inMap);
		}catch(Exception e){
			removeTargetPriceMap(targetId);
			throw new Exception("出价失败！");
		}
		removeTargetPriceMap(targetId);
		invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeService.savePrice()");
		return out;
	}

	/**
	 * 是否同意进入下一轮
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap agreeEnter(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		String userid = inMap.getString("u");
		Integer agreeType = inMap.getInt("agreeType");
		UserService us = new UserService();
		ParaMap pMap = new ParaMap();
		pMap.put("goodsType", "101");
		pMap.put("u", userid);
		pMap = us.checkBlackUser(pMap);
		if(pMap.containsKey("state") && pMap.getInt("state")!=1 && agreeType.intValue() == 1){
			out.put("state", 0);
			out.put("message", "操作失败!您已被列入黑名单，不能进入下一指标竞价");
			return out;
		}
		out = Engine.getLandEngine().commit(Constants.Action_Opinion,
				inMap);
		
		invalidateTargetInfoMap();
		removeTargetPriceMap(targetId);
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeService.agreeEnter()");
		return out;

	}

	/**
	 * 结束交易
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap doEnd(ParaMap inMap) throws Exception {
		ParaMap out = Engine.getLandEngine().commit(
				Constants.Action_EndProcess, inMap);
		invalidateTargetInfoMap();
		log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeService.doEnd()");
		return out;

	}

	/**
	 * 启动流程引擎
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap startEngine(ParaMap inMap) throws Exception {
		Engine.getLandEngine().commit(Constants.Action_StartEngine, null);
		ParaMap out = new ParaMap();
		out.put("result", "success");
		return out;
	}

	/**
	 * 活动的流程
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap activeProccess(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNull(sortField) || StrUtils.isNull(sortDir)
				|| "undefined".equals(sortField) || "undefined".equals(sortDir)) {
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "id desc");
		} else {
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField + " " + sortDir);
		}
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");

		sqlParams.put("state", Constants.ProcessEnd);

		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));

		TradeDao tradeDao = new TradeDao();
		ParaMap out = tradeDao.getNotEndProcess(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("templateid", "templateid");
		fields.put("tasknodeid", "tasknodeid");
		fields.put("state", "state");
		fields.put("mome", "mome");
		fields.put("targetname", "targetname");
		fields.put("phase", "phase");
		fields.put("task", "task");

		LandEngine tradeEngine = new LandEngine();
		List items = new ArrayList();
		for (int i = 0; i < out.getRecords().size(); i++) {
			ParaMap item = new ParaMap();
			Iterator it = fields.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = fields.getString(key);
				if (!value.equals("task")) {
					value = String.valueOf(out.getRecordValue(i, key));
				} else {
					String processId = String.valueOf(out.getRecordValue(i,
							"id"));
					value = tradeEngine.getIsHaveTimer(processId);
				}
				item.put(key, value);
			}
			items.add(item);
		}

		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap getEngineState(ParaMap inMap) {
		ParaMap out = new ParaMap();
		LandEngine tradeEngine = new LandEngine();
		boolean started = tradeEngine.getEngineState();
		out.put("started", started);
		return out;
	}

	/**
	 * 修改剩余时间和滚动条信息
	 * 
	 * @param json
	 * @return
	 */
	public ParaMap restTime(String json) {
		ParaMap out = new ParaMap();
		Date sysDate = DateUtils.now();
		String beginTime = null;
		String endTime = null;
		String ptype = null;
		String priceTime = null;
		String restTime = null;
		String process = null;
		String stdTime = null;
		String status = null;
		String stdMillisecond = null;
		String restMillisecond = null;
		Pattern pattern = Pattern
				.compile("\"status\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			status = matcher.group(1);
		}
		if (status != null && (status.equals("5") || status.equals("6"))) {
			out.put("jsonStr", json);
			return out;
		}
		pattern = Pattern
				.compile("\"beginTimeStr\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			beginTime = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"endTimeStr\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			endTime = matcher.group(1);
		}
		if (StrUtils.isNull(beginTime) || StrUtils.isNull(endTime)) {
			invalidateTargetInfoMap();
			log.debug("clear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^TradeService.restTime()");
			out.put("jsonStr", json);
			return out;

		}

		pattern = Pattern
				.compile("\"ptype\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			ptype = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"priceTimeStr\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			priceTime = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"restTime\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			restTime = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"process\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			process = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"stdTime\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			stdTime = matcher.group(1);
		}
		pattern = Pattern
				.compile("\"stdMillisecond\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			stdMillisecond = matcher.group(1);
		}
		
		pattern = Pattern.compile("\"restMillisecond\":[\"]?([^\\,^\\,^\"}]+)[\",\\,\\}]?");
		matcher = pattern.matcher(json);
		if (matcher.find()) {
			restMillisecond = matcher.group(1);
		}

		String startTime = beginTime;
		if (ptype.equals(Constants.LLimit)) {
			if (StrUtils.isNotNull(priceTime)
					&& DateUtils.getDate(priceTime).getTime() >= DateUtils
							.getDate(beginTime).getTime()) {
				startTime = priceTime;
			}
		}
		String restTimeNew = DateUtils.sub(sysDate, DateUtils.getDate(endTime));
		if (restTimeNew.indexOf("-") >= 0) {
			restTimeNew = "正在处理中...";
		}
		int processNew = DateUtils.getProcess(DateUtils.getDate(startTime),
				DateUtils.getDate(endTime));
		String stdTimeNew = DateUtils.getStr(sysDate);
		String stdMillisecondNew = sysDate.getTime() + "";
		String restMillisecondNew = String.valueOf(DateUtils.getDate(endTime).getTime() - sysDate.getTime() );
		String oldRestTimeStr = "\"restTime\":\"" + restTime + "\"";
		String newRestTimeStr = "\"restTime\":\"" + restTimeNew + "\"";
		String oldProcess = "\"process\":" + process;
		String newProcess = "\"process\":" + processNew;
		String oldStdTime = "\"stdTime\":\"" + stdTime + "\"";
		String newStdTime = "\"stdTime\":\"" + stdTimeNew + "\"";
		String oldStdMillisecond = "\"stdMillisecond\":\"" + stdMillisecond
				+ "\"";
		String newStdMillisecond = "\"stdMillisecond\":\"" + stdMillisecondNew
				+ "\"";
		String oldRestMillisecond = "\"restMillisecond\":" + restMillisecond;
		String newRestMillisecond = "\"restMillisecond\":" + restMillisecondNew; 
		json = json.replace(oldRestTimeStr, newRestTimeStr);
		json = json.replace(oldProcess, newProcess);
		json = json.replace(oldStdTime, newStdTime);
		json = json.replace(oldStdMillisecond, newStdMillisecond);
		json = json.replace(oldRestMillisecond, newRestMillisecond);
		out.put("jsonStr", json);
		return out;
	}

	/**
	 * 下载交易记录
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] downLoadOfferLog(ParaMap inMap) throws Exception {
		String licenseId = inMap.getString("licenseId");
		String type = ContentTypes.getContentType("lic");
		this.getResponse().reset();
		this.getResponse().setContentType(type + "; charset=UTF-8");
		String bidderName = new String(inMap.getString("bidderName").getBytes(
				"ISO8859-1"), "UTF-8");
		String targetName = new String(inMap.getString("targetName").getBytes(
				"ISO8859-1"), "UTF-8");
		String downLoadName = bidderName + "[" + targetName + "]交易记录.lic";
		downLoadName = new String(downLoadName.getBytes("gb2312"), "ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downLoadName + "\";");
		String content = dao.downLoadEnOfferLog(licenseId);
		return content.getBytes();
	}

	/**
	 * 获取文件内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public ParaMap getContent(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		FileItem item = inMap.getFileItem("file_Item");
		byte[] buf = item.get();
		String content = StreamUtils.byteToString(buf);
		Boolean encode = inMap.getBoolean("encode");
		if (true == encode)
			content = content.replaceAll("\n", "MMnnMM");
		out.put("jsonStr", content);
		return out;

	}

	/**
	 * 接收标的xml
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String dealTargetXml(ParaMap inMap) throws Exception {
		String xml = inMap.getString("xml");
		EngineDao engineDao = new EngineDao();
		engineDao.importTemplate(xml);
		return "succes";
	}

}

package com.before.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.JsonUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.ConfigDao;
import com.sysman.dao.FieldChangeDao;
import com.sysman.dao.UserDao;
import com.sysman.service.UserService;

/**
 * 指标委托Dao
 * @author SAMONHUA
 * 2013-01-08
 */
public class PlowTrustDao extends BaseDao {
	private static String tradeRuleConfigQ01FirstWaitTime = null;
	private static String tradeRuleConfigQ01LimitWaitTime = null;
	private static String tradeRuleConfigQ04FirstWaitTime = null;
	
	public ParaMap getTrustListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTrustListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getTrustData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTrustData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getTrustOwnerData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTrustOwnerData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("trust_id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateTrustData(ParaMap trustData) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		String businessType = trustData.containsKey("business_type") ? trustData.getString("business_type") : "501005";
		String userId = trustData.getString("u");
		String id = trustData.getString("id");
		boolean blnNewTrust = StrUtils.isNull(id);
		String trustNo = trustData.getString("no");
		
		UserService us=new UserService();
		ParaMap blackMap=new ParaMap();
		blackMap.put("goodsType", "501");
		blackMap.put("is_company", "-1");
		blackMap.put("name", trustData.get("bidder_name"));
		ParaMap isblackMap=us.checkBlackUserByName(blackMap);
		if(isblackMap.getInt("state")!=1){
			throw new Exception("保存失败!您选择的委托人："+trustData.get("bidder_name")+" 在黑名单中,此处不能够选作委托人！");
		}
		if (StrUtils.isNull(trustNo)) {
			if (blnNewTrust) {
				ParaMap newNoData = getNewTrustNo(null, null, businessType);
				if (newNoData.getInt("state") == 1)
					trustNo = newNoData.getString("no");
			}
		}
		if (StrUtils.isNotNull(trustNo)) {
			data.clear();
			data.put("id", id);
			data.put("business_type", businessType);
			data.put("no", trustNo);
			data = checkTrustExists(data);
			if (data.getInt("state") == 1) {
				if (data.getRecordInteger(0, "row_count") > 0)
					throw new Exception("委托编号“" + trustNo + "”重复，操作取消。");
			} else
				throw new Exception("检查委托编号是否重复失败，操作取消。");
		}
		//记录审核日志
		if (StrUtils.isNotNull(id) && trustData.containsKey("operate")) {
			String operate = trustData.getString("operate");
			if (StrUtils.isNotNull(operate) && operate.equals("confirm")) {
				FieldChangeDao fieldChangeDao = new FieldChangeDao();
				ParaMap change = new ParaMap();
				change.put("ref_table_name", "trans_trust");
				change.put("ref_id", id);
				change.put("field_name", "confirmed");
				change.put("new_value", trustData.getInt("confirmed"));
				change.put("change_cause", trustData.getString("confirmed_opinion"));
				fieldChangeDao.updateFieldChange(change);
			}
		}
		//新增/更新委托
		data.clear();
		data.put("id", id);
		if (trustData.containsKey("business_type"))
			data.put("business_type", businessType);
		if (trustData.containsKey("status"))
			data.put("status", trustData.getInt("status"));
		if (StrUtils.isNotNull(trustNo))
			data.put("no", trustNo);
		if (trustData.containsKey("explain"))
			data.put("explain", trustData.getString("explain"));
		if (trustData.containsKey("trust_date"))
			data.put("trust_date", trustData.getString("trust_date"));
		if (trustData.containsKey("confirmed")) {
			data.put("confirmed", trustData.getString("confirmed"));
			if (trustData.containsKey("confirmed_opinion"))
				data.put("confirmed_opinion", trustData.getString("confirmed_opinion"));
			if (trustData.containsKey("confirmed_date"))
				data.put("confirmed_date", trustData.getString("confirmed_date"));
			else
				data.put("confirmed_date", StrUtils.dateToString(new Date()));
		}
		if (trustData.containsKey("create_type"))
			data.put("create_type", trustData.getInt("create_type"));
		if (StrUtils.isNull(id)) {
			data.put("create_user_id", userId);
		}
		ParaMap format = new ParaMap();
		format.put("trust_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		format.put("confirmed_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_trust", "id", data, format);
		if (result.getInt("state") == 1) {
			if (StrUtils.isNull(id))
				id = result.getString("id");
		} else
			throw new Exception("保存委托数据失败，操作取消。");
		//保存委托与委托人关联
		data.clear();
		String trustBidderRelId = trustData.getString("trust_bidder_rel_id");
		data.put("id", trustBidderRelId);
		data.put("trust_id", id);
		data.put("bidder_id", trustData.getString("bidder_id"));
		data.put("percent", 100);
		data.put("contact", trustData.getString("trust_contact"));
		if (StrUtils.isNull(trustBidderRelId)) {
			data.put("create_user_id", userId);
		}
		result = dataSetDao.updateData("trans_trust_bidder_rel", "id", data);
		if (result.getInt("state") == 1) {
			if (StrUtils.isNull(trustBidderRelId))
				trustBidderRelId = result.getString("id");
		} else
			throw new Exception("保存委托人数据失败，操作取消。");
		//新增/更新标的
		String deleteTargetIds = trustData.getString("deleteTargetIds");
		if (StrUtils.isNotNull(deleteTargetIds)) {
			List deleteTargetIdList = StrUtils.getSubStrs(deleteTargetIds, ";");
			if (deleteTargetIdList.size() > 0) {
				data.clear();
				data.put("id", deleteTargetIdList);
				result = dataSetDao.deleteSimpleData("trans_target", data);
				if (result.getInt("state") != 1)
					throw new Exception("删除标的数据失败，操作取消。");
			}
		}
		String addTargets = trustData.getString("addTargets");
		UserDao ud=new UserDao();
		if (StrUtils.isNotNull(addTargets)) {
			//查询交易方式
			ConfigDao configDao = new ConfigDao();
			ParaMap businessTypesConfig = ParaMap.fromString(configDao.getBusinessType(6));
			ParaMap businessTypeConfig = businessTypesConfig.containsKey(businessType) ? (ParaMap) businessTypesConfig.get(businessType) : null;
			ParaMap transTypeConfig = null;
			if (businessTypeConfig != null) {
				Iterator it = businessTypeConfig.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					Object value = businessTypeConfig.get(key);
					if (value instanceof Map) {
						transTypeConfig = (ParaMap) value;
						break;
					}
				}
			}
			List addTargetList = JsonUtils.StrToList(addTargets);
			for (int i = 0 ; i < addTargetList.size(); i++) {
				Map target = (Map) addTargetList.get(i);
				String targetId = String.valueOf(target.get("id"));
				String targetNo = String.valueOf(target.get("no"));
				//检查标的是否存在，目前要求标的编号全局唯一
				if (StrUtils.isNotNull(targetNo)) {
					data.clear();
					data.put("id", id);
					data.put("target_id", targetId);
					data.put("target_no", targetNo);
					data.put("business_type", businessType);
					data = checkTrustTargetExists(data);
					if (data.getInt("state") == 1 && data.getRecordInteger(0, "row_count") > 0) {
						throw new Exception("标的(" + targetNo + ")编辑重复，操作取消。");
					}
				} else {
					int targetIndex = i + 1;
					if (targetIndex >= 1000)
						targetNo = trustNo + targetIndex;
					else
						targetNo = trustNo + StrUtils.getRightString("000" + targetIndex, 3);
				}
				data.clear();
				data.put("id", targetId);
				data.put("business_type", businessType);
				data.put("no", targetNo);
				
				data.put("reserve_price", target.get("reserve_price"));
				data.put("begin_price", target.get("begin_price"));
				data.put("price_step", target.get("price_step"));
				data.put("earnest_money", target.get("earnest_money"));
				data.put("is_online",target.get("is_online"));
				data.put("organ_id", target.get("organ_id"));
				data.put("trans_organ_id",target.get("organ_id"));
				if (target.containsKey("final_price") && businessType.equals("501006"))
					data.put("final_price", target.get("final_price"));
				data.put("unit", "元");
				//data.put("final_price", target.get("final_price"));
				//交易方式
				if (transTypeConfig != null) {
					data.put("trans_type", transTypeConfig.getInt("trans_type"));
					data.put("is_net_trans", transTypeConfig.getInt("is_net_trans"));
					data.put("trans_type_id", transTypeConfig.getString("id"));
					data.put("trans_type_name", transTypeConfig.getString("trans_type_name"));
					data.put("trans_type_label", transTypeConfig.getString("name"));
				} else {
					data.put("trans_type", 1);
					data.put("is_net_trans", 1);
					data.put("trans_type_id", null);
					data.put("trans_type_name", null);
					data.put("trans_type_label", null);
				}
				data.put("remark", target.get("remark"));
				if (StrUtils.isNull(targetId)) {//标的新增
					data.put("create_user_id", trustData.get("u"));
				}
				result = dataSetDao.updateData("trans_target", "id", data);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(targetId))
						targetId = result.getString("id");
				} else
					throw new Exception("保存标的(" + target.get("no") + ")数据失败，操作取消。");
				String goodsId = String.valueOf(target.get("goods_id"));
				data.clear();
				data.put("id", goodsId);
				data.put("trust_id", id);
				data.put("area", target.get("area"));
				data.put("canton_id", target.get("canton_id"));
				if (StrUtils.isNull(goodsId)) {
					data.put("create_user_id", target.get("u"));
				}
				result = dataSetDao.updateData("trans_goods", "id", data);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(goodsId))
						goodsId = result.getString("id");
				} else
					throw new Exception("保存标的(" + target.get("no") + ")相关数据失败，操作取消。");
				String targetGoodsRelId = String.valueOf(target.get("target_goods_rel_id"));
				data.clear();
				data.put("id", targetGoodsRelId);
				data.put("target_id", targetId);
				data.put("goods_id", goodsId);
				data.put("is_main_goods", 1);
				data.put("turn", i);
				if (StrUtils.isNull(targetGoodsRelId)) {
					data.put("create_user_id", target.get("u"));
				}
				result = dataSetDao.updateData("trans_target_goods_rel", "id", data);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(goodsId))
						targetGoodsRelId = result.getString("id");
				} else
					throw new Exception("保存标的(" + target.get("no") + ")相关关联数据失败，操作取消。");
				//多指标在系统设置中设置多指标无效，由此处直接固定写入
				getTradeRuleXMLWaitTimeConfig();
				String targetMultiTradeId1 = String.valueOf(target.get("target_multi_trade_id1"));
				data.clear();
				data.put("id", targetMultiTradeId1);
				data.put("target_id", targetId);
				data.put("init_value", target.get("begin_price"));
				if (target.containsKey("final_price") && businessType.equals("501006"))
					data.put("final_value", target.get("final_price"));
				data.put("step", target.get("price_step"));
				data.put("enter_flag", "0#1#0");
				data.put("turn", 1);
				//if (StrUtils.isNull(targetMultiTradeId1)) {
					data.put("name", "价格指标");
					data.put("type", "Q01");
					data.put("ptype", "L03");
					data.put("unit", "元");
					data.put("first_wait", StrUtils.strToInt(tradeRuleConfigQ01FirstWaitTime, 480));
					data.put("limit_wait", StrUtils.strToInt(tradeRuleConfigQ01LimitWaitTime, 300));
				//}
				result = dataSetDao.updateData("trans_target_multi_trade", "id", data);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(goodsId))
						targetMultiTradeId1 = result.getString("id");
				} else
					throw new Exception("保存标的(" + target.get("no") + ")交易信息失败，操作取消。");
				String targetMultiTradeId2 = String.valueOf(target.get("target_multi_trade_id2"));
				data.clear();
				data.put("id", targetMultiTradeId2);
				data.put("target_id", targetId);
				data.put("init_value", target.get("begin_price"));
				data.put("final_value", target.get("final_price"));
				data.put("step", target.get("price_step"));
				data.put("enter_flag", "0#2#0");
				data.put("turn", 2);
				//if (StrUtils.isNull(targetMultiTradeId2)) {
					data.put("name", "摇号指标");
					data.put("type", "Q04");
					data.put("ptype", "L04");
					data.put("unit", "元");
					data.put("first_wait", StrUtils.strToInt(tradeRuleConfigQ04FirstWaitTime, 480));
				//}
				result = dataSetDao.updateData("trans_target_multi_trade", "id", data);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(goodsId))
						targetMultiTradeId2 = result.getString("id");
				} else
					throw new Exception("保存标的(" + target.get("no") + ")摇号信息失败，操作取消。");
	        }
		}
		//检查是否是审核操作
		if (trustData.containsKey("operate")) {
			String operate = trustData.getString("operate");
			if (StrUtils.isNotNull(operate)) {
				data.clear();
				data.put("moduleNo", "trans_trust");
				data.put("dataSetNo", "updateTargetStatus");
				data.put("id", id);
				if (operate.equals("submitConfirm")) {
					data.put("target_status", 1);
				} else if (operate.equals("confirm")) {
					int trustStatus = trustData.getInt("status");
					int trustConfirmed = trustData.getInt("confirmed");
					if (trustStatus == 1)
						data.put("target_status", 1);
					else if (trustStatus == 2) {
						if (trustConfirmed == 1)
							data.put("target_status", 2);
						else
							data.put("target_status", 1);
					} else if (trustStatus == 6) //审核不通过返回委托人录件修改
						data.put("target_status", 0);
				}
				result = dataSetDao.executeSQL(data);
				if (result.getInt("state") != 1)
					throw new Exception("修改标的审核状态失败，操作取消。");
			}
		}
		result.clear();
		result.put("state", 1);
		result.put("id", id);
		if (blnNewTrust && StrUtils.isNotNull(trustNo))
			result.put("no", trustNo);
		result.put("trust_bidder_rel_id", trustBidderRelId);
		return result;
	}
	
	public ParaMap deleteTrustData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteTrustData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	public ParaMap getTrustTargetData(String moduleId, String dataSetNo, String id, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTrustTargetData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		if (StrUtils.isNull(targetId))
			sqlParams.put("target_id", null);
		else
			sqlParams.put("target_id", targetId);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getTrustTargetData(String moduleId, String dataSetNo, String id) throws Exception {
		return getTrustTargetData(moduleId, dataSetNo, id);
	}
	
	/**
	 * 检查委托是否重复
	 * 目前不作为保存的决定条件，主要是提示经办人
	 * 暂时仅检查委托案号
	 * @param trustData 委托需要检查的内容
	 * @return
	 * @throws Exception
	 */
	public ParaMap checkTrustExists(ParaMap trustData) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trust");
		sqlParams.put("dataSetNo", "queryTrustExists");
		String id = trustData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(id)) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
//		String businessType = trustData.containsKey("business_type") ? trustData.getString("business_type") : "501501";
//		sqlParams.put("business_type", businessType);
//		customCondition.append(" and business_type = :business_type");
		String no = trustData.getString("no");
		sqlParams.put("no", no);
		customCondition.append(" and no = :no");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap checkTrustTargetExists(ParaMap trustData) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trust");
		sqlParams.put("dataSetNo", "queryTrustTargetExists");
		String id = trustData.getString("id");
		String targetId = trustData.getString("target_id");
		StringBuffer customCondition = new StringBuffer("");
//		if (StrUtils.isNotNull(id)) {
//			sqlParams.put("id", id);
//			customCondition.append(" and tr.id <> :id");
//		}
		if (StrUtils.isNotNull(targetId)) {
			sqlParams.put("target_id", targetId);
			customCondition.append(" and t.id <> :target_id");
		}
		String businessType = trustData.containsKey("business_type") ? trustData.getString("business_type") : "501005";
		sqlParams.put("business_type", businessType);
		customCondition.append(" and t.business_type = :business_type");
		String targetNo = trustData.getString("target_no");
		sqlParams.put("target_no", targetNo);
		customCondition.append(" and t.no = :target_no");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	//为新的委托生成委托编号
	public ParaMap getNewTrustNo(String moduleId, String dataSetNo, String businessType) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_trust");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewTrustNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("business_type", businessType);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, "new_no");
			result.clear();
			result.put("state", 1);
			result.put("no", newNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的委托编号失败");
		}
		return result;
	}
	
	public ParaMap getTradeRuleXMLWaitTimeConfig() throws Exception {
		if (tradeRuleConfigQ01FirstWaitTime == null && tradeRuleConfigQ01LimitWaitTime == null && tradeRuleConfigQ04FirstWaitTime == null) {
			InputStream is = this.getClass().getResourceAsStream("/com/tradeplow/engine/TradeRule.xml");
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			int intReadNode = 0;
			Document document =  DocumentHelper.parseText(buffer.toString());
			Element root = document.getRootElement();
			List nodes = root.elements();
			if (nodes.size() > 0) {
				for(int i = 0; i < nodes.size(); i++) {
					Element node = (Element) nodes.get(i);
					String nodeName = node.getName();
					if (StrUtils.isNotNull(nodeName) && nodeName.equals("Quota")) {
						String nodeId = node.attributeValue("id");
						if (StrUtils.isNotNull(nodeId)) {
							if (nodeId.equals("Q01")) {
								tradeRuleConfigQ01FirstWaitTime = node.attributeValue("firstWait");
								tradeRuleConfigQ01LimitWaitTime = node.attributeValue("limitWait");
								intReadNode++;
							} else if (nodeId.equals("Q04")) {
								tradeRuleConfigQ04FirstWaitTime = node.attributeValue("firstWait");
								intReadNode++;
							}
							if (intReadNode >= 2)
								break;
						}
					}
				}
			}
		}
		ParaMap result = new ParaMap();
		result.put("tradeRuleConfigQ01FirstWaitTime", tradeRuleConfigQ01FirstWaitTime);
		result.put("tradeRuleConfigQ01LimitWaitTime", tradeRuleConfigQ01LimitWaitTime);
		result.put("tradeRuleConfigQ04FirstWaitTime", tradeRuleConfigQ04FirstWaitTime);
		return result;
	}
}

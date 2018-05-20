package com.before.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.PlowTrustDao;
import com.trademan.dao.BidderDao;

/**
 * 指标委托Service
 * @author SAMONHUA
 * 2013-01-08
 */
public class PlowTrustService extends BaseService {
	public ParaMap getTrustListData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			if (StrUtils.isNotNull(sortDir))
				sortField = sortField + " " + sortDir;
		}
		String userId = inMap.getString("u");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				sqlParams.put("business_type", businessType);
				customCondition.append(" and t.business_type = :business_type");
			}
		}
		if (inMap.containsKey("operateMode")) {
			String operateMode = inMap.getString("operateMode");
			int intOperateMode = StrUtils.strToInt(operateMode, -1);
			if (intOperateMode == 0) {//只能查看竞买人自己的委托
				sqlParams.put("user_id", userId);
				//customCondition.append(" and t.create_user_id = :user_id");
				customCondition.append(" and exists(select * from trans_trust_bidder_rel where trust_id = t.id " +
						" and exists(select * from sys_user where id = :user_id and ref_id = trans_trust_bidder_rel.bidder_id))");
			} else if (intOperateMode == 1) {//内部工作人员审核
				customCondition.append(" and t.status >= 1 and t.status <> 6");
			} else if (intOperateMode == 2) {//内部工作人员代录及审核
				customCondition.append(" and ((t.status >= 1 and t.status <> 6) or t.create_type = 1)");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and t.no like '%' || :no || '%'");
			}
		}
		if (inMap.containsKey("explain")) {
			String explain = inMap.getString("explain");
			if (StrUtils.isNotNull(explain)) {
				sqlParams.put("explain", explain);
				customCondition.append(" and t.explain like '%' || :explain || '%'");
			}
		}
		if (inMap.containsKey("bidderName") || inMap.containsKey("contact")) {
			String bidderName = inMap.getString("bidderName");
			String contact = inMap.getString("contact");
			if (StrUtils.isNotNull(bidderName)) {
				customCondition.append(" and exists(select * from trans_trust_bidder_rel where trust_id = t.id ");
				if (StrUtils.isNotNull(bidderName)) {
					sqlParams.put("bidder_name", bidderName);
					customCondition.append(" and exists(select * from trans_bidder where id = trans_trust_bidder_rel.bidder_id and name like '%' || :bidder_name || '%')");
				}
				customCondition.append(") ");
			}
			if (StrUtils.isNotNull(contact)) {
				sqlParams.put("contact", contact);
				customCondition.append(" and b.contact like '%' || :contact || '%'");
			}
		}
		if (inMap.containsKey("trustDateBegin")) {
			String trustDateBegin = inMap.getString("trustDateBegin");
			if (StrUtils.isNotNull(trustDateBegin)) {
				sqlParams.put("trust_date_begin", trustDateBegin);
				customCondition.append(" and to_char(t.trust_date, 'yyyy-mm-dd') >= :trust_date_begin ");
			}
		}
		if (inMap.containsKey("trustDateEnd")) {
			String trustDateEnd = inMap.getString("trustDateEnd");
			if (StrUtils.isNotNull(trustDateEnd)) {
				sqlParams.put("trust_date_end", trustDateEnd);
				customCondition.append(" and to_char(t.trust_date, 'yyyy-mm-dd') <= :trust_date_end ");
			}
		}
		if (inMap.containsKey("includeNotNoticeTarget") || inMap.containsKey("targetNo")) {
			String includeNotNoticeTarget = inMap.getString("includeNotNoticeTarget");
			String targetNo = inMap.getString("targetNo");
			if ((StrUtils.isNotNull(includeNotNoticeTarget) && includeNotNoticeTarget.equals("1"))
					|| StrUtils.isNotNull(targetNo)) {
				customCondition.append(" and exists(select * from trans_goods where trust_id = t.id");
				customCondition.append("   and exists(select * from trans_target_goods_rel where goods_id = trans_goods.id");
				if (StrUtils.isNotNull(includeNotNoticeTarget) && includeNotNoticeTarget.equals("1"))
					customCondition.append(" and not exists(select * from trans_notice_target_rel where target_id = trans_target_goods_rel.target_id)");
				if (StrUtils.isNotNull(targetNo)) {
					sqlParams.put("target_no", targetNo);
					customCondition.append(" and exists(select * from trans_target where id = trans_target_goods_rel.target_id and no like '%' || :target_no || '%')");
				}
				customCondition.append("))");
			}
		}
		if (inMap.containsKey("excludeId")) {
			String excludeId = inMap.getString("excludeId");
			if (StrUtils.isNotNull(excludeId)) {
				List<String> excludeIds = StrUtils.getSubStrs(excludeId, ",");
				if (excludeIds.size() > 0) {
					customCondition.append(" and t.id not in (");
					for(int i = 0; i < excludeIds.size(); i++) {
						sqlParams.put("exclude_id" + i, excludeIds.get(i));
						customCondition.append(":exclude_id" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
				
			}
		}
		if (inMap.containsKey("excludeNo")) {
			String excludeNo = inMap.getString("excludeNo");
			if (StrUtils.isNotNull(excludeNo)) {
				List<String> excludeNos = StrUtils.getSubStrs(excludeNo, ",");
				if (excludeNos.size() > 0) {
					customCondition.append(" and t.no not in (");
					for(int i = 0; i < excludeNos.size(); i++) {
						sqlParams.put("exclude_no" + i, excludeNos.get(i));
						customCondition.append(":exclude_no" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
				
			}
		}
		if (inMap.containsKey("staticStatus")) {
			String staticStatus = inMap.getString("staticStatus");
			if (StrUtils.isNotNull(staticStatus)) {
				List<String> staticStatuList = StrUtils.getSubStrs(staticStatus, ",");
				if (staticStatuList.size() > 0) {
					customCondition.append(" and t.status in (");
					for(int i = 0; i < staticStatuList.size(); i++) {
						sqlParams.put("static_status" + i, staticStatuList.get(i));
						customCondition.append(":static_status" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
				
			}
		}
		if (inMap.containsKey("staticConfirmed")) {
			String staticConfirmed = inMap.getString("staticConfirmed");
			if (StrUtils.isNotNull(staticConfirmed)) {
				List<String> staticConfirmedList = StrUtils.getSubStrs(staticConfirmed, ",");
				if (staticConfirmedList.size() > 0) {
					customCondition.append(" and t.confirmed in (");
					for(int i = 0; i < staticConfirmedList.size(); i++) {
						sqlParams.put("static_confirmed" + i, staticConfirmedList.get(i));
						customCondition.append(":static_confirmed" + i + ",");
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
				
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.getTrustListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getTrustData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.getTrustData(moduleId, dataSetNo, id);
		if (out.getInt("state") == 1 && out.getInt("totalRowCount") > 0) {
			//查询委托人的联系人列表
			if (inMap.containsKey("returnTrustBidderContacts")) {
				String bidderId = out.getRecordString(0, "bidder_id");
				if (StrUtils.isNotNull(bidderId)) {
					BidderDao bidderDao = new BidderDao();
					//ParaMap bidderContacts = bidderDao.getBidderContactData(null, null, bidderId);
//					if (bidderContacts != null && bidderContacts.size() > 0&&bidderContacts.getString("state").equals("1"))
//						out.put("bidderContacts", bidderContacts);
				}
			}
		}
		return out;
	}
	
	public ParaMap getTrustOwnerData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.getTrustOwnerData(moduleId, dataSetNo, id);
		//是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect) && (strDirect.equalsIgnoreCase("true") || strDirect.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap updateTrustData(ParaMap trustData) throws Exception {
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.updateTrustData(trustData);
		return out;
	}
	
	public ParaMap deleteTrustData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.deleteTrustData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap getTrustTargetData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		String targetId = inMap.getString("target_id");
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.getTrustTargetData(moduleId, dataSetNo, id, targetId);
		String parentCan=getParentCan(out.getRecordValue(0,"canton_id").toString(),out);//给区域添加父区域
		//是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect) && (strDirect.equalsIgnoreCase("true") || strDirect.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = MakeJSONData.makeItems(out);
//		ParaMap pa=(ParaMap)items.get(0);
//		pa.put("canton_name", parentCan);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getTradeRuleXMLWaitTimeConfig(ParaMap inMap) throws Exception {
		PlowTrustDao trustDao = new PlowTrustDao();
		ParaMap out = trustDao.getTradeRuleXMLWaitTimeConfig();
		return out;
	}
	
	public String getParentCan(String cantonid,ParaMap data) throws Exception{
		ParaMap keyData=new ParaMap();
		DataSetDao dataSetDao=new DataSetDao();
		String canton = "";
		if(StringUtils.isNotEmpty(cantonid)){
			String parentId = "";
			keyData.put("id", cantonid);
			ParaMap cantonMap = dataSetDao.querySimpleData("sys_canton",
					keyData);
			if (cantonMap.getSize() > 0) {
				List filds = cantonMap.getFields();
				for (int i = 0; i < cantonMap.getSize(); i++) {
					ParaMap out = new ParaMap();
					List record = (List) cantonMap.getRecords().get(i);
					if (filds != null && filds.size() > 0) {
						for (int j = 0; j < filds.size(); j++) {
							out.put((String) filds.get(j), record.get(j));
							if ("name".equals((String) filds.get(j))) {
								canton = (String) record.get(j);
							}
							if ("parent_id".equals((String) filds.get(j))) {
								parentId = (String) record.get(j);
							}
						}
					}
				}
			}
			if(!parentId.equals("440000")){
				keyData.clear();
				keyData.put("id", parentId);
				cantonMap = dataSetDao.querySimpleData("sys_canton",
						keyData);
				if (cantonMap.getSize() > 0) {
					List filds = cantonMap.getFields();
					for (int i = 0; i < cantonMap.getSize(); i++) {
						ParaMap out = new ParaMap();
						List record = (List) cantonMap.getRecords().get(i);
						if (filds != null && filds.size() > 0) {
							for (int j = 0; j < filds.size(); j++) {
								out.put((String) filds.get(j), record.get(j));
								if ("name".equals((String) filds.get(j))) {
									canton =  (String) record.get(j) + ">" + canton;
								}
							}
						}
						
					}
				}
			}
			
		}
		return canton;
	}
}

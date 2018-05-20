package com.before.service;

import java.util.ArrayList;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.PlowNoticeDao;
import com.before.dao.PlowTrustDao;
import com.sysman.dao.PlowExtendDao;

/**
 * 指标公告Service
 * 
 * @author SAMONHUA 2013-01-15
 */
public class PlowNoticeService extends BaseService {
	public ParaMap getNoticeListData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField)) {
			if (StrUtils.isNotNull(sortDir))
				sortField = sortField + " " + sortDir;
		}
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// 自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				sqlParams.put("business_type", businessType);
				customCondition.append(" and n.business_type = :business_type");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and n.no like '%' || :no || '%'");
			}
		}
		if (inMap.containsKey("noticeDateBegin")) {
			String noticeDateBegin = inMap.getString("noticeDateBegin");
			if (StrUtils.isNotNull(noticeDateBegin)) {
				sqlParams.put("notice_date_begin", noticeDateBegin);
				customCondition
						.append(" and to_char(n.notice_date, 'yyyy-mm-dd') >= :notice_date_begin");
			}
		}
		if (inMap.containsKey("noticeDateEnd")) {
			String noticeDateEnd = inMap.getString("noticeDateEnd");
			if (StrUtils.isNotNull(noticeDateEnd)) {
				sqlParams.put("notice_date_end", noticeDateEnd);
				customCondition
						.append(" and to_char(n.notice_date, 'yyyy-mm-dd') <= :notice_date_end");
			}
		}
		if (inMap.containsKey("status")) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				sqlParams.put("status", status);
				customCondition.append(" and n.status = :status");
			}
		}
		if (inMap.containsKey("targetNo")) {
			String targetNo = inMap.getString("targetNo");
			if (StrUtils.isNotNull(targetNo)) {
				sqlParams.put("target_no", targetNo);
				customCondition
						.append(" and exists(select * from trans_target where no like '%' || :target_no || '%' and exists(select * from trans_notice_target_rel where target_id = trans_target.id and notice_id = n.id))");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		// 查询数据
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getNoticeListData(moduleId, dataSetNo,
				sqlParams);
		// 是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect)
					&& (strDirect.equalsIgnoreCase("true") || strDirect
							.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap getNoticeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getNoticeData(moduleId, dataSetNo, id);
		if (out.getInt("state") == 1) {
			PlowTrustDao trustDao = new PlowTrustDao();
			ParaMap waitTimeConfigData = trustDao
					.getTradeRuleXMLWaitTimeConfig();
			PlowExtendDao extendDao = new PlowExtendDao();
			List extendNos = new ArrayList();
			extendNos.add("end_apply_time");
			extendNos.add("trans_type");
			extendNos.add("begin_focus_time");
			extendNos.add("end_focus_time");
			extendNos.add("begin_limit_time");
			extendNos.add("first_wait_time");
			extendNos.add("limit_wait_time");
			extendNos.add("random_wait_time");
			extendNos.add("is_online");
			ParaMap extendData = extendDao.getExtend("trans_notice", id,
					extendNos);
			if (extendData.getInt("state") == 1
					&& extendData.getInt("totalRowCount") > 0) {
				extendData = (ParaMap) extendData.get("data");
				if (extendData.containsKey("end_apply_time")) {
					ParaMap extend = (ParaMap) extendData.get("end_apply_time");
					out.put("end_apply_time", extend.get("field_value"));
				}
				if (extendData.containsKey("trans_type")) {
					ParaMap extend = (ParaMap) extendData.get("trans_type");
					out.put("trans_type", extend.get("field_value"));
				}
				if (extendData.containsKey("begin_focus_time")) {
					ParaMap extend = (ParaMap) extendData
							.get("begin_focus_time");
					out.put("begin_focus_time", extend.get("field_value"));
				}
				if (extendData.containsKey("end_focus_time")) {
					ParaMap extend = (ParaMap) extendData.get("end_focus_time");
					out.put("end_focus_time", extend.get("field_value"));
				}
				if (extendData.containsKey("begin_limit_time")) {
					ParaMap extend = (ParaMap) extendData
							.get("begin_limit_time");
					out.put("begin_limit_time", extend.get("field_value"));
				}
				if (extendData.containsKey("first_wait_time")) {
					ParaMap extend = (ParaMap) extendData
							.get("first_wait_time");
					out.put("first_wait_time", extend.get("field_value"));
				} else
					out.put("first_wait_time", waitTimeConfigData
							.get("tradeRuleConfigQ01FirstWaitTime"));
				if (extendData.containsKey("limit_wait_time")) {
					ParaMap extend = (ParaMap) extendData
							.get("limit_wait_time");
					out.put("limit_wait_time", extend.get("field_value"));
				} else
					out.put("limit_wait_time", waitTimeConfigData
							.get("tradeRuleConfigQ01LimitWaitTime"));
				if (extendData.containsKey("random_wait_time")) {
					ParaMap extend = (ParaMap) extendData
							.get("random_wait_time");
					out.put("random_wait_time", extend.get("field_value"));
				} else
					out.put("random_wait_time", waitTimeConfigData
							.get("tradeRuleConfigQ04FirstWaitTime"));

				if (extendData.containsKey("is_online")) {
					ParaMap extend = (ParaMap) extendData.get("is_online");
					out.put("is_online", extend.get("field_value"));
				}
			}
		}
		return out;
	}

	public ParaMap getNoticeTrustData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getNoticeTrustData(moduleId, dataSetNo, id);
		// 是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect)
					&& (strDirect.equalsIgnoreCase("true") || strDirect
							.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap getNoticeTargetData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getNoticeTargetData(moduleId, dataSetNo, id);
		// 是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect)
					&& (strDirect.equalsIgnoreCase("true") || strDirect
							.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap getNoticeTrustTargetData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		String trustId = inMap.getString("trustId");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getNoticeTrustTargetData(moduleId, dataSetNo,
				id, trustId);
		// 是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect)
					&& (strDirect.equalsIgnoreCase("true") || strDirect
							.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap updateNoticeData(ParaMap noticeData) throws Exception {
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.updateNoticeData(noticeData);
		return out;
	}

	public ParaMap deleteNoticeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.deleteNoticeData(moduleId, dataSetNo, id);
		return out;
	}

	public ParaMap getNewNoticeNo(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String businessType = inMap.getString("businessType");
		int intBusinessType = StrUtils.strToInt(businessType, 0);
		String noticeType = inMap.getString("noticeType");
		int intNoticeType = StrUtils.strToInt(noticeType, 0);
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		String newNoticeNo = noticeDao.getNewNoticeNo(moduleId, dataSetNo,
				intBusinessType, intNoticeType);
		ParaMap out = new ParaMap();
		out.put("state", StrUtils.isNull(newNoticeNo) ? 0 : 1);
		out.put("newNoticeNo", newNoticeNo);
		out.put("businessType", intBusinessType);
		out.put("noticeType", intNoticeType);
		return out;
	}

	public ParaMap getTrustNotNoticeTargetData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String trustId = inMap.getString("trustId");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		ParaMap out = noticeDao.getTrustNotNoticeTargetData(moduleId,
				dataSetNo, trustId);
		// 是否直接返回
		if (inMap.containsKey("direct")) {
			String strDirect = inMap.getString("direct");
			if (StrUtils.isNotNull(strDirect)
					&& (strDirect.equalsIgnoreCase("true") || strDirect
							.equals("1")))
				return out;
		}
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	public ParaMap publishNoticeData(ParaMap inMap) throws Exception {
		String id = inMap.getString("id");
		PlowNoticeDao noticeDao = new PlowNoticeDao();
		return noticeDao.publishNoticeData(id);
	}

	/**
	 * 撤销公告
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap cancleNotice(ParaMap inMap) throws Exception {
		String noticeId = inMap.getString("id");
		PlowNoticeDao plowNoticeDao = new PlowNoticeDao();
		ParaMap out = plowNoticeDao.checkCancleNotice(noticeId);
		for (int i = 0; i < out.getSize(); i++) {
			if (Integer.valueOf(out.getRecordString(i, "license_count")) != 0) {
				//throw new Exception("该公告已有竞买申请,无法撤销公告!");
				out.clear();
				out.put("state", "0");
				out.put("message","该公告已有竞买申请,无法撤销公告!");
				return out;
			}
		}
		out = plowNoticeDao.cancleNotice(noticeId);

		return out;
	}
}

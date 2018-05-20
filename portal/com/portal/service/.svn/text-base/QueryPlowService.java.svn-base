package com.portal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.base.dao.DataSetDao;
import com.base.ds.DataSourceManager;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sysman.dao.CantonDao;
import com.sysman.dao.UserDao;

public class QueryPlowService extends BaseService {
	/**
	 * 查询标的
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getPlowNoticeList(ParaMap inMap) throws Exception {
		String status = inMap.getString("status");
		String no = inMap.getString("no");
		String cantonId = inMap.getString("canton");
		String businessType = inMap.getString("business_type");
		String deatilStatus = inMap.getString("deatilStatus");
		ParaMap sqlParaMap = new ParaMap();
		StringBuffer condition = new StringBuffer();
		condition.append("    ");
		if (status != null && !"".equals(status)) {
			if (status.equals("5")) {
				if (deatilStatus.equals("-1")) {
					condition.append(" and tt.status >= ").append(status);
				} else {
					condition.append(" and tt.status in( ")
							.append(deatilStatus).append(" )");
				}
				sqlParaMap.put(DataSetDao.SQL_ORDER_BY, "end_trans_time desc");

			} else {
				if (status.equals("3"))
					sqlParaMap.put(DataSetDao.SQL_ORDER_BY,
							"end_notice_time desc");
				else if (status.equals("4"))
					sqlParaMap.put(DataSetDao.SQL_ORDER_BY,
							"begin_limit_time desc");

				condition.append(" and tt.status in( ").append(status)
						.append(")");
			}
		} else {
			if (deatilStatus.equals("-1") || StrUtils.isNull(deatilStatus)) {
				condition.append(" and tt.status >= ").append("3");
			} else {
				condition.append(" and tt.status in( ").append(deatilStatus)
						.append(" )");
			}
			sqlParaMap.put(DataSetDao.SQL_ORDER_BY, "end_apply_time desc");

		}
		if (StrUtils.isNotNull(cantonId)) {
			CantonDao cantonDao = new CantonDao();
			ParaMap cantonMap = cantonDao.getCantonData(null, null, cantonId);
			ParaMap cantonSonMap = cantonDao.getCantonFlatTreeData(null, null,
					cantonId);
			if (StrUtils.isNotNull(cantonMap.getRecordString(0, "parent_id"))) { // 如果是根节点
				// sql1.append(" and sc.id = '").append(cantonId).append("' ");
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < cantonSonMap.getSize(); i++) {
					sb.append(cantonSonMap.getRecordString(i, "id"))
							.append(",");
				}
				sb.append(cantonId);
				condition.append("  and tg.canton_id in ( ")
						.append(sb.toString()).append(")");
			} else { // 父节点 则要循环下面子节点
				condition.append(" and  tg.canton_id = '").append(cantonId)
						.append("'");
			}

		}

		if (StrUtils.isNotNull(businessType))
			condition.append(" and tt.business_type =:business_type");
		if (StrUtils.isNotNull(no))
			condition.append(" and tt.no like '").append(no).append("%'");
		if(deatilStatus.equals("3") || deatilStatus.equals("4") ){
			condition.append(" and tt.is_stop=0");
		}
		ParaMap conditionP = new ParaMap();
		conditionP.put("CUSTOM_CONDITION", condition.toString());
		DataSetDao dataSetDao = new DataSetDao();
		sqlParaMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, conditionP);
		sqlParaMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getString("pageIndex"));
		sqlParaMap.put(DataSetDao.SQL_PAGE_ROW_COUNT,
				inMap.getString("pageSize"));
		sqlParaMap.put("dataSetNo", "targetList");
		sqlParaMap.putAll(inMap);
		sqlParaMap.put("moduleId", inMap.getString("moduleId"));
		ParaMap out = dataSetDao.queryData(sqlParaMap);
		System.out.println(out);
		ParaMap custom = new ParaMap();
		int totalRowCount = out.getInt("totalRowCount");
		int pageIndex = out.getInt("pageIndex");
		int pageRowCount = out.getInt("pageIndex");
		int rowCount = out.getInt("rowCount");
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("area", "area");
		fields.put("address", "address");
		fields.put("canton_name", "canton_name");
		fields.put("status_ch", "status_ch");
		fields.put("status", "status");
		fields.put("rn_1", "num");
		fields.put("no", "no");
		fields.put("begin_price", "begin_price");
		fields.put("earnest_money", "earnest_money");
		fields.put("begin_apply_time", "begin_apply_time");
		fields.put("end_apply_time", "end_apply_time");
		fields.put("trans_type", "trans_type");
		fields.put("trans_type_chs", "trans_type_chs");
		fields.put("target_count", "target_count");
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("data", items);
		out.put("totalRowCount", totalRowCount);
		out.put("rowCount", rowCount);
		out.put("pageIndex", pageIndex);
		out.put("pageRowCount", pageRowCount);
		out.put("state", 1);
		out.put("bu_ip", getAppConfig("bu_ip"));
		out.put("bu_web", getAppConfig("bu_web"));
		out.put("demo_ip", getAppConfig("demo_ip"));
		out.put("demo_web", getAppConfig("demo_web"));
		return out;
	}

	/**
	 * 获取黑名单
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBlackUserListData(ParaMap inMap) throws Exception {
		// 组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String limit_type = inMap.getString("limit_type");
		String ban_type = inMap.getString("ban_type");
		ParaMap sqlParams = inMap;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.get("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.get("pageRowCount"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// 自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("username")) {
			String userName = inMap.getString("username");
			if (userName != null && !userName.equals("")
					&& !userName.equalsIgnoreCase("null")) {
				sqlParams.put("no", userName);
				sqlParams.put("name", userName);
				sqlParams.put("tbname", userName);
				customCondition
						.append(" and (tbld.no like '%' || :no || '%' or tbld.name like '%' || :name || '%' )");
			}
		}
		sqlParams.put("limit_type", limit_type);
		sqlParams.put("ban_type", ban_type);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		// 查询数据
		UserDao userDao = new UserDao();
		ParaMap out = userDao.getBlackUserListData(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		// 转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
}

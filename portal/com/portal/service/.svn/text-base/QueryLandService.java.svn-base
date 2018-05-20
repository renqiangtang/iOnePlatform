package com.portal.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.portal.dao.QueryLandDao;

public class QueryLandService extends BaseService {
	/**
	 * 查找标的
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetList(ParaMap inMap) throws Exception {
		ParaMap sqlParams = getPageInfo(inMap);
		int tabId = inMap.getInt("tabId");//切换页面的ID   0全部公告 ，1正在公告，2正在竞价，3结果公示 ，4补充公告
	    String moduleNo = "trans_land_portal";
	    String dataSetNo = "";
	    if(tabId==0){
	    	dataSetNo="query_all_trans_target_list";
	    }else if(tabId==1){
	    	dataSetNo="query_now_trans_target_list";
	    }else if(tabId==2){
	    	dataSetNo="query_limit_trans_target_list";
	    }else if(tabId==3){
	    	dataSetNo="query_result_trans_target_list";
	    }else if(tabId==4){
	    	dataSetNo="query_supp_trans_target_list";
	    }
	    sqlParams=getQueryCondition(sqlParams);//加载查询条件
		sqlParams.put("moduleNo", moduleNo);
		sqlParams.put("dataSetNo", dataSetNo);
		QueryLandDao queryDao = new QueryLandDao();
		ParaMap out = queryDao.getTransTargetList(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		int pageCount = 0;
		if(out.containsKey("pageCount")){
			pageCount=out.getInt("pageCount");
		}
		List items = MakeJSONData.makeItems(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		out.put("pageCount", pageCount);
		if(tabId==0){
			//start读取交易系统路径
			String bu_ip=getAppConfig("bu_ip");
			String bu_web=getAppConfig("bu_web");
			String demo_ip=getAppConfig("demo_ip");
			String demo_web=getAppConfig("demo_web");
			out.put("bu_ip", bu_ip);
			out.put("bu_web", bu_web);
			out.put("demo_ip", demo_ip);
			out.put("demo_web", demo_web);
			//end读取交易系统路径
			
			//start获取是否公布黑名单
			ParaMap sqlmap=new ParaMap();
		    sqlmap.put("goods_type", inMap.getString("goods_type"));
		    ParaMap blmap=getOvertBlack(sqlmap);
		    out.put("blackstatus", blmap.getString("status"));
			//end获取是否公布黑名单
		}
		
		return out;
	}

	/**
	 * 设置自定义查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");

		if (inMap.containsKey("goods_type")
				&& !"".equals(inMap.getString("goods_type"))) {
			String goods_type = inMap.getString("goods_type");
			if (StrUtils.isNotNull(goods_type)) {
				if(!goods_type.equals("301,401")){
					sqlParams.put("goods_type", goods_type);
					customCondition
							.append(" and ( tg.goods_type=:goods_type )");
				}else {
					customCondition
							.append(" and ( tg.goods_type in ('301','401') )");
				}
			}
		}
		if (inMap.containsKey("status")
				&& !"-1".equals(inMap.getString("status"))) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				sqlParams.put("status", status);
				customCondition.append(" and tt.status=:status");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and tt.no like '%'||:no||'%'");
			}
		}
		if (inMap.containsKey("name")) {
			String name = inMap.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and tg.name like '%'||:name||'%'");
			}
		}

		if (inMap.containsKey("address")) {
			String address = inMap.getString("address");
			if (StrUtils.isNotNull(address)) {
				sqlParams.put("address", address);
				customCondition.append(" and tg.address like '%'||:address||'%'");
			}
		}
		if (inMap.containsKey("goods_use")
				&& !"-1".equals(inMap.getString("goods_use"))) {
			String goods_use = inMap.getString("goods_use");
			if (StrUtils.isNotNull(goods_use)) {
				sqlParams.put("goods_use", goods_use);
				customCondition
						.append(" and tg.goods_use like '%'||:goods_use||'%'");
			}
		}

		if (inMap.containsKey("trans_type")
				&& !"-1".equals(inMap.getString("trans_type"))) {
			String trans_type = inMap.getString("trans_type");
			if (StrUtils.isNotNull(trans_type)) {
				customCondition.append(" and tt.trans_type ='" + trans_type
						+ "' ");
			}
		}
		
		if (inMap.containsKey("trans_type_name")
				&& !"-1".equals(inMap.getString("trans_type_name"))) {
			String trans_type_name = inMap.getString("trans_type_name");
			if (StrUtils.isNotNull(trans_type_name)) {
				customCondition.append(" and tt.trans_type_name ='" + trans_type_name
						+ "' ");
			}
		}
		if(inMap.containsKey("trans_status") && !"".equals(inMap.getString("trans_status"))){
			String trans_status = inMap.getString("trans_status");
			if (StrUtils.isNotNull(trans_status)) {
				if(trans_status.equals("-2")){
					customCondition.append(" and tt.is_suspend =1 ");
				}else if(trans_status.equals("-3")){
					customCondition.append(" and tt.is_stop =1 ");
				}else{
					customCondition.append(" and tt.status =" + trans_status );
				}
			}
		}
		
		if(inMap.containsKey("timetype") && 0!=inMap.getInt("timetype") && inMap.containsKey("timevalue") && !"".equals(inMap.getString("timevalue"))){
			int timetype=inMap.getInt("timetype");
			String timevalue=inMap.getString("timevalue");
			if(timetype==1){//保证金截止时间
				customCondition.append(" and tt.end_earnest_time <=  to_date('" + timevalue
						+ "', 'yyyy-mm-dd')");
			}else if(timetype==2){//竞买申请截止时间
				customCondition.append(" and tt.end_apply_time <= to_date('" + timevalue
						+ "', 'yyyy-mm-dd')");
			}else if(timetype==3){//挂牌截止时间
				customCondition.append(" and tt.end_list_time <=  to_date('" + timevalue
					+ "', 'yyyy-mm-dd')");
			}
		}
		
		if(inMap.containsKey("canton") && !"".equals(inMap.getString("canton"))){
			String canton = inMap.getString("canton");
			if (StrUtils.isNotNull(canton)) {
					customCondition.append(" and tg.canton_id =" + canton );
			}
		}
		if(inMap.containsKey("begin_notice_time") && !"".equals(inMap.getString("begin_notice_time"))){
			String begin_notice_time = inMap.getString("begin_notice_time");
			if (StrUtils.isNotNull(begin_notice_time)) {
					customCondition.append(" and tn.NOTICE_DATE >=to_date('" + begin_notice_time+ "', 'yyyy-mm-dd')" );
			}
		}
		if(inMap.containsKey("end_notice_time") && !"".equals(inMap.getString("end_notice_time"))){
			String end_notice_time = inMap.getString("end_notice_time");
			if (StrUtils.isNotNull(end_notice_time)) {
					customCondition.append(" and tn.NOTICE_DATE <=to_date('" + end_notice_time+ "', 'yyyy-mm-dd')" );
			}
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
	
	
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置和读取分页信息
	 */
	private ParaMap getPageInfo(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		if (StringUtils.isNotEmpty(sortField)
				&& StringUtils.isNotEmpty(sortDir))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (StringUtils.isEmpty(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, " id desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// sqlParams.put("id", sqlParams.getString("u"));
		return sqlParams;
	}
	
	public ParaMap getBlack(ParaMap inMap) throws Exception{
		ParaMap result=new ParaMap();
		ParaMap sqlParams = getPageInfo(inMap);
		int tabId = inMap.getInt("tabId");
	    String moduleNo = "trans_land_portal";
	    String dataSetNo = "";
	    if(tabId==5){//黑名单TAB
	    	dataSetNo="query_black";
	    }else{
	    	result.clear();
	    	result.put("state", 0);
	    	result.put("message", "状态栏错误！");
	    	return result;
	    }
	    ParaMap sqlmap=new ParaMap();
	    sqlmap.put("goods_type", inMap.getString("ban_type"));
	    ParaMap blmap=getOvertBlack(sqlmap);
	    if(blmap.getInt("status")==0){//不公开
	    	result.clear();
	    	result.put("Total", 0);
			result.put("pageCount", 0);
	    	result.put("status",1 );
	    	return result;
	    }
	    sqlParams=getQueryBlackCondition(sqlParams);
	    sqlParams.put("moduleNo", moduleNo);
		sqlParams.put("dataSetNo", dataSetNo);
		QueryLandDao queryDao = new QueryLandDao();
		result = queryDao.getTransTargetList(sqlParams);
		int totalRowCount = result.getInt("totalRowCount");
		int pageCount = 0;
		if(result.containsKey("pageCount")){
			pageCount=result.getInt("pageCount");
		}
		List items = MakeJSONData.makeItems(result);
		result.clear();
		result.put("Rows", items);
		result.put("Total", totalRowCount);
		result.put("pageCount", pageCount);
		result.put("status",0 );
		return result;
	}
	
	/**
	 * 设置自定义查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	private ParaMap getQueryBlackCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("ban_type")) {
			String ban_type = inMap.getString("ban_type");
			if (StrUtils.isNotNull(ban_type)) {
				sqlParams.put("ban_type", ban_type);
				customCondition.append(" and ban_type =:ban_type");
			}
		}
		if (inMap.containsKey("name")) {
			String name = inMap.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and name like '%'||:name||'%'");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return inMap;
	}
	
	/**
	 * 获取黑名单状态，0非公布，1公布
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOvertBlack(ParaMap inMap) throws Exception{
		ParaMap result=new ParaMap();
		DataSetDao dataSetDao=new DataSetDao();
		ParaMap sqlMap=new ParaMap();
		sqlMap.put("goods_type",inMap.getString("goods_type"));
		result=dataSetDao.querySimpleData("TRANS_BIDDER_LIMIT_OVERT", sqlMap);
		if(result.getInt("totalRowCount")==0){//没有记录时默认为不公开状态
			result.clear();
			result.put("status", 0);
		}else{
			int status=result.getRecordInt(0, "status");
			result.clear();
			result.put("status", status);
		}
		return result;
	}
	
}

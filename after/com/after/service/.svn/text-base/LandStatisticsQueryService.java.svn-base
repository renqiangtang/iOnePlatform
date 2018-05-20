package com.after.service;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.after.dao.LandStatisticsQueryDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class LandStatisticsQueryService  extends BaseService {
	private LandStatisticsQueryDao landStatisticsQueryDao = new LandStatisticsQueryDao();
	/**
	 * 统计-标的查询
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryLandList(ParaMap inMap) throws Exception {
		ParaMap customConditions = new ParaMap();
		StringBuffer customCondition = new StringBuffer();
		String name = inMap.getString("name");
		String organId = getOrganId(inMap.getString("u"));//得到单位id
		inMap.put("organId", organId);
		String status = inMap.getString("status");
		String end_trans_time_1 = inMap.getString("end_trans_time_1");
		String end_trans_time_2 = inMap.getString("end_trans_time_2");
		if(StrUtils.isNotNull(name)){
			customCondition.append(" and t.name = '"+name+"' ");
		}
		if(StrUtils.isNotNull(status)){
			if(status.equals("1")){
				customCondition.append(" and t.is_suspend = "+status+" ");
			}
			else if(status.equals("2")){
				customCondition.append(" and t.is_stop = "+status+" ");
			}
			else if(status.equals("5")||status.equals("6")){
				customCondition.append(" and t.status = "+status+" ");
			}
		}
		if(StrUtils.isNotNull(end_trans_time_1) || StrUtils.isNotNull(end_trans_time_2)){
			if(StrUtils.isNotNull(end_trans_time_1) && !StrUtils.isNotNull(end_trans_time_2)){
				end_trans_time_2 = StrUtils.dateToString(new Date(), "yyyy-dd-MM");
			}
			if(!StrUtils.isNotNull(end_trans_time_1) && StrUtils.isNotNull(end_trans_time_2)){
				end_trans_time_1 = "1970-01-01";
			}
			customCondition.append(" and ( t.End_Trans_Time between date'"+end_trans_time_1+"' and date'"+end_trans_time_2+"' ) ");
		}
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		inMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap result = landStatisticsQueryDao.queryLandList(inMap);
		int totalRowCount =result.getInt("totalRowCount");
		List items = MakeJSONData.makeItems(result);
		result.clear();
		result.put("Rows", items);
		result.put("Total", totalRowCount);
		return result;

	}
	
	/**
	 * 导出竞价记录
	 * @return
	 * @throws Exception
	 */
	public byte[] exportQuoteList(ParaMap inMap) throws Exception {
		byte[] fileByte = null;
		ParaMap params = new ParaMap();
		this.getResponse().reset();
		OutputStream outputStream = this.getResponse().getOutputStream();
		this.getRequest().setCharacterEncoding("UTF-8");
		this.getResponse().setContentType(
				"application/x-msdownload; charset=UTF-8");
		String ID = UUID.randomUUID().toString();
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + ID + ".pdf\";");
		//查询起始价
		String organId = getOrganId(inMap.getString("u"));//得到单位id
		inMap.put("organId", organId);
		params.put("begin_price", queryBginPrice(inMap).getString("begin_price"));
		params.put("canton", queryBginPrice(inMap).getString("canton"));
		params.put("no", queryBginPrice(inMap).getString("no"));
		//查询竞得人 //成交价格
		ParaMap query = queryVendee(inMap);
		params.put("vendeeMan", query.getString("vendeeMan"));
		params.put("trust_price", query.getString("trust_price"));
		params.put("license_no", query.getString("license_no"));
		//查询竞价记录
		ParaMap  result  = landStatisticsQueryDao.queryTranrLog(inMap);
		List items = MakeJSONData.makeItems(result);
		params.put("logs", items);
		HtmltoPDF.QuoteListPdf(outputStream,params);
		this.getResponse().flushBuffer();
		return fileByte;
	}
	
	/**
	 * 导出报名记录
	 * @return
	 * @throws Exception
	 */
	public byte[] exportEnlistList(ParaMap inMap) throws Exception {
		byte[] fileByte = null;
		ParaMap params = new ParaMap();
		this.getResponse().reset();
		OutputStream outputStream = this.getResponse().getOutputStream();
		this.getRequest().setCharacterEncoding("UTF-8");
		this.getResponse().setContentType(
		"application/x-msdownload; charset=UTF-8");
		String ID = UUID.randomUUID().toString();
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + ID + ".pdf\";");
		String organId = getOrganId(inMap.getString("u"));//得到单位id
		inMap.put("organId", organId);
		ParaMap  result  = landStatisticsQueryDao.exportEnlistList(inMap);
		List items = MakeJSONData.makeItems(result);
		params.put("canton", queryBginPrice(inMap).getString("canton"));
		params.put("no", queryBginPrice(inMap).getString("no"));
		params.put("logs", items);
		HtmltoPDF.EnlistListPdf(outputStream,params);
		this.getResponse().flushBuffer();
		return fileByte;
	}

	/**
	 * 查询起始价
	 * @param inMap
	 * @return
	 */
	private ParaMap  queryBginPrice(ParaMap inMap)throws Exception {
		String target_id = inMap.getString("target_id");
		String organId = inMap.getString("organId");
		ParaMap  result  = landStatisticsQueryDao.queryBginPrice(target_id,organId);
		ParaMap  out  = new ParaMap();
		List items = MakeJSONData.makeItems(result);
		ParaMap temp = (ParaMap) items.get(0);
		String begin_price = temp.getString("begin_price");
		String canton = temp.getString("canton");
		String no = temp.getString("no");
		out.put("begin_price", begin_price);
		out.put("canton", canton);
		out.put("no", no);
		return out;
	}

	/**
	 * 查询竞得人名称
	 * @return
	 * @throws Exception
	 */
	public  ParaMap queryVendee(ParaMap inMap) throws Exception {
		String vendeeName ="";
		String target_id = inMap.getString("target_id");
		ParaMap  result  = landStatisticsQueryDao.queryVendee(target_id);
		ParaMap  out  = new ParaMap();
		List items = MakeJSONData.makeItems(result);
		ParaMap temp = (ParaMap) items.get(0);
		String VendeeMan = temp.getString("name");
		String trust_price = temp.getString("trans_price");
		String license_no = temp.getString("license_no");
		out.put("vendeeMan", VendeeMan);
		out.put("trust_price", trust_price);
		out.put("license_no", license_no);
		return out;
	}
	
	/**
	 * 得到单位id
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("id")) {
						organId = (String) record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}

}

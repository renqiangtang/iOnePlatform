package com.trademan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.ReportChartUtils;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.base.web.RouteFilter;
import com.sysman.dao.CantonDao;
import com.trademan.dao.PlowReportDao;

public class PlowReportService extends BaseService {

	public ParaMap getParaMap(ParaMap inMap) {
		String beginyear = inMap.getString("beginTime");
		String endyear = inMap.getString("endTime");
		String beginPrice = inMap.getString("beginPrice");
		String endPrice = inMap.getString("endPrice");
		String beginArea = inMap.getString("beginArea");
		String endArea = inMap.getString("endArea");
		String status = inMap.getString("status");
		String cantonId = inMap.getString("cantonId");
		String businessType = inMap.getString("businessType");
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		if (beginyear != null && !"".equals(beginyear))
			sql.append(
					" and  to_date(to_char(end_trans_time,'yyyy-mm-dd'),'yyyy-mm-dd') >=to_date('")
					.append(beginyear).append("','yyyy-mm-dd')");
		if (endyear != null && !"".equals(endyear))
			sql.append(
					" and  to_date(to_char(end_trans_time,'yyyy-mm-dd'),'yyyy-mm-dd') <=to_date('")
					.append(endyear).append("','yyyy-mm-dd')");
		if (beginPrice != null && !"".equals(beginPrice))
			sql.append(" and tt.trans_price >=").append(beginPrice);
		if (endPrice != null && !"".equals(endPrice))
			sql.append(" and tt.trans_price<=").append(endPrice);
		if (beginArea != null && !"".equals(beginArea))
			sql.append(" and tg.area>=").append(beginArea);
		if (endArea != null && !"".equals(endArea))
			sql.append(" and tg.area <=").append(endArea);
		if (status != null && !"".equals(status))
			sql.append(" and tt.status = ").append(status);
		if (cantonId != null && !"".equals(cantonId))
			sql.append(" and tg.canton_id = '").append(cantonId).append("' ");
		if (businessType != null && !"-1".equals(businessType)) {
			sql.append(" and tt.business_type = '").append(businessType)
					.append("'");
			sql1.append("and goods_type_id||business_type_id  = ").append(
					businessType);
		}
		ParaMap custParaMap = new ParaMap();
		custParaMap.put("CUSTOM_CONDITION1", sql1.toString());
		custParaMap.put("CUSTOM_CONDITION", sql.toString());
		ParaMap sqlParaMap = new ParaMap();
		sqlParaMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, custParaMap);

		sqlParaMap.put("moduleId", inMap.getString("moduleId"));

		return sqlParaMap;

	}

	/**
	 * 交易统计报表
	 * 
	 * @param inMap
	 * @return
	 * 
	 * @throws Exception
	 */
	public ParaMap getReportMap(ParaMap inMap) throws Exception {
		PlowReportDao reportDao = new PlowReportDao();
		ParaMap out = reportDao.getTransReportData(getParaMap(inMap));
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("counts", "counts");
		fields.put("status_ch", "status_ch");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		ReportChartUtils.TITLE = "交易统计报表";
		ParaMap contionMap = new ParaMap();
		contionMap.put("label", "status_ch");
		contionMap.put("value", "counts");
		ReportChartUtils chartUtils = new ReportChartUtils();
		out.put(RouteFilter.jsonStr, chartUtils.getChartJson(
				chartUtils.toJsonChart(items, contionMap),
				ReportChartUtils.CHARTTYPEPIE));
		return out;
	}

	/**
	 * 指标库统计报表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap budgetBaseReport(ParaMap inMap) throws Exception {

		PlowReportDao reportDao = new PlowReportDao();
		ParaMap out = reportDao.getTransBudgetLib(getParaMap(inMap));
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("counts", "counts");
		fields.put("years", "years");
		fields.put("business_type_name", "business_type_name");
		fields.put("business_type", "business_type");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		ReportChartUtils.TITLE = "指标统计(单位：指标数)";
		ParaMap contionMap = new ParaMap();
		contionMap.put("label", "business_type_name");
		contionMap.put("value", "counts");
		contionMap.put("condition", "years");
		ReportChartUtils chartUtils = new ReportChartUtils();
		chartUtils.XLEGEND.put("text", "(年份)");
		chartUtils.XLEGEND.put("style", "{font-size: 13px; color: #006400}");
		chartUtils.YLEGEND.put("text", "面积");
		chartUtils.YLEGEND.put("style", "{font-size: 13px; color: #778877}");
		out.put(RouteFilter.jsonStr, chartUtils.getChartJson(
				chartUtils.toJsonChart(items, contionMap),
				ReportChartUtils.CHARTTYPELINE));
		return out;
	}

	/**
	 * 指标审核统计
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap budgetAuditReport(ParaMap inMap) throws Exception {
		String beginyear = inMap.getString("beginTime");
		String endyear = inMap.getString("endTime");
		String beginPrice = inMap.getString("beginPrice");
		String endPrice = inMap.getString("endPrice");
		String beginArea = inMap.getString("beginArea");
		String endArea = inMap.getString("endArea");
		String status = inMap.getString("status");
		String cantonId = StrUtils.isNotNull(inMap.getString("cantonId")) ? inMap
				.getString("cantonId") : "440000";
		String businessType = inMap.getString("businessType");
		String afterCheck = inMap.getString("checkAfter");
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer sql3 = new StringBuffer();
		if (beginyear != null && !"".equals(beginyear))
			sql.append(" and  begin_limit_time >=to_date('").append(beginyear)
					.append("','yyyy-mm-dd')");
		if (endyear != null && !"".equals(endyear))
			sql.append(" and end_trans_time <=to_date('").append(endyear)
					.append("','yyyy-mm-dd')");
		if (beginPrice != null && !"".equals(beginPrice))
			sql.append(" and tt.trans_price >=").append(beginPrice);
		if (endPrice != null && !"".equals(endPrice))
			sql.append(" and tt.trans_price<=").append(endPrice);
		if (beginArea != null && !"".equals(beginArea))
			sql.append(" and tg.area>=").append(beginArea);
		if (endArea != null && !"".equals(endArea))
			sql.append(" and tg.area <=").append(endArea);
		if (status != null && !"".equals(status))
			sql.append(" and tt.status = ").append(status);
		if (cantonId != null && !"".equals(cantonId)) {
			CantonDao cantonDao = new CantonDao();
			ParaMap cantonMap = cantonDao.getCantonData(null, null, cantonId);
			System.out.println(cantonMap);
			ParaMap cantonSonMap = cantonDao.getCantonFlatTreeData(null, null,
					cantonId);
			if (StrUtils.isNotNull(cantonMap.getRecordString(0, "parent_id"))) { // 如果是根节点
				// sql1.append(" and sc.id = '").append(cantonId).append("' ");
				if (cantonSonMap.getSize() == 0) {
					sql1.append(" and sc.id = '").append(cantonId).append("' ");
					sql2.append(" and ST.ID = T.CANTON_ID");
					sql3.append(" t.canton_id  ");
				} else {
					sql1.append(" and sc.parent_id = '").append(cantonId)
							.append("' ");
					sql2.append(" and ST.ID = T.CANTON_ID");
					sql3.append(" t.canton_id  ");
				}

			} else { // 父节点 则要循环下面子节点
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < cantonSonMap.getSize(); i++) {
					sb.append(cantonSonMap.getRecordString(i, "id"))
							.append(",");
				}
				sql1.append(" and sc.id in(")
						.append(sb.substring(0, sb.toString().length() - 1))
						.append(") ");
				sql2.append(" and substr(ST.ID,0,4) = substr(T.CANTON_ID,0,4)");
				sql3.append(" substr(t.canton_id,0,4) ");
			}

		}
		if (businessType != null && !"-1".equals(businessType)) {
			sql.append(" and tt.business_type = '").append(businessType)
					.append("'");
			sql1.append("and goods_type_id||business_type_id  = ").append(
					businessType);
		}
		if (afterCheck != null && !"-1".equals(afterCheck))
			sql.append(" and tt.after_check = '").append(afterCheck)
					.append("'");
		ParaMap custParaMap = new ParaMap();
		custParaMap.put("CUSTOM_CONDITION1", sql1.toString());
		custParaMap.put("CUSTOM_CONDITION", sql.toString());
		custParaMap.put("CUSTOM_CONDITION2", sql2.toString());
		custParaMap.put("CUSTOM_CONDITION3", sql3.toString());
		ParaMap sqlParaMap = new ParaMap();
		sqlParaMap.put("moduleId", inMap.getString("moduleId"));
		sqlParaMap.put("parent_id", cantonId);
		sqlParaMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, custParaMap);
		PlowReportDao reportDao = new PlowReportDao();
		ParaMap out = reportDao.getTransBudgetAudit(sqlParaMap);
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("counts", "sumarea");
		fields.put("name", "name");
		fields.put("business_name", "business_name");
		fields.put("business_type", "business_type");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		ParaMap contionMap = new ParaMap();
		contionMap.put("label", "business_name");
		contionMap.put("value", "counts");
		contionMap.put("condition", "name");
		ReportChartUtils.TITLE = "成交统计区域堆积图(单位：亩)";
		ReportChartUtils chartUtils = new ReportChartUtils();
		chartUtils.XLEGEND.put("text", "(行政区域)");
		chartUtils.XLEGEND.put("style", "{font-size: 13px; color: #006400}");
		chartUtils.YLEGEND.put("text", "面积");
		chartUtils.YLEGEND.put("style", "{font-size: 22px; color: #778877}");
		out.put(RouteFilter.jsonStr, chartUtils.getChartJson(
				chartUtils.toJsonChart(items, contionMap),
				ReportChartUtils.CHARTTYPEBARGLASS));
		return out;
	}

	/**
	 * 价款统计报表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap budgetPriceReport(ParaMap inMap) throws Exception {

		String beginyear = inMap.getString("beginTime");
		String endyear = inMap.getString("endTime");
		String beginPrice = inMap.getString("beginPrice");
		String endPrice = inMap.getString("endPrice");
		String beginArea = inMap.getString("beginArea");
		String endArea = inMap.getString("endArea");
		String status = inMap.getString("status");
		String cantonId = inMap.getString("cantonId");
		String businessType = inMap.getString("businessType");
		String priceType = inMap.getString("priceType");
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		if (beginyear != null && !"".equals(beginyear))
			sql.append(
					" and  to_date(to_char(begin_limit_time,'yyyy-mm-dd'),'yyyy-mm-dd') >=to_date('")
					.append(beginyear).append("','yyyy-mm-dd')");
		if (endyear != null && !"".equals(endyear))
			sql.append(
					" and  to_date(to_char(end_trans_time,'yyyy-mm-dd'),'yyyy-mm-dd') <=to_date('")
					.append(endyear).append("','yyyy-mm-dd')");
		if (beginPrice != null && !"".equals(beginPrice))
			sql.append(" and tt.trans_price >=").append(beginPrice);
		if (endPrice != null && !"".equals(endPrice))
			sql.append(" and tt.trans_price<=").append(endPrice);
		if (beginArea != null && !"".equals(beginArea))
			sql.append(" and tg.area>=").append(beginArea);
		if (endArea != null && !"".equals(endArea))
			sql.append(" and tg.area <=").append(endArea);
		if (status != null && !"".equals(status))
			sql.append(" and tt.status = ").append(status);
		if (cantonId != null && !"".equals(cantonId))
			sql.append(" and tg.canton_id = '").append(cantonId).append("' ");
		if (businessType != null && !"-1".equals(businessType)) {
			sql.append(" and tt.business_type = '").append(businessType)
					.append("'");
			sql1.append("and goods_type_id||business_type_id  = ").append(
					businessType);
		} else {
			sql1.append("and  goods_type_id = '501' ");
			sql.append(" and tt.business_type like '501%'");
			// sql.append(" and tt.after_check = '0'");
		}
		String c2 = new String();
		String c3 = new String();
		if (StrUtils.isNotNull(priceType)) {
			if (!priceType.equals("unitPrice")) {
				c2 = "SUM("
						+ priceType
						+ ") OVER(PARTITION BY TO_CHAR(TT.CREATE_DATE, 'mm'), tt.business_type order by to_char(tt.create_date, 'mm'))";
				if (!priceType.equals("area"))
					c3 = " to_char(nvl(t.sums/10000, 0),'99999999999999999999.9999')";
				else
					c3 = "nvl(sums,0) ";
			} else if (priceType.equals("unitPrice")) {
				c3 = " to_char(nvl(t.sums/10000, 0),'99999999999999999999.9999')";
				c2 = " SUM(trans_price) OVER(PARTITION BY TO_CHAR(TT.CREATE_DATE, 'mm'), tt.business_type order by to_char(tt.create_date, 'mm'))"
						+ "/"
						+ "SUM(area) "
						+ "OVER(PARTITION BY TO_CHAR(TT.CREATE_DATE, 'mm'),"
						+ " TT.BUSINESS_TYPE ORDER BY TO_CHAR(TT.CREATE_DATE, 'mm'))"
						+ " ";
			}

		}
		ParaMap custParaMap = new ParaMap();
		custParaMap.put("CUSTOM_CONDITION1", sql1.toString());
		custParaMap.put("CUSTOM_CONDITION2", c2);
		custParaMap.put("CUSTOM_CONDITION", sql.toString());
		custParaMap.put("CUSTOM_CONDITION3", c3);
		ParaMap sqlParaMap = new ParaMap();
		sqlParaMap.put("moduleId", inMap.getString("moduleId"));
		sqlParaMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, custParaMap);
		PlowReportDao reportDao = new PlowReportDao();
		ParaMap out = reportDao.getTransBudgetPrice(sqlParaMap);
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("sums", "sums");
		fields.put("months_", "months_");
		fields.put("business_type_name", "business_type_name");
		fields.put("business_type", "business_type");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		ParaMap contionMap = new ParaMap();
		contionMap.put("label", "business_type_name");
		contionMap.put("value", "sums");
		contionMap.put("condition", "months_");
		if (priceType.equals("area")) {
			ReportChartUtils.TITLE = "成交面积(单位：亩)";
		} else if (priceType.equals("unitPrice"))
			ReportChartUtils.TITLE = "成交单价(单位：万元)";
		else
			ReportChartUtils.TITLE = "成交金额(单位：万元)";
		out.clear();
		ReportChartUtils chartUtils = new ReportChartUtils();
		chartUtils.XLEGEND.put("text", "(月份)");
		chartUtils.XLEGEND.put("style", "{font-size: 13px; color: #006400}");
		out.put(RouteFilter.jsonStr, chartUtils.getChartJson(
				chartUtils.toJsonChart(items, contionMap),
				ReportChartUtils.CHARTTYPELINE));
		return out;
	}

	/**
	 * 指标交易统计报表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryBudgetBusi(ParaMap inMap) throws Exception {
		String beginyear = inMap.getString("beginTime");
		String endyear = inMap.getString("endTime");
		String beginPrice = inMap.getString("beginTransPrice");
		String endPrice = inMap.getString("endTransPrice");
		String beginArea = inMap.getString("beginArea");
		String endArea = inMap.getString("endArea");
		String status = StrUtils.getString(inMap.getString("status"));
		String cantonId = inMap.getString("cantonId");
		String txtCantonId = inMap.getString("txtCantonId");
		String businessType = inMap.getString("businessType");
		String beginLimitTime = inMap.getString("beginLimitTime");
		String endLimitTime = inMap.getString("endLimitTime");
		String beginNoticeTime = inMap.getString("beginNoticeTime");
		String endNoticeTime = inMap.getString("endNoticeTime");
		String no = inMap.getString("no");
		String trustZ = inMap.getString("trustZ");
		String trustS = inMap.getString("trustS");
		
		
		StringBuffer sql = new StringBuffer();
		if (beginyear != null && !"".equals(beginyear))
			sql.append(
					" and  TO_DATE(to_char(END_TRANS_TIME,'yyyy-mm-dd'), 'yyyy-mm-dd') >=to_date('")
					.append(beginyear).append("','yyyy-mm-dd')");
		if (endyear != null && !"".equals(endyear))
			sql.append(
					" and TO_DATE(to_char(END_TRANS_TIME,'yyyy-mm-dd'), 'yyyy-mm-dd') <=to_date('")
					.append(endyear).append("','yyyy-mm-dd')");
		if (beginPrice != null && !"".equals(beginPrice))
			sql.append(" and tt.trans_price >=").append(beginPrice);
		if (endPrice != null && !"".equals(endPrice))
			sql.append(" and tt.trans_price<=").append(endPrice);
		if (beginArea != null && !"".equals(beginArea))
			sql.append(" and tg.area>=").append(beginArea);
		if (endArea != null && !"".equals(endArea))
			sql.append(" and tg.area <=").append(endArea);
		if (status != null && !"".equals(status) && !"-1".equals(status))
			sql.append(" and tt.status in( ").append(status).append(" )");
		if (cantonId != null && !"".equals(cantonId)) {
			CantonDao cantonDao = new CantonDao();
			ParaMap cantonMap = cantonDao.getCantonData(null, null, cantonId);
			System.out.println(cantonMap);
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
				sql.append("  and tg.canton_id in ( ")
						.append(sb.toString()).append(")");
			} else { // 父节点 则要循环下面子节点
				sql.append(" and  tg.canton_id =   '").append(cantonId).append("'");

			}

		}
		if (businessType != null && !"-1".equals(businessType)) {
			sql.append(" and tt.business_type = '").append(businessType)
					.append("'");
		}
		if (beginLimitTime != null && !"".equals(beginLimitTime))
			sql.append(
					" and  TO_DATE(to_char(begin_limit_time,'yyyy-mm-dd'), 'yyyy-mm-dd') >=to_date('")
					.append(beginLimitTime).append("','yyyy-mm-dd')");
		if (endLimitTime != null && !"".equals(endLimitTime))
			sql.append(
					" and TO_DATE(to_char(begin_limit_time,'yyyy-mm-dd'), 'yyyy-mm-dd')<=to_date('")
					.append(endLimitTime).append("','yyyy-mm-dd')");
		if (beginNoticeTime != null && !"".equals(beginNoticeTime))
			sql.append(
					" and   TO_DATE(to_char(begin_notice_time,'yyyy-mm-dd'), 'yyyy-mm-dd') >=to_date('")
					.append(beginNoticeTime).append("','yyyy-mm-dd')");
		if (endNoticeTime != null && !"".equals(endNoticeTime))
			sql.append(
					" and  TO_DATE(to_char(begin_notice_time,'yyyy-mm-dd'), 'yyyy-mm-dd')  <=to_date('")
					.append(endNoticeTime).append("','yyyy-mm-dd')");
		if (StrUtils.isNotNull(no))
			sql.append(" and tt.no = '").append(no).append("'");
		if (StrUtils.isNotNull(trustS)) {
			sql.append(
					"   AND tl.bidder_id in (SELECT tb.id FROM trans_bidder tb WHERE tb.name LIKE '%")
					.append(trustS).append("%' )");
		}
		if (StrUtils.isNotNull(trustZ)) {
			sql.append(
					"   AND ttbr.bidder_id in (SELECT tb.id FROM trans_bidder tb WHERE tb.name LIKE '%")
					.append(trustZ).append("%' )");
		}
		
		
		ParaMap custParaMap = new ParaMap();
		custParaMap.put("CUSTOM_CONDITION", sql);
		ParaMap sqlMap = new ParaMap();
		sqlMap.put(DataSetDao.SQL_CUSTOM_CONDITIONS, custParaMap);
		// 组织查询条件及分页信息
		String sortField = inMap.getString("sortField");

		sqlMap.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlMap.put(DataSetDao.SQL_PAGE_ROW_COUNT,
		        inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlMap.put(DataSetDao.SQL_ORDER_BY, "end_trans_time desc,id");
		else
			sqlMap.put(DataSetDao.SQL_ORDER_BY, sortField);
		
		PlowReportDao plowReportDao = new PlowReportDao();
		ParaMap out = plowReportDao.getExcelBudgetStatistics(sqlMap);
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

	public byte[] expExcleReport(ParaMap inMap) throws Exception {
		ParaMap out = queryBudgetBusi(inMap);
		// System.out.println(new
		// String(this.getRequest().getParameter("businessTypeLabel").getBytes("ISO-8859-1"),"utf-8"));
		String filename = String.valueOf(System.currentTimeMillis());
		File file = new File(AppConfig.getPro("fileRoot") + "\\xls\\"
				+ filename + ".xls");
		FileOutputStream fo = new FileOutputStream(file);
		WritableWorkbook book = Workbook.createWorkbook(fo);
		WritableSheet sheet = book.createSheet("成交统计", 0);
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格定义
		WritableCellFormat wcfRight = new WritableCellFormat(wf); // 单元格定义
		wcfRight.setAlignment(jxl.format.Alignment.RIGHT);
		wcf.setAlignment(Alignment.CENTRE);
		sheet.addCell(new Label(0, 0, "耕地储备指标交易统计分析", wcf)); // 合并单元格
		sheet.addCell(new Label(0, 1, "成交时间:"));
		sheet.addCell(new Label(1, 1, StrUtils.getString(inMap
				.getString("beginTime"))
				+ "至"
				+ StrUtils.getString(inMap.getString("endTime"))));
		sheet.addCell(new Label(2, 1, "拍卖时间:"));
		sheet.addCell(new Label(3, 1, StrUtils.getString(inMap
				.getString("beginLimitTime"))
				+ "至"
				+ StrUtils.getString(inMap.getString("endLimitTime"))));
		sheet.addCell(new Label(4, 1, "公告时间:"));
		sheet.addCell(new Label(5, 1, StrUtils.getString(inMap
				.getString("beginNoticeTime"))
				+ "至"
				+ StrUtils.getString(inMap.getString("endNoticeTime"))));
		sheet.addCell(new Label(6, 1, "成交价:"));
		sheet.addCell(new Label(7, 1, StrUtils.getString(inMap
				.getString("beginTransPrice"))
				+ "至"
				+ StrUtils.getString(inMap.getString("endTransPrice"))));
		sheet.addCell(new Label(8, 1, "行政区:"));
		sheet.addCell(new Label(9, 1, new String(inMap.getString("canton")
				.getBytes("ISO-8859-1"), "UTF-8")));
		sheet.addCell(new Label(10, 1, "交易状态:"));
		sheet.addCell(new Label(11, 1, new String(inMap.getString("statusLabel")
				.getBytes("ISO-8859-1"), "UTF-8")));
		sheet.addCell(new Label(0, 2, "编号："));
		sheet.addCell(new Label(1, 2, inMap.getString("no")));
		sheet.addCell(new Label(2, 2, "受让方："));
		sheet.addCell(new Label(3, 2, inMap.getString("trustS")));
		sheet.addCell(new Label(4, 2, "转让方："));
		sheet.addCell(new Label(5, 2, inMap.getString("trustZ")));
		sheet.addCell(new Label(6, 2, "面积区间："));
		sheet.addCell(new Label(7, 2, StrUtils.getString(inMap
				.getString("beginArea"))
				+ "至"
				+ StrUtils.getString(inMap.getString("endArea")) + "亩"));
		sheet.addCell(new Label(8, 2, "业务类型："));
		sheet.addCell(new Label(9, 2, new String(StrUtils.getString(
				inMap.getString("businessTypeLabel")).getBytes("ISO-8859-1"),
				"UTF-8")));
		sheet.mergeCells(0, 0, 14, 0);
		sheet.addCell(new Label(0, 3, "编号"));
		sheet.addCell(new Label(1, 3, "交易状态"));
		sheet.addCell(new Label(2, 3, "行政区"));
		sheet.addCell(new Label(3, 3, "业务类型"));
		sheet.addCell(new Label(4, 3, "公告开始时间"));
		sheet.addCell(new Label(5, 3, "公告结束时间"));
		sheet.addCell(new Label(6, 3, "竞价开始时间"));
		sheet.addCell(new Label(7, 3, "面积"));
		sheet.addCell(new Label(8, 3, "申请截止时间"));
		sheet.addCell(new Label(9, 3, "参与竞价人数"));
		sheet.addCell(new Label(10, 3, "委托人"));
		sheet.addCell(new Label(11, 3, "竞价幅度(元)"));
		sheet.addCell(new Label(12, 3, "保证金(元)"));
		sheet.addCell(new Label(13, 3, "成交价"));
		sheet.addCell(new Label(14, 3, "成交时间"));
		sheet.addCell(new Label(15, 3, "转让方"));
		sheet.addCell(new Label(16, 3, "受让方"));
		List itemList = (List) out.get("Rows");
		for (int i = 0; i < itemList.size(); i++) {
			ParaMap obj = (ParaMap) itemList.get(i);
			sheet.addCell(new Label(0, i + 4, StrUtils.getString(obj
					.getString("no"))));
			sheet.addCell(new Label(1, i + 4, StrUtils.getString(obj
					.getString("status_ch"))));
			sheet.addCell(new Label(2, i + 4, StrUtils.getString(obj
					.getString("canton_name"))));
			sheet.addCell(new Label(3, i + 4, StrUtils.getString(obj
					.getString("business_type_name"))));
			sheet.addCell(new Label(4, i + 4, StrUtils.getString(obj
					.getString("begin_notice_time"))));
			sheet.addCell(new Label(5, i + 4, StrUtils.getString(obj
					.getString("end_notice_time"))));
			sheet.addCell(new Label(6, i + 4, StrUtils.getString(obj
					.getString("begin_limit_time"))));
			sheet.addCell(new Label(7, i + 4, StrUtils.getString(obj
					.getString("area"))));
			sheet.addCell(new Label(8, i + 4, StrUtils.getString(obj
					.getString("end_apply_time"))));
			sheet.addCell(new Label(9, i + 4, StrUtils.getString(obj
					.getString("lisence_count"))));
			sheet.addCell(new Label(10, i + 4, StrUtils.getString(obj
					.getString("user_name"))));
			sheet.addCell(new Label(11, i + 4, StrUtils.getString(obj
					.getString("price_step")), wcfRight));
			sheet.addCell(new Label(12, i + 4, StrUtils.getString(obj
					.getString("earnest_money")), wcfRight));
			sheet.addCell(new Label(13, i + 4, StrUtils.getString(obj
					.getString("trans_price")), wcfRight));
			sheet.addCell(new Label(14, i + 4, StrUtils.getString(obj
					.getString("end_trans_time"))));
			sheet.addCell(new Label(15, i + 4, StrUtils.getString(obj
					.getString("transz"))));
			sheet.addCell(new Label(16, i + 4, StrUtils.getString(obj
					.getString("transs"))));
			
			

		}
		book.write();
		book.close();
		fo.flush();
		fo.close();
		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/vnd.ms-excel; charset=UTF-8");
		String downLoadName = new String(
				(filename + ".xls").getBytes("gb2312"), "ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downLoadName + "\";");
		this.getResponse().getOutputStream();
		this.getResponse().flushBuffer();
		int len = (int) file.length();
		byte[] buf = new byte[len];
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = this.getResponse().getOutputStream();
		len = fis.read(buf);
		os.write(buf, 0, len);
		os.flush();
		fis.close();
		file.delete();
		return new byte[1];
	}
}

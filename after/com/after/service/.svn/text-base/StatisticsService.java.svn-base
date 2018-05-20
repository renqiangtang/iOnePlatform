package com.after.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.FsUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.ResourceUtils;
import com.before.dao.TransGoodsLandDao;

public class StatisticsService extends BaseService {
	public final String moduleNo = "statistics";
	public DataSetDao dao = new DataSetDao();

	/**
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap type(ParaMap inMap) throws Exception {
		String table = inMap.getString("table");
		String sql = "select id,name from " + table;
		ParaMap result = dao.queryData(sql, null);
		ParaMap out = new ParaMap();
		out.put("list", result.getListObj());
		return out;
	}

	public ParaMap targetStatistics(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		String targetName = inMap.getString("targetName");
		String transType = inMap.getString("transType");
		String goodsType = inMap.getString("goodsType");
		String status = inMap.getString("status");
		String beginTime = inMap.getString("beginTime");
		String endTime = inMap.getString("endTime");
		ParaMap sqlParams = new ParaMap();
		ParaMap customConditions = new ParaMap();
		StringBuffer customCondition = new StringBuffer();
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and t.trans_organ_id = '"+organId+"' ");
		}
		if (StringUtils.isNotEmpty(targetName)) {
			customCondition.append("and t.no like '%" + targetName + "%'");
		}
		if (StringUtils.isNotEmpty(transType) && !"-1".equals(transType)){
			customCondition.append(" and t.business_type='" + transType + "'");
		}else{
			if(StrUtils.isNotNull(goodsType)){
				if(goodsType.indexOf(",")!=-1){
					customCondition.append(" and substr(t.business_type,1,3) in ('301','401')");
				}else{
					customCondition.append(" and substr(t.business_type,1,3) in ('"+goodsType+"')");
				}
			}
		}
		if (StringUtils.isNotEmpty(status) && !"-1".equals(status))
			customCondition.append(" and t.status=" + status);
		if (StringUtils.isNotEmpty(beginTime)) {
			customCondition.append(" and t.end_trans_time >=");
			customCondition.append("  to_date('" + beginTime
					+ "', 'yyyy-mm-dd')");
		}
		if (StringUtils.isNotEmpty(endTime)) {
			customCondition.append(" and t.end_trans_time <=");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = sdf.parse(endTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d1);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
			customCondition.append("  to_date('"
					+ sdf.format(calendar.getTime()) + "', 'yyyy-mm-dd')");
		}
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		ParaMap result = dao.queryDataByModuleNo(moduleNo, "targetStatistics",
				sqlParams);
		List resultList = new ArrayList();
		List filds = result.getFields();
		for (int i = 0; i < result.getSize(); i++) {
			ParaMap out = new ParaMap();
			String business_type = null;
			String land_area = null;
			String mineral_area = null;
			for (int j = 0; j < filds.size(); j++) {
				if(filds.get(j).equals("business_type")){
					business_type = result.getRecordString(i, j);
				}
				if(filds.get(j).equals("land_area")){
					land_area = result.getRecordString(i, j);
				}
				if(filds.get(j).equals("mineral_area")){
					mineral_area = result.getRecordString(i, j);
				}
				out.put(filds.get(j), result.getRecordValue(i, j));
			}
			if(StrUtils.isNotNull(business_type) && business_type.startsWith("101")){
				out.put("area", land_area);
			}else{
				out.put("area", mineral_area);
			}
			resultList.add(out);
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", result.getInt("totalRowCount"));

		return out;

	}
	
	
	public ParaMap targetGoodsList(ParaMap inMap)throws Exception{
		String targetId = inMap.getString("targetId");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_goods_scale");
		sqlParams.put("targetId", targetId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", result.getInt("totalRowCount"));
		return out;
	}
	
	public ParaMap targetInfo(ParaMap inMap)throws Exception{
		String targetId = inMap.getString("targetId");
		TransGoodsLandDao dao = new TransGoodsLandDao();
		ParaMap result = dao.getTargetInfo(targetId);
		return result;
	}
	
	public ParaMap setScale(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		if(StrUtils.isNull(targetId)){
			out.put("state", 0);
			out.put("message", "设置失败！标的参数错误。");
			return out;
		}
		String scaleString = inMap.getString("scale");
		if(StrUtils.isNull(scaleString)){
			out.put("state", 0);
			out.put("message", "设置失败！交易物参数错误。");
			return out;
		}
		JSONArray json = JSONArray.fromObject(scaleString);
		ParaMap scaleMap = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		for (int i = 0; i < json.size(); i++) {
			JSONObject jsonObj = json.getJSONObject(i);
			String rel_id = String.valueOf(jsonObj.get("rel_id"));
			String scale = String.valueOf(jsonObj.get("scale"));
			scaleMap.clear();
			scaleMap.put("id", rel_id);
			scaleMap.put("scale", scale);
			dataSetDao.updateData("trans_target_goods_rel", "id", scaleMap);
		}
		out.put("state", 1);
		out.put("message", "设置成功！");
		return out;
	}
	
	
	public byte[] downLog(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_goods_scale");
		sqlParams.put("targetId", targetId);
		List goodsList = dataSetDao.queryData(sqlParams).getListObj();
		sqlParams.clear();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_offerlog_divide");
		sqlParams.put("targetId", targetId);
		List offerLogList = dataSetDao.queryData(sqlParams).getListObj();
		sqlParams.clear();
		sqlParams.put("id", targetId);
		ParaMap target = dataSetDao.querySimpleData("trans_target", sqlParams);
		String targetName = "标的";
		String unit = "元";
		if(target !=null && target.getSize()>0){
			targetName = target.getRecordString(0, "name");
			unit = target.getRecordString(0, "unit");;
		}
		
		String[] colum1 = new String[3+goodsList.size()];
		String[] colum2 = new String[3+goodsList.size()];
		colum1[0]="竞买人";
		colum1[1]="出价金额("+unit+")";
		colum1[2]="出价时间";
		colum2[0]="bidder_name";
		colum2[1]="price";
		colum2[2]="offer_date";
		for(int i = 0 ; i < goodsList.size() ; i ++ ){
			colum1[i+3] = ((ParaMap)goodsList.get(i)).getString("no")+"("+((ParaMap)goodsList.get(i)).getString("scale")+"%)";
			colum2[i+3] = ((ParaMap)goodsList.get(i)).getString("id");
		}
		List list = new ArrayList();
		for(int i = 0 ; i < offerLogList.size() ; i ++ ){
			ParaMap listMap = new ParaMap();
			ParaMap offerLog = (ParaMap)offerLogList.get(i);
			BigDecimal price = offerLog.getBigDecimal("price");
			listMap.put("bidder_name", offerLog.getString("bidder_name"));
			listMap.put("offer_date", offerLog.getString("offer_date"));
			listMap.put("price", this.formatMoney(offerLog.getBigDecimal("price"), unit));
			for(int j = 0 ; j < goodsList.size() ; j ++ ){
				ParaMap goods = (ParaMap)goodsList.get(j);
				String goodsId = goods.getString("id");
				BigDecimal scale = goods.getBigDecimal("scale");
				listMap.put(goodsId, this.formatMoney(price.multiply(scale).divide(new BigDecimal(Math.pow(10,2)), 2, BigDecimal.ROUND_HALF_UP),unit));
			}
			list.add(listMap);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		String nowStr = sdf.format(new Date());
		String file = this.createExcel(targetName+"出价记录("+nowStr+").xls", "offerLogExcel", colum1, colum2, list);
		byte[] buf = this.downloadFile(targetName+"出价记录("+nowStr+").xls", file);
		return buf;
	}
	
	
	
	
	  /** 
     * 生成Excel文件 
     * @param path 
     * @param dataList 
     * @return 
     */  
    public String createExcel(String filename ,String path,String[] column1 ,String[] column2, List dataList) throws Exception{  
        File file = FsUtils.get(path);  
        file = new File(file,filename);  
        WritableWorkbook book=null;  
        try {  
            book = Workbook.createWorkbook(file);  
            WritableSheet sheet = book.createSheet(file.getName(), 0);  
            this.setHeader(sheet,column1); //设置Excel标题信息  
            this.setBody(sheet,dataList,column2); // 设置Excel内容主体信息  
            book.write();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (WriteException e) {  
            e.printStackTrace();  
        } catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            try {  
                book.close();  
            } catch (WriteException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file.getAbsolutePath();  
    } 
	
	
	/** 
     * 设置Excel标题信息 
     * @param sheet 
     * @param column 
     * @throws WriteException 
     */  
    public void setHeader(WritableSheet sheet,String[] column) throws WriteException{ 
    	WritableFont font = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
        WritableCellFormat headerFormat = new  WritableCellFormat(font);  
        headerFormat.setAlignment(Alignment.CENTRE);  //水平居中对齐  
        headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);   //竖直方向居中对齐  
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);  
        for(int i=0;i<column.length;i++){  
            Label label = new Label(i,0,column[i],headerFormat);  
            sheet.addCell(label);  
            sheet.setColumnView(i, 30);  
            sheet.setRowView(0, 500);  
        }  
    }  
    
    /** 
     * 设置Excel内容主体信息 
     * @param sheet 
     * @param rowList 
     * @param column 
     * @throws Exception 
     */  
    public void setBody(WritableSheet sheet,List rowList,String[] column) throws Exception{  
       WritableCellFormat bodyFormat = new  WritableCellFormat();  
       bodyFormat.setAlignment(Alignment.CENTRE); //水平居中对齐  
       bodyFormat.setVerticalAlignment(VerticalAlignment.CENTRE);   //竖直方向居中对齐  
       bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);  
       Label label = null;  
       for(int i=0;i<rowList.size();i++){  
           ParaMap obj = (ParaMap)rowList.get(i);  
           for(int j=0;j<column.length;j++){  
              label = new Label(j,i+1,String.valueOf(((ParaMap)obj).get(column[j].toLowerCase())),bodyFormat);  
              sheet.addCell(label);  
              sheet.setRowView(i+1, 350);  
           }  
       }  
    }  
    
    public String formatMoney(BigDecimal money , String unit) throws Exception{
		if(money == null ){
			return null;
		}
		if(unit == null || "".equals(unit) || unit.equalsIgnoreCase("null")){
			unit = "元";
		}
		if(unit.equals("元")){ //如果是元格式化为千分位逗号
			DecimalFormat df = new DecimalFormat("###,###,###,###.00");
			return df.format(money.doubleValue());
		}else if(unit.equals("万元")){//如果是万元保留4为小数
			DecimalFormat df = new DecimalFormat("###,###,###,###.####");
			return df.format((money.divide(new BigDecimal(Math.pow(10,4)), 4, BigDecimal.ROUND_HALF_UP))); 
		}else{
			DecimalFormat df = new DecimalFormat("###########.00");
			return df.format(money.doubleValue());
		}
	}
    
    
    /**
	 * 下载文件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadFile(String fileName , String filePath) throws Exception {
		this.getResponse().reset();
		this.getResponse().addHeader("Content-Disposition","attachment; filename=\"" + new String(fileName.getBytes("gb2312"),"ISO8859-1")+ "\";");
		this.getResponse().setContentType("application/octet-stream; charset=UTF-8");
		byte[] buf = ResourceUtils.getBytes(filePath);
		return buf;
    }
	
	
	public ParaMap immediateStatistics(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		String targetNo = inMap.getString("targetNo");
		String businessType1 = inMap.getString("businessType");
		String bidderName = inMap.getString("bidderName");
		String beginTime = inMap.getString("beginTime");
		String endTime = inMap.getString("endTime");
		String goodsType = inMap.getString("goodsType");
		ParaMap sqlParams = new ParaMap();
		sqlParams.putAll(inMap);
		ParaMap customConditions = new ParaMap();
		StringBuffer customCondition = new StringBuffer();
		if(StrUtils.isNotNull(organId)){
			customCondition.append(" and tt.trans_organ_id = '"+organId+"' ");
		}
		if (StringUtils.isNotEmpty(targetNo)) {
			customCondition.append("and tt.no like '%" + targetNo + "%'");
		}
		if (StringUtils.isNotEmpty(businessType1)) {
			customCondition.append("and tt.business_type like '%" + businessType1 + "%'");
		}
		if (StringUtils.isNotEmpty(bidderName)) {
			customCondition.append("and get_union_bidder(tl.id) like '%" + bidderName + "%'");
		}
		if (StringUtils.isNotEmpty(beginTime)) {
			customCondition.append(" and tt.end_trans_time >=");
			customCondition.append("  to_date('" + beginTime + "', 'yyyy-mm-dd')");
		}
		if (StringUtils.isNotEmpty(endTime)) {
			customCondition.append(" and tt.end_trans_time <=");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = sdf.parse(endTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d1);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
			customCondition.append("  to_date('"+ sdf.format(calendar.getTime()) + "', 'yyyy-mm-dd')");
		}
		if (StringUtils.isNotEmpty(goodsType)) {
			if(goodsType.indexOf(",")!=-1){
				customCondition.append(" and substr(tt.business_type,1,3) in ('301','401') ");
			}else{
				customCondition.append(" and substr(tt.business_type,1,3) in ('" + goodsType + "') ");
			}
		}
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_succ_target_price_info");
		ParaMap result = dao.queryData(sqlParams);
		List targetList = result.getListObj();
		List resultList = new ArrayList();
		if(targetList!=null && targetList.size()>0){
			for(int i = 0 ; i< targetList.size(); i++){
				ParaMap target = (ParaMap)targetList.get(i);
				String businessType = target.getString("business_type");
				BigDecimal goods_size = businessType.startsWith("101")?target.getBigDecimal("land_area"):target.getBigDecimal("mineral_area");
				BigDecimal goods_size_mu = goods_size.divide(new BigDecimal("666.67"), 2, BigDecimal.ROUND_HALF_UP);
				BigDecimal begin_price = target.getBigDecimal("begin_price");
				BigDecimal begin_price_wan = begin_price.divide(new BigDecimal(Math.pow(10,4)), 2, BigDecimal.ROUND_HALF_UP);
				BigDecimal trans_price = target.getBigDecimal("trans_price");
				BigDecimal trans_price_wan=null;
				if(trans_price!=null){
					
					trans_price_wan = trans_price.divide(new BigDecimal(Math.pow(10,4)), 2, BigDecimal.ROUND_HALF_UP);
				}
				BigDecimal unit_price = new BigDecimal("0");
				if(goods_size_mu.compareTo(new BigDecimal("0"))>0){
					if(trans_price_wan!=null){
						unit_price = trans_price_wan.divide(goods_size_mu, 2, BigDecimal.ROUND_HALF_UP);
					}else{
						unit_price=null;
					}
				}
				BigDecimal more_price=null;
				BigDecimal more_price_rate=null;
				if(trans_price_wan!=null){
				 more_price = trans_price_wan.subtract(begin_price_wan);
				 more_price_rate = more_price.divide(begin_price_wan, 2, BigDecimal.ROUND_HALF_UP);
				}
				target.put("goods_size", goods_size);
				target.put("goods_size_mu", goods_size_mu);
				target.put("begin_price", begin_price_wan);
				target.put("trans_price", trans_price_wan);
				target.put("unit_price", unit_price);
				target.put("more_price", more_price);
				target.put("more_price_rate", more_price_rate);
				resultList.add(target);
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", result.getInt("totalRowCount"));

		return out;

	}
	
	
	
	public ParaMap tradeBeforeStatistics(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_trade_before");
		sqlParams.put("targetId", targetId);
		ParaMap result = dao.queryData(sqlParams);
		List resultList = result.getListObj();
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", result.getInt("totalRowCount"));
		return out;
	}
	
	
	public ParaMap tradeMiddleStatistics(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_trade_middle");
		sqlParams.put("targetId", targetId);
		ParaMap result = dao.queryData(sqlParams);
		List resultList = result.getListObj();
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", result.getInt("totalRowCount"));
		return out;
	}
	
	
	public ParaMap tradeAfterStatistics(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "statistics");
		sqlParams.put("dataSetNo", "query_target_trade_after");
		sqlParams.put("targetId", targetId);
		ParaMap result = dao.queryData(sqlParams);
		List resultList = result.getListObj();
		ParaMap out = new ParaMap();
		out.put("Rows", resultList);
		out.put("Total", result.getInt("totalRowCount"));
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
		if(result.getSize()>0){
			List filds = result.getFields();
			List record = (List)result.getRecords().get(0);
			if(filds != null && filds.size()>0 ){
				for(int i = 0 ; i < filds.size() ; i ++ ){
					if(filds.get(i).equals("id")){
						organId = (String)record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}
  
}  


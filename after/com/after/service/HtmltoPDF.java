
package com.after.service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.lowagie.text.pdf.BaseFont;


public class HtmltoPDF {
		/**
	     * @param os
	     * @param inMap
	     * @throws Exception
	     */
	    public static void QuoteListPdf(OutputStream os,ParaMap inMap) throws Exception {     
	    	ITextRenderer renderer = new ITextRenderer();     
	    	ITextFontResolver fontResolver = renderer.getFontResolver(); 
	    	fontResolver.addFont("C:/Windows/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED); 
	    	StringBuffer html =new StringBuffer("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	    	html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
	    	html.append("<style type=\"text/css\">");
	    	html.append("body {font-family: SimSun;font-size: 12px;line-height: 25px;}");
	    	html.append("h2 {padding-bottom: 15px;}");
	    	html.append(".pt30 {padding-top: 30px;}");
	    	html.append("h1 {padding-bottom: 15px;}");
	    	html.append(".tc {text-align: center}");
	    	html.append(".tr {text-align: right}");
	    	html.append(".top_infor td {height: 43px;line-height: 43px;}");
	    	html.append(".border_bottom {line-height: 15px;height: 15px;border-bottom: 1px solid #3d3c3c;width: 190px;display: inline-block;}");
	    	html.append(".price_t {border-top: 1px solid #3d3c3c;border-left: 1px solid #3d3c3c;margin-bottom: 15px;}");
	    	html.append(".price_t .num {border-bottom: 1px solid #3d3c3c;border-right: 1px solid #3d3c3c;background-color: #CCC;height: 39px;line-height: 39px;}");
	    	html.append(".price_t td {border-bottom: 1px solid #3d3c3c;border-right: 1px solid #3d3c3c;height: 39px;line-height: 39px;}");
	    	html.append("h1 {font-size: 29px;}");
	    	html.append(".pl5 {padding-left: 5px;}");
	    	html.append(".bottom_infor td {padding-left: 5px;padding-bottom: 25px;}");
	    	html.append("</style>");
	    	html.append("</head>");
	    	html.append("<body style='overflow-x:hidden;'>");
	    	html.append("<h2 class=\"tc pt30\">"+inMap.getString("canton")+"国土资源局国有土地使用权挂牌出让公告</h2>");
	    	html.append("<h2 class=\"tc pt30\">"+inMap.getString("no")+"</h2><h2 class=\"tc\">竞价起始价人民币"+inMap.getString("begin_price")+"万元</h2>");
	    	html.append("<table width='690' border='0' align='center' cellpadding='0' cellspacing='0' class='top_infor'>");
	    	html.append("<tr><td width='61' class='pl5'>主持人:</td><td width='364'><span class='border_bottom'></span></td><td width='59'>记录员:</td><td width='206'><span class='border_bottom'></span></td></tr>");
	    	html.append("<tr><td class='pl5'>竞得人:</td><td><span class='border_bottom'>"+inMap.getString("vendeeMan")+"</span></td><td colspan='2'>&nbsp;</td></tr>");
	    	html.append("</table>");
	    	html.append("<h1 class='tc'>现场竞价登记情况 </h1>");
	    	html.append("<table width='690' border='0' align='center' cellpadding='0' cellspacing='0' class='price_t'>");
	    	html.append("<tr><td class='num tc'>牌号</td><td class='tc'>叫价（万元）</td><td class='num tc'>牌号</td><td class='tc'>叫价（万元）</td></tr>");
	    	List logs = (List) inMap.get("logs");
	    	int num = 0;
	    	String str = "<tr>"; 
	    	String s = "";
	    	//16 15
	    	for(int x=0;x<logs.size();x++){
	    		ParaMap temp = (ParaMap) logs.get(x);
	    		String price = temp.getString("price");
	    		String license_no = temp.getString("license_no");
	    		str+="<td class='num tc'>"+license_no+"</td><td  class='tc'>"+price+"</td>";
	    		num++;
	    		if(num==2 && x!=(logs.size()-1)){
	    			str+="</tr>\r\n";
	    			html.append(str);
	    			s=str+s;
	    			str = "<tr>";
	    			num = 0;
	    		}else if(x==(logs.size()-1) && num ==1){
	    			str+="<td class='num tc'>-</td><td  class='tc'>-</td></tr>\r\n";
	    			html.append(str);
	    			s=str+s;
	    			str = "<tr>";
	    			num = 0;
	    		}
	    	}
	    	System.err.println(s);
	    	html.append("<tr> <td colspan='4' class='pl5'>竞买成交牌号："+inMap.getString("license_no")+"</td></tr>");
	    	String trust_price = inMap.getString("trust_price");
	    	float parseFloat = Float.parseFloat(trust_price);
	    	String digitUppercase = Change.digitUppercase(parseFloat);
	    	html.append("<tr><td colspan='4' class='pl5'>竞买国有建设用地使用使成交价："+digitUppercase+"</td></tr>");
	    	html.append("<tr><td colspan='4' class='pl5'>备注：</td></tr>");
	    	html.append("</table>");
	    	html.append("<table width='690' border='0' align='center' cellpadding='0' cellspacing='0' class=' bottom_infor'>");
	    	html.append("<tr><td>委托人签名：</td> </tr>");
	    	html.append("<tr><td>县国土资源局领导签名：</td></tr>");
	    	html.append("<tr><td style='padding-bottom:39px;'>县国土资源交易中心领导签名：</td></tr>");
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	String nowDate = Change.stringToChineseSmall(sdf.format(new Date()));
	    	html.append("<tr><td class='tr'>"+nowDate+"</td></tr>");
	    	html.append("</table>");
	    	html.append("</body>");
	    	html.append("</html>");
	    	renderer.setDocumentFromString(html.toString());
	    	renderer.layout();
	    	renderer.createPDF(os);
	    	os.close(); 
	    }
	    
	    /**
	     * @param os
	     * @param inMap
	     * @throws Exception
	     */
	    public static void EnlistListPdf(OutputStream os,ParaMap inMap) throws Exception {     
	    	ITextRenderer renderer = new ITextRenderer();     
	    	ITextFontResolver fontResolver = renderer.getFontResolver(); 
	    	fontResolver.addFont("C:/Windows/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED); 
	    	StringBuffer html =new StringBuffer("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	    	html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		    html.append("<head>");
			html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			html.append("<style type=\"text/css\">");
			html.append("body {font-family: SimSun;font-size: 12px;line-height: 25px;}");
			html.append("h2 {padding-bottom: 15px;}");
			html.append(".pt30 {padding-top: 30px;}");
			html.append(".tc {text-align: center}");
			html.append(".price_t {border-top: 1px solid #3d3c3c;border-left: 1px solid #3d3c3c;margin-bottom: 15px;}");
			html.append(".price_t th {border-bottom: 1px solid #3d3c3c;border-right: 1px solid #3d3c3c;background-color: #CCC;height: 45px;font-weight:normal;}");
			html.append(".price_t td {border-bottom: 1px solid #3d3c3c;border-right: 1px solid #3d3c3c;height: 45px;}");
			html.append("</style>");
			html.append("</head>");
			html.append("<body>");
			html.append("<h2 class=\"tc pt30\">领取挂牌转让国有建设用地使用权应价牌</h2>");
			html.append("<h2 class=\"tc\">及《"+inMap.getString("canton")+"国有建设用地使用权挂牌转让现场竞价须知》</h2>");
			html.append("<p class=\"tc\">"+inMap.getString("no")+"</p>");
			html.append("<p class=\"tc\" style=\"font-size:25px;\"><b>登&nbsp;记&nbsp;表</b></p>");
			html.append("<table width=\"690\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"price_t\">");
			html.append("<tr>");
			html.append("<th width=\"47\" align=\"center\" valign=\"middle\">编号</th>");
			html.append("<th width=\"384\" align=\"center\" valign=\"middle\" class=\"tc\">竞买单位或人人</th>");
			html.append("<th width=\"55\" align=\"center\" valign=\"middle\">应价<br />");
			html.append("牌号</th>");
			html.append("<th width=\"103\" align=\"center\" valign=\"middle\" class=\"tc\">企业<br />");
			html.append("性质</th>");
			html.append("<th width=\"100\" align=\"center\" valign=\"middle\">领取牌号<br />");
			html.append("人签名</th>");
			html.append("</tr>");
			List logs = (List) inMap.get("logs");
			for(int x = 0;x < logs.size();x++){
				ParaMap temp =  (ParaMap) logs.get(x);
				int sort = x+1;
				String bidderName = temp.getString("name");
				String regType  = temp.getString("reg_type");
				String license_no  = temp.getString("license_no");
				if(regType.equals("null")){
					regType = "-";
				}
				html.append("<tr>");
				html.append("<td align=\"center\" valign=\"middle\">"+sort+"</td>");
				html.append("<td align=\"center\" valign=\"middle\" class=\"tc\">"+bidderName+"</td>");
				html.append("<td align=\"center\" valign=\"middle\">"+license_no+"</td>");
				html.append("<td align=\"center\" valign=\"middle\" class=\"tc\">"+regType+"</td>");
				html.append("<td align=\"center\" valign=\"middle\">-</td>");
				html.append("</tr>");
			}
			html.append("</table>");
			html.append("</body></html>");
	    	renderer.setDocumentFromString(html.toString());
	    	renderer.layout();
	    	renderer.createPDF(os);
	    	os.close(); 
	    }
	    
	    
	   
}


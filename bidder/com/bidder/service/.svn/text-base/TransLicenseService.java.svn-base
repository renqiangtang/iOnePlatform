package com.bidder.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.bidder.dao.TransLicenseDao;
import com.lowagie.text.pdf.BaseFont;
import com.sysman.dao.UserDao;

public class TransLicenseService extends BaseService {

	/**
	 * 购物车标的数量
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getCartTargetNum(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String goodsType = inMap.getString("goodsType");
		String bidderId = this.getBidderId(userId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_main");
		sqlParams.put("dataSetNo", "get_cart_target_num");
		sqlParams.put("bidderId", bidderId);
		sqlParams.put("cartType", 0);
		String sql = "";
		if(goodsType.equals("301")){
			sql = " and substr(tt.business_type,1,3) in ('301','401') ";
		}else{
			sql = " and substr(tt.business_type,1,3) ='"+goodsType+"' ";
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap result = dataSetDao.queryData(sqlParams);
		List list = result.getListObj();
		ParaMap out = new ParaMap();
		if (list != null && list.size() > 0) {
			out = (ParaMap) list.get(0);
		} else {
			out.put("num", 0);
		}
		return out;
	}

	/**
	 * 购物车标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap carTargetList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String type = inMap.getString("type");
		int page = 0;
		if (StrUtils.isNotNull(inMap.getString("page"))) {
			page = inMap.getInt("page");
		}
		int pagesize = 0;
		if (StrUtils.isNotNull(inMap.getString("pagesize"))) {
			page = inMap.getInt("pagesize");
		}
		type = StrUtils.isNull(type) ? "1" : type;
		String bidderId = this.getBidderId(userId);
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap result = licenseDao.getCartTargetList(bidderId, type, page,
				pagesize);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", this.changeToObjList(result, null));
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 标的保证金列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap earnestMoneyList(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap result = dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", this.changeToObjList(result, null));
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 加入购物车
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap addToCar(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String targetId = inMap.getString("targetId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		// 校验当前标的状态及是否已经购买
		ParaMap result = licenseDao.isCanBuy(bidderId, targetId);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "加入购物车失败！" + result.getString("message"));
			return out;
		}
		// 判断当前有无正在使用的购物车
		String cartId = null;
		sqlParams.clear();
		sqlParams.put("bidder_id", bidderId);
		sqlParams.put("status", 0);
		ParaMap cartMap = dataSetDao.querySimpleData("trans_cart", sqlParams,
				" create_date desc ");
		if (cartMap.getSize() > 0) {
			cartId = (String) cartMap.getRecordValue(0, "id");
		} else {
			cartId = licenseDao.saveCart(bidderId, userId);
		}
		// 判断是否在购物车明细里
		sqlParams.clear();
		sqlParams.put("cart_id", cartId);
		sqlParams.put("target_id", targetId);
		ParaMap dtlMap = dataSetDao.querySimpleData("trans_cart_dtl",
				sqlParams, " create_date desc ");
		if (dtlMap.getSize() > 0) {
			String dtlId = String.valueOf(dtlMap.getRecordValue(0, "id"));
			int status = Integer.parseInt(String.valueOf(dtlMap.getRecordValue(
					0, "status")));
			if (status == 1) {
				out.put("state", 0);
				out.put("message", "加入购物车失败！该标的已经在购物车中了。");
			} else if (status == 2) {
				out.put("state", 0);
				out.put("message", "加入购物车失败！该标的您已申请竞买了。");
			} else {
				sqlParams.clear();
				sqlParams.put("id", dtlId);
				sqlParams.put("status", 1);
				ParaMap rdMap = dataSetDao.updateData("trans_cart_dtl", "id",
						sqlParams);
				if ("1".equals(rdMap.getString("state"))) {
					out.put("state", 1);
					out.put("message", "加入购物车成功！");
				} else {
					out.put("state", 0);
					out.put("message", "加入购物车失败！");
				}
			}
		} else {
			String dtlId = licenseDao.saveCartDtl(cartId, targetId, userId);
			if (StrUtils.isNotNull(dtlId)) {
				out.put("state", 1);
				out.put("message", "加入购物车成功！");
			} else {
				out.put("state", 0);
				out.put("message", "加入购物车失败！");
			}
		}
		return out;
	}

	/**
	 * 从购物车中删除
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap removeFromCar(ParaMap inMap) throws Exception {
		String dtlId = inMap.getString("dtlId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap out = licenseDao.removeCartDtl(dtlId);
		return out;
	}

	/**
	 * 从购物车中删除
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteCarTarget(ParaMap inMap) throws Exception {
		String dtlId = inMap.getString("dtlId");
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap out = licenseDao.deleteCarTarget(dtlId);
		return out;
	}

	/**
	 * 购物列表页面查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initProductListPageParam(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap organMap = licenseDao.getTransOrganList(bidderId);
		String organIds = licenseDao.getOrganIdsByMap(organMap);
		ParaMap out = new ParaMap();
		if (StrUtils.isNotNull(organIds)) {
			ParaMap cantonMap = licenseDao.getTransCantonList(organIds);
			ParaMap businessTypeMap = licenseDao.getTransBusinessTypeList(organIds);
			ParaMap transTypeMap = licenseDao.getTransTransactionTypeList(organIds);
			out.put("cantons", cantonMap.getListObj());
			out.put("businessTypes", businessTypeMap.getListObj());
			out.put("transTypes", transTypeMap.getListObj());
		}
		return out;
	}
	   /**
     * 将html转换成pad
     * 
     * @param inMap
     * @return
     * @throws Exception
     */
    public ParaMap htmlToPdf(ParaMap inMap) throws Exception {
    	String pdfId = UUID.randomUUID().toString().replace("-", "");
		String outputFile = AppConfig.getPro("fileRoot") + "\\pdf\\" + pdfId + ".pdf";
		String htmlContent = inMap.getString("htmlContent");// html内容
		htmlContent = htmlContent.toLowerCase();
		String webFoder = "file:///" + System.getProperty("user.dir") + "/web/base/skins/default/css";
		htmlContent = htmlContent.replace("css/all.css", webFoder + "/all.css");
		if (htmlContent.indexOf("=text/css") != -1) {
			htmlContent = htmlContent.replace("text/css", "\"text/css\"");
		}
		if (htmlContent.indexOf("rel=stylesheet") != -1) {
			htmlContent = htmlContent.replace("rel=stylesheet", "rel=\"stylesheet\"");
		}
		if (htmlContent.indexOf("css\">") != -1) {
			htmlContent = htmlContent.replace("css\">", "css\"></link>");
		}
		if (htmlContent.indexOf("utf-8\">") != -1) {
			htmlContent = htmlContent.replace("utf-8\">", "utf-8\"></meta>");
		}
		if (htmlContent.indexOf(" type=text") != -1) {
			htmlContent = htmlContent.replace("type=text/javascript", "type=\"text/javascript\"");
		}

		if (htmlContent.indexOf("align=middle") != -1) {
			htmlContent = htmlContent.replace("align=middle", "align=\"middle\"");
		}
		if (htmlContent.indexOf("bgcolor=#f5f5f5") != -1) {
			htmlContent = htmlContent.replace("bgcolor=#f5f5f5", "bgcolor=\"#f5f5f5\"");
		}
		if (htmlContent.indexOf("height=28") != -1) {
			htmlContent = htmlContent.replace("height=28", "height=\"28\"");
		}
		if (htmlContent.indexOf("height=30") != -1) {
			htmlContent = htmlContent.replace("height=30", "height=\"30\"");
		}
		if (htmlContent.indexOf("<br><br>") != -1) {
			htmlContent = htmlContent.replace("<br><br>", "<br></br>");
		}
		if (htmlContent.indexOf("content-type>") != -1) {
			htmlContent = htmlContent.replace("content-type>", "\"content-type\"></meta>");
		}
		htmlContent = replaceString(htmlContent, 0, "id=");
		htmlContent = replaceString(htmlContent, 0, "name=");
		htmlContent = replaceString(htmlContent, 0, "class=");
		htmlContent = replaceString(htmlContent, 0, "border=");
		htmlContent = replaceString(htmlContent, 0, "cellspacing=");
		htmlContent = replaceString(htmlContent, 0, "cellpadding=");
		htmlContent = replaceString(htmlContent, 0, "width=");
		htmlContent = replaceString(htmlContent, 0, "height=");
		htmlContent = replaceString(htmlContent, 0, "colspan=");
		htmlContent = replaceString(htmlContent, 0, "align=");
		htmlContent = htmlContent.replace("&nbsp;&nbsp;", "&nbsp;");
		htmlContent = htmlContent.replace("<body style=\"font-family:宋体\">", "<body>");
		StringBuffer styleStr = new StringBuffer("<style>");
		styleStr.append("body {font-family: SimSun;line-height: 240%;font-size: 22px;}");
		styleStr.append("#fullname {font-size: 48px;color: #c00;}");
		styleStr.append(".i-cert_wrap_main{line-height: 240%;}");
		styleStr.append(".i-cert_wrap_main p{margin:0;padding-bottom:0.5em;text-indent: 48px;}");
		styleStr.append(".c4 {color: #215DB7;}");
		styleStr.append(".dl_title {height: 32px;font-size: 32px;text-align: center;font-weight: 400;color: #c00;margin-bottom:0.5em}");
		styleStr.append(".dl_biddername {margin:0;font-size: 22px;font-weight: bold;}");
		styleStr.append(".i-cwm_title3 {margin-bottom:0.5em;text-align: right;height: 30px;line-height: 30px; font-weight: bold;}");
		styleStr.append("</style>");
		htmlContent = htmlContent.replace("</head>", styleStr.toString() + "</head>");
		if(inMap.getString("bidderName") != null && !inMap.getString("bidderName").equals("")){
			htmlContent = htmlContent.replace(inMap.getString("bidderName").toLowerCase(), inMap.getString("bidderName"));
		}
		if(inMap.getString("targetName") != null && !inMap.getString("targetName").equals("")){
			htmlContent = htmlContent.replace(inMap.getString("targetName").toLowerCase(), inMap.getString("targetName"));
		}
		
		OutputStream os = new FileOutputStream(outputFile);
		ITextRenderer renderer = new ITextRenderer();
		ITextFontResolver fontResolver = renderer.getFontResolver();
		fontResolver.addFont("C:/Windows/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		StringBuffer html = new StringBuffer("");
		// DOCTYPE 必需写否则类似于 这样的字符解析会出现错误
		html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		html.append(htmlContent);
		html.append("</html>");
		System.out.println(html.toString());
		renderer.setDocumentFromString(html.toString());
		// 解决图片的相对路径问题
		// renderer.getSharedContext().setBaseURL("file:/F:/teste/html/");
		renderer.layout();
		renderer.createPDF(os);
		ParaMap out = new ParaMap();
		out.put("state", 1);
		out.put("pdfId", pdfId);
		os.close();
		return out;
    }
    /**
     * 
     * @param htmlContent
     *            要替换的内容
     * @param fromIndex
     *            从字符串哪个位置开始查找
     * @param findStr
     *            要查找的字符串标识
     * @return
     */
    public String replaceString(String htmlContent, int fromIndex,
            String findStr) {
        int idindex = htmlContent.indexOf(findStr, fromIndex);
        int subindex = 0;
        if (idindex != -1) {
            if (!htmlContent.substring(idindex + findStr.length(),
                    idindex + (findStr.length() + 1)).equals("\"")) {
                int nextl = htmlContent.indexOf(">", idindex);// 查找到第一个右尖括号
                int nextn = htmlContent.indexOf(" ", idindex);// 查找到第一个空格
                if (nextn == -1) {
                    subindex = nextl;
                } else if (nextl - nextn > 0) {// 尖括号的位置大于空格的位置
                    subindex = nextn;
                } else if (nextl - nextn < 0) {
                    subindex = nextl;
                }
                htmlContent = htmlContent.substring(0,
                        idindex + findStr.length())
                        + "\""
                        + htmlContent.substring(idindex + findStr.length(), subindex)
                        + "\""
                        + htmlContent.substring(subindex);
                htmlContent = replaceString(htmlContent, subindex, findStr);
            } else {
                if (htmlContent.lastIndexOf(findStr) == idindex) {
                    return htmlContent;
                } else {
                    htmlContent = replaceString(htmlContent, idindex + findStr.length(), findStr);
                }
            }
        } else {
            return htmlContent;
        }
        return htmlContent;
    }


	public ParaMap getEarnestMoney(ParaMap inMap) throws Exception {
		TransLicenseDao licenseDao = new TransLicenseDao();
		String id = inMap.getString("targetId");
		String userId = inMap.getString("u");
		ParaMap targetMap = new ParaMap();
		targetMap.put("target_earnest_money",licenseDao.getTargetEarnestMoneyList(id));
		UserDao userDao = new UserDao();
		ParaMap bidderMap = userDao.getBidderData(null, null, userId);
		if(bidderMap !=null && bidderMap.getSize()>0){
			targetMap.put("bidder",(ParaMap)(bidderMap.getListObj().get(0)));
		}
		ParaMap target = licenseDao.getTransTarget(id);
		if(target !=null && target.getSize()>0){
			targetMap.put("unit",target.getRecordString(0, "unit"));
		}
		ParaMap licenseApplyMap=new ParaMap();
		licenseApplyMap.put("target_id", id);
		licenseApplyMap.put("bidder_id", this.getBidderId(userId));
		DataSetDao dataSetDao=new DataSetDao();
		ParaMap outApplyDateMap=dataSetDao.querySimpleData("trans_license", licenseApplyMap);
		if(outApplyDateMap.getSize()>0){
			targetMap.put("stdTime", outApplyDateMap.getRecordValue(0, "apply_date"));
		}else{
			targetMap.put("stdTime", DateUtils.nowStr());
		}
		return targetMap;
	}

	/**
	 * 标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap transTargetlist(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String cantonId = inMap.getString("cantonId");
		String businessTypeId = inMap.getString("businessTypeId");
		String transTypeId = inMap.getString("transTypeId");
		String pageStr = inMap.getString("page");
		int page = 1;
		if (StrUtils.isNotNull(pageStr)) {
			try {
				page = Integer.parseInt(pageStr);
				page = page < 1 ? 1 : page;
			} catch (Exception e) {
				page = 1;
			}
		}
		inMap.put(DataSetDao.SQL_PAGE_INDEX, page);
		inMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, 6);
		String bidderId = this.getBidderId(userId);
		inMap.put("bidderId", bidderId);
		String sql = "";
		if (StrUtils.isNotNull(cantonId)) {
			sql = sql + " and tg.canton_id = :cantonId ";
		}
		if (StrUtils.isNotNull(businessTypeId)) {
			sql = sql + " and tt.business_type = :businessTypeId ";
		}
		if (StrUtils.isNotNull(transTypeId)) {
			sql = sql + " and tt.trans_type_label = :transTypeId ";
		}
		sql=sql+" and tg.goods_type in ('101')";
		TransLicenseDao licenseDao = new TransLicenseDao();
		ParaMap out = licenseDao.transTargetlist(inMap, sql);
		int totalRowCount = out.getInt("totalRowCount");
		List resultList = new ArrayList();
		if (out.getSize() > 0) {
			List filds = out.getFields();
			for (int i = 0; i < out.getSize(); i++) {
				ParaMap targetMap = new ParaMap();
				List record = (List) out.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						targetMap.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				targetMap.put("target_earnest_money",licenseDao.getTargetEarnestMoneyList(id));
				int is_cart = Integer.parseInt(targetMap.getString("is_cart"));
				int is_union = Integer.parseInt(targetMap.getString("is_union"));
				int is_suspend = Integer.parseInt(targetMap.getString("is_suspend"));
				Date begin_apply_time = null;
				String begin_apply_str = targetMap.getString("begin_apply_time");
				if (StrUtils.isNotNull(begin_apply_str)) {
					begin_apply_time = targetMap.getDate("begin_apply_time");
				}
				Date end_apply_time = null;
				String end_apply_str = targetMap.getString("end_apply_time");
				if (StrUtils.isNotNull(end_apply_str)) {
					end_apply_time = targetMap.getDate("end_apply_time");
				}

				if(is_suspend==1){
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的已中止");
				}else if (is_cart == 1) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的已选入购物车");
				} else if (is_cart == 2) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的已做竞买申请");
				} else if (is_union == 1) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的被联合竞买人选择");
				} else if (begin_apply_time == null || end_apply_time == null) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的竞买申请时间错误");
				} else if ((DateUtils.now()).getTime() < begin_apply_time
						.getTime()) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的未到竞买申请时间");
				} else if ((DateUtils.now()).getTime() > end_apply_time
						.getTime()) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "本标的已超过竞买申请时间");
				} else {
					targetMap.put("is_bid", 1);
					targetMap.put("bid_message", "能够购买");
				}
				resultList.add(targetMap);
			}
		}
		out.clear();
		out.put("Rows", resultList);
		out.put("rowTotal", totalRowCount);
		out.put("page", page);
		out.put("pageTotal", this.getPageTotal(totalRowCount, 6));
		return out;

	}

	public String getBidderId(String userId) throws Exception {
		String bidderId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.querySimpleData("sys_user", sqlParams);
		String user_type = null;
		String ref_id = null;
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("user_type")) {
						user_type = String.valueOf(record.get(i));
					}
					if (filds.get(i).equals("ref_id")) {
						ref_id = (String) record.get(i);
					}
				}
			}
		}
		if (StrUtils.isNotNull(user_type) && "1".equals(user_type)) {
			bidderId = ref_id;
		}
		return bidderId;
	}

	/**
	 * 得到单位id
	 * 
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

	public List changeToObjList(ParaMap in, String firstId) throws Exception {
		List resultList = new ArrayList();
		if (StrUtils.isNotNull(firstId)) {
			ParaMap fMap = new ParaMap();
			fMap.put("id", firstId);
			resultList.add(fMap);
		}
		if (in.getSize() > 0) {
			List filds = in.getFields();
			for (int i = 0; i < in.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) in.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				if (StrUtils.isNotNull(firstId) && StrUtils.isNotNull(id)
						&& id.equals(firstId)) {
					resultList.set(0, out);
				} else {
					resultList.add(out);
				}
			}
		}
		return resultList;
	}

	public int getPageTotal(int rowCount, int pageSize) {
		return (rowCount + pageSize - 1) / pageSize;
	}

}

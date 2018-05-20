package com.tradeplow.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.FsUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.utils.XMLUtils;
import com.tradeplow.dao.TradeDao;

/**
 * 摇号业务类
 * 
 * @author danqing.luo
 * 
 */
public class WaveNumberService extends BaseService {
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

	/**
	 * 设置自定义查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		String organId = getOrganId(inMap.getString("u"));
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		sqlParams.put("date_format", "yyyy-MM-dd hh24:mi:ss");
		// sqlParams.put("organId", organId);
		// customCondition
		// .append(" and ( t.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");

		if (inMap.containsKey("trans_organ_id")
				&& !"-1".equals(inMap.getString("trans_organ_id"))) {
			String trans_organ_id = inMap.getString("trans_organ_id");
			if (StrUtils.isNotNull(trans_organ_id)) {
				if (trans_organ_id.equals(organId)) {
					customCondition
							.append(" and ( t.trans_organ_id is null or tt.trans_organ_id='' or tt.trans_organ_id ='"
									+ trans_organ_id + "'  )");
				} else {
					customCondition.append(" and t.trans_organ_id ='"
							+ trans_organ_id + "' ");
				}
			}
		}
		if (inMap.containsKey("canton_id")
				&& !"-1".equals(inMap.getString("canton_id"))) {
			String canton_id = inMap.getString("canton_id");
			if (StrUtils.isNotNull(canton_id)) {
				sqlParams.put("canton_id", canton_id);
				customCondition.append(" and tg.canton_id = :canton_id");
			}
		}
		if (inMap.containsKey("goods_type")
				&& !"-1".equals(inMap.getString("goods_type"))) {
			String goods_type = inMap.getString("goods_type");
			if (StrUtils.isNotNull(goods_type)) {
				sqlParams.put("goods_type", goods_type);
				customCondition
						.append(" and ( tg.goods_type=:goods_type or substr(tt.business_type, 1, 3) = :goods_type )");
			}
		}
		if (inMap.containsKey("status")
				&& !"-1".equals(inMap.getString("status"))) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				sqlParams.put("status", status);
				customCondition.append(" and t.status=:status");
			}
		}
		if (inMap.containsKey("create_user_id")
				&& !"-1".equals(inMap.getString("create_user_id"))) {
			String create_user_id = inMap.getString("create_user_id");
			if (StrUtils.isNotNull(create_user_id)) {
				sqlParams.put("create_user_id", create_user_id);
				customCondition.append(" and t.create_user_id=:create_user_id");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and t.no like '%'||:no||'%'");
			}
		}
		if (inMap.containsKey("beginArea")) {
			String no = inMap.getString("beginArea");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("beginArea", no);
				customCondition.append(" and t.area <= '%'||:beginArea||'%'");
			}
		}
		if (inMap.containsKey("endArea")) {
			String no = inMap.getString("endArea");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("endArea", no);
				customCondition.append(" and t.area < '%'||:endArea||'%'");
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
				customCondition
						.append(" and tg.address like '%'||:address||'%'");
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
		if (inMap.containsKey("is_net_trans")
				&& !"-1".equals(inMap.getString("is_net_trans"))) {
			String is_net_trans = inMap.getString("is_net_trans");
			if (StrUtils.isNotNull(is_net_trans)) {
				sqlParams.put("is_net_trans", is_net_trans);
				customCondition.append(" and t.is_net_trans =:is_net_trans");
			}
		}

		if (inMap.containsKey("trans_type")
				&& !"-1".equals(inMap.getString("trans_type"))) {
			String trans_type = inMap.getString("trans_type");
			if (StrUtils.isNotNull(trans_type)) {
				customCondition.append(" and t.trans_type ='" + trans_type
						+ "' ");
			}
		}
		if (inMap.containsKey("businessType")
				&& !"-1".equals(inMap.getString("businessType"))) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				String str[] = businessType.split(",");
				if (str != null && str.length > 0) {
					customCondition.append(" and  t.business_type in('");
					for (int i = 0; i < str.length; i++) {
						if (i == str.length - 1) {
							customCondition.append(str[i] + "')");
						} else {
							customCondition.append(str[i] + "','");
						}
					}
				}
			}
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}

	/**
	 * 查询等待摇号标的
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getWaitWaveNumberTarget(ParaMap inMap) throws Exception {
		inMap.put("dataSetNo", "trans_plow_wa_list");
		inMap.put("moduleNo", "trans_trade_plow");
		DataSetDao dataSetDao = new DataSetDao();
		getQueryCondition(inMap);
		ParaMap result = dataSetDao.queryData(inMap);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", result.getListObj());
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 获取摇号竞买人
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] getWaveNumberBidder(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		inMap.put("dataSetNo", "get_lottery_bidder");
		inMap.put("moduleNo", "trans_trade_plow");
		ParaMap out = dataSetDao.queryData(inMap);
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("license_id", "license_id");
		fields.put("no", "rownum");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		this.getResponse().reset();
		this.getResponse().setContentType("text/xml; charset=UTF-8");
		String downLoadName = new String(
				(inMap.getString("no") + ".xml").getBytes("gb2312"),
				"ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downLoadName + "\";");
		this.getResponse().flushBuffer();
		byte[] xmlByte = parseWaveNumberXMl(items, inMap).getBytes();
		return xmlByte;
	}

	/**
	 * 转换XML
	 * 
	 * @param bidderList
	 * @return
	 */
	private String parseWaveNumberXMl(List bidderList, ParaMap inMap) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element shake = doc.addElement("shake").addAttribute("time",
				DateUtils.nowStr());
		Element targets = shake.addElement("targets");
		Element target = targets.addElement("target");
		target.addAttribute("id", inMap.getString("targetId"));
		target.addAttribute("name", URLDecoder.decode(inMap.getString("no")));
			try {
				System.out.println(new String(
						inMap.getString("businessName").getBytes("ISO-8859-1"),"UTF-8"));
				target.addAttribute("businessName", new String(
						inMap.getString("businessName").getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		Element bidders = target.addElement("bidders");
		for (Object object : bidderList) {
			Map map = (Map) object;
			if (map.get("id") == null)
				continue;
			Element bidder = bidders.addElement("bidder");
			bidder.addAttribute("id", map.get("license_id").toString());
			bidder.addAttribute("no", map.get("no").toString());
			bidder.addAttribute("name", map.get("name").toString());
		}
		return doc.asXML();
	}

	public File getFile(String home, String path) {
		return new File(FsUtils.getFileRoot() + "/" + home + "/" + path);
	}

	/**
	 * 上传摇号结果处理
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap uploadWaveNumberResult(ParaMap inMap) throws Exception {
		String fileName = inMap.getString("txtfile_Name");
		String home = inMap.getString("home");
		FileItem fileItem = inMap.getFileItem("txtfile_Item");
		String fileType = fileName.substring(fileName.lastIndexOf("."),
				fileName.length());
		File file = getFile(home, "/" + fileName);
		if (!file.exists())
			FileUtils.writeStringToFile(file, "");
		fileItem.write(file);
		
		FileInputStream fi = new FileInputStream(file);
		String xml = null ;
		int b ;
		byte[] flb = new byte[(int)file.length()];
		while((b=fi.read(flb))!=-1){
		    	xml = new String(flb);
		}
		System.out.println(xml);
		ParaMap out = new ParaMap();
		SAXReader saxReader = new SAXReader();
		try {
			Document doc = saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			doc.setXMLEncoding("GBK");
			ParaMap xmlPara = new ParaMap();
			xmlPara = XMLUtils.parseXMLWave(doc.getRootElement().asXML(),
					xmlPara);
			TradeDao tradeDao = new TradeDao();
			ParaMap target = (ParaMap) xmlPara.get("target");
			ParaMap bidder = (ParaMap) xmlPara.get("bidder");
			tradeDao.doEndScenceLottery(target.getString("id"),
					bidder.getString("id"));
			out.put("state", 1);
			out.put("message", "导入成功");
		} catch (Exception e) {
			e.printStackTrace();
			out.put("state", 0);
			out.put("message", "文件解析失败");
		}
		return out;
	}

}

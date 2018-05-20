package com.base.utils;

import java.io.File;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.base.web.AppConfig;
import com.sysman.bean.Sys_Extend;
import com.sysman.dao.ExpNoticeDao;

/**
 * 
 * @author danqing.luo
 * 
 */
public class XMLUtils {

	/**
	 * 解析XML
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static ParaMap parseXML(String xml, ParaMap paraMap)
			throws DocumentException {
		SAXReader saxReadr = new SAXReader();
		Document document = saxReadr.read(new StringReader(xml));
		Element root = document.getRootElement();
		Iterator eit = root.elementIterator();
		while (eit.hasNext()) {
			Element element = (Element) eit.next();
			if (element.elements().size() != 0) {
				paraMap.put(element.getName(),
						parseXML(element.asXML(), new ParaMap())); // 下面有XML子节点
																	// 递归
			} else {

				paraMap.put(element.getName(), element.getTextTrim());

			}
		}

		return paraMap;
	}

	/**
	 * 解析坐标xml 拼装成List
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static List parseXMLListXY(String xml, String goodsId)
			throws DocumentException {
		SAXReader saxReadr = new SAXReader();
		Document document = saxReadr.read(new StringReader(xml));
		Iterator it = document.getRootElement().elementIterator();
		List list = new ArrayList();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			list.add(new Sys_Extend("trans_goods", goodsId, document
					.getRootElement().attributeValue("name")
					+ "."
					+ el.attributeValue("name") + ".x", el.attributeValue("x"),
					"坐标" + el.attributeValue("name") + ".x", IDGenerator
							.newGUID()));
			list.add(new Sys_Extend("trans_goods", goodsId, document
					.getRootElement().attributeValue("name")
					+ "."
					+ el.attributeValue("name") + ".y", el.attributeValue("y"),
					"坐标" + el.attributeValue("name") + ".y", IDGenerator
							.newGUID()));
			list.add(new Sys_Extend("trans_goods", goodsId, document
					.getRootElement().attributeValue("name")
					+ "."
					+ el.attributeValue("name") + ".JZD_ID", el
					.attributeValue("JZD_ID"),
					"坐标" + el.attributeValue("name"), IDGenerator.newGUID()));
			list.add(new Sys_Extend("trans_goods", goodsId, document
					.getRootElement().attributeValue("name")
					+ "."
					+ el.attributeValue("name") + ".RING_ID", el
					.attributeValue("RING_ID"), "坐标"
					+ el.attributeValue("name"), IDGenerator.newGUID()));

		}
		return list;
	}

	/**
	 * 拼装交易前公告数据XML
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public static String ParaMapToXMLNoticeBefore(ResultSet inSetNotice)
			throws Exception {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element el = doc.addElement("root");
		ExpNoticeDao expNoticeDao = new ExpNoticeDao();
		ParaMap pm = new ParaMap();
		while (inSetNotice.next()) {
			el.addElement("bh").addText(
					StrUtils.getString(inSetNotice.getString("no")));

			el.addElement("bt").addText(
					StrUtils.getString(inSetNotice.getString("name")));
			el.addElement("lx")
					.addText(
							StrUtils.getString(inSetNotice
									.getString("notice_type_chs")));
			el.addElement("ggrq").addText(
					StrUtils.getString(inSetNotice.getString("notice_date")));
			el.addElement("bz").addText(
					StrUtils.getString(inSetNotice.getString("remark")));
			el.addElement("ggxxxx"); //
			pm.put("noticeId", inSetNotice.getString("id"));
			ResultSet inSet = expNoticeDao.expTargetBefore(pm);
			Element db = el.addElement("bd");
			int i = 0;
			while (inSet.next()) {
				if (i == 0) { // 标的信息 上层xml结构
					el.addElement("jyfs").addText(
							StrUtils.getString(inSet
									.getString("trans_type_chs")));
					el.addElement("yxlhjm").addText(
							StrUtils.getString(inSet.getString("allow_union")));
					el.addElement("jydw").addText(
							StrUtils.getString(inSet
									.getString("trans_organ_name")));
					el.addElement("ggkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_notice_time")));
					el.addElement("ggjzsj").addText(
							StrUtils.getString(inSet
									.getString("end_notice_time")));
					el.addElement("jmsqkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_apply_time")));
					el.addElement("jmsqjzsj").addText(
							StrUtils.getString(inSet
									.getString("end_apply_time")));
					el.addElement("bzjkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_earnest_time")));
					el.addElement("bzjjzsj").addText(
							StrUtils.getString(inSet
									.getString("end_earnest_time")));
					el.addElement("gpkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_list_time")));
					el.addElement("gpjzsj")
							.addText(
									StrUtils.getString(inSet
											.getString("end_list_time")));
					el.addElement("jzbjkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_focus_time")));
					el.addElement("jzbjjzsj").addText(
							StrUtils.getString(inSet
									.getString("end_focus_time")));
					el.addElement("pmkssj").addText(
							StrUtils.getString(inSet
									.getString("begin_limit_time")));
					el.addElement("jmzg").addText(
							StrUtils.getString(inSet
									.getString("trans_condition")));
					el.addElement("jmzgszbm").addText(
							StrUtils.getString(inSet
									.getString("condition_organ_name")));
				}
				Element dbxx = db.addElement("dbxx"); // 标的信息
				dbxx.addElement("bdxh").addText(
						StrUtils.getString(inSet.getString("no")));
				dbxx.addElement("bdmc").addText(
						StrUtils.getString(inSet.getString("name")));
				dbxx.addElement("qsj").addText(
						StrUtils.getString(inSet.getString("begin_price")));
				dbxx.addElement("bdj").addText(
						StrUtils.getString(inSet.getString("reserve_price")));
				dbxx.addElement("zt").addText(
						StrUtils.getString(inSet.getString("status")));
				dbxx.addElement("jbr").addText(
						StrUtils.getString(inSet.getString("create_user")));
				dbxx.addElement("dw")
						.addText(
								StrUtils.getString(inSet
										.getString("trans_organ_name")));
				dbxx.addElement("jjjt").addText(
						StrUtils.getString(inSet.getString("price_step")));
				dbxx.addElement("zzj").addText(
						StrUtils.getString(inSet.getString("trans_price")));
				dbxx.addElement("xsdw").addText("万元");
				dbxx.addElement("bzjje").addText(
						StrUtils.getString(inSet.getString("earnest_money")));
				dbxx.addElement("bzjbz").addText(StrUtils.getString("CNY"));
				pm.clear();
				pm.put("target_id", inSet.getString("id"));
				ResultSet inSetGoods = expNoticeDao.expGoodsBefore(pm);

				Element zongdi = dbxx.addElement("zongdi");
				while (inSetGoods.next()) {
					Element zd = zongdi.addElement("zd");
					// zd.addElement("synx")
					Map map = expNoticeDao.expGoodsInfoBefore(inSetGoods
							.getString("id")); // 查询扩展信息
					zd.addElement("synx").addText(
							StrUtils.getString(map.get("use_years")));
					zd.addElement("tdyt").addText(
							StrUtils.getString(inSetGoods
									.getString("goods_use")));
					zd.addElement("tdmj").addText(
							StrUtils.getString(map.get("land_area")));
					zd.addElement("qy").addText(
							CantonEmu.getValue(StrUtils.getString(inSetGoods
									.getString("canton_id")))); // ??去哪取
					zd.addElement("zdwz").addText(
							StrUtils.getString(inSetGoods.getString("field0")));// ??去哪取
					zd.addElement("jzjj").addText("");// ??去哪取
					zd.addElement("jzmj").addText(
							StrUtils.getString(map.get("land_area")));
					zd.addElement("jzrjl").addText(
							StrUtils.getString(map.get("plot1_down")));
					zd.addElement("jzgd").addText(
							StrUtils.getString(map.get("build_height")));
					if (i == 0) {
						el.addElement("qybm").addText(
								CantonEmu.getValue(StrUtils
										.getString(inSetGoods
												.getString("canton_id"))));
					}
				}

				i++;
			}

		}
		return doc.asXML();
	}

	/**
	 * 拼装交易后公公告数据XML
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public static String paraMapToXMLNoticeAfter(ResultSet inSetNotice)
			throws Exception {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element el = doc.addElement("root");
		Element gs = el.addElement("gs");
		ExpNoticeDao expNoticeDao = new ExpNoticeDao();
		ParaMap pa = new ParaMap();
		while (inSetNotice.next()) {
			el.addElement("bh").addText(inSetNotice.getString("id"));
			pa.put("noticeId", inSetNotice.getString("id"));
			pa.put("status", "5");
			ResultSet targetSet = expNoticeDao.expTargetBefore(pa);
			Map map = new HashMap();
			int i = 1;
			while (targetSet.next()) {
				Element gsxx = gs.addElement("gsxx");
				ResultSet transType = expNoticeDao
						.getTransTransactionTypeByTransType(targetSet
								.getString("trans_type"));
				transType.next();
				gsxx.addElement("bdxh").addText(String.valueOf(i));
				gsxx.addElement("bdmc").addText(
						StrUtils.getString(targetSet.getString("name")));
				gsxx.addElement("cjj").addText(
						StrUtils.getString(targetSet.getString("trans_price")));
				gsxx.addElement("dmdj").addText(
						StrUtils.getString(targetSet.getString("trans_price"))); // ?地面地价?
				gsxx.addElement("lmdj").addText(
						StrUtils.getString(targetSet.getString("trans_price"))); // ??楼面地价
				gsxx.addElement("bxsqrs")
						.addText(
								StrUtils.getString(transType
										.getString("enter_flag_1")));
				gsxx.addElement("bxcjrs")
						.addText(
								StrUtils.getString(transType
										.getString("enter_flag_2")));
				gsxx.addElement("jdj").addText(
						StrUtils.getString(targetSet.getString("trans_price")));
				gsxx.addElement("cjsj").addText(
						StrUtils.getString(targetSet
								.getString("end_trans_time")));
				map.put(targetSet.getString("id"), String.valueOf(i)); // 存标的
				i++;
			}
			ResultSet liceseSet = expNoticeDao
					.expTargetLicenseByNoticeId(inSetNotice.getString("id"));
			Element tbxx = el.addElement("tbxx");

			while (liceseSet.next()) { // 投标人信息
				Element tbr = tbxx.addElement("tbr");
				tbr.addElement("tbqy").addText(liceseSet.getString("name"));
				tbr.addElement("tbrlx").addText(
						liceseSet.getString("is_company_chs"));
				tbr.addElement("tbbd").addText(
						matchTarget(map, inSetNotice.getString("id"),
								liceseSet.getString("id"))); // 匹配投标项目
				tbr.addElement("swdjzh").addText(
						StrUtils.getString(liceseSet
								.getString("certificate_no")));
				tbr.addElement("sfzh").addText(
						StrUtils.getString(liceseSet
								.getString("reg_corporation_idno")));
				tbr.addElement("lxdh").addText(
						StrUtils.getString(liceseSet.getString("tel")));

			}

		}
		return doc.asXML();
	}

	/**
	 * 匹配竞买人的标的
	 * 
	 * @param tbr
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public static String matchTarget(Map map, String notice_id, String tbrId)
			throws SQLException {
		ExpNoticeDao expNoticeDao = new ExpNoticeDao();
		StringBuffer sb = new StringBuffer();
		ResultSet tbr = expNoticeDao.expTargetLicenseByNoticeId(notice_id);
		while (tbr.next()) {
			if (tbr.getString("id").equals(tbrId)) {
				sb.append(map.get(tbr.getString("target_id"))).append(",");
			}
		}

		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	/**
	 * 调用webservice
	 * 
	 * @param xml
	 * @param busiCode
	 * @return
	 * @throws Exception
	 */
	public static String sendWebServiceXml(Object[] obj, String busicode)
			throws Exception {

		RPCServiceClient client = new RPCServiceClient();
		Options options = client.getOptions();
		javax.xml.namespace.QName qname = new javax.xml.namespace.QName(
				AppConfig.getPro("qname"), AppConfig.getPro(busicode));
		options.setTo(new EndpointReference(AppConfig.getPro("wsdl")));

		OMElement element = client.invokeBlocking(qname, obj);
		System.out.println(element);
		return element.getText();
	}

	/**
	 * 解析摇号结构XML
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static ParaMap parseXMLWave(String xml, ParaMap paraMap)
			throws DocumentException {
		System.out.println(xml);
		SAXReader saxReadr = new SAXReader();
		Document document = saxReadr.read(new StringReader(xml));
		Element root = document.getRootElement();
		Iterator eit = root.elementIterator();
		while (eit.hasNext()) {
			Element element = (Element) eit.next();
			if (element.elements().size() != 0) {
				ParaMap per = new ParaMap();
				List list = element.attributes();
				for (Object object : list) {
					Attribute attribute = (Attribute) object;
					per.put(attribute.getName(), attribute.getValue());
				}
				paraMap.put(element.getName(), per);
				parseXMLWave(element.asXML(), paraMap); // 下面有XML子节点

			} else {

				ParaMap per = new ParaMap();
				List list = element.attributes();
				for (Object object : list) {
					Attribute attribute = (Attribute) object;
					per.put(attribute.getName(), attribute.getValue());
				}
				paraMap.put(element.getName(), per);

			}
		}
		return paraMap;
	}

	public static void main(String args[]) {
		SAXReader saxReadr = new SAXReader();
		try {
			Document doc = saxReadr
					.read(new File(
							"\\/192.168.5.250\\fileRoot\\interface\\goods\\1366776109718.xml"));
			ParaMap paraMap = new ParaMap();
			System.out.println(XMLUtils.parseXML(doc.asXML(), paraMap));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

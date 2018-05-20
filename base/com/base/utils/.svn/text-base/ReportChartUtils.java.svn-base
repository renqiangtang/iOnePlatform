package com.base.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.json.JSONObject;

import org.apache.tools.ant.types.DataType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.net.www.content.text.plain;

/**
 * 
 * @author danqing.luo 报表工具类
 * 
 */
public class ReportChartUtils {
	public static String TITLE = "title";
	public static String STYLE = "style";
	public static String CHARTTYPEPIE = "pie"; // 饼图
	public static String CHARTTYPELINE = "line"; // 曲线图
	public static String CHARTTYPEBAR = "bar_3d"; // 堆栈图
	public static String CHARTTYPEBARGLASS = "bar_glass"; // 多堆堆栈图
	public static String CHARTYPESTACKBAR = "bar_stack";// 堆积图
	public  ParaMap XLEGEND = new ParaMap();
	public  ParaMap YLEGEND =  new ParaMap();
	public static List color = new ArrayList();
	public ArrayList<ParaMap> elements = new ArrayList<ParaMap>();
	public List elementsList = new ArrayList();
	public TreeSet condtionDistc = new TreeSet();
	public ParaMap xMap = new ParaMap();
	public TreeSet yList = new TreeSet();
	public ParaMap yMap = new ParaMap();
	public int colorIndex = 1;

	/**
	 * 产生饼图颜色
	 * 
	 * @param list
	 */
	public void getColorList(ArrayList list) {
		for (int i = 0; i < list.size(); i++) {
			color.add("#" + getRandomColor());
		}

	}

	/**
	 * 取最大数 显示Y坐标
	 * 
	 * @param list
	 * @return
	 */
	public String getMaxYaixs(Collection list) {
		yList.addAll(list);

		return (yList.last().toString()).toString();
	}

	/**
	 * 通过最大值得到x轴的升级阶梯
	 * 
	 * @param maxValue
	 * @return
	 */
	public String getReportSteps(String maxValue) {
		String steps = "1";
		if(Double.valueOf(maxValue)>50){
			steps = "10";
		}
		if (Double.valueOf(maxValue) > 100) {
			steps = "10";
		}
		if (Double.valueOf(maxValue) > 500) {
			steps = "100";
		}
		if (Double.valueOf(maxValue) > 1000) {
			steps = "500";
		}
		if (Double.valueOf(maxValue) > 10000) {
			steps = "5000";
		}

		return steps;
	}

	/**
	 * 构造堆积图X轴
	 * 
	 * @param list
	 * @return
	 */
	public ParaMap statckBarX_axis(ParaMap param) {
		xMap.clear();
		ParaMap labels = new ParaMap();
		List lable = new ArrayList();
		Set set = param.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Map map = (Map) entry.getValue();
			Set mapSet = map.entrySet();
			Iterator mapIt = mapSet.iterator();
			while (mapIt.hasNext()) {
				Entry etn = (Entry) mapIt.next();
				lable.add(etn.getKey().toString().split(",")[1]);

			}
			break;
		}
		labels.put("labels", lable);
		xMap.put("labels", labels);
		return xMap;
	}

	/**
	 * 构造堆积图类型说明
	 * 
	 * @return
	 */
	public List getGlassBarKeys() {
		ParaMap labels = new ParaMap();
		List lable = new ArrayList();
		Iterator it = condtionDistc.iterator();
		while (it.hasNext()) {
			String text = (String) it.next();
			if (text != null) {
				ParaMap colourMap = new ParaMap();
				colourMap.put("colour", "#" + getRandomColor());
				colourMap.put("text", text);
				colourMap.put("font-size", 13);
				lable.add(colourMap);
			}
		}
		return lable;
	}

	/**
	 * 获取堆积图图表颜色列表
	 * 
	 * @param list
	 * @return
	 */
	public List getGlassBarColour(List list) {
		List colourList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			ParaMap paraMap = (ParaMap) list.get(i);
			colourList.add(paraMap.get("colour"));

		}
		return colourList;
	}

	/***
	 * 数据转换成报表结构
	 * 
	 * @param paraMap
	 * @param chartType
	 *            pie 饼图 line 线性图 bar 柱状图
	 * @return
	 */
	public String getChartJson(ParaMap paraMap, String chartType) {
		elementsList.clear();
		JSONObject jsonObject = new JSONObject();
		ParaMap titleMap = new ParaMap();
		Map style = new HashMap();
		Set set = paraMap.entrySet();
		Iterator it = set.iterator();
		List list = new ArrayList();
		List valuesList = new ArrayList();
		int count = 1;
		List key = new ArrayList();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			ParaMap paraMap2 = new ParaMap();
			if ((!chartType.equals(CHARTYPESTACKBAR) || count == paraMap.size())) {
				paraMap2.put("type", chartType);
				paraMap2.put("border", 1);
				paraMap2.put("start-angle", 35);
				paraMap2.put("width", 1);
				paraMap2.put("colour", "#" + getRandomColor());
			}
			if (entry.getValue() instanceof ArrayList) {
				paraMap2.put("values", entry.getValue());
				paraMap2.put("tip", "#label# #val# 笔交易");
				color.clear();
				getColorList((ArrayList) entry.getValue());
				paraMap2.put("colours", color);
				xMap.clear();
				yList.clear();
			} else if (entry.getValue() instanceof TreeMap) {

				if (chartType.equals(CHARTTYPEBARGLASS)) {
					statckBarX_axis(paraMap);

				}
				TreeMap vParaMap = (TreeMap) entry.getValue();
				paraMap2.put("text",
						vParaMap.firstKey().toString().split(",")[0]);
				paraMap2.put("values", vParaMap.values());

			}

			if ((chartType.equals(CHARTYPESTACKBAR) && count == paraMap.size())) {
				List keyList = getGlassBarKeys();
				paraMap2.put("values", valuesList);
				paraMap2.put("keys", keyList);
				paraMap2.put("colours", getGlassBarColour(keyList));
				paraMap2.put("tip", "#x_label#, #val#亩    总面积 #total#亩");
				elementsList.add(paraMap2);
			} else if (!chartType.equals(CHARTYPESTACKBAR)) {
				elementsList.add(paraMap2);
			}
			count++;
		}

		jsonObject.elementOpt("elements", elementsList);
		if(XLEGEND.size()!=0)
		jsonObject.elementOpt("x_legend", XLEGEND);
		if(YLEGEND.size()!=0)
		jsonObject.elementOpt("y_legend", YLEGEND);
		jsonObject.elementOpt("x_axis", xMap);
		jsonObject.elementOpt("y_axis", yMap);
		jsonObject.elementOpt("num_decimals", "4");
		jsonObject.elementOpt("bg_colour", "#FFFFFF");
		titleMap.put("text", TITLE);
		jsonObject.elementOpt("title", titleMap);
		System.out.println(jsonObject.toString());
		return jsonObject.toString();
	}

	/**
	 * 转换成Json格式Map
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap toJsonChart(List items, ParaMap condtionMap)
			throws Exception {
		ParaMap elementMap = new ParaMap();
		ParaMap dataType = new ParaMap();
		condtionDistc.clear();
		if (StrUtils.isNotNull(condtionMap.getString("condition"))) {
			for (int i = 0; i < items.size(); i++) {
				ParaMap para = (ParaMap) items.get(i);
				condtionDistc.add(para.getString(condtionMap
						.getString("condition")));
				dataType.put(para.getString(condtionMap.getString("label")),
						para.getString(condtionMap.getString("value")));
			}
			xMap.clear();
			xMap.put("min", condtionDistc.first());
			xMap.put("max", condtionDistc.last());
			xMap.put("steps", "1");// getReportSteps(condtionDistc.last().toString())
		} else {
			List valueList = new ArrayList();
			for (int i = 0; i < items.size(); i++) {
				ParaMap dataMap = (ParaMap) items.get(i);
				Map lablevalue = new IdentityHashMap();
				lablevalue.put("value", Double.valueOf(dataMap
						.getString(condtionMap.getString("value"))));
				lablevalue.put("label",
						dataMap.getString(condtionMap.getString("label")));
				valueList.add(lablevalue);
			}
			elementMap.put("values", valueList);
		}
		Set typeSet = dataType.entrySet();
		Iterator tyIterator = typeSet.iterator();
		yList.clear();
		yMap.clear();
		while (tyIterator.hasNext()) {
			Entry entry = (Entry) tyIterator.next();
			TreeMap treeMap = new TreeMap();
			for (int i = 0; i < items.size(); i++) {
				ParaMap para = (ParaMap) items.get(i);
				if (entry.getKey().toString()
						.equals(para.getString(condtionMap.getString("label")))) {
					treeMap.put(
							para.getString(condtionMap.getString("label"))
									+ ","
									+ para.getString(condtionMap
											.getString("condition")),
							getDoubleAsW(para.getString(condtionMap
									.getString("value"))));
					yList.add(getDoubleAsW(para.getString(condtionMap
							.getString("value"))));
					// getMaxYaixs(para.getList("value"));

				}
			}
			elementMap.put(entry.getKey(), treeMap);
			yMap.put(
					"max",
					Math.ceil(Double.valueOf(yList.last().toString())) == 0 ? 10
							: Math.ceil(Double.valueOf(yList.last().toString())));
			yMap.put("steps", getReportSteps(String.valueOf(Math.ceil(Double
					.valueOf(yList.last().toString())) == 0 ? 10 : Math
					.ceil(Double.valueOf(yList.last().toString())))));

		}

		return elementMap;
	}

	/**
	 * 构造线性图
	 * 
	 * @param list
	 * @param condtionMap
	 * @return
	 */
	public String createReportXMLLine(List list, ParaMap condtionMap) {
		Document doc = DocumentHelper.createDocument();
		Element chat = doc.addElement("chart");
		ParaMap dataType = new ParaMap();
		chat.addAttribute("caption", TITLE);
		TreeSet treeSet = new TreeSet();
		Element categories = chat.addElement("categories");
		TreeSet condtionDistc = new TreeSet();
		for (int i = 0; i < list.size(); i++) {
			ParaMap para = (ParaMap) list.get(i);
			condtionDistc
					.add(para.getString(condtionMap.getString("condition")));
			dataType.put(para.getString(condtionMap.getString("label")),
					para.getString(condtionMap.getString("value")));

		}
		Iterator iterator = condtionDistc.iterator();
		while (iterator.hasNext()) {
			Element category = categories.addElement("category");
			category.addAttribute("label", (String) iterator.next());
		}
		System.out.println(list);
		Set typeSet = dataType.entrySet();
		Iterator tyIterator = typeSet.iterator();
		TreeMap treeMap = new TreeMap();
		while (tyIterator.hasNext()) {
			Element dataset = chat.addElement("dataset");
			Entry entry = (Entry) tyIterator.next();
			for (int i = 0; i < list.size(); i++) {
				ParaMap para = (ParaMap) list.get(i);
				if (entry.getKey().toString()
						.equals(para.getString(condtionMap.getString("label")))) {
					treeMap.put(para.getString(condtionMap.getString("label"))
							+ "," + i,
							para.getString(condtionMap.getString("value")));

				}

			}
			Set mapSet = treeMap.entrySet();
			Iterator mapit = mapSet.iterator();
			while (mapit.hasNext()) {
				Element set = dataset.addElement("set");
				Entry entryset = (Entry) mapit.next();
				set.addAttribute("label",
						entryset.getKey().toString().split(",")[0]);
				set.addAttribute("value", entryset.getValue().toString());
			}
			treeMap.clear(); //
		}
		return doc.asXML();
	}

	/**
	 * 拼装报表展示Xml // 饼图
	 * 
	 * @param paraMap
	 * @return
	 * @throws Exception
	 */
	public String toReportXmlPie(List list, ParaMap contionMap)
			throws Exception {
		Document doc = DocumentHelper.createDocument();
		Element chat = doc.addElement("chart");
		chat.addAttribute("caption", TITLE);
		for (int i = 0; i < list.size(); i++) {
			Element set = chat.addElement("set");
			ParaMap para = (ParaMap) list.get(i);
			set.addAttribute("label",
					para.getString(contionMap.getString("label")));
			set.addAttribute("value",
					para.getString(contionMap.getString("value")));
		}

		return doc.asXML();

	}

	/**
	 * 如果上亿则已万为单位返回
	 * 
	 * @param dou
	 * @return
	 */
	public Double getDoubleAsW(String dou) {

		return Double.valueOf(dou);// > 100000000 ? Double.valueOf(dou) / 10000
		// : Double.valueOf(dou);
	}

	/**
	 * 随机产生显示颜色
	 * 
	 * @return
	 */
	public String getRandomColor() {
		if (colorIndex == 1) {
			colorIndex++;
			return "006400";
		} else if (colorIndex == 2) {
			colorIndex++;
			return "483D8B";
		}
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return r + g + b;
	}
}

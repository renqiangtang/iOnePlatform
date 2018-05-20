package com.test.xml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.utils.StreamUtils;
import com.base.xml.MappingClass;
import com.base.xml.MappingUtils;
import com.base.xml.ObjectUtils;

public class Test {

	public static void main(String[] args) throws Exception {
		 InputStream in = Test.class.getResourceAsStream("公告交互文件(交易前).xml");
		 InputStream map = Test.class.getResourceAsStream("mapping.xml");
		 MappingUtils maping=new MappingUtils();
		 Object obj=maping.getObj(in,map);
		 System.out.println(obj);
		// sss(in);
		//		sss2();
	}

	// public static void main1(String[] args) throws Exception {
	// InputStream in = null;
	// InputSource is = null;
	// Mapping mapping = new Mapping();
	//
	// // 1. Load the mapping information from the file
	// in = Test.class.getResourceAsStream("mapping.xml");
	// is = new InputSource(in);
	// mapping.loadMapping(is);
	//
	// // 2. Unmarshal the data
	// Unmarshaller unmar = new Unmarshaller(mapping);
	// in = Test.class.getResourceAsStream("公告交互文件(交易前).xml");
	// is = new InputSource(in);
	// Notice notice = (Notice) unmar.unmarshal(is);
	//
	// System.out.println(notice.bh);
	//
	// }
	
	

	public static void sss2() throws Exception {
		String c = "com.test.xml.MappingClass";
		Class cc = Class.forName(c);
		Object o = cc.newInstance();
		Field f = cc.getField("name");
		System.out.println(f.getGenericType());
		f.set(o, "aaa");
		MappingClass mc = (MappingClass) o;
		ObjectUtils.print(mc);
	}

	public static void sss(InputStream in) throws Exception {
		String xml = StreamUtils.InputStreamToString(in);
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		String xpath = "/root/bd/bdxx/zongdi/zd";
		List<Element> list = root.selectNodes(xpath);
		for (int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			System.out.println(e.asXML());
			System.out.println(e.getPath());
		}
	}
}

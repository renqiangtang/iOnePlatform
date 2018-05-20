package com.base.xml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.utils.StreamUtils;

public class MappingUtils {
	public ArrayList<MappingClass> classList = new ArrayList<MappingClass>();

	public MappingClass getMappingClass(String propery, String value)
			throws Exception {
		for (int i = 0; i < classList.size(); i++) {
			MappingClass mc = classList.get(i);
			Object v = ObjectUtils.getProperyValue(mc, propery);
			if (value.equals(v)) {
				return mc;
			}
		}
		return null;
	}

	public MappingClass getMappingClassByName(String value) throws Exception {
		return getMappingClass("name", value);
	}

	public MappingClass getMappingClassByPath(String value) throws Exception {
		return getMappingClass("path", value);
	}

	public Object getObj(InputStream souceIn, InputStream mappingIn)
			throws Exception {
		String xml = StreamUtils.InputStreamToString(mappingIn);
		Document doc = DocumentHelper.parseText(xml);
		Element rootEle = doc.getRootElement();
		List list1 = rootEle.elements();
		for (int i = 0; i < list1.size(); i++) {
			Element ele1 = (Element) list1.get(i);
			MappingClass mc = new MappingClass();
			mc.fieldMap = new HashMap<String, MappingField>();
			mc.name = ele1.attributeValue("name");
			mc.path = ele1.attributeValue("path");
			mc.auto = Boolean.parseBoolean(ele1.attributeValue("auto"));
			List list2 = ele1.elements();
			for (int j = 0; j < list2.size(); j++) {
				Element ele2 = (Element) list2.get(j);
				MappingField mf = new MappingField();
				mf.name = ele2.attributeValue("name");
				mf.bindName = ele2.attributeValue("bindName");
				mf.bindType = ele2.attributeValue("bindType");
				mf.type = ele2.attributeValue("type");
				mc.fieldMap.put(mf.name, mf);
			}
			classList.add(mc);
		}
		xml = StreamUtils.InputStreamToString(souceIn);
		doc = DocumentHelper.parseText(xml);
		rootEle = doc.getRootElement();
		return parse(rootEle);
	}

	private Object parse(Element ele) throws Exception {
		String path = ele.getPath();
		MappingClass mapClass = getMappingClassByPath(path);
		if (mapClass == null)
			return null;
		Class clazz = Class.forName(mapClass.name);
		Object o = clazz.newInstance();
		if (mapClass.auto) {
			List eList1 = ele.elements();
			for (int i = 0; i < eList1.size(); i++) {
				String eleName = ((Element) eList1.get(i)).getName();
				Field f = null;
				try {
					f = clazz.getField(eleName);
				} catch (Exception ex) {
				}
				if (f == null) {
					System.out.println(clazz.getName() + " 缺少属性:" + eleName);
					continue;
				}
				if (!mapClass.fieldMap.containsKey(eleName)) {
					MappingField mf = new MappingField();
					mf.name = eleName;
					mf.bindName = eleName;
					mf.type = ObjectUtils.getProperyType(o, eleName)
							.getSimpleName();
					mapClass.fieldMap.put(mf.name, mf);
				}
			}
		}

		Iterator it = mapClass.fieldMap.keySet().iterator();
		while (it.hasNext()) {
			MappingField f = mapClass.fieldMap.get(it.next());
			Class fType = ObjectUtils.getProperyType(o, f.name);
			if (fType.getSimpleName().equals("ArrayList")) {
				ArrayList list = new ArrayList();
				Element listEle = (Element) ele.elements(f.bindName).get(0);
				if (listEle == null)
					continue;
				List eList = listEle.elements();
				for (int i = 0; i < eList.size(); i++) {
					Element ele2 = (Element) eList.get(i);
					Object o2 = parse(ele2);
					list.add(o2);
				}
				ObjectUtils.setProperyValue(o, f.name, list);
			} else {
				String txt = ele.elementTextTrim(f.bindName);
				ObjectUtils.setProperyValue(o, f.name, txt);
			}
		}

		return o;

	}
}

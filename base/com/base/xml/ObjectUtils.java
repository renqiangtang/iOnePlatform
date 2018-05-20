package com.base.xml;

import java.lang.reflect.Field;

public class ObjectUtils {

	public static void setProperyValue(Object o, String name, Object value)
			throws Exception {
		Field f = o.getClass().getField(name);
		f.set(o, value);
	}

	public static Object getProperyValue(Object o, String name)
			throws Exception {
		Field f = o.getClass().getField(name);
		return f.get(o);
	}

	public static Class getProperyType(Object o, String name) throws Exception {
		Field f = o.getClass().getField(name);
		return f.getType();
	}

	public static void print(Object o) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(o);
		Field[] fs = o.getClass().getFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			String fName = f.getName();
			String fValue = String.valueOf(f.get(o));
			sb.append("\n" + fName + " : " + fValue);
		}
		System.out.println(sb.toString());
	}

}

package com.base.utils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FlagUtils {
	public static final String nodeFormatChar = "#";
	public static final int L02HasPrice = 0;
	public static Integer getInt(String format, int index) {
		String v = getString(format, index);
		if (StringUtils.isNumeric(v))
			return new Integer(v);
		else
			return null;
	}

	public static String getString(String format, int index) {
		if (StringUtils.isEmpty(format)) {
			return null;
		}
		String[] strArr = format.split(nodeFormatChar);
		if (index > strArr.length - 1)
			return null;
		else
			return strArr[index];
	}

	public static String genStr(Object[] array) {
		return StringUtils.join(array, nodeFormatChar);
	}

	public static void main(String[] args) {
		Object[] arr = { "1231", 12, "", true, DateUtils.now(), null, -8080 };
		String ff = genStr(arr);
		System.out.println(ff);
		System.out.println(">>>" + FlagUtils.getString(ff, 5) + "<<<");
	}
}

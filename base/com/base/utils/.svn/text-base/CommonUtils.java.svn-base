package com.base.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 公共实用工具类
 * 
 * @author SAMONHUA
 * 2013-05-23
 */
public class CommonUtils {
	/**
	 * 模仿oracle中的decode函数
	 * @param args 参数列表，比较通过equals完成。如下示例中的x和0、1、2等值应该是同一类型
	 * @return 示例：decode(x, 0, "a", 1, "b", 2, "c", "d")，x=0返回a，x=1返回b，类推。全部不匹配则返回d，如果没有d则返回null。参数格式仅示例
	 */
	public static Object decode(List args) {
		if (args == null || args.size() <= 3)
			return null;
		Object expression = args.get(0);
		List compares = new ArrayList();
		List values = new ArrayList();
		for(int i = 1; i < args.size(); i = i + 2) {
			if (i + 1 == args.size()) {
				values.add(args.get(i));
			} else {
				compares.add(args.get(i));
				values.add(args.get(i + 1));
			}
		}
		for(int i = 0; i < compares.size(); i++) {
			Object compare = compares.get(i);
			if (expression == null && compare == null)
				return values.get(i);
			if (expression != null && compare != null && expression.equals(compare))
				return values.get(i);
		}
		if (values.size() > compares.size())
			return values.get(values.size() - 1);
		else
			return null;
	}
	
	/**
	 * 模仿oracle中的decode函数
	 * @param args 参数列表，比较通过equals完成。如下示例中的x和0、1、2等值应该是同一类型
	 * @return 示例：decode([x, 0, "a", 1, "b", 2, "c", "d"])，x=0返回a，x=1返回b，类推。全部不匹配则返回d，如果没有d则返回null
	 */
	public static Object decode(Object[] args) {
		if (args == null ||  args.length <= 3)
			return null;
		List argList = new ArrayList();
		for(int i = 0; i < args.length; i++)
			argList.add(args[i]);
		return decode(argList);
	}
	
	/**
	 * 模仿oracle中的case语句
	 * @param args 参数列表，如下示例中的a、b、c、d必须是boolean的包装类
	 * @return 示例：switchCase(a, 0, b, 1, c, 2, d, 3, 4)，a条件成立返回0，b返回1，类推。所有条件不匹配则返回4，如果没有4则返回null。参数格式仅示例
	 */
	public static Object switchCase(List args) {
		if (args == null || args.size() <= 1)
			return null;
		List<Boolean> expressions = new ArrayList<Boolean>();
		List values = new ArrayList();
		for(int i = 0; i < args.size(); i = i + 2) {
			if (i + 1 == args.size()) {
				values.add(args.get(i));
			} else {
				expressions.add((Boolean) args.get(i));
				values.add(args.get(i + 1));
			}
		}
		for(int i = 0; i < expressions.size(); i++) {
			if (expressions.get(i))
				return values.get(i);
		}
		if (values.size() > expressions.size())
			return values.get(values.size() - 1);
		else
			return null;
	}
	
	/**
	 * 模仿oracle中的case语句
	 * @param args 参数列表，如下示例中的a、b、c、d必须是boolean的包装类
	 * @return 示例：switchCase([a, 0, b, 1, c, 2, d, 3, 4])，a条件成立返回0，b返回1，类推。所有条件不匹配则返回4，如果没有4则返回null
	 */
	public static Object switchCase(Object[] args) {
		if (args == null ||  args.length <= 1)
			return null;
		List argList = new ArrayList();
		for(int i = 0; i < args.length; i++)
			argList.add(args[i]);
		return switchCase(argList);
	}
	
	public static void main(String[] args) {
		String a = "3";
		System.out.println(CommonUtils.decode(new Object[]{a, 1, "111", 2, "222", 3, "333", 4, "444", "999"}));
		System.out.println(CommonUtils.decode(new Object[]{a, "1", "111", "2", "222", "3", "333", "4", "444", "999"}));
		a = "6";
		System.out.println(CommonUtils.switchCase(new Object[]{a.equals("1"), "111", 1 == 1 , 12, a.equals("2"), "222", a.equals("3"), "333", a.equals("4"), "444", "999"}));
		int b = 2;
		System.out.println(CommonUtils.decode(new Object[]{b, 1, "111", 2, "222", 3, "333", 4, "444", "999"}));
	}
}
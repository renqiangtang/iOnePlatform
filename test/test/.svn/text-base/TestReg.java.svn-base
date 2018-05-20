package test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class TestReg {

	public static void groupString(String rexp, String s) {
		Pattern p = Pattern.compile(rexp);
		Matcher m = p.matcher(s);
		boolean result = m.find();
		System.out.println("该次查找获得匹配组的数量为：" + m.groupCount());
		while (result) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
			result = m.find();
		}

	}

	public static void test1() {
		String s = "172.26.22.221 - - [26/Feb/2001:10:56:03 -0500]\"get/isAlive.html Http/1.0\"200 15";
		String rexp = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\s\\-\\s\\-\\s\\[([^\\]]+)\\]";
		groupString(rexp, s);

	}

	public static void test2() {
		String s1 = "var approot = \"/guangzhou\";";
		String s2 = s1.replaceAll("var approot.*;", "var approot=\"/\";");
		System.out.println(s2);
	}

	public static void test3() throws Exception {
		String path = "D:\\workplace\\iworkplace\\iOnePlatform_guangzhou\\config\\appConfig.properties";
		String txt1 = FileUtils.readFileToString(new File(path));
		txt1 = txt1.replaceAll("versionMode=\\w+", "versionMode=demo");
		System.out.println(txt1);
	}

	public static void main(String[] args) throws Exception {
		// test3();
	}
}

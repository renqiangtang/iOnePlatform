package test;

import java.util.Date;
import java.util.jar.JarFile;

import com.base.utils.DateUtils;

public class TestJarFile {

	public static void main(String[] args) throws Exception {
		JarFile currentJar = new JarFile(
				"D:\\workplace\\iworkplace\\iOnePlatform\\release\\app.war");
		long t = currentJar.getJarEntry("base/js/ligerui.all.js").getTime();
		System.out.println("t1>>>" + DateUtils.getStr(new Date(t)));
		long d1 = 365 * 24 * 60 * 60 * 1000L;
		System.out.println(d1);
		System.out.println("t2>>>" + DateUtils.getStr(new Date(t + d1)));

	}
}

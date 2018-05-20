package test;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLog {
	protected Logger log = Logger.getLogger(this.getClass());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEnv() throws Exception {
		System.out.println(System.getProperty("file.encoding"));
	}

	

	@Test
	public void testLogger() throws Exception {
		try {
			Enumeration en = Logger.getRootLogger().getAllAppenders();
			while (en.hasMoreElements()) {
				Appender appender = (Appender) en.nextElement();
				if (appender.getName().equals("file")) {
					DailyRollingFileAppender df = (DailyRollingFileAppender) appender;
					System.out.println(df.getFile());
				}
			}
		} catch (Exception ex) {
			log.error("系统错误", ex);
		}
	}

	@Test
	public void testLogFile() throws Exception {

		File dir = new File("D:/ide/tomcat-6.0.35/logs");
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File dir, String name) {
				name = name.substring(name.lastIndexOf(".") + 1);
				System.out.println(name);
				return false;
			}

			public boolean accept(File file) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d1 = new Date(file.lastModified());
				System.out.println(df.format(d1));
				return false;
			}
		});
	}

	@Test
	public void testEncode() throws Exception {
		String s1 = "专家编程";
		System.out.println(new String(s1.getBytes(), "ISO-8859-1"));
	}

	@Test
	public void testHex() throws Exception {
		byte[] buf = { (byte) 0x4A, (byte) 0x6D, (byte) 79 };
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < buf.length; i++) {
			String s4 = Integer.toHexString(buf[i]).toUpperCase();
			list.add(s4);
		}

		System.out.println(list);
	}

}

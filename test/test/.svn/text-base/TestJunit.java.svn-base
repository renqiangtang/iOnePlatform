package test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tradeland.engine.EngineDao;
import com.base.utils.ParaMap;
import com.base.utils.StreamUtils;

public class TestJunit {
	Logger log = Logger.getLogger(this.getClass());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenStr() throws Exception {
		File dir = new File(
				"D:\\workplace\\iworkplace\\iOnePlatform\\web\\base\\js");
		File[] files = dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (!name.startsWith("all")) {
					System.out
							.println("<script type=\"text/javascript\" src=\"/base/js/"
									+ name + "\"></script>");
					return true;
				} else
					return false;
			}
		});
		//
	}

	@Test
	public void testLoadJsCss() throws Exception {
		String html = FileUtils.readFileToString(new File(
				"D:\\workplace\\iworkplace\\iOnePlatform\\web\\index.html"));
		InputStream fin = this.getClass().getResourceAsStream("/imports.txt");
		String str2 = StreamUtils.InputStreamToString(fin);
		int b1 = html.indexOf("<link");
		int b2 = html.indexOf("</head>");
		String str1 = html.substring(b1, b2);
		html = html.replaceFirst(str1, str2);
		System.out.println(html);
	}

	@Test
	public void testTimeout() throws Exception {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("task>>" + Thread.currentThread().getId());

			}
		}, 1000);
		System.out.println("main>>" + Thread.currentThread().getId());
		Thread.currentThread().join(10000);
	}

	@Test
	public void testRuleMap() throws Exception {
		EngineDao dao = new EngineDao();
		ParaMap map = dao.getRuleMap();
		log.debug(map);
	}

	@Test
	public void testCalendar() throws Exception {
		Calendar rightNow = Calendar.getInstance();
		// log.debug(rightNow.compareTo(anotherCalendar)));
	}

	@Test
	public void testParaMap() throws Exception {
		ParaMap pm = new ParaMap();
		int i = pm.getInt("aaa");
		log.debug(i);
	}

	@Test
	public void testLog4j() throws Exception {
		File file = new File("test");
		ZipFile zipFile = new ZipFile(file);
	}

}

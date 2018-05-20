package test;

import java.util.Enumeration;
import java.util.HashMap;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.trade.utils.Constants;
import com.trade.utils.Engine;
import com.trade.utils.EngineManageDao;
import com.tradeland.engine.LandEngine;

public class TestEngine1 {
	protected Logger log = Logger.getLogger(this.getClass());
	LandEngine tradeEngine = Engine.getLandEngine();

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void startEngine() throws Exception {
		tradeEngine.commit(Constants.Action_StartEngine, null);
		Thread.currentThread().sleep(99999999);
	}

	@Test
	public void testLog() throws Exception {
		Enumeration en = log.getRootLogger().getAllAppenders();
		while (en.hasMoreElements()) {
			Object obj = en.nextElement();
			if (obj instanceof DailyRollingFileAppender) {
				DailyRollingFileAppender appender = (DailyRollingFileAppender) obj;
				System.out.println(appender.getFile());
			}

		}
		log.debug(log.getRootLogger().getAppender("file"));
	}

	@Test
	public void testString() throws Exception {
		String s1 = "sss";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("001", s1);
		s1 = "eee";
		System.out.println(map.get("001"));
	}

	@Test
	public void test_engineStarted() throws Exception {
		boolean result1 = EngineManageDao.engineStarted("111");
		System.out.println(result1);
		boolean result2 = EngineManageDao.engineStarted("111");
		System.out.println(result2);
	}

	@Test
	public void test_engineStartRefresh() throws Exception {
		// EngineManageDao.engineStartRefresh("111");
		// Thread.currentThread().sleep(9999999);
	}
}

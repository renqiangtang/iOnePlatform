package com.base.log;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public class LogUtils {

	static Hashtable<Integer, String> buf = new Hashtable<Integer, String>();

	static Logger log = Logger.getLogger(LogUtils.class);

	public static void debug(Integer id, String msg) {
		String sss = buf.get(id);
		if (!msg.equals(sss)) {
			buf.put(id, msg);
			log.debug(msg);
		}
	}

	public static void info(Integer id, String msg) {
		String sss = buf.get(id);
		if (!msg.equals(sss)) {
			buf.put(id, msg);
			log.info(msg);
		}
	}

}

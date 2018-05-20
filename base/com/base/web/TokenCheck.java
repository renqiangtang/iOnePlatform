package com.base.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.utils.DateUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;

public class TokenCheck {
	static Logger log = Logger.getLogger(TokenCheck.class);
	static final String defaultRoot = "0000";
	static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	static Hashtable<String, Vector<String>> tokenTable = new Hashtable<String, Vector<String>>();
	static TreeMap<Long, String> tokenTimeTable = new TreeMap<Long, String>(
			new Comparator() {
				public int compare(Object o1, Object o2) {
					Long l1 = new Long(String.valueOf(o1.toString()));
					Long l2 = new Long(String.valueOf(o2.toString()));
					return l1 > l2 ? 1 : -1;
				}
			}); // token key最近访问时间
	static int interval = 1000 * 60 * 60 * 24;

	public static void setTokenKey(String key) {
		threadLocal.set(key);
	}

	/**
	 * 严格检查数据访问
	 * 
	 * @param inMap
	 * @return
	 */
	static List<String> strictList = new ArrayList<String>();
	static {
		strictList.add("sysman_User_getLoginUser");
		strictList.add("sysman_ExchangeData_receiveData");
		strictList.add("sysman_ExchangeData_receiveSyncData");
		strictList.add("sysman_Log_getFiles");
		strictList.add("bank_Bank_dealBankSendXml");
		strictList.add("bank_Bank_getWebSendXml");
		strictList.add("bank_Bank_dealBankFeedBack10004");
	}

	public static boolean strictParaMap(ParaMap inMap) {
		String module = inMap.getString("module");
		String service = inMap.getString("service");
		String method = inMap.getString("method");
		String mergeStr = module + "_" + service + "_" + method;
		if (strictList.indexOf(mergeStr) >= 0)
			return false;
		else
			return true;
	}

	public static void printTokens() {
		Iterator<String> it = tokenTable.keySet().iterator();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Vector<String> v1 = tokenTable.get(key);
			log.debug("..." + key + ":" + v1);
		}
	}

	public static void beginCheckU(ParaMap inMap, HttpSession session)
			throws Exception {
		if (strictParaMap(inMap)) {
			String u = inMap.getString("u");
			if (StringUtils.isEmpty(u)) {
				throw new Exception("0x01 用户为空:服务器拒绝提供数据服务");
			}
		}
	}

	/**
	 * 开始检查
	 * 
	 * @param inMap
	 * @param session
	 * @throws Exception
	 */
	public static void beginCheck(ParaMap inMap, HttpSession session)
			throws Exception {
		if (strictParaMap(inMap)) {
			String u = inMap.getString("u");
			if (StringUtils.isEmpty(u))
				throw new Exception("0x01 用户为空:服务器拒绝提供数据服务");
			String sid = inMap.getString("s");
			setTokenKey(sid);
			String tokenKey = threadLocal.get();
			String t = inMap.getString("t");
			// tokenTimeTable.put(DateUtils.nowTime(), tokenKey);
			Vector<String> v = tokenTable.get(tokenKey);
			if (StringUtils.isNotEmpty(t) && v.indexOf(t) == -1) {
				throw new Exception("0x02 非法令牌[" + t + "]:服务器拒绝提供数据服务");
			}
			if (v != null)
				v.remove(t);
			// clearTokens();
			inMap.remove("s");
			inMap.remove("t");
		}
	}

	/**
	 * 结束检查
	 * 
	 * @param inMap
	 * @param out
	 * @param session
	 * @throws Exception
	 */
	public static void endCheck(ParaMap inMap, ParaMap out, HttpSession session)
			throws Exception {
		if (strictParaMap(inMap)) {
			initTokens();
			String tokenKey = threadLocal.get();
			out.put("ts", tokenTable.get(tokenKey));
		}
	}

	/**
	 * 初始化Tokens
	 */
	private static void initTokens() {
		String tokenKey = threadLocal.get();
		Vector<String> v = tokenTable.get(tokenKey);
		if (v == null)
			v = new Vector<String>();
		int len = 3 - v.size();
		for (int i = 0; i < len; i++) {
			v.add(IDGenerator.newGUID());
		}
		tokenTable.put(tokenKey, v);
	}

	/**
	 * 获取字符串
	 * 
	 * @return
	 */
	public static String getTokensJson() {
		initTokens();
		String tokenKey = threadLocal.get();
		Vector<String> v = tokenTable.get(tokenKey);
		JSONArray jsonArr = new JSONArray();
		jsonArr.addAll(v);
		String json = jsonArr.toString();
		return json;
	}

	/**
	 * 清空Token列表
	 */
	private static void clearTokens() {
		Iterator<Long> it = tokenTimeTable.keySet().iterator();
		Long now = DateUtils.nowTime();
		while (it.hasNext()) {
			Long time = it.next();
			if (now - time >= interval) {
				String tokenKey = tokenTimeTable.get(time);
				if (StringUtils.isNotEmpty(tokenKey)) {
					tokenTimeTable.remove(time);
					tokenTable.remove(tokenKey);
				}
			} else {
				break;
			}
		}
	}

	public static void main(String[] args) {
		tokenTimeTable.put(1L, "aaa1");
		tokenTimeTable.put(3L, "aaa3");
		tokenTimeTable.put(2L, "aaa2");
		tokenTimeTable.put(5L, "aaa5");
		tokenTimeTable.put(4L, "aaa4");

		Iterator<Long> it = tokenTimeTable.keySet().iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
			break;
		}

	}
}

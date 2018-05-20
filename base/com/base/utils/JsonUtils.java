package com.base.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.base.utils.ParaMap;

/**
 * 2012-07-10
 * @author SAMONHUA
 *
 */
public class JsonUtils {
	static private Object convertJson(Object json) {
		if (json == null)
			return null;
		else if (json instanceof JSONArray) {
			List list = JsonToList((JSONArray) json);
			return list;
		} else if (json instanceof JSONObject) {
			ParaMap map = JsonToMap((JSONObject) json);
			return map;
		} else {
			return json;
		}
	}
	
	/**
	 * json对象转换为List对象
	 * @param json json对象
	 * @return
	 */
	static public List JsonToList(JSONArray json) {
		if (json == null)
			return null;
		List result = new ArrayList();
		for(int i = 0; i < json.size(); i++) {
			result.add(convertJson(json.get(i)));
		}
		return result;
	}
	
	/**
	 * json对象转换为ParaMap对象
	 * @param json json对象
	 * @return
	 */
	static public ParaMap JsonToMap(JSONObject json) {
		if (json == null || json.isEmpty() || json.isNullObject())
			return null;
		ParaMap result = new ParaMap();
		Iterator it = json.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			result.put(key, convertJson(json.get(key)));
		}
		return result;
	}
	
	/**
	 * 从JSON字段串返回ParaMap对象
	 * @param json json对象，数组等不支持
	 * @return
	 */
	static public ParaMap StrToMap(String json) {
		if (StrUtils.isNull(json))
			return null;
		try {
			return JsonToMap(JSONObject.fromObject(json));
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * 从JSON字段串返回List对象
	 * @param json json对象，数组等不支持
	 * @return
	 */
	static public List StrToList(String json) {
		if (StrUtils.isNull(json))
			return null;
		try {
			return JsonToList(JSONArray.fromObject(json));
		} catch (JSONException e) {
			return null;
		}
	}
}

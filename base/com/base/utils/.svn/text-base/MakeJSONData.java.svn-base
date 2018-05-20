package com.base.utils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.base.utils.MakeJSONDataCallBack;

/**
 * 通用生成树结构JSON数据
 * huafc
 * 2012-04-20
 */
public class MakeJSONData {
	public static String CUSTOM_RECURSE = "recurse";                            //0列表式，1递归树，2延迟加载树
	public static String CUSTOM_LEVEL_FIELD_NAME = "levelFieldName";            //树层级字段名，树状数据（recurse=1）时必须
	public static String CUSTOM_CHILDREN_KEY_NAME = "childrenKeyName";          //JSON中子记录属性名，树状数据（recurse=1）时必须
	public static String CUSTOM_ID_FIELD_NAME = "idFieldName";                  //主键字段名，树状数据（recurse=1）时必须
	public static String CUSTOM_PARENT_ID_FIELD_NAME = "parentIdFieldName";     //父ID字段名，树状数据（recurse=1）时必须
	public static String CUSTOM_CHILD_COUNT_FIELD_NAME = "childCountFieldName"; //子记录数量字段名
	public static String CUSTOM_FIELDS = "fields";                              //需要添加到JSON中的属性名与字段名匹配MAP名
	
	//解析字符串中所有以“:”开关的参数列表
	private static List parseStringParams(String str) {
		if (str == null || str.equals("") || str.equalsIgnoreCase("null") || str.indexOf(':') == -1)
			return null;
		List result = new ArrayList();
		StringTokenizer st = new StringTokenizer(" " + str, ":");
        int i = -1;
        while(st.hasMoreTokens()){
        	String paramName = st.nextToken();
        	char[] arrayParam = paramName.toCharArray();
    		StringBuilder sbParam = new StringBuilder(100);
    		for(char c:arrayParam){
    			int cAscii = (int)c;
    			if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == 95)
    				sbParam.append(c);
    			else
    				break;
    		}
    		paramName = sbParam.toString();
    		if (i > -1)
    			result.add(paramName);
        	i++;
        }
		//参数排序
		Comparator cmp = Collator.getInstance();
        Collections.sort(result, cmp);
        return result;
	}
	
	//生成节点JSON对象
	public static ParaMap createItem(ParaMap itemsMap, int record, ParaMap custom, MakeJSONDataCallBack callBack) {
		int intRecurse = 0;
		if (custom.containsKey(CUSTOM_RECURSE))
			intRecurse = custom.getInt(CUSTOM_RECURSE);
		String childrenKeyName = custom.getString(CUSTOM_CHILDREN_KEY_NAME);
		String childCountFieldName = custom.getString(CUSTOM_CHILD_COUNT_FIELD_NAME);
		String idFieldName = custom.getString(CUSTOM_ID_FIELD_NAME);
		ParaMap item = new ParaMap();
		ParaMap fields = (ParaMap) custom.get(CUSTOM_FIELDS);
		if (fields != null && fields.size() > 0) {
			Iterator it = fields.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = fields.getString(key);
				if (value.indexOf(":") >= 0) {//有冒号则表示是有格式的串
					List formatParams = parseStringParams(value);
					for (int i = formatParams.size() - 1; i >= 0 ; i--) {
						String paramName = (String) formatParams.get(i);
						value = value.replaceAll("(?u):" + paramName, String.valueOf(itemsMap.getRecordValue(record, paramName)));
					}
					item.put(key, value);
				} else if (itemsMap.getFields().indexOf(value) >= 0)
					item.put(key, itemsMap.getRecordValue(record, value));
				else
					item.put(key, value);
			}
		}
		if (intRecurse == 1) {
			List childItemList = new ArrayList();
			getChildItem(itemsMap, childItemList, String.valueOf(itemsMap.getRecordValue(record, idFieldName)), custom, callBack);
			if (childItemList.size() > 0)
				item.put(childrenKeyName, childItemList);
		} else if (intRecurse == 2) {
			if (childCountFieldName == null || childCountFieldName.equals("") || childCountFieldName.equalsIgnoreCase("null")
					|| itemsMap.getFields().indexOf(childCountFieldName) == -1
					|| Double.parseDouble(String.valueOf(itemsMap.getRecordValue(record, childCountFieldName))) > 0)
				item.put(childrenKeyName, new ArrayList());
		}
		if (callBack != null)
			callBack.makeItem(itemsMap, record, item);
		return item;
	}
	
	//生成子Item对象列表
	public static void getChildItem(ParaMap itemsMap, List childItems, String parentItemId, ParaMap custom, MakeJSONDataCallBack callBack) {
		String childrenKeyName = custom.getString(CUSTOM_CHILDREN_KEY_NAME);
		String idFieldName = custom.getString(CUSTOM_ID_FIELD_NAME);
		String parentIdFieldName = custom.getString(CUSTOM_PARENT_ID_FIELD_NAME);
		for(int i = 0;i < itemsMap.getRecords().size(); i++) {
			if (String.valueOf(itemsMap.getRecordValue(i, parentIdFieldName)).equals(parentItemId)) {
				ParaMap item = createItem(itemsMap, i, custom, callBack);
				List childItemList = new ArrayList();
				getChildItem(itemsMap, childItemList, String.valueOf(itemsMap.getRecordValue(i, idFieldName)), custom, callBack);
				if (childItemList.size() > 0)
					item.put(childrenKeyName, childItemList);
				childItems.add(item);
			}
		}
	}

	//生成树状Items
	public static List makeItems(ParaMap itemsMap, ParaMap custom, MakeJSONDataCallBack callBack) throws Exception {
		if (itemsMap == null)
			return null;
		int intRecurse = 0;
		ParaMap customConfig = custom == null ? new ParaMap() : custom;
		if (customConfig.containsKey(CUSTOM_RECURSE))
			intRecurse = customConfig.getInt(CUSTOM_RECURSE);
		String levelFieldName = customConfig.getString(CUSTOM_LEVEL_FIELD_NAME);
		ParaMap fields = (ParaMap) customConfig.get(CUSTOM_FIELDS);
		if (fields == null || fields.size() == 0) { //无添加字段清单则加载所有字段，按字段名
			//return null;
			if (fields == null)
				fields = new ParaMap();
			List itemsFields = itemsMap.getFields();
			for(int i = 0; i < itemsFields.size(); i++) {
				String fieldName = String.valueOf(itemsFields.get(i));
				fields.put(fieldName, fieldName);
			}
			customConfig.put(CUSTOM_FIELDS, fields);//此行改变的参数customConfig的值
		}
		List items = new ArrayList();
		for(int i = 0; i < itemsMap.getRecords().size(); i++) {
			//(Integer) itemsMap.getRecordValue(i, levelFieldName)
			//Math.round((Double) itemsMap.getRecordValue(i, levelFieldName))
			if (intRecurse != 1 || (Integer) itemsMap.getRecordValue(i, levelFieldName) == 1) {
				ParaMap item = createItem(itemsMap, i, customConfig, callBack);
				items.add(item);
			}
		}
		return items;
	}
	
	public static List makeItems(ParaMap itemsMap, ParaMap custom) throws Exception {
		return makeItems(itemsMap, custom, null);
	}
	
	public static List makeItems(ParaMap itemsMap) throws Exception {
		return makeItems(itemsMap, null);
	}
}

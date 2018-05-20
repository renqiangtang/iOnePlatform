package com.base.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;

/**
 * 
 * @author 朱金亮
 * 
 */

public class ParaMap extends HashMap {
	private static final long serialVersionUID = 1L;
	private static final String boolTrueValue = "true;1;yes;ok";
	private static final boolean boolNullValue = false;
	private static final boolean boolEmptyValue = false;
	
	public ParaMap() {
		
	}
	
	public ParaMap(Map map) {
		if (map != null)
			this.putAll(map);
	}

	public Object put(Object key, Object value) {
		String k = String.valueOf(key);
		return super.put(k, value);
	}

	public String getString(String key) {
		String v = null;
		if (this.containsKey(key)) {
			v = String.valueOf(get(key));
		}
		return v;
	}

	public Double getDouble(String key) {
		if (this.containsKey(key)) {
			String v = getString(key);
			Double d = Double.parseDouble(v);
			return d;
		} else {
			return null;
		}
	}

	public int getInt(String key, int defaultValue) {
		Integer v = getInteger(key);
		if (v == null)
			return defaultValue;
		else
			return v;
	}
	
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	public Integer getInteger(String key) {
		if (this.containsKey(key)) {
			String v = getString(key);
			int i = Integer.parseInt(v);
			return i;
		} else
			return null;
	}
	
	public byte[] getBytes(String key) {
		byte[] buf = null;
		if (this.containsKey(key)) {
			buf = (byte[]) get(key);
		}
		return buf;
	}
	
	public FileItem getFileItem(String key) {
		Object obj = get(key);
		return (FileItem) obj;
	}

	public Long getLong(String key) {
		if (this.containsKey(key)) {
			String v = getString(key);
			long l = Long.parseLong(v);
			return l;
		} else
			return null;
	}
	
	public Boolean getBoolean(String key) {
		if (this.containsKey(key)) {
			String v = getString(key);
			boolean l = Boolean.parseBoolean(v);
			return l;
		} else
			return null;
	}

	public BigDecimal getBigDecimal(String key) {
		BigDecimal b = null;
		if (this.containsKey(key)) {
			String v = getString(key);
			if (v != null && !"".equals(v) && !v.equalsIgnoreCase("null")) {
				b = new BigDecimal(v);
			}
		}
		return b;
	}

	public Date getDate(String key) {
		if (this.containsKey(key)) {
			String v = getString(key);
			return DateUtils.getDate(v);
		} else
			return null;
	}

	public List getList(String key) {
		Object obj = get(key);
		return (List) obj;
	}

	public List getFields() {
		return getList("fs");
	}

	public int getFieldCount() {
		List fs = getFields();
		if (fs == null)
			return -1;
		else
			return fs.size();
	}
	
	public boolean hasField(String fieldName) {
		return getFieldIndex(fieldName) != -1;
	}
	
	public int getFieldIndex(String fieldName) {
		List fs = getFields();
		if (fs == null)
			return -1;
		else
			return fs.indexOf(fieldName);
	}

	public String getField(int i) {
		List fs = getFields();
		if (fs == null)
			return null;
		else
			return String.valueOf(fs.get(i));
	}

	public List getRecords() {
		return getList("rs");
	}

	public int getRecordCount() {
		List rs = getRecords();
		if (rs == null)
			return -1;
		else
			return rs.size();
	}
	
	public int getSize() {
		return getRecordCount();
	}

	public ParaMap clone() {
		ParaMap out = new ParaMap();
		out.putAll(this);
		return out;
	}

	public Object getRecordValue(int recordNo, String fieldName) {
		return getRecordValue(recordNo, getFieldIndex(fieldName));
	}

	public Object getRecordValue(int recordNo, int fieldIndex) {
		if (fieldIndex < 0)
			return null;
		List fs = getFields();
		if (fs == null || fs.size() == 0 || fieldIndex >= fs.size())
			return null;
		List record = (List) getRecords().get(recordNo);
		return record.get(fieldIndex);
	}

	public String getRecordString(int recordNo, String fieldName) {
		return getRecordString(recordNo, getFieldIndex(fieldName));
	}

	public String getRecordString(int recordNo, int fieldIndex) {
		Object value = getRecordValue(recordNo, fieldIndex);
		if (value == null)
			return null;
		else
			return value.toString();
	}

	public Integer getRecordInteger(int recordNo, String fieldName) {
		return getRecordInteger(recordNo, getFieldIndex(fieldName));
	}

	public Integer getRecordInteger(int recordNo, int fieldIndex) {
		String value = getRecordString(recordNo, fieldIndex);
		if (StrUtils.isNull(value) || !StrUtils.isInteger(value))
			return null;
		else
			return Integer.parseInt(value);
	}
	
	public int getRecordInt(int recordNo, String fieldName, int defaultValue) {
		return getRecordInt(recordNo, getFieldIndex(fieldName), defaultValue);
	}
	
	public int getRecordInt(int recordNo, String fieldName) {
		return getRecordInt(recordNo, getFieldIndex(fieldName));
	}

	public int getRecordInt(int recordNo, int fieldIndex, int defaultValue) {
		Integer value = getRecordInteger(recordNo, fieldIndex);
		if (value == null)
			return defaultValue;
		else
			return value;
	}
	
	public int getRecordInt(int recordNo, int fieldIndex) {
		return getRecordInt(recordNo, fieldIndex, 0);
	}

	public Double getRecordDouble(int recordNo, String fieldName) {
		return getRecordDouble(recordNo, getFieldIndex(fieldName));
	}

	public Double getRecordDouble(int recordNo, int fieldIndex) {
		String value = getRecordString(recordNo, fieldIndex);
		if (StrUtils.isNull(value) || !StrUtils.isNumber(value))
			return null;
		else
			return Double.parseDouble(value);
	}
	
	public double getRecordDoubleBase(int recordNo, String fieldName) {
		return getRecordDoubleBase(recordNo, getFieldIndex(fieldName));
	}

	public double getRecordDoubleBase(int recordNo, int fieldIndex, double defaultValue) {
		Double value = getRecordDouble(recordNo, fieldIndex);
		if (value == null)
			return defaultValue;
		else
			return value;
	}
	
	public double getRecordDoubleBase(int recordNo, int fieldIndex) {
		return getRecordDoubleBase(recordNo, fieldIndex, 0);
	}

	public Date getRecordDateTime(int recordNo, String fieldName,
			String dateFormat) {
		return getRecordDateTime(recordNo, getFieldIndex(fieldName), dateFormat);
	}

	public Date getRecordDateTime(int recordNo, String fieldName) {
		return getRecordDateTime(recordNo, getFieldIndex(fieldName));
	}

	public Date getRecordDateTime(int recordNo, int fieldIndex,
			String dateFormat) {
		String value = getRecordString(recordNo, fieldIndex);
		if (StrUtils.isNull(value) || !StrUtils.isDate(value))
			return null;
		else
			return StrUtils.stringToDate(value, dateFormat);
	}

	public Date getRecordDateTime(int recordNo, int fieldIndex) {
		return getRecordDateTime(recordNo, fieldIndex, "yyyy-MM-dd HH:mm:ss");
	}

	public boolean getRecordBool(int recordNo, String fieldName) {
		return getRecordBool(recordNo, getFieldIndex(fieldName));
	}

	public boolean getRecordBool(int recordNo, int fieldIndex) {
		String value = StrUtils.trim(getRecordString(recordNo, fieldIndex));
		if (value == null)
			return boolNullValue;
		else if (value.equals(""))
			return boolEmptyValue;
		else
			return StrUtils.paramExists(boolTrueValue, value, true);
	}

	public String toString() {
		JSONObject jsonObject = JSONObject.fromObject(this);
		return jsonObject.toString();
	}
	
	public void print() {
		Iterator it = this.keySet().iterator();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			Object value = this.get(key);
			System.out.println(key + ":" + value);
		}
	}

	/**
	 * 从JSON字段串返回对象
	 * 
	 * @param json
	 *            json对象，如果json是数组对象请使用JsonUtils类中相关方法完成
	 * @return
	 */
	static public ParaMap fromString(String json) {
		return JsonUtils.StrToMap(json);
	}

	/**
	 * 返回Map中指定路径的对象值，用于json转换的Map对象。要求键都为字符串
	 * 
	 * @param path
	 *            对象值路径，如：aaaa.bbbb[2].cccc。如果某步为List可以使用索引
	 * @return
	 * @throws Exception
	 */
	public Object getDataByPath(String path) throws Exception {
		if (size() == 0 || StrUtils.isNull(path))
			return this;
		Object result = null;
		ParaMap item = this;
		List<String> configPathList = StrUtils.getSubStrs(path, ".", true);
		for (int i = 0; i < configPathList.size(); i++) {
			String subPath = configPathList.get(i);
			int itemIndex = -1;
			if (subPath.indexOf("[") != -1
					&& subPath.substring(subPath.length() - 1).equals("]")) {
				String strIndex = subPath.substring(subPath.indexOf("["),
						subPath.length() - 1);
				if (!StrUtils.isInteger(strIndex))
					throw new Exception(subPath + "中索引值错误");
				itemIndex = Integer.parseInt(strIndex);
				subPath = subPath.substring(0, subPath.indexOf("["));
			}
			if (item.containsKey(subPath)) {
				Object subItem = item.get(subPath);
				if (i == configPathList.size() - 1) {
					if (itemIndex == -1) {
						result = subItem;
					} else if (subItem instanceof List) {
						List subItemList = (List) subItem;
						if (itemIndex >= 0 && itemIndex < subItemList.size()) {
							result = subItemList.get(itemIndex);
						} else {
							throw new Exception(itemIndex + "超出" + subPath
									+ "的索引范围");
						}
					} else {
						throw new Exception(subPath
								+ "类型错误，并非列表对象。无法获取指定索引位置的对象");
					}
				} else if (subItem instanceof ParaMap) {
					item = (ParaMap) subItem;
				} else {
					throw new Exception(subPath + "类型错误。无法获取指定位置的对象进行下一轮读取数据");
				}
			} else {
				throw new Exception("取值路径" + subPath + "不存在");
			}
		}
		return result;
	}

	public List<ParaMap> getListObj() {
		List resultList = new ArrayList();
		List filds = this.getFields();
		for (int i = 0; i < this.getRecordCount(); i++) {
			ParaMap out = new ParaMap();
			for (int j = 0; j < filds.size(); j++) {
				out.put(filds.get(j), this.getRecordValue(i, j));
			}
			resultList.add(out);
		}
		return resultList;
	}

	public ParaMap getResultMap() {
		ParaMap out = new ParaMap();
		for (int i = 0; i < this.getSize(); i++) {
			String id = this.getRecordString(i, "id");
			String value = this.getRecordString(i, "value");
			out.put(id, value);
		}
		return out;
	}
}

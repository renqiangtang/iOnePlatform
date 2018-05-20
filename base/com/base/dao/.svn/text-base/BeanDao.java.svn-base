package com.base.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.base.bean.IDBean;

public class BeanDao extends BaseDao {
	Logger log = Logger.getLogger(BeanDao.class);
	public static List<String> typeList = new ArrayList();
	static {
		typeList.add("int");
		typeList.add("double");
		typeList.add("String");
		typeList.add("List");
	}

	/**
	 * 按List保存数据
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void saveBean(List<IDBean> list) throws Exception {
		for (int i = 0; i < list.size(); i++)
			saveBean(list.get(i));
	}

	/**
	 * 按Bean保存数据
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public void saveBean(IDBean obj) throws Exception {
		String tableName = obj.getClass().getSimpleName();
		saveBean(obj, tableName);

	}

	/**
	 * 按Bean保存数据
	 * 
	 * @param obj
	 * @param tableName
	 * @throws Exception
	 */
	public void saveBean(IDBean obj, String tableName) throws Exception {
		Class clazz = obj.getClass();
		Field[] fs = clazz.getFields();
		StringBuffer fsb = new StringBuffer();
		StringBuffer vsb = new StringBuffer();
		for (int i = 0; i < fs.length; i++) {
			if(typeList.indexOf(fs[i].getType().getSimpleName()) != 3)
				fsb.append(fs[i].getName().toUpperCase());
			String typeSimpleName = fs[i].getType().getSimpleName();
			int index = typeList.indexOf(typeSimpleName);
			String value = "";
			switch (index) {
			case 0:// int
				
				value = String.valueOf(fs[i].getInt(obj));
				vsb.append(value);
				break;
			case 1:// double
				value = String.valueOf(fs[i].getDouble(obj));
				vsb.append(value);
				break;
			case 2:// String
				value = String.valueOf(fs[i].get(obj));
				value = "'"+value+"'";
				vsb.append(value);
				break;
			case 3:// List
				List<IDBean> list = (List) fs[i].get(obj);
				for (int j = 0; j < list.size(); j++) {
					saveBean(list.get(j));
				}
				break;
			default:
				throw new Exception("没有合适的类型");
			}
			//vsb.append(value);
			if (i != fs.length - 1&&typeList.indexOf(fs[i].getType().getSimpleName()) != 3) {
				fsb.append(",");
				vsb.append(",");
			}

		}
		String sql = "insert into " + tableName + "(" + fsb.toString()
				+ ") values(" + vsb.toString() + ")";
		log.debug("saveBean...sql...." + sql);
		PreparedStatement ps = getCon().prepareStatement(sql);
		ps.executeUpdate();
	}

	/**
	 * 删除
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void delBean(List<IDBean> list) throws Exception {
		for (int i = 0; i < list.size(); i++)
			delBean(list.get(i));
	}

	/**
	 * 删除Bean
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public void delBean(IDBean obj) throws Exception {
		String tableName = obj.getClass().getSimpleName();
		delBean(obj, tableName);
	}

	/**
	 * 删除Bean
	 * 
	 * @param obj
	 * @param tableName
	 * @throws Exception
	 */
	public void delBean(IDBean obj, String tableName) throws Exception {
		String idValue = String.valueOf(obj.getClass().getField("id").get(obj));
		String sql = "delete from " + tableName + " where id='" + idValue + "'";
		log.debug("delBean...sql...." + sql);
		PreparedStatement ps = getCon().prepareStatement(sql);
		ps.executeUpdate();
	}

	/**
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void updateBean(List<IDBean> list) throws Exception {
		for (int i = 0; i < list.size(); i++)
			updateBean(list.get(i));
	}

	/**
	 * 更新Bean
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public void updateBean(IDBean obj) throws Exception {
		String tableName = obj.getClass().getSimpleName();
		updateBean(obj, tableName);
	}

	/**
	 * 更新Bean
	 * 
	 * @param obj
	 * @param tableName
	 * @throws Exception
	 */
	public void updateBean(IDBean obj, String tableName) throws Exception {
		delBean(obj, tableName);
		saveBean(obj, tableName);
	}

}

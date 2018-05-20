package com.base.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import com.base.dao.BaseDao;
import com.base.utils.StrUtils;

/**
 * ID号生成类
 * 
 * @author SAMONHUA
 * 
 */
public class IDGenerator {

	public static String local() {
		Random rnd = new Random();
		int p1 = rnd.nextInt(9);
		int p2 = rnd.nextInt(9);
		int p3 = rnd.nextInt(9);
		Date d1 = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String s1 = df.format(d1) + p1 + p2 + p3;
		return s1;
	}

	// ID号生成方式：0GUID，1数据库create_table_id过程生成
	// 对性能要示较高时（如需要快速频繁的插入多条记录），请调用newGUID方法
	// 如果是调用DataSetDao.updateData方法时，请先通过newGUID方法获取ID值
	private static final int GENERATOR_ID_MODE = 1;

	/**
	 * 生成新的GUID
	 * 
	 * @param secure
	 *            true表示返回安全的GUID，如“479A9159DA2A4A8EBD660B188BB07F5D”<br/>
	 *            否则返回如“479A9159-DA2A-4A8E-BD66-0B188BB07F5D”
	 * @return 返回GUID
	 */
	public static String newGUID(boolean secure) {
		return StrUtils.newGUID(secure);
	}

	/**
	 * 生成新的安全的GUID
	 * 
	 * @return 返回GUID，如：479A9159DA2A4A8EBD660B188BB07F5D
	 */
	public static String newGUID() {
		return newGUID(true);
	}

	/**
	 * 返回数据库生成的ID值，可以给表主键值，或者业务编号（此情况请手工在sys_table中中注册）
	 * 
	 * @param tableName
	 *            表名
	 * @param keyField
	 *            键值字段名，并非一定是主键字段，可以为空（系统自动按主键字段处理）
	 * @param returnCount
	 *            返回多个Id值时占用数
	 * @param returnType
	 *            返回Id值类别。1返回最大Id整数值，2返回前缀（不影响最大值），其它值返回最大Id值
	 * @return 新的最大ID值，请参考returnType参数
	 * @throws SQLException
	 */
	public static String newTableId(String tableName, String keyField,
			int returnCount, int returnType) throws SQLException {
		BaseDao baseDao = new BaseDao();
		ResultSet rs = null;
		PreparedStatement pstm = baseDao.getCon().prepareStatement(
				"select create_table_id(?, ?, ?, ?) as new_id from dual");
		try {
			pstm.setString(1, tableName);
			pstm.setString(2, keyField == null ? "" : keyField);
			pstm.setInt(3, returnCount);
			pstm.setInt(4, returnType);
			rs = pstm.executeQuery();
			if (rs.next())
				return rs.getString(1);
			else
				return null;
		} finally {
			if (rs != null)
				rs.close();
			pstm.close();
		}
	}

	/**
	 * 返回数据库生成的主键字段ID值
	 * 
	 * @param tableName
	 *            表名
	 * @return 新的最大ID值
	 * @throws SQLException
	 */
	public static String generatorID(String tableName) {
		return generatorID(tableName, null, 0, 0);
	}

	/**
	 * 返回数据库生成的指定键值字段的ID值
	 * 
	 * @param tableName
	 *            表名
	 * @param keyField
	 *            键值字段名，并非一定是主键字段，可以为空（系统自动按主键字段处理）
	 * @return 新的最大ID值
	 * @throws SQLException
	 */
	public static String generatorID(String tableName, String keyField) {
		return generatorID(tableName, keyField, 0, 0);
	}

	/**
	 * 返回数据库生成的ID值，可以给表主键值，或者业务编号（此情况请手工在sys_table中中注册）
	 * 
	 * @param tableName
	 *            表名
	 * @param keyField
	 *            键值字段名，并非一定是主键字段，可以为空（系统自动按主键字段处理）
	 * @param returnCount
	 *            返回多个Id值时占用数
	 * @return 新的最大ID值
	 * @throws SQLException
	 */
	public static String generatorID(String tableName, String keyField,
			int returnCount) {
		return generatorID(tableName, keyField, returnCount, 0);
	}

	/**
	 * 返回数据库生成的ID值，可以给表主键值，或者业务编号（此情况请手工在sys_table中中注册）
	 * 
	 * @param tableName
	 *            表名
	 * @param keyField
	 *            键值字段名，并非一定是主键字段，可以为空（系统自动按主键字段处理）
	 * @param returnCount
	 *            返回多个Id值时占用数
	 * @param returnType
	 *            返回Id值类别。1返回最大Id整数值，2返回前缀（不影响最大值），其它值返回最大Id值
	 * @return 新的最大ID值，请参考returnType参数
	 * @throws SQLException
	 */
	public static String generatorID(String tableName, String keyField,
			int returnCount, int returnType) {
		if (GENERATOR_ID_MODE == 1) {
			try {
				return newTableId(tableName, keyField, returnCount, returnType);
			} catch (Exception e) {
				Logger log = Logger.getLogger(IDGenerator.class);
				log.error("create_table_id调用失败");
				return null;
			}
		} else {
			return newGUID(true);
		}
	}
}

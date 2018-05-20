package com.base.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.base.ds.DataSourceManager;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.utils.StreamUtils;

/**
 * 
 * 
 * @author 朱金亮
 * 
 */
public class BaseDao {
	protected Logger log = Logger.getLogger(this.getClass());
	

	public Connection getCon() {
		return DataSourceManager.getConnection();
	}

	public Connection getComCon() {
		return DataSourceManager.getConnection();
	}

	public ParaMap convert(ResultSet rs) throws SQLException {
		ParaMap pm = new ParaMap();
		List fields = new ArrayList();
		List records = new ArrayList();
		int colCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String name = rs.getMetaData().getColumnName(i).toLowerCase();
			fields.add(name);
		}
		while (rs.next()) {
			List record = new ArrayList();
			for (int i = 1; i <= colCount; i++) {
				record.add(getValue(rs, i));
			}
			records.add(record);
		}
		pm.put("fs", fields);
		pm.put("rs", records);
		return pm;
	}

	public Object getValue(ResultSet rs, int col) throws SQLException {
		Object obj = rs.getObject(col);
		if (obj != null) {
			int type = rs.getMetaData().getColumnType(col);
			switch (type) {
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				double d = rs.getDouble(col);
				try {
					int i = (new Double(d)).intValue();
					BigDecimal b1 = new BigDecimal(d);
					BigDecimal b2 = new BigDecimal(i);
					int compare = b1.compareTo(b2);
					if (compare == 0)
						obj = i;
					else
						obj = d;
				} catch (Exception ex) {
					obj = d;
				}
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				obj = rs.getString(col);
				break;
			case Types.DATE:
			case Types.TIME:
				Timestamp s1 = rs.getTimestamp(col);
				Date d1 = new Date(s1.getTime());
				obj = DateUtils.getStr(d1);
				break;
			case Types.TIMESTAMP:
				Timestamp s2 = rs.getTimestamp(col);
				obj = s2.toString();
				break;
			}
		}
		return obj;
	}

	/**
	 * 更新BOLB
	 * 
	 * @author liangrj tableName为表名 keyFieldName为关键字字段名 keyFieldValue为关键字值
	 *         blobFieldName为BLOB字段名 inputStream为BLOB流
	 */
	public void updateBlobContent(String tableName, String keyFieldName,
			String keyFieldValue, String blobFieldName, InputStream inputStream)
			throws Exception {// tableName为表名，keyFieldName为关键字段名，keyFieldValue为关键字段的值，blobFieldName为BLOB字段名，inputStream为BLOB字段值
		Connection con = null;
		ResultSet resultset = null;
		Statement stmt = null;
		con = getCon();
		StringBuffer sql = new StringBuffer(" update " + tableName + " Set "
				+ blobFieldName + "= EMPTY_BLOB() where 1=1  ");
		if (keyFieldName != null && !"".equals(keyFieldName)) {
			String[] keyFieldNameArray = keyFieldName.split(",");
			String[] keyFieldValueArray = keyFieldValue.split(",");
			if (keyFieldNameArray.length > 0) {
				for (int i = 0; i < keyFieldNameArray.length; i++) {
					if (!"<null>".equals(keyFieldValueArray[i])) {
						sql.append("  And" + " " + keyFieldNameArray[i]
								+ " = '" + keyFieldValueArray[i] + "'  ");

					} else {
						sql.append("  And" + " " + keyFieldNameArray[i] + "  "
								+ " Is Null ");
					}
				}
			}
		}
		stmt = con.createStatement();
		stmt.executeUpdate(sql.toString());
		con.setAutoCommit(false);
		sql = new StringBuffer(" SELECT  " + blobFieldName + " FROM "
				+ tableName + "   WHERE 1=1  ");
		if (keyFieldName != null && !"".equals(keyFieldName)) {
			String[] keyFieldNameArray = keyFieldName.split(",");
			String[] keyFieldValueArray = keyFieldValue.split(",");
			if (keyFieldNameArray.length > 0) {
				for (int i = 0; i < keyFieldNameArray.length; i++) {
					if (!"<null>".equals(keyFieldValueArray[i])) {
						sql.append("  And" + " " + keyFieldNameArray[i]
								+ " = '" + keyFieldValueArray[i] + "'  ");
					} else {
						sql.append("  And" + " " + keyFieldNameArray[i] + "  "
								+ " Is Null ");
					}
				}
			}
		}
		sql.append(" FOR UPDATE ");
		resultset = stmt.executeQuery(sql.toString());
		while (resultset.next()) {
			oracle.sql.BLOB blob = (oracle.sql.BLOB) resultset
					.getBlob(blobFieldName);
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				out = new BufferedOutputStream(blob.getBinaryOutputStream());
				in = new BufferedInputStream(inputStream);
				byte[] buffer = new byte[2048];
				int len;
				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
		}
	}

	/**
	 * 返回指定表的指定记录的Blob字段内容 tableName为表名 keyFieldName为关键字字段名 keyFieldValue为关键字值
	 * blobFieldName为BLOB字段名
	 * 
	 * @throws Exception
	 */
	public BufferedInputStream getBlobContent(String tableName,
			String keyFieldName, String keyFieldValue, String blobFieldName)
			throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet resultset = null;
		con = getCon();
		StringBuffer sql = new StringBuffer("Select " + blobFieldName
				+ " From " + tableName + " where 1=1  ");
		if (keyFieldName != null && !"".equals(keyFieldName)) {
			String[] keyFieldNameArray = keyFieldName.split(",");
			String[] keyFieldValueArray = keyFieldValue.split(",");
			if (keyFieldNameArray.length > 0) {
				for (int i = 0; i < keyFieldNameArray.length; i++) {
					if (!"<null>".equals(keyFieldValueArray[i])) {
						sql.append("  And" + " " + keyFieldNameArray[i]
								+ " = '" + keyFieldValueArray[i] + "'  ");
					} else {
						sql.append("  And" + " " + keyFieldNameArray[i] + "  "
								+ " Is Null ");
					}
				}
			}
		}
		BufferedInputStream bis = null;
		ps = con.prepareStatement(sql.toString());
		resultset = ps.executeQuery();
		while (resultset.next()) {
			if (resultset.getBinaryStream(blobFieldName) != null) {
				bis = new BufferedInputStream(
						resultset.getBinaryStream(blobFieldName));
			}
		}
		return bis;

	}

	/**
	 * 根据登录人ID获取竞买人ID
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */

	public static ParaMap getBidderId(String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = new ParaMap();
		if (userId != null && !"".equals(userId) && !"null".equals(userId)) {
			ParaMap sqlParams = new ParaMap();
			sqlParams.put("id", userId);
			sqlParams.put("moduleNo", "sys_user_manager");
			sqlParams.put("dataSetNo", "queryBidderDataByUserId");
			sqlParams = dataSetDao.queryData(sqlParams);
			if ("1".equals(sqlParams.getString("state"))) {
				out.put("id", sqlParams.getRecordValue(0, "id"));
				out.put("name", sqlParams.getRecordValue(0, "name"));
				out.put("mobile", sqlParams.getRecordValue(0, "mobile"));
			}

		}
		return out;
	}

	/**
	 * 保存加密信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveSysLobCa(String refId, String refTableName,
			String userId, String content) throws Exception {

		ParaMap returnMap = new ParaMap();
		// 把上传的文件保存到附件表中sys_lob
		DataSetDao dao = new DataSetDao();
		// 根据数据来源表名和数据来源表ID查询最大的DATA_INDEX(数据索引，指一个ref_id值对应多个大字段时，从1开始)

		// 保存主表
		ParaMap saveLobMap = new ParaMap();
		saveLobMap.put("id", "");
		saveLobMap.put("ref_table_name", refTableName);
		saveLobMap.put("ref_id", refId);
		saveLobMap.put("data_type", 1);
		saveLobMap.put("create_user_id", userId);
		ParaMap sysLobMap = dao.updateData("sys_lob", "id", saveLobMap, null);
		if ("1".equals(sysLobMap.getString("state"))) {// 保存sys_blob_dtl
			String lobId = sysLobMap.getString("id");
			returnMap.put("lobId", lobId);
			ParaMap sysBlobDtlMap = new ParaMap();
			sysBlobDtlMap.put("id", "");
			sysBlobDtlMap.put("lob_id", lobId);
			ParaMap saveMap = new ParaMap();
			saveMap = dao.updateData("sys_blob_dtl", "id", sysBlobDtlMap, null);
			if ("1".equals(saveMap.getString("state"))) {
				String dtlId = saveMap.getString("id");
				returnMap.put("dtlId", dtlId);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						content.getBytes("UTF-8"));
				this.updateBlobContent("sys_blob_dtl", "id", dtlId, "content",
						inputStream);
				inputStream.close();
			} else {
				returnMap.put("message", "保存附件子表出错");
				return returnMap;
			}

		} else {
			returnMap.put("message", "保存附件主表出错");
			return returnMap;
		}
		returnMap.put("message", "保存成功");
		return returnMap;
	}

	public ParaMap getSysLobCa(String refId) throws Exception {
		ParaMap returnMap = new ParaMap();
		List byteList = new ArrayList();
		DataSetDao dao = new DataSetDao();
		if (StrUtils.isNotNull(refId)) {
			String ids[] = refId.split(",");
			StringBuffer customCondition = new StringBuffer("");
			if (ids != null && ids.length > 0) {
				customCondition.append(" and sl.ref_id in('");
				for (int i = 0; i < ids.length; i++) {
					if (i == ids.length - 1) {
						customCondition.append(ids[i] + "')");
					} else {
						customCondition.append(ids[i] + "','");
					}
				}
			}

			ParaMap sqlParams = new ParaMap();
			ParaMap map = new ParaMap();
			map.put("CUSTOM_CONDITION", customCondition.toString());
			sqlParams.put(dao.SQL_CUSTOM_CONDITIONS, map);
			sqlParams.put("moduleId", "B1D67A58EF5F4A50BBB1F1B30E7DEF10");
			sqlParams.put("dataSetNo", "query_sys_lob_ca");
			ParaMap sysLob = dao.queryData(sqlParams);
			List list = sysLob.getList("rs");
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String lobId = (String) sysLob.getRecordValue(i, "id");
					if (lobId != null && !"".equals(lobId)
							&& !"null".equals(lobId)) {
						BufferedInputStream bs = null;
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] bt = new byte[1024];
						int len = 0;
						bs = this.getBlobContent("sys_blob_dtl", "id", lobId,
								"content");
						if (bs != null) {
							while ((len = bs.read(bt)) > 0) {
								out.write(bt, 0, len);
							}
							bs.close();
						}
						byteList.add(out.toByteArray());
						out.close();
					}
				}
			}
		}
		returnMap.put("list", byteList);
		return returnMap;
	}

	public String getSysLobCa(List list) throws Exception {
		JSONArray jsonArray = new JSONArray();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String lobId = (String) list.get(i);
				if (lobId != null && !"".equals(lobId) && !"null".equals(lobId)) {
					BufferedInputStream bs = null;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] bt = new byte[1024];
					int len = 0;
					bs = this.getBlobContent("sys_blob_dtl", "id", lobId,
							"content");
					if (bs != null) {
						while ((len = bs.read(bt)) > 0) {
							out.write(bt, 0, len);
						}
						bs.close();
					}
					String str = StreamUtils.byteToString(out.toByteArray());
					jsonArray.add(str);
					out.close();
				}
			}
		}
		return jsonArray.toString();
	}

	

}

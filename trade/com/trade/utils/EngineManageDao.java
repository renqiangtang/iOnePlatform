package com.trade.utils;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.base.dao.BaseDao;
import com.base.ds.DataSourceManager;
import com.base.utils.IDGenerator;

public class EngineManageDao extends BaseDao {
	static Logger log = Logger.getLogger(EngineManageDao.class);

	/**
	 * 获取最近启动的机器
	 * 
	 * @param engineType
	 * @return
	 */
	public static String ip(String engineType) {
		try {
			BaseDao baseDao = new BaseDao();
			String sql = "select engine_ip from wf_engine where engine_type = '"
					+ engineType + "'";
			PreparedStatement pstm = baseDao.getCon().prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next())
				return rs.getString(1);
		} catch (Exception ex) {

		}
		return "";
	}

	/**
	 * 更新时间
	 * 
	 * @param engineType
	 * @throws Exception
	 */
	public static void updateTimeAll() {
		try {
			BaseDao baseDao = new BaseDao();
			PreparedStatement pstm = null;
			String sql = " update wf_engine set engine_time = to_timestamp('1970-01-01 01:01:01.1','yyyy-mm-dd hh24:mi:ss.ff') ";
			pstm = baseDao.getCon().prepareStatement(sql);
			pstm.executeUpdate();
			if (pstm != null) {
				pstm.close();
			}
			DataSourceManager.commit();
		} catch (Exception ex) {
			DataSourceManager.rollback();
			log.debug(ex);
		}
	}

	/**
	 * 更新时间
	 * 
	 * @param engineType
	 * @throws Exception
	 */
	public static void updateTime(String engineType) {
		try {
			BaseDao baseDao = new BaseDao();
			PreparedStatement pstm = null;
			String ip = Engine.getWebId();
			String sql = " update wf_engine set engine_time = systimestamp , engine_ip = '"
					+ ip + "'  where engine_type = '" + engineType + "'";
			pstm = baseDao.getCon().prepareStatement(sql);
			pstm.executeUpdate();
			if (pstm != null) {
				pstm.close();
			}
			DataSourceManager.commit();
		} catch (Exception ex) {
			DataSourceManager.rollback();
			log.debug(ex);
		}
	}

	public static long systimestamp() {
		try {
			BaseDao baseDao = new BaseDao();
			String sql = "select systimestamp from dual";
			PreparedStatement pstm = baseDao.getCon().prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			if (rs.next())
				return rs.getTimestamp(1).getTime();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static boolean engineStarted(String engineType) {
		BaseDao baseDao = new BaseDao();
		boolean result = false;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "  select engine_time  from wf_engine where engine_type = '"
					+ engineType + "'";
			pstm = baseDao.getCon().prepareStatement(sql);
			rs = pstm.executeQuery();
			if (rs.next()) {
				long startedTime = rs.getTimestamp(1).getTime();
				long now = systimestamp();
				long diff_time = Math.abs(now - startedTime);
				log.debug("相差时间:" + diff_time);
				if (diff_time >= 2000) {
					result = false;
				} else {
					result = true;
				}
			} else {
				String ip = InetAddress.getLocalHost().getHostName();
				sql = " insert into wf_engine(id,engine_type,engine_ip) values('"
						+ IDGenerator.newGUID()
						+ "','"
						+ engineType
						+ "','"
						+ ip + "')";
				pstm = baseDao.getCon().prepareStatement(sql);
				pstm.executeUpdate();
				result = true;
			}
			if (rs != null) {
				rs.close();
			}
			if (pstm != null) {
				pstm.close();
			}
			DataSourceManager.commit();
		} catch (Exception ex) {
			DataSourceManager.rollback();
			log.debug(ex);
		}
		return result;
	}

}
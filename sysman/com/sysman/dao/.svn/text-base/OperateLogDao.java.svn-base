package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 操作日志管理Dao
 * huafc
 * 2012-05-31
 */
public class OperateLogDao extends BaseDao {	
	public static final int LOG_LEVEL_0 = 0;//提示
	public static final int LOG_LEVEL_1 = 1;//1调试
	public static final int LOG_LEVEL_2 = 2;//2警告
	public static final int LOG_LEVEL_3 = 3;//3错误
	
	private static OperateLogDao operateLogDao = new OperateLogDao();
	
	/**
	 * 记录日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  logLevel: 日志级别。0提示，1调试，2警告，3错误。请取常量值，默认为0提示<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean log(ParaMap logData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		ParaMap operateLogData = new ParaMap();
		operateLogData.put("id", null);
		operateLogData.put("log_type", 0);
		int intLogLevel = 0;
		if (logData.containsKey("logLevel")) {
			String logLevel = logData.getString("logLevel");
			if (StrUtils.isInteger(logLevel)) {
				intLogLevel = Integer.parseInt(logLevel);
				if (intLogLevel < 0 && intLogLevel > 3)
					intLogLevel = 0;
			}
		}
		operateLogData.put("log_level", intLogLevel);
		operateLogData.put("ref_table_name", logData.getString("refTableName"));
		operateLogData.put("ref_field_name", logData.getString("refFieldName"));
		operateLogData.put("ref_field_value", logData.getString("refFieldValue"));
		operateLogData.put("operate", logData.getString("operate"));
		if (logData.containsKey("operateDate")) {
			format.put("operate_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
			operateLogData.put("operate_date", logData.getString("operateDate"));
		}
		operateLogData.put("operate_log", logData.getString("log"));
		operateLogData.put("host", logData.getString("logLevel"));
		operateLogData.put("remark", logData.getString("remark"));
		ParaMap result = dataSetDao.updateData("sys_operate_log", "id", operateLogData, format);
		return result.getInt("state") == 1;
	}
	
	/**
	 * 记录日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param logLevel: 日志级别。0提示，1调试，2警告，3错误。请取常量值，默认为0提示<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean log(String log, String operate, int logLevel,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		ParaMap logData = new ParaMap();
		logData.put("logLevel", logLevel);
		logData.put("operate", operate);
		logData.put("log", log);
		logData.put("refTableName", refTableName);
		logData.put("refFieldName", refFieldName);
		logData.put("refFieldValue", refFieldValue);
		return log(logData);
	}
	
	/**
	 * 记录信息日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean info(ParaMap logData) throws Exception {
		logData.put("logLevel", LOG_LEVEL_0);
		return log(logData);
	}
	
	/**
	 * 记录信息日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean info(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return log(log, operate, LOG_LEVEL_0, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录调试日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean debug(ParaMap logData) throws Exception {
		logData.put("logLevel", LOG_LEVEL_1);
		return log(logData);
	}
	
	/**
	 * 记录调试日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean debug(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return log(log, operate, LOG_LEVEL_1, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录警告日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean warning(ParaMap logData) throws Exception {
		logData.put("logLevel", LOG_LEVEL_2);
		return log(logData);
	}
	
	/**
	 * 记录警告日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean warning(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return log(log, operate, LOG_LEVEL_2, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录错误日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean error(ParaMap logData) throws Exception {
		logData.put("logLevel", LOG_LEVEL_3);
		return log(logData);
	}
	
	/**
	 * 记录错误日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public boolean error(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return log(log, operate, LOG_LEVEL_3, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  logLevel: 日志级别。0提示，1调试，2警告，3错误。请取常量值，默认为0提示<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeLog(ParaMap logData) throws Exception {
		return operateLogDao.log(logData);
	}
	
	/**
	 * 记录日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param logLevel: 日志级别。0提示，1调试，2警告，3错误。请取常量值，默认为0提示<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeLog(String log, String operate, int logLevel,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return operateLogDao.log(log, operate, logLevel, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录信息日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeInfo(ParaMap logData) throws Exception {
		return operateLogDao.info(logData);
	}
	
	/**
	 * 记录信息日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeInfo(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return operateLogDao.info(log, operate, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录调试日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeDebug(ParaMap logData) throws Exception {
		return operateLogDao.debug(logData);
	}
	
	/**
	 * 记录调试日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeDebug(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return operateLogDao.debug(log, operate, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录警告日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeWarning(ParaMap logData) throws Exception {
		return operateLogDao.warning(logData);
	}
	
	/**
	 * 记录警告日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeWarning(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return operateLogDao.warning(log, operate, refTableName, refFieldName, refFieldValue);
	}
	
	/**
	 * 记录错误日志
	 * @param logData 日志内容项<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operate: 操作说明<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  log: 日志内容。不能为空，否则直接返回false<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  host: 客户端地址<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldName: 写入日志关联表的关键字段名<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  refFieldValue: 写入日志关联表的关键字段值<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  operateDate: 日志日期，不建议写入，由系统自动记录<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;  remark: 备注<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeError(ParaMap logData) throws Exception {
		return operateLogDao.error(logData);
	}
	
	/**
	 * 记录错误日志
	 * @param log: 日志内容。不能为空，否则直接返回false<br/>
	 * @param operate: 操作说明<br/>
	 * @param refTableName: 写入日志关联的表名，建议写入。便于调试查找错误<br/>
	 * @param refFieldName: 写入日志关联表的关键字段名<br/>
	 * @param refFieldValue: 写入日志关联表的关键字段值<br/>
	 * @return 日志记录成功返回true
	 * @throws Exception
	 */
	public static boolean writeError(String log, String operate,
			String refTableName, String refFieldName, String refFieldValue) throws Exception {
		return operateLogDao.error(log, operate, refTableName, refFieldName, refFieldValue);
	}
}

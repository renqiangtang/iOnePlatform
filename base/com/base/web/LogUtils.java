package com.base.web;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;

import com.base.utils.DateUtils;

public class LogUtils {
	protected static Logger log = Logger.getLogger(LogUtils.class);

	public static void getLogFile() {
		DailyRollingFileAppender appender = (DailyRollingFileAppender) log
				.getRootLogger().getAppender("file");
		File file = new File(appender.getFile());
		SimpleDateFormat df = new SimpleDateFormat(appender.getDatePattern());
		String d = df.format(DateUtils.now());
		log.debug("日志文件:" + file.getAbsolutePath() + " " + d);
	}

}

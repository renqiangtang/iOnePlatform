package com.base.web;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.base.utils.DateUtils;
import com.sysman.dao.ExchangeDataDao;

public class TargetFeedbackTask extends TimerTask {
	Logger log = Logger.getLogger(this.getClass());

	public void run() {
		try {
			log.debug("");
			log.debug("");
			log.debug("标的交易信息返回旧系统开始....................................................."
					+ DateUtils.getStr(DateUtils.now()));

			log.debug("标的交易信息返回旧系统结束....................................................."
					+ DateUtils.getStr(DateUtils.now()));
			log.debug("");
			log.debug("");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
}

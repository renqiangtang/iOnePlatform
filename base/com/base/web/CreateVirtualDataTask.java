package com.base.web;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.base.utils.DateUtils;

public class CreateVirtualDataTask extends TimerTask{
	Logger log = Logger.getLogger(this.getClass());
	public void run() {
		try {
			log.debug("");
			log.debug("");
			log.debug("生成虚拟数据开始开始....................................................."+DateUtils.getStr(DateUtils.now()));
			
			try {
				com.tradeland.dao.TradeDao landDao = new com.tradeland.dao.TradeDao();
				landDao.virtualTrade();
				com.trademine.dao.TradeDao mineDao = new com.trademine.dao.TradeDao();
				mineDao.virtualTrade();
				com.tradeplow.dao.TradeDao plowDao = new com.tradeplow.dao.TradeDao();
				plowDao.virtualTrade();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
			log.debug("生成虚拟数据结束....................................................."+DateUtils.getStr(DateUtils.now()));
			log.debug("");
			log.debug("");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
}

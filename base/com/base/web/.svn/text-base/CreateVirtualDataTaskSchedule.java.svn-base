package com.base.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.base.utils.DateUtils;
import com.base.utils.StrUtils;
import com.tradeland.dao.TradeDao;

public class CreateVirtualDataTaskSchedule {
	

	public static void start() {
		doSchedule();
	}
	
	public static void doSchedule(){
		try {
			com.tradeland.dao.TradeDao landDao = new com.tradeland.dao.TradeDao();
			landDao.virtualTradeDelete();
			landDao.virtualTrade();
			com.trademine.dao.TradeDao mineDao = new com.trademine.dao.TradeDao();
			mineDao.virtualTrade();
			com.tradeplow.dao.TradeDao plowDao = new com.tradeplow.dao.TradeDao();
			plowDao.virtualTrade();
		} catch (Exception e) {
			
		}
		Timer timer = new Timer();
		Date startDate = getTomorrowZeroClock();
		timer.schedule(new CreateVirtualDataTask() , startDate , 1000l*60*60*24);
	}
	
	
	public static Date getTomorrowZeroClock(){
        Calendar cal = Calendar.getInstance(); 
        cal.add(cal.DATE, 1); 
        cal.set(Calendar.HOUR_OF_DAY,   0); 
        cal.set(Calendar.MINUTE,   0); 
        cal.set(Calendar.SECOND,   0); 
        cal.set(Calendar.MILLISECOND,   0);
        return cal.getTime();
    }
}

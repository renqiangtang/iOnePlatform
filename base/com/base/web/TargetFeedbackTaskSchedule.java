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

public class TargetFeedbackTaskSchedule {
	
	//private static List<TargetFeedbackTask> targetFeedbackTaskList = Collections.synchronizedList(new ArrayList<TargetFeedbackTask>());
	
//	public static void add(TargetFeedbackTask service) {
//		targetFeedbackTaskList.add(service);
//	}
	
	public static void start() {
		//add(new TargetFeedbackTask());	
		doSchedule();
	}
	
	public static void doSchedule(){
		String flag = AppConfig.getPro("targetFeedBackFlag");
		if(flag!=null&&"1".equals(flag.trim())){
			String period = AppConfig.getPro("targetFeedBackPeriod");
			period = StrUtils.isNull(period)?"86400000":period;
			String time = AppConfig.getPro("targetFeedBackTime");
			time = StrUtils.isNull(time)?"23:00:00":time;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String thisDay = sdf.format(DateUtils.now());
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date taskTime = DateUtils.now();
			try{
				taskTime = sdf2.parse(thisDay+" "+time);
			}catch(Exception e){
				taskTime = getTomorrowZeroClock();//明天0点
			}
			
			Timer timer = new Timer();
			
			timer.schedule(new TargetFeedbackTask() , taskTime, Long.parseLong(period));
		}
		String virtualData = AppConfig.getPro("virtualData");
		System.out.println("virtualData===>"+virtualData);
		if(virtualData!=null&&"1".equals(virtualData.trim())){
			Date nextHourTime = new Date((DateUtils.now()).getTime()+1000l*60*60);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nextStr = sdf2.format(nextHourTime);
			nextStr = nextStr.substring(0, nextStr.indexOf(":"))+":00:00";
			Date taskTime = nextHourTime;
			try{
				taskTime=sdf2.parse(nextStr);
			}catch(Exception e){
				taskTime = getTomorrowZeroClock();//明天0点
			}
			System.out.println("创建虚拟数据定时器时间===>"+sdf2.format(taskTime));
			Timer timer = new Timer();
			
			timer.schedule(new CreateVirtualDataTask() , taskTime, 1000l*60*60*24);
			
		}
		
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

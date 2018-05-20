package com.base.task;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.base.ds.DataSourceManager;

public abstract class MyTimerTask extends TimerTask {
	private static final Logger log = Logger.getLogger(MyTimerTask.class);
	public static final String Type_One = "One";
	public static final String Type_Many = "Many";
	private String id;
	private String info = "";
	private String type = "";
	private Date startTime;

	/**
	 * id=业务Id
	 * 
	 * @param id
	 */
	public MyTimerTask(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public abstract void execute() throws Exception;

	public void run() {
		Thread thread = new Thread() {
			public void run() {
				try {
					if (type.equals(Type_One))
						TaskManager.remove(id);
					log.debug(info + "定时器[" + id + "]开始执行...");
					execute();
					DataSourceManager.commit();
				} catch (Exception ex) {
					ex.printStackTrace();
					DataSourceManager.rollback();
				} finally {
					// DataSourceManager.printState();
				}
			}
		};
		thread.start();
	}
}

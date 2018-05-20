package com.base.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.utils.DateUtils;

public class TaskManager {
	static Logger log = Logger.getLogger(EngineTimerTask.class);
	private static Timer timer = new Timer();
	public static Map<String, MyTimerTask> oneTimeTaskMap = new HashMap<String, MyTimerTask>();
	public static Map<String, MyTimerTask> manyTimeTaskMap = new HashMap<String, MyTimerTask>();

	private static void addOneTimeTask(MyTimerTask task) throws Exception {
		String id = task.getId();
		if (StringUtils.isEmpty(id)) {
			throw new Exception("一次性定时任务Id不能为空！");
		}
		if (oneTimeTaskMap.containsKey(id)) {
			MyTimerTask oldTask = oneTimeTaskMap.get(id);
			remove(id);
			log.info("一次性定时任务重复[id]:" + id + " 加入时间:"
					+ DateUtils.getStr(oldTask.getStartTime()) + "已经移除...");
		}
		task.setType(MyTimerTask.Type_One);
		oneTimeTaskMap.put(id, task);
	}

	private static void addManyTimeTask(MyTimerTask task) throws Exception {
		String id = task.getId();
		if (StringUtils.isEmpty(id))
			throw new Exception("固定定时任务Id不能为空！");
		if (manyTimeTaskMap.containsKey(id))
			throw new Exception("固定定时任务Id重复..." + id);
		task.setType(MyTimerTask.Type_Many);
		manyTimeTaskMap.put(id, task);
	}

	public static void add(MyTimerTask task, long delay) throws Exception {
		task.setStartTime(new Date(DateUtils.nowTime() + delay));
		addOneTimeTask(task);
		timer.schedule(task, delay);
	}

	public static void add(MyTimerTask task, Date time) throws Exception {
		task.setStartTime(time);
		addOneTimeTask(task);
		timer.schedule(task, time);
	}

	public static void add(MyTimerTask task, long delay, long period)
			throws Exception {
		task.setStartTime(new Date(DateUtils.nowTime() + delay));
		addOneTimeTask(task);
		timer.schedule(task, delay, period);
	}

	public static void add(MyTimerTask task, Date firstTime, long period)
			throws Exception {
		addOneTimeTask(task);
		timer.schedule(task, firstTime, period);
	}

	public static void addAtFixedRate(MyTimerTask task, long delay, long period)
			throws Exception {
		task.setStartTime(new Date(DateUtils.nowTime() + delay));
		addManyTimeTask(task);
		timer.scheduleAtFixedRate(task, delay, period);
	}

	public static void addAtFixedRate(MyTimerTask task, Date firstTime,
			long period) throws Exception {
		addManyTimeTask(task);
		timer.scheduleAtFixedRate(task, firstTime, period);
	}

	public static void remove(String id) {
		MyTimerTask task = oneTimeTaskMap.get(id);
		if (task == null)
			return;
		task.cancel();
		oneTimeTaskMap.remove(id);
		manyTimeTaskMap.remove(id);
		// 从此计时器的任务队列中移除所有已取消的任务
		timer.purge();
	}

	public static void remove(MyTimerTask task) {
		remove(task.getId());
	}

	// 终止此定时器，丢弃所有当前已安排的任务
	public static void stop() throws Exception {
		timer.cancel();
	}

	public static void clear() {
		Object[] arrs = oneTimeTaskMap.values().toArray();
		for (int i = 0; i < arrs.length; i++) {
			if (arrs[i] instanceof MyTimerTask) {
				MyTimerTask task = (MyTimerTask) arrs[i];
				if (task.getInfo().indexOf("交易") >= 0) {
					log.info("清除任务" + DateUtils.getStr(task.getStartTime()));
					remove(task.getId());
				}
			}
		}
	}

	// 重新启动定时器
	public static void start() throws Exception {
		timer = new Timer();
		init();
	}

	public static String allinfo() throws Exception {
		StringBuffer sb = new StringBuffer();
		Iterator it = oneTimeTaskMap.keySet().iterator();
		while (it.hasNext()) {
			String processId = (String) it.next();
			sb.append(memInfo(processId) + "\n");
		}

		return sb.toString();
	}

	public static String memInfo(String processId) {
		String info = "引擎找不到对应的信息";
		MyTimerTask task = oneTimeTaskMap.get(processId);
		if (task != null) {
			Date dbTime = DateUtils.restoreDbTime(task.getStartTime());
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSS");
			info = task.getInfo() + "[" + task.getId() + "]执行时间:"
					+ sdf.format(dbTime);
		}
		return info;
	}

	public static void init() throws Exception {

	}

}

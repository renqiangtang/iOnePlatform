package com.sysman.service;

import java.util.Properties;

import com.base.ds.DataSourceManager;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.web.RouteFilter;

public class SysInfoService extends BaseService {
	public ParaMap getInfo(ParaMap paraMap) {
		ParaMap map = new ParaMap();
		Properties props = System.getProperties();
		map.put("startDate", DateUtils.getStr(RouteFilter.startDate));
		map.put("runDate", DateUtils.sub(RouteFilter.startDate, DateUtils.now()));
		try {
			//
			// JVM
			Runtime r = Runtime.getRuntime();
			map.put("jvmVersion",
					System.getProperties().getProperty("java.version"));
			map.put("jvmProcessor", r.availableProcessors());
			map.put("jvmMaxMemory",
					DateUtils.getInt(r.maxMemory() / 1024 / 1024) + "M");
			map.put("jvmTotalMemory",
					DateUtils.getInt(r.totalMemory() / 1024 / 1024) + "M");
			map.put("jvmActMemory",
					DateUtils.getInt((r.maxMemory() - r.totalMemory() + r
							.freeMemory()) / 1024 / 1024) + "M");
			// DataSource
			map.put("numberActive", DataSourceManager.numberActive());
			map.put("numberIdle", DataSourceManager.numberIdle());
			//
			// map.put("taskRunInfo",TaskManager.info);
			//
			// BaseDao dao = new BaseDao();
			// System.out.println(dao.getCon(paraMap.getRequest()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}

	public static void main(String[] args) throws Exception {
		Properties prop = new Properties(System.getProperties());
		prop.list(System.out);
	}
}

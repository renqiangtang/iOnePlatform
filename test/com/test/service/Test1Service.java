package com.test.service;

import java.net.URLDecoder;

import com.base.service.BaseService;
import com.base.utils.CharsetUtils;
import com.base.utils.ParaMap;
import com.base.web.RouteFilter;

public class Test1Service extends BaseService {
	/**
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap t1(ParaMap in) throws Exception {
		return this.readJson("Test1.json");
	}

	public static void main(String[] args) throws Exception {
		Test1Service obj = new Test1Service();
		ParaMap outMap = obj.t1(null);
		String json = outMap.getString(RouteFilter.jsonStr);
		System.out.println(json);
	}

	public ParaMap t2(ParaMap in) throws Exception {
		String xml = in.getString("xml");
		xml=CharsetUtils.getString(xml);
		System.out.println(xml);
		ParaMap out = new ParaMap();
		out.put("newxml", xml);
		return out;
	}

}

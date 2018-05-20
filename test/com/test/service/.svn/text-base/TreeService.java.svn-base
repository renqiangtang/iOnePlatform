package com.test.service;

import com.base.service.BaseService;
import com.base.utils.ParaMap;

public class TreeService extends BaseService {

	public ParaMap getData(ParaMap in) throws Exception {
		String parentId = in.getString("parentId");
		if ("0003".equals(parentId))
			return new ParaMap();
		else
			return readJson("Tree.json");
	}
}

package com.sysman.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

/**
 * 模块资源DAO
 * @author SAMONHUA
 * 2012-05-07
 */
public class ResourceDao extends BaseDao {
	//获取模块字符串资源
	public ParaMap getModuleResourceData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		if (StrUtils.isNull(moduleId)) {
			String moduleNo = inMap.getString("moduleNo");
			if (StrUtils.isNull(moduleNo)) {
				return null;
			}
			keyData.put("no", moduleNo);
			ParaMap moduleData = dataSetDao.querySimpleData("sys_module", keyData);
			if (moduleData.getInt("state") != 1 || moduleData.getInt("totalRowCount") == 0)
				return null;
			moduleId = moduleData.getString("id");
		}
		keyData.put("module_id", moduleId);
		ParaMap resourceData = dataSetDao.querySimpleData("sys_resource", keyData);		
		ParaMap result = new ParaMap();
		for(int i = 0;i < resourceData.getRecords().size(); i++) {
			result.put(resourceData.getRecordValue(i, "no"), resourceData.getRecordValue(i, "content"));
		}
		return result;
	}
}

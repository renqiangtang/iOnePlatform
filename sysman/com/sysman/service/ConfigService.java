package com.sysman.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sysman.dao.ConfigDao;
import com.trademan.dao.BidderDao;

import org.apache.commons.lang.StringUtils;

/**
 * 系统设置Service
 * huafc
 * 2012-05-23
 */
public class ConfigService extends BaseService {
	//返回当前系统支持的业务范围
	public ParaMap getBusinessType(ParaMap inMap) throws Exception {
		int intReturnType = inMap.containsKey("returnType") ? Integer.parseInt(inMap.getString("returnType")) : 0;
		int intUserType = inMap.containsKey("userType") ? Integer.parseInt(inMap.getString("userType")) : 1;
		String strRefId = inMap.getString("refId");
		String strOrganId = inMap.containsKey("organId") ? inMap.getString("organId") : null;
//		if (intUserType == 1) {
//			//查询竞买人的白、黑名单单位，竞买人仅能查询指定名单内的发布单位的标的以及业务类别
//			int intBidderOrganFilter = 0;
//			String strBidderOrganFilter = AppConfig.getPro("bidderOrganFilter");
//			if (StrUtils.isNotNull(strBidderOrganFilter)) {
//				intBidderOrganFilter = Integer.parseInt(strBidderOrganFilter);
//				if (intBidderOrganFilter < 0 || intBidderOrganFilter > 2)
//					intBidderOrganFilter = 0;
//			}
//			if (intBidderOrganFilter == 0)
//				strOrganId = null;
//			else {
//				int intOrganRelType = intBidderOrganFilter == 1 ? 1 : 11;
//				BidderDao bidderDao = new BidderDao();
//				strOrganId = bidderDao.getBidderRelOrganIds(strRefId, intOrganRelType);
//				//如果设置需要白、黑名单但竞买人没有设置相应的名单，则返回所有业务类别。而能查询多少数据处控制不能查询到所有的数据
//				//if (StrUtils.isNull(strOrganId))
//				//	strOrganId = "__BIDDER_NONE_REL_ORGAN__";
//			}
//		}
		ParaMap result = new ParaMap();
		result.put("state", 1);
		result.put("businessType", ConfigDao.getBusinessType(intReturnType, strOrganId));
		return result;
	}
	
	public ParaMap getTransTypes(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		result.put("state", 1);
		result.put("transType", ConfigDao.getTransTypes(inMap.getString("businessType")));
		return result;
	}
	
	public ParaMap getGeneralParams(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		ParaMap result = configDao.getGeneralParams();
		return result;
	}
	
	public ParaMap getTransParams(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		ParaMap businessType = getBusinessType(inMap);
		ParaMap result = configDao.getTransParams();
		result.put("businessType", businessType.getString("businessType"));
		return result;
	}
	
	public ParaMap getCAParams(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		ParaMap supportCAsData = configDao.getSupportCAsData();
		ParaMap result = configDao.getCAParams();
		result.put("supportCAs", supportCAsData);
		return result;
	}
	
	public ParaMap updateConfigParams(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		ParaMap result = configDao.updateConfigParams(inMap);
		return result;
	}
	
	/**
	 * 获取交易规则配置引用数据库中的内容，如moduleId
	 * @param transRuleConfig 交易规则配置
	 * @return
	 */
	private ParaMap getTransRuleConfigRefData(ParaMap transRuleConfig) throws Exception {
		if (transRuleConfig == null)
			return null;
		//获取模块
		List<String> moduleIds = new ArrayList<String>();
		ParaMap stepsDict = (ParaMap) transRuleConfig.getDataByPath("dictionaries.steps");
		if (stepsDict != null && stepsDict.size() > 0) {
			Iterator it = stepsDict.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				ParaMap value = (ParaMap) stepsDict.get(key);
				if (value != null) {
					Iterator itValue = value.keySet().iterator();
					while (itValue.hasNext()) {
						String valueKey = itValue.next().toString();
						if (StrUtils.indexOfIgnoreCase(valueKey, "module_id") >= 0) {
							String moduleId = value.getString(valueKey);
							if (StrUtils.isNotNull(moduleId) && moduleIds.indexOf(moduleId) == -1) {
								moduleIds.add(moduleId);
							}
						}
					}
				}
			}
		}
		ParaMap businessTypesMap = (ParaMap) transRuleConfig.getDataByPath("businessTypes");
		if (businessTypesMap != null && businessTypesMap.size() > 0) {
			Iterator it = businessTypesMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				ParaMap value = (ParaMap) businessTypesMap.get(key);
				if (value != null) {
					Iterator itValue = value.keySet().iterator();
					while (itValue.hasNext()) {
						String valueKey = itValue.next().toString();
						if (StrUtils.indexOfIgnoreCase(valueKey, "module_id") >= 0) {
							String moduleId = value.getString(valueKey);
							if (StrUtils.isNotNull(moduleId) && moduleIds.indexOf(moduleId) == -1) {
								moduleIds.add(moduleId);
							}
						}
					}
				}
			}
		}
		if (moduleIds.size() > 0) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", moduleIds);
			ParaMap data = dataSetDao.querySimpleData("sys_module", keyData);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				ParaMap modules = new ParaMap();
				for(int i = 0; i < data.getSize(); i ++) {
					modules.put(String.valueOf(data.getRecordValue(i, "id")), String.valueOf(data.getRecordValue(i, "name")));
				}
				ParaMap refData = new ParaMap();
				refData.put("modules", modules);
				return refData;
			} else
				return null;
		} else
			return null;
	}
	public ParaMap getTransRuleConfigPlow(ParaMap inMap) throws Exception{
		ParaMap result = new ParaMap();
		ConfigDao configDao = new ConfigDao();
		List tradeRuleXMLConfig = (List) configDao.getTradeRuleXMLConfigPlow(true);
		ParaMap transRuleConfig = configDao.getTransRuleConfig();
		ParaMap configRefData = getTransRuleConfigRefData(transRuleConfig);
		result.put("state", 1);
		result.put("tradeRuleXML", tradeRuleXMLConfig);
		if (configRefData != null && configRefData.size() > 0)
			result.putAll(configRefData);
		if (inMap == null || inMap.size() == 0 || !inMap.containsKey("configPath") || StrUtils.isNull(inMap.getString("configPath"))) {
			result.put("transRule", transRuleConfig);
		} else {
			try {
				String configPath = inMap.getString("configPath");
				if (transRuleConfig == null)
					result.put("transRule", transRuleConfig);
				else
					result.put("transRule", transRuleConfig.getDataByPath(configPath));
			} catch(Exception e) {
				result.clear();
				result.put("state", 0);
				result.put("message", e.getMessage());
				throw e;
			}
		}
		return result;
	}
	public ParaMap getTransRuleConfig(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		ConfigDao configDao = new ConfigDao();
		List tradeRuleXMLConfig = (List) configDao.getTradeRuleXMLConfig(true);
		ParaMap transRuleConfig = configDao.getTransRuleConfig();
		ParaMap configRefData = getTransRuleConfigRefData(transRuleConfig);
		result.put("state", 1);
		result.put("tradeRuleXML", tradeRuleXMLConfig);
		if (configRefData != null && configRefData.size() > 0)
			result.putAll(configRefData);
		if (inMap == null || inMap.size() == 0 || !inMap.containsKey("configPath") || StrUtils.isNull(inMap.getString("configPath"))) {
			result.put("transRule", transRuleConfig);
		} else {
			try {
				String configPath = inMap.getString("configPath");
				if (transRuleConfig == null)
					result.put("transRule", transRuleConfig);
				else
					result.put("transRule", transRuleConfig.getDataByPath(configPath));
			} catch(Exception e) {
				result.clear();
				result.put("state", 0);
				result.put("message", e.getMessage());
				throw e;
			}
		}
		return result;
	}
	
	public ParaMap getBusinessTypeConfig(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		//return configDao.getBusinessTypeConfig(inMap.getString("businessType"), inMap.getString("subItemName"));
		return null;
	}
	
	public ParaMap getTradeRuleXMLConfig(ParaMap inMap) throws Exception {
		ConfigDao configDao = new ConfigDao();
		ParaMap result = new ParaMap();
		result.put("data", configDao.getTradeRuleXMLConfig(inMap != null && inMap.containsKey("returnList") && inMap.getString("returnList").equals("1")));
		return result;
	}
}

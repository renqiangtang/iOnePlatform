package com.sysman.dao;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.ParamsDao;

/**
 * 系统设置Dao
 * huafc
 * 2012-05-23
 */
public class ConfigDao extends BaseDao {
	private static String configRootParamsId = null;
	private static String configGeneralParamsId = null;
	private static String configTransParamsId = null;
	private static String configBusinessTypeParamsId = null;
	private static String configCAParamsId = null;
	private static final String configRootParamsNo = "transConfig";
	private static final String configGeneralParamsNo = "transGeneralConfig";
	private static final String configTransParamsNo = "transTransConfig";
	private static final String configBusinessTypeParamsNo = "transBusinessType";
	private static final String configCAParamsNo = "transCAConfig";
	private static final String newRecordIdPrefix = "_NEW_";
	public final static String root_kz = "mineralProducts";
	//所有业务类别简单格式：0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌;4=探矿权出让;5=探矿权转让;6=采矿权出让;7=采矿权转让
	private static String ALL_BUSINESS_TYPE = null;
	//此MAP中的任何内容不能直接返回对象给调用方，避免数据不一致
	//格式(json)：{0:{businessType:0, businessName:'土地出让', isValid:'1', moduleId:'', moduleParams:'', url:'', shortName:'宗地', noLabel:'宗地号', nameLabel:''}, ...}
	private static ParaMap ALL_BUSINESS_TYPE_FULL = null;
	//当前系统支持的业务类别，格式：0;1;2;3
	private static ParaMap SUPPORT_BUSINESS_TYPE = null;
	//保存交易规则json字符串，当数据有更新时清空以强制重新加载
	private static String TRANS_RULE_CONFIG = null; 
	
	private void initConfigBaseData() throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParamsDao paramsDao = new ParamsDao();
		if (configRootParamsId == null) {
			ParaMap paramData = paramsDao.getParamsDataByNo(configRootParamsNo);
			if (paramData.getInt("state") == 1 && paramData.getInt("totalRowCount") > 0) {
				configRootParamsId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData = paramsDao.updateParamsData("parent_id;no", "", configRootParamsNo, "系统设置", ParamsDao.userParamsRootId,
						null, null, null, "系统设置模块配置项", null, null);
				if (paramData.getInt("state") == 1) {
					configRootParamsId = paramData.getString("id");
				}
			}
		}
		
		if (configGeneralParamsId == null) {
			ParaMap paramData = paramsDao.getParamsDataByNo(configGeneralParamsNo);
			if (paramData.getInt("state") == 1 && paramData.getInt("totalRowCount") > 0) {
				configGeneralParamsId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData = paramsDao.updateParamsData("parent_id;no", "", configGeneralParamsNo, "常规设置", configRootParamsId,
						null, null, null, "系统设置模块常规设置项", null, null);
				if (paramData.getInt("state") == 1) {
					configGeneralParamsId = paramData.getString("id");
				}
			}
		}
		
		if (configTransParamsId == null) {
			ParaMap paramData = paramsDao.getParamsDataByNo(configTransParamsNo);
			if (paramData.getInt("state") == 1 && paramData.getInt("totalRowCount") > 0) {
				configTransParamsId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData = paramsDao.updateParamsData("parent_id;no", "", configTransParamsNo, "交易设置", configRootParamsId,
						null, null, null, "系统设置模块交易设置项", null, null);
				if (paramData.getInt("state") == 1) {
					configTransParamsId = paramData.getString("id");
				}
			}
		}
		
		if (configBusinessTypeParamsId == null) {
			ParaMap paramData = paramsDao.getParamsDataByNo(configBusinessTypeParamsNo);
			if (paramData.getInt("state") == 1 && paramData.getInt("totalRowCount") > 0) {
				configBusinessTypeParamsId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData = paramsDao.updateParamsData("parent_id;no", "", configBusinessTypeParamsNo, "业务范围设置", configTransParamsId,
						null, null, null, null, null, null);
				if (paramData.getInt("state") == 1) {
					configBusinessTypeParamsId = paramData.getString("id");
				}
			}
		}
		if (configCAParamsId == null) {
			ParaMap paramData = paramsDao.getParamsDataByNo(configCAParamsNo);
			if (paramData.getInt("state") == 1 && paramData.getInt("totalRowCount") > 0) {
				configCAParamsId = String.valueOf(paramData.getRecordValue(0, "id"));
			} else {
				paramData = paramsDao.updateParamsData("parent_id;no", "", configCAParamsNo, "CA数据证书设置", configRootParamsId,
						null, null, null, null, null, null);
				if (paramData.getInt("state") == 1) {
					configCAParamsId = paramData.getString("id");
				}
			}
		}
	}
	
	private static String parseSupportBusinessType(String organId) {
		if (SUPPORT_BUSINESS_TYPE == null || SUPPORT_BUSINESS_TYPE.size() == 0)
			return null;
		if (SUPPORT_BUSINESS_TYPE.containsKey(organId))
			return SUPPORT_BUSINESS_TYPE.getString(organId);
		else if (StrUtils.isNull(organId))
			return null;
		else {
			List<String> organIds = StrUtils.getSubStrs(organId, ";");
			List<String> supportBusinessTypes = new ArrayList();
			for(int i = 0; i < organIds.size(); i++) {
				if (SUPPORT_BUSINESS_TYPE.containsKey(organIds.get(i))) {
					List<String> businessTypeRelIds = StrUtils.getSubStrs(SUPPORT_BUSINESS_TYPE.getString(organIds.get(i)), ";");
					for(int j = 0; j < businessTypeRelIds.size(); j++) {
						if (supportBusinessTypes.indexOf(businessTypeRelIds.get(i)) == -1)
							supportBusinessTypes.add(businessTypeRelIds.get(i));
					}
				}
			}
			StringBuffer supportBusinessType = new StringBuffer("");
			for(int i = 0; i < supportBusinessTypes.size(); i++) {
				supportBusinessType.append(supportBusinessTypes.get(i) + ";");
			}
			if (supportBusinessType.length() > 0) {
				supportBusinessType.deleteCharAt(supportBusinessType.length() - 1);
			}
			return supportBusinessType.toString();
		}
	}

	/**
	 * 获取当前系统的业务类别范围（businessType）
	 * @param returnType 返回值类别
	 * @param organId 交易单位ID，可传null、单个单位ID和以半角分号分隔的多个单位ID。null则返回当前系统所有交易单位支持的所有业务类别，否则返回指定单位支持的业务类别
	 * @return 按returnType的值返回键值为businessType：<br/>
	 *   0仅返回当前系统支持的键: 0;1;2;3<br/>
	 *   1仅返回当前系统支持的键值: 0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌<br/>
	 *   2仅返回当前系统所有的键: 0;1;2;3;4;5;6;7<br/>
	 *   3仅返回当前系统所有的键值: 0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌;4=探矿权出让;5=探矿权转让;6=采矿权出让;7=采矿权转让<br/>
	 *   4返回当前系统所有和支持的键: 0;1;2;3;4;5;6;7|0;1;2;3<br/>
	 *   5返回当前系统所有的键值和支持的键: 0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌;4=探矿权出让;5=探矿权转让;6=采矿权出让;7=采矿权转让|0;1;2;3<br/>
	 *   6返回当前系统所有的业务类别完整信息(JSON格式): {0:{businessType:0, businessName:'土地出让', isValid:'1', moduleId:'', moduleParams:'', url:'', shortName:'宗地', noLabel:'宗地号', nameLabel:''}, ...}<br/>
	 * @throws Exception
	 */
	public static String getBusinessType(int returnType, String organId) throws Exception {
		//检查缓存变量是否为null，如果为空则查询。仅当数据被更新时会清空缓存变量
		if (ALL_BUSINESS_TYPE == null || ALL_BUSINESS_TYPE_FULL == null) {
			ParaMap transRuleConfigData = getTransRuleConfig();
			if (transRuleConfigData == null) {
				ALL_BUSINESS_TYPE = null; 
				ALL_BUSINESS_TYPE_FULL = null; 
				SUPPORT_BUSINESS_TYPE = null;
				return null;
			}
			ParaMap businessTypesData = (ParaMap) transRuleConfigData.getDataByPath("businessTypes");
			if (businessTypesData == null || businessTypesData.size() == 0) {
				ParaMap result = new ParaMap();
				ALL_BUSINESS_TYPE = null; 
				ALL_BUSINESS_TYPE_FULL = null; 
				SUPPORT_BUSINESS_TYPE = null;
				return null;
			}
			ParaMap dictionariesData = (ParaMap) transRuleConfigData.getDataByPath("dictionaries");
			ParaMap goodsTypesDictData = (ParaMap) dictionariesData.getDataByPath("goodsTypes");
			ParaMap businessTypesDictData = (ParaMap) dictionariesData.getDataByPath("businessTypes");
			ParaMap allBusinessTypeFull = new ParaMap();
			StringBuffer allBusinessType = new StringBuffer("");
			Iterator it = businessTypesData.keySet().iterator();
			while (it.hasNext()) {
				String businessType = it.next().toString();
				ParaMap businessTypeData = (ParaMap) businessTypesData.get(businessType);
				ParaMap goodsTypeDictData = goodsTypesDictData == null ? null : (ParaMap) goodsTypesDictData.get(businessTypeData.getString("goods_type_id"));
				ParaMap businessTypeDictData = businessTypesDictData == null ? null : (ParaMap) businessTypesDictData.get(businessTypeData.getString("business_type_id"));
				String businessTypeName = businessTypeData.getString("name");
				if (StrUtils.isNull(businessTypeName)) {
					businessTypeName = goodsTypeDictData.getString("name") + businessTypeDictData.getString("name");
				}
				allBusinessType.append(businessType + "=" + businessTypeName + ";");
				allBusinessTypeFull.put(businessType, getBusinessTypeConfig(businessType));
			}
			if (allBusinessType.length() > 0) {
				allBusinessType.deleteCharAt(allBusinessType.length() - 1);
			}
			ALL_BUSINESS_TYPE = allBusinessType.toString();
			ALL_BUSINESS_TYPE_FULL = allBusinessTypeFull;			
		}
		if (SUPPORT_BUSINESS_TYPE == null)
			SUPPORT_BUSINESS_TYPE = new ParaMap();
		List<String> organIds = StrUtils.getSubStrs(organId, ";", false);
		if (returnType == 0 || returnType == 1 || returnType == 4 || returnType == 5) {
			boolean blnReturnAll = false;
			List<String> queryOrganIds = new ArrayList(); 
			if (SUPPORT_BUSINESS_TYPE.size() > 0) {
				if (organIds != null && organIds.size() > 0) {
					for(int i = 0; i < organIds.size() - 1; i++)
						if (!SUPPORT_BUSINESS_TYPE.containsKey(organIds.get(i)))
							queryOrganIds.add(organIds.get(i));
				} else
					if (!SUPPORT_BUSINESS_TYPE.containsKey(null))
						blnReturnAll = true;
			} else
				blnReturnAll = true;
			if (blnReturnAll || queryOrganIds.size() > 0) {
				DataSetDao dataSetDao = new DataSetDao();
				ParaMap supportData = new ParaMap();
				if (blnReturnAll)
					supportData.put("sql", "select distinct business_type_rel_id from trans_organ_business_rel where is_valid = 1"
							+ " and exists(select * from trans_business_type_rel where id = trans_organ_business_rel.business_type_rel_id and is_valid = 1"
							+ "   and exists(select * from trans_business_type where id = trans_business_type_rel.business_type_id and is_valid = 1)"
							+ "   and exists(select * from trans_goods_type where id = trans_business_type_rel.goods_type_id and is_valid = 1))");
				else {
					StringBuffer organCondition = new StringBuffer("");
					for(int i = 0; i < queryOrganIds.size(); i++) {
						organCondition.append(":organ_id_" + i + ",");
						supportData.put("organ_id_" + i, queryOrganIds.get(i));
					}
					organCondition.deleteCharAt(organCondition.length() - 1);
					supportData.put("sql", "select id, organ_id, business_type_rel_id from trans_organ_business_rel where is_valid = 1"
							+ " and organ_id in (" + organCondition.toString() + ")"
							+ " and exists(select * from trans_business_type_rel where id = trans_organ_business_rel.business_type_rel_id and is_valid = 1"
							+ "   and exists(select * from trans_business_type where id = trans_business_type_rel.business_type_id and is_valid = 1)"
							+ "   and exists(select * from trans_goods_type where id = trans_business_type_rel.goods_type_id and is_valid = 1))");
				}
				supportData = dataSetDao.queryData(supportData);
				List<String> supportBusinessTypes = new ArrayList();
				if (blnReturnAll) {
					for(int i = 0; i < supportData.getSize(); i++) {
						String strBusinessTypeRelId = String.valueOf(supportData.getRecordValue(i, "business_type_rel_id"));
						if (StrUtils.isNotNull(strBusinessTypeRelId) && supportBusinessTypes.indexOf(strBusinessTypeRelId) == -1)
							supportBusinessTypes.add(strBusinessTypeRelId);
					}
					StringBuffer supportBusinessType = new StringBuffer("");
					for(int i = 0; i < supportBusinessTypes.size(); i++) {
						supportBusinessType.append(supportBusinessTypes.get(i) + ";");
					}
					if (supportBusinessType.length() > 0) {
						supportBusinessType.deleteCharAt(supportBusinessType.length() - 1);
					}
					SUPPORT_BUSINESS_TYPE.put(null, supportBusinessType.toString());
				} else {
					for(int j = 0; j < queryOrganIds.size(); j++) {
						StringBuffer supportBusinessType = new StringBuffer("");
						supportBusinessTypes.clear();
						for(int i = 0; i < supportData.getSize(); i++) {
							String strOrganId = String.valueOf(supportData.getRecordValue(i, "organ_id"));
							if (strOrganId.equals(queryOrganIds.get(j))) {
								String strBusinessTypeRelId = String.valueOf(supportData.getRecordValue(i, "business_type_rel_id"));
								if (StrUtils.isNotNull(strBusinessTypeRelId) && supportBusinessTypes.indexOf(strBusinessTypeRelId) == -1)
									supportBusinessTypes.add(strBusinessTypeRelId);
							}
						}
						for(int i = 0; i < supportBusinessTypes.size(); i++) {
							supportBusinessType.append(supportBusinessTypes.get(i) + ";");
						}
						if (supportBusinessType.length() > 0) {
							supportBusinessType.deleteCharAt(supportBusinessType.length() - 1);
						}
						SUPPORT_BUSINESS_TYPE.put(queryOrganIds.get(j), supportBusinessType.toString());
					}
				}
			}
		}
		int intReturnType = returnType >= 0 && returnType <= 6 ? returnType : 0;
		if (intReturnType == 0)
			return parseSupportBusinessType(organId);
		else if (intReturnType == 3)
			return ALL_BUSINESS_TYPE;
		else if (intReturnType == 5)
			return ALL_BUSINESS_TYPE + "|" + parseSupportBusinessType(organId);
		else  if (intReturnType == 6)
			return ALL_BUSINESS_TYPE_FULL.toString();
		else {
			//1仅返回当前系统支持的键值: 0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌
			//2仅返回当前系统所有的键: 0;1;2;3;4;5;6;7
			//4返回当前系统所有的和支持的键: 0;1;2;3;4;5;6;7|0;1;2;3
			StringBuffer allBusinessTypeNo = new StringBuffer();
			StringBuffer supportBusinessTypeNoLabel = new StringBuffer();
			Iterator it = ALL_BUSINESS_TYPE_FULL.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				ParaMap value = (ParaMap) ALL_BUSINESS_TYPE_FULL.get(key);
				allBusinessTypeNo.append(key + ";");
				if (value.getString("isValid").equals("1"))
					supportBusinessTypeNoLabel.append(key + "=" + value.getString("businessName") + ";");
			}
			if (allBusinessTypeNo.length() > 0)
				allBusinessTypeNo.deleteCharAt(allBusinessTypeNo.length() - 1);
			if (supportBusinessTypeNoLabel.length() > 0)
				supportBusinessTypeNoLabel.deleteCharAt(supportBusinessTypeNoLabel.length() - 1);
			if (intReturnType == 1) {
				return supportBusinessTypeNoLabel.toString();
			} else if (intReturnType == 2) {
				return allBusinessTypeNo.toString();
			} else if (intReturnType == 4) {
				return allBusinessTypeNo.toString() + "|" + parseSupportBusinessType(organId);
			} else
				return null;
		}
	}
	
	public static String getBusinessType(int returnType) throws Exception {
		return getBusinessType(returnType, null);
	}
	
	public ParaMap getSupportCAsData() throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap result = paramsDao.getParamsDataByParentNo("supportCAs");
		return result;
	}
	
	public ParaMap getGeneralParams() throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap result = paramsDao.getParamsDataByParentNo(configGeneralParamsNo);
		return result;
	}
	
	public ParaMap getTransParams() throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap result = paramsDao.getParamsDataByParentNo(configTransParamsNo);
		return result;
	}
	
	public ParaMap getCAParams() throws Exception {
		ParamsDao paramsDao = new ParamsDao();
		ParaMap result = paramsDao.getParamsDataByParentNo(configCAParamsNo);
		return result;
	}
	
	public ParaMap updateConfigParams(ParaMap configData) throws Exception {
		initConfigBaseData();
		ParamsDao paramsDao = new ParamsDao();
		ParaMap paramData = new ParaMap();
		//基本设置
		if (configData.containsKey("transSystemName")) {
			paramsDao.updateParamsData("parent_id;no", "", "transSystemName", "系统名称", configGeneralParamsId,
					configData.getString("transSystemName"), null, null, null, null, null);
		}
		if (configData.containsKey("transSystemShortName")) {
			paramsDao.updateParamsData("parent_id;no", "", "transSystemShortName", "系统简短名称", configGeneralParamsId,
					configData.getString("transSystemShortName"), null, null, null, null, null);
		}
		//交易规则
		if (configData.containsKey("updateTransRuleConfig")) {
			updateTransRuleConfig(ParaMap.fromString(configData.getString("updateTransRuleConfig")));
		}
		//CA设置
		if (configData.containsKey("transCAType")) {
			paramsDao.updateParamsData("parent_id;no", "", "transCAType", "CA类型", configCAParamsId,
					configData.getString("transCAType"), null, null, "本系统使用的CA类型", null, null);
		}
		if (configData.containsKey("transCheckCABeforeOpenModule")) {
			paramsDao.updateParamsData("parent_id;no", "", "transCheckCABeforeOpenModule", "用户打开模块前检查CA", configCAParamsId,
					configData.getString("transCheckCABeforeOpenModule"), null, null, null, null, null);
		}
		ParaMap result = new ParaMap();
		result.put("state", 1);
		return result;
	}
	
	/**
	 * 返回当前系统所有的业务类型，格式如“0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌;4=探矿权出让;5=探矿权转让;6=采矿权出让;7=采矿权转让”
	 * @return
	 * @throws Exception
	 */
	public static String getAllBusinessType() throws Exception {
		return getBusinessType(3);
	}
	
	/**
	 * 返回当前系统所有的业务类型完整信息，格式如“{0:{businessType:0, businessName:'土地出让', isValid:'1', moduleId:'', moduleParams:'', url:'', shortName:'宗地', noLabel:'宗地号', nameLabel:''}, ...}”
	 * @return
	 * @throws Exception
	 */
	public static String getAllBusinessTypeFull() throws Exception {
		return getBusinessType(6);
	}
	
	/**
	 * 返回当前系统所有支持的业务类型
	 * @param returnLabel true将返回业务类型名称，否则史返回值
	 * @return 按returnLabel返回格式如“0;1;2;3”或者“0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌”
	 * @throws Exception
	 */
	public static String getSupportBusinessType(String organId, boolean returnLabel) throws Exception {
		return getBusinessType(returnLabel ? 1 : 0, organId);
	}
	
	/**
	 * 返回当前系统指定业务类型的标签
	 * @param businessType 业务类型值
	 * @return 返回页面类型名称，如businessType=0，返回“土地出让”
	 * @throws Exception
	 */
	public static String getBusinessTypeLabel(String businessType) throws Exception {
		if (StrUtils.isNull(businessType))
			return null;
		if (StrUtils.isNull(ALL_BUSINESS_TYPE))
			getBusinessType(0);
		return StrUtils.getParamValue(ALL_BUSINESS_TYPE, businessType, true, "=", ";");
	}
	
	/**
	 * 返回当前系统指定业务类型的编号标签
	 * @param businessType 业务类型值
	 * @return 返回页面类型名称，如businessType=0，返回“土地出让”
	 * @throws Exception
	 */
	public static String getBusinessTypeNoLabel(String businessType) throws Exception {
		if (StrUtils.isNull(businessType))
			return null;
		if (ALL_BUSINESS_TYPE_FULL == null)
			getBusinessType(0);
		if (ALL_BUSINESS_TYPE_FULL == null || ALL_BUSINESS_TYPE_FULL.size() == 0)
			return null;
		else {
			ParaMap businessTypeData = (ParaMap) ALL_BUSINESS_TYPE_FULL.get(businessType);
			if (businessTypeData == null)
				return null;
			else
				return businessTypeData.getString("noLabel");
		}
	}
	
	/**
	 * 返回当前系统指定业务类型的名称标签
	 * @param businessType 业务类型值
	 * @return 返回页面类型名称，如businessType=0，返回“土地出让”
	 * @throws Exception
	 */
	public static String getBusinessTypeNameLabel(String businessType) throws Exception {
		if (StrUtils.isNull(businessType))
			return null;
		if (ALL_BUSINESS_TYPE_FULL == null)
			getBusinessType(0);
		if (ALL_BUSINESS_TYPE_FULL == null || ALL_BUSINESS_TYPE_FULL.size() == 0)
			return null;
		else {
			ParaMap businessTypeData = (ParaMap) ALL_BUSINESS_TYPE_FULL.get(businessType);
			if (businessTypeData == null)
				return null;
			else
				return businessTypeData.getString("nameLabel");
		}
	}
	
	/**
	 * 返回当前系统指定业务类型的短名称（或者称为标的、交易物名称）
	 * @param businessType 业务类型值
	 * @return 返回页面类型名称，如businessType=0，返回“土地出让”
	 * @throws Exception
	 */
	public static String getBusinessTypeShortName(String businessType) throws Exception {
		if (StrUtils.isNull(businessType))
			return null;
		if (ALL_BUSINESS_TYPE_FULL == null)
			getBusinessType(0);
		if (ALL_BUSINESS_TYPE_FULL == null || ALL_BUSINESS_TYPE_FULL.size() == 0)
			return null;
		else {
			ParaMap businessTypeData = (ParaMap) ALL_BUSINESS_TYPE_FULL.get(businessType);
			if (businessTypeData == null)
				return null;
			else
				return businessTypeData.getString("shortName");
		}
	}
	
	/**
	 * 返回系统中交易方式
	 * @param businessType 业务类别。null返回所有交易方式，否则返回指定交易方式
	 * @return 交易方式的键值对，键为业务类别下的交易方式名称，值是交易方式字典中的内容。注意如果不传businessType参数时，返回的Map不一定正确
	 * @throws Exception
	 */
	public static ParaMap getTransTypes(String businessType) throws Exception {
		if (ALL_BUSINESS_TYPE_FULL == null)
			getBusinessType(0);
		if (ALL_BUSINESS_TYPE_FULL == null || ALL_BUSINESS_TYPE_FULL.size() == 0)
			return null;
		if (StrUtils.isNull(businessType)) {
			ParaMap transTypes = new ParaMap();
			Iterator it = ALL_BUSINESS_TYPE_FULL.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				ParaMap businessTypeTransType = getTransTypes(key);
				if (businessTypeTransType != null && businessTypeTransType.size() > 0)
					transTypes.putAll(businessTypeTransType);
			}
			return transTypes;
		} else {
			if (!ALL_BUSINESS_TYPE_FULL.containsKey(businessType))
				return null;
			ParaMap businessTypeData = (ParaMap) ALL_BUSINESS_TYPE_FULL.get(businessType);
			if (businessTypeData == null || businessTypeData.size() == 0)
				return null;
			ParaMap transTypes = new ParaMap();
			Iterator it = businessTypeData.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				Object value = businessTypeData.get(key);
				if (value instanceof ParaMap)
					transTypes.put(key, value);
			}
			return transTypes;
		}
	}
	
	private static ParaMap queryTransRuleConfigData(String tableName, String keyField, ParaMap dataCondition) throws Exception {
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = dataSetDao.querySimpleData(tableName, dataCondition, "id");
		for(int i = 0; i < data.getSize(); i++) {
			ParaMap record = new ParaMap();
			List fieldList = data.getFields();
			for(int j = 0; j < fieldList.size(); j++)
				record.put(String.valueOf(fieldList.get(j)), String.valueOf(data.getRecordValue(i, j)));
			result.put(String.valueOf(data.getRecordValue(i, keyField)), record);
		}
		return result;
	}
	
	/**
	 * 从数据库中加载交易规则设置，返回JSON字符串
	 * @return
	 * @throws IOException
	 */
	public static ParaMap loadTransRuleConfig() throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = new ParaMap();
		ParaMap dataCondition = new ParaMap();
		dataCondition.put("is_valid", 1);
		//查询字典
		ParaMap dictionaries = new ParaMap();
		dataCondition.put("!no", "000");//排除老朱添加的“000所有”记录
		dictionaries.put("goodsTypes", queryTransRuleConfigData("trans_goods_type", "id", dataCondition));
		dictionaries.put("businessTypes", queryTransRuleConfigData("trans_business_type", "id", dataCondition));
		dataCondition.remove("!no");
		dictionaries.put("steps", queryTransRuleConfigData("trans_license_step", "id", dataCondition));
		dictionaries.put("quotas", queryTransRuleConfigData("trans_multi_trade", "id", dataCondition));
		dictionaries.put("transTypes", queryTransRuleConfigData("trans_transaction_type", "id", dataCondition));
		result.put("dictionaries", dictionaries);
		//查询实际配置
		ParaMap businessTypes = new ParaMap();
		ParaMap transTypeRelData = dataSetDao.querySimpleData("trans_transaction_type_rel", dataCondition, "turn");
		ParaMap licenseStepRelData = dataSetDao.querySimpleData("trans_license_step_rel", dataCondition, "turn");
		ParaMap quotaRelData = dataSetDao.querySimpleData("trans_multi_trade_rel", dataCondition, "turn");
		ParaMap businessTypeRelData = dataSetDao.querySimpleData("trans_business_type_rel", dataCondition);
		for(int i = 0; i < businessTypeRelData.getSize(); i++) {
			String businessTypeRelId = String.valueOf(businessTypeRelData.getRecordValue(i, "id"));
			ParaMap businessType = new ParaMap();
			businessType.put("id", businessTypeRelId);
			businessType.put("goods_type_id", String.valueOf(businessTypeRelData.getRecordValue(i, "goods_type_id")));
			businessType.put("business_type_id", String.valueOf(businessTypeRelData.getRecordValue(i, "business_type_id")));
			businessType.put("name", String.valueOf(businessTypeRelData.getRecordValue(i, "name")));
			for(int j = 0; j < transTypeRelData.getSize(); j++) {
				String parentBusinessTypeRelId = String.valueOf(transTypeRelData.getRecordValue(j, "business_type_rel_id"));
				if (StrUtils.isNotNull(parentBusinessTypeRelId) && parentBusinessTypeRelId.equals(businessTypeRelId)) {
					String transTypeRelId = String.valueOf(transTypeRelData.getRecordValue(j, "id"));
					ParaMap transType = new ParaMap();
					transType.put("id", String.valueOf(transTypeRelData.getRecordValue(j, "id")));
					transType.put("trans_type_id", String.valueOf(transTypeRelData.getRecordValue(j, "trans_type_id")));
					transType.put("name", String.valueOf(transTypeRelData.getRecordValue(j, "name")));
					List relLicenseSteps = new ArrayList();
					for(int m = 0; m < licenseStepRelData.getSize(); m++) {
						if (transTypeRelId.equals(String.valueOf(licenseStepRelData.getRecordValue(m, "trans_type_rel_id")))) {
							ParaMap relLicenseStep = new ParaMap();
							relLicenseStep.put("id", String.valueOf(licenseStepRelData.getRecordValue(m, "id")));
							relLicenseStep.put("trans_type_rel_id", String.valueOf(licenseStepRelData.getRecordValue(m, "trans_type_rel_id")));
							relLicenseStep.put("license_step_id", String.valueOf(licenseStepRelData.getRecordValue(m, "license_step_id")));
							relLicenseStep.put("name", String.valueOf(licenseStepRelData.getRecordValue(m, "name")));
							relLicenseSteps.add(relLicenseStep);
						}
					}
					List relQuotas = new ArrayList();
					for(int m = 0; m < quotaRelData.getSize(); m++) {
						if (transTypeRelId.equals(String.valueOf(quotaRelData.getRecordValue(m, "trans_type_rel_id")))) {
							ParaMap relQuota = new ParaMap();
							relQuota.put("id", String.valueOf(quotaRelData.getRecordValue(m, "id")));
							relQuota.put("trans_type_rel_id", String.valueOf(quotaRelData.getRecordValue(m, "trans_type_rel_id")));
							relQuota.put("multi_trade_id", String.valueOf(quotaRelData.getRecordValue(m, "multi_trade_id")));
							relQuota.put("name", String.valueOf(quotaRelData.getRecordValue(m, "name")));
							relQuotas.add(relQuota);
						}
					}
					transType.put("steps", relLicenseSteps);
					transType.put("quotas", relQuotas);
					businessType.put(String.valueOf(transTypeRelData.getRecordValue(j, "id")), transType);
				}
			}
			businessTypes.put(String.valueOf(businessTypeRelData.getRecordValue(i, "id")), businessType);
		}
		result.put("businessTypes", businessTypes);
		return result;
	}
	
	/**
	 * 加载交易规则，返回ParaMap对象
	 * @return
	 * @throws Exception
	 */
	public static ParaMap getTransRuleConfig() throws Exception {
		if (TRANS_RULE_CONFIG == null) {
			ParaMap transRuleConfigData = loadTransRuleConfig();
			TRANS_RULE_CONFIG = transRuleConfigData.toString();
			return transRuleConfigData;
		} else
			return ParaMap.fromString(TRANS_RULE_CONFIG);
	}

	/**
	 * 更新交易规则数据，前台传入json格式，同原来文件设置方式
	 * @param configData
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateTransRuleConfig(ParaMap configData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap originalConfig = getTransRuleConfig();
		ParaMap originalDictionaries = (ParaMap) originalConfig.get("dictionaries");
		ParaMap originalBusinessTypeRels = (ParaMap) originalConfig.get("businessTypes");
		ParaMap sqlParams = new ParaMap();
		Map<String, String> newGoodsTypeIds = new HashMap<String, String>();
		Map<String, String> newBusinessTypeIds = new HashMap<String, String>();
		Map<String, String> newTransTypeIds = new HashMap<String, String>();
		Map<String, String> newStepIds = new HashMap<String, String>();
		Map<String, String> newQuotaIds = new HashMap<String, String>();
		int intRow = 0;
		List<String> deleteIds = new ArrayList<String>();
		ParaMap data = new ParaMap();
		StringBuffer updateSql = new StringBuffer();
		StringBuffer deleteSql = new StringBuffer();
		if (configData.containsKey("dictionaries")) {
			ParaMap dictionaries = (ParaMap) configData.get("dictionaries");
			//***********更新字典****************
			//更新交易物类别
			if (dictionaries.containsKey("goodsTypes")) {
				ParaMap goodsTypes = (ParaMap) dictionaries.get("goodsTypes");
				if (goodsTypes != null) {
					if (goodsTypes.size() == 0) {
						deleteSql.append("delete from trans_multi_trade_rel;\n");
						deleteSql.append("delete from trans_license_step_rel;\n");
						deleteSql.append("delete from trans_transaction_type_rel;\n");
						deleteSql.append("delete from trans_business_type_rel;\n");
						deleteSql.append("delete from trans_goods_type;\n");
					} else {
						deleteIds.clear();
						ParaMap originalGoodsTypesDict = (ParaMap) originalDictionaries.get("goodsTypes");
						if (originalGoodsTypesDict != null && originalGoodsTypesDict.size() > 0) {
							Iterator it = originalGoodsTypesDict.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next().toString();
								if (!goodsTypes.containsKey(key))
									deleteIds.add(key);
							}
							if (deleteIds.size() > 0) {
								StringBuffer deleteIdParams = new StringBuffer();
								for(int i = 0; i < deleteIds.size(); i++) {
									deleteIdParams.append(":del_goods_type_id_" + i + ",");
									sqlParams.put("del_goods_type_id_" + i, deleteIds.get(i));
								}
								deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
								deleteSql.append("delete from trans_multi_trade_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where goods_type_id in (" + deleteIdParams.toString() + ")));\n");
								deleteSql.append("delete from trans_license_step_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where goods_type_id in (" + deleteIdParams.toString() + ")));\n");
								deleteSql.append("delete from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where goods_type_id in (" + deleteIdParams.toString() + "));\n");
								deleteSql.append("delete from trans_business_type_rel where goods_type_id in (" + deleteIdParams.toString() + ");\n");
								deleteSql.append("delete from trans_goods_type where id in (" + deleteIdParams.toString() + ");\n");
							}
						}
						intRow = 0;
						Iterator it = goodsTypes.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							ParaMap item = (ParaMap) goodsTypes.get(key);
							String goodsTypeId = item.getString("id");
							if (StrUtils.isNull(goodsTypeId) || goodsTypeId.startsWith(newRecordIdPrefix)) {
								updateSql.append("insert into trans_goods_type(id, no, name, goods_name, no_label, name_label, use_label,"
										+ "edit_goods_module_id, edit_goods_module_params, edit_goods_module_url,"
										+ "view_goods_module_id, view_goods_module_params, view_goods_module_url)"
										+ " values(:goods_type_id_" + intRow + ", :goods_type_no_" + intRow + ", :goods_type_name_" + intRow + ","
										+ " :goods_type_goods_name_" + intRow + ", :goods_type_no_label_" + intRow + ", :goods_type_name_label_" + intRow + ","
										+ " :goods_type_use_label_" + intRow + ", :goods_type_edit_goods_module_id_" + intRow + ","
										+ " :goods_type_edit_goods_module_params_" + intRow + ", :goods_type_edit_goods_module_url_" + intRow + ","
										+ " :goods_type_edit_goods_module_id_" + intRow + ", :goods_type_edit_goods_module_params_" + intRow + ","
										+ " :goods_type_edit_goods_module_url_" + intRow + ");\n");
								String newGoodsTypeId = item.getString("no");//IDGenerator.generatorID("trans_goods_type");
								newGoodsTypeIds.put(goodsTypeId, newGoodsTypeId);
								goodsTypeId = newGoodsTypeId;
							} else
								updateSql.append("update trans_goods_type set no = :goods_type_no_" + intRow + ", name = :goods_type_name_" + intRow + ","
										+ " goods_name = :goods_type_goods_name_" + intRow + ", no_label = :goods_type_no_label_" + intRow + ","
										+ " name_label = :goods_type_name_label_" + intRow + ", use_label = :goods_type_use_label_" + intRow + ","
										+ " edit_goods_module_id = :goods_type_edit_goods_module_id_" + intRow + ","
										+ " edit_goods_module_params = :goods_type_edit_goods_module_params_" + intRow + ","
										+ " edit_goods_module_url = :goods_type_edit_goods_module_url_" + intRow + ","
										+ " view_goods_module_id = :goods_type_view_goods_module_id_" + intRow + ","
										+ " view_goods_module_params = :goods_type_view_goods_module_params_" + intRow + ","
										+ " view_goods_module_url = :goods_type_view_goods_module_url_" + intRow
										+ " where id = :goods_type_id_" + intRow + ";\n");
							sqlParams.put("goods_type_id_" + intRow, goodsTypeId);
							sqlParams.put("goods_type_no_" + intRow, item.getString("no"));
							sqlParams.put("goods_type_name_" + intRow, item.getString("name"));
							sqlParams.put("goods_type_goods_name_" + intRow, item.getString("goods_name"));
							sqlParams.put("goods_type_no_label_" + intRow, item.getString("no_label"));
							sqlParams.put("goods_type_name_label_" + intRow, item.getString("name_label"));
							sqlParams.put("goods_type_use_label_" + intRow, item.getString("use_label"));
							sqlParams.put("goods_type_edit_goods_module_id_" + intRow, item.getString("edit_goods_module_id"));
							sqlParams.put("goods_type_edit_goods_module_params_" + intRow, StrUtils.trim(item.getString("edit_goods_module_params")));
							sqlParams.put("goods_type_edit_goods_module_url_" + intRow, StrUtils.trim(item.getString("edit_goods_module_url")));
							sqlParams.put("goods_type_view_goods_module_id_" + intRow, item.getString("view_goods_module_id"));
							sqlParams.put("goods_type_view_goods_module_params_" + intRow, StrUtils.trim(item.getString("view_goods_module_params")));
							sqlParams.put("goods_type_view_goods_module_url_" + intRow, StrUtils.trim(item.getString("view_goods_module_url")));
							intRow++;
						}
					}
				}
			}
			//更新业务类别
			if (dictionaries.containsKey("businessTypes")) {
				ParaMap businessTypes = (ParaMap) dictionaries.get("businessTypes");
				if (businessTypes != null) {
					if (businessTypes.size() == 0) {
						deleteSql.append("delete from trans_multi_trade_rel;\n");
						deleteSql.append("delete from trans_license_step_rel;\n");
						deleteSql.append("delete from trans_transaction_type_rel;\n");
						deleteSql.append("delete from trans_business_type_rel;\n");
						deleteSql.append("delete from trans_business_type;\n");
					} else {
						deleteIds.clear();
						ParaMap originalBusinessTypesDict = (ParaMap) originalDictionaries.get("businessTypes");
						if (originalBusinessTypesDict != null && originalBusinessTypesDict.size() > 0) {
							Iterator it = originalBusinessTypesDict.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next().toString();
								if (!businessTypes.containsKey(key))
									deleteIds.add(key);
							}
							if (deleteIds.size() > 0) {
								StringBuffer deleteIdParams = new StringBuffer();
								for(int i = 0; i < deleteIds.size(); i++) {
									deleteIdParams.append(":del_business_type_id_" + i + ",");
									sqlParams.put("del_business_type_id_" + i, deleteIds.get(i));
								}
								deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
								deleteSql.append("delete from trans_multi_trade_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where business_type_id in (" + deleteIdParams.toString() + ")));\n");
								deleteSql.append("delete from trans_license_step_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where business_type_id in (" + deleteIdParams.toString() + ")));\n");
								deleteSql.append("delete from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where business_type_id in (" + deleteIdParams.toString() + "));\n");
								deleteSql.append("delete from trans_business_type_rel where business_type_id in (" + deleteIdParams.toString() + ");\n");
								deleteSql.append("delete from trans_business_type where id in (" + deleteIdParams.toString() + ");\n");
							}
						}
						intRow = 0;
						Iterator it = businessTypes.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							ParaMap item = (ParaMap) businessTypes.get(key);
							String businessTypeId = item.getString("id");
							if (StrUtils.isNull(businessTypeId) || businessTypeId.startsWith(newRecordIdPrefix)) {
								updateSql.append("insert into trans_business_type(id, no, name)"
										+ " values(:business_type_id_" + intRow + ", :business_type_no_" + intRow + ", :business_type_name_" + intRow + ");\n");
								String newBusinessTypeId = item.getString("no");//IDGenerator.generatorID("trans_business_type");
								newBusinessTypeIds.put(businessTypeId, newBusinessTypeId);
								businessTypeId = newBusinessTypeId;
							} else
								updateSql.append("update trans_business_type set no = :business_type_no_" + intRow + ", name = :business_type_name_" + intRow
										+ " where id = :business_type_id_" + intRow + ";\n");
							sqlParams.put("business_type_id_" + intRow, businessTypeId);
							sqlParams.put("business_type_no_" + intRow, item.getString("no"));
							sqlParams.put("business_type_name_" + intRow, item.getString("name"));
							intRow++;
						}
					}
				}
			}
			//更新竞买申请步骤数据
			if (dictionaries.containsKey("steps")) {
				ParaMap steps = (ParaMap) dictionaries.get("steps");
				if (steps != null) {
					if (steps.size() == 0) {
						deleteSql.append("delete from trans_license_step_rel;\n");
						deleteSql.append("delete from trans_license_step;\n");
					} else {
						deleteIds.clear();
						ParaMap originalStepsDict = (ParaMap) originalDictionaries.get("steps");
						if (originalStepsDict != null && originalStepsDict.size() > 0) {
							Iterator it = originalStepsDict.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next().toString();
								if (!steps.containsKey(key))
									deleteIds.add(key);
							}
							if (deleteIds.size() > 0) {
								StringBuffer deleteIdParams = new StringBuffer();
								for(int i = 0; i < deleteIds.size(); i++) {
									deleteIdParams.append(":del_step_id_" + i + ",");
									sqlParams.put("del_step_id_" + i, deleteIds.get(i));
								}
								deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
								deleteSql.append("delete from trans_license_step_rel where license_step_id in (" + deleteIdParams.toString() + ");\n");
								deleteSql.append("delete from trans_license_step where id in (" + deleteIdParams.toString() + ");\n");
							}
						}
						intRow = 0;
						Iterator it = steps.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							ParaMap item = (ParaMap) steps.get(key);
							String stepId = item.getString("id");
							if (StrUtils.isNull(stepId) || stepId.startsWith(newRecordIdPrefix)) {
								updateSql.append("insert into trans_license_step(id, no, name, module_id, module_params, module_url)"
										+ " values(:step_id_" + intRow + ", :step_no_" + intRow + ", :step_name_" + intRow + ", :step_module_id_" + intRow + ", :step_module_params_" + intRow + ", :step_module_url_" + intRow + ");\n");
								String newStepId = IDGenerator.generatorID("trans_license_step");
								newStepIds.put(stepId, newStepId);
								stepId = newStepId;
							} else
								updateSql.append("update trans_license_step set no = :step_no_" + intRow + ", name = :step_name_" + intRow + ", module_id = :step_module_id_" + intRow + ", module_params = :step_module_params_" + intRow + ", module_url = :step_module_url_" + intRow
										+ " where id = :step_id_" + intRow + ";\n");
							sqlParams.put("step_id_" + intRow, stepId);
							sqlParams.put("step_no_" + intRow, null);
							sqlParams.put("step_name_" + intRow, item.getString("name"));
							sqlParams.put("step_module_id_" + intRow, item.getString("module_id"));
							sqlParams.put("step_module_params_" + intRow, StrUtils.trim(item.getString("module_params")));
							sqlParams.put("step_module_url_" + intRow, StrUtils.trim(item.getString("module_url")));
							intRow++;
						}
					}
				}
			}
			//更新多指标数据
			if (dictionaries.containsKey("quotas")) {
				ParaMap quotas = (ParaMap) dictionaries.get("quotas");
				if (quotas != null) {
					if (quotas.size() == 0) {
						deleteSql.append("delete from trans_multi_trade_rel;\n");
						deleteSql.append("delete from trans_multi_trade;\n");
					} else {
						deleteIds.clear();
						ParaMap originalQuotasDict = (ParaMap) originalDictionaries.get("quotas");
						if (originalQuotasDict != null && originalQuotasDict.size() > 0) {
							Iterator it = originalQuotasDict.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next().toString();
								if (!quotas.containsKey(key))
									deleteIds.add(key);
							}
							if (deleteIds.size() > 0) {
								StringBuffer deleteIdParams = new StringBuffer();
								for(int i = 0; i < deleteIds.size(); i++) {
									deleteIdParams.append(":del_quota_id_" + i + ",");
									sqlParams.put("del_quota_id_" + i, deleteIds.get(i));
								}
								deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
								deleteSql.append("delete from trans_multi_trade_rel where multi_trade_id in (" + deleteIdParams.toString() + ");\n");
								deleteSql.append("delete from trans_multi_trade where id in (" + deleteIdParams.toString() + ");\n");
							}
						}
						intRow = 0;
						Iterator it = quotas.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							ParaMap item = (ParaMap) quotas.get(key);
							String quotaId = item.getString("id");
							if (StrUtils.isNull(quotaId) || quotaId.startsWith(newRecordIdPrefix)) {
								updateSql.append("insert into trans_multi_trade(id, no, name, class_type, unit, enter_flag_0, enter_flag_1, enter_flag_2, first_wait, limit_wait, last_wait)"
										+ " values(:quota_id_" + intRow + ", :quota_no_" + intRow + ", :quota_name_" + intRow + ", :quota_type_" + intRow + ", :quota_unit_" + intRow + ","
										+ " :quota_enter_flag_0_" + intRow + ", :quota_enter_flag_1_" + intRow + ", :quota_enter_flag_2_" + intRow + ","
										+ " :quota_first_wait_" + intRow + ", :quota_limit_wait_" + intRow + ", :quota_last_wait_" + intRow + ");\n");
								String newQuotaId = IDGenerator.generatorID("trans_multi_trade");
								newQuotaIds.put(quotaId, newQuotaId);
								quotaId = newQuotaId;
							} else
								updateSql.append("update trans_multi_trade set no = :quota_no_" + intRow + ", name = :quota_name_" + intRow + ", class_type = :quota_type_" + intRow + ", unit = :quota_unit_" + intRow + ","
										+ " enter_flag_0 = :quota_enter_flag_0_" + intRow + ", enter_flag_1 = :quota_enter_flag_1_" + intRow + ", enter_flag_2 = :quota_enter_flag_2_" + intRow + ","
										+ " first_wait = :quota_first_wait_" + intRow + ", limit_wait = :quota_limit_wait_" + intRow + ", last_wait = :quota_last_wait_" + intRow
										+ " where id = :quota_id_" + intRow + ";\n");
							sqlParams.put("quota_id_" + intRow, quotaId);
							sqlParams.put("quota_no_" + intRow, null);
							sqlParams.put("quota_name_" + intRow, item.getString("name"));
							sqlParams.put("quota_type_" + intRow, item.getString("class_type"));
							sqlParams.put("quota_unit_" + intRow, item.getString("unit"));
							sqlParams.put("quota_enter_flag_0_" + intRow, item.getString("enter_flag_0"));
							sqlParams.put("quota_enter_flag_1_" + intRow, item.getString("enter_flag_1"));
							sqlParams.put("quota_enter_flag_2_" + intRow, item.getString("enter_flag_2"));
							sqlParams.put("quota_first_wait_" + intRow, item.getString("first_wait"));
							sqlParams.put("quota_limit_wait_" + intRow, item.getString("limit_wait"));
							sqlParams.put("quota_last_wait_" + intRow, item.getString("last_wait"));
							intRow++;
						}
					}
				}
			}
			//更新交易方式
			if (dictionaries.containsKey("transTypes")) {
				ParaMap transTypes = (ParaMap) dictionaries.get("transTypes");
				if (transTypes != null) {
					if (transTypes.size() == 0) {
						deleteSql.append("delete from trans_transaction_type_rel;\n");
						deleteSql.append("delete from trans_transaction_type;\n");
					} else {
						deleteIds.clear();
						ParaMap originalTransTypesDict = (ParaMap) originalDictionaries.get("transTypes");
						if (originalTransTypesDict != null && originalTransTypesDict.size() > 0) {
							Iterator it = originalTransTypesDict.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next().toString();
								if (!transTypes.containsKey(key))
									deleteIds.add(key);
							}
							if (deleteIds.size() > 0) {
								StringBuffer deleteIdParams = new StringBuffer();
								for(int i = 0; i < deleteIds.size(); i++) {
									deleteIdParams.append(":del_trans_type_id_" + i + ",");
									sqlParams.put("del_trans_type_id_" + i, deleteIds.get(i));
								}
								deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
								deleteSql.append("delete from trans_transaction_type_rel where trans_type_id in (" + deleteIdParams.toString() + ");\n");
								deleteSql.append("delete from trans_transaction_type where id in (" + deleteIdParams.toString() + ");\n");
							}
						}
						intRow = 0;
						Iterator it = transTypes.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							ParaMap item = (ParaMap) transTypes.get(key);
							String transTypeId = item.getString("id");
							if (StrUtils.isNull(transTypeId) || transTypeId.startsWith(newRecordIdPrefix)) {
								updateSql.append("insert into trans_transaction_type(id, no, name, trans_type, is_net_trans, is_limit_trans, allow_live, allow_union, allow_trust, end_notice_time, end_list_time, end_focus_time, enter_flag_0, enter_flag_1, enter_flag_2)"
										+ " values(:trans_type_id_" + intRow + ", :trans_type_no_" + intRow + ", :trans_type_name_" + intRow + ", :trans_type_trans_type_" + intRow + ", :trans_type_is_net_trans_" + intRow + ","
										+ " :trans_type_is_limit_trans_" + intRow + ", :trans_type_allow_live_" + intRow + ", :trans_type_allow_union_" + intRow + ","
										+ " :trans_type_allow_trust_" + intRow + ", :trans_type_end_notice_time_" + intRow + ", :trans_type_end_list_time_" + intRow + ","
										+ " :trans_type_end_focus_time_" + intRow + ", :trans_type_enter_flag_0_" + intRow + ", :trans_type_enter_flag_1_" + intRow + ","
										+ " :trans_type_enter_flag_2_" + intRow + ");\n");
								String newTransTypeId = IDGenerator.generatorID("trans_transaction_type");
								newTransTypeIds.put(transTypeId, newTransTypeId);
								transTypeId = newTransTypeId;
							} else
								updateSql.append("update trans_transaction_type set no = :trans_type_no_" + intRow + ", name = :trans_type_name_" + intRow + ", trans_type = :trans_type_trans_type_" + intRow + ", is_net_trans = :trans_type_is_net_trans_" + intRow + ","
										+ " is_limit_trans = :trans_type_is_limit_trans_" + intRow + ", allow_live = :trans_type_allow_live_" + intRow + ", allow_union = :trans_type_allow_union_" + intRow + ","
										+ " allow_trust = :trans_type_allow_trust_" + intRow + ", end_notice_time = :trans_type_end_notice_time_" + intRow + ", end_list_time = :trans_type_end_list_time_" + intRow + ", end_focus_time = :trans_type_end_focus_time_" + intRow + ","
										+ " enter_flag_0 = :trans_type_enter_flag_0_" + intRow + ", enter_flag_1 = :trans_type_enter_flag_1_" + intRow + ", enter_flag_2 = :trans_type_enter_flag_2_" + intRow
										+ " where id = :trans_type_id_" + intRow + ";\n");
							sqlParams.put("trans_type_id_" + intRow, transTypeId);
							sqlParams.put("trans_type_no_" + intRow, null);
							sqlParams.put("trans_type_name_" + intRow, item.getString("name"));
							sqlParams.put("trans_type_trans_type_" + intRow, item.getString("trans_type"));
							sqlParams.put("trans_type_is_net_trans_" + intRow, item.getString("is_net_trans"));
							sqlParams.put("trans_type_is_limit_trans_" + intRow, item.getString("is_limit_trans"));
							sqlParams.put("trans_type_allow_live_" + intRow, item.getString("allow_live"));
							sqlParams.put("trans_type_allow_union_" + intRow, item.getString("allow_union"));
							sqlParams.put("trans_type_allow_trust_" + intRow, item.getString("allow_trust"));
							sqlParams.put("trans_type_end_notice_time_" + intRow, item.getString("end_notice_time"));
							sqlParams.put("trans_type_end_list_time_" + intRow, item.getString("end_list_time"));
							sqlParams.put("trans_type_end_focus_time_" + intRow, item.getString("end_focus_time"));					
							sqlParams.put("trans_type_enter_flag_0_" + intRow, item.getString("enter_flag_0"));
							sqlParams.put("trans_type_enter_flag_1_" + intRow, item.getString("enter_flag_1"));
							sqlParams.put("trans_type_enter_flag_2_" + intRow, item.getString("enter_flag_2"));
							intRow++;
						}
					}
				}
			}
		}
		//保存业务类别设置数据
		if (configData.containsKey("businessTypes")) {
			ParaMap businessTypeRels = (ParaMap) configData.get("businessTypes");
			if (businessTypeRels != null) {
				if (businessTypeRels.size() == 0) {
					deleteSql.append("delete from trans_multi_trade_rel;\n");
					deleteSql.append("delete from trans_license_step_rel;\n");
					deleteSql.append("delete from trans_transaction_type_rel;\n");
					deleteSql.append("delete from trans_business_type_rel;\n");
				} else {
					deleteIds.clear();
					if (originalBusinessTypeRels != null && originalBusinessTypeRels.size() > 0) {
						Iterator it = originalBusinessTypeRels.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next().toString();
							if (!businessTypeRels.containsKey(key))
								deleteIds.add(key);
						}
						if (deleteIds.size() > 0) {
							StringBuffer deleteIdParams = new StringBuffer();
							for(int i = 0; i < deleteIds.size(); i++) {
								deleteIdParams.append(":del_btr_id_" + i + ",");
								sqlParams.put("del_btr_id_" + i, deleteIds.get(i));
							}
							deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
							deleteSql.append("delete from trans_multi_trade_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where id in (" + deleteIdParams.toString() + ")));\n");
							deleteSql.append("delete from trans_license_step_rel where trans_type_rel_id in (select id from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where id in (" + deleteIdParams.toString() + ")));\n");
							deleteSql.append("delete from trans_transaction_type_rel where business_type_rel_id in (select id from trans_business_type_rel where id in (" + deleteIdParams.toString() + "));\n");
							deleteSql.append("delete from trans_business_type_rel where id in (" + deleteIdParams.toString() + ");\n");
						}
					}
					intRow = 0;
					boolean blnNew = false;
					Iterator it = businessTypeRels.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next().toString();
						ParaMap value = (ParaMap) businessTypeRels.get(key);
						String goodsTypeId = value.getString("goods_type_id");
						if (newGoodsTypeIds.containsKey(goodsTypeId))
							goodsTypeId = newGoodsTypeIds.get(goodsTypeId);
						String businessTypeId = value.getString("business_type_id");
						if (newBusinessTypeIds.containsKey(businessTypeId))
							businessTypeId = newBusinessTypeIds.get(businessTypeId);
						String businessTypeRelId = value.getString("id");
						blnNew = StrUtils.isNull(businessTypeRelId) || businessTypeRelId.startsWith(newRecordIdPrefix);
						if (blnNew) {
							updateSql.append("insert into trans_business_type_rel(id, goods_type_id, business_type_id, name)"
									+ " values(:btr_id_" + intRow + ", :btr_goods_type_id_" + intRow + ", :btr_business_type_id_" + intRow + ", :btr_name_" + intRow + ");\n");
							//businessTypeRelId = IDGenerator.generatorID("trans_business_type_rel");
							businessTypeRelId = goodsTypeId + businessTypeId;
						} else {
							updateSql.append("update trans_business_type_rel set goods_type_id = :btr_goods_type_id_" + intRow + ", business_type_id = :btr_business_type_id_" + intRow + ", name = :btr_name_" + intRow
									+ " where id = :btr_id_" + intRow + ";\n");
							updateSql.append("if sql%notfound then\n");
							updateSql.append("insert into trans_business_type_rel(id, goods_type_id, business_type_id, name)"
									+ " values(:btr_id_" + intRow + ", :btr_goods_type_id_" + intRow + ", :btr_business_type_id_" + intRow + ", :btr_name_" + intRow + ");\n");
							updateSql.append("end if;\n");
						}
						sqlParams.put("btr_id_" + intRow, businessTypeRelId);
						sqlParams.put("btr_goods_type_id_" + intRow, goodsTypeId);
						sqlParams.put("btr_business_type_id_" + intRow, businessTypeId);
						sqlParams.put("btr_name_" + intRow, value.getString("name"));
						ParaMap originalBusinessTypeRel = originalBusinessTypeRels == null ? null : (ParaMap) originalBusinessTypeRels.get(businessTypeRelId);
						if (!blnNew && originalBusinessTypeRel != null) {
							//检查业务类型下级数据（交易方式）
							int intDelRow = 0;
							deleteIds.clear();
							if (originalBusinessTypeRel.size() > 0) {
								Iterator it1 = originalBusinessTypeRel.keySet().iterator();
								while (it1.hasNext()) {
									String key1 = it1.next().toString();
									Object value1 = originalBusinessTypeRel.get(key1);
									if (value1 instanceof ParaMap)
										if (!value.containsKey(key1))
											deleteIds.add(key1);
								}
								if (deleteIds.size() > 0) {
									StringBuffer deleteIdParams = new StringBuffer();
									for(int i = 0; i < deleteIds.size(); i++) {
										deleteIdParams.append(":del_btrtr_id_" + i + ",");
										sqlParams.put("del_btrtr_id_" + i, deleteIds.get(i));
									}
									deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
									deleteSql.append("delete from trans_multi_trade_rel where trans_type_rel_id in (" + deleteIdParams.toString() + ");\n");
									deleteSql.append("delete from trans_license_step_rel where trans_type_rel_id in (" + deleteIdParams.toString() + ");\n");
									deleteSql.append("delete from trans_transaction_type_rel where id in (" + deleteIdParams.toString() + ");\n");
								}
							}
						}
						int intTransTypeRelRow = 0;
						Iterator itTransTypeRel = value.keySet().iterator();
						while (itTransTypeRel.hasNext()) {
							String keyTransTypeRel = itTransTypeRel.next().toString();
							Object valueTransTypeRel = value.get(keyTransTypeRel);
							if (valueTransTypeRel instanceof ParaMap) {//业务类别下属交易方式
								ParaMap itemTransTypeRel = (ParaMap) valueTransTypeRel;
								String transTypeId = itemTransTypeRel.getString("trans_type_id");
								if (newTransTypeIds.containsKey(transTypeId))
									transTypeId = newTransTypeIds.get(transTypeId);
								String transTypeRelId = itemTransTypeRel.getString("id");
								blnNew = StrUtils.isNull(transTypeRelId) || transTypeRelId.startsWith(newRecordIdPrefix);
								if (blnNew) {
									updateSql.append("insert into trans_transaction_type_rel(id, business_type_rel_id, trans_type_id, name, turn)"
											+ " values(:btrtr_id_" + intRow + "_" + intTransTypeRelRow + ", :btrtr_business_type_rel_id_" + intRow + "_" + intTransTypeRelRow + ", :btrtr_trans_type_id_" + intRow + "_" + intTransTypeRelRow + ", :btrtr_name_" + intRow + "_" + intTransTypeRelRow + ", :btrtr_turn_" + intRow + "_" + intTransTypeRelRow + ");\n");
									//transTypeRelId = IDGenerator.generatorID("trans_transaction_type_rel");
									transTypeRelId = businessTypeRelId + transTypeId;
								} else {
									updateSql.append("update trans_transaction_type_rel set business_type_rel_id = :btrtr_business_type_rel_id_" + intRow + "_" + intTransTypeRelRow + ", trans_type_id = :btrtr_trans_type_id_" + intRow + "_" + intTransTypeRelRow + ", name = :btrtr_name_" + intRow + "_" + intTransTypeRelRow + ", turn = :btrtr_turn_" + intRow + "_" + intTransTypeRelRow
											+ " where id = :btrtr_id_" + intRow + "_" + intTransTypeRelRow + ";\n");
								}
								sqlParams.put("btrtr_id_" + intRow + "_" + intTransTypeRelRow, transTypeRelId);
								sqlParams.put("btrtr_business_type_rel_id_" + intRow + "_" + intTransTypeRelRow, businessTypeRelId);
								sqlParams.put("btrtr_trans_type_id_" + intRow + "_" + intTransTypeRelRow, transTypeId);
								sqlParams.put("btrtr_name_" + intRow + "_" + intTransTypeRelRow, itemTransTypeRel.getString("name"));
								sqlParams.put("btrtr_turn_" + intRow + "_" + intTransTypeRelRow, intTransTypeRelRow);
								//更新交易方式下属竞买申请步骤
								ParaMap originalTransTypeRel = originalBusinessTypeRel == null ? null : (ParaMap) originalBusinessTypeRel.get(transTypeRelId);
								List steps = (List) itemTransTypeRel.get("steps");
								if (steps == null || steps.size() == 0) {
									if (!blnNew) {
										deleteSql.append("delete from trans_license_step_rel where trans_type_rel_id = :btrtr_id_" + intRow + "_" + intTransTypeRelRow + ";\n");
									}
								} else {
									if (!blnNew && originalTransTypeRel != null) {
										//检查是否有需要删除的竞买申请步骤
										deleteIds.clear();
										List originalLicenseStepRels = (List) originalTransTypeRel.get("steps");
										if (originalLicenseStepRels != null && originalLicenseStepRels.size() > 0) {
											for(int i = 0; i < originalLicenseStepRels.size(); i++) {
												ParaMap originalLicenseStepRel = (ParaMap) originalLicenseStepRels.get(i);
												if (originalLicenseStepRel != null && originalLicenseStepRel.containsKey("id")
														&& StrUtils.isNotNull(originalLicenseStepRel.getString("id"))) {
													String licenseStepRelId = originalLicenseStepRel.getString("id");
													boolean blnExists = false;
													for(int j = 0; j < steps.size(); j++) {
														ParaMap licenseStepRel = (ParaMap) steps.get(j);
														if (licenseStepRel != null && licenseStepRel.containsKey("id")
																&& StrUtils.isNotNull(licenseStepRel.getString("id"))
																&& licenseStepRel.getString("id").equalsIgnoreCase(licenseStepRelId)) {
															blnExists = true;
															break;
														}
													}
													if (!blnExists)
														deleteIds.add(licenseStepRelId);
												}
											}
											if (deleteIds.size() > 0) {
												StringBuffer deleteIdParams = new StringBuffer();
												for(int i = 0; i < deleteIds.size(); i++) {
													deleteIdParams.append(":del_btrtrlsr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ",");
													sqlParams.put("del_btrtrlsr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, deleteIds.get(i));
												}
												deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
												deleteSql.append("delete from trans_license_step_rel where id in (" + deleteIdParams.toString() + ");\n");
											}
										}
									}
									for(int i = 0; i < steps.size(); i++) {
										ParaMap licenseStepRel = (ParaMap) steps.get(i);
										String stepRelId = licenseStepRel.getString("id");
										if (StrUtils.isNull(stepRelId) || stepRelId.startsWith(newRecordIdPrefix)) {
											updateSql.append("insert into trans_license_step_rel(id, trans_type_rel_id, license_step_id, name, turn)"
													+ " values(:btrtrlsr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrlsr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrlsr_license_step_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrlsr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrlsr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i + ");\n");
											stepRelId = IDGenerator.generatorID("trans_license_step_rel");
										} else
											updateSql.append("update trans_license_step_rel set trans_type_rel_id = :btrtrlsr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", license_step_id = :btrtrlsr_license_step_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", name = :btrtrlsr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", turn = :btrtrlsr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i
													+ " where id = :btrtrlsr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ";\n");
										sqlParams.put("btrtrlsr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, stepRelId);
										sqlParams.put("btrtrlsr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, transTypeRelId);
										String licenseStepId = licenseStepRel.getString("license_step_id");
										if (newStepIds.containsKey(licenseStepId))
											sqlParams.put("btrtrlsr_license_step_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, newStepIds.get(licenseStepId));
										else
											sqlParams.put("btrtrlsr_license_step_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, licenseStepId);
										sqlParams.put("btrtrlsr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i, licenseStepRel.getString("name"));
										sqlParams.put("btrtrlsr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i, i);
									}
								}
								//更新交易方式下属多指标
								List quotas = (List) itemTransTypeRel.get("quotas");
								if (quotas == null || quotas.size() == 0) {
									if (!blnNew) {
										deleteSql.append("delete from trans_multi_trade_rel where trans_type_rel_id = :btrtr_id_" + intRow + "_" + intTransTypeRelRow + ";\n");
									}
								} else {
									if (!blnNew && originalTransTypeRel != null) {
										//检查是否有需要删除的多指标
										deleteIds.clear();
										List originalMultiTradeRels = (List) originalTransTypeRel.get("quotas");
										if (originalMultiTradeRels != null && originalMultiTradeRels.size() > 0) {
											for(int i = 0; i < originalMultiTradeRels.size(); i++) {
												ParaMap originalMultiTradeRel = (ParaMap) originalMultiTradeRels.get(i);
												if (originalMultiTradeRel != null && originalMultiTradeRel.containsKey("id")
														&& StrUtils.isNotNull(originalMultiTradeRel.getString("id"))) {
													String multiTradeRelId = originalMultiTradeRel.getString("id");
													boolean blnExists = false;
													for(int j = 0; j < quotas.size(); j++) {
														ParaMap multiTradeRel = (ParaMap) quotas.get(j);
														if (multiTradeRel != null && multiTradeRel.containsKey("id")
																&& StrUtils.isNotNull(multiTradeRel.getString("id"))
																&& multiTradeRel.getString("id").equalsIgnoreCase(multiTradeRelId)) {
															blnExists = true;
															break;
														}
													}
													if (!blnExists)
														deleteIds.add(multiTradeRelId);
												}
											}
											if (deleteIds.size() > 0) {
												StringBuffer deleteIdParams = new StringBuffer();
												for(int i = 0; i < deleteIds.size(); i++) {
													deleteIdParams.append(":del_btrtrmtr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ",");
													sqlParams.put("del_btrtrmtr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, deleteIds.get(i));
												}
												deleteIdParams.deleteCharAt(deleteIdParams.length() - 1);
												deleteSql.append("delete from trans_multi_trade_rel where id in (" + deleteIdParams.toString() + ");\n");
											}
										}
									}
									for(int i = 0; i < quotas.size(); i++) {
										ParaMap multiTradeRel = (ParaMap) quotas.get(i);
										String multiTradeRelId = multiTradeRel.getString("id");
										if (StrUtils.isNull(multiTradeRelId) || multiTradeRelId.startsWith(newRecordIdPrefix)) {
											updateSql.append("insert into trans_multi_trade_rel(id, trans_type_rel_id, multi_trade_id, name, turn)"
													+ " values(:btrtrmtr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrmtr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrmtr_multi_trade_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrmtr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", :btrtrmtr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i + ");\n");
											multiTradeRelId = IDGenerator.generatorID("trans_multi_trade_rel");
										} else
											updateSql.append("update trans_multi_trade_rel set trans_type_rel_id = :btrtrmtr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", multi_trade_id = :btrtrmtr_multi_trade_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", name = :btrtrmtr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i + ", turn = :btrtrmtr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i
													+ " where id = :btrtrmtr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i + ";\n");
										sqlParams.put("btrtrmtr_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, multiTradeRelId);
										sqlParams.put("btrtrmtr_trans_type_rel_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, transTypeRelId);
										String multiTradeId = multiTradeRel.getString("multi_trade_id");
										if (newQuotaIds.containsKey(multiTradeId))
											sqlParams.put("btrtrmtr_multi_trade_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, newQuotaIds.get(multiTradeId));
										else
											sqlParams.put("btrtrmtr_multi_trade_id_" + intRow + "_" + intTransTypeRelRow + "_" + i, multiTradeId);
										sqlParams.put("btrtrmtr_name_" + intRow + "_" + intTransTypeRelRow + "_" + i, multiTradeRel.getString("name"));
										sqlParams.put("btrtrmtr_turn_" + intRow + "_" + intTransTypeRelRow + "_" + i, i);
									}
								}
								intTransTypeRelRow++;
							}
						}
						intRow++;
					}
				}
			}
		}
		//保存到数据库
		if (deleteSql.length() > 0) {
			dataSetDao.executeSQL("begin\n" + deleteSql.toString() + "end;", sqlParams);
		}
		if (updateSql.length() > 0) {
			dataSetDao.executeSQL("begin\n" + updateSql.toString() + "end;", sqlParams);
		}
		//将缓存删除
		TRANS_RULE_CONFIG = null;
		ALL_BUSINESS_TYPE = null;
		ALL_BUSINESS_TYPE_FULL = null;
		SUPPORT_BUSINESS_TYPE = null;
		ParaMap result = new ParaMap();
		result.put("state", 1);
		return result;
	}
	
	/**
	 * 按业务类型返回所有设置内容，所有引用的字典的内容全部加载过来
	 * @param businessType 业务类型
	 * @return 返回结构和template.txt内容格式一致，仅包含指定businessType节点内容。并且引用属性全部替换为字典中的内容
	 * @throws Exception
	 */
	public static ParaMap getBusinessTypeConfig(String businessType) throws Exception {
		if (StrUtils.isNull(businessType))
			return null;
		ParaMap transRuleConfig = getTransRuleConfig();
		if (transRuleConfig == null)
			return null;
		ParaMap dictionariesData = transRuleConfig.containsKey("dictionaries") ? (ParaMap) transRuleConfig.get("dictionaries") : null;
		ParaMap goodsTypesDictData = dictionariesData == null ? null : (ParaMap) dictionariesData.getDataByPath("goodsTypes");
		ParaMap businessTypesDictData = dictionariesData == null ? null : (ParaMap) dictionariesData.getDataByPath("businessTypes");
		ParaMap transTypesDictData = dictionariesData == null ? null : (ParaMap) dictionariesData.getDataByPath("transTypes");
		if (transRuleConfig.containsKey("businessTypes")) {
			ParaMap businessTypesData = (ParaMap) transRuleConfig.get("businessTypes");
			if (businessTypesData.containsKey(businessType)) {
				ParaMap businessTypeData = (ParaMap) businessTypesData.get(businessType);
				ParaMap goodsTypeDictData = goodsTypesDictData == null ? null : (ParaMap) goodsTypesDictData.get(businessTypeData.getString("goods_type_id"));
				ParaMap businessTypeDictData = businessTypesDictData == null ? null : (ParaMap) businessTypesDictData.get(businessTypeData.getString("business_type_id"));
				ParaMap result = new ParaMap();
				Iterator itBusinessTypeData = businessTypeData.keySet().iterator();
				while (itBusinessTypeData.hasNext()) {
					String key = itBusinessTypeData.next().toString();
					Object value = businessTypeData.get(key);
					if (value instanceof ParaMap) {//为Map对象的则是交易方式节点
						ParaMap transTypeRelData = (ParaMap) value;
						Iterator itTransTypeRelData = transTypeRelData.keySet().iterator();
						while (itTransTypeRelData.hasNext()) {
							String keyTransTypeRelData = itTransTypeRelData.next().toString();
							Object valueTransTypeRelData = transTypeRelData.get(keyTransTypeRelData);
							if (valueTransTypeRelData instanceof List) {//关联的竞买申请步骤、多指标
								List list = (List) valueTransTypeRelData;
								ParaMap dict = (ParaMap) dictionariesData.get(keyTransTypeRelData);
								if (dict != null) {
									String relIdFieldName = keyTransTypeRelData.equalsIgnoreCase("quotas") ? "multi_trade_id" : "license_step_id";
									String relNameFieldName = keyTransTypeRelData.equalsIgnoreCase("quotas") ? "multi_trade_name" : "license_step_name";
									for(int i = 0; i < list.size(); i++) {
										ParaMap listItem = (ParaMap) list.get(i);
										ParaMap listItemDictData = (ParaMap) dict.get(listItem.getString(relIdFieldName));
										Iterator itListItemDictData = listItemDictData.keySet().iterator();
										while (itListItemDictData.hasNext()) {
											String keyListItemDictData = itListItemDictData.next().toString();
											if (!listItem.containsKey(keyListItemDictData))
												listItem.put(keyListItemDictData, listItemDictData.get(keyListItemDictData));
										}
										listItem.put(relNameFieldName, listItemDictData.get("name"));
									}
								}
							}
						}
						ParaMap transTypeDictData = transTypesDictData == null ? null : (ParaMap) transTypesDictData.get(transTypeRelData.getString("trans_type_id"));
						if (transTypeDictData != null) {
							Iterator itTransTypeDict = transTypeDictData.keySet().iterator();
							while (itTransTypeDict.hasNext()) {
								String keyTransTypeDict = itTransTypeDict.next().toString();
								if (!transTypeRelData.containsKey(keyTransTypeDict))
									transTypeRelData.put(keyTransTypeDict, transTypeDictData.get(keyTransTypeDict));
							}
							transTypeRelData.put("trans_type_name", transTypeDictData.get("name"));
						}
						result.put(key, transTypeRelData);
					} else
						result.put(key, value);
				}
				//添加businessTypeRel引用的goodsType、businessType的数据
				if (goodsTypeDictData != null && StrUtils.isNotNull(businessTypeData.getString("goods_type_id"))) {
					Iterator it = goodsTypeDictData.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next().toString();
						if (!result.containsKey(key))
							result.put(key, goodsTypeDictData.get(key));
					}
					result.put("goods_type_name", goodsTypeDictData.get("name"));
				}
				if (businessTypeDictData != null && StrUtils.isNotNull(businessTypeData.getString("business_type_id"))) {
					Iterator it = businessTypeDictData.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next().toString();
						if (!result.containsKey(key))
							result.put(key, businessTypeDictData.get(key));
					}
					result.put("business_type_name", businessTypeDictData.get("name"));
				}
				return result;
			} else
				return null;
		} else
			return null;
	}
	
	public Object getTradeRuleXMLConfig(boolean returnList) throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/com/tradeland/engine/TradeRule.xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null){
			buffer.append(line);
		}
		Document document =  DocumentHelper.parseText(buffer.toString());
		Element root = document.getRootElement();
		List nodes = root.elements();
		if (nodes.size() > 0) {
			ParaMap resultMap = new ParaMap();
			List resultList = new ArrayList();
			for(int i = 0; i < nodes.size(); i++) {
				Element node = (Element) nodes.get(i);
				ParaMap item = new ParaMap();
				item.put("id", node.attributeValue("id"));
				item.put("name", node.attributeValue("name"));
				resultList.add(item);
				resultMap.put(node.attributeValue("id"), node.attributeValue("name"));
			}
			if (returnList)
				return resultList;
			else
				return resultMap;
		} else
			return null;
	}
	/**
	 * 获取耕指业务流程
	 * @param returnList
	 * @return
	 * @throws Exception
	 */
	public Object getTradeRuleXMLConfigPlow(boolean returnList) throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/com/tradeplow/engine/TradeRule.xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null){
			buffer.append(line);
		}
		Document document =  DocumentHelper.parseText(buffer.toString());
		Element root = document.getRootElement();
		List nodes = root.elements();
		if (nodes.size() > 0) {
			ParaMap resultMap = new ParaMap();
			List resultList = new ArrayList();
			for(int i = 0; i < nodes.size(); i++) {
				Element node = (Element) nodes.get(i);
				ParaMap item = new ParaMap();
				item.put("id", node.attributeValue("id"));
				item.put("name", node.attributeValue("name"));
				resultList.add(item);
				resultMap.put(node.attributeValue("id"), node.attributeValue("name"));
			}
			if (returnList)
				return resultList;
			else
				return resultMap;
		} else
			return null;
	}
	
	public static void main(String[] args) throws Exception {
		List<String> test = new ArrayList();
		test.add("abc");
		test.add("efg");
		test.add("xyz");
		String efg = "xyz";
		System.out.println(test.contains(efg));
	}
}
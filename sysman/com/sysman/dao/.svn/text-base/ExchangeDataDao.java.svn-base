package com.sysman.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class ExchangeDataDao extends BaseDao {

	private Element addTextElement(Element parent, String name, String text,
			boolean writeCData) {
		Element result = parent.addElement(name);
		if (text != null && !text.equals("") && !text.equalsIgnoreCase("null")) {
			if (writeCData) {
				// DefaultCDATA cdata = new DefaultCDATA(text);
				// result.add(cdata);
				result.addCDATA(text);
			} else
				result.setText(text);
		}
		return result;
	}

	private Element addTextElement(Element parent, String name, String text) {
		return addTextElement(parent, name, text, false);
	}

	private void putRecordData(ParaMap data, String fieldName, String fieldValue) {
		if (data != null && StrUtils.isNotNull(fieldName)
				&& StrUtils.isNotNull(fieldValue))
			data.put(fieldName, fieldValue);
	}

	/**
	 * 记录导出标的历史记录
	 * 
	 * @param targetArea
	 *            标的数据范围
	 * @return
	 * @throws Exception
	 */
	public ParaMap recordSendData(String targetArea) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		data.put("id", null);
		data.put("status", 2);
		ParaMap result = dataSetDao.updateData("sys_exchange_send", "id", data);
		if (result.getInt("state") == 1) {
			String sendId = result.getString("id");
			String sql = "insert into sys_exchange_send_dtl(id, send_id, ref_table_name, ref_id)"
					+ " select create_table_id('sys_exchange_send_dtl'), '"
					+ sendId
					+ "', 'trans_target', id from trans_target where id in ("
					+ targetArea + ")";
			result = dataSetDao.executeSQL(sql, null);
		}
		return result;
	}

	/**
	 * 生成同步数据
	 * 
	 * @param params
	 *            同步数据范围<br/>
	 *            键syncModuleData、syncDataSetData、syncResourceData表示同步的数据范围，
	 *            记录到XML中的数据需要同步其中的模块、数据集、字符串等其中之一或者多个、全部
	 *            键syncModuleId、syncDataSetId
	 *            、syncResourceId表示需要同步的数据ID，仅一个生效，并且需要与上面的三个键值匹配
	 * @return
	 * @throws Exception
	 */
	public InputStream getSyncData(ParaMap params) throws Exception {
		String syncModuleId = params.getString("syncModuleId");
		String syncDataSetId = params.getString("syncDataSetId");
		String syncResourceId = params.getString("syncResourceId");
		boolean syncModuleData = params.getString("syncModuleData").equals("1");
		boolean syncDataSetData = params.getString("syncDataSetData").equals(
				"1");
		boolean syncResourceData = params.getString("syncResourceData").equals(
				"1");
		if (!(syncModuleData || syncDataSetData || syncResourceData)) {// 未选择任何同步的数据
			return null;
		}
		String sqlModule = null;
		String sqlDataSet = null;
		String sqlResource = null;
		ParaMap sqlModuleParams = null;
		ParaMap sqlDataSetParams = null;
		ParaMap sqlResourceParams = null;
		if (StrUtils.isNotNull(syncModuleId)
				|| StrUtils.isNotNull(syncDataSetId)
				|| StrUtils.isNotNull(syncResourceId)) {
			// 按选择节点
			if (StrUtils.isNotNull(syncModuleId)) {
				// 模块所有下级节点以及上级链路上的父节点
				sqlModule = "select * from ("
						+ "select sys_module.*, '0_' || (1000 - level) as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id = :module_id"
						+ " union select sys_module.*, '1_' || level as module_level from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = :module_id"
						+ ") order by module_level, turn";
				sqlDataSet = "select * from sys_dataset where module_id = :module_id or module_id in (select id from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = :module_id)";
				sqlResource = "select * from sys_resource where module_id = :module_id or module_id in (select id from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = :module_id)";
				sqlModuleParams = new ParaMap();
				sqlModuleParams.put("module_id", syncModuleId);
				sqlDataSetParams = new ParaMap();
				sqlDataSetParams.put("module_id", syncModuleId);
				sqlResourceParams = new ParaMap();
				sqlResourceParams.put("module_id", syncModuleId);
			} else if (StrUtils.isNotNull(syncDataSetId)) {
				sqlModule = "select * from ("
						+ "select sys_module.*, level as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id in (select module_id from sys_dataset where id = :dataset_id)"
						+ ") order by module_level desc, turn";
				sqlDataSet = "select * from sys_dataset where id = :dataset_id";
				sqlModuleParams = new ParaMap();
				sqlModuleParams.put("dataset_id", syncDataSetId);
				sqlDataSetParams = new ParaMap();
				sqlDataSetParams.put("dataset_id", syncDataSetId);
			} else if (StrUtils.isNotNull(syncResourceId)) {
				sqlModule = "select * from ("
						+ "select sys_module.*, level as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id in (select module_id from sys_resource where id = :resource_id)"
						+ ") order by module_level desc, turn";
				sqlResource = "select * from sys_resource where id = :resource_id";
				sqlModuleParams = new ParaMap();
				sqlModuleParams.put("resource_id", syncResourceId);
				sqlResourceParams = new ParaMap();
				sqlResourceParams.put("resource_id", syncResourceId);
			}
		} else {
			sqlModule = "select sys_module.*, level as module_level from sys_module CONNECT BY parent_id = PRIOR id start with parent_id is null"
					+ " order by module_level, turn";
			sqlDataSet = "select * from sys_dataset";
			sqlResource = "select * from sys_resource";
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap moduleData = dataSetDao.queryData(sqlModule, sqlModuleParams);
		ParaMap dataSetData = syncDataSetData && StrUtils.isNotNull(sqlDataSet) ? dataSetDao
				.queryData(sqlDataSet, sqlDataSetParams) : null;
		ParaMap resourceData = syncResourceData
				&& StrUtils.isNotNull(sqlResource) ? dataSetDao.queryData(
				sqlResource, sqlResourceParams) : null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = DateUtils.now();
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element rootElement = document.addElement("datas");
		rootElement.addAttribute("sync_date", sdf.format(now));
		// 导出模块
		Element modulesElement = rootElement.addElement("modules");
		modulesElement.addAttribute("sync_module", syncModuleData ? "1" : "0");
		modulesElement
				.addAttribute("sync_dataset", syncDataSetData ? "1" : "0");
		modulesElement.addAttribute("sync_resource", syncResourceData ? "1"
				: "0");
		for (int i = 0; i < moduleData.getSize(); i++) {
			String moduleId = String
					.valueOf(moduleData.getRecordValue(i, "id"));
			Element moduleElement = modulesElement.addElement("module");
			moduleElement.addAttribute("id", moduleId);
			moduleElement.addAttribute("no",
					String.valueOf(moduleData.getRecordValue(i, "no")));
			moduleElement.addAttribute("name",
					String.valueOf(moduleData.getRecordValue(i, "name")));
			// moduleElement.addAttribute("menu_id",
			// String.valueOf(moduleData.getRecordValue(i, "menu_id")));//菜单无法导入
			moduleElement.addAttribute("class_name",
					String.valueOf(moduleData.getRecordValue(i, "class_name")));
			moduleElement.addAttribute("parent_id",
					String.valueOf(moduleData.getRecordValue(i, "parent_id")));
			moduleElement.addAttribute("is_valid",
					String.valueOf(moduleData.getRecordValue(i, "is_valid")));
			moduleElement.addAttribute("remark",
					String.valueOf(moduleData.getRecordValue(i, "remark")));
			if (dataSetData != null) {
				Element dataSetsElement = moduleElement.addElement("dataSets");
				for (int j = 0; j < dataSetData.getSize(); j++) {
					if (String.valueOf(
							dataSetData.getRecordValue(j, "module_id"))
							.equalsIgnoreCase(moduleId)) {
						// Element dataSetElement =
						// dataSetsElement.addElement("dataSet");
						Element dataSetElement = addTextElement(
								dataSetsElement, "dataSet",
								String.valueOf(dataSetData.getRecordValue(j,
										"sql")), true);
						dataSetElement.addAttribute("id", String
								.valueOf(dataSetData.getRecordValue(j, "id")));
						dataSetElement.addAttribute("no", String
								.valueOf(dataSetData.getRecordValue(j, "no")));
						dataSetElement
								.addAttribute("name", String
										.valueOf(dataSetData.getRecordValue(j,
												"name")));
						dataSetElement.addAttribute("module_id", String
								.valueOf(dataSetData.getRecordValue(j,
										"module_id")));
						// dataSetElement.addAttribute("sql",
						// String.valueOf(dataSetData.getRecordValue(j,
						// "sql")));
						dataSetElement.addAttribute("is_valid", String
								.valueOf(dataSetData.getRecordValue(j,
										"is_valid")));
						dataSetElement.addAttribute("remark", String
								.valueOf(dataSetData
										.getRecordValue(j, "remark")));
					}
				}
			}
			if (resourceData != null) {
				Element resourcesElement = moduleElement
						.addElement("resources");
				for (int j = 0; j < resourceData.getSize(); j++) {
					if (String.valueOf(
							resourceData.getRecordValue(j, "module_id"))
							.equalsIgnoreCase(moduleId)) {
						// Element resourceElement =
						// resourcesElement.addElement("resource");
						Element resourceElement = addTextElement(
								resourcesElement, "resource",
								String.valueOf(resourceData.getRecordValue(j,
										"content")), true);
						resourceElement.addAttribute("id", String
								.valueOf(resourceData.getRecordValue(j, "id")));
						resourceElement.addAttribute("no", String
								.valueOf(resourceData.getRecordValue(j, "no")));
						resourceElement.addAttribute("name",
								String.valueOf(resourceData.getRecordValue(j,
										"name")));
						resourceElement.addAttribute("module_id", String
								.valueOf(resourceData.getRecordValue(j,
										"module_id")));
						// resourceElement.addAttribute("content",
						// String.valueOf(resourceData.getRecordValue(j,
						// "content")));
						resourceElement.addAttribute("is_valid", String
								.valueOf(resourceData.getRecordValue(j,
										"is_valid")));
						resourceElement.addAttribute("remark", String
								.valueOf(resourceData.getRecordValue(j,
										"remark")));
					}
				}
			}
		}

		// OutputFormat format = OutputFormat.createPrettyPrint();
		// format.setEncoding("utf-8");
		// OutputStreamWriter fw = new OutputStreamWriter(new
		// FileOutputStream("e:\\test_sync1111.xml"), "UTF-8");
		// XMLWriter xmlWriter = new XMLWriter(fw, format);
		// xmlWriter.write(document);
		// xmlWriter.close();

		return new ByteArrayInputStream(document.asXML().getBytes("utf-8"));
	}

	/**
	 * 发送同步数据到目标系统
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ParaMap syncRemoteData(ParaMap params) throws Exception {
		ParaMap result = new ParaMap();
		String syncRemoteUrl = params.getString("syncRemoteUrl");
		if (StrUtils.isNull(syncRemoteUrl)) {
			result.put("state", 0);
			result.put("message", "无有效远程系统目标URL");
			return result;
		}
		if (!syncRemoteUrl.substring(syncRemoteUrl.length() - 1).equals("/"))
			syncRemoteUrl += "/";
		syncRemoteUrl += "data?module=sysman&service=ExchangeData&u=0001&method=receiveSyncData";
		InputStream xml = getSyncData(params);
		if (xml == null) {
			result.put("state", 0);
			result.put("message", "未选择任何同步的数据");
			return result;
		}
		return result;
	}

	/**
	 * 接收同步数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public ParaMap syncData(String xml) throws Exception {
		log.debug("同步数据开始导入...");
		ParaMap result = null;
		ParaMap format = new ParaMap();
		Map<String, String> moduleIdMap = new HashMap<String, String>();
		int intModuleCount = 0;
		int intDataSetCount = 0;
		int intResourceCount = 0;
		int intModuleSyncCount = 0;
		int intDataSetSyncCount = 0;
		int intResourceSyncCount = 0;
		DataSetDao dataSetDao = new DataSetDao();
		try {
			ParaMap data = new ParaMap();
			Document document = DocumentHelper.parseText(xml);
			Element rootElement = document.getRootElement();
			// 导入模块
			Element modulesElement = rootElement.element("modules");
			boolean syncModuleData = modulesElement.attributeValue(
					"sync_module").equals("1");
			boolean syncDataSetData = modulesElement.attributeValue(
					"sync_dataset").equals("1");
			boolean syncResourceData = modulesElement.attributeValue(
					"sync_resource").equals("1");
			if (modulesElement != null) {
				log.debug("开始导入模块...");
				List modules = modulesElement.elements("module");
				if (modules != null && modules.size() > 0) {
					intModuleCount = modules.size();
					for (int i = 0; i < modules.size(); i++) {
						String moduleId = null;
						Element moduleElement = (Element) modules.get(i);
						if (syncModuleData) {
							data.clear();
							putRecordData(data, "id",
									moduleElement.attributeValue("id"));
							putRecordData(data, "no",
									moduleElement.attributeValue("no"));
							putRecordData(data, "name",
									moduleElement.attributeValue("name"));
							// putRecordData(data, "menu_id",
							// moduleElement.attributeValue("menu_id"));//菜单无法导入
							putRecordData(data, "class_name",
									moduleElement.attributeValue("class_name"));
							if (moduleIdMap.containsKey(moduleElement
									.attributeValue("parent_id")))
								putRecordData(data, "parent_id",
										moduleIdMap.get(moduleElement
												.attributeValue("parent_id")));
							else
								putRecordData(data, "parent_id",
										moduleElement
												.attributeValue("parent_id"));
							putRecordData(data, "is_valid",
									moduleElement.attributeValue("is_valid"));
							putRecordData(data, "remark",
									moduleElement.attributeValue("remark"));
							if (StrUtils.isNull(data.getString("no"))) {
								result = dataSetDao.updateData("sys_module",
										"id", data);
							} else
								result = dataSetDao.updateData("sys_module",
										"id", "no", data);
							if (result.getInt("state") == 1) {
								intModuleSyncCount++;
								moduleId = result.getString("id");
								if (StrUtils.isNotNull(moduleId)
										&& !moduleId
												.equalsIgnoreCase(moduleElement
														.attributeValue("id")))
									moduleIdMap.put(
											moduleElement.attributeValue("id"),
											moduleId);
							}
						}
						if (StrUtils.isNull(moduleId)) {
							data.clear();
							data.put("no", moduleElement.attributeValue("no"));
							result = dataSetDao.querySimpleData("sys_module",
									data);
							if (result.getInt("state") == 1
									&& result.getInt("totalRowCount") > 0)
								moduleId = String.valueOf(result
										.getRecordValue(0, "id"));
							else {
								result.clear();
								result.put("state", 0);
								result.put(
										"message",
										"模块 "
												+ moduleElement
														.attributeValue("name")
												+ " 目标系统未查询到，放弃导入该模块相关数据");
								// throw new Exception("模块 " +
								// moduleElement.attributeValue("name") +
								// " 目标系统未查询到，放弃导入该模块");
								continue;
							}
						}
						if (syncDataSetData) {
							Element dataSetsElement = moduleElement
									.element("dataSets");
							if (dataSetsElement != null) {
								log.debug("开始导入模块"
										+ moduleElement.attributeValue("name")
										+ "的数据集...");
								List dataSets = dataSetsElement
										.elements("dataSet");
								if (dataSets != null && dataSets.size() > 0) {
									intDataSetCount += dataSets.size();
									for (int j = 0; j < dataSets.size(); j++) {
										Element dataSetElement = (Element) dataSets
												.get(j);
										data.clear();
										putRecordData(data, "id",
												dataSetElement
														.attributeValue("id"));
										putRecordData(data, "no",
												dataSetElement
														.attributeValue("no"));
										putRecordData(data, "name",
												dataSetElement
														.attributeValue("name"));
										putRecordData(data, "module_id",
												moduleId);
										putRecordData(data, "sql",
												dataSetElement.getText());
										putRecordData(
												data,
												"is_valid",
												dataSetElement
														.attributeValue("is_valid"));
										putRecordData(
												data,
												"remark",
												dataSetElement
														.attributeValue("remark"));
										result = dataSetDao.updateData(
												"sys_dataset", "id",
												"module_id;no", data);
										if (result.getInt("state") == 1)
											intDataSetSyncCount++;
									}
								}
							}
						}
						if (syncResourceData) {
							Element resourcesElement = moduleElement
									.element("resources");
							if (resourcesElement != null) {
								log.debug("开始导入模块"
										+ moduleElement.attributeValue("name")
										+ "的字符串资源...");
								List resources = resourcesElement
										.elements("resource");
								if (resources != null && resources.size() > 0) {
									intResourceCount += resources.size();
									for (int j = 0; j < resources.size(); j++) {
										Element resourceElement = (Element) resources
												.get(j);
										data.clear();
										putRecordData(data, "id",
												resourceElement
														.attributeValue("id"));
										putRecordData(data, "no",
												resourceElement
														.attributeValue("no"));
										putRecordData(data, "name",
												resourceElement
														.attributeValue("name"));
										putRecordData(data, "module_id",
												moduleId);
										putRecordData(data, "content",
												resourceElement.getText());
										putRecordData(
												data,
												"is_valid",
												resourceElement
														.attributeValue("is_valid"));
										putRecordData(
												data,
												"remark",
												resourceElement
														.attributeValue("remark"));
										result = dataSetDao.updateData(
												"sys_resource", "id",
												"module_id;no", data);
										if (result.getInt("state") == 1)
											intResourceSyncCount++;
									}
								}
							}
						}
					}
				}
			}
			if (result == null)
				result = new ParaMap();
			else
				result.clear();
			result.put("state", 1);
			result.put("message", "同步数据导入成功。导入数据：模块(" + intModuleCount + "/"
					+ intModuleSyncCount + ")," + "数据集(" + intDataSetCount
					+ "/" + intDataSetSyncCount + ")," + "字符串资源("
					+ intResourceCount + "/" + intResourceSyncCount + ").");
		} catch (Exception e) {
			result.put("state", 0);
			result.put("message", "同步数据导入失败!");
			log.error("同步数据导入失败");
			throw e;
		} finally {

		}
		return result;
	}

}
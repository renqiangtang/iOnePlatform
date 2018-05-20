package com.base.dao;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.base.utils.ParaMap;
import com.base.web.AppConfig;

public class DataSetDao extends BaseDataSetDao {

	private static Element rootElement;

	public static boolean offline() {
		String getsql = AppConfig.getPro("getsql");
		if ("offline".equals(getsql))
			return true;
		else
			return false;

	}

	public static Element getRootElement() throws Exception {
		if (rootElement == null) {
			InputStream in = DataSetDao.class.getResourceAsStream("/ds.xml");
			SAXReader reader = new SAXReader();
			Document document = reader.read(in);
			rootElement = document.getRootElement();
		}
		return rootElement;
	}

	public ParaMap getDataSetSQL(String moduleId, String dataSetNo)
			throws Exception {
		if (offline()) {
			ParaMap map = new ParaMap();
			String xpath = "/datas/modules/module[@id='" + moduleId
					+ "']/dataSets/dataSet[@no='" + dataSetNo + "']";
			List<Element> list = getRootElement().selectNodes(xpath);
			if (list.size() == 1) {
				map.put("sql0", list.get(0).getText());
				map.put("sqlCount", 1);
			} else {
				throw new Exception("moduleId:" + moduleId + " dataSetNo:"
						+ dataSetNo + " 获取数据集出错(" + list.size() + ")");
			}
			return map;
		} else
			return super.getDataSetSQL(moduleId, dataSetNo);
	}

	public ParaMap getDataSetSQLByModuleNo(String moduleNo, String dataSetNo)
			throws Exception {
		if (offline()) {
			ParaMap map = new ParaMap();
			String xpath = "/datas/modules/module[@no='" + moduleNo
					+ "']/dataSets/dataSet[@no='" + dataSetNo + "']";
			List<Element> list = getRootElement().selectNodes(xpath);
			if (list.size() == 1) {
				map.put("sql0", list.get(0).getText());
				map.put("sqlCount", 1);
			} else {
				throw new Exception("moduleNo:" + moduleNo + " dataSetNo:"
						+ dataSetNo + " 获取数据集出错(" + list.size() + ")");
			}
			return map;
		} else
			return super.getDataSetSQLByModuleNo(moduleNo, dataSetNo);
	}

	public ParaMap getDataSetSQLById(String dataSetId) throws Exception {
		if (offline()) {
			ParaMap map = new ParaMap();
			String xpath = "/datas/modules/module/dataSets/dataSet[@id='"
					+ dataSetId + "']";
			List<Element> list = getRootElement().selectNodes(xpath);
			if (list.size() == 1) {
				map.put("sql0", list.get(0).getText());
				map.put("sqlCount", 1);
			} else {
				throw new Exception("dataSetId:" + dataSetId + " 获取数据集出错("
						+ list.size() + ")");
			}
			return map;
		} else
			return super.getDataSetSQLById(dataSetId);
	}

	public static void main(String[] args) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap inMap = new ParaMap();
		inMap.put("moduleNo", "trans_land_portal");
		inMap.put("dataSetNo", "query_all_trans_target_list");
		ParaMap outMap = dao.getDataSetSQL(inMap);
		System.out.println(outMap);
	}

}
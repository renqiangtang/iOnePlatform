package com.sysman.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.base.dao.BeanDao;
import com.base.service.BaseService;
import com.base.utils.FsUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.XMLUtils;
import com.sysman.bean.Sys_Extend;
import com.sysman.bean.Trans_Notice;
import com.sysman.bean.Trans_Target;
import com.sysman.bean.Trans_goods;

/**
 * 
 * @author danqing.luo
 * 
 */
public class UploadDataService extends BaseService
{
	public File getFile(String home, String path)
	{
		return new File(FsUtils.getFileRoot() + "/" + home + "/" + path);
	}

	public Document getXMLDOC(String home, String path)
	        throws DocumentException
	{
		SAXReader saxreader = new SAXReader();
		return saxreader.read(getFile(home, path));

	}

	/**
	 * 处理上传上来的宗地信息数据文件
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public ParaMap uploaddataGoods(ParaMap param) throws Exception
	{
		ParaMap out = new ParaMap();
		String longid = IDGenerator.newGUID();
		String fileName = param.getString("txtFile_Name");
		FileItem fileItem = param.getFileItem("txtFile_Item");
		String orgId = param.getString("organId");
		String home = param.getString("home");
		String path = param.getString("path");
		String fileType = fileName.substring(fileName.lastIndexOf("."),
		        fileName.length());
		File file = getFile(home, path + "/" + longid + fileType);
		if (!file.exists())
			FileUtils.writeStringToFile(file, "");
		fileItem.write(file);
		Document doc = getXMLDOC(home, path + "/" + longid + fileType);

		BeanDao beanDao = new BeanDao();
		// 装载宗地信息
		ParaMap xmlParaMap = new ParaMap();
		XMLUtils.parseXML(doc.asXML(), xmlParaMap);
		xmlParaMap = (ParaMap) xmlParaMap.get("crfasx");
		System.out.println(xmlParaMap);
		Trans_goods goods = new Trans_goods();
		goods.setGoods_type("101");
		goods.create_user_id = file.getName();
		goods.setGoods_use(xmlParaMap.getString("GHTJ_GHYT"));
		goods.setBlemish("");
		goods.id = xmlParaMap.getString("id") == null ? IDGenerator.newGUID()
		        : xmlParaMap.getString("id");
		goods.setName(doc.getRootElement().attributeValue("name"));
		goods.setNo(xmlParaMap.getString("ZDBH"));
		goods.create_organ_id = orgId;
		// transGoodsDao.updateExtend("trans_goods", goods.id ,
		// "industry_type",
		// industry_type, "产业类别");
		// 扩展信息
		List<Sys_Extend> list = new ArrayList<Sys_Extend>();
		list.add(new Sys_Extend("trans_goods", goods.id, "provide_date",
		        xmlParaMap.getString("CRYQ_GDSJ"), "供地时间", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "use_years",
		        xmlParaMap.getString("CRYQ_CRNQ"), "使用年限", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "media", xmlParaMap
		        .getString("CRYQ_GGMT"), "公告媒体", String.valueOf(IDGenerator
		        .newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "develop", xmlParaMap
		        .getString("JBQK_KFCD"), "开发强度", String.valueOf(IDGenerator
		        .newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "land_area",
		        xmlParaMap.getString("JBQK_CRMJ"), "出让面积", String
		                .valueOf(IDGenerator.newGUID())));
		// transGoodsDao.updateExtend("trans_goods", goods.id,
		// "build_height",
		// build_height, "建筑高度");
		list.add(new Sys_Extend("trans_goods", goods.id, "plot1_down",
		        xmlParaMap.getString("GHTJ_DSRJL_XX"), "地上容积率 down", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "plot1_flag",
		        xmlParaMap.getString("GHTJ_DSRJL_OP"), "地上容积率操作符", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "plot1_up", xmlParaMap
		        .getString("GHTJ_DSRJL_SX"), "地上容积率 up", String
		        .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "build_density_down",
		        xmlParaMap.getString("GHTJ_JZMD_XX"), "建筑密度 down", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "build_density_flag",
		        xmlParaMap.getString("GHTJ_JZMD_OP"), "建筑密度操作符", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "build_density_up",
		        xmlParaMap.getString("GHTJ_JZMD_SX"), "建筑密度 up", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "plot2_down",
		        xmlParaMap.getString("GHTJ_DXRJL_XX"), "地下容积率 down", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "plot2_flag",
		        xmlParaMap.getString("GHTJ_JZMD_OP"), "地下容积率操作符", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "plot2_up", xmlParaMap
		        .getString("GHTJ_DXRJL_SX"), "地下容积率 up", String
		        .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "green_ratio_down",
		        xmlParaMap.getString("GHTJ_LDL_XX"), "绿化率 down", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "green_ratio_flag",
		        xmlParaMap.getString("绿地率_操作符"), "绿化率 flag", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "green_ratio_up",
		        xmlParaMap.getString("绿地率_上限"), "绿化率 up", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "stop_ratio",
		        xmlParaMap.getString("GHTJ_TCL"), "停车率", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "stop_ratio_down",
		        xmlParaMap.getString("GHTJ_TCL_XX"), "停车率 down", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "stop_ratio_flag",
		        xmlParaMap.getString("GHTJ_TCL_OP"), "停车率 flag", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "stop_ratio_up",
		        xmlParaMap.getString("GHTJ_TCL_SX"), "停车率 up", String
		                .valueOf(IDGenerator.newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "startRequired",
		        xmlParaMap.getString(""), "开工要求", String.valueOf(System
		                .currentTimeMillis())));
		list.add(new Sys_Extend("trans_goods", goods.id, "range", xmlParaMap
		        .getString("JBQK_SZFW"), "四至范围", String.valueOf(IDGenerator
		        .newGUID())));
		list.add(new Sys_Extend("trans_goods", goods.id, "otherCondition",
		        xmlParaMap.getString("GHTJ_QTTJ"), "其它条件", String
		                .valueOf(IDGenerator.newGUID())));
		Iterator it = doc.getRootElement().element("crfadk").elementIterator();
		while (it.hasNext())
		{
			Element el = (Element) it.next();
			list.addAll(XMLUtils.parseXMLListXY(el.asXML(), goods.id)); // 坐标信息
		}
		goods.sys_extend = list;
		beanDao.saveBean(goods); // 宗地基本信息
		out.put("state", 1);
		return out;
	}

	/**
	 * 公告交互文件(交易前)
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap uploadNoticeBefore(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		String longid = IDGenerator.newGUID();
		String fileName = inMap.getString("txtFile1_Name");
		FileItem fileItem = inMap.getFileItem("txtFile1_Item");
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		String fileType = fileName.substring(fileName.lastIndexOf("."),
		        fileName.length());
		File file = getFile(home, path + "/" + longid + fileType);
		if (!file.exists())
			FileUtils.writeStringToFile(file, "");
		fileItem.write(file);
		Document doc = getXMLDOC(home, path + "/" + longid + fileType);
		ParaMap xmlParaMap = new ParaMap();
		XMLUtils.parseXML(doc.asXML(), xmlParaMap);
		Trans_Notice transNotice = new Trans_Notice();
		transNotice.name = xmlParaMap.getString("bt");
		transNotice.no = xmlParaMap.getString("bh");
		transNotice.notice_date = xmlParaMap.getString("ggrq");
		transNotice.notice_type = xmlParaMap.getString("ywlx");
		transNotice.create_user_id = fileName;
		Trans_Target transTarget = new Trans_Target();

		out.put("state", 1);
		return out;

	}

	/**
	 * 公告交互文件(交易后)
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap uploadNoticeAfter(ParaMap inMap) throws Exception
	{
		ParaMap out = new ParaMap();
		String longid = IDGenerator.newGUID();
		String fileName = inMap.getString("txtFile2_Name");
		FileItem fileItem = inMap.getFileItem("txtFile2_Item");
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		String fileType = fileName.substring(fileName.lastIndexOf("."),
		        fileName.length());
		File file = getFile(home, path + "/" + longid + fileType);
		if (!file.exists())
			FileUtils.writeStringToFile(file, "");
		fileItem.write(file);
		out.put("state", 1);
		return out;

	}

}

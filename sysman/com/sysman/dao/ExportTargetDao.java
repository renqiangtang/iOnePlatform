package com.sysman.dao;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.base.dao.BaseDao;
import com.trade.utils.Constants;
import com.base.utils.FlagUtils;
import com.base.utils.StrUtils;
import sun.misc.BASE64Encoder;

public class ExportTargetDao extends BaseDao {
	private Element addTextElement(Element parent, String name,  String text) {
		Element result = parent.addElement(name);
		if (text != null && !text.equals("") && !text.equalsIgnoreCase("null"))
			result.setText(text);
		return result;
	}
	
	/**
	 * 根据多指标名称得到该指标的类型（TradeRule.xml）
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private String getQuotaType(String name)throws Exception{
		//读取Traderule.xml中Quota节点
		String type = "";
		InputStream in = ExportTargetDao.class.getResourceAsStream("/com/base/engine/TradeRule.xml");
		StringBuffer sb = new StringBuffer();
        Reader r = new InputStreamReader(in, "utf-8");
        int length = 0;
        for (char[] c = new char[1024]; (length = r.read(c)) != -1;) {
               sb.append(c, 0, length);
        }
        r.close();
        String xmlStr = sb.toString(); 
        
        //循环节点比较名称
        Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		List quotaList = root.elements("Quota");
		if(quotaList!=null && quotaList.size()>0){
			for(int i = 0 ; i < quotaList.size() ; i ++ ){
				Element quotaElement = (Element) quotaList.get(i);
				String quotaId = quotaElement.attributeValue("id");
				String quotaName = quotaElement.attributeValue("name");
				if(quotaName !=null && quotaName.equals(name)){
					type = quotaId;
					break;
				}
			}
		}
		return type;
	}
	
	//添加多指标内容
	private int addMultiTradeElement(Element parent, ResultSet rsMultiTrade, String idPrefix,int beginNum) throws Exception {
		if (parent == null || rsMultiTrade == null || rsMultiTrade.getRow() == 0)
			return 0;
		int intIdIndex = 1;
		rsMultiTrade.last();
		int intRowCount = rsMultiTrade.getRow();
		rsMultiTrade.first();
		do {
			Element multiTradeNodeElement = parent.addElement("Node");
			multiTradeNodeElement.addAttribute("id", idPrefix + beginNum);
			multiTradeNodeElement.addAttribute("name", rsMultiTrade.getString("name"));
			multiTradeNodeElement.addAttribute("ptype", "L03");
			multiTradeNodeElement.addAttribute("qtype", getQuotaType(rsMultiTrade.getString("name")));
			if (intIdIndex < intRowCount){
				multiTradeNodeElement.addAttribute("nextId", idPrefix + (beginNum + 1));
			}else{
				multiTradeNodeElement.addAttribute("nextId", "n02");
			}
			multiTradeNodeElement.addAttribute(Constants.Punit, rsMultiTrade.getString("unit"));
			multiTradeNodeElement.addAttribute(Constants.PinitValue, rsMultiTrade.getString("init_value"));
			multiTradeNodeElement.addAttribute(Constants.PfinalValue, rsMultiTrade.getString("final_value"));
			multiTradeNodeElement.addAttribute(Constants.Pstep, rsMultiTrade.getString("step"));
			//multiTradeNodeElement.addAttribute("is_money", rsMultiTrade.getString("is_money"));
			multiTradeNodeElement.addAttribute(Constants.PrequireLicense, rsMultiTrade.getString("require_final_license"));
			multiTradeNodeElement.addAttribute(Constants.PmultiId, rsMultiTrade.getString("id"));
			intIdIndex++;
			beginNum++;
		} while (rsMultiTrade.next());
		return intRowCount;
	}
	
	public String exportTarget(String sourceDBName, String targetDataArea) throws Exception {
		String result = null;
		//当在同一Oracle数据库下不同用户名时可以传入数据库用户名，否则请使用不同的连接
		Connection connection = getCon();
		String dataBaseName;
		if (sourceDBName == null || sourceDBName.equals("") || sourceDBName.equalsIgnoreCase("null"))
			dataBaseName = "";
		else
			dataBaseName = sourceDBName + ".";
		Statement  stmTarget = null;
		PreparedStatement  pstmGoods = null;
		PreparedStatement  pstmMultiTrade = null;
		PreparedStatement  pstmNotice = null ;
		ResultSet rsTarget = null;
		ResultSet rsGoods = null;
		ResultSet rsMultiTrade = null;
		ResultSet rsNotice = null;
		try {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("utf-8");
			Element rootElement = document.addElement("datas");
			String sqlTarget = "select * from " + dataBaseName + "trans_target where is_valid = 1 " +//and status = 3 and is_suspend = 0 and is_stop = 0
					" and id in (" + targetDataArea + ")";
			StringBuffer sqlGoods = new StringBuffer("select g.*, nvl(tgr.is_main_goods, 0) as is_main_goods from " + dataBaseName + "trans_goods g");
			sqlGoods.append(" left join " + dataBaseName + "trans_target_goods_rel tgr on tgr.goods_id = g.id");
			sqlGoods.append(" where g.is_valid = 1 and tgr.is_valid = 1 and tgr.target_id = ?");
			String sqlMultiTrade = "select * from " + dataBaseName + "trans_target_multi_trade where is_valid = 1 " +
				" and target_id = ? order by turn";
			String sqlNotice = "select * from "+ dataBaseName +"trans_notice where id in (select notice_id from "+dataBaseName+" trans_notice_target_rel where target_id = ? )";
			stmTarget = connection.createStatement();
			rsTarget = stmTarget.executeQuery(sqlTarget);
			while (rsTarget.next()) {
				if (rsGoods != null)
					rsGoods.close();
				if (pstmGoods != null)
					pstmGoods.close();
				if (rsMultiTrade != null)
					rsMultiTrade.close();
				if (pstmMultiTrade != null)
					pstmMultiTrade.close();
				if (rsNotice != null)
					rsNotice.close();
				if (pstmNotice != null)
					pstmNotice.close();
				Element dataElement = rootElement.addElement("data");
				//公告信息
				pstmNotice = connection.prepareStatement(sqlNotice.toString());
				pstmNotice.setString(1, rsTarget.getString("id"));
				rsNotice = pstmNotice.executeQuery();
				Element noticesElement = dataElement.addElement("notices");
				while(rsNotice.next()){
					Element noticeElement = noticesElement.addElement("notice");
					noticeElement.addAttribute("id", rsNotice.getString("id"));
					addTextElement(noticeElement, "no", rsNotice.getString("no"));
					addTextElement(noticeElement, "title", rsNotice.getString("title"));
					addTextElement(noticeElement, "notice_date", rsNotice.getString("notice_date"));
					addTextElement(noticeElement, "notice_type", rsNotice.getString("notice_type"));
					addTextElement(noticeElement, "target_reject", rsNotice.getString("target_reject"));
					addTextElement(noticeElement, "parent_id", rsNotice.getString("parent_id"));
				}
				//获取交易物数据
				pstmGoods = connection.prepareStatement(sqlGoods.toString());
				pstmGoods.setString(1, rsTarget.getString("id"));
				rsGoods = pstmGoods.executeQuery();
				Element goodsesElement = dataElement.addElement("goodses");
				while (rsGoods.next()) {
					Element goodsElement = goodsesElement.addElement("goods");
					goodsElement.addAttribute("id", rsGoods.getString("id"));
					if (rsGoods.getInt("is_main_goods") == 1)
						goodsElement.addAttribute("is_main_goods", "1");
					addTextElement(goodsElement, "goods_type", rsGoods.getString("goods_type"));//交易物类型。0土地，1矿产，2工业楼宇，3房产
					addTextElement(goodsElement, "no", rsGoods.getString("no"));//编号
					addTextElement(goodsElement, "name", rsGoods.getString("name"));//名称
					addTextElement(goodsElement, "kind", rsGoods.getString("kind"));//性质
					addTextElement(goodsElement, "goods_use", rsGoods.getString("goods_use"));//用途
					addTextElement(goodsElement, "blemish", rsGoods.getString("blemish"));//瑕疵
					addTextElement(goodsElement, "trust_percent", rsGoods.getString("dealer_percent"));//委托份额
					addTextElement(goodsElement, "trans_percent", rsGoods.getString("trans_percent"));//交易份额
					addTextElement(goodsElement, "trust_price", rsGoods.getString("trust_price"));//委托价格
					addTextElement(goodsElement, "address", rsGoods.getString("address"));//地址
					addTextElement(goodsElement, "industry_type", rsGoods.getString("industry_type"));//产业类别
					addTextElement(goodsElement, "goods_size", rsGoods.getString("goods_size"));//面积
					addTextElement(goodsElement, "cubage", rsGoods.getString("cubage"));//容积率
					addTextElement(goodsElement, "cubage_compare", rsGoods.getString("cubage_compare"));//容积率关系。＝≠≥≤＞＜≯≮
					addTextElement(goodsElement, "overcast", rsGoods.getString("overcast"));//覆盖率
					addTextElement(goodsElement, "overcast_compare", rsGoods.getString("overcast_compare"));//覆盖率关系。＝≠≥≤＞＜≯≮
					addTextElement(goodsElement, "green_percent", rsGoods.getString("green_percent"));//绿化率
					addTextElement(goodsElement, "green_percent_compare", rsGoods.getString("green_percent_compare"));//绿化率关系。＝≠≥≤＞＜≯≮
					addTextElement(goodsElement, "building_area", rsGoods.getString("building_area"));//建筑面积
					addTextElement(goodsElement, "canton_area", rsGoods.getString("canton_area"));//行政区域
					addTextElement(goodsElement, "use_years", rsGoods.getString("use_years"));//使用年限
					addTextElement(goodsElement, "owner", rsGoods.getString("owner"));//所有（产权）人
					addTextElement(goodsElement, "ownership_type", rsGoods.getString("ownership_type"));//权属证明类型
					addTextElement(goodsElement, "ownership_no", rsGoods.getString("ownership_no"));//权属证明编号
				}
				Element targetElement = dataElement.addElement("target");
				targetElement.addAttribute("id", rsTarget.getString("id"));
				addTextElement(targetElement, "business_type", rsTarget.getString("business_type"));//业务类别，0土地出让，1土地转让，2房产拍卖，3房产挂牌，4探矿权出让，5探矿权转让，6采矿权出让，7采矿权转让
				addTextElement(targetElement, "trans_type", rsTarget.getString("trans_type"));//交易类型，0挂牌，1拍卖，2招标
				addTextElement(targetElement, "reserve_price", rsTarget.getString("reserve_price"));//底价，此价不对外公开
				addTextElement(targetElement, "begin_price", rsTarget.getString("begin_price"));//起始价
				addTextElement(targetElement, "price_step", rsTarget.getString("price_step"));//增价幅度
				addTextElement(targetElement, "earnest_money", rsTarget.getString("earnest_money"));//保证金
				addTextElement(targetElement, "trans_condition", rsTarget.getString("trans_condition"));//交易条件
				addTextElement(targetElement, "condition_organ_name", rsTarget.getString("condition_organ_name"));//条件设置部门
				addTextElement(targetElement, "is_net_trans", rsTarget.getString("is_net_trans"));//0现场交易，1网上交易，2现场和网上同步交易
				addTextElement(targetElement, "allow_live", rsTarget.getString("allow_live"));//1允许网上直播，大屏幕等是否允许直播交易情况
				addTextElement(targetElement, "allow_union", rsTarget.getString("allow_union"));//1允许联合竞买
				addTextElement(targetElement, "allow_trust", "0");//1允许委托报价
				addTextElement(targetElement, "reject_id", rsTarget.getString("reject_id"));//标的互斥ID
				addTextElement(targetElement, "begin_apply_time", rsTarget.getString("begin_apply_time"));//竞买申请开始日期
				addTextElement(targetElement, "end_apply_time", rsTarget.getString("end_apply_time"));//竞买申请结束时间
				addTextElement(targetElement, "begin_earnest_time", rsTarget.getString("begin_apply_time"));//保证金开始日期
				addTextElement(targetElement, "end_earnest_time", rsTarget.getString("end_earnest_time"));//保证金结束时间
				addTextElement(targetElement, "begin_notice_time", rsTarget.getString("begin_notice_time"));//公告开始时间
				addTextElement(targetElement, "end_notice_time", rsTarget.getString("end_notice_time"));//公告结束时间
				addTextElement(targetElement, "begin_trans_time", rsTarget.getString("begin_trans_time"));//挂牌开始时间
				addTextElement(targetElement, "end_trans_time", rsTarget.getString("end_trans_time"));//挂牌结束时间
				addTextElement(targetElement, "begin_offer_time", rsTarget.getString("begin_offer_time"));//挂牌报价（集中竞价）开始时间
				addTextElement(targetElement, "end_offer_time", rsTarget.getString("end_offer_time"));//挂牌报价（集中竞价）结束时间
				addTextElement(targetElement, "final_price", rsTarget.getString("final_price"));//封顶价
				addTextElement(targetElement, "unit", "元");//封顶价
				addTextElement(targetElement, "is_limit_trans", rsTarget.getString("is_limit_trans"));//是否进入限时竞价
				addTextElement(targetElement, "trans_mode", rsTarget.getString("trans_mode"));//进入条件
				//拍卖、现场拍卖、限时竞买开始时间
				if (rsTarget.getInt("is_net_trans") == 0)
					addTextElement(targetElement, "begin_limit_time", rsTarget.getString("begin_scene_offer_time"));
				else if (rsTarget.getInt("trans_type") == 0)
					addTextElement(targetElement, "begin_limit_time", rsTarget.getString("end_offer_time"));
				else
					addTextElement(targetElement, "begin_limit_time", rsTarget.getString("begin_offer_time"));
				//获取多指标交易数据
				try {
					pstmMultiTrade = connection.prepareStatement(sqlMultiTrade, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					pstmMultiTrade.setString(1, rsTarget.getString("id"));
					rsMultiTrade = pstmMultiTrade.executeQuery();
				} catch (SQLException e){
					//无多指标表哑处理
				}
				//开始创建多指标节点
				int turn = 1;
				Element multisElement = dataElement.addElement("multis");
				if ((rsTarget.getInt("trans_type") == 0 && rsTarget.getInt("is_net_trans") == 1 && rsTarget.getInt("is_limit_trans") == 1) || rsTarget.getInt("trans_type") == 1){
					Element beginMultiElement = multisElement.addElement("multi");
					beginMultiElement.addAttribute("id", rsTarget.getString("id"));
					beginMultiElement.addAttribute("name", "价格指标");
					beginMultiElement.addAttribute("unit", "元" );
					beginMultiElement.addAttribute("init_value", rsTarget.getString("begin_price"));
					beginMultiElement.addAttribute("final_value", rsTarget.getString("final_price"));
					beginMultiElement.addAttribute("step", rsTarget.getString("price_step"));
					int transType = rsTarget.getInt("trans_type");
					int transMode = rsTarget.getInt("trans_mode");
					String flag="1"+FlagUtils.nodeFormatChar+"0"+FlagUtils.nodeFormatChar+"0";
					if(transType == 0 ){
						switch(transMode){
							case 0 :
								flag="1"+FlagUtils.nodeFormatChar+"1"+FlagUtils.nodeFormatChar+"0";
								break;
							case 1 :
								flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"0";
								break;
							case 2 :
								flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"1";
								break;
							case 3 :
								flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"2";
								break;
						}
					}else if(transType == 1){
						flag="1-"+transMode+"-0";
					}
					
					beginMultiElement.addAttribute("enter_flag", flag);
					beginMultiElement.addAttribute("turn", String.valueOf(turn));
					turn++;
				}
				while (rsMultiTrade.next()){
					Element multiElement = multisElement.addElement("multi");
					multiElement.addAttribute("id", rsMultiTrade.getString("id"));
					multiElement.addAttribute("name", rsMultiTrade.getString("name"));
					multiElement.addAttribute("unit", rsMultiTrade.getString("unit"));
					multiElement.addAttribute("init_value", rsMultiTrade.getString("init_value"));
					multiElement.addAttribute("final_value", rsMultiTrade.getString("final_value"));
					multiElement.addAttribute("step", rsMultiTrade.getString("step"));
					multiElement.addAttribute("enter_flag", "1"+FlagUtils.nodeFormatChar+rsMultiTrade.getString("require_final_license")+FlagUtils.nodeFormatChar+"null");
					multiElement.addAttribute("turn", String.valueOf(turn));
					turn++;
					
				}
				
				/*Element templateElement = dataElement.addElement("template");//首节点
				templateElement.addAttribute("id", rsTarget.getString("id"));
				Element startNodeElement = templateElement.addElement("StartNode");
				startNodeElement.addAttribute("id", "n01");
				startNodeElement.addAttribute("nextId", "a1");
				Element nodeElement = templateElement.addElement("Node");//公告期
				nodeElement.addAttribute("id", "a1");
				nodeElement.addAttribute("name", "公告期");
				nodeElement.addAttribute("ptype", "L01");
				if (rsTarget.getInt("trans_type") == 0 || rsTarget.getInt("is_net_trans") == 1)//挂牌或者网上拍卖有下一环节
					nodeElement.addAttribute("nextId", "a2");
				else
					nodeElement.addAttribute("nextId", "n02");
				nodeElement.addAttribute("beginTime", rsTarget.getString("begin_notice_time"));
				//nodeElement.addAttribute("endTime", rsTarget.getString("end_notice_time"));
				nodeElement.addAttribute("endTime", rsTarget.getString("begin_offer_time"));
				if (rsTarget.getInt("trans_type") == 0) {//挂牌
					nodeElement = templateElement.addElement("Node");//集中报价期
					nodeElement.addAttribute("id", "a2");
					nodeElement.addAttribute("name", "集中报价期");
					nodeElement.addAttribute("ptype", "L02");
					if (rsTarget.getInt("is_net_trans") == 1 && rsTarget.getInt("is_limit_trans") == 1){//有限时竞价期
						nodeElement.addAttribute("nextId", "a3");
					}else{
						nodeElement.addAttribute("nextId", "n02");
					}
					nodeElement.addAttribute("beginTime", rsTarget.getString("begin_offer_time"));
					nodeElement.addAttribute("endTime", rsTarget.getString("end_offer_time"));
					nodeElement.addAttribute(Constants.PinitValue, rsTarget.getString("begin_price"));
					nodeElement.addAttribute(Constants.PfinalValue, rsTarget.getString("final_price"));
					nodeElement.addAttribute(Constants.Pstep, rsTarget.getString("price_step"));
					if (rsTarget.getInt("is_net_trans") == 1 && rsTarget.getInt("is_limit_trans") == 1) {//限时竞价期
						nodeElement = templateElement.addElement("Node");
						nodeElement.addAttribute("id", "a3");
						nodeElement.addAttribute("name", "价格指标");
						nodeElement.addAttribute("ptype", "L03");
						nodeElement.addAttribute("qtype", "Q01");
						nodeElement.addAttribute("nextId", "n02");
						nodeElement.addAttribute("beginTime", rsTarget.getString("end_offer_time"));
						nodeElement.addAttribute(Constants.PinitValue, rsTarget.getString("begin_price"));
						nodeElement.addAttribute(Constants.PfinalValue, rsTarget.getString("final_price"));
						nodeElement.addAttribute(Constants.Pstep, rsTarget.getString("price_step"));
						//此处添加多指标子节点
						int multiLength = addMultiTradeElement(templateElement, rsMultiTrade, "a" , 4);
						if(multiLength>0){
							nodeElement.addAttribute("nextId", "a4");
						}
					}
				} else if (rsTarget.getInt("is_net_trans") == 1) {//拍卖
					nodeElement = templateElement.addElement("Node");//限时竞价期
					nodeElement.addAttribute("id", "a2");
					nodeElement.addAttribute("name", "价格指标");
					nodeElement.addAttribute("ptype", "L03");
					nodeElement.addAttribute("qtype", "Q01");
					nodeElement.addAttribute("nextId", "n02");
					nodeElement.addAttribute("beginTime", rsTarget.getString("begin_offer_time"));
					nodeElement.addAttribute(Constants.PinitValue, rsTarget.getString("begin_price"));
					nodeElement.addAttribute(Constants.PfinalValue, rsTarget.getString("final_price"));
					nodeElement.addAttribute(Constants.Pstep, rsTarget.getString("price_step"));
					//此处添加多指标子节点
					int multiLength = addMultiTradeElement(templateElement, rsMultiTrade, "a",3);
					if(multiLength>0){
						nodeElement.addAttribute("nextId", "a3");
					}
				}
				Element endNodeElement = templateElement.addElement("EndNode");//结束节点
				endNodeElement.addAttribute("id", "n02");*/
			}
			//美化排版
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
//			XMLWriter writer = new XMLWriter(format);
//			writer.write(document);
//			writer.close();
			result = document.asXML();
			
			  OutputFormat format = OutputFormat.createPrettyPrint();
			  format.setEncoding("utf-8");
			  StringWriter writer = new StringWriter();
			  // 格式化输出流
			  XMLWriter xmlWriter = new XMLWriter(writer, format);
			  // 将document写入到输出流
			  xmlWriter.write(document);
			  xmlWriter.close();

			  result =  writer.toString();
		} finally {
			if (rsTarget != null)
				rsTarget.close();
			if (stmTarget != null)
				stmTarget.close();
			if (rsGoods != null)
				rsGoods.close();
			if (pstmGoods != null)
				pstmGoods.close();
			if (rsMultiTrade != null)
				rsMultiTrade.close();
			if (pstmMultiTrade != null)
				pstmMultiTrade.close();
			if (rsNotice != null)
				rsNotice.close();
			if (pstmNotice != null)
				pstmNotice.close();
		}
		return result;
	}
	
	private BufferedInputStream getPartContent(String sourceDBName, String id, int no, int partOrder) throws Exception {
		//当在同一Oracle数据库下不同用户名时可以传入数据库用户名，否则请使用不同的连接
		String dbName = StrUtils.isNull(sourceDBName) ? "" : sourceDBName + ".";
		Connection connection = getCon();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from " + dbName + "file_table where id = ? and no = ? and part_order = ?";
		BufferedInputStream bis = null;
		ps = connection.prepareStatement(sql);
		ps.setString(1, id);
		ps.setInt(2, no);
		ps.setInt(3, partOrder);
		rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getBinaryStream("body") != null) {
				bis = new BufferedInputStream(rs.getBinaryStream("body"));
			}
		}
		return bis;
	}
	
	private String getFileContent(String sourceDBName, String id) throws Exception {
		String dbName = StrUtils.isNull(sourceDBName) ? "" : sourceDBName + ".";
		Connection connection = getCon();
		String sql = "select id, no, part_order from " + dbName + "file_table where id = ? order by no, part_order";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		int fileNo = -1;
		while (rs.next()) {
			if (fileNo == -1)
				fileNo = rs.getInt(2);
			else if (fileNo != rs.getInt(2))
				break;
			BufferedInputStream bis = getPartContent(sourceDBName, rs.getString(1), rs.getInt(2), rs.getInt(3));
			if(bis != null){
				byte[] buffer = new byte[1024];
				int bufferSize = 0;
				while((bufferSize = bis.read(buffer)) > 0){
					out.write(buffer, 0, bufferSize);
				}
				bis.close();
			}
		}
		out.flush();
		String strBase64 = new BASE64Encoder().encode(out.toByteArray());
		out.close();
		return strBase64;
	}
	
	private String getTargetSampleFileContent(String sourceDBName, String id, int sampleType) throws Exception {
		String dbName = StrUtils.isNull(sourceDBName) ? "" : sourceDBName + ".";
		Connection connection = getCon();
		String sql = "select id, sample_type, content from " + dbName + "trans_target_sample where target_id = ? and sample_type = ?";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, id);
		ps.setInt(2, sampleType);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			BufferedInputStream bis = new BufferedInputStream(rs.getBinaryStream("content"));
			if(bis != null){
				byte[] buffer = new byte[1024];
				int bufferSize = 0;
				while((bufferSize = bis.read(buffer)) > 0){
					out.write(buffer, 0, bufferSize);
				}
				bis.close();
			}
		}
		out.flush();
		String strBase64 = new BASE64Encoder().encode(out.toByteArray());
		out.close();
		return strBase64;
	}
	
	public String exportTargetByNotice(String sourceDBName, String sourceNoticeArea) throws Exception {
		String result = null;
		//当在同一Oracle数据库下不同用户名时可以传入数据库用户名，否则请使用不同的连接
		Connection connection = getCon();
		String dataBaseName;
		if (sourceDBName == null || sourceDBName.equals("") || sourceDBName.equalsIgnoreCase("null"))
			dataBaseName = "";
		else
			dataBaseName = sourceDBName + ".";
		Statement stmNotice = null;
		PreparedStatement pstmTarget = null;
		PreparedStatement pstmGoods = null;
		PreparedStatement pstmGoodsFiles = null;
		PreparedStatement pstmMultiTrade = null;
		ResultSet rsNotice = null;
		ResultSet rsTarget = null;
		ResultSet rsGoods = null;
		ResultSet rsGoodsFiles = null;
		ResultSet rsMultiTrade = null;
		try {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("utf-8");
			Element rootElement = document.addElement("datas");
			String sqlNotice = "select * from " + dataBaseName + "trans_notice where id in (" + sourceNoticeArea + ") order by notice_date";
			String sqlTarget = "select * from " + dataBaseName + "trans_target where is_valid = 1 " +//and status = 3 and is_suspend = 0 and is_stop = 0
					" and id in (select target_id from " + dataBaseName + "trans_notice_target_rel where notice_id = ?)";
			StringBuffer sqlGoods = new StringBuffer("select g.*, nvl(tgr.is_main_goods, 0) as is_main_goods from " + dataBaseName + "trans_goods g");
			sqlGoods.append(" left join " + dataBaseName + "trans_target_goods_rel tgr on tgr.goods_id = g.id");
			sqlGoods.append(" where g.is_valid = 1 and tgr.is_valid = 1 and tgr.target_id = ?");
			String sqlGoodsFiles = "select d.file_no, l.id from " + dataBaseName + "file_list l " +
				"left join " + dataBaseName + "file_dict d on d.id = l.file_id " +
				"where l.list_no = ? " +
				"  and l.file_id in (select id from " + dataBaseName + "file_dict where id in (select file_id from " + dataBaseName + "require_file where lower(business_id) = 'trans_goods')" +
				"    and lower(file_no) in ('trans_goods_zongditu', 'trans_goods_weizhitu'))";
			String sqlMultiTrade = "select * from " + dataBaseName + "trans_target_multi_trade where is_valid = 1 " +
				" and target_id = ? order by turn";
			stmNotice = connection.createStatement();
			rsNotice = stmNotice.executeQuery(sqlNotice);
			while (rsNotice.next()) {
				if (rsTarget != null)
					rsTarget.close();
				if (pstmTarget != null)
					pstmTarget.close();
				if (rsGoods != null)
					rsGoods.close();
				if (pstmGoods != null)
					pstmGoods.close();
				if (rsGoodsFiles != null)
					rsGoodsFiles.close();
				if (pstmGoodsFiles != null)
					pstmGoodsFiles.close();
				if (rsMultiTrade != null)
					rsMultiTrade.close();
				if (pstmMultiTrade != null)
					pstmMultiTrade.close();
				Element dataElement = rootElement.addElement("data");
				addTextElement(dataElement, "id", rsNotice.getString("id"));
				addTextElement(dataElement, "no", rsNotice.getString("no"));
				addTextElement(dataElement, "title", rsNotice.getString("title"));
				addTextElement(dataElement, "notice_date", rsNotice.getString("notice_date"));
				addTextElement(dataElement, "notice_type", rsNotice.getString("notice_type"));
				addTextElement(dataElement, "target_reject", rsNotice.getString("target_reject"));
				addTextElement(dataElement, "parent_id", rsNotice.getString("parent_id"));
				//公告附件
				String strNoticeBase64 = getFileContent(sourceDBName, rsNotice.getString("id"));
				addTextElement(dataElement, "notice_content", strNoticeBase64);
				//标的信息
				pstmTarget = connection.prepareStatement(sqlTarget);
				pstmTarget.setString(1, rsNotice.getString("id"));
				rsTarget = pstmTarget.executeQuery();
				Element targetsElement = dataElement.addElement("targets");
				while(rsTarget.next()){
					if (rsGoods != null)
						rsGoods.close();
					if (pstmGoods != null)
						pstmGoods.close();
					if (rsGoodsFiles != null)
						rsGoodsFiles.close();
					if (pstmGoodsFiles != null)
						pstmGoodsFiles.close();
					if (rsMultiTrade != null)
						rsMultiTrade.close();
					if (pstmMultiTrade != null)
						pstmMultiTrade.close();
					Element targetElement = targetsElement.addElement("target");
					targetElement.addAttribute("id", rsTarget.getString("id"));
					addTextElement(targetElement, "business_type", rsTarget.getString("business_type"));//业务类别，0土地出让，1土地转让，2房产拍卖，3房产挂牌，4探矿权出让，5探矿权转让，6采矿权出让，7采矿权转让
					addTextElement(targetElement, "trans_type", rsTarget.getString("trans_type"));//交易类型，0挂牌，1拍卖，2招标
					addTextElement(targetElement, "reserve_price", rsTarget.getString("reserve_price"));//底价，此价不对外公开
					addTextElement(targetElement, "begin_price", rsTarget.getString("begin_price"));//起始价
					addTextElement(targetElement, "price_step", rsTarget.getString("price_step"));//增价幅度
					addTextElement(targetElement, "earnest_money", rsTarget.getString("earnest_money"));//保证金
					addTextElement(targetElement, "trans_condition", rsTarget.getString("trans_condition"));//交易条件
					addTextElement(targetElement, "condition_organ_name", rsTarget.getString("condition_organ_name"));//条件设置部门
					addTextElement(targetElement, "is_net_trans", rsTarget.getString("is_net_trans"));//0现场交易，1网上交易，2现场和网上同步交易
					addTextElement(targetElement, "allow_live", rsTarget.getString("allow_live"));//1允许网上直播，大屏幕等是否允许直播交易情况
					addTextElement(targetElement, "allow_union", rsTarget.getString("allow_union"));//1允许联合竞买
					addTextElement(targetElement, "allow_trust", "0");//1允许委托报价
					addTextElement(targetElement, "reject_id", rsTarget.getString("reject_id"));//标的互斥ID
					addTextElement(targetElement, "begin_apply_time", rsTarget.getString("begin_apply_time"));//竞买申请开始日期
					addTextElement(targetElement, "end_apply_time", rsTarget.getString("end_apply_time"));//竞买申请结束时间
					addTextElement(targetElement, "begin_earnest_time", rsTarget.getString("begin_apply_time"));//保证金开始日期
					addTextElement(targetElement, "end_earnest_time", rsTarget.getString("end_earnest_time"));//保证金结束时间
					addTextElement(targetElement, "begin_notice_time", rsTarget.getString("begin_notice_time"));//公告开始时间
					addTextElement(targetElement, "end_notice_time", rsTarget.getString("end_notice_time"));//公告结束时间
					addTextElement(targetElement, "begin_trans_time", rsTarget.getString("begin_trans_time"));//挂牌开始时间
					addTextElement(targetElement, "end_trans_time", rsTarget.getString("end_trans_time"));//挂牌结束时间
					addTextElement(targetElement, "begin_offer_time", rsTarget.getString("begin_offer_time"));//挂牌报价（集中竞价）开始时间
					addTextElement(targetElement, "end_offer_time", rsTarget.getString("end_offer_time"));//挂牌报价（集中竞价）结束时间
					addTextElement(targetElement, "final_price", rsTarget.getString("final_price"));//封顶价
					addTextElement(targetElement, "unit", "元");//单位
					addTextElement(targetElement, "is_limit_trans", rsTarget.getString("is_limit_trans"));//是否进入限时竞价
					addTextElement(targetElement, "trans_mode", rsTarget.getString("trans_mode"));//进入条件
					//拍卖、现场拍卖、限时竞买开始时间
					if (rsTarget.getInt("is_net_trans") == 0)
						addTextElement(targetElement, "begin_limit_time", rsTarget.getString("begin_scene_offer_time"));
					else if (rsTarget.getInt("trans_type") == 0)
						addTextElement(targetElement, "begin_limit_time", rsTarget.getString("end_offer_time"));
					else
						addTextElement(targetElement, "begin_limit_time", rsTarget.getString("begin_offer_time"));
					Element targetFilesElement = targetElement.addElement("target_files");
					String strTargetSampleFileBase64 = getTargetSampleFileContent(sourceDBName, rsTarget.getString("id"), 0);
					Element targetFileElement = addTextElement(targetFilesElement, "file", strTargetSampleFileBase64);
					targetFileElement.addAttribute("type", "0");//交易须知
					targetFileElement.addAttribute("name", "交易须知");
					strTargetSampleFileBase64 = getTargetSampleFileContent(sourceDBName, rsTarget.getString("id"), 1);
					targetFileElement = addTextElement(targetFilesElement, "file", strTargetSampleFileBase64);
					targetFileElement.addAttribute("type", "1");//竞买申请书
					targetFileElement.addAttribute("name", "竞买申请书");
					strTargetSampleFileBase64 = getTargetSampleFileContent(sourceDBName, rsTarget.getString("id"), 4);
					targetFileElement = addTextElement(targetFilesElement, "file", strTargetSampleFileBase64);
					targetFileElement.addAttribute("type", "2");//成交确认书
					targetFileElement.addAttribute("name", "成交确认书");
					strTargetSampleFileBase64 = getTargetSampleFileContent(sourceDBName, rsTarget.getString("id"), 5);
					targetFileElement = addTextElement(targetFilesElement, "file", strTargetSampleFileBase64);
					targetFileElement.addAttribute("type", "3");//合同
					targetFileElement.addAttribute("name", "合同");
					strTargetSampleFileBase64 = getTargetSampleFileContent(sourceDBName, rsTarget.getString("id"), 8);
					targetFileElement = addTextElement(targetFilesElement, "file", strTargetSampleFileBase64);
					targetFileElement.addAttribute("type", "4");//改造合同
					targetFileElement.addAttribute("name", "改造合同");
					//获取多指标交易数据
					try {
						pstmMultiTrade = connection.prepareStatement(sqlMultiTrade, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						pstmMultiTrade.setString(1, rsTarget.getString("id"));
						rsMultiTrade = pstmMultiTrade.executeQuery();
					} catch (SQLException e){
						//无多指标表哑处理
					}
					//开始创建多指标节点
					int turn = 1;
					Element multisElement = targetElement.addElement("multis");
					if ((rsTarget.getInt("trans_type") == 0 && rsTarget.getInt("is_net_trans") == 1 && rsTarget.getInt("is_limit_trans") == 1) || rsTarget.getInt("trans_type") == 1){
						Element beginMultiElement = multisElement.addElement("multi");
						beginMultiElement.addAttribute("id", rsTarget.getString("id"));
						beginMultiElement.addAttribute("name", "价格指标");
						beginMultiElement.addAttribute("unit", "元" );
						beginMultiElement.addAttribute("init_value", rsTarget.getString("begin_price"));
						beginMultiElement.addAttribute("final_value", rsTarget.getString("final_price"));
						beginMultiElement.addAttribute("step", rsTarget.getString("price_step"));
						int transType = rsTarget.getInt("trans_type");
						int transMode = rsTarget.getInt("trans_mode");
						String flag="1"+FlagUtils.nodeFormatChar+"0"+FlagUtils.nodeFormatChar+"0";
						if(transType == 0 ){
							switch(transMode){
								case 0 :
									flag="1"+FlagUtils.nodeFormatChar+"1"+FlagUtils.nodeFormatChar+"0";
									break;
								case 1 :
									flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"0";
									break;
								case 2 :
									flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"1";
									break;
								case 3 :
									flag="1"+FlagUtils.nodeFormatChar+"2"+FlagUtils.nodeFormatChar+"2";
									break;
							}
						}else if(transType == 1){
							flag="1-"+transMode+"-0";
						}
						
						beginMultiElement.addAttribute("enter_flag", flag);
						beginMultiElement.addAttribute("turn", String.valueOf(turn));
						turn++;
					}
					while (rsMultiTrade.next()){
						Element multiElement = multisElement.addElement("multi");
						multiElement.addAttribute("id", rsMultiTrade.getString("id"));
						multiElement.addAttribute("name", rsMultiTrade.getString("name"));
						multiElement.addAttribute("unit", rsMultiTrade.getString("unit"));
						multiElement.addAttribute("init_value", rsMultiTrade.getString("init_value"));
						multiElement.addAttribute("final_value", rsMultiTrade.getString("final_value"));
						multiElement.addAttribute("step", rsMultiTrade.getString("step"));
						multiElement.addAttribute("enter_flag", "1"+FlagUtils.nodeFormatChar+rsMultiTrade.getString("require_final_license")+FlagUtils.nodeFormatChar+"null");
						multiElement.addAttribute("turn", String.valueOf(turn));
						turn++;
						
					}
					//获取交易物数据
					pstmGoods = connection.prepareStatement(sqlGoods.toString());
					pstmGoods.setString(1, rsTarget.getString("id"));
					rsGoods = pstmGoods.executeQuery();
					Element goodsesElement = targetElement.addElement("goodses");
					while (rsGoods.next()) {
						Element goodsElement = goodsesElement.addElement("goods");
						goodsElement.addAttribute("id", rsGoods.getString("id"));
						if (rsGoods.getInt("is_main_goods") == 1)
							goodsElement.addAttribute("is_main_goods", "1");
						addTextElement(goodsElement, "goods_type", rsGoods.getString("goods_type"));//交易物类型。0土地，1矿产，2工业楼宇，3房产
						addTextElement(goodsElement, "no", rsGoods.getString("no"));//编号
						addTextElement(goodsElement, "name", rsGoods.getString("name"));//名称
						addTextElement(goodsElement, "kind", rsGoods.getString("kind"));//性质
						addTextElement(goodsElement, "goods_use", rsGoods.getString("goods_use"));//用途
						addTextElement(goodsElement, "blemish", rsGoods.getString("blemish"));//瑕疵
						addTextElement(goodsElement, "trust_percent", rsGoods.getString("dealer_percent"));//委托份额
						addTextElement(goodsElement, "trans_percent", rsGoods.getString("trans_percent"));//交易份额
						addTextElement(goodsElement, "trust_price", rsGoods.getString("trust_price"));//委托价格
						addTextElement(goodsElement, "address", rsGoods.getString("address"));//地址
						addTextElement(goodsElement, "industry_type", rsGoods.getString("industry_type"));//产业类别
						addTextElement(goodsElement, "goods_size", rsGoods.getString("goods_size"));//面积
						addTextElement(goodsElement, "cubage", rsGoods.getString("cubage"));//容积率
						addTextElement(goodsElement, "cubage_compare", rsGoods.getString("cubage_compare"));//容积率关系。＝≠≥≤＞＜≯≮
						addTextElement(goodsElement, "overcast", rsGoods.getString("overcast"));//覆盖率
						addTextElement(goodsElement, "overcast_compare", rsGoods.getString("overcast_compare"));//覆盖率关系。＝≠≥≤＞＜≯≮
						addTextElement(goodsElement, "green_percent", rsGoods.getString("green_percent"));//绿化率
						addTextElement(goodsElement, "green_percent_compare", rsGoods.getString("green_percent_compare"));//绿化率关系。＝≠≥≤＞＜≯≮
						addTextElement(goodsElement, "building_area", rsGoods.getString("building_area"));//建筑面积
						addTextElement(goodsElement, "canton_area", rsGoods.getString("canton_area"));//行政区域
						addTextElement(goodsElement, "use_years", rsGoods.getString("use_years"));//使用年限
						addTextElement(goodsElement, "owner", rsGoods.getString("owner"));//所有（产权）人
						addTextElement(goodsElement, "ownership_type", rsGoods.getString("ownership_type"));//权属证明类型
						addTextElement(goodsElement, "ownership_no", rsGoods.getString("ownership_no"));//权属证明编号
						//取宗地图、位置图
						pstmGoodsFiles = connection.prepareStatement(sqlGoodsFiles);
						pstmGoodsFiles.setString(1, rsGoods.getString("id"));
						rsGoodsFiles = pstmGoodsFiles.executeQuery();
						Element goodsFilesElement = goodsElement.addElement("goods_files");
						while (rsGoodsFiles.next()) {
							String strDituBase64 = getFileContent(sourceDBName, rsGoodsFiles.getString("id"));
							Element goodsFileElement = addTextElement(goodsFilesElement, "file", strDituBase64);
							goodsFileElement.addAttribute("id", rsGoodsFiles.getString("id"));
							goodsFileElement.addAttribute("type", rsGoodsFiles.getString("file_no").equalsIgnoreCase("trans_goods_zongditu") ? "0" : "1");//0宗地图，1位置图
							goodsFileElement.addAttribute("name", rsGoodsFiles.getString("file_no").equalsIgnoreCase("trans_goods_zongditu") ? "宗地图" : "位置图");
						}
					}
				}
			}
			result = document.asXML();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			
//			//另存到文件系统测试
//			FileWriter fw = new FileWriter("e:\\test1111.xml", false);
//			XMLWriter xmlWriter = new XMLWriter(fw, format);
//			xmlWriter.write(document);
//			xmlWriter.close();
//			result = "save as e:\\test1111.xml";
			
			StringWriter writer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(writer, format);
			xmlWriter.write(document);
			xmlWriter.close();
			result = writer.toString();
		} finally {
			if (rsTarget != null)
				rsTarget.close();
			if (pstmTarget != null)
				pstmTarget.close();
			if (rsGoods != null)
				rsGoods.close();
			if (pstmGoods != null)
				pstmGoods.close();
			if (rsMultiTrade != null)
				rsMultiTrade.close();
			if (pstmMultiTrade != null)
				pstmMultiTrade.close();
			if (rsNotice != null)
				rsNotice.close();
			if (stmNotice != null)
				stmNotice.close();
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		ExportTargetDao exportTargetDao = new ExportTargetDao();
        //System.out.println(exportTargetDao.exportTarget("jy2spdb20120313", "'025012340000002498', '025012340000002499','025012340000002500'"));
		System.out.println(exportTargetDao.exportTargetByNotice("jy2spdb20120313", "'025012360000000135'"));
    }
}

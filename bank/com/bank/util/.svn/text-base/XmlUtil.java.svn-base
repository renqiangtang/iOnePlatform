package com.bank.util;

import java.io.StringWriter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.base.utils.CharsetUtils;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector.Matcher;

public class XmlUtil {
	
	public  final static String xmlCoding = CharsetUtils.gbk;

	Logger log = Logger.getLogger(this.getClass());

	public void XmlUtil() {

	}

	public XmlVo readXML(String xml) throws Exception {
		XmlVo xmlVo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement(); // 得到根节点<szlr>
			Element inputElement = root.element("input");
			if (inputElement != null) {
				String trxcode = inputElement.elementText("trxcode");
				input.setTrxcode(trxcode);
				String busino = inputElement.elementText("busino");
				input.setBusino(busino);
				String draccno = inputElement.elementText("draccno");
				input.setDraccno(draccno);
				String draccname = inputElement.elementText("draccname");
				input.setDraccname(draccname);
				String craccno = inputElement.elementText("craccno");
				input.setCraccno(craccno);
				String craccname = inputElement.elementText("craccname");
				input.setCraccname(craccname);
				String amount = inputElement.elementText("amount");
				input.setAmount(amount);
				String date = inputElement.elementText("date");
				input.setDate(date);
				String remark = inputElement.elementText("remark");
				input.setRemark(remark);
				String username = inputElement.elementText("username");
				input.setUsername(username);
				String trxcacc = inputElement.elementText("trxcacc");
				input.setTrxcacc(trxcacc);
				String trxcname = inputElement.elementText("trxcname");
				input.setTrxcname(trxcname);
				String benjin = inputElement.elementText("benjin");
				input.setBenjin(benjin);
				String startdate = inputElement.elementText("startdate");
				input.setStartdate(startdate);
				String enddate = inputElement.elementText("enddate");
				input.setEnddate(enddate);
				String accno = inputElement.elementText("accno");
				input.setAccno(accno);
				String bankid = inputElement.elementText("bankid");
				input.setBankid(bankid);
				String busiid = inputElement.elementText("busiid");
				input.setBusiid(busiid);
				String orgno = inputElement.elementText("orgno");
				input.setOrgno(orgno);
				String draccbank = inputElement.elementText("draccbank");
				input.setDraccbank(draccbank);
				String draccsubbank = inputElement.elementText("draccsubbank");
				input.setDraccsubbank(draccsubbank);
				String transtime = inputElement.elementText("transtime");
				input.setTranstime(transtime);
				String currency = inputElement.elementText("currency");
				input.setCurrency(currency);
				xmlVo.setInput(input);
			}
			Element outElement = root.element("output");
			String status = outElement.elementText("status");
			output.setStatus(status);
			String retcode = outElement.elementText("retcode");
			output.setRetcode(retcode);
			String date2 = outElement.elementText("date");
			output.setDate(date2);
			String retmsg = outElement.elementText("retmsg");
			output.setRetmsg(retmsg);
			String accno2 = outElement.elementText("accno");
			output.setAccno(accno2);
			String commseq = outElement.elementText("commseq");
			output.setCommseq(commseq);
			String amount2 = outElement.elementText("amount");
			output.setAmount(amount2);
			xmlVo.setOutput(output);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlVo;
	}
	
	public String writeXMLFile(XmlVo xmlVo) throws Exception {
		return writeXMLFile(xmlVo,xmlCoding);
	}

	public String writeXMLFile(XmlVo xmlVo,String coding) throws Exception {
		String result = "";
		try {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding(coding);
			Element rootElement = document.addElement("szlr");

			XmlInput input = xmlVo.getInput();
			if (input != null) {
				Element inputElement = rootElement.addElement("input");

				if (input.getTrxcode() != null) {
					addTextElement(inputElement, "trxcode", input.getTrxcode());
				}
				if (input.getBusino() != null) {
					addTextElement(inputElement, "busino", input.getBusino());
				}
				if (input.getOrgno() != null) {
					addTextElement(inputElement, "orgno", input.getOrgno());
				}
				if (input.getCurrency() != null) {
					addTextElement(inputElement, "currency",
							input.getCurrency());
				}
				if (input.getUsername() != null) {
					addTextElement(inputElement, "username",
							input.getUsername());
				}
				if (input.getDraccbank() != null) {
					addTextElement(inputElement, "draccbank",
							input.getDraccbank());
				}
				if (input.getDraccsubbank() != null) {
					addTextElement(inputElement, "draccsubbank",
							input.getDraccsubbank());
				}
				if (input.getDraccno() != null) {
					addTextElement(inputElement, "draccno", input.getDraccno());
				}
				if (input.getDraccname() != null) {
					addTextElement(inputElement, "draccname",
							input.getDraccname());
				}
				if (input.getCraccno() != null) {
					addTextElement(inputElement, "craccno", input.getCraccno());
				}
				if (input.getCraccname() != null) {
					addTextElement(inputElement, "craccname",
							input.getCraccname());
				}
				if (input.getTrxcacc() != null) {
					addTextElement(inputElement, "trxcacc", input.getTrxcacc());
				}
				if (input.getTrxcname() != null) {
					addTextElement(inputElement, "trxcname",
							input.getTrxcname());
				}
				if (input.getBenjin() != null) {
					addTextElement(inputElement, "benjin", input.getBenjin());
				}
				if (input.getAccno() != null) {
					addTextElement(inputElement, "accno", input.getAccno());
				}
				if (input.getAppbusino() != null) {
					addTextElement(inputElement, "appbusino", input.getAppbusino());
				}
				if (input.getAmount() != null) {
					addTextElement(inputElement, "amount", input.getAmount());
				}
				if (input.getStartdate() != null) {
					addTextElement(inputElement, "startdate",
							input.getStartdate());
				}
				if (input.getEnddate() != null) {
					addTextElement(inputElement, "enddate", input.getEnddate());
				}
				if (input.getTranstime() != null) {
					addTextElement(inputElement, "transtime",
							input.getTranstime());
				}
				if (input.getBusiid() != null) {
					addTextElement(inputElement, "busiid", input.getBusiid());
				}
				if (input.getDate() != null) {
					addTextElement(inputElement, "date", input.getDate());
				}
				if (input.getAccmessage() != null) {
					addTextElement(inputElement, "accmessage", input.getAccmessage());
				}
				if (input.getRemark() != null) {
					addTextElement(inputElement, "remark", input.getRemark());
				}
				if (input.getBankid() != null) {
					addTextElement(inputElement, "bankid", input.getBankid());
				}
				//if (input.getRsa() != null) {
					addTextElement(inputElement, "rsa", input.getRsa());
				//}
			}

			XmlOutput output = xmlVo.getOutput();
			if (output != null) {
				Element outputElement = rootElement.addElement("output");
				if (output.getStatus() != null) {
					addTextElement(outputElement, "status", output.getStatus());
				}
				if (output.getRetcode() != null) {
					addTextElement(outputElement, "retcode",
							output.getRetcode());
				}
				if (output.getAmount() != null) {
					addTextElement(outputElement, "amount", output.getAmount());
				}
				if (output.getDate() != null) {
					addTextElement(outputElement, "date", output.getDate());
				}
				if (output.getRetmsg() != null) {
					addTextElement(outputElement, "retmsg", output.getRetmsg());
				}
				if (output.getAccno() != null) {
					addTextElement(outputElement, "accno", output.getAccno());
				}
				if (output.getCommseq() != null) {
					addTextElement(outputElement, "commseq",
							output.getCommseq());
				}
			}

			result = formatXml(document.asXML(),coding);
			log.info("create xml==========================>");
			log.info(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	private Element addTextElement(Element parent, String name, String text) {
		Element result = parent.addElement(name);
		if (text != null && !text.equals("") && !text.equalsIgnoreCase("null"))
			result.setText(text);
		return result;
	}
	
	private String formatXml(String str) throws Exception {
		return formatXml(str,xmlCoding);
	}

	private String formatXml(String str,String coding) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(str);
		// 格式化输出格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(coding);
		StringWriter writer = new StringWriter();
		// 格式化输出流
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		// 将document写入到输出流
		
		xmlWriter.write(document);
		xmlWriter.close();
		//兼容老系统报文（10001、10003、10004都有报文头，10002无报文头）start
        Pattern p=Pattern.compile("\t|\r|\n");//\\s*|\t|\r|\n
        String returnStr=writer.toString();
        java.util.regex.Matcher m=p.matcher(returnStr);
        returnStr =m.replaceAll("");
        if(returnStr.indexOf("10002")>=0)
        	returnStr=returnStr.substring(returnStr.indexOf("<szlr>"), returnStr.length());
        //兼容老系统报文（10001、10003、10004都有报文头，10002无报文头）end
		return returnStr;
	}

}

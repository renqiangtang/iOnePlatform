package com.bank.service;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.bank.dao.BankBaseDao;
import com.bank.dao.BankDao;
import com.bank.util.BankUtil;
import com.bank.util.MD5;
import com.bank.util.XmlInput;
import com.bank.util.XmlOutput;
import com.bank.util.XmlUtil;
import com.bank.util.XmlVo;
import com.base.service.BaseService;
import com.base.utils.CharsetUtils;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class ImitateBankService extends BaseService {
	private static Logger log = Logger.getLogger(ImitateBankService.class);

	public ParaMap send10002(ParaMap in) throws Exception{
		String childNo = in.getString("childNo");
		String amount = in.getString("amount");
		String currency = in.getString("currency");
		String transTime = in.getString("transTime");
		String bankid = in.getString("bankid");
		String rate = in.getString("rate");

		String str="<?xml version='1.0' encoding='GBK' standalone='no'?><szlr>" +
			"<input><trxcode>10002</trxcode>" +

			"<busino>DK"+getBankLSH()+"</busino>" +

			"<bankid>"+bankid+"</bankid>" +
			"<draccbank>银行</draccbank>"+
			"<draccsubbank>银行深圳分行</draccsubbank>"+
			"<draccno>123</draccno>" +

			"<draccname>师帅</draccname>" +
			"<craccno>"+childNo+"</craccno>" +
			"<craccname>师帅</craccname>" +
			"<amount>"+amount+"</amount>" +
			"<currency>"+currency+"</currency>" +
			"<rate>"+rate+"</rate>" ;
		if(StrUtils.isNotNull(transTime)){
			transTime = transTime.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
			str = str + "<transtime>"+transTime+"</transtime>";
		}

		str = str + "<busiid></busiid>"+
			"<remark>备注</remark>" +
			"<rsa>10002DK20110903002674nullnull4000023009027339382国土局土地交易中心2628481497利息</rsa>"
			+"</input>" +
			"<output>" +
			"<status>交易状态</status>" +
			"<retcode>交易结果</retcode>" +
			"<retmsg>响应时间</retmsg>" +
			"<commseq></commseq>" +
			"</output></szlr>";
		BankBaseDao bankbaseDao = (BankBaseDao)BankUtil.createInstance("BK01");
		String result = bankbaseDao.dealWith10002(str);
		result = URLDecoder.decode(result,CharsetUtils.utf);
		ParaMap out = new ParaMap();
		out.put("msg", result);
		return out;
	}



	public ParaMap send10001(ParaMap in) throws Exception{
		String centerNo = in.getString("centerNo");
		String accountName = in.getString("accountName");
		String bankid = in.getString("bankid");
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();

		BankDao _bankDao = new BankDao();
		String busino = "DK"+_bankDao.getBankLSH();
		input.setTrxcode("10001");
		input.setBusino(busino);
		input.setOrgno(centerNo);
		input.setUsername(accountName);
		input.setRemark("开户");
		input.setBankid(bankid);
		String rsa=input.getTrxcode()+input.getBusino()+input.getAccno()+input.getRemark()+input.getBankid();
		rsa=MD5.MD5Encode(rsa);
		input.setRsa(rsa);
		output.setStatus("--");
		output.setRetcode("--");
		output.setRetmsg("--");
		output.setAccno("--");
		output.setCommseq("--");
		vo.setInput(input);
		vo.setOutput(output);
		String xml = xmlUtil.writeXMLFile(vo);
		String businessId = _bankDao.saveXml(busino, bankid, "10001", xml,"");
		String feedBack = _bankDao.sendXml(xml, bankid, businessId);
		ParaMap out = new ParaMap();
		out.put("msg", feedBack);
		return out;
	}

	public ParaMap send10003(ParaMap in) throws Exception{
		String benjin = in.getString("benjin");
		String no = in.getString("no");
		String beginDate = in.getString("beginDate");
		String endDate = in.getString("endDate");
		String bankid = in.getString("bankid");
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();

		BankDao _bankDao = new BankDao();
		String busino = "DK"+_bankDao.getBankLSH();
		input.setTrxcode("10003");
		input.setBusino(busino);
		input.setBenjin(benjin);
		input.setAccno(no);
		input.setStartdate(beginDate);
		input.setEnddate(endDate);
		input.setRemark("计息");
		input.setBankid(bankid);
		String rsaStr=input.getTrxcode()+input.getBusino()+input.getBenjin()+input.getAccno()+input.getStartdate()+input.getEnddate()+input.getRemark()+input.getBankid();
		rsaStr=MD5.MD5Encode(rsaStr);
		input.setRsa(rsaStr);
		output.setStatus("--");
		output.setRetcode("--");
		output.setAmount("--");
		output.setRetmsg("--");
		output.setCommseq("--");

		vo.setInput(input);
		vo.setOutput(output);
		String xml = xmlUtil.writeXMLFile(vo);
		String businessId = _bankDao.saveXml(busino, bankid, "10003", xml,"");
		String feedBack = _bankDao.sendXml(xml, bankid, businessId);
		ParaMap out = new ParaMap();
		out.put("msg", feedBack);
		return out;
	}



	public ParaMap send10004(ParaMap in) throws Exception{
		String centerNo = in.getString("centerNo");
		String name = in.getString("name");
		String no = in.getString("no");
		String amoutStr = in.getString("amoutStr");
		String bankId = in.getString("bankId");
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();

		DecimalFormat df1 = new DecimalFormat("############0.00"); 
		BankDao _bankDao = new BankDao();

		String busino = "DK"+_bankDao.getBankLSH();
		input.setTrxcode("10004");
		input.setBusino(busino);
		input.setTrxcacc(centerNo);
		input.setTrxcname(name);
		input.setAccno(no);
		input.setAmount(amoutStr);
		input.setRemark("子转总");
		input.setBankid(bankId);
		String rsaStr=input.getTrxcode()+input.getBusino()+input.getTrxcacc()+input.getTrxcname()+input.getAccno() +input.getAmount()+input.getRemark()+input.getBankid();
		System.out.println("--->rsaStr="+rsaStr);
		rsaStr=MD5.MD5Encode(rsaStr);
		input.setRsa(rsaStr);
		output.setStatus("--");
		output.setRetcode("--");
		output.setRetmsg("--");
		output.setCommseq("--");

		vo.setInput(input);
		vo.setOutput(output);
		String xml = xmlUtil.writeXMLFile(vo);
		String businessId = _bankDao.saveXml(busino, bankId, "10004", xml, "");
		String feedBack = _bankDao.sendXml(xml, bankId, businessId);
		ParaMap out = new ParaMap();
		out.put("msg", feedBack);
		return out;
	}



	public ParaMap send10005(ParaMap in) throws Exception{
		String accountNo = in.getString("accountNo");
		String bankid = in.getString("bankid");
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();

		BankDao _bankDao = new BankDao();
		String busino = "DK"+_bankDao.getBankLSH();
		input.setTrxcode("10005");
		input.setBusino(busino);
		input.setAccno(accountNo);
		input.setRemark("销户");
		input.setBankid(bankid);
		String rsa=input.getTrxcode()+input.getBusino()+input.getAccno()+input.getRemark()+input.getBankid();
		rsa=MD5.MD5Encode(rsa);
		input.setRsa(rsa);
		output.setStatus("--");
		output.setRetcode("--");
		output.setRetmsg("--");
		output.setCommseq("--");
		vo.setInput(input);
		vo.setOutput(output);
		String xml = xmlUtil.writeXMLFile(vo);
		String businessId = _bankDao.saveXml(busino, bankid, "10005", xml,"");
		String feedBack = _bankDao.sendXml(xml, bankid, businessId);
		ParaMap out = new ParaMap();
		out.put("msg", feedBack);
		return out;
	}


	public String getBankLSH() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long r1 = System.currentTimeMillis(); 
		String dateStr = sdf.format(DateUtils.now());
		if (dateStr != null){
			dateStr = dateStr.replaceAll("-", "");
		}
		String r1Str=String.valueOf(r1);
		if(r1Str.length()>=6){
			return dateStr+r1Str.substring(r1Str.length()-6,r1Str.length());
		}else
			return dateStr+r1Str;
	}

}

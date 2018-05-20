package com.test.service;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.bank.dao.BankBaseDao;
import com.bank.util.BankUtil;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;

public class BankTestService extends BaseService {
	private static Logger log = Logger.getLogger(TService.class);
	
	public ParaMap send10002(ParaMap in) throws Exception{
		String childNo = in.getString("childNo");
		String str="<?xml version='1.0' encoding='GBK' standalone='no'?><szlr>" +
  		"<input><trxcode>10002</trxcode>" +

  		"<busino>DK"+getBankLSH()+"</busino>" +

  		"<bankid>BK01</bankid>" +
  		"<draccbank>工商银行222</draccbank>"+
  		"<draccsubbank>工商银行深圳分行福田支行222</draccsubbank>"+
  		"<draccno>123</draccno>" +

  		"<draccname>师帅</draccname>" +
  		"<craccno>"+childNo+"</craccno>" +
  		"<craccname>师帅</craccname>" +
  		"<amount>60000000</amount>" +
  		"<transtime>20120522101010</transtime>" +

  		"<busiid></busiid>"+
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
		ParaMap out = new ParaMap();
		out.put("msg", result);
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

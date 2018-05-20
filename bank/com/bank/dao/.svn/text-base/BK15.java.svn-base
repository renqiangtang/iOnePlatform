package com.bank.dao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import com.bank.util.MD5;
import com.bank.util.XmlInput;
import com.bank.util.XmlOutput;
import com.bank.util.XmlUtil;
import com.bank.util.XmlVo;
import com.base.utils.ParaMap;

public class BK15 extends BankBaseDao{
	
//	public String createChildAccount(String accountName,String bankid,String centerNo,String currency) throws Exception{
//		System.out.println("bk42");
//		return super.createChildAccount(accountName, bankid, centerNo, currency);
//	}
	public String createChildAccount(String accountName,String bankid,String centerNo,String currency) throws Exception{
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();
		
		BankDao _bankDao = new BankDao();
		String busino = "DK"+_bankDao.getBankLSH();
		input.setTrxcode("10001");
		input.setBusino(busino);
		input.setOrgno(centerNo);
		//河源，不要
		//input.setCurrency(currency);
		input.setUsername(accountName);
		input.setRemark("开户");
		input.setBankid(bankid);
		//String rsa=input.getTrxcode()+input.getBusino()+input.getOrgno()+input.getCurrency()+input.getUsername()+input.getRemark()+input.getBankid();
		String rsa=input.getTrxcode()+input.getBusino()+input.getOrgno()+input.getUsername()+input.getRemark()+input.getBankid();
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
        int status = _bankDao.dealBankFeedBack(businessId, feedBack);
        String result = null;
        if(status == 2){
        	 XmlOutput backOuput = _bankDao.parseFeedBack(feedBack);
        	 if(backOuput != null ){
        		 result = backOuput.getAccno();
        	 }
        }
        return result;
        			
		
}
	public String submitChildAccount(String targetId) throws Exception{
		
		XmlVo vo = new XmlVo();
		XmlInput input = new XmlInput();
		XmlOutput output = new XmlOutput();
		XmlUtil xmlUtil = new XmlUtil();
		
		DecimalFormat df1 = new DecimalFormat("############0.00"); 
		BankDao _bankDao = new BankDao();
		
		ParaMap childNoMap = _bankDao.getSubmitParam(targetId);
		if(childNoMap == null ){
			return null;
		}
		Iterator iter = childNoMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    ParaMap map = (ParaMap)entry.getValue(); 
		    
		    String centerNo = map.getString("centerNo");
			String name = map.getString("name");
			String no = map.getString("no");
			BigDecimal amount = map.getBigDecimal("amount");
			String bankId = map.getString("bankId");
			String billId = map.getString("billId");
			
			String amoutStr = this.changeMoneyFormat(amount);
			
			
			String busino = "DK"+_bankDao.getBankLSH();
			input.setTrxcode("10004");
			input.setBusino(busino);
			input.setTrxcacc(centerNo);
			input.setTrxcname(name);
			input.setAccno(no);
			input.setAmount(amoutStr);
			input.setRemark("子转总");
			input.setBankid(bankId);
			String rsa=input.getTrxcode()+input.getBusino()+input.getTrxcacc()+input.getTrxcname()+input.getAccno()+input.getAmount()+input.getRemark()+input.getBankid();
			System.out.println("--->rsaStr="+rsa);
			rsa=MD5.MD5Encode(rsa);
			input.setRsa(rsa);
			output.setStatus("--");
			output.setRetcode("--");
			output.setRetmsg("--");
			output.setCommseq("--");
			
			vo.setInput(input);
			vo.setOutput(output);
	        String xml = xmlUtil.writeXMLFile(vo);
			String businessId = _bankDao.saveXml(busino, bankId, "10004", xml, billId);
			
		} 
		
		return null;
	}
	

	public String dealWith10002(String xml) throws Exception{
		System.out.println("bk15");
		return super.dealWith10002(xml);
	}
	
	
	
}

package com.bank.dao;

import com.bank.util.MD5;
import com.bank.util.XmlInput;
import com.bank.util.XmlOutput;
import com.bank.util.XmlUtil;
import com.bank.util.XmlVo;

public class BK42 extends BankBaseDao{
	
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
        
//        XmlOutput backOuput = null;
//		for(int i = 0 ; i < 20 ; i ++){
//			try{
//			   Thread.sleep(1000);//等待1秒
//			   BankDao bankDao = new BankDao();
//			   backOuput = bankDao.getOutput(businessId);
//			   if(backOuput!=null && backOuput.getStatus() !=null){
//				   break;
//			   }
//			}catch(Exception e){
//			}
//		}
			
		
}

	public String dealWith10002(String xml) throws Exception{
		System.out.println("bk42");
		return super.dealWith10002(xml);
	}
	
	
	
}

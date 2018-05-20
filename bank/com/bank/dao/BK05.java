package com.bank.dao;

public class BK05 extends BankBaseDao{
	
	public String createChildAccount(String accountName,String bankid,String centerNo,String currency) throws Exception{
		System.out.println("11111111111111111111");
		return super.createChildAccount(accountName, bankid, centerNo, currency);

	}

	public String dealWith10002(String xml) throws Exception{
		System.out.println("11111111111111111111");
		return super.dealWith10002(xml);
	}
}

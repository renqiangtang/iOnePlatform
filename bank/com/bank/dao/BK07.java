package com.bank.dao;

public class BK07 extends BankBaseDao{
	
	public String createChildAccount(String accountName,String bankid,String centerNo,String currency) throws Exception{
		System.out.println("bk07");
		return super.createChildAccount(accountName, bankid, centerNo, currency);
	}

	public String dealWith10002(String xml) throws Exception{
		System.out.println("bk07");
		return super.dealWith10002(xml);
	}

}

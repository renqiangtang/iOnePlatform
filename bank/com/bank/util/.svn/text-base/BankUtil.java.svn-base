package com.bank.util;

import com.bank.dao.BankBaseDao;

public class BankUtil {

	public static Object createInstance(String bankid){  
		try{
			return (BankBaseDao) Class.forName("com.bank.dao." + bankid).newInstance();
		}catch(Exception e){
			return new BankBaseDao();
		}
	} 
}

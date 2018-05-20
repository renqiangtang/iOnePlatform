package com.bank.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;


/**
 * @author shis , 2012-05-10
 */
public class ParameterUtil {
	
	public static String bankClient_ip = null;
	public static int bankClient_port = 0;
	public static String bankClient_app = null;
	
	private Properties bankClient_properties;
	
	public ParameterUtil() {
		bankClient_properties = new Properties();
	}
	
	
	public void loadBankClicenParam() throws Exception{
		if(bankClient_ip == null || "".equals(bankClient_ip) || bankClient_port ==0 ){
			 this.readProperties();
			 bankClient_ip = bankClient_properties.getProperty("bankClient_ip");
			 bankClient_port = Integer.parseInt(bankClient_properties.getProperty("bankClient_port"));
			 bankClient_app = bankClient_properties.getProperty("bankClient_app");
	    }
	}
	
	private void readProperties() throws Exception{
		try{
			InputStream in = ParameterUtil.class.getResourceAsStream("/app.properties");
			bankClient_properties = new Properties(); 
			bankClient_properties.load(in);
		}catch (IOException e1) {   
        	e1.printStackTrace();   
        }
     }
	
	private void readProperties2() throws Exception{
		FileInputStream inputStream = null;
		try{
			inputStream = new FileInputStream(getWEBINFAddress()+File.separator+"config"+File.separator+"app.properties");
			bankClient_properties = new Properties(); 
			bankClient_properties.load(inputStream);
		}catch (IOException e1) {   
        	e1.printStackTrace();   
        }finally{
        	inputStream.close();
        } 
     }
	
	private String getWEBINFAddress() throws Exception{
		  Class theClass = ParameterUtil.class;
		  java.net.URL u = theClass.getResource("");
		  //str会得到这个函数所在类的路径
		  String str = u.toString();
		  //截去一些前面6个无用的字符
		  str = str.substring(6, str.length());
		  //将%20换成空格（如果文件夹的名称带有空格的话，会在取得的字符串上变成%20）
		  str = str.replaceAll("%20", " ");
		  //查找“WEB-INF”在该字符串的位置
		  int num = str.indexOf("WEB-INF");
		  //截取即可
		  str = str.substring(0, num + "WEB-INF".length());
		  return str;
	 }

}

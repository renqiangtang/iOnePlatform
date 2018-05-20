package com.bank.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ResourceBundle;

public class SocketUtil {
	
   public static String bankServer_ip = null;
   public static int bankServer_port = 0;
   public static int bankSever_timeout = 0;
	
	public SocketUtil() {
        super();
    }
	
	 public static void loadParam() {
	       
		  ResourceBundle rb = ResourceBundle.getBundle("com.bank.socketUtil");
	      bankServer_ip=rb.getString("bankServer_ip");
		  bankServer_port=Integer.parseInt(rb.getString("bankServer_port"));
		  bankSever_timeout=Integer.parseInt(rb.getString("bankSever_timeout"));
	     
	 }

	 public String socket(String SendStr) {
		 
		   ResourceBundle rb = ResourceBundle.getBundle("trade.socketUtil");
		 
	        OutputStream outputstream = null;
	        BufferedReader inputstream = null;
	        StringBuffer backStr = new StringBuffer();
	       
	        String ip = bankServer_ip;
            int port = bankServer_port;
            int timeout = bankSever_timeout;
	        
	        
	        try {
	            long time1 = System.currentTimeMillis();
	            java.net.Socket icbcSocket = new java.net.Socket(ip, port);
	            icbcSocket.setSoTimeout(timeout);
	            outputstream = icbcSocket.getOutputStream();
	            inputstream = new BufferedReader(new InputStreamReader(icbcSocket.getInputStream()));
	            byte buff[] = SendStr.getBytes();
	            outputstream.write(buff);
	            outputstream.flush();
	            String line = "";
	            while ((line = inputstream.readLine()) != null) {
	                backStr.append(line);
	            }
	            long time2 = System.currentTimeMillis();
	            long time3 = time2 - time1;
	            outputstream.close();
	            inputstream.close();
	           
	        } catch (Exception e) {
	            try {
	                outputstream.close();
	                inputstream.close();
	            } catch (Exception e1) {
	            }
	            return null;
	        }
	        return backStr.toString();
	    }
	 
	 
	 	public static void main(String[] args) {
	        
	    }
}

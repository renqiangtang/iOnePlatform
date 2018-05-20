package com.base.http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import com.base.utils.CharsetUtils;
import com.base.utils.StreamUtils;

public class HttpManager {
	private static final Logger log = Logger.getLogger(HttpManager.class);
	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[1];
			trustAllCerts[0] = new SSLTrustManager();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static byte[] getDataByte(String uri) {
		log.debug("getData_url:" + uri);
		long begin = System.currentTimeMillis();
		byte[] buf = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setReadTimeout(30000);
			urlConn.setRequestProperty("Accept-Charset", CharsetUtils.utf);
			urlConn.setRequestProperty("contentType", CharsetUtils.utf);
			InputStream ins = urlConn.getInputStream();
			buf = StreamUtils.InputStreamToByte(ins);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.debug("end_url: time(" + (System.currentTimeMillis() - begin)
				+ ") size(" + buf.length + ")b");
		return buf;
	}

	public static String getDataString(String uri) {
		String url = uri + "&outer=true";
		byte[] buf = getDataByte(url);
		String s = new String(buf);
		s = URLDecoder.decode(s);
		return s;
	}
	
	public static String getDataString2(String uri) {
		String url = uri + "&outer=false";
		byte[] buf = getDataByte(url);
		Properties props=System.getProperties(); //系统属性
		String systemCode = props.getProperty("file.encoding");
		System.out.println("systemCode>>>>>>>>>>>>>>>>>>>>>>>>>"+ systemCode);
		String s = new String(buf);
		System.out.println("s>>>>>>>>>>>>>>>>>>>>>>>>>"+ s);
		try{
			s = URLDecoder.decode(s,systemCode);
		}catch(Exception e){
			s = URLDecoder.decode(s);
		}
		System.out.println("s>>>>>>>>>>>>>>>>>>>>>>>>>"+ s);
		return s;
	}

	public static void main(String[] args) {
		// String
		// cc=getDataString("http://192.168.5.240/std/data?module=portal&service=Query&method=queryCanton");
		// System.out.println(cc);
		// System.out.println(CharsetUtils.getEncoding(cc));
		String url = "http://192.168.5.240/std/download?module=trademan&service=DocConfig&method=directDownload";
		url += "&path=config\\济南市国土资源土地储备交易中心\\门户文档\\成交动画.html";
		byte[] buf = getDataByte(url);

	}
}

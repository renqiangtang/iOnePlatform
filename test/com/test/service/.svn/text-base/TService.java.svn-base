package com.test.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.Pdf2SwfUtils;
import com.base.utils.StreamUtils;

public class TService extends BaseService {
	private static Logger log = Logger.getLogger(TService.class);

	public ParaMap testFlex(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		out.put("name", "zjl");
		out.put("age", 31);
		out.put("m", true);
		return out;
	}

	public ParaMap testBigText(ParaMap in) throws Exception {
		String ttt = in.getString("ttt");
		ParaMap out = new ParaMap();
		System.out.println(ttt);
		out.put("msg", "return Value:" + ttt);
		return out;
	}

	public ParaMap testHttpClient(ParaMap in) throws Exception {
		InputStream ins = this.getRequest().getInputStream();
		String xml = StreamUtils.InputStreamToString(ins);
		System.out.println("testHttpClient xml>>>" + xml);
		ParaMap out = new ParaMap();
		return out;
	}

	public ParaMap tomcat(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String cmd = "D:\\ide\\tomcat-6.0.35\\bin\\deploy.bat";
		Process process = Runtime.getRuntime().exec(cmd);
		final InputStream is1 = process.getInputStream();
		final InputStream is2 = process.getErrorStream();
		// 启动单独的线程来清空输出的缓冲区
		new Thread(new Runnable() {
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is1));
				try {
					String line = null;
					while ((line = br.readLine()) != null)
						log.debug(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		// 启动单独的线程来清空错误的缓冲区
		new Thread() {
			public void run() {
				BufferedReader br2 = new BufferedReader(new InputStreamReader(
						is2));
				try {
					String line = null;
					while ((line = br2.readLine()) != null) {
						log.debug(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
		process.waitFor();
		return out;
	}

}

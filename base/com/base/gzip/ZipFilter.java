package com.base.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ZipFilter implements Filter {
	private static final Logger log = Logger.getLogger(ZipFilter.class);

	public void init(FilterConfig filterConfig){
		log.debug("服务器正在启动..GZIPEncodeFilter..");
	}
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		String transferEncoding = getGZIPEncoding(req);
		String uri = req.getServletPath();
		long begin = System.currentTimeMillis();
		if (transferEncoding != null) {
			WrapResponse zipRes = new WrapResponse(res);
			chain.doFilter(req, zipRes);
			res.setHeader("Content-Encoding", "gzip");
			byte[] orgData = zipRes.getResponseData();
			byte[] data = gzip(orgData);
			res.setContentLength(data.length);
			ServletOutputStream output = response.getOutputStream();
			output.write(data);
			output.flush();
		} else {
			chain.doFilter(req, res);
		}
		log.debug("uri>>>" + uri + " time:"
				+ (System.currentTimeMillis() - begin));
	}

	public void destroy() {
		log.debug("服务器正在关闭..GZIPEncodeFilter..");
	}

	private static String getGZIPEncoding(HttpServletRequest request) {
		String acceptEncoding = request.getHeader("Accept-Encoding");
		if (acceptEncoding == null)
			return null;
		acceptEncoding = acceptEncoding.toLowerCase();
		if (acceptEncoding.indexOf("x-gzip") >= 0) {
			return "x-gzip";
		}
		if (acceptEncoding.indexOf("gzip") >= 0) {
			return "gzip";
		}
		return null;


	}

	public static byte[] gzip(byte[] data) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		GZIPOutputStream output = null;
		try {
			output = new GZIPOutputStream(byteOutput);
			output.write(data);
		} catch (IOException e) {
			throw new RuntimeException("G-Zip failed.", e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
		return byteOutput.toByteArray();
	}
}

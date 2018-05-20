package com.base.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.base.utils.CharsetUtils;

public class WrapResponse extends HttpServletResponseWrapper {

	private WrapServletStream out = null;
	private PrintWriter pw = null;

	private ByteArrayOutputStream buffer = null;

	public WrapResponse(HttpServletResponse response) throws IOException {
		super(response);
		buffer = new ByteArrayOutputStream();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new WrapServletStream(buffer);
		}
		return out;
	}

	public PrintWriter getWriter() throws IOException {
		if (pw == null) {
			pw = new PrintWriter(new OutputStreamWriter(buffer,
						CharsetUtils.utf));
		}
		return pw;
	}

	public void flush() {
		try {
			if (pw != null) {
				pw.close();
				pw.flush();
			}
			if (out != null) {
				out.close();
				out.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public byte[] getResponseData() throws IOException {
		flushBuffer();
		return buffer.toByteArray();
	}

}

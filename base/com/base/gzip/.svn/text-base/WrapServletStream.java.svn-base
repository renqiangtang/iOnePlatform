package com.base.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class WrapServletStream extends ServletOutputStream {

	private ByteArrayOutputStream buffer;

	public WrapServletStream(ByteArrayOutputStream source) throws IOException {
		this.buffer = source;
	}

	public void write(byte[] buf) throws IOException {
		buffer.write(buf);
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		buffer.write(buf, off, len);
	}

	public void write(int c) throws IOException {
		buffer.write(c);
	}

	public void flush() throws IOException {
		buffer.flush();
	}

	public void close() throws IOException {
		buffer.close();
	}

}

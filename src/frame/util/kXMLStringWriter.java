package frame.util;

import java.io.Writer;
import java.lang.*;
public class kXMLStringWriter extends Writer {
	private final StringBuffer buf;

	/**
    */

	public kXMLStringWriter() {
		buf = new StringBuffer();
	}

	/**
    */

	@Override
	public void close() {
	}

	/**
    */

	@Override
	public void flush() {
	}

	/**
    */

	@Override
	public void write(int c) {
		buf.append((char) c);
	}

	/**
    */

	@Override
	public void write(char b[], int off, int len) {
		buf.append(b, off, len);
	}

	/**
    */

	@Override
	public void write(String str) {
		buf.append(str);
	}

	/**
    */

	@Override
	public String toString() {
		return buf.toString();
	}
}

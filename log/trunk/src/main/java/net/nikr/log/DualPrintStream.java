/*
 * Copyright 2009, Niklas Kyster Rasmussen
 *
 * This file is part of NiKR Log.
 *
 * NiKR Log is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * NiKR Log is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NiKR Log; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 *
 * @author Niklas
 */
public class DualPrintStream extends PrintStream {
	//PrintStream ps1;
	PrintStream sytemout;
	PrintStream filePrintStream;
	String filename;


	public DualPrintStream(String filename, PrintStream sytemout) throws FileNotFoundException {
		super(sytemout);
		this.filename = filename;
		this.sytemout = sytemout;
	}

	private PrintStream getFilePrintStream(){
		try {
			filePrintStream = new PrintStream(new FileOutputStream(new File(filename), true), true, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		} catch (FileNotFoundException ex) {
		}
		return filePrintStream;

	}

	private void end(){
		if (filePrintStream != null){
			filePrintStream.close();
		}
	}


	@Override
	public void flush() {
		getFilePrintStream().flush();
		sytemout.flush();
		end();
	}
	@Override
	public boolean checkError() {
		boolean errorFlag1;
		boolean errorFlag2;
		errorFlag1 = getFilePrintStream().checkError();
		errorFlag2 = sytemout.checkError();
		end();
		return errorFlag1 || errorFlag2;
	}
	@Override
	public void close() {
		getFilePrintStream().close();
		sytemout.close();
		end();
	}
	@Override
	public void write(int b) {
		getFilePrintStream().write(b);
		sytemout.write(b);
		end();
	}
	@Override
	public void write(byte buf[], int off, int len) {
		getFilePrintStream().write(buf, off, len);
		sytemout.write(buf, off, len);
		end();
	}
	@Override
	public void print(boolean b) {
		getFilePrintStream().print(b);
		sytemout.print(b);
		end();
	}
	@Override
	public void print(char c) {
		getFilePrintStream().print(c);
		sytemout.print(c);
		end();
	}
	@Override
	public void print(int i) {
		getFilePrintStream().print(i);
		sytemout.print(i);
		end();
	}
	@Override
	public void print(long l) {
		getFilePrintStream().print(l);
		sytemout.print(l);
		end();
	}
	@Override
	public void print(float f) {
		getFilePrintStream().print(f);
		sytemout.print(f);
		end();
	}
	@Override
	public void print(double d) {
		getFilePrintStream().print(d);
		sytemout.print(d);
		end();
	}
	@Override
	public void print(char s[]) {
		getFilePrintStream().print(s);
		sytemout.print(s);
		end();
	}
	@Override
	public void print(String s) {
		getFilePrintStream().print(s);
		sytemout.print(s);
		end();
	}
	@Override
	public void print(Object obj) {
		getFilePrintStream().print(obj);
		sytemout.print(obj);
		end();
	}
	@Override
	public void println() {
		getFilePrintStream().println();
		sytemout.println();
		end();
	}
	@Override
	public void println(boolean x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(char x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(int x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(long x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(float x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
	}
	@Override
	public void println(double x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(char x[]) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(String x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public void println(Object x) {
		getFilePrintStream().println(x);
		sytemout.println(x);
		end();
	}
	@Override
	public PrintStream printf(String format, Object ... args) {
		getFilePrintStream().printf(format, args);
		sytemout.printf(format, args);
		end();
		return this;
	}
	@Override
	public PrintStream printf(Locale l, String format, Object ... args) {
		getFilePrintStream().printf(l, format, args);
		sytemout.printf(l, format, args);
		end();
		return this;
	}
	@Override
	public PrintStream format(String format, Object ... args) {
		getFilePrintStream().printf(format, args);
		sytemout.printf(format, args);
		end();
		return this;
	}
	@Override
	public PrintStream format(Locale l, String format, Object ... args) {
		getFilePrintStream().printf(l, format, args);
		sytemout.printf(l, format, args);
		end();
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		getFilePrintStream().append(csq);
		sytemout.append(csq);
		end();
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		getFilePrintStream().append(csq, start, end);
		sytemout.append(csq, start, end);
		end();
		return this;
	}
	@Override
	public PrintStream append(char c) {
		getFilePrintStream().append(c);
		sytemout.append(c);
		end();
		return this;
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		getFilePrintStream().write(b);
		sytemout.write(b);
		end();
    }
}
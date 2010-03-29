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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Niklas
 */
public class DualPrintStream extends PrintStream {
	public static final int MAX_LINES = 1000; //50MB

	private PrintStream sytemOutPrintStream;
	private PrintStream filePrintStream;
	private String filename;


	public DualPrintStream(String filename, PrintStream sytemOutPrintStream) throws FileNotFoundException {
		super(sytemOutPrintStream);
		this.filename = filename;
		this.sytemOutPrintStream = sytemOutPrintStream;
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
			checkForMaxLines();
		}
	}

	private void checkForMaxLines(){
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
			String str;
			int linesCount = 0;
			while ((str = bufferedReader.readLine()) != null) {
				lines.add(str);
				linesCount++;
			}
			bufferedReader.close();
			if (linesCount > MAX_LINES){
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
				int start = lines.size()-MAX_LINES;
				if (start < 0) start = 0;
				for (int a = start; a < lines.size(); a++){
					if (a != start) bufferedWriter.write("\n");
					bufferedWriter.write(lines.get(a)+"\r");
				}
				bufferedWriter.close();
			}

		} catch (IOException ex) {
		}
	}


	@Override
	public void flush() {
		getFilePrintStream().flush();
		sytemOutPrintStream.flush();
		end();
	}
	@Override
	public boolean checkError() {
		boolean errorFlag1;
		boolean errorFlag2;
		errorFlag1 = getFilePrintStream().checkError();
		errorFlag2 = sytemOutPrintStream.checkError();
		end();
		return errorFlag1 || errorFlag2;
	}
	@Override
	public void close() {
		getFilePrintStream().close();
		sytemOutPrintStream.close();
		end();
	}
	@Override
	public void write(int b) {
		getFilePrintStream().write(b);
		sytemOutPrintStream.write(b);
		end();
	}
	@Override
	public void write(byte buf[], int off, int len) {
		getFilePrintStream().write(buf, off, len);
		sytemOutPrintStream.write(buf, off, len);
		end();
	}
	@Override
	public void print(boolean b) {
		getFilePrintStream().print(b);
		sytemOutPrintStream.print(b);
		end();
	}
	@Override
	public void print(char c) {
		getFilePrintStream().print(c);
		sytemOutPrintStream.print(c);
		end();
	}
	@Override
	public void print(int i) {
		getFilePrintStream().print(i);
		sytemOutPrintStream.print(i);
		end();
	}
	@Override
	public void print(long l) {
		getFilePrintStream().print(l);
		sytemOutPrintStream.print(l);
		end();
	}
	@Override
	public void print(float f) {
		getFilePrintStream().print(f);
		sytemOutPrintStream.print(f);
		end();
	}
	@Override
	public void print(double d) {
		getFilePrintStream().print(d);
		sytemOutPrintStream.print(d);
		end();
	}
	@Override
	public void print(char s[]) {
		getFilePrintStream().print(s);
		sytemOutPrintStream.print(s);
		end();
	}
	@Override
	public void print(String s) {
		getFilePrintStream().print(s);
		sytemOutPrintStream.print(s);
		end();
	}
	@Override
	public void print(Object obj) {
		getFilePrintStream().print(obj);
		sytemOutPrintStream.print(obj);
		end();
	}
	@Override
	public void println() {
		getFilePrintStream().println();
		sytemOutPrintStream.println();
		end();
	}
	@Override
	public void println(boolean x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(char x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(int x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(long x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(float x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
	}
	@Override
	public void println(double x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(char x[]) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(String x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public void println(Object x) {
		getFilePrintStream().println(x);
		sytemOutPrintStream.println(x);
		end();
	}
	@Override
	public PrintStream printf(String format, Object ... args) {
		getFilePrintStream().printf(format, args);
		sytemOutPrintStream.printf(format, args);
		end();
		return this;
	}
	@Override
	public PrintStream printf(Locale l, String format, Object ... args) {
		getFilePrintStream().printf(l, format, args);
		sytemOutPrintStream.printf(l, format, args);
		end();
		return this;
	}
	@Override
	public PrintStream format(String format, Object ... args) {
		getFilePrintStream().printf(format, args);
		sytemOutPrintStream.printf(format, args);
		end();
		return this;
	}
	@Override
	public PrintStream format(Locale l, String format, Object ... args) {
		getFilePrintStream().printf(l, format, args);
		sytemOutPrintStream.printf(l, format, args);
		end();
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		getFilePrintStream().append(csq);
		sytemOutPrintStream.append(csq);
		end();
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		getFilePrintStream().append(csq, start, end);
		sytemOutPrintStream.append(csq, start, end);
		end();
		return this;
	}
	@Override
	public PrintStream append(char c) {
		getFilePrintStream().append(c);
		sytemOutPrintStream.append(c);
		end();
		return this;
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		getFilePrintStream().write(b);
		sytemOutPrintStream.write(b);
		end();
    }
}
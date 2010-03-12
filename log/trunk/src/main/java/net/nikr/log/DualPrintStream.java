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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author Niklas
 */
public class DualPrintStream extends PrintStream {
	PrintStream ps1;
	PrintStream ps2;


	public DualPrintStream(PrintStream ps1, PrintStream ps2) throws FileNotFoundException {
		super(ps1);
		this.ps1  = ps1;
		this.ps2 = ps2;
	}

	@Override
	public void flush() {
		ps1.flush();
		ps2.flush();
	}
	@Override
	public boolean checkError() {
		boolean errorFlag1;
		boolean errorFlag2;
		errorFlag1 = ps1.checkError();
		errorFlag2 = ps2.checkError();
		return errorFlag1 || errorFlag2;
	}
	@Override
	public void close() {
		ps1.close();
		ps2.close();
	}
	@Override
	public void write(int b) {
		ps1.write(b);
		ps2.write(b);
	}
	@Override
	public void write(byte buf[], int off, int len) {
		ps1.write(buf, off, len);
		ps2.write(buf, off, len);
	}
	@Override
	public void print(boolean b) {
		ps1.print(b);
		ps2.print(b);
	}
	@Override
	public void print(char c) {
		ps1.print(c);
		ps2.print(c);
	}
	@Override
	public void print(int i) {
		ps1.print(i);
		ps2.print(i);
	}
	@Override
	public void print(long l) {
		ps1.print(l);
		ps2.print(l);
	}
	@Override
	public void print(float f) {
		ps1.print(f);
		ps2.print(f);
	}
	@Override
	public void print(double d) {
		ps1.print(d);
		ps2.print(d);
	}
	@Override
	public void print(char s[]) {
		ps1.print(s);
		ps2.print(s);
	}
	@Override
	public void print(String s) {
		ps1.print(s);
		ps2.print(s);
	}
	@Override
	public void print(Object obj) {
		ps1.print(obj);
		ps2.print(obj);
	}
	@Override
	public void println() {
		ps1.println();
		ps2.println();
	}
	@Override
	public void println(boolean x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(char x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(int x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(long x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(float x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(double x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(char x[]) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(String x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public void println(Object x) {
		ps1.println(x);
		ps2.println(x);
	}
	@Override
	public PrintStream printf(String format, Object ... args) {
		ps1.printf(format, args);
		ps2.printf(format, args);
		return this;
	}
	@Override
	public PrintStream printf(Locale l, String format, Object ... args) {
		ps1.printf(l, format, args);
		ps2.printf(l, format, args);
		return this;
	}
	@Override
	public PrintStream format(String format, Object ... args) {
		ps1.printf(format, args);
		ps2.printf(format, args);
		return this;
	}
	@Override
	public PrintStream format(Locale l, String format, Object ... args) {
		ps1.printf(l, format, args);
		ps2.printf(l, format, args);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		ps1.append(csq);
		ps2.append(csq);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		ps1.append(csq, start, end);
		ps2.append(csq, start, end);
		return this;
	}
	@Override
	public PrintStream append(char c) {
		ps1.append(c);
		ps2.append(c);
		return this;
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		ps1.write(b);
		ps2.write(b);
    }
}
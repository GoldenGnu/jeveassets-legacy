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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;


public class Log {

	private static FileWriter fileWriter;
	private static boolean bInitialized = false;
	private static boolean bDebug = false;
	private static String sFilename = "";

	public static void enableDebug(){
		bDebug = true;
	}

	public static void init(Class clazz){
		init(clazz, "log.txt", false);
	}
	public static void init(Class clazz, String filename){
		init(clazz, filename, false);
	}
	public static void init(Class clazz, boolean debug){
		init(clazz, "log.txt", debug);
	}

	public static void init(Class inputClazz, String filename, boolean debug){
		sFilename = getLogFilename(inputClazz, filename);
		bDebug = debug;
		bInitialized = true;

		System.setProperty("sun.awt.exception.handler", "net.nikr.log.NikrUncaughtExceptionHandler");
		Thread.setDefaultUncaughtExceptionHandler( new NikrUncaughtExceptionHandler());

		try {
			fileWriter = new FileWriter(sFilename, false);
			fileWriter.write("");
			fileWriter.close();
		} catch (IOException ex) {
			failed("Clearing log failed (CLEAR: "+sFilename+")", ex);
		}
	}
	public static void debug(Object obj){
		debug(String.valueOf(obj));
	}
	public static void debug(boolean b){
		debug(String.valueOf(b));
	}
	public static void debug(char c){
		debug(String.valueOf(c));
	}
	public static void debug(char[] s){
		debug(String.valueOf(s));
	}
	public static void debug(double d){
		debug(String.valueOf(d));
	}
	public static void debug(float f){
		debug(String.valueOf(f));
	}
	public static void debug(int i){
		debug(String.valueOf(i));
	}
	public static void debug(long l){
		debug(String.valueOf(l));
	}
	public static void debug(String s){
		if (bDebug) print("(Debug) "+s);
	}
	public static void error(Throwable e){
		String s = "An unknown error has occurred!\n\nPlease email the log.txt to nkr@niklaskr.dk";
		print("(Error) "+s, true, e);
		printErrorToScreen(s);
		System.exit(1);
	}
	public static void error(Object obj, Throwable e){
		error(String.valueOf(obj), e);
	}
	public static void error(boolean b, Throwable e){
		error(String.valueOf(b), e);
	}
	public static void error(char c, Throwable e){
		error(String.valueOf(c), e);
	}
	public static void error(char[] s, Throwable e){
		error(String.valueOf(s), e);
	}
	public static void error(double d, Throwable e){
		error(String.valueOf(d), e);
	}
	public static void error(float f, Throwable e){
		error(String.valueOf(f), e);
	}
	public static void error(int i, Throwable e){
		error(String.valueOf(i), e);
	}
	public static void error(long l, Throwable e){
		error(String.valueOf(l), e);
	}
	public static void error(String s, Throwable e){
		print("(Error) "+s, true, e);
		printErrorToScreen(s);
		System.exit(1);
	}
	public static void error(Object obj){
		error(String.valueOf(obj));
	}
	public static void error(boolean b){
		error(String.valueOf(b));
	}
	public static void error(char c){
		error(String.valueOf(c));
	}
	public static void error(char[] s){
		error(String.valueOf(s));
	}
	public static void error(double d){
		error(String.valueOf(d));
	}
	public static void error(float f){
		error(String.valueOf(f));
	}
	public static void error(int i){
		error(String.valueOf(i));
	}
	public static void error(long l){
		error(String.valueOf(l));
	}
	public static void error(String s){
		printErrorToScreen(s);
		print("(Error) "+s, true);
		System.exit(1);
	}
	public static void info(Object obj){
		info(String.valueOf(obj));
	}
	public static void info(boolean b){
		info(String.valueOf(b));
	}
	public static void info(char c){
		info(String.valueOf(c));
	}
	public static void info(char[] s){
		info(String.valueOf(s));
	}
	public static void info(double d){
		info(String.valueOf(d));
	}
	public static void info(float f){
		info(String.valueOf(f));
	}
	public static void info(int i){
		info(String.valueOf(i));
	}
	public static void info(long l){
		info(String.valueOf(l));
	}
	public static void info(String s){
		print("(Info) "+s);
	}
	public static void warning(Object obj){
		warning(String.valueOf(obj));
	}
	public static void warning(boolean b){
		warning(String.valueOf(b));
	}
	public static void warning(char c){
		warning(String.valueOf(c));
	}
	public static void warning(char[] s){
		warning(String.valueOf(s));
	}
	public static void warning(double d){
		warning(String.valueOf(d));
	}
	public static void warning(float f){
		warning(String.valueOf(f));
	}
	public static void warning(int i){
		warning(String.valueOf(i));
	}
	public static void warning(long l){
		warning(String.valueOf(l));
	}
	public static void warning(String s){
		print("(Warning) "+s, true);
	}
	/*
	public static void showWarning(String s){
		print("(Warning) "+s, true);
		printWarningToScreen(s);
	}
	 */

	private static void print(String s){
		print(s, false, null);
	}
	private static void print(String s, boolean e){
		print(s, e, null);
	}
	private static void print(String s, boolean e, Throwable t){
		if (!bInitialized){
			failed("Log.init() must be called before using the log");
		}
		// Output string to consol
		if(e){
			System.err.println(s);
		} else {
			System.out.println(s);
		}
		//Output stack trace to consol
		if(t != null){
			t.printStackTrace();
		}
		//open file
		try {
			fileWriter = new FileWriter(sFilename, true);
		} catch (IOException ex) {
			failed("Logging to file failed (OPEN: "+sFilename+")", ex);
		}
		// Output string to file
		try {
			fileWriter.write(s+"\r\n");
		} catch (IOException ex) {
			failed("Logging to file failed (WRITE STRING)", ex);
		}
		// Output stack trace to file
		if(t != null){
			try {
				PrintWriter printWriter = new PrintWriter(fileWriter);
				t.printStackTrace(printWriter);
			} catch (Exception ex) {
				failed("Logging to file failed (WRITE STACK TRACE)", ex);
			}
		}
		//Close file
		try {
			fileWriter.close();
		} catch (IOException ex) {
			failed("Logging to file failed (CLOSE)", ex);
		}
	}

	private static void failed(String s){
		failed(s, null);
	}

	private static void failed(String s, Throwable e){
		System.err.println(s);
		printErrorToScreen(s);
		if (e != null) e.printStackTrace();
		System.exit(1);
	}

	private static void printErrorToScreen(String s){
		JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private static void printWarningToScreen(String s){
		JOptionPane.showMessageDialog(null, s, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	private static String getLogFilename(Class inputClazz, String filename){
		try {
			File file = new File(inputClazz.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			return file.getAbsolutePath()+File.separator+filename;
		} catch (URISyntaxException ex) {
			failed("Getting log filename failed", ex);
		}
		return "";
	}
}

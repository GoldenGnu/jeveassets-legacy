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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;


public class Log {

	private static final int INFO = 1;
	private static final int DEBUG = 2;
	private static final int WARNING = 3;
	private static final int ERROR = 4;
	private static final int MAX_BYTE_SIZE = 52428800; //50MB

	private static boolean bInitialized = false;
	private static boolean bDebug = false;
	private static String sLogFilename = "";
	private static String sErrorDirectory = "";
	private static String sUncaughtErrorMessage = "";
	private static DateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmm", new Locale("en"));

	public static void enableDebug(){
		bDebug = true;
	}

	public static void init(Class clazz, String sUncaughtErrorMessage){
		init(clazz, sUncaughtErrorMessage, false);
	}
	public static void init(Class clazz, String sUncaughtErrorMessage, boolean debug){
		sLogFilename = getLogFilename(clazz, "log.txt");
		sErrorDirectory = getLogFilename(clazz, "logs"+File.separator);
		Log.sUncaughtErrorMessage = sUncaughtErrorMessage;
		bDebug = debug;
		bInitialized = true;
	
		System.setProperty("sun.awt.exception.handler", "net.nikr.log.NikrUncaughtExceptionHandler");
		Thread.setDefaultUncaughtExceptionHandler( new NikrUncaughtExceptionHandler());

		//Clear logfile
		try {
			FileWriter fileWriter = new FileWriter(sLogFilename, false);
			fileWriter.write("");
			fileWriter.close();
		} catch (IOException ex) {
			failed("Clearing log failed (CLEAR: "+sLogFilename+")", ex);
		}
		
		//Add PrintStream to System.out & System.err
		try {
			PrintStream printStream = new PrintStream(new FileOutputStream(sLogFilename), true, "UTF-8");
			System.setOut( new DualPrintStream(printStream, System.out) );
			System.setErr( new DualPrintStream(printStream, System.err) );
		} catch (UnsupportedEncodingException ex) {
			failed("UTF-8 encoding not supported! ", ex);
		} catch (FileNotFoundException ex) {
			failed("Failed to setOut/setErr", ex);
		}
	}

	public static String getsUncaughtErrorMessage(){
		return sUncaughtErrorMessage;
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
		print(s, INFO);
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
		if (bDebug) print(s, DEBUG);
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
		print(s, WARNING);
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
		error(s, null);
	}

	public static void error(Throwable t){
		error("Undefined error: "+ Log.getsUncaughtErrorMessage(), t);
	}
	public static void error(Object obj, Throwable t){
		error(String.valueOf(obj), t);
	}
	public static void error(boolean b, Throwable t){
		error(String.valueOf(b), t);
	}
	public static void error(char c, Throwable t){
		error(String.valueOf(c), t);
	}
	public static void error(char[] s, Throwable t){
		error(String.valueOf(s), t);
	}
	public static void error(double d, Throwable t){
		error(String.valueOf(d), t);
	}
	public static void error(float f, Throwable t){
		error(String.valueOf(f), t);
	}
	public static void error(int i, Throwable t){
		error(String.valueOf(i), t);
	}
	public static void error(long l, Throwable t){
		error(String.valueOf(l), t);
	}
	public static void error(String s, Throwable t){
		print(s, ERROR, t);
	}

	private static void print(String message, int level){
		print(message, level, null);
	}
	private static void print(String message, int level, Throwable t){
		if (!bInitialized){
			failed("Log.init() must be called before using the log");
		}
		//Save message for screen display
		String screenMessage = message;

		switch (level){
			case INFO:
				message = "(info) "+message;
				break;
			case DEBUG:
				message = "(debug) "+message;
				break;
			case WARNING:
				message = "(warning) "+message;
				break;
			case ERROR:
				message = "(error) "+message;
				break;
		}
		//Message
		if(level >= WARNING){
			System.err.println(message);
		} else {
			System.out.println(message);
		}
		//Stack Trace
		if(t != null){
			t.printStackTrace();
		}
		//Handle Error
		if(level == ERROR){
			printErrorToScreen(screenMessage);
			copyToError();
			System.exit(1);
		}
		checkForMaxLines();
	}

	private static void copyToError(){
		try {
			File errorDirectory = new File(sErrorDirectory);
			if (!errorDirectory.exists()) errorDirectory.mkdir();
			copyFile(new File(sLogFilename), new File(errorDirectory.getAbsolutePath()+File.separator+"error"+simpleDate.format(new Date()) + ".txt"));
		} catch (IOException ex) {
			failed("Logging to file failed (COPY TO ERROR FILE)", ex);
		}
	}

	private static void copyFile(File in, File out)  throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			// magic number for Windows, 64Mb - 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size) {
				position += inChannel.transferTo(position, maxCount, outChannel);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}

  	private static void failed(String s){
		failed(s, null);
	}

	private static void failed(String s, Throwable t){
		System.err.println(s);
		if (t != null) t.printStackTrace();
		printErrorToScreen(s);
		System.exit(1);
	}

	private static void printErrorToScreen(final String s){
		try {
			JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (Throwable t) {
			System.exit(1);
		}
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

	private static void checkForMaxLines(){
		File file = new File(sLogFilename);
		if (file.length() > MAX_BYTE_SIZE){
			failed("Log size is getting to big...");
		}
	}
}

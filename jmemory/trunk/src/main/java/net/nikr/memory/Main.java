/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jMemory.
 *
 * jMemory is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jMemory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jMemory; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.memory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public final class Main {

	public static final String JAR = "jtest.jar";
	public static final String PROGRAM_VERSION = "1.1.0";
	private static final String OK_KEY = "jmemory ok";

	private static final String[] XMX = {"-Xmx4g", "-Xmx3g", "-Xmx2g", "-Xmx1g"};
	

	/**
	 * Entry point for jMemory.
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		NikrUncaughtExceptionHandler.install();
		Main main = new Main();
		main.work(args);
	}

	private Main() { }

	private void work(final String[] args) {
		execute(getLocalFile(JAR), args);
	}

	private void execute(final String jarFile, final String[] args) {
		execute(jarFile, args, 0);
	}

	private void execute(final String jarFile, final String[] args, int index) {
		if (index >= XMX.length) {
			throw new RuntimeException("I give up, tried all possibles");
		}
		System.out.println("Trying: " + XMX[index]);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(getJavaHome());
		List<String> commands = new ArrayList<String>();
		commands.add("java");
		commands.add(XMX[index]);
		commands.add("-jar");
		commands.add(jarFile);
		commands.addAll(Arrays.asList(args));

		processBuilder.command(commands);
		try {
			Process process = processBuilder.start();
			process.waitFor();
			String returnValue = convertStreamToString(process.getInputStream());
			if (!returnValue.contains(OK_KEY)) {
				index++;
				execute(jarFile, args, index);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	static String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private String getLocalFile(final String filename) {
		try {
			File dir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			String fixedFilename = dir.getAbsolutePath() + File.separator + filename;
			File file = new File(fixedFilename);
			if (!file.exists()) {
				throw new RuntimeException(fixedFilename + " not found");
			} else {
				return fixedFilename;
			}
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private static File getJavaHome() {
		return new File(System.getProperty("java.home") + File.separator + "bin");
	}
}

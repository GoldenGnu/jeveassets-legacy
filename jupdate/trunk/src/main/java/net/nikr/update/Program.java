/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * jUpdate is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jUpdate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jUpdate; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.update;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import net.nikr.update.io.DataGetter;
import net.nikr.update.io.ListGetter;


public class Program {
	
	public Program(final String link, final String jarFile) {
		update(link);
		execute(jarFile);
		SplashUpdater.hide();
		System.exit(0);
	}

	private void downloadFile(final String link, final String filename) {
		DataGetter getter = new DataGetter();
		ListGetter listGetter = new ListGetter();
		File update = new File(getUpdateDir(filename));
		List<String> list = listGetter.get(link + filename + ".md5");
		if (list.isEmpty()) {
			throw new RuntimeException("Missing md5 file");
		} else {
			getter.get(link + filename, update, list.get(0));
		}
	}

	private String getUpdateDir(final String filename) {
		try {
			File dir = new File(Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			File file = new File(dir.getAbsolutePath() + File.separator + "update" + File.separator + filename.replace("/", File.separator));
			file.getParentFile().mkdirs();
			return file.getAbsolutePath();
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Failed to get update directory");
		}
	}

	private String getLocalDir(final String filename) {
		try {
			File dir = new File(Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			File file = new File(dir.getAbsolutePath() + File.separator + filename.replace("/", File.separator));
			file.getParentFile().mkdirs();
			return file.getAbsolutePath();
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Failed to get local directory");
		}
	}

	public void deleteDirectory(final File folder) {
		File[] files = folder.listFiles();
		if (files != null) { //some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	private void update(final String link) {
		//Download file list
		ListGetter getter = new ListGetter();
		List<String> files = getter.get(link + "list.php");
		if (files.isEmpty()) {
			throw new RuntimeException("Failed to download file list");
		}
		int increase = 100 / files.size();
		//Delete update directory ()
		File updateDir = new File(getUpdateDir("never-used-filename")).getParentFile();
		deleteDirectory(updateDir);
		//Download files
		for (String filename : files) {
			SplashUpdater.addProgress(increase);
			downloadFile(link, filename);
		}
		//Move updated files to final destination
		for (String filename : files) {
			File update = new File(getUpdateDir(filename));
			File done = new File(getLocalDir(filename));
			if (done.exists()) { //Delete old file
				done.delete();
			}
			boolean renamed = update.renameTo(done);
			if (!renamed) {
				throw new RuntimeException("Failed to move file");
			}
		}
		//Delete update directory
		deleteDirectory(updateDir);
		SplashUpdater.setProgress(100);
	}

	private void execute(final String jarFile) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(getJavaHome());
		String[] commands = {"java", "-jar", jarFile};
		processBuilder.command(commands);
		try {
			Process process = processBuilder.start();
		} catch (IOException ex) {
			throw new RuntimeException("Failed run restart command");
		}
	}

	private static File getJavaHome() {
		return new File(System.getProperty("java.home") + File.separator + "bin");
	}
}

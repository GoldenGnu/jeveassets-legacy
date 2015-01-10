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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


public final class Main {

	private static String link;
	private static String jarFile;

	private Main() {
		Program program = new Program(link, jarFile);
	}

	/**
	 * Entry point for jUpdate.
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		NikrUncaughtExceptionHandler.install();
		final boolean stop;
		if (args.length == 2) {
			//Link
			if (args[0].endsWith("/")) {
				link = args[0];
			} else {
				link = args[0] + "/";
			}
			System.out.println("Updating from: " + link);
			jarFile = args[1];
			stop = false;
		} else {
			stop = true;
		}
		javax.swing.SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception ex) {
						//No problem...
					}
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);

					if (stop) {
						JOptionPane.showMessageDialog(null, "jUpdate cannot be run by itself.\r\n\r\nNo worries:\r\nThe main program will automatically check for updates on startup.\r\n\r\n", "Sorry...", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}

					SplashUpdater.start();
					SplashUpdater.setText("Loading Update");
					Main main = new Main();
				}
			});
	}
}

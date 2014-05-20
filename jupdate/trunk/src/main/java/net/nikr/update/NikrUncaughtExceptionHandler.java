/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * Original code from jEveAssets (https://code.google.com/p/jeveassets/)
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

import javax.swing.JOptionPane;


public class NikrUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static boolean error = false;

	public static void install() {
		System.setProperty("sun.awt.exception.handler", NikrUncaughtExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new NikrUncaughtExceptionHandler());
	}

	private NikrUncaughtExceptionHandler() { }

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		showError(e);
	}

	public void handle(final Throwable e) {
		showError(e);
	}

	private void showError(final Throwable e) {
		if (!error) {
			error = true;
			JOptionPane.showMessageDialog(null
					, "Automatic update failed.\r\n"
							+ "Restart jEveAssets and try again,\r\n"
							+ "or download the update manually.\r\n"
							+ "\r\n"
							+ e.getMessage()
					, "Update Failed"
					, JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
	}
}

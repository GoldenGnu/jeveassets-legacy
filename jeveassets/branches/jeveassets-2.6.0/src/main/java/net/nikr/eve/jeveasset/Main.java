/*
 * Copyright 2009-2013 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset;

import java.io.File;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.data.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.translations.conf.DefaultBundleConfiguration;
import uk.me.candle.translations.service.BasicBundleService;
import uk.me.candle.translations.service.BundleService;


public final class Main {
	/**
	 * We cannot init this until we have set the two system properties: log.home and log.level
	 * They are set in the entry point method and then the LOG is created.
	 *
	 */
	private static Logger log;
	private static BundleService bundleService;

	/**
	 * JEveAssets main launcher.
	 */
	private Program program;

	/** Creates a new instance of Main. */
	private Main() {
		program = new Program();
	}

	/**
	 * Entry point for JEveAssets.
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		SplashUpdater splashUpdater = new SplashUpdater();
		splashUpdater.start();

		boolean isDebug = false;
		boolean isPortable = false;
		boolean hasNoUpdate = false;
		boolean hasUpdate = false;

		for (String arg : args) {
			if (arg.toLowerCase().equals("-debug")) {
				isDebug = true;
			}
			if (arg.toLowerCase().equals("-portable")) {
				isPortable = true;
			}
			if (arg.toLowerCase().equals("-noupdate")) {
				hasNoUpdate = true;
			}
			if (arg.toLowerCase().equals("-update")) {
				hasUpdate = true;
			}
		}

		// the tests for null indicate that the property is not set
		// It is possible to set properties using the -Dlog.home=foo/bar
		// and thus we want to allow this to take priority over the
		// configuration options here.
		if (isPortable && System.getProperty("log.home") == null) {
			System.setProperty("log.home", "." + File.separator);
		} else {
			if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) { //Mac
				System.setProperty("log.home", System.getProperty("user.home") + File.separator + "Library" + File.separator + "Preferences" + File.separator + "JEveAssets" + File.separator);
			} else { //Windows/Linux
				System.setProperty("log.home", System.getProperty("user.home") + File.separator + ".jeveassets" + File.separator);
			}
		}
		// ditto here.
		if (isDebug && System.getProperty("log.level") == null) {
			System.setProperty("log.level", "DEBUG");
		} else {
			System.setProperty("log.level", "INFO");
		}

		// only now can we create the Logger.
		log = LoggerFactory.getLogger(Main.class);

		// Now we have the logging stuff done, we can pass the
		// variables to the main program and settings.
		Program.setDebug(isDebug);
		Settings.setPortable(isPortable);
		Program.setForceNoUpdate(hasNoUpdate && Program.isDebug());
		Program.setForceUpdate(hasUpdate && Program.isDebug());

		// fix the uncaught exception handlers
		System.setProperty("sun.awt.exception.handler", "net.nikr.eve.jeveasset.NikrUncaughtExceptionHandler");
		Thread.setDefaultUncaughtExceptionHandler(new NikrUncaughtExceptionHandler());

		//XXX Workaround for IPv6 fail (force IPv4)
		//eveonline.com is not IPv6 ready...
		System.setProperty("java.net.preferIPv4Stack" , "true");

		javax.swing.SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					createAndShowGUI();
				}
			});
	}

	private static void createAndShowGUI() {
		initLookAndFeel();

		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		Main main = new Main();
	}

	private static void initLookAndFeel() {
		String lookAndFeel;
		//lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		lookAndFeel = UIManager.getSystemLookAndFeelClassName(); //System
		//lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName(); //Java
		//lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; //Nimbus
		//lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
		//lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //GTK
		//lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception ex) {
			log.error("Failed to set LookAndFeel: " + lookAndFeel, ex);
		}
	}

	public static BundleService getBundleService() {
		//XXX Workaround for default language
		if (bundleService == null) {
			bundleService = new BasicBundleService(new DefaultBundleConfiguration(), Locale.ENGLISH);
		}
		return bundleService;
	}
}

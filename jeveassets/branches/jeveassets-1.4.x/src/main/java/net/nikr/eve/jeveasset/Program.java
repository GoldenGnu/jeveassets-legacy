/* 
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import apple.dts.samplecode.osxadapter.OSXAdapter;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import ca.odell.glazedlists.matchers.MatcherEditor.Listener;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItemName;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.gui.frame.MainMenu;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.CsvExportDialog;
import net.nikr.eve.jeveasset.gui.settings.PriceDataSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.GeneralSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.IndustryJobsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.LoadoutsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.MarketOrdersDialog;
import net.nikr.eve.jeveasset.gui.dialogs.MaterialsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.RoutingDialogue;
import net.nikr.eve.jeveasset.gui.dialogs.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.ProxySettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.UpdateDialog;
import net.nikr.eve.jeveasset.gui.dialogs.ValuesDialog;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.TablePanel;
import net.nikr.eve.jeveasset.gui.frame.ToolPanel;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.settings.ReprocessingSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.TableSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.UserItemNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.WindowSettingsPanel;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import net.nikr.eve.jeveasset.io.online.ProgramUpdateChecker;
import net.nikr.log.Log;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Program implements ActionListener, Listener<EveAsset>{

	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "1.4.1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/?page=jeveasset";

	public static final int BUTTONS_HEIGHT = 22;
	public static final int BUTTONS_WIDTH = 90;

	private final static String ACTION_TIMER = "ACTION_TIMER";

	private static boolean debug = false;
	private static boolean forceUpdate = false;
	private static boolean forceNoUpdate = false;

	//GUI
	private MainWindow mainWindow;
	
	//Dialogs
	private AccountManagerDialog accountManagerDialog;
	private SaveFilterDialog saveFilterDialog;
	private FiltersManagerDialog filtersManagerDialog;
	private AboutDialog aboutDialog;
	private ValuesDialog valuesDialog;
	private MaterialsDialog materialsDialog;
	private LoadoutsDialog loadoutsDialog;
	private RoutingDialogue routingDialogue;
	private MarketOrdersDialog marketOrdersDialog;
	private IndustryJobsDialog industryJobsDialog;
	private CsvExportDialog csvExportDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private GeneralSettingsPanel generalSettingsPanel;
	private PriceDataSettingsPanel priceDataSettingsPanel;
	private ProxySettingsPanel proxySettingsPanel;
	private UserPriceSettingsPanel userPriceSettingsPanel;
	private UserItemNameSettingsPanel userItemNameSettingsPanel;
	private WindowSettingsPanel windowSettingsPanel;
	private ReprocessingSettingsPanel reprocessingSettingsPanel;
	private TableSettingsPanel tableSettingsPanel;
	private ProgramUpdateChecker programUpdateChecker;

	private UpdateDialog updateDialog;

	private Timer timer;
	private Updatable updatable;

	//Panels
	private TablePanel tablePanel;
	private StatusPanel statusPanel;
	private ToolPanel toolPanel;

	//Data
	private Settings settings;
	private EventList<EveAsset> eveAssetEventList;

	public Program(){
		Log.info("Starting "+PROGRAM_NAME+" "+PROGRAM_VERSION);
		
		if(debug){
			Log.enableDebug();
			Log.debug("Force Update: "+forceUpdate+" Force No Update: "+forceNoUpdate);
		}

		//Config log4j
		BasicConfigurator.configure();
		Logger.getLogger("com.beimin.eveapi").setLevel(Level.INFO);
		Logger.getLogger("uk.me.candle").setLevel(Level.INFO);
		Logger.getLogger("org.apache.commons").setLevel(Level.INFO);

		//Data
		SplashUpdater.setText("Loading DATA");
		Log.info("DATA Loading...");
		
		settings = new Settings();
		settings.loadActiveProfile();
		eveAssetEventList = new BasicEventList<EveAsset>();
		programUpdateChecker = new ProgramUpdateChecker(this);

		timer = new Timer(1000, this);
		timer.setActionCommand(ACTION_TIMER);

		updatable = new Updatable(settings);
		SplashUpdater.setText("Loading GUI");
		Log.info("GUI Loading:");
		Log.info("	Frame");
		mainWindow = new MainWindow(this);
		SplashUpdater.setProgress(50);
		Log.info("	Save Filters Dialog");
		saveFilterDialog = new SaveFilterDialog(this);
		SplashUpdater.setProgress(55);
		Log.info("	Filters Manager Dialog");
		filtersManagerDialog = new FiltersManagerDialog(this, ImageGetter.getImage("folder.png"));
		SplashUpdater.setProgress(60);
		Log.info("	Account Manager Dialog");
		accountManagerDialog = new AccountManagerDialog(this, ImageGetter.getImage("key.png"));
		SplashUpdater.setProgress(65);
		Log.info("	Values Dialog");
		valuesDialog = new ValuesDialog(this, ImageGetter.getImage("icon07_02.png"));
		SplashUpdater.setProgress(68);
		Log.info("	Ship Loadouts Dialog");
		loadoutsDialog = new LoadoutsDialog(this, ImageGetter.getImage("icon26_02.png"));
		SplashUpdater.setProgress(70);
		Log.info("	Market Orders Dialog");
		marketOrdersDialog = new MarketOrdersDialog(this, ImageGetter.getImage("icon07_12.png"));
		SplashUpdater.setProgress(72);
		Log.info("	Industry Jobs Dialog");
		industryJobsDialog = new IndustryJobsDialog(this, ImageGetter.getImage("icon33_02.png"));
		SplashUpdater.setProgress(74);
		Log.info("	Routing Dialog");
		routingDialogue = new RoutingDialogue(this, ImageGetter.getImage("cog.png"));
		SplashUpdater.setProgress(76);
		Log.info("	About Dialog");
		aboutDialog = new AboutDialog(this, ImageGetter.getImage("information.png"));
		SplashUpdater.setProgress(78);
		Log.info("	Materials Dialog");
		materialsDialog = new MaterialsDialog(this, ImageGetter.getImage("icon23_16.png"));
		SplashUpdater.setProgress(80);
		Log.info("	Csv Export Dialog");
		csvExportDialog = new CsvExportDialog(this, ImageGetter.getImage("table_save.png"));
		SplashUpdater.setProgress(82);
		Log.info("	Profiles Dialog");
		profileDialog = new ProfileDialog(this, ImageGetter.getImage("profile.png"));
		SplashUpdater.setProgress(84);
		Log.info("	Options Dialog");
		settingsDialog = new SettingsDialog(this, ImageGetter.getImage("cog.png"));
		SplashUpdater.setProgress(86);
		Log.info("		General");
		generalSettingsPanel = new GeneralSettingsPanel(this, settingsDialog, ImageGetter.getIcon("cog.png"));
		SplashUpdater.setProgress(88);
		Log.info("		Table");
		tableSettingsPanel = new TableSettingsPanel(this, settingsDialog, ImageGetter.getIcon("application_view_detail.png"));
		SplashUpdater.setProgress(90);
		Log.info("		Price Data");
		priceDataSettingsPanel = new PriceDataSettingsPanel(this, settingsDialog, ImageGetter.getIcon("coins.png"));
		SplashUpdater.setProgress(91);
		Log.info("		User Price");
		userPriceSettingsPanel = new UserPriceSettingsPanel(this, settingsDialog, ImageGetter.getIcon("money.png"));
		SplashUpdater.setProgress(92);
		Log.info("		User Item Name");
		userItemNameSettingsPanel = new UserItemNameSettingsPanel(this, settingsDialog, ImageGetter.getIcon("set_name.png"));
		SplashUpdater.setProgress(93);
		Log.info("		Reprocessing");
		reprocessingSettingsPanel = new ReprocessingSettingsPanel(this, settingsDialog, ImageGetter.getIcon("reprocessing.png"));
		SplashUpdater.setProgress(94);
		Log.info("		Proxy");
		proxySettingsPanel = new ProxySettingsPanel(this, settingsDialog, ImageGetter.getIcon("server_connect.png"));
		SplashUpdater.setProgress(95);
		Log.info("		Window");
		windowSettingsPanel = new WindowSettingsPanel(this, settingsDialog, ImageGetter.getIcon("application.png"));
		SplashUpdater.setProgress(96);
		Log.info("	Update Dialog");
		updateDialog = new UpdateDialog(this, ImageGetter.getImage("update.png"));
		SplashUpdater.setProgress(97);
		Log.info("	GUI loaded");
		Log.info("Updating data...");
		updateEventList();
		SplashUpdater.setProgress(99);
		macOsxCode();
		SplashUpdater.setProgress(100);
		Log.info("Showing GUI");
		mainWindow.show();
		//Start timer
		timerTicked();
		Log.info("Startup Done");
		if(debug){
			Log.info("Show Debug Warning");
			JOptionPane.showMessageDialog(mainWindow.getFrame(), "WARNING: Debug is enabled", "Debug", JOptionPane.WARNING_MESSAGE);
		}
		if (settings.getAccounts().isEmpty()){
			Log.info("Show Account Manager");
			accountManagerDialog.setVisible(true);
		}
		programUpdateChecker.showMessages();
		
	}
	public void addPanel(JProgramPanel jProgramPanel){
		if (jProgramPanel instanceof TablePanel){
			tablePanel = (TablePanel) jProgramPanel;
		} else if (jProgramPanel instanceof StatusPanel){
			statusPanel = (StatusPanel) jProgramPanel;
		} else if (jProgramPanel instanceof ToolPanel){
			toolPanel = (ToolPanel) jProgramPanel;
		}
	}
	
	public void filtersChanged(){
		this.getFiltersManagerDialog().filtersChanged();
		this.getSaveFilterDialog().filtersChanged();
		this.getToolPanel().filtersChanged();
		this.getTablePanel().filtersChanged();
	}

	public void tableUpdated(){

	}

	private void timerTicked(){
		if (!timer.isRunning()){
			timer.start();
		}
		this.getStatusPanel().timerTicked(updatable.isUpdatable());
		this.getMainWindow().getMenu().timerTicked(updatable.isUpdatable());
	}
	
	final public void updateEventList(){
		settings.clearEveAssetList();
		eveAssetEventList.getReadWriteLock().writeLock().lock();
		eveAssetEventList.clear();
		eveAssetEventList.addAll( settings.getEventListAssets() );
		eveAssetEventList.getReadWriteLock().writeLock().unlock();
		System.gc(); //clean post-update mess :)
	}

	public void saveSettings(){
		Log.info("Saving...");
		mainWindow.updateSettings();
		settings.saveSettings();
	}
	
	public void exit(){
		saveSettings();
		Log.info("Exiting...");
		System.exit(0);
	}

	public void showAbout(){
		aboutDialog.setVisible(true);
	}

	public void showSettings(){
		settingsDialog.setVisible(true);
	}

	public void checkForProgramUpdates(Window parent){
		programUpdateChecker.showMessages(parent, true);
	}

	public String getProgramDataVersion(){
		return programUpdateChecker.getProgramDataVersion();
	}

	private void macOsxCode(){
		if (onMac()) {
			try {
				OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("saveSettings", (Class[]) null));
				OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAbout", (Class[])null));
				OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("showSettings", (Class[])null));
			} catch (NoSuchMethodException ex) {
				Log.error("NoSuchMethodException: "+ex.getMessage(), ex);
			} catch (SecurityException ex) {
				Log.error("SecurityException: "+ex.getMessage(), ex);
			}
		}
	}
	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public Settings getSettings(){
		return settings;
	}
	public MainWindow getMainWindow(){
		return mainWindow;
	}
	public FiltersManagerDialog getFiltersManagerDialog(){
		return filtersManagerDialog;
	}
	public SaveFilterDialog getSaveFilterDialog(){
		return saveFilterDialog;
	}
	public TablePanel getTablePanel(){
		return tablePanel;
	}
	public ToolPanel getToolPanel(){
		return toolPanel;
	}
	public StatusPanel getStatusPanel(){
		return statusPanel;
	}
	public EventList<EveAsset> getEveAssetEventList() {
		return eveAssetEventList;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Program.debug = debug;
	}

	public static boolean isForceNoUpdate() {
		return forceNoUpdate;
	}

	public static void setForceNoUpdate(boolean forceNoUpdate) {
		Program.forceNoUpdate = forceNoUpdate;
	}

	public static boolean isForceUpdate() {
		return forceUpdate;
	}

	public static void setForceUpdate(boolean forceUpdate) {
		Program.forceUpdate = forceUpdate;
	}

	@Override
	public void changedMatcher(Event<EveAsset> matcherEvent) {
		this.getTablePanel().updateToolPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (MainMenu.ACTION_OPEN_ACCOUNT_MANAGER.equals(e.getActionCommand())) {
			accountManagerDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_ABOUT.equals(e.getActionCommand())) {
			showAbout();
		}
		if (MainMenu.ACTION_OPEN_VALUES.equals(e.getActionCommand())) {
			valuesDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_METERIALS.equals(e.getActionCommand())) {
			materialsDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_LOADOUTS.equals(e.getActionCommand())) {
			loadoutsDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_MARKET_ORDERS.equals(e.getActionCommand())) {
			marketOrdersDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_INDUSTRY_JOBS.equals(e.getActionCommand())) {
			industryJobsDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_CSV_EXPORT.equals(e.getActionCommand())) {
			csvExportDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_ROUTING.equals(e.getActionCommand())) {
			routingDialogue = new RoutingDialogue(this, ImageGetter.getImage("routing.png"));
			// XXX Although the line above should be removed for production, removing it makes
			// XXX the GUI flicker.
			routingDialogue.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_PROFILES.equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_OPTIONS.equals(e.getActionCommand())) {
			showSettings();
		}
		if (MainMenu.ACTION_OPEN_UPDATE.equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		}
		if (TablePanel.ACTION_SET_USER_PRICE.equals(e.getActionCommand())) {
			EveAsset eveAsset = this.getTablePanel().getSelectedAsset();
			if (eveAsset.isBlueprint() && !eveAsset.isBpo()){
				JOptionPane.showMessageDialog(mainWindow.getFrame(),
						"You can not set price for Blueprint Copies.\r\n" +
						"If this is a Blueprint Original, mark it as such, to set the price", "Price Settings", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			userPriceSettingsPanel.setNewItem(new UserPrice(eveAsset));
			settingsDialog.setVisible(userPriceSettingsPanel);
		}
		if (TablePanel.ACTION_SET_ITEM_NAME.equals(e.getActionCommand())){
			EveAsset eveAsset = this.getTablePanel().getSelectedAsset();
			userItemNameSettingsPanel.setNewItem(new UserItemName(eveAsset));
			settingsDialog.setVisible(userItemNameSettingsPanel);
		}

		if (MainMenu.ACTION_OPEN_README.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathReadme()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Could not open readme.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (MainMenu.ACTION_OPEN_LICENSE.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathLicense()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Could not open license.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (MainMenu.ACTION_OPEN_CREDITS.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathCredits()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Could not open credits.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (MainMenu.ACTION_OPEN_CHANGELOG.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathChangeLog()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Could not open changelog.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (MainMenu.ACTION_EXIT_PROGRAM.equals(e.getActionCommand())) {
			exit();
		}
		if (ACTION_TIMER.equals(e.getActionCommand())) {
			timerTicked();
		}
	}
}
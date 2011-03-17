/* 
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.export.CsvExportDialog;
import net.nikr.eve.jeveasset.gui.dialogs.profile.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.GeneralSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.PriceDataSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.ProxySettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.ReprocessingSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.AssetsToolSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.OverviewToolSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserItemNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserItemNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.dialogs.settings.WindowSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog;
import net.nikr.eve.jeveasset.gui.frame.MainMenu;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.ValuesTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.tabs.assets.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.io.online.ProgramUpdateChecker;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program implements ActionListener, Listener<EveAsset>{
	private final static Logger LOG = LoggerFactory.getLogger(Program.class);

	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "1.6.4";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/jeveasset";

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
	private CsvExportDialog csvExportDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private UpdateDialog updateDialog;

	//Tabs
	private ValuesTab valuesTab;
	private MaterialsTab materialsTab;
	private LoadoutsTab loadoutsTab;
	private RoutingTab routingTab;
	private MarketOrdersTab marketOrdersTab;
	private IndustryJobsTab industryJobsTab;
	private AssetsTab assetsTab;
	private OverviewTab overviewTab;

	//Settings Panels
	private GeneralSettingsPanel generalSettingsPanel;
	private PriceDataSettingsPanel priceDataSettingsPanel;
	private ProxySettingsPanel proxySettingsPanel;
	private UserPriceSettingsPanel userPriceSettingsPanel;
	private UserItemNameSettingsPanel userItemNameSettingsPanel;
	private WindowSettingsPanel windowSettingsPanel;
	private ReprocessingSettingsPanel reprocessingSettingsPanel;
	private AssetsToolSettingsPanel assetsToolSettingsPanel;
	private OverviewToolSettingsPanel overviewToolSettingsPanel;


	private ProgramUpdateChecker programUpdateChecker;
	private Timer timer;
	private Updatable updatable;

	//Data
	private Settings settings;
	private EventList<EveAsset> eveAssetEventList;

	public Program(){
		LOG.info("Starting {} {}", PROGRAM_NAME, PROGRAM_VERSION);
		
		if(debug){
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
		}

	//Data
		SplashUpdater.setText("Loading DATA");
		LOG.info("DATA Loading...");
		settings = new Settings();
		settings.loadActiveProfile();
		eveAssetEventList = new BasicEventList<EveAsset>();
		programUpdateChecker = new ProgramUpdateChecker(this);
	//Timer
		timer = new Timer(1000, this);
		timer.setActionCommand(ACTION_TIMER);
	//Updatable
		updatable = new Updatable(settings);
	//GUI
		SplashUpdater.setText("Loading GUI");
		LOG.info("GUI Loading:");
		LOG.info("Loading: Main Window");
		mainWindow = new MainWindow(this);
		SplashUpdater.setProgress(50);
	//Tools
		LOG.info("Loading: Assets Tab");
		assetsTab = new AssetsTab(this);
		mainWindow.addTab(assetsTab);
		SplashUpdater.setProgress(55);
		LOG.info("Loading: Industry Jobs Tab");
		industryJobsTab = new IndustryJobsTab(this);
		SplashUpdater.setProgress(60);
		LOG.info("Loading: Market Orders Tab");
		marketOrdersTab = new MarketOrdersTab(this);
		SplashUpdater.setProgress(62);
		LOG.info("Loading: Materials Tab");
		materialsTab = new MaterialsTab(this);
		SplashUpdater.setProgress(64);
		LOG.info("Loading: Ship Loadouts Tab");
		loadoutsTab = new LoadoutsTab(this);
		SplashUpdater.setProgress(66);
		LOG.info("Loading: Values Tab");
		valuesTab = new ValuesTab(this);
		SplashUpdater.setProgress(68);
		LOG.info("Loading: Routing Tab");
		routingTab = new RoutingTab(this);
		SplashUpdater.setProgress(70);
		LOG.info("Loading: Overview Tab");
		overviewTab = new OverviewTab(this);
		SplashUpdater.setProgress(72);
	//Dialogs
		LOG.info("Loading: Save Filters Dialog");
		saveFilterDialog = new SaveFilterDialog(this);
		SplashUpdater.setProgress(74);
		LOG.info("Loading: Filters Manager Dialog");
		filtersManagerDialog = new FiltersManagerDialog(this, Images.IMAGE_FOLDER);
		SplashUpdater.setProgress(76);
		LOG.info("Loading: Account Manager Dialog");
		accountManagerDialog = new AccountManagerDialog(this, Images.IMAGE_DIALOG_ACCOUNT_MANAGER);
		SplashUpdater.setProgress(78);
		LOG.info("Loading: About Dialog");
		aboutDialog = new AboutDialog(this, Images.IMAGE_DIALOG_ABOUT);
		SplashUpdater.setProgress(80);
		LOG.info("Loading: Csv Export Dialog");
		csvExportDialog = new CsvExportDialog(this, Images.IMAGE_DIALOG_CSV_EXPORT);
		SplashUpdater.setProgress(82);
		LOG.info("Loading: Profiles Dialog");
		profileDialog = new ProfileDialog(this, Images.IMAGE_DIALOG_PROFILES);
		SplashUpdater.setProgress(84);
		LOG.info("Loading: Update Dialog");
		updateDialog = new UpdateDialog(this, Images.IMAGE_DIALOG_UPDATE);
		SplashUpdater.setProgress(86);
	//Settings
		LOG.info("Loading: Options Dialog");
		settingsDialog = new SettingsDialog(this, Images.IMAGE_DIALOG_SETTINGS);
		SplashUpdater.setProgress(87);
		LOG.info("Loading: General Settings Panel");
		generalSettingsPanel = new GeneralSettingsPanel(this, settingsDialog, Images.ICON_DIALOG_SETTINGS);
		SplashUpdater.setProgress(88);
		DefaultMutableTreeNode toolNode = settingsDialog.addGroup("Tools", Images.ICON_TOOLS);
		LOG.info("Loading: Assets Tool Settings Panel");
		assetsToolSettingsPanel = new AssetsToolSettingsPanel(this, settingsDialog, Images.ICON_TOOL_ASSETS, toolNode);
		SplashUpdater.setProgress(89);
		LOG.info("Loading: Overview Tool Settings Panel");
		overviewToolSettingsPanel = new OverviewToolSettingsPanel(this, settingsDialog, Images.ICON_TOOL_OVERVIEW, toolNode);
		SplashUpdater.setProgress(90);
		DefaultMutableTreeNode modifiedAssetsNode = settingsDialog.addGroup("Values", Images.ICON_MODIFIED_ASSETS);
		LOG.info("Loading: Assets Price Settings Panel");
		userPriceSettingsPanel = new UserPriceSettingsPanel(this, settingsDialog, Images.ICON_USER_ITEM_PRICE, modifiedAssetsNode);
		SplashUpdater.setProgress(91);
		LOG.info("Loading: Assets Name Settings Panel");
		userItemNameSettingsPanel = new UserItemNameSettingsPanel(this, settingsDialog, Images.ICON_USER_ITEM_NAME, modifiedAssetsNode);
		SplashUpdater.setProgress(92);
		LOG.info("Loading: Price Data Settings Panel");
		priceDataSettingsPanel = new PriceDataSettingsPanel(this, settingsDialog, Images.ICON_PRICE_DATA);
		SplashUpdater.setProgress(93);
		LOG.info("Loading: Reprocessing Settings Panel");
		reprocessingSettingsPanel = new ReprocessingSettingsPanel(this, settingsDialog, Images.ICON_REPROCESSING);
		SplashUpdater.setProgress(94);
		LOG.info("Loading: Proxy Settings Panel");
		proxySettingsPanel = new ProxySettingsPanel(this, settingsDialog, Images.ICON_PROXY);
		SplashUpdater.setProgress(95);
		LOG.info("Loading: Window Settings Panel");
		windowSettingsPanel = new WindowSettingsPanel(this, settingsDialog, Images.ICON_WINDOW);
		SplashUpdater.setProgress(96);
		LOG.info("GUI loaded");
		LOG.info("Updating data...");
		updateEventList();
		SplashUpdater.setProgress(98);
		macOsxCode();
		SplashUpdater.setProgress(100);
		LOG.info("Showing GUI");
		mainWindow.show();
		//Start timer
		timerTicked();
		LOG.info("Startup Done");
		if(debug){
			LOG.info("Show Debug Warning");
			JOptionPane.showMessageDialog(mainWindow.getFrame(), "WARNING: Debug is enabled", "Debug", JOptionPane.WARNING_MESSAGE);
		}
		programUpdateChecker.showMessages();
		if (settings.getAccounts().isEmpty()){
			LOG.info("Show Account Manager");
			accountManagerDialog.setVisible(true);
		}
	}

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected Program(boolean load) { }

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
		for (JMainTab jMainTab : mainWindow.getTabs()){
			jMainTab.updateData();
		}
	}

	public void saveSettings(){
		LOG.info("Saving...");
		mainWindow.updateSettings();
		settings.saveSettings();
	}
	
	public void exit(){
		saveSettings();
		LOG.info("Exiting...");
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
				LOG.error("NoSuchMethodException: " + ex.getMessage(), ex);
			} catch (SecurityException ex) {
				LOG.error("SecurityException: " + ex.getMessage(), ex);
			}
		}
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
	public AssetsTab getAssetsTab(){
		return assetsTab;
	}
	public StatusPanel getStatusPanel(){
		return this.getMainWindow().getStatusPanel();
	}
	public EventList<EveAsset> getEveAssetEventList() {
		return eveAssetEventList;
	}

	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
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

	/**
	 * Called when the table menu needs update
	 */
	public void updateTableMenu(){
		this.getMainWindow().getSelectedTab().updateTableMenu(this.getMainWindow().getMenu().getTableMenu());
	}

	/**
	 * Called when the active tab is change (close/open/change)
	 */
	public void tabChanged(){
		getStatusPanel().tabChanged();
		updateTableMenu();
	}


	/**
	 * Called when saved asset filters are updated (save/rename/delete)
	 */
	public void savedFiltersChanged(){
		this.getFiltersManagerDialog().savedFiltersChanged();
		this.getSaveFilterDialog().savedFiltersChanged();
		this.getAssetsTab().savedFiltersChanged();
	}

	/**
	 * Called when the asset table is filtered
	 */
	@Override
	public void changedMatcher(Event<EveAsset> matcherEvent) {
		this.getAssetsTab().updateToolPanel();
		overviewTab.updateTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	//Tools
		if (MainMenu.ACTION_OPEN_VALUES.equals(e.getActionCommand())) {
			mainWindow.addTab(valuesTab);
		}
		if (MainMenu.ACTION_OPEN_MATERIALS.equals(e.getActionCommand())) {
			mainWindow.addTab(materialsTab);
		}
		if (MainMenu.ACTION_OPEN_LOADOUTS.equals(e.getActionCommand())) {
			mainWindow.addTab(loadoutsTab);
		}
		if (MainMenu.ACTION_OPEN_MARKET_ORDERS.equals(e.getActionCommand())) {
			mainWindow.addTab(marketOrdersTab);
		}
		if (MainMenu.ACTION_OPEN_INDUSTRY_JOBS.equals(e.getActionCommand())) {
			mainWindow.addTab(industryJobsTab);
		}
		if (MainMenu.ACTION_OPEN_OVERVIEW.equals(e.getActionCommand())) {
			mainWindow.addTab(overviewTab);
			overviewTab.resetViews();
		}
		if (MainMenu.ACTION_OPEN_ROUTING.equals(e.getActionCommand())) {
			// XXX Although the line above should be removed for production, removing it makes the GUI flicker.
			routingTab = new RoutingTab(this);
			mainWindow.addTab(routingTab);
		}
	//Settings
		if (MainMenu.ACTION_OPEN_ACCOUNT_MANAGER.equals(e.getActionCommand())) {
			accountManagerDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_PROFILES.equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_OPTIONS.equals(e.getActionCommand())) {
			showSettings();
		}
		if (AssetsTab.ACTION_USER_PRICE_EDIT.equals(e.getActionCommand())) {
			EveAsset eveAsset = this.getAssetsTab().getSelectedAsset();
			if (eveAsset.isBlueprint() && !eveAsset.isBpo()){
				JOptionPane.showMessageDialog(mainWindow.getFrame(),
						"You can not set price for Blueprint Copies.\r\n" +
						"If this is a Blueprint Original, mark it as such, to set the price", "Price Settings", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			userPriceSettingsPanel.edit(new UserPrice(eveAsset));
		}
		if (AssetsTab.ACTION_USER_NAME_EDIT.equals(e.getActionCommand())){
			EveAsset eveAsset = this.getAssetsTab().getSelectedAsset();
			userItemNameSettingsPanel.edit(new UserName(eveAsset));
		}
	//Others
		if (MainMenu.ACTION_OPEN_ABOUT.equals(e.getActionCommand())) {
			showAbout();
		}
		if (MainMenu.ACTION_OPEN_CSV_EXPORT.equals(e.getActionCommand())) {
			csvExportDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_UPDATE.equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		}
	//External Files
		if (MainMenu.ACTION_OPEN_README.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathReadme(), this);
		}
		if (MainMenu.ACTION_OPEN_LICENSE.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathLicense(), this);
		}
		if (MainMenu.ACTION_OPEN_CREDITS.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathCredits(), this);
		}
		if (MainMenu.ACTION_OPEN_CHANGELOG.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathChangeLog(), this);
		}
		if (MainMenu.ACTION_EXIT_PROGRAM.equals(e.getActionCommand())) {
			exit();
		}
	//Ticker
		if (ACTION_TIMER.equals(e.getActionCommand())) {
			timerTicked();
		}
	}
}

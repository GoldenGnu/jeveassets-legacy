/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.AbstractApiParser;
import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AssetConverter;
import net.nikr.eve.jeveasset.io.eveapi.HumansGetter;
import net.nikr.eve.jeveasset.io.eveapi.IndustryJobsGetter;
import net.nikr.eve.jeveasset.io.eveapi.MarketOrdersGetter;
import net.nikr.eve.jeveasset.io.local.AssetsReader;
import net.nikr.eve.jeveasset.io.local.AssetsWriter;
import net.nikr.eve.jeveasset.io.local.ConquerableStationsReader;
import net.nikr.eve.jeveasset.io.local.ItemsReader;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.PriceDataGetter;
import net.nikr.eve.jeveasset.io.local.JumpsReader;
import net.nikr.log.Log;


public class Settings {

	private final static String PATH_SETTINGS = "data"+File.separator+"settings.xml";
	private final static String PATH_ITEMS = "data"+File.separator+"items.xml";
	private final static String PATH_JUMPS = "data"+File.separator+"jumps.xml";
	private final static String PATH_LOCATIONS = "data"+File.separator+"locations.xml";
	private final static String PATH_PRICE_DATA = "data"+File.separator+"pricedata.dat";
	private final static String PATH_ASSETS = "data"+File.separator+"assets.xml";
	private final static String PATH_CONQUERABLE_STATIONS = "data"+File.separator+"conquerable_stations.xml";
	private final static String PATH_README = "readme.txt";
	private final static String PATH_LICENSE = "license.txt";
	private final static String PATH_CREDITS = "credits.txt";

	private final static String FLAG_AUTO_RESIZE_COLUMNS_TEXT = "FLAG_AUTO_RESIZE_COLUMNS_TEXT";
	private final static String FLAG_AUTO_RESIZE_COLUMNS_WINDOW = "FLAG_AUTO_RESIZE_COLUMNS_WINDOW";
	private final static String FLAG_FILTER_ON_ENTER = "FLAG_FILTER_ON_ENTER";
	private final static String FLAG_HIGHLIGHT_SELECTED_ROWS = "FLAG_HIGHLIGHT_SELECTED_ROWS";

	private static boolean portable = false;
	
	//Data
	private List<Integer> uniqueIds = null;
	private Map<String, List<EveAsset>> uniqueAssetsDuplicates = null;
	private List<EveAsset> allAssets = null;
	private List<EveAsset> eventListAssets = null;
	private Map<String, List<AssetFilter>> assetFilters;
	private Map<Integer, Item> items;
	private Map<Integer, PriceData> priceData;
	private Map<Integer, Location> locations;
	private Map<Integer, ApiStation> conquerableStations;
	private Map<Long, String> corporations;
	private Map<Integer, UserPrice> userPrices;
	private List<Account> accounts;
	private List<String> tableColumnNames;
	private Map<String, String> tableColumnTooltips;
	private List<String> tableNumberColumns;
	private List<String> tableColumnVisible;
	private Date conquerableStationsNextUpdate;
	private Map<Long, Date> corporationsNextUpdate;
	private Map<String, Boolean> flags;
	private List<Integer> bpos;
	private boolean settingsLoaded;
	private PriceDataSettings priceDataSettings;
	private Proxy proxy;
	private String apiProxy;
	private Point windowLocation;
	private Dimension windowSize;
	private boolean windowMaximized;
	private boolean windowAutoSave;
	List<Jump> jumps;

	private PriceDataGetter priceDataGetter;
	private SplashUpdater splashUpdater;
	
	public Settings(SplashUpdater splashUpdater) {
		this(splashUpdater, true);
	}

	public Settings(SplashUpdater splashUpdater, boolean fill) {
		this.splashUpdater = splashUpdater;
		splashUpdater.setProgress(10);
		items = new HashMap<Integer, Item>();
		locations = new HashMap<Integer, Location>();
		priceData = new HashMap<Integer, PriceData>();
		conquerableStations = new HashMap<Integer, ApiStation>();
		assetFilters = new HashMap<String, List<AssetFilter>>();
		accounts = new Vector<Account>();
		corporations = new HashMap<Long, String>();
		userPrices = new HashMap<Integer, UserPrice>();
		bpos = new Vector<Integer>();
		jumps = new ArrayList<Jump>();
		
		flags = new HashMap<String, Boolean>();
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_TEXT, true);
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_WINDOW, false);
		flags.put(FLAG_FILTER_ON_ENTER, false);
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, true);

		if (fill) {
			fill();
		}
	}

	protected void fill() {
		conquerableStationsNextUpdate = Settings.getGmtNow();
		corporationsNextUpdate =  new HashMap<Long, Date>();  //Settings.cvtToGmt( new Date() );
		resetMainTableColumns();

		priceDataSettings = new PriceDataSettings(0, EveAsset.PRICE_SELL_MEDIAN, PriceDataSettings.SOURCE_EVE_CENTRAL);

		windowLocation = new Point(0, 0);
		windowSize = new Dimension(800, 600);
		windowMaximized = false;
		windowAutoSave = true;

		//Load data and overwite default values
		settingsLoaded = SettingsReader.load(this);
	//Load from file
		splashUpdater.setProgress(20);
		ItemsReader.load(this); //Items (Must be loaded before Assets)
		splashUpdater.setProgress(30);
		LocationsReader.load(this); //Locations (Must be loaded before Assets)
		splashUpdater.setProgress(40);
		ConquerableStationsReader.load(this); //Conquerable Stations (Must be loaded before Assets)
		splashUpdater.setProgress(50);
		AssetsReader.load(this); //Assets (Must be loaded before the price data)
	//Update from eve api
		MarketOrdersGetter.load(this); //Orders (Must be loaded before the price data)
		IndustryJobsGetter.load(this); //Jobs (Must be loaded before the price data)
		HumansGetter.load(this);
		splashUpdater.setProgress(60);
		JumpsReader.load(this);
	//Price data (update as needed)
		priceDataGetter = new PriceDataGetter(this); //Price Data - Must be loaded last
		splashUpdater.setProgress(70);
		
	}
	public void resetMainTableColumns(){
		//Also need to update:
		//		gui.table.EveAssetTableFormat.getColumnClass()
		//		gui.table.EveAssetTableFormat.getColumnComparator()
		//		gui.table.EveAssetTableFormat.getColumnValue()
		//		gui.table.EveAssetMatching.matches()
		//			remember to add to "All" as well...
		//		gui.dialogs.CsvExportDialog.getLine()
		//	If number column:
		//		add to mainTableNumberColumns bellow

		tableColumnNames = new Vector<String>();
		tableColumnNames.add("Name");
		tableColumnNames.add("Group");
		tableColumnNames.add("Category");
		tableColumnNames.add("Owner");
		tableColumnNames.add("Location");
		tableColumnNames.add("Security");
		tableColumnNames.add("Region");
		tableColumnNames.add("Container");
		tableColumnNames.add("Flag");
		tableColumnNames.add("Price");
		tableColumnNames.add("Sell Min");
		tableColumnNames.add("Buy Max");
		tableColumnNames.add("Reprocessed");
		tableColumnNames.add("Base Price");
		tableColumnNames.add("Value");
		tableColumnNames.add("Count");
		tableColumnNames.add("Type Count");
		tableColumnNames.add("Meta");
		tableColumnNames.add("Volume");
		tableColumnNames.add("ID");
		tableColumnNames.add("Type ID");

		tableColumnTooltips = new HashMap<String, String>();
		//tableColumnTooltips.put("Name", "Name");
		//tableColumnTooltips.put("Group", "Group");
		//tableColumnTooltips.put("Category", "Category");
		//tableColumnTooltips.put("Owner", "Owner");
		//tableColumnTooltips.put("Location", "Station");
		//tableColumnTooltips.put("Region", "Region");
		tableColumnTooltips.put("Security", "System Security Status");
		//tableColumnTooltips.put("Container", "Container");
		//tableColumnTooltips.put("Flag", "Flag");
		tableColumnTooltips.put("Price", "Default Price");
		tableColumnTooltips.put("Sell Min", "Minimum Sell Price");
		tableColumnTooltips.put("Buy Max", "Maximum Buy Price");
		tableColumnTooltips.put("Reprocessed", "Reprocessed value");
		//tableColumnTooltips.put("Base Price", "Base Price");
		tableColumnTooltips.put("Value", "Value (Count*Price)");
		//tableColumnTooltips.put("Count", "Count");
		tableColumnTooltips.put("Type Count", "Type Count (all assets of this type)");
		tableColumnTooltips.put("Meta", "Meta Level");
		//tableColumnTooltips.put("Volume", "Volume");
		tableColumnTooltips.put("ID", "ID (this specific asset)");
		tableColumnTooltips.put("Type ID", "Type ID (this type of asset)");
		
		tableColumnVisible = new Vector<String>(tableColumnNames);

		tableNumberColumns = new Vector<String>();
		tableNumberColumns.add("Count");
		tableNumberColumns.add("Price");
		tableNumberColumns.add("Sell Min");
		tableNumberColumns.add("Buy Max");
		tableNumberColumns.add("Base Price");
		tableNumberColumns.add("Value");
		tableNumberColumns.add("Volume");
		tableNumberColumns.add("ID");
		tableNumberColumns.add("Type ID");
		tableNumberColumns.add("Type Count");
		tableNumberColumns.add("Reprocessed");
		tableNumberColumns.add("Security");

	}

	public void updateOrdersAndJobs(){
		MarketOrdersGetter.load(this);
		IndustryJobsGetter.load(this);
	}

	public boolean updatePriceData(UpdateTask task){
		return priceDataGetter.updatePriceData(task);
	}

	public boolean updatePriceData(UpdateTask task, boolean forceUpdate){
		return priceDataGetter.updatePriceData(task, forceUpdate);
	}

	public static void setPortable(boolean portable) {
		Settings.portable = portable;
	}

	public static boolean isPortable() {
		return portable;
	}

	public void clearEveAssetList(){
		allAssets = null;
		eventListAssets = null;
		uniqueIds = null;
	}
	public List<EveAsset> getEventListAssets(){
		updateAssetLists(false);
		return eventListAssets;
	}
	public List<EveAsset> getAllAssets(){
		updateAssetLists(true);
		return allAssets;
	}

	public List<Integer> getUniqueIds(){
		updateAssetLists(false);
		return uniqueIds;
	}
	
	public boolean hasAssets(){
		updateAssetLists(false);
		return !uniqueIds.isEmpty();
	}
	private void updateAssetLists(boolean getAllAssets){
		if ((allAssets == null && getAllAssets) ||  (eventListAssets == null && !getAllAssets) ){
			List<EveAsset> assetList = new Vector<EveAsset>();
			uniqueIds = new Vector<Integer>();
			uniqueAssetsDuplicates = new HashMap<String, List<EveAsset>>();
			for (int a = 0; a < accounts.size(); a++){
				Account account = accounts.get(a);
				List<Human> humans = account.getHumans();
				for (int b = 0; b < humans.size(); b++){
					Human human = humans.get(b);
					if (human.isShowAssets() || getAllAssets){
						//Market Orders
						List<EveAsset> marketOrdersAssets = AssetConverter.apiMarketOrder(human.getMarketOrders(), this, human, false);
						addAssets(marketOrdersAssets, assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
						List<EveAsset> marketOrdersCorporationAssets = AssetConverter.apiMarketOrder(human.getMarketOrdersCorporation(), this, human, true);
						addAssets(marketOrdersCorporationAssets, assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
						//Industry Jobs
						List<EveAsset> industryJobAssets = AssetConverter.apiIndustryJob(human.getIndustryJobs(), this, human, false);
						addAssets(industryJobAssets, assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
						List<EveAsset> industryJobCorporationAssets = AssetConverter.apiIndustryJob(human.getIndustryJobsCorporation(), this, human, true);
						addAssets(industryJobCorporationAssets, assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
						//Assets (Must be after Industry Jobs, for bpos to be marked)
						addAssets(human.getAssets(), assetList, human.isShowAssets(), human.isUpdateCorporationAssets());
					}
				}
			}
			if (getAllAssets){
				allAssets = assetList;
			} else {
				eventListAssets = assetList;
			}
		}
	}
	private void addAssets(List<EveAsset> currentAssets, List<EveAsset> assetList, boolean shouldShow, boolean shouldShowCorp){
		for (int a = 0; a < currentAssets.size(); a++){
			EveAsset eveAsset = currentAssets.get(a);
			//User price
			if (userPrices.containsKey(eveAsset.getTypeId())){ //Add User Price
				eveAsset.setUserPrice(userPrices.get(eveAsset.getTypeId()));
			} else { //No user price, clear user price
				eveAsset.setUserPrice(null);
			}
			//Price data
			if (eveAsset.isMarketGroup()){ //Add price data
				eveAsset.setPriceData(priceData.get(eveAsset.getTypeId()));
			}
			//Reprocessed price
			eveAsset.setPriceReprocessed(0);
			if (getItems().containsKey(eveAsset.getTypeId())){
				List<Material> materials = getItems().get(eveAsset.getTypeId()).getMaterials();
				double priceReprocessed = 0;
				int portionSize = 0;
				for (int b = 0; b < materials.size(); b++){
					//Calculate reprocessed price
					Material material = materials.get(b);
					portionSize = material.getPortionSize();
					if (priceData.containsKey(material.getId())){
						PriceData priceDatum = priceData.get(material.getId());
						String priceSource = EveAsset.getPriceSource();
						double price = 0;
						if (userPrices.containsKey(material.getId())){
							price = userPrices.get(material.getId()).getPrice();
						} else {
							if (priceSource.equals(EveAsset.PRICE_BUY_AVG)) price = priceDatum.getBuyAvg();
							if (priceSource.equals(EveAsset.PRICE_BUY_MAX)) price = priceDatum.getBuyMax();
							if (priceSource.equals(EveAsset.PRICE_BUY_MIN)) price = priceDatum.getBuyMin();
							if (priceSource.equals(EveAsset.PRICE_BUY_MEDIAN)) price = priceDatum.getBuyMedian();
							if (priceSource.equals(EveAsset.PRICE_SELL_AVG)) price = priceDatum.getSellAvg();
							if (priceSource.equals(EveAsset.PRICE_SELL_MAX)) price = priceDatum.getSellMax();
							if (priceSource.equals(EveAsset.PRICE_SELL_MIN)) price = priceDatum.getSellMin();
							if (priceSource.equals(EveAsset.PRICE_SELL_MEDIAN)) price = priceDatum.getSellMedian();
						}
						priceReprocessed = priceReprocessed + (price * material.getQuantity());
					}
					//Unique Ids
					if (!uniqueIds.contains(material.getId())){
						uniqueIds.add(material.getId());
					}
				}
				if (priceReprocessed > 0 && portionSize > 0){
					priceReprocessed = priceReprocessed / portionSize;
				}
				eveAsset.setPriceReprocessed(priceReprocessed);
			}

			//Blueprint
			if (eveAsset.isBlueprint()){
				eveAsset.setBpo(bpos.contains(eveAsset.getId()));
			} else {
				eveAsset.setBpo(false);
			}
			//Type Count
			if (!uniqueAssetsDuplicates.containsKey(eveAsset.getName())){
				uniqueAssetsDuplicates.put(eveAsset.getName(), new Vector<EveAsset>());
			}
			if (shouldShow) {
				List<EveAsset> dup = uniqueAssetsDuplicates.get(eveAsset.getName());
				long newCount = eveAsset.getCount();
				if (!dup.isEmpty()){
					newCount = newCount + dup.get(0).getTypeCount();
				}
				dup.add(eveAsset);
				for (int b = 0; b < dup.size(); b++){
					dup.get(b).setTypeCount(newCount);
				}
			}
			//Unique Ids
			if (eveAsset.isMarketGroup()){
				if (!uniqueIds.contains(eveAsset.getTypeId())){
					uniqueIds.add(eveAsset.getTypeId());
				}
			}
			//Add asset
			if (shouldShow && (shouldShowCorp && eveAsset.isCorporationAsset()) || !eveAsset.isCorporationAsset()){
				assetList.add(eveAsset);
			}
			//Add sub-assets
			addAssets(eveAsset.getAssets(), assetList, shouldShow, shouldShowCorp);
		}
	}
	public List<Integer> getBpos() {
		return bpos;
	}
	public Date getConquerableStationsNextUpdate() {
		return conquerableStationsNextUpdate;
	}
	public void setConquerableStationsNextUpdate(Date conquerableStationNextUpdate) {
		this.conquerableStationsNextUpdate = conquerableStationNextUpdate;
	}
	public PriceDataSettings getPriceDataSettings() {
		return priceDataSettings;
	}
	public void setPriceDataSettings(PriceDataSettings priceDataSettings) {
		this.priceDataSettings = priceDataSettings;
		EveAsset.setPriceSource(priceDataSettings.getPriceSource());
	}
	public Map<Long, String> getCorporations() {
		return corporations;
	}
	public void setCorporations(Map<Long, String> corporations) {
		this.corporations = corporations;
	}
	public Map<Long, Date> getCorporationsNextUpdate() {
		return corporationsNextUpdate;
	}
	public void setCorporationsNextUpdate(Map<Long, Date> corporationNextUpdate) {
		this.corporationsNextUpdate = corporationNextUpdate;
	}
	public Date getPriceDataNextUpdate(){
		return priceDataGetter.getNextUpdate();
	}
	public Map<Integer, UserPrice> getUserPrices() {
		return userPrices;
	}
	public void setUserPrices(Map<Integer, UserPrice> userPrices) {
		this.userPrices = userPrices;
	}
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public Map<Integer, Item> getItems() {
		return items;
	}

	public void setItems(Map<Integer, Item> items) {
		this.items = items;
	}

	public Map<Integer, Location> getLocations() {
		return locations;
	}

	public void setLocations(Map<Integer, Location> locations) {
		this.locations = locations;
	}

	public Map<Integer, PriceData> getPriceData() {
		return priceData;
	}

	public void setPriceData(Map<Integer, PriceData> priceData) {
		this.priceData = priceData;
	}

	public Map<Integer, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	public void setConquerableStations(Map<Integer, ApiStation> conquerableStations) {
		this.conquerableStations = conquerableStations;
	}

	public Map<String, List<AssetFilter>> getAssetFilters() {
		return assetFilters;
	}

	public void setAssetFilters(Map<String, List<AssetFilter>> assetFilters) {
		this.assetFilters = assetFilters;
	}

	public void setTableColumnVisible(List<String> mainTableColumnVisible) {
		this.tableColumnVisible = mainTableColumnVisible;
	}

	public List<String> getTableColumnVisible() {
		return tableColumnVisible;
	}

	public void setTableColumnNames(List<String> mainTableColumnNames) {
		this.tableColumnNames = mainTableColumnNames;
	}

	public List<String> getTableColumnNames(){
		return tableColumnNames;
	}

	public List<String> getTableNumberColumns() {
		return tableNumberColumns;
	}

	public Map<String, String> getTableColumnTooltips() {
		return tableColumnTooltips;
	}
	
	public Map<String, Boolean> getFlags() {
		return flags;
	}

	//@NotNull
	public Proxy getProxy() {
		if (proxy == null) {
			return Proxy.NO_PROXY;
		} else {
			return proxy;
		}
	}

	public List<Jump> getJumps() {
		return jumps;
	}

	public void setJumps(List<Jump> jumps) {
		this.jumps = jumps;
	}

  /**
   *
   * @param proxy passing 'null' removes proxying.
   */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
		// pass the new proxy onto the API framework.
		AbstractApiParser.setHttpProxy(proxy);
	}

  /**
   * handles converting "basic" types to a Proxy type.
   * @param host
   * @param port
   * @param type
   * @throws IllegalArgumentException
   */
	public void setProxy(String host, int port, String type) throws IllegalArgumentException {
		// Convert the proxy type. not using the "valueof()" method so that they can be case-insensitive.
		Proxy.Type proxyType = Proxy.Type.DIRECT;
		if ("http".equalsIgnoreCase(type)) {
			proxyType = Proxy.Type.HTTP;
		} else if ("socks".equalsIgnoreCase(type)) {
			proxyType = Proxy.Type.SOCKS;
		} else if ("direct".equalsIgnoreCase(type)) {
			setProxy(Proxy.NO_PROXY);
		}

		setProxy(host, port, proxyType);
	}

  /**
   * handles converting "basic" types to a Proxy type.
   * @param host
   * @param port
   * @param type
   * @throws IllegalArgumentException
   */
	public void setProxy(String host, int port, Proxy.Type type) throws IllegalArgumentException {
		// Convert it into something we can use.
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(host);
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException("unknown host: " + host, uhe);
		}

		SocketAddress proxyAddress = new InetSocketAddress(addr, port);
		
		setProxy(new Proxy(type, proxyAddress));
	}

	public String getApiProxy() {
		return apiProxy;
	}

	/**
	 * 
	 * @param apiProxy pass null to disable any API proxy, and use the default: http://api.eve-online.com
	 */
	public void setApiProxy(String apiProxy) {
		AbstractApiParser.setEveApiURL(apiProxy);
		this.apiProxy = apiProxy;
	}

	public boolean isAutoResizeColumnsText() {
		return flags.get(FLAG_AUTO_RESIZE_COLUMNS_TEXT);
	}
	public void setAutoResizeColumnsText(boolean autoResizeColumns) {
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_TEXT, autoResizeColumns);
	}
	public boolean isAutoResizeColumnsWindow() {
		return flags.get(FLAG_AUTO_RESIZE_COLUMNS_WINDOW);
	}
	public void setAutoResizeColumnsWindow(boolean autoResizeColumns) {
		flags.put(FLAG_AUTO_RESIZE_COLUMNS_WINDOW, autoResizeColumns);
	}
	public boolean isFilterOnEnter() {
		return flags.get(FLAG_FILTER_ON_ENTER);
	}
	public void setFilterOnEnter(boolean filterOnEnter) {
		flags.put(FLAG_FILTER_ON_ENTER, filterOnEnter);
	}
	public boolean isHighlightSelectedRows() {
		return flags.get(FLAG_HIGHLIGHT_SELECTED_ROWS);
	}
	public void setHighlightSelectedRows(boolean filterOnEnter) {
		flags.put(FLAG_HIGHLIGHT_SELECTED_ROWS, filterOnEnter);
	}
	//Window
	public Point getWindowLocation() {
		return windowLocation;
	}

	public void setWindowLocation(Point windowLocation) {
		this.windowLocation = windowLocation;
	}

	public boolean isWindowMaximized() {
		return windowMaximized;
	}

	public void setWindowMaximized(boolean windowMaximized) {
		this.windowMaximized = windowMaximized;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public boolean isWindowAutoSave() {
		return windowAutoSave;
	}

	public void setWindowAutoSave(boolean windowAutoSave) {
		this.windowAutoSave = windowAutoSave;
	}
	
	public boolean isSettingsLoaded() {
		return settingsLoaded;
	}
	public void saveSettings(){
		SettingsWriter.save(this);
		AssetsWriter.save(this);
	}
	public static String getPathSettings(){
		return getLocalFile(Settings.PATH_SETTINGS, !portable);
	}
	public static String getPathConquerableStations(){
		return getLocalFile(Settings.PATH_CONQUERABLE_STATIONS, !portable);
	}
	public static String getPathJumps(){
		return getLocalFile(Settings.PATH_JUMPS, false);
	}
	public static String getPathPriceData(){
		return getLocalFile(Settings.PATH_PRICE_DATA, !portable);
	}
	public static String getPathAssets(){
		return getLocalFile(Settings.PATH_ASSETS, !portable);
	}
	public static String getPathItems(){
		return getLocalFile(Settings.PATH_ITEMS, false);
	}
	public static String getPathLocations(){
		return getLocalFile(Settings.PATH_LOCATIONS, false);
	}
	public static String getPathReadme(){
		return getLocalFile(Settings.PATH_README, false);
	}
	public static String getPathLicense(){
		return getLocalFile(Settings.PATH_LICENSE, false);
	}
	public static String getPathCredits(){
		return getLocalFile(Settings.PATH_CREDITS, false);
	}
	public static String getUserDirectory(){
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath()+File.separator;
	}

	private static String getLocalFile(String filename){
    return getLocalFile(filename, true);
  }

  /**
   *
   * @param filename the name of the data file to obtain
   * @param dynamic true if the file is expecting to be written to, false for things like the items and locations.
   * @return
   */
	private static String getLocalFile(String filename, boolean dynamic){
		try {
			File file = null;
			File ret = null;
			if (dynamic) {
				File userDir = new File(System.getProperty("user.home", "."));
				file = new File(userDir.getAbsolutePath()+File.separator+".jeveassets");
				ret = new File(file.getAbsolutePath()+File.separator+filename);
				File parent = ret.getParentFile();
				if (!parent.exists()) {
					if (!parent.mkdirs()) {
						Log.error("failed to create directories for " + parent.getAbsolutePath());
					}
				}
			} else {
				file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
				ret = new File(file.getAbsolutePath()+File.separator+filename);
			}
			return ret.getAbsolutePath();
		} catch (URISyntaxException ex) {
			Log.error("Failed to get program directory: Please email the latest error.txt in the logs directory to niklaskr@gmail.com", ex);
		}
		return null;
	}

	public static Date getGmtNow() {
		return getGmt( new Date() );
	}

	public static Date getGmt(Date date) {
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date( date.getTime() - tz.getRawOffset() );

		// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
		if ( tz.inDaylightTime( ret )) {
			Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if ( tz.inDaylightTime( dstDate )) {
				ret = dstDate;
			}
		}
		return ret;
	}

	public boolean isUpdatable(Date date){
		return ( (Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| Program.FORCE_UPDATE
				|| getApiProxy() != null)
				&& !Program.FORCE_NO_UPDATE);
	}
}

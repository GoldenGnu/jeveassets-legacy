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

package net.nikr.eve.jeveasset.io.local;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.TableSettings;
import net.nikr.eve.jeveasset.data.TableSettings.ResizeMode;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserItemNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.io.local.update.Update;
import net.nikr.eve.jeveasset.io.online.FactionGetter;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SettingsReader extends AbstractXmlReader {
	
	public static final int SETTINGS_VERSION = 2;

	private final static Logger LOG = LoggerFactory.getLogger(SettingsReader.class);

	public static boolean load(Settings settings){
		try {
			Update updater = new Update();
			updater.performUpdates(SETTINGS_VERSION);

			Element element = getDocumentElement(Settings.getPathSettings());
			parseSettings(element, settings);
		} catch (IOException ex) {
			LOG.info("Settings not loaded");
			return false;
		} catch (XmlException ex) {
			LOG.error("Settings parser error: ("+Settings.getPathSettings()+")"+ex.getMessage(), ex);
		}
		LOG.info("Settings loaded");
		return true;
	}

	private static void parseSettings(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}

		//Faction Price Data
		NodeList factionPricesNodes = element.getElementsByTagName("factionprices");
		if (factionPricesNodes.getLength() == 1){
			Element factionPricesElement = (Element) factionPricesNodes.item(0);
			NodeList factionPriceNodes = factionPricesElement.getElementsByTagName("factionprice");
			FactionGetter.parseNodes(factionPriceNodes , settings.getPriceFactionData());
		}
		//Overview
		NodeList overviewNodes = element.getElementsByTagName("overview");
		if (overviewNodes.getLength() == 1){
			Element overviewElement = (Element) overviewNodes.item(0);
			parseOverview(overviewElement, settings);
		}

		//Window
		NodeList windowNodes = element.getElementsByTagName("window");
		if (windowNodes.getLength() == 1){
			Element windowElement = (Element) windowNodes.item(0);
			parseWindow(windowElement, settings);
		}

		//Reprocessing
		NodeList reprocessingNodes = element.getElementsByTagName("reprocessing");
		if (reprocessingNodes.getLength() == 1){
			Element reprocessingElement = (Element) reprocessingNodes.item(0);
			parseReprocessing(reprocessingElement, settings);
		}

		//BPOs
		NodeList bposNodes = element.getElementsByTagName("bpos");
		if (bposNodes.getLength() == 1){
			Element bposElement = (Element) bposNodes.item(0);
			parseBposPrices(bposElement, settings);
		}

		//UserPrices
		NodeList userPriceNodes = element.getElementsByTagName("userprices");
		if (userPriceNodes.getLength() == 1){
			Element userPriceElement = (Element) userPriceNodes.item(0);
			parseUserPrices(userPriceElement, settings);
		}


		//User Item Names
		NodeList userItemNameNodes = element.getElementsByTagName("itemmames");
		if (userItemNameNodes.getLength() == 1){
			Element userItemNameElement = (Element) userItemNameNodes.item(0);
			parseUserItemNames(userItemNameElement, settings);
		}

		//PriceDataSettings
		NodeList priceDataSettingsNodes = element.getElementsByTagName("marketstat");
		if (priceDataSettingsNodes.getLength() == 1){
			Element priceDataSettingsElement = (Element) priceDataSettingsNodes.item(0);
			parsePriceDataSettings(priceDataSettingsElement, settings);
		}
		
		//Flags
		NodeList flagNodes = element.getElementsByTagName("flags");
		if (flagNodes.getLength() != 1){
			throw new XmlException("Wrong flag element count.");
		}
		Element flagsElement = (Element) flagNodes.item(0);
		parseFlags(flagsElement, settings);

		//TableSettings
		NodeList tableSettingsNodes = element.getElementsByTagName("tables");
		if (tableSettingsNodes.getLength() == 1){
			Element tableSettingsElement = (Element) tableSettingsNodes.item(0);
			parseTableSettings(tableSettingsElement, settings);
		}

		//Updates
		NodeList updateNodes = element.getElementsByTagName("updates");
		if (updateNodes.getLength() != 1){
			throw new XmlException("Wrong updates element count.");
		}
		Element updatesElement = (Element) updateNodes.item(0);
		parseUpdates(updatesElement, settings);

		//Filters
		NodeList filterNodes = element.getElementsByTagName("filters");
		if (filterNodes.getLength() != 1){
			throw new XmlException("Wrong filters element count.");
		}
		Element filtersElement = (Element) filterNodes.item(0);
		parseFilters(filtersElement, settings.getAssetFilters());
		
		// Proxy can have 0 or 1 proxy elements; at 0, the proxy stays as null.
		NodeList proxyNodes = element.getElementsByTagName("proxy");
		if (proxyNodes.getLength() == 1) {
			Element proxyElement = (Element) proxyNodes.item(0);
			parseProxy(proxyElement, settings);
		} else if (proxyNodes.getLength() > 1) {
			throw new XmlException("Wrong proxy element count.");
		}

		// API Proxy; 0 or 1 elements.
		NodeList apiProxyNodes = element.getElementsByTagName("apiProxy");
		switch (apiProxyNodes.getLength()) { // I think the 'switch' is a lot neater then the if/elseif blocks. - Candle
			case 0:
				break;
			case 1:
				Element apiProxyElement = (Element) apiProxyNodes.item(0);
				parseApiProxy(apiProxyElement, settings);
				break;
			default:
				throw new XmlException("Wrong apiProxy element count.");
		}
	}

	private static void parseOverview(Element overviewElement, Settings settings) throws XmlException {
		NodeList groupNodes = overviewElement.getElementsByTagName("group");
		for (int a = 0; a < groupNodes.getLength(); a++){
			Element groupNode = (Element) groupNodes.item(a);
			String name = AttributeGetters.getString(groupNode, "name");
			OverviewGroup overviewGroup = new OverviewGroup(name);
			settings.getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
			NodeList locationNodes = groupNode.getElementsByTagName("location");
			for (int b = 0; b < locationNodes.getLength(); b++){
				Element locationNode = (Element) locationNodes.item(b);
				String location = AttributeGetters.getString(locationNode, "name");
				String type = AttributeGetters.getString(locationNode, "type");
				overviewGroup.add( new OverviewLocation(location, OverviewLocation.LocationType.valueOf(type)));
			}
		}
	}

	private static void parseReprocessing(Element windowElement, Settings settings) throws XmlException {
		int refining = AttributeGetters.getInt(windowElement, "refining");
		int efficiency = AttributeGetters.getInt(windowElement, "efficiency");
		int processing = AttributeGetters.getInt(windowElement, "processing");
		int station = AttributeGetters.getInt(windowElement, "station");
		settings.setReprocessSettings( new ReprocessSettings(station, refining, efficiency, processing));
	}

	private static void parseWindow(Element windowElement, Settings settings) throws XmlException {
		int x = AttributeGetters.getInt(windowElement, "x");
		int y = AttributeGetters.getInt(windowElement, "y");
		int height = AttributeGetters.getInt(windowElement, "height");
		int width = AttributeGetters.getInt(windowElement, "width");
		boolean maximized = AttributeGetters.getBoolean(windowElement, "maximized");
		boolean autosave = AttributeGetters.getBoolean(windowElement, "autosave");
		settings.setWindowLocation( new Point(x, y));
		settings.setWindowSize( new Dimension(width, height));
		settings.setWindowMaximized(maximized);
		settings.setWindowAutoSave(autosave);
	}

	private static void parseProxy(Element proxyElement, Settings settings) throws XmlException {
		String addrName = AttributeGetters.getString(proxyElement, "address");
		String proxyType = AttributeGetters.getString(proxyElement, "type");
		Integer port = AttributeGetters.getInt(proxyElement, "port");
		if (addrName.length() > 0
						&& proxyType.length() > 0
						&& port != null
						&& port >= 0) { // check the proxy attributes are all there.

			// delegate to the utility method in the Settings.
			try {
				settings.setProxy(addrName, port, proxyType);
			} catch (IllegalArgumentException iae) { //catch none valid proxt settings
				settings.setProxy(null);
			}
			
		}
	}

	private static void parseBposPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("bpo");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			long id = AttributeGetters.getLong(currentNode, "id");
			settings.getBpos().add(id);
		}
	}
	private static void parseUserPrices(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("userprice");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			double price = AttributeGetters.getDouble(currentNode, "price");
			int typeID = AttributeGetters.getInt(currentNode, "typeid");
			UserItem<Integer,Double> userPrice = new UserPrice(price, typeID, name);
			settings.getUserPrices().put(typeID, userPrice);
		}
	}

	private static void parseUserItemNames(Element element, Settings settings){
		NodeList userPriceNodes = element.getElementsByTagName("itemname");
		for (int a = 0; a < userPriceNodes.getLength(); a++){
			Element currentNode = (Element) userPriceNodes.item(a);
			String name = AttributeGetters.getString(currentNode, "name");
			String typeName = AttributeGetters.getString(currentNode, "typename");
			long itemId = AttributeGetters.getLong(currentNode, "itemid");
			UserItem<Long,String> userItemName = new UserName(name, itemId, typeName);
			settings.getUserItemNames().put(itemId, userItemName);
		}
	}

	private static void parsePriceDataSettings(Element element, Settings settings){
		int region = AttributeGetters.getInt(element, "region");
		EveAsset.PriceMode priceType = EveAsset.getDefaultPriceType();
		if (AttributeGetters.haveAttribute(element, "defaultprice")){
			priceType = EveAsset.PriceMode.valueOf(AttributeGetters.getString(element, "defaultprice"));
		}
		String source = PriceDataSettings.SOURCE_EVE_CENTRAL;
		if (AttributeGetters.haveAttribute(element, "source")){
			source = AttributeGetters.getString(element, "source");
		}
		EveAsset.setPriceType(priceType);
		settings.setPriceDataSettings( new PriceDataSettings(region, source) );
	}

	private static void parseFlags(Element element, Settings settings){
		NodeList flagNodes = element.getElementsByTagName("flag");
		for (int a = 0; a < flagNodes.getLength(); a++){
			Element currentNode = (Element) flagNodes.item(a);
			String key = AttributeGetters.getString(currentNode, "key");
			boolean enabled = AttributeGetters.getBoolean(currentNode, "enabled");
			settings.getFlags().put(key, enabled);
		}
	}
	private static void parseTableSettings(Element element, Settings settings){
		NodeList tablesNodes = element.getElementsByTagName("table");
		for (int a = 0; a < tablesNodes.getLength(); a++){
			Element tableNode = (Element) tablesNodes.item(a);
			String table = AttributeGetters.getString(tableNode, "name");
			ResizeMode resize = ResizeMode.valueOf(AttributeGetters.getString(tableNode, "resize"));
			NodeList columnNodes = tableNode.getElementsByTagName("column");
			List<String> tableColumnNames = new ArrayList<String>();
			List<String> tableColumnVisible = new ArrayList<String>();
			for (int b = 0; b < columnNodes.getLength(); b++){
				Element columnNode = (Element) columnNodes.item(b);
				String name = AttributeGetters.getString(columnNode, "name");
				boolean visible = AttributeGetters.getBoolean(columnNode, "visible");
				tableColumnNames.add(name);
				if (visible) tableColumnVisible.add(name);
			}
			TableSettings tableSettings = settings.getTableSettings().get(table);
			tableSettings.setMode(resize);
			tableSettings.setTableColumnNames(tableColumnNames);
			tableSettings.setTableColumnVisible(tableColumnVisible);


		}
	}

	private static void parseUpdates(Element element, Settings settings){
		NodeList updateNodes = element.getElementsByTagName("update");
		for (int a = 0; a < updateNodes.getLength(); a++){
			Element currentNode = (Element) updateNodes.item(a);
			parseUpdate(currentNode, settings);
		}
	}
	private static void parseUpdate(Element element, Settings settings){
		String text = AttributeGetters.getString(element, "name");
		Date nextUpdate = new Date( AttributeGetters.getLong(element, "nextupdate") );
		if (text.equals("conquerable station")){
			settings.setConquerableStationsNextUpdate(nextUpdate);
		}
	}

	private static void parseFilters(Element element, Map<String, List<AssetFilter>> assetFilters){
		NodeList filterNodes = element.getElementsByTagName("filter");
		for (int a = 0; a < filterNodes.getLength(); a++){
			Element currentNode = (Element) filterNodes.item(a);
			String name = parseFilter(currentNode);
			assetFilters.put(name, parseFilterRows(currentNode));
		}
	}

	private static String parseFilter(Element element){
		return AttributeGetters.getString(element, "name");
	}

	private static List<AssetFilter> parseFilterRows(Element element){
		List<AssetFilter> assetFilters = new ArrayList<AssetFilter>();
		NodeList rowNodes = element.getElementsByTagName("row");
		for (int a = 0; a < rowNodes.getLength(); a++){
			Element currentNode = (Element) rowNodes.item(a);
			AssetFilter assetFilter = parseAssetFilter(currentNode);
			assetFilters.add(assetFilter);
		}
		return assetFilters;
	}

	private static AssetFilter parseAssetFilter(Element element){
		String text = AttributeGetters.getString(element, "text");
		String column = AttributeGetters.getString(element, "column");
		String mode = AttributeGetters.getString(element, "mode");
		boolean and = AttributeGetters.getBoolean(element, "and");
		String columnMatch = null;
		if (AttributeGetters.haveAttribute(element, "columnmatch")){
			columnMatch = AttributeGetters.getString(element, "columnmatch");
		}
		return new AssetFilter(column, text, AssetFilter.Mode.valueOf(mode), and ? AssetFilter.Junction.AND : AssetFilter.Junction.OR, columnMatch);
	}

	private static void parseApiProxy(Element apiProxyElement, Settings settings) {
		String proxyURL = AttributeGetters.getString(apiProxyElement, "url");
		settings.setApiProxy(proxyURL);
	}
}

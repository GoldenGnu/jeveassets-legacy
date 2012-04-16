/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.tests.mocks;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.awt.Dimension;
import java.awt.Point;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.data.model.Galaxy;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;

/**
 *
 * @author Candle
 */
public abstract class FakeSettings extends Settings {

	public FakeSettings() {
		super(false);
	}

	@Override
	public void clearEveAssetList() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Account> getAccounts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getApiProxy() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getConquerableStationsNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Asset> getEventListAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Boolean> getFlags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataGetter getPriceDataGetter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getPriceDataNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataSettings getPriceDataSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Profile> getProfiles() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Proxy getProxy() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ReprocessSettings getReprocessSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Integer> getUniqueIds() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, UserItem<Long,String>> getUserItemNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, UserItem<Integer,Double>> getUserPrices() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Point getWindowLocation() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Dimension getWindowSize() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean hasAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAutoUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFilterOnEnter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isForceUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isHighlightSelectedRows() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isReprocessColors() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSettingsLoaded() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdatable(Date date) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdatable(Date date, boolean ignoreOnProxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdateDev() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowAutoSave() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowMaximized() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void loadActiveProfile() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void saveAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void saveSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAccounts(List<Account> accounts) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setActiveProfile(Profile activeProfile) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setApiProxy(String apiProxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAutoUpdate(boolean updateStable) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setConquerableStationsNextUpdate(Date conquerableStationNextUpdate) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFilterOnEnter(boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setHighlightSelectedRows(boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceData(Map<Integer, PriceData> priceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceDataSettings(PriceDataSettings priceDataSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProfiles(List<Profile> profiles) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(Proxy proxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(String host, int port, String type) throws IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(String host, int port, Type type) throws IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessColors(boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessSettings(ReprocessSettings reprocessSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUpdateDev(boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserItemNames(Map<Long, UserItem<Long,String>> userItemNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserPrices(Map<Integer, UserItem<Integer,Double>> userPrices) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAutoSave(boolean windowAutoSave) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowLocation(Point windowLocation) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowMaximized(boolean windowMaximized) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowSize(Dimension windowSize) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Profile getActiveProfile() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, ApiStation> getConquerableStations() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, ItemFlag> getItemFlags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, Item> getItems() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Jump> getJumps() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, Location> getLocations() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, OverviewGroup> getOverviewGroups() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIgnoreSecureContainers() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIgnoreSecureContainers(boolean ignoreSecureContainers) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Galaxy getGalaxyModel() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, PriceData> getPriceFactionData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Stockpile> getStockpiles() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setConquerableStations(Map<Long, ApiStation> conquerableStations) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPrice(int typeID, boolean isBlueprintCopy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<SimpleColumn>> getTableColumns() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<Filter>> getTableFilters(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public float getVolume(int typeID, boolean packaged) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isStockpileFocusTab() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isStockpileHalfColors() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileFocusTab(boolean stockpileFocusOnAdd) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileHalfColors(boolean stockpileHalfColors) {
		throw new UnsupportedOperationException("not implemented");
	}
}

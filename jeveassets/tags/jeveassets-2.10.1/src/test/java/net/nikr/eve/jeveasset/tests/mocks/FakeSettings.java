/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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

import java.awt.Dimension;
import java.awt.Point;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.ExportSettings;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.data.RoutingSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagID;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;

/**
 *
 * @author Candle
 */
public abstract class FakeSettings extends Settings {

	public FakeSettings() { }

	@Override
	public String getApiProxy() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getConquerableStationsNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<SettingFlag, Boolean> getFlags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataSettings getPriceDataSettings() {
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
	public Map<Long, UserItem<Long, String>> getUserItemNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, UserItem<Integer, Double>> getUserPrices() {
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
	public boolean isUpdatable(final Date date) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdatable(final Date date, final boolean ignoreOnProxy) {
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
	public void setApiProxy(final String apiProxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setConquerableStationsNextUpdate(final Date conquerableStationNextUpdate) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFilterOnEnter(final boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setHighlightSelectedRows(final boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceData(final Map<Integer, PriceData> priceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceDataSettings(final PriceDataSettings priceDataSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(final Proxy proxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(final String host, final int port, final String type) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(final String host, final int port, final Type type) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessColors(final boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserItemNames(final Map<Long, UserItem<Long, String>> userItemNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserPrices(final Map<Integer, UserItem<Integer, Double>> userPrices) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAutoSave(final boolean windowAutoSave) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowLocation(final Point windowLocation) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowMaximized(final boolean windowMaximized) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowSize(final Dimension windowSize) {
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
	public void setIgnoreSecureContainers(final boolean ignoreSecureContainers) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Stockpile> getStockpiles() {
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
	public Map<String, List<Filter>> getTableFilters(final String key) {
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
	public void setStockpileFocusTab(final boolean stockpileFocusOnAdd) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileHalfColors(final boolean stockpileHalfColors) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, ResizeMode> getTableResize() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowAlwaysOnTop() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAlwaysOnTop(final boolean windowAlwaysOnTop) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getPathSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeBuyOrders() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeSellOrders() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeBuyOrders(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeSellOrders(boolean includeSellOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String, Integer>> getTableColumnsWidth() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getMaximumPurchaseAge() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMaximumPurchaseAge(int maximumPurchaseAge) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, Date> getAssetAdded() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<Value>> getTrackerData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, String> getOwners() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, PriceData> getPriceData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ExportSettings getExportSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFilterOnEnter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String ,View>> getTableViews() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, View> getTableViews(String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Tag> getTags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Tags getTags(TagID tagID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSettingsImported() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setSettingsImported(boolean settingsImported) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBlueprintBasePriceTech1() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setBlueprintBasePriceTech1(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBlueprintBasePriceTech2() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setBlueprintBasePriceTech2(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setJournalHistory(boolean blueprintsTech2) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isJournalHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTransactionHistory(boolean transactionHistory) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTransactionHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setEveNames(Map<Long, String> eveNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, String> getEveNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public RoutingSettings getRoutingSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeSellContracts(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeSellContracts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeBuyContracts(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeBuyContracts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileColorGroup3(int stockpileColorGroup2) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getStockpileColorGroup3() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileColorGroup2(int stockpileColorGroup1) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getStockpileColorGroup2() {
		throw new UnsupportedOperationException("not implemented");
	}
}
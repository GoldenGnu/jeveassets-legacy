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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsAssets;

public enum EveAssetTableFormat implements EnumTableColumn<EveAsset> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnName();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getName();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnGroup();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getGroup();
		}
	},
	CATEGORY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCategory();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getCategory();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnOwner();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnLocation();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getLocation();
		}
	},
	SECURITY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getSecurity();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnRegion();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getRegion();
		}
	},
	CONTAINER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnContainer();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getContainer();
		}
	},
	FLAG(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnFlag();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getFlag();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPrice();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getPrice();
		}
	},
	SELL_MIN(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSellMin();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getPriceSellMin();
		}
	},
	BUY_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnBuyMax();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getPriceBuyMax();
		}
	},
	REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnReprocessed();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getPriceReprocessed();
		}
	},
	BASE_PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnBasePrice();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getPriceBase();
		}
	},
	REPROCESSED_VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnReprocessedValue();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getValueReprocessed();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValue();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getValue();
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCount();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getCount();
		}
	},
	TYPE_COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeCount();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getTypeCount();
		}
	},
	META(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMeta();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getMeta();
		}
	},
	VOLUME(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolume();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getVolume();
		}
	},
	ITEM_ID(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnItemID();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getItemID();
		}
	},
	TYPE_ID(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeID();
		}
		@Override
		public Object getColumnValue(EveAsset from) {
			return from.getTypeID();
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private EveAssetTableFormat(Class type, Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class getType() {
		return type;
	}
	@Override
	public Comparator getComparator() {
		return comparator;
	}
}
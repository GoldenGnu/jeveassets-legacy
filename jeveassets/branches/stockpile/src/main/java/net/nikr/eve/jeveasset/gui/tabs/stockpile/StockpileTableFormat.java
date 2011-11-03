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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.TableColumn;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


enum StockpileTableFormat implements TableColumn<StockpileItem> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnName();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getName();
		}
	},
	COUNT_NOW(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNow();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getCountNow();
		}
	},
	COUNT_NEEDED(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountNeeded();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getCountNeeded();
		}
	},
	COUNT_MINIMUM(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnCountMinimum();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getCountMinimum();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnPrice();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getPrice();
		}
	},
	VALUE_NOW(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnValueNow();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getValueNow();
		}
	},
	VALUE_NEEDED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnValueNeeded();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getValueNeeded();
		}
	},
	VOLUME_NOW(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnVolumeNow();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getVolumeNow();
		}
	},
	VOLUME_NEEDED(Float.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsStockpile.get().columnVolumeNeeded();
		}
		@Override
		public Object getColumnValue(StockpileItem from) {
			return from.getVolumeNeeded();
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private StockpileTableFormat(Class type, Comparator<?> comparator) {
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

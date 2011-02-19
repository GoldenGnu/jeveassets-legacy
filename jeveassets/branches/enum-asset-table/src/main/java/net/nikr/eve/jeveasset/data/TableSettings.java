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

package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;


public class TableSettings<T extends Enum<T> & EnumTableColumn<Q>, Q>{

	public enum ResizeMode {
		TEXT,
		WINDOW,
		NONE
	}

	private final List<T> tableColumnOriginal;
	private final List<T> orderColumns;
	private final List<T> shownColumns;
	private ResizeMode mode;

	public TableSettings(List<T> tableColumnOriginal) {
		this(tableColumnOriginal, ResizeMode.TEXT);
	}

	public TableSettings(List<T> tableColumnOriginal, ResizeMode mode) {
		if (tableColumnOriginal == null || mode == null){
			throw new IllegalArgumentException("Arguments can not be null");
		}
		this.tableColumnOriginal = tableColumnOriginal;
		orderColumns = new ArrayList<T>(tableColumnOriginal);
		shownColumns = new ArrayList<T>(tableColumnOriginal);
		this.mode = mode;
	}

	public ResizeMode getMode() {
		return mode;
	}

	public void setMode(ResizeMode mode) {
		this.mode = mode;
	}

	public T getColumn(String s){
		for (T column : tableColumnOriginal){
			if (column.toString().equals(s)) return column;
		}
		return null;
	}

	public List<T> getTableColumnVisible() {
		return shownColumns;
	}

	public void setTableColumnVisible(List<T> tableColumnVisible) {
		this.shownColumns.clear();
		this.shownColumns.addAll(tableColumnVisible);
	}

	public List<T> getTableColumnNames() {
		return orderColumns;
	}

	public List<EnumTableColumn> getTableColumns() {
		List<EnumTableColumn> columns = new ArrayList<EnumTableColumn>();
		for (T column: orderColumns){
			columns.add(column);
		}
		return columns;
	}

	public List<String> getNames() {
		List<String> columns = new ArrayList<String>();
		for (EnumTableColumn column : orderColumns){
			columns.add(column.getColumnName());
		}
		return columns;
	}

	public void setTableColumnNames(List<T> tableColumnNames) {
		this.orderColumns.clear();
		this.orderColumns.addAll(tableColumnNames);
	}

}

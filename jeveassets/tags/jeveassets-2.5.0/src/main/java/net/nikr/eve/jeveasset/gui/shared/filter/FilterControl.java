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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.ExtraColumns;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;


public abstract class FilterControl<E> implements ListEventListener<E> {

	private final String name;
	private final List<EventList<E>> eventLists;
	private final List<FilterList<E>> filterLists;
	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;

	/** Do not use this constructor - it's here only for test purposes. */
	protected FilterControl() {
		name = null;
		eventLists = null;
		filterLists = null;
		filters = null;
		defaultFilters = null;
		gui = null;
	}

	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters) {
		this(jFrame, name, Collections.singletonList(eventList), Collections.singletonList(filterList), filters);
	}

	protected FilterControl(final JFrame jFrame, final String name, final List<EventList<E>> eventLists, final List<FilterList<E>> filterLists, final Map<String, List<Filter>> filters) {
		this(jFrame, name, eventLists, filterLists, filters, new HashMap<String, List<Filter>>());
	}

	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		this(jFrame, name, Collections.singletonList(eventList), Collections.singletonList(filterList), filters, defaultFilters);
	}

	protected FilterControl(final JFrame jFrame, final String name, final List<EventList<E>> eventLists, final List<FilterList<E>> filterLists, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		this.name = name;
		this.eventLists = eventLists;
		this.filterLists = filterLists;
		this.filters = filters;
		this.defaultFilters = defaultFilters;
		for (FilterList<E> filterList : filterLists) {
			filterList.addListEventListener(this);
		}
		gui = new FilterGui<E>(jFrame, this);
	}

	public List<Filter> getCurrentFilters() {
		return gui.getFilters();
	}

	public void clearCurrentFilters() {
		gui.clear();
	}

	public void addFilter(final Filter filter) {
		gui.addFilter(filter);
	}

	public void addFilters(final List<Filter> filters) {
		gui.addFilters(filters);
	}

	public String getCurrentFilterName() {
		return gui.getCurrentFilterName();
	}

	public JPanel getPanel() {
		return gui.getPanel();
	}

	public void addToolButton(final AbstractButton jButton) {
		gui.addToolButton(jButton);
	}
	public void addToolButton(final AbstractButton jButton, final int width) {
		gui.addToolButton(jButton, width);
	}
	public void addToolSeparator() {
		gui.addToolSeparator();
	}

	public JMenu getMenu(final JTable jTable, final List<E> items) {
		String text = null;
		Enum<?> column = null;
		boolean isNumeric = false;
		boolean isDate = false;
		TableModel model = jTable.getModel();
		int columnIndex = jTable.getSelectedColumn();
		if (model instanceof EventTableModel) {
			EventTableModel<?> tableModel = (EventTableModel<?>) model;
			TableFormat<?> tableFormat = tableModel.getTableFormat();
			if (tableFormat instanceof EnumTableFormatAdaptor) {
				EnumTableFormatAdaptor<?, ?> adaptor = (EnumTableFormatAdaptor) tableFormat;
				if (columnIndex >= 0
						&& columnIndex < adaptor.getShownColumns().size()
						&& items.size() == 1
						) {
					Object object = adaptor.getShownColumns().get(columnIndex);
					if (object instanceof Enum) {
						column = (Enum) object;
						isNumeric = isNumeric(column);
						isDate = isDate(column);
						text = FilterMatcher.format(getColumnValue(items.get(0), column.name()), false, false);
					}
				}
			}
		}
		return new FilterMenu<E>(gui, column, text, isNumeric, isDate);
	}

	String getName() {
		return name;
	}

	List<EventList<E>> getEventLists() {
		return eventLists;
	}

	List<FilterList<E>> getFilterLists() {
		return filterLists;
	}

	Map<String, List<Filter>> getFilters() {
		return filters;
	}

	public Map<String, List<Filter>> getAllFilters() {
		//Need to be updated each time something has changed....
		Map<String, List<Filter>> allFilters = new HashMap<String, List<Filter>>();
		allFilters.putAll(defaultFilters);
		allFilters.putAll(filters);
		return allFilters;
	}

	Map<String, List<Filter>> getDefaultFilters() {
		return defaultFilters;
	}

	int getTotalSize() {
		int totalSize = 0;
		for (EventList<E> eventList : eventLists) {
			totalSize = totalSize + eventList.size();
		}
		return totalSize;
	}

	protected abstract Enum<?>[] getColumns();
	protected abstract List<EnumTableColumn<E>> getEnumColumns();
	protected abstract List<EnumTableColumn<E>> getEnumShownColumns();
	protected abstract Enum<?> valueOf(String column);
	/**
	 * Use isNumeric(Enum column) instead.
	 */
	protected abstract boolean isNumericColumn(Enum<?> column);
	/**
	 * Use isDate(Enum column) instead.
	 */
	protected abstract boolean isDateColumn(Enum<?> column);
	protected abstract Object getColumnValue(E item, String column);

	/**
	 * Overwrite to do stuff before filtering.
	 */
	protected void beforeFilter() { }

	/**
	 * Overwrite to do stuff after filtering.
	 */
	protected void afterFilter() { }

	/**
	 * Overwrite to do stuff after saved filters have been changed.
	 */
	protected void updateFilters() { }

	protected List<EnumTableColumn<E>> columnsAsList(final EnumTableColumn<E>[] fixme) {
		List<EnumTableColumn<E>> columns = new ArrayList<EnumTableColumn<E>>();
		columns.addAll(Arrays.asList(fixme));
		return columns;
	}

	boolean isNumeric(final Enum<?> column) {
		if (column instanceof ExtraColumns) {
			return false;
		} else {
			return isNumericColumn(column);
		}
	}

	boolean isDate(final Enum<?> column) {
		if (column instanceof ExtraColumns) {
			return false;
		} else {
			return isDateColumn(column);
		}
	}
	boolean isAll(final Enum<?> column) {
		return (column instanceof ExtraColumns);
	}

	@Override
	public void listChanged(final ListEvent<E> listChanges) {
		gui.updateShowing();
	}
}

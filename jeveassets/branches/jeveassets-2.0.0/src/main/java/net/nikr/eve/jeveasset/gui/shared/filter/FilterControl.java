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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.ExtraColumns;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;


public abstract class FilterControl<E> implements ListEventListener<E>{
	
	//TODO i18n Use localized input
	//public static final Locale LOCALE = Locale.getDefault();
	
	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN
	
	private final Map<String, List<Filter>> filters;
	private final List<FilterList<E>> filterLists;
	private final List<EventList<E>> eventLists;
	private FilterGui<E> gui;

	protected FilterControl(JFrame jFrame, Map<String, List<Filter>> filters, FilterList<E> filterList, EventList<E> eventList) {
		this(jFrame, filters, Collections.singletonList(filterList), Collections.singletonList(eventList));
	}
	protected FilterControl(JFrame jFrame, Map<String, List<Filter>> filters, List<FilterList<E>> filterLists, List<EventList<E>> eventLists) {
		this.filters = filters;
		this.filterLists = filterLists;
		this.eventLists = eventLists;
		for (EventList<E> eventList : eventLists){
			eventList.addListEventListener(this);
		}
		gui = new FilterGui<E>(jFrame, this);
	}
	
	public JPanel getPanel(){
		return gui.getPanel();
	}
	
	public void addToolButton(AbstractButton jButton){
		gui.addToolButton(jButton);
	}
	public void addToolButton(AbstractButton jButton, int width){
		gui.addToolButton(jButton, width);
	}
	public void addToolSeparator(){
		gui.addToolSeparator();
	}
	
	public JMenu getMenu(JTable jTable){
		String text = null;
		Enum column = null;
		boolean isNumeric = false;
		boolean isDate = false;
		TableModel model = jTable.getModel();
		int rowIndex = jTable.getSelectedRow();
		int columnIndex = jTable.getSelectedColumn();
		
		if (rowIndex >= 0 && columnIndex >= 0
				&& jTable.getSelectedRows().length == 1
				&& jTable.getSelectedColumns().length == 1){
			text =  format( model.getValueAt(rowIndex, columnIndex) );
		}
		
		if (model instanceof EventTableModel){
			EventTableModel<?> tableModel = (EventTableModel<?>) model;
			TableFormat<?> tableFormat = tableModel.getTableFormat();
			if (tableFormat instanceof EnumTableFormatAdaptor){
				EnumTableFormatAdaptor adaptor = (EnumTableFormatAdaptor) tableFormat;
				if (columnIndex >= 0 && columnIndex < adaptor.getShownColumns().size()){
					column = (Enum) adaptor.getShownColumns().get(columnIndex);
					isNumeric = isNumeric(column);
					isDate = isDate(column);
				}

			}
		}
		return new FilterMenu<E>(gui, column, text, isNumeric, isDate);
	}

	List<FilterList<E>> getFilterLists() {
		return filterLists;
	}

	Map<String, List<Filter>> getFilters() {
		return filters;
	}
	
	int getTotalSize(){
		int totalSize = 0;
		for (EventList<E> eventList : eventLists){
			totalSize = totalSize + eventList.size();
		}
		return totalSize;
		
	}

	protected abstract Enum[] getColumns();
	protected abstract Enum valueOf(String column);
	/**
	 * Use isNumeric(Enum column) instead
	 */
	protected abstract boolean isNumericColumn(Enum column);
	/**
	 * Use isDate(Enum column) instead
	 */
	protected abstract boolean isDateColumn(Enum column);
	protected abstract Object getColumnValue(E item, String column);

	/**
	 * Overwrite to do stuff before filtering
	 */
	protected void beforeFilter() {}

	/**
	 * Overwrite to do stuff after filtering
	 */
	protected void afterFilter() {}
	
	boolean isNumeric(Enum column) {
		if(column instanceof ExtraColumns){
			return false;
		} else {
			return isNumericColumn(column);
		}
	}
		
	boolean isDate(Enum column) {
		if(column instanceof ExtraColumns){
			return false;
		} else {
			return isDateColumn(column);
		}
	}
	boolean isAll(Enum column) {
		if (column instanceof ExtraColumns){
			return true;
		} else {
			return false;
		}
	}
	
	boolean matches(final E item, final Enum enumColumn, final CompareType compare, final String text){
		if (enumColumn instanceof ExtraColumns){
			if (CompareType.isNot(compare)){
				boolean found = false;
				for (Enum testColumn : getColumns()){
					if (!matches(item, testColumn, compare, text)){ //Found
						found = true;
					}
				}
				return !found;
			} else {
				for (Enum testColumn : getColumns()){
					boolean found = matches(item, testColumn, compare, text);
					if (found) return true;
				}
				return false;
			}
		}
		Object column = getColumnValue(item, enumColumn.name());
		if (column == null) return false;
		if (compare == CompareType.CONTAINS){
			return contains(column, text);
		} else if (compare == CompareType.CONTAINS_NOT){
			return !contains(column, text);
		} else if (compare == CompareType.EQUALS || compare == CompareType.EQUALS_DATE){
			return equals(column, text);
		} else if (compare == CompareType.EQUALS_NOT || compare == CompareType.EQUALS_NOT_DATE){
			return !equals(column, text);
		} else if (compare == CompareType.GREATER_THAN){
			return great(column, text);
		} else if (compare == CompareType.LESS_THAN){
			return less(column, text);
		} else if (compare == CompareType.BEFORE){
			return before(column, text);
		} else if (compare == CompareType.AFTER){
			return after(column, text);
		} else if (compare == CompareType.GREATER_THAN_COLUMN){
			return great(column, getColumnValue(item, text));
		} else if (compare == CompareType.LESS_THAN_COLUMN){
			return less(column, getColumnValue(item, text));
		} else if (compare == CompareType.EQUALS_COLUMN){
			return equals(column, getColumnValue(item, text));
		} else if (compare == CompareType.EQUALS_NOT_COLUMN){
			return !equals(column, getColumnValue(item, text));
		} else if (compare == CompareType.CONTAINS_COLUMN){
			return contains(column, getColumnValue(item, text));
		} else if (compare == CompareType.CONTAINS_NOT_COLUMN){
			return !contains(column, getColumnValue(item, text));
		} else if (compare == CompareType.BEFORE_COLUMN){
			return before(column, getColumnValue(item, text));
		} else if (compare == CompareType.AFTER_COLUMN){
			return after(column, getColumnValue(item, text));
		} else { //Fallback: show all...
			return true;
		}
	}
	
	private boolean equals(Object object1, Object object2){
		//Null
		if (object1 == null || object2 == null) return false;
		
		//Equals (case insentive)
		return format(object1).equals(format(object2));
	}
	private boolean contains(Object object1, Object object2){
		//Null
		if (object1 == null || object2 == null) return false;
		
		//Contains (case insentive)
		return format(object1).contains(format(object2));
	}
	private boolean less(Object object1, Object object2){
		return greatThen(object2, object1, false);
	}
	private boolean great(Object object1, Object object2){
		return greatThen(object1, object2, true);
	}
	private boolean greatThen(Object object1, Object object2, boolean fallback){
		//Null
		if (object1 == null || object2 == null) return fallback;
		
		//Double / Float
		Double double1 = getDouble(object1);
		Double double2 = getDouble(object2);
		
		//Long / Integer
		Long long1 = getLong(object1);
		Long long2 = getLong(object2);
		
		if (long1 != null && long2 != null) return long1 > long2;
		if (long1 != null && double2 != null) return long1 > double2;
		if (double1 != null && double2 != null) return double1 > double2;
		if (double1 != null && long2 != null) return double1 > long2;
		
		
		return fallback; //Fallback
	}
	
	private boolean before(Object object1, Object object2) {
		//Date
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null && date2 != null){
			return date1.before(date2);
		}
		return false; //Fallback
	}

	private boolean after(Object object1, Object object2) {
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null && date2 != null){
			return date1.after(date2);
		}
		return false;
	}
	
	private Number getNumber(Object obj){
		if ( (obj instanceof Long) || (obj instanceof Integer)
				|| (obj instanceof Double) || (obj instanceof Float) ){
			return (Number)obj;
		} else {
			return createNumber(obj);
		}
	}
	private Double getDouble(Object obj){
		if (obj instanceof Double){
			return (Double)obj;
		} else if (obj instanceof Float){
			return Double.valueOf((Float) obj);
		} else {
			return createNumber(obj);
		}
	}
	private Long getLong(Object obj){
		if (obj instanceof Long){
			return (Long)obj;
		} else if (obj instanceof Integer){
			return Long.valueOf((Integer) obj);
		} else {
			return null;
		}
	}
	private Double createNumber(Object object){
		if (object instanceof String){
			String filterValue = (String) object;
			//Used to check if parsing was successful
			ParsePosition position = new ParsePosition(0);
			//Parse number using the Locale
			Number n = NumberFormat.getInstance(LOCALE).parse(filterValue, position);
			if (n != null && position.getIndex() == filterValue.length()){ //Numeric
				return n.doubleValue();
			}
		}
		return null;
	}
	
	
	private Date getDate(Object obj){
		if (obj instanceof Date){
			return (Date)obj;
		} else if (obj instanceof String){
			return Formater.columnStringToDate((String) obj);
		} else {
			return null;
		}
	}
	
	private String format(Object object1){
		//String
		String compare1 = object1.toString();
		
		//Number
		Number number1 = getNumber(object1);
		if (number1 != null) compare1 = Formater.compareFormat(number1);
		
		//Date
		Date date1 = getDate(object1);
		if (date1 != null) compare1 = Formater.columnDate(date1);
		
		return compare1.toLowerCase();
	}
	
	@Override
	public void listChanged(ListEvent<E> listChanges){
		gui.updateShowing();
	}
}

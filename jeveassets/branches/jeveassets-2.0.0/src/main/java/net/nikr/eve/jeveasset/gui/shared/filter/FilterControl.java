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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.ExtraColumns;


public abstract class FilterControl<E> implements ListEventListener<E>{
	
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
		} else if (compare == CompareType.GREATER_THEN){
			return great(column, text);
		} else if (compare == CompareType.LESS_THEN){
			return less(column, text);
		} else if (compare == CompareType.BEFORE){
			return before(column, text);
		} else if (compare == CompareType.AFTER){
			return after(column, text);
		} else if (compare == CompareType.GREATER_THEN_COLUMN){
			return great(column, getColumnValue(item, text));
		} else if (compare == CompareType.LESS_THEN_COLUMN){
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
		
		//String
		String compare1 = object1.toString();
		String compare2 = object2.toString();
		
		//Number
		Number number1 = getNumber(object1);
		Number number2 = getNumber(object2);
		if (number1 != null) compare1 = Formater.compareFormat(number1);
		if (number2 != null) compare2 = Formater.compareFormat(number2);
		
		//Date
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null) compare1 = Formater.columnDate(date1);
		if (date2 != null) compare2 = Formater.columnDate(date2);
		
		//Equals (case insentive)
		return compare1.toLowerCase().equals(compare2.toLowerCase());
	}
	private boolean contains(Object object1, Object object2){
		//Null
		if (object1 == null || object2 == null) return false;
		
		//String
		String compare1 = object1.toString();
		String compare2 = object2.toString();
		
		//Number
		Number number1 = getNumber(object1);
		Number number2 = getNumber(object2);
		if (number1 != null) compare1 = Formater.compareFormat(number1);
		if (number2 != null) compare2 = Formater.compareFormat(number2);
		
		//Date
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null) compare1 = Formater.columnDate(date1);
		if (date2 != null) compare2 = Formater.columnDate(date2);
		
		//Contains (case insentive)
		return compare1.toLowerCase().contains(compare2.toLowerCase());
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
		if (long1 != null && double2 != null) return double1 > double2;
		if (double1 != null && double2 != null) return double1 > double2;
		if (double1 != null && long2 != null) return long1 > long2;
		
		
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
			return null;
		}
	}
	private Double getDouble(Object obj){
		if (obj instanceof Double){
			return (Double)obj;
		} else if (obj instanceof Float){
			return Double.valueOf((Float) obj);
		} else {
			return null;
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
	private Date getDate(Object obj){
		if (obj instanceof Date){
			return (Date)obj;
		} else if (obj instanceof String){
			return Formater.columnStringToDate((String) obj);
		} else {
			return null;
		}
	}
	
	@Override
	public void listChanged(ListEvent<E> listChanges){
		gui.updateShowing();
	}
}

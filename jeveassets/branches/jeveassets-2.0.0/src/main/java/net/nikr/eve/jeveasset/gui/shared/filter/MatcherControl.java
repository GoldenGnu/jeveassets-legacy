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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;


public abstract class MatcherControl<E> {
	
	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN

	protected final Map<String, List<Filter>> filters;

	public MatcherControl(Map<String, List<Filter>> filters) {
		this.filters = filters;
	}

	protected abstract Enum[] getColumns();
	protected abstract Enum valueOf(String column);
	protected abstract boolean isNumeric(Object column);
	protected abstract Object getColumnValue(E item, String column);
	
	/**
	 * Overwrite to do stuff before filtering
	 */
	protected void beforeFilter() {}

	/**
	 * Overwrite to do stuff after filtering
	 */
	protected void afterFilter() {}
	
	boolean matches(final E item, final Enum enumColumn, final CompareType compare, final String text){
		Object column = getColumnValue(item, enumColumn.name());
		if (column == null) return false;
		if (compare == CompareType.CONTAINS){
			return contains(column, text);
		} else if (compare == CompareType.CONTAINS_NOT){
			return !contains(column, text);
		} else if (compare == CompareType.EQUALS){
			return equals(column, text);
		} else if (compare == CompareType.EQUALS_NOT){
			return !equals(column, text);
		} else if (compare == CompareType.GREATER_THEN){
			return great(column, text);
		} else if (compare == CompareType.LESS_THEN){
			return less(column, text);
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
		} else { //Fallback: show all...
			return true;
		}
	}
	
	private boolean equals(Object object1, Object object2){
		if (object1 == null || object2 == null) return false;
		Number number1 = getNumber(object1);
		Number number2 = getNumber(object2);
		if (number1 == null || number2 == null){
			return object1.toString().toLowerCase().equals(object2.toString().toLowerCase());
		} else {
			return Formater.compareFormat(number1).equals(Formater.compareFormat(number2));
		}
	}
	private boolean contains(Object object1, Object object2){
		if (object1 == null || object2 == null) return false;
		Number number1 = getNumber(object1);
		Number number2 = getNumber(object2);
		if (number1 == null || number2 == null){
			return object1.toString().contains(object2.toString());
		} else {
			return Formater.compareFormat(number1).contains(Formater.compareFormat(number2));
		}
	}
	private boolean less(Object object1, Object object2){
		return greatThen(object2, object1, false);
	}
	private boolean great(Object object1, Object object2){
		return greatThen(object1, object2, true);
	}
	private boolean greatThen(Object object1, Object object2, boolean fallback){
		if (object1 == null || object2 == null) return fallback;
		Double double1 = getDouble(object1);
		Double double2 = getDouble(object2);
		if (double1 == null || double2 == null){
			Long long1 = getLong(object1);
			Long long2 = getLong(object2);
			if (long1 == null || long2 == null){
				return fallback;
			} else {
				return long1 > long2;
			}
		} else {
			return double1 > double2;
		}
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
}

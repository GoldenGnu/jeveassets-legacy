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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;


public abstract class MatcherControl<E> {
	
	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN
	
	private CompareType compare;
	private String text;
	protected final Map<String, List<Filter>> filters;

	public MatcherControl(Map<String, List<Filter>> filters) {
		this.filters = filters;
	}

	boolean matches(E item, Object column, CompareType compare, String text){
		this.compare = compare;
		if (CompareType.isColumnCompare(compare)){ //Column filter - no low case
			this.text = text;
		} else {
			this.text = text.toLowerCase();
		}
		
		return matches(item, column);
	}
	protected abstract boolean matches(E item, Object column);
	protected abstract Object[] getValues();
	protected abstract Object valueOf(String column);
	protected abstract boolean isNumeric(Object column);
	protected abstract String getColumnValue(E item, String column);
	
	protected boolean compare(E item, String column){
		if (column == null) return false;
		column = column.toLowerCase();
		if (compare == CompareType.CONTAINS){
			return column.contains(text);
		} else if (compare == CompareType.CONTAINS_NOT){
			return !column.contains(text);
		} else if (compare == CompareType.EQUALS){
			return equals(column, text);
		} else if (compare == CompareType.EQUALS_NOT){
			return !equals(column, text);
		} else if (compare == CompareType.GREATER_THEN){
			Double nColumn = getNumber(column);
			Double nText = getNumber(text);
			if (nColumn == null || nText == null) return false;
			return nColumn > nText;
		} else if (compare == CompareType.LESS_THEN){
			Double nColumn = getNumber(column);
			Double nText = getNumber(text);
			if (nColumn == null || nText == null) return false;
			return nColumn < nText;
		} else if (compare == CompareType.GREATER_THEN_COLUMN){
			Double nColumn = getNumber(column);
			Double nText = getNumber(getColumnValue(item, text));
			if (nColumn == null || nText == null) return false;
			return nColumn > nText;
		} else if (compare == CompareType.LESS_THEN_COLUMN){
			Double nColumn = getNumber(column);
			Double nText = getNumber(getColumnValue(item, text));
			if (nColumn == null || nText == null) return false;
			return nColumn < nText;
		} else if (compare == CompareType.EQUALS_COLUMN){
			String compateColumn = getColumnValue(item, text);
			if (compateColumn == null) return false;
			return equals(column, compateColumn.toLowerCase());
		} else if (compare == CompareType.EQUALS_NOT_COLUMN){
			String compateColumn = getColumnValue(item, text);
			if (compateColumn == null) return true;
			return !equals(column, compateColumn.toLowerCase());
		} else if (compare == CompareType.CONTAINS_COLUMN){
			String compateColumn = getColumnValue(item, text);
			if (compateColumn == null) return false;
			return column.contains(compateColumn.toLowerCase());
		} else if (compare == CompareType.CONTAINS_NOT_COLUMN){
			String compateColumn = getColumnValue(item, text);
			if (compateColumn == null) return true;
			return !column.contains(compateColumn.toLowerCase());
		} else { //Fallback: show all...
			return true;
		}
	}
	
	private boolean equals(String s1, String s2){
		Double n1 = getNumber(s1);
		Double n2 = getNumber(s2);
		if (n1 == null || n2 == null){
			return s1.equals(s2);
		} else {
			return n1.equals(n2);
		}
		
	}
	
	private Double getNumber(String filterValue){
		//Used to check if parsing was successful
		ParsePosition position = new ParsePosition(0);
		//Parse number using the Locale
		Number n = NumberFormat.getInstance(LOCALE).parse(filterValue, position);
		if (n != null && position.getIndex() == filterValue.length()){ //Numeric
			return n.doubleValue();
		} else { //String
			return null;
		}
	}

	/**
	 * Overwrite to do stuff before filtering
	 */
	protected void beforeFilter() {}

	/**
	 * Overwrite to do stuff after filtering
	 */
	protected void afterFilter() {}

}

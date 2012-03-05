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


public class Filter {
	
	public enum CompareType{
		//FIXME - i18n
		CONTAINS("Contains"),
		CONTAINS_NOT("Does not contain"),
		EQUALS("Equals"),
		EQUALS_NOT("Does not equal")
		;
		
		String name;
		private CompareType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	enum LogicType{
		//FIXME - i18n
		AND("And"),
		OR("Or")
		;
		
		String name;
		private LogicType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private LogicType logic;
	private Object column;
	private CompareType compare;
	private String text;
	
	public Filter(boolean and, Object column, String compare, String text) {
		this(and, column, CompareType.valueOf(compare), text);
	}

	public Filter(boolean and, Object column, CompareType compare, String text) {
		this.logic = and ? LogicType.AND : LogicType.OR;
		this.column = column;
		this.compare = compare;
		this.text = text;
	}

	public Object getColumn() {
		return column;
	}

	public String getCompare() {
		return compare.name();
	}

	public boolean isAnd() {
		return logic == LogicType.AND;
	}
	
	public boolean isEmpty(){
		return text.isEmpty();
	}

	public String getText() {
		return text;
	}
	
	
}

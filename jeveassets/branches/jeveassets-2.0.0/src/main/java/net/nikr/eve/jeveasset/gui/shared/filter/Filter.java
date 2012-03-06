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

import net.nikr.eve.jeveasset.i18n.GuiShared;


public class Filter {
	
	public enum CompareType{
		CONTAINS() {
			@Override String getI18N(){ return GuiShared.get().filterContains(); }
		},
		CONTAINS_NOT() {
			@Override String getI18N(){ return GuiShared.get().filterContainsNot(); }
		},
		EQUALS() {
			@Override String getI18N(){ return GuiShared.get().filterEquals(); }
		},
		EQUALS_NOT() {
			@Override String getI18N(){ return GuiShared.get().filterEqualsNot(); }
		},
		GREATER_THEN() {
			@Override String getI18N(){ return GuiShared.get().filterGreaterThen(); }
		},
		LESS_THEN() {
			@Override String getI18N(){ return GuiShared.get().filterLessThen(); }
		},
		CONTAINS_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterContainsColumn(); }
		},
		CONTAINS_NOT_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterContainsNotColumn(); }
		},
		EQUALS_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterEqualsColumn(); }
		},
		EQUALS_NOT_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterEqualsNotColumn(); }
		},
		GREATER_THEN_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterGreaterThenColumn(); }
		},
		LESS_THEN_COLUMN() {
			@Override String getI18N(){ return GuiShared.get().filterLessThenColumn(); }
		},
		;
		
		private final static CompareType[] VALUES_STRING = new CompareType[]
			{CONTAINS,
			CONTAINS_NOT,
			EQUALS,
			EQUALS_NOT,
			CONTAINS_COLUMN,
			CONTAINS_NOT_COLUMN,
			EQUALS_COLUMN,
			EQUALS_NOT_COLUMN
		};
		
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
		public static CompareType[] valuesString(){
			return VALUES_STRING;
		}
		
		public static boolean isColumnCompare(CompareType compareType){
			return compareType == CompareType.GREATER_THEN_COLUMN 
				|| compareType == CompareType.LESS_THEN_COLUMN
				|| compareType == CompareType.EQUALS_COLUMN
				|| compareType == CompareType.EQUALS_NOT_COLUMN
				|| compareType == CompareType.CONTAINS_COLUMN
				|| compareType == CompareType.CONTAINS_NOT_COLUMN
				;
		}
		public static boolean isNumericCompare(CompareType compareType){
			return compareType == CompareType.GREATER_THEN_COLUMN 
				|| compareType == CompareType.LESS_THEN_COLUMN
				|| compareType == CompareType.GREATER_THEN
				|| compareType == CompareType.LESS_THEN
				;
		}
	}
	enum LogicType{
		AND() {
			@Override
			public String getI18N(){
				return GuiShared.get().filterAnd();
			}
		},
		OR() {
			@Override
			public String getI18N(){
				return GuiShared.get().filterOr();
			}
		},
		;
		
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}
	
	private LogicType logic;
	private Enum column;
	private CompareType compare;
	private String text;
	
	public Filter(boolean and, Enum column, String compare, String text) {
		this(and, column, CompareType.valueOf(compare), text);
	}

	public Filter(boolean and, Enum column, CompareType compare, String text) {
		this.logic = and ? LogicType.AND : LogicType.OR;
		this.column = column;
		this.compare = compare;
		this.text = text;
	}

	public Enum getColumn() {
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

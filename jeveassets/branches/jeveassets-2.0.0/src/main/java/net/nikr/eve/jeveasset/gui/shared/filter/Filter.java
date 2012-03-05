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
			@Override
			String getI18N(){
				return GuiShared.get().filterContains();
			}
		},
		CONTAINS_NOT() {
			@Override
			public String getI18N(){
				return GuiShared.get().filterContainsNot();
			}
		},
		EQUALS() {
			@Override
			public String getI18N(){
				return GuiShared.get().filterEquals();
			}
		},
		EQUALS_NOT() {
			@Override
			public String getI18N(){
				return GuiShared.get().filterEqualsNot();
			}
		},
		;
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
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

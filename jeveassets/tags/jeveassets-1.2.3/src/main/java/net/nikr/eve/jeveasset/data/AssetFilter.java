/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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


public class AssetFilter {

	public static final String MODE_CONTAIN = "Contains";
	public static final String MODE_CONTAIN_NOT = "Does not contain";
	public static final String MODE_EQUALS = "Equals";
	public static final String MODE_EQUALS_NOT = "Does not equal";
	public static final String MODE_GREATER_THAN = "Greater than";
	public static final String MODE_LESS_THAN = "Less than";
	public static final String MODE_GREATER_THAN_COLUMN = "Greater than column";
	public static final String MODE_LESS_THAN_COLUMN = "Less than column";
	
	public static final String AND = "And";
	public static final String OR = "Or";

	private String column;
	private String text;
	private String mode;
	private boolean and;
	private String columnMatch;

	public AssetFilter(String column, String text, String mode, boolean and, String columnMatch) {
		this.column = column;
		this.text = text;
		this.mode = mode;
		this.and = and;
		this.columnMatch = columnMatch;
	}

	public boolean isAnd() {
		return and;
	}

	public String getColumn() {
		return column;
	}

	public String getColumnMatch() {
		return columnMatch;
	}

	public String getMode() {
		return mode;
	}

	public String getText() {
		return text;
	}

	public boolean isEmpty(){
		return (text.equals("") && columnMatch == null);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + (this.column != null ? this.column.hashCode() : 0);
		hash = 59 * hash + (this.text != null ? this.text.hashCode() : 0);
		hash = 59 * hash + (this.mode != null ? this.mode.hashCode() : 0);
		hash = 59 * hash + (this.and ? 1 : 0);
		hash = 59 * hash + (this.columnMatch != null ? this.columnMatch.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AssetFilter other = (AssetFilter) obj;
		if ((this.column == null) ? (other.column != null) : !this.column.equals(other.column)) {
			return false;
		}
		if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
			return false;
		}
		if ((this.mode == null) ? (other.mode != null) : !this.mode.equals(other.mode)) {
			return false;
		}
		if (this.and != other.and) {
			return false;
		}
		if ((this.columnMatch == null) ? (other.columnMatch != null) : !this.columnMatch.equals(other.columnMatch)) {
			return false;
		}
		return true;
	}


}

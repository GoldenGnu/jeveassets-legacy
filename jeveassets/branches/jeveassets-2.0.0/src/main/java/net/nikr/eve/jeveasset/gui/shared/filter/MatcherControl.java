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
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;


public abstract class MatcherControl<E> {
	
	private CompareType compare;
	private String text;
	protected Map<String, List<Filter>> filters;

	public MatcherControl(Map<String, List<Filter>> filters) {
		this.filters = filters;
	}

	boolean matches(E item, Object column, CompareType compare, String text){
		this.compare = compare;
		this.text = text.toLowerCase();
		return matches(item, column);
	}
	protected abstract boolean matches(E item, Object column);
	protected abstract Object[] getValues();
	
	protected boolean compare(String s){
		if (s == null) return false;
		s = s.toLowerCase();
		if (compare == CompareType.CONTAINS){
			return s.contains(text);
		} else if (compare == CompareType.CONTAINS_NOT){
			return !s.contains(text);
		} else if (compare == CompareType.EQUALS){
			return s.equals(text);
		} else if (compare == CompareType.EQUALS_NOT){
			return !s.equals(text);
		} else { //Fallback: show all...
			return true;
		}
	}

	/**
	 * Overwrite to do stuff before filtering
	 */
	protected void preFilter() {}

	/**
	 * Overwrite to do stuff after filtering
	 */
	protected void postFilter() {}

}

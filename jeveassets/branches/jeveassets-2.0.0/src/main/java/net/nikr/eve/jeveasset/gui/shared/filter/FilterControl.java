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

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterPanel.MyMatcher;


public class FilterControl<E> {

	private FilterGui<E> gui;
	private List<FilterList<E>> filterLists;
	private MatcherControl<E> matcherControl;

	private FilterControl(JFrame jFrame, List<FilterList<E>> filterLists, MatcherControl<E> matcherControl) {
		this.filterLists = filterLists;
		this.matcherControl = matcherControl;
		gui = new FilterGui<E>(jFrame, this, matcherControl);
	}
	
	public static <E> FilterControl<E> install(JFrame jFrame, FilterList<E> filterList, MatcherControl<E> matcherControl){
		return new FilterControl<E>(jFrame, Collections.singletonList(filterList) , matcherControl);
	}
	public static <E> FilterControl<E> install(JFrame jFrame, List<FilterList<E>> filterLists, MatcherControl<E> matcherControl){
		return new FilterControl<E>(jFrame, filterLists , matcherControl);
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
	
	void refilter() {
		matcherControl.beforeFilter();
		for (FilterList<E> filterList : filterLists){
			filterList.setMatcher(new LogicalMatcher<E>(gui.getMatchers()));
		}
		matcherControl.afterFilter();
	}
	
	private static class LogicalMatcher<E> implements Matcher<E> {

		private List<MyMatcher<E>> matchers;

		public LogicalMatcher(List<MyMatcher<E>> matchers) {
			this.matchers = matchers;
		}

		@Override
		public boolean matches(E item) {
			boolean bOR = false;
			boolean bAnyORs = false;
			for (MyMatcher<E> matcher : matchers){
				if (!matcher.isEmpty()){
					if (matcher.isAnd()){ //And
						if (!matcher.matches(item)){ //if just one don't match, none match
							return false;
						}
					} else { //Or
						bAnyORs = true;
						if (matcher.matches(item)){ //if just one is true all is true
							bOR = true;
						}
					}
				}
			}
			//if any "Or" is true | if no "Or" is included | if just one "Or" it's considered as "And"
			return (bOR || !bAnyORs);
		}
	}
	
}

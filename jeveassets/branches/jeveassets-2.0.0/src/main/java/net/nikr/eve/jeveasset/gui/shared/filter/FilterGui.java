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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterPanel.MyMatcher;
import net.nikr.eve.jeveasset.i18n.GuiShared;

class FilterGui<E> implements ActionListener{

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_CLEAR = "ACTION_CLEAR";
	private final static String ACTION_SAVE = "ACTION_SAVE";
	private final static String ACTION_MANAGER = "ACTION_MANAGER";
	private final static String ACTION_SHOW_FILTERS = "ACTION_SHOW_FILTERS";
	
	private JPanel jPanel;
	private GroupLayout layout;
	private JToolBar jToolBar;
	private JDropDownButton jLoadFilter;
	private JCheckBox jShowFilters;
	private JLabel jShowing;
	private JFrame jFrame;
	
	private FilterControl<E> matcherControl;
	
	private List<FilterPanel<E>> filterPanels = new ArrayList<FilterPanel<E>>();
	private FilterSave filterSave;
	private FilterManager<E> filterManager;
	
	
	FilterGui(JFrame jFrame, FilterControl<E> matcherControl) {
		this.jFrame = jFrame;
		this.matcherControl = matcherControl;
		
		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);

		//Add
		JButton jAddField = new JButton(GuiShared.get().addField());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(ACTION_ADD);
		jAddField.addActionListener(this);
		addToolButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(GuiShared.get().clearField());
		jClearFields.setIcon(Images.ASSETS_CLEAR_FIELDS.getIcon());
		jClearFields.setActionCommand(ACTION_CLEAR);
		jClearFields.addActionListener(this);
		addToolButton(jClearFields);
		
		addToolSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(GuiShared.get().saveFilter());
		jSaveFilter.setIcon(Images.ASSETS_SAVE_FILTERS.getIcon());
		jSaveFilter.setActionCommand(ACTION_SAVE);
		jSaveFilter.addActionListener(this);
		addToolButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(GuiShared.get().loadFilter());
		jLoadFilter.setIcon( Images.ASSETS_LOAD_FILTER.getIcon());
		addToolButton(jLoadFilter);
		
		addToolSeparator();
		
		jShowFilters = new JCheckBox(GuiShared.get().showFilters());
		jShowFilters.setActionCommand(ACTION_SHOW_FILTERS);
		jShowFilters.addActionListener(this);
		jShowFilters.setSelected(true);
		addToolButton(jShowFilters, 70);
		
		jShowing = new JLabel();
		
		updateFilters();
		add();
		
		filterSave = new FilterSave(jFrame);
		filterManager = new FilterManager<E>(jFrame, this, matcherControl.getFilters());
	}
	
	JPanel getPanel(){
		return jPanel;
	}
	
	final void addToolButton(AbstractButton jButton){
		addToolButton(jButton, 90);
	}
	final void addToolButton(AbstractButton jButton, int width){
		if (width > 0){
			jButton.setMinimumSize( new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize( new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}
	final void addToolSeparator(){
		jToolBar.addSeparator();
	}
	
	private List<FilterPanel.MyMatcher<E>> getMatchers(){
		return getMatchers(true);
	}
	
	void updateShowing(){
		int showing = 0;
		for (FilterList<E> filterList : matcherControl.getFilterLists()){
			showing = showing + filterList.size();
		}
		String filterName = GuiShared.get().filterUntitled();
		if (getFilters().isEmpty()){
			filterName = GuiShared.get().filterEmpty();
		} else {
			if (matcherControl.getFilters().containsValue(getFilters())){
				for (Map.Entry<String, List<Filter>> entry : matcherControl.getFilters().entrySet()){
					if (entry.getValue().equals(getFilters())){
						filterName = entry.getKey();
						break;
					}
				}
			}
		}
		jShowing.setText(GuiShared.get().filterShowing(showing, showing, filterName));
	}
	
	private List<Filter> getFilters(){
		List<Filter> filters = new ArrayList<Filter>();
		for (FilterPanel<E> filterPanel : filterPanels){
			Filter filter = filterPanel.getFilter();
			if (!filter.isEmpty()) filters.add(filter);
		}
		return filters;
	}
	
	private List<FilterPanel.MyMatcher<E>> getMatchers(boolean includeEmpty){
		List<FilterPanel.MyMatcher<E>> matchers = new ArrayList<FilterPanel.MyMatcher<E>>();
		for (FilterPanel<E> filterPanel : filterPanels){
			MyMatcher<E> matcher = filterPanel.getMatcher();
			if (!matcher.isEmpty() || includeEmpty) matchers.add(matcher);
		}
		return matchers;
	}
	
	private void update(){
		jPanel.removeAll();
		GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
		horizontalGroup.addGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar)
				.addGap(0, 0, Short.MAX_VALUE)
				.addComponent(jShowing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		int toolbatHeight = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		verticalGroup
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBar, toolbatHeight, toolbatHeight, toolbatHeight)
					.addComponent(jShowing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);		
		if (jShowFilters.isSelected()){
			for (FilterPanel filterPanel : filterPanels){
				verticalGroup.addComponent(filterPanel.getPanel());
				horizontalGroup.addComponent(filterPanel.getPanel());
			}
		}

		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
	}
	
	void remove(FilterPanel<E> filterPanel){
		filterPanels.remove(filterPanel);
		if (filterPanels.size() == 1){
			filterPanels.get(0).setEnabled(false);
		}
		update();
		refilter();
	}
	
	private void add(){
		add(new FilterPanel<E>(this, matcherControl));
	}
	
	private void add(FilterPanel<E> filterPanel){
		filterPanels.add(filterPanel);
		if (filterPanels.size() > 1){ //Show remove buttons
			filterPanels.get(0).setEnabled(true);
		}
		if (filterPanels.size() == 1){ //Hide remove button
			filterPanels.get(0).setEnabled(false);
		}
		update();
	}
	
	private void clear(){
		while(filterPanels.size() > 0){
			remove( filterPanels.get(0) );
		}
		add();
		refilter();
	}
	
	private void loadFilter(String filterName, boolean add){
		if (filterName == null) return;
		if (matcherControl.getFilters().containsKey(filterName)){
			List<Filter> filters = matcherControl.getFilters().get( filterName );
			if (add){
				addFilters(filters);
			} else{
				setFilters(filters);
			}
		}
	}
	
	void setFilters(List<Filter> filters){
		while(filterPanels.size() > 0){
			remove( filterPanels.get(0) );
		}
		addFilters(filters);
	}
	
	void addFilter(Filter filter){
		addFilters(Collections.singletonList(filter));
	}
	
	private void addFilters(List<Filter> filters){
		//Remove single empty filter...
		if (filterPanels.size() == 1 && filterPanels.get(0).getFilter().isEmpty()){
			remove( filterPanels.get(0) );
		}
		for (Filter filter : filters){
			FilterPanel<E> filterPanel = new FilterPanel<E>(this, matcherControl);
			filterPanel.setFilter( filter );
			add(filterPanel);
		}
		update();
		refilter();
	}
	
	
	final void updateFilters(){
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().manageFilters(), Images.DIALOG_SETTINGS.getIcon());
		jMenuItem.setActionCommand(ACTION_MANAGER);
		jMenuItem.addActionListener(this);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);
		
		List<String> list = new ArrayList<String>( matcherControl.getFilters().keySet() );
		Collections.sort(list);
		
		if (list.size() > 0) jLoadFilter.addSeparator();
		
		for (String s : list){
			jMenuItem = new JMenuItem(s, Images.ASSETS_LOAD_FILTER.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(this);
			jLoadFilter.add(jMenuItem);
		}
		updateShowing();
	}
	
	void refilter() {
		matcherControl.beforeFilter();
		int showing = 0;
		for (FilterList<E> filterList : matcherControl.getFilterLists()){
			filterList.setMatcher(new LogicalMatcher<E>(getMatchers()));
			showing = showing + filterList.size();
		}
		matcherControl.afterFilter();
		updateShowing();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())){
			add();
			return;
		}
		if (ACTION_CLEAR.equals(e.getActionCommand())){
			clear();
			return;
		}
		if (ACTION_MANAGER.equals(e.getActionCommand())){
			filterManager.setVisible(true);
			return;
		}
		if (ACTION_SHOW_FILTERS.equals(e.getActionCommand())){
			update();
			return;
		}
		if (ACTION_SAVE.equals(e.getActionCommand())){
			if (getMatchers(false).isEmpty()){
				JOptionPane.showMessageDialog(jFrame, GuiShared.get().nothingToSave(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
			} else {
				String name = filterSave.show(new ArrayList<String>( matcherControl.getFilters().keySet() ));
				if (name != null){
					matcherControl.getFilters().put(name, getFilters());
					updateFilters();
				}
			}
			return;
		}
		loadFilter(e.getActionCommand(), (e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
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

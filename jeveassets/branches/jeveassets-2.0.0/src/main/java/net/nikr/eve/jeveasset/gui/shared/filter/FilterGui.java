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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterPanel.MyMatcher;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


class FilterGui<E> implements ActionListener{

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_CLEAR = "ACTION_CLEAR";
	private final static String ACTION_SAVE = "ACTION_SAVE";
	private final static String ACTION_MANAGER = "ACTION_MANAGER";
	
	private JPanel jPanel;
	private GroupLayout layout;
	private JToolBar jToolBar;
	private JDropDownButton jLoadFilter;
	
	private JFrame jFrame;
	private MatcherControl<E> matcherControl;
	private FilterControl<E> filterControl;
	
	private List<FilterPanel<E>> filterPanels = new ArrayList<FilterPanel<E>>();
	private SaveFilter saveFilter;
	
	
	FilterGui(JFrame jFrame, FilterControl<E> filterControl, MatcherControl<E> matcherControl) {
		this.jFrame = jFrame;
		this.filterControl = filterControl;
		this.matcherControl = matcherControl;
		
		saveFilter = new SaveFilter(jFrame);
		
		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);
		
		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);

		//Add
		JButton jAddField = new JButton(TabsAssets.get().addField());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(ACTION_ADD);
		jAddField.addActionListener(this);
		addToolButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(TabsAssets.get().clear());
		jClearFields.setIcon(Images.ASSETS_CLEAR_FIELDS.getIcon());
		jClearFields.setActionCommand(ACTION_CLEAR);
		jClearFields.addActionListener(this);
		addToolButton(jClearFields);
		
		addToolSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(TabsAssets.get().save());
		jSaveFilter.setIcon(Images.ASSETS_SAVE_FILTERS.getIcon());
		jSaveFilter.setActionCommand(ACTION_SAVE);
		jSaveFilter.addActionListener(this);
		addToolButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(TabsAssets.get().load1());
		jLoadFilter.setIcon( Images.ASSETS_LOAD_FILTER.getIcon());
		addToolButton(jLoadFilter);
		updateFilters();
		add();
	}
	
	JPanel getPanel(){
		return jPanel;
	}
	
	public final void addToolButton(JButton jButton){
		addToolButton(jButton, 90);
	}
	public final void addToolButton(JButton jButton, int width){
		jButton.setMinimumSize( new Dimension(width, Program.BUTTONS_HEIGHT));
		jButton.setMaximumSize( new Dimension(width, Program.BUTTONS_HEIGHT));
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}
	public final void addToolSeparator(){
		jToolBar.addSeparator();
	}
	
	List<FilterPanel.MyMatcher<E>> getMatchers(){
		return getMatchers(true);
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
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		horizontalGroup.addComponent(jToolBar);
		int toolbatHeight = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		verticalGroup.addComponent(jToolBar, toolbatHeight, toolbatHeight, toolbatHeight);
		for (FilterPanel filterPanel : filterPanels){
			verticalGroup.addComponent(filterPanel.getPanel());
			horizontalGroup.addComponent(filterPanel.getPanel());
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
		filterControl.refilter();
	}
	
	private void add(){
		add(new FilterPanel<E>(this, filterControl, matcherControl));
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
		filterControl.refilter();
	}
	
	private void loadFilter(String filterName, boolean add){
		if (filterName == null) return;
		if (matcherControl.filters.containsKey(filterName)){
			List<Filter> filters = matcherControl.filters.get( filterName );
			if (add){
				addFilters(filters);
			} else{
				setFilters(filters);
			}
		}
	}
	
	private void setFilters(List<Filter> filters){
		while(filterPanels.size() > 0){
			remove( filterPanels.get(0) );
		}
		addFilters(filters);
	}
	
	private void addFilters(List<Filter> filters){
		//Remove single empty filter...
		if (filterPanels.size() == 1 && filterPanels.get(0).getFilter().isEmpty()){
			remove( filterPanels.get(0) );
		}
		for (Filter filter : filters){
			FilterPanel<E> filterPanel = new FilterPanel<E>(this, filterControl, matcherControl);
			filterPanel.setFilter( filter );
			add(filterPanel);
		}
		update();
		filterControl.refilter();
	}
	
	
	private void updateFilters(){
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(TabsAssets.get().manage(), Images.DIALOG_SETTINGS.getIcon());
		jMenuItem.setActionCommand(ACTION_MANAGER);
		jMenuItem.addActionListener(this);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);
		
		List<String> list = new ArrayList<String>( matcherControl.filters.keySet() );
		Collections.sort(list);
		
		if (list.size() > 0) jLoadFilter.addSeparator();
		
		for (String s : list){
			jMenuItem = new JMenuItem(s, Images.ASSETS_LOAD_FILTER.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(this);
			jLoadFilter.add(jMenuItem);
		}
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
			return;
		}
		if (ACTION_SAVE.equals(e.getActionCommand())){
			if (getMatchers(false).isEmpty()){
				JOptionPane.showMessageDialog(jFrame, TabsAssets.get().nothing(), TabsAssets.get().save(), JOptionPane.PLAIN_MESSAGE);
			} else {
				String name = saveFilter.show(new ArrayList<String>( matcherControl.filters.keySet() ));
				if (name != null){
					matcherControl.filters.put(name, getFilters());
					updateFilters();
				}
			}
			return;
		}
		loadFilter(e.getActionCommand(), (e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
	}
	
}

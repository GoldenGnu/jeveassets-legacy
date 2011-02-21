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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import net.nikr.eve.jeveasset.gui.shared.JGroupLayoutPanel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.gui.dialogs.addsystem.AddSystemController;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;


public class ToolPanel extends JGroupLayoutPanel {
	
	private final static String ACTION_ADD_FIELD = "ACTION_ADD_FILTER";
	private final static String ACTION_CLEAR_FIELDS = "ACTION_RESET_FILTERS";
	private final static String ACTION_SAVE_FILTER = "ACTION_SAVE_FILTER";
	private final static String ACTION_OPEN_FILTER_MANAGER = "ACTION_OPEN_FILTER_MANAGER";
	private final static String ACTION_ADD_SYSTEM = "ADD_SYSTEM";
	private final static String ACTION_SHOW_FILTERS = "ACTION_SHOW_FILTERS";

	//Data
	private List<FilterPanel> filters;
	private MatcherEditorManager matcherEditorManager;
	int rowCount;

	//GUI
	private JDropDownButton jLoadFilter;
	private JLabel jRows;
	private JToolBar jToolBar;
	private JToggleButton jHideFilters;

	private Listener listener;

	public ToolPanel(Program program, MatcherEditorManager matcherEditorManager) {
		super(program);
		this. matcherEditorManager = matcherEditorManager;
		filters = new ArrayList<FilterPanel>();
		
		listener = new Listener();

		//Layout setup
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);

		//Add
		JButton jAddField = new JButton("Add Field");
		jAddField.setIcon(Images.ICON_ADD);
		jAddField.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jAddField.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jAddField.setHorizontalAlignment(JButton.LEFT);
		jAddField.setActionCommand(ACTION_ADD_FIELD);
		jAddField.addActionListener(listener);
		jToolBar.add(jAddField);

		//Reset
		JButton jClearFields = new JButton("Clear Fields");
		jClearFields.setIcon(Images.ICON_CLEAR);
		jClearFields.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jClearFields.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jClearFields.setHorizontalAlignment(JButton.LEFT);
		jClearFields.setActionCommand(ACTION_CLEAR_FIELDS);
		jClearFields.addActionListener(listener);
		jToolBar.add(jClearFields);

		jHideFilters = new JToggleButton("Hide Fields");
		jHideFilters.setIcon(Images.ICON_SHOW_FIELDS);
		jHideFilters.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jHideFilters.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		//jHideFilters.setHorizontalAlignment(JButton.LEFT);
		jHideFilters.setActionCommand(ACTION_SHOW_FILTERS);
		jHideFilters.addActionListener(listener);
		//jHideFilters.setSelected(true);
		jToolBar.add(jHideFilters);

		jToolBar.addSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton("Save Filter");
		jSaveFilter.setIcon(Images.ICON_SAVE);
		jSaveFilter.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jSaveFilter.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jSaveFilter.setHorizontalAlignment(JButton.LEFT);
		jSaveFilter.setActionCommand(ACTION_SAVE_FILTER);
		jSaveFilter.addActionListener(listener);
		jToolBar.add(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton("Load Filter");
		jLoadFilter.setIcon( Images.ICON_FOLDER);
		jLoadFilter.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jLoadFilter.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jLoadFilter.setHorizontalAlignment(JButton.LEFT);
		jToolBar.add(jLoadFilter);

		//TODO Completly remove "Add System"
		//Add System
		JButton jAddSystem = new JButton("Add System");
		jAddSystem.setMinimumSize( new Dimension(10, Program.BUTTONS_HEIGHT));
		jAddSystem.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jAddSystem.setHorizontalAlignment(JButton.LEFT);
		jAddSystem.setActionCommand(ACTION_ADD_SYSTEM);
		jAddSystem.addActionListener(listener);
		
		//jToolBar.add(jAddSystem);

		jRows = new JLabel();
		this.getPanel().add(jRows);

		//Add one filterPanel
		addFilter();
		savedFiltersChanged();
	}

	public Listener getListener() {
		return listener;
	}

	public List<AssetFilter> getAssetFilters(){
		List<AssetFilter> assetFilters = new ArrayList<AssetFilter>();
		for (int a = 0; a < filters.size(); a++){
			AssetFilter assetFilter = filters.get(a).getAssetFilter();
			if (!assetFilter.isEmpty()){
				assetFilters.add(assetFilter);
			}
		}

		return assetFilters;
	}
	public void setAssetFilters(List<AssetFilter> assetFilters){
		while(filters.size() > 0){
			removeFilter( filters.get(0) );
		}
		for (int a = 0; a < assetFilters.size(); a++){
			FilterPanel filterPanel = new FilterPanel(program, this);
			filterPanel.setAssetFilter( assetFilters.get(a) );
			addFilter(filterPanel);
		}
		this.updateLayout();
	}

	public void addFilter(AssetFilter assetFilter){
		addFilter(assetFilter, false);
	}

	public void addFilter(AssetFilter assetFilter, boolean unique){
		FilterPanel filterPanel = new FilterPanel(program, this);
		filterPanel.setAssetFilter(assetFilter);
		//Remove single empty filter...
		if (filters.size() == 1 && filters.get(0).getAssetFilter().isEmpty()){
			removeFilter( filters.get(0) );
		}
		if (!getAssetFilters().contains(assetFilter) || !unique){
			addFilter(filterPanel);
		}
	}

	public void clearFilters(){
		while(filters.size() > 0){
			removeFilter( filters.get(0) );
		}
		this.addFilter();
		this.updateLayout();
	}

	private void addFilter(){
		addFilter(new FilterPanel(program, this));
	}

	private void addFilter(FilterPanel filterPanel){
		this.getPanel().add(filterPanel.getPanel()); //Must be done before filtering
		filters.add(filterPanel); //Must be done before filtering
		matcherEditorManager.add(filterPanel.getEveAssetMatcherEditor()); //Tricker refiltering
		if (filters.size() > 1){ //Show remove buttons
			filters.get(0).showButton();
		}
		if (filters.size() == 1){ //Hide remove button
			filters.get(0).hideButton();
		}
		this.updateLayout();
	}

	private void removeFilter(FilterPanel filterPanel){
		this.getPanel().remove(filterPanel.getPanel());
		filters.remove(filterPanel);
		matcherEditorManager.remove(filterPanel.getEveAssetMatcherEditor());
		if (filters.size() == 1){
			filters.get(0).hideButton();
		}
		this.updateLayout();
	}
	
	private void updateLayout(){
		//Horizontal
		ParallelGroup pg;
		SequentialGroup sg;
		sg = layout.createSequentialGroup();
		sg.addComponent(jToolBar);
		sg.addGap(0, 0, Short.MAX_VALUE);
		sg.addComponent(jRows, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

		pg = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		pg.addGroup(sg);
		if (!jHideFilters.isSelected()){
			for (int a = 0; a < filters.size(); a++){
				pg.addComponent(filters.get(a).getPanel());
			}
		}
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(pg)
		);
		
		
		//Vertical
		pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		int toolbatHeight = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		pg.addComponent(jToolBar, toolbatHeight, toolbatHeight, toolbatHeight);
		pg.addComponent(jRows, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		sg = layout.createSequentialGroup();
		sg.addGroup(pg);
		if (!jHideFilters.isSelected()){
			for (int a = 0; a < filters.size(); a++){
				sg.addComponent(filters.get(a).getPanel());
			}
		}
		layout.setVerticalGroup(sg);
	}

	private void loadFilter(String filterName){
		if (filterName == null) return;
		if (program.getSettings().getAssetFilters().containsKey(filterName)){
			List<AssetFilter> assetFilters = program.getSettings().getAssetFilters().get( filterName );
			setAssetFilters(assetFilters);
		}
	}

	private void saveFilter(){
		//Tell there is nothing to save
		if (getAssetFilters().isEmpty()){
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Nothing to save...", "Save Filter", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//Ask for filter name
		SaveFilterDialog saveFilterDialog = program.getSaveFilterDialog();
		saveFilterDialog.setVisible(true);
	}

	final public void savedFiltersChanged(){
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;
		
		List<String> list = new ArrayList<String>( program.getSettings().getAssetFilters().keySet() );
		Collections.sort(list);
		for (int a = 0; a < list.size(); a++){
			String s = list.get(a);
			jMenuItem = new JMenuItem(s);
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setIcon( Images.ICON_FOLDER);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(listener);
			jLoadFilter.add(jMenuItem);
		}
		
		if (list.size() > 0) jLoadFilter.addSeparator();

		jMenuItem = new JMenuItem("Manage Saved Filters");
		jMenuItem.setActionCommand(ACTION_OPEN_FILTER_MANAGER);
		jMenuItem.addActionListener(listener);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);
		jLoadFilter.add(jMenuItem);
	}

	public void setToolbarText(String text){
		jRows.setText(text);
	}

	public class Listener  implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (FilterPanel.ACTION_REMOVE_FILTER.equals(e.getActionCommand())) {
				JButton button = (JButton)e.getSource();
				for (int a = 0; a < filters.size(); a++){
					if (button.getParent().equals(filters.get(a).getPanel())){
						removeFilter(filters.get(a));
						return;
					}
				}
				return;
			}
			if (ACTION_SHOW_FILTERS.equals(e.getActionCommand())) {
				updateLayout();
			}
			if (ACTION_ADD_FIELD.equals(e.getActionCommand())) {
				addFilter();
				return;
			}
			if (ACTION_CLEAR_FIELDS.equals(e.getActionCommand())) {
				clearFilters();
				return;
			}
			if (ACTION_SAVE_FILTER.equals(e.getActionCommand())) {
				saveFilter();
				return;
			}
			if (ACTION_OPEN_FILTER_MANAGER.equals(e.getActionCommand())) {
				program.getFiltersManagerDialog().setVisible(true);
				return;
			}
			if (ACTION_ADD_SYSTEM.equals(e.getActionCommand())) {
				new AddSystemController(program);
			}
			loadFilter(e.getActionCommand());
		}
	}
}

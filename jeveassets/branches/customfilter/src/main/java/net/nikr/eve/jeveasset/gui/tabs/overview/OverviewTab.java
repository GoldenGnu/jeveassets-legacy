/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog;
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog.CustomDialogInterface;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;


public class OverviewTab extends JMainTab implements ActionListener, CustomDialogInterface {

	private final static String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
	private final static String ACTION_ADD_NEW_GROUP = "ACTION_ADD_NEW_GROUP";
	private final static String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";
	private final static String ACTION_RENAME_GROUP = "ACTION_RENAME_GROUP";
	private final static String ACTION_ADD_GROUP_FILTER = "ACTION_ADD_GROUP_FILTER";
	private final static String ALL = "All";
	private final static String CUSTOM = "<Custom>";
	private final static String ASSET_FILTER = "Filtered Assets";

	private EventList<Overview> overviewEventList;
	private EventTableModel<Overview> overviewTableModel;
	private OverviewTableFormat overviewTableFormat;
	private JOverviewTable jTable;
	private JComboBox jViews;
	private JComboBox jCharacters;
	private JComboBox jSource;
	private OverviewGroupDialog overviewGroupDialog;
	private CustomDialog customDialog;

	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();
	

	public OverviewTab(Program program) {
		super(program, "Overview", Images.ICON_TOOL_OVERVIEW, true);

		overviewGroupDialog = new OverviewGroupDialog(program, this);
		customDialog = new CustomDialog(program);

		JLabel jViewsLabel = new JLabel("View");
		jViews = new JComboBox( new String[]  {"Stations", "Systems", "Regions", "Groups"} );
		jViews.setActionCommand(ACTION_UPDATE_LIST);
		jViews.addActionListener(this);

		JLabel jCharactersLabel = new JLabel("Character");
		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_UPDATE_LIST);
		jCharacters.addActionListener(this);

		JLabel jSourceLabel = new JLabel("Source");
		jSource = new JComboBox( new String[]  {"All Assets", ASSET_FILTER} );
		jSource.setActionCommand(ACTION_UPDATE_LIST);
		jSource.addActionListener(this);

		//Table format
		overviewTableFormat = new OverviewTableFormat();
		//Backend
		overviewEventList = new BasicEventList<Overview>();
		//For soring the table
		SortedList<Overview> overviewSortedList = new SortedList<Overview>(overviewEventList);
		//Table Model
		overviewTableModel = new EventTableModel<Overview>(overviewSortedList, overviewTableFormat);
		//Tables
		jTable = new JOverviewTable(overviewTableModel, overviewTableFormat.getColumnNames());
		//Table Selection
		EventSelectionModel<Overview> selectionModel = new EventSelectionModel<Overview>(overviewSortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Sorters
		TableComparatorChooser.install(jTable, overviewSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, overviewTableFormat);
		//Scroll Panels
		JScrollPane jOverviewScrollPanel = jTable.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jViewsLabel)
					.addComponent(jViews, 100, 100, 100)
					.addComponent(jSourceLabel)
					.addComponent(jSource, 100, 100, 100)
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 100, 100, 200)
				)
				.addComponent(jOverviewScrollPanel, 400, 400, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jViewsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jViews, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jOverviewScrollPanel, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		//Select clicked row
		jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
		jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenu jSubMenu;
		JMenuItem jSubMenuItem;
		
		int[] selectedRows = jTable.getSelectedRows();

		boolean isSingleRow = selectedRows.length == 1;
		boolean isSelected = (selectedRows.length > 0 && jTable.getSelectedColumns().length > 0);

		Overview overview = null;
		if (isSingleRow){
			overview = overviewTableModel.getElementAt(selectedRows[0]);
		}
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}

	//GROUPS
		jSubMenu = new JMenu("Groups");
		jSubMenu.setIcon(Images.ICON_GROUPS);
		jComponent.add(jSubMenu);
		if (!jViews.getSelectedItem().equals("Groups")){
			jMenuItem = new JMenuItem("Add to new group...");
			jMenuItem.setIcon(Images.ICON_ADD);
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_ADD_NEW_GROUP);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			if (!program.getSettings().getOverviewGroups().isEmpty()) jSubMenu.addSeparator();

			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				boolean found = false;
				boolean station = false;
				boolean system = false;
				boolean region = false;
				for (int b = 0; b < overviewGroup.getLocations().size(); b++){
					OverviewLocation location = overviewGroup.getLocations().get(b);
					if (overview != null && location.equalsLocation(overview)){
						found = true;
						if (location.isStation()) station = true;
						if (location.isSystem()) system = true;
						if (location.isRegion()) region = true;
					}
				}
				jCheckBoxMenuItem = new JCheckBoxMenuItem(overviewGroup.getName());
				if (station) jCheckBoxMenuItem.setIcon(Images.ICON_STATION);
				if (system) jCheckBoxMenuItem.setIcon(Images.ICON_SYSTEM);
				if (region) jCheckBoxMenuItem.setIcon(Images.ICON_REGION);
				jCheckBoxMenuItem.setEnabled(isSingleRow);
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				jSubMenu.add(jCheckBoxMenuItem);
			}
		}
	//GROUPS (Locations)
		if (jViews.getSelectedItem().equals("Groups")){
			jMenuItem = new JMenuItem("Rename Group");
			jMenuItem.setIcon(Images.ICON_RENAME);
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_RENAME_GROUP);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem("Delete Group");
			jMenuItem.setIcon(Images.ICON_DELETE);
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_DELETE_GROUP);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			if (isSingleRow){ //Add the group locations
				OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
				if (overviewGroup != null){
					if (!overviewGroup.getLocations().isEmpty()) jSubMenu.addSeparator();
					for (int a = 0; a < overviewGroup.getLocations().size(); a++){
						OverviewLocation location = overviewGroup.getLocations().get(a);
						jCheckBoxMenuItem = new JCheckBoxMenuItem(location.getName());
						if (location.isStation()) jCheckBoxMenuItem.setIcon(Images.ICON_STATION);
						if (location.isSystem()) jCheckBoxMenuItem.setIcon(Images.ICON_SYSTEM);
						if (location.isRegion()) jCheckBoxMenuItem.setIcon(Images.ICON_REGION);
						jCheckBoxMenuItem.setActionCommand(location.getName());
						jCheckBoxMenuItem.addActionListener(removeFromGroup);
						jCheckBoxMenuItem.setSelected(true);
						jSubMenu.add(jCheckBoxMenuItem);
					}
				}
			}
		}

		addSeparator(jComponent);

	//FILTERS
		jSubMenuItem = new JMenuAssetFilter(program, overview);
		if (jViews.getSelectedItem().equals("Groups")){
			jMenuItem = new JMenuItem("Locations");
			jMenuItem.setIcon(Images.ICON_LOCATIONS);
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_ADD_GROUP_FILTER);
			jMenuItem.addActionListener(this);
			jSubMenuItem.add(jMenuItem);
		}
		jComponent.add(jSubMenuItem);
		
	//LOOKUP
		//XXX - you can not lookup group locations
		jComponent.add(new JMenuLookup(program, overview));
	}

	private List<Overview> getList(List<EveAsset> input, List<String> characters, String view){
		List<Overview> locations = new ArrayList<Overview>();
		Map<String, Overview> locationsMap = new HashMap<String, Overview>();
		List<String> groupedLocations = new ArrayList<String>();
		if (view.equals("Groups")){ //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())){ //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), "", "", 0, 0, 0, 0);
					locationsMap.put(overviewGroup.getName(), overview);
					locations.add(overview);
				}
			}
		} else { //Add all grouped locations
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				for (OverviewLocation overviewLocation : overviewGroup.getLocations()){
					if (!groupedLocations.contains(overviewLocation.getName())){
						groupedLocations.add(overviewLocation.getName());
					}
				}
			}
		}
		for (EveAsset eveAsset : input){
			String name;
			if (eveAsset.isCorporationAsset()){
				name = "["+eveAsset.getOwner()+"]";
			} else {
				name = eveAsset.getOwner();
			}
			if (!characters.contains(name) && !characters.contains(ALL)) continue;
			if (eveAsset.getGroup().equals("Audit Log Secure Container") && program.getSettings().isIgnoreSecureContainers()) continue;
			if (eveAsset.getGroup().equals("Station Services")) continue;

			double reprocessedValue = eveAsset.getValueReprocessed();
			double value = eveAsset.getPrice() * eveAsset.getCount();
			long count = eveAsset.getCount();
			double volume = eveAsset.getVolume() * eveAsset.getCount();
			if (!view.equals("Groups")){ //Locations
				String location = "";
				if (view.equals("Regions")) location = eveAsset.getRegion();
				if (view.equals("Systems")) location = eveAsset.getSystem();
				if (view.equals("Stations")) location = eveAsset.getLocation();
				if (locationsMap.containsKey(location)){ //Update existing overview
					Overview overview = locationsMap.get(location);
					overview.addCount(count);
					overview.addValue(value);
					overview.addVolume(volume);
					overview.addReprocessedValue(reprocessedValue);
				} else { //Create new overview
					Overview overview = new Overview(location, eveAsset.getSystem(), eveAsset.getRegion(), reprocessedValue, volume, count, value);
					locationsMap.put(location, overview);
					locations.add(overview);
				}
			} else { //Groups
				for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
					OverviewGroup overviewGroup = entry.getValue();
					for (int c = 0; c < overviewGroup.getLocations().size(); c++){
						OverviewLocation overviewLocation = overviewGroup.getLocations().get(c);
						if (overviewLocation.equalsLocation(eveAsset)){ //Update existing overview (group)
							Overview overview = locationsMap.get(overviewGroup.getName());
							overview.addCount(count);
							overview.addValue(value);
							overview.addVolume(volume);
							overview.addReprocessedValue(reprocessedValue);
							break; //Only add once....
						}
					}
				}
			}
		}
		jTable.setGroupedLocations(groupedLocations);
		return locations;
	}

	public void updateTable(){
		updateTable(Collections.singletonList((String) jCharacters.getSelectedItem()));
	}

	public void updateTable(List<String> characters){
		//Only need to update when added to the main window
		if (!program.getMainWindow().getTabs().contains(this)) return;
		overviewEventList.getReadWriteLock().writeLock().lock();
		overviewEventList.clear();
		overviewEventList.getReadWriteLock().writeLock().unlock();
		String view = (String) jViews.getSelectedItem();
		String source = (String) jSource.getSelectedItem();
		
		if (view.equals("Regions")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getRegionColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (view.equals("Systems")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getSystemColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (view.equals("Stations")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getStationColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (view.equals("Groups")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getRegionColumns());
			overviewTableModel.fireTableStructureChanged();
		}
		overviewEventList.getReadWriteLock().writeLock().lock();
		if (source.equals(ASSET_FILTER)){
			overviewEventList.addAll(getList(program.getAssetsTab().getFilteredAssets(), characters, view));
		} else {
			overviewEventList.addAll(getList(program.getEveAssetEventList(), characters, view));
		}
		overviewEventList.getReadWriteLock().writeLock().unlock();
	}

	public void resetViews(){
		jViews.setSelectedIndex(0);
		jCharacters.setSelectedIndex(0);
		jSource.setSelectedIndex(0);
		
	}

	@Override
	public void updateData() {
		List<String> characters = new ArrayList<String>();
		List<String> chars = new ArrayList<String>();
		List<String> corps = new ArrayList<String>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				chars.add(human.getName());
				String corp = "["+human.getCorporation()+"]";
				if (!corps.contains(corp)) corps.add(corp);
			}
		}
		Collections.sort(chars);
		Collections.sort(corps);
		characters.add(ALL);
		chars.addAll(corps);
		customDialog.updateList(chars);
		characters.addAll(chars);
		characters.add(CUSTOM);
		jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
		updateTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_UPDATE_LIST.equals(e.getActionCommand())){
			String character = (String) jCharacters.getSelectedItem();
			if (character.equals(CUSTOM)){
				customDialog.show(this);
			} else {
				updateTable();
			}
			return;
		}
		//Group
		if (ACTION_ADD_NEW_GROUP.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			String station = null;
			String system = null;
			if (overview.isStation()){
				station = overview.getName();
				system = overview.getSolarSystem();
			}
			if (overview.isSystem()){
				system = overview.getSolarSystem();
			}
			String region = overview.getRegion();
			
			overviewGroupDialog.groupNew(station, system, region);
			return;
		}
		if (ACTION_DELETE_GROUP.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete Group: "+overviewGroup.getName()+"?", "Delete Group", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (value == JOptionPane.YES_OPTION){
				program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
				updateTable();
			}
			return;
		}
		if (ACTION_RENAME_GROUP.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			overviewGroupDialog.groupRename(overviewGroup);
			return;
		}
		//Filter
		if (ACTION_ADD_GROUP_FILTER.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			List<AssetFilter> assetFilters = new ArrayList<AssetFilter>();
			for (OverviewLocation location : overviewGroup.getLocations()){
				if (location.isStation()){
					AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.Mode.MODE_EQUALS, AssetFilter.Junction.OR, null);
					assetFilters.add(assetFilter);
					program.getAssetsTab().addFilter(assetFilter, true);
				}
				if (location.isSystem()){
					AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.Mode.MODE_CONTAIN, AssetFilter.Junction.OR, null);
					assetFilters.add(assetFilter);
					program.getAssetsTab().addFilter(assetFilter, true);
				}
				if (location.isRegion()){
					AssetFilter assetFilter = new AssetFilter("Region", location.getName(), AssetFilter.Mode.MODE_EQUALS, AssetFilter.Junction.OR, null);
					assetFilters.add(assetFilter);
					program.getAssetsTab().addFilter(assetFilter, true);
				}
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
	}

	@Override
	public void customDialogReady(List<String> list) {
		updateTable(list);
	}

	class AddToGroup implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(e.getActionCommand());
			if (overviewGroup != null){
				int index = jTable.getSelectedRow();
				Overview overview = overviewTableModel.getElementAt(index);
				String station = null;
				String system = null;
				if (overview.isStation()){
					station = overview.getName();
					system = overview.getSolarSystem();
				}
				if (overview.isSystem()){
					system = overview.getSolarSystem();
				}
				String region = overview.getRegion();
				overviewGroupDialog.groupAdd(station, system, region, overviewGroup);
			}
		}
	}

	class RemoveFromGroup implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = jTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			String location = e.getActionCommand();
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Remove Location:\n\""+location+"\"", "Remove Location", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (value == JOptionPane.YES_OPTION){
				overviewGroup.remove(new OverviewLocation(location));
				updateData();
			}
		}
	}
}

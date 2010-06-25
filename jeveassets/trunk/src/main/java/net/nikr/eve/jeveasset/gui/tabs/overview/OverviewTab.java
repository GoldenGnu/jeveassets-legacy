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
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;


public class OverviewTab extends JMainTab implements ActionListener, MouseListener, ClipboardOwner {

	public final static String ACTION_VIEW_SELECTED = "ACTION_VIEW_SELECTED";
	public final static String ACTION_ADD_NEW_GROUP = "ACTION_ADD_NEW_GROUP";
	public final static String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";
	public final static String ACTION_RENAME_GROUP = "ACTION_RENAME_GROUP";
	public final static String ACTION_EDIT_GROUP = "ACTION_EDIT_GROUP";
	public final static String ACTION_COPY = "ACTION_COPY";
	public final static String ACTION_ADD_STATION_FILTER = "ACTION_ADD_STATION_FILTER";
	public final static String ACTION_ADD_SYSTEM_FILTER = "ACTION_ADD_SYSTEM_FILTER";
	public final static String ACTION_ADD_REGION_FILTER = "ACTION_ADD_REGION_FILTER";
	public final static String ACTION_ADD_GROUP_FILTER = "ACTION_ADD_GROUP_FILTER";

	private EventList<Overview> overviewEventList;
	private EventTableModel<Overview> overviewTableModel;
	private OverviewTableFormat overviewTableFormat;
	private JAutoColumnTable jOverviewTable;
	private JComboBox jViews;
	private List<Overview> regions;
	private List<Overview> systems;
	private List<Overview> stations;
	private List<Overview> groups;
	private OverviewGroupDialog overviewGroupDialog;

	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();

	public OverviewTab(Program program) {
		super(program, "Overview", Images.ICON_TOOL_OVERVIEW, true);

		overviewGroupDialog = new OverviewGroupDialog(program, this);
		jViews = new JComboBox( new String[]  {"Stations", "Systems", "Regions", "Groups"} );
		jViews.setActionCommand(ACTION_VIEW_SELECTED);
		jViews.addActionListener(this);

		//Table format
		overviewTableFormat = new OverviewTableFormat();
		//Backend
		overviewEventList = new BasicEventList<Overview>();
		//For soring the table
		SortedList<Overview> overviewSortedList = new SortedList<Overview>(overviewEventList);
		//Table Model
		overviewTableModel = new EventTableModel<Overview>(overviewSortedList, overviewTableFormat);
		//Tables
		jOverviewTable = new JAutoColumnTable(overviewTableModel, overviewTableFormat.getColumnNames());
		jOverviewTable.addMouseListener(this);
		//Sorters
		TableComparatorChooser.install(jOverviewTable, overviewSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, overviewTableFormat);
		//Scroll Panels
		JScrollPane jOverviewScrollPanel = jOverviewTable.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jViews, 200, 200, 200)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOverviewScrollPanel, 400, 400, Short.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jViews, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOverviewScrollPanel, 100, 400, Short.MAX_VALUE)
				)
		);
	}

	private void showTablePopup(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();
		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenu jSubMenu;

		//Select clicked row
		jOverviewTable.setRowSelectionInterval(jOverviewTable.rowAtPoint(e.getPoint()), jOverviewTable.rowAtPoint(e.getPoint()));
		jOverviewTable.setColumnSelectionInterval(0, jOverviewTable.getColumnCount()-1);
		int index = jOverviewTable.getSelectedRow();
		Overview overview = overviewTableModel.getElementAt(index);

		jMenuItem = new JMenuItem("Copy");
		jMenuItem.setIcon(Images.ICON_COPY);
		jMenuItem.setActionCommand(ACTION_COPY);
		jMenuItem.addActionListener(this);
		jTablePopupMenu.add(jMenuItem);

		//Groups
		if (!jViews.getSelectedItem().equals("Groups")){
			jSubMenu = new JMenu("Groups");
			jSubMenu.setIcon(Images.ICON_GROUPS);
			jTablePopupMenu.add(jSubMenu);

			jMenuItem = new JMenuItem("Add to new group...");
			jMenuItem.setIcon(Images.ICON_ADD);
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
					if (location.equalsLocation(overview)){
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
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				jSubMenu.add(jCheckBoxMenuItem);
			}
		}
		//Groups - Locations
		if (jViews.getSelectedItem().equals("Groups")){
			jSubMenu = new JMenu("Group");
			jSubMenu.setIcon(Images.ICON_GROUPS);
			jTablePopupMenu.add(jSubMenu);

			jMenuItem = new JMenuItem("Rename Group");
			jMenuItem.setIcon(Images.ICON_RENAME);
			jMenuItem.setActionCommand(ACTION_RENAME_GROUP);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem("Delete Group");
			jMenuItem.setIcon(Images.ICON_DELETE);
			jMenuItem.setActionCommand(ACTION_DELETE_GROUP);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

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

		//Asset Filter
		jSubMenu = new JMenu("Add Asset Filter");
		jSubMenu.setIcon(Images.ICON_TOOL_ASSETS);
		jTablePopupMenu.add(jSubMenu);
		if (!jViews.getSelectedItem().equals("Groups")){
			if (jViews.getSelectedItem().equals("Stations")){
				jMenuItem = new JMenuItem("Station");
				jMenuItem.setIcon(Images.ICON_STATION);
				jMenuItem.setActionCommand(ACTION_ADD_STATION_FILTER);
				jMenuItem.addActionListener(this);
				jSubMenu.add(jMenuItem);
			}
			if (jViews.getSelectedItem().equals("Stations") || jViews.getSelectedItem().equals("Systems")){
				jMenuItem = new JMenuItem("System");
				jMenuItem.setIcon(Images.ICON_SYSTEM);
				jMenuItem.setActionCommand(ACTION_ADD_SYSTEM_FILTER);
				jMenuItem.addActionListener(this);
				jSubMenu.add(jMenuItem);
			}
			jMenuItem = new JMenuItem("Region");
			jMenuItem.setIcon(Images.ICON_REGION);
			jMenuItem.setActionCommand(ACTION_ADD_REGION_FILTER);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);
		} else {
			jMenuItem = new JMenuItem("Locations");
			jMenuItem.setIcon(Images.ICON_LOCATIONS);
			jMenuItem.setActionCommand(ACTION_ADD_GROUP_FILTER);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);
		}

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void updateTableView(){
		overviewEventList.getReadWriteLock().writeLock().lock();
		overviewEventList.clear();
		overviewEventList.getReadWriteLock().writeLock().unlock();
		if (jViews.getSelectedItem().equals("Regions")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getRegionColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (jViews.getSelectedItem().equals("Systems")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getSystemColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (jViews.getSelectedItem().equals("Stations")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getStationColumns());
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (jViews.getSelectedItem().equals("Groups")){
			overviewTableFormat.setColumnNames(overviewTableFormat.getRegionColumns());
			overviewTableModel.fireTableStructureChanged();
		}
		overviewEventList.getReadWriteLock().writeLock().lock();
		if (jViews.getSelectedItem().equals("Regions")){
			overviewEventList.addAll(regions);
		}
		if (jViews.getSelectedItem().equals("Systems")){
			overviewEventList.addAll(systems);
		}
		if (jViews.getSelectedItem().equals("Stations")){
			overviewEventList.addAll(stations);
		}
		if (jViews.getSelectedItem().equals("Groups")){
			overviewEventList.addAll(groups);
		}
		overviewEventList.getReadWriteLock().writeLock().unlock();
	}

	public void resetViews(){
		jViews.setSelectedItem("Stations");
	}
	
	private void copyToClipboard(Object o){
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		StringSelection st = new StringSelection(String.valueOf(o));
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(st, this);
	}

	@Override
	public void updateData() {
		stations = new ArrayList<Overview>();
		systems = new ArrayList<Overview>();
		regions = new ArrayList<Overview>();
		groups = new ArrayList<Overview>();
		overviewEventList.clear();
		EventList<EveAsset> assets = program.getEveAssetEventList();
		Map<String, Overview> regionsMap = new HashMap<String, Overview>();
		Map<String, Overview> systemsMap = new HashMap<String, Overview>();
		Map<String, Overview> stationsMap = new HashMap<String, Overview>();
		Map<String, Overview> groupsMap = new HashMap<String, Overview>();

		for (int a = 0; a < assets.size(); a++){
			EveAsset eveAsset = assets.get(a);
			//XXX Overview: We ignoring station containers (as they are not really cargo)
			if (eveAsset.getGroup().equals("Audit Log Secure Container") && program.getSettings().isIgnoreSecureContainers()) continue;

			//Ingnore Station Services (Count 1, Volume 1)
			if (eveAsset.getGroup().equals("Station Services")) continue;
			double reprocessedValue = eveAsset.getValueReprocessed();
			double value = eveAsset.getPrice() * eveAsset.getCount();
			long count = eveAsset.getCount();
			double volume = eveAsset.getVolume() * eveAsset.getCount();
			//Regions
			if (regionsMap.containsKey(eveAsset.getRegion())){
				Overview overview = regionsMap.get(eveAsset.getRegion());
				overview.addCount(count);
				overview.addValue(value);
				overview.addVolume(volume);
				overview.addReprocessedValue(reprocessedValue);
			} else {
				Overview overview = new Overview(eveAsset.getRegion(), "", eveAsset.getRegion(), reprocessedValue, volume, count, value);
				regionsMap.put(eveAsset.getRegion(), overview);
				regions.add(overview);
			}
			//Systems
			if (systemsMap.containsKey(eveAsset.getSolarSystem())){
				Overview overview = systemsMap.get(eveAsset.getSolarSystem());
				overview.addCount(count);
				overview.addValue(value);
				overview.addVolume(volume);
				overview.addReprocessedValue(reprocessedValue);
			} else {
				Overview overview = new Overview(eveAsset.getSolarSystem(), eveAsset.getSolarSystem(), eveAsset.getRegion(), reprocessedValue, volume, count, value);
				systemsMap.put(eveAsset.getSolarSystem(), overview);
				systems.add(overview);
			}
			//Stations
			if (stationsMap.containsKey(eveAsset.getLocation())){
				Overview overview = stationsMap.get(eveAsset.getLocation());
				overview.addCount(count);
				overview.addValue(value);
				overview.addVolume(volume);
				overview.addReprocessedValue(reprocessedValue);
			} else {
				Overview overview = new Overview(eveAsset.getLocation(), eveAsset.getSolarSystem(), eveAsset.getRegion(), reprocessedValue, volume, count, value);
				stationsMap.put(eveAsset.getLocation(), overview);
				stations.add(overview);
			}
			//Groups
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				if (!groupsMap.containsKey(overviewGroup.getName())){
					Overview overview = new Overview(overviewGroup.getName(), "", "", 0, 0, 0, 0);
					groupsMap.put(overviewGroup.getName(), overview);
					groups.add(overview);
				}
				for (int c = 0; c < overviewGroup.getLocations().size(); c++){
					OverviewLocation location = overviewGroup.getLocations().get(c);
					if (location.equalsLocation(eveAsset)){
						Overview overview = groupsMap.get(overviewGroup.getName());
						overview.addCount(count);
						overview.addValue(value);
						overview.addVolume(volume);
						overview.addReprocessedValue(reprocessedValue);
						break; //Only add once....
					}
				}
			}
		}
		//Cleanup
		regionsMap.clear();
		systemsMap.clear();
		stationsMap.clear();
		updateTableView();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_VIEW_SELECTED.equals(e.getActionCommand())){
			updateTableView();
			return;
		}
		//Group
		if (ACTION_ADD_NEW_GROUP.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
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
			
			overviewGroupDialog.show(station, system, region, null);
			return;
		}
		if (ACTION_DELETE_GROUP.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete Group: "+overviewGroup.getName()+"?", "Delete Group", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (value == JOptionPane.YES_OPTION){
				program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
				updateData();
			}
			return;
		}
		if (ACTION_RENAME_GROUP.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			overviewGroupDialog.show(null, null, null, overviewGroup);
			return;
		}
		//Copy
		if (ACTION_COPY.equals(e.getActionCommand())){
			String s = "";
			int[] selectedRows = jOverviewTable.getSelectedRows();
			int[] selectedColumns = jOverviewTable.getSelectedColumns();
			for (int a = 0; a < selectedRows.length; a++){
				for (int b = 0; b < selectedColumns.length; b++){
					if (b != 0) s = s + "	";
					s = s + jOverviewTable.getValueAt(selectedRows[a], selectedColumns[b]);
				}
				if ( (a + 1) < selectedRows.length ) s = s + "\r\n";
			}
			copyToClipboard(s);
		}
		//Filter
		if (ACTION_ADD_STATION_FILTER.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			AssetFilter assetFilter = new AssetFilter("Location", overview.getName(), AssetFilter.MODE_EQUALS, true, null);
			program.getToolPanel().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_SYSTEM_FILTER.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			AssetFilter assetFilter = new AssetFilter("Location", overview.getSolarSystem(), AssetFilter.MODE_CONTAIN, true, null);
			program.getToolPanel().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_REGION_FILTER.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			AssetFilter assetFilter = new AssetFilter("Region", overview.getRegion(), AssetFilter.MODE_EQUALS, true, null);
			program.getToolPanel().addFilter(assetFilter, true);
			program.getMainWindow().addTab(program.getAssetsTab());
		}
		if (ACTION_ADD_GROUP_FILTER.equals(e.getActionCommand())){
			int index = jOverviewTable.getSelectedRow();
			Overview overview = overviewTableModel.getElementAt(index);
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
			List<AssetFilter> assetFilters = new ArrayList<AssetFilter>();
			for (OverviewLocation location : overviewGroup.getLocations()){
				if (location.isStation()){
					AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.MODE_EQUALS, false, null);
					assetFilters.add(assetFilter);
					program.getToolPanel().addFilter(assetFilter, true);
				}
				if (location.isSystem()){
					AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.MODE_CONTAIN, false, null);
					assetFilters.add(assetFilter);
					program.getToolPanel().addFilter(assetFilter, true);
				}
				if (location.isRegion()){
					AssetFilter assetFilter = new AssetFilter("Region", location.getName(), AssetFilter.MODE_EQUALS, false, null);
					assetFilters.add(assetFilter);
					program.getToolPanel().addFilter(assetFilter, true);
				}
			}
			program.getMainWindow().addTab(program.getAssetsTab());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource().equals(jOverviewTable) && e.isPopupTrigger()){
			showTablePopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource().equals(jOverviewTable) && e.isPopupTrigger()){
			showTablePopup(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}

	class AddToGroup implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(e.getActionCommand());
			if (overviewGroup != null){
				int index = jOverviewTable.getSelectedRow();
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
				overviewGroupDialog.show(station, system, region, overviewGroup);
			}
		}
	}

	class RemoveFromGroup implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = jOverviewTable.getSelectedRow();
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

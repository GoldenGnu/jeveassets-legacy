/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTab implements ActionListener {

	private static final String ACTION_SELECTED = "ACTION_SELECTED";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JComboBox jOwners;
	private JButton jExpand;
	private JButton jCollapse;
	private JCheckBox jPiMaterial;
	private JSeparatorTable jTable;
	private JScrollPane jTableScroll;

	//Table
	private EventList<Material> eventList;
	private SeparatorList<Material> separatorList;
	private EventSelectionModel<Material> selectionModel;
	private EventTableModel<Material> tableModel;

	public MaterialsTab(final Program program) {
		super(program, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);
		//Category: Asteroid
		//Category: Material

		jPiMaterial = new JCheckBox(TabsMaterials.get().includePI());
		jPiMaterial.setActionCommand(ACTION_SELECTED);
		jPiMaterial.addActionListener(this);

		jOwners = new JComboBox();
		jOwners.setActionCommand(ACTION_SELECTED);
		jOwners.addActionListener(this);

		jCollapse = new JButton(TabsMaterials.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsMaterials.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		//Table Format
		EnumTableFormatAdaptor<MaterialTableFormat, Material> materialTableFormat = new EnumTableFormatAdaptor<MaterialTableFormat, Material>(MaterialTableFormat.class);
		//Backend
		eventList = new BasicEventList<Material>();
		//Separator
		separatorList = new SeparatorList<Material>(eventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = new EventTableModel<Material>(separatorList, materialTableFormat);
		//Table
		jTable = new JSeparatorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new MaterialsSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new MaterialsSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//Selection Model
		selectionModel = new EventSelectionModel<Material>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, null);
		//Scroll
		jTableScroll = new JScrollPane(jTable);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwners, 200, 200, 200)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jPiMaterial)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPiMaterial, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		List<String> owners = new ArrayList<String>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (Account account : accounts) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					String name;
					if (owner.isCorporation()) {
						name = TabsMaterials.get().whitespace(owner.getName());
					} else {
						name = owner.getName();
					}
					if (!owners.contains(name)) {
						owners.add(name);
					}
				}
			}
		}
		if (!owners.isEmpty()) {
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			Collections.sort(owners, new CaseInsensitiveComparator());
			owners.add(0, TabsMaterials.get().all());
			jOwners.setModel(new DefaultComboBoxModel(owners.toArray()));
			if (selectedItem != null && owners.contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
			jOwners.setEnabled(false);
			jOwners.setModel(new DefaultComboBoxModel());
			jOwners.getModel().setSelectedItem(TabsMaterials.get().no());
		}
	}

	@Override
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//DATA
		MenuData<Material> menuData = new MenuData<Material>(selectionModel.getSelected());
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<Material>(program, menuData));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<Material>(program, menuData));
	//LOOKUP
		jComponent.add(new JMenuLookup<Material>(program, menuData));
	//EDIT
		jComponent.add(new JMenuPrice<Material>(program, menuData));
	//REPROCESSED
		jComponent.add(new JMenuReprocessed<Material>(program, menuData));
	//INFO
		JMenuInfo.material(jComponent, selectionModel.getSelected(), eventList);
	}


	private void updateTable() {
		beforeUpdateData();
		String owner = (String) jOwners.getSelectedItem();
		List<Material> materials = new ArrayList<Material>();
		Map<String, Material> uniqueMaterials = new HashMap<String, Material>();
		Map<String, Material> totalMaterials = new HashMap<String, Material>();
		Map<String, Material> totalAllMaterials = new HashMap<String, Material>();
		Map<String, Material> summary = new HashMap<String, Material>();
		Map<String, Material> total = new HashMap<String, Material>();
		EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
		//Summary Total All
		Material summaryTotalAllMaterial = new Material(MaterialType.SUMMARY_ALL, TabsMaterials.get().all(), TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), null);
		for (Asset eveAsset : eveAssetEventList) {
			//Skip none-material + none Pi Material (if not enabled)
			if (!eveAsset.getCategory().equals("Material") && (!eveAsset.isPiMaterial() || !jPiMaterial.isSelected())) {
				continue;
			}
			//Skip not selected owners
			if (!owner.equals(eveAsset.getOwner()) && !owner.equals(TabsMaterials.get().whitespace(eveAsset.getOwner())) && !owner.equals(TabsMaterials.get().all())) {
				continue;
			}

			//Locations
			if (!uniqueMaterials.containsKey(eveAsset.getLocation() + eveAsset.getName())) { //New
				Material material = new Material(MaterialType.LOCATIONS, eveAsset.getName(), eveAsset.getLocation(), eveAsset.getGroup(), eveAsset);
				uniqueMaterials.put(eveAsset.getLocation() + eveAsset.getName(), material);
				materials.add(material);
			}
			Material material = uniqueMaterials.get(eveAsset.getLocation() + eveAsset.getName());

			//Locations Total
			if (!totalMaterials.containsKey(eveAsset.getLocation() + eveAsset.getGroup())) { //New
				Material totalMaterial = new Material(MaterialType.LOCATIONS_TOTAL, eveAsset.getGroup(), eveAsset.getLocation(), TabsMaterials.get().total(), eveAsset);
				totalMaterials.put(eveAsset.getLocation() + eveAsset.getGroup(), totalMaterial);
				materials.add(totalMaterial);
			}
			Material totalMaterial =  totalMaterials.get(eveAsset.getLocation() + eveAsset.getGroup());

			//Locations Total All
			if (!totalAllMaterials.containsKey(eveAsset.getLocation())) { //New
				Material totalAllMaterial = new Material(MaterialType.LOCATIONS_ALL, TabsMaterials.get().all(), eveAsset.getLocation(), TabsMaterials.get().total(), eveAsset);
				totalAllMaterials.put(eveAsset.getLocation(), totalAllMaterial);
				materials.add(totalAllMaterial);
			}
			Material totalAllMaterial = totalAllMaterials.get(eveAsset.getLocation());

			//Summary
			if (!summary.containsKey(eveAsset.getName())) { //New
				Material summaryMaterial = new Material(MaterialType.SUMMARY, eveAsset.getName(), TabsMaterials.get().summary(), eveAsset.getGroup(), eveAsset);
				summary.put(eveAsset.getName(), summaryMaterial);
				materials.add(summaryMaterial);
			}
			Material summaryMaterial = summary.get(eveAsset.getName());

			//Summary Total
			if (!total.containsKey(eveAsset.getGroup())) { //New
				Material summaryTotalMaterial = new Material(MaterialType.SUMMARY_TOTAL, eveAsset.getGroup(), TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), null);
				total.put(eveAsset.getGroup(), summaryTotalMaterial);
				materials.add(summaryTotalMaterial);
			}
			Material summaryTotalMaterial =  total.get(eveAsset.getGroup());

			//Update values
			material.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			totalMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			totalAllMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			summaryMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			summaryTotalMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			summaryTotalAllMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
		}
		if (!materials.isEmpty()) {
			materials.add(summaryTotalAllMaterial);
		}
		Collections.sort(materials);
		String location = "";
		for (Material material : materials) {
			if (!location.equals(material.getLocation())) {
				material.first();
				location = material.getLocation();
			}
		}
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(materials);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();

		if (!materials.isEmpty()) {
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
		}
		jTableScroll.getViewport().setViewPosition(new Point(0, 0));
		afterUpdateData();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			updateTable();
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true);
		}
	}
}

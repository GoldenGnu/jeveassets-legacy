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

package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.data.Module.FlagType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.io.local.EveFittingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadoutsTab extends JMainTab implements ActionListener {

	private static final Logger LOG = LoggerFactory.getLogger(LoadoutsTab.class);

	private static final String ACTION_FILTER = "ACTION_FILTER";
	private static final String ACTION_OWNERS = "ACTION_OWNERS";
	private static final String ACTION_EXPORT_LOADOUT = "ACTION_EXPORT_LOADOUT";
	private static final String ACTION_EXPORT_ALL_LOADOUTS = "ACTION_EXPORT_ALL_LOADOUTS";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	private static final String SHIP_CATEGORY = "Ship";

	//GUI
	private JComboBox jOwners;
	private JComboBox jShips;
	private JButton jExpand;
	private JButton jCollapse;
	private JSeparatorTable jTable;
	private JButton jExport;
	private JButton jExportAll;
	private LoadoutsExportDialog loadoutsExportDialog;
	private JCustomFileChooser jXmlFileChooser;

	//Table
	private EventList<Module> eventList;
	private FilterList<Module> filterList;
	private SeparatorList<Module> separatorList;
	private EventSelectionModel<Module> selectionModel;
	private EventTableModel<Module> tableModel;

	public LoadoutsTab(final Program program) {
		super(program, TabsLoadout.get().ship(), Images.TOOL_SHIP_LOADOUTS.getIcon(), true);

		loadoutsExportDialog = new LoadoutsExportDialog(program, this);

		try {
			jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
			}
		}
		JLabel jOwnersLabel = new JLabel(TabsLoadout.get().owner());
		jOwners = new JComboBox();
		jOwners.setActionCommand(ACTION_OWNERS);
		jOwners.addActionListener(this);

		JLabel jShipsLabel = new JLabel(TabsLoadout.get().ship1());
		jShips = new JComboBox();
		jShips.setActionCommand(ACTION_FILTER);
		jShips.addActionListener(this);

		jCollapse = new JButton(TabsLoadout.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsLoadout.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		jExport = new JButton(TabsLoadout.get().export1());
		jExport.setActionCommand(ACTION_EXPORT_LOADOUT);
		jExport.addActionListener(this);

		jExportAll = new JButton(TabsLoadout.get().export2());
		jExportAll.setActionCommand(ACTION_EXPORT_ALL_LOADOUTS);
		jExportAll.addActionListener(this);

		//Table Format
		EnumTableFormatAdaptor<ModuleTableFormat, Module> materialTableFormat = new EnumTableFormatAdaptor<ModuleTableFormat, Module>(ModuleTableFormat.class);
		//Backend
		eventList = new BasicEventList<Module>();
		//Filter
		filterList = new FilterList<Module>(eventList);
		//Separator
		separatorList = new SeparatorList<Module>(filterList, new ModuleSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = new EventTableModel<Module>(separatorList, materialTableFormat);
		//Table
		jTable = new JSeparatorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ModuleSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new ModuleSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//Selection Model
		selectionModel = new EventSelectionModel<Module>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, null);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwnersLabel)
					.addComponent(jOwners, 200, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jShipsLabel)
					.addComponent(jShips, 200, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExport, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExportAll, Program.BUTTONS_WIDTH + 10, Program.BUTTONS_WIDTH + 10, Program.BUTTONS_WIDTH + 10)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jOwnersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShipsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShips, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExport, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExportAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	private String browse() {
		File windows = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory()
							+ File.separator + "EVE"
							+ File.separator + "fittings"
							);
		File mac = new File(System.getProperty("user.home", ".")
							+ File.separator + "Library"
							+ File.separator + "Preferences"
							+ File.separator + "EVE Online Preferences"
							+ File.separator + "p_drive"
							+ File.separator + "My Documents"
							+ File.separator + "EVE"
							+ File.separator + "fittings"
							);
		LOG.info("Mac Browsing: {}", mac.getAbsolutePath());
		if (windows.exists()) { //Windows
			jXmlFileChooser.setCurrentDirectory(windows);
		} else if (mac.exists()) { //Mac
			//PENDING TEST if fittings path is set correct on mac
			//			should open: ~library/preferences/eve online preferences/p_drive/my documents/eve/overview
			jXmlFileChooser.setCurrentDirectory(mac);
		} else { //Others: use program directory is there is only Win & Mac clients
			jXmlFileChooser.setCurrentDirectory(new File(Settings.getUserDirectory()));
		}
		int bFound = jXmlFileChooser.showSaveDialog(program.getMainWindow().getFrame());
		if (bFound  == JFileChooser.APPROVE_OPTION) {
			File file = jXmlFileChooser.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public void export() {
		String fitName = loadoutsExportDialog.getFittingName();
		String fitDescription = loadoutsExportDialog.getFittingDescription();
		if (!fitName.isEmpty()) {
			String selectedShip = (String) jShips.getSelectedItem();
			Asset exportAsset = null;
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList) {
				String key = eveAsset.getName() + " #" + eveAsset.getItemID();
				if (!selectedShip.equals(key)) {
					continue;
				} else {
					exportAsset = eveAsset;
					break;
				}
			}
			loadoutsExportDialog.setVisible(false);
			if (exportAsset == null) {
				return;
			}
			String filename = browse();
			if (filename != null) {
				EveFittingWriter.save(Collections.singletonList(exportAsset), filename, fitName, fitDescription);
			}
		} else {
			JOptionPane.showMessageDialog(loadoutsExportDialog.getDialog(),
					TabsLoadout.get().name1(),
					TabsLoadout.get().empty(),
					JOptionPane.PLAIN_MESSAGE);
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
		MenuData<Module> menuData = new MenuData<Module>(selectionModel.getSelected());
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<Module>(program, menuData));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<Module>(program, menuData));
	//LOOKUP
		jComponent.add(new JMenuLookup<Module>(program, menuData));
	//EDIT
		jComponent.add(new JMenuPrice<Module>(program, menuData));
	//REPROCESSED
		jComponent.add(new JMenuReprocessed<Module>(program, menuData));
	//INFO
		JMenuInfo.module(jComponent, selectionModel.getSelected());
	}

	private void updateTable() {
		List<Module> ship = new ArrayList<Module>();
		EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
		for (Asset eveAsset : eveAssetEventList) {
			String key = eveAsset.getName() + " #" + eveAsset.getItemID();
			if (!eveAsset.getCategory().equals(SHIP_CATEGORY) || !eveAsset.isSingleton()) {
				continue;
			}
			Module moduleShip = new Module(eveAsset, TabsLoadout.get().totalShip(), eveAsset.getName(), key, TabsLoadout.get().flagTotalValue(), null, eveAsset.getPrice(), 1, eveAsset.isMarketGroup(), eveAsset.getTypeID());
			Module moduleModules = new Module(eveAsset, TabsLoadout.get().totalModules(), null, key, TabsLoadout.get().flagTotalValue(), null, 0, 0, false, null);
			Module moduleTotal = new Module(eveAsset, TabsLoadout.get().totalAll(), null, key, TabsLoadout.get().flagTotalValue(), null, eveAsset.getPrice(), 1, false, null);
			ship.add(moduleShip);
			ship.add(moduleModules);
			ship.add(moduleTotal);
			for (Asset assetModule : eveAsset.getAssets()) {
				Module module = new Module(assetModule, assetModule.getName(), assetModule.getName(), key, assetModule.getFlag(), assetModule.getPrice(), (assetModule.getPrice() * assetModule.getCount()), assetModule.getCount(), assetModule.isMarketGroup(), assetModule.getTypeID());
				if (!ship.contains(module)
						|| assetModule.getFlag().contains(FlagType.HIGH_SLOT.getFlag())
						|| assetModule.getFlag().contains(FlagType.MEDIUM_SLOT.getFlag())
						|| assetModule.getFlag().contains(FlagType.LOW_SLOT.getFlag())
						|| assetModule.getFlag().contains(FlagType.RIG_SLOTS.getFlag())
						|| assetModule.getFlag().contains(FlagType.SUB_SYSTEMS.getFlag())
						) {
					ship.add(module);
				} else {
					module = ship.get(ship.indexOf(module));
					module.addCount(assetModule.getCount());
					module.addValue(assetModule.getPrice() * assetModule.getCount());
				}
				moduleModules.addValue(assetModule.getPrice() * assetModule.getCount());
				moduleModules.addCount(assetModule.getCount());
				moduleTotal.addValue(assetModule.getPrice() * assetModule.getCount());
				moduleTotal.addCount(assetModule.getCount());
			}
		}
		Collections.sort(ship);
		String key = "";
		for (Module module : ship) {
			if (!key.equals(module.getKey())) {
				module.first();
				key = module.getKey();
			}
		}
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(ship);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
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
						name = TabsLoadout.get().whitespace9(owner.getName());
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
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			Collections.sort(owners, new CaseInsensitiveComparator());
			owners.add(0, TabsLoadout.get().all());
			jOwners.setModel(new DefaultComboBoxModel(owners.toArray()));
			if (selectedItem != null && owners.contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jOwners.setEnabled(false);
			jOwners.setModel(new DefaultComboBoxModel());
			jOwners.getModel().setSelectedItem(TabsLoadout.get().no());
			jShips.setModel(new DefaultComboBoxModel());
			jShips.getModel().setSelectedItem(TabsLoadout.get().no());
		}
		updateTable();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_OWNERS.equals(e.getActionCommand())) {
			String owner = (String) jOwners.getSelectedItem();
			List<String> charShips = new ArrayList<String>();
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList) {
				String key = eveAsset.getName() + " #" + eveAsset.getItemID();
				if (!eveAsset.getCategory().equals(SHIP_CATEGORY) || !eveAsset.isSingleton()) {
					continue;
				}
				if (!owner.equals(eveAsset.getOwner())
						&& !owner.equals(TabsLoadout.get().whitespace9(eveAsset.getOwner()))
						&& !owner.equals(TabsLoadout.get().all())
						) {
					continue;
				}
				charShips.add(key);
			}
			if (!charShips.isEmpty()) {
				Collections.sort(charShips, new CaseInsensitiveComparator());
				jExpand.setEnabled(true);
				jCollapse.setEnabled(true);
				jExport.setEnabled(true);
				jExportAll.setEnabled(true);
				jOwners.setEnabled(true);
				jShips.setEnabled(true);
				String selectedItem = (String) jShips.getSelectedItem();
				jShips.setModel(new DefaultComboBoxModel(charShips.toArray()));
				if (selectedItem != null && charShips.contains(selectedItem)) {
					jShips.setSelectedItem(selectedItem);
				} else {
					jShips.setSelectedIndex(0);
				}
			} else {
				jExpand.setEnabled(false);
				jCollapse.setEnabled(false);
				jExport.setEnabled(false);
				jExportAll.setEnabled(false);
				jShips.setEnabled(false);
				jShips.setModel(new DefaultComboBoxModel());
				jShips.getModel().setSelectedItem(TabsLoadout.get().no1());
			}
		}
		if (ACTION_FILTER.equals(e.getActionCommand())) {
			String selectedShip = (String) jShips.getSelectedItem();
			filterList.setMatcher(new Module.ModuleMatcher(selectedShip));
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true);
		}
		if (ACTION_EXPORT_LOADOUT.equals(e.getActionCommand())) {
			loadoutsExportDialog.setVisible(true);
		}
		if (ACTION_EXPORT_ALL_LOADOUTS.equals(e.getActionCommand())) {
			String filename = browse();
			List<Asset> ships = new ArrayList<Asset>();
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList) {
				if (!eveAsset.getCategory().equals(SHIP_CATEGORY) || !eveAsset.isSingleton()) {
					continue;
				}
				ships.add(eveAsset);
			}
			if (filename != null) {
				EveFittingWriter.save(new ArrayList<Asset>(ships), filename);
			}

		}

	}
}
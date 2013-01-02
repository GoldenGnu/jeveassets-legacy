/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.Percent;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTab implements ActionListener, ListEventListener<StockpileItem> {

	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_SHOPPING_LIST_MULTI = "ACTION_SHOPPING_LIST_MULTI";
	private static final String ACTION_IMPORT = "ACTION_IMPORT";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";
	private static final String ACTION_EDIT_ITEM = "ACTION_EDIT_ITEM";
	private static final String ACTION_ADD_TO = "ACTION_ADD_TO";
	private static final String ACTION_DELETE_ITEM = "ACTION_DELETE_ITEM";

	private JButton jAdd;
	private JButton jShoppingList;
	private JButton jImport;
	private JButton jExpand;
	private JButton jCollapse;
	private JSeparatorTable jTable;
	private JLabel jVolumeNow;
	private JLabel jVolumeNeeded;
	private JLabel jValueNow;
	private JLabel jValueNeeded;

	private StockpileDialog stockpileDialog;
	private StockpileItemDialog stockpileItemDialog;
	private StockpileShoppingListDialog stockpileShoppingListDialog;
	private StockpileSelectionDialog stockpileSelectionDialog;

	//Table
	private EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;
	private EventTableModel<StockpileItem> tableModel;
	private EventList<StockpileItem> eventList;
	private FilterList<StockpileItem> filterList;
	private SeparatorList<StockpileItem> separatorList;
	private EventSelectionModel<StockpileItem> selectionModel;
	private StockpileFilterControl filterControl;

	//Data
	Map<String, Long> ownersID;
	Map<Long, String> ownersName;

	public static final String NAME = "stockpile"; //Not to be changed!

	public StockpileTab(final Program program) {
		super(program, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new StockpileSelectionDialog(program);

		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		jAdd.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jAdd);

		jToolBarLeft.addSeparator();

		jShoppingList = new JButton(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jShoppingList.setActionCommand(ACTION_SHOPPING_LIST_MULTI);
		jShoppingList.addActionListener(this);
		jShoppingList.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jShoppingList.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jShoppingList.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jShoppingList);

		jToolBarLeft.addSeparator();

		jImport = new JButton(TabsStockpile.get().importEFT(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImport.setActionCommand(ACTION_IMPORT);
		jImport.addActionListener(this);
		jImport.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jImport);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		jCollapse = new JButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);
		jCollapse.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jCollapse);

		jExpand = new JButton(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);
		jExpand.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		//Backend
		eventList = new BasicEventList<StockpileItem>();
		//Filter
		filterList = new FilterList<StockpileItem>(eventList);
		filterList.addListEventListener(this);
		//Sorting (per column)
		SortedList<StockpileItem> sortedListColumn = new SortedList<StockpileItem>(filterList);
		//Sorting Total (Ensure that total is always last)
		SortedList<StockpileItem> sortedListTotal = new SortedList<StockpileItem>(sortedListColumn, new TotalComparator());
		//Separator
		separatorList = new SeparatorList<StockpileItem>(sortedListTotal, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = new EventTableModel<StockpileItem>(separatorList, tableFormat);
		//Table
		jTable = new JStockpileTable(program, tableModel);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = new EventSelectionModel<StockpileItem>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Filter GUI
		filterControl = new StockpileFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME)
				);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		int toolbatHeight = jToolBarRight.getInsets().top + jToolBarRight.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, toolbatHeight, toolbatHeight, toolbatHeight)
					.addComponent(jToolBarRight, toolbatHeight, toolbatHeight, toolbatHeight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);

		jVolumeNow = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNow(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNow);

		jValueNow = StatusPanel.createLabel(TabsStockpile.get().shownValueNow(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNow);

		jVolumeNeeded = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNeeded(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNeeded);

		jValueNeeded = StatusPanel.createLabel(TabsStockpile.get().shownValueNeeded(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNeeded);
	}

	public Stockpile showAddStockpile() {
		return stockpileDialog.showAdd();
	}

	public Stockpile addToStockpile(Stockpile stockpile, List<StockpileItem> items) {
		if (stockpile == null) { //new stockpile
			stockpile = stockpileDialog.showAdd();
		}
		if (stockpile != null) { //Add items
			List<StockpileItem> newItems = new ArrayList<StockpileItem>();
			for (StockpileItem fromItem : items) {
				//Clone item
				StockpileItem toItem = null;
				//Search for existing
				for (StockpileItem item : stockpile.getItems()) {
					if (item.getTypeID() == fromItem.getTypeID()) {
						toItem = item;
						break;
					}
				}
				if (toItem != null) { //Update existing (add counts)
					toItem.addCountMinimum(fromItem.getCountMinimum());
				} else { //Add new
					StockpileItem item = new StockpileItem(stockpile, fromItem);
					stockpile.add(item);
					newItems.add(item);
				}
			}
			if (newItems.isEmpty()) {
				tableModel.fireTableDataChanged();
			} else {
				addItems(newItems);
			}
		}
		return stockpile;
	}

	public void scrollToSctockpile(final Stockpile stockpile) {
		StockpileItem item = stockpile.getItems().get(0);
		int row = separatorList.indexOf(item) - 1;
		if (row < 0) { //Collapsed: Expand and run again...
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
					if (separator.first().equals(item)) {
						separatorList.getReadWriteLock().writeLock().lock();
						try {
							separator.setLimit(Integer.MAX_VALUE);
						} finally {
							separatorList.getReadWriteLock().writeLock().unlock();
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								scrollToSctockpile(stockpile);
							}
						});
					}
				}
			}
		} else { //Expanded
			Rectangle rect = jTable.getCellRect(row, 0, true);
			rect.setSize(jTable.getVisibleRect().getSize());
			jTable.scrollRectToVisible(rect);
		}
	}

	private void addItem(StockpileItem item) {
		addItems(Collections.singletonList(item));
	}

	private void addItems(List<StockpileItem> items) {
		if (items.isEmpty()) {
			return;
		}
		updateStockpile(items.get(0).getStockpile());
		//Lock Table
		beforeUpdateData();
		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.addAll(items);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
		//Unlcok Table
		afterUpdateData();
	}

	private void removeItem(StockpileItem item) {
		removeItems(Collections.singletonList(item));
	}

	private void removeItems(List<StockpileItem> items) {
		//Lock Table
		beforeUpdateData();
		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(items);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
		//Unlcok Table
		afterUpdateData();
	}

	public void addStockpile(Stockpile stockpile) {
		if (stockpile == null) {
			return;
		}
		updateStockpile(stockpile);
		//Lock Table
		beforeUpdateData();
		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.addAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
		//Unlcok Table
		afterUpdateData();
	}

	private void removeStockpile(Stockpile stockpile) {
		//Lock Table
		beforeUpdateData();
		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
		//Unlcok Table
		afterUpdateData();
	}

	private void updateStockpile(Stockpile stockpile) {
		//Update owner name
		stockpile.setOwner(ownersName.get(stockpile.getOwnerID()));
		//Update Item flag name
		ItemFlag flag = program.getSettings().getItemFlags().get(stockpile.getFlagID());
		if (flag != null) {
			stockpile.setFlag(flag.getFlagName());
		} else {
			stockpile.setFlag(null);
		}
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (StockpileItem item : stockpile.getItems()) {
				if (item instanceof Stockpile.StockpileTotal) {
					continue;
				}
				final int TYPE_ID = item.getTypeID();
				double price = program.getSettings().getPrice(TYPE_ID, item.isBPC());
				float volume = program.getSettings().getVolume(TYPE_ID, true);
				boolean marketGroup = ApiIdConverter.marketGroup(TYPE_ID, program.getSettings().getItems());
				item.updateValues(price, volume, marketGroup);
				//Inventory AKA Assets
				if (stockpile.isInventory()) {
					for (Asset asset : program.getEveAssetEventList()) {
						if (asset.getTypeID() != TYPE_ID) {
							continue; //Ignore wrong typeID
						}
						//Skip market orders
						if (asset.getFlag().equals(General.get().marketOrderSellFlag())) {
							continue; //Ignore market sell orders
						}
						if (asset.getFlag().equals(General.get().marketOrderBuyFlag())) {
							continue; //Ignore market buy orders
						}
						//Skip contracts
						if (asset.getFlag().equals(General.get().contractIncluded())) {
							continue; //Ignore contracts included
						}
						if (asset.getFlag().equals(General.get().contractExcluded())) {
							continue; //Ignore contracts excluded
						}
						Location location = program.getSettings().getLocations().get(asset.getLocationID());
						item.updateAsset(asset, ownersID.get(asset.getOwner()), location);
					}
				}
				//Orders & Jobs
				if (stockpile.isBuyOrders() || stockpile.isSellOrders() || stockpile.isJobs()) {
					for (Account account : program.getSettings().getAccounts()) {
						for (Owner owner : account.getOwners()) {
							if (!owner.isShowAssets()) {
								continue; //Ignore hidden owners
							}
							//Market Orders
							if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
								for (ApiMarketOrder marketOrder : owner.getMarketOrders()) {
									if (marketOrder.getTypeID() != TYPE_ID) {
										continue; //Ignore wrong typeID
									}
									Location location = program.getSettings().getLocations().get(marketOrder.getStationID());
									item.updateMarketOrder(marketOrder, owner.getOwnerID(), location);
								}
							}
							//Industry Job
							if (stockpile.isJobs()) {
								for (ApiIndustryJob industryJob : owner.getIndustryJobs()) {
									if (industryJob.getOutputTypeID() != TYPE_ID) {
										continue; //Ignore wrong typeID
									}
									Location location = program.getSettings().getLocations().get(industryJob.getOutputLocationID());
									Item itemType = program.getSettings().getItems().get(industryJob.getOutputTypeID());
									item.updateIndustryJob(industryJob, owner.getOwnerID(), location, itemType);
								}
							}
						}
					}
				}
			}
		}
		stockpile.updateTotal();
	}

	private void saveExpandedState() {
		for (int i = 0; i < separatorList.size(); i++) {
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
				StockpileItem item = (StockpileItem) separator.first();
				item.getStockpile().setExpanded(separator.getLimit() != 0);
			}
		}
	}

	private void loadExpandedState() {
		for (int i = 0; i < separatorList.size(); i++) {
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
				StockpileItem item = (StockpileItem) separator.first();
				separatorList.getReadWriteLock().writeLock().lock();
				try {
					separator.setLimit(item.getStockpile().isExpanded() ? Integer.MAX_VALUE : 0);
				} finally {
					separatorList.getReadWriteLock().writeLock().unlock();
				}
			}
		}
	}

	private void importEFT() {
		//Get string from clipboard
		String fit = getClipboardContents();

		//Validate
		fit = fit.trim();
		if (fit.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEmpty(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		String[] split = fit.split("[\r\n]");
		if (split.length < 1) { //Malformed
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importNotValid(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//Malformed
		if (!split[0].startsWith("[") || !split[0].contains(",") || !split[0].endsWith("]")) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importNotValid(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		//Format and split
		fit = fit.replace("[", "").replace("]", "");
		List<String> modules = new ArrayList<String>(Arrays.asList(fit.split("[\r\n,]")));

		//Get name of fit
		String name;
		if (modules.size() > 1) {
			name = modules.get(1).trim();
			modules.remove(1);
		} else {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importNotValid(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		//Create Stockpile
		Stockpile stockpile = stockpileDialog.showAdd(name);
		if (stockpile == null) { //Dialog cancelled
			return;
		}

		//Add modules
		Map<Integer, StockpileItem> items = new HashMap<Integer, StockpileItem>();
		for (String module : modules) {
			module = module.trim().toLowerCase(); //Format line
			if (module.isEmpty()) { //Skip empty lines
				continue;
			}
			//Search for item name
			for (Map.Entry<Integer, Item> entry : program.getSettings().getItems().entrySet()) {
				Item item = entry.getValue();
				if (item.getName().toLowerCase().equals(module)) { //Found item
					int typeID = item.getTypeID();
					if (!items.containsKey(typeID)) { //Add new item
						StockpileItem stockpileItem = new StockpileItem(stockpile, item.getName(), item.getGroup(), item.getTypeID(), 0);
						stockpile.add(stockpileItem);
						items.put(typeID, stockpileItem);
					}
					//Update item count
					StockpileItem stockpileItem = items.get(typeID);
					long count = stockpileItem.getCountMinimum();
					count++;
					stockpileItem.setCountMinimum(count);
					break; //search done
				}
			}
		}

		//Update stockpile data
		addStockpile(stockpile);
		scrollToSctockpile(stockpile);
	}

	private String getClipboardContents() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				return (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				return "";
			}
		}
		return "";
	}

	@Override
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);
		List<StockpileItem> selected = new ArrayList<StockpileItem>(selectionModel.getSelected());
		for (int i = 0; i < selected.size(); i++) { //Remove StockpileTotal and SeparatorList.Separator
			Object object = selected.get(i);
			if ((object instanceof SeparatorList.Separator) || (object instanceof StockpileTotal)) {
				selected.remove(i);
				i--;
			}
		}

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//DATA
		MenuData<StockpileItem> menuData = new MenuData<StockpileItem>(selected);
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selected));
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<StockpileItem>(program, menuData));
	//STOCKPILE
		JMenuItem jMenuItem;

		JMenu jMenu = new JMenu(TabsStockpile.get().stockpile());
		jMenu.setIcon(Images.TOOL_STOCKPILE.getIcon());
		jComponent.add(jMenu);

		JMenu jSubMenu = new JMenu(TabsStockpile.get().addToStockpile());
		jSubMenu.setEnabled(!selected.isEmpty());
		jMenu.add(jSubMenu);
		if (!selected.isEmpty()) {
			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().addToNewStockpile(), Images.EDIT_ADD.getIcon(), selected);
			jMenuItem.setActionCommand(ACTION_ADD_TO);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jSubMenu.addSeparator();
			List<Stockpile> stockpiles = program.getSettings().getStockpiles();
			Collections.sort(stockpiles);

			for (Stockpile stockpile : stockpiles) {
				jMenuItem = new JStockpileMenuItem(Images.TOOL_STOCKPILE.getIcon(), stockpile, selected);
				jMenuItem.setActionCommand(ACTION_ADD_TO);
				jMenuItem.addActionListener(this);
				jSubMenu.add(jMenuItem);
			}
		}

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().editItem(), Images.EDIT_EDIT.getIcon(), selected);
		jMenuItem.setActionCommand(ACTION_EDIT_ITEM);
		jMenuItem.addActionListener(this);
		jMenuItem.setEnabled(selected.size() == 1);
		jMenu.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().deleteItem(), Images.EDIT_DELETE.getIcon(), selected);
		jMenuItem.setActionCommand(ACTION_DELETE_ITEM);
		jMenuItem.addActionListener(this);
		jMenuItem.setEnabled(!selected.isEmpty());
		jMenu.add(jMenuItem);
	//LOOKUP
		jComponent.add(new JMenuLookup<StockpileItem>(program, menuData));
	//EDIT
		jComponent.add(new JMenuPrice<StockpileItem>(program, menuData));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//REPROCESSED
		jComponent.add(new JMenuReprocessed<StockpileItem>(program, menuData));
	//INFO
		JMenuInfo.stockpileItem(jComponent, selected);
	}

	@Override
	public void updateData() {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<StockpileItem>();
		//Owners Look-Up
		ownersID = new HashMap<String, Long>();
		ownersName = new HashMap<Long, String>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				ownersID.put(owner.getName(), owner.getOwnerID());
				ownersName.put(owner.getOwnerID(), owner.getName());
			}
		}

		for (Stockpile stockpile : program.getSettings().getStockpiles()) {
			stockpileItems.addAll(stockpile.getItems());
			updateStockpile(stockpile);
		}

		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
	}

	@Override
	public void listChanged(final ListEvent<StockpileItem> listChanges) {
		List<StockpileItem> items = new ArrayList<StockpileItem>(filterList);
		//Remove StockpileTotal and SeparatorList.Separator
		for (int i = 0; i < items.size(); i++) {
			Object object = items.get(i);
			if ((object instanceof SeparatorList.Separator) || (object instanceof StockpileTotal)) {
				items.remove(i);
				i--;
			}
		}

		double volumnNow = 0;
		double volumnNeeded = 0;
		double valueNow = 0;
		double valueNeeded = 0;

		for (StockpileItem item : items) {
			volumnNow = volumnNow + item.getVolumeNow();
			if (item.getVolumeNeeded() < 0) { //Only add if negative
				volumnNeeded = volumnNeeded + item.getVolumeNeeded();
			}
			valueNow = valueNow + item.getValueNow();
			if (item.getValueNeeded() < 0) { //Only add if negative
				valueNeeded = valueNeeded + item.getValueNeeded();
			}
		}

		jVolumeNow.setText(TabsStockpile.get().now() + Formater.doubleFormat(volumnNow));
		jValueNow.setText(TabsStockpile.get().now() + Formater.iskFormat(valueNow));
		jVolumeNeeded.setText(TabsStockpile.get().needed() + Formater.doubleFormat(volumnNeeded));
		jValueNeeded.setText(TabsStockpile.get().needed() + Formater.iskFormat(valueNeeded));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		//Shopping list single
		if (StockpileSeparatorTableCell.ACTION_SHOPPING_LIST_SINGLE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				stockpileShoppingListDialog.show(item.getStockpile());
			}
		}
		//Shopping list multi
		if (ACTION_SHOPPING_LIST_MULTI.equals(e.getActionCommand())) {
			List<Stockpile> stockpiles = stockpileSelectionDialog.show();
			if (stockpiles != null) {
				stockpileShoppingListDialog.show(stockpiles);
			}
		}
		//Collapse all
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		//Expand all
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
		//Add stockpile (EFT Import)
		if (ACTION_IMPORT.equals(e.getActionCommand())) {
			importEFT();
		}
		//Add stockpile
		if (ACTION_ADD.equals(e.getActionCommand())) {
			Stockpile stockpile = stockpileDialog.showAdd();
			if (stockpile != null) {
				addStockpile(stockpile);
				scrollToSctockpile(stockpile);
			}
		}
		//Edit stockpile
		if (StockpileSeparatorTableCell.ACTION_EDIT_STOCKPILE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				boolean updated = stockpileDialog.showEdit(stockpile);
				if (updated) {
					//To tricker resort
					removeStockpile(stockpile);
					addStockpile(stockpile);
				}
			}
		}
		//Clone stockpile
		if (StockpileSeparatorTableCell.ACTION_CLONE_STOCKPILE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				Stockpile cloneStockpile = stockpileDialog.showClone(stockpile);
				if (cloneStockpile != null) {
					addStockpile(cloneStockpile);
				}
			}
		}
		//Delete stockpile
		if (StockpileSeparatorTableCell.ACTION_DELETE_STOCKPILE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					program.getSettings().getStockpiles().remove(stockpile);
					removeStockpile(stockpile);
				}
			}
		}
		//Add item
		if (StockpileSeparatorTableCell.ACTION_ADD_ITEM.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				StockpileItem addItem = stockpileItemDialog.showAdd(stockpile);
				if (addItem != null) { //Edit/Add/Update existing or cancel
					removeItem(addItem); //Need to remove first (if it already exist)
					addItem(addItem);
				}
			}
		}
		//Add item to
		if (ACTION_ADD_TO.equals(e.getActionCommand())) {
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem) {
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				addToStockpile(jMenuItem.getStockpile(), jMenuItem.getItems());
			}
		}
		//Edit item
		if (ACTION_EDIT_ITEM.equals(e.getActionCommand())) {
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem) {
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				List<StockpileItem> items = jMenuItem.getItems();
				if (items.size() == 1) {
					StockpileItem editItem = stockpileItemDialog.showEdit(items.get(0));
					if (editItem != null) {
						removeItem(editItem);
						addItem(editItem);
					}
				}
			}
		}
		//Delete item
		if (ACTION_DELETE_ITEM.equals(e.getActionCommand())) {
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem) {
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				List<StockpileItem> items = jMenuItem.getItems();
				if (!items.isEmpty()) {
					int value;
					if (items.size() == 1) {
						value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), items.get(0).getName(), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					} else {
						value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().deleteItems(items.size()), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					}
					if (value == JOptionPane.OK_OPTION) {
						for (StockpileItem item : items) {
							item.getStockpile().remove(item);
						}
						removeItems(items);
					}
				}
			}
		}
	}

	public static class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return o1.getSeperator().compareTo(o2.getSeperator());
		}
	}

	public static class JStockpileMenuItem extends JMenuItem {

		private final List<StockpileItem> items;
		private final Stockpile stockpile;

		public JStockpileMenuItem(final Icon icon, final Stockpile stockpile, final List<StockpileItem> items) {
			super(stockpile.getName(), icon);
			this.items = items;
			this.stockpile = stockpile;
		}

		public JStockpileMenuItem(final String title, final Icon icon, final List<StockpileItem> items) {
			super(title, icon);
			this.items = items;
			this.stockpile = null;
		}

		public List<StockpileItem> getItems() {
			return items;
		}

		public Stockpile getStockpile() {
			return stockpile;
		}
	}

	public class StockpileFilterControl extends FilterControl<StockpileItem> {

		private Enum[] enumColumns = null;
		private List<EnumTableColumn<StockpileItem>> columns = null;
		private EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;

		public StockpileFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat, final EventList<StockpileItem> eventList, final FilterList<StockpileItem> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final StockpileItem item, final String columnString) {
			Enum<?> column = valueOf(columnString);
			if (column instanceof StockpileTableFormat) {
				StockpileTableFormat format = (StockpileTableFormat) column;
				return format.getColumnValue(item);
			}

			if (column instanceof StockpileExtendedTableFormat) {
				StockpileExtendedTableFormat format = (StockpileExtendedTableFormat) column;
				return format.getColumnValue(item);
			}
			return null; //Fallback: show all...
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			if (column instanceof StockpileTableFormat) {
				StockpileTableFormat format = (StockpileTableFormat) column;
				if (Number.class.isAssignableFrom(format.getType())) {
					return true;
				} else if (format.getType().getName().equals(Percent.class.getName())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			if (column instanceof StockpileTableFormat) {
				StockpileTableFormat format = (StockpileTableFormat) column;
				if (format.getType().getName().equals(Date.class.getName())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected Enum[] getColumns() {
			if (enumColumns == null) {
				enumColumns = concat(StockpileExtendedTableFormat.values(), StockpileTableFormat.values());
			}
			return enumColumns;
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			try {
				return StockpileTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return StockpileExtendedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected void afterFilter() {
			loadExpandedState();
		}

		@Override
		protected void beforeFilter() {
			saveExpandedState();
		}

		private Enum[] concat(final Enum[] a, final Enum[] b) {
			Enum<?>[] c = new Enum<?>[a.length + b.length];
			System.arraycopy(a, 0, c, 0, a.length);
			System.arraycopy(b, 0, c, a.length, b.length);
			return c;
		}

		@Override
		protected List<EnumTableColumn<StockpileItem>> getEnumColumns() {
			if (columns == null) {
				columns = new ArrayList<EnumTableColumn<StockpileItem>>();
				columns.addAll(Arrays.asList(StockpileExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(StockpileTableFormat.values()));
			}
			return columns;
		}

		@Override
		protected List<EnumTableColumn<StockpileItem>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<StockpileItem>>(tableFormat.getShownColumns());
		}
	}

	public static class TotalComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			if ((o1 instanceof StockpileTotal) && (o2 instanceof StockpileTotal)) {
				return 0;  //Equal (both StockpileTotal)
			} else if (o1 instanceof StockpileTotal) {
				return 1;  //After
			} else if (o2 instanceof StockpileTotal) {
				return -1; //Before
			} else {
				return 0;  //Equal (not StockpileTotal)
			}
		}
	}
}

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
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
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
import net.nikr.eve.jeveasset.gui.shared.*;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTab implements ActionListener {

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_IMPORT = "ACTION_IMPORT";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	private final static String ACTION_EDIT_ITEM = "ACTION_EDIT_ITEM";
	private final static String ACTION_DELETE_ITEM = "ACTION_DELETE_ITEM";
	
	private JButton jAdd;
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
	
	//Table
	private EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> stockpileTableFormat;
	private EventTableModel<StockpileItem> stockpileTableModel;
	private EventList<StockpileItem> stockpileEventList;
	private SeparatorList<StockpileItem> separatorList;
	private EventSelectionModel<StockpileItem> selectionModel;
	private StockpileFilterControl filterControl;
	
	public static final String NAME = "stockpile"; //Not to be changed!
	
	public StockpileTab(Program program) {
		super(program, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);
		
		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		
		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);
		
		jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		jAdd.setMinimumSize( new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setMaximumSize( new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jAdd);
		
		jToolBarLeft.addSeparator();
		
		jImport= new JButton(TabsStockpile.get().importEFT(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImport.setActionCommand(ACTION_IMPORT);
		jImport.addActionListener(this);
		jImport.setMinimumSize( new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setMaximumSize( new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jImport);
		
		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);
		
		jCollapse = new JButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);
		jCollapse.setMinimumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jCollapse);

		jExpand = new JButton(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);
		jExpand.setMinimumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setMaximumSize( new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jExpand);
		
		stockpileTableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		stockpileTableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		stockpileEventList = new BasicEventList<StockpileItem>();
		FilterList<StockpileItem> filterList = new FilterList<StockpileItem>(stockpileEventList);
		separatorList = new SeparatorList<StockpileItem>(filterList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		stockpileTableModel = new EventTableModel<StockpileItem>(separatorList, stockpileTableFormat);
		//Tables
		jTable = new JStockpileTable(program, stockpileTableModel);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Listeners
		installTableMenu(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Selection Model
		selectionModel = new EventSelectionModel<StockpileItem>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Filter GUI
		
		filterControl = new StockpileFilterControl(
				program.getMainWindow().getFrame(),
				program.getSettings().getTableFilters(NAME),
				filterList,
				stockpileEventList);
		
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
		
		jVolumeNeeded = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNeeded(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNeeded);
		
		jValueNow = StatusPanel.createLabel(TabsStockpile.get().shownValueNow(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNow);
		
		jValueNeeded = StatusPanel.createLabel(TabsStockpile.get().shownValueNeeded(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNeeded);
	}
	
	@Override
	public void updateSettings(){
		program.getSettings().getTableColumns().put(NAME, stockpileTableFormat.getColumns());
	}

	public Stockpile showAddStockpile() {
		Stockpile stockpile = stockpileDialog.showAdd();
		if (stockpile != null){
			updateData();
			if (program.getSettings().isStockpileFocusTab()) scrollToSctockpile(stockpile);
		}
		return stockpile;
	}
	
	public void scrollToSctockpile(final Stockpile stockpile){
		StockpileItem item = stockpile.getItems().get(0);
		int row = separatorList.indexOf(item) - 1;
		if (row < 0){ //Collapsed: Expand and run again...
			for (int i = 0; i < separatorList.size(); i++){
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator){
					SeparatorList.Separator separator = (SeparatorList.Separator) object;
					if (separator.first().equals(item)){
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
	
	private void saveExpandedState(){
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator){
				SeparatorList.Separator separator = (SeparatorList.Separator) object;
				StockpileItem item = (StockpileItem) separator.first();
				item.getStockpile().setExpanded(separator.getLimit() != 0);
			}
		}
	}
	private void loadExpandedState(){
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator){
				SeparatorList.Separator separator = (SeparatorList.Separator) object;
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
	
	private String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				//Return empty line...
			}
		}
		return result;
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);
		List<StockpileItem> selected = new ArrayList<StockpileItem>(selectionModel.getSelected());
		for (int i = 0; i < selected.size(); i++){ //Remove StockpileTotal and SeparatorList.Separator
			Object object = selected.get(i);
			if ((object instanceof SeparatorList.Separator) || (object instanceof StockpileTotal)){
				selected.remove(i);
				i--;
			}
		}
		
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selected));
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<StockpileItem>(program, selected));
	//STOCKPILE
		JMenuItem jMenuItem;
		
		JMenu jMenu = new JMenu(TabsStockpile.get().stockpile());
		jMenu.setIcon(Images.TOOL_STOCKPILE.getIcon());
		jComponent.add(jMenu);
		
		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().editItem(), selected);
		jMenuItem.setActionCommand(ACTION_EDIT_ITEM);
		jMenuItem.addActionListener(this);
		jMenuItem.setEnabled(selected.size() == 1);
		jMenu.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().deleteItem(), selected);
		jMenuItem.setActionCommand(ACTION_DELETE_ITEM);
		jMenuItem.addActionListener(this);
		jMenuItem.setEnabled(!selected.isEmpty());
		jMenu.add(jMenuItem);
	//LOOKUP
		jComponent.add(new JMenuLookup<StockpileItem>(program, selected));
	//EDIT
		jComponent.add(new JMenuEditItem<StockpileItem>(program, selected));
	//COLUMNS
		jComponent.add(stockpileTableFormat.getMenu(program, stockpileTableModel, jTable));		
	}

	@Override
	public void updateData() {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<StockpileItem>();
		//Characters Look-Up
		Map<String, Long> ownersID = new HashMap<String, Long>();
		Map<Long, String> ownersName = new HashMap<Long, String>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				ownersID.put(human.getName(), human.getOwnerID());
				ownersName.put(human.getOwnerID(), human.getName());
			}
		}
		//ItemFlag Look-Up
		Map<String, ItemFlag> flags = new HashMap<String, ItemFlag>();
		for (ItemFlag itemFlag : program.getSettings().getItemFlags().values()){
			flags.put(itemFlag.getFlagName(), itemFlag);
		}
		//Regions Look-Up
		Map<String, Long> regions = new HashMap<String, Long>();
		for (Location location : program.getSettings().getLocations().values()){
			if (location.isRegion()){
				regions.put(location.getName(), location.getLocationID());
			}
		}
		double volumnNow = 0;
		double volumnNeeded = 0;
		double valueNow = 0;
		double valueNeeded = 0;
		
		for (Stockpile stockpile : program.getSettings().getStockpiles()){
			stockpileItems.addAll(stockpile.getItems());
			stockpile.setOwner(ownersName.get(stockpile.getOwnerID()));
			ItemFlag flag = program.getSettings().getItemFlags().get(stockpile.getFlagID());
			if (flag != null) stockpile.setFlag(flag.getFlagName());
			stockpile.reset();
			if (!stockpile.isEmpty()){
				for (StockpileItem item : stockpile.getItems()){
					if (item instanceof Stockpile.StockpileTotal) continue;
					int typeID = item.getTypeID();
					double price = program.getSettings().getPrice(typeID, false);
					float volume = program.getSettings().getVolume(typeID, true);
					boolean marketGroup = ApiIdConverter.marketGroup(typeID, program.getSettings().getItems());
					item.updateValues(price, volume, marketGroup);
					//Inventory AKA Assets
					if (stockpile.isInventory()){
						for (Asset asset : program.getEveAssetEventList()){
							if (General.get().marketOrderFlag().equals(asset.getFlag())) continue; //Ignore market orders
							item.updateAsset(asset, flags.get(asset.getFlag()) , ownersID.get(asset.getOwner()), regions.get(asset.getRegion()));
						}
					}
					//Orders & Jobs
					if (stockpile.isBuyOrders() || stockpile.isSellOrders() || stockpile.isJobs()){
						for (Account account : program.getSettings().getAccounts()){
							for (Human human : account.getHumans()){
								if (human.isShowAssets()){
									//Market Orders
									for (ApiMarketOrder marketOrder : human.getMarketOrders()){
										Location location = program.getSettings().getLocations().get(marketOrder.getStationID());
										item.updateMarketOrder(marketOrder, human.getOwnerID(), location);
									}
									//Jobs
									for (ApiIndustryJob industryJob : human.getIndustryJobs()){
										Location location = program.getSettings().getLocations().get(industryJob.getOutputLocationID());
										Item itemType = program.getSettings().getItems().get(industryJob.getOutputTypeID());
										ItemFlag itemFlag = program.getSettings().getItemFlags().get(industryJob.getOutputFlag());
										item.updateIndustryJob(industryJob, itemFlag, human.getOwnerID(), location, itemType);
									}
								}
							}
						}
					}
				}
			}
			stockpile.updateTotal();
			volumnNow = volumnNow + stockpile.getTotal().getVolumeNow();
			volumnNeeded = volumnNeeded + stockpile.getTotal().getVolumeNeeded();
			valueNow = valueNow + stockpile.getTotal().getValueNow();
			valueNeeded = valueNeeded + stockpile.getTotal().getValueNeeded();
		}
		
		jVolumeNow.setText(TabsStockpile.get().now()+Formater.doubleFormat(volumnNow));
		jVolumeNeeded.setText(TabsStockpile.get().needed()+Formater.doubleFormat(volumnNeeded));
		jValueNow.setText(TabsStockpile.get().now()+Formater.iskFormat(valueNow));
		jValueNeeded.setText(TabsStockpile.get().needed()+Formater.iskFormat(valueNeeded));
		
		//Save separator expanded/collapsed state
		saveExpandedState();
		//Update list
		try {
			stockpileEventList.getReadWriteLock().writeLock().lock();
			stockpileEventList.clear();
			stockpileEventList.addAll(stockpileItems);
		} finally {
			stockpileEventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		loadExpandedState();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())){
			Stockpile stockpile = stockpileDialog.showAdd();
			if (stockpile != null) updateData();
		}
		if (ACTION_IMPORT.equals(e.getActionCommand())){
			//Get string from clipboard
			String fit = getClipboardContents();
			
			//Validate
			fit = fit.trim();
			if (fit.isEmpty()){ //Empty sting
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEmpty(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			String[] split = fit.split("[\r\n]");
			if (split.length < 1){ //Malformed
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importNotValid(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
				return;
			}
			//Malformed
			if (!split[0].startsWith("[") || !split[0].contains(",") || !split[0].endsWith("]")){
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importNotValid(), TabsStockpile.get().importEFT(), JOptionPane.PLAIN_MESSAGE);
				return;
			}
			//FIXME do some more validation...
			
			//Format and split
			fit = fit.replace("[", "").replace("]", "");
			List<String> modules = new ArrayList<String>(Arrays.asList(fit.split("[\r\n,]")));
			
			//Get name of fit
			String name;
			if (modules.size() > 1){
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
			for (String module : modules){
				module = module.trim().toLowerCase(); //Format line
				if (module.isEmpty()) continue; //Skip empty lines
				//Search for item name
				for (Map.Entry<Integer, Item> entry : program.getSettings().getItems().entrySet()){
					Item item = entry.getValue();
					if (item.getName().toLowerCase().equals(module)){ //Found item
						int typeID = item.getTypeID();
						if (!items.containsKey(typeID)){ //Add new item
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
			updateData();
			scrollToSctockpile(stockpile);
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
		if (StockpileSeparatorTableCell.ACTION_EDIT_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				boolean updated = stockpileDialog.showEdit(stockpile);
				if (updated) updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_CLONE_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				boolean updated = stockpileDialog.showClone(stockpile);
				if (updated) updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_DELETE_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION){
					program.getSettings().getStockpiles().remove(stockpile);
					updateData();
				}
			}
		}
		if (StockpileSeparatorTableCell.ACTION_SHOPPING_LIST_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				stockpileShoppingListDialog.show(item.getStockpile());
			}
		}
		if (StockpileSeparatorTableCell.ACTION_ADD_ITEM.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				boolean updated = stockpileItemDialog.showAdd(stockpile);
				if (updated) updateData();
			}
		}
		if (ACTION_EDIT_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				List<StockpileItem> items = jMenuItem.getItems();
				if (items.size() == 1){
					boolean updated = stockpileItemDialog.showEdit(items.get(0));
					if (updated) updateData();
				}
			}
		}
		if (ACTION_DELETE_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				List<StockpileItem> items = jMenuItem.getItems();
				if (!items.isEmpty()) {
					int value;
					if (items.size() == 1){
						value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), items.get(0).getName(), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					} else {
						value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().deleteItems(items.size()), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					}
					if (value == JOptionPane.OK_OPTION){
						for (StockpileItem item : items){
							item.getStockpile().remove(item);
						}
						updateData();
					}
				}
			}
		}
	}
	
	public class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(StockpileItem o1, StockpileItem o2) {
			return o1.getSeperator().compareTo(o2.getSeperator());
		}
	}
	
	public static class JStockpileMenuItem extends JMenuItem{

		private List<StockpileItem> items;
		
		public JStockpileMenuItem(String title, List<StockpileItem> items) {
			super(title);
			this.items = items;
		}

		public List<StockpileItem> getItems() {
			return items;
		}
	} 
	
	public class StockpileFilterControl extends FilterControl<StockpileItem>{

		private Enum[] enumColumns = null;
		private List<EnumTableColumn<StockpileItem>> columns = null;
		
		public StockpileFilterControl(JFrame jFrame, Map<String, List<Filter>> filters, FilterList<StockpileItem> filterList, EventList<StockpileItem> eventList) {
			super(jFrame, NAME, filters, filterList, eventList);
		}
		
		@Override
		protected Object getColumnValue(StockpileItem item, String columnString) {
			Enum column = valueOf(columnString);
			if (column instanceof StockpileTableFormat){
				StockpileTableFormat format = (StockpileTableFormat) column;
				return format.getColumnValue(item);
			}
			
			if (column instanceof StockpileExtendedTableFormat){
				StockpileExtendedTableFormat format = (StockpileExtendedTableFormat) column;
				return format.getColumnValue(item);
			}
			return null; //Fallback: show all...
		}
		
		@Override
		protected boolean isNumericColumn(Enum column) {
			if (column instanceof StockpileTableFormat){
				StockpileTableFormat format = (StockpileTableFormat) column;
				if (Number.class.isAssignableFrom(format.getType())) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected boolean isDateColumn(Enum column) {
			if (column instanceof StockpileTableFormat){
				StockpileTableFormat format = (StockpileTableFormat) column;
				if (format.getType().getName().equals(Date.class.getName())) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected Enum[] getColumns() {
			if (enumColumns == null){
				enumColumns = concat(StockpileExtendedTableFormat.values(), StockpileTableFormat.values());
			}
			return enumColumns;
		}
		
		@Override
		protected Enum valueOf(String column) {
			try {
				return StockpileTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return StockpileExtendedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: "+column);
		}

		@Override
		protected void afterFilter() {
			loadExpandedState();
		}

		@Override
		protected void beforeFilter() {
			saveExpandedState();
		}
		
		private Enum[] concat(Enum[] a, Enum[] b) {
			Enum[] c= new Enum[a.length+b.length];
			System.arraycopy(a, 0, c, 0, a.length);
			System.arraycopy(b, 0, c, a.length, b.length);
			return c;
		}
		
		@Override
		protected List<EnumTableColumn<StockpileItem>> getEnumColumns() {
			if (columns == null){
				columns = new ArrayList<EnumTableColumn<StockpileItem>>();
				columns.addAll(Arrays.asList(StockpileExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(StockpileTableFormat.values()));
			}
			return columns;
		}
	}
}
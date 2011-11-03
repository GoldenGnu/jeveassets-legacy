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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileSeparatorTableCell.JStockpileMenuItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileTab extends JMainTab implements ActionListener {

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	
	private JButton jAdd;
	private JSeparatorTable jTable;
	private JButton jExpand;
	private JButton jCollapse;
	
	private EventTableModel<StockpileItem> stockpileTableModel;
	private EventList<StockpileItem> stockpileEventList;
	private SeparatorList<StockpileItem> separatorList;
	
	private StockpileDialog stockpileDialog;
	private StockpileItemDialog stockpileItemDialog;
	
	public StockpileTab(Program program) {
		super(program, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);
		
		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		
		jAdd = new JButton(TabsStockpile.get().newStockpile());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		
		jCollapse = new JButton(TabsStockpile.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsStockpile.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);
		
		EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> stockpileTableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		stockpileEventList = new BasicEventList<StockpileItem>();
		separatorList = new SeparatorList<StockpileItem>(stockpileEventList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		stockpileTableModel = new EventTableModel<StockpileItem>(separatorList, stockpileTableFormat);
		//Tables
		jTable = new JStockpileTable(stockpileTableModel);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(jTable, separatorList, this));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(jTable, separatorList, this));
		PaddingTableCellRenderer.install(jTable, 3);

		JScrollPane jTableScroll = new JScrollPane(jTable);
		
		
		//Selection Model
		EventSelectionModel<StockpileItem> selectionModel = new EventSelectionModel<StockpileItem>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateTableMenu(JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(false);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {}

	@Override
	public void updateData() {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<StockpileItem>();
		//Characters Look-Up
		Map<String, Long> chars = new HashMap<String, Long>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				chars.put(human.getName(), human.getCharacterID());
			}
		}
		//ItemFlag Look-Up
		Map<String, Integer> flags = new HashMap<String, Integer>();
		for (ItemFlag itemFlag : program.getSettings().getItemFlags().values()){
			flags.put(itemFlag.getFlagName(), itemFlag.getFlagID());
		}
		//Regions Look-Up
		Map<String, Long> regions = new HashMap<String, Long>();
		for (Location location : program.getSettings().getLocations().values()){
			if (location.isRegion()){
				regions.put(location.getName(), location.getLocationID());
			}
		}
		for (Stockpile stockpile : program.getSettings().getStockpiles()){
			stockpileItems.addAll(stockpile.getItems());
			stockpile.reset();
			if (!stockpile.isEmpty()){
				for (StockpileItem item : stockpile.getItems()){
					for (Asset asset : program.getEveAssetEventList()){
						if (item == null) System.out.println("item FAIL!");
						if (asset == null) System.out.println("asset FAIL!");
						if (asset.getFlag() == null) System.out.println("Flag FAIL!");
						if (asset.getOwner() == null) System.out.println("Owner FAIL!");
						if (asset.getRegion() == null) System.out.println("Region FAIL!");
						if (chars == null) System.out.println("chars list FAIL!");
						if (flags == null) System.out.println("flags list FAIL!");
						if (regions == null) System.out.println("regions list FAIL!");
						item.match(asset, flags.get(asset.getFlag()), chars.get(asset.getOwner()), regions.get(asset.getRegion()));
					}
				}
			}
			stockpile.updateTotal();
		}
		regions = null;
		flags = null;
		chars = null;
		try {
			stockpileEventList.getReadWriteLock().writeLock().lock();
			stockpileEventList.clear();
			stockpileEventList.addAll(stockpileItems);
		} finally {
			stockpileEventList.getReadWriteLock().writeLock().unlock();
		}
		
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())){
			stockpileDialog.showAdd();
			updateData();
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
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			StockpileItem item = (StockpileItem) separator.first();
			Stockpile stockpile = item.getStockpile();
			stockpileDialog.showEdit(stockpile);
			updateData();
		}
		if (StockpileSeparatorTableCell.ACTION_CLONE_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			StockpileItem item = (StockpileItem) separator.first();
			Stockpile stockpile = item.getStockpile();
			stockpileDialog.showClone(stockpile);
			updateData();
		}
		if (StockpileSeparatorTableCell.ACTION_DELETE_STOCKPILE.equals(e.getActionCommand())){
			//FIXME no i18n for deleteAll dialog
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete", "Delete", JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION){
				int index = jTable.getSelectedRow();
				Object o = stockpileTableModel.getElementAt(index);
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				program.getSettings().getStockpiles().remove(stockpile);
				updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_ADD_ITEM.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			StockpileItem item = (StockpileItem) separator.first();
			Stockpile stockpile = item.getStockpile();
			stockpileItemDialog.showAdd(stockpile);
			updateData();
		}
		if (StockpileSeparatorTableCell.ACTION_EDIT_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				StockpileItem item = jMenuItem.getItem();
				stockpileItemDialog.showEdit(item);
				updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_DELETE_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				StockpileItem item = jMenuItem.getItem();
				//FIXME no i18n for delete item dialog
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete", "Delete", JOptionPane.OK_CANCEL_OPTION);
				if (value == JOptionPane.OK_OPTION){
					item.getStockpile().remove(item);
					updateData();
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
}

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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.*;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab{

	
	private EventTableModel<MarketOrder> sellOrdersTableModel;
	private EventTableModel<MarketOrder> buyOrdersTableModel;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;

	private JTable jSellTable;
	private JTable jBuyTable;

	public MarketOrdersTab(Program program) {
		super(program, TabsOrders.get().market(), Images.TOOL_MARKET_ORDERS.getIcon(), true);

		//Table format
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> buyTableFormat =
				new EnumTableFormatAdaptor<MarketTableFormat, MarketOrder>(MarketTableFormat.class);
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> sellTableFormat =
				new EnumTableFormatAdaptor<MarketTableFormat, MarketOrder>(MarketTableFormat.class);
		//Backend
		sellOrdersEventList = new BasicEventList<MarketOrder>();
		buyOrdersEventList = new BasicEventList<MarketOrder>();
		//Backend
		FilterList<MarketOrder> sellOrdersFilterList = new FilterList<MarketOrder>(sellOrdersEventList);
		FilterList<MarketOrder> buyOrdersFilterList = new FilterList<MarketOrder>(buyOrdersEventList);
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<MarketOrder>(sellOrdersFilterList);
		SortedList<MarketOrder> buyOrdersSortedList = new SortedList<MarketOrder>(buyOrdersFilterList);
		//Table Model
		sellOrdersTableModel = new EventTableModel<MarketOrder>(sellOrdersSortedList, sellTableFormat);
		buyOrdersTableModel = new EventTableModel<MarketOrder>(buyOrdersSortedList, buyTableFormat);
		//Tables
		jSellTable = new JAutoColumnTable(sellOrdersTableModel);
		jBuyTable = new JAutoColumnTable(buyOrdersTableModel);
		//Table Selection
		EventSelectionModel<MarketOrder> sellSelectionModel = new EventSelectionModel<MarketOrder>(sellOrdersSortedList);
		sellSelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jSellTable.setSelectionModel(sellSelectionModel);
		EventSelectionModel<MarketOrder> buySelectionModel = new EventSelectionModel<MarketOrder>(buyOrdersSortedList);
		buySelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jBuyTable.setSelectionModel(buySelectionModel);
		//Listeners
		installTableMenu(jSellTable);
		installTableMenu(jBuyTable);
		//Sorters
		TableComparatorChooser.install(jSellTable, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyTable, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jSellLabel = new JLabel(TabsOrders.get().sell());
		JLabel jBuyLabel = new JLabel(TabsOrders.get().buy());
		//Scroll Panels
		JScrollPane jSellTableScroll = new JScrollPane(jSellTable);
		JScrollPane jBuyTableScroll = new JScrollPane(jBuyTable);
		//Table Filter
		List<FilterList<MarketOrder>> filterLists = new ArrayList<FilterList<MarketOrder>>();
		filterLists.add(buyOrdersFilterList);
		filterLists.add(sellOrdersFilterList);
		
		MarketOrdersFilterControl filterControl = new MarketOrdersFilterControl(program.getMainWindow().getFrame(), program.getSettings().getMarketOrdersFilters(), filterLists);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jSellLabel)
						.addComponent(jBuyLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jSellTableScroll, 0, 0, Short.MAX_VALUE)
						)
						.addComponent(jBuyTableScroll, 0, 0, Short.MAX_VALUE)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(filterControl.getPanel())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellTableScroll, 0, 0, Short.MAX_VALUE)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBuyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyTableScroll, 0, 0, Short.MAX_VALUE)
				)
		);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		if (e.getSource() instanceof JTable){
			JTable jTable = (JTable) e.getSource();
			EventTableModel<?> tableModel = (EventTableModel<?>) jTable.getModel();
			//Select clicked row
			jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
			jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);
			//is single row selected
			boolean isSingleRow = jTable.getSelectedRows().length == 1;
			//COPY
			if (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0){
				jTablePopupMenu.add(new JMenuCopy(jTable));
				addSeparator(jTablePopupMenu);
			}
			//FILTER & LOOKUP
			MarketOrder marketOrder = isSingleRow ? (MarketOrder) tableModel.getElementAt(jTable.getSelectedRow()): null;
			jTablePopupMenu.add(new JMenuAssetFilter(program, marketOrder));
			jTablePopupMenu.add(new JMenuStockpile(program, marketOrder));
			jTablePopupMenu.add(new JMenuLookup(program, marketOrder));
		}
		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		JMenuItem  jMenuItem;
		
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSellSingleRow = (jSellTable.getSelectedRows().length == 1);
		boolean isBuySingleRow = (jBuyTable.getSelectedRows().length == 1);

		MarketOrder sellMarketOrder = isSellSingleRow ? sellOrdersTableModel.getElementAt(jSellTable.getSelectedRow()): null;
		MarketOrder buyMarketOrder = isBuySingleRow ? buyOrdersTableModel.getElementAt(jBuyTable.getSelectedRow()) : null;

		jMenuItem = new JMenuItem(TabsOrders.get().sell1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, sellMarketOrder));
		jComponent.add(new JMenuStockpile(program, sellMarketOrder));
		jComponent.add(new JMenuLookup(program, sellMarketOrder));

		addSeparator(jComponent);

		jMenuItem = new JMenuItem(TabsOrders.get().buy1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, buyMarketOrder));
		jComponent.add(new JMenuStockpile(program, buyMarketOrder));
		jComponent.add(new JMenuLookup(program, buyMarketOrder));
	}

	@Override
	public void updateData() {
		List<String> unique = new ArrayList<String>();
		List<MarketOrder> sellMarketOrders = new ArrayList<MarketOrder>();
		List<MarketOrder> buyMarketOrders = new ArrayList<MarketOrder>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsOrders.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					//Only add once and don't add empty orders
					List<MarketOrder> marketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human, human.getMarketOrders(), program.getSettings());
					if (!unique.contains(name) && !marketOrders.isEmpty()){
						unique.add(name);
						for (MarketOrder marketOrder :marketOrders){
							if (marketOrder.getBid() < 1){
								sellMarketOrders.add(marketOrder);
							} else {
								buyMarketOrders.add(marketOrder);
							}
						}
					}
				}
			}
		}
		if (!unique.isEmpty()){
			jSellTable.setEnabled(true);
			jBuyTable.setEnabled(true);
		} else {
			jSellTable.setEnabled(false);
			jBuyTable.setEnabled(false);
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
		try {
			sellOrdersEventList.getReadWriteLock().writeLock().lock();
			sellOrdersEventList.clear();
			sellOrdersEventList.addAll( sellMarketOrders );
			buyOrdersEventList.getReadWriteLock().writeLock().lock();
			buyOrdersEventList.clear();
			buyOrdersEventList.addAll( buyMarketOrders );
		} finally {
			sellOrdersEventList.getReadWriteLock().writeLock().unlock();
			buyOrdersEventList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public static class MarketOrdersFilterControl extends FilterControl<MarketOrder>{

		public MarketOrdersFilterControl(JFrame jFrame, Map<String, List<Filter>> filters, List<FilterList<MarketOrder>> filterLists) {
			super(jFrame, filters, filterLists);
		}
		
		@Override
		protected String getColumnValue(MarketOrder item, String column) {
			MarketTableFormat format = MarketTableFormat.valueOf(column);
			if (format == MarketTableFormat.QUANTITY){
				Quantity quantity = (Quantity)format.getColumnValue(item);
				return String.valueOf(quantity.getQuantityRemaining());
			} else {
				return format.getColumnValue(item).toString();
			}
		}
		
		@Override
		protected boolean isNumeric(Enum column) {
			MarketTableFormat format = (MarketTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else if (Quantity.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		protected boolean isDate(Enum column) {
			MarketTableFormat format = (MarketTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Enum[] getColumns() {
			return MarketTableFormat.values();
		}
		
		@Override
		protected Enum valueOf(String column) {
			return MarketTableFormat.valueOf(column);
		}
		
	}
}

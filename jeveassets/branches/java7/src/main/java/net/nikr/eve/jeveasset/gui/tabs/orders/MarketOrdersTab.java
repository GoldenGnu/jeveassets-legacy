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
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab implements ActionListener{

	private final static String ACTION_SELECTED = "ACTION_SELECTED";

	
	private JComboBox<String> jCharacters;
	private JComboBox<String> jState;
	private EventTableModel<MarketOrder> sellOrdersTableModel;
	private EventTableModel<MarketOrder> buyOrdersTableModel;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;

	private List<MarketOrder> all;
	private Map<String, List<MarketOrder>> orders;
	private List<String> characters;

	private JAutoColumnTable jSellOrders;
	private JAutoColumnTable jBuyOrders;

	private String[] orderStates = new String[]{"All", "Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending"};

	public MarketOrdersTab(Program program) {
		super(program, TabsOrders.get().market(), Images.TOOL_MARKET_ORDERS.getIcon(), true);

		jCharacters = new JComboBox<>();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox<>();
		jState.setActionCommand(ACTION_SELECTED);
		jState.addActionListener(this);

		//Table format
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> buyTableFormat =
				new EnumTableFormatAdaptor<>(MarketTableFormat.class);
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> sellTableFormat =
				new EnumTableFormatAdaptor<>(MarketTableFormat.class);
		//Backend
		sellOrdersEventList = new BasicEventList<>();
		buyOrdersEventList = new BasicEventList<>();
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<>(sellOrdersEventList);
		SortedList<MarketOrder> buyOrdersSortedList = new SortedList<>(buyOrdersEventList);
		//Table Model
		sellOrdersTableModel = new EventTableModel<>(sellOrdersSortedList, sellTableFormat);
		buyOrdersTableModel = new EventTableModel<>(buyOrdersSortedList, buyTableFormat);
		//Tables
		jSellOrders = new JAutoColumnTable(sellOrdersTableModel);
		jBuyOrders = new JAutoColumnTable(buyOrdersTableModel);
		//Table Selection
		EventSelectionModel<MarketOrder> sellSelectionModel = new EventSelectionModel<>(sellOrdersEventList);
		sellSelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jSellOrders.setSelectionModel(sellSelectionModel);
		EventSelectionModel<MarketOrder> buySelectionModel = new EventSelectionModel<>(buyOrdersEventList);
		buySelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jBuyOrders.setSelectionModel(buySelectionModel);
		//Listeners
		installTableMenu(jSellOrders);
		installTableMenu(jBuyOrders);
		//Sorters
		TableComparatorChooser.install(jSellOrders, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyOrders, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jCharactersLabel = new JLabel(TabsOrders.get().character());
		JLabel jStateLabel = new JLabel(TabsOrders.get().state());
		JLabel jSellLabel = new JLabel(TabsOrders.get().sell());
		JLabel jBuyLabel = new JLabel(TabsOrders.get().buy());
		//Scroll Panels
		JScrollPane jSellOrdersScrollPanel = jSellOrders.getScrollPanel();
		JScrollPane jBuyOrdersScrollPanel = jBuyOrders.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel)
					.addComponent(jSellLabel)
					.addComponent(jBuyLabel)
				)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jCharacters, 200, 200, 200)
							.addGap(100)
							.addComponent(jStateLabel)
							.addComponent(jState, 200, 200, 200)
						)
						.addComponent(jSellOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
					)
					.addComponent(jBuyOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jState, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBuyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
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
			jTablePopupMenu.add(new JMenuLookup(program, marketOrder));
		}
		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		JMenuItem  jMenuItem;
		
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSellSingleRow = (jSellOrders.getSelectedRows().length == 1);
		boolean isBuySingleRow = (jBuyOrders.getSelectedRows().length == 1);

		MarketOrder sellMarketOrder = isSellSingleRow ? sellOrdersTableModel.getElementAt(jSellOrders.getSelectedRow()): null;
		MarketOrder buyMarketOrder = isBuySingleRow ? buyOrdersTableModel.getElementAt(jBuyOrders.getSelectedRow()) : null;

		jMenuItem = new JMenuItem(TabsOrders.get().sell1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, sellMarketOrder));
		jComponent.add(new JMenuLookup(program, sellMarketOrder));

		addSeparator(jComponent);

		jMenuItem = new JMenuItem(TabsOrders.get().buy1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, buyMarketOrder));
		jComponent.add(new JMenuLookup(program, buyMarketOrder));
	}

	@Override
	public void updateData() {
		characters = new ArrayList<>();
		orders = new HashMap<>();
		all = new ArrayList<>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				List<MarketOrder> marketOrders = new ArrayList<>();
				orders.put(human.getName(), marketOrders);
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsOrders.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					characters.add(name);
					List<MarketOrder> characterMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrders(), program.getSettings());
					orders.put(name, characterMarketOrders);
					all.addAll(characterMarketOrders);
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jState.setEnabled(true);
			jSellOrders.setEnabled(true);
			jBuyOrders.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel<>( new Vector<>(characters)));
			jState.setModel( new DefaultComboBoxModel<>(orderStates));
			jCharacters.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			jSellOrders.setEnabled(false);
			jBuyOrders.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel<String>());
			jCharacters.getModel().setSelectedItem(TabsOrders.get().no());
			jState.setModel( new DefaultComboBoxModel<String>());
			jState.getModel().setSelectedItem(TabsOrders.get().no());
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jCharacters.getSelectedItem();
			if (characters.size() > 1){
				List<MarketOrder> marketOrders;
				List<MarketOrder> sellMarketOrders = new ArrayList<>();
				List<MarketOrder> buyMarketOrders = new ArrayList<>();
				if (selected.equals("All")){
					marketOrders = all;
				} else {
					marketOrders = orders.get(selected);
				}
				String sState = (String) jState.getSelectedItem();
				int state = 0;
				if (sState.equals("All")) state = -1;
				if (sState.equals("Active")) state = 0;
				if (sState.equals("Closed")) state = 1;
				if (sState.equals("Expired")
						|| sState.equals("Fulfilled")
						|| sState.equals("Partially Fulfilled")) state = 2;
				if (sState.equals("Cancelled")) state = 3;
				if (sState.equals("Pending")) state = 4;
				for (int a = 0; a < marketOrders.size(); a++){
					MarketOrder marketOrder = marketOrders.get(a);
					if (marketOrder.getOrderState() == state || state < 0){
						boolean add = true;
						if (state == 2){
							add = false;
							if (sState.equals("Expired") && marketOrder.getStatus().equals("Expired")){
								add = true;
							}
							if (sState.equals("Fulfilled") && marketOrder.getStatus().equals("Fulfilled")){
								add = true;
							}
							if (sState.equals("Partially Fulfilled") && marketOrder.getStatus().equals("Partially Fulfilled")){
								add = true;
							}
						}
						if (add){
							if (marketOrder.getBid() < 1){
								sellMarketOrders.add(marketOrder);
							} else {
								buyMarketOrders.add(marketOrder);
							}
						}
					}

				}
				/*
				sellOrdersEventList.clear();
				sellOrdersEventList.addAll( sellMarketOrders );
				buyOrdersEventList.clear();
				buyOrdersEventList.addAll( buyMarketOrders );
				 * 
				 */
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
		}
	}
}

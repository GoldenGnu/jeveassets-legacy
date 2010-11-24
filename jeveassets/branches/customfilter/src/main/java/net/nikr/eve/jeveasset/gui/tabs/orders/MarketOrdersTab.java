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
import java.util.Arrays;
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
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog;
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog.CustomDialogInterface;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab implements ActionListener{

	private final static String ACTION_CHARACTER_SELECTED = "ACTION_CHARACTER_SELECTED";
	private final static String ACTION_STATE_SELECTED = "ACTION_STATE_SELECTED";
	private final static String CUSTOM = "<Custom>";
	private final static String ALL = "All";
	
	private JComboBox jCharacters;
	private JComboBox jState;
	private EventTableModel<MarketOrder> sellOrdersTableModel;
	private EventTableModel<MarketOrder> buyOrdersTableModel;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;
	private CustomDialog characterCustomDialog;
	private CustomDialog stateCustomDialog;

	private List<MarketOrder> all;
	private Map<String, List<MarketOrder>> orders;
	private List<String> selectedCharacters;
	private List<String> selectedStats;

	private JAutoColumnTable jSellOrders;
	private JAutoColumnTable jBuyOrders;

	public MarketOrdersTab(Program program) {
		super(program, "Market Orders", Images.ICON_TOOL_MARKET_ORDERS, true);

		characterCustomDialog = new CustomDialog(program);
		stateCustomDialog = new CustomDialog(program);

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_CHARACTER_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox();
		jState.setActionCommand(ACTION_STATE_SELECTED);
		jState.addActionListener(this);

		//Table format
		MarketOrderTableFormat sellTableFormat = new MarketOrderTableFormat();
		MarketOrderTableFormat buyTableFormat = new MarketOrderTableFormat();
		//Backend
		sellOrdersEventList = new BasicEventList<MarketOrder>();
		buyOrdersEventList = new BasicEventList<MarketOrder>();
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<MarketOrder>(sellOrdersEventList);
		SortedList<MarketOrder> buyOrdersSortedList = new SortedList<MarketOrder>(buyOrdersEventList);
		//Table Model
		sellOrdersTableModel = new EventTableModel<MarketOrder>(sellOrdersSortedList, sellTableFormat);
		buyOrdersTableModel = new EventTableModel<MarketOrder>(buyOrdersSortedList, buyTableFormat);
		//Tables
		jSellOrders = new JAutoColumnTable(sellOrdersTableModel, sellTableFormat.getColumnNames());
		jBuyOrders = new JAutoColumnTable(buyOrdersTableModel, buyTableFormat.getColumnNames());
		//Table Selection
		EventSelectionModel<MarketOrder> sellSelectionModel = new EventSelectionModel<MarketOrder>(sellOrdersEventList);
		sellSelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jSellOrders.setSelectionModel(sellSelectionModel);
		EventSelectionModel<MarketOrder> buySelectionModel = new EventSelectionModel<MarketOrder>(buyOrdersEventList);
		buySelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jBuyOrders.setSelectionModel(buySelectionModel);
		//Listeners
		installTableMenu(jSellOrders);
		installTableMenu(jBuyOrders);
		//Sorters
		TableComparatorChooser.install(jSellOrders, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyOrders, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jCharactersLabel = new JLabel("Character");
		JLabel jStateLabel = new JLabel("State");
		JLabel jSellLabel = new JLabel("Sell Orders");
		JLabel jBuyLabel = new JLabel("Buy Orders");
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

		jMenuItem = new JMenuItem("Sell Orders:");
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, sellMarketOrder));
		jComponent.add(new JMenuLookup(program, sellMarketOrder));

		addSeparator(jComponent);

		jMenuItem = new JMenuItem("Buy Orders:");
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, buyMarketOrder));
		jComponent.add(new JMenuLookup(program, buyMarketOrder));
	}

	@Override
	public void updateData() {
		Vector<String> characters = new Vector<String>();
		orders = new HashMap<String, List<MarketOrder>>();
		all = new ArrayList<MarketOrder>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
				orders.put(human.getName(), marketOrders);
				if (human.isShowAssets()){
					characters.add(human.getName());
					List<MarketOrder> characterMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrders(), program.getSettings());
					orders.put(human.getName(), characterMarketOrders);
					all.addAll(characterMarketOrders);
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						if (!characters.contains(corpKey)){
							characters.add(corpKey);
							orders.put(corpKey, new ArrayList<MarketOrder>());
						}
						List<MarketOrder> corporationMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrdersCorporation(), program.getSettings());
						orders.get(corpKey).addAll(corporationMarketOrders);
						all.addAll(corporationMarketOrders);
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jState.setEnabled(true);
			jSellOrders.setEnabled(true);
			jBuyOrders.setEnabled(true);
			Collections.sort(characters);
			characterCustomDialog.updateList(new ArrayList<String>(characters));
			characters.add(0, ALL);
			characters.add(CUSTOM);
			jCharacters.setModel( new DefaultComboBoxModel(characters));
			stateCustomDialog.updateList(new ArrayList<String>(Arrays.asList(new String[]{"Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending"})));
			jState.setModel( new DefaultComboBoxModel(new String[]{ALL, "Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending", CUSTOM}));
			jCharacters.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			jSellOrders.setEnabled(false);
			jBuyOrders.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem("No character found");
			jState.setModel( new DefaultComboBoxModel());
			jState.getModel().setSelectedItem("No character found");
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CHARACTER_SELECTED.equals(e.getActionCommand())) {
			String character = (String) jCharacters.getSelectedItem();
			if (character.equals(CUSTOM)){
				characterCustomDialog.show(new CharacterListener());
			} else {
				selectedCharacters = Collections.singletonList(character);
				updateTable();
			}
		}
		if (ACTION_STATE_SELECTED.equals(e.getActionCommand())) {
			String state = (String) jState.getSelectedItem();
			if (state.equals(CUSTOM)){
				stateCustomDialog.show(new StatsListener());
			} else {
				selectedStats = Collections.singletonList(state);
				updateTable();
			}
		}
	}

	private void updateTable(){
			if (jCharacters.getItemCount() > 2 && selectedCharacters != null && selectedStats != null ){
				List<MarketOrder> marketOrders;
				List<MarketOrder> sellMarketOrders = new ArrayList<MarketOrder>();
				List<MarketOrder> buyMarketOrders = new ArrayList<MarketOrder>();
				if (selectedCharacters.contains(ALL)){
					marketOrders = all;
				} else {
					marketOrders = new ArrayList<MarketOrder>();
					for (String s : selectedCharacters){
						marketOrders.addAll(orders.get(s));
					}
				}
				for (int a = 0; a < marketOrders.size(); a++){
					MarketOrder marketOrder = marketOrders.get(a);
					if (selectedStats.contains(marketOrder.getStatus()) || selectedStats.contains(ALL)){
						if (marketOrder.getBid() < 1){
							sellMarketOrders.add(marketOrder);
						} else {
							buyMarketOrders.add(marketOrder);
						}
					}

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
	}

	public class CharacterListener implements CustomDialogInterface{

		@Override
		public void customDialogReady(List<String> list) {
			selectedCharacters = list;
			updateTable();
		}

	}

	public class StatsListener implements CustomDialogInterface{

		@Override
		public void customDialogReady(List<String> list) {
			selectedStats = list;
			updateTable();
		}

	}
}

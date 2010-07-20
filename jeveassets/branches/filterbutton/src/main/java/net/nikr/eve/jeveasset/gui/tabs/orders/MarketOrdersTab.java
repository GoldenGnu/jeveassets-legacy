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
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JFilterButton;
import net.nikr.eve.jeveasset.gui.shared.JMenuTools;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab implements ActionListener, MouseListener {

	private JFilterButton jCharacters;
	private JFilterButton jState;
	private EventTableModel<MarketOrder> sellOrdersTableModel;
	private EventTableModel<MarketOrder> buyOrdersTableModel;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;

	private JAutoColumnTable jSellOrders;
	private JAutoColumnTable jBuyOrders;

	private String[] orderStates = new String[]{"Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending"};

	public MarketOrdersTab(Program program) {
		super(program, "Market Orders", Images.ICON_TOOL_MARKET_ORDERS, true);

		jCharacters = new JFilterButton("Characters", this);
		jState = new JFilterButton("States", this);

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
		jSellOrders.addMouseListener(this);
		jBuyOrders = new JAutoColumnTable(buyOrdersTableModel, buyTableFormat.getColumnNames());
		jBuyOrders.addMouseListener(this);
		//Sorters
		TableComparatorChooser.install(jSellOrders, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyOrders, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jSellLabel = new JLabel("Sell Orders");
		JLabel jBuyLabel = new JLabel("Buy Orders");
		//Scroll Panels
		JScrollPane jSellOrdersScrollPanel = jSellOrders.getScrollPanel();
		JScrollPane jBuyOrdersScrollPanel = jBuyOrders.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellLabel)
					.addComponent(jBuyLabel)
				)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jCharacters, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
							.addComponent(jState, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						)
						.addComponent(jSellOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
					)
					.addComponent(jBuyOrdersScrollPanel, 0, 0, Short.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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

	private void showPopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();
		MarketOrder marketOrder = null;
		if (jSellOrders.equals(e.getSource())){
			jSellOrders.setRowSelectionInterval(jSellOrders.rowAtPoint(e.getPoint()), jSellOrders.rowAtPoint(e.getPoint()));
			jSellOrders.setColumnSelectionInterval(0, jSellOrders.getColumnCount()-1);
			int index = jSellOrders.getSelectedRow();
			marketOrder = sellOrdersTableModel.getElementAt(index);
		}
		if (jBuyOrders.equals(e.getSource())){
			jBuyOrders.setRowSelectionInterval(jBuyOrders.rowAtPoint(e.getPoint()), jBuyOrders.rowAtPoint(e.getPoint()));
			jBuyOrders.setColumnSelectionInterval(0, jBuyOrders.getColumnCount()-1);
			int index = jBuyOrders.getSelectedRow();
			marketOrder = buyOrdersTableModel.getElementAt(index);
			
		}
		if (marketOrder != null){
			jTablePopupMenu.add(JMenuTools.getAssetFilterMenu(program, marketOrder));
			jTablePopupMenu.add(JMenuTools.getLookupMenu(program, marketOrder));
		}
		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateData() {
		List<String> characters = new ArrayList<String>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (Account account : accounts){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					characters.add(human.getName());
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						if (!characters.contains(corpKey)){
							characters.add(corpKey);
						}
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
			jCharacters.setData(characters, true);
			jState.setData(orderStates, true);
			jCharacters.setEnabled(true);
			jState.setEnabled(true);
		} else {
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			jSellOrders.setEnabled(false);
			jBuyOrders.setEnabled(false);
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
		updateList();
	}

	private void updateList(){
		List<String> states = jState.getData();
		List<String> characters = jCharacters.getData();
		List<MarketOrder> sellOrders = new ArrayList<MarketOrder>();
		List<MarketOrder> buyOrders = new ArrayList<MarketOrder>();

		List<Account> accounts = program.getSettings().getAccounts();
		for (Account account : accounts){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					if (characters.contains(human.getName())){
						List<MarketOrder> characterMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrders(), program.getSettings());
						for (MarketOrder marketOrder : characterMarketOrders){
							if (states.contains(marketOrder.getStatus())){
								if (marketOrder.getBid() < 1){
									sellOrders.add(marketOrder);
								} else {
									buyOrders.add(marketOrder);
								}
							}
						}
					}
					String corpKey = "["+human.getCorporation()+"]";
					if (human.isUpdateCorporationAssets() && characters.contains(corpKey)){
						List<MarketOrder> corporationMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrdersCorporation(), program.getSettings());
						for (MarketOrder marketOrder : corporationMarketOrders){
							if (states.contains(marketOrder.getStatus())){
								if (marketOrder.getBid() < 1){
									sellOrders.add(marketOrder);
								} else {
									buyOrders.add(marketOrder);
								}
							}
						}
					}
				}
			}
		}
		sellOrdersEventList.getReadWriteLock().writeLock().lock();
		sellOrdersEventList.clear();
		sellOrdersEventList.addAll( sellOrders );
		sellOrdersEventList.getReadWriteLock().writeLock().unlock();

		buyOrdersEventList.getReadWriteLock().writeLock().lock();
		buyOrdersEventList.clear();
		buyOrdersEventList.addAll( buyOrders );
		buyOrdersEventList.getReadWriteLock().writeLock().unlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (JFilterButton.ACTION_CHANGED.equals(e.getActionCommand())) {
			updateList();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()){
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()){
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}

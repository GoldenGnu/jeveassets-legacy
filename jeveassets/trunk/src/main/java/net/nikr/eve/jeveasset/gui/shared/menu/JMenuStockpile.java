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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuStockpile<T>  extends JMenuTool<T> implements ActionListener {

	private static final String ACTION_ADD_TO_EXISTING = "ACTION_ADD_TO_EXISTING";
	private static final String ACTION_ADD_TO_NEW = "ACTION_ADD_TO_NEW";
	private static final int DEFAULT_ADD_COUNT = 0;

	public JMenuStockpile(final Program program, final List<T> items) {
		super(GuiShared.get().stockpile(), program, items); //
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().newStockpile());
		jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
		jMenuItem.setEnabled(!typeIDs.isEmpty());
		jMenuItem.setActionCommand(ACTION_ADD_TO_NEW);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		if (!program.getSettings().getStockpiles().isEmpty()) {
			this.addSeparator();
		}
		List<Stockpile> stockpiles = program.getSettings().getStockpiles();
		Collections.sort(stockpiles);
		for (Stockpile stockpile : stockpiles) {
			jMenuItem = new JStockpileMenu(stockpile);
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setEnabled(!typeIDs.isEmpty());
			jMenuItem.setActionCommand(ACTION_ADD_TO_EXISTING);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_ADD_TO_NEW.equals(e.getActionCommand())) {
			Stockpile stockpile = program.getStockpileTool().showAddStockpile();
			if (stockpile != null) {
				for (int typeID : typeIDs) {
					Item item = program.getSettings().getItems().get(typeID);
					StockpileItem stockpileItem = new StockpileItem(stockpile, item.getName(), item.getGroup(), item.getTypeID(), DEFAULT_ADD_COUNT);
					stockpile.add(stockpileItem);
				}
				program.getStockpileTool().updateData();
				program.getMainWindow().addTab(program.getStockpileTool(), program.getSettings().isStockpileFocusTab());
			}
		}
		if (ACTION_ADD_TO_EXISTING.equals(e.getActionCommand())) {
			Object source = e.getSource();
			if (source instanceof JStockpileMenu) {
				JStockpileMenu jStockpileMenu = (JStockpileMenu) source;
				Stockpile stockpile = jStockpileMenu.getStockpile();
				for (int typeID : typeIDs) {
					Item item = program.getSettings().getItems().get(typeID);
					StockpileItem stockpileItem = new StockpileItem(stockpile, item.getName(), item.getGroup(), item.getTypeID(), DEFAULT_ADD_COUNT);
					stockpile.add(stockpileItem);
				}
				program.getStockpileTool().updateData();
				program.getMainWindow().addTab(program.getStockpileTool(), program.getSettings().isStockpileFocusTab());
			}
		}
	}

	public static class JStockpileMenu extends JMenuItem {

		private Stockpile stockpile;

		public JStockpileMenu(final Stockpile stockpile) {
			super(stockpile.getName() + "...");
			this.stockpile = stockpile;
		}

		public Stockpile getStockpile() {
			return stockpile;
		}
	}

}
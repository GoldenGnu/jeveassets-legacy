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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.i18n.GuiShared;

/**
 *
 * @author Niklas
 */
public class JMenuStockpile  extends JMenuTool implements ActionListener {

	public final static String ACTION_ADD_STOCKPILE_ITEM = "ACTION_ADD_STOCKPILE_ITEM";
	public final static String ACTION_ADD_STOCKPILE = "ACTION_ADD_STOCKPILE";
	
	private Asset asset = null;
	
	public JMenuStockpile(Program program, Object object) {
		super(GuiShared.get().stockpile(), program, object); //
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		JMenuItem jMenuItem;
		JMenu jMenu;
		
		if (object instanceof Asset){
			asset = (Asset) object;
			jMenuItem = new JMenuItem(GuiShared.get().addStockpile());
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setEnabled(typeId != 0);
			jMenuItem.setActionCommand(ACTION_ADD_STOCKPILE);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
			this.addSeparator();
		}
		
		jMenu = new JMenu(GuiShared.get().addStockpileItem());
		jMenu.setIcon(Images.EDIT_ADD.getIcon());
		jMenu.setEnabled(typeId != 0);
		add(jMenu);
		
		for (Stockpile stockpile : program.getSettings().getStockpiles()){
			jMenuItem = new JStockpileMenu(stockpile);
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setActionCommand(ACTION_ADD_STOCKPILE_ITEM);
			jMenuItem.addActionListener(this);
			jMenu.add(jMenuItem);
		}

		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_STOCKPILE.equals(e.getActionCommand())) {
			boolean updated = program.getStockpileTool().showAddStockpile(asset);
			if (updated) {
				program.getMainWindow().addTab(program.getStockpileTool());
			}
		}
		if (ACTION_ADD_STOCKPILE_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenu){
				JStockpileMenu jStockpileMenu = (JStockpileMenu) source;
				Stockpile stockpile = jStockpileMenu.getStockpile();
				boolean updated = program.getStockpileTool().showAddItem(stockpile, typeId);
				if (updated) { 
					program.getMainWindow().addTab(program.getStockpileTool());
				}
			}
		}
	}
	
	public static class JStockpileMenu extends JMenuItem{

		private Stockpile stockpile;
		
		public JStockpileMenu(Stockpile stockpile) {
			super(stockpile.getName());
			this.stockpile = stockpile;
		}

		public Stockpile getStockpile() {
			return stockpile;
		}
	} 
	
}

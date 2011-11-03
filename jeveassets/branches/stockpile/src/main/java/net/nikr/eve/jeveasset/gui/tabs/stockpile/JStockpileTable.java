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

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;


public class JStockpileTable extends JSeparatorTable{

	private EventTableModel tableModel;
	
	public JStockpileTable(EventTableModel tableModel) {
		super(tableModel);
		this.tableModel = tableModel;
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		component.setForeground(isSelected ? this.getSelectionForeground() : Color.BLACK);
		if (object instanceof StockpileTotal){
			StockpileTotal stockpileTotal = (StockpileTotal) object;
			if (columnName.equals("Name")){ //TODO no translation
				component.setForeground(Color.BLACK);
				if (stockpileTotal.isOK()){
					component.setBackground( new Color(200,255,200) );
				} else {
					component.setBackground( new Color(255,200,200) );
				}
			} else if (isSelected){
				component.setBackground( this.getSelectionBackground().darker() );
			} else {
				component.setBackground( new Color(255,255,200) );
			}
			if (columnName.contains("Needed") && !stockpileTotal.isOK()){ //TODO no translation
				 component.setForeground(Color.RED.darker());
			}
		} else if ( (object instanceof StockpileItem) && columnName.equals("Name") ){ //TODO no translation
			StockpileItem stockpileItem = (StockpileItem) object;
			component.setForeground(Color.BLACK);
			if (stockpileItem.isOK()){
				component.setBackground( new Color(200,255,200) );
				
			} else {
				component.setBackground( new Color(255,200,200) );
			}
		} else if ( (object instanceof StockpileItem) && columnName.contains("Needed") ){ //TODO no translation
			StockpileItem stockpileItem = (StockpileItem) object;
			if (!stockpileItem.isOK()) component.setForeground(Color.RED.darker());
		} else if (object instanceof SeparatorList.Separator){
			//Do nothing
		} else {
			if (isSelected){
				component.setBackground(this.getSelectionBackground());
			} else {
				component.setBackground(Color.WHITE);
			}
		}
		return component;
	}
	
}

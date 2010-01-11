/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.table;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.table.CellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.table.CellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.table.CellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.table.CellRenderers.IntegerCellRenderer;


public class JAssetTable extends JTable {

	private EventTableModel<EveAsset> eveAssetTableModel;
	private DoubleCellRenderer doubleCellRenderer;
	private LongCellRenderer longCellRenderer;
	private TableCellRenderer tableCellRenderer;
	private IntegerCellRenderer integerCellRenderer;
	private FloatCellRenderer floatCellRenderer;

	private Program program;

	public JAssetTable(Program program, EventTableModel<EveAsset> eveAssetTableModel) {
		this.program = program;
		this.eveAssetTableModel = eveAssetTableModel;
		this.setModel(eveAssetTableModel);
		doubleCellRenderer = new DoubleCellRenderer();
		longCellRenderer = new LongCellRenderer();
		integerCellRenderer = new IntegerCellRenderer();
		floatCellRenderer = new FloatCellRenderer();
		tableCellRenderer = new DefaultTableCellRenderer();
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Object value = getValueAt(row, column);

        boolean isSelected = false;
        boolean hasFocus = false;

        // Only indicate the selection and focused cell if not printing
        if (!isPaintingForPrint()) {
            isSelected = isCellSelected(row, column);

            boolean rowIsLead =
                (selectionModel.getLeadSelectionIndex() == row);
            boolean colIsLead =
                (columnModel.getSelectionModel().getLeadSelectionIndex() == column);

            hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
        }

		
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		if (eveAssetTableModel.getRowCount() >= row){
			EveAsset eveAsset = eveAssetTableModel.getElementAt(row);
			//User set price
			if (eveAsset.isUserPrice() && (eveAsset.isBpo()|| !eveAsset.isBlueprint()) && columnName.equals("Price")){
				Component c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(230,230,230) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			//Blueprint Original
			if (eveAsset.isBpo()
					&& eveAsset.isBlueprint()
					&& (columnName.equals("Price")
					|| columnName.equals("Sell Min")
					|| columnName.equals("Buy Max")
					|| columnName.equals("Name"))){
				Component c;
				if (columnName.equals("Name")){
					c = tableCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				} else {
					c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				}
				if (!isSelected){
					c.setBackground( new Color(255,255,230) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			//Reproccessed is greater then price
			if (eveAsset.getPriceReprocessed() > eveAsset.getPrice() && columnName.equals("Reprocessed")){
				Component c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (!isSelected){
					c.setBackground( new Color(255,255,230) );
				} else {
					c.setBackground( this.getSelectionBackground().darker() );
				}
				return c;
			}
			//Selected row highlighting
			if (this.isRowSelected(row) && !isSelected && program.getSettings().isHighlightSelectedRows()){
				//Component c = tableCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				//if (value instanceof Double) c = doubleCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				//if (value instanceof Long) c = longCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				Component c = getCellRenderer(row, column).getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (value instanceof Integer) c = integerCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				if (value instanceof Float) c = floatCellRenderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
				c.setBackground( new Color(230,230,255) );
				return c;
			}
		}
		return super.prepareRenderer(renderer, row, column);
	}

	
}

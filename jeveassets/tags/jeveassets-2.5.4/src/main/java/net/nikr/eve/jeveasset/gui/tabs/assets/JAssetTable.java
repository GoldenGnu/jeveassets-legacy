/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JAssetTable extends JAutoColumnTable {

	private EventTableModel<Asset> tableModel;

	public JAssetTable(final Program program, final EventTableModel<Asset> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Asset asset = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		//User set price
		if (asset.isUserPrice() && columnName.equals(EveAssetTableFormat.PRICE.getColumnName())) {
			if (!isSelected) {
				component.setBackground(new Color(230, 230, 230));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		//User set name
		if (asset.isUserName() && columnName.equals(EveAssetTableFormat.NAME.getColumnName())) {
			if (!isSelected) {
				component.setBackground(new Color(230, 230, 230));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		//Blueprint Original
		if (asset.isBpo()
				&& asset.isBlueprint()
				&& (columnName.equals(EveAssetTableFormat.PRICE.getColumnName())
				|| columnName.equals(EveAssetTableFormat.PRICE_SELL_MIN.getColumnName())
				|| columnName.equals(EveAssetTableFormat.PRICE_BUY_MAX.getColumnName())
				|| columnName.equals(EveAssetTableFormat.NAME.getColumnName()))) {
			if (!isSelected) {
				component.setBackground(new Color(255, 255, 200));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}

		//Reproccessing Colors
		if (program.getSettings().isReprocessColors() && !isSelected) {
			//Zero price (White)
			if (asset.getPriceReprocessed() == 0 || asset.getPrice() == 0) {
				return component;
			}
			//Equal price (Yellow)
			if (asset.getPriceReprocessed() == asset.getPrice()) {
				if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()) {
					component.setBackground(new Color(255, 255, 160));
				} else {
					component.setBackground(new Color(255, 255, 200));
				}
				return component;
			}
			//Reprocessed highest (Red)
			if (asset.getPriceReprocessed() > asset.getPrice()) {
				if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()) {
					component.setBackground(new Color(255, 160, 160));
				} else {
					component.setBackground(new Color(255, 200, 200));
				}
				return component;
			}
			//Price highest (Green)
			if (asset.getPriceReprocessed() < asset.getPrice()) {
				if (this.isRowSelected(row) && program.getSettings().isHighlightSelectedRows()) {
					component.setBackground(new Color(160, 255, 160));
				} else {
					component.setBackground(new Color(200, 255, 200));
				}
				return component;
			}
		}

		//Reproccessed is greater then price
		if (asset.getPriceReprocessed() > asset.getPrice() && columnName.equals(EveAssetTableFormat.PRICE_REPROCESSED.getColumnName())) {
			if (!isSelected) {
				component.setBackground(new Color(255, 255, 200));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		return component;
	}
}

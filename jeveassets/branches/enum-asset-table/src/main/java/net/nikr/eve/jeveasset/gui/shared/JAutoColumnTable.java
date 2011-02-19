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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.data.ISK;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.data.Module.ModulePriceValue;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.FloatCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.IntegerCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.NumberToStringCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;


public class JAutoColumnTable extends JTable {

	private JScrollPane jScroll;
	private EnumTableFormatAdaptor formatAdaptor;
	private AbstractTableModel model;

	public JAutoColumnTable(AbstractTableModel model, EnumTableFormatAdaptor formatAdaptor) {
		super(model);
		this.model = model;
		this.formatAdaptor = formatAdaptor;

		//Scroll
		jScroll = new JScrollPane(this);

		//Listeners
		ListenerClass listener = new ListenerClass(this, jScroll);

		//Renders
		this.setDefaultRenderer(Float.class, new FloatCellRenderer());
		this.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		this.setDefaultRenderer(Long.class, new LongCellRenderer());
		this.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
		this.setDefaultRenderer(Quantity.class, new NumberToStringCellRenderer());
		this.setDefaultRenderer(ISK.class, new NumberToStringCellRenderer());
		this.setDefaultRenderer(ModulePriceValue.class, new NumberToStringCellRenderer());
	}

	public JScrollPane getScrollPanel() {
		return jScroll;
	}


	protected void resizeColumnsText(JTable jTable, JScrollPane jScroll) {
		if (jTable.getRowCount() > 0){
			int size = 0;
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int i = 0; i < jTable.getColumnCount(); i++) {
				 size = size+resizeColumn(jTable, jTable.getColumnModel().getColumn(i), i);
			}
			if (size < jScroll.getSize().width){
				jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			}
		} else {
			for (int i = 0; i < jTable.getColumnCount(); i++) {
				jTable.getColumnModel().getColumn(i).setPreferredWidth(75);
			}
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
	}

	protected void resizeColumnsWindow(JTable jTable) {
		for (int a = 0; a < jTable.getColumnCount(); a++){
			jTable.getColumnModel().getColumn(a).setPreferredWidth(75);
		}
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	protected int resizeColumn(JTable jTable, TableColumn column, int columnIndex) {
		int maxWidth = 0;
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) {
			renderer = jTable.getTableHeader().getDefaultRenderer();
		}
		Component component = renderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, 0);
		maxWidth = component.getPreferredSize().width;
		for (int a = 0; a < jTable.getRowCount(); a++){
			renderer = jTable.getCellRenderer(a, columnIndex);
			if (renderer instanceof SeparatorTableCell) continue;
			component = renderer.getTableCellRendererComponent(jTable, jTable.getValueAt(a, columnIndex), false, false, a, columnIndex);
			maxWidth = Math.max(maxWidth, component.getPreferredSize().width);
		}
		column.setPreferredWidth(maxWidth+4);
		return maxWidth+4;
	}

	private class ListenerClass implements TableModelListener, ComponentListener, PropertyChangeListener, TableColumnModelListener, MouseListener{

		private boolean columnMoved = false;
		private int from = 0;
		private int to = 0;

		public ListenerClass(JTable jTable, JScrollPane jScroll) {
			jTable.getModel().addTableModelListener(this);
			jTable.addPropertyChangeListener("model", this);

			jTable.getColumnModel().addColumnModelListener(this);
			jTable.addPropertyChangeListener("columnModel", this);


			jTable.getTableHeader().addMouseListener(this);
			jTable.addPropertyChangeListener("tableHeader", this);

			jScroll.addComponentListener(this);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE)
			autoResizeColumns();
		}

		@Override
		public void componentResized(ComponentEvent e) {
			autoResizeColumns();
		}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}

		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			//TableHeader mouse listener
			if (evt.getPropertyName().equals("tableHeader")){
				Object o = evt.getNewValue();
				if (o instanceof JTableHeader){
					JTableHeader header = (JTableHeader) o;
					header.addMouseListener(this);
				}
			}
			//Column model
			if (evt.getPropertyName().equals("columnModel")){
				Object o = evt.getNewValue();
				if (o instanceof TableColumnModel){
					TableColumnModel model = (TableColumnModel) o;
					model.addColumnModelListener(this);
				}
			}
			//Table model
			if (evt.getPropertyName().equals("model")){
				Object o = evt.getNewValue();
				if (o instanceof AbstractTableModel){
					TableModel model = (TableModel) o;
					model.addTableModelListener(this);
				} else {
					throw new IllegalArgumentException("Cannot set a TableModel that does not extend AbstractTableModel");
				}
			}

		}

		@Override
		public void columnAdded(TableColumnModelEvent e) {}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			if (e.getFromIndex() != e.getToIndex()){
				if (!columnMoved) from = e.getFromIndex();
				to = e.getToIndex();
				columnMoved = true;
			}
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			columnMoved = false;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (columnMoved){
				columnMoved = false;
				movedColumn(from, to);
				autoResizeColumns();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

	}

	public void autoResizeColumns() {
		resizeColumnsText(this, jScroll);
	}

	public void movedColumn(int from, int to){
		formatAdaptor.moveColumn(from, to);
		model.fireTableStructureChanged();
	}
}
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.data.TableSettings;
import net.nikr.eve.jeveasset.data.TableSettings.ResizeMode;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JColumnTable<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JAutoColumnTable {

	private final static String ACTION_AUTO_RESIZING_COLUMNS_TEXT = "ACTION_AUTO_RESIZING_COLUMNS_TEXT";
	private final static String ACTION_AUTO_RESIZING_COLUMNS_WINDOW = "ACTION_AUTO_RESIZING_COLUMNS_WINDOW";
	private final static String ACTION_DISABLE_AUTO_RESIZING_COLUMNS = "ACTION_DISABLE_AUTO_RESIZING_COLUMNS";
	private final static String ACTION_RESET_COLUMNS_TO_DEFAULT = "ACTION_SHOW_ALL_COLUMNS";

	private JScrollPane jTableScroll;
	private JDropDownButton jColumnsSelection;
	private JMenu jColumnMenu;
	private EnumTableFormatAdaptor<T, Q> formatAdaptor;


	private List<ColumnTableListener> listeners = new ArrayList<ColumnTableListener>();
	private ListenerClass listenerClass = new ListenerClass();

	private TableSettings<T, Q> columnTableSettings;

	public JColumnTable(AbstractTableModel abstractTableModel, TableSettings<T, Q> columnTableSettings, EnumTableFormatAdaptor<T, Q> formatAdaptor) {
		super(abstractTableModel, formatAdaptor);
		this.columnTableSettings = columnTableSettings;
		this.formatAdaptor = formatAdaptor;
		
		formatAdaptor.setTableSettings(columnTableSettings);
		

		//Listeners
		abstractTableModel.addTableModelListener(listenerClass);
		this.addPropertyChangeListener("model", listenerClass);

		//Table Button
		jColumnsSelection = new JDropDownButton(JDropDownButton.RIGHT);
		jColumnsSelection.setIcon(Images.ICON_ARROW_DOWN);
		jColumnsSelection.setHorizontalAlignment(SwingConstants.RIGHT);
		jColumnsSelection.setBorder(null);

		//Table Menu
		jColumnMenu = new JMenu(GuiShared.get().columns());
		jColumnMenu.setIcon(Images.ICON_TABLE_SHOW);

		//Table Scrollpanel
		jTableScroll = new JScrollPane(this);
		jTableScroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, jColumnsSelection);
		jTableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jTableScroll.setAutoscrolls(true);
		jTableScroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,0,0,0), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)) );

		tableUpdateColumnMenus();
		tableUpdateCoulmnsSize();
	}

	public AbstractTableModel getAbstractTableModel(){
		TableModel tableModel = getModel();
		if (tableModel instanceof AbstractTableModel){
			return (AbstractTableModel) tableModel;
		}
		return null;
	}

	public TableSettings getColumnTableSettings() {
		return columnTableSettings;
	}

	public void addColumnTableListener(ColumnTableListener listener){
		listeners.add(listener);
	}

	public void removeColumnTableListener(ColumnTableListener listener){
		listeners.remove(listener);
	}

	protected void tableUpdate(){
		for (ColumnTableListener listener : listeners){
			listener.tableUpdate();
		}
	}

	public JMenu getMenu() {
		return jColumnMenu;
	}

	public JScrollPane getScroll() {
		return jTableScroll;
	}

	private void tableUpdateCoulmnsSize(){
		if (columnTableSettings.getMode().equals(ResizeMode.TEXT)){
			resizeColumnsText(this, jTableScroll);
		}
		if (columnTableSettings.getMode().equals(ResizeMode.WINDOW)){
			resizeColumnsWindow(this);
		}
		if (!columnTableSettings.getMode().equals(ResizeMode.TEXT) && !columnTableSettings.getMode().equals(ResizeMode.WINDOW)){
			this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private void tableUpdateTableStructure(){
		if (columnTableSettings.getMode().equals(ResizeMode.TEXT)){
			getAbstractTableModel().fireTableStructureChanged();
			tableUpdateCoulmnsSize();
		} else {
			Map<String, Integer> widths = new HashMap<String, Integer>();
			for (int a = 0; a < this.getColumnCount(); a++){
				int width = this.getColumnModel().getColumn(a).getPreferredWidth();
				String name = (String)this.getColumnModel().getColumn(a).getHeaderValue();
				widths.put(name, width);
			}
			getAbstractTableModel().fireTableStructureChanged();
			for (int a = 0; a < this.getColumnCount(); a++){
				String name = (String)this.getColumnModel().getColumn(a).getHeaderValue();
				if (widths.containsKey(name)){
					int width = widths.get(name);
					this.getColumnModel().getColumn(a).setPreferredWidth(width);
				} else {
					resizeColumn(this, this.getColumnModel().getColumn(a), a);
				}
			}
		}
	}
	
	private void tableUpdateColumnMenus(){
		tableUpdateColumnMenu(jColumnsSelection);
		tableUpdateColumnMenu(jColumnMenu);
	}

	private void tableUpdateColumnMenu(JComponent jComponent){
		jComponent.removeAll();

		JCheckBoxMenuItem jCheckBoxMenuItem;
		JRadioButtonMenuItem jRadioButtonMenuItem;
		JMenuItem  jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().reset());
		jMenuItem.setActionCommand(ACTION_RESET_COLUMNS_TO_DEFAULT);
		jMenuItem.addActionListener(listenerClass);
		jComponent.add(jMenuItem);

		addSeparator(jComponent);

		ButtonGroup group = new ButtonGroup();

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().autoText());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_TEXT);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(columnTableSettings.getMode().equals(ResizeMode.TEXT));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().autoWindow());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_WINDOW);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(columnTableSettings.getMode().equals(ResizeMode.WINDOW));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().disable());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_DISABLE_AUTO_RESIZING_COLUMNS);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(!columnTableSettings.getMode().equals(ResizeMode.TEXT) && !columnTableSettings.getMode().equals(ResizeMode.WINDOW));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		addSeparator(jComponent);
		for (T column : columnTableSettings.getTableColumnNames()){
			String columnName = column.getColumnName();
			jCheckBoxMenuItem = new JCheckBoxMenuItem(columnName);
			jCheckBoxMenuItem.setActionCommand(column.toString());
			jCheckBoxMenuItem.addActionListener(listenerClass);
			jCheckBoxMenuItem.setIcon(Images.ICON_TABLE_SHOW);
			jCheckBoxMenuItem.setSelected(columnTableSettings.getTableColumnVisible().contains(column));
			jComponent.add(jCheckBoxMenuItem);
		}
	}

	private void addSeparator(JComponent jComponent){
		if (jComponent instanceof JMenu){
			JMenu jMenu = (JMenu) jComponent;
			jMenu.addSeparator();
		}
		if (jComponent instanceof JPopupMenu){
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;
			jPopupMenu.addSeparator();
		}
		if (jComponent instanceof JDropDownButton){
			JDropDownButton jDropDownButton = (JDropDownButton) jComponent;
			jDropDownButton.addSeparator();
		}
	}

	private class ListenerClass implements ActionListener, TableModelListener,
			PropertyChangeListener{

		//Data
		private int rowsLastTime = 0;
		private int rowsCount = 0;


		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_RESET_COLUMNS_TO_DEFAULT.equals(e.getActionCommand())){
				formatAdaptor.resetColumns();
				tableUpdateTableStructure();
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_AUTO_RESIZING_COLUMNS_TEXT.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.TEXT);
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_AUTO_RESIZING_COLUMNS_WINDOW.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.WINDOW);
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_DISABLE_AUTO_RESIZING_COLUMNS.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.NONE);
				for (int a = 0; a < getColumnCount(); a++){
					int width = getColumnModel().getColumn(a).getWidth();
					getColumnModel().getColumn(a).setPreferredWidth(width);
				}
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			//Hide/show column
			if (e.getSource() instanceof JCheckBoxMenuItem){
				T column = columnTableSettings.getColumn(e.getActionCommand());
				if (columnTableSettings.getTableColumnVisible().contains(column)){
					formatAdaptor.hideColumn(column);
				} else {
					formatAdaptor.showColumn(column);
				}
				//FIXME JColumnTable hide/show column
				getAbstractTableModel().fireTableStructureChanged();
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) { //Filter
			if (e.getType() == TableModelEvent.DELETE) rowsCount = rowsCount - (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (e.getType() == TableModelEvent.INSERT) rowsCount = rowsCount + (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (Math.abs(rowsLastTime + rowsCount) == getRowCount()
					&& e.getType() != TableModelEvent.UPDATE){ //Last Table Update
				rowsLastTime = getRowCount();
				rowsCount = 0;
				tableUpdateCoulmnsSize();
				tableUpdate();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("model")){
				Object o = evt.getNewValue();
				if (o instanceof AbstractTableModel){
					TableModel model = (TableModel) o;
					model.addTableModelListener(listenerClass);
				} else {
					throw new IllegalArgumentException("Cannot set a TableModel that does not extend AbstractTableModel");
				}
			}
		}

		
	}

	@Override
	public void autoResizeColumns() {
		tableUpdateCoulmnsSize();
	}

	@Override
	public void movedColumn(int from, int to) {
		formatAdaptor.moveColumn(from, to);
		tableUpdateTableStructure();
		tableUpdateColumnMenus();
	}

	public interface ColumnTableListener {
		public void tableUpdate();
	}
}

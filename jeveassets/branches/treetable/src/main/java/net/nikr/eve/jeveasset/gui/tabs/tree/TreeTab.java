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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TreeNodeData;
import ca.odell.glazedlists.swing.TreeTableCellEditor;
import ca.odell.glazedlists.swing.TreeTableCellRenderer;
import ca.odell.glazedlists.swing.TreeTableSupport;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset.TreeType;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public class TreeTab extends JMainTab implements TableMenu<TreeAsset> {

	private static final String ACTION_UPDATE = "ACTION_UPDATE";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	private final int INDENT = 10;

	//GUI
	private JTreeTable jTable;
	private JLabel jValue;
	private JLabel jReprocessed;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;
	private JToggleButton jCategories;
	private JToggleButton jLocation;

	//Table
	private DefaultEventTableModel<TreeAsset> tableModel;
	private EventList<TreeAsset> eventList;
	private FilterList<TreeAsset> filterList;
	private TreeList<TreeAsset> treeList;
	private AssetFilterControl filterControl;
	private EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
	private DefaultEventSelectionModel<TreeAsset> selectionModel;
	private AssetTreeExpansionModel expansionModel;
	private List<TreeAsset> locations = new ArrayList<TreeAsset>();
	private List<TreeAsset> categories = new ArrayList<TreeAsset>();

	public static final String NAME = "treeassets"; //Not to be changed!

	public TreeTab(final Program program) {
		//FIXME - - > TreeTable: i18n
		//FIXME - - > TreeTable: need Its own icon?
		super(program, "Tree Assets", Images.TOOL_ASSETS.getIcon(), true);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();
		
		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		ButtonGroup buttonGroup = new ButtonGroup();

		//FIXME - - > TreeTable: i18n
		jCategories = new JToggleButton("Categories", Images.LOC_GROUPS.getIcon());
		jCategories.setActionCommand(ACTION_UPDATE);
		jCategories.addActionListener(listener);
		buttonGroup.add(jCategories);
		addToolButton(jToolBarLeft, jCategories);

		//FIXME - - > TreeTable: i18n
		jLocation = new JToggleButton("Locations", Images.LOC_LOCATIONS.getIcon());
		jLocation.setActionCommand(ACTION_UPDATE);
		jLocation.addActionListener(listener);
		jLocation.setSelected(true);
		buttonGroup.add(jLocation);
		addToolButton(jToolBarLeft, jLocation);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		//FIXME - - > TreeTable: i18n
		JButton jCollapse = new JButton("Collapse", Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);
		addToolButton(jToolBarRight, jCollapse);

		//FIXME - - > TreeTable: i18n
		JButton jExpand = new JButton("Expand", Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);
		addToolButton(jToolBarRight, jExpand);


		//Table Format
		tableFormat = new EnumTableFormatAdaptor<TreeTableFormat, TreeAsset>(TreeTableFormat.class);
		//Backend
		eventList = new BasicEventList<TreeAsset>();
		//Filter
		filterList = new FilterList<TreeAsset>(eventList);
		//Tree
		expansionModel = new AssetTreeExpansionModel();
		treeList = new TreeList<TreeAsset>(filterList, new AssetTreeFormat(), expansionModel);
		treeList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(treeList, tableFormat);
		//Table
		jTable = new JTreeTable(program, tableModel);
		jTable.addMouseListener(listener);
		TreeTableSupport install = TreeTableSupport.install(jTable, treeList, 0);
		install.setEditor(new AssetTreeTableCellEditor(install.getDelegateEditor(), treeList, tableModel, INDENT, 6));
		install.setRenderer(new AssetTreeTableCellRenderer(install.getDelegateRenderer(), treeList, tableModel, INDENT, 6));
		//Selection Model
		selectionModel = EventModels.createSelectionModel(treeList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		//FIXME - - > TreeTable: installTable(...) fail
		//installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new AssetFilterControl(
				program,
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, this, jTable, TreeAsset.class);

		//FIXME - - > TreeTable: i18n
		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolume);

		//FIXME - - > TreeTable: i18n
		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon()); //Add
		this.addStatusbarLabel(jCount);

		//FIXME - - > TreeTable: i18n
		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jAverage);

		//FIXME - - > TreeTable: i18n
		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());
		this.addStatusbarLabel(jReprocessed);

		//FIXME - - > TreeTable: i18n
		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValue);

		final int TOOLBAR_HEIGHT = jToolBarLeft.getInsets().top + jToolBarLeft.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
					.addComponent(jToolBarRight, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton) {
		addToolButton(jToolBar, jButton, 90);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton, final int width) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}

	@Override
	public MenuData<TreeAsset> getMenuData() {
		return new MenuData<TreeAsset>(selectionModel.getSelected());
	}

	@Override
	public JMenu getFilterMenu() {
		return filterControl.getMenu(jTable, tableFormat, selectionModel.getSelected());
	}

	@Override
	public JMenu getColumnMenu() {
		//FIXME - - > TreeTable: Column Menu
		return tableFormat.getMenu(program, tableModel, jTable, NAME, false);
	}

	@Override
	public void addInfoMenu(JComponent jComponent) {
		JMenuInfo.treeAsset(jComponent, selectionModel.getSelected());
	}

	@Override
	public void addToolMenu(JComponent jComponent) { }

	@Override
	public void updateData() {
		//FIXME - - > TreeTable: creating data is very expensive!
		locations.clear();
		categories.clear();
		for (Asset asset : program.getAssetEventList()) {
			locations.add(new TreeAsset(asset, TreeType.LOCATION));
			categories.add(new TreeAsset(asset, TreeType.CATEGORY));
		}
		updateTable();
	}

	public void updateTable() {
		List<TreeAsset> treeAssets = locations;
		if (jCategories.isSelected()) {
			treeAssets = categories;
		}
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(treeAssets);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		for (Asset asset : filterList) {
			totalValue = totalValue + (asset.getDynamicPrice() * asset.getCount()) ;
			totalCount = totalCount + asset.getCount();
			totalVolume = totalVolume + asset.getVolumeTotal();
			totalReprocessed = totalReprocessed + asset.getValueReprocessed();
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		jVolume.setText(Formater.doubleFormat(totalVolume));
		jCount.setText(Formater.itemsFormat(totalCount));
		jAverage.setText(Formater.iskFormat(averageValue));
		jReprocessed.setText(Formater.iskFormat(totalReprocessed));
		jValue.setText(Formater.iskFormat(totalValue));
	}

	public class ListenerClass implements ActionListener, MouseListener, ListEventListener<TreeAsset> {

		private final int WIDTH = UIManager.getIcon("Tree.expandedIcon").getIconWidth();
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_UPDATE.equals(e.getActionCommand())) {
				updateTable();
			} else if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
				expansionModel.setExpande(false);
				updateTable();
			} else if (ACTION_EXPAND.equals(e.getActionCommand())) {
				expansionModel.setExpande(true);
				updateTable();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() % 2 == 0) {
				Component c = jTable.findComponentAt(e.getPoint());
				int row = jTable.rowAtPoint(e.getPoint());
				int depth = treeList.depth(row);
				final int min = INDENT + (depth * WIDTH);
				final int max = min + WIDTH;
				if (e.getPoint().x < min || e.getPoint().x > max) {
					treeList.setExpanded(row, !treeList.isExpanded(row));
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
		
		@Override
		public void listChanged(final ListEvent<TreeAsset> listChanges) {
			updateStatusbar();
			program.getOverviewTab().updateTable();
		}
		
	}

	public static class AssetTreeExpansionModel implements TreeList.ExpansionModel<TreeAsset> {

		private boolean expande = false;
		
		@Override
		public boolean isExpanded(TreeAsset element, List<TreeAsset> path) {
			return expande;
		}

		@Override
		public void setExpanded(TreeAsset element, List<TreeAsset> path, boolean expanded) {
			//FIXME - - > TreeTable: Save Tree expanded state
		}

		public void setExpande(boolean expande) {
			this.expande = expande;
		}
	}

	public static class AssetTreeComparator implements Comparator<TreeAsset> {

		@Override
		public int compare(TreeAsset o1, TreeAsset o2) {
			return o1.getCompare().compareTo(o2.getCompare());
		}
		
	}

	public static class AssetTreeFormat implements TreeList.Format<TreeAsset> {

		@Override
		public void getPath(List<TreeAsset> path, TreeAsset element) {
			path.addAll(element.getLevels());
			path.add(element);
		}

		@Override
		public boolean allowsChildren(TreeAsset element) {
			return true;
		}

		@Override
		public Comparator<? super TreeAsset> getComparator(int depth) {
			return new AssetTreeComparator();
		}
	}

	public static class AssetFilterControl extends FilterControl<TreeAsset> {

		private EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
		private Program program;

		public AssetFilterControl(final Program program, final JFrame jFrame, final EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat, final EventList<TreeAsset> eventList, final FilterList<TreeAsset> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
			this.program = program;
		}

		@Override
		protected Object getColumnValue(final TreeAsset item, final String column) {
			TreeTableFormat format = TreeTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			TreeTableFormat format = (TreeTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			TreeTableFormat format = (TreeTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Enum[] getColumns() {
			return TreeTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return TreeTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getEnumColumns() {
			return columnsAsList(TreeTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<TreeAsset>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}
	}

	public static class AssetTreeTableCellEditor extends TreeTableCellEditor {

		private int indent;
		private int spacer;
		private DefaultEventTableModel<TreeAsset> tableModel;

		public AssetTreeTableCellEditor(TableCellEditor delegate, TreeList treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
			super(delegate, treeList);
			if (indent == spacer) {
				throw new IllegalArgumentException("indent and spacer may not be equal - that invalidates indent");
			}
			this.tableModel = tableModel;
			this.indent = indent;
			this.spacer = spacer;
		}

		@Override
		protected int getIndent(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return super.getIndent(treeNodeData, showExpanderForEmptyParent) + indent;
		}

		@Override
		protected int getSpacer(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return spacer;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JPanel jPanel = (JPanel) super.getTableCellEditorComponent(table, value, isSelected, row, column); //To change body of generated methods, choose Tools | Templates.
			TreeAsset treeAsset = tableModel.getElementAt(row);
			JLabel jLabel = (JLabel) jPanel.getComponent(3);
			jLabel.setIcon(treeAsset.getIcon());
			return jPanel;
		}

		
	}

	public static class AssetTreeTableCellRenderer extends TreeTableCellRenderer {

		private int indent;
		private int spacer;
		private DefaultEventTableModel<TreeAsset> tableModel;

		public AssetTreeTableCellRenderer(TableCellRenderer delegate, TreeList treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
			super(delegate, treeList);
			if (indent == spacer) {
				throw new IllegalArgumentException("indent and spacer may not be equal - that invalidates indent");
			}
			this.tableModel = tableModel;
			this.indent = indent;
			this.spacer = spacer;
		}

		@Override
		protected int getIndent(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return super.getIndent(treeNodeData, showExpanderForEmptyParent) + indent;
		}

		@Override
		protected int getSpacer(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return spacer;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JPanel jPanel = (JPanel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			TreeAsset treeAsset = tableModel.getElementAt(row);
			JLabel jLabel = (JLabel) jPanel.getComponent(3);
			jLabel.setIcon(treeAsset.getIcon());
			return jPanel;
		}

		
	}
}

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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.FilterList;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.i18n.GuiShared;

class FilterGui<E> implements ActionListener {

	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_CLEAR = "ACTION_CLEAR";
	private static final String ACTION_SAVE = "ACTION_SAVE";
	private static final String ACTION_MANAGER = "ACTION_MANAGER";
	private static final String ACTION_SHOW_FILTERS = "ACTION_SHOW_FILTERS";
	private static final String ACTION_EXPORT = "ACTION_EXPORT";

	private JPanel jPanel;
	private GroupLayout layout;
	private JToolBar jToolBar;
	private JDropDownButton jLoadFilter;
	private JCheckBox jShowFilters;
	private JLabel jShowing;
	private JFrame jFrame;

	private FilterControl<E> matcherControl;

	private List<FilterPanel<E>> filterPanels = new ArrayList<FilterPanel<E>>();
	private FilterSave filterSave;
	private FilterManager<E> filterManager;

	private CsvExportDialog<E> export;

	FilterGui(final JFrame jFrame, final FilterControl<E> matcherControl) {
		this.jFrame = jFrame;
		this.matcherControl = matcherControl;

		export = new CsvExportDialog<E>(jFrame, matcherControl, matcherControl.getEventLists(), matcherControl.getEnumColumns());

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);

		//Add
		JButton jAddField = new JButton(GuiShared.get().addField());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(ACTION_ADD);
		jAddField.addActionListener(this);
		addToolButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(GuiShared.get().clearField());
		jClearFields.setIcon(Images.FILTER_CLEAR.getIcon());
		jClearFields.setActionCommand(ACTION_CLEAR);
		jClearFields.addActionListener(this);
		addToolButton(jClearFields);

		addToolSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(GuiShared.get().saveFilter());
		jSaveFilter.setIcon(Images.FILTER_SAVE.getIcon());
		jSaveFilter.setActionCommand(ACTION_SAVE);
		jSaveFilter.addActionListener(this);
		addToolButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(GuiShared.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jLoadFilter.keepVisible(2);
		jLoadFilter.setTopFixedCount(2);
		jLoadFilter.setInterval(125);
		addToolButton(jLoadFilter);

		addToolSeparator();

		//Export
		JButton jExport = new JButton(GuiShared.get().export());
		jExport.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(ACTION_EXPORT);
		jExport.addActionListener(this);
		addToolButton(jExport);

		addToolSeparator();

		//Show Filters
		jShowFilters = new JCheckBox(GuiShared.get().showFilters());
		jShowFilters.setActionCommand(ACTION_SHOW_FILTERS);
		jShowFilters.addActionListener(this);
		jShowFilters.setSelected(true);
		addToolButton(jShowFilters, 70);

		//Showing
		jShowing = new JLabel();



		updateFilters();
		add();

		filterSave = new FilterSave(jFrame);
		filterManager = new FilterManager<E>(jFrame, this, matcherControl.getFilters(), matcherControl.getDefaultFilters());
	}

	JPanel getPanel() {
		return jPanel;
	}

	final void addToolButton(final AbstractButton jButton) {
		addToolButton(jButton, 90);
	}
	final void addToolButton(final AbstractButton jButton, final int width) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}
	final void addToolSeparator() {
		jToolBar.addSeparator();
	}

	void updateShowing() {
		int showing = 0;
		for (FilterList<E> filterList : matcherControl.getFilterLists()) {
			showing = showing + filterList.size();
		}
		jShowing.setText(GuiShared.get().filterShowing(showing, matcherControl.getTotalSize(), getCurrentFilterName()));
	}

	String getCurrentFilterName() {
		String filterName = GuiShared.get().filterUntitled();
		if (getFilters().isEmpty()) {
			filterName = GuiShared.get().filterEmpty();
		} else {
			if (matcherControl.getAllFilters().containsValue(getFilters())) {
				for (Map.Entry<String, List<Filter>> entry : matcherControl.getAllFilters().entrySet()) {
					if (entry.getValue().equals(getFilters())) {
						filterName = entry.getKey();
						break;
					}
				}
			}
		}
		return filterName;
	}

	List<Filter> getFilters() {
		List<Filter> filters = new ArrayList<Filter>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			Filter filter = filterPanel.getFilter();
			if (!filter.isEmpty()) {
				filters.add(filter);
			}
		}
		return filters;
	}

	private List<FilterMatcher<E>> getMatchers() {
		List<FilterMatcher<E>> matchers = new ArrayList<FilterMatcher<E>>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			FilterMatcher<E> matcher = filterPanel.getMatcher();
			if (!matcher.isEmpty()) {
				matchers.add(matcher);
			}
		}
		return matchers;
	}

	private void update() {
		jPanel.removeAll();
		GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
		horizontalGroup.addGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar)
				.addGap(0, 0, Short.MAX_VALUE)
				.addComponent(jShowing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		);

		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		int toolbatHeight = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		verticalGroup
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBar, toolbatHeight, toolbatHeight, toolbatHeight)
					.addComponent(jShowing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		if (jShowFilters.isSelected()) {
			for (FilterPanel<E> filterPanel : filterPanels) {
				verticalGroup.addComponent(filterPanel.getPanel());
				horizontalGroup.addComponent(filterPanel.getPanel());
			}
		}

		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
	}

	void remove(final FilterPanel<E> filterPanel) {
		filterPanels.remove(filterPanel);
		if (filterPanels.size() == 1) {
			filterPanels.get(0).setEnabled(false);
		}
		update();
	}

	private void add() {
		add(new FilterPanel<E>(this, matcherControl));
	}

	private void add(final FilterPanel<E> filterPanel) {
		filterPanels.add(filterPanel);
		if (filterPanels.size() > 1) { //Show remove buttons
			filterPanels.get(0).setEnabled(true);
		}
		if (filterPanels.size() == 1) { //Hide remove button
			filterPanels.get(0).setEnabled(false);
		}
		update();
	}

	void clear() {
		while (filterPanels.size() > 0) {
			remove(filterPanels.get(0));
		}
		add();
		refilter();
	}

	private void loadFilter(final String filterName, final boolean add) {
		if (filterName == null) {
			return;
		}
		if (matcherControl.getAllFilters().containsKey(filterName)) {
			List<Filter> filters = matcherControl.getAllFilters().get(filterName);
			if (add) {
				addFilters(filters);
			} else {
				setFilters(filters);
			}
		}
	}

	void setFilters(final List<Filter> filters) {
		while (filterPanels.size() > 0) {
			remove(filterPanels.get(0));
		}
		addFilters(filters);
	}

	void addFilter(final Filter filter) {
		addFilters(Collections.singletonList(filter));
	}

	void addFilters(final List<Filter> filters) {
		//Remove single empty filter...
		if (filterPanels.size() == 1 && filterPanels.get(0).getFilter().isEmpty()) {
			remove(filterPanels.get(0));
		}
		for (Filter filter : filters) {
			FilterPanel<E> filterPanel = new FilterPanel<E>(this, matcherControl);
			filterPanel.setFilter(filter);
			add(filterPanel);
		}
		update();
		refilter();
	}

	final void updateFilters() {
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().manageFilters(), Images.DIALOG_SETTINGS.getIcon());
		jMenuItem.setActionCommand(ACTION_MANAGER);
		jMenuItem.addActionListener(this);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);

		List<String> filters = new ArrayList<String>(matcherControl.getFilters().keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());

		List<String> defaultFilters = new ArrayList<String>(matcherControl.getDefaultFilters().keySet());
		Collections.sort(defaultFilters, new CaseInsensitiveComparator());

		if (!filters.isEmpty() || !defaultFilters.isEmpty()) {
			jLoadFilter.addSeparator();
		}

		for (String s : defaultFilters) {
			jMenuItem = new JMenuItem(s, Images.FILTER_LOAD_DEFAULT.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(this);
			jLoadFilter.add(jMenuItem);
		}

		for (String s : filters) {
			jMenuItem = new JMenuItem(s, Images.FILTER_LOAD.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(this);
			jLoadFilter.add(jMenuItem);
		}
		updateShowing();
		matcherControl.updateFilters();
	}

	void refilter() {
		matcherControl.beforeFilter();
		List<FilterMatcher<E>> matchers = getMatchers();
		if (matchers.isEmpty()) {
			for (FilterList<E> filterList : matcherControl.getFilterLists()) {
				filterList.setMatcher(null);
			}
		} else {
			for (FilterList<E> filterList : matcherControl.getFilterLists()) {
				filterList.setMatcher(new FilterLogicalMatcher<E>(matchers));
			}
		}

		matcherControl.afterFilter();
		updateShowing();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())) {
			add();
			return;
		}
		if (ACTION_CLEAR.equals(e.getActionCommand())) {
			clear();
			return;
		}
		if (ACTION_MANAGER.equals(e.getActionCommand())) {
			filterManager.setVisible(true);
			return;
		}
		if (ACTION_SHOW_FILTERS.equals(e.getActionCommand())) {
			update();
			return;
		}
		if (ACTION_SAVE.equals(e.getActionCommand())) {
			if (getMatchers().isEmpty()) {
				JOptionPane.showMessageDialog(jFrame, GuiShared.get().nothingToSave(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
			} else {
				String name = filterSave.show(new ArrayList<String>(matcherControl.getFilters().keySet()), new ArrayList<String>(matcherControl.getDefaultFilters().keySet()));
				if (name != null && !name.isEmpty()) {
					matcherControl.getFilters().put(name, getFilters());
					updateFilters();
				}
			}
			return;
		}
		if (ACTION_EXPORT.equals(e.getActionCommand())) {
			export.setVisible(true);
			return;
		}
		loadFilter(e.getActionCommand(), (e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
	}
}
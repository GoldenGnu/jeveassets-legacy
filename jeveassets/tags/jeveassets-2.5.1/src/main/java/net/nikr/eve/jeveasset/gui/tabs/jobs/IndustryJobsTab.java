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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.IndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.data.IndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsJobs;


public class IndustryJobsTab extends JMainTab implements ListEventListener<IndustryJob> {

	private JAutoColumnTable jTable;
	private JLabel jInventionSuccess;

	//Table
	private EventList<IndustryJob> eventList;
	private FilterList<IndustryJob> filterList;
	private EventTableModel<IndustryJob> tableModel;
	private EventSelectionModel<IndustryJob> selectionModel;
	private IndustryJobData data;
	private IndustryJobsFilterControl filterControl;
	private EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat;

	public static final String NAME = "industryjobs"; //Not to be changed!

	public IndustryJobsTab(final Program program) {
		super(program, TabsJobs.get().industry(), Images.TOOL_INDUSTRY_JOBS.getIcon(), true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob>(IndustryJobTableFormat.class);
		//Backend
		eventList = new BasicEventList<IndustryJob>();
		//Filter
		filterList = new FilterList<IndustryJob>(eventList);
		filterList.addListEventListener(this);
		//Sorting (per column)
		SortedList<IndustryJob> sortedList = new SortedList<IndustryJob>(filterList);
		//Table Model
		tableModel = new EventTableModel<IndustryJob>(sortedList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = new EventSelectionModel<IndustryJob>(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Filter
		Map<String, List<Filter>> defaultFilters = new HashMap<String, List<Filter>>();
		List<Filter> filter;
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_ACTIVE.toString()));
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_PENDING.toString()));
		filter.add(new Filter(LogicType.OR, IndustryJobTableFormat.STATE, CompareType.EQUALS,  IndustryJobState.STATE_READY.toString()));
		defaultFilters.put(TabsJobs.get().active(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_ACTIVE.toString()));
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_PENDING.toString()));
		filter.add(new Filter(LogicType.AND, IndustryJobTableFormat.STATE, CompareType.EQUALS_NOT,  IndustryJobState.STATE_READY.toString()));
		defaultFilters.put(TabsJobs.get().completed(), filter);
		filterControl = new IndustryJobsFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME),
				defaultFilters
				);

		jInventionSuccess = StatusPanel.createLabel(TabsJobs.get().inventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		this.addStatusbarLabel(jInventionSuccess);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 700, 700, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {

		if (data == null) {
			data = new IndustryJobData(program);
		}
		data.updateData();

		if (!data.getOwners().isEmpty()) {
			jTable.setEnabled(true);
			Collections.sort(data.getOwners(), new CaseInsensitiveComparator());
			data.getOwners().add(0, TabsJobs.get().all());
		} else {
			jTable.setEnabled(false);
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(data.getAll());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//DATA
		MenuData<IndustryJob> menuData = new MenuData<IndustryJob>(selectionModel.getSelected());
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selectionModel.getSelected()));
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<IndustryJob>(program, menuData));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<IndustryJob>(program, menuData));
	//LOOKUP
		jComponent.add(new JMenuLookup<IndustryJob>(program, menuData));
	//REPROCESSED
		jComponent.add(new JMenuReprocessed<IndustryJob>(program, menuData));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//INFO
		JMenuInfo.industryJob(jComponent, selectionModel.getSelected());
	}

	@Override
	public void listChanged(final ListEvent<IndustryJob> listChanges) {
		int count = 0;
		double success = 0;
		for (IndustryJob industryJob : filterList) {
			if (industryJob.getActivity() == IndustryActivity.ACTIVITY_REVERSE_INVENTION && industryJob.isCompleted()) {
				count++;
				if (industryJob.getState() == IndustryJobState.STATE_DELIVERED) {
					success++;
				}
			}
		}
		if (count <= 0) {
			jInventionSuccess.setText(Formater.percentFormat(0.0));
		} else {
			jInventionSuccess.setText(Formater.percentFormat(success / count));
		}
	}

	public static class IndustryJobsFilterControl extends FilterControl<IndustryJob> {

		private EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat;

		public IndustryJobsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<IndustryJobTableFormat, IndustryJob> tableFormat, final EventList<IndustryJob> eventList, final FilterList<IndustryJob> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final IndustryJob item, final String column) {
			IndustryJobTableFormat format = IndustryJobTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			IndustryJobTableFormat format = (IndustryJobTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			IndustryJobTableFormat format = (IndustryJobTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Enum[] getColumns() {
			return IndustryJobTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return IndustryJobTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<IndustryJob>> getEnumColumns() {
			return columnsAsList(IndustryJobTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<IndustryJob>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<IndustryJob>>(tableFormat.getShownColumns());
		}
	}
}
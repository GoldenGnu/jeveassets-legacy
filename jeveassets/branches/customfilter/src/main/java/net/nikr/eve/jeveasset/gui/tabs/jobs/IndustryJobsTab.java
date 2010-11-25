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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.IndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.data.IndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog;
import net.nikr.eve.jeveasset.gui.dialogs.custom.CustomDialog.CustomDialogInterface;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class IndustryJobsTab extends JMainTab implements ActionListener {

	private final static String ACTION_CHARACTER_SELECTED = "ACTION_CHARACTER_SELECTED";
	private final static String ACTION_STATE_SELECTED = "ACTION_STATE_SELECTED";
	private final static String ACTION_ACTIVITY_SELECTED = "ACTION_ACTIVITY_SELECTED";

	private final static String CUSTOM = "<Custom>";
	private final static String ALL = "All";

	private JComboBox jCharacters;
	private JComboBox jState;
	private JComboBox jActivity;
	private JAutoColumnTable jTable;
	private CustomDialog characterCustomDialog;
	private CustomDialog stateCustomDialog;
	private CustomDialog activityCustomDialog;

	private EventList<IndustryJob> jobsEventList;
	private EventTableModel<IndustryJob> jobsTableModel;

	private List<IndustryJob> all;
	private Map<String, List<IndustryJob>> jobs;

	private List<String> selectedCharacters;
	private List<String> selectedStats;
	private List<String> selectedActivities;

	public IndustryJobsTab(Program program) {
		super(program, "Industry Jobs", Images.ICON_TOOL_INDUSTRY_JOBS, true);

		characterCustomDialog = new CustomDialog(program);
		stateCustomDialog = new CustomDialog(program);
		activityCustomDialog = new CustomDialog(program);

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_CHARACTER_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox();
		jState.setActionCommand(ACTION_STATE_SELECTED);
		jState.addActionListener(this);

		jActivity = new JComboBox();
		jActivity.setActionCommand(ACTION_ACTIVITY_SELECTED);
		jActivity.addActionListener(this);

		JLabel jCharactersLabel = new JLabel("Character");
		JLabel jStateLabel = new JLabel("State");
		JLabel jActivityLabel = new JLabel("Activity");

		//Table format
		IndustryJobTableFormat industryJobsTableFormat = new IndustryJobTableFormat();
		//Backend
		jobsEventList = new BasicEventList<IndustryJob>();
		//For soring the table
		SortedList<IndustryJob> jobsSortedList = new SortedList<IndustryJob>(jobsEventList);
		//Table Model
		jobsTableModel = new EventTableModel<IndustryJob>(jobsSortedList, industryJobsTableFormat);
		//Tables
		jTable = new JAutoColumnTable(jobsTableModel, industryJobsTableFormat.getColumnNames());
		//Table Selection
		EventSelectionModel<IndustryJob> selectionModel = new EventSelectionModel<IndustryJob>(jobsEventList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Sorters
		TableComparatorChooser.install(jTable, jobsSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, industryJobsTableFormat);
		//Scroll Panels
		JScrollPane jJobsScrollPanel = jTable.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 200, 200, 200)
					.addComponent(jActivityLabel)
					.addComponent(jActivity, 200, 200, 200)
					.addComponent(jStateLabel)
					.addComponent(jState, 200, 200, 200)
				)
				.addComponent(jJobsScrollPanel, 700, 700, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jState, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jActivityLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jActivity, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jJobsScrollPanel, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		//Select Single Row
		jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
		jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		

		boolean isSingleRow = jTable.getSelectedRows().length == 1;
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

		IndustryJob industryJob = isSingleRow ? jobsTableModel.getElementAt(jTable.getSelectedRow()) : null;
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
		jComponent.add(new JMenuAssetFilter(program, industryJob));
		jComponent.add(new JMenuLookup(program, industryJob));
	}

	@Override
	public void updateData() {
		Vector<String> characters = new Vector<String>();
		jobs = new HashMap<String, List<IndustryJob>>();
		all = new ArrayList<IndustryJob>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				if (human.isShowAssets()){
					characters.add(human.getName());
					List<IndustryJob> characterIndustryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobs(), human.getName(), program.getSettings());
					jobs.put(human.getName(), characterIndustryJobs);
					all.addAll(characterIndustryJobs);
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						if (!characters.contains(corpKey)){
							characters.add(corpKey);
							jobs.put(corpKey, new ArrayList<IndustryJob>());
						}
						List<IndustryJob> corporationIndustryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobsCorporation(), human.getCorporation(), program.getSettings());
						jobs.get(corpKey).addAll(corporationIndustryJobs);
						all.addAll(corporationIndustryJobs);
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jTable.setEnabled(true);
			jActivity.setEnabled(true);
			jState.setEnabled(true);
			Collections.sort(characters);
			characterCustomDialog.updateList(new ArrayList<String>(characters));
			characters.add(0, ALL);
			characters.add(CUSTOM);
			jCharacters.setModel( new DefaultComboBoxModel(characters));

			IndustryActivity[] activities = IndustryJob.IndustryActivity.values();
			List<String> activityList = new ArrayList<String>(activities.length+1);
			for (IndustryActivity activity : activities){
				activityList.add(activity.toString());
			}
			activityList.add(CUSTOM);
			jActivity.setModel( new DefaultComboBoxModel(new Vector<String>(activityList)));
			activityList.remove(0);
			activityList.remove(activityList.size()-1);
			activityCustomDialog.updateList(activityList);

			IndustryJobState[] states = IndustryJob.IndustryJobState.values();
			List<String> statesList = new ArrayList<String>(states.length+1);
			for (IndustryJobState state : states){
				statesList.add(state.toString());
			}
			statesList.add(CUSTOM);
			jState.setModel( new DefaultComboBoxModel(new Vector<String>(statesList)));
			statesList.remove(0);
			statesList.remove(statesList.size()-1);
			stateCustomDialog.updateList(statesList);

			jCharacters.setSelectedIndex(0);
			jActivity.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jTable.setEnabled(false);
			jActivity.setEnabled(false);
			jState.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem("No character found");
			jActivity.setModel( new DefaultComboBoxModel());
			jActivity.getModel().setSelectedItem("No character found");
			jState.setModel( new DefaultComboBoxModel());
			jState.getModel().setSelectedItem("No character found");
			jobsEventList.clear();
		}
	}

	private void updateTable(){
		if (jCharacters.getItemCount() > 2 && selectedCharacters != null && selectedStats != null && selectedActivities != null){
			List<IndustryJob> industryJobsInput;
			List<IndustryJob> industryJobsOutput = new ArrayList<IndustryJob>();
			//Characters
			if (selectedCharacters.contains(ALL)){
				industryJobsInput = all;
			} else {
				industryJobsInput = new ArrayList<IndustryJob>();
				for (String s : selectedCharacters){
					industryJobsInput.addAll(jobs.get(s));
				}
			}
			for (int a = 0; a < industryJobsInput.size(); a++){
				IndustryJob industryJob = industryJobsInput.get(a);
				boolean bState = (selectedStats.contains(industryJob.getState().toString()) || selectedStats.contains(IndustryJob.IndustryJobState.STATE_ALL.toString()));
				boolean bActivity = (selectedActivities.contains(industryJob.getActivity().toString()) || selectedActivities.contains(IndustryJob.IndustryActivity.ACTIVITY_ALL.toString()));
				if (bState && bActivity){
					industryJobsOutput.add(industryJob);
				}
			}
			try {
				jobsEventList.getReadWriteLock().writeLock().lock();
				jobsEventList.clear();
				jobsEventList.addAll( industryJobsOutput );
			} finally {
				jobsEventList.getReadWriteLock().writeLock().unlock();
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CHARACTER_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jCharacters.getSelectedItem();
			if (selected.equals(CUSTOM)){
				characterCustomDialog.show(new CharacterListener());
			} else {
				selectedCharacters = Collections.singletonList(selected);
				updateTable();
			}
		}
		if (ACTION_ACTIVITY_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jActivity.getSelectedItem();
			if (selected.equals(CUSTOM)){
				activityCustomDialog.show(new ActivityListener());
			} else {
				selectedActivities = Collections.singletonList(selected);
				updateTable();
			}
		}
		if (ACTION_STATE_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jState.getSelectedItem();
			if (selected.equals(CUSTOM)){
				stateCustomDialog.show(new StateListener());
			} else {
				selectedStats = Collections.singletonList(selected);
				updateTable();
			}
			
		}
	}

	public class CharacterListener implements CustomDialogInterface{

		@Override
		public void customDialogReady(List<String> list) {
			selectedCharacters = list;
			updateTable();
		}

	}

	public class ActivityListener implements CustomDialogInterface{

		@Override
		public void customDialogReady(List<String> list) {
			selectedActivities = list;
			updateTable();
		}

	}

	public class StateListener implements CustomDialogInterface{

		@Override
		public void customDialogReady(List<String> list) {
			selectedStats = list;
			updateTable();
		}

	}
}

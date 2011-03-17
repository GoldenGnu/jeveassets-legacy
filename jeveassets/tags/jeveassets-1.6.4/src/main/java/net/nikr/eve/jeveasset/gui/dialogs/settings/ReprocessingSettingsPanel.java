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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.gui.shared.JNumberField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ReprocessingSettingsPanel extends JSettingsPanel implements ActionListener {

	private final int LEVEL0 = 0;
	private final int LEVEL1 = 1;
	private final int LEVEL2 = 2;
	private final int LEVEL3 = 3;
	private final int LEVEL4 = 4;
	private final int LEVEL5 = 5;

	private JRadioButton jStation50;
	private JRadioButton jStationOther;
	private JTextField jStation;
	private JRadioButton[] jRefining;
	private JRadioButton[] jRefineryEfficiency;
	private JRadioButton[] jScrapmetalProcessing;

	public ReprocessingSettingsPanel(Program program, SettingsDialog optionsDialog, Icon icon) {
		super(program, optionsDialog, DialoguesSettings.get().reprocessing(), icon);

		JLabel jNotes = new JLabel(DialoguesSettings.get().reprocessingWarning());

		JLabel jStationLabel = new JLabel(DialoguesSettings.get().stationEquipment());
		jStation50 = new JRadioButton(DialoguesSettings.get().fiftyPercent());
		jStation50.addActionListener(this);
		jStationOther = new JRadioButton(DialoguesSettings.get().customPercent());
		jStationOther.addActionListener(this);
		jStation = new JNumberField();
		JLabel jStationPercentLabel = new JLabel(DialoguesSettings.get().percentSymbol());

		ButtonGroup jStationButtonGroup = new ButtonGroup();
		jStationButtonGroup.add(jStation50);
		jStationButtonGroup.add(jStationOther);

		JLabel j0 = new JLabel(DialoguesSettings.get().zero());
		JLabel j1 = new JLabel(DialoguesSettings.get().one());
		JLabel j2 = new JLabel(DialoguesSettings.get().two());
		JLabel j3 = new JLabel(DialoguesSettings.get().three());
		JLabel j4 = new JLabel(DialoguesSettings.get().four());
		JLabel j5 = new JLabel(DialoguesSettings.get().five());

		JLabel jRefiningLabel = new JLabel(DialoguesSettings.get().refiningLevel());
		jRefining = new JRadioButton[6];
		jRefining[LEVEL0] = new JRadioButton();
		jRefining[LEVEL0].addActionListener(this);
		jRefining[LEVEL1] = new JRadioButton();
		jRefining[LEVEL1].addActionListener(this);
		jRefining[LEVEL2] = new JRadioButton();
		jRefining[LEVEL2].addActionListener(this);
		jRefining[LEVEL3] = new JRadioButton();
		jRefining[LEVEL3].addActionListener(this);
		jRefining[LEVEL4] = new JRadioButton();
		jRefining[LEVEL4].addActionListener(this);
		jRefining[LEVEL5] = new JRadioButton();
		jRefining[LEVEL5].addActionListener(this);

		ButtonGroup jRefiningButtonGroup = new ButtonGroup();
		jRefiningButtonGroup.add(jRefining[LEVEL0]);
		jRefiningButtonGroup.add(jRefining[LEVEL1]);
		jRefiningButtonGroup.add(jRefining[LEVEL2]);
		jRefiningButtonGroup.add(jRefining[LEVEL3]);
		jRefiningButtonGroup.add(jRefining[LEVEL4]);
		jRefiningButtonGroup.add(jRefining[LEVEL5]);

		JLabel jRefineryEfficiencyLabel = new JLabel(DialoguesSettings.get().refiningEfficiencyLevel());
		jRefineryEfficiency = new JRadioButton[6];
		jRefineryEfficiency[LEVEL0] = new JRadioButton();
		jRefineryEfficiency[LEVEL0].addActionListener(this);
		jRefineryEfficiency[LEVEL1] = new JRadioButton();
		jRefineryEfficiency[LEVEL1].addActionListener(this);
		jRefineryEfficiency[LEVEL2] = new JRadioButton();
		jRefineryEfficiency[LEVEL2].addActionListener(this);
		jRefineryEfficiency[LEVEL3] = new JRadioButton();
		jRefineryEfficiency[LEVEL3].addActionListener(this);
		jRefineryEfficiency[LEVEL4] = new JRadioButton();
		jRefineryEfficiency[LEVEL4].addActionListener(this);
		jRefineryEfficiency[LEVEL5] = new JRadioButton();
		jRefineryEfficiency[LEVEL5].addActionListener(this);

		ButtonGroup jEfficiencyButtonGroup = new ButtonGroup();
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL0]);
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL1]);
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL2]);
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL3]);
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL4]);
		jEfficiencyButtonGroup.add(jRefineryEfficiency[LEVEL5]);

		JLabel jScrapmetalProcessingLabel = new JLabel(DialoguesSettings.get().scrapMetalProcessingLevel());
		jScrapmetalProcessing = new JRadioButton[6];
		jScrapmetalProcessing[LEVEL0] = new JRadioButton();
		jScrapmetalProcessing[LEVEL1] = new JRadioButton();
		jScrapmetalProcessing[LEVEL2] = new JRadioButton();
		jScrapmetalProcessing[LEVEL3] = new JRadioButton();
		jScrapmetalProcessing[LEVEL4] = new JRadioButton();
		jScrapmetalProcessing[LEVEL5] = new JRadioButton();

		ButtonGroup jProcessingButtonGroup = new ButtonGroup();
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL0]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL1]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL2]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL3]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL4]);
		jProcessingButtonGroup.add(jScrapmetalProcessing[LEVEL5]);


		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jNotes)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jStationLabel)
						.addComponent(jRefiningLabel)
						.addComponent(jRefineryEfficiencyLabel)
						.addComponent(jScrapmetalProcessingLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jStation50)
							.addComponent(jStationOther)
							.addComponent(jStation)
							.addComponent(jStationPercentLabel)
						)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j0)
								.addComponent(jRefining[LEVEL0])
								.addComponent(jRefineryEfficiency[LEVEL0])
								.addComponent(jScrapmetalProcessing[LEVEL0])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j1)
								.addComponent(jRefining[LEVEL1])
								.addComponent(jRefineryEfficiency[LEVEL1])
								.addComponent(jScrapmetalProcessing[LEVEL1])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j2)
								.addComponent(jRefining[LEVEL2])
								.addComponent(jRefineryEfficiency[LEVEL2])
								.addComponent(jScrapmetalProcessing[LEVEL2])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j3)
								.addComponent(jRefining[LEVEL3])
								.addComponent(jRefineryEfficiency[LEVEL3])
								.addComponent(jScrapmetalProcessing[LEVEL3])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j4)
								.addComponent(jRefining[LEVEL4])
								.addComponent(jRefineryEfficiency[LEVEL4])
								.addComponent(jScrapmetalProcessing[LEVEL4])
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(j5)
								.addComponent(jRefining[LEVEL5])
								.addComponent(jRefineryEfficiency[LEVEL5])
								.addComponent(jScrapmetalProcessing[LEVEL5])
							)
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jStationLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStation50, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStationOther, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStationPercentLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(j0, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j1, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j2, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j3, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j4, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(j5, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					//.addComponent(jRefining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(0)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRefiningLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefining[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRefineryEfficiencyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRefineryEfficiency[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jScrapmetalProcessingLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL0], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL1], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL2], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL3], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL4], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScrapmetalProcessing[LEVEL5], Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jNotes, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}



	@Override
	public boolean save() {
		ReprocessSettings reprocessSettings = new ReprocessSettings(Integer.parseInt(jStation.getText()), getSelected(jRefining), getSelected(jRefineryEfficiency), getSelected(jScrapmetalProcessing));
		boolean update = !program.getSettings().getReprocessSettings().equals(reprocessSettings);
		program.getSettings().setReprocessSettings(reprocessSettings);
		//Update table if needed
		return update;
	}

	@Override
	public void load() {
		ReprocessSettings reprocessSettings = program.getSettings().getReprocessSettings();
		if (reprocessSettings.getStation() == 50){
			 jStation50.setSelected(true);
		} else {
			jStationOther.setSelected(true);
		}
		jStation.setText(String.valueOf(reprocessSettings.getStation()));
		jRefining[reprocessSettings.getRefiningLevel()].setSelected(true);
		jRefineryEfficiency[reprocessSettings.getRefineryEfficiencyLevel()].setSelected(true);
		jScrapmetalProcessing[reprocessSettings.getScrapmetalProcessingLevel()].setSelected(true);
		validateSkills();
		validateStation();
	}

	private int getSelected(JRadioButton[] jRadioButton){
		for (int a = 0; a < jRadioButton.length; a++){
			if (jRadioButton[a].isSelected()) return a;
		}
		return 0;
	}

	private void setEnabled(JRadioButton[] jRadioButton, boolean enabled){
		for (int a = 0; a < jRadioButton.length; a++){
			jRadioButton[a].setEnabled(enabled);
		}
	}

	private void validateSkills(){
		if (getSelected(jRefining) < 5){
			setEnabled(jRefineryEfficiency, false);
			jRefineryEfficiency[LEVEL0].setSelected(true);
		} else {
			setEnabled(jRefineryEfficiency, true);
		}
		if (getSelected(jRefineryEfficiency) < 5){
			setEnabled(jScrapmetalProcessing, false);
			jScrapmetalProcessing[LEVEL0].setSelected(true);
		} else {
			setEnabled(jScrapmetalProcessing, true);
		}
	}

	private void validateStation(){
		if (jStation50.isSelected()){
			jStation.setText("50");
			jStation.setEditable(false);
		}
		if (jStationOther.isSelected()){
			jStation.setEditable(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		validateSkills();
		validateStation();
	}

}
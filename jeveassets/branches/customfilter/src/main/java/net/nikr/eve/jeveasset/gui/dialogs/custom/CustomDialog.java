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

package net.nikr.eve.jeveasset.gui.dialogs.custom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;


public class CustomDialog extends JDialogCentered implements ActionListener {

	private final static String ACTION_OK = "ACTION_OK";

	private List<JCheckBox> checkBoxes;

	private JButton jOK;

	public CustomDialog(Program program) {
		super(program, "Custom Filter");
		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);
	}

	public void updateList(List<String> list){
		jPanel.removeAll();
		checkBoxes = new ArrayList<JCheckBox>();
		ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup verticalGroup = layout.createSequentialGroup();
		for (String s : list){
			JCheckBox jCheckBox = new JCheckBox(s);
			checkBoxes.add(jCheckBox);
			horizontalGroup.addComponent(jCheckBox);
			verticalGroup.addComponent(jCheckBox, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		}
		horizontalGroup.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH);
		verticalGroup.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);

		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
	}
	
	public void show(CustomDialogInterface cdi){
		javax.swing.SwingUtilities.invokeLater(new Work(cdi));
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {

	}

	@Override
	protected void windowActivated() {
		
	}

	@Override
	protected void save() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

	private class Work implements Runnable{

		private CustomDialogInterface cdi;

		public Work(CustomDialogInterface cdi) {
			this.cdi = cdi;
		}

		@Override
		public void run() {
			setVisible(true);

			List<String> list = new ArrayList<String>();
			for (JCheckBox jCheckBox : checkBoxes){
				if (jCheckBox.isSelected()) list.add(jCheckBox.getText());
			}
			cdi.customDialogReady(list);
		}

	}

	public interface CustomDialogInterface {
		public void customDialogReady(List<String> list);
	}

}

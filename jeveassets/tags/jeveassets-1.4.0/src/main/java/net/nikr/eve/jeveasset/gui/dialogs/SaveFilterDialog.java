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

package net.nikr.eve.jeveasset.gui.dialogs;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;


public class SaveFilterDialog extends JDialogCentered implements ActionListener {

	public final static String ACTION_SAVE = "ACTION_SAVE";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_SELECTED = "ACTION_SELECTED";

	private EventList<String> filters;
	private JComboBox jName;
	private JButton jSave;

	public SaveFilterDialog(Program program) {
		super(program, "Save Filter");
		
		JLabel jText = new JLabel("Enter filter name:");

		jName = new JComboBox();
		jName.setActionCommand(ACTION_SELECTED);
		jName.addActionListener(this);
		JCopyPopup.install((JTextComponent) jName.getEditor().getEditorComponent());
		filters = new BasicEventList<String>();
		AutoCompleteSupport support = AutoCompleteSupport.install(jName, filters, new Filterator());
		
		jSave = new JButton("Save");
		jSave.setActionCommand(ACTION_SAVE);
		jSave.addActionListener(this);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jName, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jSave, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSave, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
		filtersChanged();
	}
	
	public void filtersChanged(){
		List<String> list = new ArrayList<String>( program.getSettings().getAssetFilters().keySet() );
		Collections.sort(list);
		filters.clear();
		filters.addAll(list);
	}
	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jSave;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		String returnValue = (String) jName.getSelectedItem();
		if (returnValue == null){
			JOptionPane.showMessageDialog(this.getDialog(), "You need to enter a name for the filter.", "Save Filter", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		if (returnValue.equals("")) {
			JOptionPane.showMessageDialog(this.getDialog(), "You need to enter a name for the filter.", "Save Filter", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		//Ask to overwrite...
		if (program.getSettings().getAssetFilters().containsKey(returnValue)){
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), "Overwrite?", "Overwrite Filter", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION){
				return;
			}
		}
		this.setVisible(false);
		//Update filters
		program.getSettings().getAssetFilters().put(returnValue, program.getToolPanel().getAssetFilters());
		program.filtersChanged();
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			jName.getModel().setSelectedItem("");
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SAVE.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
		if (ACTION_SELECTED.equals(e.getActionCommand())){
			if (jName.getSelectedItem() == null) jName.getModel().setSelectedItem("");
		}
	}

	class Filterator implements TextFilterator<String>{
		@Override
		public void getFilterStrings(List<String> baseList, String element) {
			if (!element.equals("")) baseList.add(element);
		}
	}
}
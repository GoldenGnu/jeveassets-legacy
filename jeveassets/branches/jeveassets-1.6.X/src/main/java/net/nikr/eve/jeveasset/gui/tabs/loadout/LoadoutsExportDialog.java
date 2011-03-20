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

package net.nikr.eve.jeveasset.gui.tabs.loadout;

import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.PlainDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;


public class LoadoutsExportDialog extends JDialogCentered implements ActionListener {

	public final static String ACTION_EXPORT_OK = "ACTION_EXPORT_OK";
	public final static String ACTION_EXPORT_CANCEL = "ACTION_EXPORT_CANCEL";

	private JTextField jName;
	private JTextPane jDescription;
	private JButton jOK;

	private LoadoutsTab loadoutsDialog;

	public LoadoutsExportDialog(Program program, LoadoutsTab loadoutsDialog) {
		super(program, "Export Fitting", Images.TOOL_SHIP_LOADOUTS.getImage());
		this.loadoutsDialog = loadoutsDialog;

		JLabel jNameLabel = new JLabel("Name:");
		jPanel.add(jNameLabel);

		jName = new JTextField();
		JCopyPopup.install(jName);
		jName.setDocument( new MaxLengthPlainDocument(40) ); //max length: 40
		jPanel.add(jName);

		JLabel jDescriptionLabel = new JLabel("Description:");
		jPanel.add(jDescriptionLabel);

		jDescription = new JTextPane();
		JCopyPopup.install(jDescription);
		jDescription.setDocument( new MaxLengthStyledDocument(400) ); //max length: 400
		JScrollPane jDescriptionScrollPane = new JScrollPane(jDescription);
		jPanel.add(jDescriptionScrollPane);

		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_EXPORT_OK);
		jOK.addActionListener(this);
		jPanel.add(jOK);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_EXPORT_CANCEL);
		jCancel.addActionListener(this);
		jPanel.add(jCancel);


		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jNameLabel)
					.addComponent(jName, 373, 373, 373)
					.addComponent(jDescriptionLabel)
					.addComponent(jDescriptionScrollPane, 373, 373, 373)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jNameLabel)
				.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jDescriptionLabel)
				.addComponent(jDescriptionScrollPane, 110, 110, 110)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public String getFittingName(){
		return jName.getText();
	}
	public String getFittingDescription(){
		String text = jDescription.getText();
		text = text.replace("\r\n", "&lt;br&gt;");
		text = text.replace("\r", "&lt;br&gt;");
		text = text.replace("\n", "&lt;br&gt;");
		return text;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		loadoutsDialog.export();
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			jName.setText("");
			jDescription.setText("");
		}
		super.setVisible(b);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_EXPORT_OK.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_EXPORT_CANCEL.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
	}

	private static class MaxLengthPlainDocument extends PlainDocument{

		private int maxLength;

		public MaxLengthPlainDocument(int maxLength) {
			this.maxLength = maxLength;
		}

		@Override
		public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			if (length+string.length() > maxLength){
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuffer currentBuffer = new StringBuffer(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			super.insertString(offset, string, attributes);
		}

	}

	private static class MaxLengthStyledDocument extends DefaultStyledDocument{

		private int maxLength;

		public MaxLengthStyledDocument(int maxLength) {
			this.maxLength = maxLength;
		}

		@Override
		public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			if (length+string.length() > maxLength){
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuffer currentBuffer = new StringBuffer(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			super.insertString(offset, string, attributes);
		}
	}

	



}

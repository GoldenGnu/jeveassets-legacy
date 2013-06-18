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

package net.nikr.eve.jeveasset.gui.shared.components;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public abstract class JAutoCompleteDialog<T> extends JDialogCentered {

	private static final String ACTION_OK = "ACTION_OK";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";

	private final EventList<T> eventList;
	private final AutoCompleteSupport<T> autoComplete;
	private final JComboBox jItems;
	private final JButton jOK;

	private boolean strict = false;

	private T value;

	public JAutoCompleteDialog(Program program, String title, Image image, String msg) {
		super(program, title, image);

		ListenerClass listener = new ListenerClass();

		JLabel jText = new JLabel(msg);

		jItems = new JComboBox();
		eventList = new BasicEventList<T>();
		SortedList<T> sortedList = new SortedList<T>(eventList, getComparator());
		autoComplete = AutoCompleteSupport.install(jItems, sortedList, getFilterator());

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jItems, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jItems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	protected abstract Comparator<T> getComparator();
	protected abstract TextFilterator<T> getFilterator();
	protected abstract T getValue(Object object);

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public final void updateData(List<T> list) {
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Can not set strict on empty EventList - so we do it now (if possible)
		if (!eventList.isEmpty()) {
			autoComplete.setStrict(isStrict());
		}
	}

	public T show() {
		autoComplete.removeFirstItem();
		if (!eventList.isEmpty()) {
			jItems.setSelectedIndex(0);
		}
		if (!isStrict()) { //No effect when strict (except a beep)
			jItems.getModel().setSelectedItem("");
		}
		value = null;
		setVisible(true);
		return value;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jItems;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		value = getValue(jItems.getSelectedItem());
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_OK.equals(e.getActionCommand())) {
				save();
			}
			if (ACTION_CANCEL.equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}
}

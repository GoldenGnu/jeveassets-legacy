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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class JMenuReprocessed<T> extends JAutoMenu<T> implements ActionListener {

	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_SET = "ACTION_SET";

	private final JMenuItem jAdd;
	private final JMenuItem jSet;
	private final Set<Integer> items = new HashSet<Integer>();

	public JMenuReprocessed(final Program program) {
		super(TabsReprocessed.get().title(), program);
		setIcon(Images.TOOL_REPROCESSED.getIcon());

		jAdd = new JMenuItem(TabsReprocessed.get().add());
		jAdd.setIcon(Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		add(jAdd);

		jSet = new JMenuItem(TabsReprocessed.get().set());
		jSet.setIcon(Images.EDIT_SET.getIcon());
		jSet.setActionCommand(ACTION_SET);
		jSet.addActionListener(this);
		add(jSet);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		items.clear();
		for (int typeID : menuData.getTypeIDs()) {
			Item item = StaticData.get().getItems().get(typeID);
			if (item != null && !item.getReprocessedMaterial().isEmpty()) {
				items.add(typeID);
			}
		}
		jAdd.setEnabled(!items.isEmpty());
		jSet.setEnabled(!items.isEmpty());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())) {
			program.getReprocessedTab().add(items);
			program.getReprocessedTab().show();
		}
		if (ACTION_SET.equals(e.getActionCommand())) {
			program.getReprocessedTab().set(items);
			program.getReprocessedTab().show();
		}
	}
}
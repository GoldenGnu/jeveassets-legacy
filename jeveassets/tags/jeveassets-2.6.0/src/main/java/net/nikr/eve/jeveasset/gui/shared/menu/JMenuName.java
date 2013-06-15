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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuName<T> extends JAutoMenu<T> implements ActionListener {

	private static final String ACTION_EDIT_NAME = "ACTION_EDIT_NAME";
	private static final String ACTION_DELETE_NAME = "ACTION_DELETE_NAME";
	private static final String ACTION_EDIT_CONTAINER = "ACTION_EDIT_CONTAINER";
	private static final String ACTION_DELETE_CONTAINERS = "ACTION_DELETE_CONTAINERS";

	private List<UserItem<Long, String>> itemNames;
	private List<UserItem<Long, String>> containerNames;
	private Asset selected;

	private JMenuItem jEditItem;
	private JMenuItem jResetItem;
	private JMenuItem jEditContainer;
	private JMenuItem jResetContainer;
	private JContainerDialog jContainerDialog;

	public JMenuName(final Program program) {
		super(GuiShared.get().itemNameTitle(), program);
		this.setIcon(Images.SETTINGS_USER_NAME.getIcon());

		jContainerDialog = new JContainerDialog(program);

		jEditItem = new JMenuItem(GuiShared.get().itemEdit());
		jEditItem.setIcon(Images.EDIT_EDIT.getIcon());
		jEditItem.setActionCommand(ACTION_EDIT_NAME);
		jEditItem.addActionListener(this);
		add(jEditItem);

		jResetItem = new JMenuItem(GuiShared.get().itemDelete());
		jResetItem.setIcon(Images.EDIT_DELETE.getIcon());
		jResetItem.setActionCommand(ACTION_DELETE_NAME);
		jResetItem.addActionListener(this);
		add(jResetItem);

		addSeparator();

		jEditContainer = new JMenuItem(GuiShared.get().containerEdit());
		jEditContainer.setIcon(Images.EDIT_EDIT.getIcon());
		jEditContainer.setActionCommand(ACTION_EDIT_CONTAINER);
		jEditContainer.addActionListener(this);
		add(jEditContainer);

		jResetContainer = new JMenuItem(GuiShared.get().containerDelete());
		jResetContainer.setIcon(Images.EDIT_DELETE.getIcon());
		jResetContainer.setActionCommand(ACTION_DELETE_CONTAINERS);
		jResetContainer.addActionListener(this);
		add(jResetContainer);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		itemNames = new ArrayList<UserItem<Long, String>>();
		containerNames = new ArrayList<UserItem<Long, String>>();
		for (Asset asset : menuData.getAssets()) {
			itemNames.add(new UserName(asset));
			for (Asset parent : asset.getParents()) {
				containerNames.add(new UserName(parent));
			}
				
		}
		jEditItem.setEnabled(itemNames.size() == 1);
		jResetItem.setEnabled(program.getUserNameSettingsPanel() != null && program.getUserNameSettingsPanel().contains(itemNames));
		jEditContainer.setEnabled(menuData.getAssets().size() == 1 && !menuData.getAssets().get(0).getParents().isEmpty());
		jResetContainer.setEnabled(program.getUserNameSettingsPanel() != null && program.getUserNameSettingsPanel().contains(containerNames));
		if (menuData.getAssets().size() == 1) {
			selected = menuData.getAssets().get(0);
		} else {
			selected = null;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_EDIT_NAME.equals(e.getActionCommand())) {
			program.getUserNameSettingsPanel().edit(itemNames.get(0));
		}
		if (ACTION_DELETE_NAME.equals(e.getActionCommand())) {
			program.getUserNameSettingsPanel().delete(itemNames);
		}
		if (ACTION_EDIT_CONTAINER.equals(e.getActionCommand())) {
			if (selected != null) {
				Asset value = jContainerDialog.showDialog(selected);
				if (value != null) {
					program.getUserNameSettingsPanel().edit(new UserName(value));
				}
			}
		}
		if (ACTION_DELETE_CONTAINERS.equals(e.getActionCommand())) {
			program.getUserNameSettingsPanel().delete(containerNames);
		}
	}

	public static class AssetMenuData extends MenuData<Asset> {

		public AssetMenuData(List<Asset> items) {
			super(items);
			setAssets(items);
		}
	}
}
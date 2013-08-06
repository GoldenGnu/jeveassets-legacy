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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagColor;
import net.nikr.eve.jeveasset.data.types.TagsType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuTags<T> extends JAutoMenu<T> {

	private enum TagsAction {
		ACTION_NEW_TAG,
	}

	private JMenuItem jNew;
	private List<TagsType> tagsTypes = new ArrayList<TagsType>();
	private ListenerClass listener = new ListenerClass();
	private JTagsDialog jTagsDialog;

	public JMenuTags(Program program) {
		super(GuiShared.get().tags(), program);

		setIcon(Images.TAG_GRAY.getIcon());

		jTagsDialog = new JTagsDialog(program);

		jNew = new JMenuItem(GuiShared.get().tagsNew(), Images.EDIT_ADD.getIcon());
		jNew.setActionCommand(TagsAction.ACTION_NEW_TAG.name());
		jNew.addActionListener(listener);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		tagsTypes = menuData.getTags();

		removeAll();

		add(jNew);

		Set<Tag> allTags = new HashSet<Tag>(Settings.get().getTags().values());

		if (!allTags.isEmpty()) {
			addSeparator();
		}

		JCheckBoxMenuItem jMenuItem;
		for (Tag tag : allTags) {
			Integer count = menuData.getTagCount().get(tag);
			if (count == null) {
				count = 0;
			}
			boolean selected = count == tagsTypes.size();
			if (selected) {
				count = 0;
			}
			jMenuItem = new JTagMenuItem(tag, count);
			jMenuItem.addActionListener(listener);
			jMenuItem.setSelected(selected);
			add(jMenuItem);
		}
	}

	private void addTag(Tag tag) {
		if (tag != null && !tag.getName().isEmpty()) {
			Tag settingsTag = Settings.get().getTags().get(tag.getName());
			if (settingsTag != null) { //Update
				TagColor color = tag.getColor(); //Save color
				tag = settingsTag;
				tag.setColor(color); //Update color
			} else { //Add new
				Settings.get().getTags().put(tag.getName(), tag);
			}
			for (TagsType tagsType : tagsTypes) {
				//Add tag to item
				tagsType.getTags().add(tag);
				//Add ID to tag
				tag.getIDs().add(tagsType.getTagID());
				//Update settings
				Settings.get().getTags(tagsType.getTagID()).add(tag);
			}
			program.updateTags();
		}
	}

	private void removeTag(Tag tag) {
		if (tag != null) {
			for (TagsType tagsType : tagsTypes) {
				//Remove tag form item
				tagsType.getTags().remove(tag);
				//Remove ID from tag
				tag.getIDs().remove(tagsType.getTagID());
				//Update settings
				Settings.get().getTags(tagsType.getTagID()).remove(tag);
			}
			//Remove empty tags
			Tag settingsTag = Settings.get().getTags().get(tag.getName());
			if (settingsTag.getIDs().isEmpty()) {
				Settings.get().getTags().remove(tag.getName());
			}
			program.updateTags();
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object object = e.getSource();
			if (object instanceof JTagMenuItem){
				JTagMenuItem jCheckBoxMenuItem = (JTagMenuItem) object;
				Tag tag = jCheckBoxMenuItem.getTag();
				if (!jCheckBoxMenuItem.isSelected()) { //State change before this is called
					removeTag(tag);
				} else {
					addTag(tag);
				}
			} else if (TagsAction.ACTION_NEW_TAG.name().equals(e.getActionCommand())) {
				Tag tag = jTagsDialog.show();
				addTag(tag);
			}
		}
	}

	private static class JTagMenuItem extends JCheckBoxMenuItem {

		private Tag tag;

		public JTagMenuItem(Tag tag, Integer count) {
			super(GuiShared.get().tagsName(tag.getName(), count));
			this.tag = tag;
		}

		public Tag getTag() {
			return tag;
		}
	}
}
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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import javax.swing.JList;
import javax.swing.ListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveJList<E> extends JList<E> {

	private final static Logger LOG = LoggerFactory.getLogger(MoveJList.class);

	private static final long serialVersionUID = 1l;

	public MoveJList() {
		super(new EditableListModel<E>());
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel
	public MoveJList(ListModel<E> dataModel) {
		EditableListModel<E> m = new EditableListModel<>();
		for (int i = 0; i < m.getSize(); ++i) {
			m.add(m.getElementAt(i));
		}
		setModel(m);
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public EditableListModel<E> getEditableModel() {
		return (EditableListModel<E>) getModel();
	}

	/**
	 *
	 * @param to
	 * @param limit
	 * @return true if all the items were added.
	 */
	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public boolean move(MoveJList<E> to, int limit) {
		EditableListModel<E> fModel = getEditableModel();
		EditableListModel<E> tModel = to.getEditableModel();
		for (E ss : getSelectedValuesList()) {
			if (fModel.contains(ss)) {
				if (to.getModel().getSize() < limit) {
					LOG.debug("Moving {}", ss);
					if (fModel.remove(ss)) {
						tModel.add(ss);
					}
				} else {
					setSelectedIndices(new int[]{});
					to.setSelectedIndices(new int[]{});
					return false;
				}
			}
		}
		setSelectedIndices(new int[]{});
		to.setSelectedIndices(new int[]{});
		return true;
	}
}

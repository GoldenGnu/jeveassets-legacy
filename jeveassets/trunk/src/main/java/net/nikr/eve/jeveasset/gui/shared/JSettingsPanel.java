/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Window;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;


public abstract class JSettingsPanel {

	protected Program program;
	protected String title;
	protected JPanel jPanel;
	protected GroupLayout layout;
	protected Window parent;

	public JSettingsPanel(Program program, Window parent, String title ) {
		this.program = program;
		this.title = title;
		this.parent = parent;

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public abstract void save();
	public abstract void load();
	public abstract void closed();
	public abstract JComponent getDefaultFocus();

	public JPanel getPanel() {
		return jPanel;
	}

	public String getTitle() {
		return title;
	}


}

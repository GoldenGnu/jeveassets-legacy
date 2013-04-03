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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class AssetsToolSettingsPanel extends JSettingsPanel {

	private JCheckBox jReprocessColors;
	private JCheckBox jSellOrders;
	private JCheckBox jBuyOrders;
	private JCheckBox jContracts;
	private JTextField jMaxOrderAge;

	public AssetsToolSettingsPanel(final Program program, final SettingsDialog settingsDialog, final DefaultMutableTreeNode parentNode) {
		super(program, settingsDialog, DialoguesSettings.get().assets(), Images.TOOL_ASSETS.getIcon(), parentNode);

		jReprocessColors = new JCheckBox(DialoguesSettings.get().showSellOrReprocessColours());
		jSellOrders = new JCheckBox(DialoguesSettings.get().includeSellOrders());
		jBuyOrders = new JCheckBox(DialoguesSettings.get().includeBuyOrders());
		jContracts = new JCheckBox(DialoguesSettings.get().includeContracts());
		jMaxOrderAge = new JIntegerField("0", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
		JLabel jMaxOrderAgeLabel = new JLabel(DialoguesSettings.get().maximumPurchaseAge());
		JLabel jDaysLabel = new JLabel(DialoguesSettings.get().days());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jReprocessColors)
				.addComponent(jSellOrders)
				.addComponent(jBuyOrders)
				.addComponent(jContracts)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jMaxOrderAgeLabel)
					.addComponent(jMaxOrderAge, 75, 75, 75)
					.addComponent(jDaysLabel)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jReprocessColors, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jBuyOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jContracts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(20)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMaxOrderAgeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMaxOrderAge, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDaysLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	@Override
	public boolean save() {
		int maximumPurchaseAge;
		try {
			maximumPurchaseAge = Integer.valueOf(jMaxOrderAge.getText());
		} catch (NumberFormatException ex) {
			maximumPurchaseAge = 0;
		}
		boolean update = jReprocessColors.isSelected() != program.getSettings().isReprocessColors()
						|| jSellOrders.isSelected() != program.getSettings().isIncludeSellOrders()
						|| jBuyOrders.isSelected() != program.getSettings().isIncludeBuyOrders()
						|| jContracts.isSelected() != program.getSettings().isIncludeContracts()
						|| maximumPurchaseAge != program.getSettings().getMaximumPurchaseAge()
						;
		program.getSettings().setReprocessColors(jReprocessColors.isSelected());
		program.getSettings().setIncludeSellOrders(jSellOrders.isSelected());
		program.getSettings().setIncludeBuyOrders(jBuyOrders.isSelected());
		program.getSettings().setIncludeContracts(jContracts.isSelected());
		program.getSettings().setMaximumPurchaseAge(maximumPurchaseAge);
		return update;
	}

	@Override
	public void load() {
		jReprocessColors.setSelected(program.getSettings().isReprocessColors());
		jSellOrders.setSelected(program.getSettings().isIncludeSellOrders());
		jBuyOrders.setSelected(program.getSettings().isIncludeBuyOrders());
		jContracts.setSelected(program.getSettings().isIncludeContracts());
		jMaxOrderAge.setText(String.valueOf(program.getSettings().getMaximumPurchaseAge()));
	}
}

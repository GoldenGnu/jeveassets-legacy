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

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class JMenuInfo {

	private static Border border = null;

	private JMenuInfo() {}

	public static void asset(final JComponent jComponent, final List<Asset> list) {
		infoItem(jComponent, new ArrayList<InfoItem>(list));
	}

	public static void overview(final JComponent jComponent, final List<Overview> list) {
		infoItem(jComponent, new ArrayList<InfoItem>(list));
	}

	private static void infoItem(final JComponent jComponent, final List<InfoItem> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			double averageValue = 0;
			double totalValue = 0;
			long totalCount = 0;
			double totalVolume = 0;
			double totalReprocessed = 0;
			for (InfoItem infoItem : list) {
				totalValue = totalValue + infoItem.getValue();
				totalCount = totalCount + infoItem.getCount();
				totalVolume = totalVolume + infoItem.getVolumeTotal();
				totalReprocessed = totalReprocessed + infoItem.getValueReprocessed();
			}
			if (totalCount > 0 && totalValue > 0) {
				averageValue = totalValue / totalCount;
			}
			createMenuItem(jPopupMenu, Formater.iskFormat(totalValue), GuiShared.get().selectionValue(), Images.TOOL_VALUES.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(totalReprocessed), GuiShared.get().selectionValueReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(averageValue), GuiShared.get().selectionAverage(), Images.ASSETS_AVERAGE.getIcon());

			createMenuItem(jPopupMenu, Formater.doubleFormat(totalVolume), GuiShared.get().selectionVolume(), Images.ASSETS_VOLUME.getIcon());

			createMenuItem(jPopupMenu, Formater.itemsFormat(totalCount), GuiShared.get().selectionCount(), Images.EDIT_ADD.getIcon());
		}
	}

	public static void marketOrder(final JComponent jComponent, final List<MarketOrder> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			double sellOrdersTotal = 0;
			double buyOrdersTotal = 0;
			double toCoverTotal = 0;
			double escrowTotal = 0;
			for (MarketOrder marketOrder : list) {
				if (marketOrder.getBid() < 1) { //Sell
					sellOrdersTotal += marketOrder.getPrice() * marketOrder.getVolRemaining();
				} else { //Buy
					buyOrdersTotal += marketOrder.getPrice() * marketOrder.getVolRemaining();
					escrowTotal += marketOrder.getEscrow();
					toCoverTotal += (marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow();
				}
			}
			createMenuItem(jPopupMenu, Formater.iskFormat(sellOrdersTotal), GuiShared.get().selectionOrdersSell(), Images.ORDERS_SELL.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(buyOrdersTotal), GuiShared.get().selectionOrdersBuy(), Images.ORDERS_BUY.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(escrowTotal), GuiShared.get().selectionOrdersEscrow(), Images.ORDERS_ESCROW.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(toCoverTotal), GuiShared.get().selectionOrdersToCover(), Images.ORDERS_TO_COVER.getIcon());
		}
	}

	public static void industryJob(final JComponent jComponent, final List<IndustryJob> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			int count = 0;
			double success = 0;
			double total = 0.0;
			for (IndustryJob industryJob : list) {
				if (industryJob.getActivity() == IndustryJob.IndustryActivity.ACTIVITY_REVERSE_INVENTION && industryJob.isCompleted()) {
					count++;
					if (industryJob.getState() == IndustryJob.IndustryJobState.STATE_DELIVERED) {
						success++;
					}
				}
			}
			if (count > 0 && success > 0) {
				total = (success / count);
			}
			createMenuItem(jPopupMenu, Formater.percentFormat(total), GuiShared.get().selectionInventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		}
	}

	public static void stockpileItem(final JComponent jComponent, final List<StockpileItem> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			double volumnNow = 0;
			double volumnNeeded = 0;
			double valueNow = 0;
			double valueNeeded = 0;

			for (StockpileItem item : list) {
				volumnNow = volumnNow + item.getVolumeNow();
				if (item.getVolumeNeeded() < 0) { //Only add if negative
					volumnNeeded = volumnNeeded + item.getVolumeNeeded();
				}
				valueNow = valueNow + item.getValueNow();
				if (item.getValueNeeded() < 0) { //Only add if negative
					valueNeeded = valueNeeded + item.getValueNeeded();
				}
			}
			createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleNow());

			createMenuItem(jPopupMenu, Formater.iskFormat(valueNow), GuiShared.get().selectionValueNow(), Images.TOOL_VALUES.getIcon());

			createMenuItem(jPopupMenu, Formater.doubleFormat(volumnNow), GuiShared.get().selectionVolumeNow(), Images.ASSETS_VOLUME.getIcon());

			createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleNeeded());

			createMenuItem(jPopupMenu, Formater.iskFormat(valueNeeded), GuiShared.get().selectionValueNeeded(), Images.TOOL_VALUES.getIcon());

			createMenuItem(jPopupMenu, Formater.doubleFormat(volumnNeeded), GuiShared.get().selectionVolumeNeeded(), Images.ASSETS_VOLUME.getIcon());
		}
	}

	public static void material(final JComponent jComponent, final List<Material> selected, final List<Material> all) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			MaterialTotal materialTotal = calcMaterialTotal(new ArrayList<Material>(selected), all);

			createMenuItem(jPopupMenu, Formater.iskFormat(materialTotal.getTotalValue()), GuiShared.get().selectionValue(), Images.TOOL_VALUES.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(materialTotal.getAverageValue()), GuiShared.get().selectionAverage(), Images.ASSETS_AVERAGE.getIcon());

			createMenuItem(jPopupMenu, Formater.itemsFormat(materialTotal.getTotalCount()), GuiShared.get().selectionCount(), Images.EDIT_ADD.getIcon());
		}
	}

	static MaterialTotal calcMaterialTotal(final List<Material> selectedList, final List<Material> all) {
		//Remove none-Material classes
		List<Material> selected = new ArrayList<Material>(selectedList);
		for (int i = 0; i < selected.size(); i++) {
			Object object = selected.get(i);
			if (!(object instanceof Material)) {
				selected.remove(i);
				i--;
			}
		}
		long totalCount = 0;
		double totalValue = 0;
		double averageValue = 0;
		boolean add;
		for (Material material : all) {
			if (material.getType() == MaterialType.LOCATIONS) {
				add = false;
				for (Material selectedMaterial : selected) {
					//Equals anything/all
					if (selectedMaterial.getType() == MaterialType.SUMMARY_ALL) {
						add = true;
						break;
					}
					//Equals group
					if (selectedMaterial.getType() == MaterialType.SUMMARY_TOTAL
							&& material.getGroup().equals(selectedMaterial.getName())
							) {
						add = true;
						break;
					}
					//Equals name
					if (selectedMaterial.getType() == MaterialType.SUMMARY
							&& material.getName().equals(selectedMaterial.getName())
							) {
						add = true;
						break;
					}
					//Equals location
					if (selectedMaterial.getType() == MaterialType.LOCATIONS_ALL
							&& material.getLocation().equals(selectedMaterial.getLocation())
							) {
						add = true;
						break;
					}
					//Equals location and group
					if (selectedMaterial.getType() == MaterialType.LOCATIONS_TOTAL
							&& material.getLocation().equals(selectedMaterial.getLocation())
							&& material.getGroup().equals(selectedMaterial.getName())
							) {
						add = true;
						break;
					}
					//Equals location and name
					if (selectedMaterial.getType() == MaterialType.LOCATIONS
							&& material.getLocation().equals(selectedMaterial.getLocation())
							&& material.getName().equals(selectedMaterial.getName())
							) {
						add = true;
						break;
					}
				}
				if (add) {
					totalCount = totalCount + material.getCount();
					totalValue = totalValue + material.getValue();
				}
			}
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		return new MaterialTotal(totalCount, totalValue, averageValue);
	}

	public static void module(final JComponent jComponent, final List<Module> selected) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			createDefault(jPopupMenu);

			long totalCount = 0;
			double totalValue = 0;
			double averageValue = 0;
			Module totalShip = null;
			Module totalModule = null;
			Module totalAll = null;
			for (int i = 0; i < selected.size(); i++) {
				Object object = selected.get(i);
				if (object instanceof Module) {
					Module module = (Module) object;
					if (module.getName().equals(TabsLoadout.get().totalShip())) {
						totalShip = module;
					} else if (module.getName().equals(TabsLoadout.get().totalModules())) {
						totalModule = module;
					} else if (module.getName().equals(TabsLoadout.get().totalAll())) {
						totalAll = module;
						break;
					} else {
						totalCount = totalCount + module.getCount();
						totalValue = totalValue + module.getValue();
					}
				}
			}
			if (totalAll != null) { //All
				totalValue = totalAll.getValue();
				totalCount = totalAll.getCount();
			} else {
				if (totalModule != null) { //Module IS total
					totalValue = totalModule.getValue();
					totalCount = totalModule.getCount();
				}
				if (totalShip != null) { //Ship is added to total
					totalValue = totalValue + totalShip.getValue();
					totalCount = totalCount + totalShip.getCount();
				}
			}
			if (totalCount > 0 && totalValue > 0) {
				averageValue = totalValue / totalCount;
			}
			createMenuItem(jPopupMenu, Formater.iskFormat(totalValue), GuiShared.get().selectionValue(), Images.TOOL_VALUES.getIcon());

			createMenuItem(jPopupMenu, Formater.iskFormat(averageValue), GuiShared.get().selectionAverage(), Images.ASSETS_AVERAGE.getIcon());

			createMenuItem(jPopupMenu, Formater.itemsFormat(totalCount), GuiShared.get().selectionCount(), Images.EDIT_ADD.getIcon());
		}
	}

	private static void createMenuItem(final JPopupMenu jPopupMenu, final String text, final String toolTipText, final Icon icon) {
		JMenuItem jMenuItem = new JMenuItem(text);
		jMenuItem.setToolTipText(toolTipText);
		jMenuItem.setEnabled(false);
		jMenuItem.setDisabledIcon(icon);
		jMenuItem.setForeground(Color.BLACK);
		jMenuItem.setHorizontalAlignment(SwingConstants.RIGHT);
		jPopupMenu.add(jMenuItem);
	}
	private static void createMenuItemGroup(final JPopupMenu jPopupMenu, final String text) {
		JMenuItem jMenuItem = new JMenuItem(text);
		jMenuItem.setEnabled(false);
		if (border == null) {
			border = BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 0, 0, 0, jMenuItem.getBackground().darker())
					, BorderFactory.createMatteBorder(1, 0, 0, 0, jMenuItem.getBackground().brighter()))
				, BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, jMenuItem.getBackground().brighter())
					, BorderFactory.createMatteBorder(0, 0, 1, 0, jMenuItem.getBackground().darker())));
		}
		jMenuItem.setForeground(Color.BLACK);
		jMenuItem.setBorder(border);
		jMenuItem.setBorderPainted(true);
		jPopupMenu.add(jMenuItem);
	}

	private static void createDefault(final JPopupMenu jPopupMenu) {
		JMenuItem jMenuItem;

		jPopupMenu.addSeparator();

		jMenuItem = new JMenuItem(GuiShared.get().selectionTitle());
		jMenuItem.setDisabledIcon(Images.DIALOG_ABOUT.getIcon());
		jMenuItem.setEnabled(false);
		jMenuItem.setForeground(Color.BLACK);
		jPopupMenu.add(jMenuItem);

		JPanel jSpacePanel = new JPanel();
		jSpacePanel.setOpaque(false);
		jSpacePanel.setMinimumSize(new Dimension(50, 5));
		jSpacePanel.setPreferredSize(new Dimension(50, 5));
		jSpacePanel.setMaximumSize(new Dimension(50, 5));
		jPopupMenu.add(jSpacePanel);
	}

	public static class MaterialTotal {
		private long totalCount;
		private double totalValue;
		private double averageValue;

		public MaterialTotal(final long totalCount, final double totalValue, final double averageValue) {
			this.totalCount = totalCount;
			this.totalValue = totalValue;
			this.averageValue = averageValue;
		}

		public double getAverageValue() {
			return averageValue;
		}

		public long getTotalCount() {
			return totalCount;
		}

		public double getTotalValue() {
			return totalValue;
		}
	}

	public interface InfoItem {
		double getValue();
		long getCount();
		double getVolumeTotal();
		double getValueReprocessed();
	}
}
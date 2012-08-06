/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Asset.PriceMode;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import uk.me.candle.eve.pricing.options.LocationType;


public class PriceDataSettingsPanel extends JSettingsPanel {

	public static final String ACTION_SOURCE_SELECTED = "ACTION_SOURCE_SELECTED";
	public static final String ACTION_LOCATION_SELECTED = "ACTION_LOCATION_SELECTED";

	private JRadioButton jRadioRegions;
	private JRadioButton jRadioSystems;
	private JRadioButton jRadioStations;
	private JComboBox jRegions;
	private JComboBox jSystems;
	private JComboBox jStations;
	private JComboBox jPriceType;
	private JComboBox jSource;

	private EventList<RegionType> regions = new BasicEventList<RegionType>();
	private AutoCompleteSupport<RegionType> regionsAutoComplete;
	private AutoCompleteSupport<Location> systemsAutoComplete;
	private AutoCompleteSupport<Location> stationsAutoComplete;

	public PriceDataSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().priceData(), Images.SETTINGS_PRICE_DATA.getIcon());

		ListenerClass listener = new ListenerClass();

		EventList<Location> systemsEventList = new BasicEventList<Location>();
		EventList<Location> stationsEventList = new BasicEventList<Location>();
		String system = "";
		String station = "";
		for (Location location : program.getSettings().getLocations().values()) {
			if (location.isStation()) {
				stationsEventList.add(location);
				if (station.length() < location.getName().length()) {
					station = location.getName();
				}
			}
			if (location.isSystem()) {
				systemsEventList.add(location);
				if (system.length() < location.getName().length()) {
					system = location.getName();
				}
			}
		}
		SortedList<Location> systemsSortedList = new SortedList<Location>(systemsEventList);
		SortedList<Location> stationsSortedList = new SortedList<Location>(stationsEventList);

		ButtonGroup group = new ButtonGroup();

		jRadioRegions = new JRadioButton();
		jRadioRegions.setActionCommand(ACTION_LOCATION_SELECTED);
		jRadioRegions.addActionListener(listener);
		group.add(jRadioRegions);

		jRadioSystems = new JRadioButton();
		jRadioSystems.setActionCommand(ACTION_LOCATION_SELECTED);
		jRadioSystems.addActionListener(listener);
		group.add(jRadioSystems);

		jRadioStations = new JRadioButton();
		jRadioStations.setActionCommand(ACTION_LOCATION_SELECTED);
		jRadioStations.addActionListener(listener);
		group.add(jRadioStations);

		JLabel jRegionsLabel = new JLabel(DialoguesSettings.get().includeRegions());
		jRegions = new JComboBox();
		jRegions.getEditor().getEditorComponent().addFocusListener(listener);
		regionsAutoComplete = AutoCompleteSupport.install(jRegions, regions, new RegionTypeFilterator());
		regionsAutoComplete.setStrict(true);

		JLabel jSystemsLabel = new JLabel(DialoguesSettings.get().includeSystems());
		jSystems = new JComboBox();
		jSystems.setPrototypeDisplayValue(system);
		jSystems.getEditor().getEditorComponent().addFocusListener(listener);
		systemsAutoComplete = AutoCompleteSupport.install(jSystems, systemsSortedList, new LocationsFilterator());
		systemsAutoComplete.setStrict(true);

		JLabel jStationsLabel = new JLabel(DialoguesSettings.get().includeStations());
		jStations = new JComboBox();
		jStations.setPrototypeDisplayValue(station);
		jStations.getEditor().getEditorComponent().addFocusListener(listener);
		stationsAutoComplete = AutoCompleteSupport.install(jStations, stationsSortedList, new LocationsFilterator());
		stationsAutoComplete.setStrict(true);

		JLabel jPriceTypeLabel = new JLabel(DialoguesSettings.get().price());
		jPriceType = new JComboBox(PriceMode.values());

		JLabel jSourceLabel = new JLabel(DialoguesSettings.get().source());
		jSource = new JComboBox(PriceSource.values());
		jSource.setActionCommand(ACTION_SOURCE_SELECTED);
		jSource.addActionListener(listener);

		JTextArea jWarning = new JTextArea(DialoguesSettings.get().changeSourceWarning());
		jWarning.setFont(this.getPanel().getFont());
		jWarning.setBackground(this.getPanel().getBackground());
		jWarning.setLineWrap(true);
		jWarning.setWrapStyleWord(true);
		jWarning.setFocusable(false);
		jWarning.setEditable(false);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRegionsLabel)
						.addComponent(jSystemsLabel)
						.addComponent(jStationsLabel)
						.addComponent(jPriceTypeLabel)
						.addComponent(jSourceLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jRadioRegions)
						.addComponent(jRadioSystems)
						.addComponent(jRadioStations)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRegions, 200, 200, 200)
						.addComponent(jSystems, 200, 200, 200)
						.addComponent(jStations, 200, 200, 200)
						.addComponent(jPriceType, 200, 200, 200)
						.addComponent(jSource, 200, 200, 200)
					)
				)
				.addComponent(jWarning)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRegionsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRadioRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSystemsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRadioSystems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSystems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jStationsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRadioStations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jPriceTypeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jWarning, 48, 48, 48)
		);
	}

	@Override
	public boolean save() {
		Object object;
		List<Long> locations = null;
		LocationType locationType = null;
		if (jRadioRegions.isSelected()) {
			locationType = LocationType.REGION;
			RegionType regionType = (RegionType) jRegions.getSelectedItem();
			locations = regionType.getRegions();
		} else if (jRadioSystems.isSelected()) {
			locationType = LocationType.SYSTEM;
			Location location = (Location) jSystems.getSelectedItem();
			locations = Collections.singletonList(location.getLocationID());
		} else if (jRadioStations.isSelected()) {
			locationType = LocationType.STATION;
			Location location = (Location) jStations.getSelectedItem();
			locations = Collections.singletonList(location.getLocationID());
		}

		//Price Type (can be a String)
		object = jPriceType.getSelectedItem();
		PriceMode priceType;
		if (object  instanceof PriceMode) {
			priceType = (PriceMode) object;
		} else {
			priceType = Asset.getPriceType();
		}

		//Source
		PriceSource source = (PriceSource) jSource.getSelectedItem();

		//Eval if table need to be updated
		boolean updateTable = !priceType.equals(Asset.getPriceType());

		//Update settings
		program.getSettings().setPriceDataSettings(new PriceDataSettings(locationType, locations, source));
		Asset.setPriceType(priceType);

		//Update table if needed
		return updateTable;
	}

	@Override
	public void load() {
		jSource.setSelectedItem(program.getSettings().getPriceDataSettings().getSource());
	}

	private void updateSource(final PriceSource source) {
		final List<Long> locations = program.getSettings().getPriceDataSettings().getLocations();
		final LocationType locationType = program.getSettings().getPriceDataSettings().getLocationType();

		//Price Types
		jPriceType.setModel(new DefaultComboBoxModel(source.getPriceTypes()));
		jPriceType.setSelectedItem(Asset.getPriceType());
		if (source.getPriceTypes().length <= 0) { //Empty
			jPriceType.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jPriceType.setEnabled(false);
		} else {
			jPriceType.setEnabled(true);
		}

		//Default
		jRadioRegions.setSelected(true);

	//REGIONS
		if (source.supportsMultipleRegions() || source.supportsSingleRegion()) {
			try {
				regions.getReadWriteLock().writeLock().lock();
				regions.clear();
				if (source.supportsMultipleRegions()) {
					regions.addAll(RegionType.getMultipleLocations());
				} else { //Single Region
					regions.addAll(RegionType.getSingleLocations());
				}
			} finally {
				regions.getReadWriteLock().writeLock().unlock();
			}
			regionsAutoComplete.removeFirstItem();
			jRegions.setEnabled(true);
			jRadioRegions.setEnabled(true);
		} else {
			jRegions.setEnabled(false);
			jRadioRegions.setEnabled(false);
			regionsAutoComplete.setFirstItem(RegionType.NOT_CONFIGURABLE);
		}
		jRegions.setSelectedIndex(0);
		if (locationType == LocationType.REGION && jRadioRegions.isEnabled()) {
			if (!locations.isEmpty()) {
				for (RegionType regionType : RegionType.values()) {
					if (regionType.getRegions().equals(locations)) {
						jRegions.setSelectedItem(regionType);
						break;
					}
				}
			}
			jRadioRegions.setSelected(true);
		}
	//SYSTEM
		if (source.supportsSystem()) {
			systemsAutoComplete.removeFirstItem();
			jRadioSystems.setEnabled(true);
			jSystems.setEnabled(true);
		} else {
			jRadioSystems.setEnabled(false);
			jSystems.setEnabled(false);
			systemsAutoComplete.setFirstItem(new Location(-1, DialoguesSettings.get().notConfigurable(), -1, "", -1));
		}
		if (locationType == LocationType.SYSTEM && jRadioSystems.isEnabled()) {
			if (!locations.isEmpty()) {
				jSystems.setSelectedItem(program.getSettings().getLocations().get(locations.get(0)));
			}
			jRadioSystems.setSelected(true);
		} else {
			jSystems.setSelectedIndex(0);
		}
	//STATION
		if (source.supportsStation()) {
			stationsAutoComplete.removeFirstItem();
			jRadioStations.setEnabled(true);
			jStations.setEnabled(true);
		} else {
			jRadioStations.setEnabled(false);
			jStations.setEnabled(false);
			stationsAutoComplete.setFirstItem(new Location(-1, DialoguesSettings.get().notConfigurable(), -1, "", -1));
		}
		if (locationType == LocationType.STATION && jRadioStations.isEnabled()) {
			if (!locations.isEmpty()) {
				jStations.setSelectedItem(program.getSettings().getLocations().get(locations.get(0)));
			}
			jRadioStations.setSelected(true);
		} else {
			jStations.setSelectedIndex(0);
		}
	}

	private class ListenerClass implements ActionListener, FocusListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_SOURCE_SELECTED.equals(e.getActionCommand())) {
				PriceSource priceSource = (PriceSource) jSource.getSelectedItem();
				updateSource(priceSource);
			}
			if (ACTION_LOCATION_SELECTED.equals(e.getActionCommand())) {
				if (jRadioRegions.isSelected()) {
					jRegions.requestFocusInWindow();
				} else if (jRadioSystems.isSelected()) {
					jSystems.requestFocusInWindow();
				} else if (jRadioStations.isSelected()) {
					jStations.requestFocusInWindow();
				}
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			if (jRegions.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioRegions.setSelected(true);
			}
			if (jSystems.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioSystems.setSelected(true);
			}
			if (jStations.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioStations.setSelected(true);
			}
		}

		@Override
		public void focusLost(final FocusEvent e) { }
	}

	static class LocationsFilterator implements TextFilterator<Location> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Location element) {
			baseList.add(element.getName());
		}
	}
	static class RegionTypeFilterator implements TextFilterator<RegionType> {
		@Override
		public void getFilterStrings(final List<String> baseList, final RegionType element) {
			baseList.add(element.toString());
		}
	}
}

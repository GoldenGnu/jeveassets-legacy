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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileDialog extends JDialogCentered {

	private enum StockpileDialogAction {
		FILTER_LOCATIONS,
		VALIDATE,
		CANCEL,
		OK,
		ADD_STATION,
		ADD_SYSTEM,
		ADD_REGION,
		ADD_OWNER,
		ADD_FLAG,
		ADD_CONTAINER,
		REMOVE
	}

	private static final int FIELD_WIDTH = 470;

	private final JTextField jName;
	private final JCheckBox jInventory;
	private final JCheckBox jBuyOrders;
	private final JCheckBox jSellOrders;
	private final JCheckBox jJobs;
	private final JDoubleField jMultiplier;
	private final JButton jOK;
	private final List<StockpilePanel> locationPanels = new ArrayList<StockpilePanel>();
	private final List<StockpilePanel> ownerPanels = new ArrayList<StockpilePanel>();
	private final List<StockpilePanel> flagPanels = new ArrayList<StockpilePanel>();
	private final List<StockpilePanel> containerPanels = new ArrayList<StockpilePanel>();
	private final JPanel jLocationsPanel;
	private final JPanel jOwnersPanel;
	private final JPanel jFlagsPanel;
	private final JPanel jContainersPanel;
	private final BorderPanel jFilterPanel;

	private Stockpile stockpile;
	private Stockpile cloneStockpile;
	private boolean updated = false;

	//Data
	private final EventList<Location> stations;
	private final EventList<Location> systems;
	private final EventList<Location> regions;
	private final Set<String> myLocations;
	private final List<Owner> owners;
	private final List<ItemFlag> itemFlags;
	private final List<String> containers;

	public StockpileDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileTitle(), Images.TOOL_STOCKPILE.getImage());
	//Data
		//Locations - static
		List<Location> stationList = new ArrayList<Location>();
		List<Location> systemList = new ArrayList<Location>();
		List<Location> regionList = new ArrayList<Location>();
		for (Location location : StaticData.get().getLocations().values()) {
			if (location.isStation()) {
				stationList.add(location);
			} else if (location.isSystem()) {
				systemList.add(location);
			} else if (location.isRegion()) {
				regionList.add(location);
			}
		}
		Collections.sort(stationList);
		Collections.sort(systemList);
		Collections.sort(regionList);
		stations = new BasicEventList<Location>();
		try {
			stations.getReadWriteLock().writeLock().lock();
			stations.clear();
			stations.addAll(stationList);
		} finally {
			stations.getReadWriteLock().writeLock().unlock();
		}
		systems = new BasicEventList<Location>();
		try {
			systems.getReadWriteLock().writeLock().lock();
			systems.clear();
			systems.addAll(systemList);
		} finally {
			systems.getReadWriteLock().writeLock().unlock();
		}
		regions = new BasicEventList<Location>();
		try {
			regions.getReadWriteLock().writeLock().lock();
			regions.clear();
			regions.addAll(regionList);
		} finally {
			regions.getReadWriteLock().writeLock().unlock();
		}
		//Flags - static
		itemFlags = new ArrayList<ItemFlag>(StaticData.get().getItemFlags().values());
		Collections.sort(itemFlags);
		//Owners - not static
		owners = new ArrayList<Owner>();
		//myLocations - not static
		myLocations = new HashSet<String>();
		//Containers - not static
		containers = new ArrayList<String>();

		ListenerClass listener = new ListenerClass();
	//Name
		BorderPanel jNamePanel = new BorderPanel(TabsStockpile.get().name());
		jName = new JTextField();
		jName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				jName.selectAll();
			}
		});
		jName.addCaretListener(listener);
		jNamePanel.add(jName);
	//Include
		BorderPanel jIncludePanel = new BorderPanel(TabsStockpile.get().include());

		jInventory = new JCheckBox(TabsStockpile.get().inventory());
		jIncludePanel.add(jInventory);

		jBuyOrders = new JCheckBox(TabsStockpile.get().buyOrders());
		jIncludePanel.add(jBuyOrders);

		jSellOrders = new JCheckBox(TabsStockpile.get().sellOrders());
		jIncludePanel.add(jSellOrders);

		jJobs = new JCheckBox(TabsStockpile.get().jobs());
		jIncludePanel.add(jJobs);
	//Multiplier
		BorderPanel jMultiplierPanel = new BorderPanel(TabsStockpile.get().multiplier());

		jMultiplier = new JDoubleField("1", DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jMultiplier.setAutoSelectAll(true);
		jMultiplierPanel.add(jMultiplier);
	//Add Filter
		JToolBar jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);
		jToolBar.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().addFilter()));

		JButton jStation = new JButton(TabsStockpile.get().station(), Images.LOC_STATION.getIcon());
		jStation.setActionCommand(StockpileDialogAction.ADD_STATION.name());
		jStation.addActionListener(listener);
		addToolButton(jToolBar, jStation);

		JButton jSystem = new JButton(TabsStockpile.get().system(), Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(StockpileDialogAction.ADD_SYSTEM.name());
		jSystem.addActionListener(listener);
		addToolButton(jToolBar, jSystem);

		JButton jRegion = new JButton(TabsStockpile.get().region(), Images.LOC_REGION.getIcon());
		jRegion.setActionCommand(StockpileDialogAction.ADD_REGION.name());
		jRegion.addActionListener(listener);
		addToolButton(jToolBar, jRegion);

		JButton jOwner = new JButton(TabsStockpile.get().owner(), Images.LOC_OWNER.getIcon());
		jOwner.setActionCommand(StockpileDialogAction.ADD_OWNER.name());
		jOwner.addActionListener(listener);
		addToolButton(jToolBar, jOwner);

		JButton jFlag = new JButton(TabsStockpile.get().flag(), Images.LOC_FLAG.getIcon());
		jFlag.setActionCommand(StockpileDialogAction.ADD_FLAG.name());
		jFlag.addActionListener(listener);
		addToolButton(jToolBar, jFlag);

		JButton jContainer = new JButton(TabsStockpile.get().container(), Images.LOC_CONTAINER_WHITE.getIcon());
		jContainer.setActionCommand(StockpileDialogAction.ADD_CONTAINER.name());
		jContainer.addActionListener(listener);
		addToolButton(jToolBar, jContainer, 80, SwingConstants.LEFT);
	//Filters
		jFilterPanel = new BorderPanel(TabsStockpile.get().filters(), BorderPanel.Alignment.VERTICAL);

		jLocationsPanel = new JPanel();
		jFilterPanel.add(jLocationsPanel);

		jOwnersPanel = new JPanel();
		jFilterPanel.add(jOwnersPanel);

		jFlagsPanel = new JPanel();
		jFilterPanel.add(jFlagsPanel);

		jContainersPanel = new JPanel();
		jFilterPanel.add(jContainersPanel);
	//OK
		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileDialogAction.OK.name());
		jOK.addActionListener(listener);
		jOK.setEnabled(false);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jNamePanel.getPanel(), FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addComponent(jIncludePanel.getPanel(), FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addComponent(jMultiplierPanel.getPanel(), FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addComponent(jToolBar, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addComponent(jFilterPanel.getPanel(), FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		final int TOOLBAR_HEIGHT = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jNamePanel.getPanel())
				.addComponent(jMultiplierPanel.getPanel())
				.addComponent(jIncludePanel.getPanel())
				.addComponent(jToolBar, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
				.addComponent(jFilterPanel.getPanel())
				.addGap(15)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton) {
		addToolButton(jToolBar, jButton, 80, SwingConstants.LEFT);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton, final int width, final int alignment) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(alignment);
		jToolBar.add(jButton);
	}

	private Stockpile getStockpile() {
		//Name
		String name = jName.getText();
		//Containers
		List<String> container = new ArrayList<String>();
		for (StockpilePanel panel : containerPanels) {
			container.addAll(panel.getContainer());
		}
		//Locations
		List<Location> location = new ArrayList<Location>();
		for (StockpilePanel panel : locationPanels) {
			location.addAll(panel.getLocation());
		}
		//Owners
		List<Long> owner = new ArrayList<Long>();
		for (StockpilePanel panel : ownerPanels) {
			owner.addAll(panel.getOwner());
		}
		//Flag
		List<Integer> flag = new ArrayList<Integer>();
		for (StockpilePanel panel : flagPanels) {
			flag.addAll(panel.getFlag());
		}
		//Multiplier
		double multiplier;
		try {
			multiplier = Double.valueOf(jMultiplier.getText());
		} catch (NumberFormatException ex) {
			multiplier = 1;
		}
		//Add
		return new Stockpile(name, owner, location, flag, container, jInventory.isSelected(), jSellOrders.isSelected(), jBuyOrders.isSelected(), jJobs.isSelected(), multiplier);
	}

	private void autoValidate() {
		boolean b = true;
		Set<Location> locationsDups = new HashSet<Location>();
		for (StockpilePanel panel : locationPanels) {
			for (Location location : panel.getLocation()) {
				boolean ok = locationsDups.add(location);
				panel.warning(!ok);
				if (!ok) {
					b = false;
				}
			}
			if (!panel.isValid()) {
				b = false;
			}
		}
		Set<String> containerDups = new HashSet<String>();
		for (StockpilePanel panel : containerPanels) {
			for (String container : panel.getContainer()) {
				boolean ok = containerDups.add(container);
				panel.warning(!ok);
				if (!ok) {
					b = false;
				}
			}
		}
		Set<Integer> flagDups = new HashSet<Integer>();
		for (StockpilePanel panel : flagPanels) {
			for (Integer flag : panel.getFlag()) {
				boolean ok = flagDups.add(flag);
				panel.warning(!ok);
				if (!ok) {
					b = false;
				}
			}
		}
		Set<Long> ownerDups = new HashSet<Long>();
		for (StockpilePanel panel : ownerPanels) {
			for (Long owner : panel.getOwner()) {
				boolean ok = ownerDups.add(owner);
				panel.warning(!ok);
				if (!ok) {
					b = false;
				}
			}
		}
		if (Settings.get().getStockpiles().contains(getStockpile())) {
			if (stockpile != null && stockpile.getName().equals(getStockpile().getName())) {
				jName.setBackground(Color.WHITE);
			} else {
				b = false;
				jName.setBackground(new Color(255, 200, 200));
			}
		} else if (jName.getText().isEmpty()) {
			jName.setBackground(new Color(255, 200, 200));
			b = false;
		} else {
			jName.setBackground(Color.WHITE);
		}
		jOK.setEnabled(b);
	}

	boolean showEdit(final Stockpile stockpile) {
		updateData();
		this.stockpile = stockpile;
		//Title
		this.getDialog().setTitle(TabsStockpile.get().editStockpileTitle());
		//Load
		loadStockpile(stockpile, stockpile.getName());
		//Show
		show();
		return updated;
	}

	Stockpile showAdd() {
		updateData();
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}
	Stockpile showAdd(final String name) {
		updateData();
		jName.setText(name);
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}

	Stockpile showClone(final Stockpile stockpile) {
		updateData();
		cloneStockpile = stockpile.clone();
		//Title
		this.getDialog().setTitle(TabsStockpile.get().cloneStockpileTitle());
		//Load
		loadStockpile(cloneStockpile, "");
		//Show
		show();
		if (updated) {
			return cloneStockpile;
		} else {
			return null;
		}
	}

	private void loadStockpile(Stockpile loadStockpile, String name) {
		//Name
		jName.setText(name);

		//Include
		jInventory.setSelected(loadStockpile.isInventory());
		jSellOrders.setSelected(loadStockpile.isSellOrders());
		jBuyOrders.setSelected(loadStockpile.isBuyOrders());
		jJobs.setSelected(loadStockpile.isJobs());

		//Multiplier
		jMultiplier.setText(Formater.compareFormat(loadStockpile.getMultiplier()));

		//Owners
		Set<Owner> ownersFound = new HashSet<Owner>();
		for (Owner owner : owners) {
			for (Long ownerID : loadStockpile.getOwnerIDs()) {
				if (owner.getOwnerID() == ownerID) {
					ownersFound.add(owner);
				}
			}
		}
		for (Owner owner : ownersFound) {
			ownerPanels.add(new StockpilePanel(owner));
		}
		//Location
		for (Location location : loadStockpile.getLocations()) {
			locationPanels.add(new StockpilePanel(location));
		}
		//Flag
		for (Integer flagID : loadStockpile.getFlagIDs()) {
			ItemFlag itemFlag = StaticData.get().getItemFlags().get(flagID);
			if (itemFlag != null) {
				flagPanels.add(new StockpilePanel(itemFlag));
			}
		}
		//Container
		for (String container : loadStockpile.getContainers()) {
			containerPanels.add(new StockpilePanel(container));
		}

		updatePanels();
	}

	private void updatePanels() {
		updatePanel(containerPanels, jContainersPanel);
		updatePanel(flagPanels, jFlagsPanel);
		updatePanel(locationPanels, jLocationsPanel);
		updatePanel(ownerPanels, jOwnersPanel);
		autoValidate();
		jFilterPanel.setVisible(!containerPanels.isEmpty() || !flagPanels.isEmpty() || !locationPanels.isEmpty() || !ownerPanels.isEmpty());
		this.getDialog().pack();
	}

	private void updatePanel(List<StockpilePanel> panels, JPanel panel) {
		panel.removeAll();
		GroupLayout groupLayout = new GroupLayout(panel);
		panel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(false);
		ParallelGroup horizontalGroup = groupLayout.createParallelGroup();
		SequentialGroup verticalGroup = groupLayout.createSequentialGroup();
		for (StockpilePanel thePanel : panels) {
			horizontalGroup.addComponent(thePanel.getPanel());
			verticalGroup.addComponent(thePanel.getPanel());
		}
		panel.setVisible(!panels.isEmpty());
		groupLayout.setHorizontalGroup(horizontalGroup);
		groupLayout.setVerticalGroup(verticalGroup);
	}

	private void show() {
		updated = false;
		super.setVisible(true);
	}

	private void updateData() {
		stockpile = null;
		cloneStockpile = null;

		//Include
		jInventory.setSelected(true);
		jSellOrders.setSelected(false);
		jBuyOrders.setSelected(false);
		jJobs.setSelected(false);

		//Name
		jName.setText("");

		//Owners
		Map<Long, Owner> ownersById = new HashMap<Long, Owner>();
		for (Account account : program.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				ownersById.put(owner.getOwnerID(), owner);
			}
		}
		owners.clear();
		owners.addAll(ownersById.values());

		//Containers & Locations Loop
		Set<String> containerSet = new HashSet<String>();
		myLocations.clear();
		for (Asset asset : program.getAssetEventList()) {
			if (!asset.getContainer().isEmpty()) {
				containerSet.add(asset.getContainer());
			}
			myLocations.add(asset.getLocation().getLocation());
			myLocations.add(asset.getLocation().getSystem());
			myLocations.add(asset.getLocation().getRegion());
		}
		for (IndustryJob industryJob : program.getIndustryJobsEventList()) {
			myLocations.add(industryJob.getLocation().getLocation());
			myLocations.add(industryJob.getLocation().getSystem());
			myLocations.add(industryJob.getLocation().getRegion());
		}
		for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
			myLocations.add(marketOrder.getLocation().getLocation());
			myLocations.add(marketOrder.getLocation().getSystem());
			myLocations.add(marketOrder.getLocation().getRegion());
		}
		//Containers
		containers.clear();
		containers.addAll(containerSet);
		Collections.sort(containers, new CaseInsensitiveComparator());

		ownerPanels.clear();
		locationPanels.clear();
		flagPanels.clear();
		containerPanels.clear();
		updatePanels();
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
	protected void windowShown() { }

	@Override
	protected void save() {
		if (stockpile != null) { //Edit
			stockpile.update(getStockpile());
		} else if (cloneStockpile != null) { //Clone
			cloneStockpile.update(getStockpile());
			Settings.get().getStockpiles().add(cloneStockpile);
		} else { //Add
			stockpile = getStockpile();
			Settings.get().getStockpiles().add(stockpile);
		}
		updated = true;
		this.setVisible(false);
	}

	private class ListenerClass implements ActionListener, CaretListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileDialogAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (StockpileDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (StockpileDialogAction.ADD_CONTAINER.name().equals(e.getActionCommand())) {
				containerPanels.add(new StockpilePanel(PanelType.CONTAINER));
				updatePanels();
			} else if (StockpileDialogAction.ADD_FLAG.name().equals(e.getActionCommand())) {
				flagPanels.add(new StockpilePanel(PanelType.FLAG));
				updatePanels();
			} else if (StockpileDialogAction.ADD_STATION.name().equals(e.getActionCommand())) {
				locationPanels.add(new StockpilePanel(PanelType.LOCATION_STATION));
				updatePanels();
			} else if (StockpileDialogAction.ADD_SYSTEM.name().equals(e.getActionCommand())) {
				locationPanels.add(new StockpilePanel(PanelType.LOCATION_SYSTEM));
				updatePanels();
			} else if (StockpileDialogAction.ADD_REGION.name().equals(e.getActionCommand())) {
				locationPanels.add(new StockpilePanel(PanelType.LOCATION_REGION));
				updatePanels();
			} else if (StockpileDialogAction.ADD_OWNER.name().equals(e.getActionCommand())) {
				ownerPanels.add(new StockpilePanel(PanelType.OWNER));
				updatePanels();
			}
		}

		@Override
		public void caretUpdate(final CaretEvent e) {
			autoValidate();
		}
	}

	static class OwnerFilterator implements TextFilterator<Owner> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Owner element) {
			baseList.add(element.getName());
		}
	}
	static class ItemFlagFilterator implements TextFilterator<ItemFlag> {
		@Override
		public void getFilterStrings(final List<String> baseList, final ItemFlag element) {
			baseList.add(element.getFlagName());
		}
	}
	static class LocationsFilterator implements TextFilterator<Location> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Location element) {
			baseList.add(element.getLocation());
		}
	}

	static class LocationsMatcher implements Matcher<Location> {

		private Set<String> myLocations;

		public LocationsMatcher(final Set<String> myLocations) {
			this.myLocations = myLocations;
		}

		@Override
		public boolean matches(final Location item) {
			return myLocations.contains(item.getLocation());
		}
	}

	enum PanelType {
		LOCATION_STATION,
		LOCATION_SYSTEM,
		LOCATION_REGION,
		OWNER,
		FLAG,
		CONTAINER
	}

	private class StockpilePanel {
		//GUI
		private final JPanel jPanel;
		private final GroupLayout layout;
		private final JButton jRemove;
		private final JLabel jType;
		private final JLabel jWarning;
		//Location
		private JCheckBox jMyLocations;
		private JComboBox jLocation;
		//Owner
		private JComboBox jOwner;
		//Flag
		private JComboBox jFlag;
		//Container
		private JComboBox jContainer;
		private final ListenerClass listener = new ListenerClass();
		//Data
		private FilterList<Location> locationsFilter;
		private PanelType panelType;

		public StockpilePanel(String container) {
			this(PanelType.CONTAINER);

			jContainer.setSelectedItem(container);
		}

		public StockpilePanel(ItemFlag itemFlag) {
			this(PanelType.FLAG);

			jFlag.setSelectedItem(itemFlag);
		}

		public StockpilePanel(Owner owner) {
			this(PanelType.OWNER);

			jOwner.setSelectedItem(owner);
		}

		public StockpilePanel(Location location) {
			this(location.isStation() ? PanelType.LOCATION_STATION : (location.isSystem() ? PanelType.LOCATION_SYSTEM : PanelType.LOCATION_REGION));
			jMyLocations.setSelected(myLocations.contains(location.getLocation()));
			refilter();
			jLocation.setSelectedItem(location);
		}

		public StockpilePanel(PanelType panelType) {
			this.panelType = panelType;

			jPanel = new JPanel();
			layout = new GroupLayout(jPanel);
			jPanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);

			jRemove = new JButton(Images.EDIT_DELETE.getIcon());
			jRemove.setActionCommand(StockpileDialogAction.REMOVE.name());
			jRemove.addActionListener(listener);

			jWarning = new JLabel(Images.UPDATE_DONE_ERROR.getIcon());

			jType = new JLabel();

			if (panelType == PanelType.CONTAINER) {
				showContainer();
			} else if (panelType == PanelType.FLAG) {
				showFlag();
			} else if (isLocation()) {
				showLocation();
			} else if (panelType == PanelType.OWNER) {
				showOwner();
			}
		}

		private void remove() {
			if (panelType == PanelType.CONTAINER) {
				containerPanels.remove(this);
			} else if (panelType == PanelType.FLAG) {
				flagPanels.remove(this);
			} else if (isLocation()) {
				locationPanels.remove(this);
			} else if (panelType == PanelType.OWNER) {
				ownerPanels.remove(this);
			}
			updatePanels();
		}

		private boolean isLocation() {
			return panelType == PanelType.LOCATION_STATION ||
					panelType == PanelType.LOCATION_SYSTEM ||
					panelType == PanelType.LOCATION_REGION;
		}

		private void refilter() {
			Location location = (Location) jLocation.getSelectedItem();
			if (jMyLocations.isSelected()) {
				locationsFilter.setMatcher(new LocationsMatcher(myLocations));
			} else {
				locationsFilter.setMatcher(null);
			}
			jLocation.setEnabled(true);
			if (locationsFilter.contains(location)) {
				jLocation.setSelectedItem(location);
			} else {
				jLocation.setSelectedIndex(0);
			}
		}

		private void showLocation() {
			jMyLocations = new JCheckBox();
			jMyLocations.setToolTipText(TabsStockpile.get().myLocations());
			jMyLocations.setActionCommand(StockpileDialogAction.FILTER_LOCATIONS.name());
			jMyLocations.addActionListener(listener);

			if (panelType == PanelType.LOCATION_STATION) {
				jType.setIcon(Images.LOC_STATION.getIcon());
				jType.setToolTipText(TabsStockpile.get().station());
				locationsFilter = new FilterList<Location>(stations);
			} else if (panelType == PanelType.LOCATION_SYSTEM) {
				jType.setIcon(Images.LOC_SYSTEM.getIcon());
				jType.setToolTipText(TabsStockpile.get().system());
				locationsFilter = new FilterList<Location>(systems);
			} else if (panelType == PanelType.LOCATION_REGION) {
				jType.setToolTipText(TabsStockpile.get().region());
				jType.setIcon(Images.LOC_REGION.getIcon());
				locationsFilter = new FilterList<Location>(regions);
			}

			jLocation = new JComboBox();
			AutoCompleteSupport<Location> locationsAutoComplete = AutoCompleteSupport.install(jLocation, locationsFilter, new LocationsFilterator());
			locationsAutoComplete.setStrict(true);
			locationsAutoComplete.setCorrectsCase(true);
			jLocation.addItemListener(listener); //Must be added after AutoCompleteSupport

			update();

			refilter();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(jType)
					.addComponent(jWarning)
					.addComponent(jLocation, 0, 0, FIELD_WIDTH)
					.addComponent(jMyLocations)
					.addComponent(jRemove, 30, 30, 30)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup()
					.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jLocation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMyLocations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			);
		}

		private void showOwner() {
			jType.setIcon(Images.LOC_OWNER.getIcon());
			jType.setToolTipText(TabsStockpile.get().owner());

			jOwner = new JComboBox();
			jOwner.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jOwner.addActionListener(listener);

			update();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(jType)
					.addComponent(jWarning)
					.addComponent(jOwner, 0, 0, FIELD_WIDTH)
					.addComponent(jRemove, 30, 30, 30)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOwner, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			);
		}

		private void showFlag() {
			jType.setIcon(Images.LOC_FLAG.getIcon());
			jType.setToolTipText(TabsStockpile.get().flag());

			jFlag = new JComboBox();
			jFlag.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jFlag.addActionListener(listener);

			update();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(jType)
					.addComponent(jWarning)
					.addComponent(jFlag, 0, 0, FIELD_WIDTH)
					.addComponent(jRemove, 30, 30, 30)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFlag, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			);
		}

		private void showContainer() {
			jType.setIcon(Images.LOC_CONTAINER_WHITE.getIcon());
			jType.setToolTipText(TabsStockpile.get().container());

			jContainer = new JComboBox();
			jContainer.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jContainer.addActionListener(listener);

			update();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(jType)
					.addComponent(jWarning)
					.addComponent(jContainer, 0, 0, FIELD_WIDTH)
					.addComponent(jRemove, 30, 30, 30)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContainer, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			);
		}

		public boolean isValid() {
			return (jLocation.getSelectedItem() != null);
		}

		public List<String> getContainer() {
			return getList(jContainer, String.class);
		}

		public List<Integer> getFlag() {
			ItemFlag value = getValue(jFlag, ItemFlag.class);
			if (value != null) {
				return Collections.singletonList(value.getFlagID());
			} else {
				return Collections.emptyList();
			}
		}

		public List<Long> getOwner() {
			Owner value = getValue(jOwner, Owner.class);
			if (value != null) {
				return Collections.singletonList(value.getOwnerID());
			} else {
				return Collections.emptyList();
			}
		}

		public List<Location> getLocation() {
			return getList(jLocation, Location.class);
		}

		private <E> E getValue(JComboBox jComboBox, Class<E> clazz) {
			if (jComboBox != null) {
				Object object = jComboBox.getSelectedItem();
				if (clazz.isInstance(object)) {
					return clazz.cast(object);
				}
			}
			return null;
		}

		private <E> List<E> getList(JComboBox jComboBox, Class<E> clazz) {
			E value = getValue(jComboBox, clazz);
			if (value != null) {
				return Collections.singletonList(value);
			} else {
				return Collections.emptyList();
			}
		}

		public JPanel getPanel() {
			return jPanel;
		}

		public final void update() {
			//Owner
			if (jOwner != null) {
				if (owners.isEmpty()) {
					jOwner.setModel(new DefaultComboBoxModel(owners.toArray()));
					jOwner.setEnabled(false);
				} else {
					Collections.sort(owners);
					jOwner.setModel(new DefaultComboBoxModel(owners.toArray()));
					jOwner.setEnabled(true);
				}
			}
			//Flag
			if (jFlag != null) {
				jFlag.setModel(new DefaultComboBoxModel(itemFlags.toArray()));
			}
			//Location
			if (jMyLocations != null && locationsFilter != null && jLocation != null) {
				jMyLocations.setSelected(true);
				locationsFilter.setMatcher(new LocationsMatcher(myLocations));
				jLocation.setEnabled(true);
				jLocation.setSelectedIndex(0);
			}
			//Container
			if (jContainer != null) {
				if (containers.isEmpty()) {
					jContainer.setModel(new DefaultComboBoxModel(containers.toArray()));
					jContainer.setEnabled(false);
				} else {
					jContainer.setModel(new DefaultComboBoxModel(containers.toArray()));
					jContainer.setEnabled(true);
				}
			}
		}

		private void warning(boolean b) {
			jWarning.setVisible(b);
		}

		private class ListenerClass implements ActionListener, ItemListener {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (StockpileDialogAction.FILTER_LOCATIONS.name().equals(e.getActionCommand())) {
					refilter();
				} else if (StockpileDialogAction.REMOVE.name().equals(e.getActionCommand())) {
					remove();
				} else if (StockpileDialogAction.VALIDATE.name().equals(e.getActionCommand())) {
					autoValidate();
				}
			}
			@Override
			public void itemStateChanged(final ItemEvent e) {
				autoValidate();
			}
		}
	}

	private static class BorderPanel {

		private enum Alignment {
			HORIZONTAL,
			VERTICAL
		}

		private final GroupLayout layout;
		private final JPanel jPanel;
		private final List<JComponent> components = new ArrayList<JComponent>();
		private final Alignment alignment;

		public BorderPanel(String title) {
			this(title, Alignment.HORIZONTAL);
		}

		public BorderPanel(String title, Alignment alignment) {
			this.alignment = alignment;
			jPanel = new JPanel();
			layout = new GroupLayout(jPanel);
			jPanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);
			jPanel.setBorder(BorderFactory.createTitledBorder(title));
		}

		public void add(JComponent jComponent) {
			components.add(jComponent);
			jPanel.removeAll();
			Group horizontalGroup;
			Group verticalGroup;
			if (alignment == Alignment.HORIZONTAL) {
				horizontalGroup = layout.createSequentialGroup();
				verticalGroup = layout.createParallelGroup();
			} else {
				horizontalGroup = layout.createParallelGroup();
				verticalGroup = layout.createSequentialGroup();
			}
			for (JComponent component : components) {
				horizontalGroup.addComponent(component);
				if (alignment == Alignment.HORIZONTAL) {
					verticalGroup.addComponent(component, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
				} else {
					verticalGroup.addComponent(component);
				}
			}
			if (components.size() > 1) {
				horizontalGroup.addGap(0, 0, Integer.MAX_VALUE);
			}
			layout.setHorizontalGroup(horizontalGroup);
			layout.setVerticalGroup(verticalGroup);
		}

		public void setVisible(boolean aFlag) {
			jPanel.setVisible(aFlag);
		}

		public JPanel getPanel() {
			return jPanel;
		}
	}
}

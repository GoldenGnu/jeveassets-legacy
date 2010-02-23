/*
 * Copyright 2009
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
package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.log.Log;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import uk.me.candle.eve.routing.cancel.CancelService;

/**
 *
 * @author Candle
 */
public class RoutingDialogue extends JDialogCentered implements ActionListener {

	public static final String ACTION_ADD = "ACTION_ADD";
	public static final String ACTION_ADD_RANDOM = "ACTION_ADD_RNDOM";
	public static final String ACTION_CLOSE = "ACTION_CLOSE";
	public static final String ACTION_REMOVE = "ACTION_REMOVE";
	public static final String ACTION_CHANGE_ALGORITHM = "ACTION_CHANGE_ALGORITHM";
	public static final String ACTION_CALCULATE = "ACTION_CALCULATE";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	private JButton close;
	private JButton add;
	private JButton remove;
	private JButton calculate;
	private JButton addRandom; // add a waypoint that the user doesn't have assets at.
	private JComboBox algorithm;
	private JTextArea description;
	private MoveJList<SolarSystem> available;
	private MoveJList<SolarSystem> waypoints;
	private JLabel availableRemaining;
	private JLabel waypointsRemaining; // waypoint count
	private ProgressBar progress;
	private JButton cancel;
	private Graph filteredGraph;

	public RoutingDialogue(Program program, Image image) {
		super(program, "Routing", image);

		close = new JButton("Close");
		add = new JButton(">>>");
		remove = new JButton("<<<");
		calculate = new JButton("Calculate Route");
		addRandom = new JButton("Other");
		cancel = new JButton("Cancel");

		close.setActionCommand(ACTION_CLOSE);
		add.setActionCommand(ACTION_ADD);
		remove.setActionCommand(ACTION_REMOVE);
		calculate.setActionCommand(ACTION_CALCULATE);
		addRandom.setActionCommand(ACTION_ADD_RANDOM);
		cancel.setActionCommand(ACTION_CANCEL);

		close.addActionListener(this);
		add.addActionListener(this);
		remove.addActionListener(this);
		calculate.addActionListener(this);
		addRandom.addActionListener(this);
		cancel.addActionListener(this);

		progress = new ProgressBar();
		progress.setValue(0);
		progress.setMaximum(1);
		progress.setMinimum(0);

		algorithm = new JComboBox(new Vector<RoutingAlgorithmContainer>(RoutingAlgorithmContainer.getRegisteredList()));
		algorithm.setSelectedIndex(0);
		algorithm.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeAlgorithm();
			}
		});

		description = new JTextArea();
		setAlgorithmDescriptionText(); // sets the desciption text.
		description.setEditable(false);
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		description.setFont(algorithm.getFont());
		Comparator<SolarSystem> comp = new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem o1, SolarSystem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};

		available = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		available.getEditableModel().setSortComparator(comp);
		waypoints = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		waypoints.getEditableModel().setSortComparator(comp);
		waypointsRemaining = new JLabel();
		availableRemaining = new JLabel();
		updateRemaining();

		doLayout();
		cancel.setEnabled(false); // can't cancel the initial load...
	}

	private void doLayout() {
		JScrollPane descrSP = new JScrollPane(description);
		description.scrollRectToVisible(new Rectangle(1,1,1,1));
		JScrollPane availSP = new JScrollPane(available);
		JScrollPane waypoSP = new JScrollPane(waypoints);

		// widths are defined in here.
    layout.setHorizontalGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(progress, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
									.addComponent(descrSP, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
									.addComponent(algorithm, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
									.addGroup(layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
											.addComponent(availSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
											.addComponent(availableRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
										)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
											.addComponent(add, 80, 80, 80)
											.addComponent(remove, 80, 80, 80)
//											.addComponent(addRandom, 80, 80, 80)
										)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
											.addComponent(calculate, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
											.addComponent(waypointsRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
											.addComponent(waypoSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
											.addComponent(cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
										)
									)
								)
								.addContainerGap()
							)
						);
		// heights are defined here.
    layout.setVerticalGroup(
							layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addContainerGap()
								.addComponent(progress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(algorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(descrSP, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(waypoSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(availSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGroup(layout.createSequentialGroup()
										.addComponent(add)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(remove)
//										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//										.addComponent(addRandom)
									)
								)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(availableRemaining)
									.addComponent(waypointsRemaining)
								)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(calculate)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(cancel)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							)
						);

		setUIEnabled(false);
	}

	private void changeAlgorithm() {
		setAlgorithmDescriptionText();
		updateRemaining();
	}

	private void setAlgorithmDescriptionText() {
		RoutingAlgorithmContainer rac = ((RoutingAlgorithmContainer) algorithm.getSelectedItem());
		description.setText(rac.getBasicDescription() + "\n\n" + rac.getTechnicalDescription());
//		description.scrollRectToVisible(new Rectangle(1,1,1,1)); This has no effect. WHY?!
	}

	private void updateRemaining() {
		updateWaypointsRemaining();
		updateAvailableRemaining();
	}
	
	private void updateWaypointsRemaining() {
		int max = ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getWaypointLimit();
		int cur = waypoints.getModel().getSize();
		if (max < cur) {
			waypointsRemaining.setForeground(Color.RED);
		} else {
			waypointsRemaining.setForeground(Color.BLACK);
		}
		waypointsRemaining.setText(cur + " of " + max + " allowed.");
	}

	private void updateAvailableRemaining() {
		int cur = available.getModel().getSize();
		int tot = cur + waypoints.getModel().getSize();
		availableRemaining.setText(cur + " of " + tot + " total");
	}

	@Override
	protected void save() {
		this.setVisible(false);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return close;
	}

	@Override
	protected JButton getDefaultButton() {
		return close;
	}

	@Override
	protected void windowActivated() {
	}

	@Override
	protected void windowShown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				windowShownInner();
			}
		}, "routing dialogue ")
		.start();
	}

	protected void windowShownInner() {
		description.scrollRectToVisible(new Rectangle(1,1,1,1));
		Settings settings = program.getSettings();

		progress.setMaximum(settings.getJumps().size() + 1 + program.getTablePanel().getFilteredAssets().size());
		progress.setMinimum(0);
		progress.setValue(0);
								
		// build the graph.
		// filter the solarsystems based on the settings.
		filteredGraph = getGraphFromJumps(progress, settings);

		// select the active places.
		SortedSet<SolarSystem> allLocs = new TreeSet<SolarSystem>(new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem o1, SolarSystem o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareTo(n2);
			}
		});
		for (EveAsset ea : program.getTablePanel().getFilteredAssets()) {
			SolarSystem loc = findNodeForLocation(filteredGraph, ea.getLocationID());
			if (loc != null) {
				allLocs.add(loc);
			} else {
				Log.debug("ignoring " + ea.getLocation());
			}
			progress.setValue(progress.getValue() + 1);
		}

		available.getEditableModel().addAll(allLocs);

		progress.setValue(progress.getMaximum());
		updateRemaining();
		setUIEnabled(true);
	}

	/**
	 * get the grapg from the supplied settings - progress is updated, one per jump via the given Progress instance.
	 * @param progress
	 * @param settings
	 * @return
	 */
	protected Graph getGraphFromJumps(Progress progress, Settings settings) {
		Graph g = new Graph();
		for (Jump jump : settings.getJumps()) { // this way we exclude the locations that are unreachable.
			SolarSystem f = null;
			SolarSystem t = null;
			for (Node n : g.getNodes()) {
				SolarSystem s = (SolarSystem)n;
				if (s.getSolarSystemID() == jump.getFrom().getSolarSystemID()) f = s;
				if (s.getSolarSystemID() == jump.getTo().getSolarSystemID()) t = s;
			}
			if (f == null) f = new SolarSystem(jump.getFrom());
			if (t == null) t = new SolarSystem(jump.getTo());
			if (!isSystemExcluded(settings, t) && !isSystemExcluded(settings, f)) {
				g.addEdge(new Edge(f, t));
			}
			progress.setValue(progress.getValue() + 1);
		}
		return g;
	}

	/**
	 * place holder method for future filtering of jumps/systems.
	 * @param settings
	 * @param solarsystem
	 * @return
	 */
	protected boolean isSystemExcluded(Settings settings, SolarSystem solarsystem) {
		return false;
	}

	/**
	 *
	 * @param g
	 * @param locationID
	 * @return null if the system is unreachable (e.g. w-space)
	 */
	private SolarSystem findNodeForLocation(Graph g, int locationID) {
		int ssid = -1;
		// from http://wiki.eve-id.net/APIvff2_Corp_AssetList_XML
		if (66000000 <= locationID && locationID <= 66999999) {
			// normal station office
			ssid = program.getSettings().getLocations().get(locationID - 6000001).getSolarSystemID();
		} else if (67000000 <= locationID && locationID <= 67999999) {
			// conquerable station office
			ssid = program.getSettings().getConquerableStations().get(locationID - 6000000).getSolarSystemID();
		} else if (60014861 <= locationID && locationID <= 60014928) {
			// conq stations; have these in the list already.
			ssid = program.getSettings().getConquerableStations().get(locationID).getSolarSystemID();
		} else if (60000000 <= locationID && locationID <= 61000000) {
			// normal station?
			ssid = program.getSettings().getLocations().get(locationID).getSolarSystemID();
		} else if (61000000 <= locationID) {
			// More conquerable statins (player built?)
			ssid = program.getSettings().getConquerableStations().get(locationID).getSolarSystemID();
		} else if (31000000 <= locationID && locationID <= 32000000) {
			return null; // unreachable system. (w-space)
		} else {
			// something else... like in space.
			ssid = locationID;//program.getSettings().getLocations().get(locationID).getSolarSystemID();
		}
		if (ssid < 0) {
			throw new RuntimeException("Unknown Location: " + locationID + ", ssid = " + ssid);
		}
		for (Node n : g.getNodes()) {
			if (n instanceof SolarSystem) {
				SolarSystem ss = (SolarSystem) n;
				if (ss.getSolarSystemID() == ssid) {
					return ss;
				}
			}
		}
		return null;
		//throw new RuntimeException("Unknown Location: " + locationID + ", ssid = " + ssid);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Log.debug(e.getActionCommand());
		if (ACTION_ADD.equals(e.getActionCommand())) {
			move(available, waypoints, ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getWaypointLimit());
		} else if (ACTION_REMOVE.equals(e.getActionCommand())) {
			move(waypoints, available, Integer.MAX_VALUE);
		} else if (ACTION_CHANGE_ALGORITHM.equals(e.getActionCommand())) {
			// this isn't used.
		} else if (ACTION_CALCULATE.equals(e.getActionCommand())) {
			processRoute();
		} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
			cancelProcessing();
		}
	}

	/**
	 * Moves the selectewd items in the 'from' JList to the 'to' JList.
	 *
	 * @param from
	 * @param to
	 * @param limit
	 * @return true if all the items were moved.
	 */
	private boolean move(MoveJList<SolarSystem> from, MoveJList<SolarSystem> to, int limit) {
		boolean b = from.move(to, limit);
		updateRemaining();
		return b;
	}

	private void processRoute() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				processRouteInner();
			}
		}, "Route Processor").start();
	}

	private void processRouteInner() {
		if (waypoints.getModel().getSize() <= 2) {
			JOptionPane.showMessageDialog(getDialog(), "There is little point in trying to calculate the\n" +
							"optimal route between two points, since there is only\n" +
							"one possible solution", "Not calculating", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			// disable the UI controls
			setUIEnabled(false);
			List<Node> inputWaypoints = new ArrayList<Node>(waypoints.getEditableModel().getAll());

			List<Node> route = ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).execute(
							progress, filteredGraph, inputWaypoints);

			StringBuilder sb = new StringBuilder("The suggested route has ");
			sb.append(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getLastDistance());
			sb.append(" jumps and is:\n");
			for (Node ss : route) {
				sb.append(" * ");
				sb.append(ss.getName());
				sb.append('\n');
			}
			sb.append("Generating this route took ");
			sb.append((int)Math.floor(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getLastTimeTaken() / 1000));
			sb.append(" seconds.");

			JOptionPane.showMessageDialog(getDialog()
							, sb.toString()
							, "Route"
							, JOptionPane.INFORMATION_MESSAGE);

		} catch (DisconnectedGraphException dce) {
			JOptionPane.showMessageDialog(getDialog()
							, dce.getMessage()
							, "Error"
							, JOptionPane.ERROR_MESSAGE);
		} finally {
			setUIEnabled(true);
		}
	}

	private void setUIEnabled(boolean b) {
		close.setEnabled(b);
		add.setEnabled(b);
		remove.setEnabled(b);
		calculate.setEnabled(b);
		addRandom.setEnabled(b);
		algorithm.setEnabled(b);
		description.setEnabled(b);
		available.setEnabled(b);
		waypoints.setEnabled(b);
		waypointsRemaining.setEnabled(b);
		availableRemaining.setEnabled(b);
		cancel.setEnabled(!b);
	}

	private void cancelProcessing() {
		((RoutingAlgorithmContainer)algorithm.getSelectedItem()).getCancelService().cancel();
	}

	/**
	 * A GUI compatable container for the routing algorithms.
	 */
	private static class RoutingAlgorithmContainer {

		RoutingAlgorithm contained;

		public RoutingAlgorithmContainer(RoutingAlgorithm contained) {
			this.contained = contained;
		}

		public int getWaypointLimit() {
			return contained.getWaypointLimit();
		}

		public String getName() {
			return contained.getName();
		}

		public String getTechnicalDescription() {
			return contained.getTechnicalDescription();
		}

		public String getBasicDescription() {
			return contained.getBasicDescription();
		}

		public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
			return contained.execute(progress, g, assetLocations);
		}

		public long getLastTimeTaken() {
			return contained.getLastTimeTaken();
		}

		public int getLastDistance() {
			return contained.getLastDistance();
		}

		public CancelService getCancelService() {
			return contained.getCancelService();
		}

		@Override
		public String toString() {
			return getName();
		}

		public static List<RoutingAlgorithmContainer> getRegisteredList() {
			List<RoutingAlgorithmContainer> list = new ArrayList<RoutingAlgorithmContainer>();
			for (RoutingAlgorithm ra : RoutingAlgorithm.getRegisteredList()) {
				list.add(new RoutingAlgorithmContainer(ra));
			}
			return list;
		}
	}

	class DummyProgress implements Progress {

		@Override
		public int getMaximum() {
			return 0;
		}

		@Override
		public void setMaximum(int maximum) {
		}

		@Override
		public int getMinimum() {
			return 0;
		}

		@Override
		public void setMinimum(int minimum) {
		}

		@Override
		public int getValue() {
			return 0;
		}

		@Override
		public void setValue(int value) {
		}
	}

	class ProgressBar extends JProgressBar implements Progress {

		private static final long serialVersionUID = 1l;
	}
}

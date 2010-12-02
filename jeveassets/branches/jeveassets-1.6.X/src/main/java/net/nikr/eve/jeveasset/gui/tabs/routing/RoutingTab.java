/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RoutingTab extends JMainTab implements ActionListener {

	private final static Logger LOG = LoggerFactory.getLogger(RoutingTab.class);

	public static final String ACTION_ADD = "ACTION_ADD";
	public static final String ACTION_ADD_RANDOM = "ACTION_ADD_RNDOM";
	public static final String ACTION_REMOVE = "ACTION_REMOVE";
	public static final String ACTION_CHANGE_ALGORITHM = "ACTION_CHANGE_ALGORITHM";
	public static final String ACTION_CALCULATE = "ACTION_CALCULATE";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
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
	protected Graph filteredGraph;
	private JTextArea lastResultArea;
	private Thread updateDataThread;

	
	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected RoutingTab(boolean load) {
		super(load);
	}

	public RoutingTab(Program program) {
		super(program, "Routing", Images.ICON_TOOL_ROUTING, true);

		add = new JButton(">>>");
		remove = new JButton("<<<");
		calculate = new JButton("Calculate Route");
		addRandom = new JButton("Other");
		cancel = new JButton("Cancel");

		add.setActionCommand(ACTION_ADD);
		remove.setActionCommand(ACTION_REMOVE);
		calculate.setActionCommand(ACTION_CALCULATE);
		addRandom.setActionCommand(ACTION_ADD_RANDOM);
		cancel.setActionCommand(ACTION_CANCEL);

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

		lastResultArea = new JTextArea();
		lastResultArea.setEditable(false);
		//lastResultArea.setEnabled(false);
		//lastResultArea.setText("Once a route has been found, it will be displayed here.");

		doLayout();
		cancel.setEnabled(false); // can't cancel the initial load...
	}

	private void doLayout() {
		JScrollPane descrSP = new JScrollPane(description);
		description.scrollRectToVisible(new Rectangle(1,1,1,1));
		JScrollPane availSP = new JScrollPane(available);
		JScrollPane waypoSP = new JScrollPane(waypoints);
		JScrollPane routeSP = new JScrollPane(lastResultArea);

		// widths are defined in here.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(progress, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
						.addComponent(descrSP, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
						.addComponent(algorithm, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(availSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
									.addComponent(availableRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									.addComponent(calculate, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									.addComponent(cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
								)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(add, 80, 80, 80)
									.addComponent(remove, 80, 80, 80)
									//.addComponent(addRandom, 80, 80, 80)
								)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(waypoSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
									.addComponent(waypointsRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									.addComponent(routeSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
								)
							)
						)
						.addContainerGap()
					)
				);
		// heights are defined here.
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(progress, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(algorithm, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(descrSP, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(waypoSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(availSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(add, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							.addComponent(remove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							//.addComponent(addRandom)
						)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(availableRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(waypointsRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addGroup(layout.createSequentialGroup()
							.addComponent(calculate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							.addComponent(cancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						)
						.addComponent(routeSP, GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE)
					)
					.addContainerGap()
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
		description.setCaretPosition(0); //This should work
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

	private void windowShownInner() {
		description.scrollRectToVisible(new Rectangle(1,1,1,1));
		Settings settings = program.getSettings();

		progress.setMaximum(settings.getJumps().size() + 1 + program.getAssetsTab().getFilteredAssets().size());
		progress.setMinimum(0);
		progress.setValue(0);

		//Only need to build the graph once
		if (filteredGraph == null){
			progress.setMaximum(settings.getJumps().size() + 1 + program.getAssetsTab().getFilteredAssets().size());
			buildGraph(settings, progress);
		} else {
			progress.setMaximum(program.getAssetsTab().getFilteredAssets().size());
		}
		processFilteredAssets(settings, progress);
	}

	protected void buildGraph(Settings settings, Progress progress) {
		// build the graph.
		// filter the solarsystems based on the settings.
		filteredGraph = new Graph();

		for (Jump jump : settings.getJumps()) { // this way we exclude the locations that are unreachable.
			SolarSystem f = null;
			SolarSystem t = null;
			for (Node n : filteredGraph.getNodes()) {
				SolarSystem s = (SolarSystem)n;
				if (s.getSystemID() == jump.getFrom().getSystemID()) f = s;
				if (s.getSystemID() == jump.getTo().getSystemID()) t = s;
			}
			if (f == null) f = new SolarSystem(jump.getFrom());
			if (t == null) t = new SolarSystem(jump.getTo());
			filteredGraph.addEdge(new Edge(f, t));
			progress.setValue(progress.getValue() + 1);
		}
	}

	protected void processFilteredAssets(Settings settings, Progress progress) {
		// select the active places.
		SortedSet<SolarSystem> allLocs = new TreeSet<SolarSystem>(new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem o1, SolarSystem o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareTo(n2);
			}
		});
		for (EveAsset ea : program.getAssetsTab().getFilteredAssets()) {
			SolarSystem loc = findNodeForLocation(filteredGraph, ea.getSolarSystemID());
			if (loc != null) {
				allLocs.add(loc);
			} else {
				LOG.debug("ignoring {}", ea.getLocation());
			}
			progress.setValue(progress.getValue() + 1);
		}

		available.getEditableModel().addAll(allLocs);
		if (progress.getMaximum() == 0) progress.setMaximum(1);
		progress.setValue(progress.getMaximum());
		updateRemaining();
		setUIEnabled(true);
	}

	/**
	 *
	 * @param g
	 * @param locationID
	 * @return null if the system is unreachable (e.g. w-space)
	 */
	private SolarSystem findNodeForLocation(Graph g, long locationID) {
		if (locationID < 0) {
			throw new RuntimeException("Unknown Location: " + locationID);
		}
		for (Node n : g.getNodes()) {
			if (n instanceof SolarSystem) {
				SolarSystem ss = (SolarSystem) n;
				if (ss.getSystemID() == locationID) {
					return ss;
				}
			}
		}
		return null;
		//throw new RuntimeException("Unknown Location: " + locationID + ", ssid = " + ssid);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.debug(e.getActionCommand());
		if (ACTION_ADD.equals(e.getActionCommand())) {
			move(available, waypoints, ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getWaypointLimit());
		} else if (ACTION_REMOVE.equals(e.getActionCommand())) {
			move(waypoints, available, Integer.MAX_VALUE);
//		} else if (ACTION_CHANGE_ALGORITHM.equals(e.getActionCommand())) {
//			// this isn't used.
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
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "There is little point in trying to calculate the\n" +
							"optimal route between two or fewer points, since there is only\n" +
							"one possible solution", "Not calculating", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			// disable the UI controls
			setUIEnabled(false);
			List<Node> inputWaypoints = new ArrayList<Node>(waypoints.getEditableModel().getAll());

			List<Node> route = executeRouteFinding(inputWaypoints);

			StringBuilder sb = new StringBuilder();
			for (Node ss : route) {
				sb.append(ss.getName());
				sb.append('\n');
			}
			int time = (int)Math.floor(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getLastTimeTaken() / 1000);
			sb.append("Generating this route took ");
			sb.append(time);
			sb.append(" second");
			if (time != 1) {
				sb.append("s");
			}
			sb.append(".");

			JOptionPane.showMessageDialog(program.getMainWindow().getFrame()
							, "A route consisting of " +
							  ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getLastDistance() +
							  " jumps has been found.\nIt took " +
							  (int)Math.floor(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getLastTimeTaken() / 1000) +
							  " seconds"
							, "Route"
							, JOptionPane.INFORMATION_MESSAGE);

			lastResultArea.setText(sb.toString());
			lastResultArea.setEnabled(true);

		} catch (DisconnectedGraphException dce) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame()
							, dce.getMessage()
							, "Error"
							, JOptionPane.ERROR_MESSAGE);
		} finally {
			setUIEnabled(true);
		}
	}

	protected List<Node> executeRouteFinding(List<Node> inputWaypoints) {
		List<Node> route = ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).execute(progress, filteredGraph, inputWaypoints);
		return route;
	}

	private void setUIEnabled(boolean b) {
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

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(false);
	}

	@Override
	public void updateData() {
		//Wait for process to complete
		if (updateDataThread != null){
			while (updateDataThread.isAlive()){ 

			}
		}
		//Do everything the constructor does...
		available.getEditableModel().clear();
		waypoints.getEditableModel().clear();
		algorithm.setSelectedIndex(0);
		lastResultArea.setText("Once a route has been found,\nit will be displayed here.");
		lastResultArea.setCaretPosition(0);
		lastResultArea.setEnabled(false);
		updateRemaining();
		setUIEnabled(false);
		cancel.setEnabled(false);
		
		updateDataThread = new Thread(new Runnable() {
			@Override
			public void run() {
				windowShownInner();
			}
		}, "routing dialogue ");
		updateDataThread.start();
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {}

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
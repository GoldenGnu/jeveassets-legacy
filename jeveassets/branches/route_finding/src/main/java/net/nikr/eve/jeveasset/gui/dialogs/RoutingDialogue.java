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

import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.log.Log;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;

/**
 *
 * @author Candle
 */
public class RoutingDialogue extends JDialogCentered implements ActionListener {

	public static final String ACTION_ADD = "ACTION_ADD";
	public static final String ACTION_REMOVE = "ACTION_REMOVE";
	public static final String ACTION_CHANGE_ALGORITHM = "ACTION_CHANGE_ALGORITHM";
	public static final String ACTION_CALCULATE = "ACTION_CALCULATE";
	private JButton close;
	private JButton add;
	private JButton remove;
	private JButton calculate;
	private JButton addRandom; // add a waypoint that the user doesn't have assets at.
	private JComboBox algorithm;
	private JTextArea description;
	private JList available;
	private JList waypoints;
	private JLabel remaining; // waypoint count
	Graph filteredGraph;

	public RoutingDialogue(Program program, Image image) {
		super(program, "Routing", image);

		close = new JButton("Close");
		add = new JButton(">>>");
		remove = new JButton("<<<");
		calculate = new JButton("Calculate Route");
		addRandom = new JButton("Other");

		algorithm = new JComboBox(new Vector<RoutingAlgorithmContainer>(RoutingAlgorithmContainer.getRegisteredList()));
		algorithm.setSelectedIndex(0);
		algorithm.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeAlgorithm();
			}
		});

		description = new JTextArea(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getDescription());
		description.setEditable(false);
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		available = new JList(new WaypointListModel());
		waypoints = new JList(new WaypointListModel());
		remaining = new JLabel();
		updateRemaining();

		JScrollPane descrSP = new JScrollPane(description);
		JScrollPane availSP = new JScrollPane(available);
		JScrollPane waypoSP = new JScrollPane(waypoints);


		// widths are defined in here.
		layout.setHorizontalGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(descrSP, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE).addComponent(algorithm, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(availSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(add, 80, 80, 80).addComponent(remove, 80, 80, 80).addComponent(addRandom, 80, 80, 80)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(calculate, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(remaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(waypoSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))).addContainerGap()));
		// heights are defined here.
		layout.setVerticalGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(algorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(descrSP, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(waypoSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(availSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup().addComponent(add).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(remove).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(addRandom))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(remaining).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(calculate).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		setUIEnabled(false);
	}

	private void changeAlgorithm() {
		description.setText(((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getDescription());
		updateRemaining();
	}

	private void updateRemaining() {
		int max = ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).getWaypointLimit();
		int cur = waypoints.getModel().getSize();
		if (max < cur) {
			remaining.setForeground(Color.RED);
		} else {
			remaining.setForeground(Color.BLACK);
		}
		remaining.setText("" + (max - cur) + " remaining");
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
		Settings settings = program.getSettings();

		final ProgressDialogue pgp = new ProgressDialogue(getDialog(), true);
		pgp.setMaximum(settings.getJumps().size() + 1 + program.getTablePanel().getFilteredAssets().size());
		pgp.setMinimum(0);
		pgp.setValue(0);
		pgp.setLocationRelativeTo(getDialog());
		//pgp.setVisible(true);
								
		// build the graph.
		// filter the solarsystems based on the settings.
		filteredGraph = new Graph();

		for (Jump jump : settings.getJumps()) { // this way we exclude the locations that are unreachable.
			filteredGraph.addEdge(new Edge(
							new SolarSystem(jump.getFrom()), new SolarSystem(jump.getTo())));
			pgp.setValue(pgp.getValue() + 1);
		}
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
			}
			pgp.setValue(pgp.getValue() + 1);
		}

		((WaypointListModel) available.getModel()).addAll(allLocs);

		pgp.setValue(pgp.getMaximum());
		setUIEnabled(true);
		pgp.setVisible(false);
		pgp.dispose();
	}

	/**
	 *
	 * @param g
	 * @param locationID
	 * @return null if the system is unreachable (e.g. w-space)
	 */
	private SolarSystem findNodeForLocation(Graph g, int locationID) {
		int ssid = -1;
		// from http://wiki.eve-id.net/APIv2_Corp_AssetList_XML
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
		throw new RuntimeException("Unknown Location: " + locationID + ", ssid = " + ssid);
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
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					processRoute();
				}
			});
		}
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @param limit
	 * @return true if all the items were added.
	 */
	private boolean move(JList from, JList to, int limit) {
		for (Object obj : from.getSelectedValues()) {
			if (to.getModel().getSize() < limit) {
				((WaypointListModel) to.getModel()).add((SolarSystem) obj);
				((WaypointListModel) to.getModel()).remove((SolarSystem) obj);
			} else {
				return false;
			}
		}
		return true;
	}

	private void processRoute() {
		// disable the UI controls
		setUIEnabled(false);
		List<Node> inputWaypoints = new ArrayList<Node>(((WaypointListModel) waypoints.getModel()).getAll());

		List<Node> route = ((RoutingAlgorithmContainer) algorithm.getSelectedItem()).execute(
						new DummyProgress(), filteredGraph, inputWaypoints);

		StringBuilder sb = new StringBuilder("The suggested route is:");
		for (Node ss : route) {
			sb.append(ss.getName());
		}

		JOptionPane.showMessageDialog(null, sb.toString());

		setUIEnabled(true);
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
		remaining.setEnabled(b);
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

		public String getDescription() {
			return contained.getDescription();
		}

		public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
			return contained.execute(progress, g, assetLocations);
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

	class WaypointListModel extends AbstractListModel {

		private static final long serialVersionUID = 1l;
		List<SolarSystem> backed = new ArrayList<SolarSystem>();

		public WaypointListModel() {
		}

		public WaypointListModel(List<SolarSystem> initial) {
			backed.addAll(initial);
		}

		List<? extends SolarSystem> getAll() {
			return Collections.unmodifiableList(backed);
		}

		@Override
		public int getSize() {
			return backed.size();
		}

		@Override
		public Object getElementAt(int index) {
			return backed.get(index);
		}

		public Node remove(int index) {
			Node b = backed.remove(index);
			changed();
			return b;
		}

		public boolean remove(SolarSystem o) {
			boolean b = backed.remove(o);
			changed();
			return b;
		}

		public boolean add(SolarSystem e) {
			boolean b = backed.add(e);
			changed();
			return b;
		}

		public boolean addAll(Collection<? extends SolarSystem> c) {
			boolean b = backed.addAll(c);
			changed();
			return b;
		}

		void changed() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireContentsChanged(this, 0, backed.size() - 1);
				}
			});
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

	class ProgressDialogue extends JDialog implements Progress {

		private static final long serialVersionUID = 1l;
		ProgressBar pb;

		public ProgressDialogue(Dialog owner, boolean modal) {
			super(owner, modal);
			pb = new ProgressBar();
			JLabel la = new JLabel("Loading Jump information.");
			la.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			setLayout(new BorderLayout(2, 2));
			add(la, BorderLayout.NORTH);
			add(pb, BorderLayout.CENTER);
			pack();
		}

		@Override
		public void setValue(int n) {
			pb.setValue(n);
		}

		@Override
		public void setMinimum(int n) {
			pb.setMinimum(n);
		}

		@Override
		public void setMaximum(int n) {
			pb.setMaximum(n);
		}

		@Override
		public int getValue() {
			return pb.getValue();
		}

		@Override
		public int getMinimum() {
			return pb.getMinimum();
		}

		@Override
		public int getMaximum() {
			return pb.getMaximum();
		}
	}
}

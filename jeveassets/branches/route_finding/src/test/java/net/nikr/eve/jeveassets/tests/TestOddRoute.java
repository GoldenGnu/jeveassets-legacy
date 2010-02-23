package net.nikr.eve.jeveassets.tests;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.dialogs.RoutingDialogue;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.io.local.JumpsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.log.Log;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.Progress;

/**
 *
 * @author Candle
 */
public class TestOddRoute {
	private static final Logger logger = Logger.getLogger(TestOddRoute.class);

	String[] waypoints = new String[]{
		"Artisine",
		"Deltole",
		"Jolia",
		"Doussivitte",
		"Misneden",
		"Bawilan",
		"Ney",
		"Stegette",
		"Odette",
		"Inghenges",
		"Sileperer"
	};


	@Test
	public void testBF() {
		Log.init(TestOddRoute.class, "aaarrggghhh!");
		BasicConfigurator.configure();
		Settings settings = new Sett();
		LocationsReader.load(settings);
		JumpsReader.load(settings);
		Dialogue d = new Dialogue(new Prog(settings), ImageGetter.getImage("folder.png"));
		Graph g = d.getGraphFromJumps(new Progre(), settings);

		List<Node> nodes = new ArrayList<Node>();
		for (Node n : g.getNodes()) {
			for (String s : waypoints) {
				if (n.getName().equals(s)) {
					nodes.add(n);
					break;
				}
			}
		}
		for (Node n : nodes) {
			System.out.println(n.getName());
		}
		BF bf = new BF();

		short[][] distances = bf.reduce(new Progre(), g, nodes);
		for (int row = 0; row < distances.length; ++row) {
			for (int col = 0; col < distances[row].length; ++col) {
				System.out.print("" + (distances[row][col] < 10 ? " " : "") + distances[row][col] + " | ");
			}
			System.out.println(" " + nodes.get(row).getName());
		}

		List<Node> route = bf.execute(new Progre(), g, nodes);
		for (Node n : route) {
			System.out.println(n.getName());
		}
		int distance = bf.getLastDistance();
		System.out.println("distance = " + distance);
	}

	private class Dialogue extends RoutingDialogue {
		public Dialogue(Program program, Image image) {
			super(program, image);
		}

		@Override
		public void windowShownInner() {
			super.windowShownInner();
		}

		@Override
		public Graph getGraphFromJumps(Progress progress, Settings settings) {
			return super.getGraphFromJumps(progress, settings);
		}

		@Override
		public boolean isSystemExcluded(Settings settings, SolarSystem solarsystem) {
			return super.isSystemExcluded(settings, solarsystem);
		}

	}

	private class BF extends BruteForce {

		@Override
		public short[][] reduce(Progress progress, Graph g, List<? extends Node> waypoints) {
			return super.reduce(progress, g, waypoints);
		}
	}

	private class NN extends NearestNeighbour {
		
	}

	private class Prog extends Program {

		public Prog(Settings settings) {
			super(settings);
		}

		@Override
		public Settings getSettings() {
			return new Sett();
		}
	}

	private class Sett extends Settings {

		public Sett() {
			super(new Splash(false), false);
		}
	}

	private class Splash extends SplashUpdater {

		public Splash() {
		}

		public Splash(boolean nothing) {
			super(nothing);
		}

		@Override
		public void run() { }

		@Override
		public void setProgress(int n) {
			logger.debug("setting progress: " + n);
		}

		@Override
		public void setText(String s) {
			logger.debug("setting text: " + s);
		}

	}

	private class Progre implements Progress {

		@Override
		public int getMaximum() {
			return 0;
		}

		@Override
		public void setMaximum(int maximum) { }

		@Override
		public int getMinimum() {
			return 0;
		}

		@Override
		public void setMinimum(int minimum) { }

		@Override
		public int getValue() {
			return 0;
		}

		@Override
		public void setValue(int value) { }

	}
}


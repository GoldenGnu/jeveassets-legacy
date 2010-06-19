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

package net.nikr.eve.jeveassets.tests;

import net.nikr.eve.jeveasset.gui.tabs.routing.MoveJList;
import java.util.Comparator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class TestMoveJList {
	MoveJList<Something> a;
	MoveJList<Something> b;
	Something[] somethings;

	@Before
	public void setup() {
		Comparator<Something> comp = new Comparator<Something>() {
			@Override
			public int compare(Something o1, Something o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		a = new MoveJList<Something>();
		a.getEditableModel().setSortComparator(comp);
		b = new MoveJList<Something>();
		b.getEditableModel().setSortComparator(comp);

		somethings = new Something[] {
							new Something("foo")
						, new Something("foobar")
						, new Something("zap")
		};

		for (int i = 0; i < somethings.length; ++i) {
			a.getEditableModel().add(somethings[i]);
		}
	}

	@Test
	public void testMove() {
		a.setSelectedIndices(new int[] {1});
		a.move(b, 10);
		assertEquals(2, a.getEditableModel().getSize());

		assertEquals(somethings[1], b.getEditableModel().getElementAt(0));
	}

	@Test
	public void testMoveAndMoveAgain() {
		a.setSelectedIndices(new int[] {1});
		a.move(b, 10);
		assertEquals(2, a.getEditableModel().getSize());
		assertEquals(somethings[1], b.getEditableModel().getElementAt(0));

		b.setSelectedIndices(new int[] {0});
		b.move(a, 10);
		assertEquals(3, a.getEditableModel().getSize());
		assertEquals(0, b.getEditableModel().getSize());
	}

	@Test
	public void testMoveAllAndMoveBack() {
		a.setSelectedIndices(new int[] {0,1, 2});
		a.move(b, 10);
		assertEquals(0, a.getEditableModel().getSize());
		assertEquals(3, b.getEditableModel().getSize());

		b.setSelectedIndices(new int[] {0, 1, 2});
		b.move(a, 10);
		assertEquals(3, a.getEditableModel().getSize());
		assertEquals(0, b.getEditableModel().getSize());
	}

	class Something {
		String name;

		public Something(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
/*
 * Copyright 2009
 *		Niklas Kyster Rasmussen
 *		Flaming Candle*
 *
 *	(*) Eve-Online names @ http://www.eveonline.com/
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA	02110-1301, USA.
 *
 */
package net.nikr.eve.jeveassets.data.simple;

// <editor-fold defaultstate="collapsed" desc="imports">

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.simple.Settings;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

// </editor-fold>
public class SettingsTests {

	@Test
	public void testSettingsSave() throws Exception {
		Settings simpleSettings = new Settings()
						.setApiProxy(new Settings.ApiProxy("some proxy"))
						.setBpos(new ListBuilder<Settings.Bpo>(new ArrayList<Settings.Bpo>())
							.add(new Settings.Bpo(100))
							.add(new Settings.Bpo(101))
							.add(new Settings.Bpo(9001))
							.build()
							)
						.setColumns(new ListBuilder<Settings.Column>(new ArrayList<Settings.Column>())
							.add(new Settings.Column("foo", true))
							.add(new Settings.Column("foobar", false))
							.add(new Settings.Column("zit", false))
							.add(new Settings.Column("plop", true))
							.build()
							)
						.setFilters(new ListBuilder<Settings.Filter>(new ArrayList<Settings.Filter>())
							.add(new Settings.Filter("bar")
								.setRows(new ListBuilder<Settings.Filter.Row>(new ArrayList<Settings.Filter.Row>())
									.add(new Settings.Filter.Row(true, "col A", "mode", "some text"))
									.build()
									)
								)
							.add(new Settings.Filter("baz")
								.setRows(new ListBuilder<Settings.Filter.Row>(new ArrayList<Settings.Filter.Row>())
									.add(new Settings.Filter.Row(true, "col B", "mode", "some other text"))
									.add(new Settings.Filter.Row(true, "col C", "mode B", "some more other text"))
									.build()
									)
								)
							.build()
							)
						.setFlags(new ListBuilder<Settings.Flag>(new ArrayList<Settings.Flag>())
							.add(new Settings.Flag("FLAG_AUTO_RESIZE_COLUMNS_TEXT", true))
							.add(new Settings.Flag("FLAG_FILTER_ON_ENTER", true))
							.add(new Settings.Flag("FLAG_AUTO_RESIZE_COLUMNS_WINDOW", false))
							.build()
						  )
						.setMarketSettings(new Settings.MarketSettings(123456, 30000001, 1000))
						.setUpdates(new Settings.Updates()
							.setConquerableStation(new Settings.Updates.Conquerablestation(1249890131387l))
							.setMarketstats(new Settings.Updates.MarketStats(1249890130000l))
							.addCorporation(new Settings.Updates.Corporation(1249908119000l, 4))
							.addCorporation(new Settings.Updates.Corporation(1249908119000l, 5))
							.addCorporation(new Settings.Updates.Corporation(1249674485000l, 6))
							)
						.setUserprices(new ListBuilder<Settings.Userprice>(new ArrayList<Settings.Userprice>())
							.add(new Settings.Userprice(81000.0, 24349, "Small Tractor Beam I Blueprint"))
							.build()
							)
						;

		// and write it.
		Serializer serializer = new Persister();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		serializer.write(simpleSettings, baos);

		// TODO compare the output from this to an example to confirm the test passes.
		System.out.println(new String(baos.toByteArray()));
	}

	@Test
	public void testSettingsRead() throws Exception {
		// TODO read an input from a resource
//		Serializer serializer = new Persister();
//		File source = new File("example.xml");
//
//		Example example = serializer.read(Example.class, source);
	}

	public static void main(String[] args) throws Exception {
		new SettingsTests().testSettingsSave();
	}

	/**
	 * probably useless as Arrays.asList(...) exists, but I like the lack of commas
	 * in the code that uses it.
	 * @param <T>
	 */
	class ListBuilder<T> {

		List<T> internal;

		public ListBuilder(List<T> list) {
			internal = list;
		}

		ListBuilder<T> add(T elem) {
			internal.add(elem);
			return this;
		}

		List<T> build() {
			return internal;
		}
	}
}
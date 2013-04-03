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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.local.ConquerableStationsReader;
import net.nikr.eve.jeveasset.io.local.LocationsReader;
import net.nikr.eve.jeveasset.tests.mocks.FakeSettings;


public class MockSettings extends FakeSettings {
	private Map<Long, Location> locations = new HashMap<Long, Location>();
	private Map<Integer, ApiStation> conquerableStations = new HashMap<Integer, ApiStation>();

	public MockSettings() {
		LocationsReader.load(this);
		ConquerableStationsReader.load(this);
	}

	@Override
	public Map<Long, Location> getLocations() {
		return locations;
	}

	@Override
	public Map<Integer, ApiStation> getConquerableStations() {
		return conquerableStations;
	}

	@Override
	public void setConquerableStations(final Map<Integer, ApiStation> conquerableStations) {
		this.conquerableStations = conquerableStations;
		for (ApiStation station : conquerableStations.values()) {
			ApiIdConverter.addLocation(station, getLocations());
		}
	}

}
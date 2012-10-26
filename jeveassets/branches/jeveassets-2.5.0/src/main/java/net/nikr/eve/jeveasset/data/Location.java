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

package net.nikr.eve.jeveasset.data;


public class Location implements Comparable<Location> {
	private long locationID; //LocationID : long
	private String name;
	private long regionID; //LocationID : long
	private String security;
	private long systemID; //LocationID : long

	public Location(final long locationID, final String name, final long regionID, final String security, final long systemID) {
		this.locationID = locationID;
		this.name = name;
		this.regionID = regionID;
		this.security = security;
		this.systemID = systemID;
	}

	public long getLocationID() {
		return locationID;
	}

	public String getName() {
		return name;
	}

	public long getRegionID() {
		return regionID;
	}

	public String getSecurity() {
		return security;
	}

	public boolean isRegion() {
		return regionID == locationID && locationID > 0;
	}
	public boolean isSystem() {
		return systemID == locationID && locationID > 0;
	}
	public boolean isStation() {
		return !isSystem() && !isRegion() && locationID > 0;
	}

	@Override
	public String toString() {
		return name;
	}

	public long getSystemID() {
		if (systemID == 0) {
			return locationID;
		} else {
			return systemID;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Location other = (Location) obj;
		if (this.locationID != other.locationID) {
			return false;
		}
		if (this.systemID != other.systemID) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (int) (this.locationID ^ (this.locationID >>> 32));
		hash = 71 * hash + (int) (this.systemID ^ (this.systemID >>> 32));
		return hash;
	}

	@Override
	public int compareTo(final Location o) {
		return this.getName().compareTo(o.getName());
	}
}
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

package net.nikr.eve.jeveasset.data;


public class ReprocessedMaterial {
	private int typeID; //TypeID : int
	private int quantity;
	private int portionSize;

	public ReprocessedMaterial(final int typeID, final int quantity, final int portionSize) {
		this.typeID = typeID;
		this.quantity = quantity;
		this.portionSize = portionSize;
	}

	public int getTypeID() {
		return typeID;
	}

	public int getPortionSize() {
		return portionSize;
	}

	public int getQuantity() {
		return quantity;
	}
}
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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.gui.images.Images;


public class TreeAsset extends Asset {

	public enum TreeType {
		CATEGORY,
		LOCATION,
	}
	private final String treeName;
	private final List<TreeAsset> levels = new ArrayList<TreeAsset>();
	private final String compare;
	private final String ownerName;
	private final Icon icon;
	private final boolean trueAsset;
	private int depth = 0;

	public TreeAsset(Asset asset, TreeType treeType) {
		super(asset);
		this.ownerName = asset.getOwner();
		this.treeName = asset.getName();
		if (asset.getItem().getGroup().equals("Audit Log Secure Container") && !asset.getAssets().isEmpty() && treeType == TreeType.LOCATION) {
			this.icon = Images.LOC_CONTAINER.getIcon();
		} else if (asset.getItem().getCategory().equals("Ship") && !asset.getAssets().isEmpty() && treeType == TreeType.LOCATION) {
			this.icon = Images.TOOL_SHIP_LOADOUTS.getIcon();
		} else {
			this.icon = null;
		}
		if (treeType == TreeType.CATEGORY) {
			this.trueAsset = true;
			String category = asset.getItem().getCategory();
			String group = asset.getItem().getGroup();
			depth = 2; //Max 2 level, so we start higher
			this.levels.add(new TreeAsset(asset, category, depth, category, null));
			depth++;
			this.levels.add(new TreeAsset(asset, group, depth, category+group, null));
			this.compare = category+group+asset.getName();
		} else if (treeType == TreeType.LOCATION) {
			this.trueAsset = asset.getAssets().isEmpty();
			Location location = asset.getLocation();
			depth++;
			this.levels.add(new TreeAsset(asset, location.getRegion(), depth, location.getRegion(), Images.LOC_REGION.getIcon()));
			depth++;
			this.levels.add(new TreeAsset(asset, location.getSystem(), depth, location.getRegion()+location.getSystem(), Images.LOC_SYSTEM.getIcon()));
			if (location.isStation()) {
				depth++;
				this.levels.add(new TreeAsset(asset, location.getLocation(), depth, location.getRegion()+location.getSystem()+location.getLocation(), Images.LOC_STATION.getIcon()));
			}
			String fullLocation = location.getRegion()+location.getSystem()+location.getLocation();
			String fullParent = "";
			if (!asset.getParents().isEmpty()) {
				for (Asset parentAsset : asset.getParents()) {
					fullParent = fullParent + parentAsset.getName() + " #" + parentAsset.getItemID();
					Icon parentIcon = null;
					depth++;
					this.levels.add(new TreeAsset(parentAsset, parentAsset.getName() + " #" + parentAsset.getItemID(), depth, fullLocation+fullParent, parentIcon));
				}
			}
			this.compare = fullLocation + fullParent + asset.getName() + " #" + asset.getItemID();
		} else { //Never happens
			this.trueAsset = true;
			this.compare = treeName;
		}
	}

	public TreeAsset(final Asset asset, final String treeName, final int depth, final String compare, final Icon icon) {
		super(new Item(0), new Location(0), null, 0, new ArrayList<Asset>(), "", 0, 0L, false, 0);
		//FIXME - - > TreeTable: Need make totals...
		this.treeName = treeName;
		this.trueAsset = false;
		this.depth = depth;
		this.compare = compare;
		this.ownerName = "";
		this.icon = icon;
	}

	public boolean isTrueAsset() {
		return trueAsset;
	}

	public int getDepth() {
		return depth;
	}

	public String getTreeName() {
		return treeName;
	}

	public List<TreeAsset> getLevels() {
		return levels;
	}

	public String getCompare() {
		return compare;
	}

	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getOwner() {
		return ownerName;
	}

	@Override
	public int compareTo(Asset o) {
		if (o instanceof TreeAsset) {
			TreeAsset treeAsset = (TreeAsset) o;
			return this.getCompare().compareTo(treeAsset.getCompare());
		} else {
			return super.compareTo(o);
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 47 * hash + (this.compare != null ? this.compare.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TreeAsset other = (TreeAsset) obj;
		if ((this.compare == null) ? (other.compare != null) : !this.compare.equals(other.compare)) {
			return false;
		}
		return true;
	}
}

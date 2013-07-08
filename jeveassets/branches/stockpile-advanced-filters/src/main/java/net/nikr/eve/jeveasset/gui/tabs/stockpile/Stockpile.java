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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuCopy.CopySeparator;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class Stockpile implements Comparable<Stockpile>, LocationType {
	private String name;
	private String ownerNames;
	private String flagNames;
	private List<Long> ownerIDs = new ArrayList<Long>();
	private List<Location> locations = new ArrayList<Location>();
	private List<Integer> flagIDs = new ArrayList<Integer>();
	private List<String> containers = new ArrayList<String>();
	private boolean inventory;
	private boolean sellOrders;
	private boolean buyOrders;
	private boolean jobs;
	private final List<StockpileItem> items = new ArrayList<StockpileItem>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private double percentFull;
	private double multiplier;

	private Stockpile(final Stockpile stockpile) {
		update(stockpile);
		for (StockpileItem item : stockpile.getItems()) {
			if (item.getItemTypeID() != 0) { //Ignore Total
				items.add(new StockpileItem(this, item));
			}
		}
		items.add(totalItem);
	}

	public Stockpile(final String name, final List<Long> ownerIDs, final List<Location> locations, final List<Integer> flagIDs, final List<String> containers, final boolean inventory, final boolean sellOrders, final boolean buyOrders, final boolean jobs, double multiplier) {
		this.name = name;
		this.ownerIDs = ownerIDs;
		this.locations = locations;
		this.flagIDs = flagIDs;
		this.containers = containers;
		this.inventory = inventory;
		this.sellOrders = sellOrders;
		this.buyOrders = buyOrders;
		this.jobs = jobs;
		this.multiplier = multiplier;
		items.add(totalItem);
	}

	final void update(final Stockpile stockpile) {
		this.name = stockpile.getName();
		this.ownerIDs = stockpile.getOwnerIDs();
		this.ownerNames = stockpile.getOwnerNames();
		this.locations = stockpile.getLocations();
		this.flagIDs = stockpile.getFlagIDs();
		this.flagNames = stockpile.getFlagNames();
		this.containers = stockpile.getContainers();
		this.inventory = stockpile.isInventory();
		this.sellOrders = stockpile.isSellOrders();
		this.buyOrders = stockpile.isBuyOrders();
		this.multiplier = stockpile.getMultiplier();
		this.jobs = stockpile.isJobs();
	}

	public boolean isOK() {
		return totalItem.isOK();
	}

	public boolean isHalf() {
		return totalItem.isHalf();
	}

	public boolean isEmpty() {
		return (items.size() <= 1);
	}

	public void add(final StockpileItem item) {
		if (!items.contains(item)) { //Only one of each type
			items.add(item);
			Collections.sort(items);
		}
	}

	public void remove(final StockpileItem item) {
		if (items.contains(item)) {
			items.remove(item);
		}
		if (items.isEmpty()) {
			items.add(totalItem);
		}
	}

	public void reset() {
		for (StockpileItem item : items) {
			item.reset();
		}
	}

	public String getName() {
		return name;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public boolean isBuyOrders() {
		return buyOrders;
	}

	public boolean isInventory() {
		return inventory;
	}

	public boolean isJobs() {
		return jobs;
	}

	public boolean isSellOrders() {
		return sellOrders;
	}

	public List<Long> getOwnerIDs() {
		return ownerIDs;
	}

	public String getOwnerNames() {
		return ownerNames;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public final void setOwners(final List<String> ownerNames) {
		if (ownerNames.isEmpty()) {
			this.ownerNames = General.get().all();
		} else if (ownerNames.size() == 1) {
			this.ownerNames = ownerNames.get(0);
		} else {
			this.ownerNames = TabsStockpile.get().multiple();
		}
	}

	public final void setOwners(final String ownerNames) {
		this.ownerNames = ownerNames;
	}

	public List<String> getContainers() {
		return containers;
	}

	public String getContainerNames() {
		if (containers.isEmpty()) {
			return General.get().all();
		} else if (containers.size() == 1) {
			return containers.get(0);
		} else {
			return TabsStockpile.get().multiple();
		}
	}

	public String getFlagNames() {
		return flagNames;
	}

	public final void setFlags(final List<String> flagNames) {
		if (flagNames.isEmpty()) {
			this.flagNames = General.get().all();
		} else if (flagNames.size() == 1) {
			this.flagNames = flagNames.get(0);
		} else {
			this.flagNames = TabsStockpile.get().multiple();
		}
	}

	public final void setFlags(final String flagNames) {
		this.flagNames = flagNames;
	}

	public List<Integer> getFlagIDs() {
		return flagIDs;
	}

	public List<StockpileItem> getItems() {
		return items;
	}

	//FIXME - - - > Stockpile: getLocation is useless
	@Override
	public Location getLocation() {
		if (locations.isEmpty()) {
			return null;
		} else {
			return locations.get(0);
		}
	}

	public List<Location> getLocations() {
		return locations;
	}

	public String getLocationNames() {
		if (locations.isEmpty()) {
			return General.get().all();
		} else if (locations.size() == 1) {
			return locations.get(0).getLocation();
		} else {
			return TabsStockpile.get().multiple();
		}
	}

	public double getPercentFull() {
		return percentFull;
	}

	public void updateTotal() {
		totalItem.reset();
		percentFull = Double.MAX_VALUE;
		items.remove(totalItem);
		for (StockpileItem item : items) {
			double percent;
			if (item.getCountNow() == 0) {
				percent = 0;
			} else {
				percent = item.getCountNow() / ((double) item.getCountMinimumMultiplied());
			}
			percentFull = Math.min(percent, percentFull);
			totalItem.updateTotal(item);
		}
		if (percentFull == Double.MAX_VALUE) { //Default value
			percentFull = 1;
		}
		items.add(totalItem);
	}

	public StockpileTotal getTotal() {
		return totalItem;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stockpile other = (Stockpile) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public Stockpile clone() {
		return new Stockpile(this);
	}

	@Override
	public int compareTo(final Stockpile o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	public static class StockpileItem implements Comparable<StockpileItem>, LocationType, ItemType, BlueprintType, PriceType, CopySeparator {
		//Constructor
		private Stockpile stockpile;
		private Item item;
		private int typeID;
		private long countMinimum;

		//Updated values
		private boolean marketGroup;
		private double price = 0.0;
		private double volume = 0.0f;

		//Updated counts
		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;

		public StockpileItem(final Stockpile stockpile, final StockpileItem stockpileItem) {
			this(stockpile,
					stockpileItem.item,
					stockpileItem.typeID,
					stockpileItem.countMinimum
					);
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final long countMinimum) {
			this.stockpile = stockpile;
			this.item = item;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
		}

		void update(StockpileItem stockpileItem) {
			this.stockpile = stockpileItem.stockpile;
			this.item = stockpileItem.item;
			this.typeID = stockpileItem.typeID;
			this.countMinimum = stockpileItem.countMinimum;
		}

		public boolean isOK() {
			return getCountNeeded() >= 0;
		}

		public boolean isHalf() {
			return getCountNow() >= (getCountMinimumMultiplied() / 2.0);
		}

		private void reset() {
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			price = 0.0;
			volume = 0.0f;
			marketGroup = false;
		}
		public void updateValues(final double updatePrice, final float updateVolume) {
			this.price = updatePrice;
			this.volume = updateVolume;
		}

		private boolean matchOwner(final long ownerID) {
			if (stockpile.getOwnerIDs().isEmpty()) {
				return true; //All
			}
			for (long stockpileOwnerID : stockpile.getOwnerIDs()) {
				if (stockpileOwnerID == ownerID) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchContainer(final String container) {
			if (stockpile.getContainers().isEmpty()) {
				return true; //All
			}
			for (String stockpileContainer : stockpile.getContainers()) {
				if (container.contains(stockpileContainer)) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final int flagID) {
			if (stockpile.getFlagIDs().isEmpty()) {
				return true; //All
			}
			for (int stockpileFlagID : stockpile.getFlagIDs()) {
				if (flagID == stockpileFlagID) { //Match self
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final Asset asset) {
			if (stockpile.getFlagIDs().isEmpty()) {
				return true; //All
			}
			for (int flagID : stockpile.getFlagIDs()) {
				if (asset.getFlagID() == flagID) { //Match self
					return true;
				}
				for (Asset parentAsset : asset.getParents()) { //Test parents
					if (parentAsset.getFlagID() == flagID) { //Parent match
						return true;
					}
				}
			}
			return false; //No match
		}
	
		private boolean matchLocation(Location location) {
			if (stockpile.getLocations().isEmpty()) {
				return true; //Nothing selected - always match
			}
			for (Location stockpileLocation : stockpile.getLocations()) {
				if (stockpileLocation.getLocation().equals(location.getStation())) {
					return true;
				}
				if (stockpileLocation.getLocation().equals(location.getSystem())) {
					return true;
				}
				if (stockpileLocation.getLocation().equals(location.getRegion())) {
					return true;
				}
			}
			return false;
		}

		void updateAsset(final Asset asset) {
			if (asset != null //better safe then sorry
					&& (
						(typeID == asset.getItem().getTypeID() && (!asset.getItem().isBlueprint() || asset.isBPO()))
						|| (typeID == -asset.getItem().getTypeID() && asset.getItem().isBlueprint() && !asset.isBPO()) //BPC
						)
					&& matchOwner(asset.getOwnerID())
					&& matchContainer(asset.getContainer())
					&& matchFlag(asset)
					&& matchLocation(asset.getLocation())
					) {
				inventoryCountNow = inventoryCountNow + asset.getCount();
			}
		}

		void updateMarketOrder(final MarketOrder marketOrder) {
			if (marketOrder != null //better safe then sorry
					&& typeID == marketOrder.getTypeID()
					&& matchOwner(marketOrder.getOwnerID())
					&& marketOrder.getOrderState() == 0 //Open/Active
					&& matchLocation(marketOrder.getLocation())
					) {
				if (marketOrder.getBid() < 1) { //Sell
					if (stockpile.isSellOrders()) {
						sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolRemaining();
					}
				} else { //Buy
					if (stockpile.isBuyOrders()) {
						buyOrdersCountNow = buyOrdersCountNow + marketOrder.getVolRemaining();
					}
				}
			}
		}

		void updateIndustryJob(final IndustryJob industryJob, final Item itemType) {
			if (industryJob != null && itemType != null //better safe then sorry
					&& typeID == industryJob.getOutputTypeID() //Produced only
					&& matchOwner(industryJob.getOwnerID())
					&& matchFlag(industryJob.getOutputFlag())
					&& matchLocation(industryJob.getLocation())
					&& industryJob.getActivityID() == 1 //Manufacturing
					&& industryJob.getCompletedStatus() == 0 //Inprogress AKA not delivered
					) {
				jobsCountNow = jobsCountNow + (industryJob.getRuns() * itemType.getPortion());
			}
		}

		public void setCountMinimum(final long countMinimum) {
			this.countMinimum = countMinimum;
			this.getStockpile().updateTotal();
		}

		public void addCountMinimum(final long countMinimum) {
			this.countMinimum = this.countMinimum + countMinimum;
			this.getStockpile().updateTotal();
		}

		public String getSeparator() {
			return stockpile.getName();
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		@Override
		public boolean isBPC() {
			return (typeID < 0);
		}

		@Override
		public boolean isBPO() {
			return isBlueprint() && !isBPC();
		}

		private boolean isBlueprint() {
			return item.isBlueprint();
		}

		public String getName() {
			if (isBPC()) { //Blueprint copy
				return item.getTypeName() + " (BPC)";
			} else if (isBPO()) { //Blueprint original
				return item.getTypeName() + " (BPO)";
			} else { //Everything else
				return item.getTypeName();
			}
		}

		public long getCountMinimum() {
			return countMinimum;
		}

		public long getCountMinimumMultiplied() {
			return (long)(stockpile.getMultiplier() * countMinimum);
		}

		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}

		public double getPercentNeeded() {
			double percent;
			if (getCountNow() == 0) {
				percent = 0;
			} else {
				percent = getCountNow() / ((double) getCountMinimumMultiplied());
			}
			return percent;
		}

		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		public long getJobsCountNow() {
			return jobsCountNow;
		}

		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}

		public long getCountNeeded() {
			return getCountNow() - getCountMinimumMultiplied();
		}

		@Override
		public Double getDynamicPrice() {
			return price;
		}

		public int getItemTypeID() {
			return typeID;
		}

		public int getTypeID() {
			return Math.abs(typeID);
		}

		public double getVolume() {
			return volume;
		}

		public double getValueNow() {
			return getCountNow() * price;
		}

		public double getValueNeeded() {
			return getCountNeeded() * price;
		}

		public double getVolumeNow() {
			return getCountNow() * volume;
		}

		public double getVolumeNeeded() {
			return getCountNeeded() * volume;
		}

		public boolean isMarketGroup() {
			return marketGroup;
		}

		@Override
		public Item getItem() {
			return item;
		}

		@Override
		public Location getLocation() {
			return stockpile.getLocation();
		}

		@Override
		public String getCopyString() {
			StringBuilder builder = new StringBuilder();
			builder.append(getStockpile().getName());
			builder.append("\t");
			builder.append(getStockpile().getOwnerNames());
			builder.append("\t");
			builder.append(getStockpile().getLocationNames());
			return builder.toString();
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final StockpileItem other = (StockpileItem) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.stockpile != other.stockpile && (this.stockpile == null || !this.stockpile.equals(other.stockpile))) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 29 * hash + this.typeID;
			hash = 29 * hash + (this.stockpile != null ? this.stockpile.hashCode() : 0);
			return hash;
		}

		@Override
		public int compareTo(final StockpileItem item) {
			//Compare groups
			int value = getItem().getGroup().compareToIgnoreCase(item.getItem().getGroup());
			if (value != 0) { //Not same group
				return value;
			} else { //Same group - compare names
				return this.getName().compareToIgnoreCase(item.getName());
			}
		}
	}

	public static class StockpileTotal extends StockpileItem {

		private boolean ok = true;
		private boolean half = true;
		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;
		private long countNeeded = 0;
		private long countMinimum = 0;
		private long countMinimumMultiplied = 0;
		private double totalPrice;
		private double totalPriceCount;
		private double valueNow = 0;
		private double valueNeeded = 0;
		private double volumeNow = 0;
		private double volumeNeeded = 0;

		public StockpileTotal(final Stockpile stockpile) {
			super(stockpile, new Item(0), 0, 0);
		}

		private void reset() {
			ok = true;
			half = true;
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			countNeeded = 0;
			countMinimum = 0;
			totalPrice = 0;
			totalPriceCount = 0;
			valueNow = 0;
			valueNeeded = 0;
			volumeNow = 0;
			volumeNeeded = 0;
			countMinimumMultiplied = 0;
		}

		private void updateTotal(final StockpileItem item) {
			if (!item.isOK()) {
				ok = false;
			}
			if (!item.isHalf()) {
				half = false;
			}
			inventoryCountNow = inventoryCountNow + item.getInventoryCountNow();
			sellOrdersCountNow = sellOrdersCountNow + item.getSellOrdersCountNow();
			buyOrdersCountNow = buyOrdersCountNow + item.getBuyOrdersCountNow();
			jobsCountNow = jobsCountNow + item.getJobsCountNow();
			//Only add if negative
			if (item.getCountNeeded() < 0) {
				countNeeded = countNeeded + item.getCountNeeded();
			}
			countMinimum = countMinimum + item.getCountMinimum();
			countMinimumMultiplied = countMinimumMultiplied + item.getCountMinimumMultiplied();
			totalPrice = totalPrice + item.getDynamicPrice();
			totalPriceCount++;
			valueNow = valueNow + item.getValueNow();
			//Only add if negative
			if (item.getValueNeeded() < 0) {
				valueNeeded = valueNeeded + item.getValueNeeded();
			}
			volumeNow = volumeNow + item.getVolumeNow();
			//Only add if negative
			if (item.getVolumeNeeded() < 0) {
				volumeNeeded = volumeNeeded + item.getVolumeNeeded();
			}
		}

		@Override
		public String getName() {
			return TabsStockpile.get().totalStockpile();
		}

		

		@Override
		public boolean isOK() {
			return ok;
		}

		@Override
		public boolean isHalf() {
			return half;
		}

		@Override
		public long getCountMinimum() {
			return countMinimum;
		}

		@Override
		public long getCountMinimumMultiplied() {
			return countMinimumMultiplied;
		}

		@Override
		public long getCountNeeded() {
			return countNeeded;
		}

		@Override
		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}

		@Override
		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		@Override
		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		@Override
		public long getJobsCountNow() {
			return jobsCountNow;
		}

		@Override
		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}

		@Override
		public Double getDynamicPrice() {
			if (totalPriceCount <= 0 || totalPrice <= 0) {
				return 0.0;
			} else {
				return totalPrice / totalPriceCount;
			}
		}

		@Override
		public double getValueNeeded() {
			return valueNeeded;
		}

		@Override
		public double getValueNow() {
			return valueNow;
		}

		@Override
		public double getVolumeNeeded() {
			return volumeNeeded;
		}

		@Override
		public double getVolumeNow() {
			return volumeNow;
		}

		@Override
		public boolean isMarketGroup() {
			return false;
		}

		@Override
		public double getPercentNeeded() {
			return getStockpile().getPercentFull();
		}
	}
}

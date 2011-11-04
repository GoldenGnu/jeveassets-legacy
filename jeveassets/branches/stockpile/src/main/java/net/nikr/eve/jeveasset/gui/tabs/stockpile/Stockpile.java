/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class Stockpile {
	private String name;
	private long characterID;
	private long locationID;
	private String location;
	private String system;
	private String region;
	private int flagID;
	private String container;
	private List<StockpileItem> items = new ArrayList<StockpileItem>();
	private StockpileTotal totalItem = new StockpileTotal(this, TabsStockpile.get().totalStockpile());

	private Stockpile(Stockpile stockpile) {
		this.name = stockpile.getName();
		this.characterID = stockpile.getCharacterID();
		this.locationID = stockpile.getLocationID();
		this.flagID = stockpile.getFlagID();
		this.container = stockpile.getContainer();
		for (StockpileItem item : stockpile.getItems()){
			if (item.getTypeID() > 0){ //Ignore Total
				items.add( new StockpileItem(this, item) );
			}
		}
		items.add(totalItem);
	}

	public Stockpile(String name, long characterID, long locationID, String location, String system, String region, int flagID, String container) {
		this.name = name;
		this.characterID = characterID;
		this.locationID = locationID;
		this.location = location;
		this.system = system;
		this.region = region;
		this.flagID = flagID;
		this.container = container;
		items.add(totalItem);
	}
	
	void update(Stockpile stockpile) {
		this.name = stockpile.getName();
		this.characterID = stockpile.getCharacterID();
		this.locationID = stockpile.getLocationID();
		this.location = stockpile.getLocation();
		this.system = stockpile.getSystem();
		this.region = stockpile.getRegion();
		this.flagID = stockpile.getFlagID();
		this.container = stockpile.getContainer();
	}
	
	public boolean isOK(){
		return totalItem.isOK();
	}
	
	public boolean isEmpty(){
		if (items.size() > 1){
			return false;
		} else {
			if (items.contains(totalItem)){
				return true;
			} else {
				return false;
			}
		}
	}
	
	public void add(StockpileItem item){
		items.remove(totalItem);
		items.add(item);
		Collections.sort(items);
		items.add(totalItem);
	}
	
	public void remove(StockpileItem item) {
		if (items.contains(item)){
			items.remove(item);
		}
		if (items.isEmpty()) items.add(totalItem);
	}
	
	public void reset(){
		for (StockpileItem item : items){
			item.reset();
		}
	}

	public String getName() {
		return name;
	}

	public long getCharacterID() {
		return characterID;
	}

	public String getContainer() {
		return container;
	}

	public int getFlagID() {
		return flagID;
	}

	public long getLocationID() {
		return locationID;
	}

	public String getLocation() {
		return location;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public List<StockpileItem> getItems() {
		return items;
	}
	
	public void updateTotal() {
		totalItem.reset();
		items.remove(totalItem);
		for (StockpileItem item : items){
			totalItem.updateTotal(item);
		}
		items.add(totalItem);
		
	}

	@Override
	public boolean equals(Object obj) {
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
	
	public static class StockpileItem implements Comparable<StockpileItem> {
		private Stockpile stockpile;
		private String name;
		private int typeID;
		private long countMinimum;
		private boolean marketGroup;
		
		private long countNow = 0;
		private double price = 0.0;
		//FIXME for ships volumn if differend is packaged
		private float volume = 0.0f;

		public StockpileItem(Stockpile stockpile, StockpileItem stockpileItem) {
			this.stockpile = stockpile;
			this.name = stockpileItem.getName();
			this.typeID = stockpileItem.getTypeID();
			this.countMinimum = stockpileItem.getCountMinimum();
		}

		public StockpileItem(Stockpile stockpile, String name, int typeID, String countMinimum) {
			this(stockpile, name, typeID, Long.valueOf(countMinimum));
		}
		public StockpileItem(Stockpile stockpile, String name, int typeID, long countMinimum) {
			this.stockpile = stockpile;
			this.name = name;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
		}
		
		public boolean isOK() {
			return getCountNeeded() == 0;
		}
		
		private void reset(){
			countNow = 0;
			price = 0.0;
			volume = 0.0f;
			marketGroup = false;
		}
		public void match(Asset asset, Integer flagID, Long characterID, Long regionID){
			if (asset.getTypeID() == typeID){
				price = asset.getPrice();
				volume = asset.getVolume();
				marketGroup = asset.isMarketGroup();
				if ((stockpile.getCharacterID() == characterID || stockpile.getCharacterID() < 0)
						&& (stockpile.getContainer().equals(asset.getContainer()) || stockpile.getContainer().equals(TabsStockpile.get().all()))
						&& (stockpile.getFlagID() == flagID || stockpile.getFlagID() < 0)
						&& (stockpile.getLocationID() == asset.getLocationID()
						|| stockpile.getLocationID() == asset.getSolarSystemID()
						|| stockpile.getLocationID() == regionID
						|| stockpile.getLocationID() < 0)
						){
					countNow = countNow + asset.getCount();
				}
			}
			
		}

		public String getSeperator() {
			return stockpile.getName();
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		public String getName() {
			return name;
		}

		public long getCountMinimum() {
			return countMinimum;
		}

		public long getCountNow() {
			return countNow;
		}
		
		public long getCountNeeded() {
			long countNeeded = countNow - countMinimum;
			if (countNeeded > 0) countNeeded = 0;
			return Math.abs(countNeeded);
		}

		public double getPrice() {
			return price;
		}

		public int getTypeID() {
			return typeID;
		}

		public float getVolume() {
			return volume;
		}

		public double getValueNow() {
			return countNow * price;
		}

		public double getValueNeeded() {
			return getCountNeeded() * price;
		}

		public float getVolumeNow() {
			return countNow * volume;
		}

		public float getVolumeNeeded() {
			return getCountNeeded() * volume;
		}
		
		public boolean isMarketGroup() {
			return marketGroup;
		}
		
		@Override
		public String toString(){
			return getName();
		}

		@Override
		public boolean equals(Object obj) {
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
		public int compareTo(StockpileItem o) {
			return this.getName().compareTo(o.getName());
		}
	}
	
	public static class StockpileTotal extends StockpileItem{

		private boolean ok = true;
		private long countNow = 0;
		private long countNeeded = 0;
		private long countMinimum = 0;
		private double price = 0;
		private double valueNow = 0;
		private double valueNeeded = 0;
		private float volumeNow = 0;
		private float volumeNeeded = 0;
		
		public StockpileTotal(Stockpile stockpile, String name) {
			super(stockpile, name, 0, 0);
		}
		
		private void reset(){
			ok = true;
			countNow = 0;
			countNeeded = 0;
			countMinimum = 0;
			price = 0;
			valueNow = 0;
			valueNeeded = 0;
			volumeNow = 0;
			volumeNeeded = 0;
		}
		
		private void updateTotal(StockpileItem item){
			countNow = countNow + item.getCountNow();
			countNeeded = countNeeded + item.getCountNeeded();
			countMinimum = countMinimum + item.getCountMinimum();
			price = price + item.getPrice() / 2;
			valueNow = valueNow + item.getValueNow();
			valueNeeded = valueNeeded + item.getValueNeeded();
			volumeNow = volumeNow + item.getVolumeNow();
			volumeNeeded = volumeNeeded + item.getVolumeNeeded();
			if (!item.isOK()) ok = false;
		}
		
		@Override
		public boolean isOK() {
			return ok;
		}

		@Override
		public long getCountMinimum() {
			return countMinimum;
		}

		@Override
		public long getCountNeeded() {
			return countNeeded;
		}

		@Override
		public long getCountNow() {
			return countNow;
		}

		@Override
		public double getPrice() {
			return price;
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
		public float getVolumeNeeded() {
			return volumeNeeded;
		}

		@Override
		public float getVolumeNow() {
			return volumeNow;
		}

		@Override
		public boolean isMarketGroup() {
			return false;
		}
	}
}

/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;

public class EveAsset implements Comparable<EveAsset> {

	public enum PriceMode {
		PRICE_SELL_MAX() {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMax();
			}
		},
		PRICE_SELL_AVG {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellAvg();
			}
		},
		PRICE_SELL_MEDIAN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellMedian();
			}
		},
		PRICE_SELL_MIN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceSellMin();
			}
		},
		PRICE_MIDPOINT {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceMidpoint();
			}
		},
		PRICE_BUY_MAX {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMax();
			}
		},
		PRICE_BUY_AVG {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyAvg();
			}
		},
		PRICE_BUY_MEDIAN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMedian();
			}
		},
		PRICE_BUY_MIN {
			@Override
			String getI18N() {
				return DataModelEveAsset.get().priceBuyMedian();
			}
		},
		;
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}
//
//	private final static String PRICE_SELL_MAX = "Sell Maximum";
//	private final static String PRICE_SELL_AVG = "Sell Average";
//	private final static String PRICE_SELL_MEDIAN = "Sell Median";
//	private final static String PRICE_SELL_MIN = "Sell Minimum";
//	private final static String PRICE_MIDPOINT = "Midpoint";
//	private final static String PRICE_BUY_MAX = "Buy Maximum";
//	private final static String PRICE_BUY_AVG = "Buy Average";
//	private final static String PRICE_BUY_MEDIAN = "Buy Median";
//	private final static String PRICE_BUY_MIN = "Buy Minimum";
//
	
	//Default
	private static PriceMode defaultPriceType = PriceMode.PRICE_MIDPOINT;

	private static PriceMode priceType = PriceMode.PRICE_MIDPOINT;

	private List<EveAsset> assets = new ArrayList<EveAsset>();
	private long locationID; //LocationID : long
	private long itemID; //ItemID : long
	private long solarSystemID; //LocationID : long
	private int typeID; //TypeID : int
	private String typeName;
	private String name;
	private String group;
	private String category;
	private String owner;
	private long count;
	private String location;
	private String container = "";
	private List<EveAsset> parents;
	private String flag;
	private double priceBase;
	private String meta;
	private boolean marketGroup;
	private PriceData priceData;
	private UserPrice userPrice;
	private boolean corporationAsset;
	private float volume;
	private String region;
	private long typeCount = 0;
	private boolean bpo;
	private boolean singleton;
	private String security;
	private double priceReprocessed;
	private String system;
	

	public EveAsset(String typeName, String group, String category, String owner, long count, String location, List<EveAsset> parents, String flag, double priceBase, String meta, long itemID, int typeID, boolean marketGroup, boolean corporationAsset, float volume, String region, long locationID, boolean singleton, String security, String system, long solarSystemID) {
		this.typeName = typeName;
		this.name = typeName;
		this.group = group;
		this.category = category;
		this.owner = owner;
		this.count = count;
		this.location = location;
		this.parents = parents;
		this.flag = flag;
		this.priceBase = priceBase;
		this.meta = meta;
		this.itemID = itemID;
		this.typeID = typeID;
		this.marketGroup = marketGroup;
		this.corporationAsset = corporationAsset;
		this.volume = volume;
		this.region = region;
		this.locationID = locationID;
		this.bpo = false;
		this.singleton = singleton;
		this.security = security;
		this.system = system;
		this.solarSystemID = solarSystemID;
	}

	public void setPriceData(PriceData priceData) {
		this.priceData = priceData;
	}

	public void setUserPrice(UserPrice userPrice) {
		this.userPrice = userPrice;
	}

	public boolean isBpo() {
		return bpo;
	}

	public void setBpo(boolean bpo) {
		this.bpo = bpo;
	}

	public boolean isBlueprint() {
		return (typeName.toLowerCase().contains("blueprint"));
	}

	public void addEveAsset(EveAsset eveAsset) {
		assets.add(eveAsset);
	}

	public List<EveAsset> getAssets() {
		return assets;
	}

	public String getCategory() {
		return category;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public List<EveAsset> getParents() {
		return parents;
	}

	public boolean isCorporationAsset() {
		return corporationAsset;
	}

	public long getCount() {
		return count;
	}

	public String getFlag() {
		return flag;
	}

	public String getGroup() {
		return group;
	}

	public long getItemID() {
		return itemID;
	}

	public String getLocation() {
		return location;
	}

	public long getLocationID() {
		return locationID;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public String getSecurity() {
		return security;
	}

	public PriceData getPriceData() {
		return priceData;
	}

	public UserPrice getUserPrice() {
		return userPrice;
	}

	public String getMeta() {
		return meta;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getOwner() {
		return owner;
	}

	/**
	 * Singleton: Unpackaged
	 *
	 * @return true if unpackaged
	 *         false if packaged
	 */
	public boolean isSingleton() {
		return singleton;
	}

	public double getPrice() {
		if (isBlueprint() && !isBpo()) {
			return 0;
		}

		if (this.getUserPrice() != null) {
			return this.getUserPrice().getPrice();
		}
		return getDefaultPrice();
	}

	public boolean isUserPrice() {
		return (this.getUserPrice() != null);
	}

	public double getDefaultPrice() {
		if (this.isMarketGroup()) {
			return getDefaultPrice(getPriceData());
		}
		return 0;
	}

	/*
	 * These should be methods on the PriceMode enum.
	 */
	public static double getDefaultPrice(PriceData priceData) {
		if (priceData != null) {
			if (priceType.equals(PriceMode.PRICE_SELL_MAX)) return priceData.getSellMax();
			if (priceType.equals(PriceMode.PRICE_SELL_AVG)) return priceData.getSellAvg();
			if (priceType.equals(PriceMode.PRICE_SELL_MEDIAN)) return priceData.getSellMedian();
			if (priceType.equals(PriceMode.PRICE_SELL_MIN)) return priceData.getSellMin();
			if (priceType.equals(PriceMode.PRICE_MIDPOINT)) return (priceData.getSellMin() + priceData.getBuyMax()) / 2;
			if (priceType.equals(PriceMode.PRICE_BUY_MAX)) return priceData.getBuyMax();
			if (priceType.equals(PriceMode.PRICE_BUY_AVG)) return priceData.getBuyAvg();
			if (priceType.equals(PriceMode.PRICE_BUY_MEDIAN)) return priceData.getBuyMedian();
			if (priceType.equals(PriceMode.PRICE_BUY_MIN)) return priceData.getBuyMin();
		}
		return 0;
	}

	public static PriceMode getDefaultPriceType(){
		return defaultPriceType;
	}

	public double getPriceSellMin() {
		if (isBlueprint() && !isBpo()) {
			return 0;
		}
		if (this.isMarketGroup() && this.getPriceData() != null) {
			return this.getPriceData().getSellMin();
		}
		return 0;
	}

	public double getPriceBuyMax() {
		if (isBlueprint() && !isBpo()) {
			return 0;
		}
		if (this.isMarketGroup() && this.getPriceData() != null) {
			return this.getPriceData().getBuyMax();
		}
		return 0;
	}

	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	public void setPriceReprocessed(double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	public double getValue() {
		return Formater.round(this.getPrice() * this.getCount(), 2);
	}

	public double getValueReprocessed() {
		return Formater.round(this.getPriceReprocessed() * this.getCount(), 2);
	}

	public double getPriceBase() {
		return priceBase;
	}

	public int getTypeID() {
		return typeID;
	}

	public String getRegion() {
		return region;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public long getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(long typeCount) {
		this.typeCount = typeCount;
	}

	public String getSystem() {
		return system;
	}

	public long getSolarSystemID() {
		return solarSystemID;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(EveAsset o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EveAsset other = (EveAsset) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	public static PriceMode getPriceType() {
		return priceType;
	}

	public static void setPriceType(PriceMode priceSource) {
		if (EveAsset.getPriceTypes().contains(priceSource)){
			EveAsset.priceType = priceSource;
		} else {
			EveAsset.priceType = PriceMode.PRICE_MIDPOINT;
		}
		
	}

	public static List<PriceMode> getPriceTypes(){
		return Arrays.asList(PriceMode.values());
	}
}
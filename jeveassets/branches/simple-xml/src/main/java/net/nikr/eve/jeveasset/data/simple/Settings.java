/*
 * Copyright 2009
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
package net.nikr.eve.jeveasset.data.simple;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 *
 * @author Flaming Candle
 */
@Root
public class Settings {
	/**
	 * stores the list of IDs that the user has marked as BPOs.
	 */
	@ElementList
	List<Bpo> bpos;

	@ElementList
	List<Userprice> userprices;

	@Element
	MarketSettings marketSettings;

	@ElementList
	List<Flag> flags;

	@ElementList
	List<Column> columns;

	@Element
	Updates updates;

	@ElementMap(key="name", entry="filter")
	Filters filterMap;

	@Element
	ApiProxy apiProxy;

	@Element
	Proxy proxy;
	
	@Root
	class Proxy {
		// <editor-fold defaultstate="collapsed" desc="properties">
		@Attribute
		private String address;
		@Attribute
		private int port;
		@Attribute
		private String type;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		// </editor-fold>
	}

	@Root
	class ApiProxy {
		// <editor-fold defaultstate="collapsed" desc="properties">
		@Attribute
		private String url;

		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		// </editor-fold>
	}

	@Root
	class Filters {
		// <editor-fold defaultstate="collapsed" desc="properties">
		@ElementList
		private List<Filter> rows;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public List<Filter> getRows() {
			return rows;
		}

		public void setRows(List<Filter> rows) {
			this.rows = rows;
		}
		// </editor-fold>

		class Filter {
			// <editor-fold defaultstate="collapsed" desc="properties">
			@Attribute
			String column;
			@Attribute
			String text;
			@Attribute
			String mode;
			@Attribute
			boolean and;
			// </editor-fold>
			// <editor-fold defaultstate="collapsed" desc="getters & setters">
			public boolean isAnd() {
				return and;
			}

			public void setAnd(boolean and) {
				this.and = and;
			}

			public String getColumn() {
				return column;
			}

			public void setColumn(String column) {
				this.column = column;
			}

			public String getMode() {
				return mode;
			}

			public void setMode(String mode) {
				this.mode = mode;
			}

			public String getText() {
				return text;
			}

			public void setText(String text) {
				this.text = text;
			}
			// </editor-fold>
		}
	}

	@Root
	class Updates {
		// <editor-fold defaultstate="collapsed" desc="properties">
		@Element
		private MarketStats marketstats;
		@Element
		private Conquerablestation ConquerableStation;
		@Element
		private Corporation corporation;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		// </editor-fold>

		abstract class UpdateSuper {
			// <editor-fold defaultstate="collapsed" desc="properties">
			@Attribute
			String name;
			@Attribute
			long nextupdate;
			// </editor-fold>

		  // <editor-fold defaultstate="collapsed" desc="getters & setters">
			public long getNextupdate() {
				return nextupdate;
			}

			public void setNextupdate(long nextupdate) {
				this.nextupdate = nextupdate;
			}
			public abstract String getName();
			public abstract void setName(String name);
		  // </editor-fold>
		}
		@Root
		class MarketStats extends UpdateSuper {
			@Override
			public String getName() {
				return "marketstats";
			}
			@Override
			public void setName(String name) {
				this.name = "marketstats";
			}
		}
		@Root
		class Conquerablestation extends UpdateSuper {
			@Override
			public String getName() {
				return "conquerable station";
			}
			@Override
			public void setName(String name) {
				this.name = "conquerable station";
			}
		}
		@Root
		class Corporation extends UpdateSuper {
			@Override
			public String getName() {
				return "corporation";
			}
			@Override
			public void setName(String name) {
				this.name = "corporation";
			}
		}
	}

	@Root
	class Column {
		// <editor-fold defaultstate="collapsed" desc="properties">
		/**
		 * TODO describe me
		 */
		@Attribute
		private String name;
		
		/**
		 * TODO describe me
		 */
		@Attribute
		private boolean visible;

		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}
		// </editor-fold>
	}

	@Root
	class Flag {
		// <editor-fold defaultstate="collapsed" desc="properties">
		@Attribute
		private String key;

		@Attribute
		private boolean enabled;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		// </editor-fold>
	}

	@Root
	class MarketSettings {
		// <editor-fold defaultstate="collapsed" desc="properties">
		/**
		 * The age for market prices
		 */
		@Attribute
		private int age;

		/**
		 * The region to fetch prices from
		 */
		@Attribute
		private int region;

		@Attribute
		private int quantity;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public int getRegion() {
			return region;
		}

		public void setRegion(int region) {
			this.region = region;
		}
		// </editor-fold>
	}

	@Root
	class Userprice {
		// <editor-fold defaultstate="collapsed" desc="properties">
		/**
		 * The user defined price for this item.
		 */
		@Attribute
		private double price;
		/**
		 * The type ID for the item.
		 */
		@Attribute
		private int typeID;
		/**
		 * A user supplied name for the item.
		 */
		@Attribute
		private String name;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public int getTypeID() {
			return typeID;
		}

		public void setTypeID(int typeID) {
			this.typeID = typeID;
		}
		// </editor-fold>
	}

	@Root
	class Bpo {
		// <editor-fold defaultstate="collapsed" desc="properties">
		/**
		 * The item ID of the BPO.
		 */
		@Attribute
		private long id;
		// </editor-fold>
		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}
		// </editor-fold>
	}
}
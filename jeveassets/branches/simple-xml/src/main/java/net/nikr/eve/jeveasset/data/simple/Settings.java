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

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
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
	@Element(name="marketstat")
	MarketSettings marketSettings;
	@ElementList
	List<Flag> flags;
	@ElementList
	List<Column> columns;
	@Element
	Updates updates;
	@ElementList
	List<Filter> filters;
	@Element(required=false)
	ApiProxy apiProxy;
	@Element(required=false)
	Proxy proxy;

	public Settings() {
	}

	public Settings(List<Bpo> bpos, List<Userprice> userprices, MarketSettings marketSettings, List<Flag> flags, List<Column> columns, Updates updates, List<Filter> filters, ApiProxy apiProxy, Proxy proxy) {
		this.bpos = bpos;
		this.userprices = userprices;
		this.marketSettings = marketSettings;
		this.flags = flags;
		this.columns = columns;
		this.updates = updates;
		this.filters = filters;
		this.apiProxy = apiProxy;
		this.proxy = proxy;
	}

	// <editor-fold defaultstate="collapsed" desc="getters & setters">
	public ApiProxy getApiProxy() {
		return apiProxy;
	}

	public Settings setApiProxy(ApiProxy apiProxy) {
		this.apiProxy = apiProxy;
		return this;
	}

	public List<Bpo> getBpos() {
		return bpos;
	}

	public Settings setBpos(List<Bpo> bpos) {
		this.bpos = bpos;
		return this;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public Settings setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public Settings setFilters(List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	public List<Flag> getFlags() {
		return flags;
	}

	public Settings setFlags(List<Flag> flags) {
		this.flags = flags;
		return this;
	}

	public MarketSettings getMarketSettings() {
		return marketSettings;
	}

	public Settings setMarketSettings(MarketSettings marketSettings) {
		this.marketSettings = marketSettings;
		return this;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public Settings setProxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public Updates getUpdates() {
		return updates;
	}

	public Settings setUpdates(Updates updates) {
		this.updates = updates;
		return this;
	}

	public List<Userprice> getUserprices() {
		return userprices;
	}

	public Settings setUserprices(List<Userprice> userprices) {
		this.userprices = userprices;
		return this;
	}
	// </editor-fold>

	static public class Proxy {
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

	static public class ApiProxy {
		// <editor-fold defaultstate="collapsed" desc="properties">

		@Attribute
		private String url;
		// </editor-fold>

		public ApiProxy(String url) {
			this.url = url;
		}

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		// </editor-fold>
	}

	public static class Filter {

		// <editor-fold defaultstate="collapsed" desc="properties">
		@Attribute
		String name;
		@ElementList(inline=true)
		private List<Row> rows;
		// </editor-fold>

		public Filter() {
		}

		public Filter(String name) {
			this.name = name;
		}

		public Filter(String name, List<Row> rows) {
			this.name = name;
			this.rows = rows;
		}

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getName() {
			return name;
		}

		public Filter setName(String name) {
			this.name = name;
			return this;
		}

		public List<Row> getRows() {
			return rows;
		}

		public Filter setRows(List<Row> rows) {
			this.rows = rows;
			return this;
		}
		// </editor-fold>

		public static class Row {
			// <editor-fold defaultstate="collapsed" desc="properties">
			@Attribute
			boolean and;
			@Attribute
			String column;
			@Attribute
			String mode;
			@Attribute
			String text;
			// </editor-fold>

			public Row() {
			}

			public Row(boolean and, String column, String mode, String text) {
				this.and = and;
				this.column = column;
				this.mode = mode;
				this.text = text;
			}

			// <editor-fold defaultstate="collapsed" desc="getters & setters">
			public String getColumn() {
				return column;
			}

			public void setColumn(String column) {
				this.column = column;
			}

			public boolean isAnd() {
				return and;
			}

			public void setAnd(boolean and) {
				this.and = and;
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

	static public class Updates {
		// <editor-fold defaultstate="collapsed" desc="properties">

		@Element
		private MarketStats marketstats;
		@Element
		private Conquerablestation ConquerableStation;
		@ElementList(inline = true)
		private List<Corporation> corporation;
		// </editor-fold>

		public Updates() {
		}

		public Updates(MarketStats marketstats, Conquerablestation ConquerableStation, List<Corporation> corporation) {
			this.marketstats = marketstats;
			this.ConquerableStation = ConquerableStation;
			this.corporation = corporation;
		}

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public Conquerablestation getConquerableStation() {
			return ConquerableStation;
		}

		public Updates setConquerableStation(Conquerablestation ConquerableStation) {
			this.ConquerableStation = ConquerableStation;
			return this;
		}

		public List<Corporation> getCorporation() {
			return corporation;
		}

		public Updates setCorporation(List<Corporation> corporation) {
			this.corporation = corporation;
			return this;
		}

		public Updates addCorporation(Corporation corp) {
			if (corporation == null) {
				corporation = new ArrayList<Corporation>();
			}
			this.corporation.add(corp);
			return this;
		}

		public MarketStats getMarketstats() {
			return marketstats;
		}

		public Updates setMarketstats(MarketStats marketstats) {
			this.marketstats = marketstats;
			return this;
		}
		// </editor-fold>

		static public abstract class UpdateSuper {
			// <editor-fold defaultstate="collapsed" desc="properties">
			@Attribute
			long nextupdate;
			// </editor-fold>

			public UpdateSuper() {
			}

			public UpdateSuper(long nextupdate) {
				this.nextupdate = nextupdate;
			}

			// <editor-fold defaultstate="collapsed" desc="getters & setters">
			public long getNextupdate() {
				return nextupdate;
			}

			public void setNextupdate(long nextupdate) {
				this.nextupdate = nextupdate;
			}
			// </editor-fold>
		}

		static public class MarketStats extends UpdateSuper {

			public MarketStats() {
			}

			public MarketStats(long nextupdate) {
				super(nextupdate);
			}
		}

		static public class Conquerablestation extends UpdateSuper {

			public Conquerablestation() {
			}

			public Conquerablestation(long nextupdate) {
				super(nextupdate);
			}
		}

		static public class Corporation extends UpdateSuper {

			@Attribute
			private long corpid;

			public Corporation() {
			}

			public Corporation(long nextupdate, long corpid) {
				super(nextupdate);
				this.corpid = corpid;
			}

			public long getCorpid() {
				return corpid;
			}

			public void setCorpid(long corpid) {
				this.corpid = corpid;
			}
		}
	}

	static public class Column {
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
		public Column() {
		}

		public Column(String name, boolean visible) {
			this.name = name;
			this.visible = visible;
		}

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

	static public class Flag {
		// <editor-fold defaultstate="collapsed" desc="properties">

		@Attribute
		private String key;
		@Attribute
		private boolean enabled;

		// </editor-fold>
		public Flag() {
		}

		public Flag(String key, boolean enabled) {
			this.key = key;
			this.enabled = enabled;
		}

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

	static public class MarketSettings {
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

		public MarketSettings() {
		}

		public MarketSettings(int age, int region, int quantity) {
			this.age = age;
			this.region = region;
			this.quantity = quantity;
		}

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public int getAge() {
			return age;
		}

		public MarketSettings setAge(int age) {
			this.age = age;
			return this;
		}

		public int getQuantity() {
			return quantity;
		}

		public MarketSettings setQuantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public int getRegion() {
			return region;
		}

		public MarketSettings setRegion(int region) {
			this.region = region;
			return this;
		}
		// </editor-fold>
	}

	static public class Userprice {
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

		public Userprice() {
		}

		public Userprice(double price, int typeID, String name) {
			this.price = price;
			this.typeID = typeID;
			this.name = name;
		}

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public String getName() {
			return name;
		}

		public Userprice setName(String name) {
			this.name = name;
			return this;
		}

		public double getPrice() {
			return price;
		}

		public Userprice setPrice(double price) {
			this.price = price;
			return this;
		}

		public int getTypeID() {
			return typeID;
		}

		public Userprice setTypeID(int typeID) {
			this.typeID = typeID;
			return this;
		}
		// </editor-fold>
	}

	static public class Bpo {
		// <editor-fold defaultstate="collapsed" desc="properties">

		/**
		 * The item ID of the BPO.
		 */
		@Attribute
		private long id;
		// </editor-fold>

		public Bpo() {
		}

		public Bpo(long id) {
			this.id = id;
		}
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

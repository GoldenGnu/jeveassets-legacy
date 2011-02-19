package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsAssets extends Bundle {
	public static TabsAssets get() {
		return BundleCache.get(TabsAssets.class);
	}

	public static TabsAssets get(Locale locale) {
		return BundleCache.get(TabsAssets.class, locale);
	}

	public TabsAssets(Locale locale) {
		super(locale);
	}

	public abstract String addFilter();
	public abstract String addField();
	public abstract String addSystem();
	public abstract String assets();
	public abstract String average();
	public abstract String average1();
	public abstract String blueprint();
	public abstract String can();
	public abstract String cancel();
	public abstract String clear();
	public abstract String count();
	public abstract String delete();
	public abstract String delete2(Object arg0);
	public abstract String delete3();
	public abstract String done();
	public abstract String edit();
	public abstract String empty();
	public abstract String enter();
	public abstract String filter();
	public abstract String load();
	public abstract String load1();
	public abstract String manage();
	public abstract String name();
	public abstract String nOfyAssets(int rowCount, int size, String filterName);
	public abstract String nothing();
	public abstract String overwrite();
	public abstract String overwrite1();
	public abstract String overwrite2();
	public abstract String price();
	public abstract String rename();
	public abstract String rename1();
	public abstract String save();
	public abstract String save1();
	public abstract String selection();
	public abstract String total();
	public abstract String total1();
	public abstract String total2();
	public abstract String value();
	public abstract String volume();
	public abstract String untitled();
	public abstract String you();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnCategory();
	public abstract String columnOwner();
	public abstract String columnCount();
	public abstract String columnLocation();
	public abstract String columnContainer();
	public abstract String columnFlag();
	public abstract String columnPrice();
	public abstract String columnSellMin();
	public abstract String columnBuyMax();
	public abstract String columnBasePrice();
	public abstract String columnValue();
	public abstract String columnMeta();
	public abstract String columnItemID();
	public abstract String columnVolume();
	public abstract String columnTypeID();
	public abstract String columnRegion();
	public abstract String columnTypeCount();
	public abstract String columnSecurity();
	public abstract String columnReprocessed();
	public abstract String columnReprocessedValue();
}

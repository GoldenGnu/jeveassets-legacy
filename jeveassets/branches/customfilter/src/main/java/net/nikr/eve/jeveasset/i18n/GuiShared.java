package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class GuiShared extends Bundle {
	public static GuiShared get() {
		return BundleCache.get(GuiShared.class);
	}

	public static GuiShared get(Locale locale) {
		return BundleCache.get(GuiShared.class, locale);
	}

	public GuiShared(Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String autoText();
	public abstract String autoWindow();
	public abstract String chruker();
	public abstract String columns();
	public abstract String copy();
	public abstract String cut();
	public abstract String disable();
	public abstract String dotlan();
	public abstract String emptyString();
	public abstract String eveCentral();
	public abstract String eveMetrics();
	public abstract String eveMarkets();
	public abstract String item();
	public abstract String lookup();
	public abstract String overwrite();
	public abstract String overwriteFile();
	public abstract String paste();
	public abstract String region();
	public abstract String reset();
	public abstract String station();
	public abstract String system();
	public abstract String today(Object arg0); 
	public abstract String whitespace37(Object arg0, Object arg1);
	public abstract String files(Object arg0);
}

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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsValues extends Bundle {
	public static TabsValues get() {
		return BundleCache.get(TabsValues.class);
	}

	public static TabsValues get(final Locale locale) {
		return BundleCache.get(TabsValues.class, locale);
	}

	public TabsValues(final Locale locale) {
		super(locale);
	}

	public abstract String assets();
	public abstract String best();
	public abstract String best1();
	public abstract String best2();
	public abstract String character();
	public abstract String corporation();
	public abstract String escrows();
	public abstract String grand();
	public abstract String no();
	public abstract String no1();
	public abstract String none();
	public abstract String select();
	public abstract String sell();
	public abstract String total();
	public abstract String values();
	public abstract String wallet();
}

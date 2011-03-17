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
package net.nikr.eve.jeveassets.tests.i18n;

import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.DialoguesAbout;
import java.util.Date;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;
import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import java.util.Locale;
import net.nikr.eve.jeveasset.i18n.DataModelAssetFilter;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;
import net.nikr.eve.jeveasset.i18n.DialoguesAddSystem;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.junit.Test;
import uk.me.candle.translations.Bundle;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class TestI18N {

	@Test public void testGeneralBundle_en() throws Exception {
		General g = Bundle.load(General.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.uncaughtErrorMessage());
	}

	@Test
	public void testDataModelAssetFilterBundle_en() throws Exception {
		DataModelAssetFilter g = Bundle.load(DataModelAssetFilter.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.modeContain());
	}

	@Test
	public void testDataModelEveAssetBundle_en() throws Exception {
		DataModelEveAsset g = Bundle.load(DataModelEveAsset.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.priceSellMax());
	}

	@Test
	public void testDataModelPriceDataSettings_en() throws Exception {
		DataModelPriceDataSettings g = Bundle.load(DataModelPriceDataSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.regionDerelik());
	}

	@Test
	public void testDialoguesAccount_en() throws Exception {
		DialoguesAccount g = Bundle.load(DialoguesAccount.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.helpText());
	}

	@Test
	public void testDialoguesAddSystem_en() throws Exception {
		DialoguesAddSystem g = Bundle.load(DialoguesAddSystem.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.filterResult(5));
	}

	@Test
	public void testDialoguesCsvExport_en() throws Exception {
		DialoguesCsvExport g = Bundle.load(DialoguesCsvExport.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.allAssets());
	}

	@Test
	public void testDialoguesProfiles_en() throws Exception {
		DialoguesProfiles g = Bundle.load(DialoguesProfiles.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.deleteProfileConfirm("delete me!"));
	}

	@Test
	public void testDialoguesSettings_en() throws Exception {
		DialoguesSettings g = Bundle.load(DialoguesSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.enterFilter());
	}

	@Test
	public void testDialoguesUpdate_en() throws Exception {
		DialoguesUpdate g = Bundle.load(DialoguesUpdate.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.nextUpdateTime(new Date(System.currentTimeMillis())));
	}

	@Test
	public void testDialoguesAbout_en() throws Exception {
		DialoguesAbout g = Bundle.load(DialoguesAbout.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.about());
	}

	@Test
	public void testGuiFrame_en() throws Exception {
		GuiFrame g = Bundle.load(GuiFrame.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.windowTitle("jEveAssets", "1.6.0", 1, 1, "default"));
	}

	@Test
	public void testGuiShared_en() throws Exception {
		GuiShared g = Bundle.load(GuiShared.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.cut());
	}

	@Test
	public void testTabsAssets_en() throws Exception {
		TabsAssets g = Bundle.load(TabsAssets.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.addFilter());
	}

	@Test
	public void testTabsJobs_en() throws Exception {
		TabsJobs g = Bundle.load(TabsJobs.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.activity());
	}

	@Test
	public void testTabsLoadout_en() throws Exception {
		TabsLoadout g = Bundle.load(TabsLoadout.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.empty());
	}

	@Test
	public void testTabsMaterials_en() throws Exception {
		TabsMaterials g = Bundle.load(TabsMaterials.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.collapse());
	}

	@Test
	public void testTabsOrders_en() throws Exception {
		TabsOrders g = Bundle.load(TabsOrders.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.buy());
	}

	@Test
	public void testTabsOverview_en() throws Exception {
		TabsOverview g = Bundle.load(TabsOverview.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.add());
	}

	@Test
	public void testTabsRouting_en() throws Exception {
		TabsRouting g = Bundle.load(TabsRouting.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.calculate());
	}

	@Test
	public void testTabsValues_en() throws Exception {
		TabsValues g = Bundle.load(TabsValues.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.assets());
	}
}
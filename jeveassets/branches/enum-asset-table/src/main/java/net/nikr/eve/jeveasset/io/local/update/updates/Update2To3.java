/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.io.local.update.updates;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat;
import net.nikr.eve.jeveasset.io.local.update.LocalUpdate;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Niklas
 */
public class Update2To3 implements LocalUpdate {
	private static final Logger LOG = LoggerFactory.getLogger(Update2To3.class);

	@Override
	public void performUpdate() {
		LOG.info("Performing update from v2 to v3");
		LOG.info("  - modifies files:");
		LOG.info("    - settings.xml");
		try {
			// We need to update the settings
			// current changes are:
			// 1. XPath: /settings/filters/filter/row[@mode]
			// changed from (e.g.) "Contains" to the enum value name in AssetFilter.Mode
			// 2. settings/marketstat[@defaultprice] --> another enum: EveAsset.PriceMode
			// 3. settings/columns/column --> settings/tables/table/column
			// settings/flags/flag --> removed two flags (now in settings/tables/table)
			String settingPath = Settings.getPathSettings();
			SAXReader xmlReader = new SAXReader();
			Document doc = xmlReader.read(settingPath);
			convertTableSettings(doc);

			FileOutputStream fos = new FileOutputStream(settingPath);
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-16");
			XMLWriter writer = new XMLWriter(fos, outformat);
			writer.write(doc);
			writer.flush();
		} catch (IOException ex) {
			LOG.error("", ex);
			throw new RuntimeException(ex);
		} catch (DocumentException ex) {
			LOG.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	private void convertTableSettings(Document doc){
		XPath xpathSelector = DocumentHelper.createXPath("/settings/tables/table/column");
		List results = xpathSelector.selectNodes(doc);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute name = element.attribute("name");
			EnumTableColumn column = convertTableColumnName(name.getText());
			element.addAttribute("name", column.toString());
		}
	}

	private EnumTableColumn convertTableColumnName(String name){
		if (name.equals("Name")) return EveAssetTableFormat.NAME;
		if (name.equals("Group")) return EveAssetTableFormat.GROUP;
		if (name.equals("Category")) return EveAssetTableFormat.CATEGORY;
		if (name.equals("Owner")) return EveAssetTableFormat.OWNER;
		if (name.equals("Location")) return EveAssetTableFormat.LOCATION;
		if (name.equals("Security")) return EveAssetTableFormat.SECURITY;
		if (name.equals("Region")) return EveAssetTableFormat.REGION;
		if (name.equals("Container")) return EveAssetTableFormat.CONTAINER;
		if (name.equals("Flag")) return EveAssetTableFormat.FLAG;
		if (name.equals("Price")) return EveAssetTableFormat.PRICE;
		if (name.equals("Sell Min")) return EveAssetTableFormat.SELL_MIN;
		if (name.equals("Buy Max")) return EveAssetTableFormat.BUY_MAX;
		if (name.equals("Reprocessed")) return EveAssetTableFormat.REPROCESSED;
		if (name.equals("Base Price")) return EveAssetTableFormat.BASE_PRICE;
		if (name.equals("Reprocessed Value")) return EveAssetTableFormat.REPROCESSED_VALUE;
		if (name.equals("Value")) return EveAssetTableFormat.VALUE;
		if (name.equals("Count")) return EveAssetTableFormat.COUNT;
		if (name.equals("Type Count")) return EveAssetTableFormat.TYPE_COUNT;
		if (name.equals("Meta")) return EveAssetTableFormat.META;
		if (name.equals("Volume")) return EveAssetTableFormat.VOLUME;
		if (name.equals("ID")) return EveAssetTableFormat.ITEM_ID;
		if (name.equals("Type ID")) return EveAssetTableFormat.TYPE_ID;
		return null;
	}

	@Override
	public int getStart() {
		return 2;
	}

	@Override
	public int getEnd() {
		return 3;
	}

}

/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

package net.nikr.eve.jeveasset.io;

import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LocalConquerableStationsWriter extends AbstractXmlWriter {

	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("stations");
		} catch (XmlException ex) {
			Log.error("Conquerable stations not saved "+ex.getMessage(), ex);
		}
		writeConquerableStations(xmldoc, settings.getConquerableStations());

		//xmldoc.normalizeDocument();
		try {
			writeXmlFile(xmldoc, Settings.getPathConquerableStations());
		} catch (XmlException ex) {
			Log.error("Conquerable stations not saved "+ex.getMessage(), ex);
		}
		Log.info("Conquerable stations saved");
	}
	private static void writeConquerableStations(Document xmldoc, Map<Integer, ApiStation> conquerableStations){
		Element parentNode = xmldoc.getDocumentElement();
		for (Map.Entry<Integer, ApiStation> entry : conquerableStations.entrySet()){
			Element node = xmldoc.createElementNS(null, "station");
			ApiStation station = entry.getValue();
			node.setAttributeNS(null, "corporationid", String.valueOf(station.getCorporationID()));
			node.setAttributeNS(null, "corporationname", station.getCorporationName());
			node.setAttributeNS(null, "solarsystemid", String.valueOf(station.getSolarSystemID()));
			node.setAttributeNS(null, "stationid", String.valueOf(station.getStationID()));
			node.setAttributeNS(null, "stationname", station.getStationName());
			node.setAttributeNS(null, "stationtypeid", String.valueOf(station.getStationTypeID()));
			parentNode.appendChild(node);
		}
	}
}
package net.nikr.eve.jeveasset.data.simple;

// <editor-fold defaultstate="collapsed" desc="imports">

import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

// </editor-fold>

@Root
public class Stations {
	@ElementList(inline=true)
	List<Station> stations;

	public class Station extends ApiStation {

		public Station() {
		}

		public Station(ApiStation station) {
			setCorporationID(station.getCorporationID());
			setCorporationName(station.getCorporationName());
			setSolarSystemID(station.getSolarSystemID());
			setStationID(station.getStationID());
			setStationName(station.getStationName());
			setStationTypeID(station.getStationTypeID());
		}

		@Attribute
		@Override
		public int getCorporationID() {
			return super.getCorporationID();
		}

		@Attribute
		@Override
		public String getCorporationName() {
			return super.getCorporationName();
		}

		@Attribute
		@Override
		public int getSolarSystemID() {
			return super.getSolarSystemID();
		}

		@Attribute
		@Override
		public int getStationID() {
			return super.getStationID();
		}

		@Attribute
		@Override
		public String getStationName() {
			return super.getStationName();
		}

		@Attribute
		@Override
		public int getStationTypeID() {
			return super.getStationTypeID();
		}

		@Attribute
		@Override
		public void setCorporationID(int corporationID) {
			super.setCorporationID(corporationID);
		}

		@Attribute
		@Override
		public void setCorporationName(String corporationName) {
			super.setCorporationName(corporationName);
		}

		@Attribute
		@Override
		public void setSolarSystemID(int solarSystemID) {
			super.setSolarSystemID(solarSystemID);
		}

		@Attribute
		@Override
		public void setStationID(int stationID) {
			super.setStationID(stationID);
		}

		@Attribute
		@Override
		public void setStationName(String stationName) {
			super.setStationName(stationName);
		}

		@Attribute
		@Override
		public void setStationTypeID(int stationTypeID) {
			super.setStationTypeID(stationTypeID);
		}

	}
}

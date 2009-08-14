package net.nikr.eve.jeveasset.data.simple;

// <editor-fold defaultstate="collapsed" desc="imports">

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

// </editor-fold>

@Root
public class Marketstats {
	@ElementList(inline=true)
	List<Marketstat> marketstats;

	public class Marketstat {
		@Attribute
		private int id;

		@Attribute
		private double allavg;
		@Attribute
		private double allmax;
		@Attribute
		private double allmedian;
		@Attribute
		private double allmin;
		@Attribute
		private double allstddev;
		@Attribute
		private long allvolume;

		@Attribute
		private double buyavg;
		@Attribute
		private double buymax;
		@Attribute
		private double buymedian;
		@Attribute
		private double buymin;
		@Attribute
		private double buystddev;
		@Attribute
		private long buyvolume;

		@Attribute
		private double sellavg;
		@Attribute
		private double sellmax;
		@Attribute
		private double sellmedian;
		@Attribute
		private double sellmin;
		@Attribute
		private double sellstddev;
		@Attribute
		private long sellvolume;

		// <editor-fold defaultstate="collapsed" desc="getters & setters">
		public double getAllavg() {
			return allavg;
		}

		public void setAllavg(double allavg) {
			this.allavg = allavg;
		}

		public double getAllmax() {
			return allmax;
		}

		public void setAllmax(double allmax) {
			this.allmax = allmax;
		}

		public double getAllmedian() {
			return allmedian;
		}

		public void setAllmedian(double allmedian) {
			this.allmedian = allmedian;
		}

		public double getAllmin() {
			return allmin;
		}

		public void setAllmin(double allmin) {
			this.allmin = allmin;
		}

		public double getAllstddev() {
			return allstddev;
		}

		public void setAllstddev(double allstddev) {
			this.allstddev = allstddev;
		}

		public long getAllvolume() {
			return allvolume;
		}

		public void setAllvolume(long allvolume) {
			this.allvolume = allvolume;
		}

		public double getBuyavg() {
			return buyavg;
		}

		public void setBuyavg(double buyavg) {
			this.buyavg = buyavg;
		}

		public double getBuymax() {
			return buymax;
		}

		public void setBuymax(double buymax) {
			this.buymax = buymax;
		}

		public double getBuymedian() {
			return buymedian;
		}

		public void setBuymedian(double buymedian) {
			this.buymedian = buymedian;
		}

		public double getBuymin() {
			return buymin;
		}

		public void setBuymin(double buymin) {
			this.buymin = buymin;
		}

		public double getBuystddev() {
			return buystddev;
		}

		public void setBuystddev(double buystddev) {
			this.buystddev = buystddev;
		}

		public long getBuyvolume() {
			return buyvolume;
		}

		public void setBuyvolume(long buyvolume) {
			this.buyvolume = buyvolume;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public double getSellavg() {
			return sellavg;
		}

		public void setSellavg(double sellavg) {
			this.sellavg = sellavg;
		}

		public double getSellmax() {
			return sellmax;
		}

		public void setSellmax(double sellmax) {
			this.sellmax = sellmax;
		}

		public double getSellmedian() {
			return sellmedian;
		}

		public void setSellmedian(double sellmedian) {
			this.sellmedian = sellmedian;
		}

		public double getSellmin() {
			return sellmin;
		}

		public void setSellmin(double sellmin) {
			this.sellmin = sellmin;
		}

		public double getSellstddev() {
			return sellstddev;
		}

		public void setSellstddev(double sellstddev) {
			this.sellstddev = sellstddev;
		}

		public long getSellvolume() {
			return sellvolume;
		}

		public void setSellvolume(long sellvolume) {
			this.sellvolume = sellvolume;
		}
		// </editor-fold>
	}


}

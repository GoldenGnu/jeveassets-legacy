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

package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PriceDataSettings {

	private final static Logger LOG = LoggerFactory.getLogger(PriceDataSettings.class);

	// should re-factor these two into an emum. - Candle 2010-09-13
	public final static String SOURCE_EVE_CENTRAL = "eve-central";
	public final static String SOURCE_EVE_METRICS = "eve-metrics";

	public final static String[] SOURCES = {SOURCE_EVE_CENTRAL, SOURCE_EVE_METRICS};


	public enum RegionType {
		REGION_EMPIRE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionEmpire();
			}
		},
		REGION_MARKET_HUBS() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionMarketHubs();
			}
		},
		REGION_ALL_AMARR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionAllAmarr();
			}
		},
		REGION_ALL_GALLENTE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionAllGallente();
			}
		},
		REGION_ALL_MINMATAR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionAllMinmatar();
			}
		},
		REGION_ALL_CALDARI() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionAllCaldari();
			}
		},
		REGION_ARIDIA() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionAridia();
			}
		},
		REGION_DEVOID() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionDevoid();
			}
		},
		REGION_DOMAIN() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionDomain();
			}
		},
		REGION_GENESIS() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionGenesis();
			}
		},
		REGION_KADOR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionKador();
			}
		},
		REGION_KOR_AZOR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionKorAzor();
			}
		},
		REGION_TASH_MURKON() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionTashMurkon();
			}
		},
		REGION_THE_BLEAK_LANDS() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionTheBleakLands();
			}
		},
		REGION_BLACK_RISE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionBlackRise();
			}
		},
		REGION_LONETREK() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionLonetrek();
			}
		},
		REGION_THE_CITADEL() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionTheCitadel();
			}
		},
		REGION_THE_FORGE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionTheForge();
			}
		},
		REGION_ESSENCE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionEssence();
			}
		},
		REGION_EVERYSHORE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionEveryshore();
			}
		},
		REGION_PLACID() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionPlacid();
			}
		},
		REGION_SINQ_LAISON() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionSinqLaison();
			}
		},
		REGION_SOLITUDE() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionSolitude();
			}
		},
		REGION_VERGE_VENDOR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionVergeVendor();
			}
		},
		REGION_METROPOLIS() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionMetropolis();
			}
		},
		REGION_HEIMATAR() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionHeimatar();
			}
		},
		REGION_MOLDEN_HEATH() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionMoldenHeath();
			}
		},
		REGION_DERELIK() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionDerelik();
			}
		},
		REGION_KHANID() {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().regionKhanid();
			}
		},;
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	public final static RegionType[] REGIONS_EVE_CENTRAL = {RegionType.REGION_EMPIRE
											,RegionType.REGION_MARKET_HUBS
											,RegionType.REGION_ALL_AMARR
											,RegionType.REGION_ALL_GALLENTE
											,RegionType.REGION_ALL_MINMATAR
											,RegionType.REGION_ALL_CALDARI
											,RegionType.REGION_ARIDIA
											,RegionType.REGION_BLACK_RISE
											,RegionType.REGION_DERELIK
											,RegionType.REGION_DEVOID
											,RegionType.REGION_DOMAIN
											,RegionType.REGION_ESSENCE
											,RegionType.REGION_EVERYSHORE
											,RegionType.REGION_GENESIS
											,RegionType.REGION_HEIMATAR
											,RegionType.REGION_KADOR
											,RegionType.REGION_KHANID
											,RegionType.REGION_KOR_AZOR
											,RegionType.REGION_LONETREK
											,RegionType.REGION_METROPOLIS
											,RegionType.REGION_MOLDEN_HEATH
											,RegionType.REGION_PLACID
											,RegionType.REGION_SINQ_LAISON
											,RegionType.REGION_SOLITUDE
											,RegionType.REGION_TASH_MURKON
											,RegionType.REGION_THE_BLEAK_LANDS
											,RegionType.REGION_THE_CITADEL
											,RegionType.REGION_THE_FORGE
											,RegionType.REGION_VERGE_VENDOR
											};

	public final static RegionType[] REGIONS_EVE_METRICS = {
											RegionType.REGION_ARIDIA
											,RegionType.REGION_BLACK_RISE
											,RegionType.REGION_DERELIK
											,RegionType.REGION_DEVOID
											,RegionType.REGION_DOMAIN
											,RegionType.REGION_ESSENCE
											,RegionType.REGION_EVERYSHORE
											,RegionType.REGION_GENESIS
											,RegionType.REGION_HEIMATAR
											,RegionType.REGION_KADOR
											,RegionType.REGION_KHANID
											,RegionType.REGION_KOR_AZOR
											,RegionType.REGION_LONETREK
											,RegionType.REGION_METROPOLIS
											,RegionType.REGION_MOLDEN_HEATH
											,RegionType.REGION_PLACID
											,RegionType.REGION_SINQ_LAISON
											,RegionType.REGION_SOLITUDE
											,RegionType.REGION_TASH_MURKON
											,RegionType.REGION_THE_BLEAK_LANDS
											,RegionType.REGION_THE_CITADEL
											,RegionType.REGION_THE_FORGE
											,RegionType.REGION_VERGE_VENDOR
											};

	private int region;
	private String source;

	public PriceDataSettings(int region, String source) {
		this.region = region;
		this.source = source;
	}

	public int getRegion() {
		if (source.equals(SOURCE_EVE_CENTRAL) && region >= REGIONS_EVE_CENTRAL.length){
			LOG.warn("PriceDataSettings: region index is larger then the region array (eve-central)");
			return 0;
		}
		if (source.equals(SOURCE_EVE_METRICS) && region >= REGIONS_EVE_METRICS.length){
			LOG.warn("PriceDataSettings: region index is larger then the region array (eve-metrics)");
			return 0;
		}
		return region;
	}

	public String getSource(){
		return source;
	}

	public List<Long> getRegions(){
		List<Long> regions = new ArrayList<Long>();
		RegionType region = null;
		if (source.equals(SOURCE_EVE_CENTRAL)){
			region = REGIONS_EVE_CENTRAL[getRegion()];
		}
		if (source.equals(SOURCE_EVE_METRICS)){
			region = REGIONS_EVE_METRICS[getRegion()];
		}
		// TODO (Candle, 2010-09-13) move all these numbers into the regiontype enum above.
		if (region.equals(RegionType.REGION_EMPIRE)){
		//Amarr
			regions.add(10000054l); //Amarr: Aridia
			regions.add(10000036l); //Amarr: Devoid
			regions.add(10000043l); //Amarr: Domain
			regions.add(10000067l); //Amarr: Genesis
			regions.add(10000052l); //Amarr: Kador
			regions.add(10000065l); //Amarr: Kor-Azor
			regions.add(10000020l); //Amarr: Tash-Murkon
			regions.add(10000038l); //Amarr: The Bleak Lands
		//Caldari
			regions.add(10000069l); //Caldari: Black Rise
			regions.add(10000016l); //Caldari: Lonetrek
			regions.add(10000033l); //Caldari: The Citadel
			regions.add(10000002l); //Caldari: The Forge
		//Gallente
			regions.add(10000064l); //Gallente: Essence
			regions.add(10000037l); //Gallente: Everyshore
			regions.add(10000048l); //Gallente: Placid
			regions.add(10000032l); //Gallente: Sinq Laison
			regions.add(10000044l); //Gallente: Solitude
			regions.add(10000068l); //Gallente: Verge Vendor
		//Minmatar
			regions.add(10000042l); //Minmatar : Metropolis
			regions.add(10000030l); //Minmatar : Heimatar
			regions.add(10000028l); //Minmatar : Molden Heath
		//Others
			regions.add(10000001l); //Ammatar: Derelik
			regions.add(10000049l); //Khanid: Khanid
		}
		if (region.equals(RegionType.REGION_MARKET_HUBS)){
			regions.add(10000002l); //Caldari: The Forge (Jita)
			regions.add(10000042l); //Minmatar : Metropolis (Hek)
			regions.add(10000030l); //Minmatar : Heimatar (Rens)
			regions.add(10000064l); //Gallente: Essence (Oursalert)
			regions.add(10000043l); //Amarr: Domain (Amarr)
		}
		if (region.equals(RegionType.REGION_ALL_AMARR)){
			regions.add(10000054l); //Amarr: Aridia
			regions.add(10000036l); //Amarr: Devoid
			regions.add(10000043l); //Amarr: Domain
			regions.add(10000067l); //Amarr: Genesis
			regions.add(10000052l); //Amarr: Kador
			regions.add(10000065l); //Amarr: Kor-Azor
			regions.add(10000020l); //Amarr: Tash-Murkon
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (region.equals(RegionType.REGION_ALL_GALLENTE)){
			regions.add(10000064l); //Gallente: Essence
			regions.add(10000037l); //Gallente: Everyshore
			regions.add(10000048l); //Gallente: Placid
			regions.add(10000032l); //Gallente: Sinq Laison
			regions.add(10000044l); //Gallente: Solitude
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (region.equals(RegionType.REGION_ALL_MINMATAR)){
			regions.add(10000042l); //Minmatar : Metropolis
			regions.add(10000030l); //Minmatar : Heimatar
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (region.equals(RegionType.REGION_ALL_CALDARI)){
			regions.add(10000069l); //Caldari: Black Rise
			regions.add(10000016l); //Caldari: Lonetrek
			regions.add(10000033l); //Caldari: The Citadel
			regions.add(10000002l); //Caldari: The Forge
		}
		if (region.equals(RegionType.REGION_ARIDIA)){
			//Amarr: Aridia
			regions.add(10000054l);
		}
		if (region.equals(RegionType.REGION_DEVOID)){
			//Amarr: Devoid
			regions.add(10000036l);
		}
		if (region.equals(RegionType.REGION_DOMAIN)){
			regions.add(10000043l); //Amarr: Domain
		}
		if (region.equals(RegionType.REGION_GENESIS)){
			regions.add(10000067l); //Amarr: Genesis
		}
		if (region.equals(RegionType.REGION_KADOR)){
			regions.add(10000052l); //Amarr: Kador
		}
		if (region.equals(RegionType.REGION_KOR_AZOR)){
			regions.add(10000065l); //Amarr: Kor-Azor
		}
		if (region.equals(RegionType.REGION_TASH_MURKON)){
			regions.add(10000020l); //Amarr: Tash-Murkon
		}
		if (region.equals(RegionType.REGION_THE_BLEAK_LANDS)){
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (region.equals(RegionType.REGION_BLACK_RISE)){
			regions.add(10000069l); //Caldari: Black Rise
		}
		if (region.equals(RegionType.REGION_LONETREK)){
			regions.add(10000016l); //Caldari: Lonetrek
		}
		if (region.equals(RegionType.REGION_THE_CITADEL)){
			regions.add(10000033l); //Caldari: The Citadel
		}
		if (region.equals(RegionType.REGION_THE_FORGE)){
			regions.add(10000002l); //Caldari: The Forge
		}
		if (region.equals(RegionType.REGION_ESSENCE)){
			regions.add(10000064l); //Gallente: Essence
		}
		if (region.equals(RegionType.REGION_EVERYSHORE)){
			regions.add(10000037l); //Gallente: Everyshore
		}
		if (region.equals(RegionType.REGION_PLACID)){
			regions.add(10000048l); //Gallente: Placid
		}
		if (region.equals(RegionType.REGION_SINQ_LAISON)){
			regions.add(10000032l); //Gallente: Sinq Laison
		}
		if (region.equals(RegionType.REGION_SOLITUDE)){
			regions.add(10000044l); //Gallente: Solitude
		}
		if (region.equals(RegionType.REGION_VERGE_VENDOR)){
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (region.equals(RegionType.REGION_METROPOLIS)){
			regions.add(10000042l); //Minmatar : Metropolis
		}
		if (region.equals(RegionType.REGION_HEIMATAR)){
			regions.add(10000030l); //Minmatar : Heimatar
		}
		if (region.equals(RegionType.REGION_MOLDEN_HEATH)){
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (region.equals(RegionType.REGION_DERELIK)){
			regions.add(10000001l); //Ammatar: Derelik
		}
		if (region.equals(RegionType.REGION_KHANID)){
			regions.add(10000049l); //Khanid: Khanid
		}
		return regions;
	}

	@Override
	public boolean equals(Object obj){
		if (obj instanceof PriceDataSettings){
			return equals( (PriceDataSettings) obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + this.region;
		hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
		return hash;
	}

	public boolean equals(PriceDataSettings priceDataSettings){
		if (priceDataSettings.getRegion() == this.getRegion() && priceDataSettings.getSource().equals(this.source) ) return true;
		return false;
	}
}
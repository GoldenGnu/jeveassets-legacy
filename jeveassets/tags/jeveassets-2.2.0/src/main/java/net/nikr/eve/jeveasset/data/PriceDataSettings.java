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
package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.nikr.eve.jeveasset.data.Asset.PriceMode;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;

public class PriceDataSettings {

	public enum PriceSource {
		EVE_CENTRAL("eve-central", true, false) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveCentral();
			}
		},
		EVE_MARKETDATA("eve-marketdata", false, true) {
			@Override public PriceMode[] getPriceTypes() {
				return new PriceMode[]{PriceMode.PRICE_SELL_MIN, PriceMode.PRICE_MIDPOINT, PriceMode.PRICE_BUY_MAX};
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveMarketdata();
			}
		},
		EVEMARKETEER("evemarketeer", false, true) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveMarketeer();
			}
		};
		private String name;
		private boolean supportsMultipleLocations;
		private boolean supportsSingleLocations;

		private PriceSource(final String name, final boolean supportMultipleLocations, final boolean supportsSingleLocations) {
			this.name = name;
			this.supportsMultipleLocations = supportMultipleLocations;
			this.supportsSingleLocations = supportsSingleLocations;
		}

		public abstract PriceMode[] getPriceTypes();
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}

		public String getName() {
			return name;
		}

		public boolean supportsMultipleLocations() {
			return supportsMultipleLocations;
		}

		public boolean supportsSingleLocations() {
			return supportsSingleLocations;
		}
	}

	public enum RegionType {
		EMPIRE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEmpire();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				//Amarr
				regions.add(10000054L); //Amarr: Aridia
				regions.add(10000036L); //Amarr: Devoid
				regions.add(10000043L); //Amarr: Domain
				regions.add(10000067L); //Amarr: Genesis
				regions.add(10000052L); //Amarr: Kador
				regions.add(10000065L); //Amarr: Kor-Azor
				regions.add(10000020L); //Amarr: Tash-Murkon
				regions.add(10000038L); //Amarr: The Bleak Lands
				//Caldari
				regions.add(10000069L); //Caldari: Black Rise
				regions.add(10000016L); //Caldari: Lonetrek
				regions.add(10000033L); //Caldari: The Citadel
				regions.add(10000002L); //Caldari: The Forge
				//Gallente
				regions.add(10000064L); //Gallente: Essence
				regions.add(10000037L); //Gallente: Everyshore
				regions.add(10000048L); //Gallente: Placid
				regions.add(10000032L); //Gallente: Sinq Laison
				regions.add(10000044L); //Gallente: Solitude
				regions.add(10000068L); //Gallente: Verge Vendor
				//Minmatar
				regions.add(10000042L); //Minmatar : Metropolis
				regions.add(10000030L); //Minmatar : Heimatar
				regions.add(10000028L); //Minmatar : Molden Heath
				//Others
				regions.add(10000001L); //Ammatar: Derelik
				regions.add(10000049L); //Khanid: Khanid
				return regions;
			}
		},
		MARKET_HUBS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMarketHubs();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000002L); //Caldari: The Forge (Jita)
				regions.add(10000042L); //Minmatar : Metropolis (Hek)
				regions.add(10000030L); //Minmatar : Heimatar (Rens)
				regions.add(10000064L); //Gallente: Essence (Oursalert)
				regions.add(10000043L); //Amarr: Domain (Amarr)
				return regions;
			}
		},
		ALL_AMARR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllAmarr();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000054L); //Amarr: Aridia
				regions.add(10000036L); //Amarr: Devoid
				regions.add(10000043L); //Amarr: Domain
				regions.add(10000067L); //Amarr: Genesis
				regions.add(10000052L); //Amarr: Kador
				regions.add(10000065L); //Amarr: Kor-Azor
				regions.add(10000020L); //Amarr: Tash-Murkon
				regions.add(10000038L); //Amarr: The Bleak Lands
				return regions;
			}
		},
		ALL_GALLENTE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllGallente();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000064L); //Gallente: Essence
				regions.add(10000037L); //Gallente: Everyshore
				regions.add(10000048L); //Gallente: Placid
				regions.add(10000032L); //Gallente: Sinq Laison
				regions.add(10000044L); //Gallente: Solitude
				regions.add(10000068L); //Gallente: Verge Vendor
				return regions;
			}
		},
		ALL_MINMATAR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllMinmatar();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000042L); //Minmatar : Metropolis
				regions.add(10000030L); //Minmatar : Heimatar
				regions.add(10000028L); //Minmatar : Molden Heath
				return regions;
			}
		},
		ALL_CALDARI() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllCaldari();
			}
			@Override List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000069L); //Caldari: Black Rise
				regions.add(10000016L); //Caldari: Lonetrek
				regions.add(10000033L); //Caldari: The Citadel
				regions.add(10000002L); //Caldari: The Forge
				return regions;
			}
		},
		ARIDIA() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAridia();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000054L); //Amarr: Aridia
			}
		},
		DEVOID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDevoid();
			}

			@Override List<Long> getRegions() {
				return Collections.singletonList(10000036L); //Amarr: Devoid
			}
		},
		DOMAIN() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDomain();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000043L); //Amarr: Domain
			}
		},
		GENESIS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionGenesis();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000067L); //Amarr: Genesis
			}
		},
		KADOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKador();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000052L); //Amarr: Kador
			}
		},
		KOR_AZOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKorAzor();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000065L); //Amarr: Kor-Azor
			}
		},
		TASH_MURKON() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTashMurkon();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000020L); //Amarr: Tash-Murkon
			}
		},
		THE_BLEAK_LANDS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheBleakLands();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000038L); //Amarr: The Bleak Lands
			}
		},
		BLACK_RISE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionBlackRise();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000069L); //Caldari: Black Rise
			}
		},
		LONETREK() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionLonetrek();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000016L); //Caldari: Lonetrek
			}
		},
		THE_CITADEL() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheCitadel();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000033L); //Caldari: The Citadel
			}
		},
		THE_FORGE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheForge();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000002L); //Caldari: The Forge
			}
		},
		ESSENCE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEssence();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000064L); //Gallente: Essence
			}
		},
		EVERYSHORE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEveryshore();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000037L); //Gallente: Everyshore
			}
		},
		PLACID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionPlacid();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000048L); //Gallente: Placid
			}
		},
		SINQ_LAISON() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionSinqLaison();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000032L); //Gallente: Sinq Laison
			}
		},
		SOLITUDE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionSolitude();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000044L); //Gallente: Solitude
			}
		},
		VERGE_VENDOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionVergeVendor();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000068L); //Gallente: Verge Vendor
			}
		},
		METROPOLIS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMetropolis();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000042L); //Minmatar : Metropolis
			}
		},
		HEIMATAR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionHeimatar();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000030L); //Minmatar : Heimatar
			}
		},
		MOLDEN_HEATH() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMoldenHeath();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000028L); //Minmatar : Molden Heath
			}
		},
		DERELIK() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDerelik();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000001L); //Ammatar: Derelik
			}
		},
		KHANID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKhanid();
			}
			@Override List<Long> getRegions() {
				return Collections.singletonList(10000049L); //Khanid: Khanid
			}
		};
		private static RegionType[] singleLocations = null;

		abstract String getI18N();
		abstract List<Long> getRegions();

		@Override
		public String toString() {
			return getI18N();
		}
		public static RegionType[] getSingleLocations() {
			if (singleLocations == null) {
				List<RegionType> list = new ArrayList<RegionType>();
				for (RegionType regionType : RegionType.values()) {
					if (regionType.getRegions().size() == 1) {
						list.add(regionType);
					}
				}
				singleLocations = list.toArray(new RegionType[list.size()]);
			}
			return singleLocations;
		}
		public static RegionType[] getMultipleLocations() {
			return RegionType.values();
		}
	}
	private RegionType regionType;
	private PriceSource priceSource;

	public PriceDataSettings() {
		regionType = getDefaultRegionType();
		priceSource = getDefaultPriceSource();
	}

	public PriceDataSettings(final RegionType region, final PriceSource source) {
		this.regionType = region;
		this.priceSource = source;
	}

	public RegionType getRegion() {
		return regionType;
	}

	public PriceSource getSource() {
		return priceSource;
	}

	public List<Long> getRegions() {
		return regionType.getRegions();
	}

	public static RegionType getDefaultRegionType() {
		return RegionType.THE_FORGE;
	}

	public static PriceSource getDefaultPriceSource() {
		return PriceSource.EVE_CENTRAL;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PriceDataSettings other = (PriceDataSettings) obj;
		if (this.regionType != other.regionType) {
			return false;
		}
		if (this.priceSource != other.priceSource) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + (this.regionType != null ? this.regionType.hashCode() : 0);
		hash = 79 * hash + (this.priceSource != null ? this.priceSource.hashCode() : 0);
		return hash;
	}
}
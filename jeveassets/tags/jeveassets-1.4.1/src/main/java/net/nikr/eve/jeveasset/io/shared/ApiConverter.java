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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.shared.assetlist.ApiAsset;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Settings;

public class ApiConverter {

	public static List<EveAsset> apiMarketOrder(List<ApiMarketOrder> marketOrders, Human human, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		for (int a = 0; a < marketOrders.size(); a++){
			ApiMarketOrder apiMarketOrder = marketOrders.get(a);
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				EveAsset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, human, bCorp, settings);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static EveAsset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, Human human, boolean bCorp, Settings settings){
		int typeID = (int)apiMarketOrder.getTypeID();
		int locationID = (int) apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		long id = apiMarketOrder.getOrderID();
		String flag = "Market Order";
		boolean corporationAsset = bCorp;
		boolean singleton  = true;

		String name = ApiIdConverter.name(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = ApiIdConverter.location(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String region = ApiIdConverter.region(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getConquerableStations(), settings.getLocations());

		List<EveAsset> parents = new ArrayList<EveAsset>();

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiIndustryJob(List<ApiIndustryJob> industryJobs, Human human, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		for (int a = 0; a < industryJobs.size(); a++){
			ApiIndustryJob apiIndustryJob = industryJobs.get(a);
			long id = apiIndustryJob.getInstalledItemID();
			if (apiIndustryJob.getCompleted() == 0){
				EveAsset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, human, bCorp, settings);
				eveAssets.add(eveAsset);
			}
			//Mark original blueprints
			boolean isCopy = (apiIndustryJob.getInstalledItemCopy() > 0);
			List<Long> bpos = settings.getBpos();
			if (bpos.contains(id)){
				bpos.remove(bpos.indexOf(id));
			}
			if (!isCopy){
				bpos.add(id);
			}
		}
		return eveAssets;
	}
	
	private static EveAsset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, Human human, boolean bCorp, Settings settings){
		int typeID = (int) apiIndustryJob.getInstalledItemTypeID();
		int locationID = (int) apiIndustryJob.getInstalledItemLocationID();
		long count = apiIndustryJob.getInstalledItemQuantity();
		long id = apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String flag = ApiIdConverter.flag(nFlag);

		String name = ApiIdConverter.name(typeID, settings.getItems());
		String group = ApiIdConverter.group(typeID, settings.getItems());
		String category = ApiIdConverter.category(typeID, settings.getItems());
		double basePrice = ApiIdConverter.priceBase(typeID, settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(typeID, settings.getItems());
		float volume = ApiIdConverter.volume(typeID, settings.getItems());
		String meta = ApiIdConverter.meta(typeID, settings.getItems());

		String owner = ApiIdConverter.owner(human, bCorp);

		String location = apiIndustryJobLocation(apiIndustryJob, settings);

		String region = ApiIdConverter.region(locationID, null, settings.getConquerableStations(), settings.getLocations());
		String security = ApiIdConverter.security(locationID, null, settings.getConquerableStations(), settings.getLocations());

		List<EveAsset> parents = new ArrayList<EveAsset>();

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiAsset(Human human, List<ApiAsset> assets, boolean bCorp, Settings settings){
		List<EveAsset> eveAssets = new ArrayList<EveAsset>();
		apiAsset(human, assets, eveAssets, null, bCorp, settings);
		return eveAssets;
	}
	private static void apiAsset(Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp, Settings settings){
		for (int a = 0; a < assets.size(); a++){
			ApiAsset asset = assets.get(a);
			EveAsset eveAsset = apiAssetsToEveAsset(human, asset, parentEveAsset, bCorp, settings);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(human, new ArrayList<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp, settings);
		}
	}
	private static EveAsset apiAssetsToEveAsset(Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp, Settings settings){
		long count = apiAsset.getQuantity();
		String flag = ApiIdConverter.flag(apiAsset.getFlag());
		long id = apiAsset.getItemID();
		int typeID = apiAsset.getTypeID();
		int locationID = apiAsset.getLocationID();
		boolean singleton  = apiAsset.getSingleton();
		boolean corporationAsset = bCorp;
		String owner = ApiIdConverter.owner(human, bCorp);

		//Calculated:
		String name = ApiIdConverter.name(apiAsset.getTypeID(), settings.getItems());
		String group = ApiIdConverter.group(apiAsset.getTypeID(), settings.getItems());
		String category = ApiIdConverter.category(apiAsset.getTypeID(), settings.getItems());
		double basePrice = ApiIdConverter.priceBase(apiAsset.getTypeID(), settings.getItems());
		String meta = ApiIdConverter.meta(apiAsset.getTypeID(), settings.getItems());
		boolean marketGroup = ApiIdConverter.marketGroup(apiAsset.getTypeID(), settings.getItems());
		float volume = ApiIdConverter.volume(apiAsset.getTypeID(), settings.getItems());
		String security = ApiIdConverter.security(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		String region = ApiIdConverter.region(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		String location = ApiIdConverter.location(apiAsset.getLocationID(), parentEveAsset, settings.getConquerableStations(), settings.getLocations());
		List<EveAsset> parents = ApiIdConverter.parents(parentEveAsset);

		return new EveAsset(name, group, category, owner, count, location, parents, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}
	public static List<MarketOrder> apiMarketOrdersToMarketOrders(List<ApiMarketOrder> apiMarketOrders, Settings settings){
		List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (int a = 0; a < apiMarketOrders.size(); a++){
			marketOrders.add(apiMarketOrderToMarketOrder(apiMarketOrders.get(a), settings));
		}
		return marketOrders;
	}
	private static MarketOrder apiMarketOrderToMarketOrder(ApiMarketOrder apiMarketOrder, Settings settings){
		String name = ApiIdConverter.name((int)apiMarketOrder.getTypeID(), settings.getItems());
		String location = ApiIdConverter.location((int)apiMarketOrder.getStationID(), null, settings.getConquerableStations(), settings.getLocations());
		return new MarketOrder(apiMarketOrder, name, location);
	}
	public static List<IndustryJob> apiIndustryJobsToIndustryJobs(List<ApiIndustryJob> apiIndustryJobs, String owner, Settings settings){
		List<IndustryJob> industryJobs = new ArrayList<IndustryJob>();
		for (int a = 0; a < apiIndustryJobs.size(); a++){
			industryJobs.add(apiIndustryJobToIndustryJob(apiIndustryJobs.get(a), owner, settings));
		}
		return industryJobs;
	}

	private static IndustryJob apiIndustryJobToIndustryJob(ApiIndustryJob apiIndustryJob, String owner, Settings settings){
		String name = ApiIdConverter.name((int)apiIndustryJob.getInstalledItemTypeID(), settings.getItems());
		//FIXME This conversion is not working...
		String location = apiIndustryJobLocation(apiIndustryJob, settings);
		return new IndustryJob(apiIndustryJob, name, location, owner);
	}
	
	private static String apiIndustryJobLocation(ApiIndustryJob apiIndustryJob, Settings settings){
		String location = ApiIdConverter.location((int)apiIndustryJob.getInstalledItemLocationID(), null, settings.getConquerableStations(), settings.getLocations());
		if (location.contains("Error !")){
			location = ApiIdConverter.location((int)apiIndustryJob.getContainerLocationID(), null, settings.getConquerableStations(), settings.getLocations());
		}
		if (location.contains("Error !")){
			location = "Unknown";
		}
		return location;
	}
}
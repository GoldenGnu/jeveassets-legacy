/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.io.local;

import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.industry.ApiIndustryJob;
import com.beimin.eveapi.order.ApiMarketOrder;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AssetConverter;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AssetsReader extends AbstractXmlReader {
	public static boolean load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathAssets());
			parseSettings(element, settings);
		} catch (IOException ex) {
			Log.info("Assets not loaded");
			return false;
		} catch (XmlException ex) {
			Log.error("Assets not loaded: ("+Settings.getPathAssets()+")"+ex.getMessage(), ex);
		}
		Log.info("Assets loaded");
		return true;
	}
	private static void parseSettings(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("assets")) {
			throw new XmlException("Wrong root element name.");
		}
		//Accounts
		NodeList accountNodes = element.getElementsByTagName("accounts");
		if (accountNodes.getLength() == 1){
			Element accountsElement = (Element) accountNodes.item(0);
			parseAccounts(accountsElement, settings.getAccounts(), settings);
		}
		
	}
	private static void parseAccounts(Element element, List<Account> accounts, Settings settings){
		NodeList accountNodes = element.getElementsByTagName("account");
		for (int a = 0; a < accountNodes.getLength(); a++){
			Element currentNode = (Element) accountNodes.item(a);
			Account account = parseAccount(currentNode);
			parseHumans(currentNode, account, settings);
			accounts.add(account);
		}
	}

	private static Account parseAccount(Node node){
		int userID = AttributeGetters.getInt(node, "userid");
		String apiKey = AttributeGetters.getString(node, "apikey");
		Date nextUpdate = new Date( AttributeGetters.getLong(node, "charactersnextupdate") );
		return new Account(userID, apiKey, nextUpdate);
	}

	private static void parseHumans(Element element, Account account, Settings settings){
		NodeList humanNodes =  element.getElementsByTagName("human");
		for (int a = 0; a < humanNodes.getLength(); a++){
			Element currentNode = (Element) humanNodes.item(a);
			Human human = parseHuman(currentNode, account);
			account.getHumans().add(human);
			NodeList assetNodes = currentNode.getElementsByTagName("assets");
			if (assetNodes.getLength() == 1) parseAssets(assetNodes.item(0), human.getAssets(), null, settings);
			parseBalances(currentNode, human);
			parseMarkerOrders(currentNode, human);
			parseIndustryJobs(currentNode, human);
		}
	}
	private static Human parseHuman(Node node, Account account){
		String name = AttributeGetters.getString(node, "name");
		long characterID = AttributeGetters.getLong(node, "id");
		String corporation = AttributeGetters.getString(node, "corporation");
		boolean updateCorporationAssets = AttributeGetters.getBoolean(node, "corpassets");
		Date assetsNextUpdate = new Date( AttributeGetters.getLong(node, "assetsnextupdate") );
		Date balanceNextUpdate = new Date( AttributeGetters.getLong(node, "balancenextupdate") );
		boolean showAssets = true;
		if (AttributeGetters.haveAttribute(node, "show")){
			showAssets = AttributeGetters.getBoolean(node, "show");
		}
		Date marketOrdersNextUpdate = Settings.getGmtNow();
		if (AttributeGetters.haveAttribute(node, "marketordersnextupdate")){
			marketOrdersNextUpdate = new Date(AttributeGetters.getLong(node, "marketordersnextupdate"));
		}
		Date industryJobsNextUpdate = Settings.getGmtNow();
		if (AttributeGetters.haveAttribute(node, "industryjobsnextupdate")){
			industryJobsNextUpdate = new Date(AttributeGetters.getLong(node, "industryjobsnextupdate"));
		}

		return new Human(account, name, characterID, corporation, updateCorporationAssets, showAssets, assetsNextUpdate, balanceNextUpdate, marketOrdersNextUpdate, industryJobsNextUpdate);
	}

	private static void parseBalances(Element element, Human human){
		NodeList balancesNodes = element.getElementsByTagName("balances");
		for (int a = 0; a < balancesNodes.getLength(); a++){
			Element currentBalancesNode = (Element) balancesNodes.item(a);
			boolean bCorp = AttributeGetters.getBoolean(currentBalancesNode, "corp");
			NodeList balanceNodes = currentBalancesNode.getElementsByTagName("balance");

			for (int b = 0; b < balanceNodes.getLength(); b++){
				Element currentNode = (Element) balanceNodes.item(b);
				ApiAccountBalance AccountBalance = parseBalance(currentNode);
				if (bCorp){
					human.getAccountBalancesCorporation().add(AccountBalance);
				} else {
					human.getAccountBalances().add(AccountBalance);
				}

			}
		}
	}
	private static ApiAccountBalance parseBalance(Element element){
		ApiAccountBalance accountBalance = new ApiAccountBalance();
		int accountID = AttributeGetters.getInt(element, "accountid");
		int accountKey = AttributeGetters.getInt(element, "accountkey");
		double balance = AttributeGetters.getDouble(element, "balance");
		accountBalance.setAccountID(accountID);
		accountBalance.setAccountKey(accountKey);
		accountBalance.setBalance(balance);
		return accountBalance;
	}

	private static void parseMarkerOrders(Element element, Human human){
		NodeList markerOrdersNodes = element.getElementsByTagName("markerorders");
		for (int a = 0; a < markerOrdersNodes.getLength(); a++){
			Element currentMarkerOrdersNode = (Element) markerOrdersNodes.item(a);
			boolean bCorp = AttributeGetters.getBoolean(currentMarkerOrdersNode, "corp");
			NodeList markerOrderNodes = currentMarkerOrdersNode.getElementsByTagName("markerorder");
			for (int b = 0; b < markerOrderNodes.getLength(); b++){
				Element currentNode = (Element) markerOrderNodes.item(b);
				ApiMarketOrder apiMarketOrder = parseMarkerOrder(currentNode);
				if (bCorp){
					human.getMarketOrdersCorporation().add(apiMarketOrder);
				} else {
					human.getMarketOrders().add(apiMarketOrder);
				}
			}
		}
	}
	private static ApiMarketOrder parseMarkerOrder(Element element){
		ApiMarketOrder apiMarketOrder = new ApiMarketOrder();
		long orderID = AttributeGetters.getLong(element, "orderid");
		long charID = AttributeGetters.getLong(element, "charid");
		long stationID = AttributeGetters.getLong(element, "stationid");
		int volEntered = AttributeGetters.getInt(element, "volentered");
		int volRemaining = AttributeGetters.getInt(element, "volremaining");
		int minVolume = AttributeGetters.getInt(element, "minvolume");
		int orderState = AttributeGetters.getInt(element, "orderstate");
		long typeID = AttributeGetters.getLong(element, "typeid");
		int range = AttributeGetters.getInt(element, "range");
		int accountKey = AttributeGetters.getInt(element, "accountkey");
		int duration = AttributeGetters.getInt(element, "duration");
		double escrow = AttributeGetters.getDouble(element, "escrow");
		double price = AttributeGetters.getDouble(element, "price");
		int bid = AttributeGetters.getInt(element, "bid");
		String issued = AttributeGetters.getString(element, "issued");
		apiMarketOrder.setOrderID(orderID);
		apiMarketOrder.setCharID(charID);
		apiMarketOrder.setStationID(stationID);
		apiMarketOrder.setVolEntered(volEntered);
		apiMarketOrder.setVolRemaining(volRemaining);
		apiMarketOrder.setMinVolume(minVolume);
		apiMarketOrder.setOrderState(orderState);
		apiMarketOrder.setTypeID(typeID);
		apiMarketOrder.setRange(range);
		apiMarketOrder.setAccountKey(accountKey);
		apiMarketOrder.setDuration(duration);
		apiMarketOrder.setEscrow(escrow);
		apiMarketOrder.setPrice(price);
		apiMarketOrder.setBid(bid);
		apiMarketOrder.setIssued(issued);
		return apiMarketOrder;
	}

	private static void parseIndustryJobs(Element element, Human human){
		NodeList industryJobsNodes = element.getElementsByTagName("industryjobs");
		for (int a = 0; a < industryJobsNodes.getLength(); a++){
			Element currentIndustryJobsNode = (Element) industryJobsNodes.item(a);
			boolean bCorp = AttributeGetters.getBoolean(currentIndustryJobsNode, "corp");
			NodeList industryJobNodes = currentIndustryJobsNode.getElementsByTagName("industryjob");
			for (int b = 0; b < industryJobNodes.getLength(); b++){
				Element currentNode = (Element) industryJobNodes.item(b);
				ApiIndustryJob apiIndustryJob = parseIndustryJobs(currentNode);
				if (bCorp){
					human.getIndustryJobsCorporation().add(apiIndustryJob);
				} else {
					human.getIndustryJobs().add(apiIndustryJob);
				}
			}
		}
	}
	private static ApiIndustryJob parseIndustryJobs(Element element){
		ApiIndustryJob apiIndustryJob = new ApiIndustryJob();

		long jobID = AttributeGetters.getLong(element, "jobid");
		long containerID = AttributeGetters.getLong(element, "containerid");
		long installedItemID = AttributeGetters.getLong(element, "installeditemid");
		long installedItemLocationID = AttributeGetters.getLong(element, "installeditemlocationid");
		int installedItemQuantity = AttributeGetters.getInt(element, "installeditemquantity");
		int installedItemProductivityLevel = AttributeGetters.getInt(element, "installeditemproductivitylevel");
		int installedItemMaterialLevel = AttributeGetters.getInt(element, "installeditemmateriallevel");
		int installedItemLicensedProductionRunsRemaining = AttributeGetters.getInt(element, "installeditemlicensedproductionrunsremaining");
		long outputLocationID = AttributeGetters.getLong(element, "outputlocationid");
		long installerID = AttributeGetters.getLong(element, "installerid");
		int runs = AttributeGetters.getInt(element, "runs");
		int licensedProductionRuns = AttributeGetters.getInt(element, "licensedproductionruns");
		long installedInSolarSystemID = AttributeGetters.getLong(element, "installedinsolarsystemid");
		long containerLocationID = AttributeGetters.getLong(element, "containerlocationid");
		int materialMultiplier = AttributeGetters.getInt(element, "materialmultiplier");
		int charMaterialMultiplier = AttributeGetters.getInt(element, "charmaterialmultiplier");
		int timeMultiplier = AttributeGetters.getInt(element, "timemultiplier");
		int charTimeMultiplier = AttributeGetters.getInt(element, "chartimemultiplier");
		long installedItemTypeID = AttributeGetters.getLong(element, "installeditemtypeid");
		long outputTypeID = AttributeGetters.getLong(element, "outputtypeid");
		long containerTypeID = AttributeGetters.getLong(element, "containertypeid");
		long installedItemCopy = AttributeGetters.getLong(element, "installeditemcopy");
		int completed = AttributeGetters.getInt(element, "completed");
		int completedSuccessfully = AttributeGetters.getInt(element, "completedsuccessfully");
		int installedItemFlag = AttributeGetters.getInt(element, "installeditemflag");
		int outputFlag = AttributeGetters.getInt(element, "outputflag");
		int activityID = AttributeGetters.getInt(element, "activityid");
		int completedStatus = AttributeGetters.getInt(element, "completedstatus");
		String installTime = AttributeGetters.getString(element, "installtime");
		String beginProductionTime = AttributeGetters.getString(element, "beginproductiontime");
		String endProductionTime = AttributeGetters.getString(element, "endproductiontime");
		String pauseProductionTime = AttributeGetters.getString(element, "pauseproductiontime");

		apiIndustryJob.setJobID(jobID);
		apiIndustryJob.setContainerID(containerID);
		apiIndustryJob.setInstalledItemID(installedItemID);
		apiIndustryJob.setInstalledItemLocationID(installedItemLocationID);
		apiIndustryJob.setInstalledItemQuantity(installedItemQuantity);
		apiIndustryJob.setInstalledItemProductivityLevel(installedItemProductivityLevel);
		apiIndustryJob.setInstalledItemMaterialLevel(installedItemMaterialLevel);
		apiIndustryJob.setInstalledItemLicensedProductionRunsRemaining(installedItemLicensedProductionRunsRemaining);
		apiIndustryJob.setOutputLocationID(outputLocationID);
		apiIndustryJob.setInstallerID(installerID);
		apiIndustryJob.setRuns(runs);
		apiIndustryJob.setLicensedProductionRuns(licensedProductionRuns);
		apiIndustryJob.setInstalledInSolarSystemID(installedInSolarSystemID);
		apiIndustryJob.setContainerLocationID(containerLocationID);
		apiIndustryJob.setMaterialMultiplier(materialMultiplier);
		apiIndustryJob.setCharMaterialMultiplier(charMaterialMultiplier);
		apiIndustryJob.setTimeMultiplier(timeMultiplier);
		apiIndustryJob.setCharTimeMultiplier(charTimeMultiplier);
		apiIndustryJob.setInstalledItemTypeID(installedItemTypeID);
		apiIndustryJob.setOutputTypeID(outputTypeID);
		apiIndustryJob.setContainerTypeID(containerTypeID);
		apiIndustryJob.setInstalledItemCopy(installedItemCopy);
		apiIndustryJob.setCompleted(completed);
		apiIndustryJob.setCompletedSuccessfully(completedSuccessfully);
		apiIndustryJob.setInstalledItemFlag(installedItemFlag);
		apiIndustryJob.setOutputFlag(outputFlag);
		apiIndustryJob.setActivityID(activityID);
		apiIndustryJob.setCompletedStatus(completedStatus);
		apiIndustryJob.setInstallTime(installTime);
		apiIndustryJob.setBeginProductionTime(beginProductionTime);
		apiIndustryJob.setEndProductionTime(endProductionTime);
		apiIndustryJob.setPauseProductionTime(pauseProductionTime);

		return apiIndustryJob;
	}

	private static void parseAssets(Node node, List<EveAsset> assets, EveAsset parentEveAsset, Settings settings){
		NodeList assetsNodes = node.getChildNodes();
		EveAsset eveAsset = null;
		for (int a = 0; a < assetsNodes.getLength(); a++){
			Node currentNode = assetsNodes.item(a);
			if (currentNode.getNodeName().equals("asset")){
				eveAsset = parseEveAsset(currentNode, parentEveAsset, settings);
				if (parentEveAsset == null){
					assets.add(eveAsset);
				} else {
					parentEveAsset.addEveAsset(eveAsset);
				}
				parseAssets(currentNode, assets, eveAsset, settings);
			}
		}
	}
	private static EveAsset parseEveAsset(Node node, EveAsset parentEveAsset, Settings settings){
		long count = AttributeGetters.getLong(node, "count");
		String flag = AttributeGetters.getString(node, "flag");
		int id = AttributeGetters.getInt(node, "id");
		int typeID = AttributeGetters.getInt(node, "typeid");
		int locationID = AttributeGetters.getInt(node, "locationid");
		if (locationID == 0 && parentEveAsset != null) locationID = parentEveAsset.getLocationID();
		boolean singleton = AttributeGetters.getBoolean(node, "singleton");
		boolean corporationAsset = AttributeGetters.getBoolean(node, "corporationasset");
		String owner = AttributeGetters.getString(node, "owner");
		
		//Calculated:
		String name = AssetConverter.name(typeID, settings);
		String group = AssetConverter.group(typeID, settings);
		String category = AssetConverter.category(typeID, settings);
		double priceBase = AssetConverter.priceBase(typeID, settings);
		String meta = AssetConverter.meta(typeID, settings);
		boolean marketGroup = AssetConverter.marketGroup(typeID, settings);
		float volume = AssetConverter.volume(typeID, settings);
		String security = AssetConverter.security(locationID, parentEveAsset, settings);
		String location = AssetConverter.location(locationID, parentEveAsset, settings);
		String container = AssetConverter.container(locationID, parentEveAsset);
		String region = AssetConverter.region(locationID, parentEveAsset, settings);
		
		return new EveAsset(name, group, category, owner, count, location, container, flag, priceBase, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}
}

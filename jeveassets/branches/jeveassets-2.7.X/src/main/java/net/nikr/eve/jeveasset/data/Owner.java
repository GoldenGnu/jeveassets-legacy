/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import com.beimin.eveapi.parser.ApiAuthorization;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;


public class Owner implements Comparable<Owner> {
	private String name;
	private long ownerID;
	private boolean showOwner;
	private Date assetLastUpdate;
	private Date assetNextUpdate;
	private Date balanceNextUpdate;
	private Date marketOrdersNextUpdate;
	private Date journalNextUpdate;
	private Date transactionsNextUpdate;
	private Date industryJobsNextUpdate;
	private Date contractsNextUpdate;
	private MyAccount parentAccount;
	private List<MyAccountBalance> accountBalances;
	private List<MyMarketOrder> marketOrders;
	private Map<Long, MyTransaction> transactions;
	private Map<Long, MyJournal> journal;
	private List<MyIndustryJob> industryJobs;
	private Map<MyContract, List<MyContractItem>> contracts;
	private List<MyAsset> assets;

	public Owner(final MyAccount parentAccount, final Owner owner) {
		this(parentAccount,
				owner.getName(),
				owner.getOwnerID(),
				owner.isShowOwner(),
				owner.getAssetLastUpdate(),
				owner.getAssetNextUpdate(),
				owner.getBalanceNextUpdate(),
				owner.getMarketOrdersNextUpdate(),
				owner.getJournalNextUpdate(),
				owner.getTransactionsNextUpdate(),
				owner.getIndustryJobsNextUpdate(),
				owner.getContractsNextUpdate());
		accountBalances = owner.getAccountBalances();
		marketOrders = owner.getMarketOrders();
		industryJobs = owner.getIndustryJobs();
		assets = owner.getAssets();
		contracts = owner.getContracts();
		transactions = owner.getTransactions();
		journal = owner.getJournal();
	}

	public Owner(final MyAccount parentAccount, final String name, final long ownerID) {
		this(parentAccount, name, ownerID, true, null, Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow(), Settings.getNow());
	}

	public Owner(final MyAccount parentAccount, final String name, final long ownerID, final boolean showOwner, final Date assetLastUpdate, final Date assetNextUpdate, final Date balanceNextUpdate, final Date marketOrdersNextUpdate, final Date journalNextUpdate, final Date transactionsNextUpdate, final Date industryJobsNextUpdate, final Date contractsNextUpdate) {
		this.parentAccount = parentAccount;
		this.name = name;
		this.ownerID = ownerID;
		this.showOwner = showOwner;
		this.assetLastUpdate = assetLastUpdate;
		this.assetNextUpdate = assetNextUpdate;
		this.balanceNextUpdate = balanceNextUpdate;
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
		this.journalNextUpdate = journalNextUpdate;
		this.transactionsNextUpdate = transactionsNextUpdate;
		this.industryJobsNextUpdate = industryJobsNextUpdate;
		this.contractsNextUpdate = contractsNextUpdate;
		//Default
		assets = new ArrayList<MyAsset>();
		accountBalances = new  ArrayList<MyAccountBalance>();
		marketOrders = new  ArrayList<MyMarketOrder>();
		transactions = new HashMap<Long, MyTransaction>();
		industryJobs = new  ArrayList<MyIndustryJob>();
		contracts = new HashMap<MyContract, List<MyContractItem>>();
		journal = new HashMap<Long, MyJournal>();
	}

	public void setAccountBalances(final List<MyAccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}

	public void setAssets(final List<MyAsset> assets) {
		this.assets = assets;
	}

	public void setAssetLastUpdate(final Date assetLastUpdate) {
		this.assetLastUpdate = assetLastUpdate;
	}

	public void setAssetNextUpdate(final Date nextUpdate) {
		this.assetNextUpdate = nextUpdate;
	}

	public void setBalanceNextUpdate(final Date balanceNextUpdate) {
		this.balanceNextUpdate = balanceNextUpdate;
	}

	public void setContracts(final Map<MyContract, List<MyContractItem>> contracts) {
		this.contracts = contracts;
	}

	public void setContractsNextUpdate(final Date contractsNextUpdate) {
		this.contractsNextUpdate = contractsNextUpdate;
	}

	public void setIndustryJobs(final List<MyIndustryJob> industryJobs) {
		this.industryJobs = industryJobs;
	}

	public void setIndustryJobsNextUpdate(final Date industryJobsNextUpdate) {
		this.industryJobsNextUpdate = industryJobsNextUpdate;
	}

	public void setMarketOrders(final List<MyMarketOrder> marketOrders) {
		this.marketOrders = marketOrders;
	}

	public void setMarketOrdersNextUpdate(final Date marketOrdersNextUpdate) {
		this.marketOrdersNextUpdate = marketOrdersNextUpdate;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
	}

	public void setShowOwner(final boolean showOwner) {
		this.showOwner = showOwner;
	}

	public void setJournal(final Map<Long, MyJournal> journal) {
		this.journal = journal;
	}

	public void setJournalNextUpdate(Date journalNextUpdate) {
		this.journalNextUpdate = journalNextUpdate;
	}

 	public void setTransactions(final Map<Long, MyTransaction> transactions) {
		this.transactions = transactions;
	}

	public void setTransactionsNextUpdate(final Date transactionsNextUpdate) {
		this.transactionsNextUpdate = transactionsNextUpdate;
	}

	public boolean isShowOwner() {
		return showOwner;
	}

	public boolean isCorporation() {
		return parentAccount.isCorporation();
	}

	public boolean isCharacter() {
		return parentAccount.isCharacter();
	}

	public List<MyAccountBalance> getAccountBalances() {
		return accountBalances;
	}

	public List<MyAsset> getAssets() {
		return assets;
	}

	public Date getAssetLastUpdate() {
		return assetLastUpdate;
	}

	public Date getAssetNextUpdate() {
		return assetNextUpdate;
	}

	public Date getBalanceNextUpdate() {
		return balanceNextUpdate;
	}

	public Map<MyContract, List<MyContractItem>> getContracts() {
		return contracts;
	}

	public Date getContractsNextUpdate() {
		return contractsNextUpdate;
	}

	public List<MyIndustryJob> getIndustryJobs() {
		return industryJobs;
	}

	public Date getIndustryJobsNextUpdate() {
		return industryJobsNextUpdate;
	}

	public List<MyMarketOrder> getMarketOrders() {
		return marketOrders;
	}

	public Date getMarketOrdersNextUpdate() {
		return marketOrdersNextUpdate;
	}

	public String getName() {
		return name;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public MyAccount getParentAccount() {
		return parentAccount;
	}

	public Map<Long, MyJournal> getJournal() {
		return journal;
	}

	public Date getJournalNextUpdate() {
		return journalNextUpdate;
	}

	public Map<Long, MyTransaction> getTransactions() {
 		return transactions;
	}

	public Date getTransactionsNextUpdate() {
		return transactionsNextUpdate;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Owner other = (Owner) obj;
		if (this.ownerID != other.ownerID) {
			return false;
		}
		if (this.parentAccount != other.parentAccount && (this.parentAccount == null || !this.parentAccount.equals(other.parentAccount))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (int) (this.ownerID ^ (this.ownerID >>> 32));
		hash = 89 * hash + (this.parentAccount != null ? this.parentAccount.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(final Owner o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

	public static ApiAuthorization getApiAuthorization(final MyAccount account) {
		return new ApiAuthorization(account.getKeyID(), account.getVCode());
	}
	public static ApiAuthorization getApiAuthorization(final Owner owner) {
		return getApiAuthorization(owner.getParentAccount(), owner.getOwnerID());
	}
	private static ApiAuthorization getApiAuthorization(final MyAccount account, final long ownerID) {
		return new ApiAuthorization(account.getKeyID(), ownerID, account.getVCode());
	}
}

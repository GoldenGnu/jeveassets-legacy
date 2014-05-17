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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.WalletTransaction;
import com.beimin.eveapi.response.shared.WalletTransactionsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccount.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.io.shared.AbstractApiAccountKeyGetter;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class TransactionsGetter extends AbstractApiAccountKeyGetter<WalletTransactionsResponse, MyTransaction> {

	private boolean saveHistory;

	public TransactionsGetter() {
		super("Transaction");
	}

	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<MyAccount> accounts, final boolean saveHistory) {
		super.loadAccounts(updateTask, forceUpdate, accounts);
		this.saveHistory = saveHistory;
	}

	@Override
	protected Map<Long, MyTransaction> get() {
		if (saveHistory) {
			return getOwner().getTransactions();
		} else {
			return new HashMap<Long, MyTransaction>();
		}
	}

	@Override
	protected void set(Map<Long, MyTransaction> values, Date nextUpdate) {
		getOwner().setTransactions(values);
		getOwner().setTransactionsNextUpdate(nextUpdate);
	}

	@Override
	protected WalletTransactionsResponse getResponse(boolean bCorp, int accountKey, long fromID, int rowCount) throws ApiException {
		if (bCorp) {
			return new com.beimin.eveapi.parser.corporation.WalletTransactionsParser()
					.getResponse(Owner.getApiAuthorization(getOwner()), accountKey, fromID, rowCount);
		} else {
			return new com.beimin.eveapi.parser.pilot.WalletTransactionsParser()
					.getTransactionsResponse(Owner.getApiAuthorization(getOwner()), fromID, rowCount);
		}
	}

	@Override
	protected Map<Long, MyTransaction> convertData(WalletTransactionsResponse response, int accountKey) {
		List<WalletTransaction> api = new ArrayList<WalletTransaction>(response.getAll());
		return ApiConverter.convertTransactions(api, getOwner(), accountKey);
	}

	@Override
	protected Date getNextUpdate() {
		return getOwner().getTransactionsNextUpdate();
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) {
		ownerTo.setTransactions(ownerFrom.getTransactions());
		ownerTo.setTransactionsNextUpdate(ownerFrom.getTransactionsNextUpdate());
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.TRANSACTIONS_CORP.getAccessMask();
		} else {
			return AccessMask.TRANSACTIONS_CHAR.getAccessMask();
		}
	}
}
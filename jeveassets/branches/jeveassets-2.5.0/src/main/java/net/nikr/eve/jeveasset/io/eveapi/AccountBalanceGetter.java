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

package net.nikr.eve.jeveasset.io.eveapi;


import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.accountbalance.AccountBalanceResponse;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class AccountBalanceGetter extends AbstractApiGetter<AccountBalanceResponse> {

	public AccountBalanceGetter() {
		super("Account Balance", 1, true, false);
	}

	@Override
	public void load(final UpdateTask updateTask, final boolean forceUpdate, final Human human) {
		super.load(updateTask, forceUpdate, human);
	}

	@Override
	public void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		super.load(updateTask, forceUpdate, accounts);
	}

	@Override
	protected AccountBalanceResponse getResponse(final boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation
					.accountbalance
					.AccountBalanceParser.getInstance()
					.getResponse(Human.getApiAuthorization(getHuman()));
		} else {
			return com.beimin.eveapi.character
					.accountbalance
					.AccountBalanceParser.getInstance()
					.getResponse(Human.getApiAuthorization(getHuman()));
		}
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		getHuman().setBalanceNextUpdate(nextUpdate);
	}

	@Override
	protected Date getNextUpdate() {
		return getHuman().getBalanceNextUpdate();
	}

	@Override
	protected void setData(final AccountBalanceResponse response) {
		List<EveAccountBalance> accountBalances = new ArrayList<EveAccountBalance>(response.getAll());
		getHuman().setAccountBalances(accountBalances);
	}

	@Override
	protected void updateFailed(final Human humanFrom, final Human humanTo) {
		humanTo.setAccountBalances(humanFrom.getAccountBalances());
		humanTo.setBalanceNextUpdate(humanFrom.getBalanceNextUpdate());
	}
}

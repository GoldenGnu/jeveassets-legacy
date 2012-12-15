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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.core.ApiError;
import com.beimin.eveapi.core.ApiResponse;
import com.beimin.eveapi.exception.ApiException;
import java.util.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractApiGetter<T extends ApiResponse> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractApiGetter.class);

	private String taskName;
	private Account account;
	private Human human;
	private boolean forceUpdate;
	private boolean updated;
	private boolean updateHuman;
	private boolean updateAccount;
	private UpdateTask updateTask;
	private Map<String, Human> owners;
	private List<Human> failOwners;
	private int requestMask;
	private boolean error;

	protected AbstractApiGetter(final String name) {
		this(name, 0, false, false);
	}

	protected AbstractApiGetter(final String taskName, final int requestMask, final boolean updateHuman, final boolean updateAccount) {
		this.taskName = taskName;
		this.updateHuman = updateHuman;
		this.updateAccount = updateAccount;
		this.requestMask = requestMask;
	}

	protected void load(final UpdateTask updateTask, final boolean forceUpdate, final String characterName) {
		init(updateTask, forceUpdate, null, null);
		load(getNextUpdate(), false, characterName);
	}

	protected void load(final UpdateTask updateTask, final boolean forceUpdate, final Human human) {
		init(updateTask, forceUpdate, human, null);
		loadHuman();
	}

	protected void load(final UpdateTask updateTask, final boolean forceUpdate, final Account account) {
		init(updateTask, forceUpdate, null, account);
		loadAccount();
	}

	protected void load(final UpdateTask updateTask, final boolean forceUpdate, final List<Account> accounts) {
		init(updateTask, forceUpdate, null, null);
		LOG.info("{} updating:", taskName);
		for (int a = 0; a < accounts.size(); a++) {
			account = accounts.get(a);
			if (updateAccount) {
				if (updateTask != null) {
					if (updateTask.isCancelled()) {
						addError(String.valueOf(account.getKeyID()), "Cancelled");
					} else {
						loadAccount();
					}
					updateTask.setTaskProgress(accounts.size(), (a + 1), 0, 100);
				} else {
					loadAccount();
				}
			}
			if (updateHuman) {
				List<Human> humans = account.getHumans();
				for (int b = 0; b < humans.size(); b++) {
					human = humans.get(b);
					if (updateTask != null) {
						if (updateTask.isCancelled()) {
							addError(human.getName(), "Cancelled");
						} else {
							loadHuman();
						}
						updateTask.setTaskProgress(accounts.size() * 3, (a * 3) + (b + 1), 0, 100);
					} else {
						loadHuman();
					}
				}
			}
		}
		//Set data for duplicated/failed owners
		if (updateHuman) {
			for (Human failHuman : failOwners) {
				Human okHuman = owners.get(failHuman.getName());
				if (okHuman != null) {
					updateFailed(okHuman, failHuman);
				}
			}
		}
		if (updated && updateTask != null && !updateTask.hasError()) {
			LOG.info("	{} updated (ALL)", taskName);
		} else if (updated && updateTask != null && updateTask.hasError()) {
			LOG.info("	{} updated (SOME)", taskName);
		} else {
			LOG.info("	{} not updated (NONE)", taskName);
		}
	}

	private void init(final UpdateTask updateTask, final boolean forceUpdate, final Human human, final Account account) {
		this.forceUpdate = forceUpdate;
		this.updateTask = updateTask;
		this.human = human;
		this.account = account;
		this.updated = false;
		this.error = false;
		this.owners = new HashMap<String, Human>();
		this.failOwners = new ArrayList<Human>();
	}

	private void loadHuman() {
		boolean updatedOK = false;
		String name = human.getName();
		//Ignore hidden owners && don't update the same owner twice
		if (human.isShowAssets() && !owners.containsKey(name)) {
			updatedOK = load(getNextUpdate(), human.isCorporation(), name); //Update...
		}
		if (updatedOK) {
			owners.put(name, human); //If updated ok: don't update the same owner again...
		} else {
			failOwners.add(human); //Save duplicated/failed owners
		}
	}

	private void loadAccount() {
		load(getNextUpdate(), false, String.valueOf("Account #" + account.getKeyID()));
	}

	private boolean load(final Date nextUpdate, final boolean updateCorporation, final String updateName) {
		//Check API key access mask
		if ((getAccessMask() & requestMask) != requestMask) {
			addError(updateName, "Not enough access privileges");
			LOG.info("	{} failed to update for: {} (NOT ENOUGH ACCESS PRIVILEGES)", taskName, updateName);
			return false;
		}
		//Check API cache time
		if (!isUpdatable(nextUpdate)) {
			addError(updateName, "Not allowed yet");
			LOG.info("	{} failed to update for: {} (NOT ALLOWED YET)", taskName, updateName);
			return false;
		}
		//Check if API key is expired (not to check the account...)
		if (isExpired() && !updateAccount) {
			addError(updateName, "API Key expired");
			LOG.info("	{} failed to update for: {} (API KEY EXPIRED)", taskName, updateName);
			return false;
		}
		try {
			T response = getResponse(updateCorporation);
			if (response instanceof ApiResponse) {
				ApiResponse apiResponse = (ApiResponse) response;
				setNextUpdate(apiResponse.getCachedUntil());
				if (!apiResponse.hasError()) {
					LOG.info("	{} updated for: {}", taskName, updateName);
					this.updated = true;
					setData(response);
					return true;
				} else {
					ApiError apiError = apiResponse.getError();
					addError(updateName, apiError.getError());
					LOG.info("	{} failed to update for: {} (API ERROR: code: {} :: {})", new Object[]{taskName, updateName, apiError.getCode(), apiError.getError()});
				}
			}
		} catch (ApiException ex) {
			addError(updateName, "Api Error (" + ex.getMessage() + ")");
			LOG.info("	{} failed to update for: {} (ApiException: {})", new Object[]{taskName, updateName, ex.getMessage()});
		}
		return false;
	}

	private long getAccessMask() {
		if (account != null) {
			return account.getAccessMask();
		} else if (human != null) {
			return human.getParentAccount().getAccessMask();
		} else {
			return 0;
		}
	}
	private boolean isExpired() {
		if (account != null) {
			return account.isExpired();
		} else if (human != null) {
			return human.getParentAccount().isExpired();
		} else {
			return false;
		}
	}

	protected Account getAccount() {
		return account;
	}

	protected Human getHuman() {
		return human;
	}

	protected boolean isForceUpdate() {
		return forceUpdate;
	}

	public boolean hasError() {
		return error;
	}

	protected void addError(final String human, final String errorText) {
		if (updateTask != null) {
			updateTask.addError(human, errorText);
		}
		error = true;
	}

	protected abstract T getResponse(boolean bCorp) throws ApiException;
	protected abstract Date getNextUpdate();
	protected abstract void setNextUpdate(Date nextUpdate);
	protected abstract void setData(T response);
	protected abstract void updateFailed(Human humanFrom, Human humanTo);

	private boolean isUpdatable(final Date date) {
		return ((Settings.getNow().after(date)
				|| Settings.getNow().equals(date)
				|| forceUpdate
				|| Program.isForceUpdate()
				)
				&& !Program.isForceNoUpdate());
	}
}

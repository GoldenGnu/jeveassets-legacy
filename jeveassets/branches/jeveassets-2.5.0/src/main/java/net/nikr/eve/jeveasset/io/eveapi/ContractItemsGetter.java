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
import com.beimin.eveapi.shared.contract.ContractType;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.ContractItemsResponse;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class ContractItemsGetter extends AbstractApiGetter<ContractItemsResponse> {

	private EveContract currentContract;

	public ContractItemsGetter() {
		super("Contract Items", false, false);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		//Calc size
		int progress = updateTask.getProgress();
		int size = 0;
		for (Account account : accounts) {
			for (Owner owner : account.getOwners()) {
				size = size + owner.getContracts().size();
			}
		}
		int count = 0;
		for (Account account : accounts) {
			for (Owner owner : account.getOwners()) {
				for (EveContract contract : owner.getContracts().keySet()) {
					if (updateTask != null && updateTask.isCancelled()) {
						return; //We are done here...
					}
					count++; //Also count COURIER
					if (contract.getType() == ContractType.COURIER) {
						continue;
					}
					this.setTaskName("Contract Item ("+contract.getContractID()+")");
					currentContract = contract;
					super.load(updateTask, forceUpdate, owner);
					if (updateTask != null) {
						updateTask.setTaskProgress(size, count, progress, 100);
					}
				}
			}
		}
	}

	@Override
	protected ContractItemsResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation.contract.ContractItemsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()), currentContract.getContractID());
		} else {
			return com.beimin.eveapi.character.contract.ContractItemsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()), currentContract.getContractID());
		}
	}

	@Override
	protected Date getNextUpdate() {
		return new Date();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		//Do nothing...
	}

	@Override
	protected void setData(ContractItemsResponse response) {
		List<EveContractItem> contractItems = new ArrayList<EveContractItem>(response.getAll());
		getOwner().setContracts(currentContract, contractItems);
	}

	@Override
	protected void updateFailed(Owner ownerFrom, Owner ownerTo) {
		//FIXME ContractItemsGetter.updateFailed does nothing
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.CONTRACTS_CORP.getAccessMask();
		} else {
			return AccessMask.CONTRACTS_CHAR.getAccessMask();
		}
	}
	
}
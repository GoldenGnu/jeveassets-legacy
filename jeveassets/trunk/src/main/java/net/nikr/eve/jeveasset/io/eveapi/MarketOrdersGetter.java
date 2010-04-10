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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.order.ApiMarketOrder;
import com.beimin.eveapi.order.Parser;
import com.beimin.eveapi.order.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import org.xml.sax.SAXException;


public class MarketOrdersGetter extends AbstractApiGetter<Response> {

	public MarketOrdersGetter() {
		super("Market Orders", true, false);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		super.load(updateTask, forceUpdate, accounts);
	}

	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		return parser.getMarketOrders(Human.getApiAuthorization(getHuman()), bCorp);
	}

	@Override
	protected Date getNextUpdate() {
		return getHuman().getMarketOrdersNextUpdate();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getHuman().setMarketOrdersNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(Response response, boolean bCorp) {
		List<ApiMarketOrder> marketOrders = new Vector<ApiMarketOrder>(response.getMarketOrders());
		if (bCorp){
			getHuman().setMarketOrdersCorporation(marketOrders);
		} else {
			getHuman().setMarketOrders(marketOrders);
		}
	}
}

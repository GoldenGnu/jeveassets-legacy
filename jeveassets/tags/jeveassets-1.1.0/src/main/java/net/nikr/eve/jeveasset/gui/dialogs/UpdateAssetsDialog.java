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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.JUpdateWindow;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.EveApiAssetsReader;
import net.nikr.eve.jeveasset.io.EveApiConquerableStationsReader;
import net.nikr.eve.jeveasset.io.EveCentralMarketstatReader;
import net.nikr.eve.jeveasset.io.Online;
import net.nikr.log.Log;


public class UpdateAssetsDialog extends JUpdateWindow implements PropertyChangeListener {

	private UpdateAssetsTask updateAssetsTask;

	public UpdateAssetsDialog(Program program, Window parent) {
		super(program, parent, "Updating Assets...");
	}

	@Override
	public void startUpdate(){
		setVisible(true);
		updateAssetsTask = new UpdateAssetsTask();
		updateAssetsTask.addPropertyChangeListener(this);
		updateAssetsTask.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int value = updateAssetsTask.getProgress();
		if (updateAssetsTask.getThrowable() != null){
			Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", updateAssetsTask.getThrowable());
		}
		if (value == 100 && updateAssetsTask.isTaskDone()){
			updateAssetsTask.setTaskDone(false);
			if (updateAssetsTask.updated){
				program.assetsChanged();
				program.getStatusPanel().updateAssetDate();
				program.getStatusPanel().updateEveCentralDate();
			}
			jProgressBar.setValue(100);
			jProgressBar.setIndeterminate(false);
			setVisible(false);
			if (updateAssetsTask.updated && !updateAssetsTask.updateFailed){ //All assets updated
				Log.info("All assets updated");
				JOptionPane.showMessageDialog(parent, "All assets and the price data was updated.", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else if(updateAssetsTask.updated && updateAssetsTask.updateFailed) { //Some assets updated
				Log.info("Some assets updated");
				JOptionPane.showMessageDialog(parent, "Some assets and the price data was updated.", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else if (!updateAssetsTask.isOnline) { //No assets updated
				Log.info("No assets updated (no connection)");
				JOptionPane.showMessageDialog(parent, "Could not update assets.\r\nPlease connect to the internet and try again...", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else if (program.getSettings().getAccounts().isEmpty()) {
				Log.info("No assets updated");
				JOptionPane.showMessageDialog(parent, "No assets updated\r\nYou need to add your API Key:\r\nOptions > Manage API Keys > Add.", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else if (!updateAssetsTask.isShown) {
				Log.info("No assets updated");
				JOptionPane.showMessageDialog(parent, "No assets updated (none selected)\r\nAll characters are hidden", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else if (updateAssetsTask.error != null) {
				Log.info("No assets updated");
				JOptionPane.showMessageDialog(parent, "No assets updated.\r\n"+updateAssetsTask.error, "Update Assets", JOptionPane.PLAIN_MESSAGE);
			} else {
				Log.info("No assets updated");
				JOptionPane.showMessageDialog(parent, "No assets updated (not allowed yet).\r\nCCP only allow you to update assets once a day...", "Update Assets", JOptionPane.PLAIN_MESSAGE);
			}
		} else if (value > 0){
			jProgressBar.setIndeterminate(false);
			jProgressBar.setValue(value);
		} else {
			jProgressBar.setIndeterminate(true);
		}
	}

	class UpdateAssetsTask extends UpdateTask {

		private boolean updated = false;
		private boolean isShown = false;
		private boolean updateFailed = false;
		private boolean isOnline = true;
		private boolean conquerableStationsUpdated = false;
		private String error = null;

		public UpdateAssetsTask() {
			
		}

		@Override
		public void update() throws Throwable {
			List<Account> accounts = program.getSettings().getAccounts();
			List<String> coporations = new Vector<String>();
			for (int a = 0; a < accounts.size(); a++){
				Account account = accounts.get(a);
				List<Human> humans = account.getHumans();
				for (int b = 0; b < humans.size(); b++){
					setTaskProgress(accounts.size() * 3, (a*3)+b, 0, 20);
					Human human = humans.get(b);
					if (human.isShowAssets()){
						isShown = true;
						if (human.isAssetsUpdatable() && !conquerableStationsUpdated){
							EveApiConquerableStationsReader.load(program.getSettings());
							conquerableStationsUpdated = true;
						}
						if (coporations.contains(human.getCorporation())){
							human.setUpdateCorporationAssets(false);
						}
						boolean returned = EveApiAssetsReader.load(program.getSettings(), human);
						if (human.isUpdateCorporationAssets()){
							coporations.add(human.getCorporation());
						}
						if (returned){
							updated = true;
						} else {
							error = EveApiAssetsReader.getError();
							isOnline = Online.isOnline(program.getSettings());
							updateFailed = true;
						}
					}
				}
			}
			if (updated){
				program.getSettings().clearEveAssetList();
				EveCentralMarketstatReader.load(program.getSettings(), this, true);
			}
		}

	}
}

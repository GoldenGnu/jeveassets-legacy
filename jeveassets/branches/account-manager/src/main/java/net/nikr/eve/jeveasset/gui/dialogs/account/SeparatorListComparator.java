/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.gui.dialogs.account;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.Human;

/**
 *
 * @author Niklas
 */
public class SeparatorListComparator implements Comparator<Human> {

	@Override
	public int compare(Human o1, Human o2) {
		Integer userId1 = o1.getParentAccount().getUserID();
		return userId1.compareTo(o2.getParentAccount().getUserID());
	}

}

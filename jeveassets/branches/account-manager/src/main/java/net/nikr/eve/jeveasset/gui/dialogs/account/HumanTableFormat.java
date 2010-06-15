/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Human;

/**
 *
 * @author Niklas
 */
public class HumanTableFormat implements AdvancedTableFormat<Object>, WritableTableFormat<Object>{

	private List<String> columnNames;

	public HumanTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Corporation");
		columnNames.add("Show Assets");
		columnNames.add("Show Corporation");
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}

	@Override
	public Object getColumnValue(Object baseObject, int column) {
		String sColumn = columnNames.get(column);
		if (baseObject instanceof Human){
			Human human = (Human) baseObject;
			if (sColumn.equals("Name")) return human.getName();
			if (sColumn.equals("Corporation")) return human.getCorporation();
			if (sColumn.equals("Show Assets")) return human.isShowAssets();
			if (sColumn.equals("Show Corporation")) return human.isUpdateCorporationAssets();
		}
		return new Object();
	}

	@Override
	public Class getColumnClass(int column) {
		String sColumn = columnNames.get(column);
		if (sColumn.equals("Name")) return String.class;
		if (sColumn.equals("Corporation")) return String.class;
		if (sColumn.equals("Show Assets")) return Boolean.class;
		if (sColumn.equals("Show Corporation")) return Boolean.class;
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	@Override
	public boolean isEditable(Object baseObject, int column) {
		String sColumn = columnNames.get(column);
		if (baseObject instanceof Human){
			if (sColumn.equals("Show Assets")) return true;
			if (sColumn.equals("Show Corporation")) return true;
		}
		return false;
	}

	@Override
	public Object setColumnValue(Object baseObject, Object editedValue, int column) {
		String sColumn = columnNames.get(column);
		if (editedValue instanceof Boolean && baseObject instanceof Human){
			Human human = (Human) baseObject;
			boolean value = (Boolean) editedValue;
			if (sColumn.equals("Show Assets")) human.setShowAssets(value);
			if (sColumn.equals("Show Corporation")) human.setUpdateCorporationAssets(value);
			return baseObject;
		}
		return null;
		
	}

}

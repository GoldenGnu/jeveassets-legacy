package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.TableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & EnumTableColumn<Q>, Q> implements AdvancedTableFormat<Q> {
	private static final Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);

	private final List<T> shownColumns;
	private final List<T> orderColumns;
	private final List<T> originalColumns;
	private TableSettings<T, Q> tableSettings = null;
	ColumnComparator columnComparator;


	public EnumTableFormatAdaptor(Class<T> enumClass) {
		shownColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		orderColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		originalColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		columnComparator = new ColumnComparator();
	}

	public List<T> getShownColumns(){
		return shownColumns;
	}

	public List<T> getOrderColumns(){
		return orderColumns;
	}

	public void setTableSettings(TableSettings<T, Q> tableSettings){
		this.tableSettings = tableSettings;
	}

	public void resetColumns(){
		orderColumns.clear();
		orderColumns.addAll(originalColumns);
		shownColumns.clear();
		shownColumns.addAll(originalColumns);
		updateColumns();
	}

	public void moveColumn(int from, int to){
		if (from == to) return;
		T fromColumn = getColumn(from);
		T toColumn = getColumn(to);

		int fromIndex = orderColumns.indexOf(fromColumn);
		orderColumns.remove(fromIndex);

		int toIndex = orderColumns.indexOf(toColumn);
		if (to > from) toIndex++;
		orderColumns.add(toIndex, fromColumn);

		updateColumns();
	}

	public void hideColumn(T column){
		if (!shownColumns.contains(column)) return;
		shownColumns.remove(column);
		updateColumns();
	}

	public void showColumn(T column){
		if (shownColumns.contains(column)) return;
		shownColumns.add(column);
		updateColumns();
	}

	private T getColumn(int i){
		if (i >= shownColumns.size()) return null;
		return shownColumns.get(i);
	}

	private void updateColumns(){
		Collections.sort(shownColumns, columnComparator);
		if (tableSettings != null){
			tableSettings.setTableColumnNames(orderColumns);
			tableSettings.setTableColumnVisible(shownColumns);
		}
	}

	private List<T> getColumns() {
		return shownColumns;
	}

	@Override public Class getColumnClass(int i) {
		return getColumn(i).getType();
	}

	@Override public Comparator getColumnComparator(int i) {
		return getColumn(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getColumns().size();
	}

	@Override public String getColumnName(int i) {
		return getColumn(i).getColumnName();
	}

	@Override public Object getColumnValue(Q e, int i) {
		return getColumn(i).getColumnValue(e);
	}

	class ColumnComparator implements Comparator<T>{

		@Override
		public int compare(T o1, T o2) {
			return orderColumns.indexOf(o1) - orderColumns.indexOf(o2);
		}

	}
}

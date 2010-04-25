/*
 * Copyright 2009, 2010
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

package net.nikr.eve.jeveasset.gui.table;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.frame.FilterPanel;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;


public class EveAssetMatcherEditor extends AbstractMatcherEditor<EveAsset> implements ActionListener, DocumentListener, KeyListener{

	public final static String ACTION_COLUMN_SELECTED = "ACTION_COLUMN_SELECTED";
	public final static String ACTION_MODE_SELECTED = "ACTION_MODE_SELECTED";
	public final static String ACTION_TIMER = "ACTION_TIMER";

	private JComboBox jAnd;
	private JComboBox jColumn;
	private JComboBox jMode;
	private JComboBox jMatchColumn;
	private JTextField jText;
	private Program program;
	private FilterPanel filterPanel;
	private EveAssetMatching eveAssetMatching = new EveAssetMatching();
	private boolean columnCompare = false;


	private Timer timer;

	public EveAssetMatcherEditor(Program program, FilterPanel filterPanel) {
		this.program = program;
		this.filterPanel = filterPanel;

		timer = new Timer(500, this);
		timer.setActionCommand(ACTION_TIMER);

		jAnd = new JComboBox(new Object[] {AssetFilter.AND, AssetFilter.OR});
		jAnd.addActionListener(this);

		Vector<String> columns = new Vector<String>();
		columns.add("All");
		columns.addAll( program.getSettings().getTableColumnNames() );
		jColumn = new JComboBox(columns);
		jColumn.setActionCommand(ACTION_COLUMN_SELECTED);
		jColumn.addActionListener(this);

		jMatchColumn = new JComboBox( new Vector<String>(program.getSettings().getTableNumberColumns()) );
		jMatchColumn.addActionListener(this);

		jMode = new JComboBox(new Object[] {AssetFilter.MODE_CONTAIN,
											AssetFilter.MODE_CONTAIN_NOT,
											AssetFilter.MODE_EQUALS,
											AssetFilter.MODE_EQUALS_NOT
											});

		jMode.setActionCommand(ACTION_MODE_SELECTED);
		jMode.addActionListener(this);

		jText = new JTextField();
		JCopyPopup.install(jText);
		jText.getDocument().addDocumentListener(this);
		jText.addKeyListener(this);
	}

	public boolean isAnd(){
		String s = (String)jAnd.getSelectedItem();
		return (s.equals(AssetFilter.AND));
	}

	public boolean isEmpty(){
		return jText.getText().equals("") && !columnCompare;
	}

	public JComboBox getAnd() {
		return jAnd;
	}

	public JComboBox getMode() {
		return jMode;
	}

	public JComboBox getColumn() {
		return jColumn;
	}

	public JComboBox getMatchColumn() {
		return jMatchColumn;
	}

	public JTextField getText() {
		return jText;
	}

	public void refilter(){
		if (columnCompare){
			this.fireChanged(new EveAssetMatcher((String) jColumn.getSelectedItem(), (String) jMode.getSelectedItem(), "", (String) jMatchColumn.getSelectedItem()));
		} else {
			this.fireChanged(new EveAssetMatcher((String) jColumn.getSelectedItem(), (String) jMode.getSelectedItem(), jText.getText(), null));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_COLUMN_SELECTED.equals(e.getActionCommand())){
			String column = (String) jColumn.getSelectedItem();
			int index = jMode.getSelectedIndex();
			if (program.getSettings().getTableNumberColumns().contains(column)){
				jMode.setModel( new DefaultComboBoxModel(
						new Object[] {AssetFilter.MODE_CONTAIN,
									  AssetFilter.MODE_CONTAIN_NOT,
									  AssetFilter.MODE_EQUALS,
									  AssetFilter.MODE_EQUALS_NOT,
									  AssetFilter.MODE_GREATER_THAN,
									  AssetFilter.MODE_LESS_THAN,
									  AssetFilter.MODE_GREATER_THAN_COLUMN,
									  AssetFilter.MODE_LESS_THAN_COLUMN
				}) );
				jMode.setSelectedIndex(index);
			} else {
				jMode.setModel( new DefaultComboBoxModel(
						new Object[] {AssetFilter.MODE_CONTAIN,
									  AssetFilter.MODE_CONTAIN_NOT,
									  AssetFilter.MODE_EQUALS,
									  AssetFilter.MODE_EQUALS_NOT
				}) );
				if (index > 3){
					jMode.setSelectedIndex(0);
				} else {
					jMode.setSelectedIndex(index);
				}
			}
		}
		if (ACTION_MODE_SELECTED.equals(e.getActionCommand())){
			String column = (String) jMode.getSelectedItem();
			if (column.equals(AssetFilter.MODE_GREATER_THAN_COLUMN) || column.equals(AssetFilter.MODE_LESS_THAN_COLUMN) ){
				columnCompare = true;
				
			} else {
				columnCompare = false;
			}
			filterPanel.columnCompare(columnCompare);
		}
		if (ACTION_TIMER.equals(e.getActionCommand())){
			timer.stop();
		}
		refilter();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (!program.getSettings().isFilterOnEnter()){
			timer.stop();
			timer.start();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (!program.getSettings().isFilterOnEnter()){
			timer.stop();
			timer.start();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (!program.getSettings().isFilterOnEnter()){
			timer.stop();
			timer.start();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			refilter();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	private class EveAssetMatcher implements Matcher<EveAsset> {
		private final String column;
		private final String mode;
		private final String text;
		private String columnMatch;

		public EveAssetMatcher(String column, String mode, String text, String columnMatch) {
			this.column = column;
			this.mode = mode;
			this.text = text;
			this.columnMatch = columnMatch;
		}

		@Override
		public boolean matches(EveAsset item) {
			return eveAssetMatching.matches(item, column, mode, text, columnMatch);
		}
	}
}
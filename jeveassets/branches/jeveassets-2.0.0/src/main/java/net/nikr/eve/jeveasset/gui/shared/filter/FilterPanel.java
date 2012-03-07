/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.matchers.Matcher;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;


class FilterPanel<E> implements ActionListener, KeyListener, DocumentListener, PropertyChangeListener{
	private final static String ACTION_FILTER = "ACTION_FILTER";
	private final static String ACTION_REMOVE = "ACTION_REMOVE";
	
	private JPanel jPanel;
	private GroupLayout layout;
	
	private JCheckBox jEnabled;
	private JComboBox jLogic;
	private JComboBox jColumn;
	private JComboBox jCompare;
	private JTextField jText;
	private JComboBox jCompareColumn;
	private JDateChooser jDate;
	
	private JLabel jSpacing;
	private JButton jRemove;
	
	
	private Timer timer;
	
	private FilterGui<E> gui;
	private FilterControl<E> matcherControl;
	private final List<Object> numericColumns;
	private final List<Object> dateColumns;

	FilterPanel(FilterGui<E> gui, FilterControl<E> matcherControl) {
		this.gui = gui;
		this.matcherControl = matcherControl;

		jEnabled = new JCheckBox();
		jEnabled.setSelected(true);
		jEnabled.addActionListener(this);
		jEnabled.setActionCommand(ACTION_FILTER);
		
		jLogic = new JComboBox(LogicType.values());
		jLogic.addActionListener(this);
		jLogic.setActionCommand(ACTION_FILTER);
		
		jColumn = new JComboBox(matcherControl.getColumns());
		jColumn.addActionListener(this);
		jColumn.setActionCommand(ACTION_FILTER);
		
		numericColumns = new ArrayList<Object>();
		for (Enum object : matcherControl.getColumns()){
			if (matcherControl.isNumeric(object)){
				numericColumns.add(object);
			}
		}
		dateColumns = new ArrayList<Object>();
		for (Enum object : matcherControl.getColumns()){
			if (matcherControl.isDate(object)){
				dateColumns.add(object);
			}
		}
		
		jCompare = new JComboBox();
		jCompare.addActionListener(this);
		jCompare.setActionCommand(ACTION_FILTER);
		
		jText = new JTextField();
		jText.getDocument().addDocumentListener(this);
		jText.addKeyListener(this);
		
		jCompareColumn = new JComboBox();
		jCompareColumn.addActionListener(this);
		jCompareColumn.setActionCommand(ACTION_FILTER);

		jDate = new JDateChooser(Settings.getGmtNow());
		jDate.setDateFormatString(Formater.COLUMN_FORMAT);
		JCalendar jCalendar = jDate.getJCalendar();
		jCalendar.setTodayButtonText("Today");
		jCalendar.setTodayButtonVisible(true);
		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor)jDate.getDateEditor().getUiComponent();
		dateEditor.setEnabled(false);
		dateEditor.setBorder(null);
		dateEditor.setDisabledTextColor(Color.BLACK);
		jDate.addPropertyChangeListener(this);
		
		jSpacing = new JLabel();
		
		jRemove = new JButton();
		jRemove.setIcon(Images.EDIT_DELETE.getIcon());
		jRemove.addActionListener(this);
		jRemove.setActionCommand(ACTION_REMOVE);
		
		timer = new Timer(500, this);
		timer.setActionCommand(ACTION_FILTER);
		
		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnabled, 30, 30, 30)
				.addComponent(jLogic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jCompare, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jText, 100, 100, Integer.MAX_VALUE)
				.addComponent(jCompareColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jSpacing, 0, 0, Integer.MAX_VALUE)
				.addComponent(jRemove, 30, 30, 30)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jEnabled, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jLogic, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jColumn, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCompare, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCompareColumn, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jDate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSpacing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		updateNumeric(false);
	}
	
	void setEnabled(boolean b){
		jRemove.setEnabled(b);
	}
	
	JPanel getPanel(){
		return jPanel;
	}
	
	MyMatcher<E> getMatcher(){
		boolean enabled = jEnabled.isSelected();
		LogicType logic = (LogicType) jLogic.getSelectedItem();
		Enum column = (Enum)jColumn.getSelectedItem();
		CompareType compare = (CompareType)jCompare.getSelectedItem();
		String text;
		if (isColumnCompare()){
			Enum compareColumn = (Enum)jCompareColumn.getSelectedItem();
			text = compareColumn.name();
		} else if (isDateCompare()) {
			text = getDataString();
		} else {
			text = jText.getText();
		}
		return new MyMatcher<E>(matcherControl, enabled, logic, column, compare, text);
	}
	
	Filter getFilter(){
		LogicType logic = (LogicType) jLogic.getSelectedItem();
		Enum column = (Enum)jColumn.getSelectedItem();
		CompareType compare = (CompareType)jCompare.getSelectedItem();
		String text;
		if (isColumnCompare()){
			Enum compareColumn = (Enum)jCompareColumn.getSelectedItem();
			text = compareColumn.name();
		} else if (isDateCompare()) {
			text = getDataString();
		} else {
			text = jText.getText();
		}
		return new Filter(logic == LogicType.AND, column, compare, text);
	}
	
	void setFilter(Filter filter){
		jEnabled.setEnabled(true);
		jLogic.setSelectedItem(filter.isAnd() ? LogicType.AND : LogicType.OR);
		jColumn.setSelectedItem(filter.getColumn());
		jCompare.setSelectedItem(CompareType.valueOf(filter.getCompare()));
		if (isColumnCompare()){
			jCompareColumn.setSelectedItem(matcherControl.valueOf(filter.getText()));
		} else if (isDateCompare()) {
			jDate.setDate(Formater.columnStringToDate(filter.getText()));
		} else {
			jText.setText(filter.getText());
		}
	}
	
	private String getDataString(){
		return  Formater.columnDate(jDate.getDate());
	}
	
	private void refilter() {
		gui.refilter();
	}
	
	private boolean isColumnCompare(){
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isColumnCompare(compareType);
	}
	
	private boolean isNumericCompare(){
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isNumericCompare(compareType);
	}
	
	private boolean isDateCompare(){
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isDateCompare(compareType);
	}
	
	
	private void updateNumeric(boolean saveIndex){
		Object object = jCompare.getSelectedItem();
		CompareType[] compareTypes;
		if (matcherControl.isNumeric((Enum)jColumn.getSelectedItem())){
			compareTypes = CompareType.valuesNumeric();
		} else if (matcherControl.isDate((Enum)jColumn.getSelectedItem())){
			compareTypes = CompareType.valuesDate();
		} else {
			compareTypes = CompareType.valuesString();
		}
		jCompare.setModel( new DefaultComboBoxModel(compareTypes));
		for (CompareType compareType : compareTypes){
			if (compareType.equals(object) && saveIndex) jCompare.setSelectedItem(compareType);
		}
		updateCompare(saveIndex);
	}

	private void updateCompare(boolean saveIndex){
		if (isColumnCompare()){ //Column
			jText.setVisible(false);
			jCompareColumn.setVisible(true);
			jDate.setVisible(false);
			jSpacing.setVisible(true);
		} else if (isDateCompare()) { //Date
			jText.setVisible(false);
			jCompareColumn.setVisible(false);
			jDate.setVisible(true);
			jSpacing.setVisible(true);
		} else { //String
			jText.setVisible(true);
			jCompareColumn.setVisible(false);
			jDate.setVisible(false);
			jSpacing.setVisible(false);
		}
		Object object = jCompareColumn.getSelectedItem();
		Object[] compareColumns;
		if (isNumericCompare()){
			compareColumns = numericColumns.toArray();
		} else if (isDateCompare()){
			compareColumns = dateColumns.toArray();
		} else {
			compareColumns = matcherControl.getColumns();
		}
		jCompareColumn.setModel( new DefaultComboBoxModel(compareColumns));
		for (Object column : compareColumns){
			if (column.equals(object) && saveIndex) jCompareColumn.setSelectedItem(column);
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		timer.stop();
		timer.start();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		timer.stop();
		timer.start();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		timer.stop();
		timer.start();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_REMOVE.equals(e.getActionCommand())){
			gui.remove(this);
		}
		if (ACTION_FILTER.equals(e.getActionCommand())){
			if (jColumn.equals(e.getSource())) updateNumeric(true);
			if (jCompare.equals(e.getSource())) updateCompare(true);
			if (jEnabled.isSelected()){
				jText.setBackground(Color.WHITE);
			} else {
				jText.setBackground(new Color(255, 200, 200));
			}
			timer.stop();
			refilter();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("date".equals(evt.getPropertyName())) {
			refilter();
		}
	}
	
	public static class MyMatcher<E> implements Matcher<E>{

		private FilterControl<E> matcherControl;
		private boolean enabled;
		private LogicType logic;
		private Enum column;
		private CompareType compare;
		private String text;

		public MyMatcher(FilterControl<E> matcherControl, boolean enabled, LogicType logic, Enum column, CompareType compare, String text) {
			this.matcherControl = matcherControl;
			this.logic = logic;
			this.enabled = enabled;
			this.column = column;
			this.compare = compare;
			this.text = text;
		}
		
		boolean isAnd(){
			return logic == LogicType.AND;
		}
		
		public boolean isEmpty(){
			return text.isEmpty() || !enabled;
		}
		
		@Override
		public boolean matches(E item) {
			return matcherControl.matches(item, column, compare, text);
		}
		
	}
}

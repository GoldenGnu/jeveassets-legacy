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

package net.nikr.eve.jeveasset.gui.dialogs.export;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import javax.swing.event.ListSelectionEvent;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.JCustomFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.CsvSettings;
import net.nikr.eve.jeveasset.data.CsvSettings.DecimalSeperator;
import net.nikr.eve.jeveasset.data.CsvSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.CsvSettings.LineDelimiter;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JMultiSelectionList;
import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import org.supercsv.prefs.CsvPreference;


public class CsvExportDialog extends JDialogCentered implements ActionListener, ListSelectionListener{

	public static final String ACTION_DISABLE_SAVED_FILTERS = "ACTION_DISABLE_SAVED_FILTERS";
	public static final String ACTION_ENABLE_SAVED_FILTERS = "ACTION_ENABLE_SAVED_FILTERS";
	public static final String ACTION_OK = "ACTION_OK";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	public static final String ACTION_DEFAULT = "ACTION_DEFAULT";
	public static final String ACTION_BROWSE = "ACTION_BROWSE";

	private JTextField jPath;
	private JButton jBrowse;
	private JRadioButton jAllAssets;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	private JComboBox jFieldDelimiter;
	private JComboBox jLineDelimiter;
	private JComboBox jDecimalSeparator;
	private JMultiSelectionList jColumnSelection;
	private JButton jOK;

	private static DecimalFormat doubleEn  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat doubleEu  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("da")));
	private static DecimalFormat longEn  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat longEu  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("da")));

	private JCustomFileChooser jCsvFileChooser;
	
	private boolean loading = false;

	public CsvExportDialog(Program program) {
		super(program, DialoguesCsvExport.get().csvExport(), Images.DIALOG_CSV_EXPORT.getImage());

		try {
			jCsvFileChooser = new JCustomFileChooser(program, "csv");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jCsvFileChooser = new JCustomFileChooser(program, "csv");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jCsvFileChooser = new JCustomFileChooser(program, "csv");
			}
		}

		jPath = new JTextField();
		JCopyPopup.install(jPath);
		jPanel.add(jPath);

		jBrowse = new JButton(DialoguesCsvExport.get().browse());
		jBrowse.setActionCommand(ACTION_BROWSE);
		jBrowse.addActionListener(this);
		jPanel.add(jBrowse);
		
		JSeparator jSeparator1 = new JSeparator();

		JLabel jAssetsLabel = new JLabel(DialoguesCsvExport.get().assets());
		jAllAssets = new JRadioButton(DialoguesCsvExport.get().allAssets());
		jAllAssets.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jAllAssets.addActionListener(this);
		jPanel.add(jAllAssets);

		jCurrentFilter = new JRadioButton(DialoguesCsvExport.get().currentFilter());
		jCurrentFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jCurrentFilter.addActionListener(this);
		jPanel.add(jCurrentFilter);

		jSavedFilter = new JRadioButton(DialoguesCsvExport.get().savedFilter());
		jSavedFilter.setActionCommand(ACTION_ENABLE_SAVED_FILTERS);
		jSavedFilter.addActionListener(this);
		jPanel.add(jSavedFilter);

		ButtonGroup jButtonGroup = new ButtonGroup();
		jButtonGroup.add(jAllAssets);
		jButtonGroup.add(jSavedFilter);
		jButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox();
		jPanel.add(jFilters);
		
		JLabel jFieldDelimiterLabel = new JLabel(DialoguesCsvExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox( FieldDelimiter.values() ); //new String[]{"Comma", "Semicolon"} );
		jFieldDelimiter.addActionListener(this);

		JLabel jLineDelimiterLabel = new JLabel(DialoguesCsvExport.get().linesTerminated());
		jLineDelimiter = new JComboBox( LineDelimiter.values() ); //new String[]{"\\n", "\\r\\n", "\\r"});
		jLineDelimiter.addActionListener(this);

		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesCsvExport.get().decimalSeperator());
		jDecimalSeparator = new JComboBox( DecimalSeperator.values() ); // new String[]{"Dot", "Comma"});
		jDecimalSeparator.addActionListener(this);

		JLabel jColumnSelectionLabel = new JLabel(DialoguesCsvExport.get().columns());
		jColumnSelection = new JMultiSelectionList( new Vector<String>(program.getSettings().getAssetTableSettings().getTableColumnNames()) );
		jColumnSelection.addListSelectionListener(this);
		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);
		jPanel.add(jColumnSelectionPanel);
		
		JSeparator jSeparator = new JSeparator();

		jOK = new JButton(DialoguesCsvExport.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);
		jPanel.add(jOK);

		JButton jDefault = new JButton(DialoguesCsvExport.get().defaultSettings());
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(this);
		jPanel.add(jDefault);
		
		JButton jCancel = new JButton(DialoguesCsvExport.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		jPanel.add(jCancel);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jPath, 390, 390, 390)
					.addComponent(jBrowse, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10)
				)
				.addComponent(jSeparator1)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jAllAssets)
						.addComponent(jCurrentFilter)
						.addComponent(jSavedFilter)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(layout.createSequentialGroup()
								.addGap(20)
								.addComponent(jFilters, 150, 150, 150)
							)
							
						)
					)
					.addGap(30)
					.addGroup(layout.createParallelGroup()
						.addComponent(jFieldDelimiterLabel)
						.addComponent(jFieldDelimiter)
						.addComponent(jLineDelimiterLabel)
						.addComponent(jLineDelimiter)
						.addComponent(jDecimalSeparatorLabel)
						.addComponent(jDecimalSeparator)

					)
					.addGap(30)
					.addGroup(layout.createParallelGroup()
						.addComponent(jColumnSelectionLabel)
						.addComponent(jColumnSelectionPanel, 165, 165, 165)
					)
					
				)
				.addComponent(jSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jPath, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBrowse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jAllAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jCurrentFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jSavedFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						
						
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jColumnSelectionLabel)
						.addComponent(jColumnSelectionPanel, 120, 120, 120)
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jFieldDelimiterLabel)
						.addComponent(jFieldDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jLineDelimiterLabel)
						.addComponent(jLineDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jDecimalSeparatorLabel)
						.addComponent(jDecimalSeparator, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)


				)
				.addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void browse(){
		String current = jPath.getText();
		int end = current.lastIndexOf(File.separator);
		if (end > 0) current = current.substring(0, end+1);
		File currentFile = new File( current );

		File defaulFile = new File(program.getSettings().getCsvSettings().getPath());
		if (currentFile.exists()){
			jCsvFileChooser.setCurrentDirectory( new File(current) );
			jCsvFileChooser.setSelectedFile( new File(jPath.getText()));
		} else {
			jCsvFileChooser.setCurrentDirectory( defaulFile );
			jCsvFileChooser.setSelectedFile( new File(program.getSettings().getCsvSettings().getFile()));
		}
		int bFound = jCsvFileChooser.showDialog(getDialog(), "OK"); //.showSaveDialog(); //; //.showOpenDialog(this);
		if (bFound  == JFileChooser.APPROVE_OPTION){
			File file = jCsvFileChooser.getSelectedFile();
			jPath.setText( file.getAbsolutePath() );
		}
	}

	private HashMap<String, ? super Object> getLine(String[] header, Asset eveAsset, String lang){
		HashMap<String, ? super Object> line = new HashMap<String, Object>();
		for (int a = 0; a < header.length; a++){
			String headerName = header[a];
			if (headerName.equals(DialoguesCsvExport.get().headerNameName())) line.put(headerName, eveAsset.getName());
			if (headerName.equals(DialoguesCsvExport.get().headerNameGroup())) line.put(headerName, eveAsset.getGroup());
			if (headerName.equals(DialoguesCsvExport.get().headerNameCategory())) line.put(headerName, eveAsset.getCategory());
			if (headerName.equals(DialoguesCsvExport.get().headerNameOwner())) line.put(headerName, eveAsset.getOwner());
			if (headerName.equals(DialoguesCsvExport.get().headerNameCount())) line.put(headerName, getValue(eveAsset.getCount(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameLocation())) line.put(headerName, eveAsset.getLocation());
			if (headerName.equals(DialoguesCsvExport.get().headerNameContainer())) line.put(headerName, eveAsset.getContainer());
			if (headerName.equals(DialoguesCsvExport.get().headerNameFlag())) line.put(headerName, eveAsset.getFlag());
			if (headerName.equals(DialoguesCsvExport.get().headerNamePrice())) line.put(headerName, getValue(eveAsset.getPrice(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameSellMin())) line.put(headerName, getValue(eveAsset.getPriceSellMin(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameBuyMax())) line.put(headerName, getValue(eveAsset.getPriceBuyMax(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameValue())) line.put(headerName, getValue(eveAsset.getValue(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameMeta())) line.put(headerName, eveAsset.getMeta());
			if (headerName.equals(DialoguesCsvExport.get().headerNameItemID())) line.put(headerName, getValue(eveAsset.getItemID(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameBasePrice())) line.put(headerName, getValue(eveAsset.getPriceBase(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameVolume())) line.put(headerName, getValue(eveAsset.getVolume(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameTypeID())) line.put(headerName, getValue(eveAsset.getTypeID(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameRegion())) line.put(headerName, eveAsset.getRegion());
			if (headerName.equals(DialoguesCsvExport.get().headerNameTypeCount())) line.put(headerName, getValue(eveAsset.getTypeCount(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameSecurity())) line.put(headerName, eveAsset.getSecurity());
			if (headerName.equals(DialoguesCsvExport.get().headerNameReprocessed())) line.put(headerName, getValue(eveAsset.getPriceReprocessed(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerNameReprocessedValue())) line.put(headerName, getValue(eveAsset.getValueReprocessed(), lang));
			if (headerName.equals(DialoguesCsvExport.get().headerSingleton())) line.put(headerName, eveAsset.getSingleton());
			if (headerName.equals(DialoguesCsvExport.get().headerVolumeTotal())) line.put(headerName, getValue(eveAsset.getVolumeTotal(), lang));
		}
		return line;
	}

	private String getValue(double number, String lang){
		if (lang.equals("Dot")){
			return doubleEn.format(number);
		}
		return doubleEu.format(number);
	}
	private String getValue(long number, String lang){
		if (lang.equals("Dot")){
			return longEn.format(number);
		}
		return longEu.format(number);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jPath;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		List<HashMap<String, ? super Object>> data = new ArrayList<HashMap<String, ? super Object>>();

		Object[] columns = jColumnSelection.getSelectedValues();

		if (columns.length == 0){
			JOptionPane.showMessageDialog(getDialog(), DialoguesCsvExport.get().selectOne(), DialoguesCsvExport.get().csvExport(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		String[] header = new String[columns.length];
		for (int a = 0; a < columns.length; a++){
			header[a] = (String) columns[a];
		}

		char fieldDelimiter = ((FieldDelimiter)jFieldDelimiter.getSelectedItem()).getCharacter();

		String lineDelimiter = ((LineDelimiter)jLineDelimiter.getSelectedItem()).getString();

		String lang = ((DecimalSeperator)jDecimalSeparator.getSelectedItem()).getString();

		if (lang.equals("Comma") && fieldDelimiter == ','){
			int nReturn = JOptionPane.showConfirmDialog(
					program.getMainWindow().getFrame(),
					DialoguesCsvExport.get().confirmStupidDecision(),
					DialoguesCsvExport.get().csvExport(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION){
				return;
			}
		}

		if (jAllAssets.isSelected()){
			EventList<Asset> assets = program.getEveAssetEventList();
			for (int a = 0; a < assets.size(); a++){
				Asset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}

		}
		if (jCurrentFilter.isSelected()){
			List<AssetFilter> assetFilters = program.getAssetsTab().getAssetFilters();
			FilterList<Asset> assets = new FilterList<Asset>(program.getEveAssetEventList(), new AssetFilterLogicalMatcher(assetFilters));
			for (int a = 0; a < assets.size(); a++){
				Asset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}
		}
		if (jSavedFilter.isSelected()){
			String s = (String) jFilters.getSelectedItem();
			List<AssetFilter> assetFilters = program.getSettings().getAssetFilters().get(s);
			FilterList<Asset> assets = new FilterList<Asset>(program.getEveAssetEventList(), new AssetFilterLogicalMatcher(assetFilters));
			for (int a = 0; a < assets.size(); a++){
				Asset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}
		}
		if (!CsvWriter.save(jPath.getText(), data, header, new CsvPreference('\"', fieldDelimiter, lineDelimiter))){
			JOptionPane.showMessageDialog(getDialog(),
					DialoguesCsvExport.get().failedToSave(),
					DialoguesCsvExport.get().csvExport(),
					JOptionPane.PLAIN_MESSAGE);
		}

		this.setVisible(false);
	}
	
	public void load(){
		loading = true;
		jLineDelimiter.setSelectedItem(program.getSettings().getCsvSettings().getLineDelimiter());

		jFieldDelimiter.setSelectedItem(program.getSettings().getCsvSettings().getFieldDelimiter());

		jDecimalSeparator.setSelectedItem(program.getSettings().getCsvSettings().getDecimalSeperator());


		if (program.getSettings().getAssetTableSettings().getTableColumnNames().size() == program.getSettings().getCsvSettings().getMaxColumns()){
			//Select saved columns
			jColumnSelection.clearSelection();
			for (String column : program.getSettings().getCsvSettings().getColumns()){
				int index = program.getSettings().getAssetTableSettings().getTableColumnNames().indexOf(column);
				jColumnSelection.addSelection(index, true);
			}
		} else {
			//Columns have changed -  select everything
			jColumnSelection.selectAll();
			program.getSettings().getCsvSettings().setColumns(program.getSettings().getAssetTableSettings().getTableColumnNames());
		}
		jColumnSelection.ensureIndexIsVisible(0);
		program.getSettings().getCsvSettings().setMaxColumns(program.getSettings().getAssetTableSettings().getTableColumnNames().size());

		jPath.setText(program.getSettings().getCsvSettings().getFile());
		loading = false;
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			Vector<String> assetFilters = new Vector<String>(program.getSettings().getAssetFilters().keySet());
			Collections.sort(assetFilters);
			jFilters.setModel( new DefaultComboBoxModel(assetFilters) );
			jFilters.setEnabled(false);

			if(assetFilters.isEmpty()){
				jSavedFilter.setEnabled(false);
			} else {
				jSavedFilter.setEnabled(true);
			}
			jAllAssets.setSelected(true);
			load();
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!loading){
			program.getSettings().getCsvSettings().setPath(new File(jPath.getText()).getParent());
			program.getSettings().getCsvSettings().setDecimalSeperator((DecimalSeperator)jDecimalSeparator.getSelectedItem());
			program.getSettings().getCsvSettings().setFieldDelimiter((FieldDelimiter)jFieldDelimiter.getSelectedItem());
			program.getSettings().getCsvSettings().setLineDelimiter((LineDelimiter)jLineDelimiter.getSelectedItem());
		}
		
		if (ACTION_DISABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(false);
		}
		if (ACTION_ENABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(true);
		}
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			program.getSettings().setCsvSettings(new CsvSettings());
			load();
		}
		if (ACTION_BROWSE.equals(e.getActionCommand())){
			browse();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!loading){
			Object[] columns = jColumnSelection.getSelectedValues();
			List<String> list = new ArrayList<String>(columns.length);
			for (Object o : columns){
				list.add((String)o);
			}
			program.getSettings().getCsvSettings().setColumns(list);
		}
	}
	
}

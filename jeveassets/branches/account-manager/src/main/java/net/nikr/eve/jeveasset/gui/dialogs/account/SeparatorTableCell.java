/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.SeparatorList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;

/**
 *
 * @author Niklas
 */
public class SeparatorTableCell extends AbstractCellEditor 
		implements TableCellRenderer, TableCellEditor, CaretListener, FocusListener, ActionListener{

	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	public final static String ACTION_EDIT = "ACTION_EDIT";
	public final static String ACTION_DELETE = "ACTION_DELETE";

	private static final Icon EXPANDED_ICON =  ImageGetter.getIcon("expanded.png"); //Icons.triangle(9, SwingConstants.EAST, Color.WHITE);
	private static final Icon COLLAPSED_ICON = ImageGetter.getIcon("collapsed.png");//  Icons.triangle(9, SwingConstants.SOUTH, Color.WHITE);
	private static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/** the separator list to lock */
	private final SeparatorList<Human> separatorList;
	private int row;

	private final JPanel jPanel;
	private final JLabel jAccount;
	private final JTextField jDescription;
	private final JButton jExpand;
	private final JButton jEdit;
	private final JButton jDelete;
	private final GroupLayout layout;
	private final JTable jTable;

	private SeparatorList.Separator<?> separator;

	public SeparatorTableCell(ActionListener actionListener, JTable jTable, SeparatorList<Human> separatorList) {
		this.jTable = jTable;
		this.separatorList = separatorList;

		jPanel = new JPanel(new BorderLayout());
		jPanel.setBackground(Color.LIGHT_GRAY);

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		jExpand = new JButton(EXPANDED_ICON);
		jExpand.setOpaque(false);
		jExpand.setContentAreaFilled(false);
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpand.setIcon(EXPANDED_ICON);
		jExpand.addActionListener(this);

		Font font = jPanel.getFont();
		
		jAccount = new JLabel();
		jAccount.setText("Account: ");

		jDescription = new JTextField();
		jDescription.addActionListener(this);
		jDescription.addFocusListener(this);
		jDescription.setBorder(null);
		jDescription.setOpaque(false);

		jEdit = new JButton("Edit");
		jEdit.setOpaque(false);
		jEdit.setActionCommand(ACTION_EDIT);
		jEdit.addActionListener(actionListener);

		jDelete = new JButton("Delete");
		jDelete.setOpaque(false);
		jDelete.setActionCommand(ACTION_DELETE);
		jDelete.addActionListener(actionListener);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(1)
				.addComponent(jAccount)
				.addGap(1)
				.addComponent(jDescription)
				.addGap(1)
				.addComponent(jEdit)
				.addComponent(jDelete)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jExpand, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
				.addComponent(jDescription, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
				.addComponent(jAccount, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
				.addComponent(jEdit, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
				.addComponent(jDelete, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
		);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		configure(value, row);
		return jPanel;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		configure(value, row);
		return jPanel;
	}

	@Override
	public Object getCellEditorValue() {
		return this.separator;
	}

	private void configure(Object value, int row) {
		this.row = row;
		if (value instanceof SeparatorList.Separator<?>){
			this.separator = (SeparatorList.Separator<?>)value;
			Human human = (Human) separator.first();
			if(human == null) return; // handle 'late' rendering calls after this separator is invalid
			Account account = human.getParentAccount();
			//jApiKey.setText(account.getApiKey());
			jExpand.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
			if (account.getName().isEmpty()){
				jDescription.setText(String.valueOf(account.getUserID()));
			} else {
				jDescription.setText(account.getName());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_EXPAND.equals(e.getActionCommand())){
			separatorList.getReadWriteLock().writeLock().lock();
			boolean collapsed;
			try {
				collapsed = separator.getLimit() == 0;
				separator.setLimit(collapsed ? Integer.MAX_VALUE : 0);
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
			jExpand.setIcon(collapsed ? COLLAPSED_ICON : EXPANDED_ICON);
		}
		if (e.getSource() instanceof JTextField){
			Human human = (Human) separator.first();
			Account account = human.getParentAccount();
			if (jDescription.getText().isEmpty()){
				jDescription.setText(String.valueOf(account.getUserID()));
			}
			account.setName(jDescription.getText());
			jDescription.transferFocus();
			if (separator.size() > 0){
				int index = jTable.getSelectedRow();
				jTable.setRowSelectionInterval(index+1, index+1);
			}
			
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		Human human = (Human) separator.first();
		Account account = human.getParentAccount();
		account.setName(jDescription.getText());
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField){
			jTable.setRowSelectionInterval(row, row);
			jDescription.selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {

	}
}
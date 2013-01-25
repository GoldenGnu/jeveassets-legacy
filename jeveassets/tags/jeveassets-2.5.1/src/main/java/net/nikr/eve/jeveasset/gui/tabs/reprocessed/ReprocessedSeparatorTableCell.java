/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class ReprocessedSeparatorTableCell extends SeparatorTableCell<ReprocessedInterface> {

	public static final String ACTION_REMOVE = "ACTION_REMOVE";

	private final JLabel jColor;
	private final JButton jRemove;
	private final JLabel jName;
	private final JLabel jPrice;
	private final JLabel jBatchSizeLabel;
	private final JLabel jBatchSize;
	private final JLabel jValueLabel;
	private final JLabel jValue;

	public ReprocessedSeparatorTableCell(final JTable jTable, final SeparatorList<ReprocessedInterface> separatorList, final ActionListener actionListener) {
		super(jTable, separatorList);

		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		jRemove = new JButton(TabsReprocessed.get().remove());
		jRemove.setOpaque(false);
		jRemove.setActionCommand(ACTION_REMOVE);
		jRemove.addActionListener(actionListener);

		jName = new JLabel();
		Font font = jName.getFont();
		jName.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));

		JLabel jSellPriceLabel = new JLabel(TabsReprocessed.get().price());
		jSellPriceLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jPrice = new JLabel();

		jBatchSizeLabel = new JLabel(TabsReprocessed.get().batch());
		jBatchSizeLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jBatchSize = new JLabel();

		jValueLabel = new JLabel(TabsReprocessed.get().value());
		jValueLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jValue = new JLabel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(5)
				.addComponent(jColor, Program.BUTTONS_HEIGHT - 6, Program.BUTTONS_HEIGHT - 6, Program.BUTTONS_HEIGHT - 6)
				.addGap(10)
				.addComponent(jRemove, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addGap(10)
				//.addComponent(jNameLabel)
				//.addGap(5)
				.addComponent(jName, 220, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(10)
				.addComponent(jSellPriceLabel)
				.addGap(5)
				.addComponent(jPrice)
				.addGap(10)
				.addComponent(jValueLabel)
				.addGap(5)
				.addComponent(jValue)
				.addGap(10)
				.addComponent(jBatchSizeLabel)
				.addGap(5)
				.addComponent(jBatchSize)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addComponent(jColor, Program.BUTTONS_HEIGHT - 6, Program.BUTTONS_HEIGHT - 6, Program.BUTTONS_HEIGHT - 6)
					)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					//.addComponent(jNameLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellPriceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPrice, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jValueLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jValue, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBatchSizeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBatchSize, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(Separator<?> separator) {
		ReprocessedInterface material = (ReprocessedInterface) separator.first();
		if (material == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jName.setText(material.getTotal().getTypeName());
		//Price
		jPrice.setText(Formater.iskFormat(material.getTotal().getSellPrice()));
		//Value
		if (material.getTotal().getValue() != material.getTotal().getSellPrice()) {
			jValue.setText(Formater.iskFormat(material.getTotal().getValue()));
			jValueLabel.setVisible(true);
			jValue.setVisible(true);
		} else {
			jValueLabel.setVisible(false);
			jValue.setVisible(false);
		}
		//Portion Size
		if (material.getPortionSize() > 1) {
			jBatchSize.setText(Formater.longFormat(material.getPortionSize()));
			jBatchSizeLabel.setVisible(true);
			jBatchSize.setVisible(true);
		} else {
			jBatchSizeLabel.setVisible(false);
			jBatchSize.setVisible(false);
		}
		//Color
		if (material.getTotal().isSell()) {
			jColor.setBackground(new Color(200, 255, 200));
		} else if (material.getTotal().isReprocess()){
			jColor.setBackground(new Color(255, 200, 200));
		} else {
			jColor.setBackground(Color.LIGHT_GRAY);
		}
	}
}

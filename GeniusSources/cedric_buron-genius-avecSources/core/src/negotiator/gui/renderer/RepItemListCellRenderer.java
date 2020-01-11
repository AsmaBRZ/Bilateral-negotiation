package negotiator.gui.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import negotiator.repository.ParticipantRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.boa.BoaRepItem;

/**
 * Renders RepItems, using {@link PartyRepItem#getName()} if possible, or
 * toString otherwise.
 */

@SuppressWarnings("serial")
public class RepItemListCellRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		comp.setText(getText((RepItem) value));
		return comp;
	}

	/**
	 * 
	 * HACK static to at least be able to re-use this code in
	 * {@link RepItemTableCellRenderer}.
	 * 
	 * @return label to show for a given RepItem.
	 */
	protected static String getText(RepItem value) {
		if (value == null) {
			return "NULL";
		}
		if (value instanceof ParticipantRepItem) {
			return ((ParticipantRepItem) value).getName();
		}
		if (value instanceof BoaRepItem) {
			return ((BoaRepItem) value).getName();
		}
		return value.toString();
	}
}

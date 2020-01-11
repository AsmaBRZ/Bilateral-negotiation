package panels;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A GUI that shows panel with given title at the left and a combobox at the
 * right. In the combo, the user can select an item from a list. This panel
 * hides automatically if there are no selectable items.
 *
 * @param <ItemType>
 *            the type of the elements in the item list.
 */
@SuppressWarnings("serial")
public class ComboboxSelectionPanel<ItemType> extends JPanel {

	private SingleSelectionModel<ItemType> model;
	final JComboBox<ItemType> combo;

	/**
	 * 
	 * @param title
	 *            the text to be placed left of the combo box
	 * @param itemsModel
	 *            the {@link SingleSelectionModel} that contains the possible
	 *            choices and can be listened for changes.
	 */
	public ComboboxSelectionPanel(final String title, final SingleSelectionModel<ItemType> itemsModel) {
		this.model = itemsModel;
		setLayout(new BorderLayout());
		combo = new JComboBox<ItemType>(itemsModel);

		add(new JLabel(title), BorderLayout.WEST);
		add(combo, BorderLayout.CENTER);
		setAlignmentX(Component.RIGHT_ALIGNMENT);
		updateVisibility();

		itemsModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateVisibility();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				updateVisibility();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				updateVisibility();
			}

		});
	}

	/**
	 * We are visible only if there is at least one possible selection. This
	 * enables us to hide eg the Mediator combobox by just emptying the mediator
	 * list in the model.
	 */
	private void updateVisibility() {
		// FIXME invokelater
		setVisible(model.getSize() > 0);
	}

	/**
	 * Set the cell renderer.
	 * 
	 * @param renderer
	 *            a {@link DefaultListCellRenderer}. Note, for unknown reason,
	 *            {@link DefaultListCellRenderer} implements
	 *            {@link ListCellRenderer}&lt;object&gt; and ignores the proper
	 *            typing
	 */
	public void setCellRenderer(DefaultListCellRenderer renderer) {
		combo.setRenderer(renderer);
	}

}

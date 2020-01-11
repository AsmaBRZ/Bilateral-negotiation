package negotiator.gui.session;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import negotiator.gui.renderer.RepItemListCellRenderer;
import negotiator.repository.PartyRepItem;
import panels.ComboboxSelectionPanel;
import panels.LabelAndComponent;
import panels.SingleSelectionModel;
import panels.TextModel;
import panels.TextPanel;
import panels.VflowPanelWithBorder;

/**
 * The mediator selector panel for single session. Visible only when the
 * strategy list is non-empty.
 */
@SuppressWarnings("serial")
public class MediatorPanel extends VflowPanelWithBorder {

	private SingleSelectionModel<PartyRepItem> partyModel;

	public MediatorPanel(TextModel nameModel, SingleSelectionModel<PartyRepItem> partyModel) {
		super("Mediator");
		this.partyModel = partyModel;
		final ComboboxSelectionPanel<PartyRepItem> mediatorcomb = new ComboboxSelectionPanel<>("Mediator Strategy",
				partyModel);

		mediatorcomb.setCellRenderer(new RepItemListCellRenderer());

		add(new LabelAndComponent("Mediator ID", new TextPanel(nameModel)));
		add(mediatorcomb);
		updateVisibility();

		connect();
	}

	private void connect() {
		partyModel.addListDataListener(new ListDataListener() {

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

	private void updateVisibility() {
		setVisible(!partyModel.getAllItems().isEmpty());
	}

}

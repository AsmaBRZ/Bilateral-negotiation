package negotiator.gui.session;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import negotiator.gui.deadline.DeadlinePanel;
import negotiator.gui.renderer.RepItemListCellRenderer;
import negotiator.repository.MultiPartyProtocolRepItem;
import panels.CheckboxPanel;
import panels.ComboboxSelectionPanel;
import panels.VflowPanelWithBorder;

/**
 * Panel that allows the user to set up the (multilateral) session settings
 */
@SuppressWarnings("serial")
public class SessionConfigPanel extends VflowPanelWithBorder {

	private SessionModel model;
	private BilateralOptionsPanel bilateralOptions;

	public SessionConfigPanel(SessionModel model) {
		super("Multiparty Negotiation Session Setup");
		this.model = model;

		initPanel();
	}

	/**
	 * Load and set all the panel elements - buttons, comboboxes, etc.
	 */
	private void initPanel() {
		ComboboxSelectionPanel<MultiPartyProtocolRepItem> protocolcomb = new ComboboxSelectionPanel<>("Protocol",
				model.getProtocolModel());
		protocolcomb.setCellRenderer(new RepItemListCellRenderer());

		MediatorPanel mediatorpanel = new MediatorPanel(model.getMediatorIdModel(), model.getMediatorModel());

		add(protocolcomb);
		add(mediatorpanel);

		add(new ParticipantsPanel(model.getParticipantModel(), model.getParticipantsModel()));
		add(new DeadlinePanel(model.getDeadlineModel()));
		add(new ComboboxSelectionPanel<>("Data persistency", model.getPersistentDatatypeModel()));
		add(new CheckboxPanel("Enable System.out print", model.getPrintEnabled()));
		add(new CheckboxPanel("Enable progress graph", model.getShowChart()));

		bilateralOptions = new BilateralOptionsPanel(model.getBilateralUtilUtilPlot(), model.getBilateralShowAllBids());
		add(bilateralOptions);

		JButton start = new JButton("Start");
		add(start);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.modelIsComplete();
			}
		});

		model.getParticipantsModel().addListDataListener(new ListDataListener() {

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
		updateVisibility();
	}

	private void updateVisibility() {
		bilateralOptions.setVisible(model.getParticipantsModel().getSize() == 2);
	}

}

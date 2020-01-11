package negotiator.gui.tournament;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import listener.Listener;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.gui.deadline.DeadlinePanel;
import negotiator.gui.renderer.RepItemListCellRenderer;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import panels.CheckboxPanel;
import panels.ComboboxSelectionPanel;
import panels.SpinnerPanel;
import panels.VflowPanelWithBorder;

/**
 * This is the user interface for the multilateral tournament and replaces the
 * old MultiTournamentUI.
 * <p/>
 * The configuration of this user interface is stored in the
 * {@link MultilateralTournamentConfiguration} variable, which is also used by
 * the tournament manager to run the tournaments.
 *
 * @author W.Pasman
 *
 */
@SuppressWarnings("serial")
public class MultiTournamentSettingsPanel extends VflowPanelWithBorder {

	private MultiTournamentModel model;
	private JButton start = new JButton("Start Tournament");
	private BilateralOptionsPanel biOptionsPanel;

	public MultiTournamentSettingsPanel(MultiTournamentModel model) {
		super("Multilateral negotiation Tournament Setup");
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
		ComboboxSelectionPanel<PartyRepItem> mediatorcomb = new ComboboxSelectionPanel<>("Mediator",
				model.getMediatorModel());
		mediatorcomb.setCellRenderer(new RepItemListCellRenderer());

		add(protocolcomb);
		add(new DeadlinePanel(model.getDeadlineModel()));
		add(new SpinnerPanel("Number of Tournaments", model.getNumTournamentsModel()));
		add(new SpinnerPanel("Agents per Session", model.getNumAgentsPerSessionModel()));
		add(new CheckboxPanel("Agent Repetition", model.getAgentRepetitionModel()));
		add(new CheckboxPanel("Randomize session order", model.getRandomSessionOrderModel()));
		add(new CheckboxPanel("Enable System.out print", model.getEnablePrint()));
		add(new ComboboxSelectionPanel<>("Data persistency", model.getPersistentDatatypeModel()));

		add(mediatorcomb);

		add(new PartiesAndProfilesPanel(model.getPartyModel(), model.getProfileModel()));

		biOptionsPanel = new BilateralOptionsPanel(model.getBilateralOptionsModel());
		add(biOptionsPanel);

		add(start);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.modelIsComplete();
			}
		});

		model.getNumAgentsPerSessionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateBipanelVisibility();
			}

		});
		updateBipanelVisibility();

	}

	private void updateBipanelVisibility() {
		biOptionsPanel.setVisible((Integer) model.getNumAgentsPerSessionModel().getValue() == 2);
	}

	/**
	 * simple stub to run this stand-alone (for testing).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame gui = new JFrame();
		gui.setLayout(new BorderLayout());
		MultiTournamentModel model = new MultiTournamentModel();
		gui.getContentPane().add(new MultiTournamentSettingsPanel(model), BorderLayout.CENTER);
		gui.pack();
		gui.setVisible(true);

		model.addListener(new Listener<MultilateralTournamentConfiguration>() {

			@Override
			public void notifyChange(MultilateralTournamentConfiguration data) {
				System.out.println("done, with " + data);
				gui.setVisible(false);
			}
		});
	}
}

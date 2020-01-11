package negotiator.gui.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import listener.DefaultListenable;
import listener.Listener;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.gui.deadline.DeadlineModel;
import negotiator.gui.negosession.ContentProxy;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.ParticipantRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import panels.BooleanModel;
import panels.SingleSelectionModel;
import panels.SubsetSelectionModel;

/**
 * Contains the basic elements of MultilateralTournamentConfiguration, but
 * mutable and the subcomponents are listenable so that we can use it for the
 * MVC pattern. It listens to changes in the protocol and updates the model when
 * necessary.
 * 
 * <p>
 * You can get notified when the multitournamentmodel is complete (as indicated
 * by the user, he can press 'start' in the GUI). The data passed with the
 * notification is the {@link MultilateralTournamentConfiguration}.
 * 
 * @author W.Pasman
 *
 */
public class MultiTournamentModel extends DefaultListenable<MultilateralTournamentConfiguration> {

	// models are all final, as they will be used to hook up the GUI.

	private final SubsetSelectionModel<ProfileRepItem> profileModel;
	private final SubsetSelectionModel<ParticipantRepItem> partyModel;
	private final SingleSelectionModel<MultiPartyProtocolRepItem> protocolModel;
	private final DeadlineModel deadlineModel = new DeadlineModel();
	private final SingleSelectionModel<PartyRepItem> mediatorModel;
	private final SpinnerModel numTournamentsModel = new SpinnerNumberModel(1, 1, 2000000000, 1);
	private final SpinnerModel numAgentsPerSessionModel = new SpinnerNumberModel(1, 1, 2000000000, 1);
	private final BooleanModel agentRepetitionModel = new BooleanModel(false);
	private final BooleanModel randomSessionOrderModel = new BooleanModel(false);
	private final BooleanModel enablePrintModel = new BooleanModel(false);
	private final BilateralOptionsModel bilateralOptionsModel;
	private final SingleSelectionModel<PersistentDataType> persistentDatatypeModel = new SingleSelectionModel<PersistentDataType>(
			Arrays.asList(PersistentDataType.values()));

	public MultiTournamentModel() {
		// load initial models.
		protocolModel = new SingleSelectionModel<>(ContentProxy.fetchProtocols());
		profileModel = new SubsetSelectionModel<ProfileRepItem>(ContentProxy.fetchProfiles());

		// stubs for the partyModel and mediatorModel, will be set properly in
		// updateSubmodels.
		partyModel = new SubsetSelectionModel<ParticipantRepItem>(new ArrayList<ParticipantRepItem>());
		mediatorModel = new SingleSelectionModel<PartyRepItem>(new ArrayList<PartyRepItem>());

		bilateralOptionsModel = new BilateralOptionsModel(protocolModel);

		updateSubmodels();
		updateAgentRepetition();

		addConstraints();

	}

	public SubsetSelectionModel<ProfileRepItem> getProfileModel() {
		return profileModel;
	}

	/**
	 * @return model containing the deadline information
	 */
	public DeadlineModel getDeadlineModel() {
		return deadlineModel;
	}

	/**
	 * @return model containing the parties to use in the tournament
	 */
	public SubsetSelectionModel<ParticipantRepItem> getPartyModel() {
		return partyModel;
	}

	/**
	 * @return the model containing the number of tournaments to be run. This is
	 *         also called "number of sessions" in some places. May be somethign
	 *         historic.
	 */
	public SpinnerModel getNumTournamentsModel() {
		return numTournamentsModel;
	}

	/**
	 * @return the model containing the number of agents per session.
	 */
	public SpinnerModel getNumAgentsPerSessionModel() {
		return numAgentsPerSessionModel;
	}

	/**
	 * @return mediator model, or null if no mediator for this protocol
	 */
	public SingleSelectionModel<PartyRepItem> getMediatorModel() {
		return mediatorModel;
	}

	/**
	 * @return this model, converted in a
	 *         {@link MultilateralTournamentConfiguration}.
	 */
	public MultilateralTournamentConfiguration getConfiguration() {
		List<ProfileRepItem> profilesB = new ArrayList<>();
		List<ParticipantRepItem> partiesB = new ArrayList<>();

		if (!bilateralOptionsModel.getPlayBothSides().getValue()) {
			profilesB = bilateralOptionsModel.getProfileModelB().getSelectedItems();
			partiesB = bilateralOptionsModel.getPartyModelB().getSelectedItems();
		}
		return new MultilateralTournamentConfiguration(protocolModel.getSelection(), deadlineModel.getDeadline(),
				mediatorModel.getSelection(), partyModel.getSelectedItems(), profileModel.getSelectedItems(), partiesB,
				profilesB, (int) numTournamentsModel.getValue(), (int) numAgentsPerSessionModel.getValue(),
				agentRepetitionModel.getValue(), randomSessionOrderModel.getValue(),
				persistentDatatypeModel.getSelection(), enablePrintModel.getValue());

	}

	/**
	 * @return model containing the protocol
	 */
	public SingleSelectionModel<MultiPartyProtocolRepItem> getProtocolModel() {
		return protocolModel;
	}

	public BooleanModel getAgentRepetitionModel() {
		return agentRepetitionModel;
	}

	public BooleanModel getRandomSessionOrderModel() {
		return randomSessionOrderModel;
	}

	/**
	 * Call this when model is completed (user clicked 'start'). TODO check that
	 * the model is indeed complete.
	 */
	public void modelIsComplete() {
		notifyChange(getConfiguration());
	}

	public BilateralOptionsModel getBilateralOptionsModel() {
		return bilateralOptionsModel;
	}

	public SingleSelectionModel<PersistentDataType> getPersistentDatatypeModel() {
		return persistentDatatypeModel;
	}

	/******************* support funcs ***********************/
	/**
	 * Update the repetition setting. Must go to "true, locked" mode if
	 * agentsPerSession is bigger than the number of available agents.
	 */
	private void updateAgentRepetition() {
		agentRepetitionModel.setLock(false);
		if ((int) numAgentsPerSessionModel.getValue() > partyModel.getSelectedItems().size()) {
			agentRepetitionModel.setValue(true);
			agentRepetitionModel.setLock(true);
		}
	}

	/**
	 * connecting listeners that check the constraints between the fields in the
	 * model
	 */
	private void addConstraints() {
		// protocol has major impact on the submodels
		protocolModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				updateSubmodels();
			}
		});

		// The "Agents per session" field by default equals number of profiles;
		// the agent repetition is depending on this too.
		profileModel.addListener(new Listener<ProfileRepItem>() {
			@Override
			public void notifyChange(ProfileRepItem data) {
				numAgentsPerSessionModel.setValue(profileModel.getSelectedItems().size());
				updateAgentRepetition();
			}
		});

		// #parties and numAgentsPerSession -> repetition
		partyModel.addListener(new Listener<ParticipantRepItem>() {
			@Override
			public void notifyChange(ParticipantRepItem data) {
				updateAgentRepetition();
			}
		});
		numAgentsPerSessionModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateAgentRepetition();
			}
		});
	}

	/**
	 * Update the models after a protocol change.
	 * 
	 * @param protocol
	 *            the new protocol.
	 */
	private void updateSubmodels() {
		MultiPartyProtocolRepItem protocol = protocolModel.getSelection();
		partyModel.setAllItems(ContentProxy.fetchPartiesForProtocol(protocol));
		mediatorModel.setAllItems(ContentProxy.fetchMediatorsForProtocol(protocol));
	}

	public BooleanModel getEnablePrint() {
		return enablePrintModel;
	}

}

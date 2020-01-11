package negotiator.gui.session;

import java.util.ArrayList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import negotiator.AgentID;
import negotiator.gui.negosession.ContentProxy;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.ParticipantRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.Participant;
import panels.SingleSelectionModel;
import panels.TextModel;

/**
 * Holds the MVC model information for a participant in the negotiation.
 */
public class ParticipantModel {
	private final TextModel partyIdModel = new TextModel("Party 1");
	private final SingleSelectionModel<ParticipantRepItem> partyModel = new SingleSelectionModel<>(
			new ArrayList<ParticipantRepItem>());
	private final SingleSelectionModel<ProfileRepItem> profileModel = new SingleSelectionModel<ProfileRepItem>(
			ContentProxy.fetchProfiles());
	private final SingleSelectionModel<MultiPartyProtocolRepItem> protocolModel;

	/**
	 * 
	 * @param protocolModel
	 *            holding the protocol that this participant has to use.
	 */
	public ParticipantModel(SingleSelectionModel<MultiPartyProtocolRepItem> protocolModel) {
		this.protocolModel = protocolModel;
		connect();
		protocolChanged();
	}

	public TextModel getIdModel() {
		return partyIdModel;
	}

	public SingleSelectionModel<ParticipantRepItem> getPartyModel() {
		return partyModel;
	}

	public SingleSelectionModel<ProfileRepItem> getProfileModel() {
		return profileModel;
	}

	/**
	 * Try to Automatically increment the current ID, strategy and profile.
	 */
	public void increment() {
		partyIdModel.increment();
		profileModel.increment();
		partyModel.increment();
	}

	/**
	 * @return {@link Participant} as set at this moment in this model
	 */
	public Participant getParticipant() {
		return new Participant(new AgentID(partyIdModel.getText()), (ParticipantRepItem) partyModel.getSelectedItem(),
				(ProfileRepItem) profileModel.getSelectedItem());
	}

	/*************************** private support funcs ********************/

	/**
	 * connext. protocol changes -> update available parties.
	 */
	private void connect() {
		protocolModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {

			}

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				protocolChanged();
			}
		});
	}

	private void protocolChanged() {
		partyModel.setAllItems(ContentProxy.fetchPartiesForProtocol(protocolModel.getSelection()));

	}

}

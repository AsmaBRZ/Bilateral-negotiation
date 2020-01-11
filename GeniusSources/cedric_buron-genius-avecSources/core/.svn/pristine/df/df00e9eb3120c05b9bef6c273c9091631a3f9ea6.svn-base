package negotiator.events;

import java.util.List;

import negotiator.Bid;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.session.Session;

/**
 * Records an end of a multi-party session end. All events happening after this
 * event are for another session.
 *
 */
public class MultipartySessionEndedEvent implements NegotiationEvent {
	private Session session;
	private Bid agreement;
	private List<NegotiationPartyInternal> parties;

	/**
	 * @param session
	 *            the session that ended
	 * @param agreement
	 *            the bid that was agreed on at the end, or null if no
	 *            agreement.
	 * @param parties
	 *            list of the involved {@link NegotiationPartyInternal} , in
	 *            correct order
	 */
	public MultipartySessionEndedEvent(Session session, Bid agreement, List<NegotiationPartyInternal> parties) {

		this.session = session;
		this.agreement = agreement;
		this.parties = parties;
	}

	public Session getSession() {
		return session;
	}

	/**
	 * 
	 * @return final agreement bid, or null if no agreement was reached
	 */
	public Bid getAgreement() {
		return agreement;
	}

	public List<NegotiationPartyInternal> getParties() {
		return parties;
	}

}

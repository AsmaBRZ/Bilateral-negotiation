package negotiator.events;

import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.SessionsInfo;
import negotiator.session.Session;

/**
 * This exception indicates that something went wrong but we did an automatic
 * recovery. For example the session was succesfully completed but the aftermath
 * failed. This can happen when there is an issue while writing the
 * {@link SessionsInfo} or when the
 * {@link NegotiationParty#negotiationEnded(negotiator.Bid)} call failed.
 *
 */
public class RecoverableSessionErrorEvent implements NegotiationEvent {

	private Session session;
	private NegotiationPartyInternal party;
	private Exception exception;

	public RecoverableSessionErrorEvent(Session session, NegotiationPartyInternal party, Exception e) {
		this.session = session;
		this.party = party;
		this.exception = e;
	}

	public Exception getException() {
		return exception;
	}

	public NegotiationPartyInternal getParty() {
		return party;
	}

	public Session getSession() {
		return session;
	}
}

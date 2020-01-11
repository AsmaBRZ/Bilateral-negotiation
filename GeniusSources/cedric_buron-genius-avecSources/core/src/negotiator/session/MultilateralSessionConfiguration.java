package negotiator.session;

import java.util.List;

import negotiator.Deadline;
import negotiator.config.MultilateralTournamentConfigurationInterface;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.MultiPartyProtocolRepItem;

/**
 * Configuration for a multilateral (single) session. Contains all info to run
 * one multi party negotiation session. This is the one-session version of
 * {@link MultilateralTournamentConfigurationInterface}.
 * 
 * @author W.Pasman
 *
 */
public interface MultilateralSessionConfiguration {

	/**
	 * @return the protocol
	 */
	MultiPartyProtocolRepItem getProtocol();

	/**
	 * @return the deadline setting
	 */
	public Deadline getDeadline();

	/**
	 * 
	 * @return the mediator. Only valid when {@link #getProtocol()} returns a
	 *         protocol that needs mediator.
	 */
	public Participant getMediator();

	/**
	 * @return the normal parties, excluding the mediator(s).
	 */
	public List<Participant> getParties();

	/**
	 * @return persistent data type to use for this run
	 */
	PersistentDataType getPersistentDataType();
}

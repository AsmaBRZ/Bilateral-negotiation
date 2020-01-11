package negotiator.session;

import java.util.Collections;
import java.util.List;

import negotiator.Deadline;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.MultiPartyProtocolRepItem;

/**
 * Holds all information to start and run a session. Contains only references,
 * no actual agent instantiations. Immutable implementation
 */
public class SessionConfiguration implements MultilateralSessionConfiguration {

	private MultiPartyProtocolRepItem protocol;
	private List<Participant> parties;
	private Deadline deadline;
	private Participant mediator;
	private PersistentDataType persistentDataType;

	public SessionConfiguration(MultiPartyProtocolRepItem protocol, Participant mediator, List<Participant> parties,
			Deadline deadline, PersistentDataType type) {
		this.protocol = protocol;
		this.parties = parties;
		this.deadline = deadline;
		this.mediator = mediator;
		this.persistentDataType = type;
	}

	@Override
	public MultiPartyProtocolRepItem getProtocol() {
		return protocol;
	}

	@Override
	public List<Participant> getParties() {
		return Collections.unmodifiableList(parties);
	}

	@Override
	public Deadline getDeadline() {
		return deadline;
	}

	@Override
	public String toString() {
		return "SessionConfiguration[" + protocol + "," + parties + "," + deadline + "]";
	}

	@Override
	public Participant getMediator() {
		return mediator;
	}

	@Override
	public PersistentDataType getPersistentDataType() {
		return persistentDataType;
	}

}

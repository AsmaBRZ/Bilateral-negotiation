package negotiator.events;

import java.util.Map;

import negotiator.parties.NegotiationParty;

/**
 * Reports log info returned by an agent. See
 * {@link NegotiationParty#negotiationEnded(negotiator.Bid)}. Immutable
 */
public class AgentLogEvent implements NegotiationEvent {

	private Map<String, String> log;
	private String id;

	/**
	 * Contains log info from the agent, as received from the
	 * {@link NegotiationParty#negotiationEnded(negotiator.Bid)} call,
	 * 
	 * @param agent
	 *            the agent that returned the info
	 * @param logresult
	 *            see {@link NegotiationParty#negotiationEnded(negotiator.Bid)}
	 */
	public AgentLogEvent(String agent, Map<String, String> logresult) {
		log = logresult;
	}

	public Map<String, String> getLog() {
		return log;
	}

	public String getAgent() {
		return id;
	}

}

package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * Symbolizes an offer of an agent for the opponent. Immutable.
 * 
 * @author Tim Baarslag and Dmytro Tykhonov
 */
public class Offer extends DefaultActionWithBid {

	public Offer(AgentID agentID, Bid bid) {
		super(agentID, bid);
	}

	/**
	 * @return string representation of action
	 */
	public String toString() {
		return "(Offer " + getContent() + ")";
	}
}
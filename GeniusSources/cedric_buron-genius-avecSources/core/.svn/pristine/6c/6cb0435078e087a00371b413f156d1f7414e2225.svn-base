package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * This action is used by parties to indicate they want not to continue after
 * this last offer. Immutable.
 * 
 * @author Reyhan
 */

public class EndNegotiationWithAnOffer extends DefaultActionWithBid {

	public EndNegotiationWithAnOffer(AgentID party, Bid bid) {
		super(party, bid);
	}

	public String toString() {
		return "(End Negotiation with Offer: " + getContent() + ")";
	}
}

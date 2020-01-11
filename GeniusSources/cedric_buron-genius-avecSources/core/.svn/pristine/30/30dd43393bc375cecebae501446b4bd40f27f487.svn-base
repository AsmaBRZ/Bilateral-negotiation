package agents.nastyagent;

import java.util.List;

import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * Keeps repeating best bid as offer
 * 
 * @author W.Pasman
 *
 */
public class OnlyBestBid extends NastyAgent {
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		return new Offer(id, bids.get(0));
	}
}

package negotiator.actions;

import negotiator.Bid;

/**
 * Interface for actions that involve a {@link Bid}
 * 
 * @author W.Pasman
 *
 */
public interface ActionWithBid extends Action {
	/**
	 * Returns the bid that is involved with this action.
	 * 
	 * @return the involved Bid.
	 */

	public Bid getBid();
}

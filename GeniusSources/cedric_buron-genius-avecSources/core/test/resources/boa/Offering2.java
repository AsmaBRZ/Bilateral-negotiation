package resources.boa;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.OfferingStrategy;

public class Offering2 extends OfferingStrategy {

	@Override
	public BidDetails determineOpeningBid() {
		return null;
	}

	@Override
	public BidDetails determineNextBid() {
		return null;
	}

	@Override
	public String getName() {
		return "Offering 2";
	}

}

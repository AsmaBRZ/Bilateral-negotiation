package resources.boa;

import java.util.List;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.OMStrategy;

public class OMStrategy1 extends OMStrategy {

	@Override
	public BidDetails getBid(List<BidDetails> bidsInRange) {
		return null;
	}

	@Override
	public boolean canUpdateOM() {
		return false;
	}

	@Override
	public String getName() {
		return "OMStrategy 1";
	}

}

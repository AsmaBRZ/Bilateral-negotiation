package examplepackage;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.ArrayList; 

import java.util.Collections;   
import agents.SimpleAgent;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.timeline.Timeline;
import negotiator.*;
/**
 * @author  Asma
 */
public class CarefulAgent extends Agent {
	private Action actionOfPartner = null;
	private Bid lastPartnerBid;
        private boolean init=true;
        private Bid oldBid=null;
	private List<Bid> acceptableBids;
	/**
	 * Note: {@link SimpleAgent} does not account for the discount factor in its
	 * computations
	 */
	private static double MINIMUM_BID_UTILITY =	 0.0;

	/**
	 * init is called when a next session starts with the same opponent.
	 */
	@Override
	public void init() {
		super.init();
		acceptableBids = new ArrayList<Bid>();
		BidIterator iter = new BidIterator(this.utilitySpace.getDomain());
		while (iter.hasNext()) {
		 	Bid bid = iter.next();
		    	try {
		    		if (getUtility(bid) >= utilitySpace.getReservationValue() && (Math.random() <= getUtility(bid))) {
		    			this.acceptableBids.add(bid);
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
			}
		}
	}

	@Override
	public String getVersion() {
		return "3.1";
	}

	@Override
	public String getName() {
		return "Careful Agent";
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		actionOfPartner = opponentAction;
		if (actionOfPartner instanceof Offer) {
			lastPartnerBid = ((Offer) actionOfPartner).getBid();
		}
	}

	@Override
	public Action chooseAction() {
		Action action = null;

		try {
			if (actionOfPartner == null)
				action = chooseRandomBidAction();
			else if (actionOfPartner instanceof Offer) {
				// get current time
				double time = timeline.getTime();
				action = chooseRandomBidAction();

				// accept under certain circumstances
				if (isAcceptable(lastPartnerBid))
					action = new Accept(getAgentID(), lastPartnerBid);

			}
		} catch (Exception e) {
			System.out.println("Exception in ChooseAction:" + e.getMessage());
			if (lastPartnerBid != null) {
				action = new Accept(getAgentID(), lastPartnerBid);
			} else {
				action = new EndNegotiation(getAgentID());
			}
		}
		return action;
	}

	private boolean isAcceptable(Bid bid) {
		if (this.acceptableBids.contains(bid)){
			return true;
		}
		return false;
	}

	/**
	 * Wrapper for getRandomBid, for convenience.
	 *
	 * @return new Action(Bid(..)), with bid utility > MINIMUM_BID_UTIL. If a
	 *         problem occurs, it returns an Accept() action.
	 */
	private Action chooseRandomBidAction() {
		Bid nextBid = null;
		try {
			nextBid = getRandomBid();
		} catch (Exception e) {
			System.out.println("Problem with received bid:" + e.getMessage() + ". cancelling bidding");
		}
		if (nextBid == null)
			return (new Accept(getAgentID(), lastPartnerBid));
		return (new Offer(getAgentID(), nextBid));
	}

	/**
	 * @return a random bid with high enough utility value.
	 * @throws Exception
	 *             if we can't compute the utility (eg no evaluators have been
	 *             set) or when other evaluators than a DiscreteEvaluator are
	 *             present in the util space.
	 */
	private Bid getRandomBid() throws Exception {
                Bid bid;
                Bid maxBid = this.utilitySpace.getMaxUtilityBid();
                if(!init){

                        Bid tmp=null;
                        int cp=0;
			for(int  i=0;i<acceptableBids.size();i++) {
			    if (cp==0){
			    	tmp = acceptableBids.get(i);
			    	cp++;
			    }else{
			    	bid = acceptableBids.get(i);
			        if(getUtility(bid)>getUtility(tmp) && getUtility(bid) < getUtility(this.oldBid)){
			        	tmp=bid;
					
			        }
			    }  
			}
			maxBid=tmp;
                        this.oldBid=maxBid;
		}
		this.init=false;
		return maxBid;
	}

	/**
	 * This function determines the accept probability for an offer. At t=0 it
	 * will prefer high-utility offers. As t gets closer to 1, it will accept
	 * lower utility offers with increasing probability. it will never accept
	 * offers with utility 0.
	 *
	 * @param u
	 *            is the utility
	 * @param t
	 *            is the time as fraction of the total available time (t=0 at
	 *            start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception
	 *             if you use wrong values for u or t.
	 *
	 */
	double Paccept(double u, double t1) throws Exception {
		double t = t1 * t1 * t1; // steeper increase when deadline approaches.
		if (u < 0 || u > 1.05)
			throw new Exception("utility " + u + " outside [0,1]");
		// normalization may be slightly off, therefore we have a broad boundary
		// up to 1.05
		if (t < 0 || t > 1)
			throw new Exception("time " + t + " outside [0,1]");
		if (u > 1.)
			u = 1;
		if (t == 0.5)
			return u;
		return (u - 2. * u * t + 2. * (-1. + t + Math.sqrt(sq(-1. + t) + u * (-1. + 2 * t)))) / (-1. + 2 * t);
	}

	double sq(double x) {
		return x * x;
	}


}

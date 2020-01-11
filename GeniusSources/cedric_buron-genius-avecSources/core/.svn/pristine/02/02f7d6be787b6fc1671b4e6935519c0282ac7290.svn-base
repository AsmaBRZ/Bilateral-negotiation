package negotiator.boaframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import misc.Pair;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.boa.BoaPartyRepItem;

/**
 * This class is used to convert a BOA party to a real agent. The
 * parseStrategyParameters loads the information object, the agentSetup uses the
 * information object to load the agent using reflection.
 * 
 * This class is special in that it is constructed directly by a
 * {@link BoaPartyRepItem} . not for inclusion in a repository or extending.
 * 
 * This is converted from {@link BOAagent} and TheBOAagent
 * 
 * For more information, see: Baarslag T., Hindriks K.V., Hendrikx M.,
 * Dirkzwager A., Jonker C.M. Decoupling Negotiating Agents to Explore the Space
 * of Negotiation Strategies. Proceedings of The Fifth International Workshop on
 * Agent-based Complex Automated Negotiations (ACAN 2012), 2012.
 * http://mmi.tudelft.nl/sites/default/files/boa.pdf
 * 
 * @author Tim Baarslag, Alex Dirkzwager, Mark Hendrikx
 */
public final class BoaParty extends AbstractNegotiationParty {
	/** Decides when to accept */
	protected AcceptanceStrategy acceptConditions;
	/** Decides what to offer */
	protected OfferingStrategy offeringStrategy;
	/** Approximates the utility of a bid for the opponent */
	protected OpponentModel opponentModel;
	/** Selects which bid to send when using an opponent model */
	protected OMStrategy omStrategy;

	// init params for all components.
	private Map<String, Double> acParams;
	private Map<String, Double> osParams;
	private Map<String, Double> omParams;
	private Map<String, Double> omsParams;

	/** Links to the negotiation domain */
	protected NegotiationSession negotiationSession;
	/** Store {@link Multi_AcceptanceCondition} outcomes */
	public ArrayList<Pair<Bid, String>> savedOutcomes;
	/** Contains the space of possible bids */
	protected OutcomeSpace outcomeSpace;
	private Bid oppBid;

	/**
	 * Stores all relevant values for initializing the components. Will be used
	 * when init is called.
	 * 
	 * @param ac
	 * @param acParams
	 * @param os
	 * @param osParams
	 * @param om
	 * @param omParams
	 * @param oms
	 * @param omsParams
	 */
	public BoaParty(AcceptanceStrategy ac, Map<String, Double> acParams, OfferingStrategy os,
			Map<String, Double> osParams, OpponentModel om, Map<String, Double> omParams, OMStrategy oms,
			Map<String, Double> omsParams) {
		acceptConditions = ac;
		this.acParams = acParams;
		offeringStrategy = os;
		this.osParams = osParams;
		opponentModel = om;
		this.omParams = omParams;
		omStrategy = oms;
		this.omsParams = omsParams;
	}

	/**
	 * Initializes the agent and creates a new negotiation session object.
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);

		SessionData sessionData = null;
		if (info.getPersistentData().getPersistentDataType() == PersistentDataType.SERIALIZABLE) {
			sessionData = (SessionData) info.getPersistentData().get();
		}
		if (sessionData == null) {
			sessionData = new SessionData();
		}
		negotiationSession = new NegotiationSession(sessionData, utilitySpace, timeline);
		initStrategies();
	}

	private void initStrategies() {
		// init the components.
		try {
			opponentModel.init(negotiationSession, omParams);
			// opponentModel.setOpponentUtilitySpace((BilateralAtomicNegotiationSession)fNegotiation);
			omStrategy.init(negotiationSession, opponentModel, omsParams);
			offeringStrategy.init(negotiationSession, opponentModel, omStrategy, osParams);
			acceptConditions.init(negotiationSession, offeringStrategy, opponentModel, acParams);
			// acceptConditions.setOpponentUtilitySpace(fNegotiation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the actions made by a partner. First, it stores the bid in the
	 * history, then updates the opponent model.
	 * 
	 * @param opponentAction
	 *            by opponent in current turn
	 */
	@Override
	public void receiveMessage(AgentID sender, Action opponentAction) {
		// 1. if the opponent made a bid
		if (opponentAction instanceof Offer) {
			oppBid = ((Offer) opponentAction).getBid();
			// 2. store the opponent's trace
			try {
				BidDetails opponentBid = new BidDetails(oppBid, negotiationSession.getUtilitySpace().getUtility(oppBid),
						negotiationSession.getTime());
				negotiationSession.getOpponentBidHistory().add(opponentBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 3. if there is an opponent model, receiveMessage it using the
			// opponent's
			// bid
			if (opponentModel != null && !(opponentModel instanceof NoModel)) {
				if (omStrategy.canUpdateOM()) {
					opponentModel.updateModel(oppBid);
				} else {
					if (!opponentModel.isCleared()) {
						opponentModel.cleanUp();
					}
				}
			}
		}
	}

	/**
	 * Chooses an action to perform.
	 * 
	 * @return Action the agent performs
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {

		BidDetails bid;

		// if our history is empty, then make an opening bid
		if (negotiationSession.getOwnBidHistory().getHistory().isEmpty()) {
			bid = offeringStrategy.determineOpeningBid();
		} else {
			// else make a normal bid
			bid = offeringStrategy.determineNextBid();
			if (offeringStrategy.isEndNegotiation()) {
				return new EndNegotiation(getPartyId());
			}
		}

		// if the offering strategy made a mistake and didn't set a bid: accept
		if (bid == null) {
			System.out.println("Error in code, null bid was given");
			return new Accept(getPartyId(), oppBid);
		} else {
			offeringStrategy.setNextBid(bid);
		}

		// check if the opponent bid should be accepted
		Actions decision = Actions.Reject;
		if (!negotiationSession.getOpponentBidHistory().getHistory().isEmpty()) {
			decision = acceptConditions.determineAcceptability();
		}

		// check if the agent decided to break off the negotiation
		if (decision.equals(Actions.Break)) {
			System.out.println("send EndNegotiation");
			return new EndNegotiation(getPartyId());
		}
		// if agent does not accept, it offers the counter bid
		if (decision.equals(Actions.Reject)) {
			negotiationSession.getOwnBidHistory().add(bid);
			return new Offer(getPartyId(), bid.getBid());
		} else {
			return new Accept(getPartyId(), oppBid);
		}
	}

	/**
	 * Returns the offering strategy of the agent.
	 * 
	 * @return offeringstrategy of the agent.
	 */
	public OfferingStrategy getOfferingStrategy() {
		return offeringStrategy;
	}

	/**
	 * Returns the opponent model of the agent.
	 * 
	 * @return opponent model of the agent.
	 */
	public OpponentModel getOpponentModel() {
		return opponentModel;
	}

	/**
	 * Returns the acceptance strategy of the agent.
	 * 
	 * @return acceptance strategy of the agent.
	 */
	public AcceptanceStrategy getAcceptanceStrategy() {
		return acceptConditions;
	}

	/**
	 * Method that first calls the endSession method of each component to
	 * receiveMessage the session data and then stores the session data if it is
	 * not empty and is changed.
	 */
	public void endSession(NegotiationResult result) {
		offeringStrategy.endSession(result);
		acceptConditions.endSession(result);
		opponentModel.endSession(result);
		SessionData savedData = negotiationSession.getSessionData();
		if (!savedData.isEmpty() && savedData.isChanged()) {
			savedData.changesCommitted();
			getData().put(savedData);
		}
	}

	/**
	 * Clears the agent's variables.
	 */
	public void cleanUp() {
		offeringStrategy = null;
		acceptConditions = null;
		omStrategy = null;
		opponentModel = null;
		outcomeSpace = null;
		negotiationSession = null;
	}

	@Override
	public String getDescription() {
		return "BOA(" + acceptConditions.getName() + "," + offeringStrategy.getName() + "," + opponentModel.getName()
				+ "," + omStrategy.getName() + ")";
	}
}
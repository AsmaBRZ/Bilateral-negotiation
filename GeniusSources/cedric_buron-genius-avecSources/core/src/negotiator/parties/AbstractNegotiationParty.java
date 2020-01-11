package negotiator.parties;

import java.util.HashMap;
import java.util.Random;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.actions.Inform;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.persistent.PersistentDataContainer;
import negotiator.protocol.MultilateralProtocol;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

/**
 * A basic implementation of the {@link NegotiationParty} interface. This basic
 * implementation sets up some common variables for you.
 *
 * @author David Festen
 * @author Reyhan (The random bid generator)
 */
public abstract class AbstractNegotiationParty implements NegotiationParty {

	/**
	 * Time line used by the party if time deadline is set.
	 */
	protected TimeLineInfo timeline;// should be final after init

	/**
	 * Random seed used by this party.
	 */
	protected Random rand;// should be final after init

	/**
	 * utility space used by this party (set in constructor).
	 * 
	 * NOTICE this is protected and appears to be used directly by lots of
	 * implementations.
	 */
	protected AbstractUtilitySpace utilitySpace;// should be final after init

	/**
	 * Last received action, or null
	 */
	private Action lastReceivedAction = null;

	private int numberOfParties = -1;

	private NegotiationInfo info;

	@Override
	public void init(NegotiationInfo info) {
		this.info = info;
		this.utilitySpace = info.getUtilitySpace();
		this.rand = new Random(info.getRandomSeed());
		this.timeline = info.getTimeline();
	}

	/**
	 * Generates a random bid which will be generated using this.utilitySpace.
	 *
	 * @return A random bid
	 */
	protected Bid generateRandomBid() {
		try {
			// Pairs <issue number, chosen value string>
			HashMap<Integer, Value> values = new HashMap<Integer, Value>();

			// For each issue, put a random value
			for (Issue currentIssue : utilitySpace.getDomain().getIssues()) {
				values.put(currentIssue.getNumber(), getRandomValue(currentIssue));
			}

			// return the generated bid
			return new Bid(utilitySpace.getDomain(), values);

		} catch (Exception e) {

			// return empty bid if an error occurred
			return new Bid(utilitySpace.getDomain());
		}
	}

	/**
	 * Gets a random value for the given issue.
	 *
	 * @param currentIssue
	 *            The issue to generate a random value for
	 * @return The random value generated for the issue
	 * @throws Exception
	 *             if the issues type is not Discrete, Real or Integer.
	 */
	protected Value getRandomValue(Issue currentIssue) throws Exception {

		Value currentValue;
		int index;

		switch (currentIssue.getType()) {
		case DISCRETE:
			IssueDiscrete discreteIssue = (IssueDiscrete) currentIssue;
			index = (rand.nextInt(discreteIssue.getNumberOfValues()));
			currentValue = discreteIssue.getValue(index);
			break;
		case REAL:
			IssueReal realIss = (IssueReal) currentIssue;
			index = rand.nextInt(realIss.getNumberOfDiscretizationSteps()); // check
																			// this!
			currentValue = new ValueReal(
					realIss.getLowerBound() + (((realIss.getUpperBound() - realIss.getLowerBound()))
							/ (realIss.getNumberOfDiscretizationSteps())) * index);
			break;
		case INTEGER:
			IssueInteger integerIssue = (IssueInteger) currentIssue;
			index = rand.nextInt(integerIssue.getUpperBound() - integerIssue.getLowerBound() + 1);
			currentValue = new ValueInteger(integerIssue.getLowerBound() + index);
			break;
		default:
			throw new Exception("issue type " + currentIssue.getType() + " not supported");
		}

		return currentValue;
	}

	/**
	 * Gets the utility for the given bid.
	 *
	 * @param bid
	 *            The bid to get the utility for
	 * @return A double value between [0, 1] (inclusive) that represents the
	 *         bids utility
	 */
	public double getUtility(Bid bid) {
		try {
			// throws exception if bid incomplete or not in utility space
			return bid == null ? 0 : utilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Gets the time discounted utility for the given bid.
	 *
	 * @param bid
	 *            The bid to get the utility for
	 * @return A double value between [0, 1] (inclusive) that represents the
	 *         bids utility
	 */
	public double getUtilityWithDiscount(Bid bid) {
		if (bid == null) {
			// utility is null if no bid
			return 0;
		} else if (timeline == null) {
			// return undiscounted utility if no timeline given
			return getUtility(bid);
		} else {
			// otherwise, return discounted utility
			return utilitySpace.getUtilityWithDiscount(bid, timeline);
		}
	}

	/**
	 * Gets this agent's utility space.
	 *
	 * @return The utility space
	 */
	public final AbstractUtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	/**
	 * Gets this agent's time line.
	 *
	 * @return The time line for this agent
	 */
	public TimeLineInfo getTimeLine() {
		return timeline;
	}

	/**
	 * Returns a human readable string representation of this party.
	 *
	 * @return the string representation of party id
	 */
	@Override
	public String toString() {
		return info.getAgentID().toString();
	}

	@Override
	public void receiveMessage(AgentID sender, Action act) {
		lastReceivedAction = act;
		if (act instanceof Inform) {
			numberOfParties = (Integer) ((Inform) act).getValue();
		}
	}

	/**
	 * 
	 * @return last received {@link Action} or null if nothing received yet.
	 */
	public Action getLastReceivedAction() {
		return lastReceivedAction;
	}

	public int getNumberOfParties() {
		if (numberOfParties == -1) {
			System.out.println("Make sure that you call the super class in receiveMessage() method.");
		}
		return numberOfParties;
	}

	final public AgentID getPartyId() {
		return info.getAgentID();
	}

	@Override
	public Class<? extends MultilateralProtocol> getProtocol() {
		return StackedAlternatingOffersProtocol.class;
	}

	@Override
	public HashMap<String, String> negotiationEnded(Bid acceptedBid) {
		return null;
	}

	/**
	 * @return persistent data
	 */
	public PersistentDataContainer getData() {
		return info.getPersistentData();
	}

	public Deadline getDeadlines() {
		return info.getDeadline();
	}

}

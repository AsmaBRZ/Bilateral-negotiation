package negotiator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.actions.OfferForVoting;
import negotiator.parties.NegotiationInfo;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MultilateralProtocol;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.AbstractUtilitySpace;

/**
 * Adapts {@link Agent} to the {@link NegotiationParty} so that legacy agents
 * can be run in the new multiparty system. Notice that these agents could
 * handle only 1 opponent, and thus may behave weird if presented with more than
 * one opponent.
 * 
 * What is unusual (in the Java sense) is that Agent extends this, not the other
 * way round. This way, all old agents also become a NegotiationParty.
 * 
 */
public abstract class AgentAdapter implements NegotiationParty {

	/**
	 * 
	 * @return the actual agent that is being adapted.
	 */
	abstract protected Agent getAgent();

	private Action lastAction = null;
	private AbstractUtilitySpace utilSpace;

	@Override
	public final void init(NegotiationInfo info) {
		this.utilSpace = info.getUtilitySpace();
		getAgent().internalInit(0, 1, new Date(), info.getDeadline().getTimeOrDefaultTimeout(), info.getTimeline(),
				utilSpace, new HashMap<AgentParameterVariable, AgentParamValue>(), info.getAgentID());
		getAgent().setName(info.getAgentID().toString());
		getAgent().init();
	}

	@Override
	public final Action chooseAction(List<Class<? extends Action>> possibleActions) {
		lastAction = getAgent().chooseAction();
		return lastAction;
	}

	@Override
	public final void receiveMessage(AgentID sender, Action action) {
		if (action instanceof Offer || action instanceof Accept || action instanceof OfferForVoting
				|| action instanceof EndNegotiation) {
			getAgent().ReceiveMessage(action);
		}
	}

	/**
	 * This is a convenience wrapper so that we don't have to fix all old agent
	 * descriptions (these used to be in the xml file)
	 */
	@Override
	public String getDescription() {
		return "Agent " + getAgent().getClass().getSimpleName();
	}

	@Override
	public final Class<? extends MultilateralProtocol> getProtocol() {
		return StackedAlternatingOffersProtocol.class;
	}

	@Override
	public final Map<String, String> negotiationEnded(Bid acceptedBid) {
		double util = 0;
		if (acceptedBid != null) {
			try {
				util = getAgent().getUtility(acceptedBid);
			} catch (Exception e) {
			}
		}
		getAgent().endSession(new NegotiationResult(util, lastAction, acceptedBid));
		return null;
	}

}

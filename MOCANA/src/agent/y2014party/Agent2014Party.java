package agent.y2014party;

import negotiator.*;
import negotiator.actions.Action;
import negotiator.actions.Inform;
import negotiator.boaframework.NegotiationSession;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * P-CBU created on 31/05/17.
 */
public abstract class Agent2014Party extends AbstractNegotiationParty {

    private negotiator.Agent myAgent;
    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        return myAgent.chooseAction();
    }

    public abstract Agent initAgent();

    public Agent getMyAgent() {
        return myAgent;
    }

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        myAgent = initAgent();
        myAgent.internalInit(0,1,new Date(), (int) Math.ceil(info.getTimeline().getTotalTime()), info.getTimeline(),utilitySpace,new HashMap<>(), info.getAgentID());
        myAgent.init();
    }

    @Override
    public void receiveMessage(AgentID sender, Action arguments) {
        super.receiveMessage(sender, arguments);
        if(!(arguments instanceof Inform)) {
            myAgent.ReceiveMessage(arguments);
        }
    }

}

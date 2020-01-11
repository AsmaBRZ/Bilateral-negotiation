package agent.y2014party;

import agents.anac.y2014.KGAgent.KGAgent;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class KGAgentParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new KGAgent();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - KGAgent";
    }

}

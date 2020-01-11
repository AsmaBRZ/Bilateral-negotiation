package agent.y2014party;

import agents.anac.y2014.AgentWhale.WhaleAgent;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class WhaleAgentParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new WhaleAgent();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - Whale";
    }

}
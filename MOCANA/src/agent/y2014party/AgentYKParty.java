package agent.y2014party;

import agents.anac.y2014.AgentYK.AgentYK;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class AgentYKParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new AgentYK();
    }


    @Override
    public String getDescription() {
        return "ANAC 2014 - AgentYK";
    }

}

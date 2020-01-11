package agent.y2014party;

import agents.anac.y2014.AgentM.AgentM;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class AgentMParty extends Agent2014Party {

    @Override
    public Agent initAgent() {
        return new AgentM();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - AgentM";
    }
}


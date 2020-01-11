package agent.y2014party;

import fr.kyriba.agent.mctsagent.MCTSAgent;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class MCTSParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new MCTSAgent();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - MCTS";
    }

}

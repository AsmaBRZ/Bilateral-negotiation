package agent.y2014party;

import agents.anac.y2014.DoNA.DoNA;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class DoNAParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new DoNA();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - DoNA";
    }

}

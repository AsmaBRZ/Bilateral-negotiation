package agent.y2014party;

import agents.anac.y2014.BraveCat.BraveCat;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class BraveCatParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new BraveCat();
    }


    @Override
    public String getDescription() {
        return "ANAC 2014 - BraveCat";
    }

}

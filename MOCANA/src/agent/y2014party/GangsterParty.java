package agent.y2014party;

import agents.anac.y2014.Gangster.Gangster;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class GangsterParty extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new Gangster();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - Gangsta";
    }

}

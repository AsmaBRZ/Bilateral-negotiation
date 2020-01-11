package agent.y2014party;

import agents.anac.y2014.TUDelftGroup2.Group2Agent;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class Group2Party extends Agent2014Party{
    @Override
    public Agent initAgent() {
        return new Group2Agent();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - Group2";
    }

}

package agent.y2014party;

import agents.anac.y2014.E2Agent.AnacSampleAgent;
import negotiator.Agent;

/**
 * P-CBU created on 31/05/17.
 */
public class E2Party extends Agent2014Party {
    @Override
    public Agent initAgent() {
        return new AnacSampleAgent();
    }

    @Override
    public String getDescription() {
        return "ANAC 2014 - E2";
    }

}

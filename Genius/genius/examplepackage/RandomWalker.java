package examplepackage;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import java.util.Random;

/**
 * @author Asma 
 */
public class CarefulAgent extends Agent{

    private Random rand;
    private Bid lastBid;

    @Override
    public void init(){
        super.init();
        rand = new Random();
    }

    @Override
    public Action chooseAction() {
        Bid bid = utilitySpace.getDomain().getRandomBid(rand);
        Action action = new Offer(getAgentID(), bid);
        return action;
    }

    public void ReceiveMessage(Action action){
        if(action instanceof Offer)
        lastBid = ((Offer) action).getBid();
    }
}

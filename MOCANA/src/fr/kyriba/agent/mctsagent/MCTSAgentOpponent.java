package fr.kyriba.agent.mctsagent;

import fr.kyriba.ExperimentsConstants;
import fr.kyriba.bidgeneration.MCTSBidGenerator;
import fr.kyriba.connectors.geniusconnectors.GeniusDomainAdapter;
import fr.kyriba.connectors.geniusconnectors.GeniusOffer;
import fr.kyriba.protocol.CommunicativeAct;
import fr.kyriba.protocol.Offer;
import fr.kyriba.protocol.ProtocolDescriptor;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Reject;
import negotiator.issue.Issue;
import negotiator.utility.NonlinearUtilitySpace;

import java.util.List;

/**
 * @author P-CBU
 */
public class MCTSAgentOpponent extends MCTSAgent{
    @Override
    //fonction de réponse (nouvelle proposition): 2 cas. Cas sans données: envoyer une proposition optimale au sens de la fonction d'utilité. Cas avec données: MCTS.
    public Action chooseAction() {
        if(handler.getHistory().size()<2){
            try {
                Bid firstBid = utilitySpace.getDomain().getRandomBid(ExperimentsConstants.RANDOM);
                if(utilitySpace instanceof NonlinearUtilitySpace){
                    double ut = utilitySpace.getUtility(firstBid);
                    for(int i = 0; i<100000; i++){
                        Bid testBid = utilitySpace.getDomain().getRandomBid(ExperimentsConstants.RANDOM);
                        if(ut<utilitySpace.getUtility(testBid)) {
                            ut = utilitySpace.getUtility(testBid);
                            System.out.println("utilité: "+ut);
                            firstBid = testBid;
                        }
                    }
                }
                else {
                    firstBid = utilitySpace.getMaxUtilityBid();
                }
                handler.store(GeniusOffer.offerFromGenius(GeniusDomainAdapter.convertFromGenius(utilitySpace.getDomain().getIssues()),new negotiator.actions.Offer(getAgentID(),firstBid),0,handler.getHistory().size(), CommunicativeAct.PROPOSE));
                return new negotiator.actions.Offer(getAgentID(),firstBid);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            //Stratégie
            MCTSBidGenerator generator = new MCTSBidGenerator(handler, alpha,GeniusDomainAdapter.convertFromGenius(utilitySpace.getDomain().getIssues()), this, new ProtocolDescriptor(2, this), nbSamplesGaussianClassification, ExperimentsConstants.SIMULATION_TIME_LIMIT_OPPONENT);//todo change the first argument
            try {
                Offer offer = generator.nextBid();
                Bid generatedBid = ((GeniusOffer) offer).toGenius(utilitySpace.getDomain());
                Action action;
                System.out.println(getUtility(generatedBid));
                if (getUtility(generatedBid) < getUtility(((GeniusOffer) handler.getLastMove()).toGenius(utilitySpace.getDomain()))) {
                    action = new Accept(getAgentID(),((GeniusOffer) handler.getLastMove()).toGenius(utilitySpace.getDomain()));
                } else if (getUtility(generatedBid) < 0.0) {
                    action = new Reject(getAgentID(), generatedBid);
                } else {
                    action = new negotiator.actions.Offer(getAgentID(),generatedBid);
                    List<Issue> domain = utilitySpace.getDomain().getIssues();
                    handler.store(GeniusOffer.offerFromGenius(GeniusDomainAdapter.convertFromGenius(domain),action,0,handler.getHistory().size()==0?0:handler.getLastMove().getRound()+1, CommunicativeAct.PROPOSE));
                }
                ExperimentsConstants.DEBUG_CTR ++;
                return action;
            } catch (Exception e) {
                System.err.println("Could not generate a bid");
                e.printStackTrace();
                return null;
            }
        }
    }
}

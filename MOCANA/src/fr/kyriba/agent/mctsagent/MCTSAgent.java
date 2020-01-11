package fr.kyriba.agent.mctsagent;


import fr.kyriba.ExperimentsConstants;
import fr.kyriba.agent.Agent;
import fr.kyriba.connectors.geniusconnectors.*;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.protocol.CommunicativeAct;
import fr.kyriba.protocol.Offer;
import fr.kyriba.protocol.ProtocolDescriptor;
import fr.kyriba.bidgeneration.MCTSBidGenerator;
import fr.kyriba.history.HistoryListHandler;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Reject;
import negotiator.issue.*;
import negotiator.utility.NonlinearUtilitySpace;

import java.util.*;

import static fr.kyriba.ExperimentsConstants.SEED;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**Classe agent de MCTS. étend la classe Agent de GENIUS (faire une encapsulation ?). 2 comportements à gérer: que faire quand on reçoit un message ? Que faire quand on nou demande d'agir ?*/
public class MCTSAgent extends negotiator.Agent implements Agent {

    protected HistoryHandler handler;
    protected double alpha;
    protected int nbSamplesGaussianClassification;
    protected double time;

    public Offer getBestSoFar() {
        return bestSoFar;
    }

    protected Offer bestSoFar;

    public MCTSAgent(){
        super();
        ExperimentsConstants.DEBUG_CTR = 0;
        alpha = ExperimentsConstants.ALPHA;
        handler = new HistoryListHandler();
        ExperimentsConstants.RANDOM = new Random(SEED);
        nbSamplesGaussianClassification = ExperimentsConstants.NBSAMPLESGAUSSIAN;
    }

    public void init(){
        super.init();
        System.out.println(utilitySpace);
    }

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
            MCTSBidGenerator generator = new MCTSBidGenerator(handler, alpha,GeniusDomainAdapter.convertFromGenius(utilitySpace.getDomain().getIssues()), this, new ProtocolDescriptor(2, this), nbSamplesGaussianClassification, ExperimentsConstants.SIMULATION_TIME_LIMIT);//todo change the first argument
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

    @Override
    //réception du message: stockage dans l'historique
    public void ReceiveMessage(Action action){
        List<Issue> domain = utilitySpace.getDomain().getIssues();
        if(action instanceof negotiator.actions.Offer){
            handler.store(GeniusOffer.offerFromGenius(GeniusDomainAdapter.convertFromGenius(domain),action,1,handler.getHistory().size()==0?0:handler.getLastMove().getRound()+1, CommunicativeAct.PROPOSE));
            if(bestSoFar==null||utilitySpace.getUtility(((GeniusOffer)bestSoFar).toGenius(utilitySpace.getDomain()))<utilitySpace.getUtility(((negotiator.actions.Offer) action).getBid())){
                bestSoFar=handler.getLastMove();
            }
            time=timeline.getTime();
        }
    }

    public double getTime() {
        return time;
    }
}
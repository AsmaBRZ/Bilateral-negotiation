package fr.kyriba.bidgeneration;

import fr.kyriba.*;
import fr.kyriba.bidgeneration.MCTS.MCTNode;
import fr.kyriba.bidgeneration.MCTS.MCTree;
import fr.kyriba.agent.mctsagent.MCTSAgent;
import fr.kyriba.bidgeneration.strategymodeling.StrategyModeler;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.*;
import fr.kyriba.connectors.geniusconnectors.GeniusDiscreteAttribute;
import fr.kyriba.connectors.geniusconnectors.GeniusOffer;
import fr.kyriba.connectors.geniusconnectors.GeniusQuantitativeAttribute;
import fr.kyriba.bidgeneration.strategymodeling.GaussianStrategyModeler;
import fr.kyriba.bidgeneration.strategymodeling.kernel.RationalQuadraticKernel;
import fr.kyriba.bidgeneration.utilitymodeling.BayesianUtilityModeler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class MCTSBidGenerator {
/**Stratégie MCTS de l'agent*/
    private final HistoryHandler handler;

    private MCTree tree;
    private final double alpha;//∈[0,1]
    private final NegotiationDomain domain;
    private final MCTSAgent myAgent;
    private final ProtocolDescriptor protocol;
    private List<BayesianUtilityModeler> utilityModelers;
    private HistoryHandler history;
    private int nbSamplesGaussianClassification;
    private int nbSamples;
    private double timeLimit;
    private List<GaussianStrategyModeler> gaussianModelers;
    public static FileWriter writer;

    public MCTSBidGenerator(HistoryHandler handler, double alpha, NegotiationDomain domain, MCTSAgent myAgent, ProtocolDescriptor protocol, int nbSamplesGaussianClassification, double timeLimit) {
        this.alpha = alpha;
        this.domain = domain;
        this.myAgent = myAgent;
        this.protocol = protocol;
        this.utilityModelers = new ArrayList<>();
        for (int i = 0; i < protocol.getPlayerNumber(); i++) {
            utilityModelers.add(new BayesianUtilityModeler(i, domain, ExperimentsConstants.HYPOTHESIS_NUMBER, ExperimentsConstants.PROBA_LINEAR, ExperimentsConstants.SIGMA, ExperimentsConstants.BAYESIAN_ALPHA, handler));
        }
        this.history = handler;
        this.nbSamplesGaussianClassification = nbSamplesGaussianClassification;
        nbSamples = ExperimentsConstants.NB_SAMPLES;
        tree = new MCTree(new MCTNode(protocol.getPlayerNumber() - 1, (handler.getLastMove())), null);
        this.timeLimit = timeLimit;
        this.handler = handler;
        gaussianModelers = new ArrayList<>();
    }

    public MCTree getTree() {
        return tree;
    }

    public void setTree(MCTree tree) {
        this.tree = tree;
    }

    /**Générateur d'offre (fonction appelant les autres)*/
    public Offer nextBid() throws Exception {
        writer = new FileWriter("simu.txt");
        gaussianModelers = new ArrayList<>();
        for (int i = 0; i < protocol.getPlayerNumber(); i++) {
            gaussianModelers.add(new GaussianStrategyModeler(ExperimentsConstants.TIME_LIMIT, nbSamplesGaussianClassification, handler, new RationalQuadraticKernel(1.0)));
            gaussianModelers.get(i).optimizeKernel();
        }
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            if(tree==null){
                System.err.println("Tree should not be null");
            }
            runnables.add(new MCTSThread(tree,this,protocol, history));
        }
        ExecutorService execute = Executors.newFixedThreadPool(50);
        for(Runnable r : runnables){
            execute.execute(r);
        }
//        while(tree.getRoot().getVisited()<20000){
//            Thread.sleep((long) timeLimit);
//        }
        Thread.sleep((long)timeLimit);
        execute.shutdownNow();
        Thread.sleep(500);
        Offer nextMove = tree.getSubtrees().get(0).getRoot().getMove();
        writer.close();
        System.out.println("total visited: " + tree.getRoot().getVisited());
        System.out.println("subtrees: " + tree.getSubtrees().size());
        MCTree initialNode = tree.getSubtrees().get(0);
        double score = ExperimentsConstants.W*myAgent.utilitySpace.getUtility(((GeniusOffer)initialNode.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()))+(1.0-ExperimentsConstants.W)*initialNode.getRoot().getScore(0);
        for (MCTree node : tree.getSubtrees()) {
            double weightedScore = ExperimentsConstants.W*myAgent.utilitySpace.getUtility(((GeniusOffer)node.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()))+(1.0-ExperimentsConstants.W)*node.getRoot().getScore(0);
            if (weightedScore >= score) {
                score = weightedScore;
                nextMove = node.getRoot().getMove();
            }
        }
//        score-=0.1;

        File f = new File("branches.csv");
        FileWriter writer = new FileWriter(f);
        double finalScore = score;
//        Object[] moves = (tree.getSubtrees().stream().filter(t->t.getRoot().getScore(0)>= finalScore)).sorted((o1, o2) -> {
//            double value1, value2;
//            if(o1.getRoot().getScore(1)>myAgent.getUtility(((GeniusOffer)o1.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()))) {
//                value1 = myAgent.getUtility(((GeniusOffer) o1.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()));
//            }
//            else{
//                value1=o1.getRoot().getScore(1)+2*myAgent.getUtility(((GeniusOffer)o1.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()));
//            }
//            if(o2.getRoot().getScore(1)>myAgent.getUtility(((GeniusOffer)o2.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()))){
//                value2 = myAgent.getUtility(((GeniusOffer)o2.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()));
//
//            }
//            else{
//                value2=o2.getRoot().getScore(1)+2*myAgent.getUtility(((GeniusOffer)o2.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain()));
//            }
//            double value = value1-value2;
//            return value<0?1:value==0?0:-1;
//        }).toArray();
//        nextMove = ((MCTree) moves[0]).getRoot().getMove();
        if(myAgent.getUtility(((GeniusOffer)nextMove).toGenius(myAgent.utilitySpace.getDomain()))<=0.5){
            System.err.println("Nouvelle technique : on passe pour des cons, les autres se marrent, et on frappe. C’est nouveau.");
        }
        writer.write("Move;value;visited;score\n");
        for(MCTree child: tree.getSubtrees()) {
            writer.write(child.getRoot().getMove() + ";" + child.getRoot().getScore(0) + ";" + child.getRoot().getVisited() + ";" + child.getRoot().getScore(0) + "\n");
        }
        System.out.println(nextMove);
        writer.write("\n\n");
        writer.close();
        if(myAgent.utilitySpace.getUtility(((GeniusOffer)nextMove).toGenius(myAgent.utilitySpace.getDomain()))<myAgent.utilitySpace.getUtility(((GeniusOffer)handler.getLastMove()).toGenius(myAgent.utilitySpace.getDomain()))){
            for(MCTree t:tree.getSubtrees()){
                System.out.println(myAgent.utilitySpace.getUtility(((GeniusOffer)t.getRoot().getMove()).toGenius(myAgent.utilitySpace.getDomain())));
            }
        }
        return nextMove;
    }
/**simulation MCTS*/
    public Offer simulate(MCTree myTree) {
        CommunicativeAct act = myTree.getRoot().getMove().getAct();
        int player = myTree.getRoot().getPlayer();
        Offer offer = myTree.getRoot().getMove();
        for (int i = 0; i < protocol.getPlayerNumber(); i++) {
            HistoryHandler handler = new HistoryListHandler();
            int finalI = i;
            handler.storeAll(history.getHistory().stream().filter(p -> p.getPlayer() == finalI).collect(Collectors.toList()));
        }
        Offer previousOffer = offer;
        int currentPlayer = protocol.nextPlayer(player);
        List<StrategyModeler> modelers = new ArrayList<>();
        for(int i =0; i<protocol.getPlayerNumber(); i++){
            modelers.add(gaussianModelers.get(i).generateModeler(myTree.getRoot().getMove()));
        }
        while (act.equals(CommunicativeAct.PROPOSE)) {
            NegotiationDomain domain = myTree.getRoot().getMove().getDomain();
            Map<Integer, Object> o = modelers.get(currentPlayer).generateOffer();
            Offer computedOffer = new GeniusOffer(domain, o, protocol.nextPlayer(currentPlayer), previousOffer.getRound() + 1, CommunicativeAct.PROPOSE);
            Offer definitiveOffer;
            if (utilityModelers.get(currentPlayer).estimateUtility(history, myAgent.getTime()).apply(computedOffer) < utilityModelers.get(currentPlayer).estimateUtility(history,myAgent.getTime()).apply(previousOffer)) {
                definitiveOffer = new GeniusOffer(domain, previousOffer.getValues(), protocol.nextPlayer(currentPlayer), previousOffer.getRound() + 1, CommunicativeAct.ACCEPT);
            } else if (utilityModelers.get(currentPlayer).estimateUtility(history, myAgent.getTime()).apply(computedOffer) <= 0) {
                definitiveOffer = new GeniusOffer(domain, new HashMap<>(), protocol.nextPlayer(currentPlayer), previousOffer.getRound() + 1, CommunicativeAct.REJECT);
            } else {
                definitiveOffer = computedOffer;
            }
            if(definitiveOffer.getRound()>=ExperimentsConstants.ROUND_MAX_SIM){
                definitiveOffer = new GeniusOffer(domain, new HashMap<>(), protocol.nextPlayer(currentPlayer), previousOffer.getRound() + 1, CommunicativeAct.REJECT);
            }
            previousOffer = definitiveOffer;
            act = previousOffer.getAct();
            currentPlayer = protocol.nextPlayer(currentPlayer);
        }
        return previousOffer;
    }

    private static double upperConfidence(MCTNode node, int player, int n){
        return node.getScore(player)/((double)node.getVisited())+ExperimentsConstants.UCB_C*Math.sqrt(Math.log((double) n)/((double)node.getVisited()));
    }

    //tree must have at least one child
    /**sélection MCTS*/
    public static MCTree select(MCTree tree) {
        if (tree.getSubtrees().size() == 0) {
            throw new IllegalArgumentException("This tree has no child, and should at least have one");
        }
        int currentPlayer = tree.getRoot().getPlayer();
        MCTree selectedChild = tree.getSubtrees().get(0);
        double score = upperConfidence(tree.getSubtrees().get(0).getRoot(),currentPlayer, tree.getRoot().getVisited());
        for (MCTree child : tree.getSubtrees()) {
            double ucb = upperConfidence(child.getRoot(), currentPlayer, tree.getRoot().getVisited());
            if (ucb > score) {
                score = ucb;
                selectedChild = child;
            }
        }
        return selectedChild;
    }
/**Vérifie s'il est nécessaire de faire une extension lors de la sélection*/
    public boolean needsExtension(MCTree node) {
            return Math.pow(node.getRoot().getVisited(), alpha) >= node.getSubtrees().size();
    }
/**Extension MCTS*/
    public synchronized MCTree extend(MCTree tree) throws Exception {
        List<Double> scores = new ArrayList<>();
        for(int i = 0; i<protocol.getPlayerNumber(); i++){
            scores.add(0.0);
        }
        Offer offer;
        do {HashMap<Integer, Object> values = new HashMap<>();
        for (Attribute attribute : domain.getAttributes()) {
            if (attribute instanceof DiscreteAttribute) {
                GeniusDiscreteAttribute discreteAttribute = (GeniusDiscreteAttribute) attribute;
                int optionIndex = ExperimentsConstants.RANDOM.nextInt(discreteAttribute.getValues().size());
                values.put(discreteAttribute.getNumber(), discreteAttribute.getValues().get(optionIndex));
            } else if (attribute instanceof GeniusQuantitativeAttribute) {
                GeniusQuantitativeAttribute quantitativeAttribute = (GeniusQuantitativeAttribute) attribute;
                double value = quantitativeAttribute.getMin() + (ExperimentsConstants.RANDOM.nextDouble()) * (quantitativeAttribute.getMax() - quantitativeAttribute.getMin());
                values.put(quantitativeAttribute.getNumber(), value);
            } else {
                throw new Exception("Unknown issue type");
            }
        }
        offer = new GeniusOffer(domain, values, 0, history.getLastMove().getRound(), CommunicativeAct.PROPOSE);}
        while(myAgent.utilitySpace.getUtility(((GeniusOffer)offer).toGenius(myAgent.utilitySpace.getDomain()))<myAgent.utilitySpace.getUtility(((GeniusOffer)myAgent.getBestSoFar()).toGenius(myAgent.utilitySpace.getDomain())));
//        while(myAgent.utilitySpace.getUtility(((GeniusOffer)offer).toGenius(myAgent.utilitySpace.getDomain()))<0.8);
        MCTNode node = new MCTNode(tree.getRoot().getPlayer() + 1 >= protocol.getPlayerNumber() ? 0 : tree.getRoot().getPlayer() + 1, offer);
        node.setScores(scores);
        MCTree child = new MCTree(node, tree);
        tree.getSubtrees().add(child);
        return child;
    }
/**Rétropropagation MCTS*/
    public synchronized void backpropagate(MCTree tree, List<Double> utilities) {
        List<Double> previousUtilities = tree.getRoot().getScores();
        int nbVisited = tree.getRoot().getVisited();
        tree.getRoot().setVisited(nbVisited + nbSamples);
        for (int i = 0; i < previousUtilities.size(); i++) {
            previousUtilities.set(i, (previousUtilities.get(i) * nbVisited + utilities.get(i) * nbSamples) / (nbSamples + nbVisited));
        }
        if (tree.getParent() != null) {
            backpropagate(tree.getParent(), utilities);
        }
    }

    List<BayesianUtilityModeler> getUtilityModelers() {
        return utilityModelers;
    }

    public double getTime(){
        return myAgent.getTime();
    }

}

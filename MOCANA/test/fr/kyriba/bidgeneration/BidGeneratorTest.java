package fr.kyriba.bidgeneration;

import fr.kyriba.ExperimentsConstants;
import fr.kyriba.agent.mctsagent.MCTSAgent;
import fr.kyriba.bidgeneration.MCTS.MCTNode;
import fr.kyriba.bidgeneration.MCTS.MCTree;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.*;
import fr.kyriba.connectors.geniusconnectors.GeniusDiscreteAttribute;
import fr.kyriba.connectors.geniusconnectors.GeniusDomainAdapter;
import fr.kyriba.connectors.geniusconnectors.GeniusOffer;
import fr.kyriba.connectors.geniusconnectors.GeniusQuantitativeAttribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 03/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
@RunWith(MockitoJUnitRunner.class)
public class BidGeneratorTest {

    @Mock
    NegotiationDomain domain;
    MCTree initTree;
    MCTSBidGenerator generator;

    @Before
    public void initialize(){

        //mocking domain
        ExperimentsConstants.RANDOM = new Random(ExperimentsConstants.SEED);
        ArrayList<Attribute> issues = new ArrayList<>();
        GeniusDiscreteAttribute attributeDiscrete = new GeniusDiscreteAttribute(0,"discrete", new String[]{"foo", "bar"});
        GeniusQuantitativeAttribute attributeInteger = new GeniusQuantitativeAttribute(1,"integer",0.0, 42.0, false);
        GeniusQuantitativeAttribute attributeReal = new GeniusQuantitativeAttribute(2,"integer",12.0, 24.0, true);

        ((GeniusDomainAdapter)domain).addAttribute(attributeDiscrete);
        ((GeniusDomainAdapter)domain).addAttribute(attributeInteger);
        ((GeniusDomainAdapter)domain).addAttribute(attributeReal);
        HashMap<Integer, Object> values = new HashMap<>();
        values.put(0,"foo");
        values.put(1,12);
        values.put(2,15.7);
        GeniusOffer rootOffer = new GeniusOffer(domain, values, 0, 1, CommunicativeAct.PROPOSE);
        MCTNode rootNode = new MCTNode(0,rootOffer);
        List<Double> scores = new ArrayList<>();
        scores.add(0.6);
        scores.add(0.2);
        rootNode.setScores(scores);
        rootNode.setVisited(4);
        initTree = new MCTree(rootNode, null);

        values = new HashMap<>();
        values.put(0,"foo");
        values.put(1,11);
        values.put(2,14.7);
        Offer leftOffer = new GeniusOffer(domain, values, 1, 2, CommunicativeAct.PROPOSE);
        MCTNode leftNode = new MCTNode(1,leftOffer);
        scores = new ArrayList<>();
        scores.add(0.4);
        scores.add(0.5);
        leftNode.setScores(scores);
        leftNode.setVisited(1);
        MCTree leftTree = new MCTree(leftNode, initTree);
        initTree.addSubtree(leftTree);

        values = new HashMap<>();
        values.put(0,"foo");
        values.put(1,38);
        values.put(2,12.2);
        Offer middleOffer = new GeniusOffer(domain, values, 1, 2, CommunicativeAct.PROPOSE);
        MCTNode middleNode = new MCTNode(1,middleOffer);
        scores = new ArrayList<>();
        scores.add(0.6);
        scores.add(0.2);
        middleNode.setScores(scores);
        middleNode.setVisited(1);
        MCTree middleTree = new MCTree(middleNode, initTree);
        initTree.addSubtree(middleTree);

        values = new HashMap<>();
        values.put(0,"bar");
        values.put(1,34);
        values.put(2,19.6);
        Offer rightOffer = new GeniusOffer(domain, values, 1, 2, CommunicativeAct.PROPOSE);
        MCTNode rightNode = new MCTNode(1,rightOffer);
        scores = new ArrayList<>();
        scores.add(0.7);
        scores.add(0.05);
        rightNode.setScores(scores);
        rightNode.setVisited(1);
        MCTree rightTree = new MCTree(rightNode, initTree);
        initTree.addSubtree(rightTree);
        HistoryHandler handler = new HistoryListHandler();
        handler.store(rootOffer);
        handler.store(rightOffer);
        handler.store(new GeniusOffer(domain, values, 0, 2, CommunicativeAct.PROPOSE));
        generator = new MCTSBidGenerator(handler, 0.5, domain, new MCTSAgent(), new ProtocolDescriptor(2, new MCTSAgent()),  100,ExperimentsConstants.SIMULATION_TIME_LIMIT);
        generator.setTree(initTree);
    }

    @Test
    public void testSelect(){
        MCTree selected = generator.select(initTree);
        Assert.assertEquals(selected,initTree.getSubtrees().get(2));

    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionSelect(){
        generator.select(initTree.getSubtrees().get(0));
    }

    @Test
    public void testNeedExtension(){
        Assert.assertFalse(generator.needsExtension(initTree));
        Assert.assertTrue(generator.needsExtension(initTree.getSubtrees().get(0)));
    }

    @Test
    public void testExtension(){
        try {
            generator.extend(initTree.getSubtrees().get(2));
        } catch (Exception e) {
            fail();
        }
        Assert.assertTrue(initTree.getSubtrees().get(2).getSubtrees().size() == 1);
    }

    @Test
    public void testSimulation(){
        HashMap<Integer, Object> values = new HashMap<>();
        values.put(0,"bar");
        values.put(1,34);
        values.put(2,19.6);
        Offer rightOffer = new GeniusOffer(domain, values, 0, 2, CommunicativeAct.PROPOSE);
        MCTNode rightNode = new MCTNode(1,rightOffer);
        List<Double> scores = new ArrayList<>();
        scores.add(0.7);
        scores.add(0.05);
        rightNode.setScores(scores);
        rightNode.setVisited(1);
        MCTree rightTree = new MCTree(rightNode, initTree);
        initTree.getSubtrees().get(0).addSubtree(rightTree);
        Offer offer = generator.simulate(rightTree);
        Assert.assertEquals(offer.getAct(), CommunicativeAct.ACCEPT);
        Assert.assertEquals(offer.getValue(0), "foo");
        Assert.assertEquals((Double)offer.getValue(1),34.0,0.001);
        Assert.assertEquals((Double)offer.getValue(2),19.6,0.001);
    }

    @Test
    public void testBackpropagate() {
        List<Double> utilities = new ArrayList<>();
        utilities.add(2.0);
        utilities.add(1.0);
        generator.backpropagate(initTree.getSubtrees().get(0), utilities);
        Assert.assertEquals(initTree.getRoot().getScore(0),1.9461538462, 0.0001);
        Assert.assertEquals(initTree.getRoot().getScore(1),0.9692307692, 0.0001);
        Assert.assertEquals(initTree.getRoot().getVisited(), 104);
    }

    @Test
    public void testNextBid(){
        try {
            System.out.println(generator.nextBid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package fr.kyriba;

import fr.kyriba.connectors.geniusconnectors.*;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.*;
import fr.kyriba.bidgeneration.utilitymodeling.BayesianUtilityModeler;
import fr.kyriba.bidgeneration.utilitymodeling.Hypothesis;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Function;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 09/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class BayesianModelerTest {
    BayesianUtilityModeler modeler;
    NegotiationDomain domain;
    HistoryHandler history;
    @Mock
    Hypothesis hypo;

    @Before
    public void init(){
        history = new HistoryListHandler();

        //mocking domain
        ArrayList<GeniusAttribute> issues = new ArrayList<>();
//        GeniusDiscreteAttribute attributeDiscrete = new GeniusDiscreteAttribute(0,"discrete", new String[]{"foo", "bar"});
//        GeniusQuantitativeAttribute attributeInteger = new GeniusQuantitativeAttribute(1,"integer",0.0, 42.0, false);
        GeniusQuantitativeAttribute attributeReal = new GeniusQuantitativeAttribute(0,"real",0.0, 100.0, true);
//        issues.add(attributeDiscrete);
//        issues.add(attributeInteger);
        issues.add(attributeReal);
        domain = new GeniusDomainAdapter(issues);

        Map<Integer, Object> values = new HashMap<>();
        values.put(0,30.);
        Offer offer = new GeniusOffer(domain, values,0,0,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,30.);
        offer = new GeniusOffer(domain, values,1,1,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,75.);
        offer = new GeniusOffer(domain, values,0,2,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,75.);
        offer = new GeniusOffer(domain, values,1,3,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,23.);
        offer = new GeniusOffer(domain, values,0,4,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,23.);
        offer = new GeniusOffer(domain, values,1,5,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,80.);
        offer = new GeniusOffer(domain, values,0,6,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,80.);
        offer = new GeniusOffer(domain, values,1,7,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,42.);
        offer = new GeniusOffer(domain, values,0,8,CommunicativeAct.PROPOSE);
        history.store(offer);

        values = new HashMap<>();
        values.put(0,42.);
        offer = new GeniusOffer(domain, values,1,9,CommunicativeAct.PROPOSE);
        history.store(offer);

        ExperimentsConstants.RANDOM = new Random(0);
        modeler = new BayesianUtilityModeler(0, domain, 10, 0.15, 1, 0.05, history);

//        hypo = Mockito.mock(Hypothesis.class);
//        Mockito.when(hypo.computeUtility(Mockito.any(Offer.class))).thenReturn(1.0);
    }

    @Test
    public void generationTest(){
//        ExperimentsConstants.RANDOM = new Random(0);
        Function<Offer,Double> model = modeler.estimateUtility(history,10);
//        List<Hypothesis> hypothesises = modeler.generateHypothesis();
//        for(Hypothesis h:hypothesises){
//            double weightSum=0;
//            for(double weight: h.getWeights().values()){
//                Assert.assertTrue(weight<=1);
//                weightSum+=weight;
//            }
//            Assert.assertEquals(weightSum, 1.0, 0.001);
//            for(int i =0; i<domain.getAttributes().size(); i++){
//                Function<Object, Double> eval = h.getEvaluators().get(i);
//                if(domain.getAttributes().get(i) instanceof DiscreteAttribute){
//                    double valSum = 0.0;
//                    for(String s: ((DiscreteAttribute) domain.getAttributes().get(i)).getValues()){
//                        Assert.assertTrue(eval.apply(s)<=1);
//                        valSum+=eval.apply(s);
//                    }
//                    Assert.assertEquals(valSum, 1, 0.01);
//                }
//                else{
//                    if(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMin())<0.5) {
//                        Assert.assertEquals(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMin()), 0,0.01);
//                    }
//                    else{
//                        Assert.assertEquals(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMin()), 1, 0.01);
//                    }
//                    if(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMax())<0.5) {
//                        Assert.assertEquals(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMax()), 0,0.01);
//                    }
//                    else{
//                        Assert.assertEquals(eval.apply(((QuantitativeAttribute)domain.getAttributes().get(i)).getMax()), 1, 0.01);
//                    }
//                }
//            }
//        }
        System.out.println("foo");
    }

    @Test
    public void probaTest(){
        Assert.assertEquals(modeler.computeProba(history.getMove(0), hypo, 0.95), 0.407, 0.001);
    }
}

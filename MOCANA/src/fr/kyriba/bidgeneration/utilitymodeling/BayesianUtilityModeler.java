package fr.kyriba.bidgeneration.utilitymodeling;

import fr.kyriba.history.HistoryHandler;
import fr.kyriba.protocol.CommunicativeAct;
import fr.kyriba.protocol.NegotiationDomain;
import fr.kyriba.protocol.Offer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Crp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class BayesianUtilityModeler {
    private int player;
    private NegotiationDomain domain;
    private int hypothesisNumber;
    private double probaLinear;
    private double sigma;
    private double alpha;
    private HistoryHandler history;
    private List<Double> probabilities;
    private List<Hypothesis> hypos;

    public BayesianUtilityModeler(int player, NegotiationDomain domain, int hypothesisNumber, double probaLinear, double sigma, double alpha, HistoryHandler history) {
        this.player = player;
        this.domain = domain;
        this.hypothesisNumber = hypothesisNumber;
        this.probaLinear = probaLinear;
        this.sigma = sigma;
        this.alpha = alpha;
        this.history = history;
        probabilities = new ArrayList<>();
        for(int i = 0; i<hypothesisNumber; i++){
            probabilities.add(1.0/((double)hypothesisNumber));
        }
        hypos = generateHypothesis();
    }

    public Function<Offer, Double> estimateUtility(HistoryHandler handler, double time){//TODO proba à mettre à jour une unique fois.
        history = handler;
        List<Hypothesis> hypothesises = hypos;
        try {
            for (Offer offer : history.getHistory()) {
                double sum = 0;
                if (offer.getPlayer() == player) {
                    List<Double> previousProba = new ArrayList<>();
                    previousProba.addAll(probabilities);
                    for (int i = 0; i < hypothesises.size(); i++) {
                        Hypothesis h = hypothesises.get(i);
                        probabilities.set(i, computeProba(offer, h, time) * previousProba.get(i));
                    }
                    for (int i = 0; i < hypothesises.size(); i++) {
                        sum += probabilities.get(i);
                    }
                    for (int i = 0; i < hypothesises.size(); i++) {
                        probabilities.set(i, probabilities.get(i) / sum);
                    }
                }
            }
        }
        catch(ConcurrentModificationException e){//Case: time elapsed. The thread needs to be killed.
            Thread.currentThread().interrupt();
        }
        return b -> {
            if(b.getAct().equals(CommunicativeAct.REJECT)){
                return 0.0;
            }
            BigDecimal sbis = new BigDecimal(0.0);
            BigDecimal p = new BigDecimal(0.0);
            double total = 0.0;
            final int n = hypothesises.get(0).getDomain().getAttributes().size();

            for(int j = 0; j<hypothesises.size() ; j++){
                double d = 0;
                for (int i = 0; i<n; i++){
                    int index = hypothesises.get(0).getDomain().getAttributes().get(i).getNumber();
                    Object o = b.getValue(index);
                    if(!((Double) (hypothesises.get(j).getWeights().get(index) * hypothesises.get(j).getEvaluators().get(index).apply(o))).equals(Double.NaN)){
                        d+=hypothesises.get(j).getWeights().get(index) * hypothesises.get(j).getEvaluators().get(index).apply(o);
                    }
                }
                total += d;
                p = p.add(new BigDecimal(probabilities.get(j)), new MathContext(50, RoundingMode.HALF_DOWN));
                sbis = sbis.add(new BigDecimal(probabilities.get(j)).multiply(new BigDecimal(d)));
            }
            return sbis.doubleValue();
        };
    }

    public List<Hypothesis> generateHypothesis(){
        List<Hypothesis> hypothesis = new ArrayList<>();
        for(int i = 0; i<hypothesisNumber; i++){
            hypothesis.add(Hypothesis.generateHypothesis(domain, probaLinear));
        }
        return hypothesis;
    }

    public double computeProba(Offer offer, Hypothesis hypothesis, double time){//TODO test
        return 1.0/(sigma*Math.sqrt(2.0*Math.PI))*Math.exp(-Math.pow(hypothesis.computeUtility(offer)-(1.0-/*TODO modifié: time*/alpha*((double) offer.getRound()+1)), 2.0)/(2.0*Math.pow(sigma,2.0)));
    }
}

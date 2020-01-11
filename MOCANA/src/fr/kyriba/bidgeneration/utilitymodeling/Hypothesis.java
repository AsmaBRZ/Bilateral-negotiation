package fr.kyriba.bidgeneration.utilitymodeling;

import fr.kyriba.ExperimentsConstants;
import fr.kyriba.protocol.*;

import java.util.*;
import java.util.function.Function;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 05/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class Hypothesis {
    private NegotiationDomain domain;

    public void setWeights(Map<Integer,Double> weights) {
        this.weights = weights;
    }

    private Map<Integer,Function<Object, Double>> evaluators;
    private Map<Integer,Double> weights;

    private Hypothesis(NegotiationDomain domain){
        this.domain = domain;
    }

    public NegotiationDomain getDomain() {
        return domain;
    }

    public Map<Integer, Double> getWeights() {
        return weights;
    }

    public static Hypothesis generateHypothesis(NegotiationDomain domain, double probaLinear){
        Map<Integer,Function<Object, Double>> theEvaluators = new HashMap<>();
        if(probaLinear<0 || probaLinear>1) {
            throw new IllegalArgumentException("probaLinear should be a probability between 0 and 1. Received " + probaLinear);
        }
        Map<Integer,Double> weights = new HashMap<>();
        Hypothesis hypothesis = new Hypothesis(domain);
        //Generate the values
        for(Attribute attribute:domain.getAttributes()){
            if(attribute instanceof DiscreteAttribute){
                DiscreteAttribute discreteAttribute = (DiscreteAttribute) attribute;
                List<Double> options = generateNormalizedListOfReal(discreteAttribute.getValues().size());
                theEvaluators.put(attribute.getNumber(),(s) -> {
                    if(! (s instanceof String) || discreteAttribute.getValues().indexOf(s)==-1){
                        throw new IllegalArgumentException("Evaluator of a discrete attribute called on something other than a registered value: " + s);
                    }
                    Double d = options.get(discreteAttribute.getValues().indexOf(s));
                    return d;
                });
            }
            else if(attribute instanceof QuantitativeAttribute){
                QuantitativeAttribute quantitativeAttribute = (QuantitativeAttribute) attribute;
                double gen = ExperimentsConstants.RANDOM.nextDouble();
                if (gen<probaLinear){
                    if(ExperimentsConstants.RANDOM.nextBoolean()){
                        theEvaluators.put(attribute.getNumber(),(d) -> {
                            double val;
                            if(d instanceof Integer){
                                val = ((Integer) d).doubleValue();
                            }
                            else
                                val = (double) d;
                            checkQuantitativeValidity(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), val);
                            Double toReturn = increasing(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), val);
                            return toReturn;
                            });
                        }
                    else{
                        theEvaluators.put(attribute.getNumber(),(d) -> {
                            double val;
                            if(d instanceof Integer){
                                val = ((Integer) d).doubleValue();
                            }
                            else
                                val = (double) d;
                            checkQuantitativeValidity(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), val);
                            Double toReturn = decreasing(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), val);
                            return toReturn;
                        });
                    }
                }
                else{
                    double peak = quantitativeAttribute.getMin() + ExperimentsConstants.RANDOM.nextDouble()*(quantitativeAttribute.getMax()-quantitativeAttribute.getMin());
                    theEvaluators.put(attribute.getNumber(),(d) ->{
                        double val;
                        if(d instanceof Integer){
                            val = (Integer) d;
                        }
                        else if(d instanceof Double) {
                            val = (double) d;
                        }
                        else
                            throw new IllegalArgumentException("I have received a so called 'quantitative value' that is not an integer nor a real. I put it here: " + d);
                        checkQuantitativeValidity(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), val);
                        return triangle(quantitativeAttribute.getMin(), quantitativeAttribute.getMax(), peak, val);
                    });
                }
            }
            else
                throw new IllegalArgumentException();
        }
        hypothesis.setEvaluators(theEvaluators);

        //Generate the weights
        int weightSize = domain.getAttributes().size();
        List<Integer> ranks = new ArrayList<>();
        for(Attribute attribute: domain.getAttributes()){
            int i = attribute.getNumber();
            ranks.add(i);
        }
        java.util.Collections.shuffle(ranks, ExperimentsConstants.RANDOM);
        int j = 0;
        for(Attribute attribute: domain.getAttributes()){
            int i = attribute.getNumber();
            weights.put(i,2.0*((double)ranks.get(j++))/(((double) weightSize+1)*((double)weightSize)));
        }
//        weights = generateNormalizedListOfReal(weightSize);
        hypothesis.setWeights(weights);

        return hypothesis;
    }

    public static double increasing(double min, double max, double x){
        if(x>max || x<min){
            throw new IllegalArgumentException("The required value is " + x + " should be between " + min + " and " + max);
        }
        return (x-min)/(max-min);
    }

    public static double decreasing(double min, double max, double x){
        if(x>max || x<min){
            throw new IllegalArgumentException("The required value is " + x + " should be between " + min + " and " + max);
        }
        return 1-increasing(min, max, x);
    }

    public static double triangle(double min, double max, double peak, double x){
        if(x>max || x<min){
            throw new IllegalArgumentException("The required value is " + x + " should be between " + min + " and " + max);
        }
        if(x<peak){
            return increasing(min, peak, x);
        }
        else{
            return decreasing(peak, max, x);
        }
    }

    public Map<Integer,Function<Object, Double>> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(Map<Integer,Function<Object, Double>> evaluators) {
        this.evaluators = evaluators;
    }

    public static List<Double> generateNormalizedListOfReal(int size){
        List<Double> doubles = new ArrayList<>();
        double total = 0;
        for(int i = 0; i<size; i++){
            double weight = ExperimentsConstants.RANDOM.nextDouble();
            doubles.add(weight);
            total += weight;
        }
        for(int i = 0; i<size; i++){
            doubles.set(i, doubles.get(i)/total);
        }
        return doubles;
    }

    private static void checkQuantitativeValidity(double min, double max, double x) {
        if (x < min || x > max) {
            throw new IllegalArgumentException("Trying to evaluate a value out of range. min = " +
                    min + " max = " + max +
                    ". Reveived: " + x);
        }
    }

    public double computeUtility(Offer offer){
        double utility = 0.0;
        if(offer.getAct()!=CommunicativeAct.REJECT) {
            for (Attribute attribute : domain.getAttributes()) {
                int i = attribute.getNumber();
                utility += weights.get(i) * evaluators.get(i).apply(offer.getValue(i));
            }
        }
        return utility;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("Hypothesis: ");
        for (Attribute a: domain.getAttributes()){
            int i = a.getNumber();
            builder.append("attribute no " + i + "\tweight: " + weights.get(i) + "\ttype:");
            if(domain.getAttributeByNumber(i) instanceof DiscreteAttribute) {
                builder.append("discrete" + "\tvalues:\n");
                for(String s: ((DiscreteAttribute) domain.getAttributes().get(i)).getValues()){
                    builder.append(s + ":\t" + evaluators.get(i).apply(s)+"\n");
                }
                builder.append("\n\n");
            }
            else{
                double epsilon = ((QuantitativeAttribute)domain.getAttributeByNumber(i)).getMax()/10.0e6;
                double optimal;
                double sizeInterval = ((QuantitativeAttribute)domain.getAttributeByNumber(i)).getMax();
                if(evaluators.get(i).apply(sizeInterval/2.0)>evaluators.get(i).apply(sizeInterval/2.0+epsilon)){
                    optimal = sizeInterval-sizeInterval/(2*evaluators.get(i).apply(sizeInterval/2.0));
                }
                else{
                    optimal = sizeInterval/(2*evaluators.get(i).apply(sizeInterval/2.0));
                }
                builder.append("quantitative" + "\tvalues:\tmin: " + evaluators.get(i).apply(((QuantitativeAttribute)domain.getAttributeByNumber(i)).getMin())+"\tmax: " + evaluators.get(i).apply(((QuantitativeAttribute) domain.getAttributeByNumber(i)).getMax())+ "\t optimal: " + optimal);
            }
        }
        return builder.toString();
    }

    public static Hypothesis sumUp(List<Hypothesis> hypothesises, List<Double> probabilities) {
        Hypothesis sum = new Hypothesis(hypothesises.get(0).getDomain());
        final int n = hypothesises.get(0).getDomain().getAttributes().size();
        Map<Integer,Function<Object, Double>> evaluators = new HashMap<>();
        Map<Integer,Double> weights = new HashMap<>();
        for (int i = 0; i<n; i++){
            int finalI = i;
            int index = hypothesises.get(0).getDomain().getAttributes().get(i).getNumber();
            Function <Object, Double> mapper = o -> {
                double s = 0.0;
                for(int j = 0; j<hypothesises.size() ; j++){
                    if(hypothesises.get(j).getEvaluators().get(index).apply(o)!=Double.NaN)
                        s+=n*probabilities.get(j)*hypothesises.get(j).getWeights().get(index)*hypothesises.get(j).getEvaluators().get(index).apply(o);
                }
                return s;
            };
            evaluators.put(index, mapper);
            weights.put(index, 1.0/(double)n);
        }
        sum.setEvaluators(evaluators);
        sum.setWeights(weights);
        return sum;
    }
}

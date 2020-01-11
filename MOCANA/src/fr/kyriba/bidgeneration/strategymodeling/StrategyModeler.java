package fr.kyriba.bidgeneration.strategymodeling;

import fr.kyriba.bidgeneration.MCTSBidGenerator;
import fr.kyriba.protocol.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 03/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**Modélisateur d'offre complète - généré par le GaussianStrategyModeler*/
public class StrategyModeler {
    private Map<Integer,QuantitativeGenerator> quantitativeGaussians;
    private Map<Integer,QualitativeGenerator> qualitativeGaussians;
    private NegotiationDomain domain;

    public StrategyModeler(NegotiationDomain domain) {
        qualitativeGaussians = new HashMap<>();
        quantitativeGaussians = new HashMap<>();
        this.domain = domain;
    }

    public Map<Integer, QuantitativeGenerator> getQuantitativeGaussians() {
        return quantitativeGaussians;
    }

    public Map<Integer, QualitativeGenerator> getQualitativeGaussians() {
        return qualitativeGaussians;
    }

    public Map<Integer,Object> generateOffer(){
        Map<Integer,Object> values = new HashMap<>();
        for(Attribute a:domain.getAttributes()){
            int i = a.getNumber();
            if(qualitativeGaussians.get(i) != null) {
                values.put(i, qualitativeGaussians.get(i).generate());
            }
            else if(quantitativeGaussians.get(i) != null) {
                double value = quantitativeGaussians.get(i).generate();
                int num = 0;
                while (value < ((QuantitativeAttribute) domain.getAttributeByNumber(i)).getMin() || value > ((QuantitativeAttribute) domain.getAttributeByNumber(i)).getMax()) {
                    num++;
                    if(num>10000){
                        value = (((QuantitativeAttribute) domain.getAttributeByNumber(i)).getMin()+((QuantitativeAttribute) domain.getAttributeByNumber(i)).getMax())/2.0;
                    }
                    else {
                        value = quantitativeGaussians.get(i).generate();
                    }
                }
                values.put(i, value);
            }
            else{
                throw new IllegalStateException("Attribute number " + i + " is unknown");
            }
        }
        return values;
    }
}

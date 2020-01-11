package fr.kyriba.bidgeneration.strategymodeling;
/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2016-11-02  P-CBU   Initial version.                                         *
 ********************************************************************************/

import fr.kyriba.ExperimentsConstants;

import java.util.*;

/**
 * P-CBU created on 02/11/16.
 */
/**Génère un élément discret du domaine en fonction de la modélisation qui en a été faite.*/
public class QualitativeGenerator {
    Map<String,Double> probas;

    public QualitativeGenerator(Map<String, Double> probas) {
        this.probas = probas;
    }

    public String generate(){
        List<String> values = (Arrays.asList(probas.keySet().toArray(new String[0])));
        values.sort((o1, o2) -> o1.charAt(0)>o2.charAt(0)?1:o1.charAt(0)<o2.charAt(0)?-1:0);
        double proba = 0;
        Random random = ExperimentsConstants.RANDOM;
        double randomValue = random.nextDouble();
        for(String s: values){
            proba+=probas.get(s);
            if(randomValue<proba){
                return s;
            }
        }
        throw new IllegalStateException("Something went wrong; maybe the probas of this qualitative attribute do not sum to 1");
    }
}

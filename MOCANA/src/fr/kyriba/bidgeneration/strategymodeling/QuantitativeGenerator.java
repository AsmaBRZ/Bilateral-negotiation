package fr.kyriba.bidgeneration.strategymodeling;

import fr.kyriba.ExperimentsConstants;
import negotiator.issue.ValueReal;

import java.util.Random;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2016-11-02  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**Génère un élément continu du domaine en fonction de la modélisation qui en a été faite.*/
public class QuantitativeGenerator {
  private final double mean;
  private final double variance;

  public QuantitativeGenerator(double mean, double variance) {
    this.mean = mean;
    this.variance = variance;
  }

  public double getMean() {
    return mean;
  }

  public double getVariance() {
    return variance;
  }

  public double generate() {
    Random random = ExperimentsConstants.RANDOM;
    return random.nextGaussian()*variance+mean;
  }
}

package fr.kyriba;

import java.util.Random;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 08/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class ExperimentsConstants {
    //Utility Bayesian Modeling
    public static final int HYPOTHESIS_NUMBER = 200;
    public static final double SIGMA = 0.5;
    public static final double PROBA_LINEAR = 0.2;
    public static final double BAYESIAN_ALPHA = 0.05;


    public static final int NB_SAMPLES = 1;
    public static final double SIMULATION_TIME_LIMIT = 10000;
    public static final double ALPHA = 0.5;
    public static final int ROUND_MAX_SIM = 150;
    public static final double UCB_C = 1.0;
    public static final double W = 0.0;
    public static final double SIMULATION_TIME_LIMIT_OPPONENT = 240000;
    public static Random RANDOM;
    public static int SEED = 0;

    //Gaussian Strategy Modeling
    public static final double TIME_LIMIT = 10;
    public static final int NBSAMPLESGAUSSIAN = 100;

    public static int DEBUG_CTR;
}

package fr.kyriba.connectors.geniusconnectors;

import fr.kyriba.protocol.QuantitativeAttribute;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class GeniusQuantitativeAttribute extends GeniusAttribute implements QuantitativeAttribute {

    private final double min;
    private final double max;
    private boolean real;
    private String name;

    public GeniusQuantitativeAttribute(int number, String name, double min, double max, boolean real) {
        super(number);
        this.real = real;
        this.min = min;
        this.max = max;
        this.name = name;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    public boolean isReal() {
        return real;
    }

    public String getName(){
        return name;
    }
}

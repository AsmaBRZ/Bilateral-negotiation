package fr.kyriba.connectors.geniusconnectors;

import fr.kyriba.protocol.DiscreteAttribute;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;

import java.util.ArrayList;
import java.util.List;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class GeniusDiscreteAttribute extends GeniusAttribute implements DiscreteAttribute {
    private IssueDiscrete issue;

    public GeniusDiscreteAttribute(int number, String name, String[] values) {
        super(number);
        issue = new IssueDiscrete(name, getNumber(), values);
    }

    @Override
    public List<String> getValues() {
        List<String> list = new ArrayList<>();
        for(ValueDiscrete value:issue.getValues()){
            list.add(value.getValue());
        }
        return list;
    }
}

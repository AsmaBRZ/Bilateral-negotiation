package fr.kyriba.connectors.geniusconnectors;

import fr.kyriba.protocol.Attribute;
import fr.kyriba.protocol.NegotiationDomain;
import negotiator.issue.*;

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
public class GeniusDomainAdapter implements NegotiationDomain {

    private List<GeniusAttribute> attributes;

    @Override
    public List<? extends Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public Attribute getAttributeByNumber(int i) {
        for(Attribute a:attributes){
            if(a.getNumber() == i){
                return a;
            }
        }
        throw new IllegalArgumentException("No attribute with nb: " + i);
    }

    public GeniusDomainAdapter(){
        attributes = new ArrayList<>();
    }

    public GeniusDomainAdapter(List<GeniusAttribute> attributes){
        for(GeniusAttribute attribute: attributes){
            attribute.setNumber(attribute.getNumber());
        }
        this.attributes = attributes;
    }

    public static GeniusDomainAdapter convertFromGenius(List<Issue> domain) {
        List<GeniusAttribute> geniusAttributes = new ArrayList<>();
        for (Issue issue : domain) {
            if (issue instanceof IssueDiscrete) {
                String[] vals = new String[((IssueDiscrete) issue).getNumberOfValues()];
                for (int i = 0; i < ((IssueDiscrete) issue).getNumberOfValues(); i++) {
                    vals[i] = ((IssueDiscrete) issue).getValue(i).getValue();
                }
                geniusAttributes.add(new GeniusDiscreteAttribute(issue.getNumber(), issue.getName(), vals));
            } else if (issue instanceof IssueInteger || issue instanceof IssueReal) {
                double min;
                double max;
                boolean real;
                if (issue instanceof IssueInteger) {
                    min = (double) ((IssueInteger) issue).getLowerBound();
                    max = (double) ((IssueInteger) issue).getUpperBound();
                    real = false;
                } else {
                    min = ((IssueReal) issue).getLowerBound();
                    max = ((IssueReal) issue).getUpperBound();
                    real = true;
                }
                geniusAttributes.add(new GeniusQuantitativeAttribute(issue.getNumber(), issue.getName(), min, max, real));
            } else {
                throw new IllegalArgumentException("This issue " + issue + " is not handled by the system");
            }
        }
        return new GeniusDomainAdapter(geniusAttributes);
    }

    public void addAttribute(GeniusAttribute attribute){
        attributes.add(attribute);
    }

}

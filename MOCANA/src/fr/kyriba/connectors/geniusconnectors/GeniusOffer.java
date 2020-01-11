package fr.kyriba.connectors.geniusconnectors;

import fr.kyriba.protocol.*;
import fr.kyriba.protocol.Offer;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.*;

import java.util.HashMap;
import java.util.Map;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class GeniusOffer implements Offer {
    private NegotiationDomain domain;
    private Map<Integer, Object> values;
    private int player;
    private int round;
    private CommunicativeAct act;

    public GeniusOffer(NegotiationDomain domain, Map<Integer, Object> values, int player, int round, CommunicativeAct act){
        this.domain = domain;
        this.values = values;
        this.player = player;
        this.round = round;
        this.act = act;
    }

    public static GeniusOffer offerFromGenius(NegotiationDomain negotiationDomain, negotiator.actions.Action geniusAction, int player, int round, CommunicativeAct act){
        Map<Integer,Object> values = new HashMap<>();
        if(geniusAction instanceof negotiator.actions.Offer){
            for (Attribute attribute :negotiationDomain.getAttributes()) {
                int j = attribute.getNumber();
                if (attribute instanceof DiscreteAttribute) {
                    values.put(j,((ValueDiscrete)((negotiator.actions.Offer)geniusAction).getBid().getValue(negotiationDomain.getAttributes().get(j).getNumber())).getValue());
                } else if (attribute instanceof QuantitativeAttribute) {
                    if(!((GeniusQuantitativeAttribute)attribute).isReal()){
                        values.put(j,((ValueInteger)((negotiator.actions.Offer)geniusAction).getBid().getValue(j)).getValue());
                    }
                    else{
                        values.put(j,((ValueReal)((negotiator.actions.Offer)geniusAction).getBid().getValue(j)).getValue());
                    }
                }
                else{
                    throw new IllegalArgumentException("This issue " + attribute + " is not handled by the system");
                }
            }
        }
        return new GeniusOffer(negotiationDomain,values,player,round,act);
    }

    @Override
    public Map<Integer, Object> getValues() {
        return values;
    }

    @Override
    public Object getValue(int i) {
        return values.get(i);
    }

    @Override
    public NegotiationDomain getDomain() {
        return domain;
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public int getRound() {
        return round;
    }

    @Override
    public CommunicativeAct getAct() {
        return act;
    }

    public void setAct(CommunicativeAct act) {
        this.act = act;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder(act.toString());
        for(int i : values.keySet()){
            builder.append("; value ").append(i).append(": ").append(values.get(i)).append("; round: ").append(round).append("; ");
        }
        return builder.toString();
    }
    public Bid toGenius(Domain geniusDomain){
        HashMap<Integer, Value> geniusValues = new HashMap<>();
        for(Attribute a:getDomain().getAttributes()){
            int i = a.getNumber();
            if(domain.getAttributeByNumber(i) instanceof DiscreteAttribute){
                geniusValues.put(i, new ValueDiscrete((String)values.get(i)));
            }
            else if(domain.getAttributeByNumber(i) instanceof QuantitativeAttribute){
                if(((GeniusQuantitativeAttribute)domain.getAttributeByNumber(i)).isReal()) {
                    geniusValues.put(i, new ValueReal((Double) values.get(i)));
                }
                else{
                    geniusValues.put(i, new ValueInteger(((Number)values.get(i)).intValue()));
                }
            }
        }
        return new Bid(geniusDomain, geniusValues);
    }
}

package fr.kyriba.history;

import fr.kyriba.protocol.Offer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/

public class HistoryListHandler implements HistoryHandler {
    /**Historique stocké en mémoire sous forme de liste. Pas de persistence*/

    private List<Offer> actions;

    public HistoryListHandler() {
        actions = new ArrayList<>();
    }

    @Override
    public void store(Offer offer) {
        actions.add(offer);
        List<Integer> rounds = new ArrayList<>();
        Set<Integer> roundSet = new HashSet<>();
        for(Offer o:actions) {
            rounds.add(o.getRound());
            roundSet.add(o.getRound());
        }
    }

    @Override
    public synchronized List<Offer> getHistory() {
        return actions;
    }

    @Override
    public Offer getLastMove() {
        if(actions.size() == 0){
            throw new IllegalStateException("You asked a move from an empty list.");
        }
        return actions.get(actions.size()-1);
    }

    @Override
    public Offer getMove(int i) {
        if(actions.size() < i){
            throw new IllegalArgumentException("You try to access the move " + i + " of a move list of " + actions.size() + " elements.");
        }
        return actions.get(i);
    }

    @Override
    public void storeAll(List<Offer> offers) {
        actions.addAll(offers);
    }

    @Override
    public HistoryListHandler filterPlayer(int player){
        HistoryListHandler handler = new HistoryListHandler();
        for(int i = 0; i<getHistory().size(); i++){
            if(getHistory().get(i).getPlayer()==player){
                handler.store(getHistory().get(i));
            }
        }
        return handler;
    }

    @Override
    public HistoryHandler filterRound(Offer move) {
        HistoryHandler handler = new HistoryListHandler();
        for (Offer action : actions) {
            if (action.getRound() <= move.getRound()) {
                handler.store(action);
            } else {
                return handler;
            }
        }
        return handler;
    }
}

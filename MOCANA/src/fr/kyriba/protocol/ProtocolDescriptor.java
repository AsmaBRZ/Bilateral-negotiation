package fr.kyriba.protocol;

import fr.kyriba.agent.Agent;
import negotiator.Domain;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 03/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public class ProtocolDescriptor {
    /**Protocole à proprement parler*/
    private final int playerNumber;
    private final Agent myAgent;

    public ProtocolDescriptor(int playerNumber, Agent myAgent) {
        this.playerNumber = playerNumber;
        this.myAgent = myAgent;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Domain getDomain(){
        if(! (myAgent instanceof negotiator.Agent)){
            throw new IllegalArgumentException("This agent should be a genius agent !");
        }
        negotiator.Agent gAgent = (negotiator.Agent) myAgent;
        return gAgent.utilitySpace.getDomain();
    }

    public int nextPlayer(int currentPlayer){
        return (currentPlayer + 1) % playerNumber;
    }
}

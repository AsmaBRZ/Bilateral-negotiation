package fr.kyriba.protocol;

import java.util.Map;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public interface Offer {
    /**Abstraction d'une offre*/
    Map<Integer, Object> getValues();
    Object getValue(int i);
    NegotiationDomain getDomain();
    int getPlayer();
    int getRound();
    CommunicativeAct getAct();
}

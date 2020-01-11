package fr.kyriba.history;


import fr.kyriba.protocol.Offer;

import java.util.List;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 03/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public interface HistoryHandler {
    void store(Offer offer);
    List<Offer> getHistory();
    Offer getLastMove();
    Offer getMove(int i);
    void storeAll(List<Offer> offers);
    HistoryListHandler filterPlayer(int player);

    HistoryHandler filterRound(Offer move);
}

package fr.kyriba.connectors.geniusconnectors;

import fr.kyriba.protocol.Attribute;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public abstract class GeniusAttribute implements Attribute {
    public void setNumber(int number) {
        this.number = number;
    }

    private int number;

    @Override
    public int getNumber() {
        return number;
    }

    public GeniusAttribute(int number){
        this.number = number;
    }

}

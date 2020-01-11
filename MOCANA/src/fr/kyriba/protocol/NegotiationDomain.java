package fr.kyriba.protocol;

import java.util.List;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 04/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
public interface NegotiationDomain {
    /**Abstraction du domaine de négociation*/
    List<? extends Attribute> getAttributes();

    Attribute getAttributeByNumber(int i);

}

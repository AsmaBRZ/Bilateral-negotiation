package fr.kyriba;
/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2016-11-02  P-CBU   Initial version.                                         *
 ********************************************************************************/
import fr.kyriba.agent.Agent;
import fr.kyriba.protocol.ProtocolDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProtocolTest {

    ProtocolDescriptor protocol;
    @Mock
    Agent agent;

    @Before
    public void initialize(){
        protocol = new ProtocolDescriptor(5,agent);
    }

    @Test
    public void testNext(){
        for (int i =0; i<4; i++) {
            Assert.assertEquals(protocol.nextPlayer(i), i+1);
        }
        Assert.assertEquals(protocol.nextPlayer(4), 0);
        Assert.assertNotEquals(protocol.nextPlayer(2), 4);
    }
}

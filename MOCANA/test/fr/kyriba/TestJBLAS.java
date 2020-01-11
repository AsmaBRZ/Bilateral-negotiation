/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2017-02-09  P-CBU   Initial version.                                         *
 ********************************************************************************/

package fr.kyriba;

import org.jblas.DoubleMatrix;
import org.junit.Test;

/**
 * P-CBU created on 09/02/17.
 */
public class TestJBLAS {
    @Test
    public void testMultJblas() {
        double[][] mat_d = {{0.5, 0.5}, {0.2, 0.5}};
        DoubleMatrix mat = new DoubleMatrix(mat_d);
        System.out.println(mat.transpose().mmul(mat));
    }
}

package fr.kyriba;

import Jama.Matrix;
import fr.kyriba.bidgeneration.strategymodeling.MatrixUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * P-CBU created on 05/05/17.
 */
public class UtilitiesTester {
    @Test
    public void printMatrixTester(){
        double[][] values = {{0.5,1.0,1.5},{2.5,5.0,2.0},{5.1,2.8,1.0},{8.4,4.6,3.0}};
        Matrix mat = new Matrix(values);
        String strMat = MatrixUtilities.printMatrix(mat);
        String expectedStr = "0.5\t1.0\t1.5\t\n2.5\t5.0\t2.0\t\n5.1\t2.8\t1.0\t\n8.4\t4.6\t3.0\t\n";
        Assert.assertEquals(strMat, expectedStr);
    }
}

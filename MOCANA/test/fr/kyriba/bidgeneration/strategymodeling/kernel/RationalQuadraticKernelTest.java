package fr.kyriba.bidgeneration.strategymodeling.kernel;

import Jama.Matrix;
import fr.kyriba.bidgeneration.strategymodeling.MatrixUtilities;
import fr.kyriba.connectors.geniusconnectors.GeniusDomainAdapter;
import fr.kyriba.connectors.geniusconnectors.GeniusOffer;
import fr.kyriba.connectors.geniusconnectors.GeniusQuantitativeAttribute;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.CommunicativeAct;
import fr.kyriba.protocol.Offer;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * P-CBU created on 18/07/17.
 */
public class RationalQuadraticKernelTest{

    @Test
    public void testEinsum1(){
        double[][] aArray = new double[5][2];
        double[][] bArray = new double[5][2];

        aArray[0][0] = 1;
        aArray[1][0] = 2;
        aArray[2][0] = 3;
        aArray[3][0] = 4;
        aArray[4][0] = 5;
        aArray[0][1] = 11;
        aArray[1][1] = 12;
        aArray[2][1] = 13;
        aArray[3][1] = 14;
        aArray[4][1] = 15;
        bArray[0][0] = 21;
        bArray[1][0] = 22;
        bArray[2][0] = 23;
        bArray[3][0] = 24;
        bArray[4][0] = 25;
        bArray[0][1] = 21;
        bArray[1][1] = 22;
        bArray[2][1] = 23;
        bArray[3][1] = 24;
        bArray[4][1] = 25;

        Matrix a = new Matrix(aArray);
        Matrix b = new Matrix(bArray);

        Matrix c = MatrixUtilities.einsum1(a,b);

        Assert.assertEquals(c.get(0,0), 355, 0.001);
        Assert.assertEquals(c.get(0,1), 1505, 0.001);
    }

    @Test
    public void testOptimization(){
        RationalQuadraticKernel kernel = new RationalQuadraticKernel(0.641);
        HistoryHandler handler = new HistoryListHandler();
        GeniusDomainAdapter domain = new GeniusDomainAdapter();
        domain.addAttribute(new GeniusQuantitativeAttribute(1,"price", 0,1000,false));
        domain.addAttribute(new GeniusQuantitativeAttribute(2,"note", 0,10,false));
        Map<Integer, Object> values = new HashMap<>();
        values.put(1,50);
        values.put(2,1);
        Offer offer = new GeniusOffer(domain,values ,0, 0, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,100);
        values.put(2,2);
        offer = new GeniusOffer(domain, values,1, 1, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,150);
        values.put(2,3);
        offer = new GeniusOffer(domain, values,0, 2, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,200);
        values.put(2,4);
        offer = new GeniusOffer(domain, values,1, 3, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,250);
        values.put(2,5);
        offer = new GeniusOffer(domain, values,0, 4, CommunicativeAct.PROPOSE);
        handler.store(offer);

        kernel.optimize(handler);
        Assert.assertEquals(1.285715, kernel.getK(), 0.001);
        Assert.assertEquals(0.640598, kernel.getAlpha(), 0.001);
    }
    @Test
    public void testEinsum3(){
        double d = 0;
        Matrix[]a = new Matrix[2];
        Matrix[]b = new Matrix[2];
        for (int k = 0; k < 2; k++) {
            a[k] = new Matrix(5,5);
            b[k] = new Matrix(5,5);
            for(int i =0; i<5; i++) {
                for (int j = 0; j < 5; j++) {
                    a[k].set(i, j, d);
                    d = d + 1;
                    b[k].set(i, j, d);
                    d = d + 1;
                }
            }
        }
        Matrix m = MatrixUtilities.einsum3(a, b);
        Assert.assertEquals(20200,m.get(0,0), 1);
        Assert.assertEquals(50200,m.get(0,1), 1);
        Assert.assertEquals(51450,m.get(1,0), 1);
        Assert.assertEquals(143950,m.get(1,1), 1);
    }

    @Test
    public void testEinsum2() {
        Matrix a = new Matrix(4, 2);
        Matrix b = new Matrix(3, 2);
        double d = 0;
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < 4; i++) {
                a.set(i, k, d);
                d = d + 1;
            }
            for (int j = 0; j < 3; j++) {
                b.set(j, k, d);
                d = d + 1;
            }
        }
        Matrix[] m = MatrixUtilities.einsum2(a, b);
        Matrix m1, m2;
        m1 = new Matrix(3, 4);
        m2 = new Matrix(3, 4);
        m1.set(0, 0, 0);
        m1.set(0, 1, 4);
        m1.set(0, 2, 8);
        m1.set(0, 3, 12);
        m1.set(1, 0, 0);
        m1.set(1, 1, 5);
        m1.set(1, 2, 10);
        m1.set(1, 3, 15);
        m1.set(2, 0, 0);
        m1.set(2, 1, 6);
        m1.set(2, 2, 12);
        m1.set(2, 3, 18);
        m2.set(0, 0, 77);
        m2.set(0, 1, 88);
        m2.set(0, 2, 99);
        m2.set(0, 3, 110);
        m2.set(1, 0, 84);
        m2.set(1, 1, 96);
        m2.set(1, 2, 108);
        m2.set(1, 3, 120);
        m2.set(2, 0, 91);
        m2.set(2, 1, 104);
        m2.set(2, 2, 117);
        m2.set(2, 3, 130);
        m1 = m1.transpose();
        m2 = m2.transpose();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                Assert.assertEquals(m[0].get(i, j), m1.get(i, j), 0.001);
                Assert.assertEquals(m[1].get(i, j), m2.get(i, j), 0.001);
            }
        }
    }
}

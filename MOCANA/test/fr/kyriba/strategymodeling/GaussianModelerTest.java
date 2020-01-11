package fr.kyriba.strategymodeling;
/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2016-10-10  P-CBU   Initial version.                                         *
 ********************************************************************************/
import Jama.Matrix;
import fr.kyriba.ExperimentsConstants;
import fr.kyriba.bidgeneration.strategymodeling.GaussianStrategyModeler;
import fr.kyriba.bidgeneration.strategymodeling.QuantitativeGenerator;
import fr.kyriba.bidgeneration.strategymodeling.StrategyModeler;
import fr.kyriba.bidgeneration.strategymodeling.QualitativeGenerator;
import fr.kyriba.connectors.geniusconnectors.GeniusDiscreteAttribute;
import fr.kyriba.connectors.geniusconnectors.GeniusDomainAdapter;
import fr.kyriba.connectors.geniusconnectors.GeniusOffer;
import fr.kyriba.connectors.geniusconnectors.GeniusQuantitativeAttribute;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.CommunicativeAct;
import fr.kyriba.protocol.Offer;
import fr.kyriba.bidgeneration.strategymodeling.kernel.MaternKernel;
import jdistlib.math.Bessel;
import jsat.classifiers.linear.SMIDAS;
import org.apache.commons.math3.special.Gamma;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * @author P-CBU
 */
@RunWith(MockitoJUnitRunner.class)
public class GaussianModelerTest {

    GaussianStrategyModeler modeler;
    HistoryHandler handler;

    Matrix Kmat;

    @Before
    public void initialize(){
        handler = new HistoryListHandler();
        Offer offer = new GeniusOffer(new GeniusDomainAdapter(new ArrayList<>()),new HashMap(), 0, 0, CommunicativeAct.PROPOSE);
        handler.store(offer);
        offer = new GeniusOffer(new GeniusDomainAdapter(new ArrayList<>()),new HashMap(), 0, 1, CommunicativeAct.PROPOSE);
        handler.store(offer);
        offer = new GeniusOffer(new GeniusDomainAdapter(new ArrayList<>()),new HashMap(), 0, 2, CommunicativeAct.PROPOSE);
        handler.store(offer);
        offer = new GeniusOffer(new GeniusDomainAdapter(new ArrayList<>()),new HashMap(), 0, 3, CommunicativeAct.PROPOSE);
        handler.store(offer);
        offer = new GeniusOffer(new GeniusDomainAdapter(new ArrayList<>()),new HashMap(), 0, 4, CommunicativeAct.PROPOSE);
        handler.store(offer);
        modeler = new GaussianStrategyModeler(0.01,10000, handler, new MaternKernel(0.5));
        double[][] KArray = {{1.00000 , 0.84132 , 0.96943 , 0.19494 , 0.37610 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.84132 , 1.00000 , 0.47024 , 0.15733 , 0.97113 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.96943 , 0.47024 , 1.00000 , 0.78941 , 0.35262 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.19494 , 0.15733 , 0.78941 , 1.00000 , 0.83970 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.37610 , 0.97113 , 0.35262 , 0.83970 , 1.00000 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 1.00000 , 0.56079 , 0.71128 , 0.61469 , 0.65781 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.56079 , 1.00000 , 0.99118 , 0.90799 , 0.22037 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.71128 , 0.99118 , 1.00000 , 0.92024 , 0.47017 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.61469 , 0.90799 , 0.92024 , 1.00000 , 0.43143 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.65781 , 0.22037 , 0.47017 , 0.43143 , 1.00000 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 1.00000 , 0.34361 , 0.16955 , 0.13460 , 0.18045},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.34361 , 1.00000 , 0.60428 , 0.35908 , 0.11173},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.16955 , 0.60428 , 1.00000 , 0.52722 , 0.76381},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.13460 , 0.35908 , 0.52722 , 1.00000 , 0.72070},
                {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.18045 , 0.11173 , 0.76381 , 0.72070 , 1.00000}};
        Kmat = new Matrix(KArray);
        //Moche mais bon
//        try {
//            Field field = GaussianStrategyModeler.class.getDeclaredField("kMatrix");
//            field.setAccessible(true);
//            field.set(modeler, Kmat);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        modeler = Mockito.spy(modeler);
    }

    //Check validity of Modified Bessel function
    @Test
    public void besselTest(){
        Assert.assertEquals(Bessel.k(0.5,0.7,false), 1.238, 0.01);
    }

    @Test
    public void testMaternCovariance(){
        Assert.assertEquals(1.0, Gamma.gamma(2.0),0.001);
        Assert.assertEquals(0.13534, modeler.computeCovariance(0.0,2.0), 0.01);
        Assert.assertEquals(0.36788, modeler.computeCovariance(1.0,2.0), 0.01);
        Assert.assertEquals(1.0, modeler.computeCovariance(2.0,2.0), 0.01);
    }


    @Test
    public void testHattedF(){
        Mockito.doReturn(Kmat).when(modeler).computeDiscreteKMatrix(any(Integer.class));
        double[][] fArray = {{1.0, 3.0, 2.0, 4.0, 2.0, 3.0, 5.0, 7.0, 9.0, 8.0, 4.0, 2.0, 3.0, 4.0, 9.0}};
        Matrix f = new Matrix(fArray);
        double[][] yArray={{2.0, 2.0, 2.0, 3.0, 2.0, 4.0, 6.0, 7.0, 8.0, 9.0, 3.0, 2.0, 3.0, 4.0, 10.0}};
        Matrix ymat = new Matrix(yArray);
        Matrix hattedF = modeler.computeHattedF(5,3, f,ymat, new double[3]);
        double[][] expectedHattedFArray = {{7.4668, 7.6102, 8.3021, 7.1896, 8.5619,21.2488,23.6915,26.1882,25.7062,18.7036, 7.3569, 7.8905,14.8318,14.1852,16.2271}};
        Matrix expectedHattedF = new Matrix(expectedHattedFArray);
        for(int i = 0; i<15; i++) {
            Assert.assertEquals(expectedHattedF.get(0,i),hattedF.get(i,0), 0.01);
        }
    }

    @Test
    public void testPiStar(){
        Mockito.doReturn(Kmat).when(modeler).computeDiscreteKMatrix(any(Integer.class));
        double[][] kStarArray = {{0.01, 0.64, 0.380, 0.426, 0.458, 0.984, 0.87, 0.158, 0.474, 0.518, 0.184, 0.587, 0.33, 0.87, 0.91}};
        double[][] mySigmaArray = {{1.95040,   0.83923,   1.76262},
            {0.83923,   0.38797,   0.85855},
            {1.76262,   0.85855,   2.05845}};
        Matrix mySigma = new Matrix(mySigmaArray);
        Mockito.doReturn(new Matrix(kStarArray)).when(modeler).starVector(Mockito.any(Integer.class));
        Mockito.doReturn(mySigma).when(modeler).computeSigma(Mockito.any(Matrix.class), Mockito.any(Matrix.class), Mockito.any(Integer.class), Mockito.any(Matrix.class), Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(Matrix.class));
        double[][] hattedFArray = {{7.2406,7.3861,8.1168,7.1271,8.3892,19.2162,22.2877,24.3007,23.8971,16.3629,3.7847,5.6680,7.0113,6.7374,6.3947}};
        Matrix hattedF = new Matrix(hattedFArray);
        int x = 5;
        int c = 3;
        int n = 5;
        double[][] yArray = {{1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0}};
        Matrix y = new Matrix(yArray);
        Matrix piStar = modeler.computeMeanPiStar(hattedF,x,y,c,n);
        Assert.assertEquals(piStar.get(0,0), 0.48,0.1);
        Assert.assertEquals(piStar.get(1,0), 0.07,0.01);
        Assert.assertEquals(piStar.get(2,0), 0.44,0.1);
//        System.out.println(piStar);
    }

    @Test
    public void testIntegration(){
        ExperimentsConstants.RANDOM = new Random(ExperimentsConstants.SEED);
        HistoryHandler handler = new HistoryListHandler();
        GeniusDomainAdapter domain = new GeniusDomainAdapter();
        domain.addAttribute(new GeniusQuantitativeAttribute(1,"price", 0,1000,false));
        Map<Integer, Object> values = new HashMap<>();
        values.put(1,50);
        Offer offer = new GeniusOffer(domain,values ,0, 0, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,1000);
        offer = new GeniusOffer(domain, values,1, 1, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,100);
        offer = new GeniusOffer(domain, values,0, 2, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,900);
        offer = new GeniusOffer(domain, values,1, 3, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,150);
        offer = new GeniusOffer(domain, values,0, 4, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,850);
        offer = new GeniusOffer(domain, values,1, 5, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,200);
        offer = new GeniusOffer(domain, values,0, 6, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,800);
        offer = new GeniusOffer(domain, values,1, 7, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,250);
        offer = new GeniusOffer(domain, values,0, 8, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,750);
        offer = new GeniusOffer(domain, values,1, 9, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,300);
        offer = new GeniusOffer(domain, values,0, 10, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,700);
        offer = new GeniusOffer(domain, values,1, 11, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,350);
        offer = new GeniusOffer(domain, values,0, 12, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,650);
        offer = new GeniusOffer(domain, values,1, 13, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,0);
        offer = new GeniusOffer(domain, values,0, 14, CommunicativeAct.PROPOSE);

        GaussianStrategyModeler modeler = new GaussianStrategyModeler(1000,1,handler.filterPlayer(0), new MaternKernel(0.5));

        StrategyModeler model = modeler.generateModeler(offer);
        Assert.assertEquals(277.8298 , (Double)model.generateOffer().get(1), .5);//exemple à tester: Gaussian Processes for Regression: A Quick Introduction; M. Ebden
    }

    @Test
    public void testDiscreteKMatrix(){
        Matrix mat = modeler.computeDiscreteKMatrix(3);
        double[][] expectedKArray = {{1.0, 0.367879, 0.135335, 0.049787, 0.018316, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.367879, 1.0, 0.367879, 0.135335, 0.049787, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.135335, 0.367879, 1.0, 0.367879, 0.135335, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.049787, 0.135335, 0.367879, 1.0, 0.367879, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.018316, 0.049787, 0.135335, 0.367879, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.367879, 0.135335, 0.049787, 0.018316, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.367879, 1.0, 0.367879, 0.135335, 0.049787, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.135335, 0.367879, 1.0, 0.367879, 0.135335, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.049787, 0.135335, 0.367879, 1.0, 0.367879, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.018316, 0.049787, 0.135335, 0.367879, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.367879, 0.135335, 0.049787, 0.018316},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.367879, 1.0, 0.367879, 0.135335, 0.049787},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.135335, 0.367879, 1.0, 0.367879, 0.135335},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.049787, 0.135335, 0.367879, 1.0, 0.367879},
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.018316, 0.049787, 0.135335, 0.367879, 1.0}};
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                Assert.assertEquals(mat.get(i,j),expectedKArray[i][j], 0.0001);
            }
        }
    }

    @Test
    public void testEuclidian(){
        GeniusDomainAdapter domain = new GeniusDomainAdapter();
        domain.addAttribute(new GeniusQuantitativeAttribute(2,"price", 0,1000,false));
        domain.addAttribute(new GeniusQuantitativeAttribute(4,"foo", 0,100,false));
        String[] s1 = {"baz", "qux", "quux", "corge"};
        domain.addAttribute(new GeniusDiscreteAttribute(1,"bar", s1));
        String[] s2 = {"grault", "garply", "waldo", "fred"};
        Map<String, Double> probas = new HashMap<>();
        probas.put("baz", 0.4);
        probas.put("qux", 0.2);
        probas.put("quux", 0.15);
        probas.put("corge", 0.25);
        domain.addAttribute(new GeniusDiscreteAttribute(3,"plugh", s2));
        QuantitativeGenerator quant1 = new QuantitativeGenerator(300.0,300.0);
        QuantitativeGenerator quant2 = new QuantitativeGenerator(60.0,10.0);
        fr.kyriba.bidgeneration.strategymodeling.QualitativeGenerator qual1 = new QualitativeGenerator(probas);
        probas = new HashMap<>();
        probas.put("grault", 0.1);
        probas.put("garply", 0.5);
        probas.put("waldo", 0.3);
        probas.put("fred", 0.1);
        QualitativeGenerator qual2 = new QualitativeGenerator(probas);
        StrategyModeler myModeler = new StrategyModeler(domain);
        myModeler.getQualitativeGaussians().put(1,qual1);
        myModeler.getQualitativeGaussians().put(3,qual2);
        myModeler.getQuantitativeGaussians().put(2,quant1);
        myModeler.getQuantitativeGaussians().put(4,quant2);
        Map<Integer, Object> vals = new HashMap<>();
        vals.put(1,"quux");
        vals.put(2,100.0);
        vals.put(3,"garply");
        vals.put(4,60.0);
        Offer o = new GeniusOffer(domain, vals,1,1, CommunicativeAct.PROPOSE);
        Assert.assertEquals(modeler.computeEuclidianDistance(myModeler, o), Math.sqrt(1.1903), 0.1);
    }

    @Test
    public void testOptimization(){
        handler = new HistoryListHandler();
        GeniusDomainAdapter domain = new GeniusDomainAdapter();
        domain.addAttribute(new GeniusQuantitativeAttribute(1,"price", 0,1000,false));
        domain.addAttribute(new GeniusQuantitativeAttribute(2,"foo", 0,10,false));
        Map<Integer, Object> values = new HashMap<>();
        values.put(1,50);
        values.put(2,2);
        Offer offer = new GeniusOffer(domain,values ,0, 0, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,100);
        values.put(2,5);
        offer = new GeniusOffer(domain, values,0, 2, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,150);
        values.put(2,3);
        offer = new GeniusOffer(domain, values,0, 4, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,200);
        values.put(2,4);
        offer = new GeniusOffer(domain, values,0, 6, CommunicativeAct.PROPOSE);
        handler.store(offer);
        values = new HashMap<>();
        values.put(1,250);
        values.put(2,8);
        offer = new GeniusOffer(domain, values,0, 8, CommunicativeAct.PROPOSE);
        handler.store(offer);
        modeler.setHandler(handler);
    }

}

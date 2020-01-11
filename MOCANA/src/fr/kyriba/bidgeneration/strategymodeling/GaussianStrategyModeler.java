package fr.kyriba.bidgeneration.strategymodeling;

import Jama.*;
import fr.kyriba.ExperimentsConstants;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.protocol.*;
import fr.kyriba.bidgeneration.strategymodeling.kernel.Kernel;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

import java.util.*;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/

/**Classe principale de modélisation de la stratégie. But: renvoyer un modélisateur (moyenne/écart) type pour chaque variable.
 * Voir: C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
 * À réfactorer ? Il me semble qu'il y a des doublons.
 */
public class GaussianStrategyModeler{
    public synchronized void setHandler(HistoryHandler handler) {
        this.handler = handler;
    }

    /**timelimit: in ms*/
    public GaussianStrategyModeler(double timeLimit, int nbSamples, HistoryHandler handler, Kernel kernel) {
        this.kernel = kernel;
        this.timeLimit = timeLimit;
        this.nbSamples = nbSamples;
        this.handler = handler;
        kMatrix = null;
    }

    private double timeLimit;
    private Matrix kMatrix;
    private int nbSamples;
    private Kernel kernel;

    private HistoryHandler handler;

    //calcule la covariance entre deux propositions au sens du kernel choisi.
    public double computeCovariance(Double x, Double y){
        return kernel.apply(x, y);
    }

    //Calcule la matrice K du cas discret telle que définie dans C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
    public Matrix computeDiscreteKMatrix(int c){
        Matrix matrix;
        int n = handler.getHistory().size();
        double[][] matrixArray = new double[c*n][c*n];
        for(int k=0; k<c; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j <= i; j++) {
                    matrixArray[k*n+i][k*n+j] = computeCovariance((double) handler.getHistory().get(i).getRound(), (double) handler.getHistory().get(j).getRound());
                    if (i != j) {
                        matrixArray[k*n+j][k*n+i] = matrixArray[k*n+i][k*n+j];
                    }
                }
            }
        }
        matrix = new Matrix(matrixArray);
        return matrix;
    }

    //Calcule la matrice K telle que définie dans C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
    public Matrix computeKMatrix(){
        if(kMatrix != null){
            return kMatrix;
        }
        Matrix matrix;
        int n = handler.getHistory().size();
        double[][] matrixArray = new double[n][n];
        for(int i = 0; i<n; i++){
            for(int j=0; j<=i; j++){
                matrixArray[i][j] = computeCovariance((double) handler.getHistory().get(i).getRound(), (double) handler.getHistory().get(j).getRound());
                if(i!=j){
                    matrixArray[j][i] = matrixArray[i][j];
                }
            }
        }
        matrix = new Matrix(matrixArray);
        return matrix;
    }
    //Vecteur k* dans la cas discret
    private Matrix discreteStarVector(int round, int c){
        Matrix vector;
        int n = handler.getHistory().size();
        double[][] vectorArray = new double[1][n*c];
        for(int j = 0; j<c; j++) {
            for (int i = 0; i < n; i++) {
                vectorArray[0][j*n+i] = computeCovariance((double) handler.getHistory().get(i).getRound(), (double) round);
            }
        }
        vector = new Matrix(vectorArray);
        return vector;
    }

    //Vecteur k* dans la cas continu
    public Matrix starVector(int round){
        Matrix vector;
        int n = handler.getHistory().size();
        double[][] vectorArray = new double[1][n];
        for(int i = 0; i<n;i++){
            vectorArray[0][i] = computeCovariance((double) handler.getHistory().get(i).getRound(), (double) round);
        }
        vector = new Matrix(vectorArray);
        return vector;
    }

    //Scalaire k**
    private double doubleStar(int round){
        return computeCovariance((double)round,(double) round);
    }
    private Matrix doubleStarClassification(int round, int nbClasses){
        Matrix k = new Matrix(nbClasses,1);
        for(int i = 0; i<nbClasses; i++) {
            k.set(i, 0, computeCovariance((double) round, (double) round));
        }
        return k;
    }

    public StrategyModeler generateModeler(Offer x) {
        //Case without data
        StrategyModeler modeler = new StrategyModeler(x.getDomain());
        Random rand = ExperimentsConstants.RANDOM;
        if(handler.getHistory().size() <= 1){
            //create random modeler
            NegotiationDomain domain = x.getDomain();
            List<? extends Attribute> attributes = domain.getAttributes();
            for (Attribute attribute:attributes) {
                int i = attribute.getNumber();
                if (attribute instanceof QuantitativeAttribute) {
                    QuantitativeAttribute quantitativeAttribute = (QuantitativeAttribute) attribute;
                    double min = quantitativeAttribute.getMin();
                    double max = quantitativeAttribute.getMax();
                    double mean = rand.nextDouble()*(max-min)+min;
                    double std = (max-min)/3.0;//FIXME any other idea?
                    modeler.getQuantitativeGaussians().put(i, new QuantitativeGenerator(mean, std));
                } else if (attribute instanceof DiscreteAttribute) {
                    DiscreteAttribute discreteAttribute = (DiscreteAttribute) attribute;
                    int nbPossible = discreteAttribute.getValues().size();
                    Map<String, Double> values = new HashMap<>();
                    for(int j = 0; j<nbPossible; j++){
                        values.put(discreteAttribute.getValues().get(j),1.0/(double)nbPossible);
                    }
                    modeler.getQualitativeGaussians().put(i,new QualitativeGenerator(values));
                } else {
                    throw new IllegalStateException("The attribute nb " + i + " is of an unknown type");
                }
            }
        }
        else {
            modeler= parametrizedModeler(handler, x);
        }
        return modeler;
    }

    //Calcul du modélisateur pour un attribut continu
    private QuantitativeGenerator quantitativeModeler(HistoryHandler h, Offer x, int attributeNumber){
        int n = h.getHistory().size();
        double[][] yArray = new double[1][n];
        for (int i = 0; i < n; i++) {
            if(h.getHistory().get(i).getValue(attributeNumber) instanceof Integer){
                yArray[0][i] = (double)((int) handler.getHistory().get(i).getValue(attributeNumber));
            }
            else {
                yArray[0][i] = (Double) handler.getHistory().get(i).getValue(attributeNumber);
            }
        }
        Matrix yVector = new Matrix(yArray);
        Matrix alafin = starVector(x.getRound()).transpose();
        alafin = alafin.transpose();
        Matrix solve = computeKMatrix();
        try {
            solve = solve.inverse();
        }
        catch(RuntimeException e){
            System.err.println("Erreur, la matrice est singulière:\n" + MatrixUtilities.printMatrix(solve) + "\n");
            for(Offer o:h.getHistory()){
                System.err.println(o);
            }
            e.printStackTrace();
        }
        alafin = alafin.times(solve);
        alafin = alafin.times(yVector.transpose());
        double mean = alafin.get(0,0);
        double variance = doubleStar(x.getRound()) - starVector(x.getRound()).times((computeKMatrix().inverse())).times(starVector(x.getRound()).transpose()).get(0, 0);
        return new QuantitativeGenerator(mean, variance);
    }

    //Calcul du modélisateur pour un attribut discret
    private QualitativeGenerator qualitativeModeler(Attribute attribute, int attributeNumber, HistoryHandler h, Offer x){//See sec. 3.5 of C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
        int n = h.getHistory().size();
        DiscreteAttribute discrete = (DiscreteAttribute) attribute;
        int c = discrete.getValues().size();
        double[][] fArray = new double[1][c * n];
        Matrix f = new Matrix(fArray);
        double[][] yArray = new double[1][n * c];
        for (int i = 0; i < n; i++) {
            int index = discrete.getValues().indexOf(h.getHistory().get(i).getValue(attributeNumber));
            if (index == -1) {
                throw new IllegalStateException("Error: The value " + h.getHistory().get(i).getValue(attributeNumber) + " is not present in the attribute number " + attributeNumber);
            }
            yArray[0][n*index + i] = 1;
        }
        Matrix yMat = new Matrix(yArray);
        double[][] kMatrixArray = new double[n * c][n * c];
        Matrix mat = computeKMatrix();
        for (int l = 0; l < n; l++) {
            for (int m = 0; m < n; m++) {
                for (int k = 0; k < c; k++) {
                    kMatrixArray[k * n + l][k * n + m] = mat.get(l, m);
                }
            }
        }
        double[] z = new double[c];
        double time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < timeLimit) {//TODO Condition d'arrêt supplémentaire ?
            f = computeHattedF(n, c, f, yMat, z);
        }
        double[] probas = computeMeanPiStar(f, x.getRound(), yMat, c, n).getArray()[0];
        Map<String,Double> values = new HashMap<>();
        for(int i =0; i<c; i++){
            values.put(discrete.getValues().get(i),probas[i]);
        }
        return new QualitativeGenerator(values);
    }

    //Calcul du modélisateur.
    private StrategyModeler parametrizedModeler(HistoryHandler h, Offer x){
        //Case with data
        StrategyModeler modeler = new StrategyModeler(x.getDomain());
        List<? extends Attribute> attributes = x.getDomain().getAttributes();
        for (Attribute attribute:attributes) {
            int j = attribute.getNumber();
            if (attribute instanceof QuantitativeAttribute) {
                modeler.getQuantitativeGaussians().put(j, quantitativeModeler(h, x, j));
            } else if (attribute instanceof DiscreteAttribute) {
                modeler.getQualitativeGaussians().put(j, qualitativeModeler(attribute, j, h, x));
            } else {
                throw new IllegalStateException("The attribute nb " + j + " is of an unknown type");
            }
        }
        return modeler;
    }

    //Calcul de la distance euclidienne entre une offre et sa prévision.
    public double computeEuclidianDistance(StrategyModeler modeler, Offer move) {
        double distance = 0.0;
        for(int i: modeler.getQuantitativeGaussians().keySet()){
            double range = modeler.getQuantitativeGaussians().get(i).getVariance();
            distance+=Math.pow((modeler.getQuantitativeGaussians().get(i).getMean()-((Double)move.getValue(i)))/range, 2);
        }
        for(int i: modeler.getQualitativeGaussians().keySet()){
            QualitativeGenerator qualit =  modeler.getQualitativeGaussians().get(i);
            distance+=Math.pow(1.0-qualit.probas.get(move.getValue(i)),2.0);
        }
        return Math.sqrt(distance);
    }

    //Calcul de 𝛔 tel que défini dans le chapitre 2
    public Matrix computeSigma(Matrix Sigma, Matrix C, int xStar, Matrix b, int c, int i, int n, Matrix kcStar){
        for(int j = 0; j<c; j++){
            Matrix kccStar = discreteStarVector(xStar,c).getMatrix(j*n, (j+1)*n, 0,1);
            Sigma.set(i,j,C.transpose().times(kccStar).get(0,0));
        }
        Sigma.set(i,i,Sigma.get(i,i)+doubleStarClassification(xStar, c).get(c-1,0)-(b.transpose().times(kcStar)).get(0,0));
        return Sigma;
    }

    //calcul de ̅𝛑 ⃰(algo 3.4)
    public Matrix computeMeanPiStar(Matrix hattedF, int xStar, Matrix y, int c, int n) {//Algorithm 3.4 of C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
        double[][] pi = computePi(hattedF.getArray()[0],c,n);
        Matrix piMat = new Matrix(pi);
        Matrix D = MatrixUtilities.diag(piMat.getArray()[0]);
        Matrix[] Ec = new Matrix[c];
        Matrix stackedE = new Matrix(n,n);
        for(int i = 0; i<c; i++){
            Matrix Dc = D.getMatrix(i*n, (i+1)*n,i*n, (i+1)*n);
            Matrix L = (Matrix.identity(n,n).plus(MatrixUtilities.sqrt(Dc).times(computeDiscreteKMatrix(c).getMatrix(i*n, (i+1)*n,i*n, (i+1)*n)).times(MatrixUtilities.sqrt(Dc)))).chol().getL();
            Matrix div = L.solve(MatrixUtilities.sqrt(Dc));
            Ec[i] = MatrixUtilities.sqrt(Dc).times(L.transpose().solve(div));
            stackedE = stackedE.plus(Ec[i]);
        }
        Matrix M = stackedE.chol().getL();
        Matrix Sigma = new Matrix(c,c);
        Matrix mu = new Matrix(c,1);
        for(int i = 0; i<c; i++){
            Matrix yc = y.getMatrix(i*n, (i+1)*n, 0,1);
            Matrix pic = piMat.getMatrix(i*n, (i+1)*n, 0,1);
            Matrix kcStar = discreteStarVector(xStar,c).getMatrix(i*n, (i+1)*n, 0,1);
            mu.set(i,0,(yc.minus(pic)).transpose().times(kcStar).get(0,0));
            Matrix b = Ec[i].times(kcStar);
            Matrix div = M.solve(b);
            Matrix C = Ec[i].times(M.transpose().solve(div));
            Sigma = computeSigma(Sigma, C, xStar, b, c, i, n, kcStar);
        }
        Matrix piStar = new Matrix(c,1);
        for(int i=0; i<nbSamples; i++){
            MultivariateNormalDistribution distrib = new MultivariateNormalDistribution(mu.getArray()[0], Sigma.getArray());
            double[][]fstarArray = new double[][] {distrib.sample()};
            Matrix fStar = new Matrix(fstarArray);
            piStar = piStar.plus(MatrixUtilities.exp(fStar).times(1.0/ MatrixUtilities.sum(MatrixUtilities.exp(fStar))));
        }
        return piStar.times(1/nbSamples);
    }

    //calcul de 𝛑 (algo 3.4 ligne 2)
    private double[][] computePi(double[]fArray, int c, int n){
        double[][] pi = new double[1][c * n];
        for (int i = 0; i < c * n; i++) {
            double sum = 0;
            for (int l = 0; l < c; l++) {
                sum += Math.exp(fArray[l * n + i % n]);
            }
            pi[0][i] = Math.exp(fArray[i]) / sum;
        }
        return pi;
    }

    //calcul de ̂f (algo 3.4 ligne 2)
    public Matrix computeHattedF(int n, int c, Matrix f, Matrix yMat, double[] z) {//Algorithm 3.3 of C. E. Rasmussen & C. K. I. Williams, Gaussian Processes for Machine Learning, the MIT Press, 2006, © 2006 Massachusetts Institute of Technology. www.GaussianProcess.org/gpml
        double[] fArray = f.getArray()[0];
        double[][] piArray = computePi(fArray,c,n);
        Matrix piMat = new Matrix(piArray);
        double[][] piMajArray = new double[c*n][n];
        for (int i = 0; i<c*n; i++){
            piMajArray[i][i%n] = piArray[0][i];
        }
        Matrix PiMaj = new Matrix(piMajArray);
        Matrix[] Ec = new Matrix[c];
        Matrix M;
        Matrix stackedE = new Matrix(n,n);
        Matrix D = MatrixUtilities.diag(piArray[0]);
        Matrix E = new Matrix(n*c,n*c);
        for(int i = 0; i<c; i++){
            Matrix Dc = D.getMatrix(i*n, (i+1)*n-1,i*n, (i+1)*n-1);
            Matrix L = (Matrix.identity(n,n).chol().getL().plus(MatrixUtilities.sqrt(Dc).times(computeDiscreteKMatrix(c).getMatrix(i*n, (i+1)*n-1,i*n, (i+1)*n-1)).times(MatrixUtilities.sqrt(Dc))));
            Matrix div = L.solve(MatrixUtilities.sqrt(Dc));
            Ec[i] = MatrixUtilities.sqrt(Dc).times(L.transpose().solve(div));
            int[] range = new int[n];
            for(int j = 0; j<n; j++){
                range [j] = i*n+j;
            }
            E.setMatrix(range, range, Ec[i]);
            stackedE = stackedE.plus(Ec[i]);
            for(int l = 0; l<n; l++){
                z[i]+=Math.log(L.get(l,l));
            }
        }
        M = stackedE.chol().getL();
        Matrix b = ((D.minus(PiMaj.times(PiMaj.transpose()))).times(f.transpose())).plus(yMat.minus(piMat).transpose());
        Matrix cVec = E.times(computeDiscreteKMatrix(c)).times(b);
        Matrix R = D.inverse().times(PiMaj);
        Matrix div1 = M.solve(R.transpose().times(cVec));
        Matrix div2 = (E.times(R)).times(M.transpose().solve(div1));
        Matrix a = b.minus(cVec).plus(div2);
        f = computeDiscreteKMatrix(c).times(a);
        return f;
    }

    public HistoryHandler getHandler() {
        return handler;
    }

    public void optimizeKernel() {
        kernel.optimize(handler);
    }
}

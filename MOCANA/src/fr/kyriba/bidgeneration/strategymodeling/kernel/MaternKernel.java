package fr.kyriba.bidgeneration.strategymodeling.kernel;

import Jama.Matrix;
import fr.kyriba.bidgeneration.strategymodeling.MatrixUtilities;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.protocol.Attribute;
import fr.kyriba.protocol.QuantitativeAttribute;
import jdistlib.math.Bessel;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimplePointChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.special.Gamma;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static agents.anac.y2015.Phoenix.GP.MatrixOperations.sqrt;
import static java.lang.Double.NaN;

/**
 * P-CBU created on 13/06/17.
 */
public class MaternKernel implements Kernel {

    private double l;
    private double nu;

    public MaternKernel(double nu){
        this.nu = nu;
    }

    /**Kernel de Matérn. Correct en ce qui concerne les domaines continus. Reste à revérifier le cas discret*/
    @Override
    public double apply(Double x, Double y) {
        if(x.equals(y)) {
            return 1.0;
        }
        return  Math.pow(2.0, 1.0-nu)/ Gamma.gamma(nu)*Math.pow(Math.sqrt(2*nu)*Math.abs(x-y)/l, nu)* Bessel.k(Math.sqrt(2.0*nu)*Math.abs(x-y)/l, nu, false);
    }

    //Fonction d'optimisation: optimisation de l
    @Override
    public void optimize(HistoryHandler handler) {
        Matrix K = MatrixUtilities.computeKMatrix(handler, this);
        Function<Double,Double> maternLikelihood = val -> {
            double previousRho = l;
            l = Math.exp(val);
            int n = handler.getHistory().size();
            List<Integer> indices = new ArrayList<>();
            for(Attribute a: handler.getLastMove().getDomain().getAttributes()){
                if(a instanceof QuantitativeAttribute){
                    indices.add(a.getNumber());
                }
            }
            double[][] y = new double[n][indices.size()];
            for(int i = 0; i<n; i++){
                for (int j= 0; j<indices.size(); j++){
                    y[i][j] = (double) (Integer) handler.getMove(i).getValue(indices.get(j));
                }
            }
            Matrix yMat = new Matrix(y);
            Matrix alpha = K.solve(yMat);
            Matrix logLikelihoodGradDim = MatrixUtilities.sumColumns(alpha.arrayTimes(yMat));
            logLikelihoodGradDim =logLikelihoodGradDim.arrayTimes(new Matrix(logLikelihoodGradDim.getRowDimension(), logLikelihoodGradDim.getColumnDimension(), -0.5));
            Matrix L = K.chol().getL();
            logLikelihoodGradDim =logLikelihoodGradDim.minus(new Matrix(logLikelihoodGradDim.getRowDimension(), logLikelihoodGradDim.getColumnDimension(), MatrixUtilities.log(L).trace()));
            logLikelihoodGradDim = logLikelihoodGradDim.minus(new Matrix(logLikelihoodGradDim.getRowDimension(), logLikelihoodGradDim.getColumnDimension(), n/2.0*Math.log(Math.PI*2.0)));
            Double value = MatrixUtilities.sum(logLikelihoodGradDim);
            l = previousRho;
            return -value;
        };
        Function<Double,Double> maternGradientLikelihood = val -> {
            l = Math.exp(val);
            int n = handler.getHistory().size();
            List<Integer> indices = new ArrayList<>();
            for(Attribute a: handler.getLastMove().getDomain().getAttributes()){
                if(a instanceof QuantitativeAttribute){
                    indices.add(a.getNumber());
                }
            }
            double[][] dArray = new double[n][n];
            double[][] y = new double[indices.size()][n];
            for(int i = 0; i<n; i++){
                for(int j = i; j<n; j++) {
                    dArray[i][j] = Math.pow(handler.getMove(i).getRound() - handler.getMove(j).getRound(), 2.0)/Math.pow(l, 2.0);
                    dArray[j][i] = dArray[i][j];
                }
                for (int j= 0; j<indices.size(); j++){
                    y[j][i] = (double) (Integer) handler.getMove(i).getValue(indices.get(j));
                }
            }
            List<Matrix> yMat = new ArrayList<>();
            for (double[] aY : y) {
                yMat.add(new Matrix(new double[][]{aY}));
            }
            Matrix D = new Matrix(dArray);
            Matrix KGradient;
            if(nu == 0.5){
                KGradient =  K.arrayTimes(D).arrayRightDivide(MatrixUtilities.sqrt(D));
                KGradient = MatrixUtilities.map(KGradient, aDouble -> aDouble.equals(NaN)?0:aDouble);
            }
            else if(nu == 1.5){
                KGradient =  D.times(3.0).times(MatrixUtilities.exp(D.times(-3.0)));
            }
            else if(nu == 2.5){
                Matrix tmp = sqrt(D.times(5.0));
                KGradient = D.times(5.0/3.0).times(tmp.plus(new Matrix(n,n,1.0))).times(MatrixUtilities.exp(tmp));
            }
            else{throw new IllegalStateException("nu has a value of " + nu + ". For the moment, only 0.5, 1.5 and 2.5 are supported");
            }
            List<Matrix> alpha = new ArrayList<>();
            List<Matrix> tmps = new ArrayList<>();
            List<Double> gradientLikelihoodDims = new ArrayList<>();
            double total = 0.0;
            for(int i = 0; i<yMat.size(); i++) {
                alpha.add(K.solve(yMat.get(i).transpose()));
                tmps.add(alpha.get(i).times(alpha.get(i).transpose()));
                tmps.set(i, tmps.get(i).minus(K.solve(Matrix.identity(n,n))));
                gradientLikelihoodDims.add(0.5* MatrixUtilities.sum(tmps.get(i).arrayTimes(KGradient)));
                total +=gradientLikelihoodDims.get
                        (i);
            }
            return -total;
        };

        SimplePointChecker<PointValuePair> checker = new SimplePointChecker<>(1e-5, 1e-5);
        MultivariateOptimizer mOptimizer = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES, checker);
        PointValuePair p = mOptimizer.optimize(new InitialGuess(new double[]{1.5}),new MaxEval(100000), new ObjectiveFunction(doubles -> maternLikelihood.apply(doubles[0])), GoalType.MINIMIZE,new SearchInterval(0.0, 2),new ObjectiveFunctionGradient(doubles ->new double[]{maternGradientLikelihood.apply(doubles[0])}));
        l= p.getPoint()[0];

    }

    //Gradient
    @Override
    public double gradient(double x, double y) {
        throw new IllegalStateException("Matern gradient is too complex to compute, implementation of K Gradient special (see optimal function");
    }

}

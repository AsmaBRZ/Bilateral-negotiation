package fr.kyriba.bidgeneration.strategymodeling.kernel;

import Jama.CholeskyDecomposition;
import Jama.Matrix;
import fr.kyriba.bidgeneration.strategymodeling.MatrixUtilities;
import fr.kyriba.history.HistoryHandler;
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

import java.util.function.Function;

/**
 * P-CBU created on 13/06/17.
 */
public class RationalQuadraticKernel implements Kernel {
    private double k;
    private double alpha;

    /**Kernel Rational Quadratic. Testé sur des vraies séries et plus efficace à long terme que Matérn. Pas encore de traitement du cas discret.*/
    public RationalQuadraticKernel(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double apply(Double x, Double y) {
        if(x.equals(y)){
            return 1.0;
        }
        else{
            Double d = Math.pow(1.0+Math.pow(x-y, 2)/(2*alpha*Math.pow(k, 2)), -alpha);
            return d;
        }
    }

    //Optimisation de k
    @Override
    public void optimize(HistoryHandler handler) {
        k = 1;
        alpha = 1;
//        Matrix K = MatrixUtilities.computeKMatrix(handler, this);
        Matrix ymat = MatrixUtilities.computeyMat(handler);
//        for(int i=0; i<K.getColumnDimension(); i++) {
//            K.set(i, i, K.get(i, i)+1e-10);
//        }
        Function<double[],Double> rationalQuadraticLikelihood = val ->{
            RationalQuadraticKernel kernel = new RationalQuadraticKernel(val[0]);
            kernel.k = val[1];
            Matrix parametrizedK = MatrixUtilities.computeKMatrix(handler, kernel);
            if(parametrizedK.det() == 0){
                return Double.POSITIVE_INFINITY;
            }
            Matrix L = new CholeskyDecomposition(parametrizedK).getL();
            Matrix a;
//            try{
                a = parametrizedK.solve(ymat.transpose());
//            }
//            catch(RuntimeException e){
//                System.err.println(MatrixUtilities.printMatrix(parametrizedK));
//                throw e;
//            }
            Matrix Ld = MatrixUtilities.einsum1(ymat.transpose(), a).times(-0.5);
            Ld = Ld.minus(new Matrix(Ld.getRowDimension(), Ld.getColumnDimension(),MatrixUtilities.log(L).trace()));
            Ld = Ld.minus(new Matrix(Ld.getRowDimension(), Ld.getColumnDimension(),parametrizedK.getRowDimension()/2.0*Math.log(2*Math.PI)));
            return MatrixUtilities.sum(Ld);
        };
        Function<double[],double[]> rationalQuadraticGradientLikelihood = val -> {
            RationalQuadraticKernel kernel = new RationalQuadraticKernel(val[0]);
            kernel.k = val[1];
            Matrix parametrizedK = MatrixUtilities.computeKMatrix(handler, kernel);
            Matrix a = parametrizedK.solve(ymat.transpose());
            Matrix[] tmp = MatrixUtilities.einsum2(a,a);
            Matrix tmp2 = parametrizedK.solve(Matrix.identity(parametrizedK.getColumnDimension(), parametrizedK.getRowDimension()));
            for(int i = 0; i<tmp.length; i++){
                tmp[i] = tmp[i].minus(tmp2);
            }
            Matrix[] kGrad =kernel.grad(handler, val[1], val[0]);
            Matrix llgd = MatrixUtilities.einsum3(tmp, kGrad).times(0.5);
            return new double[]{MatrixUtilities.sumColumns(llgd).get(0,0),MatrixUtilities.sumColumns(llgd).get(1,0)};
        };
        SimplePointChecker<PointValuePair> checker = new SimplePointChecker<>(1e-5, 1e-5);
        MultivariateOptimizer mOptimizer = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES, checker);
        PointValuePair p = mOptimizer.optimize(new InitialGuess(new double[]{1.5,0.8}),new MaxEval(100000), new ObjectiveFunction(rationalQuadraticLikelihood::apply), GoalType.MAXIMIZE,new SearchInterval(0.0, 2),new ObjectiveFunctionGradient(rationalQuadraticGradientLikelihood::apply));
        alpha= p.getPoint()[0];
        k= p.getPoint()[1];
    }

    @Override
    public double gradient(double x, double y){
        double d = Math.abs(x-y);
        return Math.pow(d, 2) * Math.pow(Math.pow(d, 2)/2*alpha*Math.pow(k, 2)+1, -alpha-1)/Math.pow(k, 3);
    }

    public Matrix[] grad(HistoryHandler handler, double k, double alpha){
        Matrix[] matrices = new Matrix[2];
        int n = handler.getHistory().size();

        Matrix dists = new Matrix(handler.getHistory().size(), handler.getHistory().size());
        for (int i = 0; i < n; i++) {
            for(int j = 0; j<handler.getHistory().size(); j++) {
                double d = handler.getMove(i).getRound() - handler.getMove(j).getRound();
                dists.set(i,j,d*d);
            }
        }
        Matrix tmp = dists.times(1 / (2 * alpha * k * k));
        Matrix base = new Matrix(tmp.getRowDimension(), tmp.getColumnDimension(), 1.0).plus(tmp);
        Matrix K = MatrixUtilities.pow(base, -alpha);
        for (int w = 0; w < K.getRowDimension(); w++) {
            K.set(w, w, 1);
        }
        Matrix lsg = MatrixUtilities.div(MatrixUtilities.elementWiseMult(dists,K),(base.times(k*k)));
        Matrix ag = MatrixUtilities.elementWiseMult(K,(MatrixUtilities.log(base).times(-alpha).plus(MatrixUtilities.div(dists,base.times(2*k*k)))));
        matrices[1] = lsg;
        matrices[0] = ag;
        return matrices;
    }


    public double getK() {
        return k;
    }

    public double getAlpha() {
        return alpha;
    }
}

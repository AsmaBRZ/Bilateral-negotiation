package fr.kyriba.bidgeneration.strategymodeling;

import Jama.Matrix;
import fr.kyriba.bidgeneration.strategymodeling.kernel.Kernel;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.protocol.Attribute;
import fr.kyriba.protocol.DiscreteAttribute;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 2017-06-15  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**
 * P-CBU created on 13/06/17.
 */
/**Classe contenant toutes les fonctions utilitaires sur les matrices*/
public class MatrixUtilities {

    public static Matrix sqrt(Matrix initial){
        return map(initial, Math::sqrt);
    }

    public static Matrix log(Matrix initial){
        return map(initial, Math::log);
    }

    public static Matrix exp(Matrix initial){
        return map(initial, Math::exp);
    }

    public static Matrix map(Matrix initial, Function<Double, Double> toApply){
        double [][] initialArray = initial.getArray();
        double[][] root = new double[initial.getRowDimension()][initial.getColumnDimension()];
        for(int i = 0; i<initialArray.length; i++){
            for(int j = 0; j<initialArray[i].length; j++)
                root[i][j] = toApply.apply(initialArray[i][j]);
        }
        return new Matrix(root);
    }

    public static double sum(Matrix mat){
        double sum = 0;
        for(double[] tab:mat.getArray())
            for(double d:tab)
                sum+=d;
        return sum;
    }

    public static Matrix computeKMatrix(HistoryHandler handler, Kernel kernel){
        Matrix matrix;
        int n = handler.getHistory().size();
        double[][] matrixArray = new double[n][n];
        for(int i = 0; i<n; i++){
            for(int j=0; j<=i; j++){
                matrixArray[i][j] = kernel.apply((double) handler.getHistory().get(i).getRound(), (double) handler.getHistory().get(j).getRound());
                if(i!=j){
                    matrixArray[j][i] = matrixArray[i][j];
                }
            }
        }
        matrix = new Matrix(matrixArray);
        return matrix;
    }

    public static Matrix computeyMat(HistoryHandler handler) {
        int n = handler.getHistory().size();
        int c = 0;
        for(int i = 0; i<handler.getLastMove().getDomain().getAttributes().size(); i++){
            if(c < handler.getLastMove().getDomain().getAttributes().get(i).getNumber()){
                c = handler.getLastMove().getDomain().getAttributes().get(i).getNumber();
            }
        }
        double[][] yArray = new double[c][n];//FIXME Debug qui fait chier.
        for(int j = 0; j<c; j++) {
            for (int i = 0; i < n; i++) {
                if(handler.getLastMove().getDomain().getAttributes().get(j) instanceof DiscreteAttribute){
                    throw new IllegalStateException("Discrete attributes not handled yet");
                }
                int index = handler.getLastMove().getDomain().getAttributes().get(j).getNumber();
                double doubleMove;
                if(handler.getMove(i).getValue(index) instanceof Integer){
                    int move = (Integer)handler.getMove(i).getValue(index);
                    doubleMove = (double) move;
                }
                else{
                    doubleMove = (Double) handler.getMove(i).getValue(index);
                }
                    yArray[j][i] = doubleMove;
            }
        }
        return new Matrix(yArray);
    }

//    public static Matrix[] computeKGradientMatrix(HistoryHandler handler, Kernel kernel) {
//        Matrix[] matrices;
//        int n = handler.getHistory().size();
//        for(int l = 0; l<; l++) {
//            double[][] matrixArray = new double[n][n];
//            for (int i = 0; i < n; i++) {
//                Matrix dists = new Matrix(handler.getHistory().size(), handler.getHistory().size());
//                for(int index1=0; index1<handler.getHistory().size(); index1++) {
//                    for (int index2 = 0; index2 < handler.getHistory().size(); index2++) {
//                        dists.set(index1, index2, (handler.getMove(index1).getRound()-handler.getMove(index2).getRound())*handler.getMove(index1).getRound()-handler.getMove(index2).getRound());
//                    }
//                }
//                Matrix tmp = dists.times(1/(2*kernel))
//                MatrixUtilities.div(dists.times(computeKMatrix(handler, kernel)),);
//                for (int j = 0; j <= i; j++) {
//                    matrixArray[i][j] = kernel.gradient((double) handler.getHistory().get(i).getRound(), (double) handler.getHistory().get(j).getRound());
//                    if (i != j) {
//                        matrixArray[j][i] = matrixArray[i][j];
//                    }
//                }
//            }
//            matrix = new Matrix(matrixArray);
//        }
//        return matrix;
//    }

    public static String printMatrix(Matrix mat){
        int m = mat.getColumnDimension();
        int n = mat.getRowDimension();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<n; i++){
            for(int j = 0; j<m; j++){
                builder.append(mat.get(i, j));
                builder.append("\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static Matrix sumColumns(Matrix matrix) {
        Matrix S = new Matrix(matrix.getColumnDimension(), 1);
        for(int i =0; i<matrix.getColumnDimension(); i++){
            double sum = 0.0;
            for(int j = 0; j<matrix.getRowDimension(); j++){
                sum+=matrix.get(j,i);
            }
            S.set(i, 0, sum);
        }
        return S;
    }
    public static Matrix diag(double[] array) {
        double[][] matArray = new double[array.length][array.length];
        for(int i =0; i<array.length; i++){
            matArray[i][i] = array[i];
        }
        return new Matrix(matArray);
    }

    /**Einstein summation ik, ik → k*/
    public static Matrix einsum1(Matrix a, Matrix b){
        if(a.getRowDimension()!=b.getRowDimension()){
            throw new IllegalArgumentException("a row dimension should be identical to b row dimension");
        }
        else{
            Matrix result = new Matrix(1,a.getColumnDimension());
            for(int i=0; i<a.getColumnDimension(); i++){
                for(int j = 0; j<a.getRowDimension(); j++){
                        result.set(0,i,result.get(0,i)+a.get(j,i)*b.get(j, i));
                }
            }
            return result;
        }
    }

    /**Einstein summation ik, jk → ijk*/
    public static Matrix[] einsum2(Matrix a, Matrix b){
        if(a.getColumnDimension() != b.getColumnDimension()){
            throw new IllegalArgumentException("a dimension should be identical to b dimension");
        }
        Matrix[] result = new Matrix[a.getColumnDimension()];
        for(int i = 0; i<a.getColumnDimension(); i++){
            result[i] = new Matrix(a.getRowDimension(), b.getRowDimension());
            for(int j = 0; j<a.getRowDimension(); j++){
                for(int k = 0; k<b.getRowDimension(); k++) {
                    result[i].set(j, k, a.get(j,i)*b.get(k,i));
                }
            }
        }
        return result;
    }

    /**Einstein summation ijk, ijl → kl*/
    public static Matrix einsum3(Matrix[] a, Matrix[] b){
        int row = a[0].getRowDimension();
        int column = a[0].getColumnDimension();

        for(Matrix m: a){
            if(m.getRowDimension()!= row || m.getColumnDimension() != column){
                throw new IllegalArgumentException("The matrices should have the same dimension. First matrix: " + row + "," + column + "this matrix" + m.getRowDimension() + "," + m.getColumnDimension());
            }
        }
        for(Matrix m: b){
            if(m.getRowDimension()!= row || m.getColumnDimension() != column){
                throw new IllegalArgumentException("The matrices should have the same dimension. First matrix: " + row + "," + column + "this matrix" + m.getRowDimension() + "," + m.getColumnDimension());
            }
        }
        Matrix result = new Matrix(a.length, b.length);
        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < b.length; j++){
                double val = 0;
                for (int k = 0; k<row; k++){
                    for(int l = 0; l < column; l++){
                        val += a[i].get(k, l) * b[j].get(k, l);
                    }
                }
                result.set(i, j, val);
            }
        }
        return result;
    }

    public static Matrix pow(Matrix matrix, double e) {
        Matrix m = new Matrix(matrix.getRowDimension(),matrix.getColumnDimension());
        for(int i = 0; i<matrix.getRowDimension(); i++){
            for(int j = 0; j<matrix.getColumnDimension(); j++){
                m.set(i,j,Math.pow(matrix.get(i, j), e));
            }
        }
        return m;
    }

    public static Matrix div(Matrix dividend, Matrix divider) {
        if(dividend.getColumnDimension() != divider.getColumnDimension() && dividend.getRowDimension()!= divider.getRowDimension()){
            throw new IllegalArgumentException("Matrix dimensions of dividend and divider must agree, found: " +dividend.getRowDimension() + "," + dividend.getColumnDimension()+ ";" + divider.getRowDimension() + "," + divider.getColumnDimension());
        }
        Matrix quotient = new Matrix(dividend.getColumnDimension(), dividend.getRowDimension());
        for(int i = 0; i<dividend.getColumnDimension(); i++){
            for(int j = 0; j<dividend.getRowDimension(); j++){
                quotient.set(i,j,dividend.get(i,j)/divider.get(i,j));
            }
        }

        return quotient;
    }

    public static Matrix elementWiseMult(Matrix a, Matrix b){
        if(a.getColumnDimension() != b.getColumnDimension() || a.getRowDimension() != b.getRowDimension()){
            throw new IllegalArgumentException("Matrix dimensiosn do not agree a: " + a.getRowDimension() + "," + a.getColumnDimension() + "; b: " + b.getRowDimension() + "," + b.getColumnDimension());
        }
        Matrix result = new Matrix(a.getRowDimension(), a.getColumnDimension());
        for(int i = 0; i<a.getRowDimension(); i++){
            for(int j = 0 ; j<a.getColumnDimension(); j++){
                result.set (i, j, a.get(i, j)*b.get(i, j));
            }
        }
        return result;
    }
}

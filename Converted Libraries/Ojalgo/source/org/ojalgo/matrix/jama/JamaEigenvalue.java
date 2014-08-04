/*
 * Copyright 1997-2014 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.matrix.jama;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.ComplexAggregator;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * This class adapts JAMA's EigenvalueDecomposition to ojAlgo's {@linkplain Eigenvalue} interface.
 * 
 * @author apete
 */
public abstract class JamaEigenvalue extends JamaAbstractDecomposition implements Eigenvalue<Double> {

    public static final class General extends JamaEigenvalue {

        public General() {
            super();
        }

        @Override
        boolean compute(final Matrix aDelegate) {

            this.setDelegate(new EigenvalueDecomposition(aDelegate));

            return true;
        }
    }

    public static final class Nonsymmetric extends JamaEigenvalue {

        public Nonsymmetric() {
            super();
        }

        @Override
        boolean compute(final Matrix aDelegate) {

            this.setDelegate(new EigenvalueDecomposition(aDelegate, false));

            return true;
        }
    }

    public static final class Symmetric extends JamaEigenvalue {

        public Symmetric() {
            super();
        }

        @Override
        boolean compute(final Matrix aDelegate) {

            this.setDelegate(new EigenvalueDecomposition(aDelegate, true));

            return true;
        }
    }

    private EigenvalueDecomposition myDelegate;

    private JamaMatrix myInverse;

    /**
     * Not recommended to use this constructor directly. Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.EigenvalueDecomposition#makeJama()} instead.
     */

    protected JamaEigenvalue() {
        super();
    }

    public Double calculateDeterminant(final Access2D<Double> matrix) {
        this.compute(matrix);
        return this.getDeterminant();
    }

    public boolean compute(final Access2D<?> matrix, final boolean eigenvaluesOnly) {
        return this.compute(matrix);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext context) {
        return MatrixUtils.equals(aStore, this, context);
    }

    public JamaMatrix getD() {
        return new JamaMatrix(myDelegate.getD());
    }

    public Double getDeterminant() {

        final AggregatorFunction<ComplexNumber> tmpVisitor = ComplexAggregator.getCollection().product();

        this.getEigenvalues().visitAll(tmpVisitor);

        return tmpVisitor.getNumber().doubleValue();
    }

    public Array1D<ComplexNumber> getEigenvalues() {

        final double[] tmpRe = myDelegate.getRealEigenvalues();
        final double[] tmpIm = myDelegate.getImagEigenvalues();

        final Array1D<ComplexNumber> retVal = Array1D.COMPLEX.makeZero(tmpRe.length);

        for (int i = 0; i < retVal.size(); i++) {
            retVal.set(i, ComplexNumber.makeRectangular(tmpRe[i], tmpIm[i]));
        }

        retVal.sortDescending();

        return retVal;
    }

    @Override
    public JamaMatrix getInverse() {

        if (myInverse == null) {

            final double[][] tmpQ1 = this.getV().getDelegate().getArray();
            final double[] tmpEigen = myDelegate.getRealEigenvalues();

            final Matrix tmpMtrx = new Matrix(tmpEigen.length, tmpQ1.length);

            for (int i = 0; i < tmpEigen.length; i++) {
                if (TypeUtils.isZero(tmpEigen[i])) {
                    for (int j = 0; j < tmpQ1.length; j++) {
                        tmpMtrx.set(i, j, PrimitiveMath.ZERO);
                    }
                } else {
                    for (int j = 0; j < tmpQ1.length; j++) {
                        tmpMtrx.set(i, j, tmpQ1[j][i] / tmpEigen[i]);
                    }
                }
            }

            myInverse = new JamaMatrix(this.getV().getDelegate().times(tmpMtrx));
        }

        return myInverse;
    }

    public ComplexNumber getTrace() {

        final AggregatorFunction<ComplexNumber> tmpVisitor = ComplexAggregator.getCollection().sum();

        this.getEigenvalues().visitAll(tmpVisitor);

        return tmpVisitor.getNumber();
    }

    public JamaMatrix getV() {
        return new JamaMatrix(myDelegate.getV());
    }

    public boolean isAspectRatioNormal() {
        return true;
    }

    public boolean isComputed() {
        return myDelegate != null;
    }

    public boolean isFullSize() {
        return true;
    }

    public boolean isHermitian() {
        return myDelegate.isSymmetric();
    }

    public boolean isOrdered() {
        return !this.isHermitian();
    }

    public boolean isSolvable() {
        return (myDelegate != null) && myDelegate.isSymmetric();
    }

    public MatrixStore<Double> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    public void reset() {
        myDelegate = null;
        myInverse = null;
    }

    @Override
    public JamaMatrix solve(final Access2D<Double> rhs) {
        return this.getInverse().multiplyRight((Access1D<Double>) rhs);
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    final void setDelegate(final EigenvalueDecomposition newDelegate) {
        myDelegate = newDelegate;
    }

    @Override
    Matrix solve(final Matrix aRHS) {
        // TODO Auto-generated method stub
        return null;
    }

}

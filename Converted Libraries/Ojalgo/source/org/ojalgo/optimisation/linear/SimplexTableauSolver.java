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
package org.ojalgo.optimisation.linear;

import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.PrimitiveFunction.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.optimisation.Expression.Index;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * SimplexTableauSolver
 *
 * @author apete
 */
final class SimplexTableauSolver extends LinearSolver {

    static final class PivotPoint {

        private int myRowObjective = -1;

        private final SimplexTableauSolver mySolver;
        int col = -1;
        int row = -1;

        @SuppressWarnings("unused")
        private PivotPoint() {
            this(null);
            ProgrammingError.throwForIllegalInvocation();
        }

        PivotPoint(final SimplexTableauSolver solver) {

            super();

            mySolver = solver;

            myRowObjective = mySolver.countConstraints() + 1;

            this.reset();
        }

        int getColRHS() {
            return mySolver.countVariables();
        }

        int getRowObjective() {
            return myRowObjective;
        }

        boolean isPhase1() {
            return myRowObjective == (mySolver.countConstraints() + 1);
        }

        boolean isPhase2() {
            return myRowObjective == mySolver.countConstraints();
        }

        double objective() {
            return mySolver.getTableauElement(this.getRowObjective(), mySolver.countVariables());
        }

        int phase() {
            return myRowObjective == mySolver.countConstraints() ? 2 : 1;
        }

        void reset() {
            row = -1;
            col = -1;
        }

        void switchToPhase2() {
            myRowObjective = mySolver.countConstraints();
        }

    }

    private final int[] myBasis;
    private final PivotPoint myPoint;
    private final PrimitiveDenseStore myTransposedTableau;

    SimplexTableauSolver(final ExpressionsBasedModel aModel, final Optimisation.Options solverOptions, final LinearSolver.Builder matrices) {

        super(aModel, solverOptions, matrices);

        myPoint = new PivotPoint(this);

        final int tmpConstraintsCount = this.countConstraints();

        final MatrixStore.Builder<Double> tmpTableauBuilder = ZeroStore.makePrimitive(1, 1).builder();
        tmpTableauBuilder.left(matrices.getC().transpose());

        if (tmpConstraintsCount >= 1) {
            tmpTableauBuilder.above(matrices.getAE(), matrices.getBE());
        }
        tmpTableauBuilder.below(1);
        //myTransposedTableau = (PrimitiveDenseStore) tmpTableauBuilder.build().transpose().copy();
        myTransposedTableau = PrimitiveDenseStore.FACTORY.transpose(tmpTableauBuilder.build());

        final Result tmpKickStarter = matrices.getKickStarter();
        final int[] tmpBasis = tmpKickStarter != null ? tmpKickStarter.getBasis() : null;
        if ((tmpBasis != null) && (tmpBasis.length == tmpConstraintsCount)) {
            myBasis = tmpBasis;
            this.include(tmpBasis);
        } else {
            myBasis = AccessUtils.makeIncreasingRange(-tmpConstraintsCount, tmpConstraintsCount);
        }

        for (int i = 0; i < tmpConstraintsCount; i++) {
            if (myBasis[i] < 0) {
                myTransposedTableau.caxpy(NEG, i, myPoint.getRowObjective(), 0);
            }
        }

        if (this.isDebug() && this.isTableauPrintable()) {
            this.logDebugTableau("Tableau Created");
        }
    }

    public Result solve(final Result kickStarter) {

        while (this.needsAnotherIteration()) {

            this.performIteration(myPoint.row, myPoint.col);

            if (this.isDebug() && this.isTableauPrintable()) {
                this.logDebugTableau("Tableau Iteration");
            }
        }

        return this.buildResult();
    }

    private int countBasicArtificials() {
        int retVal = 0;
        final int tmpLength = myBasis.length;
        for (int i = 0; i < tmpLength; i++) {
            if (myBasis[i] < 0) {
                retVal++;
            }
        }
        return retVal;
    }

    private final boolean isTableauPrintable() {
        return myTransposedTableau.count() <= 512L;
    }

    private final void logDebugTableau(final String aMessage) {
        this.debug(aMessage + "; Basics: " + Arrays.toString(myBasis));
        this.debug(myTransposedTableau.transpose());
    }

    /**
     * Extract solution MatrixStore from the tableau
     */
    @Override
    protected PhysicalStore<Double> extractSolution() {

        final int tmpCountVariables = this.countVariables();

        this.resetX();

        final int tmpLength = myBasis.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpBasisIndex = myBasis[i];
            if (tmpBasisIndex >= 0) {
                this.setX(tmpBasisIndex, myTransposedTableau.doubleValue(tmpCountVariables, i));
            }
        }
        final PhysicalStore<Double> tmpTableauSolution = this.getX();

        final ExpressionsBasedModel tmpModel = this.getModel();

        if (tmpModel != null) {

            final List<Variable> tmpFreeVariables = tmpModel.getFreeVariables();
            final Set<Index> tmpFixedVariables = tmpModel.getFixedVariables();

            final PrimitiveDenseStore tmpModelSolution = PrimitiveDenseStore.FACTORY.makeZero(tmpFixedVariables.size() + tmpFreeVariables.size(), 1);

            final int tmpModelSolutionSize = (int) tmpModelSolution.count();
            final int tmpVariablesSize = tmpModel.getVariables().size();
            if (tmpModelSolutionSize != tmpVariablesSize) {
                throw new IllegalStateException();
            }

            for (final Index tmpFixed : tmpFixedVariables) {
                tmpModelSolution.set(tmpFixed.index, 0, tmpModel.getVariable(tmpFixed.index).getValue().doubleValue());
            }

            final List<Variable> tmpPositive = tmpModel.getPositiveVariables();
            for (int p = 0; p < tmpPositive.size(); p++) {
                final int tmpIndex = tmpModel.indexOf(tmpPositive.get(p));
                tmpModelSolution.set(tmpIndex, 0, tmpTableauSolution.doubleValue(p));
            }

            final List<Variable> tmpNegative = tmpModel.getNegativeVariables();
            for (int n = 0; n < tmpNegative.size(); n++) {
                final int tmpIndex = tmpModel.indexOf(tmpNegative.get(n));
                tmpModelSolution.set(tmpIndex, 0, tmpModelSolution.doubleValue(tmpIndex) - tmpTableauSolution.doubleValue(tmpPositive.size() + n));
            }

            return tmpModelSolution;

        } else {

            return tmpTableauSolution;
        }

    }

    @Override
    protected boolean initialise(final Result kickStart) {
        return false;
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean needsAnotherIteration() {

        if (this.isDebug()) {
            this.debug("\nNeeds Another Iteration? Phase={} Artificials={} Objective={}", myPoint.phase(), this.countBasisDeficit(), myPoint.objective());
        }

        boolean retVal = false;
        myPoint.reset();

        if (myPoint.isPhase1()) {

            final double tmpPhaseOneValue = myTransposedTableau.doubleValue(this.countVariables(), myPoint.getRowObjective());

            if (options.objective.isZero(tmpPhaseOneValue)) {

                if (this.isDebug()) {
                    this.debug("\nSwitching to Phase2 with {} artificial variable(s) still in the basis.\n", this.countBasicArtificials());
                    this.debug("Reduced artificial costs:\n{}", this.sliceTableauRow(myPoint.getRowObjective()).copy(this.getExcluded()));
                }

                myPoint.switchToPhase2();
            }
        }

        myPoint.col = this.findNextPivotCol();

        if (myPoint.col >= 0) {

            myPoint.row = this.findNextPivotRow();

            if (myPoint.row >= 0) {

                retVal = true;

            } else {

                if (myPoint.isPhase2()) {

                    this.setState(State.UNBOUNDED);
                    retVal = false;

                } else {

                    this.setState(State.INFEASIBLE);
                    retVal = false;
                }
            }

        } else {

            if (myPoint.isPhase1()) {

                this.setState(State.INFEASIBLE);
                retVal = false;

            } else {

                this.setState(State.OPTIMAL);
                retVal = false;
            }
        }

        if (this.isDebug()) {
            if (retVal) {
                this.debug("\n==>>\tRow: {},\tExit: {},\tColumn/Enter: {}.\n", myPoint.row, myBasis[myPoint.row], myPoint.col);
            } else {
                this.debug("\n==>>\tNo more iterations needed/possible.\n");
            }
        }

        return retVal;
    }

    @Override
    protected boolean validate() {

        final boolean retVal = true;
        this.setState(State.VALID);

        return retVal;
    }

    int findNextPivotCol() {

        final int[] tmpExcluded = this.getExcluded();

        if (this.isDebug()) {
            if (options.validate) {
                this.debug("\nfindNextPivotCol (index of most negative value) among these:\n{}",
                        this.sliceTableauRow(myPoint.getRowObjective()).copy(tmpExcluded));
            } else {
                this.debug("\nfindNextPivotCol");
            }
        }

        int retVal = -1;

        double tmpVal;
        double tmpMinVal = myPoint.isPhase2() ? -options.problem.error() : ZERO;
        //double tmpMinVal = ZERO;

        int tmpCol;

        for (int e = 0; e < tmpExcluded.length; e++) {
            tmpCol = tmpExcluded[e];
            tmpVal = myTransposedTableau.doubleValue(tmpCol, myPoint.getRowObjective());
            if (tmpVal < tmpMinVal) {
                retVal = tmpCol;
                tmpMinVal = tmpVal;
                if (this.isDebug()) {
                    this.debug("Col: {}\t=>\tReduced Contribution Weight: {}.", tmpCol, tmpVal);
                }
            }
        }

        return retVal;
    }

    int findNextPivotRow() {

        final int tmpNumerCol = myPoint.getColRHS();
        final int tmpDenomCol = myPoint.col;

        if (this.isDebug()) {
            if (options.validate) {
                final Array1D<Double> tmpNumerators = this.sliceTableauColumn(tmpNumerCol);
                final Array1D<Double> tmpDenominators = this.sliceTableauColumn(tmpDenomCol);
                final Array1D<Double> tmpRatios = tmpNumerators.copy();
                tmpRatios.modifyMatching(DIVIDE, tmpDenominators);
                this.debug("\nfindNextPivotRow (smallest positive ratio) among these:\nNumerators={}\nDenominators={}\nRatios={}", tmpNumerators,
                        tmpDenominators, tmpRatios);
            } else {
                this.debug("\nfindNextPivotRow");
            }
        }

        int retVal = -1;
        double tmpNumer = NaN, tmpDenom = NaN, tmpRatio = NaN;
        double tmpMinRatio = MAX_VALUE;

        final int tmpConstraintsCount = this.countConstraints();

        final boolean tmpPhase2 = myPoint.isPhase2();

        for (int i = 0; i < tmpConstraintsCount; i++) {

            final boolean tmpSpecialCase = tmpPhase2 && (myBasis[i] < 0);

            tmpDenom = myTransposedTableau.doubleValue(tmpDenomCol, i);

            if (options.problem.isZero(tmpDenom)) {

                tmpRatio = MAX_VALUE;

            } else {

                // Should always be >=0.0, but very small numbers may "accidentally" get a negative sign.
                tmpNumer = Math.abs(myTransposedTableau.doubleValue(tmpNumerCol, i));

                if (tmpSpecialCase) {
                    if (options.problem.isSmallComparedTo(tmpDenom, tmpNumer)) {
                        tmpRatio = PrimitiveMath.MACHINE_DOUBLE_ERROR;
                    } else {
                        tmpRatio = MAX_VALUE;
                    }
                } else {
                    tmpRatio = tmpNumer / tmpDenom;
                }
            }

            if ((tmpSpecialCase || (tmpDenom > ZERO)) && (tmpRatio >= ZERO) && (tmpRatio < tmpMinRatio)) {

                retVal = i;
                tmpMinRatio = tmpRatio;

                if (this.isDebug()) {
                    this.debug("Row: {}\t=>\tRatio: {},\tNumerator/RHS: {}, \tDenominator/Pivot: {}.", i, tmpRatio, tmpNumer, tmpDenom);
                }
            }
        }

        return retVal;
    }

    /**
     * It's transposed for you!
     */
    double getTableauElement(final int aRow, final int aCol) {
        return myTransposedTableau.doubleValue(aCol, aRow);
    }

    void performIteration(final int aPivotRow, final int aPivotCol) {

        final double tmpPivotElement = this.getTableauElement(aPivotRow, aPivotCol);
        final double tmpPivotRHS = this.getTableauElement(aPivotRow, myPoint.getColRHS());

        for (int i = 0; i <= myPoint.getRowObjective(); i++) {
            if (i != aPivotRow) {

                final double tmpPivotColVal = this.getTableauElement(i, aPivotCol);

                if (tmpPivotColVal != ZERO) {
                    myTransposedTableau.caxpy(-tmpPivotColVal / tmpPivotElement, aPivotRow, i, 0);
                }
            }
        }

        if (Math.abs(tmpPivotElement) < ONE) {
            myTransposedTableau.modifyColumn(0, aPivotRow, DIVIDE.second(tmpPivotElement));
        } else if (tmpPivotElement != ONE) {
            myTransposedTableau.modifyColumn(0, aPivotRow, MULTIPLY.second(ONE / tmpPivotElement));
        }

        if (this.isDebug()) {
            this.debug("Iteration Point <{},{}>\tPivot: {} => {}\tRHS: {} => {}.", aPivotRow, aPivotCol, tmpPivotElement,
                    this.getTableauElement(aPivotRow, aPivotCol), tmpPivotRHS, this.getTableauElement(aPivotRow, myPoint.getColRHS()));
        }

        final int tmpOld = myBasis[aPivotRow];
        if (tmpOld >= 0) {
            this.exclude(tmpOld);
        }
        final int tmpNew = aPivotCol;
        if (tmpNew >= 0) {
            this.include(tmpNew);
        }
        myBasis[aPivotRow] = aPivotCol;

        if (options.validate) {

            // Right-most column of the tableau
            final Array1D<Double> tmpRHS = this.sliceTableauColumn(myPoint.getColRHS());

            final AggregatorFunction<Double> tmpMinAggr = PrimitiveAggregator.getCollection().minimum();
            tmpRHS.visitAll(tmpMinAggr);
            final double tmpMinVal = tmpMinAggr.doubleValue();

            if ((tmpMinVal < ZERO) && !options.problem.isZero(tmpMinVal)) {
                this.debug("\nNegative RHS! {}", tmpMinVal);
                if (this.isDebug()) {
                    this.debug("Entire RHS columns: {}\n", tmpRHS);
                }
            }

            final ExpressionsBasedModel tmpModel = this.getModel();

            if ((tmpModel != null) && (myPoint.isPhase2())) {
                final Result tmpResult = this.buildResult();
                if (!tmpModel.validate(tmpResult, options.slack)) {
                    if (this.isDebug()) {
                        this.debug("Model validation failed!\n");
                    }
                    this.setState(State.FAILED);
                }
            }
        }
    }

    /**
     * It's transposed for you, and only returns the part of the column corresponding to the constraints - not the
     * objective(s).
     */
    Array1D<Double> sliceTableauColumn(final int aCol) {
        return myTransposedTableau.asArray2D().sliceRow(aCol, 0).subList(0, this.countConstraints());
    }

    /**
     * It's transposed for you, and only returns the part of the row corresponding to the variables - not the RHS.
     */
    Array1D<Double> sliceTableauRow(final int aRow) {
        return myTransposedTableau.asArray2D().sliceColumn(0, aRow).subList(0, this.countVariables());
    }

}

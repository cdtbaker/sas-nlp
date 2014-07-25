package org.ojalgo.optimisation.quadratic;
import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.PrimitiveFunction.*;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.store.AboveBelowStore;
import org.ojalgo.matrix.store.LeftRightStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
/** 
 * @author apete
 */
final class LagrangeSolver extends QuadraticSolver {
  private final LU<Double> myLU=LUDecomposition.makePrimitive();
  private final SingularValue<Double> mySingularValue=SingularValueDecomposition.makePrimitive();
  LagrangeSolver(  final ExpressionsBasedModel aModel,  final Optimisation.Options solverOptions,  final QuadraticSolver.Builder matrices){
    super(aModel,solverOptions,matrices);
  }
  private void extractSolution(  final Builder aSolver){
    final MatrixStore<Double> tmpSolutionX=aSolver.getX();
    final int tmpCountVariables=this.countVariables();
    final int tmpCountEqualityConstraints=this.countEqualityConstraints();
    for (int i=0; i < tmpCountVariables; i++) {
      this.setX(i,tmpSolutionX.doubleValue(i));
    }
    for (int i=0; i < tmpCountEqualityConstraints; i++) {
      this.setLE(i,tmpSolutionX.doubleValue(tmpCountVariables + i));
    }
  }
  private Builder makeBuilder(  final boolean addSmallDiagonal){
    Builder tmpBuilder=null;
    MatrixStore<Double> tmpQ=this.getQ();
    final MatrixStore<Double> tmpC=this.getC();
    if (addSmallDiagonal) {
      final PhysicalStore<Double> tmpCopyQ=tmpQ.copy();
      final double tmpLargest=tmpCopyQ.aggregateAll(Aggregator.LARGEST);
      final double tmpRelativelySmall=MACHINE_DOUBLE_ERROR * tmpLargest;
      final double tmpPracticalLimit=MACHINE_DOUBLE_ERROR + IS_ZERO;
      final double tmpSmallToAdd=Math.max(tmpRelativelySmall,tmpPracticalLimit);
      final UnaryFunction<Double> tmpFunc=ADD.second(tmpSmallToAdd);
      tmpCopyQ.modifyDiagonal(0,0,tmpFunc);
      tmpQ=tmpCopyQ;
    }
    if (this.hasEqualityConstraints()) {
      final MatrixStore<Double> tmpAE=this.getAE();
      final MatrixStore<Double> tmpBE=this.getBE();
      final int tmpZeroSize=tmpAE.getRowDim();
      final MatrixStore<Double> tmpUpperLeftAE=tmpQ;
      final MatrixStore<Double> tmpUpperRightAE=tmpAE.builder().transpose().build();
      final MatrixStore<Double> tmpLowerLefAE=tmpAE;
      final MatrixStore<Double> tmpLowerRightAE=ZeroStore.makePrimitive(tmpZeroSize,tmpZeroSize);
      final MatrixStore<Double> tmpSubAE=new AboveBelowStore<Double>(new LeftRightStore<Double>(tmpUpperLeftAE,tmpUpperRightAE),new LeftRightStore<Double>(tmpLowerLefAE,tmpLowerRightAE));
      final MatrixStore<Double> tmpUpperBE=tmpC;
      final MatrixStore<Double> tmpLowerBE=tmpBE;
      final MatrixStore<Double> tmpSubBE=new AboveBelowStore<Double>(tmpUpperBE,tmpLowerBE);
      tmpBuilder=new Builder().equalities(tmpSubAE,tmpSubBE);
    }
 else {
      tmpBuilder=new Builder().equalities(tmpQ,tmpC);
    }
    return tmpBuilder;
  }
  private void performIteration(  final Builder builder){
    final MatrixStore<Double> tmpAE=builder.getAE();
    final MatrixStore<Double> tmpBE=builder.getBE();
    final DecompositionStore<Double> tmpX=builder.getX();
    myLU.compute(tmpAE);
    if (myLU.isSolvable()) {
      if (this.isDebug()) {
        this.logDebug("LU solvable");
      }
      final MatrixStore<Double> tmpSolution=myLU.solve(tmpBE,tmpX);
      this.setState(State.DISTINCT);
      if (tmpSolution != tmpX) {
        tmpX.fillMatching(tmpSolution);
      }
    }
 else {
      if (this.isDebug()) {
        this.logDebug("LU not solvable, trying SVD");
      }
      mySingularValue.compute(tmpAE);
      if (mySingularValue.isSolvable()) {
        if (this.isDebug()) {
          this.logDebug("SVD solvable");
        }
        final MatrixStore<Double> tmpSolution=mySingularValue.solve(tmpBE,tmpX);
        this.setState(State.OPTIMAL);
        if (tmpSolution != tmpX) {
          tmpX.fillMatching(tmpSolution);
        }
        final AggregatorFunction<Double> tmpFrobNormCalc=PrimitiveAggregator.getCollection().norm2();
        final MatrixStore<Double> tmpSlack=builder.getSE();
        tmpSlack.visitAll(tmpFrobNormCalc);
        if (!options.slack.isZero(tmpFrobNormCalc.doubleValue())) {
          if (this.isDebug()) {
            this.logDebug("Solution not accurate enough!");
          }
          this.resetX();
          this.setState(State.INFEASIBLE);
        }
      }
 else {
        if (this.isDebug()) {
          this.logDebug("SVD not solvable");
        }
        this.resetX();
        this.setState(State.INFEASIBLE);
      }
    }
  }
  @Override protected boolean initialise(  final Result kickStart){
    myLU.reset();
    mySingularValue.reset();
    return true;
  }
  @Override protected boolean needsAnotherIteration(){
    return this.countIterations() < 1;
  }
  @Override protected void performIteration(){
    Builder tmpBuilder=this.makeBuilder(false);
    this.performIteration(tmpBuilder);
    if (this.getState().isFeasible()) {
      this.extractSolution(tmpBuilder);
      this.setState(State.OPTIMAL);
    }
 else {
      tmpBuilder=this.makeBuilder(true);
      this.performIteration(tmpBuilder);
      if (this.getState().isFeasible()) {
        this.extractSolution(tmpBuilder);
        this.setState(State.OPTIMAL);
      }
 else {
        this.resetX();
        this.setState(State.INFEASIBLE);
      }
    }
  }
  @Override protected boolean validate(){
    boolean retVal=true;
    this.setState(State.VALID);
    try {
      final MatrixStore<Double> tmpQ=this.getQ();
      final Cholesky<Double> tmpCholesky=CholeskyDecomposition.makePrimitive();
      tmpCholesky.compute(tmpQ,true);
      if (!tmpCholesky.isSPD()) {
        final Eigenvalue<Double> tmpEigenvalue=EigenvalueDecomposition.makePrimitive(true);
        tmpEigenvalue.compute(tmpQ,true);
        final MatrixStore<Double> tmpD=tmpEigenvalue.getD();
        for (int ij=0; retVal && (ij < tmpD.getMinDim()); ij++) {
          if (tmpD.doubleValue(ij,ij) < ZERO) {
            retVal=false;
            this.setState(State.INVALID);
          }
        }
      }
      if (retVal) {
      }
    }
 catch (    final Exception ex) {
      retVal=false;
      this.setState(State.FAILED);
    }
    return retVal;
  }
}

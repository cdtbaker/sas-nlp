package org.ojalgo.optimisation.quadratic;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.decomposition.SingularValueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.ExpressionsBasedModel;
final class NullspaceSolver extends QuadraticSolver {
  public NullspaceSolver(  final ExpressionsBasedModel aModel,  final Options solverOptions,  final QuadraticSolver.Builder matrices){
    super(aModel,solverOptions,matrices);
  }
  @Override protected boolean initialise(  final Result kickStart){
    return true;
  }
  @Override protected boolean needsAnotherIteration(){
    return false;
  }
  @Override protected void performIteration(){
    this.resetX();
    this.resetLE();
    this.resetLI();
    this.setState(State.FAILED);
    final MatrixStore<Double> tmpQ=this.getQ();
    final MatrixStore<Double> tmpC=this.getC();
    final MatrixStore<Double> tmpAE=this.getAE();
    final MatrixStore<Double> tmpBE=this.getBE();
    final QR<Double> tmpQR=QRDecomposition.makePrimitive();
    tmpQR.compute(tmpAE.transpose(),true);
    final MatrixStore<Double> tmpQRQ1=tmpQR.getQ().builder().columns(0,tmpQR.getRank()).build();
    final MatrixStore<Double> tmpQRQ2=tmpQR.getQ().builder().columns(tmpQR.getRank(),tmpQR.getQ().getColDim()).build();
    final MatrixStore<Double> tmpQRR1=tmpQR.getR().builder().rows(0,tmpQR.getRank()).build();
    final SingularValue<Double> tmpFeasibleQR=SingularValueDecomposition.makePrimitive();
    tmpFeasibleQR.compute(tmpAE);
    final MatrixStore<Double> tmpFeasibleAE=tmpFeasibleQR.solve(tmpBE).copy();
    final MatrixStore<Double> tmpShouldBeBE=tmpAE.multiplyRight(tmpFeasibleAE).copy();
    if (!tmpShouldBeBE.equals(tmpBE,options.slack)) {
      throw new IllegalStateException();
    }
    final MatrixStore<Double> tmpNullspaceAE=tmpQRQ2.copy();
    if (tmpNullspaceAE.countColumns() > 0L) {
      final MatrixStore<Double> tmpSubAE=tmpQ.multiplyLeft(tmpNullspaceAE.transpose()).multiplyRight(tmpNullspaceAE).copy();
      final PhysicalStore<Double> tmpCopy=tmpQ.multiplyRight(tmpFeasibleAE).copy();
      tmpCopy.fillMatching(tmpCopy,PrimitiveFunction.SUBTRACT,tmpC);
      tmpCopy.modifyAll(PrimitiveFunction.NEGATE);
      final MatrixStore<Double> tmpSubBE=tmpCopy.multiplyLeft(tmpNullspaceAE.transpose()).copy();
      final QR<Double> tmpSubQR=QRDecomposition.makePrimitive();
      tmpSubQR.compute(tmpSubAE);
      if (tmpSubQR.isSolvable()) {
        final PhysicalStore<Double> tmpSubX=tmpSubQR.solve(tmpSubBE).copy();
        final AggregatorFunction<Double> tmpNorm=Aggregator.NORM2.getPrimitiveFunction();
        tmpSubX.visitAll(tmpNorm);
        if (tmpNorm.doubleValue() > 0.0) {
          final PhysicalStore<Double> tmpX=tmpFeasibleAE.copy();
          final MatrixStore<Double> tmpDiffX=tmpNullspaceAE.multiplyRight(tmpSubX).copy();
          tmpX.maxpy(1.0,tmpDiffX);
          if (tmpAE.multiplyRight(tmpX).equals(tmpBE,options.slack)) {
            this.getX().fillMatching(tmpX);
            this.setState(State.OPTIMAL);
          }
        }
 else {
          this.getX().fillMatching(tmpFeasibleAE);
          this.setState(State.OPTIMAL);
        }
      }
 else {
        this.getX().fillMatching(tmpFeasibleAE);
        this.setState(State.FEASIBLE);
      }
    }
 else {
      this.getX().fillMatching(tmpFeasibleAE);
      this.setState(State.OPTIMAL);
    }
    if (this.getState().isFeasible()) {
      final PhysicalStore<Double> tmpLE=PrimitiveDenseStore.FACTORY.copy(tmpC);
      tmpLE.maxpy(-1.0,tmpQ.multiplyRight(this.getX()));
      this.getLE().fillMatching(tmpQR.solve(tmpLE));
    }
  }
  @Override protected boolean validate(){
    final boolean retVal=true;
    this.setState(State.VALID);
    return retVal;
  }
}

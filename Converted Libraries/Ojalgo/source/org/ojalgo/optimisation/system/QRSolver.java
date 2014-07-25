package org.ojalgo.optimisation.system;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
public final class QRSolver extends DecompositionSolver<QR<Double>> {
  public QRSolver(){
    super(QRDecomposition.makePrimitive());
  }
  private QRSolver(  final QR<Double> decomposition){
    super(decomposition);
  }
  @Override protected boolean validate(  final MatrixStore<Double> body){
    return true;
  }
}

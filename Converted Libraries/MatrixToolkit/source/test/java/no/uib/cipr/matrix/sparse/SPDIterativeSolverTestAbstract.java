package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.SymmDenseEVD;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of iterative solvers for SPD matrices
 */
public abstract class SPDIterativeSolverTestAbstract extends IterativeSolverTestAbstract {
  public SPDIterativeSolverTestAbstract(  String arg0){
    super(arg0);
  }
  @Override protected void createMatrix() throws Exception {
    int n=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,n));
    A=new FlexCompRowMatrix(n,n);
    Utilities.symmetryPopulate(A,b);
    addDiagonal(A,shift);
    SymmDenseEVD evd=SymmDenseEVD.factorize(A);
    while (n > 0 && evd.getEigenvalues()[0] <= 0) {
      addDiagonal(A,shift);
      evd=SymmDenseEVD.factorize(A);
    }
  }
}

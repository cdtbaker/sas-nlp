package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.SymmDenseEVD;
import no.uib.cipr.matrix.sparse.Chebyshev;
/** 
 * Test of Chebyshev solver
 */
public class ChebyshevTest extends SPDIterativeSolverTestAbstract {
  public ChebyshevTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    SymmDenseEVD evd=SymmDenseEVD.factorize(A);
    double[] eigs=evd.getEigenvalues();
    double eigmin=1, eigmax=1;
    if (eigs.length > 0) {
      eigmin=eigs[0];
      eigmax=eigs[eigs.length - 1];
    }
    solver=new Chebyshev(x,eigmin,eigmax);
  }
}

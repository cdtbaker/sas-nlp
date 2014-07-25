package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSymmBandMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SymmBandEVD;
import no.uib.cipr.matrix.UpperSymmBandMatrix;
/** 
 * Test of the symmetric, tridiagonal eigenvalue solver
 */
public class SymmBandEigenvalueTest extends SymmEigenvalueTestAbstract {
  private LowerSymmBandMatrix L;
  private UpperSymmBandMatrix U;
  public SymmBandEigenvalueTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    super.setUp();
    int kd=Utilities.getInt(1,A.numRows());
    L=new LowerSymmBandMatrix(A,kd);
    U=new UpperSymmBandMatrix(A,kd);
  }
  @Override protected void tearDown() throws Exception {
    super.tearDown();
    L=null;
    U=null;
  }
  public void testLowerStaticFactorize() throws NotConvergedException {
    SymmBandEVD evd=SymmBandEVD.factorize(L,L.numSubDiagonals());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperStaticFactorize() throws NotConvergedException {
    SymmBandEVD evd=SymmBandEVD.factorize(U,U.numSuperDiagonals());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testLowerFactor() throws NotConvergedException {
    SymmBandEVD evd=new SymmBandEVD(A.numRows(),false);
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperFactor() throws NotConvergedException {
    SymmBandEVD evd=new SymmBandEVD(A.numRows(),true);
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testLowerRepeatFactor() throws NotConvergedException {
    SymmBandEVD evd=new SymmBandEVD(A.numRows(),false);
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperRepeatFactor() throws NotConvergedException {
    SymmBandEVD evd=new SymmBandEVD(A.numRows(),true);
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
}

package no.uib.cipr.matrix;
import junit.framework.TestCase;
/** 
 * Tests the dense LU decomposition
 */
public class DenseLUTest extends TestCase {
  /** 
 * Matrix to decompose
 */
  private DenseMatrix A;
  private DenseMatrix I;
  private final int max=100;
  public DenseLUTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new DenseMatrix(n,n);
    Utilities.populate(A);
    Utilities.addDiagonal(A,1);
    while (Utilities.singular(A))     Utilities.addDiagonal(A,1);
    I=Matrices.identity(n);
  }
  @Override protected void tearDown() throws Exception {
    A=null;
    I=null;
  }
  public void testDenseLU(){
    int n=A.numRows();
    DenseLU lu=new DenseLU(n,n);
    lu.factor(A.copy());
    lu.solve(I);
    Matrix J=I.mult(A,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testDenseLUtranspose(){
    int n=A.numRows();
    DenseLU lu=new DenseLU(n,n);
    lu.factor(A.copy());
    lu.transSolve(I);
    Matrix J=I.transAmult(A,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testDenseLUrcond(){
    int n=A.numRows();
    DenseLU lu=new DenseLU(n,n);
    lu.factor(A.copy());
    lu.rcond(A,Matrix.Norm.One);
    lu.rcond(A,Matrix.Norm.Infinity);
  }
  public void testDensePLU(){
    Matrix m=new DenseMatrix(new double[][]{{2,-1,-2},{-4,6,3},{-4,-2,8}});
    DenseLU dlu=DenseLU.factorize(m);
    Matrix p=dlu.getP();
    Matrix l=dlu.getL();
    Matrix u=dlu.getU();
    Matrix lu=l.mult(u,new DenseMatrix(3,3));
    Matrix x=p.mult(lu,new DenseMatrix(3,3));
    MatrixTestAbstract.assertEquals(m,x);
  }
}

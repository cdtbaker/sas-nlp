package no.uib.cipr.matrix;
/** 
 * Test of UnitLowerTriangDenseMatrix
 */
public class UnitLowerTriangDenseMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitLowerTriangDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UnitLowerTriangDenseMatrix(n);
    Ad=Utilities.unitLowerPopulate(A);
    Utilities.unitSet(Ad);
  }
  public void testMultUpper(){
    Matrix lu=new DenseMatrix(new double[][]{{-4.00,6.00,3.00},{1.00,-8.00,5.00},{-0.50,-0.25,0.75}});
    Matrix l=new UnitLowerTriangDenseMatrix(lu,false);
    Matrix u=new UpperTriangDenseMatrix(lu,false);
    Matrix e=new DenseMatrix(new double[][]{{-4,6,3},{-4,-2,8},{2,-1,-2}});
    Matrix out=l.mult(u,new DenseMatrix(3,3));
    MatrixTestAbstract.assertEquals(e,out);
  }
}

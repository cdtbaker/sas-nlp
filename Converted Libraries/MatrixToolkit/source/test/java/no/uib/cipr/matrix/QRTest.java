package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.QR;
/** 
 * QR test
 */
public class QRTest extends OrthogonalTestAbstract {
  public QRTest(  String arg0){
    super(arg0);
  }
  public void testStaticFactorize(){
    assertEquals(A,QR.factorize(A));
  }
  public void testRepeatStaticFactorize(){
    assertEquals(A,QR.factorize(A));
    assertEquals(A,QR.factorize(A));
  }
  public void testFactor(){
    QR qr=new QR(A.numRows(),A.numColumns());
    assertEquals(A,qr.factor(new DenseMatrix(A)));
  }
  public void testRepeatFactor(){
    QR qr=new QR(A.numRows(),A.numColumns());
    qr.factor(new DenseMatrix(A));
    assertEquals(A,qr);
    qr.factor(new DenseMatrix(A));
    assertEquals(A,qr);
  }
  public void testStaticFactorizeNonSquare(){
    assertEquals(Ar,QR.factorize(Ar));
  }
  public void testRepeatStaticFactorizeNonSquare(){
    assertEquals(Ar,QR.factorize(Ar));
    assertEquals(Ar,QR.factorize(Ar));
  }
  public void testFactorNonSquare(){
    QR qr=new QR(Ar.numRows(),Ar.numColumns());
    assertEquals(Ar,qr.factor(new DenseMatrix(Ar)));
  }
  public void testRepeatFactorNonSquare(){
    QR qr=new QR(Ar.numRows(),Ar.numColumns());
    qr.factor(new DenseMatrix(Ar));
    assertEquals(Ar,qr);
    qr.factor(new DenseMatrix(Ar));
    assertEquals(Ar,qr);
  }
  private void assertEquals(  Matrix A,  QR qr){
    assertEquals(A,qr.getQ().mult(qr.getR(),A.copy().zero()));
  }
}

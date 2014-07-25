package cern.colt.matrix.impl;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
class QRTest {
  /** 
 * Constructor for QRTest.
 */
  public QRTest(){
    super();
  }
  public static void main(  String args[]){
    DoubleMatrix2D xmatrix, ymatrix, zmatrix;
    DoubleFactory2D myfactory;
    myfactory=DoubleFactory2D.dense;
    xmatrix=myfactory.make(8,2);
    ymatrix=myfactory.make(8,1);
    xmatrix.set(0,0,1);
    xmatrix.set(1,0,1);
    xmatrix.set(2,0,1);
    xmatrix.set(3,0,1);
    xmatrix.set(4,0,1);
    xmatrix.set(5,0,1);
    xmatrix.set(6,0,1);
    xmatrix.set(7,0,1);
    xmatrix.set(0,1,80);
    xmatrix.set(1,1,220);
    xmatrix.set(2,1,140);
    xmatrix.set(3,1,120);
    xmatrix.set(4,1,180);
    xmatrix.set(5,1,100);
    xmatrix.set(6,1,200);
    xmatrix.set(7,1,160);
    ymatrix.set(0,0,0.6);
    ymatrix.set(1,0,6.70);
    ymatrix.set(2,0,5.30);
    ymatrix.set(3,0,4.00);
    ymatrix.set(4,0,6.55);
    ymatrix.set(5,0,2.15);
    ymatrix.set(6,0,6.60);
    ymatrix.set(7,0,5.75);
    Algebra myAlgebra=new Algebra();
    zmatrix=myAlgebra.solve(xmatrix,ymatrix);
    System.err.println(xmatrix);
    System.err.println(ymatrix);
    System.err.println(zmatrix);
  }
}

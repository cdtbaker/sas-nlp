package org.ejml.data;
/** 
 * An eigenpair is a set composed of an eigenvalue and an eigenvector.  In this library since only real
 * matrices are supported, all eigenpairs are real valued.
 * @author Peter Abeles
 */
public class Eigenpair {
  public double value;
  public DenseMatrix64F vector;
  public Eigenpair(  double value,  DenseMatrix64F vector){
    this.value=value;
    this.vector=vector;
  }
}

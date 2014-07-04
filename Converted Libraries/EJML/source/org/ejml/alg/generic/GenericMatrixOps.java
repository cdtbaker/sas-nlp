package org.ejml.alg.generic;
import org.ejml.data.ReshapeMatrix64F;
import java.util.Random;
/** 
 * @author Peter Abeles
 */
public class GenericMatrixOps {
  public static boolean isEquivalent(  ReshapeMatrix64F a,  ReshapeMatrix64F b,  double tol){
    if (a.numRows != b.numRows || a.numCols != b.numCols)     return false;
    for (int i=0; i < a.numRows; i++) {
      for (int j=0; j < a.numCols; j++) {
        double diff=Math.abs(a.get(i,j) - b.get(i,j));
        if (diff > tol)         return false;
      }
    }
    return true;
  }
  /** 
 * Returns true if the provided matrix is has a value of 1 along the diagonal
 * elements and zero along all the other elements.
 * @param a Matrix being inspected.
 * @param tol How close to zero or one each element needs to be.
 * @return If it is within tolerance to an identity matrix.
 */
  public static boolean isIdentity(  ReshapeMatrix64F a,  double tol){
    for (int i=0; i < a.numRows; i++) {
      for (int j=0; j < a.numCols; j++) {
        if (i == j) {
          if (Math.abs(a.get(i,j) - 1.0) > tol)           return false;
        }
 else {
          if (Math.abs(a.get(i,j)) > tol)           return false;
        }
      }
    }
    return true;
  }
  public static boolean isEquivalentTriangle(  boolean upper,  ReshapeMatrix64F a,  ReshapeMatrix64F b,  double tol){
    if (a.numRows != b.numRows || a.numCols != b.numCols)     return false;
    if (upper) {
      for (int i=0; i < a.numRows; i++) {
        for (int j=i; j < a.numCols; j++) {
          double diff=Math.abs(a.get(i,j) - b.get(i,j));
          if (diff > tol)           return false;
        }
      }
    }
 else {
      for (int j=0; j < a.numCols; j++) {
        for (int i=j; i < a.numRows; i++) {
          double diff=Math.abs(a.get(i,j) - b.get(i,j));
          if (diff > tol)           return false;
        }
      }
    }
    return true;
  }
  public static void copy(  ReshapeMatrix64F from,  ReshapeMatrix64F to){
    int numCols=from.getNumCols();
    int numRows=from.getNumRows();
    for (int i=0; i < numRows; i++) {
      for (int j=0; j < numCols; j++) {
        to.set(i,j,from.get(i,j));
      }
    }
  }
  public static void setRandom(  ReshapeMatrix64F a,  double min,  double max,  Random rand){
    for (int i=0; i < a.numRows; i++) {
      for (int j=0; j < a.numCols; j++) {
        double val=rand.nextDouble() * (max - min) + min;
        a.set(i,j,val);
      }
    }
  }
}

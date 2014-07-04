package org.ejml.alg.dense.decomposition.eig;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
/** 
 * @author Peter Abeles
 */
public interface EigenvalueExtractor {
  public boolean process(  DenseMatrix64F A);
  public int getNumberOfEigenvalues();
  public Complex64F[] getEigenvalues();
}

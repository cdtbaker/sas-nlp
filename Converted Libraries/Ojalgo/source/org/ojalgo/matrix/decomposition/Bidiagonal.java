package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
/** 
 * A general matrix [A] can be factorized by similarity
 * transformations into the form [A]=[Q1][D][Q2]<sup>-1</sup> where:
 * <ul>
 * <li>[A] (m-by-n) is any, real or complex, matrix</li>
 * <li>[D] (r-by-r) or (m-by-n) is, upper or lower, bidiagonal</li>
 * <li>[Q1] (m-by-r) or (m-by-m) is orthogonal</li>
 * <li>[Q2] (n-by-r) or (n-by-n) is orthogonal</li>
 * <li>r = min(m,n)</li>
 * </ul>
 * @author apete
 */
public interface Bidiagonal<N extends Number> extends MatrixDecomposition<N> {
  /** 
 * @param matrix A matrix to decompose
 * @return true if the computation suceeded; false if not
 */
  boolean compute(  Access2D<?> matrix,  boolean fullSize);
  MatrixStore<N> getD();
  MatrixStore<N> getQ1();
  MatrixStore<N> getQ2();
  boolean isUpper();
}

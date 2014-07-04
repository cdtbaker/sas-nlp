package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
/** 
 * Hessenberg: [A] = [Q][H][Q]<sup>T</sup>
 * A general square matrix [A] can be decomposed by orthogonal
 * similarity transformations into the form [A]=[Q][H][Q]<sup>T</sup>
 * where
 * <ul>
 * <li>[H] is upper (or lower) hessenberg matrix</li>
 * <li>[Q] is orthogonal/unitary</li>
 * </ul>
 * @author apete
 */
public interface Hessenberg<N extends Number> extends MatrixDecomposition<N> {
  boolean compute(  Access2D<?> matrix,  boolean upper);
  MatrixStore<N> getH();
  MatrixStore<N> getQ();
  boolean isUpper();
}

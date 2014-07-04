package org.ojalgo.matrix.decomposition;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * Schur: [A] = [Q][U][Q]<sup>-1</sup>
 * [A] = [Q][U][Q]<sup>-1</sup> where:
 * <ul>
 * <li>[A] is a square complex entry matrix.</li>
 * <li>[Q] is a unitary matrix (so that [Q]<sup>-1</sup> equals
 * [Q]<sup>H</sup>).</li>
 * <li>[U] is an upper triangular matrix, which is called a Schur form
 * of [A]. Since [U] is similar to [A], it has the same multiset of
 * eigenvalues, and since it is triangular, those eigenvalues are the
 * diagonal entries of [U].</li>
 * </ul>
 * @author apete
 */
public interface Schur<N extends Number> extends MatrixDecomposition<N> {
  Array1D<ComplexNumber> getDiagonal();
  MatrixStore<N> getQ();
  MatrixStore<N> getU();
  boolean isOrdered();
}

package org.ojalgo.matrix.decomposition;
import org.ojalgo.matrix.store.MatrixStore;
/** 
 * Tridiagonal: [A] = [Q][D][Q]<sup>H</sup>
 * Any square symmetric (hermitian) matrix [A] can be factorized by
 * similarity transformations into the form,
 * [A]=[Q][D][Q]<sup>-1</sup>
 * where [Q] is an orthogonal (unitary) matrix and [D] is a real
 * symmetric tridiagonal matrix. Note that [D] can/should be made real
 * even when [A] has complex elements. Since [Q] is orthogonal (unitary)
 * [Q]<sup>-1</sup> = [Q]<sup>H</sup> and when it is real [Q]<sup>H</sup> = [Q]<sup>T</sup>.
 * @author apete
 */
public interface Tridiagonal<N extends Number> extends MatrixDecomposition<N> {
  MatrixStore<N> getD();
  MatrixStore<N> getQ();
}

package no.uib.cipr.matrix;
/** 
 * The job the singular value solvers are to do. This only limits which singular vectors
 * are computed, all the singular values are always computed
 */
enum JobSVD {/** 
 * Compute all of the singular vectors 
 */
All, /** 
 * Do not compute any singular vectors 
 */
None, /** 
 * Overwrite passed data. For an <code>M*N</code> matrix, this either overwrites the
 * passed matrix with as many singular vectors as there is room for. Details depend on
 * the actual algorithm
 */
Overwrite, /** 
 * Compute parts of the singular vectors. For an <code>M*N</code> matrix, this
 * computes <code>min(M,N)</code> singular vectors
 */
Part; /** 
 * @return the netlib character version of this designation, for use with F2J.
 */
public String netlib(){
switch (this) {
case All:
    return "A";
case Part:
  return "S";
case Overwrite:
return "O";
default :
return "N";
}
}
}

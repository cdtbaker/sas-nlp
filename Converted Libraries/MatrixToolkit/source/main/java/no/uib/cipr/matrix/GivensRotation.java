package no.uib.cipr.matrix;
/** 
 * Givens plane rotation
 */
public class GivensRotation {
  /** 
 * Cosine and sine of the rotation angle. c = x / sqrt(x^2 + y^2), and s =
 * -y / sqrt(x^2 + y^2)
 */
  private final double c, s;
  /** 
 * Constructs a Givens plane rotation for a given 2-vector
 * @param xFirst component of the vector
 * @param ySecond component of the vector
 */
  public GivensRotation(  double x,  double y){
    double roe=Math.abs(x) > Math.abs(y) ? x : y;
    double scale=Math.abs(x) + Math.abs(y);
    if (scale != 0) {
      double xs=x / scale;
      double ys=y / scale;
      double r=scale * Math.sqrt(xs * xs + ys * ys);
      if (roe < 0)       r*=-1;
      c=x / r;
      s=y / r;
    }
 else {
      c=1;
      s=0;
    }
  }
  /** 
 * Applies the Givens rotation to two elements in a matrix column
 * @param HMatrix to apply to
 * @param columnColumn index
 * @param i1Row index of first element
 * @param i2Row index of second element
 */
  public void apply(  Matrix H,  int column,  int i1,  int i2){
    double temp=c * H.get(i1,column) + s * H.get(i2,column);
    H.set(i2,column,-s * H.get(i1,column) + c * H.get(i2,column));
    H.set(i1,column,temp);
  }
  /** 
 * Applies the Givens rotation to two elements of a vector
 * @param xVector to apply to
 * @param i1Index of first element
 * @param i2Index of second element
 */
  public void apply(  Vector x,  int i1,  int i2){
    double temp=c * x.get(i1) + s * x.get(i2);
    x.set(i2,-s * x.get(i1) + c * x.get(i2));
    x.set(i1,temp);
  }
}

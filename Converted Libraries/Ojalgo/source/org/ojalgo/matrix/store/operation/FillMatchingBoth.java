package org.ojalgo.matrix.store.operation;
public final class FillMatchingBoth extends MatrixOperation {
  /** 
 * 2013-10-22: Was set to 128 (based on calibration) but I saw a dip in relative performance (java matrix benchmark)
 * at size 200. So I cahnged it to 256.
 */
  public static int THRESHOLD=256;
  private FillMatchingBoth(){
    super();
  }
}

package org.apache.commons.math3.linear;
/** 
 * Default implementation of the {@link RealMatrixChangingVisitor} interface.
 * <p>
 * This class is a convenience to create custom visitors without defining all
 * methods. This class provides default implementations that do nothing.
 * </p>
 * @version $Id: DefaultRealMatrixChangingVisitor.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class DefaultRealMatrixChangingVisitor implements RealMatrixChangingVisitor {
  /** 
 * {@inheritDoc} 
 */
  public void start(  int rows,  int columns,  int startRow,  int endRow,  int startColumn,  int endColumn){
  }
  /** 
 * {@inheritDoc} 
 */
  public double visit(  int row,  int column,  double value){
    return value;
  }
  /** 
 * {@inheritDoc} 
 */
  public double end(){
    return 0;
  }
}

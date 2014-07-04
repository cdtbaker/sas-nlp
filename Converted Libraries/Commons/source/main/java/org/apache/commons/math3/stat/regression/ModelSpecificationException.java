package org.apache.commons.math3.stat.regression;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
/** 
 * Exception thrown when a regression model is not correctly specified.
 * @since 3.0
 * @version $Id: ModelSpecificationException.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class ModelSpecificationException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=4206514456095401070L;
  /** 
 * @param pattern message pattern describing the specification error.
 * @param args arguments.
 */
  public ModelSpecificationException(  Localizable pattern,  Object... args){
    super(pattern,args);
  }
}

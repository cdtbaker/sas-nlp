package org.apache.commons.math3.complex;
import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
/** 
 * Representation of the complex numbers field.
 * <p>
 * This class is a singleton.
 * </p>
 * @see Complex
 * @version $Id: ComplexField.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class ComplexField implements Field<Complex>, Serializable {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=-6130362688700788798L;
  /** 
 * Private constructor for the singleton.
 */
  private ComplexField(){
  }
  /** 
 * Get the unique instance.
 * @return the unique instance
 */
  public static ComplexField getInstance(){
    return LazyHolder.INSTANCE;
  }
  /** 
 * {@inheritDoc} 
 */
  public Complex getOne(){
    return Complex.ONE;
  }
  /** 
 * {@inheritDoc} 
 */
  public Complex getZero(){
    return Complex.ZERO;
  }
  /** 
 * {@inheritDoc} 
 */
  public Class<? extends FieldElement<Complex>> getRuntimeClass(){
    return Complex.class;
  }
  /** 
 * Holder for the instance.
 * <p>We use here the Initialization On Demand Holder Idiom.</p>
 */
private static class LazyHolder {
    /** 
 * Cached field instance. 
 */
    private static final ComplexField INSTANCE=new ComplexField();
  }
  /** 
 * Handle deserialization of the singleton.
 * @return the singleton instance
 */
  private Object readResolve(){
    return LazyHolder.INSTANCE;
  }
}

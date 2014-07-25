package org.apache.commons.math3.util;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
/** 
 * The field of double precision floating-point numbers.
 * @since 3.1
 * @version $Id: Decimal64Field.java 1306177 2012-03-28 05:40:46Z celestin $
 * @see Decimal64
 */
public class Decimal64Field implements Field<Decimal64> {
  /** 
 * The unique instance of this class. 
 */
  private static final Decimal64Field INSTANCE=new Decimal64Field();
  /** 
 * Default constructor. 
 */
  private Decimal64Field(){
  }
  /** 
 * Returns the unique instance of this class.
 * @return the unique instance of this class
 */
  public static final Decimal64Field getInstance(){
    return INSTANCE;
  }
  /** 
 * {@inheritDoc} 
 */
  public Decimal64 getZero(){
    return Decimal64.ZERO;
  }
  /** 
 * {@inheritDoc} 
 */
  public Decimal64 getOne(){
    return Decimal64.ONE;
  }
  /** 
 * {@inheritDoc} 
 */
  public Class<? extends FieldElement<Decimal64>> getRuntimeClass(){
    return Decimal64.class;
  }
}

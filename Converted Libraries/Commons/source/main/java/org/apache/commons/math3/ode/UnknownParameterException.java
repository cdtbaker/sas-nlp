package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a parameter is unknown.
 * @since 3.1
 * @version $Id: UnknownParameterException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class UnknownParameterException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=20120902L;
  /** 
 * Parameter name. 
 */
  private final String name;
  /** 
 * Construct an exception from the unknown parameter.
 * @param name parameter name.
 */
  public UnknownParameterException(  final String name){
    super(LocalizedFormats.UNKNOWN_PARAMETER);
    this.name=name;
  }
  /** 
 * @return the name of the unknown parameter.
 */
  public String getName(){
    return name;
  }
}

package org.apache.commons.math3.ode;
import java.util.Collection;
/** 
 * This interface enables to process any parameterizable object.
 * @version $Id: Parameterizable.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public interface Parameterizable {
  /** 
 * Get the names of the supported parameters.
 * @return parameters names
 * @see #isSupported(String)
 */
  Collection<String> getParametersNames();
  /** 
 * Check if a parameter is supported.
 * <p>Supported parameters are those listed by {@link #getParametersNames()}.</p>
 * @param name parameter name to check
 * @return true if the parameter is supported
 * @see #getParametersNames()
 */
  boolean isSupported(  String name);
}

package org.jscience.geography.coordinates.crs;
import org.jscience.geography.coordinates.Coordinates;
/** 
 * This interface represents a converter between {@link org.jscience.geography.coordinates.Coordinates coordinates}.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public interface CoordinatesConverter<S extends Coordinates<?>,T extends Coordinates<?>> {
  /** 
 * Converts the specified coordinates.
 * @param source the source coordinates.
 * @return the corresponding target coordinates.
 * @throws ConversionException if this conversion cannot be performed.
 */
  T convert(  S source);
}

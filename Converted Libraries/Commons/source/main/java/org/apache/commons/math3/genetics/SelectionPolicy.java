package org.apache.commons.math3.genetics;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Algorithm used to select a chromosome pair from a population.
 * @since 2.0
 * @version $Id: SelectionPolicy.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface SelectionPolicy {
  /** 
 * Select two chromosomes from the population.
 * @param population the population from which the chromosomes are choosen.
 * @return the selected chromosomes.
 * @throws MathIllegalArgumentException if the population is not compatible with this {@link SelectionPolicy}
 */
  ChromosomePair select(  Population population) throws MathIllegalArgumentException ;
}

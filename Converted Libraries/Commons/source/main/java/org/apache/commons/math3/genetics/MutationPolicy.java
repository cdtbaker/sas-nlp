package org.apache.commons.math3.genetics;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Algorithm used to mutate a chromosome.
 * @since 2.0
 * @version $Id: MutationPolicy.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface MutationPolicy {
  /** 
 * Mutate the given chromosome.
 * @param original the original chromosome.
 * @return the mutated chromosome.
 * @throws MathIllegalArgumentException if the given chromosome is not compatible with this {@link MutationPolicy}
 */
  Chromosome mutate(  Chromosome original) throws MathIllegalArgumentException ;
}

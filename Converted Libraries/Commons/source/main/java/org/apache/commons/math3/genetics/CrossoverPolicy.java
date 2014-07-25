package org.apache.commons.math3.genetics;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Policy used to create a pair of new chromosomes by performing a crossover
 * operation on a source pair of chromosomes.
 * @since 2.0
 * @version $Id: CrossoverPolicy.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface CrossoverPolicy {
  /** 
 * Perform a crossover operation on the given chromosomes.
 * @param first the first chromosome.
 * @param second the second chromosome.
 * @return the pair of new chromosomes that resulted from the crossover.
 * @throws MathIllegalArgumentException if the given chromosomes are not compatible with this {@link CrossoverPolicy}
 */
  ChromosomePair crossover(  Chromosome first,  Chromosome second) throws MathIllegalArgumentException ;
}

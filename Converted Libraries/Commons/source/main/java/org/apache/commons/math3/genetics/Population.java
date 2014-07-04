package org.apache.commons.math3.genetics;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
/** 
 * A collection of chromosomes that facilitates generational evolution.
 * @since 2.0
 * @version $Id: Population.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface Population extends Iterable<Chromosome> {
  /** 
 * Access the current population size.
 * @return the current population size.
 */
  int getPopulationSize();
  /** 
 * Access the maximum population size.
 * @return the maximum population size.
 */
  int getPopulationLimit();
  /** 
 * Start the population for the next generation.
 * @return the beginnings of the next generation.
 */
  Population nextGeneration();
  /** 
 * Add the given chromosome to the population.
 * @param chromosome the chromosome to add.
 * @throws NumberIsTooLargeException if the population would exceed the population limit when adding
 * this chromosome
 */
  void addChromosome(  Chromosome chromosome) throws NumberIsTooLargeException ;
  /** 
 * Access the fittest chromosome in this population.
 * @return the fittest chromosome.
 */
  Chromosome getFittestChromosome();
}

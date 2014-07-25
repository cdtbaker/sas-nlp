package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Tournament selection scheme. Each of the two selected chromosomes is selected
 * based on n-ary tournament -- this is done by drawing {@link #arity} random
 * chromosomes without replacement from the population, and then selecting the
 * fittest chromosome among them.
 * @since 2.0
 * @version $Id: TournamentSelection.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class TournamentSelection implements SelectionPolicy {
  /** 
 * number of chromosomes included in the tournament selections 
 */
  private int arity;
  /** 
 * Creates a new TournamentSelection instance.
 * @param arity how many chromosomes will be drawn to the tournament
 */
  public TournamentSelection(  final int arity){
    this.arity=arity;
  }
  /** 
 * Select two chromosomes from the population. Each of the two selected
 * chromosomes is selected based on n-ary tournament -- this is done by
 * drawing {@link #arity} random chromosomes without replacement from the
 * population, and then selecting the fittest chromosome among them.
 * @param population the population from which the chromosomes are chosen.
 * @return the selected chromosomes.
 * @throws MathIllegalArgumentException if the tournament arity is bigger than the population size
 */
  public ChromosomePair select(  final Population population) throws MathIllegalArgumentException {
    return new ChromosomePair(tournament((ListPopulation)population),tournament((ListPopulation)population));
  }
  /** 
 * Helper for {@link #select(Population)}. Draw {@link #arity} random chromosomes without replacement from the
 * population, and then select the fittest chromosome among them.
 * @param population the population from which the chromosomes are choosen.
 * @return the selected chromosome.
 * @throws MathIllegalArgumentException if the tournament arity is bigger than the population size
 */
  private Chromosome tournament(  final ListPopulation population) throws MathIllegalArgumentException {
    if (population.getPopulationSize() < this.arity) {
      throw new MathIllegalArgumentException(LocalizedFormats.TOO_LARGE_TOURNAMENT_ARITY,arity,population.getPopulationSize());
    }
    ListPopulation tournamentPopulation=new ListPopulation(this.arity){
      public Population nextGeneration(){
        return null;
      }
    }
;
    List<Chromosome> chromosomes=new ArrayList<Chromosome>(population.getChromosomes());
    for (int i=0; i < this.arity; i++) {
      int rind=GeneticAlgorithm.getRandomGenerator().nextInt(chromosomes.size());
      tournamentPopulation.addChromosome(chromosomes.get(rind));
      chromosomes.remove(rind);
    }
    return tournamentPopulation.getFittestChromosome();
  }
  /** 
 * Gets the arity (number of chromosomes drawn to the tournament).
 * @return arity of the tournament
 */
  public int getArity(){
    return arity;
  }
  /** 
 * Sets the arity (number of chromosomes drawn to the tournament).
 * @param arity arity of the tournament
 */
  public void setArity(  final int arity){
    this.arity=arity;
  }
}

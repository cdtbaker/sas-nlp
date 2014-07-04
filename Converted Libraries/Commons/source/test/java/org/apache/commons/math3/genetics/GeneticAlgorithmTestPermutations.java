package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * This is also an example of usage.
 * This algorithm does "stochastic sorting" of a sequence 0,...,N.
 */
public class GeneticAlgorithmTestPermutations {
  private static final int DIMENSION=20;
  private static final int POPULATION_SIZE=80;
  private static final int NUM_GENERATIONS=200;
  private static final double ELITISM_RATE=0.2;
  private static final double CROSSOVER_RATE=1;
  private static final double MUTATION_RATE=0.08;
  private static final int TOURNAMENT_ARITY=2;
  private static final List<Integer> sequence=new ArrayList<Integer>();
static {
    for (int i=0; i < DIMENSION; i++) {
      sequence.add(i);
    }
  }
  @Test public void test(){
    GeneticAlgorithm ga=new GeneticAlgorithm(new OnePointCrossover<Integer>(),CROSSOVER_RATE,new RandomKeyMutation(),MUTATION_RATE,new TournamentSelection(TOURNAMENT_ARITY));
    Population initial=randomPopulation();
    StoppingCondition stopCond=new FixedGenerationCount(NUM_GENERATIONS);
    Chromosome bestInitial=initial.getFittestChromosome();
    Population finalPopulation=ga.evolve(initial,stopCond);
    Chromosome bestFinal=finalPopulation.getFittestChromosome();
    Assert.assertTrue(bestFinal.compareTo(bestInitial) > 0);
  }
  /** 
 * Initializes a random population
 */
  private static ElitisticListPopulation randomPopulation(){
    List<Chromosome> popList=new ArrayList<Chromosome>();
    for (int i=0; i < POPULATION_SIZE; i++) {
      Chromosome randChrom=new MinPermutations(RandomKey.randomPermutation(DIMENSION));
      popList.add(randChrom);
    }
    return new ElitisticListPopulation(popList,popList.size(),ELITISM_RATE);
  }
  /** 
 * Chromosomes representing a permutation of (0,1,2,...,DIMENSION-1).
 * The goal is to sort the sequence.
 */
private static class MinPermutations extends RandomKey<Integer> {
    public MinPermutations(    List<Double> representation){
      super(representation);
    }
    public double fitness(){
      int res=0;
      List<Integer> decoded=decode(sequence);
      for (int i=0; i < decoded.size(); i++) {
        int value=decoded.get(i);
        if (value != i) {
          res+=FastMath.abs(value - i);
        }
      }
      return -res;
    }
    @Override public AbstractListChromosome<Double> newFixedLengthChromosome(    List<Double> chromosomeRepresentation){
      return new MinPermutations(chromosomeRepresentation);
    }
  }
}

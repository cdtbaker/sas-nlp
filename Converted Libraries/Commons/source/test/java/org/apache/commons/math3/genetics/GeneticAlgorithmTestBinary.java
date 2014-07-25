package org.apache.commons.math3.genetics;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
/** 
 * This is also an example of usage.
 */
public class GeneticAlgorithmTestBinary {
  private static final int DIMENSION=50;
  private static final int POPULATION_SIZE=50;
  private static final int NUM_GENERATIONS=50;
  private static final double ELITISM_RATE=0.2;
  private static final double CROSSOVER_RATE=1;
  private static final double MUTATION_RATE=0.1;
  private static final int TOURNAMENT_ARITY=2;
  @Test public void test(){
    GeneticAlgorithm ga=new GeneticAlgorithm(new OnePointCrossover<Integer>(),CROSSOVER_RATE,new BinaryMutation(),MUTATION_RATE,new TournamentSelection(TOURNAMENT_ARITY));
    Assert.assertEquals(0,ga.getGenerationsEvolved());
    Population initial=randomPopulation();
    StoppingCondition stopCond=new FixedGenerationCount(NUM_GENERATIONS);
    Chromosome bestInitial=initial.getFittestChromosome();
    Population finalPopulation=ga.evolve(initial,stopCond);
    Chromosome bestFinal=finalPopulation.getFittestChromosome();
    Assert.assertTrue(bestFinal.compareTo(bestInitial) > 0);
    Assert.assertEquals(NUM_GENERATIONS,ga.getGenerationsEvolved());
  }
  /** 
 * Initializes a random population.
 */
  private static ElitisticListPopulation randomPopulation(){
    List<Chromosome> popList=new LinkedList<Chromosome>();
    for (int i=0; i < POPULATION_SIZE; i++) {
      BinaryChromosome randChrom=new FindOnes(BinaryChromosome.randomBinaryRepresentation(DIMENSION));
      popList.add(randChrom);
    }
    return new ElitisticListPopulation(popList,popList.size(),ELITISM_RATE);
  }
  /** 
 * Chromosomes represented by a binary chromosome.
 * The goal is to set all bits (genes) to 1.
 */
private static class FindOnes extends BinaryChromosome {
    public FindOnes(    List<Integer> representation){
      super(representation);
    }
    /** 
 * Returns number of elements != 0
 */
    public double fitness(){
      int num=0;
      for (      int val : this.getRepresentation()) {
        if (val != 0)         num++;
      }
      return num;
    }
    @Override public AbstractListChromosome<Integer> newFixedLengthChromosome(    List<Integer> chromosomeRepresentation){
      return new FindOnes(chromosomeRepresentation);
    }
  }
}

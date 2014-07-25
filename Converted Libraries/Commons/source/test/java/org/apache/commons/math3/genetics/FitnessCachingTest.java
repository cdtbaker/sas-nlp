package org.apache.commons.math3.genetics;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
public class FitnessCachingTest {
  private static final int DIMENSION=50;
  private static final double CROSSOVER_RATE=1;
  private static final double MUTATION_RATE=0.1;
  private static final int TOURNAMENT_ARITY=5;
  private static final int POPULATION_SIZE=10;
  private static final int NUM_GENERATIONS=50;
  private static final double ELITISM_RATE=0.2;
  private static int fitnessCalls=0;
  @Test public void testFitnessCaching(){
    GeneticAlgorithm ga=new GeneticAlgorithm(new OnePointCrossover<Integer>(),CROSSOVER_RATE,new BinaryMutation(),MUTATION_RATE,new TournamentSelection(TOURNAMENT_ARITY));
    Population initial=randomPopulation();
    StoppingCondition stopCond=new FixedGenerationCount(NUM_GENERATIONS);
    ga.evolve(initial,stopCond);
    int neededCalls=POPULATION_SIZE + (NUM_GENERATIONS - 1) * (int)(POPULATION_SIZE * (1.0 - ELITISM_RATE));
    Assert.assertTrue(fitnessCalls <= neededCalls);
  }
  /** 
 * Initializes a random population.
 */
  private static ElitisticListPopulation randomPopulation(){
    List<Chromosome> popList=new LinkedList<Chromosome>();
    for (int i=0; i < POPULATION_SIZE; i++) {
      BinaryChromosome randChrom=new DummyCountingBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(DIMENSION));
      popList.add(randChrom);
    }
    return new ElitisticListPopulation(popList,popList.size(),ELITISM_RATE);
  }
private static class DummyCountingBinaryChromosome extends DummyBinaryChromosome {
    public DummyCountingBinaryChromosome(    List<Integer> representation){
      super(representation);
    }
    @Override public double fitness(){
      fitnessCalls++;
      return 0;
    }
  }
}

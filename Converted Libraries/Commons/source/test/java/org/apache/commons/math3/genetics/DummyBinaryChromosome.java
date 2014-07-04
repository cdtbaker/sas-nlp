package org.apache.commons.math3.genetics;
import java.util.List;
/** 
 * Implementation of BinaryChromosome for testing purposes
 */
public class DummyBinaryChromosome extends BinaryChromosome {
  public DummyBinaryChromosome(  List<Integer> representation){
    super(representation);
  }
  public DummyBinaryChromosome(  Integer[] representation){
    super(representation);
  }
  @Override public AbstractListChromosome<Integer> newFixedLengthChromosome(  List<Integer> chromosomeRepresentation){
    return new DummyBinaryChromosome(chromosomeRepresentation);
  }
  public double fitness(){
    return 0;
  }
}

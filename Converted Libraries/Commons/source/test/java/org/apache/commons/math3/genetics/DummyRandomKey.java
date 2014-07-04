package org.apache.commons.math3.genetics;
import java.util.List;
/** 
 * Implementation of RandomKey for testing purposes
 */
public class DummyRandomKey extends RandomKey<String> {
  public DummyRandomKey(  List<Double> representation){
    super(representation);
  }
  public DummyRandomKey(  Double[] representation){
    super(representation);
  }
  @Override public AbstractListChromosome<Double> newFixedLengthChromosome(  List<Double> chromosomeRepresentation){
    return new DummyRandomKey(chromosomeRepresentation);
  }
  public double fitness(){
    return 0;
  }
}

package org.apache.commons.math3.genetics;
import java.util.Arrays;
import java.util.List;
/** 
 * Implementation of ListChromosome for testing purposes
 */
public class DummyListChromosome extends AbstractListChromosome<Integer> {
  public DummyListChromosome(  final Integer[] representation){
    super(representation);
  }
  public DummyListChromosome(  final List<Integer> representation){
    super(representation);
  }
  public double fitness(){
    return 0;
  }
  @Override protected void checkValidity(  final List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
  }
  @Override public AbstractListChromosome<Integer> newFixedLengthChromosome(  final List<Integer> chromosomeRepresentation){
    return new DummyListChromosome(chromosomeRepresentation);
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=prime * result + (getRepresentation() == null ? 0 : getRepresentation().hashCode());
    return result;
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DummyListChromosome)) {
      return false;
    }
    final DummyListChromosome other=(DummyListChromosome)obj;
    if (getRepresentation() == null) {
      if (other.getRepresentation() != null) {
        return false;
      }
    }
    final Integer[] rep=getRepresentation().toArray(new Integer[getRepresentation().size()]);
    final Integer[] otherRep=other.getRepresentation().toArray(new Integer[other.getRepresentation().size()]);
    return Arrays.equals(rep,otherRep);
  }
}

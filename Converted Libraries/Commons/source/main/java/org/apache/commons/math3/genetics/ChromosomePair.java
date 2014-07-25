package org.apache.commons.math3.genetics;
/** 
 * A pair of {@link Chromosome} objects.
 * @since 2.0
 * @version $Id: ChromosomePair.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class ChromosomePair {
  /** 
 * the first chromosome in the pair. 
 */
  private final Chromosome first;
  /** 
 * the second chromosome in the pair. 
 */
  private final Chromosome second;
  /** 
 * Create a chromosome pair.
 * @param c1 the first chromosome.
 * @param c2 the second chromosome.
 */
  public ChromosomePair(  final Chromosome c1,  final Chromosome c2){
    super();
    first=c1;
    second=c2;
  }
  /** 
 * Access the first chromosome.
 * @return the first chromosome.
 */
  public Chromosome getFirst(){
    return first;
  }
  /** 
 * Access the second chromosome.
 * @return the second chromosome.
 */
  public Chromosome getSecond(){
    return second;
  }
  @Override public String toString(){
    return String.format("(%s,%s)",getFirst(),getSecond());
  }
}

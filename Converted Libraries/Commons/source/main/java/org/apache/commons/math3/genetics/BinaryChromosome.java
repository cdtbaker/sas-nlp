package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Chromosome represented by a vector of 0s and 1s.
 * @version $Id: BinaryChromosome.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public abstract class BinaryChromosome extends AbstractListChromosome<Integer> {
  /** 
 * Constructor.
 * @param representation list of {0,1} values representing the chromosome
 * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
 */
  public BinaryChromosome(  List<Integer> representation) throws InvalidRepresentationException {
    super(representation);
  }
  /** 
 * Constructor.
 * @param representation array of {0,1} values representing the chromosome
 * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
 */
  public BinaryChromosome(  Integer[] representation) throws InvalidRepresentationException {
    super(representation);
  }
  /** 
 * {@inheritDoc}
 */
  @Override protected void checkValidity(  List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
    for (    int i : chromosomeRepresentation) {
      if (i < 0 || i > 1) {
        throw new InvalidRepresentationException(LocalizedFormats.INVALID_BINARY_DIGIT,i);
      }
    }
  }
  /** 
 * Returns a representation of a random binary array of length <code>length</code>.
 * @param length length of the array
 * @return a random binary array of length <code>length</code>
 */
  public static List<Integer> randomBinaryRepresentation(  int length){
    List<Integer> rList=new ArrayList<Integer>(length);
    for (int j=0; j < length; j++) {
      rList.add(GeneticAlgorithm.getRandomGenerator().nextInt(2));
    }
    return rList;
  }
  @Override protected boolean isSame(  Chromosome another){
    if (!(another instanceof BinaryChromosome)) {
      return false;
    }
    BinaryChromosome anotherBc=(BinaryChromosome)another;
    if (getLength() != anotherBc.getLength()) {
      return false;
    }
    for (int i=0; i < getRepresentation().size(); i++) {
      if (!(getRepresentation().get(i).equals(anotherBc.getRepresentation().get(i)))) {
        return false;
      }
    }
    return true;
  }
}

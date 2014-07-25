package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Mutation for {@link BinaryChromosome}s. Randomly changes one gene.
 * @version $Id: BinaryMutation.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class BinaryMutation implements MutationPolicy {
  /** 
 * Mutate the given chromosome. Randomly changes one gene.
 * @param original the original chromosome.
 * @return the mutated chromosome.
 * @throws MathIllegalArgumentException if <code>original</code> is not an instance of {@link BinaryChromosome}.
 */
  public Chromosome mutate(  Chromosome original) throws MathIllegalArgumentException {
    if (!(original instanceof BinaryChromosome)) {
      throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME);
    }
    BinaryChromosome origChrom=(BinaryChromosome)original;
    List<Integer> newRepr=new ArrayList<Integer>(origChrom.getRepresentation());
    int geneIndex=GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
    newRepr.set(geneIndex,origChrom.getRepresentation().get(geneIndex) == 0 ? 1 : 0);
    Chromosome newChrom=origChrom.newFixedLengthChromosome(newRepr);
    return newChrom;
  }
}

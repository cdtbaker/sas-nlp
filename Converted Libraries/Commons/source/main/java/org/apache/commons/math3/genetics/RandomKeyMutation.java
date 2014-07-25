package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Mutation operator for {@link RandomKey}s. Changes a randomly chosen element
 * of the array representation to a random value uniformly distributed in [0,1].
 * @since 2.0
 * @version $Id: RandomKeyMutation.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class RandomKeyMutation implements MutationPolicy {
  /** 
 * {@inheritDoc}
 * @throws MathIllegalArgumentException if <code>original</code> is not a {@link RandomKey} instance
 */
  public Chromosome mutate(  final Chromosome original) throws MathIllegalArgumentException {
    if (!(original instanceof RandomKey<?>)) {
      throw new MathIllegalArgumentException(LocalizedFormats.RANDOMKEY_MUTATION_WRONG_CLASS,original.getClass().getSimpleName());
    }
    RandomKey<?> originalRk=(RandomKey<?>)original;
    List<Double> repr=originalRk.getRepresentation();
    int rInd=GeneticAlgorithm.getRandomGenerator().nextInt(repr.size());
    List<Double> newRepr=new ArrayList<Double>(repr);
    newRepr.set(rInd,GeneticAlgorithm.getRandomGenerator().nextDouble());
    return originalRk.newFixedLengthChromosome(newRepr);
  }
}

package org.apache.commons.math3.random;
import org.apache.commons.math3.util.FastMath;
/** 
 * Generate random vectors isotropically located on the surface of a sphere.
 * @since 2.1
 * @version $Id: UnitSphereRandomVectorGenerator.java 1444500 2013-02-10 08:10:40Z tn $
 */
public class UnitSphereRandomVectorGenerator implements RandomVectorGenerator {
  /** 
 * RNG used for generating the individual components of the vectors.
 */
  private final RandomGenerator rand;
  /** 
 * Space dimension.
 */
  private final int dimension;
  /** 
 * @param dimension Space dimension.
 * @param rand RNG for the individual components of the vectors.
 */
  public UnitSphereRandomVectorGenerator(  final int dimension,  final RandomGenerator rand){
    this.dimension=dimension;
    this.rand=rand;
  }
  /** 
 * Create an object that will use a default RNG ({@link MersenneTwister}),
 * in order to generate the individual components.
 * @param dimension Space dimension.
 */
  public UnitSphereRandomVectorGenerator(  final int dimension){
    this(dimension,new MersenneTwister());
  }
  /** 
 * {@inheritDoc} 
 */
  public double[] nextVector(){
    final double[] v=new double[dimension];
    double normSq=0;
    for (int i=0; i < dimension; i++) {
      final double comp=rand.nextGaussian();
      v[i]=comp;
      normSq+=comp * comp;
    }
    final double f=1 / FastMath.sqrt(normSq);
    for (int i=0; i < dimension; i++) {
      v[i]*=f;
    }
    return v;
  }
}

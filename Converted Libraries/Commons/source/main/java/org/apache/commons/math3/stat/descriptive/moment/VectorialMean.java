package org.apache.commons.math3.stat.descriptive.moment;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
/** 
 * Returns the arithmetic mean of the available vectors.
 * @since 1.2
 * @version $Id: VectorialMean.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class VectorialMean implements Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=8223009086481006892L;
  /** 
 * Means for each component. 
 */
  private final Mean[] means;
  /** 
 * Constructs a VectorialMean.
 * @param dimension vectors dimension
 */
  public VectorialMean(  int dimension){
    means=new Mean[dimension];
    for (int i=0; i < dimension; ++i) {
      means[i]=new Mean();
    }
  }
  /** 
 * Add a new vector to the sample.
 * @param v vector to add
 * @throws DimensionMismatchException if the vector does not have the right dimension
 */
  public void increment(  double[] v) throws DimensionMismatchException {
    if (v.length != means.length) {
      throw new DimensionMismatchException(v.length,means.length);
    }
    for (int i=0; i < v.length; ++i) {
      means[i].increment(v[i]);
    }
  }
  /** 
 * Get the mean vector.
 * @return mean vector
 */
  public double[] getResult(){
    double[] result=new double[means.length];
    for (int i=0; i < result.length; ++i) {
      result[i]=means[i].getResult();
    }
    return result;
  }
  /** 
 * Get the number of vectors in the sample.
 * @return number of vectors in the sample
 */
  public long getN(){
    return (means.length == 0) ? 0 : means[0].getN();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=prime * result + Arrays.hashCode(means);
    return result;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public boolean equals(  Object obj){
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof VectorialMean)) {
      return false;
    }
    VectorialMean other=(VectorialMean)obj;
    if (!Arrays.equals(means,other.means)) {
      return false;
    }
    return true;
  }
}

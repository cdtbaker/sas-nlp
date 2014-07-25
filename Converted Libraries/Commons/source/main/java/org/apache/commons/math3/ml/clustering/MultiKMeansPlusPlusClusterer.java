package org.apache.commons.math3.ml.clustering;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
/** 
 * A wrapper around a k-means++ clustering algorithm which performs multiple trials
 * and returns the best solution.
 * @param<T>
 *  type of the points to cluster
 * @version $Id: MultiKMeansPlusPlusClusterer.java 1462375 2013-03-29 01:42:42Z psteitz $
 * @since 3.2
 */
public class MultiKMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {
  /** 
 * The underlying k-means clusterer. 
 */
  private final KMeansPlusPlusClusterer<T> clusterer;
  /** 
 * The number of trial runs. 
 */
  private final int numTrials;
  /** 
 * Build a clusterer.
 * @param clusterer the k-means clusterer to use
 * @param numTrials number of trial runs
 */
  public MultiKMeansPlusPlusClusterer(  final KMeansPlusPlusClusterer<T> clusterer,  final int numTrials){
    super(clusterer.getDistanceMeasure());
    this.clusterer=clusterer;
    this.numTrials=numTrials;
  }
  /** 
 * Returns the embedded k-means clusterer used by this instance.
 * @return the embedded clusterer
 */
  public KMeansPlusPlusClusterer<T> getClusterer(){
    return clusterer;
  }
  /** 
 * Returns the number of trials this instance will do.
 * @return the number of trials
 */
  public int getNumTrials(){
    return numTrials;
  }
  /** 
 * Runs the K-means++ clustering algorithm.
 * @param points the points to cluster
 * @return a list of clusters containing the points
 * @throws MathIllegalArgumentException if the data points are null or the number
 * of clusters is larger than the number of data points
 * @throws ConvergenceException if an empty cluster is encountered and the
 * underlying {@link KMeansPlusPlusClusterer} has its{@link KMeansPlusPlusClusterer.EmptyClusterStrategy} is set to {@code ERROR}.
 */
  public List<CentroidCluster<T>> cluster(  final Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
    List<CentroidCluster<T>> best=null;
    double bestVarianceSum=Double.POSITIVE_INFINITY;
    for (int i=0; i < numTrials; ++i) {
      List<CentroidCluster<T>> clusters=clusterer.cluster(points);
      double varianceSum=0.0;
      for (      final CentroidCluster<T> cluster : clusters) {
        if (!cluster.getPoints().isEmpty()) {
          final Clusterable center=cluster.getCenter();
          final Variance stat=new Variance();
          for (          final T point : cluster.getPoints()) {
            stat.increment(distance(point,center));
          }
          varianceSum+=stat.getResult();
        }
      }
      if (varianceSum <= bestVarianceSum) {
        best=clusters;
        bestVarianceSum=varianceSum;
      }
    }
    return best;
  }
}

package org.apache.commons.math3.ml.clustering;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.MathUtils;
/** 
 * Clustering algorithm based on David Arthur and Sergei Vassilvitski k-means++ algorithm.
 * @param<T>
 *  type of the points to cluster
 * @see <a href="http://en.wikipedia.org/wiki/K-means%2B%2B">K-means++ (wikipedia)</a>
 * @version $Id: KMeansPlusPlusClusterer.java 1461866 2013-03-27 21:54:36Z tn $
 * @since 3.2
 */
public class KMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {
  /** 
 * Strategies to use for replacing an empty cluster. 
 */
  public static enum EmptyClusterStrategy {  /** 
 * Split the cluster with largest distance variance. 
 */
  LARGEST_VARIANCE,   /** 
 * Split the cluster with largest number of points. 
 */
  LARGEST_POINTS_NUMBER,   /** 
 * Create a cluster around the point farthest from its centroid. 
 */
  FARTHEST_POINT,   /** 
 * Generate an error. 
 */
  ERROR}
  /** 
 * The number of clusters. 
 */
  private final int k;
  /** 
 * The maximum number of iterations. 
 */
  private final int maxIterations;
  /** 
 * Random generator for choosing initial centers. 
 */
  private final RandomGenerator random;
  /** 
 * Selected strategy for empty clusters. 
 */
  private final EmptyClusterStrategy emptyStrategy;
  /** 
 * Build a clusterer.
 * <p>
 * The default strategy for handling empty clusters that may appear during
 * algorithm iterations is to split the cluster with largest distance variance.
 * <p>
 * The euclidean distance will be used as default distance measure.
 * @param k the number of clusters to split the data into
 */
  public KMeansPlusPlusClusterer(  final int k){
    this(k,-1);
  }
  /** 
 * Build a clusterer.
 * <p>
 * The default strategy for handling empty clusters that may appear during
 * algorithm iterations is to split the cluster with largest distance variance.
 * <p>
 * The euclidean distance will be used as default distance measure.
 * @param k the number of clusters to split the data into
 * @param maxIterations the maximum number of iterations to run the algorithm for.
 * If negative, no maximum will be used.
 */
  public KMeansPlusPlusClusterer(  final int k,  final int maxIterations){
    this(k,maxIterations,new EuclideanDistance());
  }
  /** 
 * Build a clusterer.
 * <p>
 * The default strategy for handling empty clusters that may appear during
 * algorithm iterations is to split the cluster with largest distance variance.
 * @param k the number of clusters to split the data into
 * @param maxIterations the maximum number of iterations to run the algorithm for.
 * If negative, no maximum will be used.
 * @param measure the distance measure to use
 */
  public KMeansPlusPlusClusterer(  final int k,  final int maxIterations,  final DistanceMeasure measure){
    this(k,maxIterations,measure,new JDKRandomGenerator());
  }
  /** 
 * Build a clusterer.
 * <p>
 * The default strategy for handling empty clusters that may appear during
 * algorithm iterations is to split the cluster with largest distance variance.
 * @param k the number of clusters to split the data into
 * @param maxIterations the maximum number of iterations to run the algorithm for.
 * If negative, no maximum will be used.
 * @param measure the distance measure to use
 * @param random random generator to use for choosing initial centers
 */
  public KMeansPlusPlusClusterer(  final int k,  final int maxIterations,  final DistanceMeasure measure,  final RandomGenerator random){
    this(k,maxIterations,measure,random,EmptyClusterStrategy.LARGEST_VARIANCE);
  }
  /** 
 * Build a clusterer.
 * @param k the number of clusters to split the data into
 * @param maxIterations the maximum number of iterations to run the algorithm for.
 * If negative, no maximum will be used.
 * @param measure the distance measure to use
 * @param random random generator to use for choosing initial centers
 * @param emptyStrategy strategy to use for handling empty clusters that
 * may appear during algorithm iterations
 */
  public KMeansPlusPlusClusterer(  final int k,  final int maxIterations,  final DistanceMeasure measure,  final RandomGenerator random,  final EmptyClusterStrategy emptyStrategy){
    super(measure);
    this.k=k;
    this.maxIterations=maxIterations;
    this.random=random;
    this.emptyStrategy=emptyStrategy;
  }
  /** 
 * Return the number of clusters this instance will use.
 * @return the number of clusters
 */
  public int getK(){
    return k;
  }
  /** 
 * Returns the maximum number of iterations this instance will use.
 * @return the maximum number of iterations, or -1 if no maximum is set
 */
  public int getMaxIterations(){
    return maxIterations;
  }
  /** 
 * Returns the random generator this instance will use.
 * @return the random generator
 */
  public RandomGenerator getRandomGenerator(){
    return random;
  }
  /** 
 * Returns the {@link EmptyClusterStrategy} used by this instance.
 * @return the {@link EmptyClusterStrategy}
 */
  public EmptyClusterStrategy getEmptyClusterStrategy(){
    return emptyStrategy;
  }
  /** 
 * Runs the K-means++ clustering algorithm.
 * @param points the points to cluster
 * @return a list of clusters containing the points
 * @throws MathIllegalArgumentException if the data points are null or the number
 * of clusters is larger than the number of data points
 * @throws ConvergenceException if an empty cluster is encountered and the{@link #emptyStrategy} is set to {@code ERROR}
 */
  public List<CentroidCluster<T>> cluster(  final Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
    MathUtils.checkNotNull(points);
    if (points.size() < k) {
      throw new NumberIsTooSmallException(points.size(),k,false);
    }
    List<CentroidCluster<T>> clusters=chooseInitialCenters(points);
    int[] assignments=new int[points.size()];
    assignPointsToClusters(clusters,points,assignments);
    final int max=(maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;
    for (int count=0; count < max; count++) {
      boolean emptyCluster=false;
      List<CentroidCluster<T>> newClusters=new ArrayList<CentroidCluster<T>>();
      for (      final CentroidCluster<T> cluster : clusters) {
        final Clusterable newCenter;
        if (cluster.getPoints().isEmpty()) {
switch (emptyStrategy) {
case LARGEST_VARIANCE:
            newCenter=getPointFromLargestVarianceCluster(clusters);
          break;
case LARGEST_POINTS_NUMBER:
        newCenter=getPointFromLargestNumberCluster(clusters);
      break;
case FARTHEST_POINT:
    newCenter=getFarthestPoint(clusters);
  break;
default :
throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
}
emptyCluster=true;
}
 else {
newCenter=centroidOf(cluster.getPoints(),cluster.getCenter().getPoint().length);
}
newClusters.add(new CentroidCluster<T>(newCenter));
}
int changes=assignPointsToClusters(newClusters,points,assignments);
clusters=newClusters;
if (changes == 0 && !emptyCluster) {
return clusters;
}
}
return clusters;
}
/** 
 * Adds the given points to the closest {@link Cluster}.
 * @param clusters the {@link Cluster}s to add the points to
 * @param points the points to add to the given {@link Cluster}s
 * @param assignments points assignments to clusters
 * @return the number of points assigned to different clusters as the iteration before
 */
private int assignPointsToClusters(final List<CentroidCluster<T>> clusters,final Collection<T> points,final int[] assignments){
int assignedDifferently=0;
int pointIndex=0;
for (final T p : points) {
int clusterIndex=getNearestCluster(clusters,p);
if (clusterIndex != assignments[pointIndex]) {
assignedDifferently++;
}
CentroidCluster<T> cluster=clusters.get(clusterIndex);
cluster.addPoint(p);
assignments[pointIndex++]=clusterIndex;
}
return assignedDifferently;
}
/** 
 * Use K-means++ to choose the initial centers.
 * @param points the points to choose the initial centers from
 * @return the initial centers
 */
private List<CentroidCluster<T>> chooseInitialCenters(final Collection<T> points){
final List<T> pointList=Collections.unmodifiableList(new ArrayList<T>(points));
final int numPoints=pointList.size();
final boolean[] taken=new boolean[numPoints];
final List<CentroidCluster<T>> resultSet=new ArrayList<CentroidCluster<T>>();
final int firstPointIndex=random.nextInt(numPoints);
final T firstPoint=pointList.get(firstPointIndex);
resultSet.add(new CentroidCluster<T>(firstPoint));
taken[firstPointIndex]=true;
final double[] minDistSquared=new double[numPoints];
for (int i=0; i < numPoints; i++) {
if (i != firstPointIndex) {
double d=distance(firstPoint,pointList.get(i));
minDistSquared[i]=d * d;
}
}
while (resultSet.size() < k) {
double distSqSum=0.0;
for (int i=0; i < numPoints; i++) {
if (!taken[i]) {
distSqSum+=minDistSquared[i];
}
}
final double r=random.nextDouble() * distSqSum;
int nextPointIndex=-1;
double sum=0.0;
for (int i=0; i < numPoints; i++) {
if (!taken[i]) {
sum+=minDistSquared[i];
if (sum >= r) {
nextPointIndex=i;
break;
}
}
}
if (nextPointIndex == -1) {
for (int i=numPoints - 1; i >= 0; i--) {
if (!taken[i]) {
nextPointIndex=i;
break;
}
}
}
if (nextPointIndex >= 0) {
final T p=pointList.get(nextPointIndex);
resultSet.add(new CentroidCluster<T>(p));
taken[nextPointIndex]=true;
if (resultSet.size() < k) {
for (int j=0; j < numPoints; j++) {
if (!taken[j]) {
double d=distance(p,pointList.get(j));
double d2=d * d;
if (d2 < minDistSquared[j]) {
  minDistSquared[j]=d2;
}
}
}
}
}
 else {
break;
}
}
return resultSet;
}
/** 
 * Get a random point from the {@link Cluster} with the largest distance variance.
 * @param clusters the {@link Cluster}s to search
 * @return a random point from the selected cluster
 * @throws ConvergenceException if clusters are all empty
 */
private T getPointFromLargestVarianceCluster(final Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
double maxVariance=Double.NEGATIVE_INFINITY;
Cluster<T> selected=null;
for (final CentroidCluster<T> cluster : clusters) {
if (!cluster.getPoints().isEmpty()) {
final Clusterable center=cluster.getCenter();
final Variance stat=new Variance();
for (final T point : cluster.getPoints()) {
stat.increment(distance(point,center));
}
final double variance=stat.getResult();
if (variance > maxVariance) {
maxVariance=variance;
selected=cluster;
}
}
}
if (selected == null) {
throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
}
final List<T> selectedPoints=selected.getPoints();
return selectedPoints.remove(random.nextInt(selectedPoints.size()));
}
/** 
 * Get a random point from the {@link Cluster} with the largest number of points
 * @param clusters the {@link Cluster}s to search
 * @return a random point from the selected cluster
 * @throws ConvergenceException if clusters are all empty
 */
private T getPointFromLargestNumberCluster(final Collection<? extends Cluster<T>> clusters) throws ConvergenceException {
int maxNumber=0;
Cluster<T> selected=null;
for (final Cluster<T> cluster : clusters) {
final int number=cluster.getPoints().size();
if (number > maxNumber) {
maxNumber=number;
selected=cluster;
}
}
if (selected == null) {
throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
}
final List<T> selectedPoints=selected.getPoints();
return selectedPoints.remove(random.nextInt(selectedPoints.size()));
}
/** 
 * Get the point farthest to its cluster center
 * @param clusters the {@link Cluster}s to search
 * @return point farthest to its cluster center
 * @throws ConvergenceException if clusters are all empty
 */
private T getFarthestPoint(final Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
double maxDistance=Double.NEGATIVE_INFINITY;
Cluster<T> selectedCluster=null;
int selectedPoint=-1;
for (final CentroidCluster<T> cluster : clusters) {
final Clusterable center=cluster.getCenter();
final List<T> points=cluster.getPoints();
for (int i=0; i < points.size(); ++i) {
final double distance=distance(points.get(i),center);
if (distance > maxDistance) {
maxDistance=distance;
selectedCluster=cluster;
selectedPoint=i;
}
}
}
if (selectedCluster == null) {
throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
}
return selectedCluster.getPoints().remove(selectedPoint);
}
/** 
 * Returns the nearest {@link Cluster} to the given point
 * @param clusters the {@link Cluster}s to search
 * @param point the point to find the nearest {@link Cluster} for
 * @return the index of the nearest {@link Cluster} to the given point
 */
private int getNearestCluster(final Collection<CentroidCluster<T>> clusters,final T point){
double minDistance=Double.MAX_VALUE;
int clusterIndex=0;
int minCluster=0;
for (final CentroidCluster<T> c : clusters) {
final double distance=distance(point,c.getCenter());
if (distance < minDistance) {
minDistance=distance;
minCluster=clusterIndex;
}
clusterIndex++;
}
return minCluster;
}
/** 
 * Computes the centroid for a set of points.
 * @param points the set of points
 * @param dimension the point dimension
 * @return the computed centroid for the set of points
 */
private Clusterable centroidOf(final Collection<T> points,final int dimension){
final double[] centroid=new double[dimension];
for (final T p : points) {
final double[] point=p.getPoint();
for (int i=0; i < centroid.length; i++) {
centroid[i]+=point[i];
}
}
for (int i=0; i < centroid.length; i++) {
centroid[i]/=points.size();
}
return new DoublePoint(centroid);
}
}

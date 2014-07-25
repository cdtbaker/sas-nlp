/** 
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 * @author James Gosling
 * @author Kevin A. Smith
 */
public class QSortAlgorithm extends SortAlgorithm {
  /** 
 * A version of pause() that makes it easier to ensure that we pause
 * exactly the right number of times.
 */
  private boolean pauseTrue(  int lo,  int hi) throws Exception {
    super.pause(lo,hi);
    return true;
  }
  /** 
 * This is a generic version of C.A.R Hoare's Quick Sort
 * algorithm.  This will handle arrays that are already
 * sorted, and arrays with duplicate keys.<BR>
 * If you think of a one dimensional array as going from
 * the lowest index on the left to the highest index on the right
 * then the parameters to this function are lowest index or
 * left and highest index or right.  The first time you call
 * this function it will be with the parameters 0, a.length - 1.
 * @param a       an integer array
 * @param lo0     left boundary of array partition
 * @param hi0     right boundary of array partition
 */
  void QuickSort(  int a[],  int lo0,  int hi0) throws Exception {
    int lo=lo0;
    int hi=hi0;
    int mid;
    if (hi0 > lo0) {
      mid=a[(lo0 + hi0) / 2];
      while (lo <= hi) {
        while ((lo < hi0) && pauseTrue(lo0,hi0) && (a[lo] < mid)) {
          ++lo;
        }
        while ((hi > lo0) && pauseTrue(lo0,hi0) && (a[hi] > mid)) {
          --hi;
        }
        if (lo <= hi) {
          swap(a,lo,hi);
          ++lo;
          --hi;
        }
      }
      if (lo0 < hi) {
        QuickSort(a,lo0,hi);
      }
      if (lo < hi0) {
        QuickSort(a,lo,hi0);
      }
    }
  }
  private void swap(  int a[],  int i,  int j){
    int T;
    T=a[i];
    a[i]=a[j];
    a[j]=T;
  }
  @Override public void sort(  int a[]) throws Exception {
    QuickSort(a,0,a.length - 1);
  }
}

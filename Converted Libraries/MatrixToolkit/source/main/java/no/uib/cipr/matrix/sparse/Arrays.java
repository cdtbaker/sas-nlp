package no.uib.cipr.matrix.sparse;
/** 
 * Array utilities. Complements <code>java.util.Arrays</code>
 * @deprecated java.utils.Arrays and Google Guava provide this functionality nowadays.
 */
@Deprecated class Arrays {
  private Arrays(){
  }
  /** 
 * Searches for a key in a sorted array, and returns an index to an element
 * which is greater than or equal key.
 * @param indexSorted array of integers
 * @param keySearch for something equal or greater
 * @param beginStart posisiton in the index
 * @param endOne past the end position in the index
 * @return end if nothing greater or equal was found, else an index
 * satisfying the search criteria
 */
  public static int binarySearchGreater(  int[] index,  int key,  int begin,  int end){
    return binarySearchInterval(index,key,begin,end,true);
  }
  /** 
 * Searches for a key in a sorted array, and returns an index to an element
 * which is greater than or equal key.
 * @param indexSorted array of integers
 * @param keySearch for something equal or greater
 * @return index.length if nothing greater or equal was found, else an index
 * satisfying the search criteria
 */
  public static int binarySearchGreater(  int[] index,  int key){
    return binarySearchInterval(index,key,0,index.length,true);
  }
  /** 
 * Searches for a key in a sorted array, and returns an index to an element
 * which is smaller than or equal key.
 * @param indexSorted array of integers
 * @param keySearch for something equal or greater
 * @param beginStart posisiton in the index
 * @param endOne past the end position in the index
 * @return begin-1 if nothing smaller or equal was found, else an index
 * satisfying the search criteria
 */
  public static int binarySearchSmaller(  int[] index,  int key,  int begin,  int end){
    return binarySearchInterval(index,key,begin,end,false);
  }
  /** 
 * Searches for a key in a sorted array, and returns an index to an element
 * which is smaller than or equal key.
 * @param indexSorted array of integers
 * @param keySearch for something equal or greater
 * @return -1 if nothing smaller or equal was found, else an index
 * satisfying the search criteria
 */
  public static int binarySearchSmaller(  int[] index,  int key){
    return binarySearchInterval(index,key,0,index.length,false);
  }
  /** 
 * Searches for a key in a subset of a sorted array.
 * @param indexSorted array of integers
 * @param keyKey to search for
 * @param beginStart posisiton in the index
 * @param endOne past the end position in the index
 * @return Integer index to key. -1 if not found
 */
  public static int binarySearch(  int[] index,  int key,  int begin,  int end){
    return java.util.Arrays.binarySearch(index,begin,end,key);
  }
  private static int binarySearchInterval(  int[] index,  int key,  int begin,  int end,  boolean greater){
    if (begin == end)     if (greater)     return end;
 else     return begin - 1;
    end--;
    int mid=(end + begin) >> 1;
    while (begin <= end) {
      mid=(end + begin) >> 1;
      if (index[mid] < key)       begin=mid + 1;
 else       if (index[mid] > key)       end=mid - 1;
 else       return mid;
    }
    if ((greater && index[mid] >= key) || (!greater && index[mid] <= key))     return mid;
 else     if (greater)     return mid + 1;
 else     return mid - 1;
  }
  /** 
 * Finds the number of repeated entries
 * @param numMaximum index value
 * @param indIndices to check for repetitions
 * @return Array of length <code>num</code> with the number of repeated
 * indices of <code>ind</code>
 */
  public static int[] bandwidth(  int num,  int[] ind){
    int[] nz=new int[num];
    for (int i=0; i < ind.length; ++i)     nz[ind[i]]++;
    return nz;
  }
}

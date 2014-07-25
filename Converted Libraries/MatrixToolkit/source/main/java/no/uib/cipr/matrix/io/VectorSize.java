package no.uib.cipr.matrix.io;
/** 
 * Contains the size of a vectir stored in a variant of the <a
 * href="http://math.nist.gov/MatrixMarket">Matrix Market</a> exchange format
 */
public class VectorSize {
  /** 
 * Size of the vector
 */
  private int size;
  /** 
 * Number of entries stored
 */
  private int numEntries;
  /** 
 * Constructor for VectorSize. Assumes dense format
 * @param sizeSize of the matrix
 */
  public VectorSize(  int size){
    this.size=size;
    numEntries=size;
    if (size < 0)     throw new IllegalArgumentException("size < 0");
  }
  /** 
 * Constructor for VectorSize
 * @param sizeSize of the matrix
 * @param numEntriesNumber of entries stored
 */
  public VectorSize(  int size,  int numEntries){
    this.size=size;
    this.numEntries=numEntries;
    if (size < 0 || numEntries < 0)     throw new IllegalArgumentException("size < 0 || numEntries < 0");
    if (numEntries > size)     throw new IllegalArgumentException("numEntries > size");
  }
  /** 
 * Returns the size of the vector
 */
  public int size(){
    return size;
  }
  /** 
 * Returns the number of entries in the vector
 */
  public int numEntries(){
    return numEntries;
  }
}

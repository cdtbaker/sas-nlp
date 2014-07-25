package no.uib.cipr.matrix;
/** 
 * An entry of a vector. Returned by the iterators over a vector structure
 */
public interface VectorEntry {
  /** 
 * Returns the current index
 */
  int index();
  /** 
 * Returns the value at the current index
 */
  double get();
  /** 
 * Sets the value at the current index
 */
  void set(  double value);
}

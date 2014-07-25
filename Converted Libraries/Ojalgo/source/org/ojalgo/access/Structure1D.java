package org.ojalgo.access;
interface Structure1D {
  /** 
 * @return The total number of elements in this structure.
 */
  long count();
  /** 
 * @return The total number of elements contained in this structure
 * @deprecated v35 Use {@link #count()} instead.
 */
  @Deprecated int size();
}

package org.ojalgo.access;
interface StructureAnyD extends Structure1D {
  /** 
 * count() == count(0) * count(1) * count(2) * count(3) * ...
 */
  long count(  int dimension);
  /** 
 * size() == size(0)*size(1)*size(2)*size(3)*...
 * @deprecated v35 Use {@link #count(int)} instead
 */
  @Deprecated int size(  int aDim);
}

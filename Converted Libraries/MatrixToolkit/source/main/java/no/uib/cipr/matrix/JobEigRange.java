package no.uib.cipr.matrix;
/** 
 * When computing eigenvalues, this indicates which eigenvalues to locate. 
 */
enum JobEigRange {/** 
 * All eigenvalues will be computed 
 */
All, /** 
 * The eigenvalues with the given indices are computed 
 */
Indices, /** 
 * Eigenvalues in a given interval will be found 
 */
Interval; /** 
 * @return the netlib character version of this designation, for use with F2J.
 */
public String netlib(){
switch (this) {
case All:
    return "A";
case Indices:
  return "I";
default :
return "V";
}
}
}

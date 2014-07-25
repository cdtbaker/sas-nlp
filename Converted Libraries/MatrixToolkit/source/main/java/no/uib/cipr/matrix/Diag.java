package no.uib.cipr.matrix;
/** 
 * Diagonal enumeration 
 */
enum Diag {/** 
 * Matrix is not unit diagonal 
 */
NonUnit, /** 
 * Matrix is unit diagonal 
 */
Unit; /** 
 * @return the netlib character version of this designation, for use with F2J.
 */
public String netlib(){
  if (this == NonUnit)   return "N";
  return "U";
}
}

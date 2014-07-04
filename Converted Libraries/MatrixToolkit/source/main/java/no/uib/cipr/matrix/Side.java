package no.uib.cipr.matrix;
/** 
 * Side enumeration 
 */
enum Side {/** 
 * Apply operation from left 
 */
Left, /** 
 * Apply operation from right 
 */
Right; /** 
 * @return the netlib character version of this designation, for use with F2J.
 */
public String netlib(){
  if (this == Left)   return "L";
  return "R";
}
}

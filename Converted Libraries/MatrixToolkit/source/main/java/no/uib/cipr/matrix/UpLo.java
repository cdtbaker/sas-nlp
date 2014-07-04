package no.uib.cipr.matrix;
/** 
 * Designates the Upper or lower part of a Matrix.
 */
enum UpLo {Upper, Lower; /** 
 * @return the netlib character version of this designation, for use with F2J.
 */
public String netlib(){
  if (this == Upper)   return "U";
  return "L";
}
}

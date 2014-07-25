package cern.jet.stat.quantile;
/** 
 * Holds some utility methods shared by different quantile finding implementations.
 */
class Utils {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected Utils(){
    throw new RuntimeException("Non instantiable");
  }
  /** 
 * Similar to Math.ceil(value), but adjusts small numerical rounding errors +- epsilon.
 */
  public static long epsilonCeiling(  double value){
    double epsilon=0.0000001;
    return (long)Math.ceil(value - epsilon);
  }
}

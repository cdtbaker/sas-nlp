package cern.colt.map;
/** 
 * Provides various hash functions.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class HashFunctions extends Object {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected HashFunctions(){
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  char value){
    return (int)value;
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  double value){
    long bits=Double.doubleToLongBits(value);
    return (int)(bits ^ (bits >>> 32));
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  float value){
    return Float.floatToIntBits(value * 663608941.737f);
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  int value){
    return value;
  }
  /** 
 * Returns a hashcode for the specified value. 
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  long value){
    return (int)(value ^ (value >> 32));
  }
  /** 
 * Returns a hashcode for the specified object.
 * @return  a hash code value for the specified object. 
 */
  public static int hash(  Object object){
    return object == null ? 0 : object.hashCode();
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  short value){
    return (int)value;
  }
  /** 
 * Returns a hashcode for the specified value.
 * @return  a hash code value for the specified value. 
 */
  public static int hash(  boolean value){
    return value ? 1231 : 1237;
  }
}

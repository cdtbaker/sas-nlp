package sun.misc;
/** 
 * A class to signal exception from the RegexpPool class.
 * @author  James Gosling
 */
public class REException extends Exception {
  REException(  String s){
    super(s);
  }
}

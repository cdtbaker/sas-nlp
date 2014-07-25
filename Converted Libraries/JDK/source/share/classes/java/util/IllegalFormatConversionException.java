package java.util;
/** 
 * Unchecked exception thrown when the argument corresponding to the format
 * specifier is of an incompatible type.
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to any
 * method or constructor in this class will cause a {@link NullPointerException} to be thrown.
 * @since 1.5
 */
public class IllegalFormatConversionException extends IllegalFormatException {
  private static final long serialVersionUID=17000126L;
  private char c;
  private Class arg;
  /** 
 * Constructs an instance of this class with the mismatched conversion and
 * the corresponding argument class.
 * @param cInapplicable conversion
 * @param argClass of the mismatched argument
 */
  public IllegalFormatConversionException(  char c,  Class<?> arg){
    if (arg == null)     throw new NullPointerException();
    this.c=c;
    this.arg=arg;
  }
  /** 
 * Returns the inapplicable conversion.
 * @return  The inapplicable conversion
 */
  public char getConversion(){
    return c;
  }
  /** 
 * Returns the class of the mismatched argument.
 * @return   The class of the mismatched argument
 */
  public Class<?> getArgumentClass(){
    return arg;
  }
  public String getMessage(){
    return String.format("%c != %s",c,arg.getName());
  }
}

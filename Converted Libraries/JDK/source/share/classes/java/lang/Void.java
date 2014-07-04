package java.lang;
/** 
 * The {@code Void} class is an uninstantiable placeholder class to hold a
 * reference to the {@code Class} object representing the Java keyword
 * void.
 * @author  unascribed
 * @since   JDK1.1
 */
public final class Void {
  /** 
 * The {@code Class} object representing the pseudo-type corresponding to
 * the keyword {@code void}.
 */
  public static final Class<Void> TYPE=Class.getPrimitiveClass("void");
  private Void(){
  }
}

package sun.security.provider;
/** 
 * Native PRNG implementation for Windows. Currently a dummy, we do
 * not support a fully native PRNG on Windows.
 * @since   1.5
 * @author  Andreas Sterbenz
 */
public final class NativePRNG {
  static boolean isAvailable(){
    return false;
  }
}

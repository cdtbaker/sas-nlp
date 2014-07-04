package sun.security.jca;
import java.lang.ref.*;
import java.security.*;
/** 
 * Collection of static utility methods used by the security framework.
 * @author  Andreas Sterbenz
 * @since   1.5
 */
public final class JCAUtil {
  private JCAUtil(){
  }
  private static final Object LOCK=JCAUtil.class;
  private static volatile SecureRandom secureRandom;
  private final static int ARRAY_SIZE=4096;
  /** 
 * Get the size of a temporary buffer array to use in order to be
 * cache efficient. totalSize indicates the total amount of data to
 * be buffered. Used by the engineUpdate(ByteBuffer) methods.
 */
  public static int getTempArraySize(  int totalSize){
    return Math.min(ARRAY_SIZE,totalSize);
  }
  /** 
 * Get a SecureRandom instance. This method should me used by JDK
 * internal code in favor of calling "new SecureRandom()". That needs to
 * iterate through the provider table to find the default SecureRandom
 * implementation, which is fairly inefficient.
 */
  public static SecureRandom getSecureRandom(){
    SecureRandom r=secureRandom;
    if (r == null) {
synchronized (LOCK) {
        r=secureRandom;
        if (r == null) {
          r=new SecureRandom();
          secureRandom=r;
        }
      }
    }
    return r;
  }
}

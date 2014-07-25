package sun.security.provider;
import java.io.IOException;
/** 
 * Seed generator for Windows making use of MS CryptoAPI using native code.
 */
class NativeSeedGenerator extends SeedGenerator {
  /** 
 * Create a new CryptoAPI seed generator instances.
 * @exception IOException if CryptoAPI seeds are not available
 * on this platform.
 */
  NativeSeedGenerator() throws IOException {
    super();
    if (!nativeGenerateSeed(new byte[2])) {
      throw new IOException("Required native CryptoAPI features not " + " available on this machine");
    }
  }
  /** 
 * Native method to do the actual work.
 */
  private static native boolean nativeGenerateSeed(  byte[] result);
  @Override void getSeedBytes(  byte[] result){
    if (nativeGenerateSeed(result) == false) {
      throw new InternalError("Unexpected CryptoAPI failure generating seed");
    }
  }
}

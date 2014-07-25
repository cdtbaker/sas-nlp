package com.sun.net.ssl.internal.ssl;
import sun.security.ssl.SunJSSE;
/** 
 * Main class for the SunJSSE provider. The actual code was moved to the
 * class sun.security.ssl.SunJSSE, but for backward compatibility we
 * continue to use this class as the main Provider class.
 */
public final class Provider extends SunJSSE {
  private static final long serialVersionUID=3231825739635378733L;
  public Provider(){
    super();
  }
  public Provider(  java.security.Provider cryptoProvider){
    super(cryptoProvider);
  }
  public Provider(  String cryptoProvider){
    super(cryptoProvider);
  }
  public static synchronized boolean isFIPS(){
    return SunJSSE.isFIPS();
  }
  /** 
 * Installs the JSSE provider.
 */
  public static synchronized void install(){
  }
}

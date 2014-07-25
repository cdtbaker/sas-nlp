package sun.security.jgss;
/** 
 * Denotes what client is calling the JGSS-API. The object can be sent deep
 * into the mechanism level so that special actions can be performed for
 * different callers.
 */
public class GSSCaller {
  public static final GSSCaller CALLER_UNKNOWN=new GSSCaller();
  public static final GSSCaller CALLER_INITIATE=new GSSCaller();
  public static final GSSCaller CALLER_ACCEPT=new GSSCaller();
  public static final GSSCaller CALLER_SSL_CLIENT=new GSSCaller();
  public static final GSSCaller CALLER_SSL_SERVER=new GSSCaller();
}

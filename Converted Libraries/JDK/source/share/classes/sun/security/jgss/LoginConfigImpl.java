package sun.security.jgss;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.ietf.jgss.Oid;
/** 
 * A Configuration implementation especially designed for JGSS.
 * @author weijun.wang
 * @since 1.6
 */
public class LoginConfigImpl extends Configuration {
  private final Configuration config;
  private final GSSCaller caller;
  private final String mechName;
  private static final sun.security.util.Debug debug=sun.security.util.Debug.getInstance("gssloginconfig","\t[GSS LoginConfigImpl]");
  /** 
 * A new instance of LoginConfigImpl must be created for each login request
 * since it's only used by a single (caller, mech) pair
 * @param caller defined in GSSUtil as CALLER_XXX final fields
 * @param oid defined in GSSUtil as XXX_MECH_OID final fields
 */
  public LoginConfigImpl(  GSSCaller caller,  Oid mech){
    this.caller=caller;
    if (mech.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
      mechName="krb5";
    }
 else {
      throw new IllegalArgumentException(mech.toString() + " not supported");
    }
    config=java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Configuration>(){
      public Configuration run(){
        return Configuration.getConfiguration();
      }
    }
);
  }
  /** 
 * @param name Almost useless, since the (caller, mech) is already passed
 * into constructor. The only use will be detecting OTHER which
 * is called in LoginContext
 */
  public AppConfigurationEntry[] getAppConfigurationEntry(  String name){
    AppConfigurationEntry[] entries=null;
    if ("OTHER".equalsIgnoreCase(name)) {
      return null;
    }
    String[] alts=null;
    if ("krb5".equals(mechName)) {
      if (caller == GSSCaller.CALLER_INITIATE) {
        alts=new String[]{"com.sun.security.jgss.krb5.initiate","com.sun.security.jgss.initiate"};
      }
 else       if (caller == GSSCaller.CALLER_ACCEPT) {
        alts=new String[]{"com.sun.security.jgss.krb5.accept","com.sun.security.jgss.accept"};
      }
 else       if (caller == GSSCaller.CALLER_SSL_CLIENT) {
        alts=new String[]{"com.sun.security.jgss.krb5.initiate","com.sun.net.ssl.client"};
      }
 else       if (caller == GSSCaller.CALLER_SSL_SERVER) {
        alts=new String[]{"com.sun.security.jgss.krb5.accept","com.sun.net.ssl.server"};
      }
 else       if (caller instanceof HttpCaller) {
        alts=new String[]{"com.sun.security.jgss.krb5.initiate"};
      }
 else       if (caller == GSSCaller.CALLER_UNKNOWN) {
        throw new AssertionError("caller not defined");
      }
    }
 else {
      throw new IllegalArgumentException(mechName + " not supported");
    }
    for (    String alt : alts) {
      entries=config.getAppConfigurationEntry(alt);
      if (debug != null) {
        debug.println("Trying " + alt + ((entries == null) ? ": does not exist." : ": Found!"));
      }
      if (entries != null) {
        break;
      }
    }
    if (entries == null) {
      if (debug != null) {
        debug.println("Cannot read JGSS entry, use default values instead.");
      }
      entries=getDefaultConfigurationEntry();
    }
    return entries;
  }
  /** 
 * Default value for a caller-mech pair when no entry is defined in
 * the system-wide Configuration object.
 */
  private AppConfigurationEntry[] getDefaultConfigurationEntry(){
    HashMap<String,String> options=new HashMap<String,String>(2);
    if (mechName == null || mechName.equals("krb5")) {
      if (isServerSide(caller)) {
        options.put("useKeyTab","true");
        options.put("storeKey","true");
        options.put("doNotPrompt","true");
        options.put("isInitiator","false");
      }
 else {
        options.put("useTicketCache","true");
        options.put("doNotPrompt","false");
      }
      return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,options)};
    }
    return null;
  }
  private static boolean isServerSide(  GSSCaller caller){
    return GSSCaller.CALLER_ACCEPT == caller || GSSCaller.CALLER_SSL_SERVER == caller;
  }
}

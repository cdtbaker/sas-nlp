package sun.security.provider;
import java.util.*;
import java.security.*;
import sun.security.action.PutAllAction;
import sun.security.rsa.SunRsaSignEntries;
/** 
 * Provider used for verification of signed JAR files *if* the Sun and
 * SunRsaSign main classes have been removed. Otherwise, this provider is not
 * necessary and registers no algorithms. This functionality only exists to
 * support a use case required by a specific customer and is not generally
 * supported.
 * @since  1.7
 * @author Andreas Sterbenz
 */
public final class VerificationProvider extends Provider {
  private static final long serialVersionUID=7482667077568930381L;
  private static final boolean ACTIVE;
static {
    boolean b;
    try {
      Class.forName("sun.security.provider.Sun");
      Class.forName("sun.security.rsa.SunRsaSign");
      b=false;
    }
 catch (    ClassNotFoundException e) {
      b=true;
    }
    ACTIVE=b;
  }
  public VerificationProvider(){
    super("SunJarVerification",1.7,"Jar Verification Provider");
    if (ACTIVE == false) {
      return;
    }
    if (System.getSecurityManager() == null) {
      SunEntries.putEntries(this);
      SunRsaSignEntries.putEntries(this);
    }
 else {
      Map<Object,Object> map=new LinkedHashMap<>();
      SunEntries.putEntries(map);
      SunRsaSignEntries.putEntries(map);
      AccessController.doPrivileged(new PutAllAction(this,map));
    }
  }
}

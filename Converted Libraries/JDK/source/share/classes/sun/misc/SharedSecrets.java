package sun.misc;
import java.util.jar.JarFile;
import java.io.Console;
import java.io.FileDescriptor;
import java.security.ProtectionDomain;
import javax.security.auth.kerberos.KeyTab;
import java.security.AccessController;
/** 
 * A repository of "shared secrets", which are a mechanism for
 * calling implementation-private methods in another package without
 * using reflection. A package-private class implements a public
 * interface and provides the ability to call package-private methods
 * within that package; the object implementing that interface is
 * provided through a third package to which access is restricted.
 * This framework avoids the primary disadvantage of using reflection
 * for this purpose, namely the loss of compile-time checking. 
 */
public class SharedSecrets {
  private static final Unsafe unsafe=Unsafe.getUnsafe();
  private static JavaUtilJarAccess javaUtilJarAccess;
  private static JavaLangAccess javaLangAccess;
  private static JavaIOAccess javaIOAccess;
  private static JavaNetAccess javaNetAccess;
  private static JavaNioAccess javaNioAccess;
  private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
  private static JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
  private static JavaSecurityAccess javaSecurityAccess;
  private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;
  public static JavaUtilJarAccess javaUtilJarAccess(){
    if (javaUtilJarAccess == null) {
      unsafe.ensureClassInitialized(JarFile.class);
    }
    return javaUtilJarAccess;
  }
  public static void setJavaUtilJarAccess(  JavaUtilJarAccess access){
    javaUtilJarAccess=access;
  }
  public static void setJavaLangAccess(  JavaLangAccess jla){
    javaLangAccess=jla;
  }
  public static JavaLangAccess getJavaLangAccess(){
    return javaLangAccess;
  }
  public static void setJavaNetAccess(  JavaNetAccess jna){
    javaNetAccess=jna;
  }
  public static JavaNetAccess getJavaNetAccess(){
    return javaNetAccess;
  }
  public static void setJavaNioAccess(  JavaNioAccess jna){
    javaNioAccess=jna;
  }
  public static JavaNioAccess getJavaNioAccess(){
    if (javaNioAccess == null) {
      unsafe.ensureClassInitialized(java.nio.ByteOrder.class);
    }
    return javaNioAccess;
  }
  public static void setJavaIOAccess(  JavaIOAccess jia){
    javaIOAccess=jia;
  }
  public static JavaIOAccess getJavaIOAccess(){
    if (javaIOAccess == null) {
      unsafe.ensureClassInitialized(Console.class);
    }
    return javaIOAccess;
  }
  public static void setJavaIOFileDescriptorAccess(  JavaIOFileDescriptorAccess jiofda){
    javaIOFileDescriptorAccess=jiofda;
  }
  public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess(){
    if (javaIOFileDescriptorAccess == null)     unsafe.ensureClassInitialized(FileDescriptor.class);
    return javaIOFileDescriptorAccess;
  }
  public static void setJavaSecurityProtectionDomainAccess(  JavaSecurityProtectionDomainAccess jspda){
    javaSecurityProtectionDomainAccess=jspda;
  }
  public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess(){
    if (javaSecurityProtectionDomainAccess == null)     unsafe.ensureClassInitialized(ProtectionDomain.class);
    return javaSecurityProtectionDomainAccess;
  }
  public static void setJavaSecurityAccess(  JavaSecurityAccess jsa){
    javaSecurityAccess=jsa;
  }
  public static JavaSecurityAccess getJavaSecurityAccess(){
    if (javaSecurityAccess == null) {
      unsafe.ensureClassInitialized(AccessController.class);
    }
    return javaSecurityAccess;
  }
  public static void setJavaxSecurityAuthKerberosAccess(  JavaxSecurityAuthKerberosAccess jsaka){
    javaxSecurityAuthKerberosAccess=jsaka;
  }
  public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess(){
    if (javaxSecurityAuthKerberosAccess == null)     unsafe.ensureClassInitialized(KeyTab.class);
    return javaxSecurityAuthKerberosAccess;
  }
}

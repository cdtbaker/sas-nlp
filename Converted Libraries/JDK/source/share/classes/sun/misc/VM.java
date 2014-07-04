package sun.misc;
import static java.lang.Thread.State.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class VM {
  private static boolean suspended=false;
  /** 
 * @deprecated 
 */
  @Deprecated public static boolean threadsSuspended(){
    return suspended;
  }
  public static boolean allowThreadSuspension(  ThreadGroup g,  boolean b){
    return g.allowThreadSuspension(b);
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static boolean suspendThreads(){
    suspended=true;
    return true;
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static void unsuspendThreads(){
    suspended=false;
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static void unsuspendSomeThreads(){
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static final int STATE_GREEN=1;
  /** 
 * @deprecated 
 */
  @Deprecated public static final int STATE_YELLOW=2;
  /** 
 * @deprecated 
 */
  @Deprecated public static final int STATE_RED=3;
  /** 
 * @deprecated 
 */
  @Deprecated public static final int getState(){
    return STATE_GREEN;
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static void registerVMNotification(  VMNotification n){
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static void asChange(  int as_old,  int as_new){
  }
  /** 
 * @deprecated 
 */
  @Deprecated public static void asChange_otherthread(  int as_old,  int as_new){
  }
  /** 
 * Write the current profiling contents to the file "java.prof".
 * If the file already exists, it will be overwritten.
 */
  private static volatile boolean booted=false;
  public static void booted(){
    booted=true;
  }
  public static boolean isBooted(){
    return booted;
  }
  private static long directMemory=64 * 1024 * 1024;
  public static long maxDirectMemory(){
    return directMemory;
  }
  private static boolean pageAlignDirectMemory;
  public static boolean isDirectMemoryPageAligned(){
    return pageAlignDirectMemory;
  }
  private static boolean defaultAllowArraySyntax=false;
  private static boolean allowArraySyntax=defaultAllowArraySyntax;
  public static boolean allowArraySyntax(){
    return allowArraySyntax;
  }
  /** 
 * Returns the system property of the specified key saved at
 * system initialization time.  This method should only be used
 * for the system properties that are not changed during runtime.
 * It accesses a private copy of the system properties so
 * that user's locking of the system properties object will not
 * cause the library to deadlock.
 * Note that the saved system properties do not include
 * the ones set by sun.misc.Version.init().
 */
  public static String getSavedProperty(  String key){
    if (savedProps.isEmpty())     throw new IllegalStateException("Should be non-empty if initialized");
    return savedProps.getProperty(key);
  }
  private static final Properties savedProps=new Properties();
  public static void saveAndRemoveProperties(  Properties props){
    if (booted)     throw new IllegalStateException("System initialization has completed");
    savedProps.putAll(props);
    String s=(String)props.remove("sun.nio.MaxDirectMemorySize");
    if (s != null) {
      if (s.equals("-1")) {
        directMemory=Runtime.getRuntime().maxMemory();
      }
 else {
        long l=Long.parseLong(s);
        if (l > -1)         directMemory=l;
      }
    }
    s=(String)props.remove("sun.nio.PageAlignDirectMemory");
    if ("true".equals(s))     pageAlignDirectMemory=true;
    s=props.getProperty("sun.lang.ClassLoader.allowArraySyntax");
    allowArraySyntax=(s == null ? defaultAllowArraySyntax : Boolean.parseBoolean(s));
    props.remove("java.lang.Integer.IntegerCache.high");
    props.remove("sun.zip.disableMemoryMapping");
    props.remove("sun.java.launcher.diag");
  }
  public static void initializeOSEnvironment(){
    if (!booted) {
      OSEnvironment.initialize();
    }
  }
  private static volatile int finalRefCount=0;
  private static volatile int peakFinalRefCount=0;
  public static int getFinalRefCount(){
    return finalRefCount;
  }
  public static int getPeakFinalRefCount(){
    return peakFinalRefCount;
  }
  public static void addFinalRefCount(  int n){
    finalRefCount+=n;
    if (finalRefCount > peakFinalRefCount) {
      peakFinalRefCount=finalRefCount;
    }
  }
  /** 
 * Returns Thread.State for the given threadStatus
 */
  public static Thread.State toThreadState(  int threadStatus){
    if ((threadStatus & JVMTI_THREAD_STATE_RUNNABLE) != 0) {
      return RUNNABLE;
    }
 else     if ((threadStatus & JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER) != 0) {
      return BLOCKED;
    }
 else     if ((threadStatus & JVMTI_THREAD_STATE_WAITING_INDEFINITELY) != 0) {
      return WAITING;
    }
 else     if ((threadStatus & JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT) != 0) {
      return TIMED_WAITING;
    }
 else     if ((threadStatus & JVMTI_THREAD_STATE_TERMINATED) != 0) {
      return TERMINATED;
    }
 else     if ((threadStatus & JVMTI_THREAD_STATE_ALIVE) == 0) {
      return NEW;
    }
 else {
      return RUNNABLE;
    }
  }
  private final static int JVMTI_THREAD_STATE_ALIVE=0x0001;
  private final static int JVMTI_THREAD_STATE_TERMINATED=0x0002;
  private final static int JVMTI_THREAD_STATE_RUNNABLE=0x0004;
  private final static int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER=0x0400;
  private final static int JVMTI_THREAD_STATE_WAITING_INDEFINITELY=0x0010;
  private final static int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT=0x0020;
static {
    initialize();
  }
  private native static void initialize();
}

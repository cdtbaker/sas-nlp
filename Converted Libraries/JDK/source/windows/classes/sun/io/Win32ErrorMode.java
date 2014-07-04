package sun.io;
/** 
 * Used to set the Windows error mode at VM initialization time.
 * <p>
 * The error mode decides whether the system will handle specific types of serious errors
 * or whether the process will handle them.
 * @since 1.6
 */
public class Win32ErrorMode {
  private static final long SEM_FAILCRITICALERRORS=0x0001;
  private static final long SEM_NOGPFAULTERRORBOX=0x0002;
  private static final long SEM_NOALIGNMENTFAULTEXCEPT=0x0004;
  private static final long SEM_NOOPENFILEERRORBOX=0x8000;
  private Win32ErrorMode(){
  }
  /** 
 * Invoke at VM initialization time to disable the critical error message box.
 * <p>
 * The critial error message box is disabled unless the system property
 * <tt>sun.io.allowCriticalErrorMessageBox</tt> is set to something other than
 * <code>false</code>. This includes the empty string.
 * <p>
 * This method does nothing if invoked after VM and class library initialization
 * has completed.
 */
  public static void initialize(){
    if (!sun.misc.VM.isBooted()) {
      String s=(String)System.getProperty("sun.io.allowCriticalErrorMessageBox");
      if (s == null || s.equals(Boolean.FALSE.toString())) {
        long mode=setErrorMode(0);
        mode|=SEM_FAILCRITICALERRORS;
        setErrorMode(mode);
      }
    }
  }
  private static native long setErrorMode(  long mode);
}

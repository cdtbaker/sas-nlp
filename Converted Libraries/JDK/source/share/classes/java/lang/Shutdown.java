package java.lang;
/** 
 * Package-private utility class containing data structures and logic
 * governing the virtual-machine shutdown sequence.
 * @author   Mark Reinhold
 * @since    1.3
 */
class Shutdown {
  private static final int RUNNING=0;
  private static final int HOOKS=1;
  private static final int FINALIZERS=2;
  private static int state=RUNNING;
  private static boolean runFinalizersOnExit=false;
  private static final int MAX_SYSTEM_HOOKS=10;
  private static final Runnable[] hooks=new Runnable[MAX_SYSTEM_HOOKS];
  private static int currentRunningHook=0;
private static class Lock {
  }
  private static Object lock=new Lock();
  private static Object haltLock=new Lock();
  static void setRunFinalizersOnExit(  boolean run){
synchronized (lock) {
      runFinalizersOnExit=run;
    }
  }
  /** 
 * Add a new shutdown hook.  Checks the shutdown state and the hook itself,
 * but does not do any security checks.
 * The registerShutdownInProgress parameter should be false except
 * registering the DeleteOnExitHook since the first file may
 * be added to the delete on exit list by the application shutdown
 * hooks.
 * @params slot  the slot in the shutdown hook array, whose element
 * will be invoked in order during shutdown
 * @params registerShutdownInProgress true to allow the hook
 * to be registered even if the shutdown is in progress.
 * @params hook  the hook to be registered
 * @throw IllegalStateException
 * if registerShutdownInProgress is false and shutdown is in progress; or
 * if registerShutdownInProgress is true and the shutdown process
 * already passes the given slot
 */
  static void add(  int slot,  boolean registerShutdownInProgress,  Runnable hook){
synchronized (lock) {
      if (hooks[slot] != null)       throw new InternalError("Shutdown hook at slot " + slot + " already registered");
      if (!registerShutdownInProgress) {
        if (state > RUNNING)         throw new IllegalStateException("Shutdown in progress");
      }
 else {
        if (state > HOOKS || (state == HOOKS && slot <= currentRunningHook))         throw new IllegalStateException("Shutdown in progress");
      }
      hooks[slot]=hook;
    }
  }
  private static void runHooks(){
    for (int i=0; i < MAX_SYSTEM_HOOKS; i++) {
      try {
        Runnable hook;
synchronized (lock) {
          currentRunningHook=i;
          hook=hooks[i];
        }
        if (hook != null)         hook.run();
      }
 catch (      Throwable t) {
        if (t instanceof ThreadDeath) {
          ThreadDeath td=(ThreadDeath)t;
          throw td;
        }
      }
    }
  }
  static void halt(  int status){
synchronized (haltLock) {
      halt0(status);
    }
  }
  static native void halt0(  int status);
  private static native void runAllFinalizers();
  private static void sequence(){
synchronized (lock) {
      if (state != HOOKS)       return;
    }
    runHooks();
    boolean rfoe;
synchronized (lock) {
      state=FINALIZERS;
      rfoe=runFinalizersOnExit;
    }
    if (rfoe)     runAllFinalizers();
  }
  static void exit(  int status){
    boolean runMoreFinalizers=false;
synchronized (lock) {
      if (status != 0)       runFinalizersOnExit=false;
switch (state) {
case RUNNING:
        state=HOOKS;
      break;
case HOOKS:
    break;
case FINALIZERS:
  if (status != 0) {
    halt(status);
  }
 else {
    runMoreFinalizers=runFinalizersOnExit;
  }
break;
}
}
if (runMoreFinalizers) {
runAllFinalizers();
halt(status);
}
synchronized (Shutdown.class) {
sequence();
halt(status);
}
}
static void shutdown(){
synchronized (lock) {
switch (state) {
case RUNNING:
state=HOOKS;
break;
case HOOKS:
case FINALIZERS:
break;
}
}
synchronized (Shutdown.class) {
sequence();
}
}
}

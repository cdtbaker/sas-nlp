package sun.nio.fs;
import static sun.nio.fs.WindowsNativeDispatcher.*;
import static sun.nio.fs.WindowsConstants.*;
/** 
 * Security related utility methods.
 */
class WindowsSecurity {
  private WindowsSecurity(){
  }
  private static long openProcessToken(  int access){
    try {
      return OpenProcessToken(GetCurrentProcess(),access);
    }
 catch (    WindowsException x) {
      return 0L;
    }
  }
  /** 
 * Returns the access token for this process with TOKEN_DUPLICATE access
 */
  static final long processTokenWithDuplicateAccess=openProcessToken(TOKEN_DUPLICATE);
  /** 
 * Returns the access token for this process with TOKEN_QUERY access
 */
  static final long processTokenWithQueryAccess=openProcessToken(TOKEN_QUERY);
  /** 
 * Returned by enablePrivilege when code may require a given privilege.
 * The drop method should be invoked after the operation completes so as
 * to revert the privilege.
 */
static interface Privilege {
    void drop();
  }
  /** 
 * Attempts to enable the given privilege for this method.
 */
  static Privilege enablePrivilege(  String priv){
    final long pLuid;
    try {
      pLuid=LookupPrivilegeValue(priv);
    }
 catch (    WindowsException x) {
      throw new AssertionError(x);
    }
    long hToken=0L;
    boolean impersontating=false;
    boolean elevated=false;
    try {
      hToken=OpenThreadToken(GetCurrentThread(),TOKEN_ADJUST_PRIVILEGES,false);
      if (hToken == 0L && processTokenWithDuplicateAccess != 0L) {
        hToken=DuplicateTokenEx(processTokenWithDuplicateAccess,(TOKEN_ADJUST_PRIVILEGES | TOKEN_IMPERSONATE));
        SetThreadToken(0L,hToken);
        impersontating=true;
      }
      if (hToken != 0L) {
        AdjustTokenPrivileges(hToken,pLuid,SE_PRIVILEGE_ENABLED);
        elevated=true;
      }
    }
 catch (    WindowsException x) {
    }
    final long token=hToken;
    final boolean stopImpersontating=impersontating;
    final boolean needToRevert=elevated;
    return new Privilege(){
      @Override public void drop(){
        try {
          if (stopImpersontating) {
            SetThreadToken(0L,0L);
          }
 else {
            if (needToRevert) {
              AdjustTokenPrivileges(token,pLuid,0);
            }
          }
        }
 catch (        WindowsException x) {
          throw new AssertionError(x);
        }
      }
    }
;
  }
}

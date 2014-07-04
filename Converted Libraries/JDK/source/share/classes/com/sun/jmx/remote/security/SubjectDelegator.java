package com.sun.jmx.remote.security;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import javax.management.remote.SubjectDelegationPermission;
import com.sun.jmx.remote.util.CacheMap;
public class SubjectDelegator {
  private static final int PRINCIPALS_CACHE_SIZE=10;
  private static final int ACC_CACHE_SIZE=10;
  private CacheMap<Subject,Principal[]> principalsCache;
  private CacheMap<Subject,AccessControlContext> accCache;
  public synchronized AccessControlContext delegatedContext(  AccessControlContext authenticatedACC,  Subject delegatedSubject,  boolean removeCallerContext) throws SecurityException {
    if (principalsCache == null || accCache == null) {
      principalsCache=new CacheMap<Subject,Principal[]>(PRINCIPALS_CACHE_SIZE);
      accCache=new CacheMap<Subject,AccessControlContext>(ACC_CACHE_SIZE);
    }
    Principal[] delegatedPrincipals=principalsCache.get(delegatedSubject);
    if (delegatedPrincipals == null) {
      delegatedPrincipals=delegatedSubject.getPrincipals().toArray(new Principal[0]);
      principalsCache.put(delegatedSubject,delegatedPrincipals);
    }
    AccessControlContext delegatedACC=accCache.get(delegatedSubject);
    if (delegatedACC == null) {
      if (removeCallerContext) {
        delegatedACC=JMXSubjectDomainCombiner.getDomainCombinerContext(delegatedSubject);
      }
 else {
        delegatedACC=JMXSubjectDomainCombiner.getContext(delegatedSubject);
      }
      accCache.put(delegatedSubject,delegatedACC);
    }
    final Principal[] dp=delegatedPrincipals;
    PrivilegedAction<Void> action=new PrivilegedAction<Void>(){
      public Void run(){
        for (int i=0; i < dp.length; i++) {
          final String pname=dp[i].getClass().getName() + "." + dp[i].getName();
          Permission sdp=new SubjectDelegationPermission(pname);
          AccessController.checkPermission(sdp);
        }
        return null;
      }
    }
;
    AccessController.doPrivileged(action,authenticatedACC);
    return delegatedACC;
  }
  /** 
 * Check if the connector server creator can assume the identity of each
 * principal in the authenticated subject, i.e. check if the connector
 * server creator codebase contains a subject delegation permission for
 * each principal present in the authenticated subject.
 * @return {@code true} if the connector server creator can delegate to all
 * the authenticated principals in the subject. Otherwise, {@code false}.
 */
  public static synchronized boolean checkRemoveCallerContext(  Subject subject){
    try {
      final Principal[] dp=subject.getPrincipals().toArray(new Principal[0]);
      for (int i=0; i < dp.length; i++) {
        final String pname=dp[i].getClass().getName() + "." + dp[i].getName();
        final Permission sdp=new SubjectDelegationPermission(pname);
        AccessController.checkPermission(sdp);
      }
    }
 catch (    SecurityException e) {
      return false;
    }
    return true;
  }
}

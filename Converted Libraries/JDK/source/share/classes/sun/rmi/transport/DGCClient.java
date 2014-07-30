package sun.rmi.transport;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import sun.misc.GC;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;
/** 
 * DGCClient implements the client-side of the RMI distributed garbage
 * collection system.
 * The external interface to DGCClient is the "registerRefs" method.
 * When a LiveRef to a remote object enters the VM, it needs to be
 * registered with the DGCClient to participate in distributed garbage
 * collection.
 * When the first LiveRef to a particular remote object is registered,
 * a "dirty" call is made to the server-side distributed garbage
 * collector for the remote object, which returns a lease guaranteeing
 * that the server-side DGC will not collect the remote object for a
 * certain period of time.  While LiveRef instances to remote objects
 * on a particular server exist, the DGCClient periodically sends more
 * "dirty" calls to renew its lease.
 * The DGCClient tracks the local reachability of registered LiveRef
 * instances (using phantom references).  When the LiveRef instance
 * for a particular remote object becomes garbage collected locally,
 * a "clean" call is made to the server-side distributed garbage
 * collector, indicating that the server no longer needs to keep the
 * remote object alive for this client.
 * @see java.rmi.dgc.DGC, sun.rmi.transport.DGCImpl
 * @author  Ann Wollrath
 * @author  Peter Jones
 */
final class DGCClient {
  /** 
 * next sequence number for DGC calls (access synchronized on class) 
 */
  private static long nextSequenceNum=Long.MIN_VALUE;
  /** 
 * unique identifier for this VM as a client of DGC 
 */
  private static VMID vmid=new VMID();
  /** 
 * lease duration to request (usually ignored by server) 
 */
  private static final long leaseValue=AccessController.doPrivileged(new GetLongAction("java.rmi.dgc.leaseValue",600000)).longValue();
  /** 
 * maximum interval between retries of failed clean calls 
 */
  private static final long cleanInterval=AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.cleanInterval",180000)).longValue();
  /** 
 * maximum interval between complete garbage collections of local heap 
 */
  private static final long gcInterval=AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.client.gcInterval",3600000)).longValue();
  /** 
 * minimum retry count for dirty calls that fail 
 */
  private static final int dirtyFailureRetries=5;
  /** 
 * retry count for clean calls that fail with ConnectException 
 */
  private static final int cleanFailureRetries=5;
  /** 
 * constant empty ObjID array for lease renewal optimization 
 */
  private static final ObjID[] emptyObjIDArray=new ObjID[0];
  /** 
 * ObjID for server-side DGC object 
 */
  private static final ObjID dgcID=new ObjID(ObjID.DGC_ID);
  private DGCClient(){
  }
  /** 
 * Register the LiveRef instances in the supplied list to participate
 * in distributed garbage collection.
 * All of the LiveRefs in the list must be for remote objects at the
 * given endpoint.
 */
  static void registerRefs(  Endpoint ep,  List refs){
    EndpointEntry epEntry;
    do {
      epEntry=EndpointEntry.lookup(ep);
    }
 while (!epEntry.registerRefs(refs));
  }
  /** 
 * Get the next sequence number to be used for a dirty or clean
 * operation from this VM.  This method should only be called while
 * synchronized on the EndpointEntry whose data structures the
 * operation affects.
 */
  private static synchronized long getNextSequenceNum(){
    return nextSequenceNum++;
  }
  /** 
 * Given the length of a lease and the time that it was granted,
 * compute the absolute time at which it should be renewed, giving
 * room for reasonable computational and communication delays.
 */
  private static long computeRenewTime(  long grantTime,  long duration){
    return grantTime + (duration / 2);
  }
  /** 
 * EndpointEntry encapsulates the client-side DGC information specific
 * to a particular Endpoint.  Of most significance is the table that
 * maps LiveRef value to RefEntry objects and the renew/clean thread
 * that handles asynchronous client-side DGC operations.
 */
private static class EndpointEntry {
    /** 
 * the endpoint that this entry is for 
 */
    private Endpoint endpoint;
    /** 
 * synthesized reference to the remote server-side DGC 
 */
    private DGC dgc;
    /** 
 * table of refs held for endpoint: maps LiveRef to RefEntry 
 */
    private Map refTable=new HashMap(5);
    /** 
 * set of RefEntry instances from last (failed) dirty call 
 */
    private Set invalidRefs=new HashSet(5);
    /** 
 * true if this entry has been removed from the global table 
 */
    private boolean removed=false;
    /** 
 * absolute time to renew current lease to this endpoint 
 */
    private long renewTime=Long.MAX_VALUE;
    /** 
 * absolute time current lease to this endpoint will expire 
 */
    private long expirationTime=Long.MIN_VALUE;
    /** 
 * count of recent dirty calls that have failed 
 */
    private int dirtyFailures=0;
    /** 
 * absolute time of first recent failed dirty call 
 */
    private long dirtyFailureStartTime;
    /** 
 * (average) elapsed time for recent failed dirty calls 
 */
    private long dirtyFailureDuration;
    /** 
 * renew/clean thread for handling lease renewals and clean calls 
 */
    private Thread renewCleanThread;
    /** 
 * true if renew/clean thread may be interrupted 
 */
    private boolean interruptible=false;
    /** 
 * reference queue for phantom references 
 */
    private ReferenceQueue refQueue=new ReferenceQueue();
    /** 
 * set of clean calls that need to be made 
 */
    private Set pendingCleans=new HashSet(5);
    /** 
 * global endpoint table: maps Endpoint to EndpointEntry 
 */
    private static Map endpointTable=new HashMap(5);
    /** 
 * handle for GC latency request (for future cancellation) 
 */
    private static GC.LatencyRequest gcLatencyRequest=null;
    /** 
 * Look up the EndpointEntry for the given Endpoint.  An entry is
 * created if one does not already exist.
 */
    public static EndpointEntry lookup(    Endpoint ep){
synchronized (endpointTable) {
        EndpointEntry entry=(EndpointEntry)endpointTable.get(ep);
        if (entry == null) {
          entry=new EndpointEntry(ep);
          endpointTable.put(ep,entry);
          if (gcLatencyRequest == null) {
            gcLatencyRequest=GC.requestLatency(gcInterval);
          }
        }
        return entry;
      }
    }
    private EndpointEntry(    final Endpoint endpoint){
      this.endpoint=endpoint;
      try {
        LiveRef dgcRef=new LiveRef(dgcID,endpoint,false);
        dgc=(DGC)Util.createProxy(DGCImpl.class,new UnicastRef(dgcRef),true);
      }
 catch (      RemoteException e) {
        throw new Error("internal error creating DGC stub");
      }
      renewCleanThread=AccessController.doPrivileged(new NewThreadAction(new RenewCleanThread(),"RenewClean-" + endpoint,true));
      renewCleanThread.start();
    }
    /** 
 * Register the LiveRef instances in the supplied list to participate
 * in distributed garbage collection.
 * This method returns false if this entry was removed from the
 * global endpoint table (because it was empty) before these refs
 * could be registered.  In that case, a new EndpointEntry needs
 * to be looked up.
 * This method must NOT be called while synchronized on this entry.
 */
    public boolean registerRefs(    List refs){
      assert !Thread.holdsLock(this);
      Set refsToDirty=null;
      long sequenceNum;
synchronized (this) {
        if (removed) {
          return false;
        }
        Iterator iter=refs.iterator();
        while (iter.hasNext()) {
          LiveRef ref=(LiveRef)iter.next();
          assert ref.getEndpoint().equals(endpoint);
          RefEntry refEntry=(RefEntry)refTable.get(ref);
          if (refEntry == null) {
            LiveRef refClone=(LiveRef)ref.clone();
            refEntry=new RefEntry(refClone);
            refTable.put(refClone,refEntry);
            if (refsToDirty == null) {
              refsToDirty=new HashSet(5);
            }
            refsToDirty.add(refEntry);
          }
          refEntry.addInstanceToRefSet(ref);
        }
        if (refsToDirty == null) {
          return true;
        }
        refsToDirty.addAll(invalidRefs);
        invalidRefs.clear();
        sequenceNum=getNextSequenceNum();
      }
      makeDirtyCall(refsToDirty,sequenceNum);
      return true;
    }
    /** 
 * Remove the given RefEntry from the ref table.  If that makes
 * the ref table empty, remove this entry from the global endpoint
 * table.
 * This method must ONLY be called while synchronized on this entry.
 */
    private void removeRefEntry(    RefEntry refEntry){
      assert Thread.holdsLock(this);
      assert !removed;
      assert refTable.containsKey(refEntry.getRef());
      refTable.remove(refEntry.getRef());
      invalidRefs.remove(refEntry);
      if (refTable.isEmpty()) {
synchronized (endpointTable) {
          endpointTable.remove(endpoint);
          Transport transport=endpoint.getOutboundTransport();
          transport.free(endpoint);
          if (endpointTable.isEmpty()) {
            assert gcLatencyRequest != null;
            gcLatencyRequest.cancel();
            gcLatencyRequest=null;
          }
          removed=true;
        }
      }
    }
    /** 
 * Make a DGC dirty call to this entry's endpoint, for the ObjIDs
 * corresponding to the given set of refs and with the given
 * sequence number.
 * This method must NOT be called while synchronized on this entry.
 */
    private void makeDirtyCall(    Set refEntries,    long sequenceNum){
      assert !Thread.holdsLock(this);
      ObjID[] ids;
      if (refEntries != null) {
        ids=createObjIDArray(refEntries);
      }
 else {
        ids=emptyObjIDArray;
      }
      long startTime=System.currentTimeMillis();
      try {
        Lease lease=dgc.dirty(ids,sequenceNum,new Lease(vmid,leaseValue));
        long duration=lease.getValue();
        long newRenewTime=computeRenewTime(startTime,duration);
        long newExpirationTime=startTime + duration;
synchronized (this) {
          dirtyFailures=0;
          setRenewTime(newRenewTime);
          expirationTime=newExpirationTime;
        }
      }
 catch (      Exception e) {
        long endTime=System.currentTimeMillis();
synchronized (this) {
          dirtyFailures++;
          if (dirtyFailures == 1) {
            dirtyFailureStartTime=startTime;
            dirtyFailureDuration=endTime - startTime;
            setRenewTime(endTime);
          }
 else {
            int n=dirtyFailures - 2;
            if (n == 0) {
              dirtyFailureDuration=Math.max((dirtyFailureDuration + (endTime - startTime)) >> 1,1000);
            }
            long newRenewTime=endTime + (dirtyFailureDuration << n);
            if (newRenewTime < expirationTime || dirtyFailures < dirtyFailureRetries || newRenewTime < dirtyFailureStartTime + leaseValue) {
              setRenewTime(newRenewTime);
            }
 else {
              setRenewTime(Long.MAX_VALUE);
            }
          }
          if (refEntries != null) {
            invalidRefs.addAll(refEntries);
            Iterator iter=refEntries.iterator();
            while (iter.hasNext()) {
              RefEntry refEntry=(RefEntry)iter.next();
              refEntry.markDirtyFailed();
            }
          }
          if (renewTime >= expirationTime) {
            invalidRefs.addAll(refTable.values());
          }
        }
      }
    }
    /** 
 * Set the absolute time at which the lease for this entry should
 * be renewed.
 * This method must ONLY be called while synchronized on this entry.
 */
    private void setRenewTime(    long newRenewTime){
      assert Thread.holdsLock(this);
      if (newRenewTime < renewTime) {
        renewTime=newRenewTime;
        if (interruptible) {
          AccessController.doPrivileged(new PrivilegedAction<Void>(){
            public Void run(){
              renewCleanThread.interrupt();
              return null;
            }
          }
);
        }
      }
 else {
        renewTime=newRenewTime;
      }
    }
    /** 
 * RenewCleanThread handles the asynchronous client-side DGC activity
 * for this entry: renewing the leases and making clean calls.
 */
private class RenewCleanThread implements Runnable {
      public void run(){
        do {
          long timeToWait;
          RefEntry.PhantomLiveRef phantom=null;
          boolean needRenewal=false;
          Set refsToDirty=null;
          long sequenceNum=Long.MIN_VALUE;
synchronized (EndpointEntry.this) {
            long timeUntilRenew=renewTime - System.currentTimeMillis();
            timeToWait=Math.max(timeUntilRenew,1);
            if (!pendingCleans.isEmpty()) {
              timeToWait=Math.min(timeToWait,cleanInterval);
            }
            interruptible=true;
          }
          try {
            phantom=(RefEntry.PhantomLiveRef)refQueue.remove(timeToWait);
          }
 catch (          InterruptedException e) {
          }
synchronized (EndpointEntry.this) {
            interruptible=false;
            Thread.interrupted();
            if (phantom != null) {
              processPhantomRefs(phantom);
            }
            long currentTime=System.currentTimeMillis();
            if (currentTime > renewTime) {
              needRenewal=true;
              if (!invalidRefs.isEmpty()) {
                refsToDirty=invalidRefs;
                invalidRefs=new HashSet(5);
              }
              sequenceNum=getNextSequenceNum();
            }
          }
          if (needRenewal) {
            makeDirtyCall(refsToDirty,sequenceNum);
          }
          if (!pendingCleans.isEmpty()) {
            makeCleanCalls();
          }
        }
 while (!removed || !pendingCleans.isEmpty());
      }
    }
    /** 
 * Process the notification of the given phantom reference and any
 * others that are on this entry's reference queue.  Each phantom
 * reference is removed from its RefEntry's ref set.  All ref
 * entries that have no more registered instances are collected
 * into up to two batched clean call requests: one for refs
 * requiring a "strong" clean call, and one for the rest.
 * This method must ONLY be called while synchronized on this entry.
 */
    private void processPhantomRefs(    RefEntry.PhantomLiveRef phantom){
      assert Thread.holdsLock(this);
      Set strongCleans=null;
      Set normalCleans=null;
      do {
        RefEntry refEntry=phantom.getRefEntry();
        refEntry.removeInstanceFromRefSet(phantom);
        if (refEntry.isRefSetEmpty()) {
          if (refEntry.hasDirtyFailed()) {
            if (strongCleans == null) {
              strongCleans=new HashSet(5);
            }
            strongCleans.add(refEntry);
          }
 else {
            if (normalCleans == null) {
              normalCleans=new HashSet(5);
            }
            normalCleans.add(refEntry);
          }
          removeRefEntry(refEntry);
        }
      }
 while ((phantom=(RefEntry.PhantomLiveRef)refQueue.poll()) != null);
      if (strongCleans != null) {
        pendingCleans.add(new CleanRequest(createObjIDArray(strongCleans),getNextSequenceNum(),true));
      }
      if (normalCleans != null) {
        pendingCleans.add(new CleanRequest(createObjIDArray(normalCleans),getNextSequenceNum(),false));
      }
    }
    /** 
 * CleanRequest holds the data for the parameters of a clean call
 * that needs to be made.
 */
private static class CleanRequest {
      final ObjID[] objIDs;
      final long sequenceNum;
      final boolean strong;
      /** 
 * how many times this request has failed 
 */
      int failures=0;
      CleanRequest(      ObjID[] objIDs,      long sequenceNum,      boolean strong){
        this.objIDs=objIDs;
        this.sequenceNum=sequenceNum;
        this.strong=strong;
      }
    }
    /** 
 * Make all of the clean calls described by the clean requests in
 * this entry's set of "pending cleans".  Clean requests for clean
 * calls that succeed are removed from the "pending cleans" set.
 * This method must NOT be called while synchronized on this entry.
 */
    private void makeCleanCalls(){
      assert !Thread.holdsLock(this);
      Iterator iter=pendingCleans.iterator();
      while (iter.hasNext()) {
        CleanRequest request=(CleanRequest)iter.next();
        try {
          dgc.clean(request.objIDs,request.sequenceNum,vmid,request.strong);
          iter.remove();
        }
 catch (        Exception e) {
          if (++request.failures >= cleanFailureRetries) {
            iter.remove();
          }
        }
      }
    }
    /** 
 * Create an array of ObjIDs (needed for the DGC remote calls)
 * from the ids in the given set of refs.
 */
    private static ObjID[] createObjIDArray(    Set refEntries){
      ObjID[] ids=new ObjID[refEntries.size()];
      Iterator iter=refEntries.iterator();
      for (int i=0; i < ids.length; i++) {
        ids[i]=((RefEntry)iter.next()).getRef().getObjID();
      }
      return ids;
    }
    /** 
 * RefEntry encapsulates the client-side DGC information specific
 * to a particular LiveRef value.  In particular, it contains a
 * set of phantom references to all of the instances of the LiveRef
 * value registered in the system (but not garbage collected
 * locally).
 */
private class RefEntry {
      /** 
 * LiveRef value for this entry (not a registered instance) 
 */
      private LiveRef ref;
      /** 
 * set of phantom references to registered instances 
 */
      private Set refSet=new HashSet(5);
      /** 
 * true if a dirty call containing this ref has failed 
 */
      private boolean dirtyFailed=false;
      public RefEntry(      LiveRef ref){
        this.ref=ref;
      }
      /** 
 * Return the LiveRef value for this entry (not a registered
 * instance).
 */
      public LiveRef getRef(){
        return ref;
      }
      /** 
 * Add a LiveRef to the set of registered instances for this entry.
 * This method must ONLY be invoked while synchronized on this
 * RefEntry's EndpointEntry.
 */
      public void addInstanceToRefSet(      LiveRef ref){
        assert Thread.holdsLock(EndpointEntry.this);
        assert ref.equals(this.ref);
        refSet.add(new PhantomLiveRef(ref));
      }
      /** 
 * Remove a PhantomLiveRef from the set of registered instances.
 * This method must ONLY be invoked while synchronized on this
 * RefEntry's EndpointEntry.
 */
      public void removeInstanceFromRefSet(      PhantomLiveRef phantom){
        assert Thread.holdsLock(EndpointEntry.this);
        assert refSet.contains(phantom);
        refSet.remove(phantom);
      }
      /** 
 * Return true if there are no registered LiveRef instances for
 * this entry still reachable in this VM.
 * This method must ONLY be invoked while synchronized on this
 * RefEntry's EndpointEntry.
 */
      public boolean isRefSetEmpty(){
        assert Thread.holdsLock(EndpointEntry.this);
        return refSet.size() == 0;
      }
      /** 
 * Record that a dirty call that explicitly contained this
 * entry's ref has failed.
 * This method must ONLY be invoked while synchronized on this
 * RefEntry's EndpointEntry.
 */
      public void markDirtyFailed(){
        assert Thread.holdsLock(EndpointEntry.this);
        dirtyFailed=true;
      }
      /** 
 * Return true if a dirty call that explicitly contained this
 * entry's ref has failed (and therefore a clean call for this
 * ref needs to be marked "strong").
 * This method must ONLY be invoked while synchronized on this
 * RefEntry's EndpointEntry.
 */
      public boolean hasDirtyFailed(){
        assert Thread.holdsLock(EndpointEntry.this);
        return dirtyFailed;
      }
      /** 
 * PhantomLiveRef is a PhantomReference to a LiveRef instance,
 * used to detect when the LiveRef becomes permanently
 * unreachable in this VM.
 */
private class PhantomLiveRef extends PhantomReference {
        public PhantomLiveRef(        LiveRef ref){
          super(ref,EndpointEntry.this.refQueue);
        }
        public RefEntry getRefEntry(){
          return RefEntry.this;
        }
      }
    }
  }
}
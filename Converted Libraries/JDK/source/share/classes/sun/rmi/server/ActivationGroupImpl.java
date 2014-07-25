package sun.rmi.server;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.rmi.registry.RegistryImpl;
/** 
 * The default activation group implementation.
 * @author      Ann Wollrath
 * @since       1.2
 * @see java.rmi.activation.ActivationGroup
 */
public class ActivationGroupImpl extends ActivationGroup {
  private static final long serialVersionUID=5758693559430427303L;
  /** 
 * maps persistent IDs to activated remote objects 
 */
  private final Hashtable<ActivationID,ActiveEntry> active=new Hashtable<ActivationID,ActiveEntry>();
  private boolean groupInactive=false;
  private final ActivationGroupID groupID;
  private final List<ActivationID> lockedIDs=new ArrayList<ActivationID>();
  /** 
 * Creates a default activation group implementation.
 * @param id the group's identifier
 * @param data ignored
 */
  public ActivationGroupImpl(  ActivationGroupID id,  MarshalledObject<?> data) throws RemoteException {
    super(id);
    groupID=id;
    unexportObject(this,true);
    RMIServerSocketFactory ssf=new ServerSocketFactoryImpl();
    UnicastRemoteObject.exportObject(this,0,null,ssf);
    if (System.getSecurityManager() == null) {
      try {
        System.setSecurityManager(new SecurityManager());
      }
 catch (      Exception e) {
        throw new RemoteException("unable to set security manager",e);
      }
    }
  }
  /** 
 * Trivial server socket factory used to export the activation group
 * impl on an unshared port.
 */
private static class ServerSocketFactoryImpl implements RMIServerSocketFactory {
    public ServerSocket createServerSocket(    int port) throws IOException {
      RMISocketFactory sf=RMISocketFactory.getSocketFactory();
      if (sf == null) {
        sf=RMISocketFactory.getDefaultSocketFactory();
      }
      return sf.createServerSocket(port);
    }
  }
  private void acquireLock(  ActivationID id){
    ActivationID waitForID;
    for (; ; ) {
synchronized (lockedIDs) {
        int index=lockedIDs.indexOf(id);
        if (index < 0) {
          lockedIDs.add(id);
          return;
        }
 else {
          waitForID=lockedIDs.get(index);
        }
      }
synchronized (waitForID) {
synchronized (lockedIDs) {
          int index=lockedIDs.indexOf(waitForID);
          if (index < 0)           continue;
          ActivationID actualID=lockedIDs.get(index);
          if (actualID != waitForID)           continue;
        }
        try {
          waitForID.wait();
        }
 catch (        InterruptedException ignore) {
        }
      }
    }
  }
  private void releaseLock(  ActivationID id){
synchronized (lockedIDs) {
      id=lockedIDs.remove(lockedIDs.indexOf(id));
    }
synchronized (id) {
      id.notifyAll();
    }
  }
  /** 
 * Creates a new instance of an activatable remote object. The
 * <code>Activator</code> calls this method to create an activatable
 * object in this group. This method should be idempotent; a call to
 * activate an already active object should return the previously
 * activated object.
 * Note: this method assumes that the Activator will only invoke
 * newInstance for the same object in a serial fashion (i.e.,
 * the activator will not allow the group to see concurrent requests
 * to activate the same object.
 * @param id the object's activation identifier
 * @param desc the object's activation descriptor
 * @return a marshalled object containing the activated object's stub
 */
  public MarshalledObject<? extends Remote> newInstance(  final ActivationID id,  final ActivationDesc desc) throws ActivationException, RemoteException {
    RegistryImpl.checkAccess("ActivationInstantiator.newInstance");
    if (!groupID.equals(desc.getGroupID()))     throw new ActivationException("newInstance in wrong group");
    try {
      acquireLock(id);
synchronized (this) {
        if (groupInactive == true)         throw new InactiveGroupException("group is inactive");
      }
      ActiveEntry entry=active.get(id);
      if (entry != null)       return entry.mobj;
      String className=desc.getClassName();
      final Class<? extends Remote> cl=RMIClassLoader.loadClass(desc.getLocation(),className).asSubclass(Remote.class);
      Remote impl=null;
      final Thread t=Thread.currentThread();
      final ClassLoader savedCcl=t.getContextClassLoader();
      ClassLoader objcl=cl.getClassLoader();
      final ClassLoader ccl=covers(objcl,savedCcl) ? objcl : savedCcl;
      try {
        impl=AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>(){
          public Remote run() throws InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Constructor<? extends Remote> constructor=cl.getDeclaredConstructor(ActivationID.class,MarshalledObject.class);
            constructor.setAccessible(true);
            try {
              t.setContextClassLoader(ccl);
              return constructor.newInstance(id,desc.getData());
            }
  finally {
              t.setContextClassLoader(savedCcl);
            }
          }
        }
);
      }
 catch (      PrivilegedActionException pae) {
        Throwable e=pae.getException();
        if (e instanceof InstantiationException) {
          throw (InstantiationException)e;
        }
 else         if (e instanceof NoSuchMethodException) {
          throw (NoSuchMethodException)e;
        }
 else         if (e instanceof IllegalAccessException) {
          throw (IllegalAccessException)e;
        }
 else         if (e instanceof InvocationTargetException) {
          throw (InvocationTargetException)e;
        }
 else         if (e instanceof RuntimeException) {
          throw (RuntimeException)e;
        }
 else         if (e instanceof Error) {
          throw (Error)e;
        }
      }
      entry=new ActiveEntry(impl);
      active.put(id,entry);
      return entry.mobj;
    }
 catch (    NoSuchMethodException e) {
      throw new ActivationException("Activatable object must provide an activation" + " constructor",e);
    }
catch (    NoSuchMethodError e) {
      throw new ActivationException("Activatable object must provide an activation" + " constructor",e);
    }
catch (    InvocationTargetException e) {
      throw new ActivationException("exception in object constructor",e.getTargetException());
    }
catch (    Exception e) {
      throw new ActivationException("unable to activate object",e);
    }
 finally {
      releaseLock(id);
      checkInactiveGroup();
    }
  }
  /** 
 * The group's <code>inactiveObject</code> method is called
 * indirectly via a call to the <code>Activatable.inactive</code>
 * method. A remote object implementation must call
 * <code>Activatable</code>'s <code>inactive</code> method when
 * that object deactivates (the object deems that it is no longer
 * active). If the object does not call
 * <code>Activatable.inactive</code> when it deactivates, the
 * object will never be garbage collected since the group keeps
 * strong references to the objects it creates. <p>
 * The group's <code>inactiveObject</code> method
 * unexports the remote object from the RMI runtime so that the
 * object can no longer receive incoming RMI calls. This call will
 * only succeed if the object has no pending/executing calls. If
 * the object does have pending/executing RMI calls, then false
 * will be returned.
 * If the object has no pending/executing calls, the object is
 * removed from the RMI runtime and the group informs its
 * <code>ActivationMonitor</code> (via the monitor's
 * <code>inactiveObject</code> method) that the remote object is
 * not currently active so that the remote object will be
 * re-activated by the activator upon a subsequent activation
 * request.
 * @param id the object's activation identifier
 * @returns true if the operation succeeds (the operation will
 * succeed if the object in currently known to be active and is
 * either already unexported or is currently exported and has no
 * pending/executing calls); false is returned if the object has
 * pending/executing calls in which case it cannot be deactivated
 * @exception UnknownObjectException if object is unknown (may already
 * be inactive)
 * @exception RemoteException if call informing monitor fails
 */
  public boolean inactiveObject(  ActivationID id) throws ActivationException, UnknownObjectException, RemoteException {
    try {
      acquireLock(id);
synchronized (this) {
        if (groupInactive == true)         throw new ActivationException("group is inactive");
      }
      ActiveEntry entry=active.get(id);
      if (entry == null) {
        throw new UnknownObjectException("object not active");
      }
      try {
        if (Activatable.unexportObject(entry.impl,false) == false)         return false;
      }
 catch (      NoSuchObjectException allowUnexportedObjects) {
      }
      try {
        super.inactiveObject(id);
      }
 catch (      UnknownObjectException allowUnregisteredObjects) {
      }
      active.remove(id);
    }
  finally {
      releaseLock(id);
      checkInactiveGroup();
    }
    return true;
  }
  private void checkInactiveGroup(){
    boolean groupMarkedInactive=false;
synchronized (this) {
      if (active.size() == 0 && lockedIDs.size() == 0 && groupInactive == false) {
        groupInactive=true;
        groupMarkedInactive=true;
      }
    }
    if (groupMarkedInactive) {
      try {
        super.inactiveGroup();
      }
 catch (      Exception ignoreDeactivateFailure) {
      }
      try {
        UnicastRemoteObject.unexportObject(this,true);
      }
 catch (      NoSuchObjectException allowUnexportedGroup) {
      }
    }
  }
  /** 
 * The group's <code>activeObject</code> method is called when an
 * object is exported (either by <code>Activatable</code> object
 * construction or an explicit call to
 * <code>Activatable.exportObject</code>. The group must inform its
 * <code>ActivationMonitor</code> that the object is active (via
 * the monitor's <code>activeObject</code> method) if the group
 * hasn't already done so.
 * @param id the object's identifier
 * @param obj the remote object implementation
 * @exception UnknownObjectException if object is not registered
 * @exception RemoteException if call informing monitor fails
 */
  public void activeObject(  ActivationID id,  Remote impl) throws ActivationException, UnknownObjectException, RemoteException {
    try {
      acquireLock(id);
synchronized (this) {
        if (groupInactive == true)         throw new ActivationException("group is inactive");
      }
      if (!active.contains(id)) {
        ActiveEntry entry=new ActiveEntry(impl);
        active.put(id,entry);
        try {
          super.activeObject(id,entry.mobj);
        }
 catch (        RemoteException e) {
        }
      }
    }
  finally {
      releaseLock(id);
      checkInactiveGroup();
    }
  }
  /** 
 * Entry in table for active object.
 */
private static class ActiveEntry {
    Remote impl;
    MarshalledObject<Remote> mobj;
    ActiveEntry(    Remote impl) throws ActivationException {
      this.impl=impl;
      try {
        this.mobj=new MarshalledObject<Remote>(impl);
      }
 catch (      IOException e) {
        throw new ActivationException("failed to marshal remote object",e);
      }
    }
  }
  /** 
 * Returns true if the first argument is either equal to, or is a
 * descendant of, the second argument.  Null is treated as the root of
 * the tree.
 */
  private static boolean covers(  ClassLoader sub,  ClassLoader sup){
    if (sup == null) {
      return true;
    }
 else     if (sub == null) {
      return false;
    }
    do {
      if (sub == sup) {
        return true;
      }
      sub=sub.getParent();
    }
 while (sub != null);
    return false;
  }
}

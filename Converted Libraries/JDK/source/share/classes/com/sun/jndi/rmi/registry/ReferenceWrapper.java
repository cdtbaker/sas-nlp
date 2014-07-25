package com.sun.jndi.rmi.registry;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.*;
/** 
 * The ReferenceWrapper class is a Remote wrapper for Reference
 * objects.  It wraps around a Reference on the server, and makes the
 * Reference accessible to clients.
 * @author Scott Seligman
 */
public class ReferenceWrapper extends UnicastRemoteObject implements RemoteReference {
  protected Reference wrappee;
  public ReferenceWrapper(  Reference wrappee) throws NamingException, RemoteException {
    this.wrappee=wrappee;
  }
  public Reference getReference() throws RemoteException {
    return wrappee;
  }
  private static final long serialVersionUID=6078186197417641456L;
}

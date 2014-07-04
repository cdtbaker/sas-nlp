package com.sun.jndi.rmi.registry;
import java.rmi.*;
import javax.naming.*;
/** 
 * The RemoteReference interface wraps a Reference in a Remote wrapper.
 * @author Scott Seligman
 */
public interface RemoteReference extends Remote {
  Reference getReference() throws NamingException, RemoteException ;
}

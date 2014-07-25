package com.sun.jndi.cosnaming;
import javax.naming.*;
import javax.naming.spi.StateFactory;
import java.util.Hashtable;
import org.omg.CORBA.ORB;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import com.sun.jndi.toolkit.corba.CorbaUtils;
/** 
 * StateFactory that turns java.rmi.Remote objects to org.omg.CORBA.Object.
 * @author Rosanna Lee
 */
public class RemoteToCorba implements StateFactory {
  public RemoteToCorba(){
  }
  /** 
 * Returns the CORBA object for a Remote object.
 * If input is not a Remote object, or if Remote object uses JRMP, return null.
 * If the RMI-IIOP library is not available, throw ConfigurationException.
 * @param orig The object to turn into a CORBA object. If not Remote,
 * or if is a JRMP stub or impl, return null.
 * @param name Ignored
 * @param ctx The non-null CNCtx whose ORB to use.
 * @param env Ignored
 * @return The CORBA object for <tt>orig</tt> or null.
 * @exception ConfigurationException If the CORBA object cannot be obtained
 * due to configuration problems, for instance, if RMI-IIOP not available.
 * @exception NamingException If some other problem prevented a CORBA
 * object from being obtained from the Remote object.
 */
  public Object getStateToBind(  Object orig,  Name name,  Context ctx,  Hashtable<?,?> env) throws NamingException {
    if (orig instanceof org.omg.CORBA.Object) {
      return null;
    }
    if (orig instanceof Remote) {
      try {
        return CorbaUtils.remoteToCorba((Remote)orig,((CNCtx)ctx)._orb);
      }
 catch (      ClassNotFoundException e) {
        throw new ConfigurationException("javax.rmi packages not available");
      }
    }
    return null;
  }
}

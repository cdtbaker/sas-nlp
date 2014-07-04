package com.sun.jndi.url.corbaname;
import com.sun.jndi.url.iiop.iiopURLContextFactory;
/** 
 * A corbaname URL context factory.
 * It just uses the iiop URL context factory but is needed here
 * so that NamingManager.getURLContext() will find it.
 * @author Rosanna Lee
 */
final public class corbanameURLContextFactory extends iiopURLContextFactory {
}

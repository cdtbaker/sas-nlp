package com.sun.org.apache.xml.internal.security.keys.storage;
import java.util.Iterator;
/** 
 * @author $Author: mullan $
 */
public abstract class StorageResolverSpi {
  /** 
 * Method getIterator
 * @return the iterator for the storage
 */
  public abstract Iterator getIterator();
}

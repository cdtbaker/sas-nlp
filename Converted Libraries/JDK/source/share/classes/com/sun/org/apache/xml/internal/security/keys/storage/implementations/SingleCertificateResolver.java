package com.sun.org.apache.xml.internal.security.keys.storage.implementations;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
/** 
 * This {@link StorageResolverSpi} makes a single {@link X509Certificate}available to the {@link com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver}.
 * @author $Author: mullan $
 */
public class SingleCertificateResolver extends StorageResolverSpi {
  /** 
 * Field _certificate 
 */
  X509Certificate _certificate=null;
  /** 
 * Field _iterator 
 */
  Iterator _iterator=null;
  /** 
 * @param x509cert the single {@link X509Certificate}
 */
  public SingleCertificateResolver(  X509Certificate x509cert){
    this._certificate=x509cert;
    this._iterator=new InternalIterator(this._certificate);
  }
  /** 
 * @inheritDoc 
 */
  public Iterator getIterator(){
    return this._iterator;
  }
  /** 
 * Class InternalIterator
 * @author $Author: mullan $
 * @version $Revision: 1.5 $
 */
static class InternalIterator implements Iterator {
    /** 
 * Field _alreadyReturned 
 */
    boolean _alreadyReturned=false;
    /** 
 * Field _certificate 
 */
    X509Certificate _certificate=null;
    /** 
 * Constructor InternalIterator
 * @param x509cert
 */
    public InternalIterator(    X509Certificate x509cert){
      this._certificate=x509cert;
    }
    /** 
 * @inheritDoc 
 */
    public boolean hasNext(){
      return (!this._alreadyReturned);
    }
    /** 
 * @inheritDoc 
 */
    public Object next(){
      this._alreadyReturned=true;
      return this._certificate;
    }
    /** 
 * Method remove
 */
    public void remove(){
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
  }
}

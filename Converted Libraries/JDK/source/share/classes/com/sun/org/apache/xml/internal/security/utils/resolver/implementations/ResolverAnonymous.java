package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
/** 
 * @author $Author: mullan $
 */
public class ResolverAnonymous extends ResourceResolverSpi {
  private XMLSignatureInput _input=null;
  /** 
 * @param filename
 * @throws FileNotFoundException
 * @throws IOException
 */
  public ResolverAnonymous(  String filename) throws FileNotFoundException, IOException {
    this._input=new XMLSignatureInput(new FileInputStream(filename));
  }
  /** 
 * @param is
 */
  public ResolverAnonymous(  InputStream is){
    this._input=new XMLSignatureInput(is);
  }
  /** 
 * @inheritDoc 
 */
  public XMLSignatureInput engineResolve(  Attr uri,  String BaseURI){
    return this._input;
  }
  /** 
 * @inheritDoc
 */
  public boolean engineCanResolve(  Attr uri,  String BaseURI){
    if (uri == null) {
      return true;
    }
    return false;
  }
  /** 
 * @inheritDoc 
 */
  public String[] engineGetPropertyKeys(){
    return new String[0];
  }
}

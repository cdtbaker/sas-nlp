package com.sun.org.apache.xml.internal.security.transforms.implementations;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
/** 
 * Class TransformC14NExclusive
 * @author $Author: mullan $
 * @version $Revision: 1.5 $
 */
public class TransformC14NExclusive extends TransformSpi {
  /** 
 * Field implementedTransformURI 
 */
  public static final String implementedTransformURI=Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS;
  /** 
 * Method engineGetURI
 * @inheritDoc
 */
  protected String engineGetURI(){
    return implementedTransformURI;
  }
  /** 
 * Method enginePerformTransform
 * @param input
 * @return the transformed of the input
 * @throws CanonicalizationException
 */
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input,  Transform _transformObject) throws CanonicalizationException {
    return enginePerformTransform(input,null,_transformObject);
  }
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input,  OutputStream os,  Transform _transformObject) throws CanonicalizationException {
    try {
      String inclusiveNamespaces=null;
      if (_transformObject.length(InclusiveNamespaces.ExclusiveCanonicalizationNamespace,InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES) == 1) {
        Element inclusiveElement=XMLUtils.selectNode(_transformObject.getElement().getFirstChild(),InclusiveNamespaces.ExclusiveCanonicalizationNamespace,InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES,0);
        inclusiveNamespaces=new InclusiveNamespaces(inclusiveElement,_transformObject.getBaseURI()).getInclusiveNamespaces();
      }
      Canonicalizer20010315ExclOmitComments c14n=new Canonicalizer20010315ExclOmitComments();
      if (os != null) {
        c14n.setWriter(os);
      }
      byte[] result;
      result=c14n.engineCanonicalize(input,inclusiveNamespaces);
      XMLSignatureInput output=new XMLSignatureInput(result);
      if (os != null) {
        output.setOutputStream(os);
      }
      return output;
    }
 catch (    XMLSecurityException ex) {
      throw new CanonicalizationException("empty",ex);
    }
  }
}

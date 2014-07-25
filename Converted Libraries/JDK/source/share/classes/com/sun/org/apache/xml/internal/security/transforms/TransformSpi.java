package com.sun.org.apache.xml.internal.security.transforms;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.xml.sax.SAXException;
/** 
 * Base class which all Transform algorithms extend. The common methods that
 * have to be overridden are the{@link #enginePerformTransform(XMLSignatureInput,Transform)} method.
 * @author Christian Geuer-Pollmann
 */
public abstract class TransformSpi {
  /** 
 * For API compatibility not thread safe.
 * @deprecated
 */
  protected Transform _transformObject=null;
  /** 
 * Set the transform object.
 * Depeprecated For API compatibility.
 * @param transform the Transform
 * @deprecated
 */
  protected void setTransform(  Transform transform){
    this._transformObject=transform;
  }
  /** 
 * The mega method which MUST be implemented by the Transformation Algorithm.
 * @param input {@link XMLSignatureInput} as the input of transformation
 * @param os where to output this transformation.
 * @param _transformObject the Transform
 * @return {@link XMLSignatureInput} as the result of transformation
 * @throws CanonicalizationException
 * @throws IOException
 * @throws InvalidCanonicalizerException
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws TransformationException
 */
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input,  OutputStream os,  Transform _transformObject) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
    return enginePerformTransform(input,_transformObject);
  }
  /** 
 * The mega method which MUST be implemented by the Transformation Algorithm.
 * In order to be compatible with preexisting Transform implementations,
 * by default this implementation invokes the deprecated, thread-unsafe
 * methods. Subclasses should override this with a thread-safe
 * implementation.
 * @param input {@link XMLSignatureInput} as the input of transformation
 * @param _transformObject the Transform
 * @return {@link XMLSignatureInput} as the result of transformation
 * @throws CanonicalizationException
 * @throws IOException
 * @throws InvalidCanonicalizerException
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws TransformationException
 */
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input,  Transform _transformObject) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
    try {
      TransformSpi tmp=(TransformSpi)getClass().newInstance();
      tmp.setTransform(_transformObject);
      return tmp.enginePerformTransform(input);
    }
 catch (    InstantiationException e) {
      throw new TransformationException("",e);
    }
catch (    IllegalAccessException e) {
      throw new TransformationException("",e);
    }
  }
  /** 
 * The mega method which MUST be implemented by the Transformation Algorithm.
 * @deprecated
 * @param input {@link XMLSignatureInput} as the input of transformation
 * @return {@link XMLSignatureInput} as the result of transformation
 * @throws CanonicalizationException
 * @throws IOException
 * @throws InvalidCanonicalizerException
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws TransformationException
 */
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
    throw new UnsupportedOperationException();
  }
  /** 
 * Returns the URI representation of <code>Transformation algorithm</code>
 * @return the URI representation of <code>Transformation algorithm</code>
 */
  protected abstract String engineGetURI();
}

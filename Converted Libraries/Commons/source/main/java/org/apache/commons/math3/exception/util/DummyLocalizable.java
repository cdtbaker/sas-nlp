package org.apache.commons.math3.exception.util;
import java.util.Locale;
/** 
 * Dummy implementation of the {@link Localizable} interface, without localization.
 * @version $Id: DummyLocalizable.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.2
 */
public class DummyLocalizable implements Localizable {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=8843275624471387299L;
  /** 
 * Source string. 
 */
  private final String source;
  /** 
 * Simple constructor.
 * @param source source text
 */
  public DummyLocalizable(  final String source){
    this.source=source;
  }
  /** 
 * {@inheritDoc} 
 */
  public String getSourceString(){
    return source;
  }
  /** 
 * {@inheritDoc} 
 */
  public String getLocalizedString(  Locale locale){
    return source;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public String toString(){
    return source;
  }
}

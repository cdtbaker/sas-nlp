package org.apache.commons.math3.util;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * @version $Id: TestBean.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class TestBean {
  private Double x=Double.valueOf(1.0);
  private String y="1.0";
  /** 
 */
  public Double getX(){
    return x;
  }
  /** 
 */
  public String getY(){
    return y;
  }
  /** 
 */
  public void setX(  Double double1){
    x=double1;
  }
  /** 
 */
  public void setY(  String string){
    y=string;
  }
  /** 
 */
  public Double getZ(){
    throw new MathUnsupportedOperationException(LocalizedFormats.SIMPLE_MESSAGE,"?");
  }
  /** 
 */
  public void setZ(  Double double1){
  }
}

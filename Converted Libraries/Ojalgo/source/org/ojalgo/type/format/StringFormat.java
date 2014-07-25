package org.ojalgo.type.format;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
public final class StringFormat extends Format {
  public StringFormat(){
    super();
  }
  @Override public StringBuffer format(  final Object anObject,  final StringBuffer aBuffer,  final FieldPosition aPosition){
    return aBuffer.append(anObject);
  }
  @Override public String parseObject(  final String aSource,  final ParsePosition aPosition){
    return aSource;
  }
}

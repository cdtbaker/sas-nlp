package org.ojalgo.type.format;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
public class BinaryFormat extends Format {
  public BinaryFormat(){
    super();
  }
  @Override public StringBuffer format(  final Object someObj,  final StringBuffer aBufferToAppendTo,  final FieldPosition aPosition){
    return aBufferToAppendTo.append(new String((byte[])someObj));
  }
  @Override public byte[] parseObject(  final String someSource,  final ParsePosition somePos){
    return someSource.getBytes();
  }
}

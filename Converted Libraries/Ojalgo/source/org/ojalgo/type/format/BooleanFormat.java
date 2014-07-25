package org.ojalgo.type.format;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import org.ojalgo.type.context.BooleanContext;
/** 
 * BooleanFormat doesn't do anything useful, but it was needed for{@linkplain BooleanContext} (that doesn't do anything either).
 * @author apete
 */
public final class BooleanFormat extends Format {
  public BooleanFormat(){
    super();
  }
  @Override public StringBuffer format(  final Object anObject,  final StringBuffer aBuffer,  final FieldPosition aPosition){
    if ((anObject instanceof Boolean) && ((Boolean)anObject).booleanValue()) {
      aBuffer.append(true);
    }
 else {
      aBuffer.append(false);
    }
    return aBuffer;
  }
  @Override public Boolean parseObject(  final String aSource,  final ParsePosition aPosition){
    return Boolean.valueOf(aSource);
  }
}

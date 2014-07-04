package org.ojalgo.type.format;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.ojalgo.function.UnaryFunction;
public class TransformationFormat<N extends Number> extends NumberFormat {
  private final UnaryFunction<N> myTransfoFunc;
  private final UnaryFunction<N> myInverseFunc;
  private final NumberFormat myFormat;
  public TransformationFormat(  final UnaryFunction<N> transformer,  final NumberFormat format,  final UnaryFunction<N> inverse){
    super();
    myTransfoFunc=transformer;
    myFormat=format;
    myInverseFunc=inverse;
  }
  public StringBuffer format(  final double number,  final StringBuffer toAppendTo,  final FieldPosition pos){
    return myFormat.format(myTransfoFunc.invoke(number),toAppendTo,pos);
  }
  public StringBuffer format(  final long number,  final StringBuffer toAppendTo,  final FieldPosition pos){
    return myFormat.format(myTransfoFunc.invoke(number),toAppendTo,pos);
  }
  @SuppressWarnings("unchecked") @Override public StringBuffer format(  final Object obj,  final StringBuffer toAppendTo,  final FieldPosition pos){
    return myFormat.format(myTransfoFunc.invoke((N)obj),toAppendTo,pos);
  }
  public Number parse(  final String source,  final ParsePosition parsePosition){
    return myInverseFunc.invoke((N)myFormat.parseObject(source,parsePosition));
  }
}

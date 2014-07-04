package org.ojalgo.scalar;
import org.ojalgo.type.context.NumberContext;
abstract class AbstractScalar<N extends Number> extends Number implements Scalar<N> {
  AbstractScalar(){
    super();
  }
  public final String toPlainString(  final NumberContext context){
    return context.enforce(this.toBigDecimal()).toPlainString();
  }
}

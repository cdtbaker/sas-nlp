package org.ojalgo.finance.portfolio;
import java.math.BigDecimal;
import org.ojalgo.type.TypeUtils;
final class LowerUpper {
  final BigDecimal lower;
  final BigDecimal upper;
  LowerUpper(  final Number someLower,  final Number someUpper){
    super();
    lower=TypeUtils.toBigDecimal(someLower);
    upper=TypeUtils.toBigDecimal(someUpper);
  }
}

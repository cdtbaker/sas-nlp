package org.ojalgo.type.format;
import java.text.NumberFormat;
import java.util.Locale;
public enum NumberStyle {CURRENCY, GENERAL, INTEGER, PERCENT, SCIENTIFIC; public NumberFormat getFormat(){
  return this.getFormat(Locale.getDefault());
}
public NumberFormat getFormat(final Locale locale){
switch (this) {
case CURRENCY:
    return NumberFormat.getCurrencyInstance(locale != null ? locale : Locale.getDefault());
case INTEGER:
  return NumberFormat.getIntegerInstance(locale != null ? locale : Locale.getDefault());
case PERCENT:
return NumberFormat.getPercentInstance(locale != null ? locale : Locale.getDefault());
default :
return NumberFormat.getInstance(locale != null ? locale : Locale.getDefault());
}
}
}

package org.ojalgo.type.format;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
public enum DatePart {DATE, DATETIME, TIME; public Format getFormat(){
  return this.getFormat(DateStyle.SHORT,Locale.getDefault());
}
public Format getFormat(final DateStyle style){
  return this.getFormat(style,Locale.getDefault());
}
public Format getFormat(final DateStyle style,final Locale locale){
  final DateStyle tmpStyle=style != null ? style : DateStyle.SHORT;
  final Locale tmpLocale=locale != null ? locale : Locale.getDefault();
switch (tmpStyle) {
case SQL:
switch (this) {
case DATE:
      return new SimpleDateFormat("yyyy-MM-dd");
case TIME:
    return new SimpleDateFormat("HH:mm:ss");
default :
  return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
default :
switch (this) {
case DATE:
return DateFormat.getDateInstance(tmpStyle.intValue(),tmpLocale);
case TIME:
return DateFormat.getTimeInstance(tmpStyle.intValue(),tmpLocale);
default :
return DateFormat.getDateTimeInstance(tmpStyle.intValue(),tmpStyle.intValue(),tmpLocale);
}
}
}
public Format getFormat(final Locale locale){
return this.getFormat(DateStyle.SHORT,locale);
}
}

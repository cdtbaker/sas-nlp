package org.ojalgo.type.format;
import java.text.DateFormat;
import java.text.Format;
import java.util.Locale;
public enum DateStyle {FULL(DateFormat.FULL), LONG(DateFormat.LONG), MEDIUM(DateFormat.MEDIUM), SHORT(DateFormat.SHORT), SQL(Integer.MAX_VALUE); private int myIntValue;
DateStyle(final int aStyleValue){
  myIntValue=aStyleValue;
}
public Format getFormat(){
  return DatePart.DATETIME.getFormat(this,Locale.getDefault());
}
public Format getFormat(final DatePart aPart){
  return aPart.getFormat(this,Locale.getDefault());
}
public Format getFormat(final DatePart aPart,final Locale aLocale){
  return aPart.getFormat(this,aLocale);
}
public Format getFormat(final Locale aLocale){
  return DatePart.DATETIME.getFormat(this,aLocale);
}
/** 
 * @return {@linkplain DateFormat#FULL}, {@linkplain DateFormat#LONG}, {@linkplain DateFormat#MEDIUM} or {@linkplain DateFormat#SHORT}
 */
public int intValue(){
  return myIntValue;
}
}

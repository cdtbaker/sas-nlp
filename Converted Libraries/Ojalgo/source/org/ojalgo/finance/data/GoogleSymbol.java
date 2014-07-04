package org.ojalgo.finance.data;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.ojalgo.netio.ASCII;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;
public class GoogleSymbol extends DataSource<GoogleSymbol.Data> {
public static final class Data extends DatePrice {
    public double close;
    public double high;
    public double low;
    public double open;
    public double volume;
    protected Data(    final Calendar aDate){
      super(aDate);
    }
    @Override public double getPrice(){
      return close;
    }
  }
  private static final String CSV="csv";
  private static final String DAILY="daily";
  private static final GenericContext<Date> DATE_FORMAT=new GenericContext<Date>(new SimpleDateFormat("dd-MMM-yy",Locale.US));
  private static final String FINANCE_GOOGLE_COM="finance.google.com";
  private static final String FINANCE_HISTORICAL="/finance/historical";
  private static final String HISTPERIOD="histperiod";
  private static final String JAN_2_1970="Jan+2,+1970";
  private static final String OUTPUT="output";
  private static final String Q="q";
  private static final String STARTDATE="startdate";
  private static final String WEEKLY="weekly";
  public GoogleSymbol(  final String aSymbol){
    this(aSymbol,CalendarDateUnit.DAY);
  }
  public GoogleSymbol(  final String aSymbol,  final CalendarDateUnit aResolution){
    super(aSymbol,aResolution);
    this.setHost(FINANCE_GOOGLE_COM);
    this.setPath(FINANCE_HISTORICAL);
    this.addQueryParameter(Q,aSymbol);
    this.addQueryParameter(STARTDATE,JAN_2_1970);
switch (aResolution) {
case WEEK:
      this.addQueryParameter(HISTPERIOD,WEEKLY);
    break;
default :
  this.addQueryParameter(HISTPERIOD,DAILY);
break;
}
this.addQueryParameter(OUTPUT,CSV);
}
@Override protected GoogleSymbol.Data parse(final String aLine){
Data retVal=null;
int tmpInclusiveBegin=0;
int tmpExclusiveEnd=aLine.indexOf(ASCII.COMMA,tmpInclusiveBegin);
String tmpString=aLine.substring(tmpInclusiveBegin,tmpExclusiveEnd);
final Calendar tmpCalendar=new GregorianCalendar();
tmpCalendar.setTime(DATE_FORMAT.parse(tmpString));
this.getResolution().round(tmpCalendar);
retVal=new Data(tmpCalendar);
tmpInclusiveBegin=tmpExclusiveEnd + 1;
tmpExclusiveEnd=aLine.indexOf(ASCII.COMMA,tmpInclusiveBegin);
tmpString=aLine.substring(tmpInclusiveBegin,tmpExclusiveEnd);
try {
retVal.open=Double.parseDouble(tmpString);
}
 catch (final NumberFormatException ex) {
retVal.open=Double.NaN;
}
tmpInclusiveBegin=tmpExclusiveEnd + 1;
tmpExclusiveEnd=aLine.indexOf(ASCII.COMMA,tmpInclusiveBegin);
tmpString=aLine.substring(tmpInclusiveBegin,tmpExclusiveEnd);
try {
retVal.high=Double.parseDouble(tmpString);
}
 catch (final NumberFormatException ex) {
retVal.high=Double.NaN;
}
tmpInclusiveBegin=tmpExclusiveEnd + 1;
tmpExclusiveEnd=aLine.indexOf(ASCII.COMMA,tmpInclusiveBegin);
tmpString=aLine.substring(tmpInclusiveBegin,tmpExclusiveEnd);
try {
retVal.low=Double.parseDouble(tmpString);
}
 catch (final NumberFormatException ex) {
retVal.low=Double.NaN;
}
tmpInclusiveBegin=tmpExclusiveEnd + 1;
tmpExclusiveEnd=aLine.indexOf(ASCII.COMMA,tmpInclusiveBegin);
tmpString=aLine.substring(tmpInclusiveBegin,tmpExclusiveEnd);
try {
retVal.close=Double.parseDouble(tmpString);
}
 catch (final NumberFormatException ex) {
retVal.close=Double.NaN;
}
tmpInclusiveBegin=tmpExclusiveEnd + 1;
tmpString=aLine.substring(tmpInclusiveBegin);
try {
retVal.volume=Double.parseDouble(tmpString);
}
 catch (final NumberFormatException ex) {
retVal.volume=Double.NaN;
}
return retVal;
}
}

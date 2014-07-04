package org.ojalgo.finance.data;
import java.util.Calendar;
import java.util.Date;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.keyvalue.KeyValue;
public abstract class DatePrice implements KeyValue<CalendarDate,Double> {
  public final CalendarDate key;
  protected DatePrice(  final Calendar aDate){
    super();
    key=new CalendarDate(aDate);
  }
  protected DatePrice(  final Date aDate){
    super();
    key=new CalendarDate(aDate);
  }
  protected DatePrice(  final long aDate){
    super();
    key=new CalendarDate(aDate);
  }
  public int compareTo(  final KeyValue<CalendarDate,?> ref){
    return key.compareTo(ref.getKey());
  }
  public final CalendarDate getKey(){
    return key;
  }
  public abstract double getPrice();
  public final Double getValue(){
    return this.getPrice();
  }
  @Override public final String toString(){
    return key + ": " + this.getPrice();
  }
}

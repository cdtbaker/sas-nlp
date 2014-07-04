package org.ojalgo.type;
import org.ojalgo.constant.PrimitiveMath;
public final class CalendarDateDuration extends Number implements Comparable<CalendarDateDuration> {
  public final double measure;
  public final CalendarDateUnit unit;
  public CalendarDateDuration(  final double aMeasure,  final CalendarDateUnit aUnit){
    super();
    measure=aMeasure;
    unit=aUnit;
  }
  CalendarDateDuration(){
    this(PrimitiveMath.ONE,CalendarDateUnit.MILLIS);
  }
  public int compareTo(  final CalendarDateDuration aReference){
    final long tmpVal=this.toDurationInMillis();
    final long refVal=aReference.toDurationInMillis();
    return (tmpVal < refVal ? -1 : (tmpVal == refVal ? 0 : 1));
  }
  public CalendarDateDuration convertTo(  final CalendarDateUnit aDestinationUnit){
    return new CalendarDateDuration(aDestinationUnit.convert(measure,unit),aDestinationUnit);
  }
  @Override public double doubleValue(){
    return measure;
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CalendarDateDuration)) {
      return false;
    }
    final CalendarDateDuration other=(CalendarDateDuration)obj;
    if (Double.doubleToLongBits(measure) != Double.doubleToLongBits(other.measure)) {
      return false;
    }
    if (unit != other.unit) {
      return false;
    }
    return true;
  }
  @Override public float floatValue(){
    return (float)measure;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    long temp;
    temp=Double.doubleToLongBits(measure);
    result=(prime * result) + (int)(temp ^ (temp >>> 32));
    result=(prime * result) + ((unit == null) ? 0 : unit.hashCode());
    return result;
  }
  @Override public int intValue(){
    return (int)measure;
  }
  @Override public long longValue(){
    return (long)measure;
  }
  public long toDurationInMillis(){
    return (long)(measure * unit.size());
  }
  @Override public String toString(){
    return Double.toString(measure) + unit.toString();
  }
}

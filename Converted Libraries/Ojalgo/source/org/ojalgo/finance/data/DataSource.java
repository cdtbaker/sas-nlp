package org.ojalgo.finance.data;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.Colour;
import org.ojalgo.type.TypeCache;
public abstract class DataSource<DP extends DatePrice> {
  protected static final boolean DEBUG=false;
  private final CalendarDateUnit myResolution;
  private final ResourceLocator myResourceLocator=new ResourceLocator();
  private final String mySymbol;
  @SuppressWarnings("unused") private DataSource(){
    this(null,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected DataSource(  final String aSymbol,  final CalendarDateUnit aResolution){
    super();
    mySymbol=aSymbol;
    myResolution=aResolution;
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DataSource)) {
      return false;
    }
    final DataSource<?> other=(DataSource<?>)obj;
    if (myResolution != other.myResolution) {
      return false;
    }
    if (!this.getClass().equals(other.getClass())) {
      return false;
    }
    if (mySymbol == null) {
      if (other.mySymbol != null) {
        return false;
      }
    }
 else     if (!mySymbol.equals(other.mySymbol)) {
      return false;
    }
    return true;
  }
  public List<DP> getHistoricalPrices(){
    return this.getHistoricalPrices(myResourceLocator.getStreamReader());
  }
  public List<DP> getHistoricalPrices(  final BufferedReader aReader){
    final ArrayList<DP> retVal=new ArrayList<DP>();
    String tmpLine;
    DP tmpHistPrice;
    try {
      tmpLine=aReader.readLine();
      if (DEBUG) {
        BasicLogger.logDebug(tmpLine);
      }
      while ((tmpLine=aReader.readLine()) != null) {
        if (DEBUG) {
          BasicLogger.logDebug(tmpLine);
        }
        tmpHistPrice=this.parse(tmpLine);
        retVal.add(tmpHistPrice);
      }
      aReader.close();
    }
 catch (    final IOException anException) {
      anException.printStackTrace();
    }
    Collections.sort(retVal);
    return retVal;
  }
  public CalendarDateSeries<Double> getPriceSeries(){
    return this.getPriceSeries(myResourceLocator.getStreamReader());
  }
  public CalendarDateSeries<Double> getPriceSeries(  final BufferedReader aReader){
    final CalendarDateSeries<Double> retVal=new CalendarDateSeries<Double>(myResolution).name(mySymbol).colour(Colour.random());
    for (    final DatePrice tmpDatePrice : this.getHistoricalPrices(aReader)) {
      retVal.put(tmpDatePrice.getKey(),tmpDatePrice.getValue());
    }
    return retVal;
  }
  public CalendarDateUnit getResolution(){
    return myResolution;
  }
  public String getSymbol(){
    return mySymbol;
  }
  public TypeCache<? extends List<DP>> getSymbolCache(  final long aPurgeIntervalMeassure,  final CalendarDateUnit aPurgeIntervalUnit){
    return new TypeCache<List<DP>>(aPurgeIntervalMeassure,aPurgeIntervalUnit){
      @Override protected List<DP> recreateCache(){
        return DataSource.this.getHistoricalPrices();
      }
    }
;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + ((myResolution == null) ? 0 : myResolution.hashCode());
    result=(prime * result) + ((mySymbol == null) ? 0 : mySymbol.hashCode());
    return result;
  }
  protected String addQueryParameter(  final String aKey,  final String aValue){
    return myResourceLocator.addQueryParameter(aKey,aValue);
  }
  protected abstract DP parse(  String aLine);
  protected void setHost(  final String aHost){
    myResourceLocator.setHost(aHost);
  }
  protected void setPath(  final String aPath){
    myResourceLocator.setPath(aPath);
  }
}

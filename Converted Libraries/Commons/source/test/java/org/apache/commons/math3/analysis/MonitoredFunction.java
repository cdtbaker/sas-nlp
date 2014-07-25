package org.apache.commons.math3.analysis;
/** 
 * Wrapper class for counting functions calls.
 * @version $Id: MonitoredFunction.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class MonitoredFunction implements UnivariateFunction {
  public MonitoredFunction(  UnivariateFunction f){
    callsCount=0;
    this.f=f;
  }
  public void setCallsCount(  int callsCount){
    this.callsCount=callsCount;
  }
  public int getCallsCount(){
    return callsCount;
  }
  public double value(  double x){
    ++callsCount;
    return f.value(x);
  }
  private int callsCount;
  private UnivariateFunction f;
}

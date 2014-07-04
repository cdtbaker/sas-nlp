package org.apache.commons.math3.stat.descriptive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.DefaultTransformer;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.NumberTransformer;
/** 
 * @version $Id: ListUnivariateImpl.java 1334421 2012-05-05 13:41:04Z tn $
 */
public class ListUnivariateImpl extends DescriptiveStatistics implements Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-8837442489133392138L;
  /** 
 * Holds a reference to a list - GENERICs are going to make
 * our lives easier here as we could only accept List<Number>
 */
  protected List<Object> list;
  /** 
 * Number Transformer maps Objects to Number for us. 
 */
  protected NumberTransformer transformer;
  /** 
 * No argument Constructor
 */
  public ListUnivariateImpl(){
    this(new ArrayList<Object>());
  }
  /** 
 * Construct a ListUnivariate with a specific List.
 * @param list The list that will back this DescriptiveStatistics
 */
  public ListUnivariateImpl(  List<Object> list){
    this(list,new DefaultTransformer());
  }
  /** 
 * Construct a ListUnivariate with a specific List.
 * @param list The list that will back this DescriptiveStatistics
 * @param transformer the number transformer used to convert the list items.
 */
  public ListUnivariateImpl(  List<Object> list,  NumberTransformer transformer){
    super();
    this.list=list;
    this.transformer=transformer;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public double[] getValues(){
    int length=list.size();
    final int wSize=getWindowSize();
    if (wSize != DescriptiveStatistics.INFINITE_WINDOW && wSize < list.size()) {
      length=list.size() - FastMath.max(0,list.size() - wSize);
    }
    double[] copiedArray=new double[length];
    for (int i=0; i < copiedArray.length; i++) {
      copiedArray[i]=getElement(i);
    }
    return copiedArray;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public double getElement(  int index){
    double value=Double.NaN;
    int calcIndex=index;
    final int wSize=getWindowSize();
    if (wSize != DescriptiveStatistics.INFINITE_WINDOW && wSize < list.size()) {
      calcIndex=(list.size() - wSize) + index;
    }
    try {
      value=transformer.transform(list.get(calcIndex));
    }
 catch (    MathIllegalArgumentException e) {
      e.printStackTrace();
    }
    return value;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public long getN(){
    int n=0;
    final int wSize=getWindowSize();
    if (wSize != DescriptiveStatistics.INFINITE_WINDOW) {
      if (list.size() > wSize) {
        n=wSize;
      }
 else {
        n=list.size();
      }
    }
 else {
      n=list.size();
    }
    return n;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void addValue(  double v){
    list.add(Double.valueOf(v));
  }
  /** 
 * Adds an object to this list.
 * @param o Object to add to the list
 */
  public void addObject(  Object o){
    list.add(o);
  }
  /** 
 * Clears all statistics.
 * <p>
 * <strong>N.B.: </strong> This method has the side effect of clearing the underlying list.
 */
  @Override public void clear(){
    list.clear();
  }
  /** 
 * Apply the given statistic to this univariate collection.
 * @param stat the statistic to apply
 * @return the computed value of the statistic.
 */
  @Override public double apply(  UnivariateStatistic stat){
    double[] v=this.getValues();
    if (v != null) {
      return stat.evaluate(v,0,v.length);
    }
    return Double.NaN;
  }
  /** 
 * Access the number transformer.
 * @return the number transformer.
 */
  public NumberTransformer getTransformer(){
    return transformer;
  }
  /** 
 * Modify the number transformer.
 * @param transformer the new number transformer.
 */
  public void setTransformer(  NumberTransformer transformer){
    this.transformer=transformer;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setWindowSize(  int windowSize){
    super.setWindowSize(windowSize);
    int extra=list.size() - windowSize;
    for (int i=0; i < extra; i++) {
      list.remove(0);
    }
  }
}

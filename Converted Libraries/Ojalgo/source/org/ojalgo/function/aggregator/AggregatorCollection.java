package org.ojalgo.function.aggregator;
/** 
 * Do not cache instances of this class! The methods {@linkplain BigAggregator#getCollection()},{@linkplain ComplexAggregator#getCollection()} and {@linkplain PrimitiveAggregator#getCollection()}return threadlocal instances, and when you access the individual
 * aggregators they are {@linkplain AggregatorFunction#reset()} for you.
 * @author apete
 */
public abstract class AggregatorCollection<N extends Number> {
  protected AggregatorCollection(){
    super();
  }
  /** 
 * Count of non-zero elements
 */
  public abstract AggregatorFunction<N> cardinality();
  /** 
 * Largest absolute value
 */
  public abstract AggregatorFunction<N> largest();
  /** 
 * Max value
 */
  public abstract AggregatorFunction<N> maximum();
  /** 
 * Min value
 */
  public abstract AggregatorFunction<N> minimum();
  /** 
 * Equivalent to, but probably faster than, norm(1);
 */
  public abstract AggregatorFunction<N> norm1();
  /** 
 * Equivalent to, but probably faster than, norm(2);
 */
  public abstract AggregatorFunction<N> norm2();
  /** 
 * Running product
 */
  public abstract AggregatorFunction<N> product();
  /** 
 * Running product of squares
 */
  public abstract AggregatorFunction<N> product2();
  /** 
 * Smallest non-zero absolute value
 */
  public abstract AggregatorFunction<N> smallest();
  /** 
 * Running sum
 */
  public abstract AggregatorFunction<N> sum();
  /** 
 * Running sum of squares
 */
  public abstract AggregatorFunction<N> sum2();
}

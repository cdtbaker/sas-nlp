package cern.jet.stat.quantile;
import cern.colt.list.DoubleArrayList;
import cern.jet.math.Arithmetic;
import cern.jet.random.engine.RandomEngine;
import cern.jet.random.sampling.RandomSamplingAssistant;
/** 
 * Approximate quantile finding algorithm for known <tt>N</tt> requiring only one pass and little main memory; computes quantiles over a sequence of <tt>double</tt> elements.
 * <p>Needs as input the following parameters:<p>
 * <dt>1. <tt>N</tt> - the number of values of the data sequence over which quantiles are to be determined.
 * <dt>2. <tt>quantiles</tt> - the number of quantiles to be computed.
 * <dt>3. <tt>epsilon</tt> - the allowed approximation error on quantiles. The approximation guarantee of this algorithm is explicit.
 * <p>It is also possible to couple the approximation algorithm with random sampling to further reduce memory requirements. 
 * With sampling, the approximation guarantees are explicit but probabilistic, i.e. they apply with respect to a (user controlled) confidence parameter "delta".
 * <dt>4. <tt>delta</tt> - the probability allowed that the approximation error fails to be smaller than epsilon. Set <tt>delta</tt> to zero for explicit non probabilistic guarantees.
 * You usually don't instantiate quantile finders by using the constructor. Instead use the factory <tt>QuantileFinderFactor</tt> to do so. It will set up the right parametrization for you.
 * <p>After Gurmeet Singh Manku, Sridhar Rajagopalan and Bruce G. Lindsay, 
 * Approximate Medians and other Quantiles in One Pass and with Limited Memory,
 * Proc. of the 1998 ACM SIGMOD Int. Conf. on Management of Data,
 * Paper available <A HREF="http://www-cad.eecs.berkeley.edu/~manku"> here</A>.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see QuantileFinderFactory
 * @see UnknownApproximateDoubleQuantileFinder
 */
class KnownDoubleQuantileEstimator extends DoubleQuantileEstimator {
  protected double beta;
  protected boolean weHadMoreThanOneEmptyBuffer;
  protected RandomSamplingAssistant samplingAssistant;
  protected double samplingRate;
  protected long N;
  /** 
 * Constructs an approximate quantile finder with b buffers, each having k elements.
 * @param b the number of buffers
 * @param k the number of elements per buffer
 * @param N the total number of elements over which quantiles are to be computed.
 * @param samplingRate 1.0 --> all elements are consumed. 10.0 --> Consumes one random element from successive blocks of 10 elements each. Etc.
 * @param generator a uniform random number generator.
 */
  public KnownDoubleQuantileEstimator(  int b,  int k,  long N,  double samplingRate,  RandomEngine generator){
    this.samplingRate=samplingRate;
    this.N=N;
    if (this.samplingRate <= 1.0) {
      this.samplingAssistant=null;
    }
 else {
      this.samplingAssistant=new RandomSamplingAssistant(Arithmetic.floor(N / samplingRate),N,generator);
    }
    setUp(b,k);
    this.clear();
  }
  /** 
 * @param infinities the number of infinities to fill.
 * @param buffer the buffer into which the infinities shall be filled.
 */
  protected void addInfinities(  int missingInfinities,  DoubleBuffer buffer){
    RandomSamplingAssistant oldAssistant=this.samplingAssistant;
    this.samplingAssistant=null;
    boolean even=true;
    for (int i=0; i < missingInfinities; i++) {
      if (even)       buffer.values.add(Double.MAX_VALUE);
 else       buffer.values.add(-Double.MAX_VALUE);
      even=!even;
    }
    this.samplingAssistant=oldAssistant;
  }
  /** 
 * Not yet commented.
 */
  protected DoubleBuffer[] buffersToCollapse(){
    int minLevel=bufferSet._getMinLevelOfFullOrPartialBuffers();
    return bufferSet._getFullOrPartialBuffersWithLevel(minLevel);
  }
  /** 
 * Removes all elements from the receiver.  The receiver will
 * be empty after this call returns, and its memory requirements will be close to zero.
 */
  public void clear(){
    super.clear();
    this.beta=1.0;
    this.weHadMoreThanOneEmptyBuffer=false;
    RandomSamplingAssistant assist=this.samplingAssistant;
    if (assist != null) {
      this.samplingAssistant=new RandomSamplingAssistant(Arithmetic.floor(N / samplingRate),N,assist.getRandomGenerator());
    }
  }
  /** 
 * Returns a deep copy of the receiver.
 * @return a deep copy of the receiver.
 */
  public Object clone(){
    KnownDoubleQuantileEstimator copy=(KnownDoubleQuantileEstimator)super.clone();
    if (this.samplingAssistant != null)     copy.samplingAssistant=(RandomSamplingAssistant)copy.samplingAssistant.clone();
    return copy;
  }
  /** 
 * Not yet commented.
 */
  protected void newBuffer(){
    int numberOfEmptyBuffers=this.bufferSet._getNumberOfEmptyBuffers();
    if (numberOfEmptyBuffers == 0)     throw new RuntimeException("Oops, no empty buffer.");
    this.currentBufferToFill=this.bufferSet._getFirstEmptyBuffer();
    if (numberOfEmptyBuffers == 1 && !this.weHadMoreThanOneEmptyBuffer) {
      this.currentBufferToFill.level(this.bufferSet._getMinLevelOfFullOrPartialBuffers());
    }
 else {
      this.weHadMoreThanOneEmptyBuffer=true;
      this.currentBufferToFill.level(0);
    }
    this.currentBufferToFill.weight(1);
  }
  /** 
 * Not yet commented.
 */
  protected void postCollapse(  DoubleBuffer[] toCollapse){
    this.weHadMoreThanOneEmptyBuffer=false;
  }
  /** 
 */
  protected DoubleArrayList preProcessPhis(  DoubleArrayList phis){
    if (beta > 1.0) {
      phis=phis.copy();
      for (int i=phis.size(); --i >= 0; ) {
        phis.set(i,(2 * phis.get(i) + beta - 1) / (2 * beta));
      }
    }
    return phis;
  }
  /** 
 * Computes the specified quantile elements over the values previously added.
 * @param phis the quantiles for which elements are to be computed. Each phi must be in the interval [0.0,1.0]. <tt>phis</tt> must be sorted ascending.
 * @return the approximate quantile elements.
 */
  public DoubleArrayList quantileElements(  DoubleArrayList phis){
    DoubleBuffer partial=this.bufferSet._getPartialBuffer();
    int missingValues=0;
    if (partial != null) {
      missingValues=bufferSet.k() - partial.size();
      if (missingValues <= 0)       throw new RuntimeException("Oops! illegal missing values.");
      this.addInfinities(missingValues,partial);
      this.beta=(this.totalElementsFilled + missingValues) / (double)this.totalElementsFilled;
    }
 else {
      this.beta=1.0;
    }
    DoubleArrayList quantileElements=super.quantileElements(phis);
    if (partial != null)     removeInfinitiesFrom(missingValues,partial);
    return quantileElements;
  }
  /** 
 * Reading off quantiles requires to fill some +infinity, -infinity values to make a partial buffer become full.
 * This method removes the infinities which were previously temporarily added to a partial buffer.
 * Removing them is necessary if we want to continue filling more elements.
 * Precondition: the buffer is sorted ascending.
 * @param infinities the number of infinities previously filled.
 * @param buffer the buffer into which the infinities were filled.
 */
  protected void removeInfinitiesFrom(  int infinities,  DoubleBuffer buffer){
    int plusInf=0;
    int minusInf=0;
    boolean even=true;
    for (int i=0; i < infinities; i++) {
      if (even)       plusInf++;
 else       minusInf++;
      even=!even;
    }
    buffer.values.removeFromTo(buffer.size() - plusInf,buffer.size() - 1);
    buffer.values.removeFromTo(0,minusInf - 1);
  }
  /** 
 * Not yet commented.
 */
  protected boolean sampleNextElement(){
    if (samplingAssistant == null)     return true;
    return samplingAssistant.sampleNextElement();
  }
}

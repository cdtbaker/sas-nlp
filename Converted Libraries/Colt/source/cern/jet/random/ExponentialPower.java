package cern.jet.random;
import cern.jet.random.engine.RandomEngine;
/** 
 * Exponential Power distribution.
 * <p>
 * Valid parameter ranges: <tt>tau &gt;= 1</tt>.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b>
 * <dt>Method: Non-universal rejection method for logconcave densities.
 * <dt>This is a port of <tt>epd.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * L. Devroye (1986): Non-Uniform Random Variate Generation , Springer Verlag, New York.
 * <p>
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class ExponentialPower extends AbstractContinousDistribution {
  protected double tau;
  private double s, sm1, tau_set=-1.0;
  protected static ExponentialPower shared=new ExponentialPower(1.0,makeDefaultGenerator());
  /** 
 * Constructs an Exponential Power distribution.
 * Example: tau=1.0.
 * @throws IllegalArgumentException if <tt>tau &lt; 1.0</tt>.
 */
  public ExponentialPower(  double tau,  RandomEngine randomGenerator){
    setRandomGenerator(randomGenerator);
    setState(tau);
  }
  /** 
 * Returns a random number from the distribution.
 */
  public double nextDouble(){
    return nextDouble(this.tau);
  }
  /** 
 * Returns a random number from the distribution; bypasses the internal state.
 * @throws IllegalArgumentException if <tt>tau &lt; 1.0</tt>.
 */
  public double nextDouble(  double tau){
    double u, u1, v, x, y;
    if (tau != tau_set) {
      s=1.0 / tau;
      sm1=1.0 - s;
      tau_set=tau;
    }
    do {
      u=randomGenerator.raw();
      u=(2.0 * u) - 1.0;
      u1=Math.abs(u);
      v=randomGenerator.raw();
      if (u1 <= sm1) {
        x=u1;
      }
 else {
        y=tau * (1.0 - u1);
        x=sm1 - s * Math.log(y);
        v=v * y;
      }
    }
 while (Math.log(v) > -Math.exp(Math.log(x) * tau));
    if (u < 0.0)     return x;
 else     return -x;
  }
  /** 
 * Sets the distribution parameter.
 * @throws IllegalArgumentException if <tt>tau &lt; 1.0</tt>.
 */
  public void setState(  double tau){
    if (tau < 1.0)     throw new IllegalArgumentException();
    this.tau=tau;
  }
  /** 
 * Returns a random number from the distribution.
 * @throws IllegalArgumentException if <tt>tau &lt; 1.0</tt>.
 */
  public static double staticNextDouble(  double tau){
synchronized (shared) {
      return shared.nextDouble(tau);
    }
  }
  /** 
 * Returns a String representation of the receiver.
 */
  public String toString(){
    return this.getClass().getName() + "(" + tau+ ")";
  }
  /** 
 * Sets the uniform random number generated shared by all <b>static</b> methods.
 * @param randomGenerator the new uniform random number generator to be shared.
 */
  private static void xstaticSetRandomGenerator(  RandomEngine randomGenerator){
synchronized (shared) {
      shared.setRandomGenerator(randomGenerator);
    }
  }
}

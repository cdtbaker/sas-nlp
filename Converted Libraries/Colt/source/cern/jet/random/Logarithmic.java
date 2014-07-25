package cern.jet.random;
import cern.jet.random.engine.RandomEngine;
/** 
 * Logarithmic distribution.
 * <p>
 * Valid parameter ranges: <tt>0 &lt; p &lt; 1</tt>.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> 
 * <dt>
 * Method: Inversion/Transformation.
 * <dt>
 * This is a port of <tt>lsk.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * A.W. Kemp (1981): Efficient generation of logarithmically distributed pseudo-random variables, Appl. Statist. 30, 249-253.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Logarithmic extends AbstractContinousDistribution {
  protected double my_p;
  private double t, h, a_prev=-1.0;
  protected static Logarithmic shared=new Logarithmic(0.5,makeDefaultGenerator());
  /** 
 * Constructs a Logarithmic distribution.
 */
  public Logarithmic(  double p,  RandomEngine randomGenerator){
    setRandomGenerator(randomGenerator);
    setState(p);
  }
  /** 
 * Returns a random number from the distribution.
 */
  public double nextDouble(){
    return nextDouble(this.my_p);
  }
  /** 
 * Returns a random number from the distribution; bypasses the internal state.
 */
  public double nextDouble(  double a){
    double u, v, p, q;
    int k;
    if (a != a_prev) {
      a_prev=a;
      if (a < 0.97)       t=-a / Math.log(1.0 - a);
 else       h=Math.log(1.0 - a);
    }
    u=randomGenerator.raw();
    if (a < 0.97) {
      k=1;
      p=t;
      while (u > p) {
        u-=p;
        k++;
        p*=a * (k - 1.0) / (double)k;
      }
      return k;
    }
    if (u > a)     return 1;
    u=randomGenerator.raw();
    v=u;
    q=1.0 - Math.exp(v * h);
    if (u <= q * q) {
      k=(int)(1 + Math.log(u) / Math.log(q));
      return k;
    }
    if (u > q)     return 1;
    return 2;
  }
  /** 
 * Sets the distribution parameter.
 */
  public void setState(  double p){
    this.my_p=p;
  }
  /** 
 * Returns a random number from the distribution.
 */
  public static double staticNextDouble(  double p){
synchronized (shared) {
      return shared.nextDouble(p);
    }
  }
  /** 
 * Returns a String representation of the receiver.
 */
  public String toString(){
    return this.getClass().getName() + "(" + my_p+ ")";
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

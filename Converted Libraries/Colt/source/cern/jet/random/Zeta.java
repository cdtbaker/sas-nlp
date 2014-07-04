package cern.jet.random;
import cern.jet.random.engine.RandomEngine;
/** 
 * Zeta distribution.
 * <p>
 * Valid parameter ranges: <tt>ro &gt; 0</tt> and <tt>pk &gt;= 0</tt>.
 * <dt>
 * If either <tt>ro &gt; 100</tt>  or  <tt>k &gt; 10000</tt> numerical problems in
 * computing the theoretical moments arise, therefore <tt>ro &lt;= 100</tt> and 
 * <tt>k &lt;= 10000</tt> are recommended.                                      
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> 
 * <dt>Method: Acceptance/Rejection.
 * High performance implementation.
 * <dt>This is a port and adaption of <tt>Zeta.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * J. Dagpunar (1988): Principles of Random Variate  Generation, Clarendon Press, Oxford.   
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Zeta extends AbstractDiscreteDistribution {
  protected double ro;
  protected double pk;
  protected double c, d, ro_prev=-1.0, pk_prev=-1.0;
  protected double maxlongint=Long.MAX_VALUE - 1.5;
  protected static Zeta shared=new Zeta(1.0,1.0,makeDefaultGenerator());
  /** 
 * Constructs a Zeta distribution.
 */
  public Zeta(  double ro,  double pk,  RandomEngine randomGenerator){
    setRandomGenerator(randomGenerator);
    setState(ro,pk);
  }
  /** 
 * Returns a zeta distributed random number.
 */
  protected long generateZeta(  double ro,  double pk,  RandomEngine randomGenerator){
    double u, v, e, x;
    long k;
    if (ro != ro_prev || pk != pk_prev) {
      ro_prev=ro;
      pk_prev=pk;
      if (ro < pk) {
        c=pk - 0.5;
        d=0;
      }
 else {
        c=ro - 0.5;
        d=(1.0 + ro) * Math.log((1.0 + pk) / (1.0 + ro));
      }
    }
    do {
      do {
        u=randomGenerator.raw();
        v=randomGenerator.raw();
        x=(c + 0.5) * Math.exp(-Math.log(u) / ro) - c;
      }
 while (x <= 0.5 || x >= maxlongint);
      k=(int)(x + 0.5);
      e=-Math.log(v);
    }
 while (e < (1.0 + ro) * Math.log((k + pk) / (x + c)) - d);
    return k;
  }
  /** 
 * Returns a random number from the distribution.
 */
  public int nextInt(){
    return (int)generateZeta(ro,pk,randomGenerator);
  }
  /** 
 * Sets the parameters.
 */
  public void setState(  double ro,  double pk){
    this.ro=ro;
    this.pk=pk;
  }
  /** 
 * Returns a random number from the distribution.
 */
  public static int staticNextInt(  double ro,  double pk){
synchronized (shared) {
      shared.setState(ro,pk);
      return shared.nextInt();
    }
  }
  /** 
 * Returns a String representation of the receiver.
 */
  public String toString(){
    return this.getClass().getName() + "(" + ro+ ","+ pk+ ")";
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

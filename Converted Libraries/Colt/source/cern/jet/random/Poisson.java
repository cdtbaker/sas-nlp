package cern.jet.random;
import cern.jet.math.Arithmetic;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Probability;
/** 
 * Poisson distribution (quick); See the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node208.html#SECTION0002080000000000000000"> math definition</A>
 * and <A HREF="http://www.statsoft.com/textbook/glosp.html#Poisson Distribution"> animated definition</A>.
 * <p>
 * <tt>p(k) = (mean^k / k!) * exp(-mean)</tt> for <tt>k &gt;= 0</tt>.
 * <p>
 * Valid parameter ranges: <tt>mean &gt; 0</tt>.
 * Note: if <tt>mean &lt;= 0.0</tt> then always returns zero.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> High performance implementation.
 * Patchwork Rejection/Inversion method.
 * <dt>This is a port of <tt>pprsc.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * H. Zechner (1994): Efficient sampling from continuous and discrete unimodal distributions,
 * Doctoral Dissertation, 156 pp., Technical University Graz, Austria.
 * <p>
 * Also see
 * <p>
 * Stadlober E., H. Zechner (1999), <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">The patchwork rejection method for sampling from unimodal distributions</A>,
 * to appear in ACM Transactions on Modelling and Simulation.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Poisson extends AbstractDiscreteDistribution {
  protected double mean;
  protected double my_old=-1.0;
  protected double p, q, p0;
  protected double[] pp=new double[36];
  protected int llll;
  protected double my_last=-1.0;
  protected double ll;
  protected int k2, k4, k1, k5;
  protected double dl, dr, r1, r2, r4, r5, lr, l_my, c_pm;
  protected double f1, f2, f4, f5, p1, p2, p3, p4, p5, p6;
  protected int m;
  protected static final double MEAN_MAX=Integer.MAX_VALUE;
  protected static final double SWITCH_MEAN=10.0;
  protected static Poisson shared=new Poisson(0.0,makeDefaultGenerator());
  /** 
 * Constructs a poisson distribution.
 * Example: mean=1.0.
 */
  public Poisson(  double mean,  RandomEngine randomGenerator){
    setRandomGenerator(randomGenerator);
    setMean(mean);
  }
  /** 
 * Returns the cumulative distribution function.
 */
  public double cdf(  int k){
    return Probability.poisson(k,this.mean);
  }
  /** 
 * Returns a deep copy of the receiver; the copy will produce identical sequences.
 * After this call has returned, the copy and the receiver have equal but separate state.
 * @return a copy of the receiver.
 */
  public Object clone(){
    Poisson copy=(Poisson)super.clone();
    if (this.pp != null)     copy.pp=(double[])this.pp.clone();
    return copy;
  }
  private static double f(  int k,  double l_nu,  double c_pm){
    return Math.exp(k * l_nu - Arithmetic.logFactorial(k) - c_pm);
  }
  /** 
 * Returns a random number from the distribution.
 */
  public int nextInt(){
    return nextInt(this.mean);
  }
  /** 
 * Returns a random number from the distribution; bypasses the internal state.
 */
  public int nextInt(  double theMean){
    RandomEngine gen=this.randomGenerator;
    double my=theMean;
    double t, g, my_k;
    double gx, gy, px, py, e, x, xx, delta, v;
    int sign;
    double u;
    int k, i;
    if (my < SWITCH_MEAN) {
      if (my != my_old) {
        my_old=my;
        llll=0;
        p=Math.exp(-my);
        q=p;
        p0=p;
      }
      m=(my > 1.0) ? (int)my : 1;
      for (; ; ) {
        u=gen.raw();
        k=0;
        if (u <= p0)         return (k);
        if (llll != 0) {
          i=(u > 0.458) ? Math.min(llll,m) : 1;
          for (k=i; k <= llll; k++)           if (u <= pp[k])           return (k);
          if (llll == 35)           continue;
        }
        for (k=llll + 1; k <= 35; k++) {
          p*=my / (double)k;
          q+=p;
          pp[k]=q;
          if (u <= q) {
            llll=k;
            return (k);
          }
        }
        llll=35;
      }
    }
 else     if (my < MEAN_MAX) {
      int Dk, X, Y;
      double Ds, U, V, W;
      m=(int)my;
      if (my != my_last) {
        my_last=my;
        Ds=Math.sqrt(my + 0.25);
        k2=(int)Math.ceil(my - 0.5 - Ds);
        k4=(int)(my - 0.5 + Ds);
        k1=k2 + k2 - m + 1;
        k5=k4 + k4 - m;
        dl=(double)(k2 - k1);
        dr=(double)(k5 - k4);
        r1=my / (double)k1;
        r2=my / (double)k2;
        r4=my / (double)(k4 + 1);
        r5=my / (double)(k5 + 1);
        ll=Math.log(r1);
        lr=-Math.log(r5);
        l_my=Math.log(my);
        c_pm=m * l_my - Arithmetic.logFactorial(m);
        f2=f(k2,l_my,c_pm);
        f4=f(k4,l_my,c_pm);
        f1=f(k1,l_my,c_pm);
        f5=f(k5,l_my,c_pm);
        p1=f2 * (dl + 1.0);
        p2=f2 * dl + p1;
        p3=f4 * (dr + 1.0) + p2;
        p4=f4 * dr + p3;
        p5=f1 / ll + p4;
        p6=f5 / lr + p5;
      }
      for (; ; ) {
        if ((U=gen.raw() * p6) < p2) {
          if ((V=U - p1) < 0.0)           return (k2 + (int)(U / f2));
          if ((W=V / dl) < f1)           return (k1 + (int)(V / f1));
          Dk=(int)(dl * gen.raw()) + 1;
          if (W <= f2 - Dk * (f2 - f2 / r2)) {
            return (k2 - Dk);
          }
          if ((V=f2 + f2 - W) < 1.0) {
            Y=k2 + Dk;
            if (V <= f2 + Dk * (1.0 - f2) / (dl + 1.0)) {
              return (Y);
            }
            if (V <= f(Y,l_my,c_pm))             return (Y);
          }
          X=k2 - Dk;
        }
 else         if (U < p4) {
          if ((V=U - p3) < 0.0)           return (k4 - (int)((U - p2) / f4));
          if ((W=V / dr) < f5)           return (k5 - (int)(V / f5));
          Dk=(int)(dr * gen.raw()) + 1;
          if (W <= f4 - Dk * (f4 - f4 * r4)) {
            return (k4 + Dk);
          }
          if ((V=f4 + f4 - W) < 1.0) {
            Y=k4 - Dk;
            if (V <= f4 + Dk * (1.0 - f4) / dr) {
              return (Y);
            }
            if (V <= f(Y,l_my,c_pm))             return (Y);
          }
          X=k4 + Dk;
        }
 else {
          W=gen.raw();
          if (U < p5) {
            Dk=(int)(1.0 - Math.log(W) / ll);
            if ((X=k1 - Dk) < 0)             continue;
            W*=(U - p4) * ll;
            if (W <= f1 - Dk * (f1 - f1 / r1))             return (X);
          }
 else {
            Dk=(int)(1.0 - Math.log(W) / lr);
            X=k5 + Dk;
            W*=(U - p5) * lr;
            if (W <= f5 - Dk * (f5 - f5 * r5))             return (X);
          }
        }
        if (Math.log(W) <= X * l_my - Arithmetic.logFactorial(X) - c_pm)         return (X);
      }
    }
 else {
      return (int)my;
    }
  }
  /** 
 * Returns the probability distribution function.
 */
  public double pdf(  int k){
    return Math.exp(k * Math.log(this.mean) - Arithmetic.logFactorial(k) - this.mean);
  }
  /** 
 * Sets the mean.
 */
  public void setMean(  double mean){
    this.mean=mean;
  }
  /** 
 * Returns a random number from the distribution with the given mean.
 */
  public static int staticNextInt(  double mean){
synchronized (shared) {
      shared.setMean(mean);
      return shared.nextInt();
    }
  }
  /** 
 * Returns a String representation of the receiver.
 */
  public String toString(){
    return this.getClass().getName() + "(" + mean+ ")";
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

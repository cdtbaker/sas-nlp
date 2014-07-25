package cern.jet.random;
import cern.jet.math.Arithmetic;
import cern.jet.random.engine.RandomEngine;
/** 
 * HyperGeometric distribution; See the <A HREF="http://library.advanced.org/10030/6atpdvah.htm"> math definition</A>
 * The hypergeometric distribution with parameters <tt>N</tt>, <tt>n</tt> and <tt>s</tt> is the probability distribution of the random variable X, 
 * whose value is the number of successes in a sample of <tt>n</tt> items from a population of size <tt>N</tt> that has <tt>s</tt> 'success' items and <tt>N - s</tt> 'failure' items.
 * <p>            
 * <tt>p(k) = C(s,k) * C(N-s,n-k) / C(N,n)</tt> where <tt>C(a,b) = a! / (b! * (a-b)!)</tt>.
 * <p>
 * valid for N >= 2, s,n <= N. 
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> High performance implementation.
 * Patchwork Rejection/Inversion method.
 * <dt>This is a port of <tt>hprsc.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * H. Zechner (1994): Efficient sampling from continuous and discrete unimodal distributions,
 * Doctoral Dissertation, 156 pp., Technical University Graz, Austria.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class HyperGeometric extends AbstractDiscreteDistribution {
  protected int my_N;
  protected int my_s;
  protected int my_n;
  private int N_last=-1, M_last=-1, n_last=-1;
  private int N_Mn, m;
  private int mp, b;
  private double Mp, np, fm;
  private int k2, k4, k1, k5;
  private double dl, dr, r1, r2, r4, r5, ll, lr, c_pm, f1, f2, f4, f5, p1, p2, p3, p4, p5, p6;
  protected static HyperGeometric shared=new HyperGeometric(1,1,1,makeDefaultGenerator());
  /** 
 * Constructs a HyperGeometric distribution.
 */
  public HyperGeometric(  int N,  int s,  int n,  RandomEngine randomGenerator){
    setRandomGenerator(randomGenerator);
    setState(N,s,n);
  }
  private static double fc_lnpk(  int k,  int N_Mn,  int M,  int n){
    return (Arithmetic.logFactorial(k) + Arithmetic.logFactorial(M - k) + Arithmetic.logFactorial(n - k)+ Arithmetic.logFactorial(N_Mn + k));
  }
  /** 
 * Returns a random number from the distribution.
 */
  protected int hmdu(  int N,  int M,  int n,  RandomEngine randomGenerator){
    int I, K;
    double p, nu, c, d, U;
    if (N != N_last || M != M_last || n != n_last) {
      N_last=N;
      M_last=M;
      n_last=n;
      Mp=(double)(M + 1);
      np=(double)(n + 1);
      N_Mn=N - M - n;
      p=Mp / (N + 2.0);
      nu=np * p;
      if ((m=(int)nu) == nu && p == 0.5) {
        mp=m--;
      }
 else {
        mp=m + 1;
      }
      fm=Math.exp(Arithmetic.logFactorial(N - M) - Arithmetic.logFactorial(N_Mn + m) - Arithmetic.logFactorial(n - m) + Arithmetic.logFactorial(M) - Arithmetic.logFactorial(M - m) - Arithmetic.logFactorial(m) - Arithmetic.logFactorial(N) + Arithmetic.logFactorial(N - n) + Arithmetic.logFactorial(n));
      b=(int)(nu + 11.0 * Math.sqrt(nu * (1.0 - p) * (1.0 - n / (double)N) + 1.0));
      if (b > n)       b=n;
    }
    for (; ; ) {
      if ((U=randomGenerator.raw() - fm) <= 0.0)       return (m);
      c=d=fm;
      for (I=1; I <= m; I++) {
        K=mp - I;
        c*=(double)K / (np - K) * ((double)(N_Mn + K) / (Mp - K));
        if ((U-=c) <= 0.0)         return (K - 1);
        K=m + I;
        d*=(np - K) / (double)K * ((Mp - K) / (double)(N_Mn + K));
        if ((U-=d) <= 0.0)         return (K);
      }
      for (K=mp + m; K <= b; K++) {
        d*=(np - K) / (double)K * ((Mp - K) / (double)(N_Mn + K));
        if ((U-=d) <= 0.0)         return (K);
      }
    }
  }
  /** 
 * Returns a random number from the distribution.
 */
  protected int hprs(  int N,  int M,  int n,  RandomEngine randomGenerator){
    int Dk, X, V;
    double Mp, np, p, nu, U, Y, W;
    if (N != N_last || M != M_last || n != n_last) {
      N_last=N;
      M_last=M;
      n_last=n;
      Mp=(double)(M + 1);
      np=(double)(n + 1);
      N_Mn=N - M - n;
      p=Mp / (N + 2.0);
      nu=np * p;
      U=Math.sqrt(nu * (1.0 - p) * (1.0 - (n + 2.0) / (N + 3.0)) + 0.25);
      m=(int)nu;
      k2=(int)Math.ceil(nu - 0.5 - U);
      if (k2 >= m)       k2=m - 1;
      k4=(int)(nu - 0.5 + U);
      k1=k2 + k2 - m + 1;
      k5=k4 + k4 - m;
      dl=(double)(k2 - k1);
      dr=(double)(k5 - k4);
      r1=(np / (double)k1 - 1.0) * (Mp - k1) / (double)(N_Mn + k1);
      r2=(np / (double)k2 - 1.0) * (Mp - k2) / (double)(N_Mn + k2);
      r4=(np / (double)(k4 + 1) - 1.0) * (M - k4) / (double)(N_Mn + k4 + 1);
      r5=(np / (double)(k5 + 1) - 1.0) * (M - k5) / (double)(N_Mn + k5 + 1);
      ll=Math.log(r1);
      lr=-Math.log(r5);
      c_pm=fc_lnpk(m,N_Mn,M,n);
      f2=Math.exp(c_pm - fc_lnpk(k2,N_Mn,M,n));
      f4=Math.exp(c_pm - fc_lnpk(k4,N_Mn,M,n));
      f1=Math.exp(c_pm - fc_lnpk(k1,N_Mn,M,n));
      f5=Math.exp(c_pm - fc_lnpk(k5,N_Mn,M,n));
      p1=f2 * (dl + 1.0);
      p2=f2 * dl + p1;
      p3=f4 * (dr + 1.0) + p2;
      p4=f4 * dr + p3;
      p5=f1 / ll + p4;
      p6=f5 / lr + p5;
    }
    for (; ; ) {
      if ((U=randomGenerator.raw() * p6) < p2) {
        if ((W=U - p1) < 0.0)         return (k2 + (int)(U / f2));
        if ((Y=W / dl) < f1)         return (k1 + (int)(W / f1));
        Dk=(int)(dl * randomGenerator.raw()) + 1;
        if (Y <= f2 - Dk * (f2 - f2 / r2)) {
          return (k2 - Dk);
        }
        if ((W=f2 + f2 - Y) < 1.0) {
          V=k2 + Dk;
          if (W <= f2 + Dk * (1.0 - f2) / (dl + 1.0)) {
            return (V);
          }
          if (Math.log(W) <= c_pm - fc_lnpk(V,N_Mn,M,n)) {
            return (V);
          }
        }
        X=k2 - Dk;
      }
 else       if (U < p4) {
        if ((W=U - p3) < 0.0)         return (k4 - (int)((U - p2) / f4));
        if ((Y=W / dr) < f5)         return (k5 - (int)(W / f5));
        Dk=(int)(dr * randomGenerator.raw()) + 1;
        if (Y <= f4 - Dk * (f4 - f4 * r4)) {
          return (k4 + Dk);
        }
        if ((W=f4 + f4 - Y) < 1.0) {
          V=k4 - Dk;
          if (W <= f4 + Dk * (1.0 - f4) / dr) {
            return (V);
          }
          if (Math.log(W) <= c_pm - fc_lnpk(V,N_Mn,M,n)) {
            return (V);
          }
        }
        X=k4 + Dk;
      }
 else {
        Y=randomGenerator.raw();
        if (U < p5) {
          Dk=(int)(1.0 - Math.log(Y) / ll);
          if ((X=k1 - Dk) < 0)           continue;
          Y*=(U - p4) * ll;
          if (Y <= f1 - Dk * (f1 - f1 / r1)) {
            return (X);
          }
        }
 else {
          Dk=(int)(1.0 - Math.log(Y) / lr);
          if ((X=k5 + Dk) > n)           continue;
          Y*=(U - p5) * lr;
          if (Y <= f5 - Dk * (f5 - f5 * r5)) {
            return (X);
          }
        }
      }
      if (Math.log(Y) <= c_pm - fc_lnpk(X,N_Mn,M,n))       return (X);
    }
  }
  /** 
 * Returns a random number from the distribution.
 */
  public int nextInt(){
    return nextInt(this.my_N,this.my_s,this.my_n,this.randomGenerator);
  }
  /** 
 * Returns a random number from the distribution; bypasses the internal state.
 */
  public int nextInt(  int N,  int s,  int n){
    return nextInt(N,s,n,this.randomGenerator);
  }
  /** 
 * Returns a random number from the distribution; bypasses the internal state.
 */
  protected int nextInt(  int N,  int M,  int n,  RandomEngine randomGenerator){
    int Nhalf, n_le_Nhalf, M_le_Nhalf, K;
    Nhalf=N / 2;
    n_le_Nhalf=(n <= Nhalf) ? n : N - n;
    M_le_Nhalf=(M <= Nhalf) ? M : N - M;
    if ((n * M / N) < 10) {
      K=(n_le_Nhalf <= M_le_Nhalf) ? hmdu(N,M_le_Nhalf,n_le_Nhalf,randomGenerator) : hmdu(N,n_le_Nhalf,M_le_Nhalf,randomGenerator);
    }
 else {
      K=(n_le_Nhalf <= M_le_Nhalf) ? hprs(N,M_le_Nhalf,n_le_Nhalf,randomGenerator) : hprs(N,n_le_Nhalf,M_le_Nhalf,randomGenerator);
    }
    if (n <= Nhalf) {
      return (M <= Nhalf) ? K : n - K;
    }
 else {
      return (M <= Nhalf) ? M - K : n - N + M + K;
    }
  }
  /** 
 * Returns the probability distribution function.
 */
  public double pdf(  int k){
    return Arithmetic.binomial(my_s,k) * Arithmetic.binomial(my_N - my_s,my_n - k) / Arithmetic.binomial(my_N,my_n);
  }
  /** 
 * Sets the parameters.
 */
  public void setState(  int N,  int s,  int n){
    this.my_N=N;
    this.my_s=s;
    this.my_n=n;
  }
  /** 
 * Returns a random number from the distribution.
 */
  public static double staticNextInt(  int N,  int M,  int n){
synchronized (shared) {
      return shared.nextInt(N,M,n);
    }
  }
  /** 
 * Returns a String representation of the receiver.
 */
  public String toString(){
    return this.getClass().getName() + "(" + my_N+ ","+ my_s+ ","+ my_n+ ")";
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

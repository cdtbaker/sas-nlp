package cern.jet.random;
import cern.jet.random.engine.RandomEngine;
/** 
 * Contains methods for conveniently generating pseudo-random numbers from special distributions such as the Burr, Cauchy, Erlang, Geometric, Lambda, Laplace, Logistic, Weibull, etc.
 * <p>
 * <b>About this class:</b>
 * <dt>All distributions are obtained by using a <b>uniform</b> pseudo-random number generator.
 * followed by a transformation to the desired distribution.
 * <p>
 * <b>Example usage:</b><pre>
 * cern.jet.random.engine.RandomEngine generator;
 * generator = new cern.jet.random.engine.MersenneTwister(new java.util.Date());
 * //generator = new edu.cornell.lassp.houle.RngPack.Ranecu(new java.util.Date());
 * //generator = new edu.cornell.lassp.houle.RngPack.Ranmar(new java.util.Date());
 * //generator = new edu.cornell.lassp.houle.RngPack.Ranlux(new java.util.Date());
 * //generator = AbstractDistribution.makeDefaultGenerator();
 * for (int i=1000000; --i >=0; ) {
 * int cauchy = Distributions.nextCauchy(generator);
 * ...
 * }
 * </pre>
 * @see cern.jet.random.engine.MersenneTwister
 * @see java.util.Random
 * @see java.lang.Math
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Distributions {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected Distributions(){
    throw new RuntimeException("Non instantiable");
  }
  /** 
 * Returns the probability distribution function of the discrete geometric distribution.
 * <p>
 * <tt>p(k) = p * (1-p)^k</tt> for <tt> k &gt;= 0</tt>.
 * <p>
 * @param k the argument to the probability distribution function.
 * @param p the parameter of the probability distribution function.
 */
  public static double geometricPdf(  int k,  double p){
    if (k < 0)     throw new IllegalArgumentException();
    return p * Math.pow(1 - p,k);
  }
  /** 
 * Returns a random number from the Burr II, VII, VIII, X Distributions.
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>burr1.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * L. Devroye (1986): Non-Uniform Random Variate Generation, Springer Verlag, New York.                                      
 * <p>
 * @param r must be &gt; 0.
 * @param nr the number of the burr distribution (e.g. 2,7,8,10).
 */
  public static double nextBurr1(  double r,  int nr,  RandomEngine randomGenerator){
    double y;
    y=Math.exp(Math.log(randomGenerator.raw()) / r);
switch (nr) {
case 2:
      return (-Math.log(1 / y - 1));
case 7:
    return (Math.log(2 * y / (2 - 2 * y)) / 2);
case 8:
  return (Math.log(Math.tan(y * Math.PI / 2.0)));
case 10:
return (Math.sqrt(-Math.log(1 - y)));
}
return 0;
}
/** 
 * Returns a random number from the Burr III, IV, V, VI, IX, XII distributions.
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>burr2.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * L. Devroye (1986): Non-Uniform Random Variate Generation, Springer Verlag, New York.                                      
 * <p>
 * @param r must be &gt; 0.
 * @param k must be &gt; 0.
 * @param nr the number of the burr distribution (e.g. 3,4,5,6,9,12).
 */
public static double nextBurr2(double r,double k,int nr,RandomEngine randomGenerator){
double y, u;
u=randomGenerator.raw();
y=Math.exp(-Math.log(u) / r) - 1.0;
switch (nr) {
case 3:
return (Math.exp(-Math.log(y) / k));
case 4:
y=Math.exp(k * Math.log(y)) + 1.0;
y=k / y;
return (y);
case 5:
y=Math.atan(-Math.log(y / k));
return (y);
case 6:
y=-Math.log(y / k) / r;
y=Math.log(y + Math.sqrt(y * y + 1.0));
return (y);
case 9:
y=1.0 + 2.0 * u / (k * (1.0 - u));
y=Math.exp(Math.log(y) / r) - 1.0;
return Math.log(y);
case 12:
return Math.exp(Math.log(y) / k);
}
return 0;
}
/** 
 * Returns a cauchy distributed random number from the standard Cauchy distribution C(0,1).  
 * <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node25.html#SECTION000250000000000000000"> math definition</A>
 * and <A HREF="http://www.statsoft.com/textbook/glosc.html#Cauchy Distribution"> animated definition</A>. 
 * <p>
 * <tt>p(x) = 1/ (mean*pi * (1+(x/mean)^2))</tt>.
 * <p>
 * <b>Implementation:</b>
 * This is a port of <tt>cin.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * <p>
 * @returns a number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
public static double nextCauchy(RandomEngine randomGenerator){
return Math.tan(Math.PI * randomGenerator.raw());
}
/** 
 * Returns an erlang distributed random number with the given variance and mean.
 */
public static double nextErlang(double variance,double mean,RandomEngine randomGenerator){
int k=(int)((mean * mean) / variance + 0.5);
k=(k > 0) ? k : 1;
double a=k / mean;
double prod=1.0;
for (int i=0; i < k; i++) prod*=randomGenerator.raw();
return -Math.log(prod) / a;
}
/** 
 * Returns a discrete geometric distributed random number; <A HREF="http://www.statsoft.com/textbook/glosf.html#Geometric Distribution">Definition</A>.
 * <p>
 * <tt>p(k) = p * (1-p)^k</tt> for <tt> k &gt;= 0</tt>.
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>geo.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * @param p must satisfy <tt>0 &lt; p &lt; 1</tt>.
 * <p>
 */
public static int nextGeometric(double p,RandomEngine randomGenerator){
double u=randomGenerator.raw();
return (int)(Math.log(u) / Math.log(1.0 - p));
}
/** 
 * Returns a lambda distributed random number with parameters l3 and l4.
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>lamin.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>
 * J.S. Ramberg, B:W. Schmeiser (1974): An approximate method for generating asymmetric variables, Communications ACM 17, 78-82.
 * <p>
 */
public static double nextLambda(double l3,double l4,RandomEngine randomGenerator){
double l_sign;
if ((l3 < 0) || (l4 < 0)) l_sign=-1.0;
 else l_sign=1.0;
double u=randomGenerator.raw();
double x=l_sign * (Math.exp(Math.log(u) * l3) - Math.exp(Math.log(1.0 - u) * l4));
return x;
}
/** 
 * Returns a Laplace (Double Exponential) distributed random number from the standard Laplace distribution L(0,1).  
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>lapin.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * <p>
 * @returns a number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
public static double nextLaplace(RandomEngine randomGenerator){
double u=randomGenerator.raw();
u=u + u - 1.0;
if (u > 0) return -Math.log(1.0 - u);
 else return Math.log(1.0 + u);
}
/** 
 * Returns a random number from the standard Logistic distribution Log(0,1).
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>login.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 */
public static double nextLogistic(RandomEngine randomGenerator){
double u=randomGenerator.raw();
return (-Math.log(1.0 / u - 1.0));
}
/** 
 * Returns a power-law distributed random number with the given exponent and lower cutoff.
 * @param alpha the exponent 
 * @param cut the lower cutoff
 */
public static double nextPowLaw(double alpha,double cut,RandomEngine randomGenerator){
return cut * Math.pow(randomGenerator.raw(),1.0 / (alpha + 1.0));
}
/** 
 * Returns a random number from the standard Triangular distribution in (-1,1).
 * <p>
 * <b>Implementation:</b> Inversion method.
 * This is a port of <tt>tra.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * <p>
 */
public static double nextTriangular(RandomEngine randomGenerator){
double u;
u=randomGenerator.raw();
if (u <= 0.5) return (Math.sqrt(2.0 * u) - 1.0);
 else return (1.0 - Math.sqrt(2.0 * (1.0 - u)));
}
/** 
 * Returns a weibull distributed random number. 
 * Polar method.
 * See Simulation, Modelling & Analysis by Law & Kelton, pp259
 */
public static double nextWeibull(double alpha,double beta,RandomEngine randomGenerator){
return Math.pow(beta * (-Math.log(1.0 - randomGenerator.raw())),1.0 / alpha);
}
/** 
 * Returns a zipfian distributed random number with the given skew.
 * <p>
 * Algorithm from page 551 of:
 * Devroye, Luc (1986) `Non-uniform random variate generation',
 * Springer-Verlag: Berlin.   ISBN 3-540-96305-7 (also 0-387-96305-7)
 * @param z the skew of the distribution (must be &gt;1.0).
 * @returns a zipfian distributed number in the closed interval <tt>[1,Integer.MAX_VALUE]</tt>.
 */
public static int nextZipfInt(double z,RandomEngine randomGenerator){
final double b=Math.pow(2.0,z - 1.0);
final double constant=-1.0 / (z - 1.0);
int result=0;
for (; ; ) {
double u=randomGenerator.raw();
double v=randomGenerator.raw();
result=(int)(Math.floor(Math.pow(u,constant)));
double t=Math.pow(1.0 + 1.0 / result,z - 1.0);
if (v * result * (t - 1.0) / (b - 1.0) <= t / b) break;
}
return result;
}
}

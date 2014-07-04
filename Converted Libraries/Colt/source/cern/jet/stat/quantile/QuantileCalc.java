package cern.jet.stat.quantile;
/** 
 * Computes b and k vor various parameters.
 */
class QuantileCalc extends Object {
  /** 
 * Efficiently computes the binomial coefficient, often also referred to as "n over k" or "n choose k".
 * The binomial coefficient is defined as n!/((n-k)!*k!).
 * Tries to avoid numeric overflows. 
 * @return the binomial coefficient.
 */
  public static double binomial(  long n,  long k){
    if (k == 0 || k == n) {
      return 1.0;
    }
    if (k > n / 2.0)     k=n - k;
    double binomial=1.0;
    long N=n - k + 1;
    for (long i=k; i > 0; ) {
      binomial*=((double)N++) / (double)(i--);
    }
    return binomial;
  }
  /** 
 * Returns the smallest <code>long &gt;= value</code>.
 * <dt>Examples: <code>1.0 -> 1, 1.2 -> 2, 1.9 -> 2</code>.
 * This method is safer than using (long) Math.ceil(value), because of possible rounding error.
 */
  public static long ceiling(  double value){
    return Math.round(Math.ceil(value));
  }
  /** 
 * Computes the number of buffers and number of values per buffer such that
 * quantiles can be determined with an approximation error no more than epsilon with a certain probability.
 * Assumes that quantiles are to be computed over N values.
 * The required sampling rate is computed and stored in the first element of the provided <tt>returnSamplingRate</tt> array, which, therefore must be at least of length 1.
 * @param N the number of values over which quantiles shall be computed (e.g <tt>10^6</tt>).
 * @param epsilon the approximation error which is guaranteed not to be exceeded (e.g. <tt>0.001</tt>) (<tt>0 &lt;= epsilon &lt;= 1</tt>). To get exact result, set <tt>epsilon=0.0</tt>;
 * @param delta the probability that the approximation error is more than than epsilon (e.g. <tt>0.0001</tt>) (<tt>0 &lt;= delta &lt;= 1</tt>). To avoid probabilistic answers, set <tt>delta=0.0</tt>.
 * @param quantiles the number of quantiles to be computed (e.g. <tt>100</tt>) (<tt>quantiles &gt;= 1</tt>). If unknown in advance, set this number large, e.g. <tt>quantiles &gt;= 10000</tt>.
 * @param samplingRate a <tt>double[1]</tt> where the sampling rate is to be filled in.
 * @return <tt>long[2]</tt> - <tt>long[0]</tt>=the number of buffers, <tt>long[1]</tt>=the number of elements per buffer, <tt>returnSamplingRate[0]</tt>=the required sampling rate.
 */
  public static long[] known_N_compute_B_and_K(  long N,  double epsilon,  double delta,  int quantiles,  double[] returnSamplingRate){
    if (delta > 0.0) {
      return known_N_compute_B_and_K_slow(N,epsilon,delta,quantiles,returnSamplingRate);
    }
    returnSamplingRate[0]=1.0;
    return known_N_compute_B_and_K_quick(N,epsilon);
  }
  /** 
 * Computes the number of buffers and number of values per buffer such that
 * quantiles can be determined with a <b>guaranteed</b> approximation error no more than epsilon.
 * Assumes that quantiles are to be computed over N values.
 * @return <tt>long[2]</tt> - <tt>long[0]</tt>=the number of buffers, <tt>long[1]</tt>=the number of elements per buffer.
 * @param N the anticipated number of values over which quantiles shall be determined.
 * @param epsilon the approximation error which is guaranteed not to be exceeded (e.g. <tt>0.001</tt>) (<tt>0 &lt;= epsilon &lt;= 1</tt>). To get exact result, set <tt>epsilon=0.0</tt>;
 */
  protected static long[] known_N_compute_B_and_K_quick(  long N,  double epsilon){
    if (epsilon <= 0.0) {
      long[] result=new long[2];
      result[0]=1;
      result[1]=N;
      return result;
    }
    final int maxBuffers=50;
    final int maxHeight=50;
    final double N_double=(double)N;
    final double c=N_double * epsilon * 2.0;
    int[] heightMaximums=new int[maxBuffers - 1];
    for (int b=2; b <= maxBuffers; b++) {
      int h=3;
      while (h <= maxHeight && (h - 2) * ((double)Math.round(binomial(b + h - 2,h - 1))) - ((double)Math.round(binomial(b + h - 3,h - 3))) + ((double)Math.round(binomial(b + h - 3,h - 2))) - c > 0.0) {
        h++;
      }
      while (h <= maxHeight && (h - 2) * ((double)Math.round(binomial(b + h - 2,h - 1))) - ((double)Math.round(binomial(b + h - 3,h - 3))) + ((double)Math.round(binomial(b + h - 3,h - 2))) - c <= 0.0) {
        h++;
      }
      h--;
      int hMax;
      if (h >= maxHeight && (h - 2) * ((double)Math.round(binomial(b + h - 2,h - 1))) - ((double)Math.round(binomial(b + h - 3,h - 3))) + ((double)Math.round(binomial(b + h - 3,h - 2))) - c > 0.0) {
        hMax=Integer.MIN_VALUE;
      }
 else {
        hMax=h;
      }
      heightMaximums[b - 2]=hMax;
    }
    long[] kMinimums=new long[maxBuffers - 1];
    for (int b=2; b <= maxBuffers; b++) {
      int h=heightMaximums[b - 2];
      long kMin=Long.MAX_VALUE;
      if (h > Integer.MIN_VALUE) {
        double value=((double)Math.round(binomial(b + h - 2,h - 1)));
        long tmpK=ceiling(N_double / value);
        if (tmpK <= Long.MAX_VALUE) {
          kMin=tmpK;
        }
      }
      kMinimums[b - 2]=kMin;
    }
    long multMin=Long.MAX_VALUE;
    int minB=-1;
    for (int b=2; b <= maxBuffers; b++) {
      if (kMinimums[b - 2] < Long.MAX_VALUE) {
        long mult=((long)b) * ((long)kMinimums[b - 2]);
        if (mult < multMin) {
          multMin=mult;
          minB=b;
        }
      }
    }
    long b, k;
    if (minB != -1) {
      b=minB;
      k=kMinimums[minB - 2];
    }
 else {
      b=1;
      k=N;
    }
    long[] result=new long[2];
    result[0]=b;
    result[1]=k;
    return result;
  }
  /** 
 * Computes the number of buffers and number of values per buffer such that
 * quantiles can be determined with an approximation error no more than epsilon with a certain probability.
 * Assumes that quantiles are to be computed over N values.
 * The required sampling rate is computed and stored in the first element of the provided <tt>returnSamplingRate</tt> array, which, therefore must be at least of length 1.
 * @param N the anticipated number of values over which quantiles shall be computed (e.g 10^6).
 * @param epsilon the approximation error which is guaranteed not to be exceeded (e.g. <tt>0.001</tt>) (<tt>0 &lt;= epsilon &lt;= 1</tt>). To get exact result, set <tt>epsilon=0.0</tt>;
 * @param delta the probability that the approximation error is more than than epsilon (e.g. <tt>0.0001</tt>) (<tt>0 &lt;= delta &lt;= 1</tt>). To avoid probabilistic answers, set <tt>delta=0.0</tt>.
 * @param quantiles the number of quantiles to be computed (e.g. <tt>100</tt>) (<tt>quantiles &gt;= 1</tt>). If unknown in advance, set this number large, e.g. <tt>quantiles &gt;= 10000</tt>.
 * @param samplingRate a <tt>double[1]</tt> where the sampling rate is to be filled in.
 * @return <tt>long[2]</tt> - <tt>long[0]</tt>=the number of buffers, <tt>long[1]</tt>=the number of elements per buffer, <tt>returnSamplingRate[0]</tt>=the required sampling rate.
 */
  protected static long[] known_N_compute_B_and_K_slow(  long N,  double epsilon,  double delta,  int quantiles,  double[] returnSamplingRate){
    if (epsilon <= 0.0) {
      long[] result=new long[2];
      result[0]=1;
      result[1]=N;
      returnSamplingRate[0]=1.0;
      return result;
    }
    final int maxBuffers=50;
    final int maxHeight=50;
    final double N_double=N;
    long ret_b=1;
    long ret_k=N;
    double sampling_rate=1.0;
    long memory=N;
    final double logarithm=Math.log(2.0 * quantiles / delta);
    final double c=2.0 * epsilon * N_double;
    for (long b=2; b < maxBuffers; b++)     for (long h=3; h < maxHeight; h++) {
      double binomial=binomial(b + h - 2,h - 1);
      long tmp=ceiling(N_double / binomial);
      if ((b * tmp < memory) && ((h - 2) * binomial - binomial(b + h - 3,h - 3) + binomial(b + h - 3,h - 2) <= c)) {
        ret_k=tmp;
        ret_b=b;
        memory=ret_k * b;
        sampling_rate=1.0;
      }
      if (delta > 0.0) {
        double t=(h - 2) * binomial(b + h - 2,h - 1) - binomial(b + h - 3,h - 3) + binomial(b + h - 3,h - 2);
        double u=logarithm / epsilon;
        double v=binomial(b + h - 2,h - 1);
        double w=logarithm / (2.0 * epsilon * epsilon);
        double x=0.5 + 0.5 * Math.sqrt(1.0 + 4.0 * t / u);
        long k=ceiling(w * x * x / v);
        if (b * k < memory) {
          ret_k=k;
          ret_b=b;
          memory=b * k;
          sampling_rate=N_double * 2.0 * epsilon* epsilon / logarithm;
        }
      }
    }
    long[] result=new long[2];
    result[0]=ret_b;
    result[1]=ret_k;
    returnSamplingRate[0]=sampling_rate;
    return result;
  }
  public static void main(  String[] args){
    test_B_and_K_Calculation(args);
  }
  /** 
 * Computes b and k for different parameters.
 */
  public static void test_B_and_K_Calculation(  String[] args){
    boolean known_N;
    if (args == null)     known_N=false;
 else     known_N=new Boolean(args[0]).booleanValue();
    int[] quantiles={1,1000};
    long[] sizes={100000,1000000,10000000,1000000000};
    double[] deltas={0.0,0.001,0.0001,0.00001};
    double[] epsilons={0.0,0.1,0.05,0.01,0.005,0.001,0.0000001};
    if (!known_N)     sizes=new long[]{0};
    System.out.println("\n\n");
    if (known_N)     System.out.println("Computing b's and k's for KNOWN N");
 else     System.out.println("Computing b's and k's for UNKNOWN N");
    System.out.println("mem [elements/1024]");
    System.out.println("***********************************");
    for (int q=0; q < quantiles.length; q++) {
      int p=quantiles[q];
      System.out.println("------------------------------");
      System.out.println("computing for p = " + p);
      for (int s=0; s < sizes.length; s++) {
        long N=sizes[s];
        System.out.println("   ------------------------------");
        System.out.println("   computing for N = " + N);
        for (int d=0; d < deltas.length; d++) {
          double delta=deltas[d];
          System.out.println("      ------------------------------");
          System.out.println("      computing for delta = " + delta);
          for (int e=0; e < epsilons.length; e++) {
            double epsilon=epsilons[e];
            double[] returnSamplingRate=new double[1];
            long[] result;
            if (known_N) {
              result=known_N_compute_B_and_K(N,epsilon,delta,p,returnSamplingRate);
            }
 else {
              result=unknown_N_compute_B_and_K(epsilon,delta,p);
            }
            long b=result[0];
            long k=result[1];
            System.out.print("         (e,d,N,p)=(" + epsilon + ","+ delta+ ","+ N+ ","+ p+ ") --> ");
            System.out.print("(b,k,mem");
            if (known_N)             System.out.print(",sampling");
            System.out.print(")=(" + b + ","+ k+ ","+ (b * k / 1024));
            if (known_N)             System.out.print("," + returnSamplingRate[0]);
            System.out.println(")");
          }
        }
      }
    }
  }
  /** 
 * Computes the number of buffers and number of values per buffer such that
 * quantiles can be determined with an approximation error no more than epsilon with a certain probability.
 * @param epsilon the approximation error which is guaranteed not to be exceeded (e.g. <tt>0.001</tt>) (<tt>0 &lt;= epsilon &lt;= 1</tt>). To get exact results, set <tt>epsilon=0.0</tt>;
 * @param delta the probability that the approximation error is more than than epsilon (e.g. <tt>0.0001</tt>) (<tt>0 &lt;= delta &lt;= 1</tt>). To get exact results, set <tt>delta=0.0</tt>.
 * @param quantiles the number of quantiles to be computed (e.g. <tt>100</tt>) (<tt>quantiles &gt;= 1</tt>). If unknown in advance, set this number large, e.g. <tt>quantiles &gt;= 10000</tt>.
 * @return <tt>long[3]</tt> - <tt>long[0]</tt>=the number of buffers, <tt>long[1]</tt>=the number of elements per buffer, <tt>long[2]</tt>=the tree height where sampling shall start.
 */
  public static long[] unknown_N_compute_B_and_K(  double epsilon,  double delta,  int quantiles){
    if (epsilon <= 0.0 || delta <= 0.0) {
      long[] result=new long[3];
      result[0]=1;
      result[1]=Long.MAX_VALUE;
      result[2]=Long.MAX_VALUE;
      return result;
    }
    int max_b=50;
    int max_h=50;
    int max_H=50;
    int max_Iterations=2;
    long best_b=Long.MAX_VALUE;
    long best_k=Long.MAX_VALUE;
    long best_h=Long.MAX_VALUE;
    long best_memory=Long.MAX_VALUE;
    double pow=Math.pow(2.0,max_H);
    double logDelta=Math.log(2.0 / (delta / quantiles)) / (2.0 * epsilon * epsilon);
    while (best_b == Long.MAX_VALUE && max_Iterations-- > 0) {
      for (int b=2; b <= max_b; b++) {
        for (int h=2; h <= max_h; h++) {
          double Ld=binomial(b + h - 2,h - 1);
          double Ls=binomial(b + h - 3,h - 1);
          double c=logDelta / Math.min(Ld,8.0 * Ls / 3.0);
          double beta=Ld / Ls;
          double cc=(beta - 2.0) * (max_H - 2.0) / (beta + pow - 2.0);
          double d=(h + 3 + cc) / (2.0 * epsilon);
          double f=c * c + 4.0 * c * d;
          if (f < 0.0)           continue;
          double root=Math.sqrt(f);
          double alpha_one=(c + 2.0 * d + root) / (2.0 * d);
          double alpha_two=(c + 2.0 * d - root) / (2.0 * d);
          boolean alpha_one_OK=false;
          boolean alpha_two_OK=false;
          if (0.0 < alpha_one && alpha_one < 1.0)           alpha_one_OK=true;
          if (0.0 < alpha_two && alpha_two < 1.0)           alpha_two_OK=true;
          if (alpha_one_OK || alpha_two_OK) {
            double alpha=alpha_one;
            if (alpha_one_OK && alpha_two_OK) {
              alpha=Math.max(alpha_one,alpha_two);
            }
 else             if (alpha_two_OK) {
              alpha=alpha_two;
            }
            long k=ceiling(Math.max(d / alpha,(h + 1) / (2.0 * epsilon)));
            if (k > 0) {
              long memory=b * k;
              if (memory < best_memory) {
                best_k=k;
                best_b=b;
                best_h=h;
                best_memory=memory;
              }
            }
          }
        }
      }
      if (best_b == Long.MAX_VALUE) {
        System.out.println("Warning: Computing b and k looks like a lot of work!");
        max_b*=2;
        max_h*=2;
        max_H*=2;
      }
    }
    long[] result=new long[3];
    if (best_b == Long.MAX_VALUE) {
      result[0]=1;
      result[1]=Long.MAX_VALUE;
      result[2]=Long.MAX_VALUE;
    }
 else {
      result[0]=best_b;
      result[1]=best_k;
      result[2]=best_h;
    }
    return result;
  }
}

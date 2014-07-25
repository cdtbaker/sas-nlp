package cern.jet.stat.quantile;
import cern.colt.Timer;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
/** 
 * A class holding test cases for exact and approximate quantile finders.
 */
class QuantileFinderTest {
  /** 
 * Finds the first and last indexes of a specific element within a sorted list.
 * @return int[]
 * @param list cern.colt.list.DoubleArrayList
 * @param element the element to search for
 */
  protected static IntArrayList binaryMultiSearch(  DoubleArrayList list,  double element){
    int index=list.binarySearch(element);
    if (index < 0)     return null;
    int from=index - 1;
    while (from >= 0 && list.get(from) == element)     from--;
    from++;
    int to=index + 1;
    while (to < list.size() && list.get(to) == element)     to++;
    to--;
    return new IntArrayList(new int[]{from,to});
  }
  /** 
 * Observed epsilon
 */
  public static double epsilon(  int size,  double phi,  double rank){
    double s=size;
    return Math.abs(rank / s - phi);
  }
  /** 
 * Observed epsilon
 */
  public static double epsilon(  DoubleArrayList sortedList,  double phi,  double element){
    double rank=cern.jet.stat.Descriptive.rankInterpolated(sortedList,element);
    return epsilon(sortedList.size(),phi,rank);
  }
  /** 
 * Observed epsilon
 */
  public static double epsilon(  DoubleArrayList sortedList,  DoubleQuantileFinder finder,  double phi){
    double element=finder.quantileElements(new DoubleArrayList(new double[]{phi})).get(0);
    return epsilon(sortedList,phi,element);
  }
  public static void main(  String[] args){
    testBestBandKCalculation(args);
  }
  /** 
 * This method was created in VisualAge.
 * @return double[]
 * @param values cern.it.hepodbms.primitivearray.DoubleArrayList
 * @param phis double[]
 */
  public static double observedEpsilonAtPhi(  double phi,  ExactDoubleQuantileFinder exactFinder,  DoubleQuantileFinder approxFinder){
    int N=(int)exactFinder.size();
    int exactRank=(int)Utils.epsilonCeiling(phi * N) - 1;
    exactFinder.quantileElements(new DoubleArrayList(new double[]{phi})).get(0);
    double approxElement=approxFinder.quantileElements(new DoubleArrayList(new double[]{phi})).get(0);
    IntArrayList approxRanks=binaryMultiSearch(exactFinder.buffer,approxElement);
    int from=approxRanks.get(0);
    int to=approxRanks.get(1);
    int distance;
    if (from <= exactRank && exactRank <= to)     distance=0;
 else {
      if (from > exactRank)       distance=Math.abs(from - exactRank);
 else       distance=Math.abs(exactRank - to);
    }
    double epsilon=(double)distance / (double)N;
    return epsilon;
  }
  /** 
 * This method was created in VisualAge.
 * @return double[]
 * @param values cern.it.hepodbms.primitivearray.DoubleArrayList
 * @param phis double[]
 */
  public static DoubleArrayList observedEpsilonsAtPhis(  DoubleArrayList phis,  ExactDoubleQuantileFinder exactFinder,  DoubleQuantileFinder approxFinder,  double desiredEpsilon){
    DoubleArrayList epsilons=new DoubleArrayList(phis.size());
    for (int i=phis.size(); --i >= 0; ) {
      double epsilon=observedEpsilonAtPhi(phis.get(i),exactFinder,approxFinder);
      epsilons.add(epsilon);
      if (epsilon > desiredEpsilon)       System.out.println("Real epsilon = " + epsilon + " is larger than desired by "+ (epsilon - desiredEpsilon));
    }
    return epsilons;
  }
  /** 
 * Not yet commented.
 */
  public static void test(){
    String[] args=new String[20];
    String size="10000";
    args[0]=size;
    String b="12";
    args[1]=b;
    String k="2290";
    args[2]=k;
    String enableLogging="log";
    args[3]=enableLogging;
    String chunks="10";
    args[4]=chunks;
    String computeExactQuantilesAlso="exact";
    args[5]=computeExactQuantilesAlso;
    String doShuffle="shuffle";
    args[6]=doShuffle;
    String epsilon="0.001";
    args[7]=epsilon;
    String delta="0.0001";
    args[8]=delta;
    String quantiles="1";
    args[9]=quantiles;
    String max_N="-1";
    args[10]=max_N;
    testQuantileCalculation(args);
  }
  /** 
 * This method was created in VisualAge.
 */
  public static void testBestBandKCalculation(  String[] args){
    int[] quantiles={100,10000};
    long[] sizes={Long.MAX_VALUE,1000000,10000000,100000000};
    double[] deltas={0.0,0.1,0.00001};
    double[] epsilons={0.0,0.1,0.01,0.001,0.0001,0.00001,0.000001};
    System.out.println("\n\n");
    System.out.println("mem [Math.round(elements/1000.0)]");
    System.out.println("***********************************");
    Timer timer=new Timer().start();
    for (int q=0; q < quantiles.length; q++) {
      int p=quantiles[q];
      System.out.println("------------------------------");
      System.out.println("computing for p = " + p);
      for (int s=0; s < sizes.length; s++) {
        long N=sizes[s];
        System.out.println("   ------------------------------");
        System.out.println("   computing for N = " + N);
        for (int e=0; e < epsilons.length; e++) {
          double epsilon=epsilons[e];
          System.out.println("      ------------------------------");
          System.out.println("      computing for e = " + epsilon);
          for (int d=0; d < deltas.length; d++) {
            double delta=deltas[d];
            for (int knownCounter=0; knownCounter < 2; knownCounter++) {
              boolean known_N;
              if (knownCounter == 0)               known_N=true;
 else               known_N=false;
              DoubleQuantileFinder finder=QuantileFinderFactory.newDoubleQuantileFinder(known_N,N,epsilon,delta,p,null);
              String knownStr=known_N ? "  known" : "unknown";
              long mem=finder.totalMemory();
              if (mem == 0)               mem=N;
              System.out.print("         (known, d)=(" + knownStr + ", "+ delta+ ") --> ");
              System.out.print("(MB,mem");
              System.out.print(")=(" + mem * 8.0 / 1024.0 / 1024.0 + ",  " + mem / 1000.0 + ",  " + Math.round(mem * 8.0 / 1024.0 / 1024.0));
              System.out.println(")");
            }
          }
        }
      }
    }
    timer.stop().display();
  }
  /** 
 * This method was created in VisualAge.
 */
  public static void testLocalVarDeclarationSpeed(  int size){
    System.out.println("free=" + Runtime.getRuntime().freeMemory());
    System.out.println("total=" + Runtime.getRuntime().totalMemory());
    Timer timer=new Timer().start();
    DoubleBuffer buffer;
    int val;
    double f;
    int j;
    for (int i=0; i < size; i++) {
      for (j=0; j < size; j++) {
        buffer=null;
        val=10;
        f=1.0f;
      }
    }
    System.out.println(timer.stop());
    System.out.println("free=" + Runtime.getRuntime().freeMemory());
    System.out.println("total=" + Runtime.getRuntime().totalMemory());
  }
  /** 
 */
  public static void testQuantileCalculation(  String[] args){
    int size=Integer.parseInt(args[0]);
    int b=Integer.parseInt(args[1]);
    int k=Integer.parseInt(args[2]);
    int chunks=Integer.parseInt(args[4]);
    boolean computeExactQuantilesAlso=args[5].equals("exact");
    boolean doShuffle=args[6].equals("shuffle");
    double epsilon=new Double(args[7]).doubleValue();
    double delta=new Double(args[8]).doubleValue();
    int quantiles=Integer.parseInt(args[9]);
    long max_N=Long.parseLong(args[10]);
    System.out.println("free=" + Runtime.getRuntime().freeMemory());
    System.out.println("total=" + Runtime.getRuntime().totalMemory());
    double[] phis={0.001,0.01,0.1,0.5,0.9,0.99,0.999,1.0};
    Timer timer=new Timer();
    Timer timer2=new Timer();
    DoubleQuantileFinder approxFinder;
    approxFinder=QuantileFinderFactory.newDoubleQuantileFinder(false,max_N,epsilon,delta,quantiles,null);
    System.out.println(approxFinder);
    DoubleQuantileFinder exactFinder=QuantileFinderFactory.newDoubleQuantileFinder(false,-1,0.0,delta,quantiles,null);
    System.out.println(exactFinder);
    DoubleArrayList list=new DoubleArrayList(size);
    for (int chunk=0; chunk < chunks; chunk++) {
      list.setSize(0);
      int d=chunk * size;
      timer2.start();
      for (int i=0; i < size; i++) {
        list.add((double)(i + d));
      }
      timer2.stop();
      if (doShuffle) {
        Timer timer3=new Timer().start();
        list.shuffle();
        System.out.println("shuffling took ");
        timer3.stop().display();
      }
      timer.start();
      approxFinder.addAllOf(list);
      timer.stop();
      if (computeExactQuantilesAlso) {
        exactFinder.addAllOf(list);
      }
    }
    System.out.println("list.add() took" + timer2);
    System.out.println("approxFinder.add() took" + timer);
    timer.reset().start();
    DoubleArrayList approxQuantiles=approxFinder.quantileElements(new DoubleArrayList(phis));
    timer.stop().display();
    System.out.println("Phis=" + new DoubleArrayList(phis));
    System.out.println("ApproxQuantiles=" + approxQuantiles);
    if (computeExactQuantilesAlso) {
      System.out.println("Comparing with exact quantile computation...");
      timer.reset().start();
      DoubleArrayList exactQuantiles=exactFinder.quantileElements(new DoubleArrayList(phis));
      timer.stop().display();
      System.out.println("ExactQuantiles=" + exactQuantiles);
      DoubleArrayList observedEpsilons=observedEpsilonsAtPhis(new DoubleArrayList(phis),(ExactDoubleQuantileFinder)exactFinder,approxFinder,epsilon);
      System.out.println("observedEpsilons=" + observedEpsilons);
      double element=1000.0f;
      System.out.println("exact phi(" + element + ")="+ exactFinder.phi(element));
      System.out.println("apprx phi(" + element + ")="+ approxFinder.phi(element));
      System.out.println("exact elem(phi(" + element + "))="+ exactFinder.quantileElements(new DoubleArrayList(new double[]{exactFinder.phi(element)})));
      System.out.println("apprx elem(phi(" + element + "))="+ approxFinder.quantileElements(new DoubleArrayList(new double[]{approxFinder.phi(element)})));
    }
  }
  /** 
 * Not yet commented.
 */
  public static void testRank(){
    DoubleArrayList list=new DoubleArrayList(new double[]{1.0f,5.0f,5.0f,5.0f,7.0f,10.f});
  }
}

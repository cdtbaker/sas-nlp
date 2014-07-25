package cern.colt.matrix.linalg;
import cern.colt.matrix.DoubleMatrix2D;
import EDU.oswego.cs.dl.util.concurrent.FJTask;
import EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup;
class Smp {
  protected FJTaskRunnerGroup taskGroup;
  protected int maxThreads;
  /** 
 * Constructs a new Smp using a maximum of <tt>maxThreads<tt> threads.
 */
  protected Smp(  int maxThreads){
    maxThreads=Math.max(1,maxThreads);
    this.maxThreads=maxThreads;
    if (maxThreads > 1) {
      this.taskGroup=new FJTaskRunnerGroup(maxThreads);
    }
 else {
      this.taskGroup=null;
    }
  }
  /** 
 * Clean up deamon threads, if necessary.
 */
  public void finalize(){
    if (this.taskGroup != null)     this.taskGroup.interruptAll();
  }
  protected void run(  final DoubleMatrix2D[] blocksA,  final DoubleMatrix2D[] blocksB,  final double[] results,  final Matrix2DMatrix2DFunction function){
    final FJTask[] subTasks=new FJTask[blocksA.length];
    for (int i=0; i < blocksA.length; i++) {
      final int k=i;
      subTasks[i]=new FJTask(){
        public void run(){
          double result=function.apply(blocksA[k],blocksB != null ? blocksB[k] : null);
          if (results != null)           results[k]=result;
        }
      }
;
    }
    try {
      this.taskGroup.invoke(new FJTask(){
        public void run(){
          coInvoke(subTasks);
        }
      }
);
    }
 catch (    InterruptedException exc) {
    }
  }
  protected DoubleMatrix2D[] splitBlockedNN(  DoubleMatrix2D A,  int threshold,  long flops){
    int noOfTasks=(int)Math.min(flops / threshold,this.maxThreads);
    boolean splitHoriz=(A.columns() < noOfTasks);
    int p=splitHoriz ? A.rows() : A.columns();
    noOfTasks=Math.min(p,noOfTasks);
    if (noOfTasks < 2) {
      return null;
    }
    int span=p / noOfTasks;
    final DoubleMatrix2D[] blocks=new DoubleMatrix2D[noOfTasks];
    for (int i=0; i < noOfTasks; i++) {
      final int offset=i * span;
      if (i == noOfTasks - 1)       span=p - span * i;
      final DoubleMatrix2D AA, BB, CC;
      if (!splitHoriz) {
        blocks[i]=A.viewPart(0,offset,A.rows(),span);
      }
 else {
        blocks[i]=A.viewPart(offset,0,span,A.columns());
      }
    }
    return blocks;
  }
  protected DoubleMatrix2D[][] splitBlockedNN(  DoubleMatrix2D A,  DoubleMatrix2D B,  int threshold,  long flops){
    DoubleMatrix2D[] blocksA=splitBlockedNN(A,threshold,flops);
    if (blocksA == null)     return null;
    DoubleMatrix2D[] blocksB=splitBlockedNN(B,threshold,flops);
    if (blocksB == null)     return null;
    DoubleMatrix2D[][] blocks={blocksA,blocksB};
    return blocks;
  }
  protected DoubleMatrix2D[] splitStridedNN(  DoubleMatrix2D A,  int threshold,  long flops){
    int noOfTasks=(int)Math.min(flops / threshold,this.maxThreads);
    boolean splitHoriz=(A.columns() < noOfTasks);
    int p=splitHoriz ? A.rows() : A.columns();
    noOfTasks=Math.min(p,noOfTasks);
    if (noOfTasks < 2) {
      return null;
    }
    int span=p / noOfTasks;
    final DoubleMatrix2D[] blocks=new DoubleMatrix2D[noOfTasks];
    for (int i=0; i < noOfTasks; i++) {
      final int offset=i * span;
      if (i == noOfTasks - 1)       span=p - span * i;
      final DoubleMatrix2D AA, BB, CC;
      if (!splitHoriz) {
        blocks[i]=A.viewPart(0,i,A.rows(),A.columns() - i).viewStrides(1,noOfTasks);
      }
 else {
        blocks[i]=A.viewPart(i,0,A.rows() - i,A.columns()).viewStrides(noOfTasks,1);
      }
    }
    return blocks;
  }
  /** 
 * Prints various snapshot statistics to System.out; Simply delegates to {@link EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup#stats}.
 */
  public void stats(){
    if (this.taskGroup != null)     this.taskGroup.stats();
  }
}

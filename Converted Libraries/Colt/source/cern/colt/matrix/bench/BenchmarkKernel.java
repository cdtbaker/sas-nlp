package cern.colt.matrix.bench;
/** 
 * Not yet documented.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 10-Nov-99
 */
class BenchmarkKernel {
  /** 
 * Benchmark constructor comment.
 */
  protected BenchmarkKernel(){
  }
  /** 
 * Executes procedure repeatadly until more than minSeconds have elapsed.
 */
  public static float run(  double minSeconds,  TimerProcedure procedure){
    long iter=0;
    long minMillis=(long)(minSeconds * 1000);
    long begin=System.currentTimeMillis();
    long limit=begin + minMillis;
    while (System.currentTimeMillis() < limit) {
      procedure.init();
      procedure.apply(null);
      iter++;
    }
    long end=System.currentTimeMillis();
    if (minSeconds / iter < 0.1) {
      begin=System.currentTimeMillis();
      for (long i=iter; --i >= 0; ) {
        procedure.init();
        procedure.apply(null);
      }
      end=System.currentTimeMillis();
    }
    long begin2=System.currentTimeMillis();
    int dummy=1;
    for (long i=iter; --i >= 0; ) {
      dummy*=i;
      procedure.init();
    }
    long end2=System.currentTimeMillis();
    long elapsed=(end - begin) - (end2 - begin2);
    return (float)elapsed / 1000.0f / iter;
  }
  /** 
 * Returns a String with the system's properties (vendor, version, operating system, etc.)
 */
  public static String systemInfo(){
    String[] properties={"java.vm.vendor","java.vm.version","java.vm.name","os.name","os.version","os.arch","java.version","java.vendor","java.vendor.url"};
    cern.colt.matrix.ObjectMatrix2D matrix=new cern.colt.matrix.impl.DenseObjectMatrix2D(properties.length,2);
    matrix.viewColumn(0).assign(properties);
    for (int i=0; i < properties.length; i++) {
      String value=System.getProperty(properties[i]);
      if (value == null)       value="?";
      matrix.set(i,1,value);
    }
    cern.colt.matrix.objectalgo.Formatter formatter=new cern.colt.matrix.objectalgo.Formatter();
    formatter.setPrintShape(false);
    return formatter.toString(matrix);
  }
}

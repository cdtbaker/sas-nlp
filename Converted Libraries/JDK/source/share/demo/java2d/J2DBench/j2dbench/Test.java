package j2dbench;
public abstract class Test extends Option.Enable {
  private DependentLink dependencies;
  public Test(  Group parent,  String nodeName,  String description){
    super(parent,nodeName,description,false);
  }
  public void addDependency(  Modifier mod){
    addDependency(mod,null);
  }
  public void addDependency(  Modifier mod,  Modifier.Filter filter){
    dependencies=DependentLink.add(dependencies,mod,filter);
  }
  public void addDependencies(  Group g,  boolean recursive){
    addDependencies(g,recursive,null);
  }
  public void addDependencies(  Group g,  boolean recursive,  Modifier.Filter filter){
    if (g instanceof Modifier) {
      addDependency((Modifier)g,filter);
    }
    for (Node n=g.getFirstChild(); n != null; n=n.getNext()) {
      if (n instanceof Modifier) {
        addDependency((Modifier)n,filter);
      }
 else       if (recursive && n instanceof Group) {
        addDependencies((Group)n,recursive,filter);
      }
    }
  }
  public void runTest(  TestEnvironment env){
    if (!env.isStopped() && isEnabled()) {
      dependencies.recurseAndRun(env,this);
    }
  }
  public void runOneTest(  TestEnvironment env){
    if (!env.isStopped()) {
      Result result=new Result(this);
      env.erase();
      Object ctx=initTest(env,result);
      result.setModifiers(env.getModifiers());
      try {
        runTestLoop(env,result,ctx);
      }
 catch (      Throwable t) {
        result.setError(t);
      }
      cleanupTest(env,ctx);
      if (result.getError() != null || result.getNumRuns() != 0) {
        if (J2DBench.printresults.isEnabled()) {
          result.summarize();
        }
        env.record(result);
      }
      ctx=null;
      result=null;
      env.idle();
    }
  }
  public abstract Object initTest(  TestEnvironment env,  Result result);
  public abstract void runTest(  Object context,  int numReps);
  public abstract void cleanupTest(  TestEnvironment env,  Object context);
  public void runTestLoop(  TestEnvironment env,  Result result,  Object ctx){
    runTest(ctx,1);
    int numReps=env.getRepCount();
    if (numReps == 0) {
      numReps=calibrate(env,ctx);
    }
    result.setReps(numReps);
    int numRuns=env.getRunCount();
    for (int i=0; i < numRuns; i++) {
      if (env.idle()) {
        break;
      }
      env.sync();
      env.startTiming();
      runTest(ctx,numReps);
      env.sync();
      env.stopTiming();
      result.addTime(env.getTimeMillis());
      env.flushToScreen();
    }
  }
  public int calibrate(  TestEnvironment env,  Object ctx){
    long testTime=env.getTestTime();
    int numReps=0;
    int totalReps=0;
    env.idle();
    long now=System.currentTimeMillis();
    long startTime=now;
    while (numReps < 1000 && now < startTime + 1000) {
      runTest(ctx,1);
      numReps++;
      now=System.currentTimeMillis();
    }
    env.sync();
    now=System.currentTimeMillis();
    int reps=250;
    while (now < startTime + 1000) {
      runTest(ctx,reps);
      env.sync();
      numReps+=reps;
      reps*=2;
      now=System.currentTimeMillis();
    }
    while (now < startTime + testTime) {
      int estimate=(int)(numReps * testTime / (now - startTime));
      if (estimate <= numReps) {
        estimate=numReps + 1;
      }
      runTest(ctx,estimate - numReps);
      numReps=estimate;
      env.sync();
      now=System.currentTimeMillis();
    }
    int estimate=(int)(numReps * testTime / (now - startTime));
    if (estimate < 1) {
      estimate=1;
    }
    return estimate;
  }
  static int prevw;
  public static int adjustWidth(  int w,  int h){
    int bestv=w;
    int bestw=w;
    boolean verbose=(prevw != w && J2DBench.verbose.isEnabled());
    for (int i=0; i < 3; i++) {
      int w2=w - i;
      int u=w2;
      int v=h;
      while (u > 0) {
        if (u < v) {
          int t=u;
          u=v;
          v=t;
        }
        u-=v;
      }
      if (verbose) {
        System.out.println("w = " + w2 + ", h = "+ h+ ", w % 3 == "+ (w2 % 3)+ ", gcd(w, h) = "+ v);
      }
      if (v < bestv && (w2 % 3) != 0) {
        bestv=v;
        bestw=w2;
      }
    }
    if (verbose) {
      System.out.println("using " + bestw + " (gcd = "+ bestv+ ")");
      prevw=w;
    }
    return bestw;
  }
  public String toString(){
    return "Test(" + getTreeName() + ")";
  }
public static class DependentLink {
    public static DependentLink add(    DependentLink d,    Modifier mod,    Modifier.Filter filter){
      DependentLink dl=new DependentLink(mod,filter);
      if (d == null) {
        d=dl;
      }
 else {
        DependentLink last=d;
        while (last.next != null) {
          last=last.next;
        }
        last.next=dl;
      }
      return d;
    }
    private DependentLink next;
    private Modifier mod;
    private Modifier.Filter filter;
    private DependentLink(    Modifier mod,    Modifier.Filter filter){
      this.mod=mod;
      this.filter=filter;
    }
    public Modifier getModifier(){
      return mod;
    }
    public Modifier.Filter getFilter(){
      return filter;
    }
    public DependentLink getNext(){
      return next;
    }
    public void recurseAndRun(    TestEnvironment env,    Test test){
      Modifier.Iterator iter=mod.getIterator(env);
      while (iter.hasNext()) {
        Object val=iter.next();
        if (filter == null || filter.isCompatible(val)) {
          mod.modifyTest(env,val);
          if (next == null) {
            test.runOneTest(env);
            env.idle();
          }
 else {
            next.recurseAndRun(env,test);
          }
          mod.restoreTest(env,val);
        }
      }
    }
  }
}

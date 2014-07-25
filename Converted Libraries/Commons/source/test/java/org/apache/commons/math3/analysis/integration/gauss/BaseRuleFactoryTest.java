package org.apache.commons.math3.analysis.integration.gauss;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.junit.Assert;
/** 
 * Test for {@link BaseRuleFactory}.
 * @version $Id$
 */
public class BaseRuleFactoryTest {
  /** 
 * Tests that a given rule rule will be computed and added once to the cache
 * whatever the number of times this rule is called concurrently.
 */
  @Test public void testConcurrentCreation() throws InterruptedException, ExecutionException {
    final int numTasks=20;
    final ThreadPoolExecutor exec=new ThreadPoolExecutor(3,numTasks,1,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(2));
    final List<Future<Pair<double[],double[]>>> results=new ArrayList<Future<Pair<double[],double[]>>>();
    for (int i=0; i < numTasks; i++) {
      results.add(exec.submit(new RuleBuilder()));
    }
    for (    Future<Pair<double[],double[]>> f : results) {
      f.get();
    }
    final int n=RuleBuilder.getNumberOfCalls();
    Assert.assertEquals("Rule computation was called " + n + " times",1,n);
  }
}
class RuleBuilder implements Callable<Pair<double[],double[]>> {
  private static final DummyRuleFactory factory=new DummyRuleFactory();
  public Pair<double[],double[]> call(){
    final int dummy=2;
    return factory.getRule(dummy);
  }
  public static int getNumberOfCalls(){
    return factory.getNumberOfCalls();
  }
}
class DummyRuleFactory extends BaseRuleFactory<Double> {
  /** 
 * Rule computations counter. 
 */
  private static AtomicInteger nCalls=new AtomicInteger();
  @Override protected Pair<Double[],Double[]> computeRule(  int order){
    nCalls.getAndIncrement();
    try {
      Thread.sleep(20);
    }
 catch (    InterruptedException e) {
      Assert.fail("Unexpected interruption");
    }
    final Double[] p=new Double[order];
    final Double[] w=new Double[order];
    for (int i=0; i < order; i++) {
      p[i]=new Double(i);
      w[i]=new Double(i);
    }
    return new Pair<Double[],Double[]>(p,w);
  }
  public int getNumberOfCalls(){
    return nCalls.get();
  }
}

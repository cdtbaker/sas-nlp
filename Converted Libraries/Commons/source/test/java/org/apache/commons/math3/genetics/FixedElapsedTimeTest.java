package org.apache.commons.math3.genetics;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class FixedElapsedTimeTest {
  @Test public void testIsSatisfied(){
    final Population pop=new Population(){
      public void addChromosome(      final Chromosome chromosome){
      }
      public Chromosome getFittestChromosome(){
        return null;
      }
      public int getPopulationLimit(){
        return 0;
      }
      public int getPopulationSize(){
        return 0;
      }
      public Population nextGeneration(){
        return null;
      }
      public Iterator<Chromosome> iterator(){
        return null;
      }
    }
;
    final long start=System.nanoTime();
    final long duration=3;
    final FixedElapsedTime tec=new FixedElapsedTime(duration,TimeUnit.SECONDS);
    while (!tec.isSatisfied(pop)) {
      try {
        Thread.sleep(50);
      }
 catch (      InterruptedException e) {
      }
    }
    final long end=System.nanoTime();
    final long elapsedTime=end - start;
    final long diff=FastMath.abs(elapsedTime - TimeUnit.SECONDS.toNanos(duration));
    Assert.assertTrue(diff < TimeUnit.MILLISECONDS.toNanos(100));
  }
}

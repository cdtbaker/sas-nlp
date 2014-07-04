package org.apache.commons.math3.random;
import java.util.Random;
/** 
 * Dummy AbstractRandomGenerator concrete subclass that just wraps a
 * java.util.Random instance.  Used by AbstractRandomGeneratorTest to test
 * default implementations in AbstractRandomGenerator.
 * @version $Id: TestRandomGenerator.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class TestRandomGenerator extends AbstractRandomGenerator {
  private Random random=new Random();
  @Override public void setSeed(  long seed){
    clear();
    random.setSeed(seed);
  }
  @Override public double nextDouble(){
    return random.nextDouble();
  }
}

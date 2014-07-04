package org.apache.commons.math3.random;
/** 
 * Test cases for the AbstractRandomGenerator class.
 * @version $Id: AbstractRandomGeneratorTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class AbstractRandomGeneratorTest extends RandomGeneratorAbstractTest {
  public AbstractRandomGeneratorTest(){
    super();
  }
  @Override protected RandomGenerator makeGenerator(){
    RandomGenerator generator=new TestRandomGenerator();
    generator.setSeed(1001);
    return generator;
  }
}

package cern.jet.random.engine;
import java.util.Date;
/** 
 * Same as <tt>MersenneTwister</tt> except that method <tt>raw()</tt> returns 64 bit random numbers instead of 32 bit random numbers.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see MersenneTwister
 */
public class MersenneTwister64 extends MersenneTwister {
  /** 
 * Constructs and returns a random number generator with a default seed, which is a <b>constant</b>.
 */
  public MersenneTwister64(){
    super();
  }
  /** 
 * Constructs and returns a random number generator with the given seed.
 * @param seed should not be 0, in such a case <tt>MersenneTwister64.DEFAULT_SEED</tt> is silently substituted.
 */
  public MersenneTwister64(  int seed){
    super(seed);
  }
  /** 
 * Constructs and returns a random number generator seeded with the given date.
 * @param d typically <tt>new java.util.Date()</tt>
 */
  public MersenneTwister64(  Date d){
    super(d);
  }
  /** 
 * Returns a 64 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
  public double raw(){
    return nextDouble();
  }
}

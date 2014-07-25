package org.apache.commons.math3.random;
import java.io.Serializable;
/** 
 * This abstract class implements the WELL class of pseudo-random number generator
 * from Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto Matsumoto.
 * <p>This generator is described in a paper by Fran&ccedil;ois Panneton,
 * Pierre L'Ecuyer and Makoto Matsumoto <a
 * href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">Improved
 * Long-Period Generators Based on Linear Recurrences Modulo 2</a> ACM
 * Transactions on Mathematical Software, 32, 1 (2006). The errata for the paper
 * are in <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng-errata.txt">wellrng-errata.txt</a>.</p>
 * @see <a href="http://www.iro.umontreal.ca/~panneton/WELLRNG.html">WELL Random number generator</a>
 * @version $Id: AbstractWell.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.2
 */
public abstract class AbstractWell extends BitsStreamGenerator implements Serializable {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=-817701723016583596L;
  /** 
 * Current index in the bytes pool. 
 */
  protected int index;
  /** 
 * Bytes pool. 
 */
  protected final int[] v;
  /** 
 * Index indirection table giving for each index its predecessor taking table size into account. 
 */
  protected final int[] iRm1;
  /** 
 * Index indirection table giving for each index its second predecessor taking table size into account. 
 */
  protected final int[] iRm2;
  /** 
 * Index indirection table giving for each index the value index + m1 taking table size into account. 
 */
  protected final int[] i1;
  /** 
 * Index indirection table giving for each index the value index + m2 taking table size into account. 
 */
  protected final int[] i2;
  /** 
 * Index indirection table giving for each index the value index + m3 taking table size into account. 
 */
  protected final int[] i3;
  /** 
 * Creates a new random number generator.
 * <p>The instance is initialized using the current time plus the
 * system identity hash code of this instance as the seed.</p>
 * @param k number of bits in the pool (not necessarily a multiple of 32)
 * @param m1 first parameter of the algorithm
 * @param m2 second parameter of the algorithm
 * @param m3 third parameter of the algorithm
 */
  protected AbstractWell(  final int k,  final int m1,  final int m2,  final int m3){
    this(k,m1,m2,m3,null);
  }
  /** 
 * Creates a new random number generator using a single int seed.
 * @param k number of bits in the pool (not necessarily a multiple of 32)
 * @param m1 first parameter of the algorithm
 * @param m2 second parameter of the algorithm
 * @param m3 third parameter of the algorithm
 * @param seed the initial seed (32 bits integer)
 */
  protected AbstractWell(  final int k,  final int m1,  final int m2,  final int m3,  final int seed){
    this(k,m1,m2,m3,new int[]{seed});
  }
  /** 
 * Creates a new random number generator using an int array seed.
 * @param k number of bits in the pool (not necessarily a multiple of 32)
 * @param m1 first parameter of the algorithm
 * @param m2 second parameter of the algorithm
 * @param m3 third parameter of the algorithm
 * @param seed the initial seed (32 bits integers array), if null
 * the seed of the generator will be related to the current time
 */
  protected AbstractWell(  final int k,  final int m1,  final int m2,  final int m3,  final int[] seed){
    final int w=32;
    final int r=(k + w - 1) / w;
    this.v=new int[r];
    this.index=0;
    iRm1=new int[r];
    iRm2=new int[r];
    i1=new int[r];
    i2=new int[r];
    i3=new int[r];
    for (int j=0; j < r; ++j) {
      iRm1[j]=(j + r - 1) % r;
      iRm2[j]=(j + r - 2) % r;
      i1[j]=(j + m1) % r;
      i2[j]=(j + m2) % r;
      i3[j]=(j + m3) % r;
    }
    setSeed(seed);
  }
  /** 
 * Creates a new random number generator using a single long seed.
 * @param k number of bits in the pool (not necessarily a multiple of 32)
 * @param m1 first parameter of the algorithm
 * @param m2 second parameter of the algorithm
 * @param m3 third parameter of the algorithm
 * @param seed the initial seed (64 bits integer)
 */
  protected AbstractWell(  final int k,  final int m1,  final int m2,  final int m3,  final long seed){
    this(k,m1,m2,m3,new int[]{(int)(seed >>> 32),(int)(seed & 0xffffffffl)});
  }
  /** 
 * Reinitialize the generator as if just built with the given int seed.
 * <p>The state of the generator is exactly the same as a new
 * generator built with the same seed.</p>
 * @param seed the initial seed (32 bits integer)
 */
  @Override public void setSeed(  final int seed){
    setSeed(new int[]{seed});
  }
  /** 
 * Reinitialize the generator as if just built with the given int array seed.
 * <p>The state of the generator is exactly the same as a new
 * generator built with the same seed.</p>
 * @param seed the initial seed (32 bits integers array). If null
 * the seed of the generator will be the system time plus the system identity
 * hash code of the instance.
 */
  @Override public void setSeed(  final int[] seed){
    if (seed == null) {
      setSeed(System.currentTimeMillis() + System.identityHashCode(this));
      return;
    }
    System.arraycopy(seed,0,v,0,Math.min(seed.length,v.length));
    if (seed.length < v.length) {
      for (int i=seed.length; i < v.length; ++i) {
        final long l=v[i - seed.length];
        v[i]=(int)((1812433253l * (l ^ (l >> 30)) + i) & 0xffffffffL);
      }
    }
    index=0;
    clear();
  }
  /** 
 * Reinitialize the generator as if just built with the given long seed.
 * <p>The state of the generator is exactly the same as a new
 * generator built with the same seed.</p>
 * @param seed the initial seed (64 bits integer)
 */
  @Override public void setSeed(  final long seed){
    setSeed(new int[]{(int)(seed >>> 32),(int)(seed & 0xffffffffl)});
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected abstract int next(  final int bits);
}

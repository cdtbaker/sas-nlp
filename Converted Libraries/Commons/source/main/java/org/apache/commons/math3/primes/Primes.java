package org.apache.commons.math3.primes;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.List;
/** 
 * Methods related to prime numbers in the range of <code>int</code>:
 * <ul>
 * <li>primality test</li>
 * <li>prime number generation</li>
 * <li>factorization</li>
 * </ul>
 * @version $Id: Primes.java 1462702 2013-03-30 04:45:52Z psteitz $
 * @since 3.2
 */
public class Primes {
  /** 
 * Hide utility class.
 */
  private Primes(){
  }
  /** 
 * Primality test: tells if the argument is a (provable) prime or not.
 * <p>
 * It uses the Miller-Rabin probabilistic test in such a way that a result is guaranteed:
 * it uses the firsts prime numbers as successive base (see Handbook of applied cryptography
 * by Menezes, table 4.1).
 * @param n number to test.
 * @return true if n is prime. (All numbers &lt; 2 return false).
 */
  public static boolean isPrime(  int n){
    if (n < 2) {
      return false;
    }
    for (    int p : SmallPrimes.PRIMES) {
      if (0 == (n % p)) {
        return n == p;
      }
    }
    return SmallPrimes.millerRabinPrimeTest(n);
  }
  /** 
 * Return the smallest prime greater than or equal to n.
 * @param n a positive number.
 * @return the smallest prime greater than or equal to n.
 * @throws MathIllegalArgumentException if n &lt; 0.
 */
  public static int nextPrime(  int n){
    if (n < 0) {
      throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL,n,0);
    }
    if (n == 2) {
      return 2;
    }
    n=n | 1;
    if (n == 1) {
      return 2;
    }
    if (isPrime(n)) {
      return n;
    }
    final int rem=n % 3;
    if (0 == rem) {
      n+=2;
    }
 else     if (1 == rem) {
      n+=4;
    }
    while (true) {
      if (isPrime(n)) {
        return n;
      }
      n+=2;
      if (isPrime(n)) {
        return n;
      }
      n+=4;
    }
  }
  /** 
 * Prime factors decomposition
 * @param n number to factorize: must be &ge; 2
 * @return list of prime factors of n
 * @throws MathIllegalArgumentException if n &lt; 2.
 */
  public static List<Integer> primeFactors(  int n){
    if (n < 2) {
      throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL,n,2);
    }
    return SmallPrimes.trialDivision(n);
  }
}

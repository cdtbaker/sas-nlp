package sun.security.provider;
import java.math.BigInteger;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.DSAParameterSpec;
/** 
 * This class generates parameters for the DSA algorithm. It uses a default
 * prime modulus size of 1024 bits, which can be overwritten during
 * initialization.
 * @author Jan Luehe
 * @see java.security.AlgorithmParameters
 * @see java.security.spec.AlgorithmParameterSpec
 * @see DSAParameters
 * @since 1.2
 */
public class DSAParameterGenerator extends AlgorithmParameterGeneratorSpi {
  private int modLen=1024;
  private SecureRandom random;
  private static final BigInteger ZERO=BigInteger.valueOf(0);
  private static final BigInteger ONE=BigInteger.valueOf(1);
  private static final BigInteger TWO=BigInteger.valueOf(2);
  private SHA sha;
  public DSAParameterGenerator(){
    this.sha=new SHA();
  }
  /** 
 * Initializes this parameter generator for a certain strength
 * and source of randomness.
 * @param strength the strength (size of prime) in bits
 * @param random the source of randomness
 */
  protected void engineInit(  int strength,  SecureRandom random){
    if ((strength < 512) || (strength > 1024) || (strength % 64 != 0)) {
      throw new InvalidParameterException("Prime size must range from 512 to 1024 " + "and be a multiple of 64");
    }
    this.modLen=strength;
    this.random=random;
  }
  /** 
 * Initializes this parameter generator with a set of
 * algorithm-specific parameter generation values.
 * @param params the set of algorithm-specific parameter generation values
 * @param random the source of randomness
 * @exception InvalidAlgorithmParameterException if the given parameter
 * generation values are inappropriate for this parameter generator
 */
  protected void engineInit(  AlgorithmParameterSpec genParamSpec,  SecureRandom random) throws InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("Invalid parameter");
  }
  /** 
 * Generates the parameters.
 * @return the new AlgorithmParameters object
 */
  protected AlgorithmParameters engineGenerateParameters(){
    AlgorithmParameters algParams=null;
    try {
      if (this.random == null) {
        this.random=new SecureRandom();
      }
      BigInteger[] pAndQ=generatePandQ(this.random,this.modLen);
      BigInteger paramP=pAndQ[0];
      BigInteger paramQ=pAndQ[1];
      BigInteger paramG=generateG(paramP,paramQ);
      DSAParameterSpec dsaParamSpec=new DSAParameterSpec(paramP,paramQ,paramG);
      algParams=AlgorithmParameters.getInstance("DSA","SUN");
      algParams.init(dsaParamSpec);
    }
 catch (    InvalidParameterSpecException e) {
      throw new RuntimeException(e.getMessage());
    }
catch (    NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    }
catch (    NoSuchProviderException e) {
      throw new RuntimeException(e.getMessage());
    }
    return algParams;
  }
  BigInteger[] generatePandQ(  SecureRandom random,  int L){
    BigInteger[] result=null;
    byte[] seed=new byte[20];
    while (result == null) {
      for (int i=0; i < 20; i++) {
        seed[i]=(byte)random.nextInt();
      }
      result=generatePandQ(seed,L);
    }
    return result;
  }
  BigInteger[] generatePandQ(  byte[] seed,  int L){
    int g=seed.length * 8;
    int n=(L - 1) / 160;
    int b=(L - 1) % 160;
    BigInteger SEED=new BigInteger(1,seed);
    BigInteger TWOG=TWO.pow(2 * g);
    byte[] U1=SHA(seed);
    byte[] U2=SHA(toByteArray((SEED.add(ONE)).mod(TWOG)));
    xor(U1,U2);
    byte[] U=U1;
    U[0]|=0x80;
    U[19]|=1;
    BigInteger q=new BigInteger(1,U);
    if (!q.isProbablePrime(80)) {
      return null;
    }
 else {
      BigInteger V[]=new BigInteger[n + 1];
      BigInteger offset=TWO;
      for (int counter=0; counter < 4096; counter++) {
        for (int k=0; k <= n; k++) {
          BigInteger K=BigInteger.valueOf(k);
          BigInteger tmp=(SEED.add(offset).add(K)).mod(TWOG);
          V[k]=new BigInteger(1,SHA(toByteArray(tmp)));
        }
        BigInteger W=V[0];
        for (int i=1; i < n; i++) {
          W=W.add(V[i].multiply(TWO.pow(i * 160)));
        }
        W=W.add((V[n].mod(TWO.pow(b))).multiply(TWO.pow(n * 160)));
        BigInteger TWOLm1=TWO.pow(L - 1);
        BigInteger X=W.add(TWOLm1);
        BigInteger c=X.mod(q.multiply(TWO));
        BigInteger p=X.subtract(c.subtract(ONE));
        if (p.compareTo(TWOLm1) > -1 && p.isProbablePrime(80)) {
          BigInteger[] result={p,q,SEED,BigInteger.valueOf(counter)};
          return result;
        }
        offset=offset.add(BigInteger.valueOf(n)).add(ONE);
      }
      return null;
    }
  }
  BigInteger generateG(  BigInteger p,  BigInteger q){
    BigInteger h=ONE;
    BigInteger pMinusOneOverQ=(p.subtract(ONE)).divide(q);
    BigInteger g=ONE;
    while (g.compareTo(TWO) < 0) {
      g=h.modPow(pMinusOneOverQ,p);
      h=h.add(ONE);
    }
    return g;
  }
  private byte[] SHA(  byte[] array){
    sha.engineReset();
    sha.engineUpdate(array,0,array.length);
    return sha.engineDigest();
  }
  private byte[] toByteArray(  BigInteger bigInt){
    byte[] result=bigInt.toByteArray();
    if (result[0] == 0) {
      byte[] tmp=new byte[result.length - 1];
      System.arraycopy(result,1,tmp,0,tmp.length);
      result=tmp;
    }
    return result;
  }
  private void xor(  byte[] U1,  byte[] U2){
    for (int i=0; i < U1.length; i++) {
      U1[i]^=U2[i];
    }
  }
}

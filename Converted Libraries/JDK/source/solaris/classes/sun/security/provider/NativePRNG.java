package sun.security.provider;
import java.io.*;
import java.security.*;
import java.security.SecureRandom;
/** 
 * Native PRNG implementation for Solaris/Linux. It interacts with
 * /dev/random and /dev/urandom, so it is only available if those
 * files are present. Otherwise, SHA1PRNG is used instead of this class.
 * getSeed() and setSeed() directly read/write /dev/random. However,
 * /dev/random is only writable by root in many configurations. Because
 * we cannot just ignore bytes specified via setSeed(), we keep a
 * SHA1PRNG around in parallel.
 * nextBytes() reads the bytes directly from /dev/urandom (and then
 * mixes them with bytes from the SHA1PRNG for the reasons explained
 * above). Reading bytes from /dev/urandom means that constantly get
 * new entropy the operating system has collected. This is a notable
 * advantage over the SHA1PRNG model, which acquires entropy only
 * initially during startup although the VM may be running for months.
 * Also note that we do not need any initial pure random seed from
 * /dev/random. This is an advantage because on some versions of Linux
 * it can be exhausted very quickly and could thus impact startup time.
 * Finally, note that we use a singleton for the actual work (RandomIO)
 * to avoid having to open and close /dev/[u]random constantly. However,
 * there may me many NativePRNG instances created by the JCA framework.
 * @since   1.5
 * @author  Andreas Sterbenz
 */
public final class NativePRNG extends SecureRandomSpi {
  private static final long serialVersionUID=-6599091113397072932L;
  private static final String NAME_RANDOM="/dev/random";
  private static final String NAME_URANDOM="/dev/urandom";
  private static final RandomIO INSTANCE=initIO();
  private static RandomIO initIO(){
    return AccessController.doPrivileged(new PrivilegedAction<RandomIO>(){
      public RandomIO run(){
        File randomFile=new File(NAME_RANDOM);
        if (randomFile.exists() == false) {
          return null;
        }
        File urandomFile=new File(NAME_URANDOM);
        if (urandomFile.exists() == false) {
          return null;
        }
        try {
          return new RandomIO(randomFile,urandomFile);
        }
 catch (        Exception e) {
          return null;
        }
      }
    }
);
  }
  static boolean isAvailable(){
    return INSTANCE != null;
  }
  public NativePRNG(){
    super();
    if (INSTANCE == null) {
      throw new AssertionError("NativePRNG not available");
    }
  }
  protected void engineSetSeed(  byte[] seed){
    INSTANCE.implSetSeed(seed);
  }
  protected void engineNextBytes(  byte[] bytes){
    INSTANCE.implNextBytes(bytes);
  }
  protected byte[] engineGenerateSeed(  int numBytes){
    return INSTANCE.implGenerateSeed(numBytes);
  }
  /** 
 * Nested class doing the actual work. Singleton, see INSTANCE above.
 */
private static class RandomIO {
    private final static long MAX_BUFFER_TIME=100;
    private final static int BUFFER_SIZE=32;
    private final InputStream randomIn, urandomIn;
    private OutputStream randomOut;
    private boolean randomOutInitialized;
    private volatile sun.security.provider.SecureRandom mixRandom;
    private final byte[] urandomBuffer;
    private int buffered;
    private long lastRead;
    private final Object LOCK_GET_BYTES=new Object();
    private final Object LOCK_GET_SEED=new Object();
    private final Object LOCK_SET_SEED=new Object();
    private RandomIO(    File randomFile,    File urandomFile) throws IOException {
      randomIn=new FileInputStream(randomFile);
      urandomIn=new FileInputStream(urandomFile);
      urandomBuffer=new byte[BUFFER_SIZE];
    }
    private sun.security.provider.SecureRandom getMixRandom(){
      sun.security.provider.SecureRandom r=mixRandom;
      if (r == null) {
synchronized (LOCK_GET_BYTES) {
          r=mixRandom;
          if (r == null) {
            r=new sun.security.provider.SecureRandom();
            try {
              byte[] b=new byte[20];
              readFully(urandomIn,b);
              r.engineSetSeed(b);
            }
 catch (            IOException e) {
              throw new ProviderException("init failed",e);
            }
            mixRandom=r;
          }
        }
      }
      return r;
    }
    private static void readFully(    InputStream in,    byte[] data) throws IOException {
      int len=data.length;
      int ofs=0;
      while (len > 0) {
        int k=in.read(data,ofs,len);
        if (k <= 0) {
          throw new EOFException("/dev/[u]random closed?");
        }
        ofs+=k;
        len-=k;
      }
      if (len > 0) {
        throw new IOException("Could not read from /dev/[u]random");
      }
    }
    private byte[] implGenerateSeed(    int numBytes){
synchronized (LOCK_GET_SEED) {
        try {
          byte[] b=new byte[numBytes];
          readFully(randomIn,b);
          return b;
        }
 catch (        IOException e) {
          throw new ProviderException("generateSeed() failed",e);
        }
      }
    }
    private void implSetSeed(    byte[] seed){
synchronized (LOCK_SET_SEED) {
        if (randomOutInitialized == false) {
          randomOutInitialized=true;
          randomOut=AccessController.doPrivileged(new PrivilegedAction<OutputStream>(){
            public OutputStream run(){
              try {
                return new FileOutputStream(NAME_RANDOM,true);
              }
 catch (              Exception e) {
                return null;
              }
            }
          }
);
        }
        if (randomOut != null) {
          try {
            randomOut.write(seed);
          }
 catch (          IOException e) {
            throw new ProviderException("setSeed() failed",e);
          }
        }
        getMixRandom().engineSetSeed(seed);
      }
    }
    private void ensureBufferValid() throws IOException {
      long time=System.currentTimeMillis();
      if ((buffered > 0) && (time - lastRead < MAX_BUFFER_TIME)) {
        return;
      }
      lastRead=time;
      readFully(urandomIn,urandomBuffer);
      buffered=urandomBuffer.length;
    }
    private void implNextBytes(    byte[] data){
synchronized (LOCK_GET_BYTES) {
        try {
          getMixRandom().engineNextBytes(data);
          int len=data.length;
          int ofs=0;
          while (len > 0) {
            ensureBufferValid();
            int bufferOfs=urandomBuffer.length - buffered;
            while ((len > 0) && (buffered > 0)) {
              data[ofs++]^=urandomBuffer[bufferOfs++];
              len--;
              buffered--;
            }
          }
        }
 catch (        IOException e) {
          throw new ProviderException("nextBytes() failed",e);
        }
      }
    }
  }
}

package sun.nio.cs;
public interface ArrayEncoder {
  int encode(  char[] src,  int off,  int len,  byte[] dst);
}

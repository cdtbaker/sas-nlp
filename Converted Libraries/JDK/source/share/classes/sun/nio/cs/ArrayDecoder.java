package sun.nio.cs;
public interface ArrayDecoder {
  int decode(  byte[] src,  int off,  int len,  char[] dst);
}

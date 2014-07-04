package com.sun.java.util.jar.pack;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.sun.java.util.jar.pack.Constants.*;
/** 
 * Adaptive coding.
 * See the section "Adaptive Encodings" in the Pack200 spec.
 * @author John Rose
 */
class AdaptiveCoding implements CodingMethod {
  CodingMethod headCoding;
  int headLength;
  CodingMethod tailCoding;
  public AdaptiveCoding(  int headLength,  CodingMethod headCoding,  CodingMethod tailCoding){
    assert (isCodableLength(headLength));
    this.headLength=headLength;
    this.headCoding=headCoding;
    this.tailCoding=tailCoding;
  }
  public void setHeadCoding(  CodingMethod headCoding){
    this.headCoding=headCoding;
  }
  public void setHeadLength(  int headLength){
    assert (isCodableLength(headLength));
    this.headLength=headLength;
  }
  public void setTailCoding(  CodingMethod tailCoding){
    this.tailCoding=tailCoding;
  }
  public boolean isTrivial(){
    return headCoding == tailCoding;
  }
  public void writeArrayTo(  OutputStream out,  int[] a,  int start,  int end) throws IOException {
    writeArray(this,out,a,start,end);
  }
  private static void writeArray(  AdaptiveCoding run,  OutputStream out,  int[] a,  int start,  int end) throws IOException {
    for (; ; ) {
      int mid=start + run.headLength;
      assert (mid <= end);
      run.headCoding.writeArrayTo(out,a,start,mid);
      start=mid;
      if (run.tailCoding instanceof AdaptiveCoding) {
        run=(AdaptiveCoding)run.tailCoding;
        continue;
      }
      break;
    }
    run.tailCoding.writeArrayTo(out,a,start,end);
  }
  public void readArrayFrom(  InputStream in,  int[] a,  int start,  int end) throws IOException {
    readArray(this,in,a,start,end);
  }
  private static void readArray(  AdaptiveCoding run,  InputStream in,  int[] a,  int start,  int end) throws IOException {
    for (; ; ) {
      int mid=start + run.headLength;
      assert (mid <= end);
      run.headCoding.readArrayFrom(in,a,start,mid);
      start=mid;
      if (run.tailCoding instanceof AdaptiveCoding) {
        run=(AdaptiveCoding)run.tailCoding;
        continue;
      }
      break;
    }
    run.tailCoding.readArrayFrom(in,a,start,end);
  }
  public static final int KX_MIN=0;
  public static final int KX_MAX=3;
  public static final int KX_LG2BASE=4;
  public static final int KX_BASE=16;
  public static final int KB_MIN=0x00;
  public static final int KB_MAX=0xFF;
  public static final int KB_OFFSET=1;
  public static final int KB_DEFAULT=3;
  static int getKXOf(  int K){
    for (int KX=KX_MIN; KX <= KX_MAX; KX++) {
      if (((K - KB_OFFSET) & ~KB_MAX) == 0)       return KX;
      K>>>=KX_LG2BASE;
    }
    return -1;
  }
  static int getKBOf(  int K){
    int KX=getKXOf(K);
    if (KX < 0)     return -1;
    K>>>=(KX * KX_LG2BASE);
    return K - 1;
  }
  static int decodeK(  int KX,  int KB){
    assert (KX_MIN <= KX && KX <= KX_MAX);
    assert (KB_MIN <= KB && KB <= KB_MAX);
    return (KB + KB_OFFSET) << (KX * KX_LG2BASE);
  }
  static int getNextK(  int K){
    if (K <= 0)     return 1;
    int KX=getKXOf(K);
    if (KX < 0)     return Integer.MAX_VALUE;
    int unit=1 << (KX * KX_LG2BASE);
    int mask=KB_MAX << (KX * KX_LG2BASE);
    int K1=K + unit;
    K1&=~(unit - 1);
    if (((K1 - unit) & ~mask) == 0) {
      assert (getKXOf(K1) == KX);
      return K1;
    }
    if (KX == KX_MAX)     return Integer.MAX_VALUE;
    KX+=1;
    int mask2=KB_MAX << (KX * KX_LG2BASE);
    K1|=(mask & ~mask2);
    K1+=unit;
    assert (getKXOf(K1) == KX);
    return K1;
  }
  public static boolean isCodableLength(  int K){
    int KX=getKXOf(K);
    if (KX < 0)     return false;
    int unit=1 << (KX * KX_LG2BASE);
    int mask=KB_MAX << (KX * KX_LG2BASE);
    return ((K - unit) & ~mask) == 0;
  }
  public byte[] getMetaCoding(  Coding dflt){
    ByteArrayOutputStream bytes=new ByteArrayOutputStream(10);
    try {
      makeMetaCoding(this,dflt,bytes);
    }
 catch (    IOException ee) {
      throw new RuntimeException(ee);
    }
    return bytes.toByteArray();
  }
  private static void makeMetaCoding(  AdaptiveCoding run,  Coding dflt,  ByteArrayOutputStream bytes) throws IOException {
    for (; ; ) {
      CodingMethod headCoding=run.headCoding;
      int headLength=run.headLength;
      CodingMethod tailCoding=run.tailCoding;
      int K=headLength;
      assert (isCodableLength(K));
      int ADef=(headCoding == dflt) ? 1 : 0;
      int BDef=(tailCoding == dflt) ? 1 : 0;
      if (ADef + BDef > 1)       BDef=0;
      int ABDef=1 * ADef + 2 * BDef;
      assert (ABDef < 3);
      int KX=getKXOf(K);
      int KB=getKBOf(K);
      assert (decodeK(KX,KB) == K);
      int KBFlag=(KB != KB_DEFAULT) ? 1 : 0;
      bytes.write(_meta_run + KX + 4 * KBFlag + 8 * ABDef);
      if (KBFlag != 0)       bytes.write(KB);
      if (ADef == 0)       bytes.write(headCoding.getMetaCoding(dflt));
      if (tailCoding instanceof AdaptiveCoding) {
        run=(AdaptiveCoding)tailCoding;
        continue;
      }
      if (BDef == 0)       bytes.write(tailCoding.getMetaCoding(dflt));
      break;
    }
  }
  public static int parseMetaCoding(  byte[] bytes,  int pos,  Coding dflt,  CodingMethod res[]){
    int op=bytes[pos++] & 0xFF;
    if (op < _meta_run || op >= _meta_pop)     return pos - 1;
    AdaptiveCoding prevc=null;
    for (boolean keepGoing=true; keepGoing; ) {
      keepGoing=false;
      assert (op >= _meta_run);
      op-=_meta_run;
      int KX=op % 4;
      int KBFlag=(op / 4) % 2;
      int ABDef=(op / 8);
      assert (ABDef < 3);
      int ADef=(ABDef & 1);
      int BDef=(ABDef & 2);
      CodingMethod[] ACode={dflt}, BCode={dflt};
      int KB=KB_DEFAULT;
      if (KBFlag != 0)       KB=bytes[pos++] & 0xFF;
      if (ADef == 0) {
        pos=BandStructure.parseMetaCoding(bytes,pos,dflt,ACode);
      }
      if (BDef == 0 && ((op=bytes[pos] & 0xFF) >= _meta_run) && op < _meta_pop) {
        pos++;
        keepGoing=true;
      }
 else       if (BDef == 0) {
        pos=BandStructure.parseMetaCoding(bytes,pos,dflt,BCode);
      }
      AdaptiveCoding newc=new AdaptiveCoding(decodeK(KX,KB),ACode[0],BCode[0]);
      if (prevc == null) {
        res[0]=newc;
      }
 else {
        prevc.tailCoding=newc;
      }
      prevc=newc;
    }
    return pos;
  }
  private String keyString(  CodingMethod m){
    if (m instanceof Coding)     return ((Coding)m).keyString();
    return m.toString();
  }
  public String toString(){
    StringBuilder res=new StringBuilder(20);
    AdaptiveCoding run=this;
    res.append("run(");
    for (; ; ) {
      res.append(run.headLength).append("*");
      res.append(keyString(run.headCoding));
      if (run.tailCoding instanceof AdaptiveCoding) {
        run=(AdaptiveCoding)run.tailCoding;
        res.append(" ");
        continue;
      }
      break;
    }
    res.append(" **").append(keyString(run.tailCoding));
    res.append(")");
    return res.toString();
  }
}

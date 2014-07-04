package org.ojalgo.netio;
public enum LineTerminator {MACINTOSH(new char[]{ASCII.CR}), UNIX(new char[]{ASCII.LF}), WINDOWS(new char[]{ASCII.CR,ASCII.LF}); private final char[] myCharArray;
LineTerminator(final char[] aCharArray){
  myCharArray=aCharArray;
}
public final char[] getCharArray(){
  return myCharArray.clone();
}
@Override public String toString(){
  return String.valueOf(myCharArray);
}
}

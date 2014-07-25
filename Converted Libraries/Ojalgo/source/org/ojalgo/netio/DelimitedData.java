package org.ojalgo.netio;
import java.util.ArrayList;
import java.util.List;
/** 
 * http://www.iana.org/assignments/media-types/
 * http://www.webmaster-toolkit.com/mime-types.shtml
 * @author apete
 */
public final class DelimitedData extends Object {
  public static DelimitedData makeCommaDelimited(){
    return new DelimitedData(ASCII.COMMA,LineTerminator.WINDOWS);
  }
  public static DelimitedData makeSemicolonDelimited(){
    return new DelimitedData(ASCII.SEMICOLON,LineTerminator.WINDOWS);
  }
  public static DelimitedData makeSpaceDelimited(){
    return new DelimitedData(ASCII.SP,LineTerminator.WINDOWS);
  }
  public static DelimitedData makeTabDelimited(){
    return new DelimitedData(ASCII.HT,LineTerminator.WINDOWS);
  }
  private final char myDelimiter;
  private final List<List<Object>> myLines;
  private final LineTerminator myTerminator;
  public DelimitedData(  char aDelimiter,  LineTerminator aTerminator){
    super();
    myDelimiter=aDelimiter;
    myTerminator=aTerminator;
    myLines=new ArrayList<List<Object>>();
  }
  public void addEmptyLines(  int aNumberOfLines,  int aNumberOfElementsOnEachLine){
    for (int i=0; i < aNumberOfLines; i++) {
      myLines.add(new ArrayList<Object>(aNumberOfElementsOnEachLine));
    }
  }
  public void addLine(  List<?> aLine){
    myLines.add((List<Object>)aLine);
  }
  /** 
 * The row and column must already exist. One way to create it is
 * to call {@linkplain #addEmptyLines(int,int)}.
 */
  public void set(  int aRowIndex,  int aColumnIndex,  Object anElement){
    myLines.get(aRowIndex).set(aColumnIndex,anElement);
  }
  public String toString(){
    StringBuilder retVal=new StringBuilder();
    for (    List<Object> tmpLine : myLines) {
      for (      Object tmpElement : tmpLine) {
        if (tmpElement != null) {
          retVal.append(tmpElement);
        }
        retVal.append(myDelimiter);
      }
      retVal.append(myTerminator);
    }
    return retVal.toString();
  }
}

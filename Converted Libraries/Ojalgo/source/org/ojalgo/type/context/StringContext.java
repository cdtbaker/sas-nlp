package org.ojalgo.type.context;
import java.text.Format;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.format.StringFormat;
/** 
 * StringContext
 * @author apete
 */
public final class StringContext extends FormatContext<String> {
  private static final Format DEFAULT_FORMAT=new StringFormat();
  private final int myLength;
  public StringContext(){
    super(DEFAULT_FORMAT);
    myLength=0;
  }
  public StringContext(  final Format format,  final int length){
    super(format);
    myLength=length;
  }
  public StringContext(  final int length){
    super(DEFAULT_FORMAT);
    myLength=length;
  }
  private StringContext(  final Format format){
    super(format);
    myLength=0;
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public String enforce(  final String object){
    String retVal=object.trim();
    final int tmpLength=retVal.length();
    if ((myLength > 1) && (tmpLength > myLength)) {
      retVal=retVal.substring(0,myLength - 1).trim() + "…";
    }
    return retVal;
  }
  @Override protected void configureFormat(  final Format format,  final Object object){
  }
  @Override protected String handleFormatException(  final Format format,  final Object object){
    return "";
  }
  @Override protected String handleParseException(  final Format format,  final String string){
    return "";
  }
}

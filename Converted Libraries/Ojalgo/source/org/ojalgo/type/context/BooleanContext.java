package org.ojalgo.type.context;
import java.text.Format;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.format.BooleanFormat;
/** 
 * BooleanContext
 * @author apete
 */
public final class BooleanContext extends FormatContext<Boolean> {
  private static final Format DEFAULT_FORMAT=new BooleanFormat();
  public BooleanContext(){
    super(DEFAULT_FORMAT);
  }
  private BooleanContext(  final Format format){
    super(format);
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public Boolean enforce(  final Boolean object){
    return object;
  }
  @Override protected void configureFormat(  final Format format,  final Object object){
  }
  @Override protected String handleFormatException(  final Format format,  final Object object){
    return "";
  }
  @Override protected Boolean handleParseException(  final Format format,  final String string){
    return false;
  }
}

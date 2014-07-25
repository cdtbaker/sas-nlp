package org.ojalgo.type.context;
import java.text.Format;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.format.BinaryFormat;
public final class BinaryContext extends FormatContext<byte[]> {
  private static final Format DEFAULT_FORMAT=new BinaryFormat();
  public BinaryContext(){
    super(DEFAULT_FORMAT);
  }
  private BinaryContext(  final Format format){
    super(format);
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public byte[] enforce(  final byte[] object){
    return object;
  }
  @Override protected void configureFormat(  final Format format,  final Object object){
  }
  @Override protected String handleFormatException(  final Format format,  final Object object){
    return "";
  }
  @Override protected byte[] handleParseException(  final Format format,  final String string){
    return new byte[]{};
  }
}

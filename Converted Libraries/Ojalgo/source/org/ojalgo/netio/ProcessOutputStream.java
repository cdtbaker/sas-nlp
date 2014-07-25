package org.ojalgo.netio;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
public class ProcessOutputStream extends PrintStream {
  private static String EMPTY="";
  private String myLastArgument=EMPTY;
  private String myLastCommand=EMPTY;
  public ProcessOutputStream(  Process aProcess){
    super(aProcess.getOutputStream(),true);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  File someFile) throws FileNotFoundException {
    super(someFile);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  File someFile,  String someCsn) throws FileNotFoundException, UnsupportedEncodingException {
    super(someFile,someCsn);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  OutputStream anOutputStream){
    super(anOutputStream);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  OutputStream someOut,  boolean autoFlush){
    super(someOut,autoFlush);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  OutputStream someOut,  boolean autoFlush,  String someEncoding) throws UnsupportedEncodingException {
    super(someOut,autoFlush,someEncoding);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  String someFileName) throws FileNotFoundException {
    super(someFileName);
  }
  @SuppressWarnings("unused") private ProcessOutputStream(  String someFileName,  String someCsn) throws FileNotFoundException, UnsupportedEncodingException {
    super(someFileName,someCsn);
  }
  public String getLastArgument(){
    return myLastArgument;
  }
  public String getLastCommand(){
    return myLastCommand;
  }
  public void sendBatch(  List<Message> aBatch){
    StringBuilder retVal=new StringBuilder();
    Message tmpMessage=aBatch.get(0);
    retVal.append(tmpMessage);
    for (int i=1; i < aBatch.size(); i++) {
      tmpMessage=aBatch.get(i);
      retVal.append(ASCII.LF);
      retVal.append(tmpMessage.toString());
    }
    myLastCommand=tmpMessage.getCommand();
    myLastArgument=tmpMessage.getArgument();
    this.writeString(retVal.toString());
  }
  public void sendMessage(  Message aMessage){
    myLastCommand=aMessage.getCommand();
    myLastArgument=aMessage.getArgument();
    this.writeString(aMessage.toString());
  }
  private void writeString(  String aString){
    this.print(aString.length() + String.valueOf(ASCII.SP) + aString);
  }
}

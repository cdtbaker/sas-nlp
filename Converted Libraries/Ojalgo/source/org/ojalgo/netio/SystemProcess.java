package org.ojalgo.netio;
import java.io.IOException;
import java.util.List;
import org.ojalgo.ProgrammingError;
public abstract class SystemProcess {
  private static String ERROR_TEXT="System command not working!";
  private ProcessInputStream myInStream;
  private ProcessOutputStream myOutStream;
  private Process myProcess;
  public SystemProcess(  final String[] aCommandArray,  final String aLastCommandPrefix){
    super();
    final Runtime tmpRuntime=Runtime.getRuntime();
    try {
      myProcess=tmpRuntime.exec(aCommandArray);
      myOutStream=new ProcessOutputStream(myProcess);
      myInStream=new ProcessInputStream(myProcess,aLastCommandPrefix);
      myInStream.collectMessages();
    }
 catch (    final IOException anException) {
      throw new ProgrammingError(ERROR_TEXT);
    }
  }
  @SuppressWarnings("unused") private SystemProcess(){
    super();
    ProgrammingError.throwForIllegalInvocation();
  }
  public List<Message> communicate(  final List<Message> aBatch){
    myOutStream.sendBatch(aBatch);
    return myInStream.collectMessages();
  }
  public List<Message> communicate(  final Message aMessage){
    myOutStream.sendMessage(aMessage);
    return myInStream.collectMessages();
  }
  public String getLastReturnArgument(){
    return myInStream.getLastArgument();
  }
  public void terminate(){
    try {
      if (myOutStream != null) {
        myOutStream.close();
      }
      if (myInStream != null) {
        myInStream.close();
      }
      if (myProcess != null) {
        myProcess.destroy();
      }
    }
 catch (    final IOException anException) {
      ;
    }
 finally {
      myOutStream=null;
      myInStream=null;
      myProcess=null;
    }
  }
}

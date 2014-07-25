package org.ojalgo.netio;
import java.util.List;
import org.ojalgo.array.ArrayUtils;
public class Message {
  private static String EMPTY="";
  public static String toString(  final List<Message> aCollection){
    final StringBuilder retVal=new StringBuilder();
    if (aCollection.size() >= 1) {
      retVal.append(aCollection.get(0));
      for (int i=1; i < aCollection.size(); i++) {
        retVal.append(ASCII.LF);
        retVal.append(aCollection.get(i));
      }
    }
    return retVal.toString();
  }
  private final String[] myCommand;
  public Message(  final String aCommand){
    super();
    myCommand=new String[]{aCommand};
  }
  public Message(  final String aCommand,  final String anArgument){
    super();
    myCommand=new String[]{aCommand,anArgument};
  }
  public Message(  final String aCommand,  final String anArgument,  final String aParameter){
    super();
    myCommand=new String[]{aCommand,anArgument,aParameter};
  }
  public Message(  final String[] aCommand){
    super();
    myCommand=ArrayUtils.copyOf(aCommand);
  }
  @SuppressWarnings("unused") private Message(){
    this(EMPTY);
  }
  public String getArgument(){
    final StringBuilder retVal=new StringBuilder();
    if (myCommand.length >= 2) {
      retVal.append(myCommand[1]);
      for (int i=2; i < myCommand.length; i++) {
        retVal.append(ASCII.SP);
        retVal.append(myCommand[i]);
      }
    }
    return retVal.toString();
  }
  public String getCommand(){
    return myCommand[0];
  }
  @Override public String toString(){
    final StringBuilder retVal=new StringBuilder();
    retVal.append(myCommand[0]);
    for (int i=1; i < myCommand.length; i++) {
      retVal.append(ASCII.SP);
      retVal.append(myCommand[i]);
    }
    retVal.append(ASCII.SEMICOLON);
    return retVal.toString();
  }
}

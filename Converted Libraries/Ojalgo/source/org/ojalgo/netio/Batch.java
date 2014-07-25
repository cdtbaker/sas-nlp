package org.ojalgo.netio;
import java.util.ArrayList;
import java.util.Collection;
public class Batch extends ArrayList<Message> {
  public Batch(){
    super();
  }
  public Batch(  Collection<? extends Message> aCollection){
    super(aCollection);
  }
  public Batch(  int anInitialCapacity){
    super(anInitialCapacity);
  }
  public boolean add(  String aCommand){
    return super.add(new Message(aCommand));
  }
  public boolean add(  String aCommand,  String anArgument){
    return super.add(new Message(aCommand,anArgument));
  }
  public boolean add(  String aCommand,  String anArgument,  String aParameter){
    return super.add(new Message(aCommand,anArgument,aParameter));
  }
  public boolean add(  String[] aCommand){
    return super.add(new Message(aCommand));
  }
  @Override public String toString(){
    return Message.toString(this);
  }
}

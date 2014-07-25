package com.sun.tools.example.debug.bdi;
import java.util.EventObject;
import com.sun.jdi.request.EventRequest;
public class SpecEvent extends EventObject {
  private static final long serialVersionUID=4820735456787276230L;
  private EventRequestSpec eventRequestSpec;
  public SpecEvent(  EventRequestSpec eventRequestSpec){
    super(eventRequestSpec.specs);
    this.eventRequestSpec=eventRequestSpec;
  }
  public EventRequestSpec getEventRequestSpec(){
    return eventRequestSpec;
  }
  public EventRequest getEventRequest(){
    return eventRequestSpec.getEventRequest();
  }
}

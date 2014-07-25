package sun.net.httpserver;
import com.sun.net.httpserver.*;
import com.sun.net.httpserver.spi.*;
class Event {
  ExchangeImpl exchange;
  protected Event(  ExchangeImpl t){
    this.exchange=t;
  }
}
class WriteFinishedEvent extends Event {
  WriteFinishedEvent(  ExchangeImpl t){
    super(t);
    assert !t.writefinished;
    t.writefinished=true;
  }
}

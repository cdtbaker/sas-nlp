package org.ojalgo.type;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class ScheduleBuilder {
  private final TimerTask myTask;
  private Date myStartDate=null;
  private int myRepetitionMeassure;
  private CalendarDateUnit myRepetitionUnit=null;
  public ScheduleBuilder(  final TimerTask aTask){
    super();
    myTask=aTask;
  }
  @SuppressWarnings("unused") private ScheduleBuilder(){
    this(null);
  }
  public ScheduleBuilder repetition(  final int aRepetitionMeassure,  final CalendarDateUnit aRepetitionUnit){
    myRepetitionMeassure=aRepetitionMeassure;
    myRepetitionUnit=aRepetitionUnit;
    return this;
  }
  public void schedule(  final Timer aTimer){
    if (myStartDate != null) {
      if (myRepetitionUnit != null) {
        aTimer.scheduleAtFixedRate(myTask,myStartDate,myRepetitionMeassure * myRepetitionUnit.size());
      }
 else {
        aTimer.schedule(myTask,myStartDate);
      }
    }
 else {
      if (myRepetitionUnit != null) {
        aTimer.scheduleAtFixedRate(myTask,new Date(),myRepetitionMeassure * myRepetitionUnit.size());
      }
 else {
        aTimer.schedule(myTask,new Date());
      }
    }
  }
  public ScheduleBuilder start(  final Date aStartDate){
    myStartDate=new Date(aStartDate.getTime());
    return this;
  }
  public ScheduleBuilder start(  final int aDelayMeassure,  final CalendarDateUnit aDelayUnit){
    myStartDate=new Date(System.currentTimeMillis() + aDelayMeassure * aDelayUnit.size());
    return this;
  }
}

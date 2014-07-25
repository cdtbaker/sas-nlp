package javax.management.timer;
/** 
 * <p>Definitions of the notifications sent by TimerAlarmClock
 * MBeans.</p>
 */
class TimerAlarmClockNotification extends javax.management.Notification {
  private static final long serialVersionUID=-4841061275673620641L;
  /** 
 * Constructor.
 * @param source the source.
 */
  public TimerAlarmClockNotification(  TimerAlarmClock source){
    super("",source,0);
  }
}

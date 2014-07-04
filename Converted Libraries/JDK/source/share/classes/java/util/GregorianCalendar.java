package java.util;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.JulianCalendar;
import sun.util.calendar.ZoneInfo;
/** 
 * <code>GregorianCalendar</code> is a concrete subclass of
 * <code>Calendar</code> and provides the standard calendar system
 * used by most of the world.
 * <p> <code>GregorianCalendar</code> is a hybrid calendar that
 * supports both the Julian and Gregorian calendar systems with the
 * support of a single discontinuity, which corresponds by default to
 * the Gregorian date when the Gregorian calendar was instituted
 * (October 15, 1582 in some countries, later in others).  The cutover
 * date may be changed by the caller by calling {@link #setGregorianChange(Date) setGregorianChange()}.
 * <p>
 * Historically, in those countries which adopted the Gregorian calendar first,
 * October 4, 1582 (Julian) was thus followed by October 15, 1582 (Gregorian). This calendar models
 * this correctly.  Before the Gregorian cutover, <code>GregorianCalendar</code>
 * implements the Julian calendar.  The only difference between the Gregorian
 * and the Julian calendar is the leap year rule. The Julian calendar specifies
 * leap years every four years, whereas the Gregorian calendar omits century
 * years which are not divisible by 400.
 * <p>
 * <code>GregorianCalendar</code> implements <em>proleptic</em> Gregorian and
 * Julian calendars. That is, dates are computed by extrapolating the current
 * rules indefinitely far backward and forward in time. As a result,
 * <code>GregorianCalendar</code> may be used for all years to generate
 * meaningful and consistent results. However, dates obtained using
 * <code>GregorianCalendar</code> are historically accurate only from March 1, 4
 * AD onward, when modern Julian calendar rules were adopted.  Before this date,
 * leap year rules were applied irregularly, and before 45 BC the Julian
 * calendar did not even exist.
 * <p>
 * Prior to the institution of the Gregorian calendar, New Year's Day was
 * March 25. To avoid confusion, this calendar always uses January 1. A manual
 * adjustment may be made if desired for dates that are prior to the Gregorian
 * changeover and which fall between January 1 and March 24.
 * <h4><a name="week_and_year">Week Of Year and Week Year</a></h4>
 * <p>Values calculated for the {@link Calendar#WEEK_OF_YEARWEEK_OF_YEAR} field range from 1 to 53. The first week of a
 * calendar year is the earliest seven day period starting on {@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek()} that contains at
 * least {@link Calendar#getMinimalDaysInFirstWeek()getMinimalDaysInFirstWeek()} days from that year. It thus depends
 * on the values of {@code getMinimalDaysInFirstWeek()}, {@codegetFirstDayOfWeek()}, and the day of the week of January 1. Weeks
 * between week 1 of one year and week 1 of the following year
 * (exclusive) are numbered sequentially from 2 to 52 or 53 (except
 * for year(s) involved in the Julian-Gregorian transition).
 * <p>The {@code getFirstDayOfWeek()} and {@codegetMinimalDaysInFirstWeek()} values are initialized using
 * locale-dependent resources when constructing a {@codeGregorianCalendar}. <a name="iso8601_compatible_setting">The week
 * determination is compatible</a> with the ISO 8601 standard when {@codegetFirstDayOfWeek()} is {@code MONDAY} and {@codegetMinimalDaysInFirstWeek()} is 4, which values are used in locales
 * where the standard is preferred. These values can explicitly be set by
 * calling {@link Calendar#setFirstDayOfWeek(int) setFirstDayOfWeek()} and{@link Calendar#setMinimalDaysInFirstWeek(int)setMinimalDaysInFirstWeek()}.
 * <p>A <a name="week_year"><em>week year</em></a> is in sync with a{@code WEEK_OF_YEAR} cycle. All weeks between the first and last
 * weeks (inclusive) have the same <em>week year</em> value.
 * Therefore, the first and last days of a week year may have
 * different calendar year values.
 * <p>For example, January 1, 1998 is a Thursday. If {@codegetFirstDayOfWeek()} is {@code MONDAY} and {@codegetMinimalDaysInFirstWeek()} is 4 (ISO 8601 standard compatible
 * setting), then week 1 of 1998 starts on December 29, 1997, and ends
 * on January 4, 1998. The week year is 1998 for the last three days
 * of calendar year 1997. If, however, {@code getFirstDayOfWeek()} is{@code SUNDAY}, then week 1 of 1998 starts on January 4, 1998, and
 * ends on January 10, 1998; the first three days of 1998 then are
 * part of week 53 of 1997 and their week year is 1997.
 * <h4>Week Of Month</h4>
 * <p>Values calculated for the <code>WEEK_OF_MONTH</code> field range from 0
 * to 6.  Week 1 of a month (the days with <code>WEEK_OF_MONTH =
 * 1</code>) is the earliest set of at least
 * <code>getMinimalDaysInFirstWeek()</code> contiguous days in that month,
 * ending on the day before <code>getFirstDayOfWeek()</code>.  Unlike
 * week 1 of a year, week 1 of a month may be shorter than 7 days, need
 * not start on <code>getFirstDayOfWeek()</code>, and will not include days of
 * the previous month.  Days of a month before week 1 have a
 * <code>WEEK_OF_MONTH</code> of 0.
 * <p>For example, if <code>getFirstDayOfWeek()</code> is <code>SUNDAY</code>
 * and <code>getMinimalDaysInFirstWeek()</code> is 4, then the first week of
 * January 1998 is Sunday, January 4 through Saturday, January 10.  These days
 * have a <code>WEEK_OF_MONTH</code> of 1.  Thursday, January 1 through
 * Saturday, January 3 have a <code>WEEK_OF_MONTH</code> of 0.  If
 * <code>getMinimalDaysInFirstWeek()</code> is changed to 3, then January 1
 * through January 3 have a <code>WEEK_OF_MONTH</code> of 1.
 * <h4>Default Fields Values</h4>
 * <p>The <code>clear</code> method sets calendar field(s)
 * undefined. <code>GregorianCalendar</code> uses the following
 * default value for each calendar field if its value is undefined.
 * <table cellpadding="0" cellspacing="3" border="0"
 * summary="GregorianCalendar default field values"
 * style="text-align: left; width: 66%;">
 * <tbody>
 * <tr>
 * <th style="vertical-align: top; background-color: rgb(204, 204, 255);
 * text-align: center;">Field<br>
 * </th>
 * <th style="vertical-align: top; background-color: rgb(204, 204, 255);
 * text-align: center;">Default Value<br>
 * </th>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle;">
 * <code>ERA<br></code>
 * </td>
 * <td style="vertical-align: middle;">
 * <code>AD<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle; background-color: rgb(238, 238, 255);">
 * <code>YEAR<br></code>
 * </td>
 * <td style="vertical-align: middle; background-color: rgb(238, 238, 255);">
 * <code>1970<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle;">
 * <code>MONTH<br></code>
 * </td>
 * <td style="vertical-align: middle;">
 * <code>JANUARY<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: top; background-color: rgb(238, 238, 255);">
 * <code>DAY_OF_MONTH<br></code>
 * </td>
 * <td style="vertical-align: top; background-color: rgb(238, 238, 255);">
 * <code>1<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle;">
 * <code>DAY_OF_WEEK<br></code>
 * </td>
 * <td style="vertical-align: middle;">
 * <code>the first day of week<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: top; background-color: rgb(238, 238, 255);">
 * <code>WEEK_OF_MONTH<br></code>
 * </td>
 * <td style="vertical-align: top; background-color: rgb(238, 238, 255);">
 * <code>0<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: top;">
 * <code>DAY_OF_WEEK_IN_MONTH<br></code>
 * </td>
 * <td style="vertical-align: top;">
 * <code>1<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle; background-color: rgb(238, 238, 255);">
 * <code>AM_PM<br></code>
 * </td>
 * <td style="vertical-align: middle; background-color: rgb(238, 238, 255);">
 * <code>AM<br></code>
 * </td>
 * </tr>
 * <tr>
 * <td style="vertical-align: middle;">
 * <code>HOUR, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND<br></code>
 * </td>
 * <td style="vertical-align: middle;">
 * <code>0<br></code>
 * </td>
 * </tr>
 * </tbody>
 * </table>
 * <br>Default values are not applicable for the fields not listed above.
 * <p>
 * <strong>Example:</strong>
 * <blockquote>
 * <pre>
 * // get the supported ids for GMT-08:00 (Pacific Standard Time)
 * String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
 * // if no ids were returned, something is wrong. get out.
 * if (ids.length == 0)
 * System.exit(0);
 * // begin output
 * System.out.println("Current Time");
 * // create a Pacific Standard Time time zone
 * SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
 * // set up rules for Daylight Saving Time
 * pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 * pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
 * // create a GregorianCalendar with the Pacific Daylight time zone
 * // and the current date and time
 * Calendar calendar = new GregorianCalendar(pdt);
 * Date trialTime = new Date();
 * calendar.setTime(trialTime);
 * // print out a bunch of interesting things
 * System.out.println("ERA: " + calendar.get(Calendar.ERA));
 * System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
 * System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
 * System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
 * System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
 * System.out.println("DATE: " + calendar.get(Calendar.DATE));
 * System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
 * System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
 * System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
 * System.out.println("DAY_OF_WEEK_IN_MONTH: "
 * + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
 * System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
 * System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
 * System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
 * System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
 * System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
 * System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
 * System.out.println("ZONE_OFFSET: "
 * + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
 * System.out.println("DST_OFFSET: "
 * + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));
 * System.out.println("Current Time, with hour reset to 3");
 * calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
 * calendar.set(Calendar.HOUR, 3);
 * System.out.println("ERA: " + calendar.get(Calendar.ERA));
 * System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
 * System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
 * System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
 * System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
 * System.out.println("DATE: " + calendar.get(Calendar.DATE));
 * System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
 * System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
 * System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
 * System.out.println("DAY_OF_WEEK_IN_MONTH: "
 * + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
 * System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
 * System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
 * System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
 * System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
 * System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
 * System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
 * System.out.println("ZONE_OFFSET: "
 * + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
 * System.out.println("DST_OFFSET: "
 * + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours
 * </pre>
 * </blockquote>
 * @see TimeZone
 * @author David Goldsmith, Mark Davis, Chen-Lieh Huang, Alan Liu
 * @since JDK1.1
 */
public class GregorianCalendar extends Calendar {
  /** 
 * Value of the <code>ERA</code> field indicating
 * the period before the common era (before Christ), also known as BCE.
 * The sequence of years at the transition from <code>BC</code> to <code>AD</code> is
 * ..., 2 BC, 1 BC, 1 AD, 2 AD,...
 * @see #ERA
 */
  public static final int BC=0;
  /** 
 * Value of the {@link #ERA} field indicating
 * the period before the common era, the same value as {@link #BC}.
 * @see #CE
 */
  static final int BCE=0;
  /** 
 * Value of the <code>ERA</code> field indicating
 * the common era (Anno Domini), also known as CE.
 * The sequence of years at the transition from <code>BC</code> to <code>AD</code> is
 * ..., 2 BC, 1 BC, 1 AD, 2 AD,...
 * @see #ERA
 */
  public static final int AD=1;
  /** 
 * Value of the {@link #ERA} field indicating
 * the common era, the same value as {@link #AD}.
 * @see #BCE
 */
  static final int CE=1;
  private static final int EPOCH_OFFSET=719163;
  private static final int EPOCH_YEAR=1970;
  static final int MONTH_LENGTH[]={31,28,31,30,31,30,31,31,30,31,30,31};
  static final int LEAP_MONTH_LENGTH[]={31,29,31,30,31,30,31,31,30,31,30,31};
  private static final int ONE_SECOND=1000;
  private static final int ONE_MINUTE=60 * ONE_SECOND;
  private static final int ONE_HOUR=60 * ONE_MINUTE;
  private static final long ONE_DAY=24 * ONE_HOUR;
  private static final long ONE_WEEK=7 * ONE_DAY;
  static final int MIN_VALUES[]={BCE,1,JANUARY,1,0,1,1,SUNDAY,1,AM,0,0,0,0,0,-13 * ONE_HOUR,0};
  static final int LEAST_MAX_VALUES[]={CE,292269054,DECEMBER,52,4,28,365,SATURDAY,4,PM,11,23,59,59,999,14 * ONE_HOUR,20 * ONE_MINUTE};
  static final int MAX_VALUES[]={CE,292278994,DECEMBER,53,6,31,366,SATURDAY,6,PM,11,23,59,59,999,14 * ONE_HOUR,2 * ONE_HOUR};
  static final long serialVersionUID=-8125100834729963327L;
  private static final Gregorian gcal=CalendarSystem.getGregorianCalendar();
  private static JulianCalendar jcal;
  private static Era[] jeras;
  static final long DEFAULT_GREGORIAN_CUTOVER=-12219292800000L;
  /** 
 * The point at which the Gregorian calendar rules are used, measured in
 * milliseconds from the standard epoch.  Default is October 15, 1582
 * (Gregorian) 00:00:00 UTC or -12219292800000L.  For this value, October 4,
 * 1582 (Julian) is followed by October 15, 1582 (Gregorian).  This
 * corresponds to Julian day number 2299161.
 * @serial
 */
  private long gregorianCutover=DEFAULT_GREGORIAN_CUTOVER;
  /** 
 * The fixed date of the gregorianCutover.
 */
  private transient long gregorianCutoverDate=(((DEFAULT_GREGORIAN_CUTOVER + 1) / ONE_DAY) - 1) + EPOCH_OFFSET;
  /** 
 * The normalized year of the gregorianCutover in Gregorian, with
 * 0 representing 1 BCE, -1 representing 2 BCE, etc.
 */
  private transient int gregorianCutoverYear=1582;
  /** 
 * The normalized year of the gregorianCutover in Julian, with 0
 * representing 1 BCE, -1 representing 2 BCE, etc.
 */
  private transient int gregorianCutoverYearJulian=1582;
  /** 
 * gdate always has a sun.util.calendar.Gregorian.Date instance to
 * avoid overhead of creating it. The assumption is that most
 * applications will need only Gregorian calendar calculations.
 */
  private transient BaseCalendar.Date gdate;
  /** 
 * Reference to either gdate or a JulianCalendar.Date
 * instance. After calling complete(), this value is guaranteed to
 * be set.
 */
  private transient BaseCalendar.Date cdate;
  /** 
 * The CalendarSystem used to calculate the date in cdate. After
 * calling complete(), this value is guaranteed to be set and
 * consistent with the cdate value.
 */
  private transient BaseCalendar calsys;
  /** 
 * Temporary int[2] to get time zone offsets. zoneOffsets[0] gets
 * the GMT offset value and zoneOffsets[1] gets the DST saving
 * value.
 */
  private transient int[] zoneOffsets;
  /** 
 * Temporary storage for saving original fields[] values in
 * non-lenient mode.
 */
  private transient int[] originalFields;
  /** 
 * Constructs a default <code>GregorianCalendar</code> using the current time
 * in the default time zone with the default locale.
 */
  public GregorianCalendar(){
    this(TimeZone.getDefaultRef(),Locale.getDefault(Locale.Category.FORMAT));
    setZoneShared(true);
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> based on the current time
 * in the given time zone with the default locale.
 * @param zone the given time zone.
 */
  public GregorianCalendar(  TimeZone zone){
    this(zone,Locale.getDefault(Locale.Category.FORMAT));
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> based on the current time
 * in the default time zone with the given locale.
 * @param aLocale the given locale.
 */
  public GregorianCalendar(  Locale aLocale){
    this(TimeZone.getDefaultRef(),aLocale);
    setZoneShared(true);
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> based on the current time
 * in the given time zone with the given locale.
 * @param zone the given time zone.
 * @param aLocale the given locale.
 */
  public GregorianCalendar(  TimeZone zone,  Locale aLocale){
    super(zone,aLocale);
    gdate=(BaseCalendar.Date)gcal.newCalendarDate(zone);
    setTimeInMillis(System.currentTimeMillis());
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> with the given date set
 * in the default time zone with the default locale.
 * @param year the value used to set the <code>YEAR</code> calendar field in the calendar.
 * @param month the value used to set the <code>MONTH</code> calendar field in the calendar.
 * Month value is 0-based. e.g., 0 for January.
 * @param dayOfMonth the value used to set the <code>DAY_OF_MONTH</code> calendar field in the calendar.
 */
  public GregorianCalendar(  int year,  int month,  int dayOfMonth){
    this(year,month,dayOfMonth,0,0,0,0);
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> with the given date
 * and time set for the default time zone with the default locale.
 * @param year the value used to set the <code>YEAR</code> calendar field in the calendar.
 * @param month the value used to set the <code>MONTH</code> calendar field in the calendar.
 * Month value is 0-based. e.g., 0 for January.
 * @param dayOfMonth the value used to set the <code>DAY_OF_MONTH</code> calendar field in the calendar.
 * @param hourOfDay the value used to set the <code>HOUR_OF_DAY</code> calendar field
 * in the calendar.
 * @param minute the value used to set the <code>MINUTE</code> calendar field
 * in the calendar.
 */
  public GregorianCalendar(  int year,  int month,  int dayOfMonth,  int hourOfDay,  int minute){
    this(year,month,dayOfMonth,hourOfDay,minute,0,0);
  }
  /** 
 * Constructs a GregorianCalendar with the given date
 * and time set for the default time zone with the default locale.
 * @param year the value used to set the <code>YEAR</code> calendar field in the calendar.
 * @param month the value used to set the <code>MONTH</code> calendar field in the calendar.
 * Month value is 0-based. e.g., 0 for January.
 * @param dayOfMonth the value used to set the <code>DAY_OF_MONTH</code> calendar field in the calendar.
 * @param hourOfDay the value used to set the <code>HOUR_OF_DAY</code> calendar field
 * in the calendar.
 * @param minute the value used to set the <code>MINUTE</code> calendar field
 * in the calendar.
 * @param second the value used to set the <code>SECOND</code> calendar field
 * in the calendar.
 */
  public GregorianCalendar(  int year,  int month,  int dayOfMonth,  int hourOfDay,  int minute,  int second){
    this(year,month,dayOfMonth,hourOfDay,minute,second,0);
  }
  /** 
 * Constructs a <code>GregorianCalendar</code> with the given date
 * and time set for the default time zone with the default locale.
 * @param year the value used to set the <code>YEAR</code> calendar field in the calendar.
 * @param month the value used to set the <code>MONTH</code> calendar field in the calendar.
 * Month value is 0-based. e.g., 0 for January.
 * @param dayOfMonth the value used to set the <code>DAY_OF_MONTH</code> calendar field in the calendar.
 * @param hourOfDay the value used to set the <code>HOUR_OF_DAY</code> calendar field
 * in the calendar.
 * @param minute the value used to set the <code>MINUTE</code> calendar field
 * in the calendar.
 * @param second the value used to set the <code>SECOND</code> calendar field
 * in the calendar.
 * @param millis the value used to set the <code>MILLISECOND</code> calendar field
 */
  GregorianCalendar(  int year,  int month,  int dayOfMonth,  int hourOfDay,  int minute,  int second,  int millis){
    super();
    gdate=(BaseCalendar.Date)gcal.newCalendarDate(getZone());
    this.set(YEAR,year);
    this.set(MONTH,month);
    this.set(DAY_OF_MONTH,dayOfMonth);
    if (hourOfDay >= 12 && hourOfDay <= 23) {
      this.internalSet(AM_PM,PM);
      this.internalSet(HOUR,hourOfDay - 12);
    }
 else {
      this.internalSet(HOUR,hourOfDay);
    }
    setFieldsComputed(HOUR_MASK | AM_PM_MASK);
    this.set(HOUR_OF_DAY,hourOfDay);
    this.set(MINUTE,minute);
    this.set(SECOND,second);
    this.internalSet(MILLISECOND,millis);
  }
  /** 
 * Sets the <code>GregorianCalendar</code> change date. This is the point when the switch
 * from Julian dates to Gregorian dates occurred. Default is October 15,
 * 1582 (Gregorian). Previous to this, dates will be in the Julian calendar.
 * <p>
 * To obtain a pure Julian calendar, set the change date to
 * <code>Date(Long.MAX_VALUE)</code>.  To obtain a pure Gregorian calendar,
 * set the change date to <code>Date(Long.MIN_VALUE)</code>.
 * @param date the given Gregorian cutover date.
 */
  public void setGregorianChange(  Date date){
    long cutoverTime=date.getTime();
    if (cutoverTime == gregorianCutover) {
      return;
    }
    complete();
    setGregorianChange(cutoverTime);
  }
  private void setGregorianChange(  long cutoverTime){
    gregorianCutover=cutoverTime;
    gregorianCutoverDate=CalendarUtils.floorDivide(cutoverTime,ONE_DAY) + EPOCH_OFFSET;
    if (cutoverTime == Long.MAX_VALUE) {
      gregorianCutoverDate++;
    }
    BaseCalendar.Date d=getGregorianCutoverDate();
    gregorianCutoverYear=d.getYear();
    BaseCalendar jcal=getJulianCalendarSystem();
    d=(BaseCalendar.Date)jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    jcal.getCalendarDateFromFixedDate(d,gregorianCutoverDate - 1);
    gregorianCutoverYearJulian=d.getNormalizedYear();
    if (time < gregorianCutover) {
      setUnnormalized();
    }
  }
  /** 
 * Gets the Gregorian Calendar change date.  This is the point when the
 * switch from Julian dates to Gregorian dates occurred. Default is
 * October 15, 1582 (Gregorian). Previous to this, dates will be in the Julian
 * calendar.
 * @return the Gregorian cutover date for this <code>GregorianCalendar</code> object.
 */
  public final Date getGregorianChange(){
    return new Date(gregorianCutover);
  }
  /** 
 * Determines if the given year is a leap year. Returns <code>true</code> if
 * the given year is a leap year. To specify BC year numbers,
 * <code>1 - year number</code> must be given. For example, year BC 4 is
 * specified as -3.
 * @param year the given year.
 * @return <code>true</code> if the given year is a leap year; <code>false</code> otherwise.
 */
  public boolean isLeapYear(  int year){
    if ((year & 3) != 0) {
      return false;
    }
    if (year > gregorianCutoverYear) {
      return (year % 100 != 0) || (year % 400 == 0);
    }
    if (year < gregorianCutoverYearJulian) {
      return true;
    }
    boolean gregorian;
    if (gregorianCutoverYear == gregorianCutoverYearJulian) {
      BaseCalendar.Date d=getCalendarDate(gregorianCutoverDate);
      gregorian=d.getMonth() < BaseCalendar.MARCH;
    }
 else {
      gregorian=year == gregorianCutoverYear;
    }
    return gregorian ? (year % 100 != 0) || (year % 400 == 0) : true;
  }
  /** 
 * Compares this <code>GregorianCalendar</code> to the specified
 * <code>Object</code>. The result is <code>true</code> if and
 * only if the argument is a <code>GregorianCalendar</code> object
 * that represents the same time value (millisecond offset from
 * the <a href="Calendar.html#Epoch">Epoch</a>) under the same
 * <code>Calendar</code> parameters and Gregorian change date as
 * this object.
 * @param obj the object to compare with.
 * @return <code>true</code> if this object is equal to <code>obj</code>;
 * <code>false</code> otherwise.
 * @see Calendar#compareTo(Calendar)
 */
  public boolean equals(  Object obj){
    return obj instanceof GregorianCalendar && super.equals(obj) && gregorianCutover == ((GregorianCalendar)obj).gregorianCutover;
  }
  /** 
 * Generates the hash code for this <code>GregorianCalendar</code> object.
 */
  public int hashCode(){
    return super.hashCode() ^ (int)gregorianCutoverDate;
  }
  /** 
 * Adds the specified (signed) amount of time to the given calendar field,
 * based on the calendar's rules.
 * <p><em>Add rule 1</em>. The value of <code>field</code>
 * after the call minus the value of <code>field</code> before the
 * call is <code>amount</code>, modulo any overflow that has occurred in
 * <code>field</code>. Overflow occurs when a field value exceeds its
 * range and, as a result, the next larger field is incremented or
 * decremented and the field value is adjusted back into its range.</p>
 * <p><em>Add rule 2</em>. If a smaller field is expected to be
 * invariant, but it is impossible for it to be equal to its
 * prior value because of changes in its minimum or maximum after
 * <code>field</code> is changed, then its value is adjusted to be as close
 * as possible to its expected value. A smaller field represents a
 * smaller unit of time. <code>HOUR</code> is a smaller field than
 * <code>DAY_OF_MONTH</code>. No adjustment is made to smaller fields
 * that are not expected to be invariant. The calendar system
 * determines what fields are expected to be invariant.</p>
 * @param field the calendar field.
 * @param amount the amount of date or time to be added to the field.
 * @exception IllegalArgumentException if <code>field</code> is
 * <code>ZONE_OFFSET</code>, <code>DST_OFFSET</code>, or unknown,
 * or if any calendar fields have out-of-range values in
 * non-lenient mode.
 */
  public void add(  int field,  int amount){
    if (amount == 0) {
      return;
    }
    if (field < 0 || field >= ZONE_OFFSET) {
      throw new IllegalArgumentException();
    }
    complete();
    if (field == YEAR) {
      int year=internalGet(YEAR);
      if (internalGetEra() == CE) {
        year+=amount;
        if (year > 0) {
          set(YEAR,year);
        }
 else {
          set(YEAR,1 - year);
          set(ERA,BCE);
        }
      }
 else {
        year-=amount;
        if (year > 0) {
          set(YEAR,year);
        }
 else {
          set(YEAR,1 - year);
          set(ERA,CE);
        }
      }
      pinDayOfMonth();
    }
 else     if (field == MONTH) {
      int month=internalGet(MONTH) + amount;
      int year=internalGet(YEAR);
      int y_amount;
      if (month >= 0) {
        y_amount=month / 12;
      }
 else {
        y_amount=(month + 1) / 12 - 1;
      }
      if (y_amount != 0) {
        if (internalGetEra() == CE) {
          year+=y_amount;
          if (year > 0) {
            set(YEAR,year);
          }
 else {
            set(YEAR,1 - year);
            set(ERA,BCE);
          }
        }
 else {
          year-=y_amount;
          if (year > 0) {
            set(YEAR,year);
          }
 else {
            set(YEAR,1 - year);
            set(ERA,CE);
          }
        }
      }
      if (month >= 0) {
        set(MONTH,(int)(month % 12));
      }
 else {
        month%=12;
        if (month < 0) {
          month+=12;
        }
        set(MONTH,JANUARY + month);
      }
      pinDayOfMonth();
    }
 else     if (field == ERA) {
      int era=internalGet(ERA) + amount;
      if (era < 0) {
        era=0;
      }
      if (era > 1) {
        era=1;
      }
      set(ERA,era);
    }
 else {
      long delta=amount;
      long timeOfDay=0;
switch (field) {
case HOUR:
case HOUR_OF_DAY:
        delta*=60 * 60 * 1000;
      break;
case MINUTE:
    delta*=60 * 1000;
  break;
case SECOND:
delta*=1000;
break;
case MILLISECOND:
break;
case WEEK_OF_YEAR:
case WEEK_OF_MONTH:
case DAY_OF_WEEK_IN_MONTH:
delta*=7;
break;
case DAY_OF_MONTH:
case DAY_OF_YEAR:
case DAY_OF_WEEK:
break;
case AM_PM:
delta=amount / 2;
timeOfDay=12 * (amount % 2);
break;
}
if (field >= HOUR) {
setTimeInMillis(time + delta);
return;
}
long fd=getCurrentFixedDate();
timeOfDay+=internalGet(HOUR_OF_DAY);
timeOfDay*=60;
timeOfDay+=internalGet(MINUTE);
timeOfDay*=60;
timeOfDay+=internalGet(SECOND);
timeOfDay*=1000;
timeOfDay+=internalGet(MILLISECOND);
if (timeOfDay >= ONE_DAY) {
fd++;
timeOfDay-=ONE_DAY;
}
 else if (timeOfDay < 0) {
fd--;
timeOfDay+=ONE_DAY;
}
fd+=delta;
int zoneOffset=internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET);
setTimeInMillis((fd - EPOCH_OFFSET) * ONE_DAY + timeOfDay - zoneOffset);
zoneOffset-=internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET);
if (zoneOffset != 0) {
setTimeInMillis(time + zoneOffset);
long fd2=getCurrentFixedDate();
if (fd2 != fd) {
setTimeInMillis(time - zoneOffset);
}
}
}
}
/** 
 * Adds or subtracts (up/down) a single unit of time on the given time
 * field without changing larger fields.
 * <p>
 * <em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to December 31, 1999. Calling {@link #roll(int,boolean) roll(Calendar.MONTH, true)}sets the calendar to January 31, 1999.  The <code>YEAR</code> field is unchanged
 * because it is a larger field than <code>MONTH</code>.</p>
 * @param up indicates if the value of the specified calendar field is to be
 * rolled up or rolled down. Use <code>true</code> if rolling up, <code>false</code> otherwise.
 * @exception IllegalArgumentException if <code>field</code> is
 * <code>ZONE_OFFSET</code>, <code>DST_OFFSET</code>, or unknown,
 * or if any calendar fields have out-of-range values in
 * non-lenient mode.
 * @see #add(int,int)
 * @see #set(int,int)
 */
public void roll(int field,boolean up){
roll(field,up ? +1 : -1);
}
/** 
 * Adds a signed amount to the specified calendar field without changing larger fields.
 * A negative roll amount means to subtract from field without changing
 * larger fields. If the specified amount is 0, this method performs nothing.
 * <p>This method calls {@link #complete()} before adding the
 * amount so that all the calendar fields are normalized. If there
 * is any calendar field having an out-of-range value in non-lenient mode, then an
 * <code>IllegalArgumentException</code> is thrown.
 * <p>
 * <em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to August 31, 1999. Calling <code>roll(Calendar.MONTH,
 * 8)</code> sets the calendar to April 30, <strong>1999</strong>. Using a
 * <code>GregorianCalendar</code>, the <code>DAY_OF_MONTH</code> field cannot
 * be 31 in the month April. <code>DAY_OF_MONTH</code> is set to the closest possible
 * value, 30. The <code>YEAR</code> field maintains the value of 1999 because it
 * is a larger field than <code>MONTH</code>.
 * <p>
 * <em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to Sunday June 6, 1999. Calling
 * <code>roll(Calendar.WEEK_OF_MONTH, -1)</code> sets the calendar to
 * Tuesday June 1, 1999, whereas calling
 * <code>add(Calendar.WEEK_OF_MONTH, -1)</code> sets the calendar to
 * Sunday May 30, 1999. This is because the roll rule imposes an
 * additional constraint: The <code>MONTH</code> must not change when the
 * <code>WEEK_OF_MONTH</code> is rolled. Taken together with add rule 1,
 * the resultant date must be between Tuesday June 1 and Saturday June
 * 5. According to add rule 2, the <code>DAY_OF_WEEK</code>, an invariant
 * when changing the <code>WEEK_OF_MONTH</code>, is set to Tuesday, the
 * closest possible value to Sunday (where Sunday is the first day of the
 * week).</p>
 * @param field the calendar field.
 * @param amount the signed amount to add to <code>field</code>.
 * @exception IllegalArgumentException if <code>field</code> is
 * <code>ZONE_OFFSET</code>, <code>DST_OFFSET</code>, or unknown,
 * or if any calendar fields have out-of-range values in
 * non-lenient mode.
 * @see #roll(int,boolean)
 * @see #add(int,int)
 * @see #set(int,int)
 * @since 1.2
 */
public void roll(int field,int amount){
if (amount == 0) {
return;
}
if (field < 0 || field >= ZONE_OFFSET) {
throw new IllegalArgumentException();
}
complete();
int min=getMinimum(field);
int max=getMaximum(field);
switch (field) {
case AM_PM:
case ERA:
case YEAR:
case MINUTE:
case SECOND:
case MILLISECOND:
break;
case HOUR:
case HOUR_OF_DAY:
{
int unit=max + 1;
int h=internalGet(field);
int nh=(h + amount) % unit;
if (nh < 0) {
nh+=unit;
}
time+=ONE_HOUR * (nh - h);
CalendarDate d=calsys.getCalendarDate(time,getZone());
if (internalGet(DAY_OF_MONTH) != d.getDayOfMonth()) {
d.setDate(internalGet(YEAR),internalGet(MONTH) + 1,internalGet(DAY_OF_MONTH));
if (field == HOUR) {
assert (internalGet(AM_PM) == PM);
d.addHours(+12);
}
time=calsys.getTime(d);
}
int hourOfDay=d.getHours();
internalSet(field,hourOfDay % unit);
if (field == HOUR) {
internalSet(HOUR_OF_DAY,hourOfDay);
}
 else {
internalSet(AM_PM,hourOfDay / 12);
internalSet(HOUR,hourOfDay % 12);
}
int zoneOffset=d.getZoneOffset();
int saving=d.getDaylightSaving();
internalSet(ZONE_OFFSET,zoneOffset - saving);
internalSet(DST_OFFSET,saving);
return;
}
case MONTH:
{
if (!isCutoverYear(cdate.getNormalizedYear())) {
int mon=(internalGet(MONTH) + amount) % 12;
if (mon < 0) {
mon+=12;
}
set(MONTH,mon);
int monthLen=monthLength(mon);
if (internalGet(DAY_OF_MONTH) > monthLen) {
set(DAY_OF_MONTH,monthLen);
}
}
 else {
int yearLength=getActualMaximum(MONTH) + 1;
int mon=(internalGet(MONTH) + amount) % yearLength;
if (mon < 0) {
mon+=yearLength;
}
set(MONTH,mon);
int monthLen=getActualMaximum(DAY_OF_MONTH);
if (internalGet(DAY_OF_MONTH) > monthLen) {
set(DAY_OF_MONTH,monthLen);
}
}
return;
}
case WEEK_OF_YEAR:
{
int y=cdate.getNormalizedYear();
max=getActualMaximum(WEEK_OF_YEAR);
set(DAY_OF_WEEK,internalGet(DAY_OF_WEEK));
int woy=internalGet(WEEK_OF_YEAR);
int value=woy + amount;
if (!isCutoverYear(y)) {
if (value > min && value < max) {
set(WEEK_OF_YEAR,value);
return;
}
long fd=getCurrentFixedDate();
long day1=fd - (7 * (woy - min));
if (calsys.getYearFromFixedDate(day1) != y) {
min++;
}
fd+=7 * (max - internalGet(WEEK_OF_YEAR));
if (calsys.getYearFromFixedDate(fd) != y) {
max--;
}
break;
}
long fd=getCurrentFixedDate();
BaseCalendar cal;
if (gregorianCutoverYear == gregorianCutoverYearJulian) {
cal=getCutoverCalendarSystem();
}
 else if (y == gregorianCutoverYear) {
cal=gcal;
}
 else {
cal=getJulianCalendarSystem();
}
long day1=fd - (7 * (woy - min));
if (cal.getYearFromFixedDate(day1) != y) {
min++;
}
fd+=7 * (max - woy);
cal=(fd >= gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
if (cal.getYearFromFixedDate(fd) != y) {
max--;
}
value=getRolledValue(woy,amount,min,max) - 1;
BaseCalendar.Date d=getCalendarDate(day1 + value * 7);
set(MONTH,d.getMonth() - 1);
set(DAY_OF_MONTH,d.getDayOfMonth());
return;
}
case WEEK_OF_MONTH:
{
boolean isCutoverYear=isCutoverYear(cdate.getNormalizedYear());
int dow=internalGet(DAY_OF_WEEK) - getFirstDayOfWeek();
if (dow < 0) {
dow+=7;
}
long fd=getCurrentFixedDate();
long month1;
int monthLength;
if (isCutoverYear) {
month1=getFixedDateMonth1(cdate,fd);
monthLength=actualMonthLength();
}
 else {
month1=fd - internalGet(DAY_OF_MONTH) + 1;
monthLength=calsys.getMonthLength(cdate);
}
long monthDay1st=calsys.getDayOfWeekDateOnOrBefore(month1 + 6,getFirstDayOfWeek());
if ((int)(monthDay1st - month1) >= getMinimalDaysInFirstWeek()) {
monthDay1st-=7;
}
max=getActualMaximum(field);
int value=getRolledValue(internalGet(field),amount,1,max) - 1;
long nfd=monthDay1st + value * 7 + dow;
if (nfd < month1) {
nfd=month1;
}
 else if (nfd >= (month1 + monthLength)) {
nfd=month1 + monthLength - 1;
}
int dayOfMonth;
if (isCutoverYear) {
BaseCalendar.Date d=getCalendarDate(nfd);
dayOfMonth=d.getDayOfMonth();
}
 else {
dayOfMonth=(int)(nfd - month1) + 1;
}
set(DAY_OF_MONTH,dayOfMonth);
return;
}
case DAY_OF_MONTH:
{
if (!isCutoverYear(cdate.getNormalizedYear())) {
max=calsys.getMonthLength(cdate);
break;
}
long fd=getCurrentFixedDate();
long month1=getFixedDateMonth1(cdate,fd);
int value=getRolledValue((int)(fd - month1),amount,0,actualMonthLength() - 1);
BaseCalendar.Date d=getCalendarDate(month1 + value);
assert d.getMonth() - 1 == internalGet(MONTH);
set(DAY_OF_MONTH,d.getDayOfMonth());
return;
}
case DAY_OF_YEAR:
{
max=getActualMaximum(field);
if (!isCutoverYear(cdate.getNormalizedYear())) {
break;
}
long fd=getCurrentFixedDate();
long jan1=fd - internalGet(DAY_OF_YEAR) + 1;
int value=getRolledValue((int)(fd - jan1) + 1,amount,min,max);
BaseCalendar.Date d=getCalendarDate(jan1 + value - 1);
set(MONTH,d.getMonth() - 1);
set(DAY_OF_MONTH,d.getDayOfMonth());
return;
}
case DAY_OF_WEEK:
{
if (!isCutoverYear(cdate.getNormalizedYear())) {
int weekOfYear=internalGet(WEEK_OF_YEAR);
if (weekOfYear > 1 && weekOfYear < 52) {
set(WEEK_OF_YEAR,weekOfYear);
max=SATURDAY;
break;
}
}
amount%=7;
if (amount == 0) {
return;
}
long fd=getCurrentFixedDate();
long dowFirst=calsys.getDayOfWeekDateOnOrBefore(fd,getFirstDayOfWeek());
fd+=amount;
if (fd < dowFirst) {
fd+=7;
}
 else if (fd >= dowFirst + 7) {
fd-=7;
}
BaseCalendar.Date d=getCalendarDate(fd);
set(ERA,(d.getNormalizedYear() <= 0 ? BCE : CE));
set(d.getYear(),d.getMonth() - 1,d.getDayOfMonth());
return;
}
case DAY_OF_WEEK_IN_MONTH:
{
min=1;
if (!isCutoverYear(cdate.getNormalizedYear())) {
int dom=internalGet(DAY_OF_MONTH);
int monthLength=calsys.getMonthLength(cdate);
int lastDays=monthLength % 7;
max=monthLength / 7;
int x=(dom - 1) % 7;
if (x < lastDays) {
max++;
}
set(DAY_OF_WEEK,internalGet(DAY_OF_WEEK));
break;
}
long fd=getCurrentFixedDate();
long month1=getFixedDateMonth1(cdate,fd);
int monthLength=actualMonthLength();
int lastDays=monthLength % 7;
max=monthLength / 7;
int x=(int)(fd - month1) % 7;
if (x < lastDays) {
max++;
}
int value=getRolledValue(internalGet(field),amount,min,max) - 1;
fd=month1 + value * 7 + x;
BaseCalendar cal=(fd >= gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
BaseCalendar.Date d=(BaseCalendar.Date)cal.newCalendarDate(TimeZone.NO_TIMEZONE);
cal.getCalendarDateFromFixedDate(d,fd);
set(DAY_OF_MONTH,d.getDayOfMonth());
return;
}
}
set(field,getRolledValue(internalGet(field),amount,min,max));
}
/** 
 * Returns the minimum value for the given calendar field of this
 * <code>GregorianCalendar</code> instance. The minimum value is
 * defined as the smallest value returned by the {@link Calendar#get(int) get} method for any possible time value,
 * taking into consideration the current values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * @param field the calendar field.
 * @return the minimum value for the given calendar field.
 * @see #getMaximum(int)
 * @see #getGreatestMinimum(int)
 * @see #getLeastMaximum(int)
 * @see #getActualMinimum(int)
 * @see #getActualMaximum(int)
 */
public int getMinimum(int field){
return MIN_VALUES[field];
}
/** 
 * Returns the maximum value for the given calendar field of this
 * <code>GregorianCalendar</code> instance. The maximum value is
 * defined as the largest value returned by the {@link Calendar#get(int) get} method for any possible time value,
 * taking into consideration the current values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * @param field the calendar field.
 * @return the maximum value for the given calendar field.
 * @see #getMinimum(int)
 * @see #getGreatestMinimum(int)
 * @see #getLeastMaximum(int)
 * @see #getActualMinimum(int)
 * @see #getActualMaximum(int)
 */
public int getMaximum(int field){
switch (field) {
case MONTH:
case DAY_OF_MONTH:
case DAY_OF_YEAR:
case WEEK_OF_YEAR:
case WEEK_OF_MONTH:
case DAY_OF_WEEK_IN_MONTH:
case YEAR:
{
if (gregorianCutoverYear > 200) {
break;
}
GregorianCalendar gc=(GregorianCalendar)clone();
gc.setLenient(true);
gc.setTimeInMillis(gregorianCutover);
int v1=gc.getActualMaximum(field);
gc.setTimeInMillis(gregorianCutover - 1);
int v2=gc.getActualMaximum(field);
return Math.max(MAX_VALUES[field],Math.max(v1,v2));
}
}
return MAX_VALUES[field];
}
/** 
 * Returns the highest minimum value for the given calendar field
 * of this <code>GregorianCalendar</code> instance. The highest
 * minimum value is defined as the largest value returned by{@link #getActualMinimum(int)} for any possible time value,
 * taking into consideration the current values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * @param field the calendar field.
 * @return the highest minimum value for the given calendar field.
 * @see #getMinimum(int)
 * @see #getMaximum(int)
 * @see #getLeastMaximum(int)
 * @see #getActualMinimum(int)
 * @see #getActualMaximum(int)
 */
public int getGreatestMinimum(int field){
if (field == DAY_OF_MONTH) {
BaseCalendar.Date d=getGregorianCutoverDate();
long mon1=getFixedDateMonth1(d,gregorianCutoverDate);
d=getCalendarDate(mon1);
return Math.max(MIN_VALUES[field],d.getDayOfMonth());
}
return MIN_VALUES[field];
}
/** 
 * Returns the lowest maximum value for the given calendar field
 * of this <code>GregorianCalendar</code> instance. The lowest
 * maximum value is defined as the smallest value returned by{@link #getActualMaximum(int)} for any possible time value,
 * taking into consideration the current values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * @param field the calendar field
 * @return the lowest maximum value for the given calendar field.
 * @see #getMinimum(int)
 * @see #getMaximum(int)
 * @see #getGreatestMinimum(int)
 * @see #getActualMinimum(int)
 * @see #getActualMaximum(int)
 */
public int getLeastMaximum(int field){
switch (field) {
case MONTH:
case DAY_OF_MONTH:
case DAY_OF_YEAR:
case WEEK_OF_YEAR:
case WEEK_OF_MONTH:
case DAY_OF_WEEK_IN_MONTH:
case YEAR:
{
GregorianCalendar gc=(GregorianCalendar)clone();
gc.setLenient(true);
gc.setTimeInMillis(gregorianCutover);
int v1=gc.getActualMaximum(field);
gc.setTimeInMillis(gregorianCutover - 1);
int v2=gc.getActualMaximum(field);
return Math.min(LEAST_MAX_VALUES[field],Math.min(v1,v2));
}
}
return LEAST_MAX_VALUES[field];
}
/** 
 * Returns the minimum value that this calendar field could have,
 * taking into consideration the given time value and the current
 * values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * <p>For example, if the Gregorian change date is January 10,
 * 1970 and the date of this <code>GregorianCalendar</code> is
 * January 20, 1970, the actual minimum value of the
 * <code>DAY_OF_MONTH</code> field is 10 because the previous date
 * of January 10, 1970 is December 27, 1996 (in the Julian
 * calendar). Therefore, December 28, 1969 to January 9, 1970
 * don't exist.
 * @param field the calendar field
 * @return the minimum of the given field for the time value of
 * this <code>GregorianCalendar</code>
 * @see #getMinimum(int)
 * @see #getMaximum(int)
 * @see #getGreatestMinimum(int)
 * @see #getLeastMaximum(int)
 * @see #getActualMaximum(int)
 * @since 1.2
 */
public int getActualMinimum(int field){
if (field == DAY_OF_MONTH) {
GregorianCalendar gc=getNormalizedCalendar();
int year=gc.cdate.getNormalizedYear();
if (year == gregorianCutoverYear || year == gregorianCutoverYearJulian) {
long month1=getFixedDateMonth1(gc.cdate,gc.calsys.getFixedDate(gc.cdate));
BaseCalendar.Date d=getCalendarDate(month1);
return d.getDayOfMonth();
}
}
return getMinimum(field);
}
/** 
 * Returns the maximum value that this calendar field could have,
 * taking into consideration the given time value and the current
 * values of the{@link Calendar#getFirstDayOfWeek() getFirstDayOfWeek},{@link Calendar#getMinimalDaysInFirstWeek() getMinimalDaysInFirstWeek},{@link #getGregorianChange() getGregorianChange} and{@link Calendar#getTimeZone() getTimeZone} methods.
 * For example, if the date of this instance is February 1, 2004,
 * the actual maximum value of the <code>DAY_OF_MONTH</code> field
 * is 29 because 2004 is a leap year, and if the date of this
 * instance is February 1, 2005, it's 28.
 * <p>This method calculates the maximum value of {@link Calendar#WEEK_OF_YEAR WEEK_OF_YEAR} based on the {@link Calendar#YEAR YEAR} (calendar year) value, not the <a
 * href="#week_year">week year</a>. Call {@link #getWeeksInWeekYear()} to get the maximum value of {@codeWEEK_OF_YEAR} in the week year of this {@code GregorianCalendar}.
 * @param field the calendar field
 * @return the maximum of the given field for the time value of
 * this <code>GregorianCalendar</code>
 * @see #getMinimum(int)
 * @see #getMaximum(int)
 * @see #getGreatestMinimum(int)
 * @see #getLeastMaximum(int)
 * @see #getActualMinimum(int)
 * @since 1.2
 */
public int getActualMaximum(int field){
final int fieldsForFixedMax=ERA_MASK | DAY_OF_WEEK_MASK | HOUR_MASK| AM_PM_MASK| HOUR_OF_DAY_MASK| MINUTE_MASK| SECOND_MASK| MILLISECOND_MASK| ZONE_OFFSET_MASK| DST_OFFSET_MASK;
if ((fieldsForFixedMax & (1 << field)) != 0) {
return getMaximum(field);
}
GregorianCalendar gc=getNormalizedCalendar();
BaseCalendar.Date date=gc.cdate;
BaseCalendar cal=gc.calsys;
int normalizedYear=date.getNormalizedYear();
int value=-1;
switch (field) {
case MONTH:
{
if (!gc.isCutoverYear(normalizedYear)) {
value=DECEMBER;
break;
}
long nextJan1;
do {
nextJan1=gcal.getFixedDate(++normalizedYear,BaseCalendar.JANUARY,1,null);
}
 while (nextJan1 < gregorianCutoverDate);
BaseCalendar.Date d=(BaseCalendar.Date)date.clone();
cal.getCalendarDateFromFixedDate(d,nextJan1 - 1);
value=d.getMonth() - 1;
}
break;
case DAY_OF_MONTH:
{
value=cal.getMonthLength(date);
if (!gc.isCutoverYear(normalizedYear) || date.getDayOfMonth() == value) {
break;
}
long fd=gc.getCurrentFixedDate();
if (fd >= gregorianCutoverDate) {
break;
}
int monthLength=gc.actualMonthLength();
long monthEnd=gc.getFixedDateMonth1(gc.cdate,fd) + monthLength - 1;
BaseCalendar.Date d=gc.getCalendarDate(monthEnd);
value=d.getDayOfMonth();
}
break;
case DAY_OF_YEAR:
{
if (!gc.isCutoverYear(normalizedYear)) {
value=cal.getYearLength(date);
break;
}
long jan1;
if (gregorianCutoverYear == gregorianCutoverYearJulian) {
BaseCalendar cocal=gc.getCutoverCalendarSystem();
jan1=cocal.getFixedDate(normalizedYear,1,1,null);
}
 else if (normalizedYear == gregorianCutoverYearJulian) {
jan1=cal.getFixedDate(normalizedYear,1,1,null);
}
 else {
jan1=gregorianCutoverDate;
}
long nextJan1=gcal.getFixedDate(++normalizedYear,1,1,null);
if (nextJan1 < gregorianCutoverDate) {
nextJan1=gregorianCutoverDate;
}
assert jan1 <= cal.getFixedDate(date.getNormalizedYear(),date.getMonth(),date.getDayOfMonth(),date);
assert nextJan1 >= cal.getFixedDate(date.getNormalizedYear(),date.getMonth(),date.getDayOfMonth(),date);
value=(int)(nextJan1 - jan1);
}
break;
case WEEK_OF_YEAR:
{
if (!gc.isCutoverYear(normalizedYear)) {
CalendarDate d=cal.newCalendarDate(TimeZone.NO_TIMEZONE);
d.setDate(date.getYear(),BaseCalendar.JANUARY,1);
int dayOfWeek=cal.getDayOfWeek(d);
dayOfWeek-=getFirstDayOfWeek();
if (dayOfWeek < 0) {
dayOfWeek+=7;
}
value=52;
int magic=dayOfWeek + getMinimalDaysInFirstWeek() - 1;
if ((magic == 6) || (date.isLeapYear() && (magic == 5 || magic == 12))) {
value++;
}
break;
}
if (gc == this) {
gc=(GregorianCalendar)gc.clone();
}
int maxDayOfYear=getActualMaximum(DAY_OF_YEAR);
gc.set(DAY_OF_YEAR,maxDayOfYear);
value=gc.get(WEEK_OF_YEAR);
if (internalGet(YEAR) != gc.getWeekYear()) {
gc.set(DAY_OF_YEAR,maxDayOfYear - 7);
value=gc.get(WEEK_OF_YEAR);
}
}
break;
case WEEK_OF_MONTH:
{
if (!gc.isCutoverYear(normalizedYear)) {
CalendarDate d=cal.newCalendarDate(null);
d.setDate(date.getYear(),date.getMonth(),1);
int dayOfWeek=cal.getDayOfWeek(d);
int monthLength=cal.getMonthLength(d);
dayOfWeek-=getFirstDayOfWeek();
if (dayOfWeek < 0) {
dayOfWeek+=7;
}
int nDaysFirstWeek=7 - dayOfWeek;
value=3;
if (nDaysFirstWeek >= getMinimalDaysInFirstWeek()) {
value++;
}
monthLength-=nDaysFirstWeek + 7 * 3;
if (monthLength > 0) {
value++;
if (monthLength > 7) {
value++;
}
}
break;
}
if (gc == this) {
gc=(GregorianCalendar)gc.clone();
}
int y=gc.internalGet(YEAR);
int m=gc.internalGet(MONTH);
do {
value=gc.get(WEEK_OF_MONTH);
gc.add(WEEK_OF_MONTH,+1);
}
 while (gc.get(YEAR) == y && gc.get(MONTH) == m);
}
break;
case DAY_OF_WEEK_IN_MONTH:
{
int ndays, dow1;
int dow=date.getDayOfWeek();
if (!gc.isCutoverYear(normalizedYear)) {
BaseCalendar.Date d=(BaseCalendar.Date)date.clone();
ndays=cal.getMonthLength(d);
d.setDayOfMonth(1);
cal.normalize(d);
dow1=d.getDayOfWeek();
}
 else {
if (gc == this) {
gc=(GregorianCalendar)clone();
}
ndays=gc.actualMonthLength();
gc.set(DAY_OF_MONTH,gc.getActualMinimum(DAY_OF_MONTH));
dow1=gc.get(DAY_OF_WEEK);
}
int x=dow - dow1;
if (x < 0) {
x+=7;
}
ndays-=x;
value=(ndays + 6) / 7;
}
break;
case YEAR:
{
if (gc == this) {
gc=(GregorianCalendar)clone();
}
long current=gc.getYearOffsetInMillis();
if (gc.internalGetEra() == CE) {
gc.setTimeInMillis(Long.MAX_VALUE);
value=gc.get(YEAR);
long maxEnd=gc.getYearOffsetInMillis();
if (current > maxEnd) {
value--;
}
}
 else {
CalendarSystem mincal=gc.getTimeInMillis() >= gregorianCutover ? gcal : getJulianCalendarSystem();
CalendarDate d=mincal.getCalendarDate(Long.MIN_VALUE,getZone());
long maxEnd=(cal.getDayOfYear(d) - 1) * 24 + d.getHours();
maxEnd*=60;
maxEnd+=d.getMinutes();
maxEnd*=60;
maxEnd+=d.getSeconds();
maxEnd*=1000;
maxEnd+=d.getMillis();
value=d.getYear();
if (value <= 0) {
assert mincal == gcal;
value=1 - value;
}
if (current < maxEnd) {
value--;
}
}
}
break;
default :
throw new ArrayIndexOutOfBoundsException(field);
}
return value;
}
/** 
 * Returns the millisecond offset from the beginning of this
 * year. This Calendar object must have been normalized.
 */
private final long getYearOffsetInMillis(){
long t=(internalGet(DAY_OF_YEAR) - 1) * 24;
t+=internalGet(HOUR_OF_DAY);
t*=60;
t+=internalGet(MINUTE);
t*=60;
t+=internalGet(SECOND);
t*=1000;
return t + internalGet(MILLISECOND) - (internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET));
}
public Object clone(){
GregorianCalendar other=(GregorianCalendar)super.clone();
other.gdate=(BaseCalendar.Date)gdate.clone();
if (cdate != null) {
if (cdate != gdate) {
other.cdate=(BaseCalendar.Date)cdate.clone();
}
 else {
other.cdate=other.gdate;
}
}
other.originalFields=null;
other.zoneOffsets=null;
return other;
}
public TimeZone getTimeZone(){
TimeZone zone=super.getTimeZone();
gdate.setZone(zone);
if (cdate != null && cdate != gdate) {
cdate.setZone(zone);
}
return zone;
}
public void setTimeZone(TimeZone zone){
super.setTimeZone(zone);
gdate.setZone(zone);
if (cdate != null && cdate != gdate) {
cdate.setZone(zone);
}
}
/** 
 * Returns {@code true} indicating this {@code GregorianCalendar}supports week dates.
 * @return {@code true} (always)
 * @see #getWeekYear()
 * @see #setWeekDate(int,int,int)
 * @see #getWeeksInWeekYear()
 * @since 1.7
 */
@Override public final boolean isWeekDateSupported(){
return true;
}
/** 
 * Returns the <a href="#week_year">week year</a> represented by this{@code GregorianCalendar}. The dates in the weeks between 1 and the
 * maximum week number of the week year have the same week year value
 * that may be one year before or after the {@link Calendar#YEAR YEAR}(calendar year) value.
 * <p>This method calls {@link Calendar#complete()} before
 * calculating the week year.
 * @return the week year represented by this {@code GregorianCalendar}.
 * If the {@link Calendar#ERA ERA} value is {@link #BC}, the year is
 * represented by 0 or a negative number: BC 1 is 0, BC 2
 * is -1, BC 3 is -2, and so on.
 * @throws IllegalArgumentExceptionif any of the calendar fields is invalid in non-lenient mode.
 * @see #isWeekDateSupported()
 * @see #getWeeksInWeekYear()
 * @see Calendar#getFirstDayOfWeek()
 * @see Calendar#getMinimalDaysInFirstWeek()
 * @since 1.7
 */
@Override public int getWeekYear(){
int year=get(YEAR);
if (internalGetEra() == BCE) {
year=1 - year;
}
if (year > gregorianCutoverYear + 1) {
int weekOfYear=internalGet(WEEK_OF_YEAR);
if (internalGet(MONTH) == JANUARY) {
if (weekOfYear >= 52) {
--year;
}
}
 else {
if (weekOfYear == 1) {
++year;
}
}
return year;
}
int dayOfYear=internalGet(DAY_OF_YEAR);
int maxDayOfYear=getActualMaximum(DAY_OF_YEAR);
int minimalDays=getMinimalDaysInFirstWeek();
if (dayOfYear > minimalDays && dayOfYear < (maxDayOfYear - 6)) {
return year;
}
GregorianCalendar cal=(GregorianCalendar)clone();
cal.setLenient(true);
cal.setTimeZone(TimeZone.getTimeZone("GMT"));
cal.set(DAY_OF_YEAR,1);
cal.complete();
int delta=getFirstDayOfWeek() - cal.get(DAY_OF_WEEK);
if (delta != 0) {
if (delta < 0) {
delta+=7;
}
cal.add(DAY_OF_YEAR,delta);
}
int minDayOfYear=cal.get(DAY_OF_YEAR);
if (dayOfYear < minDayOfYear) {
if (minDayOfYear <= minimalDays) {
--year;
}
}
 else {
cal.set(YEAR,year + 1);
cal.set(DAY_OF_YEAR,1);
cal.complete();
int del=getFirstDayOfWeek() - cal.get(DAY_OF_WEEK);
if (del != 0) {
if (del < 0) {
del+=7;
}
cal.add(DAY_OF_YEAR,del);
}
minDayOfYear=cal.get(DAY_OF_YEAR) - 1;
if (minDayOfYear == 0) {
minDayOfYear=7;
}
if (minDayOfYear >= minimalDays) {
int days=maxDayOfYear - dayOfYear + 1;
if (days <= (7 - minDayOfYear)) {
++year;
}
}
}
return year;
}
/** 
 * Sets this {@code GregorianCalendar} to the date given by the
 * date specifiers - <a href="#week_year">{@code weekYear}</a>,{@code weekOfYear}, and {@code dayOfWeek}. {@code weekOfYear}follows the <a href="#week_and_year">{@code WEEK_OF_YEAR}numbering</a>.  The {@code dayOfWeek} value must be one of the{@link Calendar#DAY_OF_WEEK DAY_OF_WEEK} values: {@link Calendar#SUNDAY SUNDAY} to {@link Calendar#SATURDAY SATURDAY}.
 * <p>Note that the numeric day-of-week representation differs from
 * the ISO 8601 standard, and that the {@code weekOfYear}numbering is compatible with the standard when {@codegetFirstDayOfWeek()} is {@code MONDAY} and {@codegetMinimalDaysInFirstWeek()} is 4.
 * <p>Unlike the {@code set} method, all of the calendar fields
 * and the instant of time value are calculated upon return.
 * <p>If {@code weekOfYear} is out of the valid week-of-year
 * range in {@code weekYear}, the {@code weekYear}and {@code weekOfYear} values are adjusted in lenient
 * mode, or an {@code IllegalArgumentException} is thrown in
 * non-lenient mode.
 * @param weekYear    the week year
 * @param weekOfYear  the week number based on {@code weekYear}
 * @param dayOfWeek   the day of week value: one of the constants
 * for the {@link #DAY_OF_WEEK DAY_OF_WEEK} field:{@link Calendar#SUNDAY SUNDAY}, ...,{@link Calendar#SATURDAY SATURDAY}.
 * @exception IllegalArgumentExceptionif any of the given date specifiers is invalid,
 * or if any of the calendar fields are inconsistent
 * with the given date specifiers in non-lenient mode
 * @see GregorianCalendar#isWeekDateSupported()
 * @see Calendar#getFirstDayOfWeek()
 * @see Calendar#getMinimalDaysInFirstWeek()
 * @since 1.7
 */
@Override public void setWeekDate(int weekYear,int weekOfYear,int dayOfWeek){
if (dayOfWeek < SUNDAY || dayOfWeek > SATURDAY) {
throw new IllegalArgumentException("invalid dayOfWeek: " + dayOfWeek);
}
GregorianCalendar gc=(GregorianCalendar)clone();
gc.setLenient(true);
int era=gc.get(ERA);
gc.clear();
gc.setTimeZone(TimeZone.getTimeZone("GMT"));
gc.set(ERA,era);
gc.set(YEAR,weekYear);
gc.set(WEEK_OF_YEAR,1);
gc.set(DAY_OF_WEEK,getFirstDayOfWeek());
int days=dayOfWeek - getFirstDayOfWeek();
if (days < 0) {
days+=7;
}
days+=7 * (weekOfYear - 1);
if (days != 0) {
gc.add(DAY_OF_YEAR,days);
}
 else {
gc.complete();
}
if (!isLenient() && (gc.getWeekYear() != weekYear || gc.internalGet(WEEK_OF_YEAR) != weekOfYear || gc.internalGet(DAY_OF_WEEK) != dayOfWeek)) {
throw new IllegalArgumentException();
}
set(ERA,gc.internalGet(ERA));
set(YEAR,gc.internalGet(YEAR));
set(MONTH,gc.internalGet(MONTH));
set(DAY_OF_MONTH,gc.internalGet(DAY_OF_MONTH));
internalSet(WEEK_OF_YEAR,weekOfYear);
complete();
}
/** 
 * Returns the number of weeks in the <a href="#week_year">week year</a>
 * represented by this {@code GregorianCalendar}.
 * <p>For example, if this {@code GregorianCalendar}'s date is
 * December 31, 2008 with <a href="#iso8601_compatible_setting">the ISO
 * 8601 compatible setting</a>, this method will return 53 for the
 * period: December 29, 2008 to January 3, 2010 while {@link #getActualMaximum(int) getActualMaximum(WEEK_OF_YEAR)} will return
 * 52 for the period: December 31, 2007 to December 28, 2008.
 * @return the number of weeks in the week year.
 * @see Calendar#WEEK_OF_YEAR
 * @see #getWeekYear()
 * @see #getActualMaximum(int)
 * @since 1.7
 */
public int getWeeksInWeekYear(){
GregorianCalendar gc=getNormalizedCalendar();
int weekYear=gc.getWeekYear();
if (weekYear == gc.internalGet(YEAR)) {
return gc.getActualMaximum(WEEK_OF_YEAR);
}
if (gc == this) {
gc=(GregorianCalendar)gc.clone();
}
gc.setWeekDate(weekYear,2,internalGet(DAY_OF_WEEK));
return gc.getActualMaximum(WEEK_OF_YEAR);
}
/** 
 * The fixed date corresponding to gdate. If the value is
 * Long.MIN_VALUE, the fixed date value is unknown. Currently,
 * Julian calendar dates are not cached.
 */
transient private long cachedFixedDate=Long.MIN_VALUE;
/** 
 * Converts the time value (millisecond offset from the <a
 * href="Calendar.html#Epoch">Epoch</a>) to calendar field values.
 * The time is <em>not</em>
 * recomputed first; to recompute the time, then the fields, call the
 * <code>complete</code> method.
 * @see Calendar#complete
 */
protected void computeFields(){
int mask=0;
if (isPartiallyNormalized()) {
mask=getSetStateFields();
int fieldMask=~mask & ALL_FIELDS;
if (fieldMask != 0 || calsys == null) {
mask|=computeFields(fieldMask,mask & (ZONE_OFFSET_MASK | DST_OFFSET_MASK));
assert mask == ALL_FIELDS;
}
}
 else {
mask=ALL_FIELDS;
computeFields(mask,0);
}
setFieldsComputed(mask);
}
/** 
 * This computeFields implements the conversion from UTC
 * (millisecond offset from the Epoch) to calendar
 * field values. fieldMask specifies which fields to change the
 * setting state to COMPUTED, although all fields are set to
 * the correct values. This is required to fix 4685354.
 * @param fieldMask a bit mask to specify which fields to change
 * the setting state.
 * @param tzMask a bit mask to specify which time zone offset
 * fields to be used for time calculations
 * @return a new field mask that indicates what field values have
 * actually been set.
 */
private int computeFields(int fieldMask,int tzMask){
int zoneOffset=0;
TimeZone tz=getZone();
if (zoneOffsets == null) {
zoneOffsets=new int[2];
}
if (tzMask != (ZONE_OFFSET_MASK | DST_OFFSET_MASK)) {
if (tz instanceof ZoneInfo) {
zoneOffset=((ZoneInfo)tz).getOffsets(time,zoneOffsets);
}
 else {
zoneOffset=tz.getOffset(time);
zoneOffsets[0]=tz.getRawOffset();
zoneOffsets[1]=zoneOffset - zoneOffsets[0];
}
}
if (tzMask != 0) {
if (isFieldSet(tzMask,ZONE_OFFSET)) {
zoneOffsets[0]=internalGet(ZONE_OFFSET);
}
if (isFieldSet(tzMask,DST_OFFSET)) {
zoneOffsets[1]=internalGet(DST_OFFSET);
}
zoneOffset=zoneOffsets[0] + zoneOffsets[1];
}
long fixedDate=zoneOffset / ONE_DAY;
int timeOfDay=zoneOffset % (int)ONE_DAY;
fixedDate+=time / ONE_DAY;
timeOfDay+=(int)(time % ONE_DAY);
if (timeOfDay >= ONE_DAY) {
timeOfDay-=ONE_DAY;
++fixedDate;
}
 else {
while (timeOfDay < 0) {
timeOfDay+=ONE_DAY;
--fixedDate;
}
}
fixedDate+=EPOCH_OFFSET;
int era=CE;
int year;
if (fixedDate >= gregorianCutoverDate) {
assert cachedFixedDate == Long.MIN_VALUE || gdate.isNormalized() : "cache control: not normalized";
assert cachedFixedDate == Long.MIN_VALUE || gcal.getFixedDate(gdate.getNormalizedYear(),gdate.getMonth(),gdate.getDayOfMonth(),gdate) == cachedFixedDate : "cache control: inconsictency" + ", cachedFixedDate=" + cachedFixedDate + ", computed="+ gcal.getFixedDate(gdate.getNormalizedYear(),gdate.getMonth(),gdate.getDayOfMonth(),gdate)+ ", date="+ gdate;
if (fixedDate != cachedFixedDate) {
gcal.getCalendarDateFromFixedDate(gdate,fixedDate);
cachedFixedDate=fixedDate;
}
year=gdate.getYear();
if (year <= 0) {
year=1 - year;
era=BCE;
}
calsys=gcal;
cdate=gdate;
assert cdate.getDayOfWeek() > 0 : "dow=" + cdate.getDayOfWeek() + ", date="+ cdate;
}
 else {
calsys=getJulianCalendarSystem();
cdate=(BaseCalendar.Date)jcal.newCalendarDate(getZone());
jcal.getCalendarDateFromFixedDate(cdate,fixedDate);
Era e=cdate.getEra();
if (e == jeras[0]) {
era=BCE;
}
year=cdate.getYear();
}
internalSet(ERA,era);
internalSet(YEAR,year);
int mask=fieldMask | (ERA_MASK | YEAR_MASK);
int month=cdate.getMonth() - 1;
int dayOfMonth=cdate.getDayOfMonth();
if ((fieldMask & (MONTH_MASK | DAY_OF_MONTH_MASK | DAY_OF_WEEK_MASK)) != 0) {
internalSet(MONTH,month);
internalSet(DAY_OF_MONTH,dayOfMonth);
internalSet(DAY_OF_WEEK,cdate.getDayOfWeek());
mask|=MONTH_MASK | DAY_OF_MONTH_MASK | DAY_OF_WEEK_MASK;
}
if ((fieldMask & (HOUR_OF_DAY_MASK | AM_PM_MASK | HOUR_MASK| MINUTE_MASK| SECOND_MASK| MILLISECOND_MASK)) != 0) {
if (timeOfDay != 0) {
int hours=timeOfDay / ONE_HOUR;
internalSet(HOUR_OF_DAY,hours);
internalSet(AM_PM,hours / 12);
internalSet(HOUR,hours % 12);
int r=timeOfDay % ONE_HOUR;
internalSet(MINUTE,r / ONE_MINUTE);
r%=ONE_MINUTE;
internalSet(SECOND,r / ONE_SECOND);
internalSet(MILLISECOND,r % ONE_SECOND);
}
 else {
internalSet(HOUR_OF_DAY,0);
internalSet(AM_PM,AM);
internalSet(HOUR,0);
internalSet(MINUTE,0);
internalSet(SECOND,0);
internalSet(MILLISECOND,0);
}
mask|=(HOUR_OF_DAY_MASK | AM_PM_MASK | HOUR_MASK| MINUTE_MASK| SECOND_MASK| MILLISECOND_MASK);
}
if ((fieldMask & (ZONE_OFFSET_MASK | DST_OFFSET_MASK)) != 0) {
internalSet(ZONE_OFFSET,zoneOffsets[0]);
internalSet(DST_OFFSET,zoneOffsets[1]);
mask|=(ZONE_OFFSET_MASK | DST_OFFSET_MASK);
}
if ((fieldMask & (DAY_OF_YEAR_MASK | WEEK_OF_YEAR_MASK | WEEK_OF_MONTH_MASK| DAY_OF_WEEK_IN_MONTH_MASK)) != 0) {
int normalizedYear=cdate.getNormalizedYear();
long fixedDateJan1=calsys.getFixedDate(normalizedYear,1,1,cdate);
int dayOfYear=(int)(fixedDate - fixedDateJan1) + 1;
long fixedDateMonth1=fixedDate - dayOfMonth + 1;
int cutoverGap=0;
int cutoverYear=(calsys == gcal) ? gregorianCutoverYear : gregorianCutoverYearJulian;
int relativeDayOfMonth=dayOfMonth - 1;
if (normalizedYear == cutoverYear) {
if (gregorianCutoverYearJulian <= gregorianCutoverYear) {
fixedDateJan1=getFixedDateJan1(cdate,fixedDate);
if (fixedDate >= gregorianCutoverDate) {
fixedDateMonth1=getFixedDateMonth1(cdate,fixedDate);
}
}
int realDayOfYear=(int)(fixedDate - fixedDateJan1) + 1;
cutoverGap=dayOfYear - realDayOfYear;
dayOfYear=realDayOfYear;
relativeDayOfMonth=(int)(fixedDate - fixedDateMonth1);
}
internalSet(DAY_OF_YEAR,dayOfYear);
internalSet(DAY_OF_WEEK_IN_MONTH,relativeDayOfMonth / 7 + 1);
int weekOfYear=getWeekNumber(fixedDateJan1,fixedDate);
if (weekOfYear == 0) {
long fixedDec31=fixedDateJan1 - 1;
long prevJan1=fixedDateJan1 - 365;
if (normalizedYear > (cutoverYear + 1)) {
if (CalendarUtils.isGregorianLeapYear(normalizedYear - 1)) {
--prevJan1;
}
}
 else if (normalizedYear <= gregorianCutoverYearJulian) {
if (CalendarUtils.isJulianLeapYear(normalizedYear - 1)) {
--prevJan1;
}
}
 else {
BaseCalendar calForJan1=calsys;
int prevYear=getCalendarDate(fixedDec31).getNormalizedYear();
if (prevYear == gregorianCutoverYear) {
calForJan1=getCutoverCalendarSystem();
if (calForJan1 == jcal) {
prevJan1=calForJan1.getFixedDate(prevYear,BaseCalendar.JANUARY,1,null);
}
 else {
prevJan1=gregorianCutoverDate;
calForJan1=gcal;
}
}
 else if (prevYear <= gregorianCutoverYearJulian) {
calForJan1=getJulianCalendarSystem();
prevJan1=calForJan1.getFixedDate(prevYear,BaseCalendar.JANUARY,1,null);
}
}
weekOfYear=getWeekNumber(prevJan1,fixedDec31);
}
 else {
if (normalizedYear > gregorianCutoverYear || normalizedYear < (gregorianCutoverYearJulian - 1)) {
if (weekOfYear >= 52) {
long nextJan1=fixedDateJan1 + 365;
if (cdate.isLeapYear()) {
nextJan1++;
}
long nextJan1st=calsys.getDayOfWeekDateOnOrBefore(nextJan1 + 6,getFirstDayOfWeek());
int ndays=(int)(nextJan1st - nextJan1);
if (ndays >= getMinimalDaysInFirstWeek() && fixedDate >= (nextJan1st - 7)) {
weekOfYear=1;
}
}
}
 else {
BaseCalendar calForJan1=calsys;
int nextYear=normalizedYear + 1;
if (nextYear == (gregorianCutoverYearJulian + 1) && nextYear < gregorianCutoverYear) {
nextYear=gregorianCutoverYear;
}
if (nextYear == gregorianCutoverYear) {
calForJan1=getCutoverCalendarSystem();
}
long nextJan1;
if (nextYear > gregorianCutoverYear || gregorianCutoverYearJulian == gregorianCutoverYear || nextYear == gregorianCutoverYearJulian) {
nextJan1=calForJan1.getFixedDate(nextYear,BaseCalendar.JANUARY,1,null);
}
 else {
nextJan1=gregorianCutoverDate;
calForJan1=gcal;
}
long nextJan1st=calForJan1.getDayOfWeekDateOnOrBefore(nextJan1 + 6,getFirstDayOfWeek());
int ndays=(int)(nextJan1st - nextJan1);
if (ndays >= getMinimalDaysInFirstWeek() && fixedDate >= (nextJan1st - 7)) {
weekOfYear=1;
}
}
}
internalSet(WEEK_OF_YEAR,weekOfYear);
internalSet(WEEK_OF_MONTH,getWeekNumber(fixedDateMonth1,fixedDate));
mask|=(DAY_OF_YEAR_MASK | WEEK_OF_YEAR_MASK | WEEK_OF_MONTH_MASK| DAY_OF_WEEK_IN_MONTH_MASK);
}
return mask;
}
/** 
 * Returns the number of weeks in a period between fixedDay1 and
 * fixedDate. The getFirstDayOfWeek-getMinimalDaysInFirstWeek rule
 * is applied to calculate the number of weeks.
 * @param fixedDay1 the fixed date of the first day of the period
 * @param fixedDate the fixed date of the last day of the period
 * @return the number of weeks of the given period
 */
private final int getWeekNumber(long fixedDay1,long fixedDate){
long fixedDay1st=gcal.getDayOfWeekDateOnOrBefore(fixedDay1 + 6,getFirstDayOfWeek());
int ndays=(int)(fixedDay1st - fixedDay1);
assert ndays <= 7;
if (ndays >= getMinimalDaysInFirstWeek()) {
fixedDay1st-=7;
}
int normalizedDayOfPeriod=(int)(fixedDate - fixedDay1st);
if (normalizedDayOfPeriod >= 0) {
return normalizedDayOfPeriod / 7 + 1;
}
return CalendarUtils.floorDivide(normalizedDayOfPeriod,7) + 1;
}
/** 
 * Converts calendar field values to the time value (millisecond
 * offset from the <a href="Calendar.html#Epoch">Epoch</a>).
 * @exception IllegalArgumentException if any calendar fields are invalid.
 */
protected void computeTime(){
if (!isLenient()) {
if (originalFields == null) {
originalFields=new int[FIELD_COUNT];
}
for (int field=0; field < FIELD_COUNT; field++) {
int value=internalGet(field);
if (isExternallySet(field)) {
if (value < getMinimum(field) || value > getMaximum(field)) {
throw new IllegalArgumentException(getFieldName(field));
}
}
originalFields[field]=value;
}
}
int fieldMask=selectFields();
int year=isSet(YEAR) ? internalGet(YEAR) : EPOCH_YEAR;
int era=internalGetEra();
if (era == BCE) {
year=1 - year;
}
 else if (era != CE) {
throw new IllegalArgumentException("Invalid era");
}
if (year <= 0 && !isSet(ERA)) {
fieldMask|=ERA_MASK;
setFieldsComputed(ERA_MASK);
}
long timeOfDay=0;
if (isFieldSet(fieldMask,HOUR_OF_DAY)) {
timeOfDay+=(long)internalGet(HOUR_OF_DAY);
}
 else {
timeOfDay+=internalGet(HOUR);
if (isFieldSet(fieldMask,AM_PM)) {
timeOfDay+=12 * internalGet(AM_PM);
}
}
timeOfDay*=60;
timeOfDay+=internalGet(MINUTE);
timeOfDay*=60;
timeOfDay+=internalGet(SECOND);
timeOfDay*=1000;
timeOfDay+=internalGet(MILLISECOND);
long fixedDate=timeOfDay / ONE_DAY;
timeOfDay%=ONE_DAY;
while (timeOfDay < 0) {
timeOfDay+=ONE_DAY;
--fixedDate;
}
calculateFixedDate: {
long gfd, jfd;
if (year > gregorianCutoverYear && year > gregorianCutoverYearJulian) {
gfd=fixedDate + getFixedDate(gcal,year,fieldMask);
if (gfd >= gregorianCutoverDate) {
fixedDate=gfd;
break calculateFixedDate;
}
jfd=fixedDate + getFixedDate(getJulianCalendarSystem(),year,fieldMask);
}
 else if (year < gregorianCutoverYear && year < gregorianCutoverYearJulian) {
jfd=fixedDate + getFixedDate(getJulianCalendarSystem(),year,fieldMask);
if (jfd < gregorianCutoverDate) {
fixedDate=jfd;
break calculateFixedDate;
}
gfd=jfd;
}
 else {
jfd=fixedDate + getFixedDate(getJulianCalendarSystem(),year,fieldMask);
gfd=fixedDate + getFixedDate(gcal,year,fieldMask);
}
if (isFieldSet(fieldMask,DAY_OF_YEAR) || isFieldSet(fieldMask,WEEK_OF_YEAR)) {
if (gregorianCutoverYear == gregorianCutoverYearJulian) {
fixedDate=jfd;
break calculateFixedDate;
}
 else if (year == gregorianCutoverYear) {
fixedDate=gfd;
break calculateFixedDate;
}
}
if (gfd >= gregorianCutoverDate) {
if (jfd >= gregorianCutoverDate) {
fixedDate=gfd;
}
 else {
if (calsys == gcal || calsys == null) {
fixedDate=gfd;
}
 else {
fixedDate=jfd;
}
}
}
 else {
if (jfd < gregorianCutoverDate) {
fixedDate=jfd;
}
 else {
if (!isLenient()) {
throw new IllegalArgumentException("the specified date doesn't exist");
}
fixedDate=jfd;
}
}
}
long millis=(fixedDate - EPOCH_OFFSET) * ONE_DAY + timeOfDay;
TimeZone zone=getZone();
if (zoneOffsets == null) {
zoneOffsets=new int[2];
}
int tzMask=fieldMask & (ZONE_OFFSET_MASK | DST_OFFSET_MASK);
if (tzMask != (ZONE_OFFSET_MASK | DST_OFFSET_MASK)) {
if (zone instanceof ZoneInfo) {
((ZoneInfo)zone).getOffsetsByWall(millis,zoneOffsets);
}
 else {
int gmtOffset=isFieldSet(fieldMask,ZONE_OFFSET) ? internalGet(ZONE_OFFSET) : zone.getRawOffset();
zone.getOffsets(millis - gmtOffset,zoneOffsets);
}
}
if (tzMask != 0) {
if (isFieldSet(tzMask,ZONE_OFFSET)) {
zoneOffsets[0]=internalGet(ZONE_OFFSET);
}
if (isFieldSet(tzMask,DST_OFFSET)) {
zoneOffsets[1]=internalGet(DST_OFFSET);
}
}
millis-=zoneOffsets[0] + zoneOffsets[1];
time=millis;
int mask=computeFields(fieldMask | getSetStateFields(),tzMask);
if (!isLenient()) {
for (int field=0; field < FIELD_COUNT; field++) {
if (!isExternallySet(field)) {
continue;
}
if (originalFields[field] != internalGet(field)) {
String s=originalFields[field] + " -> " + internalGet(field);
System.arraycopy(originalFields,0,fields,0,fields.length);
throw new IllegalArgumentException(getFieldName(field) + ": " + s);
}
}
}
setFieldsNormalized(mask);
}
/** 
 * Computes the fixed date under either the Gregorian or the
 * Julian calendar, using the given year and the specified calendar fields.
 * @param cal the CalendarSystem to be used for the date calculation
 * @param year the normalized year number, with 0 indicating the
 * year 1 BCE, -1 indicating 2 BCE, etc.
 * @param fieldMask the calendar fields to be used for the date calculation
 * @return the fixed date
 * @see Calendar#selectFields
 */
private long getFixedDate(BaseCalendar cal,int year,int fieldMask){
int month=JANUARY;
if (isFieldSet(fieldMask,MONTH)) {
month=internalGet(MONTH);
if (month > DECEMBER) {
year+=month / 12;
month%=12;
}
 else if (month < JANUARY) {
int[] rem=new int[1];
year+=CalendarUtils.floorDivide(month,12,rem);
month=rem[0];
}
}
long fixedDate=cal.getFixedDate(year,month + 1,1,cal == gcal ? gdate : null);
if (isFieldSet(fieldMask,MONTH)) {
if (isFieldSet(fieldMask,DAY_OF_MONTH)) {
if (isSet(DAY_OF_MONTH)) {
fixedDate+=internalGet(DAY_OF_MONTH);
fixedDate--;
}
}
 else {
if (isFieldSet(fieldMask,WEEK_OF_MONTH)) {
long firstDayOfWeek=cal.getDayOfWeekDateOnOrBefore(fixedDate + 6,getFirstDayOfWeek());
if ((firstDayOfWeek - fixedDate) >= getMinimalDaysInFirstWeek()) {
firstDayOfWeek-=7;
}
if (isFieldSet(fieldMask,DAY_OF_WEEK)) {
firstDayOfWeek=cal.getDayOfWeekDateOnOrBefore(firstDayOfWeek + 6,internalGet(DAY_OF_WEEK));
}
fixedDate=firstDayOfWeek + 7 * (internalGet(WEEK_OF_MONTH) - 1);
}
 else {
int dayOfWeek;
if (isFieldSet(fieldMask,DAY_OF_WEEK)) {
dayOfWeek=internalGet(DAY_OF_WEEK);
}
 else {
dayOfWeek=getFirstDayOfWeek();
}
int dowim;
if (isFieldSet(fieldMask,DAY_OF_WEEK_IN_MONTH)) {
dowim=internalGet(DAY_OF_WEEK_IN_MONTH);
}
 else {
dowim=1;
}
if (dowim >= 0) {
fixedDate=cal.getDayOfWeekDateOnOrBefore(fixedDate + (7 * dowim) - 1,dayOfWeek);
}
 else {
int lastDate=monthLength(month,year) + (7 * (dowim + 1));
fixedDate=cal.getDayOfWeekDateOnOrBefore(fixedDate + lastDate - 1,dayOfWeek);
}
}
}
}
 else {
if (year == gregorianCutoverYear && cal == gcal && fixedDate < gregorianCutoverDate && gregorianCutoverYear != gregorianCutoverYearJulian) {
fixedDate=gregorianCutoverDate;
}
if (isFieldSet(fieldMask,DAY_OF_YEAR)) {
fixedDate+=internalGet(DAY_OF_YEAR);
fixedDate--;
}
 else {
long firstDayOfWeek=cal.getDayOfWeekDateOnOrBefore(fixedDate + 6,getFirstDayOfWeek());
if ((firstDayOfWeek - fixedDate) >= getMinimalDaysInFirstWeek()) {
firstDayOfWeek-=7;
}
if (isFieldSet(fieldMask,DAY_OF_WEEK)) {
int dayOfWeek=internalGet(DAY_OF_WEEK);
if (dayOfWeek != getFirstDayOfWeek()) {
firstDayOfWeek=cal.getDayOfWeekDateOnOrBefore(firstDayOfWeek + 6,dayOfWeek);
}
}
fixedDate=firstDayOfWeek + 7 * ((long)internalGet(WEEK_OF_YEAR) - 1);
}
}
return fixedDate;
}
/** 
 * Returns this object if it's normalized (all fields and time are
 * in sync). Otherwise, a cloned object is returned after calling
 * complete() in lenient mode.
 */
private final GregorianCalendar getNormalizedCalendar(){
GregorianCalendar gc;
if (isFullyNormalized()) {
gc=this;
}
 else {
gc=(GregorianCalendar)this.clone();
gc.setLenient(true);
gc.complete();
}
return gc;
}
/** 
 * Returns the Julian calendar system instance (singleton). 'jcal'
 * and 'jeras' are set upon the return.
 */
synchronized private static final BaseCalendar getJulianCalendarSystem(){
if (jcal == null) {
jcal=(JulianCalendar)CalendarSystem.forName("julian");
jeras=jcal.getEras();
}
return jcal;
}
/** 
 * Returns the calendar system for dates before the cutover date
 * in the cutover year. If the cutover date is January 1, the
 * method returns Gregorian. Otherwise, Julian.
 */
private BaseCalendar getCutoverCalendarSystem(){
if (gregorianCutoverYearJulian < gregorianCutoverYear) {
return gcal;
}
return getJulianCalendarSystem();
}
/** 
 * Determines if the specified year (normalized) is the Gregorian
 * cutover year. This object must have been normalized.
 */
private final boolean isCutoverYear(int normalizedYear){
int cutoverYear=(calsys == gcal) ? gregorianCutoverYear : gregorianCutoverYearJulian;
return normalizedYear == cutoverYear;
}
/** 
 * Returns the fixed date of the first day of the year (usually
 * January 1) before the specified date.
 * @param date the date for which the first day of the year is
 * calculated. The date has to be in the cut-over year (Gregorian
 * or Julian).
 * @param fixedDate the fixed date representation of the date
 */
private final long getFixedDateJan1(BaseCalendar.Date date,long fixedDate){
assert date.getNormalizedYear() == gregorianCutoverYear || date.getNormalizedYear() == gregorianCutoverYearJulian;
if (gregorianCutoverYear != gregorianCutoverYearJulian) {
if (fixedDate >= gregorianCutoverDate) {
return gregorianCutoverDate;
}
}
BaseCalendar jcal=getJulianCalendarSystem();
return jcal.getFixedDate(date.getNormalizedYear(),BaseCalendar.JANUARY,1,null);
}
/** 
 * Returns the fixed date of the first date of the month (usually
 * the 1st of the month) before the specified date.
 * @param date the date for which the first day of the month is
 * calculated. The date has to be in the cut-over year (Gregorian
 * or Julian).
 * @param fixedDate the fixed date representation of the date
 */
private final long getFixedDateMonth1(BaseCalendar.Date date,long fixedDate){
assert date.getNormalizedYear() == gregorianCutoverYear || date.getNormalizedYear() == gregorianCutoverYearJulian;
BaseCalendar.Date gCutover=getGregorianCutoverDate();
if (gCutover.getMonth() == BaseCalendar.JANUARY && gCutover.getDayOfMonth() == 1) {
return fixedDate - date.getDayOfMonth() + 1;
}
long fixedDateMonth1;
if (date.getMonth() == gCutover.getMonth()) {
BaseCalendar.Date jLastDate=getLastJulianDate();
if (gregorianCutoverYear == gregorianCutoverYearJulian && gCutover.getMonth() == jLastDate.getMonth()) {
fixedDateMonth1=jcal.getFixedDate(date.getNormalizedYear(),date.getMonth(),1,null);
}
 else {
fixedDateMonth1=gregorianCutoverDate;
}
}
 else {
fixedDateMonth1=fixedDate - date.getDayOfMonth() + 1;
}
return fixedDateMonth1;
}
/** 
 * Returns a CalendarDate produced from the specified fixed date.
 * @param fd the fixed date
 */
private final BaseCalendar.Date getCalendarDate(long fd){
BaseCalendar cal=(fd >= gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
BaseCalendar.Date d=(BaseCalendar.Date)cal.newCalendarDate(TimeZone.NO_TIMEZONE);
cal.getCalendarDateFromFixedDate(d,fd);
return d;
}
/** 
 * Returns the Gregorian cutover date as a BaseCalendar.Date. The
 * date is a Gregorian date.
 */
private final BaseCalendar.Date getGregorianCutoverDate(){
return getCalendarDate(gregorianCutoverDate);
}
/** 
 * Returns the day before the Gregorian cutover date as a
 * BaseCalendar.Date. The date is a Julian date.
 */
private final BaseCalendar.Date getLastJulianDate(){
return getCalendarDate(gregorianCutoverDate - 1);
}
/** 
 * Returns the length of the specified month in the specified
 * year. The year number must be normalized.
 * @see #isLeapYear(int)
 */
private final int monthLength(int month,int year){
return isLeapYear(year) ? LEAP_MONTH_LENGTH[month] : MONTH_LENGTH[month];
}
/** 
 * Returns the length of the specified month in the year provided
 * by internalGet(YEAR).
 * @see #isLeapYear(int)
 */
private final int monthLength(int month){
int year=internalGet(YEAR);
if (internalGetEra() == BCE) {
year=1 - year;
}
return monthLength(month,year);
}
private final int actualMonthLength(){
int year=cdate.getNormalizedYear();
if (year != gregorianCutoverYear && year != gregorianCutoverYearJulian) {
return calsys.getMonthLength(cdate);
}
BaseCalendar.Date date=(BaseCalendar.Date)cdate.clone();
long fd=calsys.getFixedDate(date);
long month1=getFixedDateMonth1(date,fd);
long next1=month1 + calsys.getMonthLength(date);
if (next1 < gregorianCutoverDate) {
return (int)(next1 - month1);
}
if (cdate != gdate) {
date=(BaseCalendar.Date)gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
}
gcal.getCalendarDateFromFixedDate(date,next1);
next1=getFixedDateMonth1(date,next1);
return (int)(next1 - month1);
}
/** 
 * Returns the length (in days) of the specified year. The year
 * must be normalized.
 */
private final int yearLength(int year){
return isLeapYear(year) ? 366 : 365;
}
/** 
 * Returns the length (in days) of the year provided by
 * internalGet(YEAR).
 */
private final int yearLength(){
int year=internalGet(YEAR);
if (internalGetEra() == BCE) {
year=1 - year;
}
return yearLength(year);
}
/** 
 * After adjustments such as add(MONTH), add(YEAR), we don't want the
 * month to jump around.  E.g., we don't want Jan 31 + 1 month to go to Mar
 * 3, we want it to go to Feb 28.  Adjustments which might run into this
 * problem call this method to retain the proper month.
 */
private final void pinDayOfMonth(){
int year=internalGet(YEAR);
int monthLen;
if (year > gregorianCutoverYear || year < gregorianCutoverYearJulian) {
monthLen=monthLength(internalGet(MONTH));
}
 else {
GregorianCalendar gc=getNormalizedCalendar();
monthLen=gc.getActualMaximum(DAY_OF_MONTH);
}
int dom=internalGet(DAY_OF_MONTH);
if (dom > monthLen) {
set(DAY_OF_MONTH,monthLen);
}
}
/** 
 * Returns the fixed date value of this object. The time value and
 * calendar fields must be in synch.
 */
private final long getCurrentFixedDate(){
return (calsys == gcal) ? cachedFixedDate : calsys.getFixedDate(cdate);
}
/** 
 * Returns the new value after 'roll'ing the specified value and amount.
 */
private static final int getRolledValue(int value,int amount,int min,int max){
assert value >= min && value <= max;
int range=max - min + 1;
amount%=range;
int n=value + amount;
if (n > max) {
n-=range;
}
 else if (n < min) {
n+=range;
}
assert n >= min && n <= max;
return n;
}
/** 
 * Returns the ERA.  We need a special method for this because the
 * default ERA is CE, but a zero (unset) ERA is BCE.
 */
private final int internalGetEra(){
return isSet(ERA) ? internalGet(ERA) : CE;
}
/** 
 * Updates internal state.
 */
private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
stream.defaultReadObject();
if (gdate == null) {
gdate=(BaseCalendar.Date)gcal.newCalendarDate(getZone());
cachedFixedDate=Long.MIN_VALUE;
}
setGregorianChange(gregorianCutover);
}
}

package sun.security.jgss;
import org.ietf.jgss.MessageProp;
import java.util.LinkedList;
/** 
 * A utility class that implements a number list that keeps track of which
 * tokens have arrived by storing their token numbers in the list. It helps
 * detect old tokens, out of sequence tokens, and duplicate tokens.
 * Each element of the list is an interval [a, b]. Its existence in the
 * list implies that all token numbers in the range a, a+1, ..., b-1, b
 * have arrived. Gaps in arrived token numbers are represented by the
 * numbers that fall in between two elements of the list. eg. {[a,b],
 * [c,d]} indicates that the token numbers b+1, ..., c-1 have not arrived
 * yet.
 * The maximum number of intervals that we keep track of is
 * MAX_INTERVALS. Thus if there are too many gaps, then some of the older
 * sequence numbers are deleted from the list. The earliest sequence number
 * that exists in the list is the windowStart. The next expected sequence
 * number, or expectedNumber, is one greater than the latest sequence
 * number in the list.
 * The list keeps track the first token number that should have arrived
 * (initNumber) so that it is able to detect if certain numbers occur after
 * the first valid token number but before windowStart. That would happen
 * if the number of elements (intervals) exceeds MAX_INTERVALS and some
 * initial elements had  to be deleted.
 * The working of the list is optimized for the normal case where the
 * tokens arrive in sequence.
 * @author Mayank Upadhyay
 * @since 1.4
 */
public class TokenTracker {
  static final int MAX_INTERVALS=5;
  private int initNumber;
  private int windowStart;
  private int expectedNumber;
  private int windowStartIndex=0;
  private LinkedList<Entry> list=new LinkedList<Entry>();
  public TokenTracker(  int initNumber){
    this.initNumber=initNumber;
    this.windowStart=initNumber;
    this.expectedNumber=initNumber;
    Entry entry=new Entry(initNumber - 1);
    list.add(entry);
  }
  /** 
 * Returns the index for the entry into which this number will fit. If
 * there is none, it returns the index of the last interval
 * which precedes this number. It returns -1 if the number needs to be
 * a in a new interval ahead of the whole list.
 */
  private int getIntervalIndex(  int number){
    Entry entry=null;
    int i;
    for (i=list.size() - 1; i >= 0; i--) {
      entry=list.get(i);
      if (entry.compareTo(number) <= 0)       break;
    }
    return i;
  }
  /** 
 * Sets the sequencing and replay information for the given token
 * number.
 * The following represents the number line with positions of
 * initNumber, windowStart, expectedNumber marked on it. Regions in
 * between them show the different sequencing and replay state
 * possibilites for tokens that fall in there.
 * (1)      windowStart
 * initNumber               expectedNumber
 * |                           |
 * ---|---------------------------|---
 * GAP |    DUP/UNSEQ              | GAP
 * (2)       initNumber   windowStart   expectedNumber
 * |               |              |
 * ---|---------------|--------------|---
 * GAP |      OLD      |  DUP/UNSEQ   | GAP
 * (3)                                windowStart
 * expectedNumber            initNumber
 * |                           |
 * ---|---------------------------|---
 * DUP/UNSEQ |           GAP             | DUP/UNSEQ
 * (4)      expectedNumber    initNumber   windowStart
 * |               |              |
 * ---|---------------|--------------|---
 * DUP/UNSEQ |        GAP    |    OLD       | DUP/UNSEQ
 * (5)      windowStart   expectedNumber    initNumber
 * |               |              |
 * ---|---------------|--------------|---
 * OLD |    DUP/UNSEQ  |     GAP      | OLD
 * (This analysis leaves out the possibility that expectedNumber passes
 * initNumber after wrapping around. That may be added later.)
 */
  synchronized public final void getProps(  int number,  MessageProp prop){
    boolean gap=false;
    boolean old=false;
    boolean unsequenced=false;
    boolean duplicate=false;
    int pos=getIntervalIndex(number);
    Entry entry=null;
    if (pos != -1)     entry=list.get(pos);
    if (number == expectedNumber) {
      expectedNumber++;
    }
 else {
      if (entry != null && entry.contains(number))       duplicate=true;
 else {
        if (expectedNumber >= initNumber) {
          if (number > expectedNumber) {
            gap=true;
          }
 else           if (number >= windowStart) {
            unsequenced=true;
          }
 else           if (number >= initNumber) {
            old=true;
          }
 else {
            gap=true;
          }
        }
 else {
          if (number > expectedNumber) {
            if (number < initNumber) {
              gap=true;
            }
 else             if (windowStart >= initNumber) {
              if (number >= windowStart) {
                unsequenced=true;
              }
 else               old=true;
            }
 else {
              old=true;
            }
          }
 else           if (windowStart > expectedNumber) {
            unsequenced=true;
          }
 else           if (number < windowStart) {
            old=true;
          }
 else           unsequenced=true;
        }
      }
    }
    if (!duplicate && !old)     add(number,pos);
    if (gap)     expectedNumber=number + 1;
    prop.setSupplementaryStates(duplicate,old,unsequenced,gap,0,null);
  }
  /** 
 * Adds the number to the list just after the entry that is currently
 * at position prevEntryPos. If prevEntryPos is -1, then the number
 * will begin a new interval at the front of the list.
 */
  private void add(  int number,  int prevEntryPos){
    Entry entry;
    Entry entryBefore=null;
    Entry entryAfter=null;
    boolean appended=false;
    boolean prepended=false;
    if (prevEntryPos != -1) {
      entryBefore=list.get(prevEntryPos);
      if (number == (entryBefore.getEnd() + 1)) {
        entryBefore.setEnd(number);
        appended=true;
      }
    }
    int nextEntryPos=prevEntryPos + 1;
    if ((nextEntryPos) < list.size()) {
      entryAfter=list.get(nextEntryPos);
      if (number == (entryAfter.getStart() - 1)) {
        if (!appended) {
          entryAfter.setStart(number);
        }
 else {
          entryAfter.setStart(entryBefore.getStart());
          list.remove(prevEntryPos);
          if (windowStartIndex > prevEntryPos)           windowStartIndex--;
        }
        prepended=true;
      }
    }
    if (prepended || appended)     return;
    if (list.size() < MAX_INTERVALS) {
      entry=new Entry(number);
      if (prevEntryPos < windowStartIndex)       windowStartIndex++;
    }
 else {
      int oldWindowStartIndex=windowStartIndex;
      if (windowStartIndex == (list.size() - 1))       windowStartIndex=0;
      entry=list.remove(oldWindowStartIndex);
      windowStart=list.get(windowStartIndex).getStart();
      entry.setStart(number);
      entry.setEnd(number);
      if (prevEntryPos >= oldWindowStartIndex) {
        prevEntryPos--;
      }
 else {
        if (oldWindowStartIndex != windowStartIndex) {
          if (prevEntryPos == -1)           windowStart=number;
        }
 else {
          windowStartIndex++;
        }
      }
    }
    list.add(prevEntryPos + 1,entry);
  }
  public String toString(){
    StringBuffer buf=new StringBuffer("TokenTracker: ");
    buf.append(" initNumber=").append(initNumber);
    buf.append(" windowStart=").append(windowStart);
    buf.append(" expectedNumber=").append(expectedNumber);
    buf.append(" windowStartIndex=").append(windowStartIndex);
    buf.append("\n\tIntervals are: {");
    for (int i=0; i < list.size(); i++) {
      if (i != 0)       buf.append(", ");
      buf.append(list.get(i).toString());
    }
    buf.append('}');
    return buf.toString();
  }
  /** 
 * An entry in the list that represents the sequence of received
 * tokens. Each entry is actaully an interval of numbers, all of which
 * have been received.
 */
class Entry {
    private int start;
    private int end;
    Entry(    int number){
      start=number;
      end=number;
    }
    /** 
 * Returns -1 if this interval represented by this entry precedes
 * the number, 0 if the the number is contained in the interval,
 * and -1 if the interval occurs after the number.
 */
    final int compareTo(    int number){
      if (start > number)       return 1;
 else       if (end < number)       return -1;
 else       return 0;
    }
    final boolean contains(    int number){
      return (number >= start && number <= end);
    }
    final void append(    int number){
      if (number == (end + 1))       end=number;
    }
    final void setInterval(    int start,    int end){
      this.start=start;
      this.end=end;
    }
    final void setEnd(    int end){
      this.end=end;
    }
    final void setStart(    int start){
      this.start=start;
    }
    final int getStart(){
      return start;
    }
    final int getEnd(){
      return end;
    }
    public String toString(){
      return ("[" + start + ", "+ end+ "]");
    }
  }
}

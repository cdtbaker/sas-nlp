package edu.umd.cs.piccolo;
import java.util.ArrayList;
import java.util.Iterator;
/** 
 * Performance log.
 */
public class PerformanceLog {
  private final ArrayList log=new ArrayList();
  private long testTime;
public static class ZLogEntry {
    public String name;
    public long time;
    public ZLogEntry(    final String aName,    final long aTime){
      name=aName;
      time=aTime;
    }
  }
  public void startTest(){
    Runtime.getRuntime().gc();
    testTime=System.currentTimeMillis();
  }
  public void endTest(  final String name){
    testTime=System.currentTimeMillis() - testTime;
    addEntry(name,testTime);
    System.gc();
  }
  public void addEntry(  final String aName,  final long aTime){
    log.add(new ZLogEntry(aName,aTime));
  }
  public void clear(){
    log.clear();
  }
  public void writeLog(){
    System.out.println();
    System.out.println("Test data for input into spreadsheet:");
    System.out.println();
    Iterator i=log.iterator();
    while (i.hasNext()) {
      final ZLogEntry each=(ZLogEntry)i.next();
      System.out.println(each.time);
    }
    System.out.println();
    System.out.println("Labled test results, see above for simple column \n of times for input into spreadsheet:");
    System.out.println();
    i=log.iterator();
    while (i.hasNext()) {
      final ZLogEntry each=(ZLogEntry)i.next();
      System.out.println(each.name + ", " + each.time);
    }
  }
}

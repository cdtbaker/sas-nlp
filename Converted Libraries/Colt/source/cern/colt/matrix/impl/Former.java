package cern.colt.matrix.impl;
/** 
 * Formats a double into a string (like sprintf in C).
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 21/07/00
 * @see java.util.Comparator
 * @see cern.colt
 * @see cern.colt.Sorting
 */
public interface Former {
  /** 
 * Formats a double into a string (like sprintf in C).
 * @param x the number to format
 * @return the formatted string 
 * @exception IllegalArgumentException if bad argument
 */
  String form(  double value);
}

package com.sun.media.sound;
/** 
 * <code>SoftControl</code> are the basic controls
 * used for control-rate processing.
 * @author Karl Helgason
 */
public interface SoftControl {
  public double[] get(  int instance,  String name);
}

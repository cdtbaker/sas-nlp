package com.sun.tools.hat.internal.parser;
import java.io.IOException;
/** 
 * Positionable read only buffer
 * @author A. Sundararajan
 */
public interface ReadBuffer {
  public void get(  long pos,  byte[] buf) throws IOException ;
  public char getChar(  long pos) throws IOException ;
  public byte getByte(  long pos) throws IOException ;
  public short getShort(  long pos) throws IOException ;
  public int getInt(  long pos) throws IOException ;
  public long getLong(  long pos) throws IOException ;
}

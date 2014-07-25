package edu.umd.cs.piccolo.util;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import junit.framework.TestCase;
/** 
 * Unit test for PUtil.
 */
public class PUtilTest extends TestCase {
  public void testPreventCodeCleanFinal(){
    final Enumeration ne=PUtil.NULL_ENUMERATION;
    try {
      PUtil.NULL_ENUMERATION=null;
    }
  finally {
      PUtil.NULL_ENUMERATION=ne;
    }
    final Iterator ni=PUtil.NULL_ITERATOR;
    try {
      PUtil.NULL_ITERATOR=null;
    }
  finally {
      PUtil.NULL_ITERATOR=ni;
    }
    final OutputStream no=PUtil.NULL_OUTPUT_STREAM;
    try {
      PUtil.NULL_OUTPUT_STREAM=null;
    }
  finally {
      PUtil.NULL_OUTPUT_STREAM=no;
    }
  }
}

package com.sun.tools.hat.internal.oql;
/** 
 * This visitor is supplied to OQLEngine.executeQuery
 * to receive result set objects one by one.
 */
public interface ObjectVisitor {
  public boolean visit(  Object o);
}

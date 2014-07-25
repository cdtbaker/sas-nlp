package org.apache.commons.math3.exception.util;
/** 
 * Interface for accessing the context data structure stored in Commons Math
 * exceptions.
 * @version $Id: ExceptionContextProvider.java 1364388 2012-07-22 18:16:43Z tn $
 */
public interface ExceptionContextProvider {
  /** 
 * Gets a reference to the "rich context" data structure that allows to
 * customize error messages and store key, value pairs in exceptions.
 * @return a reference to the exception context.
 */
  ExceptionContext getContext();
}

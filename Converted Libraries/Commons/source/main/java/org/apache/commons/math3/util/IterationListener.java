package org.apache.commons.math3.util;
import java.util.EventListener;
/** 
 * The listener interface for receiving events occurring in an iterative
 * algorithm.
 * @version $Id: IterationListener.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface IterationListener extends EventListener {
  /** 
 * Invoked after completion of the initial phase of the iterative algorithm
 * (prior to the main iteration loop).
 * @param e The {@link IterationEvent} object.
 */
  void initializationPerformed(  IterationEvent e);
  /** 
 * Invoked each time an iteration is completed (in the main iteration loop).
 * @param e The {@link IterationEvent} object.
 */
  void iterationPerformed(  IterationEvent e);
  /** 
 * Invoked each time a new iteration is completed (in the main iteration
 * loop).
 * @param e The {@link IterationEvent} object.
 */
  void iterationStarted(  IterationEvent e);
  /** 
 * Invoked after completion of the operations which occur after breaking out
 * of the main iteration loop.
 * @param e The {@link IterationEvent} object.
 */
  void terminationPerformed(  IterationEvent e);
}

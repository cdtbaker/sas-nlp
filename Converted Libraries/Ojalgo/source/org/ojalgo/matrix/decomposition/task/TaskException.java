package org.ojalgo.matrix.decomposition.task;
import org.ojalgo.RecoverableCondition;
public class TaskException extends RecoverableCondition {
  public TaskException(  String aDescription){
    super(aDescription);
  }
}

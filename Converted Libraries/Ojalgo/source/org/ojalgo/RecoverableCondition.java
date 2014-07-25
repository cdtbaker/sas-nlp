package org.ojalgo;
/** 
 * Something that potentially could go wrong, actually did go wrong. The API
 * user is expected to recover and continue execution. Is always declared to be
 * thrown, and must be caught.
 * @author apete
 */
public class RecoverableCondition extends Exception {
  public RecoverableCondition(  final String description){
    super(description);
  }
  @SuppressWarnings("unused") private RecoverableCondition(){
    super();
  }
  @Override public String toString(){
    final String retVal=this.getClass().getSimpleName();
    final String tmpMessage=this.getLocalizedMessage();
    return (tmpMessage != null) ? (retVal + ": " + tmpMessage) : retVal;
  }
}

package org.ojalgo;
/** 
 * Incorrect use of the API. The code needs to be changed. Typically execution
 * can't continue. Is never declared to be thrown, and should not be caught.
 * @author apete
 */
public class ProgrammingError extends RuntimeException {
  /** 
 * For hidden, not-to-be-used, constructors and methods.
 */
  public static void throwForIllegalInvocation(){
    throw new ProgrammingError("Don't use this method/constructor!");
  }
  public static void throwForTryingToModifyAnImmutableObject(){
    throw new ProgrammingError("This class is immutable!");
  }
  public static void throwForUnsupportedOptionalOperation(){
    throw new ProgrammingError("Unsupported optional operation!");
  }
  /** 
 * Instead of IllegalArgumentException and/or NullPointerException.
 * @param obj
 */
  public static void throwIfNull(  final Object obj){
    if (obj == null) {
      throw new ProgrammingError("Null object reference!");
    }
  }
  public ProgrammingError(  final String aString){
    super(aString);
  }
  public ProgrammingError(  final Throwable someCause){
    super(someCause);
  }
  @SuppressWarnings("unused") private ProgrammingError(){
    super();
  }
  ProgrammingError(  final String someMessage,  final Throwable someCause){
    super(someMessage,someCause);
  }
  @Override public String toString(){
    final String retVal=this.getClass().getSimpleName();
    final String tmpMessage=this.getLocalizedMessage();
    return (tmpMessage != null) ? (retVal + ": " + tmpMessage) : retVal;
  }
}

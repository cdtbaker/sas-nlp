package org.ojalgo.type;
/** 
 * Alternator
 * @author apete
 */
public class Alternator<T> {
  private final T myAlternativeA;
  private final T myAlternativeB;
  private boolean mySwitch;
  public Alternator(  final T anAlternativeA,  final T anAlternativeB){
    super();
    mySwitch=false;
    myAlternativeA=anAlternativeA;
    myAlternativeB=anAlternativeB;
  }
  @SuppressWarnings("unused") private Alternator(){
    this(null,null);
  }
  public T get(){
    return (mySwitch=!mySwitch) ? myAlternativeA : myAlternativeB;
  }
}

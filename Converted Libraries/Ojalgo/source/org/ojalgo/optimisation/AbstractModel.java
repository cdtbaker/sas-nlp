package org.ojalgo.optimisation;
import java.io.Serializable;
public abstract class AbstractModel<S extends Optimisation.Solver> implements Optimisation.Model, Serializable {
  public final Optimisation.Options options;
  private boolean myMinimisation=true;
  protected AbstractModel(){
    super();
    options=new Optimisation.Options();
  }
  protected AbstractModel(  final Optimisation.Options someOptions){
    super();
    options=someOptions;
  }
  public abstract void destroy();
  public final boolean isMaximisation(){
    return !myMinimisation;
  }
  public final boolean isMinimisation(){
    return myMinimisation;
  }
  public final void setMaximisation(  final boolean aFlag){
    myMinimisation=!aFlag;
  }
  public final void setMinimisation(  final boolean aFlag){
    myMinimisation=aFlag;
  }
}

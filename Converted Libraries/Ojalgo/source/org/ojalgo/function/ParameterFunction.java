package org.ojalgo.function;
public abstract class ParameterFunction<N extends Number> implements Function<N> {
  /** 
 * A {@linkplain ParameterFunction} with a set/fixed parameter.
 * @author apete
 */
public static final class FixedParameter<N extends Number> implements UnaryFunction<N> {
    private final ParameterFunction<N> myFunction;
    private final int myParameter;
    @SuppressWarnings("unused") private FixedParameter(){
      this(null,0);
    }
    FixedParameter(    final ParameterFunction<N> aFunc,    final int aParam){
      super();
      myFunction=aFunc;
      myParameter=aParam;
    }
    public final ParameterFunction<N> getFunction(){
      return myFunction;
    }
    public final int getParameter(){
      return myParameter;
    }
    public final double invoke(    final double aFirstArg){
      return myFunction.invoke(aFirstArg,myParameter);
    }
    public final N invoke(    final N aFirstArg){
      return myFunction.invoke(aFirstArg,myParameter);
    }
  }
  protected ParameterFunction(){
    super();
  }
  public abstract double invoke(  double arg,  int param);
  public abstract N invoke(  N arg,  int param);
  public final UnaryFunction<N> parameter(  final int param){
    return new FixedParameter<N>(this,param);
  }
}

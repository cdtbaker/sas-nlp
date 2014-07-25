package org.ojalgo.matrix.store;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.scalar.Scalar;
public class ModificationStore<N extends Number> extends LogicalStore<N> {
  private final UnaryFunction<N> myFunction;
  public ModificationStore(  final MatrixStore<N> aBase,  final UnaryFunction<N> aFunc){
    super(aBase.getRowDim(),aBase.getColDim(),aBase);
    myFunction=aFunc;
  }
  private ModificationStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    super(aRowDim,aColDim,aBase);
    myFunction=null;
  }
  public double doubleValue(  final long aRow,  final long aCol){
    return myFunction.invoke(this.getBase().doubleValue(aRow,aCol));
  }
  public N get(  final long aRow,  final long aCol){
    return myFunction.invoke(this.getBase().get(aRow,aCol));
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return this.factory().toScalar(this.get(row,column));
  }
}

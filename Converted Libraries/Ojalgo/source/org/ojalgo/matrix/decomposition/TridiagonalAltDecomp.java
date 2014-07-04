package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.type.context.NumberContext;
class TridiagonalAltDecomp extends InPlaceDecomposition<Double> implements Tridiagonal<Double> {
  SimpleArray<Double> myMain;
  SimpleArray<Double> myOff;
  public TridiagonalAltDecomp(){
    super(PrimitiveDenseStore.FACTORY);
  }
  public boolean compute(  final Access2D<?> matrix){
    this.setInPlace(matrix);
    final PrimitiveDenseStore tmpStore=(PrimitiveDenseStore)this.getInPlace();
    myMain=SimpleArray.makePrimitive(tmpStore.getMinDim());
    myOff=SimpleArray.makePrimitive(tmpStore.getMinDim());
    this.getInPlace().tred2(myMain,myOff,true);
    return true;
  }
  public boolean equals(  final MatrixStore<Double> other,  final NumberContext context){
    return MatrixUtils.equals(other,this,context);
  }
  public MatrixStore<Double> getD(){
    final Array1D<Double> tmpMain=Array1D.PRIMITIVE.wrap(myMain);
    final Array1D<Double> tmpOff=Array1D.PRIMITIVE.wrap(myOff).subList(1,myOff.length);
    final DiagonalAccess<Double> tmpAccess=DiagonalAccess.makePrimitive(tmpMain,tmpOff,tmpOff);
    return this.wrap(tmpAccess);
  }
  public MatrixStore<Double> getQ(){
    return this.getInPlace();
  }
  public boolean isFullSize(){
    return true;
  }
  public boolean isSolvable(){
    return false;
  }
  public MatrixStore<Double> reconstruct(){
    return MatrixUtils.reconstruct(this);
  }
}

package org.ojalgo.matrix;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;
/** 
 * PrimitiveMatrix
 * @author apete
 */
public final class PrimitiveMatrix extends AbstractMatrix<Double,PrimitiveMatrix> {
  public static final BasicMatrix.Factory<PrimitiveMatrix> FACTORY=new MatrixFactory<Double,PrimitiveMatrix>(PrimitiveMatrix.class,PrimitiveDenseStore.FACTORY);
  public static Builder<PrimitiveMatrix> getBuilder(  final int aLength){
    return FACTORY.getBuilder(aLength);
  }
  public static Builder<PrimitiveMatrix> getBuilder(  final int aRowDim,  final int aColDim){
    return FACTORY.getBuilder(aRowDim,aColDim);
  }
  /** 
 * This method is for internal use only - YOU should NOT use it!
 */
  PrimitiveMatrix(  final MatrixStore<Double> aStore){
    super(aStore);
  }
  public PrimitiveMatrix enforce(  final NumberContext aContext){
    return this.modify(aContext.getPrimitiveEnforceFunction());
  }
  public PrimitiveMatrix round(  final NumberContext aContext){
    return this.modify(aContext.getPrimitiveRoundFunction());
  }
  public BigDecimal toBigDecimal(  final int row,  final int column){
    return new BigDecimal(this.getStore().doubleValue(row,column));
  }
  public ComplexNumber toComplexNumber(  final int row,  final int column){
    return ComplexNumber.makeReal(this.getStore().doubleValue(row,column));
  }
  @Override public PhysicalStore<Double> toPrimitiveStore(){
    return this.getStore().copy();
  }
  public String toString(  final int row,  final int column){
    return Double.toString(this.doubleValue(row,column));
  }
  @SuppressWarnings("unchecked") @Override MatrixFactory<Double,PrimitiveMatrix> getFactory(){
    return (MatrixFactory<Double,PrimitiveMatrix>)FACTORY;
  }
  @SuppressWarnings("unchecked") @Override MatrixStore<Double> getStoreFrom(  final Access1D<?> aMtrx){
    if (aMtrx instanceof PrimitiveMatrix) {
      return ((PrimitiveMatrix)aMtrx).getStore();
    }
 else     if (aMtrx instanceof PrimitiveDenseStore) {
      return (PrimitiveDenseStore)aMtrx;
    }
 else     if ((aMtrx instanceof MatrixStore) && !this.isEmpty() && (aMtrx.get(0) instanceof Double)) {
      return (MatrixStore<Double>)aMtrx;
    }
 else     if (aMtrx instanceof Access2D<?>) {
      return this.getPhysicalFactory().copy((Access2D<?>)aMtrx);
    }
 else {
      return this.getPhysicalFactory().columns(aMtrx);
    }
  }
}

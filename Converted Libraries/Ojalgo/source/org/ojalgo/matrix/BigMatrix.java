package org.ojalgo.matrix;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;
/** 
 * BigMatrix
 * @author apete
 */
public final class BigMatrix extends AbstractMatrix<BigDecimal,BigMatrix> {
  public static final BasicMatrix.Factory<BigMatrix> FACTORY=new MatrixFactory<BigDecimal,BigMatrix>(BigMatrix.class,BigDenseStore.FACTORY);
  public static Builder<BigMatrix> getBuilder(  final int aLength){
    return FACTORY.getBuilder(aLength);
  }
  public static Builder<BigMatrix> getBuilder(  final int aRowDim,  final int aColDim){
    return FACTORY.getBuilder(aRowDim,aColDim);
  }
  /** 
 * This method is for internal use only - YOU should NOT use it!
 */
  BigMatrix(  final MatrixStore<BigDecimal> aStore){
    super(aStore);
  }
  public BigMatrix enforce(  final NumberContext aContext){
    return this.modify(aContext.getBigEnforceFunction());
  }
  public BigMatrix round(  final NumberContext aContext){
    return this.modify(aContext.getBigRoundFunction());
  }
  public BigDecimal toBigDecimal(  final int row,  final int column){
    return this.getStore().get(row,column);
  }
  @Override public PhysicalStore<BigDecimal> toBigStore(){
    return this.getStore().copy();
  }
  public ComplexNumber toComplexNumber(  final int row,  final int column){
    return ComplexNumber.makeReal(this.getStore().doubleValue(row,column));
  }
  public String toString(  final int row,  final int column){
    return this.toBigDecimal(row,column).toPlainString();
  }
  @SuppressWarnings("unchecked") @Override MatrixFactory<BigDecimal,BigMatrix> getFactory(){
    return (MatrixFactory<BigDecimal,BigMatrix>)FACTORY;
  }
  @SuppressWarnings("unchecked") @Override MatrixStore<BigDecimal> getStoreFrom(  final Access1D<?> aMtrx){
    if (aMtrx instanceof BigMatrix) {
      return ((BigMatrix)aMtrx).getStore();
    }
 else     if (aMtrx instanceof BigDenseStore) {
      return (BigDenseStore)aMtrx;
    }
 else     if ((aMtrx instanceof MatrixStore) && !this.isEmpty() && (aMtrx.get(0) instanceof BigDecimal)) {
      return (MatrixStore<BigDecimal>)aMtrx;
    }
 else     if (aMtrx instanceof Access2D<?>) {
      return this.getPhysicalFactory().copy((Access2D<?>)aMtrx);
    }
 else {
      return this.getPhysicalFactory().columns(aMtrx);
    }
  }
}

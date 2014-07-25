package org.ojalgo.matrix;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;
/** 
 * ComplexMatrix
 * @author apete
 */
public final class ComplexMatrix extends AbstractMatrix<ComplexNumber,ComplexMatrix> {
  public static final BasicMatrix.Factory<ComplexMatrix> FACTORY=new MatrixFactory<ComplexNumber,ComplexMatrix>(ComplexMatrix.class,ComplexDenseStore.FACTORY);
  public static Builder<ComplexMatrix> getBuilder(  final int aLength){
    return FACTORY.getBuilder(aLength);
  }
  public static Builder<ComplexMatrix> getBuilder(  final int aRowDim,  final int aColDim){
    return FACTORY.getBuilder(aRowDim,aColDim);
  }
  /** 
 * This method is for internal use only - YOU should NOT use it!
 */
  ComplexMatrix(  final MatrixStore<ComplexNumber> aStore){
    super(aStore);
  }
  public ComplexMatrix enforce(  final NumberContext aContext){
    return this.modify(aContext.getComplexEnforceFunction());
  }
  public ComplexMatrix round(  final NumberContext aContext){
    return this.modify(aContext.getComplexRoundFunction());
  }
  public BigDecimal toBigDecimal(  final int row,  final int column){
    return new BigDecimal(this.getStore().doubleValue(row,column));
  }
  public ComplexNumber toComplexNumber(  final int row,  final int column){
    return this.getStore().get(row,column);
  }
  @Override public PhysicalStore<ComplexNumber> toComplexStore(){
    return this.getStore().copy();
  }
  public String toString(  final int row,  final int column){
    return this.toComplexNumber(row,column).toString();
  }
  @SuppressWarnings("unchecked") @Override MatrixFactory<ComplexNumber,ComplexMatrix> getFactory(){
    return (MatrixFactory<ComplexNumber,ComplexMatrix>)FACTORY;
  }
  @SuppressWarnings("unchecked") @Override MatrixStore<ComplexNumber> getStoreFrom(  final Access1D<?> aMtrx){
    if (aMtrx instanceof ComplexMatrix) {
      return ((ComplexMatrix)aMtrx).getStore();
    }
 else     if (aMtrx instanceof ComplexDenseStore) {
      return (ComplexDenseStore)aMtrx;
    }
 else     if ((aMtrx instanceof MatrixStore) && !this.isEmpty() && (aMtrx.get(0) instanceof ComplexNumber)) {
      return (MatrixStore<ComplexNumber>)aMtrx;
    }
 else     if (aMtrx instanceof Access2D<?>) {
      return this.getPhysicalFactory().copy((Access2D<?>)aMtrx);
    }
 else {
      return this.getPhysicalFactory().columns(aMtrx);
    }
  }
}

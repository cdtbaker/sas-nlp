package org.ojalgo.matrix.store;
import java.io.Serializable;
import java.util.Iterator;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.type.context.NumberContext;
abstract class AbstractStore<N extends Number> implements MatrixStore<N>, Serializable {
  private final int myColDim;
  private transient Class<?> myComponentType=null;
  private final int myRowDim;
  @SuppressWarnings("unused") private AbstractStore(){
    this(0,0);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected AbstractStore(  final int rowCount,  final int colCount){
    super();
    myRowDim=rowCount;
    myColDim=colCount;
  }
  @SuppressWarnings("unchecked") public N aggregateAll(  final Aggregator aggregator){
    final AggregatorFunction<N> tmpFunction=(AggregatorFunction<N>)aggregator.getFunction(this.getComponentType());
    this.visitAll(tmpFunction);
    return tmpFunction.getNumber();
  }
  public final MatrixStore.Builder<N> builder(){
    return new MatrixStore.Builder<N>(this);
  }
  public PhysicalStore<N> conjugate(){
    return this.factory().conjugate(this);
  }
  public PhysicalStore<N> copy(){
    return this.factory().copy(this);
  }
  public long count(){
    return myRowDim * myColDim;
  }
  public long countColumns(){
    return myColDim;
  }
  public long countRows(){
    return myRowDim;
  }
  public double doubleValue(  final long index){
    return this.doubleValue(AccessUtils.row((int)index,myRowDim),AccessUtils.column((int)index,myRowDim));
  }
  public final boolean equals(  final MatrixStore<N> other,  final NumberContext context){
    return AccessUtils.equals(this,other,context);
  }
  @SuppressWarnings("unchecked") @Override public final boolean equals(  final Object someObj){
    if (someObj instanceof MatrixStore) {
      return this.equals((MatrixStore<N>)someObj,NumberContext.getGeneral(6));
    }
 else {
      return super.equals(someObj);
    }
  }
  public N get(  final long index){
    return this.get(AccessUtils.row(index,myRowDim),AccessUtils.column(index,myRowDim));
  }
  public final int getColDim(){
    return myColDim;
  }
  public int getMaxDim(){
    return Math.max(myRowDim,myColDim);
  }
  public int getMinDim(){
    return Math.min(myRowDim,myColDim);
  }
  public final int getRowDim(){
    return myRowDim;
  }
  @Override public final int hashCode(){
    return MatrixUtils.hashCode(this);
  }
  public boolean isAbsolute(  final int row,  final int column){
    return this.toScalar(row,column).isAbsolute();
  }
  public boolean isPositive(  final int row,  final int column){
    return this.toScalar(row,column).isPositive();
  }
  public boolean isReal(  final int row,  final int column){
    return this.toScalar(row,column).isReal();
  }
  public boolean isZero(  final int row,  final int column){
    return this.toScalar(row,column).isZero();
  }
  public final Iterator<N> iterator(){
    return new Iterator1D<N>(this);
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#multiplyLeft(org.ojalgo.matrix.store.MatrixStore)
 */
  public MatrixStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    final int tmpRowDim=(int)(leftMtrx.count() / this.countRows());
    final int tmpColDim=this.getColDim();
    final PhysicalStore<N> retVal=this.factory().makeZero(tmpRowDim,tmpColDim);
    retVal.fillByMultiplying(leftMtrx,this);
    return retVal;
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#multiplyRight(org.ojalgo.matrix.store.MatrixStore)
 */
  public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    final int tmpRowDim=this.getRowDim();
    final int tmpColDim=(int)(rightMtrx.count() / this.getColDim());
    final PhysicalStore<N> retVal=this.factory().makeZero(tmpRowDim,tmpColDim);
    retVal.fillByMultiplying(this,rightMtrx);
    return retVal;
  }
  public final int size(){
    return (int)this.count();
  }
  @Override public final String toString(){
    return MatrixUtils.toString(this);
  }
  public PhysicalStore<N> transpose(){
    return this.factory().transpose(this);
  }
  public void visitAll(  final VoidFunction<N> visitor){
    final int tmpRowDim=this.getRowDim();
    final int tmpColDim=this.getColDim();
    for (int j=0; j < tmpColDim; j++) {
      for (int i=0; i < tmpRowDim; i++) {
        visitor.invoke(this.get(i,j));
      }
    }
  }
  public void visitColumn(  final int row,  final int column,  final VoidFunction<N> visitor){
    final int tmpRowDim=this.getRowDim();
    for (int i=row; i < tmpRowDim; i++) {
      visitor.invoke(this.get(i,column));
    }
  }
  public void visitDiagonal(  final int row,  final int column,  final VoidFunction<N> visitor){
    final int tmpRowDim=this.getRowDim();
    final int tmpColDim=this.getColDim();
    for (int ij=0; ((row + ij) < tmpRowDim) && ((column + ij) < tmpColDim); ij++) {
      visitor.invoke(this.get(row + ij,column + ij));
    }
  }
  public void visitRow(  final int row,  final int column,  final VoidFunction<N> visitor){
    final int tmpColDim=this.getColDim();
    for (int j=column; j < tmpColDim; j++) {
      visitor.invoke(this.get(row,j));
    }
  }
  protected final Class<?> getComponentType(){
    if (myComponentType == null) {
      myComponentType=this.get(0,0).getClass();
    }
    return myComponentType;
  }
}

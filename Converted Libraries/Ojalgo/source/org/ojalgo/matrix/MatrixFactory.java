package org.ojalgo.matrix;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.Access2D.Builder;
import org.ojalgo.matrix.store.AboveBelowStore;
import org.ojalgo.matrix.store.IdentityStore;
import org.ojalgo.matrix.store.LeftRightStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.random.RandomNumber;
/** 
 * MatrixFactory creates instances of classes that implement the{@linkplain org.ojalgo.matrix.BasicMatrix} interface and have a 
 * constructor that takes a MatrixStore as input.
 * @author apete
 */
final class MatrixFactory<N extends Number,I extends BasicMatrix<N>> implements BasicMatrix.Factory<I> {
class MatrixBuilder implements Access2D.Builder<I> {
    private final PhysicalStore<N> myPhysicalStore;
    private final PhysicalStore.Factory<N,?> myFactory;
    @SuppressWarnings("unused") private MatrixBuilder(){
      this(null,0,0);
    }
    protected MatrixBuilder(    final PhysicalStore.Factory<N,?> aFactory,    final int aRowDim,    final int aColDim){
      super();
      myPhysicalStore=aFactory.makeZero(aRowDim,aColDim);
      myFactory=aFactory;
    }
    MatrixBuilder(    final PhysicalStore<N> aPhysicalStore){
      super();
      myPhysicalStore=aPhysicalStore;
      myFactory=aPhysicalStore.factory();
    }
    @Override public I build(){
      return MatrixFactory.this.instantiate(myPhysicalStore);
    }
    public long count(){
      return myPhysicalStore.count();
    }
    public long countColumns(){
      return myPhysicalStore.countColumns();
    }
    public long countRows(){
      return myPhysicalStore.countRows();
    }
    public final MatrixBuilder fillAll(    final Number aNmbr){
      myPhysicalStore.fillAll(myFactory.getNumber(aNmbr));
      return this;
    }
    public final MatrixBuilder fillColumn(    final long aRow,    final long aCol,    final Number aNmbr){
      myPhysicalStore.fillColumn((int)aRow,(int)aCol,myFactory.getNumber(aNmbr));
      return this;
    }
    public final MatrixBuilder fillDiagonal(    final long aRow,    final long aCol,    final Number aNmbr){
      myPhysicalStore.fillDiagonal((int)aRow,(int)aCol,myFactory.getNumber(aNmbr));
      return this;
    }
    public final MatrixBuilder fillRow(    final long aRow,    final long aCol,    final Number aNmbr){
      myPhysicalStore.fillRow((int)aRow,(int)aCol,myFactory.getNumber(aNmbr));
      return this;
    }
    public final int getColDim(){
      return myPhysicalStore.getColDim();
    }
    public final int getRowDim(){
      return myPhysicalStore.getRowDim();
    }
    public final MatrixBuilder set(    final long index,    final double aNmbr){
      myPhysicalStore.set(index,aNmbr);
      return this;
    }
    public final MatrixBuilder set(    final long aRow,    final long aCol,    final double aNmbr){
      myPhysicalStore.set(aRow,aCol,aNmbr);
      return this;
    }
    public final MatrixBuilder set(    final long aRow,    final long aCol,    final Number aNmbr){
      myPhysicalStore.set(aRow,aCol,myFactory.getNumber(aNmbr));
      return this;
    }
    public final MatrixBuilder set(    final long index,    final Number aNmbr){
      myPhysicalStore.set(index,myFactory.getNumber(aNmbr));
      return this;
    }
    public int size(){
      return myPhysicalStore.size();
    }
  }
  private static Constructor<? extends BasicMatrix> getConstructor(  final Class<? extends BasicMatrix> aTemplate){
    try {
      final Constructor<? extends BasicMatrix> retVal=aTemplate.getDeclaredConstructor(MatrixStore.class);
      retVal.setAccessible(true);
      return retVal;
    }
 catch (    final SecurityException anException) {
      return null;
    }
catch (    final NoSuchMethodException anException) {
      return null;
    }
  }
  private final Constructor<I> myConstructor;
  private final PhysicalStore.Factory<N,?> myPhysicalFactory;
  @SuppressWarnings("unused") private MatrixFactory(){
    this(null,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  MatrixFactory(  final Class<I> aTemplate,  final PhysicalStore.Factory<N,?> aPhysical){
    super();
    myPhysicalFactory=aPhysical;
    myConstructor=(Constructor<I>)MatrixFactory.getConstructor(aTemplate);
  }
  public I columns(  final Access1D<?>... source){
    return this.instantiate(myPhysicalFactory.columns(source));
  }
  public I columns(  final double[]... source){
    return this.instantiate(myPhysicalFactory.columns(source));
  }
  public I columns(  final List<? extends Number>... source){
    return this.instantiate(myPhysicalFactory.columns(source));
  }
  public I columns(  final Number[]... source){
    return this.instantiate(myPhysicalFactory.columns(source));
  }
  public I copy(  final Access2D<?> source){
    return this.instantiate(myPhysicalFactory.copy(source));
  }
  public Builder<I> getBuilder(  final int count){
    return this.getBuilder(count,1);
  }
  public Builder<I> getBuilder(  final int rows,  final int columns){
    return new MatrixBuilder(myPhysicalFactory,rows,columns);
  }
  public I makeEye(  final long rows,  final long columns){
    final int tmpMinDim=(int)Math.min(rows,columns);
    MatrixStore<N> retVal=new IdentityStore<N>(myPhysicalFactory,tmpMinDim);
    if (rows > tmpMinDim) {
      retVal=new AboveBelowStore<N>(retVal,new ZeroStore<N>(myPhysicalFactory,(int)rows - tmpMinDim,(int)columns));
    }
 else     if (columns > tmpMinDim) {
      retVal=new LeftRightStore<N>(retVal,new ZeroStore<N>(myPhysicalFactory,(int)rows,(int)columns - tmpMinDim));
    }
    return this.instantiate(retVal);
  }
  public I makeRandom(  final long rows,  final long columns,  final RandomNumber distribution){
    return this.instantiate(myPhysicalFactory.makeRandom(rows,columns,distribution));
  }
  public I makeZero(  final long rows,  final long columns){
    return this.instantiate(new ZeroStore<N>(myPhysicalFactory,(int)rows,(int)columns));
  }
  public I rows(  final Access1D<?>... source){
    return this.instantiate(myPhysicalFactory.rows(source));
  }
  public I rows(  final double[]... source){
    return this.instantiate(myPhysicalFactory.rows(source));
  }
  public I rows(  final List<? extends Number>... source){
    return this.instantiate(myPhysicalFactory.rows(source));
  }
  public I rows(  final Number[]... source){
    return this.instantiate(myPhysicalFactory.rows(source));
  }
  protected final PhysicalStore.Factory<N,?> getPhysicalFactory(){
    return myPhysicalFactory;
  }
  /** 
 * This method is for internal use only - YOU should NOT use it!
 */
  final I instantiate(  final MatrixStore<N> aStore){
    try {
      return myConstructor.newInstance(aStore);
    }
 catch (    final IllegalArgumentException anException) {
      throw new ProgrammingError(anException);
    }
catch (    final InstantiationException anException) {
      throw new ProgrammingError(anException);
    }
catch (    final IllegalAccessException anException) {
      throw new ProgrammingError(anException);
    }
catch (    final InvocationTargetException anException) {
      throw new ProgrammingError(anException);
    }
  }
  final MatrixBuilder wrap(  final PhysicalStore<N> aStore){
    return new MatrixBuilder(aStore);
  }
}

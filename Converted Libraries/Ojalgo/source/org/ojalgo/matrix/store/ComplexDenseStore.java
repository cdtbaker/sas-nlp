package org.ojalgo.matrix.store;
import static org.ojalgo.function.ComplexFunction.*;
import java.util.List;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.ComplexArray;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ComplexFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.ComplexAggregator;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.store.operation.*;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
/** 
 * A {@linkplain ComplexNumber} implementation of {@linkplain PhysicalStore}.
 * @author apete
 */
public final class ComplexDenseStore extends ComplexArray implements PhysicalStore<ComplexNumber>, DecompositionStore<ComplexNumber> {
public static interface ComplexMultiplyBoth {
    void invoke(    ComplexNumber[] product,    Access1D<ComplexNumber> left,    int complexity,    Access1D<ComplexNumber> right);
  }
public static interface ComplexMultiplyLeft {
    void invoke(    ComplexNumber[] product,    Access1D<ComplexNumber> left,    int complexity,    ComplexNumber[] right);
  }
public static interface ComplexMultiplyRight {
    void invoke(    ComplexNumber[] product,    ComplexNumber[] left,    int complexity,    Access1D<ComplexNumber> right);
  }
  public static final DecompositionStore.Factory<ComplexNumber,ComplexDenseStore> FACTORY=new DecompositionStore.Factory<ComplexNumber,ComplexDenseStore>(){
    public AggregatorCollection<ComplexNumber> aggregator(){
      return ComplexAggregator.getCollection();
    }
    public ComplexDenseStore columns(    final Access1D<?>... source){
      final int tmpRowDim=source[0].size();
      final int tmpColDim=source.length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      Access1D<?> tmpColumn;
      for (int j=0; j < tmpColDim; j++) {
        tmpColumn=source[j];
        for (int i=0; i < tmpRowDim; i++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpColumn.get(i));
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore columns(    final double[]... source){
      final int tmpRowDim=source[0].length;
      final int tmpColDim=source.length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      double[] tmpColumn;
      for (int j=0; j < tmpColDim; j++) {
        tmpColumn=source[j];
        for (int i=0; i < tmpRowDim; i++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpColumn[i]);
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore columns(    final List<? extends Number>... source){
      final int tmpRowDim=source[0].size();
      final int tmpColDim=source.length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      List<? extends Number> tmpColumn;
      for (int j=0; j < tmpColDim; j++) {
        tmpColumn=source[j];
        for (int i=0; i < tmpRowDim; i++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpColumn.get(i));
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore columns(    final Number[]... source){
      final int tmpRowDim=source[0].length;
      final int tmpColDim=source.length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      Number[] tmpColumn;
      for (int j=0; j < tmpColDim; j++) {
        tmpColumn=source[j];
        for (int i=0; i < tmpRowDim; i++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpColumn[i]);
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore conjugate(    final Access2D<?> source){
      final ComplexDenseStore retVal=new ComplexDenseStore((int)source.countColumns(),(int)source.countRows());
      retVal.fillConjugated(source);
      return retVal;
    }
    public ComplexDenseStore copy(    final Access2D<?> source){
      final ComplexDenseStore retVal=new ComplexDenseStore(source.getRowDim(),source.getColDim());
      retVal.fillMatching(source);
      return retVal;
    }
    public FunctionSet<ComplexNumber> function(){
      return ComplexFunction.getSet();
    }
    /** 
 * @deprecated Use {@link #aggregator()} instead
 */
    @Deprecated public AggregatorCollection<ComplexNumber> getAggregatorCollection(){
      return this.aggregator();
    }
    /** 
 * @deprecated Use {@link #function()} instead
 */
    @Deprecated public FunctionSet<ComplexNumber> getFunctionSet(){
      return this.function();
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public ComplexNumber getNumber(    final double value){
      return this.scalar().cast(value);
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public ComplexNumber getNumber(    final Number value){
      return this.scalar().cast(value);
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public Scalar<ComplexNumber> getStaticOne(){
      return this.scalar().one();
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public Scalar<ComplexNumber> getStaticZero(){
      return this.scalar().zero();
    }
    public SimpleArray.Complex makeArray(    final int length){
      return SimpleArray.makeComplex(length);
    }
    public ComplexDenseStore makeEye(    final long rows,    final long columns){
      final ComplexDenseStore retVal=this.makeZero(rows,columns);
      retVal.myUtility.fillDiagonal(0,0,this.getStaticOne().getNumber());
      return retVal;
    }
    public Householder.Complex makeHouseholder(    final int length){
      return new Householder.Complex(length);
    }
    public ComplexDenseStore makeRandom(    final long rows,    final long columns,    final RandomNumber distribution){
      final int tmpRowDim=(int)rows;
      final int tmpColDim=(int)columns;
      final int tmpLength=tmpRowDim * tmpColDim;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpLength];
      for (int i=0; i < tmpLength; i++) {
        tmpData[i]=TypeUtils.toComplexNumber(distribution.doubleValue());
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public Rotation.Complex makeRotation(    final int low,    final int high,    final ComplexNumber cos,    final ComplexNumber sin){
      return new Rotation.Complex(low,high,cos,sin);
    }
    public Rotation.Complex makeRotation(    final int low,    final int high,    final double cos,    final double sin){
      return this.makeRotation(low,high,ComplexNumber.makeReal(cos),ComplexNumber.makeReal(sin));
    }
    public ComplexDenseStore makeZero(    final long rows,    final long columns){
      return new ComplexDenseStore((int)rows,(int)columns);
    }
    public ComplexDenseStore rows(    final Access1D<?>... source){
      final int tmpRowDim=source.length;
      final int tmpColDim=source[0].size();
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      Access1D<?> tmpRow;
      for (int i=0; i < tmpRowDim; i++) {
        tmpRow=source[i];
        for (int j=0; j < tmpColDim; j++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpRow.get(j));
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore rows(    final double[]... source){
      final int tmpRowDim=source.length;
      final int tmpColDim=source[0].length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      double[] tmpRow;
      for (int i=0; i < tmpRowDim; i++) {
        tmpRow=source[i];
        for (int j=0; j < tmpColDim; j++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpRow[j]);
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore rows(    final List<? extends Number>... source){
      final int tmpRowDim=source.length;
      final int tmpColDim=source[0].size();
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      List<? extends Number> tmpRow;
      for (int i=0; i < tmpRowDim; i++) {
        tmpRow=source[i];
        for (int j=0; j < tmpColDim; j++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpRow.get(j));
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public ComplexDenseStore rows(    final Number[]... source){
      final int tmpRowDim=source.length;
      final int tmpColDim=source[0].length;
      final ComplexNumber[] tmpData=new ComplexNumber[tmpRowDim * tmpColDim];
      Number[] tmpRow;
      for (int i=0; i < tmpRowDim; i++) {
        tmpRow=source[i];
        for (int j=0; j < tmpColDim; j++) {
          tmpData[i + (tmpRowDim * j)]=TypeUtils.toComplexNumber(tmpRow[j]);
        }
      }
      return new ComplexDenseStore(tmpRowDim,tmpColDim,tmpData);
    }
    public Scalar.Factory<ComplexNumber> scalar(){
      return ComplexNumber.FACTORY;
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public Scalar<ComplexNumber> toScalar(    final double value){
      return this.scalar().convert(value);
    }
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated public Scalar<ComplexNumber> toScalar(    final Number value){
      return this.scalar().convert(value);
    }
    public ComplexDenseStore transpose(    final Access2D<?> source){
      final ComplexDenseStore retVal=new ComplexDenseStore((int)source.countColumns(),(int)source.countRows());
      retVal.fillTransposed(source);
      return retVal;
    }
  }
;
  static ComplexDenseStore cast(  final Access1D<ComplexNumber> mtrx){
    if (mtrx instanceof ComplexDenseStore) {
      return (ComplexDenseStore)mtrx;
    }
 else     if (mtrx instanceof Access2D<?>) {
      return FACTORY.copy((Access2D<?>)mtrx);
    }
 else {
      return FACTORY.columns(mtrx);
    }
  }
  static Householder.Complex cast(  final Householder<ComplexNumber> aTransf){
    if (aTransf instanceof Householder.Complex) {
      return (Householder.Complex)aTransf;
    }
 else     if (aTransf instanceof DecompositionStore.HouseholderReference<?>) {
      return ((DecompositionStore.HouseholderReference<ComplexNumber>)aTransf).getComplexWorker().copy(aTransf);
    }
 else {
      return new Householder.Complex(aTransf);
    }
  }
  static Rotation.Complex cast(  final Rotation<ComplexNumber> aTransf){
    if (aTransf instanceof Rotation.Complex) {
      return (Rotation.Complex)aTransf;
    }
 else {
      return new Rotation.Complex(aTransf);
    }
  }
  private final ComplexMultiplyBoth multiplyBoth;
  private final ComplexMultiplyLeft multiplyLeft;
  private final ComplexMultiplyRight multiplyRight;
  private final int myColDim;
  private final int myRowDim;
  private final Array2D<ComplexNumber> myUtility;
  ComplexDenseStore(  final ComplexNumber[] anArray){
    super(anArray);
    myRowDim=anArray.length;
    myColDim=1;
    myUtility=this.asArray2D(myRowDim,myColDim);
    multiplyBoth=MultiplyBoth.getComplex(myRowDim,myColDim);
    multiplyLeft=MultiplyLeft.getComplex(myRowDim,myColDim);
    multiplyRight=MultiplyRight.getComplex(myRowDim,myColDim);
  }
  ComplexDenseStore(  final int aLength){
    super(aLength);
    myRowDim=aLength;
    myColDim=1;
    myUtility=this.asArray2D(myRowDim,myColDim);
    multiplyBoth=MultiplyBoth.getComplex(myRowDim,myColDim);
    multiplyLeft=MultiplyLeft.getComplex(myRowDim,myColDim);
    multiplyRight=MultiplyRight.getComplex(myRowDim,myColDim);
  }
  ComplexDenseStore(  final int aRowDim,  final int aColDim){
    super(aRowDim * aColDim);
    myRowDim=aRowDim;
    myColDim=aColDim;
    myUtility=this.asArray2D(myRowDim,myColDim);
    multiplyBoth=MultiplyBoth.getComplex(myRowDim,myColDim);
    multiplyLeft=MultiplyLeft.getComplex(myRowDim,myColDim);
    multiplyRight=MultiplyRight.getComplex(myRowDim,myColDim);
  }
  ComplexDenseStore(  final int aRowDim,  final int aColDim,  final ComplexNumber[] anArray){
    super(anArray);
    myRowDim=aRowDim;
    myColDim=aColDim;
    myUtility=this.asArray2D(myRowDim,myColDim);
    multiplyBoth=MultiplyBoth.getComplex(myRowDim,myColDim);
    multiplyLeft=MultiplyLeft.getComplex(myRowDim,myColDim);
    multiplyRight=MultiplyRight.getComplex(myRowDim,myColDim);
  }
  public ComplexNumber aggregateAll(  final Aggregator aggregator){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    final AggregatorFunction<ComplexNumber> tmpMainAggr=aggregator.getComplexFunction();
    if (tmpColDim > AggregateAll.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          final AggregatorFunction<ComplexNumber> tmpPartAggr=aggregator.getComplexFunction();
          ComplexDenseStore.this.visit(tmpRowDim * aFirst,tmpRowDim * aLimit,1,tmpPartAggr);
synchronized (tmpMainAggr) {
            tmpMainAggr.merge(tmpPartAggr.getNumber());
          }
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,AggregateAll.THRESHOLD);
    }
 else {
      ComplexDenseStore.this.visit(0,length,1,tmpMainAggr);
    }
    return tmpMainAggr.getNumber();
  }
  public void applyCholesky(  final int iterationPoint,  final SimpleArray<ComplexNumber> multipliers){
    final ComplexNumber[] tmpData=this.data();
    final ComplexNumber[] tmpColumn=((SimpleArray.Complex)multipliers).data;
    if ((myColDim - iterationPoint - 1) > ApplyCholesky.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          ApplyCholesky.invoke(tmpData,myRowDim,aFirst,aLimit,tmpColumn);
        }
      }
;
      tmpConquerer.invoke(iterationPoint + 1,myColDim,ApplyCholesky.THRESHOLD);
    }
 else {
      ApplyCholesky.invoke(tmpData,myRowDim,iterationPoint + 1,myColDim,tmpColumn);
    }
  }
  public void applyLU(  final int iterationPoint,  final SimpleArray<ComplexNumber> multipliers){
    final ComplexNumber[] tmpData=this.data();
    final ComplexNumber[] tmpColumn=((SimpleArray.Complex)multipliers).data;
    if ((myColDim - iterationPoint - 1) > ApplyLU.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          ApplyLU.invoke(tmpData,myRowDim,aFirst,aLimit,tmpColumn,iterationPoint);
        }
      }
;
      tmpConquerer.invoke(iterationPoint + 1,myColDim,ApplyLU.THRESHOLD);
    }
 else {
      ApplyLU.invoke(tmpData,myRowDim,iterationPoint + 1,myColDim,tmpColumn,iterationPoint);
    }
  }
  public Array2D<ComplexNumber> asArray2D(){
    return myUtility;
  }
  public Array1D<ComplexNumber> asList(){
    return myUtility.asArray1D();
  }
  public final MatrixStore.Builder<ComplexNumber> builder(){
    return new MatrixStore.Builder<ComplexNumber>(this);
  }
  public void caxpy(  final ComplexNumber aSclrA,  final int aColX,  final int aColY,  final int aFirstRow){
    CAXPY.invoke(this.data(),aColY * myRowDim,this.data(),aColX * myRowDim,aSclrA,aFirstRow,myRowDim);
  }
  public Array1D<ComplexNumber> computeInPlaceSchur(  final PhysicalStore<ComplexNumber> aTransformationCollector,  final boolean eigenvalue){
    throw new UnsupportedOperationException();
  }
  public ComplexDenseStore conjugate(){
    final ComplexDenseStore retVal=new ComplexDenseStore(myColDim,myRowDim);
    retVal.fillConjugated(this);
    return retVal;
  }
  public ComplexDenseStore copy(){
    return new ComplexDenseStore(myRowDim,myColDim,this.copyOfData());
  }
  public long countColumns(){
    return myColDim;
  }
  public long countRows(){
    return myRowDim;
  }
  public void divideAndCopyColumn(  final int aRow,  final int aCol,  final SimpleArray<ComplexNumber> aDestination){
    final ComplexNumber[] tmpData=this.data();
    final int tmpRowDim=myRowDim;
    final ComplexNumber[] tmpDestination=((SimpleArray.Complex)aDestination).data;
    int tmpIndex=aRow + (aCol * tmpRowDim);
    final ComplexNumber tmpDenominator=tmpData[tmpIndex];
    for (int i=aRow + 1; i < tmpRowDim; i++) {
      tmpIndex++;
      tmpDestination[i]=tmpData[tmpIndex]=tmpData[tmpIndex].divide(tmpDenominator);
    }
  }
  public double doubleValue(  final long aRow,  final long aCol){
    return this.doubleValue(aRow + (aCol * myRowDim));
  }
  public boolean equals(  final MatrixStore<ComplexNumber> other,  final NumberContext context){
    return AccessUtils.equals(this,other,context);
  }
  @SuppressWarnings("unchecked") @Override public boolean equals(  final Object anObj){
    if (anObj instanceof MatrixStore) {
      return this.equals((MatrixStore<ComplexNumber>)anObj,NumberContext.getGeneral(6));
    }
 else {
      return super.equals(anObj);
    }
  }
  public void exchangeColumns(  final int aColA,  final int aColB){
    myUtility.exchangeColumns(aColA,aColB);
  }
  public void exchangeRows(  final int aRowA,  final int aRowB){
    myUtility.exchangeRows(aRowA,aRowB);
  }
  public PhysicalStore.Factory<ComplexNumber,ComplexDenseStore> factory(){
    return FACTORY;
  }
  public void fillByMultiplying(  final Access1D<ComplexNumber> left,  final Access1D<ComplexNumber> right){
    final int tmpComplexity=((int)left.count()) / myRowDim;
    final ComplexNumber[] tmpProductData=this.data();
    if (right instanceof ComplexDenseStore) {
      multiplyLeft.invoke(tmpProductData,left,tmpComplexity,ComplexDenseStore.cast(right).data());
    }
 else     if (left instanceof ComplexDenseStore) {
      multiplyRight.invoke(tmpProductData,ComplexDenseStore.cast(left).data(),tmpComplexity,right);
    }
 else {
      multiplyBoth.invoke(tmpProductData,left,tmpComplexity,right);
    }
  }
  public void fillColumn(  final long aRow,  final long aCol,  final ComplexNumber aNmbr){
    myUtility.fillColumn(aRow,aCol,aNmbr);
  }
  public void fillConjugated(  final Access2D<? extends Number> source){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillConjugated.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          FillConjugated.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,source);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillConjugated.THRESHOLD);
    }
 else {
      FillConjugated.invoke(this.data(),tmpRowDim,0,tmpColDim,source);
    }
  }
  public void fillDiagonal(  final long aRow,  final long aCol,  final ComplexNumber aNmbr){
    myUtility.fillDiagonal(aRow,aCol,aNmbr);
  }
  public void fillMatching(  final Access1D<? extends Number> source){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillMatchingSingle.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          FillMatchingSingle.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,source);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillMatchingSingle.THRESHOLD);
    }
 else {
      FillMatchingSingle.invoke(this.data(),tmpRowDim,0,tmpColDim,source);
    }
  }
  public void fillMatching(  final Access1D<ComplexNumber> leftArg,  final BinaryFunction<ComplexNumber> func,  final Access1D<ComplexNumber> rightArg){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillMatchingBoth.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          ComplexDenseStore.this.fill(tmpRowDim * aFirst,tmpRowDim * aLimit,leftArg,func,rightArg);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillMatchingBoth.THRESHOLD);
    }
 else {
      this.fill(0,tmpRowDim * tmpColDim,leftArg,func,rightArg);
    }
  }
  public void fillMatching(  final Access1D<ComplexNumber> aLeftArg,  final BinaryFunction<ComplexNumber> aFunc,  final ComplexNumber aRightArg){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillMatchingLeft.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          ComplexDenseStore.this.fill(tmpRowDim * aFirst,tmpRowDim * aLimit,aLeftArg,aFunc,aRightArg);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillMatchingLeft.THRESHOLD);
    }
 else {
      this.fill(0,tmpRowDim * tmpColDim,aLeftArg,aFunc,aRightArg);
    }
  }
  public void fillMatching(  final ComplexNumber aLeftArg,  final BinaryFunction<ComplexNumber> aFunc,  final Access1D<ComplexNumber> aRightArg){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillMatchingRight.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          ComplexDenseStore.this.fill(tmpRowDim * aFirst,tmpRowDim * aLimit,aLeftArg,aFunc,aRightArg);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillMatchingRight.THRESHOLD);
    }
 else {
      this.fill(0,tmpRowDim * tmpColDim,aLeftArg,aFunc,aRightArg);
    }
  }
  public void fillRow(  final long aRow,  final long aCol,  final ComplexNumber aNmbr){
    myUtility.fillRow(aRow,aCol,aNmbr);
  }
  public void fillTransposed(  final Access2D<? extends Number> source){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > FillTransposed.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          FillTransposed.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,source);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,FillTransposed.THRESHOLD);
    }
 else {
      FillTransposed.invoke(this.data(),tmpRowDim,0,tmpColDim,source);
    }
  }
  public boolean generateApplyAndCopyHouseholderColumn(  final int aRow,  final int aCol,  final Householder<ComplexNumber> aDestination){
    return GenerateApplyAndCopyHouseholderColumn.invoke(this.data(),myRowDim,aRow,aCol,(Householder.Complex)aDestination);
  }
  public boolean generateApplyAndCopyHouseholderRow(  final int aRow,  final int aCol,  final Householder<ComplexNumber> aDestination){
    return GenerateApplyAndCopyHouseholderRow.invoke(this.data(),myRowDim,aRow,aCol,(Householder.Complex)aDestination);
  }
  public ComplexNumber get(  final long aRow,  final long aCol){
    return myUtility.get(aRow,aCol);
  }
  public int getColDim(){
    return myColDim;
  }
  public int getIndexOfLargestInColumn(  final int aRow,  final int aCol){
    return myUtility.getIndexOfLargestInColumn(aRow,aCol);
  }
  public int getMaxDim(){
    return Math.max(myRowDim,myColDim);
  }
  public int getMinDim(){
    return Math.min(myRowDim,myColDim);
  }
  public int getRowDim(){
    return myRowDim;
  }
  @Override public int hashCode(){
    return MatrixUtils.hashCode(this);
  }
  public boolean isAbsolute(  final int row,  final int column){
    return myUtility.isAbsolute(row,column);
  }
  public boolean isAbsolute(  final long row,  final long column){
    return myUtility.isAbsolute(row,column);
  }
  public boolean isInfinite(  final long row,  final long column){
    return myUtility.isInfinite(row,column);
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isNaN(  final long row,  final long column){
    return myUtility.isNaN(row,column);
  }
  public boolean isPositive(  final int row,  final int column){
    return myUtility.isPositive(row,column);
  }
  public boolean isPositive(  final long row,  final long column){
    return myUtility.isPositive(row,column);
  }
  public boolean isReal(  final int row,  final int column){
    return myUtility.isReal(row,column);
  }
  public boolean isReal(  final long row,  final long column){
    return myUtility.isReal(row,column);
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public boolean isZero(  final int row,  final int column){
    return myUtility.isZero(row,column);
  }
  public boolean isZero(  final long row,  final long column){
    return myUtility.isZero(row,column);
  }
  public void maxpy(  final ComplexNumber aSclrA,  final MatrixStore<ComplexNumber> aMtrxX){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > MAXPY.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          MAXPY.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,aSclrA,aMtrxX);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,MAXPY.THRESHOLD);
    }
 else {
      MAXPY.invoke(this.data(),tmpRowDim,0,tmpColDim,aSclrA,aMtrxX);
    }
  }
  @Override public void modifyAll(  final UnaryFunction<ComplexNumber> aFunc){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > ModifyAll.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          ComplexDenseStore.this.modify(tmpRowDim * aFirst,tmpRowDim * aLimit,1,aFunc);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,ModifyAll.THRESHOLD);
    }
 else {
      this.modify(tmpRowDim * 0,tmpRowDim * tmpColDim,1,aFunc);
    }
  }
  public void modifyColumn(  final int aRow,  final int aCol,  final UnaryFunction<ComplexNumber> aFunc){
    myUtility.modifyColumn(aRow,aCol,aFunc);
  }
  public void modifyColumn(  final long row,  final long column,  final UnaryFunction<ComplexNumber> function){
    myUtility.modifyColumn(row,column,function);
  }
  public void modifyDiagonal(  final int aRow,  final int aCol,  final UnaryFunction<ComplexNumber> aFunc){
    myUtility.modifyDiagonal(aRow,aCol,aFunc);
  }
  public void modifyDiagonal(  final long row,  final long column,  final UnaryFunction<ComplexNumber> function){
    myUtility.modifyDiagonal(row,column,function);
  }
  public void modifyOne(  final int aRow,  final int aCol,  final UnaryFunction<ComplexNumber> aFunc){
    ComplexNumber tmpValue=this.get(aRow,aCol);
    tmpValue=aFunc.invoke(tmpValue);
    this.set(aRow,aCol,tmpValue);
  }
  public void modifyRow(  final int aRow,  final int aCol,  final UnaryFunction<ComplexNumber> aFunc){
    myUtility.modifyRow(aRow,aCol,aFunc);
  }
  public void modifyRow(  final long row,  final long column,  final UnaryFunction<ComplexNumber> function){
    myUtility.modifyRow(row,column,function);
  }
  public MatrixStore<ComplexNumber> multiplyLeft(  final Access1D<ComplexNumber> left){
    final ComplexDenseStore retVal=FACTORY.makeZero(left.count() / myRowDim,myColDim);
    retVal.multiplyLeft.invoke(retVal.data(),left,myRowDim,this.data());
    return retVal;
  }
  public MatrixStore<ComplexNumber> multiplyRight(  final Access1D<ComplexNumber> right){
    final ComplexDenseStore retVal=FACTORY.makeZero(myRowDim,right.count() / myColDim);
    retVal.multiplyRight.invoke(retVal.data(),this.data(),myColDim,right);
    return retVal;
  }
  public void negateColumn(  final int aCol){
    myUtility.modifyColumn(0,aCol,ComplexFunction.NEGATE);
  }
  public void raxpy(  final ComplexNumber aSclrA,  final int aRowX,  final int aRowY,  final int aFirstCol){
    RAXPY.invoke(this.data(),aRowY,this.data(),aRowX,aSclrA,aFirstCol,myColDim);
  }
  public void rotateRight(  final int aLow,  final int aHigh,  final double aCos,  final double aSin){
    RotateRight.invoke(this.data(),myRowDim,aLow,aHigh,FACTORY.getNumber(aCos),FACTORY.getNumber(aSin));
  }
  public void set(  final long aRow,  final long aCol,  final double aNmbr){
    myUtility.set(aRow,aCol,aNmbr);
  }
  public void set(  final long aRow,  final long aCol,  final Number aNmbr){
    myUtility.set(aRow,aCol,aNmbr);
  }
  public void setToIdentity(  final int aCol){
    myUtility.set(aCol,aCol,ComplexNumber.ONE);
    myUtility.fillColumn(aCol + 1,aCol,ComplexNumber.ZERO);
  }
  public void substituteBackwards(  final Access2D<ComplexNumber> aBody,  final boolean conjugated){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > SubstituteBackwards.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          SubstituteBackwards.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,aBody,conjugated);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,SubstituteBackwards.THRESHOLD);
    }
 else {
      SubstituteBackwards.invoke(this.data(),tmpRowDim,0,tmpColDim,aBody,conjugated);
    }
  }
  public void substituteForwards(  final Access2D<ComplexNumber> aBody,  final boolean onesOnDiagonal,  final boolean zerosAboveDiagonal){
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if (tmpColDim > SubstituteForwards.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          SubstituteForwards.invoke(ComplexDenseStore.this.data(),tmpRowDim,aFirst,aLimit,aBody,onesOnDiagonal,zerosAboveDiagonal);
        }
      }
;
      tmpConquerer.invoke(0,tmpColDim,SubstituteForwards.THRESHOLD);
    }
 else {
      SubstituteForwards.invoke(this.data(),tmpRowDim,0,tmpColDim,aBody,onesOnDiagonal,zerosAboveDiagonal);
    }
  }
  public Scalar<ComplexNumber> toScalar(  final int row,  final int column){
    return myUtility.toScalar(row,column);
  }
  @Override public final String toString(){
    return MatrixUtils.toString(this);
  }
  public void transformLeft(  final Householder<ComplexNumber> aTransf,  final int aFirstCol){
    final Householder.Complex tmpTransf=ComplexDenseStore.cast(aTransf);
    final ComplexNumber[] tmpData=this.data();
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if ((tmpColDim - aFirstCol) > HouseholderLeft.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          HouseholderLeft.invoke(tmpData,tmpRowDim,aFirst,aLimit,tmpTransf);
        }
      }
;
      tmpConquerer.invoke(aFirstCol,tmpColDim,HouseholderLeft.THRESHOLD);
    }
 else {
      HouseholderLeft.invoke(tmpData,tmpRowDim,aFirstCol,tmpColDim,tmpTransf);
    }
  }
  public void transformLeft(  final Rotation<ComplexNumber> aTransf){
    final Rotation.Complex tmpTransf=ComplexDenseStore.cast(aTransf);
    final int tmpLow=tmpTransf.low;
    final int tmpHigh=tmpTransf.high;
    if (tmpLow != tmpHigh) {
      if ((tmpTransf.cos != null) && (tmpTransf.sin != null)) {
        RotateLeft.invoke(this.data(),myColDim,tmpLow,tmpHigh,tmpTransf.cos,tmpTransf.sin);
      }
 else {
        myUtility.exchangeRows(tmpLow,tmpHigh);
      }
    }
 else {
      if (tmpTransf.cos != null) {
        myUtility.modifyRow(tmpLow,0,MULTIPLY,tmpTransf.cos);
      }
 else       if (tmpTransf.sin != null) {
        myUtility.modifyRow(tmpLow,0,DIVIDE,tmpTransf.sin);
      }
 else {
        myUtility.modifyRow(tmpLow,0,NEGATE);
      }
    }
  }
  public void transformRight(  final Householder<ComplexNumber> aTransf,  final int aFirstRow){
    final Householder.Complex tmpTransf=ComplexDenseStore.cast(aTransf);
    final ComplexNumber[] tmpData=this.data();
    final int tmpRowDim=myRowDim;
    final int tmpColDim=myColDim;
    if ((tmpRowDim - aFirstRow) > HouseholderRight.THRESHOLD) {
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int aFirst,        final int aLimit){
          HouseholderRight.invoke(tmpData,aFirst,aLimit,tmpColDim,tmpTransf);
        }
      }
;
      tmpConquerer.invoke(aFirstRow,tmpRowDim,HouseholderRight.THRESHOLD);
    }
 else {
      HouseholderRight.invoke(tmpData,aFirstRow,tmpRowDim,tmpColDim,tmpTransf);
    }
  }
  public void transformRight(  final Rotation<ComplexNumber> aTransf){
    final Rotation.Complex tmpTransf=ComplexDenseStore.cast(aTransf);
    final int tmpLow=tmpTransf.low;
    final int tmpHigh=tmpTransf.high;
    if (tmpLow != tmpHigh) {
      if ((tmpTransf.cos != null) && (tmpTransf.sin != null)) {
        RotateRight.invoke(this.data(),myRowDim,tmpLow,tmpHigh,tmpTransf.cos,tmpTransf.sin);
      }
 else {
        myUtility.exchangeColumns(tmpLow,tmpHigh);
      }
    }
 else {
      if (tmpTransf.cos != null) {
        myUtility.modifyColumn(0,tmpHigh,MULTIPLY,tmpTransf.cos);
      }
 else       if (tmpTransf.sin != null) {
        myUtility.modifyColumn(0,tmpHigh,DIVIDE,tmpTransf.sin);
      }
 else {
        myUtility.modifyColumn(0,tmpHigh,NEGATE);
      }
    }
  }
  public void transformSymmetric(  final Householder<ComplexNumber> aTransf){
    HouseholderHermitian.invoke(this.data(),ComplexDenseStore.cast(aTransf),new ComplexNumber[aTransf.size()]);
  }
  public ComplexDenseStore transpose(){
    final ComplexDenseStore retVal=new ComplexDenseStore(myColDim,myRowDim);
    retVal.fillTransposed(this);
    return retVal;
  }
  public void tred2(  final SimpleArray<ComplexNumber> mainDiagonal,  final SimpleArray<ComplexNumber> offDiagonal,  final boolean yesvecs){
    throw new UnsupportedOperationException();
  }
  @Override public void visitAll(  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitAll(visitor);
  }
  public void visitColumn(  final int row,  final int column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitColumn(row,column,visitor);
  }
  public void visitColumn(  final long row,  final long column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitColumn(row,column,visitor);
  }
  public void visitDiagonal(  final int row,  final int column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitDiagonal(row,column,visitor);
  }
  public void visitDiagonal(  final long row,  final long column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitDiagonal(row,column,visitor);
  }
  public void visitRow(  final int row,  final int column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitRow(row,column,visitor);
  }
  public void visitRow(  final long row,  final long column,  final VoidFunction<ComplexNumber> visitor){
    myUtility.visitRow(row,column,visitor);
  }
}

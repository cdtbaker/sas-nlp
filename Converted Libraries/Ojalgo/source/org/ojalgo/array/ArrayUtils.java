package org.ojalgo.array;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.matrix.store.MatrixStore;
public abstract class ArrayUtils {
  public static double[] copyOf(  final double[] original){
    final int tmpLength=original.length;
    final double[] retVal=new double[tmpLength];
    System.arraycopy(original,0,retVal,0,tmpLength);
    return retVal;
  }
  public static int[] copyOf(  final int[] original){
    final int tmpLength=original.length;
    final int[] retVal=new int[tmpLength];
    System.arraycopy(original,0,retVal,0,tmpLength);
    return retVal;
  }
  public static long[] copyOf(  final long[] original){
    final int tmpLength=original.length;
    final long[] retVal=new long[tmpLength];
    System.arraycopy(original,0,retVal,0,tmpLength);
    return retVal;
  }
  @SuppressWarnings("unchecked") public static <T>T[] copyOf(  final T[] original){
    final int tmpLength=original.length;
    final T[] retVal=(T[])Array.newInstance(original.getClass().getComponentType(),tmpLength);
    System.arraycopy(original,0,retVal,0,tmpLength);
    return retVal;
  }
  public static void exchangeColumns(  final double[][] rawArray,  final int aColA,  final int aColB){
    double tmpElem;
    final int tmpLength=rawArray.length;
    for (int i=0; i < tmpLength; i++) {
      tmpElem=rawArray[i][aColA];
      rawArray[i][aColA]=rawArray[i][aColB];
      rawArray[i][aColB]=tmpElem;
    }
  }
  public static void exchangeRows(  final double[][] rawArray,  final int aRowA,  final int aRowB){
    final double[] tmpRow=rawArray[aRowA];
    rawArray[aRowA]=rawArray[aRowB];
    rawArray[aRowB]=tmpRow;
  }
  public static void fillAll(  final double[][] rawArray,  final double aNmbr){
    final int tmpLength=rawArray.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=rawArray[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        rawArray[i][j]=aNmbr;
      }
    }
  }
  public static void fillColumn(  final double[][] rawArray,  final int aRow,  final int aCol,  final double aNmbr){
    final int tmpLength=rawArray.length;
    for (int i=aRow; i < tmpLength; i++) {
      rawArray[i][aCol]=aNmbr;
    }
  }
  public static void fillDiagonal(  final double[][] rawArray,  final int aRow,  final int aCol,  final double aNmbr){
    final int tmpLength=rawArray.length;
    for (int ij=0; ((aRow + ij) < tmpLength) && ((aCol + ij) < rawArray[aRow + ij].length); ij++) {
      rawArray[aRow + ij][aCol + ij]=aNmbr;
    }
  }
  public static void fillMatching(  final double[][] anArrayToBeUpdated,  final double aLeftFirstArg,  final BinaryFunction<Double> aFunc,  final double[][] aRightSecondArg){
    final int tmpLength=anArrayToBeUpdated.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=anArrayToBeUpdated[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        anArrayToBeUpdated[i][j]=aFunc.invoke(aLeftFirstArg,aRightSecondArg[i][j]);
      }
    }
  }
  public static void fillMatching(  final double[][] anArrayToBeUpdated,  final double[][] aLeftFirstArg,  final BinaryFunction<Double> aFunc,  final double aRightSecondArg){
    final int tmpLength=anArrayToBeUpdated.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=anArrayToBeUpdated[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        anArrayToBeUpdated[i][j]=aFunc.invoke(aLeftFirstArg[i][j],aRightSecondArg);
      }
    }
  }
  public static void fillMatching(  final double[][] anArrayToBeUpdated,  final double[][] aLeftFirstArg,  final BinaryFunction<Double> aFunc,  final double[][] aRightSecondArg){
    final int tmpLength=anArrayToBeUpdated.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=anArrayToBeUpdated[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        anArrayToBeUpdated[i][j]=aFunc.invoke(aLeftFirstArg[i][j],aRightSecondArg[i][j]);
      }
    }
  }
  public static void fillRange(  final double[][] rawArray,  final int first,  final int limit,  final double value){
    final int tmpLength=rawArray.length;
    for (int index=first; index < limit; index++) {
      final int tmpRow=AccessUtils.row(index,tmpLength);
      final int tmpColumn=AccessUtils.column(index,tmpLength);
      rawArray[tmpRow][tmpColumn]=value;
    }
  }
  public static void fillRow(  final double[][] rawArray,  final int aRow,  final int aCol,  final double aNmbr){
    final int tmpLength=rawArray[aRow].length;
    for (int j=aCol; j < tmpLength; j++) {
      rawArray[aRow][j]=aNmbr;
    }
  }
  public static void modifyAll(  final double[][] rawArray,  final UnaryFunction<?> aFunc){
    final int tmpLength=rawArray.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=rawArray[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        rawArray[i][j]=aFunc.invoke(rawArray[i][j]);
      }
    }
  }
  public static void modifyColumn(  final double[][] rawArray,  final int aRow,  final int aCol,  final UnaryFunction<?> aFunc){
    final int tmpLength=rawArray.length;
    for (int i=aRow; i < tmpLength; i++) {
      rawArray[i][aCol]=aFunc.invoke(rawArray[i][aCol]);
    }
  }
  public static void modifyDiagonal(  final double[][] rawArray,  final int aRow,  final int aCol,  final UnaryFunction<?> aFunc){
    final int tmpLength=rawArray.length;
    for (int ij=0; ((aRow + ij) < tmpLength) && ((aCol + ij) < rawArray[aRow + ij].length); ij++) {
      rawArray[aRow + ij][aCol + ij]=aFunc.invoke(rawArray[aRow + ij][aCol + ij]);
    }
  }
  public static void modifyRow(  final double[][] rawArray,  final int aRow,  final int aCol,  final UnaryFunction<?> aFunc){
    final int tmpLength=rawArray[aRow].length;
    for (int j=aCol; j < tmpLength; j++) {
      rawArray[aRow][j]=aFunc.invoke(rawArray[aRow][j]);
    }
  }
  public static double[] toRawCopyOf(  final Access1D<?> original){
    final int tmpLength=original.size();
    final double[] retVal=new double[tmpLength];
    for (int i=tmpLength; i-- != 0; ) {
      retVal[i]=original.doubleValue(i);
    }
    return retVal;
  }
  public static double[][] toRawCopyOf(  final Access2D<?> original){
    final int tmpRowDim=original.getRowDim();
    final int tmpColDim=original.getColDim();
    final double[][] retVal=new double[tmpRowDim][tmpColDim];
    double[] tmpRow;
    for (int i=tmpRowDim; i-- != 0; ) {
      tmpRow=retVal[i];
      for (int j=tmpColDim; j-- != 0; ) {
        tmpRow[j]=original.doubleValue(i,j);
      }
    }
    return retVal;
  }
  public static double[][] toRawCopyOf(  final MatrixStore<?> original){
    return ArrayUtils.toRawCopyOf((Access2D<?>)original);
  }
  public static void visitAll(  final double[][] rawArray,  final VoidFunction<?> visitor){
    final int tmpLength=rawArray.length;
    for (int i=0; i < tmpLength; i++) {
      final int tmpInnerLength=rawArray[i].length;
      for (int j=0; j < tmpInnerLength; j++) {
        visitor.invoke(rawArray[i][j]);
      }
    }
  }
  public static void visitColumn(  final double[][] rawArray,  final int aRow,  final int aCol,  final VoidFunction<?> visitor){
    final int tmpLength=rawArray[aRow].length;
    for (int j=aCol; j < tmpLength; j++) {
      visitor.invoke(rawArray[aRow][j]);
    }
  }
  public static void visitDiagonal(  final double[][] rawArray,  final int aRow,  final int aCol,  final VoidFunction<?> visitor){
    final int tmpLength=rawArray.length;
    for (int ij=0; ((aRow + ij) < tmpLength) && ((aCol + ij) < rawArray[aRow + ij].length); ij++) {
      visitor.invoke(rawArray[aRow + ij][aCol + ij]);
    }
  }
  public static void visitRange(  final double[][] rawArray,  final int first,  final int limit,  final VoidFunction<?> visitor){
    final int tmpStructure=rawArray.length;
    for (int index=first; index < limit; index++) {
      visitor.invoke(rawArray[AccessUtils.row(index,tmpStructure)][AccessUtils.column(index,tmpStructure)]);
    }
  }
  public static void visitRow(  final double[][] rawArray,  final int aRow,  final int aCol,  final VoidFunction<?> visitor){
    final int tmpLength=rawArray.length;
    for (int i=aRow; i < tmpLength; i++) {
      visitor.invoke(rawArray[i][aCol]);
    }
  }
  public static Access1D<Double> wrapAccess1D(  final double[] aRaw){
    return new Access1D<Double>(){
      public long count(){
        return aRaw.length;
      }
      public double doubleValue(      final long index){
        return aRaw[(int)index];
      }
      public Double get(      final long index){
        return aRaw[(int)index];
      }
      public final Iterator<Double> iterator(){
        return new Iterator1D<Double>(this);
      }
      public int size(){
        return aRaw.length;
      }
    }
;
  }
  public static <N extends Number>Access1D<N> wrapAccess1D(  final List<? extends N> aList){
    return new Access1D<N>(){
      public long count(){
        return aList.size();
      }
      public double doubleValue(      final long index){
        return aList.get((int)index).doubleValue();
      }
      public N get(      final long index){
        return aList.get((int)index);
      }
      public final Iterator<N> iterator(){
        return new Iterator1D<N>(this);
      }
      public int size(){
        return aList.size();
      }
    }
;
  }
  public static <N extends Number>Access1D<N> wrapAccess1D(  final N[] aRaw){
    return new Access1D<N>(){
      public long count(){
        return aRaw.length;
      }
      public double doubleValue(      final long index){
        return aRaw[(int)index].doubleValue();
      }
      public N get(      final long index){
        return aRaw[(int)index];
      }
      public final Iterator<N> iterator(){
        return new Iterator1D<N>(this);
      }
      public int size(){
        return aRaw.length;
      }
    }
;
  }
  public static Access2D<Double> wrapAccess2D(  final double[][] aRaw){
    return new Access2D<Double>(){
      public long count(){
        return aRaw.length * aRaw[0].length;
      }
      public long countColumns(){
        return aRaw[0].length;
      }
      public long countRows(){
        return aRaw.length;
      }
      public double doubleValue(      final long index){
        return aRaw[AccessUtils.row((int)index,aRaw.length)][AccessUtils.column((int)index,aRaw.length)];
      }
      public double doubleValue(      final long aRow,      final long aCol){
        return aRaw[(int)aRow][(int)aCol];
      }
      public Double get(      final long index){
        return aRaw[AccessUtils.row((int)index,aRaw.length)][AccessUtils.column((int)index,aRaw.length)];
      }
      public Double get(      final long aRow,      final long aCol){
        return aRaw[(int)aRow][(int)aCol];
      }
      public int getColDim(){
        return aRaw[0].length;
      }
      public int getRowDim(){
        return aRaw.length;
      }
      public Iterator<Double> iterator(){
        return new Iterator1D<Double>(this);
      }
      public int size(){
        return aRaw.length * aRaw[0].length;
      }
    }
;
  }
  public static <N extends Number>Access2D<N> wrapAccess2D(  final N[][] aRaw){
    return new Access2D<N>(){
      public long count(){
        return aRaw.length * aRaw[0].length;
      }
      public long countColumns(){
        return aRaw[0].length;
      }
      public long countRows(){
        return aRaw.length;
      }
      public double doubleValue(      final long index){
        return aRaw[AccessUtils.row((int)index,aRaw.length)][AccessUtils.column((int)index,aRaw.length)].doubleValue();
      }
      public double doubleValue(      final long aRow,      final long aCol){
        return aRaw[(int)aRow][(int)aCol].doubleValue();
      }
      public N get(      final long index){
        return null;
      }
      public N get(      final long aRow,      final long aCol){
        return aRaw[(int)aRow][(int)aCol];
      }
      public int getColDim(){
        return aRaw[0].length;
      }
      public int getRowDim(){
        return aRaw.length;
      }
      public Iterator<N> iterator(){
        return new Iterator1D<N>(this);
      }
      public int size(){
        return aRaw.length * aRaw[0].length;
      }
    }
;
  }
  private ArrayUtils(){
    super();
  }
}

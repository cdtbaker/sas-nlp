package org.ojalgo.matrix.decomposition;
import java.math.BigDecimal;
import java.util.Iterator;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.ComplexNumber;
final class DiagonalAccess<N extends Number> implements Access2D<N> {
  public static DiagonalAccess<BigDecimal> makeBig(  final Array1D<BigDecimal> mainD,  final Array1D<BigDecimal> superD,  final Array1D<BigDecimal> subD){
    return new DiagonalAccess<BigDecimal>(mainD,superD,subD,BigMath.ZERO);
  }
  public static DiagonalAccess<ComplexNumber> makeComplex(  final Array1D<ComplexNumber> mainD,  final Array1D<ComplexNumber> superD,  final Array1D<ComplexNumber> subD){
    return new DiagonalAccess<ComplexNumber>(mainD,superD,subD,ComplexNumber.ZERO);
  }
  public static DiagonalAccess<Double> makePrimitive(  final Array1D<Double> mainD,  final Array1D<Double> superD,  final Array1D<Double> subD){
    return new DiagonalAccess<Double>(mainD,superD,subD,PrimitiveMath.ZERO);
  }
  private final int myDim;
  private final N myZero;
  final Array1D<N> mainDiagonal;
  final Array1D<N> subdiagonal;
  final Array1D<N> superdiagonal;
  @SuppressWarnings("unused") private DiagonalAccess(){
    this(null,null,null,null);
  }
  DiagonalAccess(  final Array1D<N> aMainDiagonal,  final Array1D<N> aSuperdiagonal,  final Array1D<N> aSubdiagonal,  final N aZero){
    super();
    mainDiagonal=aMainDiagonal;
    superdiagonal=aSuperdiagonal;
    subdiagonal=aSubdiagonal;
    myZero=aZero;
    if (aMainDiagonal != null) {
      myDim=aMainDiagonal.length;
    }
 else     if (aSuperdiagonal != null) {
      myDim=aSuperdiagonal.length + 1;
    }
 else {
      myDim=aSubdiagonal.length + 1;
    }
  }
  public DiagonalAccess<N> columns(  final int aFirst,  final int aLimit){
    final Array1D<N> tmpMainDiagonal=mainDiagonal != null ? mainDiagonal.subList(aFirst,aLimit) : null;
    final Array1D<N> tmpSuperdiagonal=superdiagonal != null ? superdiagonal.subList(Math.max(aFirst - 1,0),aLimit - 1) : null;
    final Array1D<N> tmpSubdiagonal=subdiagonal != null ? subdiagonal.subList(aFirst,Math.min(aLimit,myDim - 1)) : null;
    return new DiagonalAccess<N>(tmpMainDiagonal,tmpSuperdiagonal,tmpSubdiagonal,myZero);
  }
  public long count(){
    return this.size();
  }
  public long countColumns(){
    return myDim;
  }
  public long countRows(){
    return myDim;
  }
  public double doubleValue(  final long anInd){
    return this.doubleValue(AccessUtils.row((int)anInd,myDim),AccessUtils.column((int)anInd,myDim));
  }
  public double doubleValue(  final long aRow,  final long aCol){
    if ((mainDiagonal != null) && (aRow == aCol)) {
      return mainDiagonal.doubleValue(aRow);
    }
 else     if ((superdiagonal != null) && ((aCol - aRow) == 1)) {
      return superdiagonal.doubleValue(aRow);
    }
 else     if ((subdiagonal != null) && ((aRow - aCol) == 1)) {
      return subdiagonal.doubleValue(aCol);
    }
 else {
      return PrimitiveMath.ZERO;
    }
  }
  public N get(  final long index){
    return this.get(AccessUtils.row(index,myDim),AccessUtils.column(index,myDim));
  }
  public N get(  final long aRow,  final long aCol){
    if ((mainDiagonal != null) && (aRow == aCol)) {
      return mainDiagonal.get(aRow);
    }
 else     if ((superdiagonal != null) && ((aCol - aRow) == 1)) {
      return superdiagonal.get(aRow);
    }
 else     if ((subdiagonal != null) && ((aRow - aCol) == 1)) {
      return subdiagonal.get(aCol);
    }
 else {
      return myZero;
    }
  }
  public int getColDim(){
    return myDim;
  }
  public int getMinDim(){
    return myDim;
  }
  public int getRowDim(){
    return myDim;
  }
  public Iterator<N> iterator(){
    return new Iterator1D<N>(this);
  }
  public DiagonalAccess<N> rows(  final int aFirst,  final int aLimit){
    final Array1D<N> tmpMainDiagonal=mainDiagonal != null ? mainDiagonal.subList(aFirst,aLimit) : null;
    final Array1D<N> tmpSuperdiagonal=superdiagonal != null ? superdiagonal.subList(aFirst,Math.min(aLimit,myDim - 1)) : null;
    final Array1D<N> tmpSubdiagonal=subdiagonal != null ? subdiagonal.subList(Math.max(aFirst - 1,0),aLimit - 1) : null;
    return new DiagonalAccess<N>(tmpMainDiagonal,tmpSuperdiagonal,tmpSubdiagonal,myZero);
  }
  public int size(){
    return myDim * myDim;
  }
  @Override public String toString(){
    return "DiagonalAccess [mainDiagonal=" + mainDiagonal + ", subdiagonal="+ subdiagonal+ ", superdiagonal="+ superdiagonal+ "]";
  }
  public final DiagonalAccess<N> transpose(){
    return new DiagonalAccess<N>(mainDiagonal,subdiagonal,superdiagonal,myZero);
  }
}

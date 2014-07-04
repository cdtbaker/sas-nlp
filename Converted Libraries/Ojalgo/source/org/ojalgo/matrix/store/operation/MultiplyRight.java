package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.store.BigDenseStore.BigMultiplyRight;
import org.ojalgo.matrix.store.ComplexDenseStore.ComplexMultiplyRight;
import org.ojalgo.matrix.store.PrimitiveDenseStore.PrimitiveMultiplyRight;
import org.ojalgo.scalar.ComplexNumber;
public final class MultiplyRight extends MatrixOperation {
  public static int THRESHOLD=32;
  static final BigMultiplyRight BIG=new BigMultiplyRight(){
    public void invoke(    final BigDecimal[] product,    final BigDecimal[] left,    final int complexity,    final Access1D<BigDecimal> right){
      MultiplyRight.invoke(product,0,((int)right.count()) / complexity,left,complexity,right);
    }
  }
;
  static final BigMultiplyRight BIG_MT=new BigMultiplyRight(){
    public void invoke(    final BigDecimal[] product,    final BigDecimal[] left,    final int complexity,    final Access1D<BigDecimal> right){
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int first,        final int limit){
          MultiplyRight.invoke(product,first,limit,left,complexity,right);
        }
      }
;
      tmpConquerer.invoke(0,((int)right.count()) / complexity,THRESHOLD);
    }
  }
;
  static final ComplexMultiplyRight COMPLEX=new ComplexMultiplyRight(){
    public void invoke(    final ComplexNumber[] product,    final ComplexNumber[] left,    final int complexity,    final Access1D<ComplexNumber> right){
      MultiplyRight.invoke(product,0,((int)right.count()) / complexity,left,complexity,right);
    }
  }
;
  static final ComplexMultiplyRight COMPLEX_MT=new ComplexMultiplyRight(){
    public void invoke(    final ComplexNumber[] product,    final ComplexNumber[] left,    final int complexity,    final Access1D<ComplexNumber> right){
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int first,        final int limit){
          MultiplyRight.invoke(product,first,limit,left,complexity,right);
        }
      }
;
      tmpConquerer.invoke(0,((int)right.count()) / complexity,THRESHOLD);
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      MultiplyRight.invoke(product,0,((int)right.count()) / complexity,left,complexity,right);
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_1X1=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      double tmp00=PrimitiveMath.ZERO;
      final int tmpLeftStruct=left.length / complexity;
      for (int c=0; c < complexity; c++) {
        tmp00+=left[c * tmpLeftStruct] * right.doubleValue(c);
      }
      product[0]=tmp00;
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_2X2=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      double tmp00=PrimitiveMath.ZERO;
      double tmp10=PrimitiveMath.ZERO;
      double tmp01=PrimitiveMath.ZERO;
      double tmp11=PrimitiveMath.ZERO;
      final int tmpLeftStruct=left.length / complexity;
      int tmpIndex;
      for (long c=0; c < complexity; c++) {
        tmpIndex=(int)(c * tmpLeftStruct);
        final double tmpLeft0=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft1=left[tmpIndex];
        tmpIndex=(int)c;
        final double tmpRight0=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight1=right.doubleValue(tmpIndex);
        tmp00+=tmpLeft0 * tmpRight0;
        tmp10+=tmpLeft1 * tmpRight0;
        tmp01+=tmpLeft0 * tmpRight1;
        tmp11+=tmpLeft1 * tmpRight1;
      }
      product[0]=tmp00;
      product[1]=tmp10;
      product[2]=tmp01;
      product[3]=tmp11;
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_3X3=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      double tmp00=PrimitiveMath.ZERO;
      double tmp10=PrimitiveMath.ZERO;
      double tmp20=PrimitiveMath.ZERO;
      double tmp01=PrimitiveMath.ZERO;
      double tmp11=PrimitiveMath.ZERO;
      double tmp21=PrimitiveMath.ZERO;
      double tmp02=PrimitiveMath.ZERO;
      double tmp12=PrimitiveMath.ZERO;
      double tmp22=PrimitiveMath.ZERO;
      final int tmpLeftStruct=left.length / complexity;
      int tmpIndex;
      for (long c=0; c < complexity; c++) {
        tmpIndex=(int)(c * tmpLeftStruct);
        final double tmpLeft0=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft1=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft2=left[tmpIndex];
        tmpIndex=(int)c;
        final double tmpRight0=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight1=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight2=right.doubleValue(tmpIndex);
        tmp00+=tmpLeft0 * tmpRight0;
        tmp10+=tmpLeft1 * tmpRight0;
        tmp20+=tmpLeft2 * tmpRight0;
        tmp01+=tmpLeft0 * tmpRight1;
        tmp11+=tmpLeft1 * tmpRight1;
        tmp21+=tmpLeft2 * tmpRight1;
        tmp02+=tmpLeft0 * tmpRight2;
        tmp12+=tmpLeft1 * tmpRight2;
        tmp22+=tmpLeft2 * tmpRight2;
      }
      product[0]=tmp00;
      product[1]=tmp10;
      product[2]=tmp20;
      product[3]=tmp01;
      product[4]=tmp11;
      product[5]=tmp21;
      product[6]=tmp02;
      product[7]=tmp12;
      product[8]=tmp22;
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_4X4=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      double tmp00=PrimitiveMath.ZERO;
      double tmp10=PrimitiveMath.ZERO;
      double tmp20=PrimitiveMath.ZERO;
      double tmp30=PrimitiveMath.ZERO;
      double tmp01=PrimitiveMath.ZERO;
      double tmp11=PrimitiveMath.ZERO;
      double tmp21=PrimitiveMath.ZERO;
      double tmp31=PrimitiveMath.ZERO;
      double tmp02=PrimitiveMath.ZERO;
      double tmp12=PrimitiveMath.ZERO;
      double tmp22=PrimitiveMath.ZERO;
      double tmp32=PrimitiveMath.ZERO;
      double tmp03=PrimitiveMath.ZERO;
      double tmp13=PrimitiveMath.ZERO;
      double tmp23=PrimitiveMath.ZERO;
      double tmp33=PrimitiveMath.ZERO;
      final int tmpLeftStruct=left.length / complexity;
      int tmpIndex;
      for (long c=0; c < complexity; c++) {
        tmpIndex=(int)(c * tmpLeftStruct);
        final double tmpLeft0=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft1=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft2=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft3=left[tmpIndex];
        tmpIndex=(int)c;
        final double tmpRight0=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight1=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight2=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight3=right.doubleValue(tmpIndex);
        tmp00+=tmpLeft0 * tmpRight0;
        tmp10+=tmpLeft1 * tmpRight0;
        tmp20+=tmpLeft2 * tmpRight0;
        tmp30+=tmpLeft3 * tmpRight0;
        tmp01+=tmpLeft0 * tmpRight1;
        tmp11+=tmpLeft1 * tmpRight1;
        tmp21+=tmpLeft2 * tmpRight1;
        tmp31+=tmpLeft3 * tmpRight1;
        tmp02+=tmpLeft0 * tmpRight2;
        tmp12+=tmpLeft1 * tmpRight2;
        tmp22+=tmpLeft2 * tmpRight2;
        tmp32+=tmpLeft3 * tmpRight2;
        tmp03+=tmpLeft0 * tmpRight3;
        tmp13+=tmpLeft1 * tmpRight3;
        tmp23+=tmpLeft2 * tmpRight3;
        tmp33+=tmpLeft3 * tmpRight3;
      }
      product[0]=tmp00;
      product[1]=tmp10;
      product[2]=tmp20;
      product[3]=tmp30;
      product[4]=tmp01;
      product[5]=tmp11;
      product[6]=tmp21;
      product[7]=tmp31;
      product[8]=tmp02;
      product[9]=tmp12;
      product[10]=tmp22;
      product[11]=tmp32;
      product[12]=tmp03;
      product[13]=tmp13;
      product[14]=tmp23;
      product[15]=tmp33;
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_5X5=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      double tmp00=PrimitiveMath.ZERO;
      double tmp10=PrimitiveMath.ZERO;
      double tmp20=PrimitiveMath.ZERO;
      double tmp30=PrimitiveMath.ZERO;
      double tmp40=PrimitiveMath.ZERO;
      double tmp01=PrimitiveMath.ZERO;
      double tmp11=PrimitiveMath.ZERO;
      double tmp21=PrimitiveMath.ZERO;
      double tmp31=PrimitiveMath.ZERO;
      double tmp41=PrimitiveMath.ZERO;
      double tmp02=PrimitiveMath.ZERO;
      double tmp12=PrimitiveMath.ZERO;
      double tmp22=PrimitiveMath.ZERO;
      double tmp32=PrimitiveMath.ZERO;
      double tmp42=PrimitiveMath.ZERO;
      double tmp03=PrimitiveMath.ZERO;
      double tmp13=PrimitiveMath.ZERO;
      double tmp23=PrimitiveMath.ZERO;
      double tmp33=PrimitiveMath.ZERO;
      double tmp43=PrimitiveMath.ZERO;
      double tmp04=PrimitiveMath.ZERO;
      double tmp14=PrimitiveMath.ZERO;
      double tmp24=PrimitiveMath.ZERO;
      double tmp34=PrimitiveMath.ZERO;
      double tmp44=PrimitiveMath.ZERO;
      final int tmpLeftStruct=left.length / complexity;
      int tmpIndex;
      for (long c=0; c < complexity; c++) {
        tmpIndex=(int)(c * tmpLeftStruct);
        final double tmpLeft0=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft1=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft2=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft3=left[tmpIndex];
        tmpIndex++;
        final double tmpLeft4=left[tmpIndex];
        tmpIndex=(int)c;
        final double tmpRight0=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight1=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight2=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight3=right.doubleValue(tmpIndex);
        tmpIndex+=complexity;
        final double tmpRight4=right.doubleValue(tmpIndex);
        tmp00+=tmpLeft0 * tmpRight0;
        tmp10+=tmpLeft1 * tmpRight0;
        tmp20+=tmpLeft2 * tmpRight0;
        tmp30+=tmpLeft3 * tmpRight0;
        tmp40+=tmpLeft4 * tmpRight0;
        tmp01+=tmpLeft0 * tmpRight1;
        tmp11+=tmpLeft1 * tmpRight1;
        tmp21+=tmpLeft2 * tmpRight1;
        tmp31+=tmpLeft3 * tmpRight1;
        tmp41+=tmpLeft4 * tmpRight1;
        tmp02+=tmpLeft0 * tmpRight2;
        tmp12+=tmpLeft1 * tmpRight2;
        tmp22+=tmpLeft2 * tmpRight2;
        tmp32+=tmpLeft3 * tmpRight2;
        tmp42+=tmpLeft4 * tmpRight2;
        tmp03+=tmpLeft0 * tmpRight3;
        tmp13+=tmpLeft1 * tmpRight3;
        tmp23+=tmpLeft2 * tmpRight3;
        tmp33+=tmpLeft3 * tmpRight3;
        tmp43+=tmpLeft4 * tmpRight3;
        tmp04+=tmpLeft0 * tmpRight4;
        tmp14+=tmpLeft1 * tmpRight4;
        tmp24+=tmpLeft2 * tmpRight4;
        tmp34+=tmpLeft3 * tmpRight4;
        tmp44+=tmpLeft4 * tmpRight4;
      }
      product[0]=tmp00;
      product[1]=tmp10;
      product[2]=tmp20;
      product[3]=tmp30;
      product[4]=tmp40;
      product[5]=tmp01;
      product[6]=tmp11;
      product[7]=tmp21;
      product[8]=tmp31;
      product[9]=tmp41;
      product[10]=tmp02;
      product[11]=tmp12;
      product[12]=tmp22;
      product[13]=tmp32;
      product[14]=tmp42;
      product[15]=tmp03;
      product[16]=tmp13;
      product[17]=tmp23;
      product[18]=tmp33;
      product[19]=tmp43;
      product[20]=tmp04;
      product[21]=tmp14;
      product[22]=tmp24;
      product[23]=tmp34;
      product[24]=tmp44;
    }
  }
;
  static final PrimitiveMultiplyRight PRIMITIVE_MT=new PrimitiveMultiplyRight(){
    public void invoke(    final double[] product,    final double[] left,    final int complexity,    final Access1D<?> right){
      final DivideAndConquer tmpConquerer=new DivideAndConquer(){
        @Override public void conquer(        final int first,        final int limit){
          MultiplyRight.invoke(product,first,limit,left,complexity,right);
        }
      }
;
      tmpConquerer.invoke(0,((int)right.count()) / complexity,THRESHOLD);
    }
  }
;
  public static BigMultiplyRight getBig(  final long rows,  final long columns){
    if (rows > THRESHOLD) {
      return BIG_MT;
    }
 else {
      return BIG;
    }
  }
  public static ComplexMultiplyRight getComplex(  final long rows,  final long columns){
    if (rows > THRESHOLD) {
      return COMPLEX_MT;
    }
 else {
      return COMPLEX;
    }
  }
  public static PrimitiveMultiplyRight getPrimitive(  final long rows,  final long columns){
    if (columns > THRESHOLD) {
      return PRIMITIVE_MT;
    }
 else     if ((rows == 5) && (columns == 5)) {
      return PRIMITIVE_5X5;
    }
 else     if ((rows == 4) && (columns == 4)) {
      return PRIMITIVE_4X4;
    }
 else     if ((rows == 3) && (columns == 3)) {
      return PRIMITIVE_3X3;
    }
 else     if ((rows == 2) && (columns == 2)) {
      return PRIMITIVE_2X2;
    }
 else     if ((rows == 1) && (columns == 1)) {
      return PRIMITIVE_1X1;
    }
 else {
      return PRIMITIVE;
    }
  }
  static void invoke(  final BigDecimal[] product,  final int firstColumn,  final int columnLimit,  final BigDecimal[] left,  final int complexity,  final Access1D<BigDecimal> right){
    final int tmpRowDim=left.length / complexity;
    final int tmpIndexLimit=tmpRowDim * columnLimit;
    for (int tmpIndex=tmpRowDim * firstColumn; tmpIndex < tmpIndexLimit; tmpIndex++) {
      product[tmpIndex]=BigMath.ZERO;
    }
    for (int j=firstColumn; j < columnLimit; j++) {
      for (int c=0; c < complexity; c++) {
        CAXPY.invoke(product,j * tmpRowDim,left,c * tmpRowDim,right.get(c + (j * complexity)),0,tmpRowDim);
      }
    }
  }
  static void invoke(  final ComplexNumber[] product,  final int firstColumn,  final int columnLimit,  final ComplexNumber[] left,  final int complexity,  final Access1D<ComplexNumber> right){
    final int tmpRowDim=left.length / complexity;
    final int tmpIndexLimit=tmpRowDim * columnLimit;
    for (int tmpIndex=tmpRowDim * firstColumn; tmpIndex < tmpIndexLimit; tmpIndex++) {
      product[tmpIndex]=ComplexNumber.ZERO;
    }
    for (int j=firstColumn; j < columnLimit; j++) {
      for (int c=0; c < complexity; c++) {
        CAXPY.invoke(product,j * tmpRowDim,left,c * tmpRowDim,right.get(c + (j * complexity)),0,tmpRowDim);
      }
    }
  }
  static void invoke(  final double[] product,  final int firstColumn,  final int columnLimit,  final double[] left,  final int complexity,  final Access1D<?> right){
    final int tmpRowDim=left.length / complexity;
    final int tmpIndexLimit=tmpRowDim * columnLimit;
    for (int tmpIndex=tmpRowDim * firstColumn; tmpIndex < tmpIndexLimit; tmpIndex++) {
      product[tmpIndex]=PrimitiveMath.ZERO;
    }
    for (int j=firstColumn; j < columnLimit; j++) {
      for (int c=0; c < complexity; c++) {
        CAXPY.invoke(product,j * tmpRowDim,left,c * tmpRowDim,right.doubleValue(c + (j * complexity)),0,tmpRowDim);
      }
    }
  }
  private MultiplyRight(){
    super();
  }
}

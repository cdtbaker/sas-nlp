package org.ojalgo.matrix.jama;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;
/** 
 * This class adapts JAMA's LUDecomposition to ojAlgo's {@linkplain LU} interface.
 * @author apete
 */
public final class JamaLU extends JamaAbstractDecomposition implements LU<Double> {
  private LUDecomposition myDelegate;
  /** 
 * Not recommended to use this constructor directly. Consider using the static factory method{@linkplain org.ojalgo.matrix.decomposition.LUDecomposition#makeJama()} instead.
 */
  public JamaLU(){
    super();
  }
  public Double calculateDeterminant(  final Access2D<Double> matrix){
    this.compute(matrix);
    return this.getDeterminant();
  }
  public boolean computeWithoutPivoting(  final MatrixStore<?> matrix){
    return this.compute(matrix);
  }
  public boolean equals(  final MatrixStore<Double> aStore,  final NumberContext context){
    return MatrixUtils.equals(aStore,this,context);
  }
  public Double getDeterminant(){
    return myDelegate.det();
  }
  @Override public JamaMatrix getInverse(){
    return this.solve(this.makeEyeStore(myDelegate.getL().getRowDimension(),myDelegate.getU().getColumnDimension()));
  }
  public JamaMatrix getL(){
    return new JamaMatrix(myDelegate.getL());
  }
  public int[] getPivotOrder(){
    return myDelegate.getPivot();
  }
  public int getRank(){
    int retVal=0;
    final MatrixStore<Double> tmpU=this.getU();
    final int tmpMinDim=tmpU.getMinDim();
    for (int ij=0; ij < tmpMinDim; ij++) {
      if (!tmpU.toScalar(ij,ij).isZero()) {
        retVal++;
      }
    }
    return retVal;
  }
  public int[] getReducedPivots(){
    return null;
  }
  public JamaMatrix getU(){
    return new JamaMatrix(myDelegate.getU());
  }
  public boolean isAspectRatioNormal(){
    return myDelegate.getL().getRowDimension() >= myDelegate.getU().getColumnDimension();
  }
  public boolean isComputed(){
    return myDelegate != null;
  }
  public boolean isFullSize(){
    return false;
  }
  public boolean isSolvable(){
    return (myDelegate != null) && myDelegate.isNonsingular();
  }
  public boolean isSquareAndNotSingular(){
    return (myDelegate != null) && (myDelegate.getL().getRowDimension() == myDelegate.getU().getColumnDimension()) && myDelegate.isNonsingular();
  }
  public MatrixStore<Double> reconstruct(){
    return MatrixUtils.reconstruct(this);
  }
  public void reset(){
    myDelegate=null;
  }
  @Override public String toString(){
    return myDelegate.toString();
  }
  @Override boolean compute(  final Matrix aDelegate){
    myDelegate=new LUDecomposition(aDelegate);
    return this.isComputed();
  }
  @Override Matrix solve(  final Matrix aRHS){
    return myDelegate.solve(aRHS);
  }
}

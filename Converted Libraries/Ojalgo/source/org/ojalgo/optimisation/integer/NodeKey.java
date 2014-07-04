package org.ojalgo.optimisation.integer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;
final class NodeKey implements Serializable, Comparable<NodeKey> {
  private final int[] myLowerBounds;
  private final int[] myUpperBounds;
  /** 
 * The parent node objective function value
 */
  private final double myParentValue;
  private NodeKey(  final int[] lowerBounds,  final int[] upperBounds,  final double parentValue){
    super();
    myLowerBounds=lowerBounds;
    myUpperBounds=upperBounds;
    myParentValue=parentValue;
  }
  NodeKey(  final ExpressionsBasedModel integerModel){
    super();
    final List<Variable> tmpIntegerVariables=integerModel.getIntegerVariables();
    final int tmpLength=tmpIntegerVariables.size();
    myLowerBounds=new int[tmpLength];
    myUpperBounds=new int[tmpLength];
    Arrays.fill(myLowerBounds,Integer.MIN_VALUE);
    Arrays.fill(myUpperBounds,Integer.MAX_VALUE);
    for (int i=0; i < tmpLength; i++) {
      final Variable tmpVariable=tmpIntegerVariables.get(i);
      final BigDecimal tmpLowerLimit=tmpVariable.getLowerLimit();
      if (tmpLowerLimit != null) {
        myLowerBounds[i]=tmpLowerLimit.intValue();
      }
      final BigDecimal tmpUpperLimit=tmpVariable.getUpperLimit();
      if (tmpUpperLimit != null) {
        myUpperBounds[i]=tmpUpperLimit.intValue();
      }
    }
    if (integerModel.isMinimisation()) {
      myParentValue=PrimitiveMath.POSITIVE_INFINITY;
    }
 else {
      myParentValue=PrimitiveMath.NEGATIVE_INFINITY;
    }
  }
  public int compareTo(  final NodeKey ref){
    return Double.compare(myParentValue,ref.getParentValue());
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof NodeKey)) {
      return false;
    }
    final NodeKey other=(NodeKey)obj;
    if (!Arrays.equals(myLowerBounds,other.myLowerBounds)) {
      return false;
    }
    if (!Arrays.equals(myUpperBounds,other.myUpperBounds)) {
      return false;
    }
    return true;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + Arrays.hashCode(myLowerBounds);
    result=(prime * result) + Arrays.hashCode(myUpperBounds);
    return result;
  }
  @Override public String toString(){
    final StringBuilder retVal=new StringBuilder(Double.toString(myParentValue));
    retVal.append(':');
    retVal.append(' ');
    retVal.append('[');
    if (myLowerBounds.length > 0) {
      this.append(retVal,0);
    }
    for (int i=1; i < myLowerBounds.length; i++) {
      retVal.append(',');
      retVal.append(' ');
      this.append(retVal,i);
    }
    return retVal.append(']').toString();
  }
  private void append(  final StringBuilder builder,  final int index){
    builder.append(index);
    builder.append('=');
    builder.append(myLowerBounds[index]);
    builder.append('<');
    builder.append(myUpperBounds[index]);
  }
  private int[] getLowerBounds(){
    return ArrayUtils.copyOf(myLowerBounds);
  }
  private int[] getUpperBounds(){
    return ArrayUtils.copyOf(myUpperBounds);
  }
  long calculateTreeSize(){
    long retVal=1;
    final int tmpLength=myLowerBounds.length;
    for (int i=0; i < tmpLength; i++) {
      retVal*=(1L + (myUpperBounds[i] - myLowerBounds[i]));
    }
    return retVal;
  }
  NodeKey createLowerBranch(  final int index,  final double value,  final double parentValue){
    final int[] tmpLBs=this.getLowerBounds();
    final int[] tmpUBs=this.getUpperBounds();
    final int tmpFloor=(int)Math.floor(value);
    if ((tmpFloor >= tmpUBs[index]) && (tmpFloor > tmpLBs[index])) {
      tmpUBs[index]=tmpFloor - 1;
    }
 else {
      tmpUBs[index]=tmpFloor;
    }
    return new NodeKey(tmpLBs,tmpUBs,parentValue);
  }
  NodeKey createUpperBranch(  final int index,  final double value,  final double parentValue){
    final int[] tmpLBs=this.getLowerBounds();
    final int[] tmpUBs=this.getUpperBounds();
    final int tmpCeil=(int)Math.ceil(value);
    if ((tmpCeil <= tmpLBs[index]) && (tmpCeil < tmpUBs[index])) {
      tmpLBs[index]=tmpCeil + 1;
    }
 else {
      tmpLBs[index]=tmpCeil;
    }
    return new NodeKey(tmpLBs,tmpUBs,parentValue);
  }
  double getIntegerFraction(  final int index,  final double value){
    final double tmpFeasibleValue=Math.min(Math.max(myLowerBounds[index],value),myUpperBounds[index]);
    return Math.abs(tmpFeasibleValue - Math.rint(tmpFeasibleValue));
  }
  BigDecimal getLowerBound(  final int index){
    return new BigDecimal(myLowerBounds[index]);
  }
  double getParentValue(){
    return myParentValue;
  }
  BigDecimal getUpperBound(  final int index){
    return new BigDecimal(myUpperBounds[index]);
  }
}

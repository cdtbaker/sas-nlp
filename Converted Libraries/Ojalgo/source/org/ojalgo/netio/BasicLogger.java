package org.ojalgo.netio;
import java.io.PrintStream;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BigMatrix;
import org.ojalgo.matrix.ComplexMatrix;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
/** 
 * An extremely simple/basic logging system that uses{@linkplain System#out}, {@linkplain System#err} or any other{@linkplain PrintStream}.
 * @author apete
 */
public class BasicLogger {
  public static PrintStream DEBUG=System.out;
  public static PrintStream ERROR=System.err;
  public static void logDebug(){
    BasicLogger.println(DEBUG);
  }
  public static void logDebug(  final String aMessage){
    BasicLogger.println(DEBUG,aMessage);
  }
  public static void logDebug(  final String aMessage,  final Access2D<?> aStore){
    BasicLogger.println(DEBUG,aMessage);
    BasicLogger.printMtrx(DEBUG,aStore,NumberContext.getGeneral(6));
  }
  public static void logDebug(  final String aMessagePattern,  final Object... someArgs){
    BasicLogger.println(DEBUG,aMessagePattern,someArgs);
  }
  public static void logDebugStackTrace(  final String aMessage){
    BasicLogger.printStackTrace(DEBUG,aMessage);
  }
  public static void logError(){
    BasicLogger.println(ERROR);
  }
  public static void logError(  final String aMessage){
    BasicLogger.println(ERROR,aMessage);
  }
  public static void logError(  final String aMessage,  final Access2D<?> aStore){
    BasicLogger.println(ERROR,aMessage);
    BasicLogger.printMtrx(ERROR,aStore,NumberContext.getGeneral(6));
  }
  public static void logError(  final String aMessage,  final BasicMatrix aMtrx){
    BasicLogger.println(ERROR,aMessage);
    BasicLogger.printMtrx(ERROR,aMtrx,NumberContext.getGeneral(6),true);
  }
  public static void logError(  final String aMessagePattern,  final Object... someArgs){
    BasicLogger.println(ERROR,aMessagePattern,someArgs);
  }
  public static void logErrorStackTrace(  final String aMessage){
    BasicLogger.printStackTrace(ERROR,aMessage);
  }
  public static void println(  final PrintStream aStream){
    aStream.println();
  }
  public static void println(  final PrintStream aStream,  final String aMessage){
    aStream.println(aMessage);
  }
  public static void println(  final PrintStream aStream,  final String aMessagePattern,  final Object... someArgs){
    aStream.println(TypeUtils.format(aMessagePattern,someArgs));
  }
  public static void printMtrx(  final PrintStream aStream,  final Access2D<?> aStore,  final NumberContext aCntxt){
    if (aStore.get(0,0) instanceof ComplexNumber) {
      BasicLogger.printMtrx(aStream,ComplexMatrix.FACTORY.copy(aStore),aCntxt,false);
    }
 else {
      BasicLogger.printMtrx(aStream,BigMatrix.FACTORY.copy(aStore),aCntxt,true);
    }
  }
  public static void printMtrx(  final PrintStream aStream,  final BasicMatrix aMtrx,  final NumberContext aCntxt,  final boolean plainString){
    final int tmpRowDim=aMtrx.getRowDim();
    final int tmpColDim=aMtrx.getColDim();
    final String[][] tmpElements=new String[tmpRowDim][tmpColDim];
    int tmpWidth=0;
    Scalar<?> tmpElementNumber;
    String tmpElementString;
    for (int j=0; j < tmpColDim; j++) {
      for (int i=0; i < tmpRowDim; i++) {
        tmpElementNumber=aMtrx.toScalar(i,j);
        if (plainString) {
          tmpElementString=tmpElementNumber.toPlainString(aCntxt);
        }
 else {
          tmpElementString=tmpElementNumber.toString(aCntxt);
        }
        tmpWidth=Math.max(tmpWidth,tmpElementString.length());
        tmpElements[i][j]=tmpElementString;
      }
    }
    tmpWidth++;
    int tmpPadding;
    for (int i=0; i < tmpRowDim; i++) {
      for (int j=0; j < tmpColDim; j++) {
        tmpElementString=tmpElements[i][j];
        tmpPadding=tmpWidth - tmpElementString.length();
        for (int p=0; p < tmpPadding; p++) {
          aStream.print(ASCII.SP);
        }
        aStream.print(tmpElementString);
      }
      aStream.println();
    }
  }
  public static void printStackTrace(  final PrintStream aStream,  final Throwable aThrowable){
    aThrowable.printStackTrace(aStream);
  }
  static void printStackTrace(  final PrintStream aStream,  final String aMessage){
    try {
      throw new RecoverableCondition(aMessage);
    }
 catch (    final RecoverableCondition anException) {
      BasicLogger.printStackTrace(aStream,anException);
    }
  }
}

package sun.tools.tree;
import sun.tools.java.*;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import java.util.Hashtable;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class NotExpression extends UnaryExpression {
  /** 
 * Constructor
 */
  public NotExpression(  long where,  Expression right){
    super(NOT,where,Type.tBoolean,right);
  }
  /** 
 * Select the type of the expression
 */
  void selectType(  Environment env,  Context ctx,  int tm){
    right=convert(env,ctx,Type.tBoolean,right);
  }
  public void checkCondition(  Environment env,  Context ctx,  Vset vset,  Hashtable exp,  ConditionVars cvars){
    right.checkCondition(env,ctx,vset,exp,cvars);
    right=convert(env,ctx,Type.tBoolean,right);
    Vset temp=cvars.vsFalse;
    cvars.vsFalse=cvars.vsTrue;
    cvars.vsTrue=temp;
  }
  /** 
 * Evaluate
 */
  Expression eval(  boolean a){
    return new BooleanExpression(where,!a);
  }
  /** 
 * Simplify
 */
  Expression simplify(){
switch (right.op) {
case NOT:
      return ((NotExpression)right).right;
case EQ:
case NE:
case LT:
case LE:
case GT:
case GE:
    break;
default :
  return this;
}
BinaryExpression bin=(BinaryExpression)right;
if (bin.left.type.inMask(TM_REAL)) {
return this;
}
switch (right.op) {
case EQ:
return new NotEqualExpression(where,bin.left,bin.right);
case NE:
return new EqualExpression(where,bin.left,bin.right);
case LT:
return new GreaterOrEqualExpression(where,bin.left,bin.right);
case LE:
return new GreaterExpression(where,bin.left,bin.right);
case GT:
return new LessOrEqualExpression(where,bin.left,bin.right);
case GE:
return new LessExpression(where,bin.left,bin.right);
}
return this;
}
/** 
 * Code
 */
void codeBranch(Environment env,Context ctx,Assembler asm,Label lbl,boolean whenTrue){
right.codeBranch(env,ctx,asm,lbl,!whenTrue);
}
/** 
 * Instead of relying on the default code generation which uses
 * conditional branching, generate a simpler stream using XOR.
 */
public void codeValue(Environment env,Context ctx,Assembler asm){
right.codeValue(env,ctx,asm);
asm.add(where,opc_ldc,new Integer(1));
asm.add(where,opc_ixor);
}
}

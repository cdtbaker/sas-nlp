package sun.tools.tree;
import sun.tools.java.*;
import sun.tools.asm.Assembler;
import java.io.PrintStream;
import java.util.Hashtable;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class AssignExpression extends BinaryAssignExpression {
  private FieldUpdater updater=null;
  /** 
 * Constructor
 */
  public AssignExpression(  long where,  Expression left,  Expression right){
    super(ASSIGN,where,left,right);
  }
  /** 
 * Check an assignment expression
 */
  public Vset checkValue(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    if (left instanceof IdentifierExpression) {
      vset=right.checkValue(env,ctx,vset,exp);
      vset=left.checkLHS(env,ctx,vset,exp);
    }
 else {
      vset=left.checkLHS(env,ctx,vset,exp);
      vset=right.checkValue(env,ctx,vset,exp);
    }
    type=left.type;
    right=convert(env,ctx,type,right);
    updater=left.getAssigner(env,ctx);
    return vset;
  }
  /** 
 * Inline
 */
  public Expression inlineValue(  Environment env,  Context ctx){
    if (implementation != null)     return implementation.inlineValue(env,ctx);
    left=left.inlineLHS(env,ctx);
    right=right.inlineValue(env,ctx);
    if (updater != null) {
      updater=updater.inline(env,ctx);
    }
    return this;
  }
  /** 
 * Create a copy of the expression for method inlining
 */
  public Expression copyInline(  Context ctx){
    if (implementation != null)     return implementation.copyInline(ctx);
    AssignExpression e=(AssignExpression)clone();
    e.left=left.copyInline(ctx);
    e.right=right.copyInline(ctx);
    if (updater != null) {
      e.updater=updater.copyInline(ctx);
    }
    return e;
  }
  /** 
 * The cost of inlining this expression
 */
  public int costInline(  int thresh,  Environment env,  Context ctx){
    return (updater != null) ? right.costInline(thresh,env,ctx) + updater.costInline(thresh,env,ctx,false) : right.costInline(thresh,env,ctx) + left.costInline(thresh,env,ctx) + 2;
  }
  /** 
 * Code
 */
  public void codeValue(  Environment env,  Context ctx,  Assembler asm){
    if (updater == null) {
      int depth=left.codeLValue(env,ctx,asm);
      right.codeValue(env,ctx,asm);
      codeDup(env,ctx,asm,right.type.stackSize(),depth);
      left.codeStore(env,ctx,asm);
    }
 else {
      updater.startAssign(env,ctx,asm);
      right.codeValue(env,ctx,asm);
      updater.finishAssign(env,ctx,asm,true);
    }
  }
  public void code(  Environment env,  Context ctx,  Assembler asm){
    if (updater == null) {
      left.codeLValue(env,ctx,asm);
      right.codeValue(env,ctx,asm);
      left.codeStore(env,ctx,asm);
    }
 else {
      updater.startAssign(env,ctx,asm);
      right.codeValue(env,ctx,asm);
      updater.finishAssign(env,ctx,asm,false);
    }
  }
}

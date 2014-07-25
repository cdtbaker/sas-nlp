package sun.tools.tree;
import sun.tools.java.*;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import java.io.PrintStream;
import java.util.Hashtable;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class Statement extends Node {
  public static final Vset DEAD_END=Vset.DEAD_END;
  Identifier labels[]=null;
  /** 
 * Constructor
 */
  Statement(  int op,  long where){
    super(op,where);
  }
  /** 
 * An empty statement.  Its costInline is infinite.
 */
  public static final Statement empty=new Statement(STAT,0);
  /** 
 * The largest possible interesting inline cost value.
 */
  public static final int MAXINLINECOST=Integer.getInteger("javac.maxinlinecost",30).intValue();
  /** 
 * Insert a bit of code at the front of a statement.
 * Side-effect s2, if it is a CompoundStatement.
 */
  public static Statement insertStatement(  Statement s1,  Statement s2){
    if (s2 == null) {
      s2=s1;
    }
 else     if (s2 instanceof CompoundStatement) {
      ((CompoundStatement)s2).insertStatement(s1);
    }
 else {
      Statement body[]={s1,s2};
      s2=new CompoundStatement(s1.getWhere(),body);
    }
    return s2;
  }
  /** 
 * Set the label of a statement
 */
  public void setLabel(  Environment env,  Expression e){
    if (e.op == IDENT) {
      if (labels == null) {
        labels=new Identifier[1];
      }
 else {
        Identifier newLabels[]=new Identifier[labels.length + 1];
        System.arraycopy(labels,0,newLabels,1,labels.length);
        labels=newLabels;
      }
      labels[0]=((IdentifierExpression)e).id;
    }
 else {
      env.error(e.where,"invalid.label");
    }
  }
  /** 
 * Check a statement
 */
  public Vset checkMethod(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    CheckContext mctx=new CheckContext(ctx,new Statement(METHOD,0));
    ctx=mctx;
    vset=check(env,ctx,vset,exp);
    if (!ctx.field.getType().getReturnType().isType(TC_VOID)) {
      if (!vset.isDeadEnd()) {
        env.error(ctx.field.getWhere(),"return.required.at.end",ctx.field);
      }
    }
    vset=vset.join(mctx.vsBreak);
    return vset;
  }
  Vset checkDeclaration(  Environment env,  Context ctx,  Vset vset,  int mod,  Type t,  Hashtable exp){
    throw new CompilerError("checkDeclaration");
  }
  /** 
 * Make sure the labels on this statement do not duplicate the
 * labels on any enclosing statement.  Provided as a convenience
 * for subclasses.
 */
  protected void checkLabel(  Environment env,  Context ctx){
    if (labels != null) {
      loop:       for (int i=0; i < labels.length; i++) {
        for (int j=i + 1; j < labels.length; j++) {
          if (labels[i] == labels[j]) {
            env.error(where,"nested.duplicate.label",labels[i]);
            continue loop;
          }
        }
        CheckContext destCtx=(CheckContext)ctx.getLabelContext(labels[i]);
        if (destCtx != null) {
          if (destCtx.frameNumber == ctx.frameNumber) {
            env.error(where,"nested.duplicate.label",labels[i]);
          }
        }
      }
    }
  }
  Vset check(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    throw new CompilerError("check");
  }
  /** 
 * This is called in contexts where declarations are valid. 
 */
  Vset checkBlockStatement(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    return check(env,ctx,vset,exp);
  }
  Vset reach(  Environment env,  Vset vset){
    if (vset.isDeadEnd()) {
      env.error(where,"stat.not.reached");
      vset=vset.clearDeadEnd();
    }
    return vset;
  }
  /** 
 * Inline
 */
  public Statement inline(  Environment env,  Context ctx){
    return this;
  }
  /** 
 * Eliminate this statement, which is only possible if it has no label.
 */
  public Statement eliminate(  Environment env,  Statement s){
    if ((s != null) && (labels != null)) {
      Statement args[]={s};
      s=new CompoundStatement(where,args);
      s.labels=labels;
    }
    return s;
  }
  /** 
 * Code
 */
  public void code(  Environment env,  Context ctx,  Assembler asm){
    throw new CompilerError("code");
  }
  /** 
 * Generate the code to call all finally's for a break, continue, or
 * return statement.  We must call "jsr" on all the cleanup code between
 * the current context "ctx", and the destination context "stopctx".
 * If 'save' isn't null, there is also a value on the top of the stack
 */
  void codeFinally(  Environment env,  Context ctx,  Assembler asm,  Context stopctx,  Type save){
    Integer num=null;
    boolean haveCleanup=false;
    boolean haveNonLocalFinally=false;
    for (Context c=ctx; (c != null) && (c != stopctx); c=c.prev) {
      if (c.node == null)       continue;
      if (c.node.op == SYNCHRONIZED) {
        haveCleanup=true;
      }
 else       if (c.node.op == FINALLY && ((CodeContext)c).contLabel != null) {
        haveCleanup=true;
        FinallyStatement st=((FinallyStatement)(c.node));
        if (!st.finallyCanFinish) {
          haveNonLocalFinally=true;
          break;
        }
      }
    }
    if (!haveCleanup) {
      return;
    }
    if (save != null) {
      ClassDefinition def=ctx.field.getClassDefinition();
      if (!haveNonLocalFinally) {
        LocalMember lf=ctx.getLocalField(idFinallyReturnValue);
        num=new Integer(lf.number);
        asm.add(where,opc_istore + save.getTypeCodeOffset(),num);
      }
 else {
switch (ctx.field.getType().getReturnType().getTypeCode()) {
case TC_VOID:
          break;
case TC_DOUBLE:
case TC_LONG:
        asm.add(where,opc_pop2);
      break;
default :
    asm.add(where,opc_pop);
  break;
}
}
}
for (Context c=ctx; (c != null) && (c != stopctx); c=c.prev) {
if (c.node == null) continue;
if (c.node.op == SYNCHRONIZED) {
asm.add(where,opc_jsr,((CodeContext)c).contLabel);
}
 else if (c.node.op == FINALLY && ((CodeContext)c).contLabel != null) {
FinallyStatement st=((FinallyStatement)(c.node));
Label label=((CodeContext)c).contLabel;
if (st.finallyCanFinish) {
asm.add(where,opc_jsr,label);
}
 else {
asm.add(where,opc_goto,label);
break;
}
}
}
if (num != null) {
asm.add(where,opc_iload + save.getTypeCodeOffset(),num);
}
}
public boolean hasLabel(Identifier lbl){
Identifier labels[]=this.labels;
if (labels != null) {
for (int i=labels.length; --i >= 0; ) {
if (labels[i].equals(lbl)) {
return true;
}
}
}
return false;
}
/** 
 * Check if the first thing is a constructor invocation
 */
public Expression firstConstructor(){
return null;
}
/** 
 * Create a copy of the statement for method inlining
 */
public Statement copyInline(Context ctx,boolean valNeeded){
return (Statement)clone();
}
public int costInline(int thresh,Environment env,Context ctx){
return thresh;
}
/** 
 * Print
 */
void printIndent(PrintStream out,int indent){
for (int i=0; i < indent; i++) {
out.print("    ");
}
}
public void print(PrintStream out,int indent){
if (labels != null) {
for (int i=labels.length; --i >= 0; ) out.print(labels[i] + ": ");
}
}
public void print(PrintStream out){
print(out,0);
}
}

package sun.tools.javac;
import sun.tools.java.*;
import sun.tools.tree.*;
import sun.tools.asm.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.PrintStream;
/** 
 * A Source Member
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
@Deprecated public class SourceMember extends MemberDefinition implements Constants {
  /** 
 * The argument names (if it is a method)
 */
  Vector args;
  MemberDefinition abstractSource;
  /** 
 * The status of the field
 */
  int status;
  static final int PARSED=0;
  static final int CHECKING=1;
  static final int CHECKED=2;
  static final int INLINING=3;
  static final int INLINED=4;
  static final int ERROR=5;
  public Vector getArguments(){
    return args;
  }
  /** 
 * Constructor
 * @param argNames a vector of IdentifierToken
 */
  public SourceMember(  long where,  ClassDefinition clazz,  String doc,  int modifiers,  Type type,  Identifier name,  Vector argNames,  IdentifierToken exp[],  Node value){
    super(where,clazz,modifiers,type,name,exp,value);
    this.documentation=doc;
    this.args=argNames;
    if (ClassDefinition.containsDeprecated(documentation)) {
      this.modifiers|=M_DEPRECATED;
    }
  }
  void createArgumentFields(  Vector argNames){
    if (isMethod()) {
      args=new Vector();
      if (isConstructor() || !(isStatic() || isInitializer())) {
        args.addElement(((SourceClass)clazz).getThisArgument());
      }
      if (argNames != null) {
        Enumeration e=argNames.elements();
        Type argTypes[]=getType().getArgumentTypes();
        for (int i=0; i < argTypes.length; i++) {
          Object x=e.nextElement();
          if (x instanceof LocalMember) {
            args=argNames;
            return;
          }
          Identifier id;
          int mod;
          long where;
          if (x instanceof Identifier) {
            id=(Identifier)x;
            mod=0;
            where=getWhere();
          }
 else {
            IdentifierToken token=(IdentifierToken)x;
            id=token.getName();
            mod=token.getModifiers();
            where=token.getWhere();
          }
          args.addElement(new LocalMember(where,clazz,mod,argTypes[i],id));
        }
      }
    }
  }
  LocalMember outerThisArg=null;
  /** 
 * Get outer instance link, or null if none.
 */
  public LocalMember getOuterThisArg(){
    return outerThisArg;
  }
  /** 
 * Add the outer.this argument to the list of arguments for this
 * constructor.  This is called from resolveTypeStructure.  Any
 * additional uplevel arguments get added later by addUplevelArguments().
 */
  void addOuterThis(){
    UplevelReference refs=clazz.getReferences();
    while (refs != null && !refs.isClientOuterField()) {
      refs=refs.getNext();
    }
    if (refs == null) {
      return;
    }
    Type oldArgTypes[]=type.getArgumentTypes();
    Type argTypes[]=new Type[oldArgTypes.length + 1];
    LocalMember arg=refs.getLocalArgument();
    outerThisArg=arg;
    args.insertElementAt(arg,1);
    argTypes[0]=arg.getType();
    for (int i=0; i < oldArgTypes.length; i++) {
      argTypes[i + 1]=oldArgTypes[i];
    }
    type=Type.tMethod(type.getReturnType(),argTypes);
  }
  /** 
 * Prepend argument names and argument types for local variable references.
 * This information is never seen by the type-check phase,
 * but it affects code generation, which is the earliest moment
 * we have comprehensive information on uplevel references.
 * The code() methods tweaks the constructor calls, prepending
 * the proper values to the argument list.
 */
  void addUplevelArguments(){
    UplevelReference refs=clazz.getReferences();
    clazz.getReferencesFrozen();
    int count=0;
    for (UplevelReference r=refs; r != null; r=r.getNext()) {
      if (!r.isClientOuterField()) {
        count+=1;
      }
    }
    if (count == 0) {
      return;
    }
    Type oldArgTypes[]=type.getArgumentTypes();
    Type argTypes[]=new Type[oldArgTypes.length + count];
    int ins=0;
    for (UplevelReference r=refs; r != null; r=r.getNext()) {
      if (!r.isClientOuterField()) {
        LocalMember arg=r.getLocalArgument();
        args.insertElementAt(arg,1 + ins);
        argTypes[ins]=arg.getType();
        ins++;
      }
    }
    for (int i=0; i < oldArgTypes.length; i++) {
      argTypes[ins + i]=oldArgTypes[i];
    }
    type=Type.tMethod(type.getReturnType(),argTypes);
  }
  /** 
 * Constructor for an inner class.
 */
  public SourceMember(  ClassDefinition innerClass){
    super(innerClass);
  }
  /** 
 * Constructor.
 * Used only to generate an abstract copy of a method that a class
 * inherits from an interface
 */
  public SourceMember(  MemberDefinition f,  ClassDefinition c,  Environment env){
    this(f.getWhere(),c,f.getDocumentation(),f.getModifiers() | M_ABSTRACT,f.getType(),f.getName(),null,f.getExceptionIds(),null);
    this.args=f.getArguments();
    this.abstractSource=f;
    this.exp=f.getExceptions(env);
  }
  /** 
 * Get exceptions
 */
  public ClassDeclaration[] getExceptions(  Environment env){
    if ((!isMethod()) || (exp != null)) {
      return exp;
    }
    if (expIds == null) {
      exp=new ClassDeclaration[0];
      return exp;
    }
    env=((SourceClass)getClassDefinition()).setupEnv(env);
    exp=new ClassDeclaration[expIds.length];
    for (int i=0; i < exp.length; i++) {
      Identifier e=expIds[i].getName();
      Identifier rexp=getClassDefinition().resolveName(env,e);
      exp[i]=env.getClassDeclaration(rexp);
    }
    return exp;
  }
  /** 
 * Set array of name-resolved exceptions directly, e.g., for access methods.
 */
  public void setExceptions(  ClassDeclaration[] exp){
    this.exp=exp;
  }
  /** 
 * Resolve types in a field, after parsing.
 * @see ClassDefinition.resolveTypeStructure
 */
  public boolean resolved=false;
  public void resolveTypeStructure(  Environment env){
    if (tracing)     env.dtEnter("SourceMember.resolveTypeStructure: " + this);
    if (resolved) {
      if (tracing)       env.dtEvent("SourceMember.resolveTypeStructure: OK " + this);
      throw new CompilerError("multiple member type resolution");
    }
 else {
      if (tracing)       env.dtEvent("SourceMember.resolveTypeStructure: RESOLVING " + this);
      resolved=true;
    }
    super.resolveTypeStructure(env);
    if (isInnerClass()) {
      ClassDefinition nc=getInnerClass();
      if (nc instanceof SourceClass && !nc.isLocal()) {
        ((SourceClass)nc).resolveTypeStructure(env);
      }
      type=innerClass.getType();
    }
 else {
      type=env.resolveNames(getClassDefinition(),type,isSynthetic());
      getExceptions(env);
      if (isMethod()) {
        Vector argNames=args;
        args=null;
        createArgumentFields(argNames);
        if (isConstructor()) {
          addOuterThis();
        }
      }
    }
    if (tracing)     env.dtExit("SourceMember.resolveTypeStructure: " + this);
  }
  /** 
 * Get the class declaration in which the field is actually defined
 */
  public ClassDeclaration getDefiningClassDeclaration(){
    if (abstractSource == null)     return super.getDefiningClassDeclaration();
 else     return abstractSource.getDefiningClassDeclaration();
  }
  /** 
 * A source field never reports deprecation, since the compiler
 * allows access to deprecated features that are being compiled
 * in the same job.
 */
  public boolean reportDeprecated(  Environment env){
    return false;
  }
  /** 
 * Check this field.
 * <p>
 * This is the method which requests checking.
 * The real work is done by
 * <tt>Vset check(Environment, Context, Vset)</tt>.
 */
  public void check(  Environment env) throws ClassNotFound {
    if (tracing)     env.dtEnter("SourceMember.check: " + getName() + ", status = "+ status);
    if (status == PARSED) {
      if (isSynthetic() && getValue() == null) {
        status=CHECKED;
        if (tracing)         env.dtExit("SourceMember.check: BREAKING CYCLE");
        return;
      }
      if (tracing)       env.dtEvent("SourceMember.check: CHECKING CLASS");
      clazz.check(env);
      if (status == PARSED) {
        if (getClassDefinition().getError()) {
          status=ERROR;
        }
 else {
          if (tracing)           env.dtExit("SourceMember.check: CHECK FAILED");
          throw new CompilerError("check failed");
        }
      }
    }
    if (tracing)     env.dtExit("SourceMember.check: DONE " + getName() + ", status = "+ status);
  }
  /** 
 * Check a field.
 * @param vset tells which uplevel variables are definitely assigned
 * The vset is also used to track the initialization of blank finals
 * by whichever fields which are relevant to them.
 */
  public Vset check(  Environment env,  Context ctx,  Vset vset) throws ClassNotFound {
    if (tracing)     env.dtEvent("SourceMember.check: MEMBER " + getName() + ", status = "+ status);
    if (status == PARSED) {
      if (isInnerClass()) {
        ClassDefinition nc=getInnerClass();
        if (nc instanceof SourceClass && !nc.isLocal() && nc.isInsideLocal()) {
          status=CHECKING;
          vset=((SourceClass)nc).checkInsideClass(env,ctx,vset);
        }
        status=CHECKED;
        return vset;
      }
      if (env.dump()) {
        System.out.println("[check field " + getClassDeclaration().getName() + "."+ getName()+ "]");
        if (getValue() != null) {
          getValue().print(System.out);
          System.out.println();
        }
      }
      env=new Environment(env,this);
      env.resolve(where,getClassDefinition(),getType());
      if (isMethod()) {
        ClassDeclaration throwable=env.getClassDeclaration(idJavaLangThrowable);
        ClassDeclaration exp[]=getExceptions(env);
        for (int i=0; i < exp.length; i++) {
          ClassDefinition def;
          long where=getWhere();
          if (expIds != null && i < expIds.length) {
            where=IdentifierToken.getWhere(expIds[i],where);
          }
          try {
            def=exp[i].getClassDefinition(env);
            env.resolveByName(where,getClassDefinition(),def.getName());
          }
 catch (          ClassNotFound e) {
            env.error(where,"class.not.found",e.name,"throws");
            break;
          }
          def.noteUsedBy(getClassDefinition(),where,env);
          if (!getClassDefinition().canAccess(env,def.getClassDeclaration())) {
            env.error(where,"cant.access.class",def);
          }
 else           if (!def.subClassOf(env,throwable)) {
            env.error(where,"throws.not.throwable",def);
          }
        }
      }
      status=CHECKING;
      if (isMethod() && args != null) {
        int length=args.size();
        outer_loop:         for (int i=0; i < length; i++) {
          LocalMember lf=(LocalMember)(args.elementAt(i));
          Identifier name_i=lf.getName();
          for (int j=i + 1; j < length; j++) {
            LocalMember lf2=(LocalMember)(args.elementAt(j));
            Identifier name_j=lf2.getName();
            if (name_i.equals(name_j)) {
              env.error(lf2.getWhere(),"duplicate.argument",name_i);
              break outer_loop;
            }
          }
        }
      }
      if (getValue() != null) {
        ctx=new Context(ctx,this);
        if (isMethod()) {
          Statement s=(Statement)getValue();
          for (Enumeration e=args.elements(); e.hasMoreElements(); ) {
            LocalMember f=(LocalMember)e.nextElement();
            vset.addVar(ctx.declare(env,f));
          }
          if (isConstructor()) {
            vset.clearVar(ctx.getThisNumber());
            Expression supCall=s.firstConstructor();
            if ((supCall == null) && (getClassDefinition().getSuperClass() != null)) {
              supCall=getDefaultSuperCall(env);
              Statement scs=new ExpressionStatement(where,supCall);
              s=Statement.insertStatement(scs,s);
              setValue(s);
            }
          }
          ClassDeclaration exp[]=getExceptions(env);
          int htsize=(exp.length > 3) ? 17 : 7;
          Hashtable thrown=new Hashtable(htsize);
          vset=s.checkMethod(env,ctx,vset,thrown);
          ClassDeclaration ignore1=env.getClassDeclaration(idJavaLangError);
          ClassDeclaration ignore2=env.getClassDeclaration(idJavaLangRuntimeException);
          for (Enumeration e=thrown.keys(); e.hasMoreElements(); ) {
            ClassDeclaration c=(ClassDeclaration)e.nextElement();
            ClassDefinition def=c.getClassDefinition(env);
            if (def.subClassOf(env,ignore1) || def.subClassOf(env,ignore2)) {
              continue;
            }
            boolean ok=false;
            if (!isInitializer()) {
              for (int i=0; i < exp.length; i++) {
                if (def.subClassOf(env,exp[i])) {
                  ok=true;
                }
              }
            }
            if (!ok) {
              Node n=(Node)thrown.get(c);
              long where=n.getWhere();
              String errorMsg;
              if (isConstructor()) {
                if (where == getClassDefinition().getWhere()) {
                  errorMsg="def.constructor.exception";
                }
 else {
                  errorMsg="constructor.exception";
                }
              }
 else               if (isInitializer()) {
                errorMsg="initializer.exception";
              }
 else {
                errorMsg="uncaught.exception";
              }
              env.error(where,errorMsg,c.getName());
            }
          }
        }
 else {
          Hashtable thrown=new Hashtable(3);
          Expression val=(Expression)getValue();
          vset=val.checkInitializer(env,ctx,vset,getType(),thrown);
          setValue(val.convert(env,ctx,getType(),val));
          if (isStatic() && isFinal() && !clazz.isTopLevel()) {
            if (!((Expression)getValue()).isConstant()) {
              env.error(where,"static.inner.field",getName(),this);
              setValue(null);
            }
          }
          ClassDeclaration except=env.getClassDeclaration(idJavaLangThrowable);
          ClassDeclaration ignore1=env.getClassDeclaration(idJavaLangError);
          ClassDeclaration ignore2=env.getClassDeclaration(idJavaLangRuntimeException);
          for (Enumeration e=thrown.keys(); e.hasMoreElements(); ) {
            ClassDeclaration c=(ClassDeclaration)e.nextElement();
            ClassDefinition def=c.getClassDefinition(env);
            if (!def.subClassOf(env,ignore1) && !def.subClassOf(env,ignore2) && def.subClassOf(env,except)) {
              Node n=(Node)thrown.get(c);
              env.error(n.getWhere(),"initializer.exception",c.getName());
            }
          }
        }
        if (env.dump()) {
          getValue().print(System.out);
          System.out.println();
        }
      }
      status=getClassDefinition().getError() ? ERROR : CHECKED;
    }
    if (isInitializer() && vset.isDeadEnd()) {
      env.error(where,"init.no.normal.completion");
      vset=vset.clearDeadEnd();
    }
    return vset;
  }
  private Expression getDefaultSuperCall(  Environment env){
    Expression se=null;
    ClassDefinition sclass=getClassDefinition().getSuperClass().getClassDefinition();
    ClassDefinition reqc=(sclass == null) ? null : sclass.isTopLevel() ? null : sclass.getOuterClass();
    ClassDefinition thisc=getClassDefinition();
    if (reqc != null && !Context.outerLinkExists(env,reqc,thisc)) {
      se=new SuperExpression(where,new NullExpression(where));
      env.error(where,"no.default.outer.arg",reqc,getClassDefinition());
    }
    if (se == null) {
      se=new SuperExpression(where);
    }
    return new MethodExpression(where,se,idInit,new Expression[0]);
  }
  /** 
 * Inline the field
 */
  void inline(  Environment env) throws ClassNotFound {
switch (status) {
case PARSED:
      check(env);
    inline(env);
  break;
case CHECKED:
if (env.dump()) {
  System.out.println("[inline field " + getClassDeclaration().getName() + "."+ getName()+ "]");
}
status=INLINING;
env=new Environment(env,this);
if (isMethod()) {
if ((!isNative()) && (!isAbstract())) {
Statement s=(Statement)getValue();
Context ctx=new Context((Context)null,this);
for (Enumeration e=args.elements(); e.hasMoreElements(); ) {
LocalMember local=(LocalMember)e.nextElement();
ctx.declare(env,local);
}
setValue(s.inline(env,ctx));
}
}
 else if (isInnerClass()) {
ClassDefinition nc=getInnerClass();
if (nc instanceof SourceClass && !nc.isLocal() && nc.isInsideLocal()) {
status=INLINING;
((SourceClass)nc).inlineLocalClass(env);
}
status=INLINED;
break;
}
 else {
if (getValue() != null) {
Context ctx=new Context((Context)null,this);
if (!isStatic()) {
Context ctxInst=new Context(ctx,this);
LocalMember thisArg=((SourceClass)clazz).getThisArgument();
ctxInst.declare(env,thisArg);
setValue(((Expression)getValue()).inlineValue(env,ctxInst));
}
 else {
setValue(((Expression)getValue()).inlineValue(env,ctx));
}
}
}
if (env.dump()) {
System.out.println("[inlined field " + getClassDeclaration().getName() + "."+ getName()+ "]");
if (getValue() != null) {
getValue().print(System.out);
System.out.println();
}
 else {
System.out.println("<empty>");
}
}
status=INLINED;
break;
}
}
/** 
 * Get the value of the field (or null if the value can't be determined)
 */
public Node getValue(Environment env) throws ClassNotFound {
Node value=getValue();
if (value != null && status != INLINED) {
env=((SourceClass)clazz).setupEnv(env);
inline(env);
value=(status == INLINED) ? getValue() : null;
}
return value;
}
public boolean isInlineable(Environment env,boolean fromFinal) throws ClassNotFound {
if (super.isInlineable(env,fromFinal)) {
getValue(env);
return (status == INLINED) && !getClassDefinition().getError();
}
return false;
}
/** 
 * Get the initial value of the field
 */
public Object getInitialValue(){
if (isMethod() || (getValue() == null) || (!isFinal())|| (status != INLINED)) {
return null;
}
return ((Expression)getValue()).getValue();
}
/** 
 * Generate code
 */
public void code(Environment env,Assembler asm) throws ClassNotFound {
switch (status) {
case PARSED:
check(env);
code(env,asm);
return;
case CHECKED:
inline(env);
code(env,asm);
return;
case INLINED:
if (env.dump()) {
System.out.println("[code field " + getClassDeclaration().getName() + "."+ getName()+ "]");
}
if (isMethod() && (!isNative()) && (!isAbstract())) {
env=new Environment(env,this);
Context ctx=new Context((Context)null,this);
Statement s=(Statement)getValue();
for (Enumeration e=args.elements(); e.hasMoreElements(); ) {
LocalMember f=(LocalMember)e.nextElement();
ctx.declare(env,f);
}
if (s != null) {
s.code(env,ctx,asm);
}
if (getType().getReturnType().isType(TC_VOID) && !isInitializer()) {
asm.add(getWhere(),opc_return,true);
}
}
return;
}
}
public void codeInit(Environment env,Context ctx,Assembler asm) throws ClassNotFound {
if (isMethod()) {
return;
}
switch (status) {
case PARSED:
check(env);
codeInit(env,ctx,asm);
return;
case CHECKED:
inline(env);
codeInit(env,ctx,asm);
return;
case INLINED:
if (env.dump()) {
System.out.println("[code initializer  " + getClassDeclaration().getName() + "."+ getName()+ "]");
}
if (getValue() != null) {
Expression e=(Expression)getValue();
if (isStatic()) {
if (getInitialValue() == null) {
e.codeValue(env,ctx,asm);
asm.add(getWhere(),opc_putstatic,this);
}
}
 else {
asm.add(getWhere(),opc_aload,new Integer(0));
e.codeValue(env,ctx,asm);
asm.add(getWhere(),opc_putfield,this);
}
}
return;
}
}
/** 
 * Print for debugging
 */
public void print(PrintStream out){
super.print(out);
if (getValue() != null) {
getValue().print(out);
out.println();
}
}
}

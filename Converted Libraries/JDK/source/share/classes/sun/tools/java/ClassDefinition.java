package sun.tools.java;
import java.util.*;
import java.io.OutputStream;
import java.io.PrintStream;
import sun.tools.tree.Context;
import sun.tools.tree.Vset;
import sun.tools.tree.Expression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.UplevelReference;
/** 
 * This class is a Java class definition
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class ClassDefinition implements Constants {
  protected Object source;
  protected long where;
  protected int modifiers;
  protected Identifier localName;
  protected ClassDeclaration declaration;
  protected IdentifierToken superClassId;
  protected IdentifierToken interfaceIds[];
  protected ClassDeclaration superClass;
  protected ClassDeclaration interfaces[];
  protected ClassDefinition outerClass;
  protected MemberDefinition outerMember;
  protected MemberDefinition innerClassMember;
  protected MemberDefinition firstMember;
  protected MemberDefinition lastMember;
  protected boolean resolved;
  protected String documentation;
  protected boolean error;
  protected boolean nestError;
  protected UplevelReference references;
  protected boolean referencesFrozen;
  private Hashtable fieldHash=new Hashtable(31);
  private int abstr;
  private Hashtable localClasses=null;
  private final int LOCAL_CLASSES_SIZE=31;
  protected Context classContext;
  public Context getClassContext(){
    return classContext;
  }
  /** 
 * Constructor
 */
  protected ClassDefinition(  Object source,  long where,  ClassDeclaration declaration,  int modifiers,  IdentifierToken superClass,  IdentifierToken interfaces[]){
    this.source=source;
    this.where=where;
    this.declaration=declaration;
    this.modifiers=modifiers;
    this.superClassId=superClass;
    this.interfaceIds=interfaces;
  }
  /** 
 * Get the source of the class
 */
  public final Object getSource(){
    return source;
  }
  /** 
 * Check if there were any errors in this class.
 */
  public final boolean getError(){
    return error;
  }
  /** 
 * Mark this class to be erroneous.
 */
  public final void setError(){
    this.error=true;
    setNestError();
  }
  /** 
 * Check if there were any errors in our class nest.
 */
  public final boolean getNestError(){
    return nestError || ((outerClass != null) ? outerClass.getNestError() : false);
  }
  /** 
 * Mark this class, and all siblings in its class nest, to be
 * erroneous.
 */
  public final void setNestError(){
    this.nestError=true;
    if (outerClass != null) {
      outerClass.setNestError();
    }
  }
  /** 
 * Get the position in the input
 */
  public final long getWhere(){
    return where;
  }
  /** 
 * Get the class declaration
 */
  public final ClassDeclaration getClassDeclaration(){
    return declaration;
  }
  /** 
 * Get the class' modifiers
 */
  public final int getModifiers(){
    return modifiers;
  }
  public final void subModifiers(  int mod){
    modifiers&=~mod;
  }
  public final void addModifiers(  int mod){
    modifiers|=mod;
  }
  protected boolean supersCheckStarted=!(this instanceof sun.tools.javac.SourceClass);
  /** 
 * Get the class' super class
 */
  public final ClassDeclaration getSuperClass(){
    if (!supersCheckStarted)     throw new CompilerError("unresolved super");
    return superClass;
  }
  /** 
 * Get the super class, and resolve names now if necessary.
 * It is only possible to resolve names at this point if we are
 * a source class.  The provision of this method at this level
 * in the class hierarchy is dubious, but see 'getInnerClass' below.
 * All other calls to 'getSuperClass(env)' appear in 'SourceClass'.
 * NOTE: An older definition of this method has been moved to
 * 'SourceClass', where it overrides this one.
 * @see #resolveTypeStructure
 */
  public ClassDeclaration getSuperClass(  Environment env){
    return getSuperClass();
  }
  /** 
 * Get the class' interfaces
 */
  public final ClassDeclaration getInterfaces()[]{
    if (interfaces == null)     throw new CompilerError("getInterfaces");
    return interfaces;
  }
  /** 
 * Get the class' enclosing class (or null if not inner)
 */
  public final ClassDefinition getOuterClass(){
    return outerClass;
  }
  /** 
 * Set the class' enclosing class.  Must be done at most once.
 */
  protected final void setOuterClass(  ClassDefinition outerClass){
    if (this.outerClass != null)     throw new CompilerError("setOuterClass");
    this.outerClass=outerClass;
  }
  /** 
 * Set the class' enclosing current instance pointer.
 * Must be done at most once.
 */
  protected final void setOuterMember(  MemberDefinition outerMember){
    if (isStatic() || !isInnerClass())     throw new CompilerError("setOuterField");
    if (this.outerMember != null)     throw new CompilerError("setOuterField");
    this.outerMember=outerMember;
  }
  /** 
 * Tell if the class is inner.
 * This predicate also returns true for top-level nested types.
 * To test for a true inner class as seen by the programmer,
 * use <tt>!isTopLevel()</tt>.
 */
  public final boolean isInnerClass(){
    return outerClass != null;
  }
  /** 
 * Tell if the class is a member of another class.
 * This is false for package members and for block-local classes.
 */
  public final boolean isMember(){
    return outerClass != null && !isLocal();
  }
  /** 
 * Tell if the class is "top-level", which is either a package member,
 * or a static member of another top-level class.
 */
  public final boolean isTopLevel(){
    return outerClass == null || isStatic() || isInterface();
  }
  /** 
 * Tell if the class is local or inside a local class,
 * which means it cannot be mentioned outside of its file.
 */
  public final boolean isInsideLocal(){
    return isLocal() || (outerClass != null && outerClass.isInsideLocal());
  }
  /** 
 * Tell if the class is local or or anonymous class, or inside
 * such a class, which means it cannot be mentioned outside of
 * its file.
 */
  public final boolean isInsideLocalOrAnonymous(){
    return isLocal() || isAnonymous() || (outerClass != null && outerClass.isInsideLocalOrAnonymous());
  }
  /** 
 * Return a simple identifier for this class (idNull if anonymous).
 */
  public Identifier getLocalName(){
    if (localName != null) {
      return localName;
    }
    return getName().getFlatName().getName();
  }
  /** 
 * Set the local name of a class.  Must be a local class.
 */
  public void setLocalName(  Identifier name){
    if (isLocal()) {
      localName=name;
    }
  }
  /** 
 * If inner, get the field for this class in the enclosing class
 */
  public final MemberDefinition getInnerClassMember(){
    if (outerClass == null)     return null;
    if (innerClassMember == null) {
      Identifier nm=getName().getFlatName().getName();
      for (MemberDefinition field=outerClass.getFirstMatch(nm); field != null; field=field.getNextMatch()) {
        if (field.isInnerClass()) {
          innerClassMember=field;
          break;
        }
      }
      if (innerClassMember == null)       throw new CompilerError("getInnerClassField");
    }
    return innerClassMember;
  }
  /** 
 * If inner, return an innermost uplevel self pointer, if any exists.
 * Otherwise, return null.
 */
  public final MemberDefinition findOuterMember(){
    return outerMember;
  }
  /** 
 * See if this is a (nested) static class.
 */
  public final boolean isStatic(){
    return (modifiers & ACC_STATIC) != 0;
  }
  /** 
 * Get the class' top-level enclosing class
 */
  public final ClassDefinition getTopClass(){
    ClassDefinition p, q;
    for (p=this; (q=p.outerClass) != null; p=q)     ;
    return p;
  }
  /** 
 * Get the class' first field or first match
 */
  public final MemberDefinition getFirstMember(){
    return firstMember;
  }
  public final MemberDefinition getFirstMatch(  Identifier name){
    return (MemberDefinition)fieldHash.get(name);
  }
  /** 
 * Get the class' name
 */
  public final Identifier getName(){
    return declaration.getName();
  }
  /** 
 * Get the class' type
 */
  public final Type getType(){
    return declaration.getType();
  }
  /** 
 * Get the class' documentation
 */
  public String getDocumentation(){
    return documentation;
  }
  /** 
 * Return true if the given documentation string contains a deprecation
 * paragraph.  This is true if the string contains the tag @deprecated
 * is the first word in a line.
 */
  public static boolean containsDeprecated(  String documentation){
    if (documentation == null) {
      return false;
    }
    doScan:     for (int scan=0; (scan=documentation.indexOf(paraDeprecated,scan)) >= 0; scan+=paraDeprecated.length()) {
      for (int beg=scan - 1; beg >= 0; beg--) {
        char ch=documentation.charAt(beg);
        if (ch == '\n' || ch == '\r') {
          break;
        }
        if (!Character.isSpace(ch)) {
          continue doScan;
        }
      }
      int end=scan + paraDeprecated.length();
      if (end < documentation.length()) {
        char ch=documentation.charAt(end);
        if (!(ch == '\n' || ch == '\r') && !Character.isSpace(ch)) {
          continue doScan;
        }
      }
      return true;
    }
    return false;
  }
  public final boolean inSamePackage(  ClassDeclaration c){
    return inSamePackage(c.getName().getQualifier());
  }
  public final boolean inSamePackage(  ClassDefinition c){
    return inSamePackage(c.getName().getQualifier());
  }
  public final boolean inSamePackage(  Identifier packageName){
    return (getName().getQualifier().equals(packageName));
  }
  /** 
 * Checks
 */
  public final boolean isInterface(){
    return (getModifiers() & M_INTERFACE) != 0;
  }
  public final boolean isClass(){
    return (getModifiers() & M_INTERFACE) == 0;
  }
  public final boolean isPublic(){
    return (getModifiers() & M_PUBLIC) != 0;
  }
  public final boolean isPrivate(){
    return (getModifiers() & M_PRIVATE) != 0;
  }
  public final boolean isProtected(){
    return (getModifiers() & M_PROTECTED) != 0;
  }
  public final boolean isPackagePrivate(){
    return (modifiers & (M_PUBLIC | M_PRIVATE | M_PROTECTED)) == 0;
  }
  public final boolean isFinal(){
    return (getModifiers() & M_FINAL) != 0;
  }
  public final boolean isAbstract(){
    return (getModifiers() & M_ABSTRACT) != 0;
  }
  public final boolean isSynthetic(){
    return (getModifiers() & M_SYNTHETIC) != 0;
  }
  public final boolean isDeprecated(){
    return (getModifiers() & M_DEPRECATED) != 0;
  }
  public final boolean isAnonymous(){
    return (getModifiers() & M_ANONYMOUS) != 0;
  }
  public final boolean isLocal(){
    return (getModifiers() & M_LOCAL) != 0;
  }
  public final boolean hasConstructor(){
    return getFirstMatch(idInit) != null;
  }
  /** 
 * Check to see if a class must be abstract.  This method replaces
 * isAbstract(env)
 */
  public final boolean mustBeAbstract(  Environment env){
    if (isAbstract()) {
      return true;
    }
    collectInheritedMethods(env);
    Iterator methods=getMethods();
    while (methods.hasNext()) {
      MemberDefinition method=(MemberDefinition)methods.next();
      if (method.isAbstract()) {
        return true;
      }
    }
    return getPermanentlyAbstractMethods().hasNext();
  }
  /** 
 * Check if this is a super class of another class
 */
  public boolean superClassOf(  Environment env,  ClassDeclaration otherClass) throws ClassNotFound {
    while (otherClass != null) {
      if (getClassDeclaration().equals(otherClass)) {
        return true;
      }
      otherClass=otherClass.getClassDefinition(env).getSuperClass();
    }
    return false;
  }
  /** 
 * Check if this is an enclosing class of another class
 */
  public boolean enclosingClassOf(  ClassDefinition otherClass){
    while ((otherClass=otherClass.getOuterClass()) != null) {
      if (this == otherClass) {
        return true;
      }
    }
    return false;
  }
  /** 
 * Check if this is a sub class of another class
 */
  public boolean subClassOf(  Environment env,  ClassDeclaration otherClass) throws ClassNotFound {
    ClassDeclaration c=getClassDeclaration();
    while (c != null) {
      if (c.equals(otherClass)) {
        return true;
      }
      c=c.getClassDefinition(env).getSuperClass();
    }
    return false;
  }
  /** 
 * Check if this class is implemented by another class
 */
  public boolean implementedBy(  Environment env,  ClassDeclaration c) throws ClassNotFound {
    for (; c != null; c=c.getClassDefinition(env).getSuperClass()) {
      if (getClassDeclaration().equals(c)) {
        return true;
      }
      ClassDeclaration intf[]=c.getClassDefinition(env).getInterfaces();
      for (int i=0; i < intf.length; i++) {
        if (implementedBy(env,intf[i])) {
          return true;
        }
      }
    }
    return false;
  }
  /** 
 * Check to see if a class which implements interface `this' could
 * possibly implement the interface `intDef'.  Note that the only
 * way that this can fail is if `this' and `intDef' have methods
 * which are of the same signature and different return types.  This
 * method is used by Environment.explicitCast() to determine if a
 * cast between two interfaces is legal.
 * This method should only be called on a class after it has been
 * basicCheck()'ed.
 */
  public boolean couldImplement(  ClassDefinition intDef){
    if (!doInheritanceChecks) {
      throw new CompilerError("couldImplement: no checks");
    }
    if (!isInterface() || !intDef.isInterface()) {
      throw new CompilerError("couldImplement: not interface");
    }
    if (allMethods == null) {
      throw new CompilerError("couldImplement: called early");
    }
    Iterator otherMethods=intDef.getMethods();
    while (otherMethods.hasNext()) {
      MemberDefinition method=(MemberDefinition)otherMethods.next();
      Identifier name=method.getName();
      Type type=method.getType();
      MemberDefinition myMethod=allMethods.lookupSig(name,type);
      if (myMethod != null) {
        if (!myMethod.sameReturnType(method)) {
          return false;
        }
      }
    }
    return true;
  }
  /** 
 * Check if another class can be accessed from the 'extends' or 'implements'
 * clause of this class.
 */
  public boolean extendsCanAccess(  Environment env,  ClassDeclaration c) throws ClassNotFound {
    if (outerClass != null) {
      return outerClass.canAccess(env,c);
    }
    ClassDefinition cdef=c.getClassDefinition(env);
    if (cdef.isLocal()) {
      throw new CompilerError("top local");
    }
    if (cdef.isInnerClass()) {
      MemberDefinition f=cdef.getInnerClassMember();
      if (f.isPublic()) {
        return true;
      }
      if (f.isPrivate()) {
        return getClassDeclaration().equals(f.getTopClass().getClassDeclaration());
      }
      return getName().getQualifier().equals(f.getClassDeclaration().getName().getQualifier());
    }
    if (cdef.isPublic()) {
      return true;
    }
    return getName().getQualifier().equals(c.getName().getQualifier());
  }
  /** 
 * Check if another class can be accessed from within the body of this class.
 */
  public boolean canAccess(  Environment env,  ClassDeclaration c) throws ClassNotFound {
    ClassDefinition cdef=c.getClassDefinition(env);
    if (cdef.isLocal()) {
      return true;
    }
    if (cdef.isInnerClass()) {
      return canAccess(env,cdef.getInnerClassMember());
    }
    if (cdef.isPublic()) {
      return true;
    }
    return getName().getQualifier().equals(c.getName().getQualifier());
  }
  /** 
 * Check if a field can be accessed from a class
 */
  public boolean canAccess(  Environment env,  MemberDefinition f) throws ClassNotFound {
    if (f.isPublic()) {
      return true;
    }
    if (f.isProtected() && subClassOf(env,f.getClassDeclaration())) {
      return true;
    }
    if (f.isPrivate()) {
      return getTopClass().getClassDeclaration().equals(f.getTopClass().getClassDeclaration());
    }
    return getName().getQualifier().equals(f.getClassDeclaration().getName().getQualifier());
  }
  /** 
 * Check if a class is entitled to inline access to a class from
 * another class.
 */
  public boolean permitInlinedAccess(  Environment env,  ClassDeclaration c) throws ClassNotFound {
    return (env.opt() && c.equals(declaration)) || (env.opt_interclass() && canAccess(env,c));
  }
  /** 
 * Check if a class is entitled to inline access to a method from
 * another class.
 */
  public boolean permitInlinedAccess(  Environment env,  MemberDefinition f) throws ClassNotFound {
    return (env.opt() && (f.clazz.getClassDeclaration().equals(declaration))) || (env.opt_interclass() && canAccess(env,f));
  }
  /** 
 * We know the the field is marked protected (and not public) and that
 * the field is visible (as per canAccess).  Can we access the field as
 * <accessor>.<field>, where <accessor> has the type <accessorType>?
 * Protected fields can only be accessed when the accessorType is a
 * subclass of the current class
 */
  public boolean protectedAccess(  Environment env,  MemberDefinition f,  Type accessorType) throws ClassNotFound {
    return f.isStatic() || (accessorType.isType(TC_ARRAY) && (f.getName() == idClone) && (f.getType().getArgumentTypes().length == 0)) || (accessorType.isType(TC_CLASS) && env.getClassDefinition(accessorType.getClassName()).subClassOf(env,getClassDeclaration()))|| (getName().getQualifier().equals(f.getClassDeclaration().getName().getQualifier()));
  }
  /** 
 * Find or create an access method for a private member,
 * or return null if this is not possible.
 */
  public MemberDefinition getAccessMember(  Environment env,  Context ctx,  MemberDefinition field,  boolean isSuper){
    throw new CompilerError("binary getAccessMember");
  }
  /** 
 * Find or create an update method for a private member,
 * or return null if this is not possible.
 */
  public MemberDefinition getUpdateMember(  Environment env,  Context ctx,  MemberDefinition field,  boolean isSuper){
    throw new CompilerError("binary getUpdateMember");
  }
  /** 
 * Get a field from this class.  Report ambiguous fields.
 * If no accessible field is found, this method may return an
 * inaccessible field to allow a useful error message.
 * getVariable now takes the source class `source' as an argument.
 * This allows getVariable to check whether a field is inaccessible
 * before it signals that a field is ambiguous.  The compiler used to
 * signal an ambiguity even when one of the fields involved was not
 * accessible.  (bug 4053724)
 */
  public MemberDefinition getVariable(  Environment env,  Identifier nm,  ClassDefinition source) throws AmbiguousMember, ClassNotFound {
    return getVariable0(env,nm,source,true,true);
  }
  private MemberDefinition getVariable0(  Environment env,  Identifier nm,  ClassDefinition source,  boolean showPrivate,  boolean showPackage) throws AmbiguousMember, ClassNotFound {
    for (MemberDefinition member=getFirstMatch(nm); member != null; member=member.getNextMatch()) {
      if (member.isVariable()) {
        if ((showPrivate || !member.isPrivate()) && (showPackage || !member.isPackagePrivate())) {
          return member;
        }
 else {
          return null;
        }
      }
    }
    ClassDeclaration sup=getSuperClass();
    MemberDefinition field=null;
    if (sup != null) {
      field=sup.getClassDefinition(env).getVariable0(env,nm,source,false,showPackage && inSamePackage(sup));
    }
    for (int i=0; i < interfaces.length; i++) {
      MemberDefinition field2=interfaces[i].getClassDefinition(env).getVariable0(env,nm,source,true,true);
      if (field2 != null) {
        if (field != null && source.canAccess(env,field) && field2 != field) {
          throw new AmbiguousMember(field2,field);
        }
        field=field2;
      }
    }
    return field;
  }
  /** 
 * Tells whether to report a deprecation error for this class.
 */
  public boolean reportDeprecated(  Environment env){
    return (isDeprecated() || (outerClass != null && outerClass.reportDeprecated(env)));
  }
  /** 
 * Note that this class is being used somehow by <tt>ref</tt>.
 * Report deprecation errors, etc.
 */
  public void noteUsedBy(  ClassDefinition ref,  long where,  Environment env){
    if (reportDeprecated(env)) {
      env.error(where,"warn.class.is.deprecated",this);
    }
  }
  /** 
 * Get an inner class.
 * Look in supers but not outers.
 * (This is used directly to resolve expressions like "site.K", and
 * inside a loop to resolve lone names like "K" or the "K" in "K.L".)
 * Called from 'Context' and 'FieldExpression' as well as this class.
 * @see FieldExpression.checkCommon
 * @see resolveName
 */
  public MemberDefinition getInnerClass(  Environment env,  Identifier nm) throws ClassNotFound {
    for (MemberDefinition field=getFirstMatch(nm); field != null; field=field.getNextMatch()) {
      if (field.isInnerClass()) {
        if (field.getInnerClass().isLocal()) {
          continue;
        }
        return field;
      }
    }
    ClassDeclaration sup=getSuperClass(env);
    if (sup != null)     return sup.getClassDefinition(env).getInnerClass(env,nm);
    return null;
  }
  /** 
 * Lookup a method.  This code implements the method lookup
 * mechanism specified in JLS 15.11.2.
 * This mechanism cannot be used to lookup synthetic methods.
 */
  private MemberDefinition matchMethod(  Environment env,  ClassDefinition accessor,  Identifier methodName,  Type[] argumentTypes,  boolean isAnonConstCall,  Identifier accessPackage) throws AmbiguousMember, ClassNotFound {
    if (allMethods == null || !allMethods.isFrozen()) {
      throw new CompilerError("matchMethod called early");
    }
    MemberDefinition tentative=null;
    List candidateList=null;
    Iterator methods=allMethods.lookupName(methodName);
    while (methods.hasNext()) {
      MemberDefinition method=(MemberDefinition)methods.next();
      if (!env.isApplicable(method,argumentTypes)) {
        continue;
      }
      if (accessor != null) {
        if (!accessor.canAccess(env,method)) {
          continue;
        }
      }
 else       if (isAnonConstCall) {
        if (method.isPrivate() || (method.isPackagePrivate() && accessPackage != null && !inSamePackage(accessPackage))) {
          continue;
        }
      }
 else {
      }
      if (tentative == null) {
        tentative=method;
      }
 else {
        if (env.isMoreSpecific(method,tentative)) {
          tentative=method;
        }
 else {
          if (!env.isMoreSpecific(tentative,method)) {
            if (candidateList == null) {
              candidateList=new ArrayList();
            }
            candidateList.add(method);
          }
        }
      }
    }
    if (tentative != null && candidateList != null) {
      Iterator candidates=candidateList.iterator();
      while (candidates.hasNext()) {
        MemberDefinition method=(MemberDefinition)candidates.next();
        if (!env.isMoreSpecific(tentative,method)) {
          throw new AmbiguousMember(tentative,method);
        }
      }
    }
    return tentative;
  }
  /** 
 * Lookup a method.  This code implements the method lookup
 * mechanism specified in JLS 15.11.2.
 * This mechanism cannot be used to lookup synthetic methods.
 */
  public MemberDefinition matchMethod(  Environment env,  ClassDefinition accessor,  Identifier methodName,  Type[] argumentTypes) throws AmbiguousMember, ClassNotFound {
    return matchMethod(env,accessor,methodName,argumentTypes,false,null);
  }
  /** 
 * Lookup a method.  This code implements the method lookup
 * mechanism specified in JLS 15.11.2.
 * This mechanism cannot be used to lookup synthetic methods.
 */
  public MemberDefinition matchMethod(  Environment env,  ClassDefinition accessor,  Identifier methodName) throws AmbiguousMember, ClassNotFound {
    return matchMethod(env,accessor,methodName,Type.noArgs,false,null);
  }
  /** 
 * A version of matchMethod to be used only for constructors
 * when we cannot pass in a sourceClass argument.  We just assert
 * our package name.
 * This is used only for anonymous classes, where we have to look up
 * a (potentially) protected constructor with no valid sourceClass
 * parameter available.
 */
  public MemberDefinition matchAnonConstructor(  Environment env,  Identifier accessPackage,  Type argumentTypes[]) throws AmbiguousMember, ClassNotFound {
    return matchMethod(env,null,idInit,argumentTypes,true,accessPackage);
  }
  /** 
 * Find a method, ie: exact match in this class or any of the super
 * classes.
 * Only called by javadoc.  For now I am holding off rewriting this
 * code to rely on collectInheritedMethods(), as that code has
 * not gotten along with javadoc in the past.
 */
  public MemberDefinition findMethod(  Environment env,  Identifier nm,  Type t) throws ClassNotFound {
    MemberDefinition f;
    for (f=getFirstMatch(nm); f != null; f=f.getNextMatch()) {
      if (f.getType().equalArguments(t)) {
        return f;
      }
    }
    if (nm.equals(idInit)) {
      return null;
    }
    ClassDeclaration sup=getSuperClass();
    if (sup == null)     return null;
    return sup.getClassDefinition(env).findMethod(env,nm,t);
  }
  protected void basicCheck(  Environment env) throws ClassNotFound {
    if (outerClass != null)     outerClass.basicCheck(env);
  }
  /** 
 * Check this class.
 */
  public void check(  Environment env) throws ClassNotFound {
  }
  public Vset checkLocalClass(  Environment env,  Context ctx,  Vset vset,  ClassDefinition sup,  Expression args[],  Type argTypes[]) throws ClassNotFound {
    throw new CompilerError("checkLocalClass");
  }
  MethodSet allMethods=null;
  private List permanentlyAbstractMethods=new ArrayList();
  /** 
 * This method returns an Iterator of all abstract methods
 * in our superclasses which we are unable to implement.
 */
  protected Iterator getPermanentlyAbstractMethods(){
    if (allMethods == null) {
      throw new CompilerError("isPermanentlyAbstract() called early");
    }
    return permanentlyAbstractMethods.iterator();
  }
  /** 
 * A flag used by turnOffInheritanceChecks() to indicate if
 * inheritance checks are on or off.
 */
  protected static boolean doInheritanceChecks=true;
  /** 
 * This is a workaround to allow javadoc to turn off certain
 * inheritance/override checks which interfere with javadoc
 * badly.  In the future it might be good to eliminate the
 * shared sources of javadoc and javac to avoid the need for this
 * sort of workaround.
 */
  public static void turnOffInheritanceChecks(){
    doInheritanceChecks=false;
  }
  /** 
 * Add all of the methods declared in or above `parent' to
 * `allMethods', the set of methods in the current class.
 * `myMethods' is the set of all methods declared in this
 * class, and `mirandaMethods' is a repository for Miranda methods.
 * If mirandaMethods is null, no mirandaMethods will be
 * generated.
 * For a definition of Miranda methods, see the comment above the
 * method addMirandaMethods() which occurs later in this file.
 */
  private void collectOneClass(  Environment env,  ClassDeclaration parent,  MethodSet myMethods,  MethodSet allMethods,  MethodSet mirandaMethods){
    try {
      ClassDefinition pClass=parent.getClassDefinition(env);
      Iterator methods=pClass.getMethods(env);
      while (methods.hasNext()) {
        MemberDefinition method=(MemberDefinition)methods.next();
        if (method.isPrivate() || method.isConstructor() || (pClass.isInterface() && !method.isAbstract())) {
          continue;
        }
        Identifier name=method.getName();
        Type type=method.getType();
        MemberDefinition override=myMethods.lookupSig(name,type);
        if (method.isPackagePrivate() && !inSamePackage(method.getClassDeclaration())) {
          if (override != null && this instanceof sun.tools.javac.SourceClass) {
            env.error(method.getWhere(),"warn.no.override.access",override,override.getClassDeclaration(),method.getClassDeclaration());
          }
          if (method.isAbstract()) {
            permanentlyAbstractMethods.add(method);
          }
          continue;
        }
        if (override != null) {
          override.checkOverride(env,method);
        }
 else {
          MemberDefinition formerMethod=allMethods.lookupSig(name,type);
          if (formerMethod == null) {
            if (mirandaMethods != null && pClass.isInterface() && !isInterface()) {
              method=new sun.tools.javac.SourceMember(method,this,env);
              mirandaMethods.add(method);
            }
            allMethods.add(method);
          }
 else           if (isInterface() && !formerMethod.isAbstract() && method.isAbstract()) {
            allMethods.replace(method);
          }
 else {
            if (!formerMethod.checkMeet(env,method,this.getClassDeclaration())) {
              continue;
            }
            if (formerMethod.couldOverride(env,method)) {
              continue;
            }
            if (method.couldOverride(env,formerMethod)) {
              if (mirandaMethods != null && pClass.isInterface() && !isInterface()) {
                method=new sun.tools.javac.SourceMember(method,this,env);
                mirandaMethods.replace(method);
              }
              allMethods.replace(method);
              continue;
            }
            env.error(this.where,"nontrivial.meet",method,formerMethod.getClassDefinition(),method.getClassDeclaration());
          }
        }
      }
    }
 catch (    ClassNotFound ee) {
      env.error(getWhere(),"class.not.found",ee.name,this);
    }
  }
  /** 
 * <p>Collect all methods defined in this class or inherited from
 * any of our superclasses or interfaces.  Look for any
 * incompatible definitions.
 * <p>This function is also responsible for collecting the
 * <em>Miranda</em> methods for a class.  For a definition of
 * Miranda methods, see the comment in addMirandaMethods()
 * below.
 */
  protected void collectInheritedMethods(  Environment env){
    MethodSet myMethods;
    MethodSet mirandaMethods;
    if (allMethods != null) {
      if (allMethods.isFrozen()) {
        return;
      }
 else {
        throw new CompilerError("collectInheritedMethods()");
      }
    }
    myMethods=new MethodSet();
    allMethods=new MethodSet();
    if (env.version12()) {
      mirandaMethods=null;
    }
 else {
      mirandaMethods=new MethodSet();
    }
    for (MemberDefinition member=getFirstMember(); member != null; member=member.nextMember) {
      if (member.isMethod() && !member.isInitializer()) {
        methodSetAdd(env,myMethods,member);
        methodSetAdd(env,allMethods,member);
      }
    }
    ClassDeclaration scDecl=getSuperClass(env);
    if (scDecl != null) {
      collectOneClass(env,scDecl,myMethods,allMethods,mirandaMethods);
      ClassDefinition sc=scDecl.getClassDefinition();
      Iterator supIter=sc.getPermanentlyAbstractMethods();
      while (supIter.hasNext()) {
        permanentlyAbstractMethods.add(supIter.next());
      }
    }
    for (int i=0; i < interfaces.length; i++) {
      collectOneClass(env,interfaces[i],myMethods,allMethods,mirandaMethods);
    }
    allMethods.freeze();
    if (mirandaMethods != null && mirandaMethods.size() > 0) {
      addMirandaMethods(env,mirandaMethods.iterator());
    }
  }
  private static void methodSetAdd(  Environment env,  MethodSet methodSet,  MemberDefinition newMethod){
    MemberDefinition oldMethod=methodSet.lookupSig(newMethod.getName(),newMethod.getType());
    if (oldMethod != null) {
      Type oldReturnType=oldMethod.getType().getReturnType();
      Type newReturnType=newMethod.getType().getReturnType();
      try {
        if (env.isMoreSpecific(newReturnType,oldReturnType)) {
          methodSet.replace(newMethod);
        }
      }
 catch (      ClassNotFound ignore) {
      }
    }
 else {
      methodSet.add(newMethod);
    }
  }
  /** 
 * Get an Iterator of all methods which could be accessed in an
 * instance of this class.
 */
  public Iterator getMethods(  Environment env){
    if (allMethods == null) {
      collectInheritedMethods(env);
    }
    return getMethods();
  }
  /** 
 * Get an Iterator of all methods which could be accessed in an
 * instance of this class.  Throw a compiler error if we haven't
 * generated this information yet.
 */
  public Iterator getMethods(){
    if (allMethods == null) {
      throw new CompilerError("getMethods: too early");
    }
    return allMethods.iterator();
  }
  /** 
 * Add a list of methods to this class as miranda methods.  This
 * gets overridden with a meaningful implementation in SourceClass.
 * BinaryClass should not need to do anything -- it should already
 * have its miranda methods and, if it doesn't, then that doesn't
 * affect our compilation.
 */
  protected void addMirandaMethods(  Environment env,  Iterator mirandas){
  }
  public void inlineLocalClass(  Environment env){
  }
  /** 
 * We create a stub for this.  Source classes do more work.
 * Some calls from 'SourceClass.checkSupers' execute this method.
 * @see sun.tools.javac.SourceClass#resolveTypeStructure
 */
  public void resolveTypeStructure(  Environment env){
  }
  /** 
 * Look up an inner class name, from somewhere inside this class.
 * Since supers and outers are in scope, search them too.
 * <p>
 * If no inner class is found, env.resolveName() is then called,
 * to interpret the ambient package and import directives.
 * <p>
 * This routine operates on a "best-efforts" basis.  If
 * at some point a class is not found, the partially-resolved
 * identifier is returned.  Eventually, someone else has to
 * try to get the ClassDefinition and diagnose the ClassNotFound.
 * <p>
 * resolveName() looks at surrounding scopes, and hence
 * pulling in both inherited and uplevel types.  By contrast,
 * resolveInnerClass() is intended only for interpreting
 * explicitly qualified names, and so look only at inherited
 * types.  Also, resolveName() looks for package prefixes,
 * which appear similar to "very uplevel" outer classes.
 * <p>
 * A similar (but more complex) name-lookup process happens
 * when field and identifier expressions denoting qualified names
 * are type-checked.  The added complexity comes from the fact
 * that variables may occur in such names, and take precedence
 * over class and package names.
 * <p>
 * In the expression type-checker, resolveInnerClass() is paralleled
 * by code in FieldExpression.checkAmbigName(), which also calls
 * ClassDefinition.getInnerClass() to interpret names of the form
 * "OuterClass.Inner" (and also outerObject.Inner).  The checking
 * of an identifier expression that fails to be a variable is referred
 * directly to resolveName().
 */
  public Identifier resolveName(  Environment env,  Identifier name){
    if (tracing)     env.dtEvent("ClassDefinition.resolveName: " + name);
    if (name.isQualified()) {
      Identifier rhead=resolveName(env,name.getHead());
      if (rhead.hasAmbigPrefix()) {
        return rhead;
      }
      if (!env.classExists(rhead)) {
        return env.resolvePackageQualifiedName(name);
      }
      try {
        return env.getClassDefinition(rhead).resolveInnerClass(env,name.getTail());
      }
 catch (      ClassNotFound ee) {
        return Identifier.lookupInner(rhead,name.getTail());
      }
    }
    int ls=-2;
    LocalMember lf=null;
    if (classContext != null) {
      lf=classContext.getLocalClass(name);
      if (lf != null) {
        ls=lf.getScopeNumber();
      }
    }
    for (ClassDefinition c=this; c != null; c=c.outerClass) {
      try {
        MemberDefinition f=c.getInnerClass(env,name);
        if (f != null && (lf == null || classContext.getScopeNumber(c) > ls)) {
          return f.getInnerClass().getName();
        }
      }
 catch (      ClassNotFound ee) {
      }
    }
    if (lf != null) {
      return lf.getInnerClass().getName();
    }
    return env.resolveName(name);
  }
  /** 
 * Interpret a qualified class name, which may have further subcomponents..
 * Follow inheritance links, as in:
 * class C { class N { } }  class D extends C { }  ... new D.N() ...
 * Ignore outer scopes and packages.
 * @see resolveName
 */
  public Identifier resolveInnerClass(  Environment env,  Identifier nm){
    if (nm.isInner())     throw new CompilerError("inner");
    if (nm.isQualified()) {
      Identifier rhead=resolveInnerClass(env,nm.getHead());
      try {
        return env.getClassDefinition(rhead).resolveInnerClass(env,nm.getTail());
      }
 catch (      ClassNotFound ee) {
        return Identifier.lookupInner(rhead,nm.getTail());
      }
    }
 else {
      try {
        MemberDefinition f=getInnerClass(env,nm);
        if (f != null) {
          return f.getInnerClass().getName();
        }
      }
 catch (      ClassNotFound ee) {
      }
      return Identifier.lookupInner(this.getName(),nm);
    }
  }
  /** 
 * While resolving import directives, the question has arisen:
 * does a given inner class exist?  If the top-level class exists,
 * we ask it about an inner class via this method.
 * This method looks only at the literal name of the class,
 * and does not attempt to follow inheritance links.
 * This is necessary, since at the time imports are being
 * processed, inheritance links have not been resolved yet.
 * (Thus, an import directive must always spell a class
 * name exactly.)
 */
  public boolean innerClassExists(  Identifier nm){
    for (MemberDefinition field=getFirstMatch(nm.getHead()); field != null; field=field.getNextMatch()) {
      if (field.isInnerClass()) {
        if (field.getInnerClass().isLocal()) {
          continue;
        }
        return !nm.isQualified() || field.getInnerClass().innerClassExists(nm.getTail());
      }
    }
    return false;
  }
  /** 
 * Find any method with a given name.
 */
  public MemberDefinition findAnyMethod(  Environment env,  Identifier nm) throws ClassNotFound {
    MemberDefinition f;
    for (f=getFirstMatch(nm); f != null; f=f.getNextMatch()) {
      if (f.isMethod()) {
        return f;
      }
    }
    ClassDeclaration sup=getSuperClass();
    if (sup == null)     return null;
    return sup.getClassDefinition(env).findAnyMethod(env,nm);
  }
  /** 
 * Given the fact that this class has no method "nm" matching "argTypes",
 * find out if the mismatch can be blamed on a particular actual argument
 * which disagrees with all of the overloadings.
 * If so, return the code (i<<2)+(castOK<<1)+ambig, where
 * "i" is the number of the offending argument, and
 * "castOK" is 1 if a cast could fix the problem.
 * The target type for the argument is returned in margTypeResult[0].
 * If not all methods agree on this type, "ambig" is 1.
 * If there is more than one method, the choice of target type is
 * arbitrary.<p>
 * Return -1 if every argument is acceptable to at least one method.
 * Return -2 if there are no methods of the required arity.
 * The value "start" gives the index of the first argument to begin
 * checking.
 */
  public int diagnoseMismatch(  Environment env,  Identifier nm,  Type argTypes[],  int start,  Type margTypeResult[]) throws ClassNotFound {
    int haveMatch[]=new int[argTypes.length];
    Type margType[]=new Type[argTypes.length];
    if (!diagnoseMismatch(env,nm,argTypes,start,haveMatch,margType))     return -2;
    for (int i=start; i < argTypes.length; i++) {
      if (haveMatch[i] < 4) {
        margTypeResult[0]=margType[i];
        return (i << 2) | haveMatch[i];
      }
    }
    return -1;
  }
  private boolean diagnoseMismatch(  Environment env,  Identifier nm,  Type argTypes[],  int start,  int haveMatch[],  Type margType[]) throws ClassNotFound {
    boolean haveOne=false;
    MemberDefinition f;
    for (f=getFirstMatch(nm); f != null; f=f.getNextMatch()) {
      if (!f.isMethod()) {
        continue;
      }
      Type fArgTypes[]=f.getType().getArgumentTypes();
      if (fArgTypes.length == argTypes.length) {
        haveOne=true;
        for (int i=start; i < argTypes.length; i++) {
          Type at=argTypes[i];
          Type ft=fArgTypes[i];
          if (env.implicitCast(at,ft)) {
            haveMatch[i]=4;
            continue;
          }
 else           if (haveMatch[i] <= 2 && env.explicitCast(at,ft)) {
            if (haveMatch[i] < 2)             margType[i]=null;
            haveMatch[i]=2;
          }
 else           if (haveMatch[i] > 0) {
            continue;
          }
          if (margType[i] == null)           margType[i]=ft;
 else           if (margType[i] != ft)           haveMatch[i]|=1;
        }
      }
    }
    if (nm.equals(idInit)) {
      return haveOne;
    }
    ClassDeclaration sup=getSuperClass();
    if (sup != null) {
      if (sup.getClassDefinition(env).diagnoseMismatch(env,nm,argTypes,start,haveMatch,margType))       haveOne=true;
    }
    return haveOne;
  }
  /** 
 * Add a field (no checks)
 */
  public void addMember(  MemberDefinition field){
    if (firstMember == null) {
      firstMember=lastMember=field;
    }
 else     if (field.isSynthetic() && field.isFinal() && field.isVariable()) {
      field.nextMember=firstMember;
      firstMember=field;
      field.nextMatch=(MemberDefinition)fieldHash.get(field.name);
    }
 else {
      lastMember.nextMember=field;
      lastMember=field;
      field.nextMatch=(MemberDefinition)fieldHash.get(field.name);
    }
    fieldHash.put(field.name,field);
  }
  /** 
 * Add a field (subclasses make checks)
 */
  public void addMember(  Environment env,  MemberDefinition field){
    addMember(field);
    if (resolved) {
      field.resolveTypeStructure(env);
    }
  }
  /** 
 * Find or create an uplevel reference for the given target.
 */
  public UplevelReference getReference(  LocalMember target){
    for (UplevelReference r=references; r != null; r=r.getNext()) {
      if (r.getTarget() == target) {
        return r;
      }
    }
    return addReference(target);
  }
  protected UplevelReference addReference(  LocalMember target){
    if (target.getClassDefinition() == this) {
      throw new CompilerError("addReference " + target);
    }
    referencesMustNotBeFrozen();
    UplevelReference r=new UplevelReference(this,target);
    references=r.insertInto(references);
    return r;
  }
  /** 
 * Return the list of all uplevel references.
 */
  public UplevelReference getReferences(){
    return references;
  }
  /** 
 * Return the same value as getReferences.
 * Also, mark the set of references frozen.
 * After that, it is an error to add new references.
 */
  public UplevelReference getReferencesFrozen(){
    referencesFrozen=true;
    return references;
  }
  /** 
 * assertion check
 */
  public final void referencesMustNotBeFrozen(){
    if (referencesFrozen) {
      throw new CompilerError("referencesMustNotBeFrozen " + this);
    }
  }
  /** 
 * Get helper method for class literal lookup.
 */
  public MemberDefinition getClassLiteralLookup(  long fwhere){
    throw new CompilerError("binary class");
  }
  /** 
 * Add a dependency
 */
  public void addDependency(  ClassDeclaration c){
    throw new CompilerError("addDependency");
  }
  /** 
 * Maintain a hash table of local and anonymous classes
 * whose internal names are prefixed by the current class.
 * The key is the simple internal name, less the prefix.
 */
  public ClassDefinition getLocalClass(  String name){
    if (localClasses == null) {
      return null;
    }
 else {
      return (ClassDefinition)localClasses.get(name);
    }
  }
  public void addLocalClass(  ClassDefinition c,  String name){
    if (localClasses == null) {
      localClasses=new Hashtable(LOCAL_CLASSES_SIZE);
    }
    localClasses.put(name,c);
  }
  /** 
 * Print for debugging
 */
  public void print(  PrintStream out){
    if (isPublic()) {
      out.print("public ");
    }
    if (isInterface()) {
      out.print("interface ");
    }
 else {
      out.print("class ");
    }
    out.print(getName() + " ");
    if (getSuperClass() != null) {
      out.print("extends " + getSuperClass().getName() + " ");
    }
    if (interfaces.length > 0) {
      out.print("implements ");
      for (int i=0; i < interfaces.length; i++) {
        if (i > 0) {
          out.print(", ");
        }
        out.print(interfaces[i].getName());
        out.print(" ");
      }
    }
    out.println("{");
    for (MemberDefinition f=getFirstMember(); f != null; f=f.getNextMember()) {
      out.print("    ");
      f.print(out);
    }
    out.println("}");
  }
  /** 
 * Convert to String
 */
  public String toString(){
    return getClassDeclaration().toString();
  }
  /** 
 * After the class has been written to disk, try to free up
 * some storage.
 */
  public void cleanup(  Environment env){
    if (env.dump()) {
      env.output("[cleanup " + getName() + "]");
    }
    for (MemberDefinition f=getFirstMember(); f != null; f=f.getNextMember()) {
      f.cleanup(env);
    }
    documentation=null;
  }
}

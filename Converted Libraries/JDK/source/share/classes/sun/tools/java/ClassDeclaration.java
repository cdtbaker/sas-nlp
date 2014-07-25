package sun.tools.java;
/** 
 * This class represents an Java class declaration. It refers
 * to either a binary or source definition.
 * ClassDefinitions are loaded on demand, this means that
 * class declarations are late bound. The definition of the
 * class is obtained in stages. The status field describes
 * the state of the class definition:
 * CS_UNDEFINED - the definition is not yet loaded
 * CS_UNDECIDED - a binary definition is loaded, but it is
 * still unclear if the source definition need to
 * be loaded
 * CS_BINARY    - the binary class is loaded
 * CS_PARSED    - the class is loaded from the source file, the
 * type information is available, but the class has
 * not yet been compiled.
 * CS_CHECKED   - the class is loaded from the source file and has
 * been type-checked.
 * CS_COMPILED  - the class has been type checked, compiled,
 * and written out.
 * CS_NOTFOUND  - no class definition could be found
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public final class ClassDeclaration implements Constants {
  int status;
  Type type;
  ClassDefinition definition;
  /** 
 * Constructor
 */
  public ClassDeclaration(  Identifier name){
    this.type=Type.tClass(name);
  }
  /** 
 * Get the status of the class
 */
  public int getStatus(){
    return status;
  }
  /** 
 * Get the name of the class
 */
  public Identifier getName(){
    return type.getClassName();
  }
  /** 
 * Get the type of the class
 */
  public Type getType(){
    return type;
  }
  /** 
 * Check if the class is defined
 */
  public boolean isDefined(){
switch (status) {
case CS_BINARY:
case CS_PARSED:
case CS_CHECKED:
case CS_COMPILED:
      return true;
  }
  return false;
}
/** 
 * Get the definition of this class. Returns null if
 * the class is not yet defined.
 */
public ClassDefinition getClassDefinition(){
  return definition;
}
/** 
 * This is a flag for use by getClassDefinition(env).  It is
 * used to mark that a class has been successfully looked up
 * by that method before.
 */
private boolean found=false;
/** 
 * Get the definition of this class, if the class is not
 * yet defined, load the definition. Loading a class may
 * throw various exceptions.
 */
public ClassDefinition getClassDefinition(Environment env) throws ClassNotFound {
  if (tracing)   env.dtEvent("getClassDefinition: " + getName() + ", status "+ getStatus());
  if (found) {
    return definition;
  }
  for (; ; ) {
switch (status) {
case CS_UNDEFINED:
case CS_UNDECIDED:
case CS_SOURCE:
      env.loadDefinition(this);
    break;
case CS_BINARY:
case CS_PARSED:
  if (!definition.isInsideLocal()) {
    definition.basicCheck(env);
  }
found=true;
return definition;
case CS_CHECKED:
case CS_COMPILED:
found=true;
return definition;
default :
throw new ClassNotFound(getName());
}
}
}
/** 
 * Get the definition of this class, if the class is not
 * yet defined, load the definition. Loading a class may
 * throw various exceptions.  Perform no basicCheck() on this
 * class.
 */
public ClassDefinition getClassDefinitionNoCheck(Environment env) throws ClassNotFound {
if (tracing) env.dtEvent("getClassDefinition: " + getName() + ", status "+ getStatus());
for (; ; ) {
switch (status) {
case CS_UNDEFINED:
case CS_UNDECIDED:
case CS_SOURCE:
env.loadDefinition(this);
break;
case CS_BINARY:
case CS_PARSED:
case CS_CHECKED:
case CS_COMPILED:
return definition;
default :
throw new ClassNotFound(getName());
}
}
}
/** 
 * Set the class definition
 */
public void setDefinition(ClassDefinition definition,int status){
if ((definition != null) && !getName().equals(definition.getName())) {
throw new CompilerError("setDefinition: name mismatch: " + this + ", "+ definition);
}
this.definition=definition;
this.status=status;
}
/** 
 * Equality
 */
public boolean equals(Object obj){
if ((obj != null) && (obj instanceof ClassDeclaration)) {
return type.equals(((ClassDeclaration)obj).type);
}
return false;
}
/** 
 * toString
 */
public String toString(){
String name=getName().toString();
String type="type ";
String nested=getName().isInner() ? "nested " : "";
if (getClassDefinition() != null) {
if (getClassDefinition().isInterface()) {
type="interface ";
}
 else {
type="class ";
}
if (!getClassDefinition().isTopLevel()) {
nested="inner ";
if (getClassDefinition().isLocal()) {
nested="local ";
if (!getClassDefinition().isAnonymous()) {
name=getClassDefinition().getLocalName() + " (" + name+ ")";
}
}
}
}
return nested + type + name;
}
}

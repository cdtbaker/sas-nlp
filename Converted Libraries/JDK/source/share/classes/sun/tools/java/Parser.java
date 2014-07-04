package sun.tools.java;
import sun.tools.tree.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
/** 
 * This class is used to parse Java statements and expressions.
 * The result is a parse tree.<p>
 * This class implements an operator precedence parser. Errors are
 * reported to the Environment object, if the error can't be
 * resolved immediately, a SyntaxError exception is thrown.<p>
 * Error recovery is implemented by catching SyntaxError exceptions
 * and discarding input tokens until an input token is reached that
 * is possibly a legal continuation.<p>
 * The parse tree that is constructed represents the input
 * exactly (no rewrites to simpler forms). This is important
 * if the resulting tree is to be used for code formatting in
 * a programming environment. Currently only documentation comments
 * are retained.<p>
 * The parsing algorithm does NOT use any type information. Changes
 * in the type system do not affect the structure of the parse tree.
 * This restriction does introduce an ambiguity an expression of the
 * form: (e1) e2 is assumed to be a cast if e2 does not start with
 * an operator. That means that (a) - b is interpreted as subtract
 * b from a and not cast negative b to type a. However, if a is a
 * simple type (byte, int, ...) then it is assumed to be a cast.<p>
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 * @author      Arthur van Hoff
 */
public class Parser extends Scanner implements ParserActions, Constants {
  /** 
 * Create a parser
 */
  protected Parser(  Environment env,  InputStream in) throws IOException {
    super(env,in);
    this.scanner=this;
    this.actions=this;
  }
  /** 
 * Create a parser, given a scanner.
 */
  protected Parser(  Scanner scanner) throws IOException {
    super(scanner.env);
    this.scanner=scanner;
    ((Scanner)this).env=scanner.env;
    ((Scanner)this).token=scanner.token;
    ((Scanner)this).pos=scanner.pos;
    this.actions=this;
  }
  /** 
 * Create a parser, given a scanner and the semantic callback.
 */
  public Parser(  Scanner scanner,  ParserActions actions) throws IOException {
    this(scanner);
    this.actions=actions;
  }
  /** 
 * Usually <code>this.actions == (ParserActions)this</code>.
 * However, a delegate scanner can produce tokens for this parser,
 * in which case <code>(Scanner)this</code> is unused,
 * except for <code>this.token</code> and <code>this.pos</code>
 * instance variables which are filled from the real scanner
 * by <code>this.scan()</code> and the constructor.
 */
  ParserActions actions;
  /** 
 * package declaration
 * @deprecated
 */
  @Deprecated public void packageDeclaration(  long off,  IdentifierToken nm){
    packageDeclaration(off,nm.id);
  }
  /** 
 * @deprecated
 */
  @Deprecated protected void packageDeclaration(  long off,  Identifier nm){
    throw new RuntimeException("beginClass method is abstract");
  }
  /** 
 * import class
 * @deprecated
 */
  @Deprecated public void importClass(  long off,  IdentifierToken nm){
    importClass(off,nm.id);
  }
  /** 
 * @deprecated Use the version with the IdentifierToken arguments.
 */
  @Deprecated protected void importClass(  long off,  Identifier nm){
    throw new RuntimeException("importClass method is abstract");
  }
  /** 
 * import package
 * @deprecated
 */
  @Deprecated public void importPackage(  long off,  IdentifierToken nm){
    importPackage(off,nm.id);
  }
  /** 
 * @deprecated Use the version with the IdentifierToken arguments.
 */
  @Deprecated protected void importPackage(  long off,  Identifier nm){
    throw new RuntimeException("importPackage method is abstract");
  }
  /** 
 * Define class
 * @deprecated
 */
  @Deprecated public ClassDefinition beginClass(  long off,  String doc,  int mod,  IdentifierToken nm,  IdentifierToken sup,  IdentifierToken impl[]){
    Identifier supId=(sup == null) ? null : sup.id;
    Identifier implIds[]=null;
    if (impl != null) {
      implIds=new Identifier[impl.length];
      for (int i=0; i < impl.length; i++) {
        implIds[i]=impl[i].id;
      }
    }
    beginClass(off,doc,mod,nm.id,supId,implIds);
    return getCurrentClass();
  }
  /** 
 * @deprecated Use the version with the IdentifierToken arguments.
 */
  @Deprecated protected void beginClass(  long off,  String doc,  int mod,  Identifier nm,  Identifier sup,  Identifier impl[]){
    throw new RuntimeException("beginClass method is abstract");
  }
  /** 
 * Report the current class under construction.
 * By default, it's a no-op which returns null.
 * It may only be called before the corresponding endClass().
 */
  protected ClassDefinition getCurrentClass(){
    return null;
  }
  /** 
 * End class
 * @deprecated
 */
  @Deprecated public void endClass(  long off,  ClassDefinition c){
    endClass(off,c.getName().getFlatName().getName());
  }
  /** 
 * @deprecated Use the version with the IdentifierToken arguments.
 */
  @Deprecated protected void endClass(  long off,  Identifier nm){
    throw new RuntimeException("endClass method is abstract");
  }
  /** 
 * Define a field
 * @deprecated
 */
  @Deprecated public void defineField(  long where,  ClassDefinition c,  String doc,  int mod,  Type t,  IdentifierToken nm,  IdentifierToken args[],  IdentifierToken exp[],  Node val){
    Identifier argIds[]=null;
    Identifier expIds[]=null;
    if (args != null) {
      argIds=new Identifier[args.length];
      for (int i=0; i < args.length; i++) {
        argIds[i]=args[i].id;
      }
    }
    if (exp != null) {
      expIds=new Identifier[exp.length];
      for (int i=0; i < exp.length; i++) {
        expIds[i]=exp[i].id;
      }
    }
    defineField(where,doc,mod,t,nm.id,argIds,expIds,val);
  }
  /** 
 * @deprecated Use the version with the IdentifierToken arguments.
 */
  @Deprecated protected void defineField(  long where,  String doc,  int mod,  Type t,  Identifier nm,  Identifier args[],  Identifier exp[],  Node val){
    throw new RuntimeException("defineField method is abstract");
  }
  private Node args[]=new Node[32];
  protected int argIndex=0;
  protected final void addArgument(  Node n){
    if (argIndex == args.length) {
      Node newArgs[]=new Node[args.length * 2];
      System.arraycopy(args,0,newArgs,0,args.length);
      args=newArgs;
    }
    args[argIndex++]=n;
  }
  protected final Expression exprArgs(  int index)[]{
    Expression e[]=new Expression[argIndex - index];
    System.arraycopy(args,index,e,0,argIndex - index);
    argIndex=index;
    return e;
  }
  protected final Statement statArgs(  int index)[]{
    Statement s[]=new Statement[argIndex - index];
    System.arraycopy(args,index,s,0,argIndex - index);
    argIndex=index;
    return s;
  }
  /** 
 * Expect a token, return its value, scan the next token or
 * throw an exception.
 */
  protected void expect(  int t) throws SyntaxError, IOException {
    if (token != t) {
switch (t) {
case IDENT:
        env.error(scanner.prevPos,"identifier.expected");
      break;
default :
    env.error(scanner.prevPos,"token.expected",opNames[t]);
  break;
}
throw new SyntaxError();
}
scan();
}
/** 
 * Parse a type expression. Does not parse the []'s.
 */
protected Expression parseTypeExpression() throws SyntaxError, IOException {
switch (token) {
case VOID:
return new TypeExpression(scan(),Type.tVoid);
case BOOLEAN:
return new TypeExpression(scan(),Type.tBoolean);
case BYTE:
return new TypeExpression(scan(),Type.tByte);
case CHAR:
return new TypeExpression(scan(),Type.tChar);
case SHORT:
return new TypeExpression(scan(),Type.tShort);
case INT:
return new TypeExpression(scan(),Type.tInt);
case LONG:
return new TypeExpression(scan(),Type.tLong);
case FLOAT:
return new TypeExpression(scan(),Type.tFloat);
case DOUBLE:
return new TypeExpression(scan(),Type.tDouble);
case IDENT:
Expression e=new IdentifierExpression(pos,scanner.idValue);
scan();
while (token == FIELD) {
e=new FieldExpression(scan(),e,scanner.idValue);
expect(IDENT);
}
return e;
}
env.error(pos,"type.expected");
throw new SyntaxError();
}
/** 
 * Parse a method invocation. Should be called when the current
 * then is the '(' of the argument list.
 */
protected Expression parseMethodExpression(Expression e,Identifier id) throws SyntaxError, IOException {
long p=scan();
int i=argIndex;
if (token != RPAREN) {
addArgument(parseExpression());
while (token == COMMA) {
scan();
addArgument(parseExpression());
}
}
expect(RPAREN);
return new MethodExpression(p,e,id,exprArgs(i));
}
/** 
 * Parse a new instance expression.  Should be called when the current
 * token is the '(' of the argument list.
 */
protected Expression parseNewInstanceExpression(long p,Expression outerArg,Expression type) throws SyntaxError, IOException {
int i=argIndex;
expect(LPAREN);
if (token != RPAREN) {
addArgument(parseExpression());
while (token == COMMA) {
scan();
addArgument(parseExpression());
}
}
expect(RPAREN);
ClassDefinition body=null;
if (token == LBRACE && !(type instanceof TypeExpression)) {
long tp=pos;
Identifier superName=FieldExpression.toIdentifier(type);
if (superName == null) {
env.error(type.getWhere(),"type.expected");
}
Vector ext=new Vector(1);
Vector impl=new Vector(0);
ext.addElement(new IdentifierToken(idNull));
if (token == IMPLEMENTS || token == EXTENDS) {
env.error(pos,"anonymous.extends");
parseInheritance(ext,impl);
}
body=parseClassBody(new IdentifierToken(tp,idNull),M_ANONYMOUS | M_LOCAL,EXPR,null,ext,impl,type.getWhere());
}
if (outerArg == null && body == null) {
return new NewInstanceExpression(p,type,exprArgs(i));
}
return new NewInstanceExpression(p,type,exprArgs(i),outerArg,body);
}
/** 
 * Parse a primary expression.
 */
protected Expression parseTerm() throws SyntaxError, IOException {
switch (token) {
case CHARVAL:
{
char v=scanner.charValue;
return new CharExpression(scan(),v);
}
case INTVAL:
{
int v=scanner.intValue;
long q=scan();
if (v < 0 && radix == 10) env.error(q,"overflow.int.dec");
return new IntExpression(q,v);
}
case LONGVAL:
{
long v=scanner.longValue;
long q=scan();
if (v < 0 && radix == 10) env.error(q,"overflow.long.dec");
return new LongExpression(q,v);
}
case FLOATVAL:
{
float v=scanner.floatValue;
return new FloatExpression(scan(),v);
}
case DOUBLEVAL:
{
double v=scanner.doubleValue;
return new DoubleExpression(scan(),v);
}
case STRINGVAL:
{
String v=scanner.stringValue;
return new StringExpression(scan(),v);
}
case IDENT:
{
Identifier v=scanner.idValue;
long p=scan();
return (token == LPAREN) ? parseMethodExpression(null,v) : new IdentifierExpression(p,v);
}
case TRUE:
return new BooleanExpression(scan(),true);
case FALSE:
return new BooleanExpression(scan(),false);
case NULL:
return new NullExpression(scan());
case THIS:
{
Expression e=new ThisExpression(scan());
return (token == LPAREN) ? parseMethodExpression(e,idInit) : e;
}
case SUPER:
{
Expression e=new SuperExpression(scan());
return (token == LPAREN) ? parseMethodExpression(e,idInit) : e;
}
case VOID:
case BOOLEAN:
case BYTE:
case CHAR:
case SHORT:
case INT:
case LONG:
case FLOAT:
case DOUBLE:
return parseTypeExpression();
case ADD:
{
long p=scan();
switch (token) {
case INTVAL:
{
int v=scanner.intValue;
long q=scan();
if (v < 0 && radix == 10) env.error(q,"overflow.int.dec");
return new IntExpression(q,v);
}
case LONGVAL:
{
long v=scanner.longValue;
long q=scan();
if (v < 0 && radix == 10) env.error(q,"overflow.long.dec");
return new LongExpression(q,v);
}
case FLOATVAL:
{
float v=scanner.floatValue;
return new FloatExpression(scan(),v);
}
case DOUBLEVAL:
{
double v=scanner.doubleValue;
return new DoubleExpression(scan(),v);
}
}
return new PositiveExpression(p,parseTerm());
}
case SUB:
{
long p=scan();
switch (token) {
case INTVAL:
{
int v=-scanner.intValue;
return new IntExpression(scan(),v);
}
case LONGVAL:
{
long v=-scanner.longValue;
return new LongExpression(scan(),v);
}
case FLOATVAL:
{
float v=-scanner.floatValue;
return new FloatExpression(scan(),v);
}
case DOUBLEVAL:
{
double v=-scanner.doubleValue;
return new DoubleExpression(scan(),v);
}
}
return new NegativeExpression(p,parseTerm());
}
case NOT:
return new NotExpression(scan(),parseTerm());
case BITNOT:
return new BitNotExpression(scan(),parseTerm());
case INC:
return new PreIncExpression(scan(),parseTerm());
case DEC:
return new PreDecExpression(scan(),parseTerm());
case LPAREN:
{
long p=scan();
Expression e=parseExpression();
expect(RPAREN);
if (e.getOp() == TYPE) {
return new CastExpression(p,e,parseTerm());
}
switch (token) {
case INC:
return new PostIncExpression(scan(),e);
case DEC:
return new PostDecExpression(scan(),e);
case LPAREN:
case CHARVAL:
case INTVAL:
case LONGVAL:
case FLOATVAL:
case DOUBLEVAL:
case STRINGVAL:
case IDENT:
case TRUE:
case FALSE:
case NOT:
case BITNOT:
case THIS:
case SUPER:
case NULL:
case NEW:
return new CastExpression(p,e,parseTerm());
}
return new ExprExpression(p,e);
}
case LBRACE:
{
long p=scan();
int i=argIndex;
if (token != RBRACE) {
addArgument(parseExpression());
while (token == COMMA) {
scan();
if (token == RBRACE) {
break;
}
addArgument(parseExpression());
}
}
expect(RBRACE);
return new ArrayExpression(p,exprArgs(i));
}
case NEW:
{
long p=scan();
int i=argIndex;
if (token == LPAREN) {
scan();
Expression e=parseExpression();
expect(RPAREN);
env.error(p,"not.supported","new(...)");
return new NullExpression(p);
}
Expression e=parseTypeExpression();
if (token == LSQBRACKET) {
while (token == LSQBRACKET) {
scan();
addArgument((token != RSQBRACKET) ? parseExpression() : null);
expect(RSQBRACKET);
}
Expression[] dims=exprArgs(i);
if (token == LBRACE) {
return new NewArrayExpression(p,e,dims,parseTerm());
}
return new NewArrayExpression(p,e,dims);
}
 else {
return parseNewInstanceExpression(p,null,e);
}
}
}
env.error(scanner.prevPos,"missing.term");
return new IntExpression(pos,0);
}
/** 
 * Parse an expression.
 */
protected Expression parseExpression() throws SyntaxError, IOException {
for (Expression e=parseTerm(); e != null; e=e.order()) {
Expression more=parseBinaryExpression(e);
if (more == null) return e;
e=more;
}
return null;
}
/** 
 * Given a left-hand term, parse an operator and right-hand term.
 */
protected Expression parseBinaryExpression(Expression e) throws SyntaxError, IOException {
if (e != null) {
switch (token) {
case LSQBRACKET:
{
long p=scan();
Expression index=(token != RSQBRACKET) ? parseExpression() : null;
expect(RSQBRACKET);
e=new ArrayAccessExpression(p,e,index);
break;
}
case INC:
e=new PostIncExpression(scan(),e);
break;
case DEC:
e=new PostDecExpression(scan(),e);
break;
case FIELD:
{
long p=scan();
if (token == THIS) {
long q=scan();
if (token == LPAREN) {
e=new ThisExpression(q,e);
e=parseMethodExpression(e,idInit);
}
 else {
e=new FieldExpression(p,e,idThis);
}
break;
}
if (token == SUPER) {
long q=scan();
if (token == LPAREN) {
e=new SuperExpression(q,e);
e=parseMethodExpression(e,idInit);
}
 else {
e=new FieldExpression(p,e,idSuper);
}
break;
}
if (token == NEW) {
scan();
if (token != IDENT) expect(IDENT);
e=parseNewInstanceExpression(p,e,parseTypeExpression());
break;
}
if (token == CLASS) {
scan();
e=new FieldExpression(p,e,idClass);
break;
}
Identifier id=scanner.idValue;
expect(IDENT);
if (token == LPAREN) {
e=parseMethodExpression(e,id);
}
 else {
e=new FieldExpression(p,e,id);
}
break;
}
case INSTANCEOF:
e=new InstanceOfExpression(scan(),e,parseTerm());
break;
case ADD:
e=new AddExpression(scan(),e,parseTerm());
break;
case SUB:
e=new SubtractExpression(scan(),e,parseTerm());
break;
case MUL:
e=new MultiplyExpression(scan(),e,parseTerm());
break;
case DIV:
e=new DivideExpression(scan(),e,parseTerm());
break;
case REM:
e=new RemainderExpression(scan(),e,parseTerm());
break;
case LSHIFT:
e=new ShiftLeftExpression(scan(),e,parseTerm());
break;
case RSHIFT:
e=new ShiftRightExpression(scan(),e,parseTerm());
break;
case URSHIFT:
e=new UnsignedShiftRightExpression(scan(),e,parseTerm());
break;
case LT:
e=new LessExpression(scan(),e,parseTerm());
break;
case LE:
e=new LessOrEqualExpression(scan(),e,parseTerm());
break;
case GT:
e=new GreaterExpression(scan(),e,parseTerm());
break;
case GE:
e=new GreaterOrEqualExpression(scan(),e,parseTerm());
break;
case EQ:
e=new EqualExpression(scan(),e,parseTerm());
break;
case NE:
e=new NotEqualExpression(scan(),e,parseTerm());
break;
case BITAND:
e=new BitAndExpression(scan(),e,parseTerm());
break;
case BITXOR:
e=new BitXorExpression(scan(),e,parseTerm());
break;
case BITOR:
e=new BitOrExpression(scan(),e,parseTerm());
break;
case AND:
e=new AndExpression(scan(),e,parseTerm());
break;
case OR:
e=new OrExpression(scan(),e,parseTerm());
break;
case ASSIGN:
e=new AssignExpression(scan(),e,parseTerm());
break;
case ASGMUL:
e=new AssignMultiplyExpression(scan(),e,parseTerm());
break;
case ASGDIV:
e=new AssignDivideExpression(scan(),e,parseTerm());
break;
case ASGREM:
e=new AssignRemainderExpression(scan(),e,parseTerm());
break;
case ASGADD:
e=new AssignAddExpression(scan(),e,parseTerm());
break;
case ASGSUB:
e=new AssignSubtractExpression(scan(),e,parseTerm());
break;
case ASGLSHIFT:
e=new AssignShiftLeftExpression(scan(),e,parseTerm());
break;
case ASGRSHIFT:
e=new AssignShiftRightExpression(scan(),e,parseTerm());
break;
case ASGURSHIFT:
e=new AssignUnsignedShiftRightExpression(scan(),e,parseTerm());
break;
case ASGBITAND:
e=new AssignBitAndExpression(scan(),e,parseTerm());
break;
case ASGBITOR:
e=new AssignBitOrExpression(scan(),e,parseTerm());
break;
case ASGBITXOR:
e=new AssignBitXorExpression(scan(),e,parseTerm());
break;
case QUESTIONMARK:
{
long p=scan();
Expression second=parseExpression();
expect(COLON);
Expression third=parseExpression();
if (third instanceof AssignExpression || third instanceof AssignOpExpression) {
env.error(third.getWhere(),"assign.in.conditionalexpr");
}
e=new ConditionalExpression(p,e,second,third);
break;
}
default :
return null;
}
}
return e;
}
/** 
 * Recover after a syntax error in a statement. This involves
 * discarding tokens until EOF or a possible continuation is
 * encountered.
 */
protected boolean recoverStatement() throws SyntaxError, IOException {
while (true) {
switch (token) {
case EOF:
case RBRACE:
case LBRACE:
case IF:
case FOR:
case WHILE:
case DO:
case TRY:
case CATCH:
case FINALLY:
case BREAK:
case CONTINUE:
case RETURN:
return true;
case VOID:
case STATIC:
case PUBLIC:
case PRIVATE:
case SYNCHRONIZED:
case INTERFACE:
case CLASS:
case TRANSIENT:
expect(RBRACE);
return false;
case LPAREN:
match(LPAREN,RPAREN);
scan();
break;
case LSQBRACKET:
match(LSQBRACKET,RSQBRACKET);
scan();
break;
default :
scan();
break;
}
}
}
/** 
 * Parse declaration, called after the type expression
 * has been parsed and the current token is IDENT.
 */
protected Statement parseDeclaration(long p,int mod,Expression type) throws SyntaxError, IOException {
int i=argIndex;
if (token == IDENT) {
addArgument(new VarDeclarationStatement(pos,parseExpression()));
while (token == COMMA) {
scan();
addArgument(new VarDeclarationStatement(pos,parseExpression()));
}
}
return new DeclarationStatement(p,mod,type,statArgs(i));
}
/** 
 * Check if an expression is a legal toplevel expression.
 * Only method, inc, dec, and new expression are allowed.
 */
protected void topLevelExpression(Expression e){
switch (e.getOp()) {
case ASSIGN:
case ASGMUL:
case ASGDIV:
case ASGREM:
case ASGADD:
case ASGSUB:
case ASGLSHIFT:
case ASGRSHIFT:
case ASGURSHIFT:
case ASGBITAND:
case ASGBITOR:
case ASGBITXOR:
case PREINC:
case PREDEC:
case POSTINC:
case POSTDEC:
case METHOD:
case NEWINSTANCE:
return;
}
env.error(e.getWhere(),"invalid.expr");
}
/** 
 * Parse a statement.
 */
protected Statement parseStatement() throws SyntaxError, IOException {
switch (token) {
case SEMICOLON:
return new CompoundStatement(scan(),new Statement[0]);
case LBRACE:
return parseBlockStatement();
case IF:
{
long p=scan();
expect(LPAREN);
Expression c=parseExpression();
expect(RPAREN);
Statement t=parseStatement();
if (token == ELSE) {
scan();
return new IfStatement(p,c,t,parseStatement());
}
 else {
return new IfStatement(p,c,t,null);
}
}
case ELSE:
{
env.error(scan(),"else.without.if");
return parseStatement();
}
case FOR:
{
long p=scan();
Statement init=null;
Expression cond=null, inc=null;
expect(LPAREN);
if (token != SEMICOLON) {
long p2=pos;
int mod=parseModifiers(M_FINAL);
Expression e=parseExpression();
if (token == IDENT) {
init=parseDeclaration(p2,mod,e);
}
 else {
if (mod != 0) {
expect(IDENT);
}
topLevelExpression(e);
while (token == COMMA) {
long p3=scan();
Expression e2=parseExpression();
topLevelExpression(e2);
e=new CommaExpression(p3,e,e2);
}
init=new ExpressionStatement(p2,e);
}
}
expect(SEMICOLON);
if (token != SEMICOLON) {
cond=parseExpression();
}
expect(SEMICOLON);
if (token != RPAREN) {
inc=parseExpression();
topLevelExpression(inc);
while (token == COMMA) {
long p2=scan();
Expression e2=parseExpression();
topLevelExpression(e2);
inc=new CommaExpression(p2,inc,e2);
}
}
expect(RPAREN);
return new ForStatement(p,init,cond,inc,parseStatement());
}
case WHILE:
{
long p=scan();
expect(LPAREN);
Expression cond=parseExpression();
expect(RPAREN);
return new WhileStatement(p,cond,parseStatement());
}
case DO:
{
long p=scan();
Statement body=parseStatement();
expect(WHILE);
expect(LPAREN);
Expression cond=parseExpression();
expect(RPAREN);
expect(SEMICOLON);
return new DoStatement(p,body,cond);
}
case BREAK:
{
long p=scan();
Identifier label=null;
if (token == IDENT) {
label=scanner.idValue;
scan();
}
expect(SEMICOLON);
return new BreakStatement(p,label);
}
case CONTINUE:
{
long p=scan();
Identifier label=null;
if (token == IDENT) {
label=scanner.idValue;
scan();
}
expect(SEMICOLON);
return new ContinueStatement(p,label);
}
case RETURN:
{
long p=scan();
Expression e=null;
if (token != SEMICOLON) {
e=parseExpression();
}
expect(SEMICOLON);
return new ReturnStatement(p,e);
}
case SWITCH:
{
long p=scan();
int i=argIndex;
expect(LPAREN);
Expression e=parseExpression();
expect(RPAREN);
expect(LBRACE);
while ((token != EOF) && (token != RBRACE)) {
int j=argIndex;
try {
switch (token) {
case CASE:
addArgument(new CaseStatement(scan(),parseExpression()));
expect(COLON);
break;
case DEFAULT:
addArgument(new CaseStatement(scan(),null));
expect(COLON);
break;
default :
addArgument(parseStatement());
break;
}
}
 catch (SyntaxError ee) {
argIndex=j;
if (!recoverStatement()) {
throw ee;
}
}
}
expect(RBRACE);
return new SwitchStatement(p,e,statArgs(i));
}
case CASE:
{
env.error(pos,"case.without.switch");
while (token == CASE) {
scan();
parseExpression();
expect(COLON);
}
return parseStatement();
}
case DEFAULT:
{
env.error(pos,"default.without.switch");
scan();
expect(COLON);
return parseStatement();
}
case TRY:
{
long p=scan();
Statement init=null;
int i=argIndex;
boolean catches=false;
if (false && token == LPAREN) {
expect(LPAREN);
long p2=pos;
int mod=parseModifiers(M_FINAL);
Expression e=parseExpression();
if (token == IDENT) {
init=parseDeclaration(p2,mod,e);
}
 else {
if (mod != 0) {
expect(IDENT);
}
init=new ExpressionStatement(p2,e);
}
expect(RPAREN);
}
Statement s=parseBlockStatement();
if (init != null) {
}
while (token == CATCH) {
long pp=pos;
expect(CATCH);
expect(LPAREN);
int mod=parseModifiers(M_FINAL);
Expression t=parseExpression();
IdentifierToken id=scanner.getIdToken();
expect(IDENT);
id.modifiers=mod;
expect(RPAREN);
addArgument(new CatchStatement(pp,t,id,parseBlockStatement()));
catches=true;
}
if (catches) s=new TryStatement(p,s,statArgs(i));
if (token == FINALLY) {
scan();
return new FinallyStatement(p,s,parseBlockStatement());
}
 else if (catches || init != null) {
return s;
}
 else {
env.error(pos,"try.without.catch.finally");
return new TryStatement(p,s,null);
}
}
case CATCH:
{
env.error(pos,"catch.without.try");
Statement s;
do {
scan();
expect(LPAREN);
parseModifiers(M_FINAL);
parseExpression();
expect(IDENT);
expect(RPAREN);
s=parseBlockStatement();
}
 while (token == CATCH);
if (token == FINALLY) {
scan();
s=parseBlockStatement();
}
return s;
}
case FINALLY:
{
env.error(pos,"finally.without.try");
scan();
return parseBlockStatement();
}
case THROW:
{
long p=scan();
Expression e=parseExpression();
expect(SEMICOLON);
return new ThrowStatement(p,e);
}
case GOTO:
{
long p=scan();
expect(IDENT);
expect(SEMICOLON);
env.error(p,"not.supported","goto");
return new CompoundStatement(p,new Statement[0]);
}
case SYNCHRONIZED:
{
long p=scan();
expect(LPAREN);
Expression e=parseExpression();
expect(RPAREN);
return new SynchronizedStatement(p,e,parseBlockStatement());
}
case INTERFACE:
case CLASS:
return parseLocalClass(0);
case CONST:
case ABSTRACT:
case FINAL:
case STRICTFP:
{
long p=pos;
int mod=parseModifiers(M_FINAL | M_ABSTRACT | M_STRICTFP);
switch (token) {
case INTERFACE:
case CLASS:
return parseLocalClass(mod);
case BOOLEAN:
case BYTE:
case CHAR:
case SHORT:
case INT:
case LONG:
case FLOAT:
case DOUBLE:
case IDENT:
{
if ((mod & (M_ABSTRACT | M_STRICTFP)) != 0) {
mod&=~(M_ABSTRACT | M_STRICTFP);
expect(CLASS);
}
Expression e=parseExpression();
if (token != IDENT) {
expect(IDENT);
}
Statement s=parseDeclaration(p,mod,e);
expect(SEMICOLON);
return s;
}
default :
env.error(pos,"type.expected");
throw new SyntaxError();
}
}
case VOID:
case STATIC:
case PUBLIC:
case PRIVATE:
case TRANSIENT:
env.error(pos,"statement.expected");
throw new SyntaxError();
}
long p=pos;
Expression e=parseExpression();
if (token == IDENT) {
Statement s=parseDeclaration(p,0,e);
expect(SEMICOLON);
return s;
}
if (token == COLON) {
scan();
Statement s=parseStatement();
s.setLabel(env,e);
return s;
}
topLevelExpression(e);
expect(SEMICOLON);
return new ExpressionStatement(p,e);
}
protected Statement parseBlockStatement() throws SyntaxError, IOException {
if (token != LBRACE) {
env.error(scanner.prevPos,"token.expected",opNames[LBRACE]);
return parseStatement();
}
long p=scan();
int i=argIndex;
while ((token != EOF) && (token != RBRACE)) {
int j=argIndex;
try {
addArgument(parseStatement());
}
 catch (SyntaxError e) {
argIndex=j;
if (!recoverStatement()) {
throw e;
}
}
}
expect(RBRACE);
return new CompoundStatement(p,statArgs(i));
}
/** 
 * Parse an identifier. ie: a.b.c returns "a.b.c"
 * If star is true then "a.b.*" is allowed.
 * The return value encodes both the identifier and its location.
 */
protected IdentifierToken parseName(boolean star) throws SyntaxError, IOException {
IdentifierToken res=scanner.getIdToken();
expect(IDENT);
if (token != FIELD) {
return res;
}
StringBuffer buf=new StringBuffer(res.id.toString());
while (token == FIELD) {
scan();
if ((token == MUL) && star) {
scan();
buf.append(".*");
break;
}
buf.append('.');
if (token == IDENT) {
buf.append(scanner.idValue);
}
expect(IDENT);
}
res.id=Identifier.lookup(buf.toString());
return res;
}
/** 
 * @deprecated
 * @see #parseName
 */
@Deprecated protected Identifier parseIdentifier(boolean star) throws SyntaxError, IOException {
return parseName(star).id;
}
/** 
 * Parse a type expression, this results in a Type.
 * The parse includes trailing array brackets.
 */
protected Type parseType() throws SyntaxError, IOException {
Type t;
switch (token) {
case IDENT:
t=Type.tClass(parseName(false).id);
break;
case VOID:
scan();
t=Type.tVoid;
break;
case BOOLEAN:
scan();
t=Type.tBoolean;
break;
case BYTE:
scan();
t=Type.tByte;
break;
case CHAR:
scan();
t=Type.tChar;
break;
case SHORT:
scan();
t=Type.tShort;
break;
case INT:
scan();
t=Type.tInt;
break;
case FLOAT:
scan();
t=Type.tFloat;
break;
case LONG:
scan();
t=Type.tLong;
break;
case DOUBLE:
scan();
t=Type.tDouble;
break;
default :
env.error(pos,"type.expected");
throw new SyntaxError();
}
return parseArrayBrackets(t);
}
/** 
 * Parse the tail of a type expression, which might be array brackets.
 * Return the given type, as possibly modified by the suffix.
 */
protected Type parseArrayBrackets(Type t) throws SyntaxError, IOException {
while (token == LSQBRACKET) {
scan();
if (token != RSQBRACKET) {
env.error(pos,"array.dim.in.decl");
parseExpression();
}
expect(RSQBRACKET);
t=Type.tArray(t);
}
return t;
}
private int aCount=0;
private Type aTypes[]=new Type[8];
private IdentifierToken aNames[]=new IdentifierToken[aTypes.length];
private void addArgument(int mod,Type t,IdentifierToken nm){
nm.modifiers=mod;
if (aCount >= aTypes.length) {
Type newATypes[]=new Type[aCount * 2];
System.arraycopy(aTypes,0,newATypes,0,aCount);
aTypes=newATypes;
IdentifierToken newANames[]=new IdentifierToken[aCount * 2];
System.arraycopy(aNames,0,newANames,0,aCount);
aNames=newANames;
}
aTypes[aCount]=t;
aNames[aCount++]=nm;
}
/** 
 * Parse a possibly-empty sequence of modifier keywords.
 * Return the resulting bitmask.
 * Diagnose repeated modifiers, but make no other checks.
 * Only modifiers mentioned in the given bitmask are scanned;
 * an unmatched modifier must be handled by the caller.
 */
protected int parseModifiers(int mask) throws IOException {
int mod=0;
while (true) {
if (token == CONST) {
env.error(pos,"not.supported","const");
scan();
}
int nextmod=0;
switch (token) {
case PRIVATE:
nextmod=M_PRIVATE;
break;
case PUBLIC:
nextmod=M_PUBLIC;
break;
case PROTECTED:
nextmod=M_PROTECTED;
break;
case STATIC:
nextmod=M_STATIC;
break;
case TRANSIENT:
nextmod=M_TRANSIENT;
break;
case FINAL:
nextmod=M_FINAL;
break;
case ABSTRACT:
nextmod=M_ABSTRACT;
break;
case NATIVE:
nextmod=M_NATIVE;
break;
case VOLATILE:
nextmod=M_VOLATILE;
break;
case SYNCHRONIZED:
nextmod=M_SYNCHRONIZED;
break;
case STRICTFP:
nextmod=M_STRICTFP;
break;
}
if ((nextmod & mask) == 0) {
break;
}
if ((nextmod & mod) != 0) {
env.error(pos,"repeated.modifier");
}
mod|=nextmod;
scan();
}
return mod;
}
private ClassDefinition curClass;
/** 
 * Parse a field.
 */
protected void parseField() throws SyntaxError, IOException {
if (token == SEMICOLON) {
scan();
return;
}
String doc=scanner.docComment;
long p=pos;
int mod=parseModifiers(MM_FIELD | MM_METHOD);
if ((mod == (mod & M_STATIC)) && (token == LBRACE)) {
actions.defineField(p,curClass,doc,mod,Type.tMethod(Type.tVoid),new IdentifierToken(idClassInit),null,null,parseStatement());
return;
}
if (token == CLASS || token == INTERFACE) {
parseNamedClass(mod,CLASS,doc);
return;
}
p=pos;
Type t=parseType();
IdentifierToken id=null;
switch (token) {
case IDENT:
id=scanner.getIdToken();
p=scan();
break;
case LPAREN:
id=new IdentifierToken(idInit);
if ((mod & M_STRICTFP) != 0) env.error(pos,"bad.constructor.modifier");
break;
default :
expect(IDENT);
}
if (token == LPAREN) {
scan();
aCount=0;
if (token != RPAREN) {
int am=parseModifiers(M_FINAL);
Type at=parseType();
IdentifierToken an=scanner.getIdToken();
expect(IDENT);
at=parseArrayBrackets(at);
addArgument(am,at,an);
while (token == COMMA) {
scan();
am=parseModifiers(M_FINAL);
at=parseType();
an=scanner.getIdToken();
expect(IDENT);
at=parseArrayBrackets(at);
addArgument(am,at,an);
}
}
expect(RPAREN);
t=parseArrayBrackets(t);
Type atypes[]=new Type[aCount];
System.arraycopy(aTypes,0,atypes,0,aCount);
IdentifierToken anames[]=new IdentifierToken[aCount];
System.arraycopy(aNames,0,anames,0,aCount);
t=Type.tMethod(t,atypes);
IdentifierToken exp[]=null;
if (token == THROWS) {
Vector v=new Vector();
scan();
v.addElement(parseName(false));
while (token == COMMA) {
scan();
v.addElement(parseName(false));
}
exp=new IdentifierToken[v.size()];
v.copyInto(exp);
}
switch (token) {
case LBRACE:
int oldFPstate=FPstate;
if ((mod & M_STRICTFP) != 0) {
FPstate=M_STRICTFP;
}
 else {
mod|=FPstate & M_STRICTFP;
}
actions.defineField(p,curClass,doc,mod,t,id,anames,exp,parseStatement());
FPstate=oldFPstate;
break;
case SEMICOLON:
scan();
actions.defineField(p,curClass,doc,mod,t,id,anames,exp,null);
break;
default :
if ((mod & (M_NATIVE | M_ABSTRACT)) == 0) {
expect(LBRACE);
}
 else {
expect(SEMICOLON);
}
}
return;
}
while (true) {
p=pos;
Type vt=parseArrayBrackets(t);
Node init=null;
if (token == ASSIGN) {
scan();
init=parseExpression();
}
actions.defineField(p,curClass,doc,mod,vt,id,null,null,init);
if (token != COMMA) {
expect(SEMICOLON);
return;
}
scan();
id=scanner.getIdToken();
expect(IDENT);
}
}
/** 
 * Recover after a syntax error in a field. This involves
 * discarding tokens until an EOF or a possible legal
 * continuation is encountered.
 */
protected void recoverField(ClassDefinition newClass) throws SyntaxError, IOException {
while (true) {
switch (token) {
case EOF:
case STATIC:
case FINAL:
case PUBLIC:
case PRIVATE:
case SYNCHRONIZED:
case TRANSIENT:
case VOID:
case BOOLEAN:
case BYTE:
case CHAR:
case SHORT:
case INT:
case FLOAT:
case LONG:
case DOUBLE:
return;
case LBRACE:
match(LBRACE,RBRACE);
scan();
break;
case LPAREN:
match(LPAREN,RPAREN);
scan();
break;
case LSQBRACKET:
match(LSQBRACKET,RSQBRACKET);
scan();
break;
case RBRACE:
case INTERFACE:
case CLASS:
case IMPORT:
case PACKAGE:
actions.endClass(pos,newClass);
throw new SyntaxError();
default :
scan();
break;
}
}
}
/** 
 * Parse a top-level class or interface declaration.
 */
protected void parseClass() throws SyntaxError, IOException {
String doc=scanner.docComment;
int mod=parseModifiers(MM_CLASS | MM_MEMBER);
parseNamedClass(mod,PACKAGE,doc);
}
private int FPstate=0;
/** 
 * Parse a block-local class or interface declaration.
 */
protected Statement parseLocalClass(int mod) throws SyntaxError, IOException {
long p=pos;
ClassDefinition body=parseNamedClass(M_LOCAL | mod,STAT,null);
Statement ds[]={new VarDeclarationStatement(p,new LocalMember(body),null)};
Expression type=new TypeExpression(p,body.getType());
return new DeclarationStatement(p,0,type,ds);
}
/** 
 * Parse a named class or interface declaration,
 * starting at "class" or "interface".
 * @arg ctx Syntactic context of the class, one of {PACKAGE CLASS STAT EXPR}.
 */
protected ClassDefinition parseNamedClass(int mod,int ctx,String doc) throws SyntaxError, IOException {
switch (token) {
case INTERFACE:
scan();
mod|=M_INTERFACE;
break;
case CLASS:
scan();
break;
default :
env.error(pos,"class.expected");
break;
}
int oldFPstate=FPstate;
if ((mod & M_STRICTFP) != 0) {
FPstate=M_STRICTFP;
}
 else {
mod|=FPstate & M_STRICTFP;
}
IdentifierToken nm=scanner.getIdToken();
long p=pos;
expect(IDENT);
Vector ext=new Vector();
Vector impl=new Vector();
parseInheritance(ext,impl);
ClassDefinition tmp=parseClassBody(nm,mod,ctx,doc,ext,impl,p);
FPstate=oldFPstate;
return tmp;
}
protected void parseInheritance(Vector ext,Vector impl) throws SyntaxError, IOException {
if (token == EXTENDS) {
scan();
ext.addElement(parseName(false));
while (token == COMMA) {
scan();
ext.addElement(parseName(false));
}
}
if (token == IMPLEMENTS) {
scan();
impl.addElement(parseName(false));
while (token == COMMA) {
scan();
impl.addElement(parseName(false));
}
}
}
/** 
 * Parse the body of a class or interface declaration,
 * starting at the left brace.
 */
protected ClassDefinition parseClassBody(IdentifierToken nm,int mod,int ctx,String doc,Vector ext,Vector impl,long p) throws SyntaxError, IOException {
IdentifierToken sup=null;
if ((mod & M_INTERFACE) != 0) {
if (impl.size() > 0) {
env.error(((IdentifierToken)impl.elementAt(0)).getWhere(),"intf.impl.intf");
}
impl=ext;
}
 else {
if (ext.size() > 0) {
if (ext.size() > 1) {
env.error(((IdentifierToken)ext.elementAt(1)).getWhere(),"multiple.inherit");
}
sup=(IdentifierToken)ext.elementAt(0);
}
}
ClassDefinition oldClass=curClass;
IdentifierToken implids[]=new IdentifierToken[impl.size()];
impl.copyInto(implids);
ClassDefinition newClass=actions.beginClass(p,doc,mod,nm,sup,implids);
expect(LBRACE);
while ((token != EOF) && (token != RBRACE)) {
try {
curClass=newClass;
parseField();
}
 catch (SyntaxError e) {
recoverField(newClass);
}
 finally {
curClass=oldClass;
}
}
expect(RBRACE);
actions.endClass(scanner.prevPos,newClass);
return newClass;
}
/** 
 * Recover after a syntax error in the file.
 * This involves discarding tokens until an EOF
 * or a possible legal continuation is encountered.
 */
protected void recoverFile() throws IOException {
while (true) {
switch (token) {
case CLASS:
case INTERFACE:
return;
case LBRACE:
match(LBRACE,RBRACE);
scan();
break;
case LPAREN:
match(LPAREN,RPAREN);
scan();
break;
case LSQBRACKET:
match(LSQBRACKET,RSQBRACKET);
scan();
break;
case EOF:
return;
default :
scan();
break;
}
}
}
/** 
 * Parse an Java file.
 */
public void parseFile(){
try {
try {
if (token == PACKAGE) {
long p=scan();
IdentifierToken id=parseName(false);
expect(SEMICOLON);
actions.packageDeclaration(p,id);
}
}
 catch (SyntaxError e) {
recoverFile();
}
while (token == IMPORT) {
try {
long p=scan();
IdentifierToken id=parseName(true);
expect(SEMICOLON);
if (id.id.getName().equals(idStar)) {
id.id=id.id.getQualifier();
actions.importPackage(p,id);
}
 else {
actions.importClass(p,id);
}
}
 catch (SyntaxError e) {
recoverFile();
}
}
while (token != EOF) {
try {
switch (token) {
case FINAL:
case PUBLIC:
case PRIVATE:
case ABSTRACT:
case CLASS:
case INTERFACE:
case STRICTFP:
parseClass();
break;
case SEMICOLON:
scan();
break;
case EOF:
return;
default :
env.error(pos,"toplevel.expected");
throw new SyntaxError();
}
}
 catch (SyntaxError e) {
recoverFile();
}
}
}
 catch (IOException e) {
env.error(pos,"io.exception",env.getSource());
return;
}
}
/** 
 * Usually <code>this.scanner == (Scanner)this</code>.
 * However, a delegate scanner can produce tokens for this parser,
 * in which case <code>(Scanner)this</code> is unused,
 * except for <code>this.token</code> and <code>this.pos</code>
 * instance variables which are filled from the real scanner
 * by <code>this.scan()</code> and the constructor.
 */
protected Scanner scanner;
public long scan() throws IOException {
if (scanner != this && scanner != null) {
long result=scanner.scan();
((Scanner)this).token=scanner.token;
((Scanner)this).pos=scanner.pos;
return result;
}
return super.scan();
}
public void match(int open,int close) throws IOException {
if (scanner != this) {
scanner.match(open,close);
((Scanner)this).token=scanner.token;
((Scanner)this).pos=scanner.pos;
return;
}
super.match(open,close);
}
}

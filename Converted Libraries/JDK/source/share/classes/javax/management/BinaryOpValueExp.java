package javax.management;
/** 
 * This class is used by the query-building mechanism to represent binary
 * operations.
 * @serial include
 * @since 1.5
 */
class BinaryOpValueExp extends QueryEval implements ValueExp {
  private static final long serialVersionUID=1216286847881456786L;
  /** 
 * @serial The operator
 */
  private int op;
  /** 
 * @serial The first value
 */
  private ValueExp exp1;
  /** 
 * @serial The second value
 */
  private ValueExp exp2;
  /** 
 * Basic Constructor.
 */
  public BinaryOpValueExp(){
  }
  /** 
 * Creates a new BinaryOpValueExp using operator o applied on v1 and
 * v2 values.
 */
  public BinaryOpValueExp(  int o,  ValueExp v1,  ValueExp v2){
    op=o;
    exp1=v1;
    exp2=v2;
  }
  /** 
 * Returns the operator of the value expression.
 */
  public int getOperator(){
    return op;
  }
  /** 
 * Returns the left value of the value expression.
 */
  public ValueExp getLeftValue(){
    return exp1;
  }
  /** 
 * Returns the right value of the value expression.
 */
  public ValueExp getRightValue(){
    return exp2;
  }
  /** 
 * Applies the BinaryOpValueExp on a MBean.
 * @param name The name of the MBean on which the BinaryOpValueExp will be applied.
 * @return  The ValueExp.
 * @exception BadStringOperationException
 * @exception BadBinaryOpValueExpException
 * @exception BadAttributeValueExpException
 * @exception InvalidApplicationException
 */
  public ValueExp apply(  ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    ValueExp val1=exp1.apply(name);
    ValueExp val2=exp2.apply(name);
    String sval1;
    String sval2;
    double dval1;
    double dval2;
    long lval1;
    long lval2;
    boolean numeric=val1 instanceof NumericValueExp;
    if (numeric) {
      if (((NumericValueExp)val1).isLong()) {
        lval1=((NumericValueExp)val1).longValue();
        lval2=((NumericValueExp)val2).longValue();
switch (op) {
case Query.PLUS:
          return Query.value(lval1 + lval2);
case Query.TIMES:
        return Query.value(lval1 * lval2);
case Query.MINUS:
      return Query.value(lval1 - lval2);
case Query.DIV:
    return Query.value(lval1 / lval2);
}
}
 else {
dval1=((NumericValueExp)val1).doubleValue();
dval2=((NumericValueExp)val2).doubleValue();
switch (op) {
case Query.PLUS:
  return Query.value(dval1 + dval2);
case Query.TIMES:
return Query.value(dval1 * dval2);
case Query.MINUS:
return Query.value(dval1 - dval2);
case Query.DIV:
return Query.value(dval1 / dval2);
}
}
}
 else {
sval1=((StringValueExp)val1).getValue();
sval2=((StringValueExp)val2).getValue();
switch (op) {
case Query.PLUS:
return new StringValueExp(sval1 + sval2);
default :
throw new BadStringOperationException(opString());
}
}
throw new BadBinaryOpValueExpException(this);
}
/** 
 * Returns the string representing the object
 */
public String toString(){
try {
return parens(exp1,true) + " " + opString()+ " "+ parens(exp2,false);
}
 catch (BadBinaryOpValueExpException ex) {
return "invalid expression";
}
}
private String parens(ValueExp subexp,boolean left) throws BadBinaryOpValueExpException {
boolean omit;
if (subexp instanceof BinaryOpValueExp) {
int subop=((BinaryOpValueExp)subexp).op;
if (left) omit=(precedence(subop) >= precedence(op));
 else omit=(precedence(subop) > precedence(op));
}
 else omit=true;
if (omit) return subexp.toString();
 else return "(" + subexp + ")";
}
private int precedence(int xop) throws BadBinaryOpValueExpException {
switch (xop) {
case Query.PLUS:
case Query.MINUS:
return 0;
case Query.TIMES:
case Query.DIV:
return 1;
default :
throw new BadBinaryOpValueExpException(this);
}
}
private String opString() throws BadBinaryOpValueExpException {
switch (op) {
case Query.PLUS:
return "+";
case Query.TIMES:
return "*";
case Query.MINUS:
return "-";
case Query.DIV:
return "/";
}
throw new BadBinaryOpValueExpException(this);
}
@Deprecated public void setMBeanServer(MBeanServer s){
super.setMBeanServer(s);
}
}

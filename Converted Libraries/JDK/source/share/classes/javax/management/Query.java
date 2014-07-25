package javax.management;
/** 
 * <p>Constructs query object constraints.</p>
 * <p>The MBean Server can be queried for MBeans that meet a particular
 * condition, using its {@link MBeanServer#queryNames queryNames} or{@link MBeanServer#queryMBeans queryMBeans} method.  The {@link QueryExp}parameter to the method can be any implementation of the interface{@code QueryExp}, but it is usually best to obtain the {@code QueryExp}value by calling the static methods in this class.  This is particularly
 * true when querying a remote MBean Server: a custom implementation of the{@code QueryExp} interface might not be present in the remote MBean Server,
 * but the methods in this class return only standard classes that are
 * part of the JMX implementation.</p>
 * <p>As an example, suppose you wanted to find all MBeans where the {@codeEnabled} attribute is {@code true} and the {@code Owner} attribute is {@code"Duke"}. Here is how you could construct the appropriate {@code QueryExp} by
 * chaining together method calls:</p>
 * <pre>
 * QueryExp query =
 * Query.and(Query.eq(Query.attr("Enabled"), Query.value(true)),
 * Query.eq(Query.attr("Owner"), Query.value("Duke")));
 * </pre>
 * @since 1.5
 */
public class Query extends Object {
  /** 
 * A code representing the {@link Query#gt} query.  This is chiefly
 * of interest for the serialized form of queries.
 */
  public static final int GT=0;
  /** 
 * A code representing the {@link Query#lt} query.  This is chiefly
 * of interest for the serialized form of queries.
 */
  public static final int LT=1;
  /** 
 * A code representing the {@link Query#geq} query.  This is chiefly
 * of interest for the serialized form of queries.
 */
  public static final int GE=2;
  /** 
 * A code representing the {@link Query#leq} query.  This is chiefly
 * of interest for the serialized form of queries.
 */
  public static final int LE=3;
  /** 
 * A code representing the {@link Query#eq} query.  This is chiefly
 * of interest for the serialized form of queries.
 */
  public static final int EQ=4;
  /** 
 * A code representing the {@link Query#plus} expression.  This
 * is chiefly of interest for the serialized form of queries.
 */
  public static final int PLUS=0;
  /** 
 * A code representing the {@link Query#minus} expression.  This
 * is chiefly of interest for the serialized form of queries.
 */
  public static final int MINUS=1;
  /** 
 * A code representing the {@link Query#times} expression.  This
 * is chiefly of interest for the serialized form of queries.
 */
  public static final int TIMES=2;
  /** 
 * A code representing the {@link Query#div} expression.  This is
 * chiefly of interest for the serialized form of queries.
 */
  public static final int DIV=3;
  /** 
 * Basic constructor.
 */
  public Query(){
  }
  /** 
 */
  public static QueryExp and(  QueryExp q1,  QueryExp q2){
    return new AndQueryExp(q1,q2);
  }
  /** 
 */
  public static QueryExp or(  QueryExp q1,  QueryExp q2){
    return new OrQueryExp(q1,q2);
  }
  /** 
 */
  public static QueryExp gt(  ValueExp v1,  ValueExp v2){
    return new BinaryRelQueryExp(GT,v1,v2);
  }
  /** 
 */
  public static QueryExp geq(  ValueExp v1,  ValueExp v2){
    return new BinaryRelQueryExp(GE,v1,v2);
  }
  /** 
 */
  public static QueryExp leq(  ValueExp v1,  ValueExp v2){
    return new BinaryRelQueryExp(LE,v1,v2);
  }
  /** 
 */
  public static QueryExp lt(  ValueExp v1,  ValueExp v2){
    return new BinaryRelQueryExp(LT,v1,v2);
  }
  /** 
 */
  public static QueryExp eq(  ValueExp v1,  ValueExp v2){
    return new BinaryRelQueryExp(EQ,v1,v2);
  }
  /** 
 */
  public static QueryExp between(  ValueExp v1,  ValueExp v2,  ValueExp v3){
    return new BetweenQueryExp(v1,v2,v3);
  }
  /** 
 */
  public static QueryExp match(  AttributeValueExp a,  StringValueExp s){
    return new MatchQueryExp(a,s);
  }
  /** 
 * <p>Returns a new attribute expression.  See {@link AttributeValueExp}for a detailed description of the semantics of the expression.</p>
 * <p>Evaluating this expression for a given
 * <code>objectName</code> includes performing {@link MBeanServer#getAttribute MBeanServer.getAttribute(objectName,
 * name)}.</p>
 * @param name The name of the attribute.
 * @return An attribute expression for the attribute named {@code name}.
 */
  public static AttributeValueExp attr(  String name){
    return new AttributeValueExp(name);
  }
  /** 
 */
  public static AttributeValueExp attr(  String className,  String name){
    return new QualifiedAttributeValueExp(className,name);
  }
  /** 
 */
  public static AttributeValueExp classattr(){
    return new ClassAttributeValueExp();
  }
  /** 
 */
  public static QueryExp not(  QueryExp queryExp){
    return new NotQueryExp(queryExp);
  }
  /** 
 */
  public static QueryExp in(  ValueExp val,  ValueExp valueList[]){
    return new InQueryExp(val,valueList);
  }
  /** 
 * Returns a new string expression.
 * @param val The string value.
 * @return  A ValueExp object containing the string argument.
 */
  public static StringValueExp value(  String val){
    return new StringValueExp(val);
  }
  /** 
 */
  public static ValueExp value(  Number val){
    return new NumericValueExp(val);
  }
  /** 
 */
  public static ValueExp value(  int val){
    return new NumericValueExp((long)val);
  }
  /** 
 */
  public static ValueExp value(  long val){
    return new NumericValueExp(val);
  }
  /** 
 */
  public static ValueExp value(  float val){
    return new NumericValueExp((double)val);
  }
  /** 
 */
  public static ValueExp value(  double val){
    return new NumericValueExp(val);
  }
  /** 
 */
  public static ValueExp value(  boolean val){
    return new BooleanValueExp(val);
  }
  /** 
 */
  public static ValueExp plus(  ValueExp value1,  ValueExp value2){
    return new BinaryOpValueExp(PLUS,value1,value2);
  }
  /** 
 */
  public static ValueExp times(  ValueExp value1,  ValueExp value2){
    return new BinaryOpValueExp(TIMES,value1,value2);
  }
  /** 
 */
  public static ValueExp minus(  ValueExp value1,  ValueExp value2){
    return new BinaryOpValueExp(MINUS,value1,value2);
  }
  /** 
 */
  public static ValueExp div(  ValueExp value1,  ValueExp value2){
    return new BinaryOpValueExp(DIV,value1,value2);
  }
  /** 
 */
  public static QueryExp initialSubString(  AttributeValueExp a,  StringValueExp s){
    return new MatchQueryExp(a,new StringValueExp(escapeString(s.getValue()) + "*"));
  }
  /** 
 */
  public static QueryExp anySubString(  AttributeValueExp a,  StringValueExp s){
    return new MatchQueryExp(a,new StringValueExp("*" + escapeString(s.getValue()) + "*"));
  }
  /** 
 */
  public static QueryExp finalSubString(  AttributeValueExp a,  StringValueExp s){
    return new MatchQueryExp(a,new StringValueExp("*" + escapeString(s.getValue())));
  }
  /** 
 */
  public static QueryExp isInstanceOf(  StringValueExp classNameValue){
    return new InstanceOfQueryExp(classNameValue);
  }
  /** 
 * Utility method to escape strings used with
 * Query.{initial|any|final}SubString() methods.
 */
  private static String escapeString(  String s){
    if (s == null)     return null;
    s=s.replace("\\","\\\\");
    s=s.replace("*","\\*");
    s=s.replace("?","\\?");
    s=s.replace("[","\\[");
    return s;
  }
}

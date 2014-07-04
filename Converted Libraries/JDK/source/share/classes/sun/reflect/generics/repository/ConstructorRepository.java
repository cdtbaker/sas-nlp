package sun.reflect.generics.repository;
import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;
/** 
 * This class represents the generic type information for a constructor.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 */
public class ConstructorRepository extends GenericDeclRepository<MethodTypeSignature> {
  private Type[] paramTypes;
  private Type[] exceptionTypes;
  protected ConstructorRepository(  String rawSig,  GenericsFactory f){
    super(rawSig,f);
  }
  protected MethodTypeSignature parse(  String s){
    return SignatureParser.make().parseMethodSig(s);
  }
  /** 
 * Static factory method.
 * @param rawSig - the generic signature of the reflective object
 * that this repository is servicing
 * @param f - a factory that will provide instances of reflective
 * objects when this repository converts its AST
 * @return a <tt>ConstructorRepository</tt> that manages the generic type
 * information represented in the signature <tt>rawSig</tt>
 */
  public static ConstructorRepository make(  String rawSig,  GenericsFactory f){
    return new ConstructorRepository(rawSig,f);
  }
  public Type[] getParameterTypes(){
    if (paramTypes == null) {
      TypeSignature[] pts=getTree().getParameterTypes();
      Type[] ps=new Type[pts.length];
      for (int i=0; i < pts.length; i++) {
        Reifier r=getReifier();
        pts[i].accept(r);
        ps[i]=r.getResult();
      }
      paramTypes=ps;
    }
    return paramTypes.clone();
  }
  public Type[] getExceptionTypes(){
    if (exceptionTypes == null) {
      FieldTypeSignature[] ets=getTree().getExceptionTypes();
      Type[] es=new Type[ets.length];
      for (int i=0; i < ets.length; i++) {
        Reifier r=getReifier();
        ets[i].accept(r);
        es[i]=r.getResult();
      }
      exceptionTypes=es;
    }
    return exceptionTypes.clone();
  }
}

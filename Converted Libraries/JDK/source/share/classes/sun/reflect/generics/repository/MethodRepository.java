package sun.reflect.generics.repository;
import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;
/** 
 * This class represents the generic type information for a method.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 */
public class MethodRepository extends ConstructorRepository {
  private Type returnType;
  private MethodRepository(  String rawSig,  GenericsFactory f){
    super(rawSig,f);
  }
  /** 
 * Static factory method.
 * @param rawSig - the generic signature of the reflective object
 * that this repository is servicing
 * @param f - a factory that will provide instances of reflective
 * objects when this repository converts its AST
 * @return a <tt>MethodRepository</tt> that manages the generic type
 * information represented in the signature <tt>rawSig</tt>
 */
  public static MethodRepository make(  String rawSig,  GenericsFactory f){
    return new MethodRepository(rawSig,f);
  }
  public Type getReturnType(){
    if (returnType == null) {
      Reifier r=getReifier();
      getTree().getReturnType().accept(r);
      returnType=r.getResult();
    }
    return returnType;
  }
}

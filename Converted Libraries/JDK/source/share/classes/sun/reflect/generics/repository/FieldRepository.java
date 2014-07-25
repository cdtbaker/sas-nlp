package sun.reflect.generics.repository;
import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.visitor.Reifier;
/** 
 * This class represents the generic type information for a constructor.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 */
public class FieldRepository extends AbstractRepository<TypeSignature> {
  private Type genericType;
  protected FieldRepository(  String rawSig,  GenericsFactory f){
    super(rawSig,f);
  }
  protected TypeSignature parse(  String s){
    return SignatureParser.make().parseTypeSig(s);
  }
  /** 
 * Static factory method.
 * @param rawSig - the generic signature of the reflective object
 * that this repository is servicing
 * @param f - a factory that will provide instances of reflective
 * objects when this repository converts its AST
 * @return a <tt>FieldRepository</tt> that manages the generic type
 * information represented in the signature <tt>rawSig</tt>
 */
  public static FieldRepository make(  String rawSig,  GenericsFactory f){
    return new FieldRepository(rawSig,f);
  }
  public Type getGenericType(){
    if (genericType == null) {
      Reifier r=getReifier();
      getTree().accept(r);
      genericType=r.getResult();
    }
    return genericType;
  }
}

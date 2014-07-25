package sun.reflect.generics.repository;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.TypeTree;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.parser.SignatureParser;
import java.lang.reflect.Type;
/** 
 * This class represents the generic type information for a class.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 */
public class ClassRepository extends GenericDeclRepository<ClassSignature> {
  private Type superclass;
  private Type[] superInterfaces;
  private ClassRepository(  String rawSig,  GenericsFactory f){
    super(rawSig,f);
  }
  protected ClassSignature parse(  String s){
    return SignatureParser.make().parseClassSig(s);
  }
  /** 
 * Static factory method.
 * @param rawSig - the generic signature of the reflective object
 * that this repository is servicing
 * @param f - a factory that will provide instances of reflective
 * objects when this repository converts its AST
 * @return a <tt>ClassRepository</tt> that manages the generic type
 * information represented in the signature <tt>rawSig</tt>
 */
  public static ClassRepository make(  String rawSig,  GenericsFactory f){
    return new ClassRepository(rawSig,f);
  }
  public Type getSuperclass(){
    if (superclass == null) {
      Reifier r=getReifier();
      getTree().getSuperclass().accept(r);
      superclass=r.getResult();
    }
    return superclass;
  }
  public Type[] getSuperInterfaces(){
    if (superInterfaces == null) {
      TypeTree[] ts=getTree().getSuperInterfaces();
      Type[] sis=new Type[ts.length];
      for (int i=0; i < ts.length; i++) {
        Reifier r=getReifier();
        ts[i].accept(r);
        sis[i]=r.getResult();
      }
      superInterfaces=sis;
    }
    return superInterfaces.clone();
  }
}

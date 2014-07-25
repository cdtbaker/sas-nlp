package sun.reflect.generics.repository;
import java.lang.reflect.TypeVariable;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.Signature;
import sun.reflect.generics.visitor.Reifier;
/** 
 * This class represents the generic type information for a generic
 * declaration.
 * The code is not dependent on a particular reflective implementation.
 * It is designed to be used unchanged by at least core reflection and JDI.
 */
public abstract class GenericDeclRepository<S extends Signature> extends AbstractRepository<S> {
  private TypeVariable[] typeParams;
  protected GenericDeclRepository(  String rawSig,  GenericsFactory f){
    super(rawSig,f);
  }
  /** 
 * Return the formal type parameters of this generic declaration.
 * @return the formal type parameters of this generic declaration
 */
  public TypeVariable[] getTypeParameters(){
    if (typeParams == null) {
      FormalTypeParameter[] ftps=getTree().getFormalTypeParameters();
      TypeVariable[] tps=new TypeVariable[ftps.length];
      for (int i=0; i < ftps.length; i++) {
        Reifier r=getReifier();
        ftps[i].accept(r);
        tps[i]=(TypeVariable<?>)r.getResult();
      }
      typeParams=tps;
    }
    return typeParams.clone();
  }
}

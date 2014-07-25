package javax.management.relation;
import static com.sun.jmx.defaults.JmxProperties.RELATION_LOGGER;
import static com.sun.jmx.mbeanserver.Util.cast;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
/** 
 * A RelationTypeSupport object implements the RelationType interface.
 * <P>It represents a relation type, providing role information for each role
 * expected to be supported in every relation of that type.
 * <P>A relation type includes a relation type name and a list of
 * role infos (represented by RoleInfo objects).
 * <P>A relation type has to be declared in the Relation Service:
 * <P>- either using the createRelationType() method, where a RelationTypeSupport
 * object will be created and kept in the Relation Service
 * <P>- either using the addRelationType() method where the user has to create
 * an object implementing the RelationType interface, and this object will be
 * used as representing a relation type in the Relation Service.
 * <p>The <b>serialVersionUID</b> of this class is <code>4611072955724144607L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class RelationTypeSupport implements RelationType {
  private static final long oldSerialVersionUID=-8179019472410837190L;
  private static final long newSerialVersionUID=4611072955724144607L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("myTypeName",String.class),new ObjectStreamField("myRoleName2InfoMap",HashMap.class),new ObjectStreamField("myIsInRelServFlg",boolean.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("typeName",String.class),new ObjectStreamField("roleName2InfoMap",Map.class),new ObjectStreamField("isInRelationService",boolean.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField typeName String Relation type name
 * @serialField roleName2InfoMap Map {@link Map} holding the mapping:
 * &lt;role name ({@link String})&gt; -&gt; &lt;role info ({@link RoleInfo} object)&gt;
 * @serialField isInRelationService boolean Flag specifying whether the relation type has been declared in the
 * Relation Service (so can no longer be updated)
 */
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat=false;
static {
    try {
      GetPropertyAction act=new GetPropertyAction("jmx.serial.form");
      String form=AccessController.doPrivileged(act);
      compat=(form != null && form.equals("1.0"));
    }
 catch (    Exception e) {
    }
    if (compat) {
      serialPersistentFields=oldSerialPersistentFields;
      serialVersionUID=oldSerialVersionUID;
    }
 else {
      serialPersistentFields=newSerialPersistentFields;
      serialVersionUID=newSerialVersionUID;
    }
  }
  /** 
 * @serial Relation type name
 */
  private String typeName=null;
  /** 
 * @serial {@link Map} holding the mapping:
 * &lt;role name ({@link String})&gt; -&gt; &lt;role info ({@link RoleInfo} object)&gt;
 */
  private Map<String,RoleInfo> roleName2InfoMap=new HashMap<String,RoleInfo>();
  /** 
 * @serial Flag specifying whether the relation type has been declared in the
 * Relation Service (so can no longer be updated)
 */
  private boolean isInRelationService=false;
  /** 
 * Constructor where all role definitions are dynamically created and
 * passed as parameter.
 * @param relationTypeName  Name of relation type
 * @param roleInfoArray  List of role definitions (RoleInfo objects)
 * @exception IllegalArgumentException  if null parameter
 * @exception InvalidRelationTypeException  if:
 * <P>- the same name has been used for two different roles
 * <P>- no role info provided
 * <P>- one null role info provided
 */
  public RelationTypeSupport(  String relationTypeName,  RoleInfo[] roleInfoArray) throws IllegalArgumentException, InvalidRelationTypeException {
    if (relationTypeName == null || roleInfoArray == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(RelationTypeSupport.class.getName(),"RelationTypeSupport",relationTypeName);
    initMembers(relationTypeName,roleInfoArray);
    RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(),"RelationTypeSupport");
    return;
  }
  /** 
 * Constructor to be used for subclasses.
 * @param relationTypeName  Name of relation type.
 * @exception IllegalArgumentException  if null parameter.
 */
  protected RelationTypeSupport(  String relationTypeName){
    if (relationTypeName == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(RelationTypeSupport.class.getName(),"RelationTypeSupport",relationTypeName);
    typeName=relationTypeName;
    RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(),"RelationTypeSupport");
    return;
  }
  /** 
 * Returns the relation type name.
 * @return the relation type name.
 */
  public String getRelationTypeName(){
    return typeName;
  }
  /** 
 * Returns the list of role definitions (ArrayList of RoleInfo objects).
 */
  public List<RoleInfo> getRoleInfos(){
    return new ArrayList<RoleInfo>(roleName2InfoMap.values());
  }
  /** 
 * Returns the role info (RoleInfo object) for the given role info name
 * (null if not found).
 * @param roleInfoName  role info name
 * @return RoleInfo object providing role definition
 * does not exist
 * @exception IllegalArgumentException  if null parameter
 * @exception RoleInfoNotFoundException  if no role info with that name in
 * relation type.
 */
  public RoleInfo getRoleInfo(  String roleInfoName) throws IllegalArgumentException, RoleInfoNotFoundException {
    if (roleInfoName == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(RelationTypeSupport.class.getName(),"getRoleInfo",roleInfoName);
    RoleInfo result=roleName2InfoMap.get(roleInfoName);
    if (result == null) {
      StringBuilder excMsgStrB=new StringBuilder();
      String excMsg="No role info for role ";
      excMsgStrB.append(excMsg);
      excMsgStrB.append(roleInfoName);
      throw new RoleInfoNotFoundException(excMsgStrB.toString());
    }
    RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(),"getRoleInfo");
    return result;
  }
  /** 
 * Add a role info.
 * This method of course should not be used after the creation of the
 * relation type, because updating it would invalidate that the relations
 * created associated to that type still conform to it.
 * Can throw a RuntimeException if trying to update a relation type
 * declared in the Relation Service.
 * @param roleInfo  role info to be added.
 * @exception IllegalArgumentException  if null parameter.
 * @exception InvalidRelationTypeException  if there is already a role
 * info in current relation type with the same name.
 */
  protected void addRoleInfo(  RoleInfo roleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (roleInfo == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(RelationTypeSupport.class.getName(),"addRoleInfo",roleInfo);
    if (isInRelationService) {
      String excMsg="Relation type cannot be updated as it is declared in the Relation Service.";
      throw new RuntimeException(excMsg);
    }
    String roleName=roleInfo.getName();
    if (roleName2InfoMap.containsKey(roleName)) {
      StringBuilder excMsgStrB=new StringBuilder();
      String excMsg="Two role infos provided for role ";
      excMsgStrB.append(excMsg);
      excMsgStrB.append(roleName);
      throw new InvalidRelationTypeException(excMsgStrB.toString());
    }
    roleName2InfoMap.put(roleName,new RoleInfo(roleInfo));
    RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(),"addRoleInfo");
    return;
  }
  void setRelationServiceFlag(  boolean flag){
    isInRelationService=flag;
    return;
  }
  private void initMembers(  String relationTypeName,  RoleInfo[] roleInfoArray) throws IllegalArgumentException, InvalidRelationTypeException {
    if (relationTypeName == null || roleInfoArray == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(RelationTypeSupport.class.getName(),"initMembers",relationTypeName);
    typeName=relationTypeName;
    checkRoleInfos(roleInfoArray);
    for (int i=0; i < roleInfoArray.length; i++) {
      RoleInfo currRoleInfo=roleInfoArray[i];
      roleName2InfoMap.put(currRoleInfo.getName(),new RoleInfo(currRoleInfo));
    }
    RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(),"initMembers");
    return;
  }
  static void checkRoleInfos(  RoleInfo[] roleInfoArray) throws IllegalArgumentException, InvalidRelationTypeException {
    if (roleInfoArray == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    if (roleInfoArray.length == 0) {
      String excMsg="No role info provided.";
      throw new InvalidRelationTypeException(excMsg);
    }
    Set<String> roleNames=new HashSet<String>();
    for (int i=0; i < roleInfoArray.length; i++) {
      RoleInfo currRoleInfo=roleInfoArray[i];
      if (currRoleInfo == null) {
        String excMsg="Null role info provided.";
        throw new InvalidRelationTypeException(excMsg);
      }
      String roleName=currRoleInfo.getName();
      if (roleNames.contains(roleName)) {
        StringBuilder excMsgStrB=new StringBuilder();
        String excMsg="Two role infos provided for role ";
        excMsgStrB.append(excMsg);
        excMsgStrB.append(roleName);
        throw new InvalidRelationTypeException(excMsgStrB.toString());
      }
      roleNames.add(roleName);
    }
    return;
  }
  /** 
 * Deserializes a {@link RelationTypeSupport} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField fields=in.readFields();
      typeName=(String)fields.get("myTypeName",null);
      if (fields.defaulted("myTypeName")) {
        throw new NullPointerException("myTypeName");
      }
      roleName2InfoMap=cast(fields.get("myRoleName2InfoMap",null));
      if (fields.defaulted("myRoleName2InfoMap")) {
        throw new NullPointerException("myRoleName2InfoMap");
      }
      isInRelationService=fields.get("myIsInRelServFlg",false);
      if (fields.defaulted("myIsInRelServFlg")) {
        throw new NullPointerException("myIsInRelServFlg");
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes a {@link RelationTypeSupport} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("myTypeName",typeName);
      fields.put("myRoleName2InfoMap",roleName2InfoMap);
      fields.put("myIsInRelServFlg",isInRelationService);
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}

package sun.security.acl;
import java.util.*;
import java.security.*;
import java.security.acl.*;
/** 
 * This class implements a group of principals.
 * @author      Satish Dharmaraj
 */
public class GroupImpl implements Group {
  private Vector<Principal> groupMembers=new Vector<>(50,100);
  private String group;
  /** 
 * Constructs a Group object with no members.
 * @param groupName the name of the group
 */
  public GroupImpl(  String groupName){
    this.group=groupName;
  }
  /** 
 * adds the specified member to the group.
 * @param user The principal to add to the group.
 * @return true if the member was added - false if the
 * member could not be added.
 */
  public boolean addMember(  Principal user){
    if (groupMembers.contains(user))     return false;
    if (group.equals(user.toString()))     throw new IllegalArgumentException();
    groupMembers.addElement(user);
    return true;
  }
  /** 
 * removes the specified member from the group.
 * @param user The principal to remove from the group.
 * @param true if the principal was removed false if
 * the principal was not a member
 */
  public boolean removeMember(  Principal user){
    return groupMembers.removeElement(user);
  }
  /** 
 * returns the enumeration of the members in the group.
 */
  public Enumeration<? extends Principal> members(){
    return groupMembers.elements();
  }
  /** 
 * This function returns true if the group passed matches
 * the group represented in this interface.
 * @param another The group to compare this group to.
 */
  public boolean equals(  Object obj){
    if (this == obj) {
      return true;
    }
    if (obj instanceof Group == false) {
      return false;
    }
    Group another=(Group)obj;
    return group.equals(another.toString());
  }
  public boolean equals(  Group another){
    return equals((Object)another);
  }
  /** 
 * Prints a stringified version of the group.
 */
  public String toString(){
    return group;
  }
  /** 
 * return a hashcode for the principal.
 */
  public int hashCode(){
    return group.hashCode();
  }
  /** 
 * returns true if the passed principal is a member of the group.
 * @param member The principal whose membership must be checked for.
 * @return true if the principal is a member of this group,
 * false otherwise
 */
  public boolean isMember(  Principal member){
    if (groupMembers.contains(member)) {
      return true;
    }
 else {
      Vector<Group> alreadySeen=new Vector<>(10);
      return isMemberRecurse(member,alreadySeen);
    }
  }
  /** 
 * return the name of the principal.
 */
  public String getName(){
    return group;
  }
  boolean isMemberRecurse(  Principal member,  Vector<Group> alreadySeen){
    Enumeration<? extends Principal> e=members();
    while (e.hasMoreElements()) {
      boolean mem=false;
      Principal p=(Principal)e.nextElement();
      if (p.equals(member)) {
        return true;
      }
 else       if (p instanceof GroupImpl) {
        GroupImpl g=(GroupImpl)p;
        alreadySeen.addElement(this);
        if (!alreadySeen.contains(g))         mem=g.isMemberRecurse(member,alreadySeen);
      }
 else       if (p instanceof Group) {
        Group g=(Group)p;
        if (!alreadySeen.contains(g))         mem=g.isMember(member);
      }
      if (mem)       return mem;
    }
    return false;
  }
}

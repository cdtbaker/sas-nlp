import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.attribute.PosixFilePermission.*;
import static java.nio.file.FileVisitResult.*;
import java.io.IOException;
import java.util.*;
/** 
 * Sample code that changes the permissions of files in a similar manner to the
 * chmod(1) program.
 */
public class Chmod {
  /** 
 * Compiles a list of one or more <em>symbolic mode expressions</em> that
 * may be used to change a set of file permissions. This method is
 * intended for use where file permissions are required to be changed in
 * a manner similar to the UNIX <i>chmod</i> program.
 * <p> The {@code exprs} parameter is a comma separated list of expressions
 * where each takes the form:
 * <blockquote>
 * <i>who operator</i> [<i>permissions</i>]
 * </blockquote>
 * where <i>who</i> is one or more of the characters {@code 'u'}, {@code 'g'},{@code 'o'}, or {@code 'a'} meaning the owner (user), group, others, or
 * all (owner, group, and others) respectively.
 * <p> <i>operator</i> is the character {@code '+'}, {@code '-'}, or {@code'='} signifying how permissions are to be changed. {@code '+'} means the
 * permissions are added, {@code '-'} means the permissions are removed, and{@code '='} means the permissions are assigned absolutely.
 * <p> <i>permissions</i> is a sequence of zero or more of the following:{@code 'r'} for read permission, {@code 'w'} for write permission, and{@code 'x'} for execute permission. If <i>permissions</i> is omitted
 * when assigned absolutely, then the permissions are cleared for
 * the owner, group, or others as identified by <i>who</i>. When omitted
 * when adding or removing then the expression is ignored.
 * <p> The following examples demonstrate possible values for the {@codeexprs} parameter:
 * <table border="0">
 * <tr>
 * <td> {@code u=rw} </td>
 * <td> Sets the owner permissions to be read and write. </td>
 * </tr>
 * <tr>
 * <td> {@code ug+w} </td>
 * <td> Sets the owner write and group write permissions. </td>
 * </tr>
 * <tr>
 * <td> {@code u+w,o-rwx} </td>
 * <td> Sets the owner write, and removes the others read, others write
 * and others execute permissions. </td>
 * </tr>
 * <tr>
 * <td> {@code o=} </td>
 * <td> Sets the others permission to none (others read, others write and
 * others execute permissions are removed if set) </td>
 * </tr>
 * </table>
 * @param exprsList of one or more <em>symbolic mode expressions</em>
 * @return  A {@code Changer} that may be used to changer a set of
 * file permissions
 * @throws IllegalArgumentExceptionIf the value of the {@code exprs} parameter is invalid
 */
  public static Changer compile(  String exprs){
    if (exprs.length() < 2)     throw new IllegalArgumentException("Invalid mode");
    final Set<PosixFilePermission> toAdd=new HashSet<PosixFilePermission>();
    final Set<PosixFilePermission> toRemove=new HashSet<PosixFilePermission>();
    for (    String expr : exprs.split(",")) {
      if (expr.length() < 2)       throw new IllegalArgumentException("Invalid mode");
      int pos=0;
      boolean u=false;
      boolean g=false;
      boolean o=false;
      boolean done=false;
      for (; ; ) {
switch (expr.charAt(pos)) {
case 'u':
          u=true;
        break;
case 'g':
      g=true;
    break;
case 'o':
  o=true;
break;
case 'a':
u=true;
g=true;
o=true;
break;
default :
done=true;
}
if (done) break;
pos++;
}
if (!u && !g && !o) throw new IllegalArgumentException("Invalid mode");
char op=expr.charAt(pos++);
String mask=(expr.length() == pos) ? "" : expr.substring(pos);
boolean add=(op == '+');
boolean remove=(op == '-');
boolean assign=(op == '=');
if (!add && !remove && !assign) throw new IllegalArgumentException("Invalid mode");
if (assign && mask.length() == 0) {
assign=false;
remove=true;
mask="rwx";
}
boolean r=false;
boolean w=false;
boolean x=false;
for (int i=0; i < mask.length(); i++) {
switch (mask.charAt(i)) {
case 'r':
r=true;
break;
case 'w':
w=true;
break;
case 'x':
x=true;
break;
default :
throw new IllegalArgumentException("Invalid mode");
}
}
if (add) {
if (u) {
if (r) toAdd.add(OWNER_READ);
if (w) toAdd.add(OWNER_WRITE);
if (x) toAdd.add(OWNER_EXECUTE);
}
if (g) {
if (r) toAdd.add(GROUP_READ);
if (w) toAdd.add(GROUP_WRITE);
if (x) toAdd.add(GROUP_EXECUTE);
}
if (o) {
if (r) toAdd.add(OTHERS_READ);
if (w) toAdd.add(OTHERS_WRITE);
if (x) toAdd.add(OTHERS_EXECUTE);
}
}
if (remove) {
if (u) {
if (r) toRemove.add(OWNER_READ);
if (w) toRemove.add(OWNER_WRITE);
if (x) toRemove.add(OWNER_EXECUTE);
}
if (g) {
if (r) toRemove.add(GROUP_READ);
if (w) toRemove.add(GROUP_WRITE);
if (x) toRemove.add(GROUP_EXECUTE);
}
if (o) {
if (r) toRemove.add(OTHERS_READ);
if (w) toRemove.add(OTHERS_WRITE);
if (x) toRemove.add(OTHERS_EXECUTE);
}
}
if (assign) {
if (u) {
if (r) toAdd.add(OWNER_READ);
 else toRemove.add(OWNER_READ);
if (w) toAdd.add(OWNER_WRITE);
 else toRemove.add(OWNER_WRITE);
if (x) toAdd.add(OWNER_EXECUTE);
 else toRemove.add(OWNER_EXECUTE);
}
if (g) {
if (r) toAdd.add(GROUP_READ);
 else toRemove.add(GROUP_READ);
if (w) toAdd.add(GROUP_WRITE);
 else toRemove.add(GROUP_WRITE);
if (x) toAdd.add(GROUP_EXECUTE);
 else toRemove.add(GROUP_EXECUTE);
}
if (o) {
if (r) toAdd.add(OTHERS_READ);
 else toRemove.add(OTHERS_READ);
if (w) toAdd.add(OTHERS_WRITE);
 else toRemove.add(OTHERS_WRITE);
if (x) toAdd.add(OTHERS_EXECUTE);
 else toRemove.add(OTHERS_EXECUTE);
}
}
}
return new Changer(){
@Override public Set<PosixFilePermission> change(Set<PosixFilePermission> perms){
perms.addAll(toAdd);
perms.removeAll(toRemove);
return perms;
}
}
;
}
/** 
 * A task that <i>changes</i> a set of {@link PosixFilePermission} elements.
 */
public interface Changer {
/** 
 * Applies the changes to the given set of permissions.
 * @param permsThe set of permissions to change
 * @return  The {@code perms} parameter
 */
Set<PosixFilePermission> change(Set<PosixFilePermission> perms);
}
/** 
 * Changes the permissions of the file using the given Changer.
 */
static void chmod(Path file,Changer changer){
try {
Set<PosixFilePermission> perms=Files.getPosixFilePermissions(file);
Files.setPosixFilePermissions(file,changer.change(perms));
}
 catch (IOException x) {
System.err.println(x);
}
}
/** 
 * Changes the permission of each file and directory visited
 */
static class TreeVisitor implements FileVisitor<Path> {
private final Changer changer;
TreeVisitor(Changer changer){
this.changer=changer;
}
@Override public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs){
chmod(dir,changer);
return CONTINUE;
}
@Override public FileVisitResult visitFile(Path file,BasicFileAttributes attrs){
chmod(file,changer);
return CONTINUE;
}
@Override public FileVisitResult postVisitDirectory(Path dir,IOException exc){
if (exc != null) System.err.println("WARNING: " + exc);
return CONTINUE;
}
@Override public FileVisitResult visitFileFailed(Path file,IOException exc){
System.err.println("WARNING: " + exc);
return CONTINUE;
}
}
static void usage(){
System.err.println("java Chmod [-R] symbolic-mode-list file...");
System.exit(-1);
}
public static void main(String[] args) throws IOException {
if (args.length < 2) usage();
int argi=0;
int maxDepth=0;
if (args[argi].equals("-R")) {
if (args.length < 3) usage();
argi++;
maxDepth=Integer.MAX_VALUE;
}
Changer changer=compile(args[argi++]);
TreeVisitor visitor=new TreeVisitor(changer);
Set<FileVisitOption> opts=Collections.emptySet();
while (argi < args.length) {
Path file=Paths.get(args[argi]);
Files.walkFileTree(file,opts,maxDepth,visitor);
argi++;
}
}
}

package j2dbench;
public interface Modifier {
  public Modifier.Iterator getIterator(  TestEnvironment env);
  public void modifyTest(  TestEnvironment env,  Object val);
  public void restoreTest(  TestEnvironment env,  Object val);
  public String getTreeName();
  public String getAbbreviatedModifierDescription(  Object val);
  public String getModifierValueName(  Object val);
public static interface Iterator {
    public boolean hasNext();
    public Object next();
  }
public static interface Filter {
    public boolean isCompatible(    Object val);
  }
}
